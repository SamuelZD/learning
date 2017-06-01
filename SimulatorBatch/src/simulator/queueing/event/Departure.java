package simulator.queueing.event;
import java.util.Vector;

import simulator.*;
import simulator.queueing.*;
import simulator.queueing.object.*;


/**
 * Departure event for releasing a sub batch of packets
 * @author SU Ziodng - LIP/ENSL
 *
 */
public class Departure extends Event {

	// ATTRIBUTES
	private Vector<BatchCustomer> customers; // customer to be served
	private BatchServer server;
	private Queue queue;
	private boolean last = false;
	
	// CONSTRUCTOR
	public Departure(double date, Vector<BatchCustomer> customers_, BatchServer server_, Queue queue_, boolean last_) {
		super(date);
		this.customers = customers_;
		this.server = server_;
		this.queue = queue_;
		this.last = last_;
	}

	@Override
	public void execute(DES simu) {
		server.processDeparture( (QDES) simu, this.customers, this.queue , last);
	}
	
	
}
