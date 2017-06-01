package simulator.queueing.event;
import java.util.Vector;

import simulator.*;
import simulator.queueing.*;
import simulator.queueing.object.*;


/**
 * Service class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class ServiceBatch extends Event {

	// ATTRIBUTES
	//private Customer customer; // customer to be served
	private Vector<Vector<BatchCustomer>> customers; //customers, in a batch, to be served
	private Server server; //server associated with a batch of customer
	private Queue queue; //the arrival queue for a batch of packets
		
	// CONSTRUCTOR
	public ServiceBatch(double date, Vector<Vector<BatchCustomer>> customers_, Server server_, Queue queue_) {
		super(date);
		this.customers = customers_;
		this.server = server_;
		this.queue = queue_;
	}

	@Override 
	public void execute(DES simu) {
		((BatchServer)this.server).processService( (QDES) simu, this.customers, this.queue);
	}
	
	
}
