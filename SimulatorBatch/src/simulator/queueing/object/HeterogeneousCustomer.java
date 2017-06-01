package simulator.queueing.object;

public class HeterogeneousCustomer extends Customer {

	// ATTRIBUTES
	double serviceTime;
	
	// CONSTRUCTOR
	public HeterogeneousCustomer(double serviceTime_) {
		super();
		this.serviceTime = serviceTime_;
	}
	
	public HeterogeneousCustomer()
	{
		super();
	}
	
	// METHODS
	public double getServiceTime() {
		return this.serviceTime;
	}
	public void setServiceTime(double serviceTime_)
	{
		this.serviceTime = serviceTime_;
	}
	
}
