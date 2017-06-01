package simulator;

import java.util.Observable;
import java.util.Observer;

/**
 * SimObject class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public abstract class SimObject extends Observable  implements Observer {

	public abstract void init(DES simu);
	
	public void declareNotification(Notification notification) {
		this.setChanged();
		this.notifyObservers(notification);
	}
	
}
