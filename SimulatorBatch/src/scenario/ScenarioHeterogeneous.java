package scenario;

import simulator.queueing.*;
import simulator.queueing.object.*;
import distribution.*;
import analysis.*;

/**
 * Second scenario to run for the paper => Relative to the heterogeneous case
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class ScenarioHeterogeneous {

	/**
	 * Main method to launch the simulations.
	 */
	public static void main(String[] args) {
		Configuration.printCredits();
		
		// Input parameters
		int N = 4; // number of queue served in by the processor
		int K = 128; // capacity of the queues		
		
		// Default
		boolean traceSimON = false; // Do not trace the simulation results
		boolean traceModelON = false; // Do not trace the model results
		String traceSimFile = ""; // Simulation trace file (in case the user would like to trace the results)
		String traceModelFile = ""; // Model trace file (in case the user would like to trace the results)
		boolean verb_solver = false; // Do not print all the model iterations
		double solver_convergence_eps = 0.000000001; // Convergence criterion of the model
		double Lambda = 2.6;
		double Mu = 1.0;
		int limit = 10000;
		
		// Parameters from the command line
		int args_index = 0;
		for (String s: args) { // does not include the name of the main class
                       
			if (s.compareTo("-k") == 0) { // max queue size (capacity)
				K = Integer.parseInt(args[args_index+1]);
            }
			
			if (s.compareTo("-limit") == 0) { // limit on the number of model iterations
				limit = Integer.parseInt(args[args_index+1]);
            }
            
			if (s.compareTo("-Lambda") == 0) { // Global arrival rate
				Lambda = Double.parseDouble(args[args_index+1]);
            }
			
			if (s.compareTo("-Mu") == 0) { // Global service rate
				Mu = Double.parseDouble(args[args_index+1]);
            }
			
			
			if (s.compareTo("-tsim") == 0) { // simulation time
				Configuration.SIMULATION_TIME = Double.parseDouble(args[args_index+1]);
            }
			
			if (s.compareTo("-traceSim") == 0) { // trace simulation results
				traceSimON = true;
				traceSimFile = args[args_index+1];
            }
			
			if (s.compareTo("-traceModel") == 0) { // trace model results
				traceModelON = true;
				traceModelFile = args[args_index+1];
            }
				
			if (s.compareTo("-verbose") == 0) { // verbose mode for model iterations
				verb_solver = true;
            }
			
			if (s.compareTo("-eps") == 0) { // convergence precision
				solver_convergence_eps = Double.parseDouble(args[args_index+1]);
            }
			
			args_index++;
        }
		
		// Display parameters
		System.out.println("* tsim = "+Configuration.SIMULATION_TIME+" "+Configuration.TIME_UNIT);
		System.out.println("* K = "+K);
		System.out.println("* N = "+N);
		System.out.println("* Lambda = "+Lambda);
		System.out.println("* Mu = "+Mu);
		
		// Create the queues
		Queue[] queues = new Queue[N];
		for (int k=0; k<N ; k++) {
			queues[k] = new Queue(K);
		}
		
		
		// Create the CPU server
		double mu_0 = Mu;
		double mu_1 = Mu;
		double mu_2 = Mu;
		double mu_3 = Mu;
		
		
		Distribution[] service = new Distribution[N];
		service[0] = new Exponential(mu_0);
		service[1] = new Exponential(mu_1);
		service[2] = new Exponential(mu_2);
		service[3] = new Exponential(mu_3);
		
		Server server = new HeterogeneousServer();
	
		// Create the traffic sources		
		// Create the arrival distributions and associated sources
		
		double lambda_0 = 0.25*Lambda;
		double lambda_1 = 0.25*Lambda;
		double lambda_2 = 0.25*Lambda;
		double lambda_3 = 0.25*Lambda;
		
		Distribution[] arrival = new Distribution[N];
		arrival[0] = new Exponential(lambda_0);
		arrival[1] = new Exponential(lambda_1);
		arrival[2] = new Exponential(lambda_2);
		arrival[3] = new Exponential(lambda_3);
		
		Source[] sources = new Source[N]; // Create N sources with Poisson arrivals
		for (int k=0; k<N ; k++) {
			sources[k] = new HeterogeneousSource(arrival[k], service[k]);
		}
			
		// Create the simulator
		QDES simu = new QDES();
		
		// Register the simulation objects
		for (int k=0; k<N ; k++) {
			simu.register(sources[k]); // here the sources
			simu.register(queues[k]); // here the queue
		}
		simu.register(server); // here the polling server/processor
		
		// Attach the simulation objects with each others
		for (int k=0; k<N ; k++) {
			simu.attach(sources[k], queues[k]); // each source sends traffic to a specific queue
			simu.attach(queues[k], server); // each queue is served by the server in a polling fashion
		}
		
		// Initialize the simulation
		simu.init();
		
		// Run the simulation
		simu.run();
			
		// Collect and display the simulation results
		simu.getResults();
		
		// Simulation tracing features		
		if (traceSimON) {
			simu.traceResults(traceSimFile);
		}
		
		// Apply the global model with server vacation
		// Create the solver
		Solver1LimitedPolling solver = new Solver1LimitedPolling();
		
		// Add queues to the solver
		solver.addQueue(K, lambda_0, mu_0);
		solver.addQueue(K, lambda_1, mu_1);
		solver.addQueue(K, lambda_2, mu_2);
		solver.addQueue(K, lambda_3, mu_3);
		
		//solver.addQueue(K, lambda_4, mu);
		
		// Init the solver
		solver.init();
		
		// Run the solver
		solver.run(solver_convergence_eps, verb_solver, limit);
		
		// Model tracing features
		if (traceModelON) {
			solver.traceResults(traceModelFile); // Trace the achieved performances
			solver.traceDistribution(traceModelFile+"Dist", N-1); // Trace the states distribution
		}		
	}
}

