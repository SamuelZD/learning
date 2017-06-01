package distribution;

import java.util.Random;

/**
 * Pseudo random numbers generator with uniform distribution.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class Uniform extends Distribution {

	// ATTRIBUTES
	private double mean;
	private Random rand;
	
	// CONSTRUCTOR
	public Uniform(double mean_) {
		this.type = Distribution.Type.UNIFORM;
		this.mean = mean_;
		this.rand = new Random();
	}
	
	public Uniform(double mean_, long seed_) {
		this.type = Distribution.Type.UNIFORM;
		this.mean = mean_;
		this.rand = new Random(seed_);
	}
	
	// METHODS
	@Override
	public double nextDouble() {
		return this.rand.nextDouble()*this.mean*2;
	}
	
	@Override
	public double getMean() {
		return this.mean;
	}
	
}
