package simulator.queueing.object;

import simulator.*;
import simulator.queueing.*;
import simulator.queueing.entry.*;

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Vector;

import scenario.Configuration;

/**
 * Queue class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class Queue extends SimObject {
	
	// ATTRIBUTES
	private static int nbQueuesGenerated = 0;
	
	private int id;
	private int capacity; // -1 means infinite capacity queue
	private List<Customer> customers;
	private boolean inVacation;
	private boolean inTotalVacation;
	private int count = Configuration.EXTRALOT;
	private int countExhaust = 0; //count used for exhaust M limited
	private boolean firsTime = true;
	
	public boolean getFirstTime(){
		return this.firsTime;
	}
	
	public void setFirstTime(boolean ft ){
		this.firsTime = ft;
	}
	
	public int getCount()
	{
		return count;
	}
	
    public void setCount(int count_)
    {
    	this.count = count_;
    }
	
    public int getCountExhauste()
    {
    	return this.countExhaust;
    }
    
    public void setCountExhauste(int count_)
    {
    	this.countExhaust = count_;
    }
    
	// CONSTRUCTOR
	public Queue(int capacity_) {
		this.id = (++nbQueuesGenerated);
		this.capacity = capacity_;
		this.customers = new Vector<Customer>();
		this.inVacation = false;
		this.inTotalVacation = false;
	}
	
	public Queue() { // means infinite capacity queue
		this(-1);
	}
	
	// METHODS
	
	public int getId() {
		return this.id;
	}
	
	
	public int getCapacity() {
		return this.capacity;
	}
	
	public int getNbCustomers() {
		return this.customers.size();
	}
	
	public boolean isEmpty() {
		return (this.getNbCustomers() == 0);
	}
	
	public boolean isFull() {
		return (this.getNbCustomers() == this.capacity);
	}

	public void queue(Customer c) {
		if (!this.isFull()) {
			this.customers.add(c); // CHECK IF ADDED AT THE END OF THE LIST (OK FOR VECTOR);
		}
	}
	
	public void dequeueHead() {
		this.customers.remove(0); // CHECK IF FIRST PACKET COME TO BE REMOVED 
	}
	
	public void dequeue(Customer c) {
		this.customers.remove(c);
	}
	
	public void processArrival(QDES simu, Customer c) {
		
		if (this.isFull()) {
			
			// The customer is blocked => Notify the observers
			Notification blockingNotification = new Notification(Notification.Type.BLOCKING);
			this.declareNotification(blockingNotification);
			
		} else {
			
			this.queue(c); // Queue the customer
			c.setPreviousQueue(c.getCurrentQueue());
			c.setCurrentQueue(this);		
			c.setArrivalTime(simu.getSimTime()); // for computing statistics
			
			// Notify the observers of the arrival
			Notification arrivalNotification = new Notification(Notification.Type.ARRIVAL, (Double) simu.getSimTime(), simu);
			this.declareNotification(arrivalNotification);
			
			// LOOK IF THE CUSTOMER COULD BE PROCESSED IMMEDIATELY BY A UNOCCUPIED SERVER
			QueueEntry entry = simu.lookAt(this);
			Server server = entry.getNextUnoccupiedServer();
			if (server != null) {
				
				server.serveQueue(simu, this);
				
			} else {
				
				// THIS CODE MUST BE VALIDATED
				if (!this.inVacation) {
					this.startVacation(simu);
				}
				// THE PREVIOUS CODE MUST BE VALIDATED
			}
			
		}
	}
	
	public Customer getFirstWaitingUnholdCustomer() { // WARNING: ASSUME FIFO BEHAVIOUR
		
		/* TEST ITERATION ON CUSTOMERS
		
		System.out.println("**** NEW TEST ****");		
		Iterator<Customer> iterTest = this.customers.iterator();
		Customer cTest;
		for (int k=0; k<3; k++) {
			if (iterTest.hasNext()) {
				cTest = iterTest.next();
				System.out.println(cTest);
			}
		}
		System.out.println("**** TEST RESTART ITER ****");
		iterTest = this.customers.iterator();
		while (iterTest.hasNext()) {
			cTest = iterTest.next();
			System.out.println(cTest);
		}
		
		END TEST ITERATION ON CUSTOMERS */
		
		Iterator<Customer> iter = this.customers.iterator();
		Customer c;
		while (iter.hasNext()) {
			c = iter.next();
			if (! c.hold() ) {
				return c;
			}
		}
		return null;
	}
	
	public Customer getCustomer(int i) {
		return this.customers.get(i);
	}
	
	/**
	 * toString
	 */
	public String toString() {
		return "Queue "+this.id;
	}
		
	@Override
	public void init(DES simu) {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

	public void startVacation(QDES simu) {
		this.inVacation = true;
		// Notify the observers of the arrival
		Notification startVacationNotification = new Notification(Notification.Type.START_VACATION, (Double) simu.getSimTime(), simu);
		this.declareNotification(startVacationNotification);
	}
	
	public void endVacation(QDES simu) {
		this.inVacation = false;
		Notification endVacationNotification = new Notification(Notification.Type.END_VACATION, (Double) simu.getSimTime(), simu);
		this.declareNotification(endVacationNotification);
	}
	
	public void startTotalVacation(QDES simu)
	{
		this.inTotalVacation = true;
		Notification startTV = new Notification(Notification.Type.START_TV, (Double) simu.getSimTime());
		this.declareNotification(startTV);
	}
	
	public void endTotalVacation(QDES simu)
	{
		this.inTotalVacation = false;
		Notification endTV = new Notification(Notification.Type.END_TV, (Double) simu.getSimTime());
		this.declareNotification(endTV);
	}
	
	public boolean inVacation() {
		return this.inVacation;
	}
	
	public boolean inTotalVacation()
	{
		return this.inTotalVacation;
	}
	
}
