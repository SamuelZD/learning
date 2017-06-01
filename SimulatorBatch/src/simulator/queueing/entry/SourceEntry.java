package simulator.queueing.entry;

import simulator.*;
import simulator.queueing.object.*;
import scenario.*;

import java.util.Observable;

/**
 * SourceEntry class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class SourceEntry extends SimObjectEntry {

	// NOTES ON ATTRIBUTES
	// The associated simulation objects are queues
	
	// CONSTRUCTOR
	public SourceEntry(Source observedSource) {
		super(observedSource);
	}
	
	// METHODS
	public Queue getTargetedQueue() { // SIMPLY RETURN THE FIRST ONE AT THIS STAGE
		return (Queue) this.associatedSimObjects.get(0);
	}
	
	@Override
	public void update(Observable observedSource, Object notificationType) {
		// TODO
		
	}

	@Override
	public void getResults() {
		System.out.println("\nParameters of "+this.getObservedSimObject());
		System.out.println("- lambda_in = "+(1.0/((Source) this.getObservedSimObject()).getInterArrivalDistribution().getMean())+" cust./"+Configuration.TIME_UNIT+" (computed from the mean)");	
	}

	
}
