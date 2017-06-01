package simulator.queueing.object;

/**
 * Gated_M_limited
 */

import distribution.Distribution;
import distribution.Exponential;
import scenario.Configuration;
import simulator.Notification;
import simulator.queueing.QDES;
import simulator.queueing.entry.ServerEntry;
import simulator.queueing.event.Service;

public class LotServerWithCache extends HeterogeneousServer {
	
	private boolean one = true;
	
	
	public LotServerWithCache(){
		super();
		//by default, the same with HeterogeneousServer
	}

	
	public void processService(QDES simu, Customer c) {	
		//this.busy = false;
		c.endService();
		this.processDeparture(simu, c);
	}
	

	
	public void serveQueue(QDES simu, Queue q)
	{
		/*
		if(simu.getSimTime() > 180000 && one)
		{
			System.out.println("********************" + q.getNbCustomers());
			one = false;
		}
		 */
		
		//exactly same with father
		//I didn't change it.

		HeterogeneousCustomer c = (HeterogeneousCustomer) q.getFirstWaitingUnholdCustomer();		
		c.setCurrentServer(this);
		this.busy = true;
		c.startHold();
		//c.startService();
		double serviceTime = c.getServiceTime();
		serviceTime = q.getFirstTime() ? serviceTime : (serviceTime/100);
		Service serviceEvent = new Service(simu.getSimTime()+serviceTime, c);
		serviceEvent.schedule(simu);

	}
	
	public void switchQueue(QDES simu, Queue currentQueue) {
		
		//currentQueue finishes its service, so how we can change others service time ?
		//currentQueue.setCount(currentQueue.getCount() - 1);
		
		if(currentQueue.getFirstTime())
		{
			if(currentQueue.getNbCustomers() < Configuration.EXTRALOT)
			{
				currentQueue.setCount(currentQueue.getNbCustomers());
				
			}
			
			currentQueue.setFirstTime(false);
		}
		
		if(currentQueue.getCount()!= 0)
		{
			if(currentQueue.getFirstWaitingUnholdCustomer() != null )
			{
				currentQueue.setCount(currentQueue.getCount() - 1 );
				currentQueue.endVacation(simu);
				this.serveQueue(simu, currentQueue);
			}
			else
			{
				currentQueue.setCount(0);
				this.switchQueue(simu, currentQueue);
			}
			
		}
		else{ // count = 0 
			
           
			// Start vacation
			if (currentQueue.getFirstWaitingUnholdCustomer() != null && !currentQueue.inVacation()) { // At least one customer waiting to be served
				
				currentQueue.startVacation(simu);
				

				
			}
			
			///////////////////////
			currentQueue.setCount(Configuration.EXTRALOT);
			currentQueue.setFirstTime(true);
			///////////////////////

			/////////////////////
			this.busy = false;
			////////////////////
			
			ServerEntry entry = simu.lookAt(this); // look at the next queue to be served
			Queue nextQueue = entry.getNextQueueToServe(currentQueue);


			if (nextQueue.getFirstWaitingUnholdCustomer() != null) { // Serve the customer

				// At least one customer waiting to be served
				nextQueue.endVacation(simu);
				this.serveQueue(simu, nextQueue);

			} else if (entry.anyQueueWaitingProcessing()) {


				// ADDED FOR MODEL DEBUG 
				Notification hAttemptNotification = new Notification(Notification.Type.H_ATTEMPT, (Double) simu.getSimTime());
				nextQueue.declareNotification(hAttemptNotification);
				// END ADDED FOR MODEL DEBUG	


				// Program a switching event
				this.switchQueue(simu, nextQueue);

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

	
	
	
	
	
}
