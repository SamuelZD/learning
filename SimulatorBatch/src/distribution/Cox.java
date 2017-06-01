package distribution;

import distribution.Exponential;

import java.util.Random;

public class Cox extends Distribution {
	
	private Random rand;
	
	private double cof = 1;
	
	private double prob_exp;
	
	private Exponential e_mu_1;
	private Exponential e_mu_2;
	
	public Cox( double cof_ , double prob_exp_ , double mu_1_, double mu_2_)
	{
		this.cof = cof_;
		this.prob_exp = prob_exp_;
		this.e_mu_1 = new Exponential(mu_1_*cof_);
		this.e_mu_2 = new Exponential(mu_2_*cof_);
		rand = new Random();
		
	}

	@Override
	public double nextDouble() {
		if(this.rand.nextDouble() < this.prob_exp)
		{
			return this.e_mu_1.nextDouble();
		}
		else
			return this.e_mu_1.nextDouble() + this.e_mu_2.nextDouble();
	}

	@Override
	public double getMean() {
		return this.e_mu_1.getMean() + (1 - this.prob_exp)*this.e_mu_2.getMean();
	}

}
