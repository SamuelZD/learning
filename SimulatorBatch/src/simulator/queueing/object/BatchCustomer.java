package simulator.queueing.object;

public class BatchCustomer extends HeterogeneousCustomer {
	
	//ARRIBUTES
	/**
	 * the procedures of batch's service containing two stages : 
	 * the first one is looking up in the flow table
	 * the other one is releasing sub batch
	 */

	private double releaseTime = 0;
	private double serviceTimeHit = 0;
	private double serviceTimeMiss = 0;
	private int export = 0;// the existing port
	
	//CONSTRUCTOR
	public BatchCustomer(double serviceTimeHit_, double serviceTimeMiss_, double releaseTime_)
	{
		super();
		this.serviceTimeHit = serviceTimeHit_;
		this.serviceTimeMiss = serviceTimeMiss_;
		this.releaseTime = releaseTime_; 
	}
	
	public BatchCustomer()
	{
		super();
	}
	
	//METHODS
	
	public void setExport(int export)
	{
		this.export = export;
	}
	
	public int getExport()
	{
		return this.export;
	}
	
	public void setReleaseTime(double releaseTime_)
	{
		this.releaseTime = releaseTime_;
	}
	
	
	public double getServiceTimeReleasing()
	{
		return this.releaseTime;
	}
	
	public void setServiceTimeHit( double serviceTimeHit_)
	{
		this.serviceTimeHit = serviceTimeHit_;
	}
	
	public double getServiceTimeHit()
	{
		return this.serviceTimeHit;
	}
	
	public void setServiceTimeMiss(double serviceTimeMiss_)
	{
		this.serviceTimeMiss = serviceTime;
	}
	
	public double getServiceTimeMiss()
	{
		return this.serviceTimeMiss;
	}
	
}