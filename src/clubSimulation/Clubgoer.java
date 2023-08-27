//M. M. Kuttel 2023 mkuttel@gmail.com
package clubSimulation;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
/*
 This is the basic ClubGoer Thread class, representing the patrons at the club


@author: M.M Kuttel
@edited by: Kananelo Chabeli
 */

public class Clubgoer extends Thread {
	/**
        A shared club for the simulation. synchronization mechanism need to be employed on this object.    
    */
	public static ClubGrid club; //shared club
    /**
        GridBlock object that represents this Clubgoer's current grid block in the club    
    */
	GridBlock currentBlock;
    /**
        Random object for generating some random behavoiur between of threads
    */
	private Random rand;
    /**
        this Clubgoer's current speed in the club
    */ 
	private int movingSpeed;
    /**
            
    */
	private static PeopleLocation barmanLocation;
    /**
        PeopleLocation object that represent this Clubgoer's current location in the club    
    */
	private PeopleLocation myLocation;
    /**
        Boolean flag that determines if this Clubgoer is in the club or  not    
    */
	private boolean inRoom;
    /**
        Boolean flag that determines if this Clubgoer, is thirsty. When set to true, the thread  heads to he bar to get drinks    
    */
	private boolean thirsty;
    /**
        Boolean flag that determines if the thread want's to leave. Set at random        
    */
	private boolean wantToLeave;
    /**
        Integer that represents thsi Clubgoer's unique Identity number    
    */
	private int ID; //thread ID 
    /**
        Boolean flag that dertimines if this Thread ahs been served drinks
    */
    private boolean served;
    /**
        CountDownLatch object that forces threads to wait before the user presses start AND the AndreBarman thread reach the bar blocks of the  club    
    */
    public static CountDownLatch latch; // A count down latch to make all threads weight untill

	/**
        Creates a new thread with given ID, Location and speed.
        
        @param: ID an integer that uniquely identifies threads in the class
        @param: loc PeopleLocation object that defines th current locatio of the thread in the club
        @param: speed an integer that represents the speed of the patron in the club
    */    
	Clubgoer( int ID,  PeopleLocation loc,  int speed) {
		this.ID=ID;
		movingSpeed=speed; //range of speeds for customers
		this.myLocation = loc; //for easy lookups
		inRoom=false; //not in room yet
		thirsty=true; //thirsty when arrive
        served=false;
		wantToLeave=false;	 //want to stay when arrive
        served=false;
		rand=new Random();
	}
    /**
        Marks this Clubgoer served by setting boolean instance variable "served" to true.    
    */
    public void markServed(){served=true;}
	/**
        Returns true if this Clubgoer is in the club, and false otherwise
    */
	public  boolean inRoom() {
		return inRoom;
	}
	
	/**
        Returns current x-coordinate of this Clubgoer
    
        @return: an integer representing the current x-coordinate of the patron    
    */
	public   int getX() { return currentBlock.getX();}	
	
	/**
        Returns current y-coordinate of this Clubgoer
    
        @return: an integer representing the current y-coordinate of the patron    
    */
	public   int getY() {	return currentBlock.getY();	}
	
	/**
        Returns the speed of this Clubgoer.
        
        @return: an integer representing the thread's speed in the club    
    */
	public   int getSpeed() { return movingSpeed; }
   
