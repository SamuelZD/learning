package simulator.queueing.event;

import simulator.DES;
import simulator.queueing.QDES;
import simulator.queueing.entry.SourceEntry;
import simulator.queueing.object.Customer;
import simulator.queueing.object.HeterogeneousCustomer;
import simulator.queueing.object.Source;
import simulator.queueing.object.HeterogeneousSource;

import java.util.Random;

public class HeterogeneousSourceTransmission extends SourceTransmission {
	
	// CONSTRUCTOR
	public HeterogeneousSourceTransmission(double date, Source source_) {
		super(date, source_);
	}
	
	// METHODS
	@Override
	public void execute(DES simu) {

		//Create a new customer
		HeterogeneousSource s = ((HeterogeneousSource) this.getSource());

		double serviceTime = s.getServiceTimeDistribution().nextDouble();
		Customer c = new HeterogeneousCustomer();
		
		((HeterogeneousCustomer) c).setServiceTime(serviceTime);

		c.setSource(s);
		
		// Find the next queue and process the corresponding arrival
		SourceEntry entry = ((QDES) simu).lookAt(s);
		entry.getTargetedQueue().processArrival((QDES) simu, c);
		
		// Schedule the next source transmission
		s.scheduleNextTransmission((QDES) simu);
				
	}

}
