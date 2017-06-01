package simulator.queueing.event;
import simulator.*;
import simulator.queueing.*;
import simulator.queueing.object.*;


/**
 * Service class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class Service extends Event {

	// ATTRIBUTES
	private Customer customer; // customer to be served
		
	// CONSTRUCTOR
	public Service(double date, Customer customer_) {
		super(date);
		this.customer = customer_;
	}

	@Override
	public void execute(DES simu) {
		this.customer.getCurrentServer().processService( (QDES) simu, this.customer);
	}
	
	
}
