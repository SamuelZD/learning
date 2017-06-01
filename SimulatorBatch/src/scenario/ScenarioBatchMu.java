package scenario;

import simulator.queueing.*;
import simulator.queueing.object.*;
import distribution.*;
import analysis.Enumerator;

import java.util.Vector;

import analysis.*;

/**
 * Relative to 1 CPU : Gated_M_limited; Releasing a batch of packets in the same time
 * @author SU Zidong - LIP/ENSL
 * 
 * Evaluation Mu by Bruno's method.
 *
 */

public class ScenarioBatchMu {

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
		double Lambda = 30;
		int limit = 10000;
		int method = 0; // 0 bruno ; 1 thomas
		boolean debug = true;
		String scenario = "simple";
		
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
			timeH = 0.03;
			timeM = 0.09;
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
		} else if (scenario.equals("scenarioA")) {
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
		
		
		Server server = new BatchServer();

	
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
			svtm.addElement(lower);
			if (lower > 1 / mus.get(i))
			{
				System.out.println("Queue(" + (i + 1) + ") is wrong");	
			}
			System.out.println("time(" + (i + 1) + ") : " + 1 / mus.get(i) + " " + "Lower of Time: " +svtm.get(i) + " ");
		}




		///////////////////////// Bruno

		// Apply the global model with server vacation
		// Create the solver
		//Solver1LimitedPollingEMU solverB = new Solver1LimitedPollingEMU();
		Solver1LimitedPollingEMU solverB = new Solver1LimitedPollingEMU();
		// Add queues to the solver
		if (method == 0) // method Bruno
		{
			for(int i = 0 ; i < N ; i++)
			{
				solverB.addQueue(K, lambdas[i]);	
			}
	
			//add the probas instant
			solverB.addProbasInstant(  ((QSimObjects)(simu.getSimObjects())).getProbaInstant().get(0) );
			solverB.addProbasInstant(  ((QSimObjects)(simu.getSimObjects())).getProbaInstant().get(1) );
			solverB.addProbasInstant(  ((QSimObjects)(simu.getSimObjects())).getProbaInstant().get(2) );
			
		}
		
		
		
		// Add the batch and cache parameters to the solver
		solverB.setTimeParameters(1/mum_0, 1/muh_0, 1/mur_0); //service time
		solverB.setBatchSize(Configuration.EXTRALOT + 1); // batch size
		
		
		
		// Init the solver
		solverB.init();
		
		//the maximaux mu
		double time_approxi = 0;
		int M = Configuration.EXTRALOT + 1;
		if(M<(N-1)) time_approxi = timeM + timeR;
		else time_approxi = ((N-1)*timeM + timeH*(Configuration.EXTRALOT + 2 -N))/(Configuration.EXTRALOT+1) + timeR;
		ScenarioBatchMu helper = new ScenarioBatchMu();
		for(int i = 0 ; i < N ;i++)
		{
			solverB.setMu(i, 1/time_approxi);
		    //solverB.setMu(i, mus.get(i));
			//solverB.setMu(i, helper.computeMuG(timeH, timeM, timeR));
			//solverB.setMu(i, helper.computeMuTry(timeH, timeM, timeR));
			//solverB.setMu(i, helper.computeMu(timeH, timeM, timeR));
			//solverB.setMu(i,helper.computeMuEnumerator(timeH, timeM, timeR));
		}

		
		
		//run the model
		solverB.run(solver_convergence_eps, verb_solver, limit);
		
		
		// Model tracing features
		if (traceModelON && method == 0) {
		solverB.traceResults(traceModelFile); // Trace the achieved performances
		solverB.traceDistribution(traceModelFile+"Dist", N-1); // Trace the states distribution
		}		
		
		System.out.println("Scenario " + scenario + " : " + Configuration.N + " ports;"  + " enumerator");
		for(double mu : mus)
		{
			System.out.println("mu " + mu );
		}
		
		//debug
		System.out.println(helper.computeMuEnumerator(timeH, timeM, timeR));