	/**
            
    */
    public static void setBarmanPosition(PeopleLocation location){barmanLocation=location;}
    /**
        Forces this thread to wait if the entrance block is currently occupied. 
        
        @throw InterrupedExcepion when the Thread.sleep() method is interruped.
        @see Thread.sleep()   
    */
    private void waitAtEntrance() throws InterruptedException{
        while(club.whereEntrance().occupied()){sleep(movingSpeed);}        
    }
    /**
        Checks if the button is pressed and halts the movement of this Thread if so. (spin wait)
        
        @throw InterrupedExcepion when the Thread.sleep() method is interruped.
        @see Thread.sleep()     
    
    */
	private void checkPause() throws InterruptedException
     {
        
		while(ClubSimulation.paused.get()){
            sleep(movingSpeed/5);// sleep thread for 0.1s before next check if pause pressed
        }  	
        
    } 
    /**
            Returns this thread's ID number
            
            @return: ID instance variable of the calling object.    
    
    */
    public int getID(){return ID;}  
    /**
        Forces All thread to wait, until the start button is pressed.
 
        @throw InterrupedExcepion when the Thread.sleep() method is interruped.
        @see Thread.sleep()   
    */
	private void startSim() throws InterruptedException
     {
            latch.await(); // wait untill the gate is released.
    }
	/**
        When this thread's instance variable <code>thirsty</code> is set to true, the calling thread heads to the bar by invoking this method,a and when it is onfron of the Bar, it waits there until Andreman serves drinks. If the thread waits for more than 5 seconds before being served,it oborts the mission of getting drink and will try again later. This is done to prevent deadlocks. 

     @throw InterrupedExcepion when the Thread.sleep() method is interruped.
        @see Thread.sleep()     
            
    */
	private void getDrink() throws InterruptedException{    
                //System.out.println("Thread-"+ID+" going to get drinks");
                sleep(movingSpeed/5);//wait a bit;
                AndreBarman.speeder.getAndIncrement();//increase serving speed of Andre
                if(currentBlock.getY()==club.getBar_y()-1)//when at teh counter (not the Bar)
	            {       
                 checkPause();  
                 AndreBarman.addToThirsty(this);// add the thread to waiting list
                 int checks=0;
                 while(!served){
                     
                     if(checks==10) break;
                     sleep(movingSpeed); // wait until served.
                     checks++;
                 }
                 checkPause();
                 thirsty=false;                        
                }   
		}
	//--------------------------------------------------------
	//DO NOT CHANGE THE CODE BELOW HERE - it is not necessary
	//clubgoer enters club
	public void enterClub() throws InterruptedException {
            currentBlock = club.enterClub(myLocation);  //enter through entrance
		    inRoom=true;
		    //System.out.println("Thread "+this.ID + " entered club at position: " + currentBlock.getX()  + " " +currentBlock.getY() );
		    sleep(movingSpeed);  //wait a bit at door
	}
	//go to bar
	private void headToBar() throws InterruptedException {
		int x_mv= rand.nextInt(3)-1;	//	-1,0 or 1
		int y_mv= Integer.signum(club.getBar_y()-1-currentBlock.getY());//-1,0 or 1(Threads are not allowed on the Bar Counter (only the Barman)
		currentBlock=club.move(currentBlock,x_mv,y_mv,myLocation,false); //head toward bar
		//System.out.println("Thread "+this.ID + " moved toward bar to position: " + currentBlock.getX()  + " " +currentBlock.getY() );
		sleep(movingSpeed/2);  //wait a bit
	}
	//go head towards exit
	private void headTowardsExit() throws InterruptedException {
		GridBlock exit= club.getExit();
		int x_mv= Integer.signum(exit.getX()-currentBlock.getX());//x_mv is -1,0 or 1
		int y_mv= Integer.signum(exit.getY()-currentBlock.getY());//-1,0 or 1
		currentBlock=club.move(currentBlock,x_mv,y_mv,myLocation,false); 
		//System.out.println("Thread "+this.ID + " moved to towards exit: " + currentBlock.getX()  + " " +currentBlock.getY() );
		sleep(movingSpeed);//wait a bit
	}
	//dancing in the club
	private void dance() throws InterruptedException {		
		for(int i=0;i<3;i++) { //sequence of 3
			int x_mv= rand.nextInt(3)-1; //-1,0 or 1
			int y_mv=Integer.signum(1-x_mv);
			
			for(int j=0;j<4;j++) { //do four fast dance steps
					currentBlock=club.move(currentBlock,x_mv,y_mv, myLocation,false);
                    currentBlock.setIsOccupied(ID);	
					sleep(movingSpeed/5); 
					x_mv*=-1;
					y_mv*=-1;
			}
			checkPause();
		}
	}
	//wandering about  in the club
		private void wander() throws InterruptedException {		
			for(int i=0;i<2;i++) { ////wander for two steps
				int x_mv= rand.nextInt(3)-1; //-1,0 or 1
				int y_mv= Integer.signum(-rand.nextInt(4)+1); //-1,0 or 1  (more likely to head away from bar)
				currentBlock=club.move(currentBlock,x_mv,y_mv, myLocation,false);
                currentBlock.setIsOccupied(ID);	
				sleep(movingSpeed); 
			}
		}
	//leave club
	private void leave() throws InterruptedException {
		club.leaveClub(currentBlock,myLocation);		
		inRoom=false;
	}	
	public void run() {
		try {
			startSim(); 
			checkPause();
			sleep(movingSpeed*(rand.nextInt(100)+1)); //arriving takes a while
			checkPause();
			myLocation.setArrived();
			checkPause(); //check whethere have been asked to pause
            waitAtEntrance();
			enterClub();
			while (inRoom) {	
				checkPause(); //check every step
				if((!thirsty)&&(!wantToLeave)) {
					if (rand.nextInt(100) >95){ 
						thirsty = true; //thirsty every now and then
                        served=false;
					}else if (rand.nextInt(100) >98) {
						wantToLeave=true; }//at some point want to leave
				}
				
				if (wantToLeave) {	 //leaving overides thirsty	
					sleep(movingSpeed/5);  //wait a bit		
					if (currentBlock.isExit()) { 
						leave();
						//System.out.println("Thread "+this.ID + " left club");
					}
					else {
						//System.out.println("Thread "+this.ID + " going to exit" );
						headTowardsExit();
					}				 
				}
				else if (thirsty) {
					//sleep(movingSpeed/5);  //wait a bit		
					if (currentBlock.getY()==club.getBar_y()-1) {//get drink when at the counter
						getDrink();//
						//System.out.println("Thread "+this.ID + " got drink");
					}
					else {
						//System.out.println("Thread "+this.ID + " going to getDrink");
						headToBar();
					}
				}
				else {
					if (currentBlock.isDanceFloor()) {
						dance();
						//System.out.println("Thread "+this.ID + " dancing " );
					}
				wander();
				//System.out.println("Thread "+this.ID + " wandering about " );
				}
			}
			//System.out.println("Thread "+this.ID + " is done");

		} catch (InterruptedException e1) {  //do nothing
		}
        
	}
	
}
