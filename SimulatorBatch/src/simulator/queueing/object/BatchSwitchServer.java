package simulator.queueing.object;

import java.util.Iterator;
import java.util.Vector;

import distribution.Distribution;
import distribution.Exponential;

import java.util.Random;

import scenario.Configuration;
import simulator.*;
import simulator.queueing.*;
import simulator.queueing.entry.ServerEntry;
import simulator.queueing.event.*;

/**
 * Server class. It is equivalent to a M-limited polling server.
 * Release the packets by the routing table : 
 * by example : releasing 2 packets at same time towards the same port
 * 
 * IMPORTANT : DIFFERENT WITH "LotServerR"
 * 
 * En train de faire
 * 
 * already backup in the archive 01022017
 *
 */
public class BatchSwitchServer extends BatchServer {

	private Distribution switchDistribution;
	// CONSTRUCTOR
	public BatchSwitchServer()
	{
		super(); //by default as 1 exponential
		this.switchDistribution = new Exponential(50);//by default
	}
	

	// METHODS
	
	/**
	 * Function serveQueue who is called by the function switch : 
	 * Serving a batch of packets at the same time
	 * 
	 * (Function switch is called by Function processDepature,
	 * function processDepature is called by function processService,
	 * function processService is called by the Event "service")
	 */
	public void serveQueue(QDES simu, Queue q)
	{
		/*
		 * 
		 * deciding the size of batch : 
		 * if number of free packets (F) is bigger than M (the batch size)
		 * 		M is choosed
		 * if not
		 * 		F is choosed 
		 *  		 
		 *  New method
		 */
		
		//notification for calculating the proba instant
		Notification numberOfCustomer = new Notification(Notification.Type.WAITING_SIZE, q.getNbCustomers());
		q.declareNotification(numberOfCustomer);
		
		
		this.busy = true; //set the server busy, blocking the server; IMPORTANT
		
		double serviceTimeBatch = 0; //the total time of service for a batch of packets
		int batchsize = 0;
		
		Vector<Vector<BatchCustomer>> subcustomers = new Vector<Vector<BatchCustomer>>();
		for(int i = 0; i < Configuration.N -1 ; i++)//initialization
		{
			subcustomers.addElement(new Vector<BatchCustomer>());
		}
		
		for(int i = 0; i < Configuration.EXTRALOT+1 ;i++)
		{			

			//there is no more clients in the queue
			if (q.getFirstWaitingUnholdCustomer() == null) break;
			
			//get the first unhold clients in the queue
			BatchCustomer c = (BatchCustomer)q.getFirstWaitingUnholdCustomer();
			c.startHold();//important
			c.setCurrentServer(this);//set current server
			int export = c.getExport();
			if (export == 0) System.out.println("***************************************" + "get the wrong export");//debug
			if (export > Configuration.N) System.out.println("***************************************" + "get the wrong export, more bigger");//debug
			
			//choose the service Time
			if (subcustomers.get(export-1).size() == 0) c.setServiceTime(c.getServiceTimeMiss());// the first one
			else c.setServiceTime(c.getServiceTimeHit());// the second one;
			serviceTimeBatch += c.getServiceTime();
			
			//Add customer to subcustomer
			subcustomers.get(export-1).add(c);
			batchsize ++;
			
		}
		
		//debug
//		System.out.println("**************");
//		for(Vector<BatchCustomer> sub : subcustomers)
//		{
//			System.out.println("sub batch size: " + sub.size() );
//		}
//		System.out.println("**************");
		ServiceBatch serviceEvent = new ServiceBatch(simu.getSimTime() + serviceTimeBatch, subcustomers, this, q);
		serviceEvent.schedule(simu);
		
		//the whole batch size
		Notification batchevent = new Notification(Notification.Type.BATCH_SIZE, batchsize);
		q.declareNotification(batchevent);
				
	}
	
