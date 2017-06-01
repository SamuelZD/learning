package simulator.queueing.object;

import distribution.Distribution;
import simulator.queueing.QDES;
import simulator.queueing.event.HeterogeneousSourceTransmission;

public class HeterogeneousSource extends Source {

	// ATTRIBUTES
	Distribution serviceTimeDistribution;
	
	// CONSTRUCTOR
	public HeterogeneousSource(Distribution interArrivalDistribution_, Distribution serviceTimeDistribution_) {
		super(interArrivalDistribution_);
		this.serviceTimeDistribution = serviceTimeDistribution_;
	}
	
	// METHODS
	public Distribution getServiceTimeDistribution() {
		return this.serviceTimeDistribution;
	}

	
	public void scheduleNextTransmission(QDES simu) {
		
		// Draw the inter-arrival time at random according to the selected distribution
		double it = this.getInterArrivalDistribution().nextDouble();
		HeterogeneousSourceTransmission st = new HeterogeneousSourceTransmission(simu.getSimTime()+it, this);
		// Schedule the event in the simulator
		st.schedule(simu);
		
	}
	
}