//		System.out.println(helper.computeMu(timeH, timeM, timeR));
		/////////////////////////Bruno End
		//System.out.println(helper.computeCombination(8, 2));
		
		
		
		
	}
	
	//for the 4 ports and 4 Port
	public double computeMu(double timeH, double timeM, double timeR){
		double serviceTime = 0;
		int n = Configuration.N - 1;//3
		int m = Configuration.EXTRALOT + 1;//4
		double p1 = n/Math.pow(n, m);
		//double p2 = Math.pow(2/3,m)*n-p1;
		double p2 = Math.pow(2,m)/Math.pow(3,m)*n - 2*p1;
		//double p2 = (Math.pow(2/3,m) - 1/Math.pow(n, m)*2)*n;
		double p3 = 1 - p1 - p2;
		double[] probas = {p1, p2, p3};
		
		//debug
		for(double p : probas)System.out.println("special case :" + p );
		
		for(int k = 1 ; k <= 3 ; k++){
			double time = (k*timeM + (m - k)*timeH)/m + timeR;
			serviceTime += time*probas[k-1];
			System.out.println("serviceTime " + serviceTime);
		}
		
		return 1/serviceTime;
	}
	
	//compute the mu
	public double computeMuTry( double timeH, double timeM, double timeR){
		ScenarioBatchMu helper = new ScenarioBatchMu();
		int nbp = Configuration.N - 1;
		int m = Configuration.EXTRALOT + 1;
		int min = (nbp > m)?m:nbp;
		double[] probas = new double[min];
		double mum = Math.pow(nbp, m);
			
		for(int i = 1 ; i <= min ; i++){
			double num = 1;
			for(int j = 0 ; j < i ; j++){
				num *= (nbp - j);
			}
			for(int j = 0 ; j < (m - i); j++){
				num *= i;
			}
			//num *= helper.computeArragement(m, i);
			//num *= helper.computeCombination(m, m-i);
			//System.out.println("i " + i + "num " + num );
//			probas[i-1] = num/mum;
			System.out.println(i+": " + helper.computeCombination(m - 1, i - 1)*helper.computeCombination(nbp, i));
			probas[i -1] = helper.computeCombination(m - 1, i - 1)*helper.computeCombination(nbp, i)/helper.computeCombination(m+nbp-1, nbp-1);
			//soir20170522 wrong
//			if( i == 1) probas[i - 1] = nbp/mum;
//			else{
//				//probas[i - 1] = ( helper.computeCombination(nbp, i)*Math.pow(i, m) - helper.computeCombination(nbp, i-1)*Math.pow(i-1, m) )/mum;
//				probas[i - 1] = ( Math.pow(i, m) - Math.pow(i-1, m) )/mum;
//			}
		}
		
		double serviceTime = 0;
		for(int i = 0 ; i < min ; i++){
			int k = i+1;
			serviceTime += probas[i]*((k*timeM + (m - k)*timeH)/m);
			//System.out.println("serviceTime " + serviceTime);
		}
		serviceTime += timeR;
		
		//////////debug
		double probsum = 0;
		for(double p : probas) {
			System.out.println("nb of port " + p);
			probsum += p;
		}
		System.out.println("sum of prob " + probsum);
		
		return 1/serviceTime;
		
	}
	
	//compute mu general
	public double computeMuG(double timeH, double timeM, double timeR){
		
		ScenarioBatchMu helper = new ScenarioBatchMu();
		int nbp = Configuration.N - 1;
		int m = Configuration.EXTRALOT + 1;
		int min = (nbp > m)?m:nbp;
		
		double[] probas = new double[min];
		for(int i = 0 ; i < min ; i++){
			//for the first one
			if( i == 0){
				probas[i] = helper.computeCombination(nbp, 1)/Math.pow(nbp, m);
				//probas[i] = 1/Math.pow(nbp, m);
			}
//			else if (i == (min -1)){ // the last one
//				probas[i] = 0;
//				double sum = 0;
//				for(double nb : probas) sum += nb;
//				probas[i] = 1 - sum;
//			}
			else{// the general case
				int k = i + 1;
				//rapport
				double factor_before = helper.computeCombination(nbp, k);
				double factor_after = Math.pow(k, m)/Math.pow(nbp, m) - Math.pow(k-1, m)/Math.pow(nbp, m)*helper.computeCombination(k, k-1);//raport
				probas[i] = factor_before*factor_after;
				probas[i] =  Math.pow(k, m)/Math.pow(nbp, m)*helper.computeCombination(nbp, k) - Math.pow(k-1, m)/Math.pow(nbp, m)*helper.computeCombination(nbp, k-1); //wrong
				
				//rapport ancien wrong
//				double sum = 0;
//				for(int j = 0 ; j < i ; j++){
//					sum += probas[j];
//				}
//				probas[i] = Math.pow(k, m)/Math.pow(nbp, m)*helper.computeCombination(nbp, k) - sum;
				
				//19 wrong?
//				double sum = 0;
//				for(int j = 0 ; j < i ; j++){
//					sum += probas[j];
//				}
//				probas[i] = Math.pow(k, m)/Math.pow(nbp, m) - sum;
				//probas[i] = Math.pow(k, m)/Math.pow(nbp, m) - Math.pow(k - 1, m)/Math.pow(nbp, m) ;
				
			}
		}
		
		double serviceTime = 0;
		for(int i = 0 ; i < min ; i++){
			int k = i+1;
			serviceTime += probas[i]*((k*timeM + (m - k)*timeH)/m);
			//System.out.println("serviceTime " + serviceTime);
		}
		serviceTime += timeR;
		
		//////////debug
		double probsum = 0;
		for(double p : probas) {
			System.out.println("nb of port " + p);
			probsum += p;
		}
		System.out.println("sum of prob " + probsum);
		
		return 1/serviceTime;
	}
	
	//compute the combination value
	public double computeCombination(int N, int k){
		double nume = 1, mom = 1;
		
		//the bord
		if(k == 0) return 1;
		
		for(int i = 1; i<= k ; i++ ){
			mom *= i;
		}
		
		for(int i = 0; i < k; i++){
			nume *= (N-i);
		}
		
		return nume/mom;
	}
	
	//compute the arragement
	public int computeArragement(int N, int k){
		int sum = 1;
		for (int i = 0 ; i < k ; i++){
			sum *= (N - i);
		}
		
		return sum;
	}
	
	//enumerator method
	public double computeMuEnumerator( double timeH, double timeM, double timeR){
		Enumerator enumator = new Enumerator(Configuration.EXTRALOT+1, Configuration.N -1);
		int nbp = Configuration.N - 1;
		int m = Configuration.EXTRALOT + 1;
		int min = (nbp > m)?m:nbp;
		double[] probas = new double[min];
			
		for(int i = 1 ; i <= min ; i++){
			probas = enumator.getProbas();
		}
		
		double serviceTime = 0;
		for(int i = 0 ; i < min ; i++){
			int k = i+1;
			serviceTime += probas[i]*((k*timeM + (m - k)*timeH)/m);
			//System.out.println("serviceTime " + serviceTime);
		}
		serviceTime += timeR;
		
		//////////debug
		double probsum = 0;
		for(double p : probas) {
			System.out.println("nb of port " + p);
			probsum += p;
		}
		System.out.println("sum of prob " + probsum);
		
		return 1/serviceTime;
		
	}
	
}

