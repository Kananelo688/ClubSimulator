package clubSimulation;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.Thread;
import java.util.Random;
import java.util.*;
public class  AndreBarman extends Thread{
    
    private GridBlock andreGrid;// Andre's grid block
    private PeopleLocation andreLocation; // Andre's location
    private AtomicBoolean inRoom; // will be used to check
    private ClubGrid club; //shared    club
    private int ID; // Andre's ID
    private int speed;//andre's speed on the bar counter ( He doesn't stand on the same spot)
    private Random rand; 
    public static CountDownLatch latch;
    public static ArrayList<GridBlock> thirstyThreads;// array that stores threads that are currently thirsty
    //Defualt constructor
    public AndreBarman(int ID,int speed)
    {   
        this.ID=ID;
        this.speed=speed;
        inRoom=new AtomicBoolean(false); 
        rand=new Random();
        thirstyThreads=new ArrayList<>();//
        latch=new CountDownLatch(1);
    }
    //synchronized to prevent simultenous access by multiple threaads
    synchronized public ArrayList<GridBlock> getThirstyThreads() {return thirstyThreads;}
    synchronized public void addToThirsty(GridBlock block){thirstyThreads.add(block);}
    public void setGridBlock(GridBlock block){andreGrid=block;}
    public void setLocation(PeopleLocation location){andreLocation=location;}
    public void setClub(ClubGrid grid){club=grid;}
    public boolean andreInRoom(){return inRoom.get();}
    public PeopleLocation getAndreLocation(){return andreLocation;}
    public GridBlock getAndreGrid(){return andreGrid;}
    private void checkPause() throws InterruptedException
    {
		while(ClubSimulation.paused.get()){
            sleep(100);// sleep thread for 0.1s before next check if pause pressed
        }  	
        
    } 
    private void startSim() throws InterruptedException
     {
            latch.await(); // wait untill the gate is released.
    }
    private void enterClub() throws InterruptedException {
        ClubGrid.andre.set(true);
		andreGrid = club.enterClub(andreLocation);  //enter through entrance
        ClubGrid.andre.set(false);    		
        inRoom.set(true);
        sleep(speed/3);
	}
    private void headToBar() throws InterruptedException {
		int x_mv= rand.nextInt(3)-1;	//	-1,0 or 1
		int y_mv= Integer.signum(club.getBar_y()-andreGrid.getY());//-1,0 or 1
		andreGrid=club.move(andreGrid,x_mv,y_mv,andreLocation); //head toward bar
	}
    private void serveDrinks(GridBlock blockToServe){
		int x_mv= Integer.signum(blockToserve.getX()-andreGrid.getX());//x_mv is -1,0 or 1
		int y_mv= Integer.signum(andreGrid.getY()-andreGrid.getY());//keep andre y-coodinate constant
		andreGrid=club.move(andreGrid,x_mv,y_mv,andreLocation);
        sleep(speed/3);// wait a bit      
    }
    public void run(){
            try{
            startSim(); 
			checkPause();
            andreLocation.setArrived(); 
            checkPause(); //check whethere have been asked to pause
			enterClub();
            boolean unchecked=true;
            while(true){
                    checkPause();
                    if(!andreGrid.isBar()){
                        sleep(speed/3);
                        System.out.println("Andre heading towards the Bar");//for debug
                        headToBar();
                        continue;                    
                    }
                    if(andreGrid.isBar() && unchecked){
                        System.out.println("Andre arrived at the Bar");//for debug
                        club.isBarMan.set(false);unchecked=false;
                        Clubgoer.latch.countDown();// open the entrace after getting to the bar
                        //andreGrid.setY(club.getBar_y());//fix the y-coordibate of Andre at the counter                    
                    }
                    if(thirstyThreads.size()!=0){
                            checkPause();
                            //System.out.println("Andre ready to serve Bar");//for debug
                            int andreX=andreGrid.getX();
                            int targetX=thirstyThreads.get(0).getX();
                            int x_mv1=-1,x_mv2=1;
                            if(andreX>targetX){
                                  serveDrinks(GridBlock thirstyThreads.get(0)); 
                                  checkPause();                     
                            }
                            else if(andreX<targetX){
                                serveDrinks(thirstyThreads.get(0));
                                checkPause();                          
                            }else{
                                //System.out.println("Andre serving thread in block with x-coordnate: "+targetX);//for debug
                                checkPause();
                                sleep(1000); // sleep for 1s to symbolize srving of drinks
                                GridBlock served=thirstyThreads.remove(0);
                                served.markServed();                          
                            }
                    }                
            }
             
            }
            catch(InterruptedException e1) {}
          
    }
}
