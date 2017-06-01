package simulator;

import simulator.queueing.*;

/**
 * Notification class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class Notification {

	// ATTRIBUTES
	public static enum Type {BLOCKING, ARRIVAL, DEPARTURE, RESPONSE_TIME, SERVICE_TIME,WAITING_SIZE, SERVICE_TIME_BATCH, START_VACATION, END_VACATION, F_ATTEMPT, F_SUCCESS, G_ATTEMPT, G_SUCCESS, H_ATTEMPT, H_SUCCESS, START_SWITCH, END_SWITCH, START_TV, END_TV, BATCH_SIZE};
	private Type type;
	private Object data;
	private QDES simu;
	
	// CONSTRUCTOR
	public Notification(Type type_, Object data_, QDES simu_) {
		this.type = type_;
		this.data = data_;
		this.simu = simu_;
	}	
	
	public Notification(Type type_, Object data_) {
		this(type_, data_, null);
	}
	
	public Notification(Type type_) {
		this(type_, null);
	}
	
	// METHODS
	public Type getType() {
		return this.type;
	}
	
	public Object getData() {
		return this.data;
	}
	
	public QDES getSimu() {
		return this.simu;
	}
	
}
