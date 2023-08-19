package clubSimulation;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.Thread;
import java.util.Random;
public class  AndreBarman extends Thread{
    
    private GridBlock andreGrid;// Andre's grid block
    private PeopleLocation andreLocation; // Andre's location
    private AtomicBoolean inRoom; // will be used to check
    private ClubGrid club; //shared    
    private int ID; // Andre's ID
    private int speed;//andre's speed on the bar counter ( He doesn't stand on the same spot)
    private Random rand; 
    public static final CountDownLatch latch=new CountDownLatch(1);
    //Defualt constructor
    public AndreBarman(int ID,int speed)
    {   
        this.ID=ID;
        this.speed=speed;
        inRoom.set(false); 
        rand=new Random();
    }
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
		andreGrid = club.enterClub(andreLocation);  //enter through entrance
		inRoom.set(true);
        Clubgoer.latch.countDown();
	}
    private void headToBar() throws InterruptedException {
		int x_mv= rand.nextInt(3)-1;	//	-1,0 or 1
		int y_mv= Integer.signum(club.getBar_y()-andreGrid.getY());//-1,0 or 1
		andreGrid=club.move(andreGrid,x_mv,y_mv,andreLocation); //head toward bar
	}
    public void run(){
            try{
            startSim(); 
			checkPause();
            andreLocation.setArrived(); 
            checkPause(); //check whethere have been asked to pause
			enterClub();
            // head to bar and stop when you are there
            while(!andreGrid.isBar()){
                headToBar();            
            }
             andreGrid=new GridBlock(club.getMaxX()/2, club.getBar_y(), false, false, false);//andre should be at the middle of the bar
             andreLocation.setLocation(andreGrid);
            }
            catch(InterruptedException e1) {}
          
    }
}
