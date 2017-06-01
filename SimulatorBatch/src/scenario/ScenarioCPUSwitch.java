package scenario;

import simulator.queueing.*;
import simulator.queueing.object.*;
import distribution.*;
import analysis.*;

/**
 * First scenario to run for the paper => Relative to 1 CPU
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class ScenarioCPUSwitch {

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
		boolean traceAdSoON = false;
		String traceSimFile = ""; // Simulation trace file (in case the user would like to trace the results)
		String traceModelFile = ""; // Model trace file (in case the user would like to trace the results)
		String traceAdSoFile = ""; //Model of AdSo
		boolean verb_solver = false; // Do not print all the model iterations
		double solver_convergence_eps = 0.000000001; // Convergence criterion of the model
		double Lambda = 2.2;
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

			if (s.compareTo("-traceAdSo") == 0){
				traceAdSoON = true;
				traceAdSoFile = args[args_index+1];
			}

			if (s.compareTo("-verbose") == 0) { // verbose mode for model iterations
				verb_solver = true;
			}

			if (s.compareTo("-eps") == 0) { // convergence precision
				solver_convergence_eps = Double.parseDouble(args[args_index+1]);
			}
			
			if (s.compareTo("-switchRate") == 0) { // simulation time
				Configuration.SWITCHRATE = Double.parseDouble(args[args_index+1]);
			}

			args_index++;
		}

		// Display parameters
		System.out.println("* tsim = "+Configuration.SIMULATION_TIME+" "+Configuration.TIME_UNIT);
		System.out.println("* K = "+K);
		System.out.println("* N = "+N);
		System.out.println("* Lambda = "+Lambda);
		System.out.println("* Switch Rate = "+Configuration.SWITCHRATE);

		// Create the queues
		Queue[] queues = new Queue[N];
		for (int k=0; k<N ; k++) {
			queues[k] = new Queue(K);
		}


		// Create the CPU server
		double mu_0 = 1.0;
		double mu_1 = 1.0;
		double mu_2 = 1.0;
		double mu_3 = 1.0;
		double mu_4 = 1.0;
		double mu_5 = 1.0;

		Distribution[] service = new Distribution[N];



		service[0] = new Exponential(mu_0);
		service[1] = new Exponential(mu_1);
		service[2] = new Exponential(mu_2);
		service[3] = new Exponential(mu_3);
		//service[4] = new Exponential(mu_4);
		//service[5] = new Exponential(mu_5);



		//Server server = new HeterogeneousServer();
		Server server = new SimpleSwitchServer();
		( (SimpleSwitchServer)server ).setSwitchDistribution(new Exponential(Configuration.SWITCHRATE));

		// Create the traffic sources		
		// Create the arrival distributions and associated sources

		
		double lambda_0 = 0.05*Lambda;
		double lambda_1 = 0.1*Lambda;
		double lambda_2 = 0.15*Lambda;
		double lambda_3 = 0.2*Lambda;
		double lambda_4 = 0.25*Lambda;
		double lambda_5 = 0.3*Lambda;
		
		/*
		double lambda_0 = 0.25*Lambda;
		double lambda_1 = 0.25*Lambda;
		double lambda_2 = 0.25*Lambda;
		double lambda_3 = 0.25*Lambda;
		 */


		Distribution[] arrival = new Distribution[N];


		arrival[0] = new Exponential(lambda_0);
		arrival[1] = new Exponential(lambda_1);
		arrival[2] = new Exponential(lambda_2);
		arrival[3] = new Exponential(lambda_3);










		//////////////////////////Guillaume


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


		///////////////////////////////////AdSO
		/*

		double[] lams = { lambda_0 , lambda_1 , lambda_2 , lambda_3};
		double[] mus = {mu_0 , mu_1 , mu_2 , mu_3};
		AdSo as = new AdSo(N,K,lams,mus);
		as.compute();

		if(traceAdSoON)
		{
			as.traceResults(traceAdSoFile);
		}
		
		
		double[] lams = { 0.1 , 0.2 , 0.3};
		double[] mus = {mu_0 , mu_1 , mu_2};
		AdSo as = new AdSo(3,3,lams,mus);
		as.compute();
		
		if(traceAdSoON)
		{
		as.traceResults(traceAdSoFile);
		}
		 */
		////////////////////////////////////AdSoEnd
		
		
		///////////////////////////model with switch
		//Solver1LimitedPollingSwitch2 solverSwitch = new Solver1LimitedPollingSwitch2(Configuration.SWITCHRATE);
		//Solver1LimitedPollingSwitch solverSwitch = new Solver1LimitedPollingSwitch(Configuration.SWITCHRATE);
		Solver1LimitedPollingSwitchB solverSwitch = new Solver1LimitedPollingSwitchB(Configuration.SWITCHRATE);
		
		solverSwitch.addQueue(K, lambda_0, mu_0);
		solverSwitch.addQueue(K, lambda_1, mu_1);
		solverSwitch.addQueue(K, lambda_2, mu_2);
		solverSwitch.addQueue(K, lambda_3, mu_3);
		
		solverSwitch.init();
		
		solverSwitch.run(solver_convergence_eps, verb_solver, limit);
		
		if(traceModelON)
		{
			solverSwitch.traceResults(traceModelFile); // Trace the achieved performances
		}

		/*
		// Apply the global model with server vacation
		// Create the solver
		Solver1LimitedPolling solver = new Solver1LimitedPolling();

		// Add queues to the solver
		solver.addQueue(K, lambda_0, mu_0);
		solver.addQueue(K, lambda_1, mu_1);
		solver.addQueue(K, lambda_2, mu_2);
		solver.addQueue(K, lambda_3, mu_3);


		// Init the solver
		solver.init();

		// Run the solver
		solver.run(solver_convergence_eps, verb_solver, limit);

		// Model tracing features
		if (traceModelON) {
			solver.traceResults(traceModelFile); // Trace the achieved performances
			solver.traceDistribution(traceModelFile+"Dist", N-1); // Trace the states distribution
		}		
		 
		*/
		/////////////////////////Guillaume

	}



}

