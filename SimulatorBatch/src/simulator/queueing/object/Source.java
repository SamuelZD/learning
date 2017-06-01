package simulator.queueing.object;

import distribution.*;
import simulator.*;
import simulator.queueing.*;
import simulator.queueing.event.*;

import java.util.Observable;

/**
 * Source class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class Source extends SimObject {

	// ATTRIBUTES
	private static int nbSourcesGenerated = 0;
	
	private int id;
	private Distribution interArrivalDistribution;
	
	// CONSTRUCTOR
	public Source(Distribution interArrivalDistribution_) {
		this.id = (++nbSourcesGenerated);
		this.interArrivalDistribution = interArrivalDistribution_;
	}
	
	// METHODS
	
	public String toString() {	
		return "Source "+this.id;
	}
	

	public int getId() {
		return this.id;
	}
	
	public void scheduleNextTransmission(QDES simu) {
		
		// Draw the inter-arrival time at random according to the selected distribution
		double it = this.interArrivalDistribution.nextDouble();
		SourceTransmission st = new SourceTransmission(simu.getSimTime()+it, this);
		// Schedule the event in the simulator
		st.schedule(simu);
		
	}
	
	public Distribution getInterArrivalDistribution() {
		return this.interArrivalDistribution;
	}
	
	@Override
	public void init(DES simu) {
		this.scheduleNextTransmission((QDES) simu);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

}
