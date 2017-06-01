package simulator.queueing.object;

import java.util.Random;

import distribution.Distribution;
import scenario.Configuration;
import simulator.queueing.QDES;
import simulator.queueing.event.BatchSourceTransmission;

public class BatchSource extends Source {

	//Distribution for releasing time
	Distribution releaseTimeDistribution;
	Distribution serviceTimeHitDistribution;
	Distribution serviceTimeMissDistribution;
	private Random random;
	//
	//CONSTRUTOR
	public BatchSource(Distribution interArrivalDistribution_, Distribution serviceTimeHitDistribution_, Distribution serviceTimeMissDistribution_, Distribution releaseTimeDistribution_) {
		super(interArrivalDistribution_);
		// TODO Auto-generated constructor stub
		this.releaseTimeDistribution = releaseTimeDistribution_;
		this.serviceTimeMissDistribution = serviceTimeMissDistribution_;
		this.serviceTimeHitDistribution = serviceTimeHitDistribution_;
		this.random = new Random();
	}
	
	//METHODES
	public Distribution getReleaseTimeDistribution()
	{
		return this.releaseTimeDistribution;
	}
	
	public Distribution getServiceTimeMissDistribution()
	{
		return this.serviceTimeMissDistribution;
	}
	
	public Distribution getServiceTimeHitDistribution()
	{
		return this.serviceTimeHitDistribution;
	}
	
	public int getExport()
	{
		return this.random.nextInt(Configuration.N - 1) +1 ; //start from 1
	}
	@Override
	public void scheduleNextTransmission(QDES simu) {
		
		// Draw the inter-arrival time at random according to the selected distribution
		double it = this.getInterArrivalDistribution().nextDouble();
		
		BatchSourceTransmission st = new BatchSourceTransmission(simu.getSimTime()+it, this);
		// Schedule the event in the simulator
		st.schedule(simu);
		
		
	}

}