	/**
	 * ProcessService for the event ServiceBatch
	 * 
	 * Create all the Departure event for each sub batch of customers
	 */
	public void processService(QDES simu, Vector<Vector<BatchCustomer>> customers, Queue q)
	{
		Vector<BatchCustomer> last = new Vector<BatchCustomer>();
		double rt= 0;//releasing time
		double tt = 0; //total time : releasing time + service time
		double size = 0;
		
		//control the last one TODO:
		for(int i = Configuration.N -2 ; i >= 0 ; i--)
		{
			if(customers.get(i).size() != 0)
			{
				last = customers.get(i);
				break;
			}
		}
		for(Vector<BatchCustomer> sub : customers)
		{
			//no client in one of exports
			if(sub.size() == 0) continue;
			
			for(BatchCustomer c : sub)
			{
				rt +=  c.getServiceTimeReleasing();
				tt += c.getServiceTimeReleasing() + c.getServiceTime();
				size++;
			}
			boolean ifLast = false;
			if(sub == last) ifLast = true;
			Departure departureEvent = new Departure(simu.getSimTime() + rt, sub, this, q, ifLast);
			departureEvent.schedule(simu);	
			
			//for get the sub batch size
//			Notification batchevent = new Notification(Notification.Type.BATCH_SIZE, sub.size());
//			q.declareNotification(batchevent);
		}
		
		//get the average all the service time 
		if (size != 0)
		{
			Notification serviceBatchNotification = new Notification(Notification.Type.SERVICE_TIME_BATCH, tt/size);
			q.declareNotification(serviceBatchNotification);
		}
	}
	
	
	/**
	 * Redefine processService (calling processDeparture for really deleting customer)
	 * change serving a customer into serving a batch of customers
	 * 
	 * @param simu
	 * @param customers
	 */
	//TEMP I change the name from "processService"
	//TODO :
	public void processDeparture(QDES simu, Vector<BatchCustomer> subcustomers, Queue q, boolean last)
	{
		// note that after the last one, switch to next queue
		Iterator<BatchCustomer> it = subcustomers.iterator();
		

		
		while(it.hasNext())
		{
			BatchCustomer c = it.next();
			c.endHold();
			c.endService();
			//debug
			if(c.getCurrentQueue().getId() != q.getId()) System.out.println("**********************The queue is wrong*************");

			q.dequeue(c);
			
			//declaration a notification
			//Notify the departure and the response time
			Notification departureNotification = new Notification(Notification.Type.DEPARTURE, (Double) simu.getSimTime());
			q.declareNotification(departureNotification);
			
			double responseTime = simu.getSimTime() - c.getArrivalTime();
			Notification responseNotification = new Notification(Notification.Type.RESPONSE_TIME, responseTime);
			q.declareNotification(responseNotification);
			
			//all service time (lookup time and releasing time)
			double serviceTime = c.getServiceTime() + c.getServiceTimeReleasing();
			Notification serviceNotification = new Notification(Notification.Type.SERVICE_TIME, serviceTime);
			q.declareNotification(serviceNotification);
		}
		//debug
		//System.out.println("Releasing (QID : " + q.getId() + ") " + simu.getSimTime() + " Customers :" + subcustomers.size());
		//the last one
		if(last)
		{
			this.busy = false; // IMPORTANT
			//this.switchQueue(simu, q);
			this.serveSwitch(simu, q);
		}
		

	}
	
	public void serveSwitch(QDES simu, Queue currentQueue)
	{
		//very important
		this.busy = true;
		
		double switchTime = this.switchDistribution.nextDouble();
		Switch switchEvent = new Switch(simu.getSimTime() + switchTime , this , currentQueue);
		switchEvent.schedule(simu);
	}
	
	
	
	public void processSwitch(QDES simu, Queue currentQueue)
	{
		
		
		this.busy = false;

		// Start vacation
		//if (!currentQueue.inVacation()) {
		if (currentQueue.getFirstWaitingUnholdCustomer() != null && !currentQueue.inVacation()) { // At least one customer waiting to be served
			currentQueue.startVacation(simu);
		}



		
		ServerEntry entry = simu.lookAt(this); // look at the next queue to be served
		Queue nextQueue = entry.getNextQueueToServe(currentQueue);
		
		/////////////////////
		if(!currentQueue.inTotalVacation()) currentQueue.startTotalVacation(simu);
		/////////////////////
		
		/////////////////////
		if(nextQueue.inTotalVacation()) nextQueue.endTotalVacation(simu);
		/////////////////////
		
		if(nextQueue.inVacation()) nextQueue.endVacation(simu);
		
		if (nextQueue.getFirstWaitingUnholdCustomer() != null) {
			
			//nextQueue.endVacation(simu);
			
			this.serveQueue(simu, nextQueue);

		}
		else 
		{
			this.serveSwitch(simu, nextQueue);
			
			if (entry.anyQueueWaitingProcessing()) {

				//System.out.println("the queue is empty");
				

				// ADDED FOR MODEL DEBUG 
				Notification hAttemptNotification = new Notification(Notification.Type.H_ATTEMPT, (Double) simu.getSimTime());
				nextQueue.declareNotification(hAttemptNotification);
				// END ADDED FOR MODEL DEBUG	

			} else { // Other queues empty => totally empty system

				// ADDED FOR MODEL DEBUG
				Notification hAttemptNotification = new Notification(Notification.Type.H_ATTEMPT, (Double) simu.getSimTime());
				nextQueue.declareNotification(hAttemptNotification);

				Notification hSuccessNotification = new Notification(Notification.Type.H_SUCCESS, (Double) simu.getSimTime());
				nextQueue.declareNotification(hSuccessNotification);
				// END ADDED FOR MODEL DEBUG	

			}

		}

	}
	
	public void setSwitchDistribution( Distribution distribution_ )
	{
		this.switchDistribution = distribution_;
	}
	
}