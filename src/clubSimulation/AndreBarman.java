package clubSimulation;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.Thread;
import java.util.Random;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
/**
AndreBarman is a Thread that represents the bar man in the club. It is responsile for serving drinks to customers when ever they are hungry. The barman is design to be moving up and down on the Bar counter, dristributing drinks. When a patron is thirsty, it heads down to the bar, waits infront of the counter until served. The serving speed of Andrew is made proportional to the number of drink requets so as to prevent deadlocks in the program. This just minimizes the chances of Deadlock, but does ot guarantees complete prevention.

<p>
When the program starts, Andre if the frist to get into the club and only after reaching the club will the other threads be let in </p>


@author: Kananelo Chabeli
*/

public class  AndreBarman extends Thread{
    /**
       andrGrid, is the GrideBlock object which represent where AndreBarman thread is in the simulation 
    */
    private GridBlock andreGrid;
    /**
        Defines Andre's location in the club    
    */
    private PeopleLocation andreLocation; // Andre's location
    /**
        Flag to determine if Andre is in the room    
    */
    private AtomicBoolean inRoom; // will be used to check
    /**
    Shared club object which is where all threads will be interacting    
    */
    private ClubGrid club; //shared    club
    /**
        Identification number of Andre int the club. ( THis is  not used though becuase Andre is marked "Bar man")    
    */
    private int ID; // Andre's ID
    /**
        Andre's speed when he enters to the club, heading towards the bar. This speed is not the speed at which he serves drinks to patrons     
    */
    private int speed;
    /**
        A Random object for generating random behaviour    
    */
    private Random rand; 
    /**
        CountDownLatch object on which andre is locked on before the program starts. This is released when user presses Start Button         
    */
    public static CountDownLatch latch;
    /**
        An ArrayList of club goers, which arre patrons. When  thread is safety, it is added into this array list and Andre uses this 
        to determine the positions that need drinks
    */
    private static ArrayList<Clubgoer> thirstyThreads;// array that stores threads that are currently thirsty
    /**
       PeopleCounter obect display statistics on the screen as the simulation proceeds     
    */
    private PeopleCounter counter;
    /**
        Andre's  serving speed in the club. This is increased as more and more drinks requests are recieved.    
    */
    public static AtomicInteger speeder;
    /**
        Creates a new AndreBarman obejct with given ID and moving speed. (Speed of Andre as it heads towards the bar)
        
        @param: ID an integer that define Andre's ID number
        @param: speed an integer that determines Andre's speed as he enters the club    
    */
    public AndreBarman(int ID,int speed)
    {   
        this.ID=ID;
        this.speed=speed;
        inRoom=new AtomicBoolean(false); 
        rand=new Random();
        thirstyThreads=new ArrayList<>();//
        latch=new CountDownLatch(1);
        speeder=new AtomicInteger(1);
    }
    /**
        Adds a club goer to the end of the thirstyThreads ArrayList.
        
        @param:   apt a Clugoer to be added at the end of the queue to get drinks 
    */
    synchronized public static void addToThirsty(Clubgoer apt){thirstyThreads.add(apt);}
    /**
        Sets the grid block of this AndreBarman object.

        @param: block a GridBlock object to set the calling object's grid block        
    */
    public void setGridBlock(GridBlock block){andreGrid=block;}
    /**
        Sets the location of this AndreBarman.

        @param: location PeopleLocation object to set the calling object's location in the club    
    */
    public void setLocation(PeopleLocation location){andreLocation=location;}
    /**
        Sets the club of the caling object.

        @param: grid a ClubGrid object to set the calling object's club data field.
    */
    public void setClub(ClubGrid grid){club=grid;}   
     /**
        Determines if the calling object is in the Club
         
        @return: true when in the club, and false otherwise        
    */
    public boolean andreInRoom(){return inRoom.get();}
    /**
        Finds the location of Andre in the club
        
        @return: PeopleLocation object which represents current AndreBarman's location in the club    
    */
    synchronized public PeopleLocation getAndreLocation(){return andreLocation;}
    /**
        Returns the grid block where Andre is currently.
        
        @return: a GridBlock object which defines the current AndreBarman's position in the clb        
    */
    public GridBlock getAndreGrid(){return andreGrid;}
    /**
        Checks if the pause/Resume button has been pressed, and halts the simulation if pressed.
        
         @throws: InterruptedException - execption thrown by sleep method.   
    */
    private void checkPause() throws InterruptedException
    {
		while(ClubSimulation.paused.get()){
            sleep(speed);// sleep thread for 0.1s before next check if pause pressed
        }  	
        
    } 
    /**
        Sets counter data field of this object.

        @param: c a PeopleCounter object that is to set the counter object of the calling AndreBarman.
    */
    public void setPeopleCounter(PeopleCounter c){counter=c;}
    /**
        Determines if the simulation has been started by pressing "Start" button, and halts the execution if not.        
    */
    private void startSim() throws InterruptedException
     {
            latch.await(); // wait untill the gate is released.
    }
    /**
        Enters AndreBarman into the club, through entrance door.
        
        @throws: InterruptedException - execption thrown by sleep method.
    */
    private void enterClub() throws InterruptedException {
        ClubGrid.andre.set(true);
		andreGrid = club.enterClub(andreLocation);  //enter through entrance
        //System.out.println("Andre's entrace dimensions: ("+andreGrid.getX()+","+andreGrid.getY()+")");
        ClubGrid.andre.set(false);    		
        inRoom.set(true);
        sleep(speed/3);
	}
    /**
        Heads AndreBarman towards the bar.
    
         @throws: InterruptedException - execption thrown by sleep method.
    */
    private void headToBar() throws InterruptedException {
		int x_mv= rand.nextInt(3)-1;	//	-1,0 or 1
		int y_mv= Integer.signum(club.getBar_y()-andreGrid.getY());//-1,0 or 1
		andreGrid=club.move(andreGrid,x_mv,y_mv,andreLocation,true); //head toward bar
	}
    /**
        Serves customers in the club drinks. The customers are served on the basis of first-come-first serves. Adjusts the serving speed accordingly if more and more request keep coming.
    
     @throws: InterruptedException - execption thrown by sleep method.    
                
    */
    private void serveDrinks() throws InterruptedException{
        if(thirstyThreads.size()!=0){
            Clubgoer serve= thirstyThreads.remove(0);
        
            int dist=andreGrid.getX()-serve.currentBlock.getX();
            if(dist<0){
               for(int i=1;i<=Math.abs(dist);i++){
                   checkPause();
                   sleep(100/speeder.get());//wait a bit
                  // if(!club.inPatronArea(andreGrid.getX()+1,andreGrid.getY())){continue;}
                   andreGrid=club.move(andreGrid,1,0,andreLocation,true);                                  
                }
                 checkPause();
               // System.out.println("Serving thread-"+serve.getID()+"Drinks...");//for debug
                sleep(200/speeder.get());//serves drinks
                serve.markServed();//release the thread so it does its thing again 
                counter.incrServed();  //increase the number of threads
                speeder.getAndDecrement();
               // System.out.println("Done.");                             
            }else if(dist>0){
                 for(int i=1;i<=dist;i++){
                   checkPause();
                    sleep(100/speeder.get());//wait a bit
                  // if(!club.inPatronArea(andreGrid.getX()-1,andreGrid.getY())){continue;} 
                   andreGrid=club.move(andreGrid,-1,0,andreLocation,true);                                  
                }
                checkPause();
               // System.out.println("Serving thread-"+serve.getID()+" Drinks...");
                sleep(200/speeder.get());//serves drinks
                serve.markServed();//release the thread so it does its thing again  
                counter.incrServed(); 
                speeder.getAndDecrement();
                //System.out.println("Done.");   
            }else{
                 checkPause();
                //System.out.println("Serving thread-"+serve.getID()+" Drinks...");
                sleep(200/speeder.get());//serves drinks
                serve.markServed();//release the thread so it does its thing again 
                counter.incrServed(); 
                //System.out.println("Done.");  
                speeder.getAndDecrement();               
            }
         }        
    }
    /**
        Run the actual Thread.
    */
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
                Clubgoer.latch.countDown();//set patron in   
                while(true){
                    checkPause();
                    serveDrinks();  
                    sleep(speed/2);     
                }             
            }
            catch(InterruptedException e1) {}
          
    }
}
