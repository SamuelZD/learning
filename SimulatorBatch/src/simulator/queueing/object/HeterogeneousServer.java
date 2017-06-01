package simulator.queueing.object;

import distribution.Distribution;
import distribution.Exponential;
import simulator.queueing.QDES;
import simulator.queueing.event.Service;

public class HeterogeneousServer extends SimpleServer {

	
	public HeterogeneousServer() {
		super(new Exponential(1));
		// Set by default an exponential distribution with rate 1
	}

	public void serveQueue(QDES simu, Queue q){
		
		HeterogeneousCustomer c = (HeterogeneousCustomer) q.getFirstWaitingUnholdCustomer();		
		c.setCurrentServer(this);
		this.busy = true;
		c.startHold();
		c.startService();
		double serviceTime = c.getServiceTime();
		Service serviceEvent = new Service(simu.getSimTime()+serviceTime, c);
		serviceEvent.schedule(simu);
	}
	
	
}
