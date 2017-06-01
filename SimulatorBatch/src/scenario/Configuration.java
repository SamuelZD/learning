package scenario;

/**
 * Configuration class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class Configuration {

	// INPUT PARAMETERS
	public static double SIMULATION_TIME = 500;	// Simulation time set at 500 µs by default
	public final static String TIME_UNIT = "µs"; // Time unit
	public static double SWITCHRATE = 50;
	public static int EXTRALOT = 31;
	public static int N = 4; // the number of port
	
	// PRINT FUNCTIONS
	/**
	 * Print credits (authors, etc..)
	 */
	public static void printCredits() {
		System.out.println("**************************************************************");
		System.out.println("* QUEUEING SIMULATION TOOL FOR REFLEXION PROJECT *");
		System.out.println("* AUTHOR: Guillaume Artero Gallardo and SU Zidong - LIP/ENSL *");
		System.out.println("**************************************************************");
	}

}
