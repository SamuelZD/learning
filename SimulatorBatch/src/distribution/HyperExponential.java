package distribution;

import java.util.Random;

/**
 * Pseudo random numbers generator with hyper-exponential distribution.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class HyperExponential extends Distribution {

	// ATTRIBUTES
	private double lambda;
	private double pburst;
	private Random rand;
	
	private Exponential exp_burst;
	private Exponential exp_not_burst;
	
	// CONSTRUCTOR
	public HyperExponential(double lambda_, double pburst_, double decreaseFactor_) {
		this.type = Distribution.Type.HYPER_EXPONENTIAL;
		this.lambda = lambda_;
		this.pburst = pburst_;
		double lambda_not_burst = lambda_*(decreaseFactor_*pburst_+1-pburst_);
		double lambda_burst = lambda_not_burst*1.0/decreaseFactor_;
		System.out.println("Lambda_burst = "+lambda_burst);
		System.out.println("Lambda_not_burst = "+lambda_not_burst);
		
		
		this.exp_burst = new Exponential(lambda_burst);
		this.exp_not_burst = new Exponential(lambda_not_burst);
		this.rand = new Random();
	}
	
	public HyperExponential(double lambda_, double pburst_, double decreaseFactor_, long seed_) {
		this.type = Distribution.Type.HYPER_EXPONENTIAL;
		this.lambda = lambda_;
		this.pburst = pburst_;
		double lambda_not_burst = lambda_*(decreaseFactor_*pburst_+1-pburst_);
		double lambda_burst = lambda_not_burst*1.0/decreaseFactor_;
		this.exp_burst = new Exponential(lambda_burst);
		this.exp_not_burst = new Exponential(lambda_not_burst);
		this.rand = new Random(seed_);
	}
	
	//constructor for special demands
	public HyperExponential(double mu_nburst, double mu_burst, double pburst_ , int nb)
	{
		this.type = Distribution.Type.HYPER_EXPONENTIAL;
		this.exp_not_burst = new Exponential(mu_nburst);
		this.exp_burst = new Exponential(mu_burst);
		this.pburst = pburst_;
		this.rand = new Random();
	}
	
	// METHODS
	@Override
	public double nextDouble() {
		if (this.rand.nextDouble()<this.pburst) {
			return this.exp_burst.nextDouble();
		} else {
			return this.exp_not_burst.nextDouble();
		}
	}
	
	@Override
	public double getMean() {
		return 1.0/this.lambda; 
	}
	
	public double getLambda() {
		return this.lambda;
	}	
}
