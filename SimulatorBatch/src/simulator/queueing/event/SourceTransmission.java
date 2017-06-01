package simulator.queueing.event;
import simulator.*;
import simulator.queueing.*;
import simulator.queueing.object.*;
import simulator.queueing.entry.*;

/**
 * SourceTransmission class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class SourceTransmission extends Event {

	// ATTRIBUTES
	private Source source;
	private double proba = 0.1; //the probability of customer in flow table
	
	// CONSTRUCTOR
	public SourceTransmission(double date, Source source_) {
		super(date);
		this.source = source_;
	}

	// METHODS
	
	@Override
	public void execute(DES simu) {

		//Create a new customer
		Customer c = new Customer();
		c.setSource(this.source);
		
		
		// Find the next queue and process the corresponding arrival
		SourceEntry entry = ((QDES) simu).lookAt(this.source);
		entry.getTargetedQueue().processArrival((QDES) simu, c);
		
		// Schedule the next source transmission
		this.source.scheduleNextTransmission((QDES) simu);
				
	}
	
	public Source getSource() {
		return this.source;
	}
	
}
