package simulator.queueing.object;

import distribution.*;
import simulator.*;
import simulator.queueing.*;
import simulator.queueing.event.*;

/**
 * Server class. It is equivalent to a 1-limited polling server.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class SimpleServer extends Server {

	// ATTRIBUTES
	protected Distribution serviceDistribution;
	
	// CONSTRUCTOR
	public SimpleServer(Distribution serviceDistribution_) {
		super();
		this.serviceDistribution = serviceDistribution_;
		
	}
	
	// METHODS
	public void serveQueue(QDES simu, Queue q){
		
		Customer c = q.getFirstWaitingUnholdCustomer();		
		c.setCurrentServer(this);
		this.busy = true;
		c.startHold();
		c.startService();
		double serviceTime = this.serviceDistribution.nextDouble();
		Service serviceEvent = new Service(simu.getSimTime()+serviceTime, c);
		serviceEvent.schedule(simu);
	}
	
	public void processService(QDES simu, Customer c) {
		this.busy = false;
		c.endService();
		this.processDeparture(simu, c);
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
		
		
		this.switchQueue(simu, currentQueue);
		
	}
	
}
