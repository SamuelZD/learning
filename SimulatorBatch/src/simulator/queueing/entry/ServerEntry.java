package simulator.queueing.entry;

import simulator.*;
import simulator.queueing.object.*;

import java.util.Iterator;
import java.util.Observable;

/**
 * ServerEntry class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class ServerEntry extends SimObjectEntry {

	// ATTRIBUTES
	// The associated simulation objects are queues
		
	// CONSTRUCTOR
	public ServerEntry(Server observedServer) {
		super(observedServer);
	}
		
	// METHODS
	public Queue getNextQueueToServe(Queue currentQueue) { // Round robin fashion
		Iterator<SimObject> iter = this.associatedSimObjects.iterator();
		while (!( (Queue) iter.next()).equals(currentQueue)) {
			// Searching for the place just after the current queue
		}
		
		if (iter.hasNext()) {
			return (Queue) iter.next();
		} else {
			return (Queue) this.associatedSimObjects.iterator().next(); // CHECK IF START A NEW ITERATOR => SHOULD DO SO
		}
		
	}
	
	public boolean allQueuesEmpty() {
		Iterator<SimObject> iter = this.associatedSimObjects.iterator();
		while (iter.hasNext()) {
			if (!((Queue) iter.next()).isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public boolean otherQueuesEmpty(Queue currentQueue) {
		Iterator<SimObject> iter = this.associatedSimObjects.iterator();
		Queue q;
		while (iter.hasNext()) {
			q = ((Queue) iter.next());
			if (!q.isEmpty() && q!=currentQueue) {
				return false;
			}
		}
		return true;
	}
	
	public boolean anyQueueWaitingProcessing() {
		Iterator<SimObject> iter = this.associatedSimObjects.iterator();
		while (iter.hasNext()) {
			Queue q = (Queue) (iter.next());
			if (q.getFirstWaitingUnholdCustomer() != null) {
				return true;
			}
			
		}
		return false;
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getResults() {
		// TODO Auto-generated method stub
		
	}

	
}
