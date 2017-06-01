package simulator;

/**
 * Interface SimObjects.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public interface SimObjects {

	public void init(DES simu);
	public void getResults();
	
	public void traceResults(String outputFile);
	public void traceProbaInstant(String outputFile);
		
}
