package distribution;

/**
 * Constant distribution class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class Constant extends Distribution {

	private double constantValue;
	
	public Constant(double value) {
		this.type = Distribution.Type.CONSTANT;
		this.constantValue = value;
	}
	
	@Override
	public double nextDouble() {
		return this.constantValue;
	}
	
	@Override
	public double getMean() {
		return this.constantValue;
	}

}
