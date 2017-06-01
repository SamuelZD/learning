package scenario;

import simulator.queueing.*;
import simulator.queueing.object.*;
import distribution.*;

import java.util.Vector;

import analysis.*;

/**
 * Relative to 1 CPU : Gated_M_limited; Releasing a batch of packets in the same time
 * @author SU Zidong - LIP/ENSL
 * 
 * Evaluation Mu by Bruno's method.
 *
 */

public class ScenarioBatchMuAndSwtch {

	/**
	 * Main method to launch the simulations.
	 */
	public static void main(String[] args) {
		
		Configuration.printCredits();
		
		// Input parameters
		int N = Configuration.N; // number of queue served in by the processor by default as 3
		int K = 128; // capacity of the queues		
		
		// Default
		boolean traceSimON = false; // Do not trace the simulation results
		boolean traceModelON = false; // Do not trace the model results
		String traceSimFile = ""; // Simulation trace file (in case the user would like to trace the results)
		String traceModelFile = ""; // Model trace file (in case the user would like to trace the results)
		boolean verb_solver = false; // Do not print all the model iterations
		double solver_convergence_eps = 0.000000001; // Convergence criterion of the model
		double Lambda = 2;
		int limit = 10000;
		int method = 0; // 0 bruno ; 1 thomas
		boolean debug = true;
		String scenario = "switch_batch";
		
		// Parameters from the command line
		int args_index = 0;
		for (String s: args) { // does not include the name of the main class
                       
			if (s.compareTo("-n") == 0) { // the number of port //IMPORTANT FOR A SERVICE BATCH
				N = Integer.parseInt(args[args_index+1]);
				Configuration.N = N;
			}
			
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
				//debug = false;
            }
							
			if (s.compareTo("-verbose") == 0) { // verbose mode for model iterations
				verb_solver = true;
            }
			
			if (s.compareTo("-eps") == 0) { // convergence precision
				solver_convergence_eps = Double.parseDouble(args[args_index+1]);
            }
			
			if (s.compareTo("-extralot") == 0){ //lot size
				Configuration.EXTRALOT = Integer.parseInt(args[args_index+1]);
			}
			
			if (s.compareTo("-scenario") == 0 ){
			    scenario = args[args_index + 1];
			}
			
			if (s.compareTo("-method") == 0 ){
				method = Integer.parseInt(args[args_index+1]);
				debug = false;
			}
			
			args_index++;
        }
		
		// Display parameters
		System.out.println("* tsim = "+Configuration.SIMULATION_TIME+" "+Configuration.TIME_UNIT);
		System.out.println("* K = "+K);
		System.out.println("* N = "+N);
		System.out.println("* Lambda = "+Lambda);
		System.out.println("* LOT = "+ (Configuration.EXTRALOT + 1));
		
		
		// Create the queues
		Queue[] queues = new Queue[N];
		for (int k=0; k<N ; k++) {
			queues[k] = new Queue(K);
		}
		
		// approximation 2
		double timeM = 0.5;
		double timeH = 0.5;
		double timeR = 0.5;
		if (scenario.equals("simple")) {// simple routing
			timeM = 0.09;
			timeH = 0.03;
			timeR = 0.0625;

		} else if (scenario.equals("complet")) {
			// comple routing
			timeH = 0.27;
			timeM = 0.297;
			timeR = 0.0625;
		} else if (scenario.equals("ip")) {
			// IP sec routing
			timeH = 2.67;
			timeM = 2.733;
			timeR = 0.0625;
		} else if (scenario.equals("mascots")) {
			timeH = 0.5;
			timeM = 0.5;
			timeR = 0.5;
		} else if (scenario.equals("batch_switch")) {
			timeH = 0.03;
			timeM = 0.3;
			timeR = 0.0625;
		}
		

		// Create the CPU server, Hit rate : simple routing		
		double muh_0 = 1/timeH;// 33 
		double mum_0 = 1/timeM;// 11
		double mur_0 = 1/timeR;// 16
		
		Distribution[] serviceHit = new Distribution[N];
		Distribution[] serviceMiss = new Distribution[N];
		Distribution[] release = new Distribution[N];
		
		for (int i = 0 ; i < N ; i++)
		{
			serviceHit[i] = new Exponential(muh_0);
			serviceMiss[i] = new Exponential(mum_0);
			release[i] = new Exponential(mur_0);
		}
		
		
		//Server server = new BatchServer();
		Server server = new BatchSwitchServer();
		( (BatchSwitchServer)server ).setSwitchDistribution(new Exponential(Configuration.SWITCHRATE));

	
		// Create the traffic sources		
		// Create the arrival distributions and associated sources
		
		double[] lambdas = new double[N];
		double[] probas = new double[N];
		switch(N){
		case 4:	{
			double[] temps = {0.15, 0.2, 0.25, 0.4}; 
			probas = temps; 
			break;}
		case 8:{
			double[] temps = {0.05, 0.07, 0.09 , 0.11, 0.13, 0.15, 0.17, 0.23};//8 ports
			probas = temps;
			break;}
		case 6:{
			double[] temps = {0.05, 0.1, 0.15 , 0.18, 0.22, 0.3};//6 ports
			probas = temps;
			}
		}	
		
		for(int i = 0 ; i < N ;i++)
		{
			lambdas[i] = probas[i]*Lambda;
		}
		
	
		
		Distribution[] arrival = new Distribution[N];
		
		for(int i = 0; i < N ; i++)
		{
			arrival[i] = new Exponential(lambdas[i]);
		}	
	
		
		//////////////////////////Guillaume
		
		System.out.println(" Simple Forwarding Scenario : ");
        
		Source[] sources = new Source[N]; // Create N sources with Poisson arrivals
		for (int k=0; k<N ; k++) {
			sources[k] = new BatchSource(arrival[k], serviceHit[k], serviceMiss[k],release[k]);
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
			simu.traceResults(traceSimFile); //Time by batch
			simu.traceProbaInstant(traceSimFile);
		}
		
		
		////////////////////////////////////Model
		//calculate mu
		double mu = 5.66;// M = 16
		//double mu = 2.76; // M = 1
		int M = Configuration.EXTRALOT + 1;
//		double time = ((N-1)*timeM + ( M - N + 1)*timeH)/M + timeR;
		//double mu = 1/time;
		//double mu = 4.3271;//M = 8
		Solver1LimitedPollingSwitchB solverSwitch = new Solver1LimitedPollingSwitchB(Configuration.SWITCHRATE*M);
		
		for(int i=0; i < N ;i++){
			solverSwitch.addQueue(K, lambdas[i], mu);
		}

		
		
		
		solverSwitch.init();
		
		solverSwitch.run(solver_convergence_eps, verb_solver, limit);
		
		if(traceModelON)
		{
			//solverSwitch.traceResultsWithSwitchRate(traceModelFile); // Trace the achieved performances
			solverSwitch.traceResults(traceModelFile); // Trace the achieved performances
		}



		
		
	}
	
}

