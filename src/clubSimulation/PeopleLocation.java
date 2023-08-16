package clubSimulation;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.atomic.*;

public class PeopleLocation  { // this is a separate class so don't have to access thread
	
	private final int ID; //each person has an ID
	private Color myColor; //colour of the person
	private AtomicBoolean inRoom; //are they in the club?
	private AtomicBoolean arrived; //have they arrived at the club?
	private GridBlock location; //which GridBlock are they on?
	
	PeopleLocation(int ID ) {
		Random rand = new Random();
		float c = rand.nextFloat(); //bit of a hack to get different colours
		myColor = new Color(c, rand.nextFloat(), c);	//only set at beginning	by thread
		inRoom = new AtomicBoolean(false); //not in club
		arrived = new AtomicBoolean(false); //have not arrive outside
		this.ID=ID;
	}
	
	//setter
	synchronized public  void setInRoom(boolean in) {
		this.inRoom.set(in);
	}
	
	//getter and setter
	public boolean getArrived() {
		return arrived.get();
	}
	public void setArrived() {
		this.arrived.set(true);
	}
//Kany: These methds should be synchronized so that Threads do not set the same location simulteneously

//getter and setter
	synchronized public GridBlock getLocation() {
		return location;
	}
	synchronized public  void setLocation(GridBlock location) {
		this.location = location;
	}

	//getter
	synchronized public  int getX() { return location.getX();}	
	
	//getter
	synchronized public  int getY() {	return location.getY();	}
	
	//getter
	synchronized public  int getID() {	return ID;	}

	//getter
	public  synchronized boolean inRoom() {
		return inRoom.get();
	}
	//getter and setter
	public synchronized  Color getColor() { return myColor; }
	public synchronized  void setColor(Color myColor) { this.myColor= myColor; }
}
