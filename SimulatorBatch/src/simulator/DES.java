package simulator;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import scenario.Configuration;

/**
 * Discrete-Event Simulator class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class DES {
	
	// ATTRIBUTES
	private double simTime;
	private double endSimTime;
	private List<Event> eventList;
	protected SimObjects simObjects;
		
	// CONSTRUCTOR
	public DES() {
		this.simTime = 0;
		this.endSimTime = Configuration.SIMULATION_TIME;
		this.eventList = new Vector<Event>();
	}
	
	// METHODS

	public double getSimTime() {
		return this.simTime;
	}
	
	public double getEndSimTime() {
		return this.endSimTime;
	}
	
	public void setSimTime(double t) {
		this.simTime = t;
	}
	
	/*
	 * Insert an event.
	 */
	public void insertEvent(Event e) {
		double eventDate = e.getDate();
		// Searching for the right inserting position
		Iterator<Event> iter = this.eventList.iterator();
		int index = 0;
		while (iter.hasNext() && iter.next().getDate()<=eventDate ) {
			index++;
		}
		// Inserting the event in the event list
		this.eventList.add(index, e);
	}
	
	/*
	 * Is there a next event to process ?
	 */
	public boolean hasNextEvent() {
		return !(this.eventList.isEmpty());
	}
	
	/*
	 * Process next event.
	 */
	public void processNextEvent() {
		Event e = this.eventList.get(0);
		this.setSimTime(e.getDate());
		this.eventList.remove(e); // NOT IN LAST POSITION
		e.execute(this);		
	}
	
	
	/*
	 * Remove an event from the event list
	 */
	public void cancel(Event e) {
		this.eventList.remove(e);
	}

	/*
	 * Initialize the simulation.
	 */
	public void init() {
		this.simObjects.init(this);
	}
	
	/*
	 * Get the results.
	 */
	public void getResults() {
		this.simObjects.getResults();
	}
	
	/*
	 * Trace the results.
	 */
	public void traceResults(String outputFile) {
		this.simObjects.traceResults(outputFile);
	}
	
	/*
	 * Trace the instante probability 
	 */
	public void traceProbaInstant(String outputFile)
	{
		this.simObjects.traceProbaInstant(outputFile);
	}
	/*
	 * Run the simulation.
	 */
	public void run() {
		System.out.println("* Starting simulation");
		while(this.getSimTime()<=this.getEndSimTime() && this.hasNextEvent()) {
			this.processNextEvent();
		}
		System.out.println("* Simulation completed in "+this.getSimTime()+" "+Configuration.TIME_UNIT);
	}
}
