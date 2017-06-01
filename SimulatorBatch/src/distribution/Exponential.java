package distribution;

import java.util.Random;

/**
 * Pseudo random numbers generator with exponential distribution.
 * @author Guillaume Artero Gallardo - LIP/ENSLs
 *
 */
public class Exponential extends Distribution {

	// ATTRIBUTES
	private double lambda;
	private Random rand;
	
	// CONSTRUCTOR
	public Exponential(double lambda_) {
		this.type = Distribution.Type.EXPONENTIAL;
		this.lambda = lambda_;
		this.rand = new Random();
	}
	
	public Exponential(double lambda_, long seed_) {
		this.type = Distribution.Type.EXPONENTIAL;
		this.lambda = lambda_;
		this.rand = new Random(seed_);
	}
	
	// METHODS
	@Override
	public double nextDouble() {
		return (-1.0/this.lambda)*Math.log(1-this.rand.nextDouble());
	}
	
	@Override
	public double getMean() {
		return 1.0/this.lambda; 
	}
	
	public void setLambda(double new_lambda) {
		this.lambda = new_lambda;
	}
	
	public double getLambda() {
		return this.lambda;
	}
	
}
