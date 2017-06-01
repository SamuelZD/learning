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
 * Q ->  T -> X -> Q
 *
 */

public class ScenarioBatchCycle {

	/**
	 * Main method to launch the simulations.
	 */
	public static void main(String[] args) {
		
		Configuration.printCredits();
		
		// Input parameters
		int N = Configuration.N; // number of queue served in by the processor by default as 3
		int K = 128; // capacity of the queues		
		
		int type = 2; // the type of function is used, the approximation Bruno
		
		// Default
		boolean traceSimON = false; // Do not trace the simulation results
		boolean traceModelON = false; // Do not trace the model results
		String traceSimFile = ""; // Simulation trace file (in case the user would like to trace the results)
		String traceModelFile = ""; // Model trace file (in case the user would like to trace the results)
		boolean verb_solver = false; // Do not print all the model iterations
		double solver_convergence_eps = 0.000000001; // Convergence criterion of the model
		double Lambda = 5;
		double prob = 9.05;   //proba seuil
		int limit = 10000;
		int method = 0; // 0 bruno ; 1 thomas
		boolean debug = true;
		
		// Parameters from the command line
		int args_index = 0;
		for (String s: args) { // does not include the name of the main class
                       
			if (s.compareTo("-n") == 0) { // the number of port //IMPORTANT FOR A SERVICE BATCH
				N = Integer.parseInt(args[args_index+1]);
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
			
			if (s.compareTo("-type") == 0 ){
				type = Integer.parseInt(args[args_index+1]);
			}
			
			if (s.compareTo("-method") == 0 ){
				method = Integer.parseInt(args[args_index+1]);
				debug = false;
			}
			
			if (s.compareTo("-threshold") == 0){
				prob = Double.parseDouble(args[args_index+1]);
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
		
		
		// Create the CPU server, Hit rate
		double timeM = 0.09;
		double timeH = 0.03;
		double timeR = 0.0625;
		
		double muh_0 = 1/0.03;
		double mum_0 = 1/0.09;
		double mur_0 = 1/0.0625;
		
		Distribution[] serviceHit = new Distribution[N];
		Distribution[] serviceMiss = new Distribution[N];
		Distribution[] release = new Distribution[N];
		
		
		serviceHit[0] = new Exponential(muh_0);
		serviceHit[1] = new Exponential(muh_0);
		serviceHit[2] = new Exponential(muh_0);
		
		serviceMiss[0] = new Exponential(mum_0);
		serviceMiss[1] = new Exponential(mum_0);
		serviceMiss[2] = new Exponential(mum_0);
		
		
		release[0] = new Exponential(mur_0);
		release[1] = new Exponential(mur_0);
		release[2] = new Exponential(mur_0);
		
		
		Server server = new BatchServer();

	
		// Create the traffic sources		
		// Create the arrival distributions and associated sources
		
		
		double lambda_0 = 0.2*Lambda;
		double lambda_1 = 0.3*Lambda;
		double lambda_2 = 0.5*Lambda;

		
	
		
		Distribution[] arrival = new Distribution[N];
		
		
		arrival[0] = new Exponential(lambda_0);
		arrival[1] = new Exponential(lambda_1);
		arrival[2] = new Exponential(lambda_2);

		
	
		
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
			simu.traceResults(traceSimFile);
			simu.traceProbaInstant(traceSimFile);
		}
		
		//collect each queue's mu
		Vector<Double> mus = ((QSimObjects) (simu.getSimObjects())).getMus();
		Vector<Double> musBatch = ((QSimObjects) (simu.getSimObjects())).getMusBatch();
		
		//debug
		Vector<Double> svtm = new Vector<Double>();
		Vector<Double> batchsizes = ((QSimObjects) (simu.getSimObjects())).getBatchSize();
		for (int i = 0; i < N; i++) {
			double batchsize = batchsizes.get(i);
			System.out.println("batch size of Q(" + (i + 1) + "):" + batchsize);
			double lower = (timeM + timeH * (batchsize - 1)) / batchsize + timeR;
			if (lower > 1 / mus.get(i))
				System.out.println("Queue(" + (i + 1) + ") is wrong");
			svtm.addElement(lower);
		}

		
		//cycle model
		Solver1LimitedPollingHelperM solveHelper = new Solver1LimitedPollingHelperM();
		
		//set the time
		solveHelper.setTimes(timeM, timeH, timeR);
		
		//add queues
		solveHelper.addQueue(K, lambda_0);
		solveHelper.addQueue(K, lambda_1);
		solveHelper.addQueue(K, lambda_2);
		
		solveHelper.iterative();
		
		if(traceModelON)
		{
			solveHelper.traceResults(traceModelFile);
		}
		

		
	}
	
	
	
}

