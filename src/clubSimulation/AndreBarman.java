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
    private static ArrayList<Clubgoer> thirstyThreads;// array that stores threads that are currently thirsty
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
    synchronized public static void addToThirsty(Clubgoer apt){thirstyThreads.add(apt);};
    public void setGridBlock(GridBlock block){andreGrid=block;}
    public void setLocation(PeopleLocation location){andreLocation=location;}
    public void setClub(ClubGrid grid){club=grid;}
    public boolean andreInRoom(){return inRoom.get();}
    synchronized public PeopleLocation getAndreLocation(){return andreLocation;}
    public GridBlock getAndreGrid(){return andreGrid;}
    private void checkPause() throws InterruptedException
    {
		while(ClubSimulation.paused.get()){
            sleep(speed);// sleep thread for 0.1s before next check if pause pressed
        }  	
        
    } 
    private void startSim() throws InterruptedException
     {
            latch.await(); // wait untill the gate is released.
    }
    private void enterClub() throws InterruptedException {
        ClubGrid.andre.set(true);
		andreGrid = club.enterClub(andreLocation);  //enter through entrance
        //System.out.println("Andre's entrace dimensions: ("+andreGrid.getX()+","+andreGrid.getY()+")");
        ClubGrid.andre.set(false);    		
        inRoom.set(true);
        sleep(speed/3);
	}
    private void headToBar() throws InterruptedException {
		int x_mv= rand.nextInt(3)-1;	//	-1,0 or 1
		int y_mv= Integer.signum(club.getBar_y()-andreGrid.getY());//-1,0 or 1
		andreGrid=club.move(andreGrid,x_mv,y_mv,andreLocation,true); //head toward bar
	}
    private void serveDrinks() throws InterruptedException{
        if(thirstyThreads.size()!=0){
            Clubgoer serve= thirstyThreads.remove(0);
            int dist=andreGrid.getX()-serve.currentBlock.getX();
            if(dist<0){
               for(int i=1;i<=Math.abs(dist);i++){
                   checkPause();
                   sleep(1000);//wait a bit
                  // if(!club.inPatronArea(andreGrid.getX()+1,andreGrid.getY())){continue;}
                   andreGrid=club.move(andreGrid,1,0,andreLocation,true);                                  
                }
                 checkPause();
               // System.out.println("Serving thread-"+serve.getID()+"Drinks...");//for debug
                sleep(2000);//serves drinks
                serve.markServed();//release the thread so it does its thing again  
               // System.out.println("Done.");                             
            }else if(dist>0){
                 for(int i=1;i<=dist;i++){
                   checkPause();
                    sleep(1000);//wait a bit
                  // if(!club.inPatronArea(andreGrid.getX()-1,andreGrid.getY())){continue;} 
                   andreGrid=club.move(andreGrid,-1,0,andreLocation,true);                                  
                }
                checkPause();
               // System.out.println("Serving thread-"+serve.getID()+" Drinks...");
                sleep(2000);//serves drinks
                serve.markServed();//release the thread so it does its thing again  
                //System.out.println("Done.");   
            }else{
                 checkPause();
                //System.out.println("Serving thread-"+serve.getID()+" Drinks...");
                sleep(2000);//serves drinks
                serve.markServed();//release the thread so it does its thing again  
                //System.out.println("Done.");                 
            }
         }        
    }
    public void run(){
            try{
                startSim(); 
			    checkPause();
                sleep(speed);//take time to get to the bar
                andreLocation.setArrived(); 
                checkPause(); //check whethere have been asked to pause
			    enterClub();
                while(!andreGrid.isBar()){
                        sleep(speed/2);
                        checkPause();
                        headToBar(); 
                }
                ClubGrid.isBarMan.set(false);
                Clubgoer.latch.countDown();   
                while(true){
                    checkPause();
                    serveDrinks();  
                    sleep(speed/2);     
                }             
            }
            catch(InterruptedException e1) {}
          
    }
}
