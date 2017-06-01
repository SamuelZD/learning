package simulator.queueing;

import simulator.*;
import simulator.queueing.object.*;
import simulator.queueing.entry.*;

/**
 * QDES class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class QDES extends DES {

	// CONSTRUCTOR
	public QDES() {
		super();
		this.simObjects = new QSimObjects(); // DO NOT FORGET !
	}
	
	// METHODS
	public void register(Source source) {
		((QSimObjects) this.simObjects).register(source);
	}
	
	public void register(Queue queue) {
		((QSimObjects) this.simObjects).register(queue);
	}
	
	public void register(Server server) {
		((QSimObjects) this.simObjects).register(server);
	}
	
	public void attach(Source source, Queue queue) {
		((QSimObjects) this.simObjects).attach(source, queue);
	}
	
	public void attach(Queue queue, Server server) {
		((QSimObjects) this.simObjects).attach(queue, server);
	}
	
	public SourceEntry lookAt(Source source) {
		return ((QSimObjects) this.simObjects).lookAt(source);
	}
	
	public QueueEntry lookAt(Queue queue) {
		return ((QSimObjects) this.simObjects).lookAt(queue);
	}
	
	public ServerEntry lookAt(Server server) {
		return ((QSimObjects) this.simObjects).lookAt(server);
	}
	
	public SimObjects getSimObjects()
	{
		return this.simObjects;
	}
}
