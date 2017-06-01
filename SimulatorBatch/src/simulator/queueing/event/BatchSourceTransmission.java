package simulator.queueing.event;

import java.util.Random;

import scenario.Configuration;
import simulator.DES;
import simulator.queueing.QDES;
import simulator.queueing.entry.SourceEntry;
import simulator.queueing.object.Customer;
import simulator.queueing.object.BatchCustomer;
import simulator.queueing.object.BatchSource;
import simulator.queueing.object.Source;

public class BatchSourceTransmission extends HeterogeneousSourceTransmission {

	//CONSTRUTCOR
	public BatchSourceTransmission(double date, Source source_) {
		super(date, source_);
		// TODO Auto-generated constructor stub
		
	}

	
	// METHODS
	@Override
	public void execute(DES simu) {

		//Create a new customer
		BatchSource s = ((BatchSource) this.getSource());

		double serviceTimeHit = s.getServiceTimeHitDistribution().nextDouble();
		double serviceTimeMiss = s.getServiceTimeMissDistribution().nextDouble();
		double releaseTime = s.getReleaseTimeDistribution().nextDouble();
		int export = s.getExport();
		
		Customer c = new BatchCustomer(serviceTimeHit, serviceTimeMiss, releaseTime);
		((BatchCustomer) c).setExport(export);
		
		c.setSource(s);
		
		// Find the next queue and process the corresponding arrival
		SourceEntry entry = ((QDES) simu).lookAt(s);
		entry.getTargetedQueue().processArrival((QDES) simu, c);
		
		// Schedule the next source transmission
		s.scheduleNextTransmission((QDES) simu);
				
	}
}
