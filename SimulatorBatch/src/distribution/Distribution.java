package distribution;

/**
 * Pseudo random numbers generator with any distribution.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public abstract class Distribution {

	// ATTRIBUTES
	public static enum Type {EXPONENTIAL, UNIFORM, CONSTANT, HYPER_EXPONENTIAL, ERLANG4};
	
	protected Type type;
	
	// METHODS
	public Type getType() {
		return this.type;
	}
	
	public abstract double nextDouble();
	public abstract double getMean();
	
}
