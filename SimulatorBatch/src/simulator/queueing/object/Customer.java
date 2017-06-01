package simulator.queueing.object;

/**
 * Customer class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class Customer {

	// ATTRIBUTES
	private static int nbCustomersGenerated = 0;
	
	private int id;
	private Queue currentQueue;
	private Queue previousQueue;
	private Source source;
	private double arrivalTime;
	private boolean inService;
	private boolean hold;  // hold by a CPU server (means that has been served but not freed from the memory)
	private Server currentServer; // server used for service
	
	// CONSTRUCTOR
	public Customer() {
		this.id = (++Customer.nbCustomersGenerated);
		this.arrivalTime = -1;
		this.inService = false;
		this.hold = false;
		this.currentQueue = null;
		this.previousQueue = null;
		this.source = null;
		this.currentServer = null;
	}
	
	// METHODS
	
	/**
	 * Get the customer id
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Get the arrival time
	 */
	public double getArrivalTime() {
		return this.arrivalTime;
	}
	
	public void setArrivalTime(double time) {
		this.arrivalTime = time;
	}
	
	public boolean inService() {
		return this.inService;
	}
	
	public void startService() {
		this.inService = true;
	}
	
	public void endService() {
		this.inService = false;
	}
	
	public boolean hold() {
		return this.hold;
	}
	
	public void startHold() {
		this.hold = true;
	}
	
	public void endHold() {
		this.hold = false;
	}
	
	public Queue getCurrentQueue() {
		return this.currentQueue;
	}
	
	public void setCurrentQueue(Queue queue_) {
		this.currentQueue = queue_;
	}
	
	public Queue getPreviousQueue() {
		return this.previousQueue;
	}
	
	public void setPreviousQueue(Queue queue_) {
		this.previousQueue = queue_;
	}
	
	public Source getSource() {
		return this.source;
	}
	
	public void setSource(Source source_) {
		this.source = source_;
	}
	
	public Server getCurrentServer() {
		return this.currentServer;
	}
	
	public void setCurrentServer(Server server_) {
		this.currentServer = server_;
	}
	

	/**
	 * toString
	 */
	public String toString() {
		return "Customer "+this.id;
	}
	
	
}
