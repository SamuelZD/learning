package simulator.queueing.object;


import distribution.Distribution;
import distribution.Exponential;
import simulator.Notification;
import simulator.queueing.QDES;
import simulator.queueing.entry.ServerEntry;
import simulator.queueing.event.Switch;

public class SimpleSwitchServer extends HeterogeneousServer {
	
	private Distribution switchDistribution;

	public SimpleSwitchServer() {
		super();
		// // Set by default an exponential distribution with rate 1
		this.switchDistribution = new Exponential(1);
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
	
public void processDeparture(QDES simu, Customer c) {
		
		c.endHold();
		Queue currentQueue = c.getCurrentQueue();
		currentQueue.dequeue(c);
		
		// Notify the departure and the response time
		Notification departureNotification = new Notification(Notification.Type.DEPARTURE, (Double) simu.getSimTime());
		currentQueue.declareNotification(departureNotification);
		
		double responseTime = simu.getSimTime() - c.getArrivalTime();
		Notification responseTimeNotification = new Notification(Notification.Type.RESPONSE_TIME, responseTime);
		currentQueue.declareNotification(responseTimeNotification);
		
		// END OF SERVICE => COLLECT DATA TO COMPUTE F AND G PROBABILITIES
		if (currentQueue.isEmpty()) {
			Notification fAttemptNotification = new Notification(Notification.Type.F_ATTEMPT, (Double) simu.getSimTime());
			currentQueue.declareNotification(fAttemptNotification);
			
			if (simu.lookAt(c.getCurrentServer()).allQueuesEmpty()) {
				Notification fSuccessNotification = new Notification(Notification.Type.F_SUCCESS, (Double) simu.getSimTime());
				currentQueue.declareNotification(fSuccessNotification);
			}
			
		} else {
			Notification gAttemptNotification = new Notification(Notification.Type.G_ATTEMPT, (Double) simu.getSimTime());
			currentQueue.declareNotification(gAttemptNotification);
			
			if (simu.lookAt(c.getCurrentServer()).otherQueuesEmpty(currentQueue)) {
				Notification gSuccessNotification = new Notification(Notification.Type.G_SUCCESS, (Double) simu.getSimTime());
				currentQueue.declareNotification(gSuccessNotification);
			}
			
		}
		// END COLLECT DATA FOR MODEL DEBUG
		
		
		this.serveSwitch(simu, currentQueue);
		
	}
	
	public void setSwitchDistribution( Distribution distribution_ )
	{
		this.switchDistribution = distribution_;
	}
	
	

}
