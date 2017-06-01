package simulator;

import java.util.List;
import java.util.Observer;
import java.util.Vector;

/**
 * SimObjectEntry class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public abstract class SimObjectEntry implements Observer {

	// ATTRIBUTES
	protected List<SimObject> associatedSimObjects;
	protected SimObject observedSimObject;
	
	// CONSTRUCTOR
	protected SimObjectEntry(SimObject observedSimObject_) {
		this.observedSimObject = observedSimObject_;
		this.associatedSimObjects = new Vector<SimObject>();
	}
	
	// METHODS
	public abstract void getResults();
	
	public SimObject getObservedSimObject() {
		return this.observedSimObject;
	}
	
	public void attach(SimObject attachedObject) {
		if (!associatedSimObjects.contains(attachedObject)) {
			this.associatedSimObjects.add(attachedObject);
		}
	}
	
}
