package analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import scenario.Configuration;

//test my idea ing
/**
 * Integration of the mu computation for the cache and batch strategy
 * 
 * mu = ( 2*M + (L-2)*H )/ L + R
 * L = P(k,)*k + P(k,)*M (k >= M)
 * 
 * Proposed by Bruno, use the probabilities of the simulation
 * 
 */
public class Solver1LimitedPollingEMU {

	// Solver attributes
	private int N; // number of queues included in the model
	private List<QueueInfo> queues; // recorded queues in the model
	private int M; // batch size
	private double timeH;
	private double timeM;
	private double timeR;
	private Vector<double[]> probaIns;
	
	private String type = "inf"; // the type of formula, by defaut one Miss
	
	// Solver constructor (before any solver initialization)	
	public Solver1LimitedPollingEMU() {
		this.N = 0;
		this.queues = new Vector<QueueInfo>();
		this.probaIns = new Vector<double[]>();
	}
	
	public Solver1LimitedPollingEMU(int m, double tm, double th, double tr) {
		this.N = 0;
		this.queues = new Vector<QueueInfo>();
		this.M = m;
		this.timeH = th;
		this.timeM = tm;
		this.timeR = tr;
	}
	// Solver methods
	
	/**
	 * Add a queue in the solver
	 * @param K: capacity of the queue
	 * @param lambda: arrival rate
	 * @param mu: service rate
	 */
	public void addQueue(int K, double lambda) {
		this.N++;
		this.queues.add(new QueueInfo(K, lambda));
	}
	
	/**
	 * add the probas instant
	 */
	public void addProbasInstant(double[] probas)
	{
		this.probaIns.add(probas);
	}
	
	/**
	 * Set the batch size in the system
	 * @param m : batch size
	 */
	public void setBatchSize(int m)
	{
		this.M = m;
	}
	
	/**
	 * Choose which formula should be used
	 * @param t
	 */
	public void setType(String t)
	{
		this.type = t;
	}
	
	/**
	 * set the mus
	 * 
	 */
	public void setMu(int q_ , double mu)
	{
		this.queues.get(q_).setMu(mu);
	}
	
	
	/**
	 * 
	 * @param tm  sum of miss lookup time and service time for a packet 
	 * @param th  sum of hit lookup time and service time for a packet
	 * @param tr  releasing time for a packet
	 */
	public void setTimeParameters(double tm, double th, double tr)
	{
		this.timeM = tm;
		this.timeH = th;
		this.timeR = tr;
	}
	
	/**
	 * Set the most big lambda for thomas's method
	 * @param bl
	 */
	public void setBigLambda(double bl)
	{
		for (int i = 0 ; i < this.N ; i++)
		{
			this.queues.get(i).setLambda(bl);
		}
	}
	
	public double[] getMinProcessingTime()
	{
		
		double[] processingTime = new double[this.N];
		
		for (int i = 0 ; i < this.N ; i++)
		{
			processingTime[i] = 1/this.queues.get(i).getMu();
		}
		
		return processingTime;
	}
	
	// Print methods
	public void printMuTime()
	{
		for (int i = 0 ; i < N ; i++)
		{
			System.out.print("Mu(" + (i + 1) + ") = " + this.queues.get(i).getMu() + " ; ");
		}
		System.out.println("");
	}
	
	/**
	 * Print the average number of packets in every queue (computed by the model)
	 */
	public void printQ() {
		for (int i = 0; i<N ; i++) {
			System.out.print("Q("+(i+1)+") = "+this.queues.get(i).getQ()+" ; ");
		}
		System.out.println("");
	}
	
	/**
	 * Print the blocking probability at every queue (computed by the model)
	 */
	public void printB() {
		for (int i = 0; i<N ; i++) {
			System.out.print("B("+(i+1)+") = "+(this.queues.get(i).getState(this.queues.get(i).getK(), 0).getProba()+this.queues.get(i).getState(this.queues.get(i).getK(), 1).getProba())+" ; ");
		}
		System.out.println("");
	}
	
	/**
	 * Print the sum of the black and blue states
	 */
	public void printSumProcessedEmptyState()
	{
		double[] probas = new double[N];
		for(int i = 0 ; i < N ; i++ )
		{
			probas[i] = this.queues.get(i).getSumProcessedStateProbas();			
		}
		
		for(int i= 0; i < N ; i++ )
		{
			double sum = 0;
			for( int j = 0 ; j < this.N ; j++)
			{
				sum+=probas[j];
			}
			System.out.print("SUM(" + (i+1) + ") = " + (this.queues.get(i).getState(0, 0).getProba() + sum ) + " ; " );
		}
		
		System.out.println("");
		
	}
	
	/**
	 * Initialize the solver => initialize every queue and compute to global arrival rate
	 */
	public void init() {
		for (int i = 0; i<N ; i++) {
			this.queues.get(i).init();
			this.computeSystemInputLoad();
			
			//init the mu
			double initMu = 1 / (this.timeM + this.timeR);
			
			this.queues.get(i).setMu(initMu);
		}
	}
	
	// Computation of the Markov Chain transition rate parameters	
	
	/**
	 * Equation 6 (report)
	 * @param i: index of the queue
	 * @return Epsilon_i as calculated in Eq. 6
	 */
	public double computeEpsilonArbitrary(int i) {
		QueueInfo q = this.queues.get(i);
		double sum_den = 0.0;
		for (int k=0; k<=q.getK(); k++) {
			sum_den += q.getState(k, 1).getProba();
		}
		
		double result = q.getState(0,1).getProba()/sum_den;
		
		q.setEpsilon(result);
		
		System.out.println("[computeEpsilonArbitrary] E"+i+" estimate before contraint = "+result);
		
		result = q.getEpsilon();
		System.out.println("[computeEpsilonArbitrary] E"+i+" estimate after contraint= "+result);
		return result;
	}
	
	
	
	/**
	 * Computation of alpha_i based on Espilon_j calculated by Eq. 6
	 * @param i: index of the queue
	 * @return alpha_i estimate
	 */
	public double computeAlphaArbitrary(int i) {
		double sum = 0.0;
		QueueInfo q;
		for (int j=0 ; j<N ; j++) {
			if (j!=i) {
				q = this.queues.get(j);
				sum += (1.0/q.getMu())*(1 - this.computeEpsilonArbitrary(j));
			}	
		}
		q = this.queues.get(i);
		double result = (1.0-q.getG())/sum;
		System.out.println("[ComputeAlphaArbitrary] Alpha estimate = "+result);
		return result;
	}
	

	
	/**
	 * Compute f_i
	 * @param i: index of the queue
	 * @return f_i estimate
	 */
	public double computeGammaCut(int i) {
		double result = 0.0;
		
		
		QueueInfo q = this.queues.get(i);
		
		double old_value = q.getGamma();
		
		//result = (q.getSystemInputLoad()*(1-q.getServerUtilization())-q.getMu()*q.getF()*q.getState(1,0).getProba())/( q.getState(0,1).getProba() );
		result = (q.getSystemInputLoad()*(q.getState(0, 0).getProba())-q.getMu()*q.getF()*q.getState(1,0).getProba())/( q.getState(0,1).getProba() );
		
		if (result <= 0) {
			result = old_value/2;
		}
	
		System.out.println("[computeGammaCut] Gamma"+i+" estimate = "+result);
		System.out.println("[computeGammaCut] First Gamma assoicated "+ i + " " + (q.getSystemInputLoad()*(q.getState(0, 0).getProba())-q.getMu()*q.getF()*q.getState(1,0).getProba()));
		System.out.println("[computeGammaCut] Secon Gamma assoicated "+ i + " " + q.getState(0, 1).getProba());
		return result;
	}
	
	
	/**
	 * Compute g_i based on Espilon_j calculated by Eq. 6
	 * @param i: index of the queue
	 * @return g_i estimate
	 */
	public double computeGArbitrary(int i) {
		double product = 1.0;
		for (int j=0; j<N; j++) {
			if (j != i) {
				product = product*this.computeEpsilonArbitrary(j);
				//product = product*this.computeEpsilonPrime(j);
			}
		}
		System.out.println("[computeGArbitrary] G"+i+" ESTIMATE = "+product);
		return product;
	}
	
		

	/**
	 * approximation Inferior borne
	 * conditionne
	 * @param q_
	 * @return
	 */
	private double approximationInferior(int q_)
	{
		double serviceTime = 0;
		QueueInfo q = this.queues.get(q_);
		double m = 0 ;
		for (int i = 1; i <= q.getK(); i++) {
			int k = 0;
			if (i >= M)
				k = M;
			else
				k = i;
			double prob = this.probaIns.get(q_)[i]/(1 - this.probaIns.get(q_)[0]);
			m += prob * k;
		}
		
		serviceTime += (this.timeM + (m - 1)*this.timeH)/m+ this.timeR ; 
		
		return serviceTime;
	}
	/**
	 * approximation superior formula
	 * @param q_
	 * @return
	 */
	private double approximationSuperior(int q_)
	{
		double serviceTime = 0;
		QueueInfo q = this.queues.get(q_);
		double m = 0 ;
		for (int i = 1; i <= q.getK(); i++) {
			int k = 0;
			if (i >= M)
				k = M;
			else
				k = i;
			
			double prob = this.probaIns.get(q_)[i]/(1 - this.probaIns.get(q_)[0]);
			m += prob * k;
		}
		
		//serviceTime += (2*this.timeM + (m - 2)*this.timeH)/m + this.timeR;
		double second = ((m - 2)>0)?(m-2):0;
		//serviceTime += (2*this.timeM + second*this.timeH)/m + this.timeR;
		double first = 0 ;
		if(m-1<=0) first = 0;
		else if(m-1<1) first = m-1;
		else first = 1;
		//serviceTime += (this.timeM*((m>1)?1:m) + first*this.timeM + second*this.timeH)/m + this.timeR;
		serviceTime += (this.timeM*(m>2?2:m) + second*this.timeH)/m + this.timeR;
		
		return serviceTime;
	}
	
	/**
	 * approximation pondération
	 * @param q_
	 * @return
	 */
	private double approximationPonderation(int q_)
	{
		double serviceTime = 0;
		QueueInfo q = this.queues.get(q_);
		double m = 0 ;
		for (int i = 1; i <= q.getK(); i++) {
			int k = 0;
			if (i >= M)
				k = M;
			else
				k = i;
			
			double prob = this.probaIns.get(q_)[i]/(1 - this.probaIns.get(q_)[0]);
			m += prob * k;
		}
		
		double min = ((Configuration.N - 1) > m)?m:(Configuration.N - 1);
		double proba = Math.pow(1/min, m);
		double serviceTimeInf = (this.timeM + (m - 1)*this.timeH)/m+ this.timeR ;
		double first = 0 ;
		if(m-1<=0) first = 0;
		else if(m-1<1) first = m-1;
		else first = 1; 
		double serviceTimeSup = (this.timeM + first*this.timeM + (((m - 2)>0)?(m-2):0)*this.timeH)/m + this.timeR;

		serviceTime = min*proba*serviceTimeInf + (1 - min*proba)*serviceTimeSup;
		
		return serviceTime;
	}
	
	
	/**
	 * Compute the mu for each port in the case batch and cache
	 *
	 */
	//TODO:pronctuation
	public double computeProcessingRate(int q_)
	{
		
		double serviceTime = 0;
		QueueInfo q = this.queues.get(q_);
		//now
		switch (type)
		{
			case "inf":{
				serviceTime = this.approximationInferior(q_);
				break;
			}
			case "sup":{
				serviceTime = this.approximationSuperior(q_);
				break;
			}	
			case "pon":{
				serviceTime = this.approximationPonderation(q_);
				break;
			}
		}
		
		q.setMu(1/serviceTime);
		return serviceTime;
		
		
	}
	
	
	/**
	 * Compute server utilisation U using Eq. 3 (see report)
	 * @return U estimate
	 */
	public double computeServerUtilization() {
		double result = 0.0;
		double old_result = 0.5; // to possibly perform corrections if U out of bounds
		QueueInfo q;
		for (int i=0; i<N; i++) {
			q = this.queues.get(i);
			old_result = q.getServerUtilization();
			result += q.getLambda()*(1- (q.getState(q.getK(),0).getProba()+q.getState(q.getK(),1).getProba()) )/q.getMu();
		}
		
		System.out.println("[computeServerUtilization] U estimate = "+result);
		if (result > 1) {
			result = (old_result+1)/2;
			System.out.println("[computeServerUtilization] U corrected to "+result);
		}
		return result;
	}

	
	/**
	 * Compute the state (0,F) using the sum of black and blue is 1
	 * @return probability (0,F)
	 * 
	 */
	public double computeProcessedPrbas()
	{
		double sum = 0;
		for(int i = 0 ; i < this.N ; i++)
		{
			sum+=this.queues.get(i).getSumProcessedStateProbas();
		}
		return sum;
	}
	
	/**
	 * 
	 * @param i   NO. of Queue
	 * @param m	  Batch size
	 * @return proba    probability sum of states bigger than m  
	 */
	public double computeProbaM(int i, int m)
	{
		double proba = 0;
		
		QueueInfo q = this.queues.get(i);
		
		for (int k = m; k <= q.getK(); k++)
		{
			proba += q.getState(k, 0).getProba() + q.getState(k, 1).getProba();
		}
		
		return proba;
	}
	
	 
	

	
	// Fixed-point methods
	
	/**
	 * Run an iteration of the fix-point problem
	 */
	//TODO:iterative
	public void iterate() {
		
		double uvalue = this.computeServerUtilization();
		
		for (int i = 0; i<N ; i++) {
			
			System.out.println("QUEUE NUMBER "+i);
						
			this.queues.get(i).setServerUtilization(uvalue);
			
			//recaluculate mu
			/*
			double muvalue = this.computeProcessingRate(i);
			this.queues.get(i).setMu(muvalue);
			*/
			
			//////////////////////////
			double prob0F = 1 - this.computeProcessedPrbas();
			this.queues.get(i).set0F(prob0F);
			/////////////////////////
			
		
			double gvalue = this.computeGArbitrary(i);
			this.queues.get(i).setG(gvalue);
			
			double fvalue = gvalue;
			this.queues.get(i).setF(fvalue);						
			
			double alphavalue = this.computeAlphaArbitrary(i);
			this.queues.get(i).setAlpha(alphavalue);
			
			double gammavalue = this.computeGammaCut(i);
			this.queues.get(i).setGamma(gammavalue); 
			
			this.queues.get(i).solve();
			



			
		}
	}
	
	
	/**
	 * the convergence criteria satisfaction
	 * @param eps: precision (e.g. 0.000001)
	 * @return true or false
	 */
	public boolean checkCriteria(double eps) {
		boolean result = true;
		for (int i = 0; i<N ; i++) {
			double dif = this.queues.get(i).getDiff();
			//double crit = this.queues.get(i).getMaxVar(); // Criteria of type 1 (state probas)
			double crit = this.queues.get(i).getVarQ(); // Criteria of type 2 (average queue size)
			System.out.println("[Queue "+i+"] rel. diff. relative since last iteration = " + crit);
			System.out.println("[Queue "+i+"] rel. diff. since last iteration = " + dif);
			//result = result && (crit<eps || dif < eps) ;
			result = result && (crit<eps );
		}
		if (result == true) {
			System.out.println("Model convergence reached !");
		}
		return result;
	}
	
	/**
	 * Solve the fix-point problem in verbose mode
	 * @param eps: convergence criteria precision
	 * @param limit: max. number of allowed iterations
	 */
	public void run(double eps, int limit) {
		this.run(eps, true, limit);
	}
	
	/**
	 * Solve the fix-point problem
	 * @param eps: convergence criteria precision
	 * @param verbose_mode: print the resulting calculations at the end of each iteration (true) / only once convergence is reached (false)
	 * @param limit: max. number of allowed iterations
	 */
	public void run(double eps, boolean verbose_mode, int limit) {
		int nb_max_iter = limit;
		int nb_iter = 0;
		while (!this.checkCriteria(eps) && (nb_iter<nb_max_iter)) {
			nb_iter++;
			this.iterate();
			if (verbose_mode) {
				System.out.println("Iteration "+(nb_iter)+":");
				this.printMuTime();
				this.printQ();
				this.printB();
				this.printSumProcessedEmptyState();
			}
		}
		if (!verbose_mode) { // print only the last iteration results
			System.out.println("\nSolver results with "+ nb_iter +" iterations:");
			this.printMuTime();
			this.printQ();
			this.printB();
			this.printSumProcessedEmptyState();
			//TODO:
			/*
			for(int i = 0; i < this.N ; i++)
			{
				System.out.print("P(K,R) "  + this.queues.get(i).getState(this.queues.get(i).getK(), 1).getProba() + " ");
			}
			System.out.println("");
			*/
		}
	}
	
	// Tracing methods
	
	/**
	 * Trace the model results at the end of a trace output file
	 * @param traceFile: selected trace output file
	 */
	public void traceResults(String traceFile) {
		// Trace format: N lambda_1 mu_1 Q_1 B_1 ... lambda_N mu_N Q_N B_N
		String outputString = ""+N+"";
		double K;
		double lambda;
		double mu;
		double Q;
		double B;
		
		for (int i=0; i<N; i++) {
			K = this.queues.get(i).getK();
			lambda = this.queues.get(i).getLambda();
			mu = this.queues.get(i).getMu();
			Q = this.queues.get(i).getQ();
			B = this.queues.get(i).getState(this.queues.get(i).getK(), 0).getProba() + this.queues.get(i).getState(this.queues.get(i).getK(), 1).getProba();
			outputString = outputString+" "+lambda+" "+mu+" "+Q+" "+B;
		}
		String fileAddr = System.getProperty("user.dir") + "/traces/"+ traceFile;
		try
		{
			boolean erase = false; //true;
			FileWriter fw = new FileWriter(fileAddr, !erase);
			BufferedWriter output = new BufferedWriter(fw);
			output.write(outputString+"\n");
			output.flush(); // Send to the file
			output.close();
		} 
		catch(IOException ioe){
			System.out.print("Error: ");
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Trace the sum of state blue and black
	 * @param traceFile
	 */
	public void traceSum(String traceFile)
	{
		String outputString = ""+N+"";
		double lambda_in = this.computeSystemInputLoad();
		outputString = outputString + " "+lambda_in;
		double sum = 0;
		
		for(int i = 0 ; i < this.N ; i++)
		{ 
			double part = this.computeProcessedPrbas();
			sum = this.queues.get(i).getState(0, 0).getProba() + part;
			outputString = outputString + " " + "id:" + (i+1) + " " + "(0,F):" +this.queues.get(i).getState(0, 0).getProba()+ " " + "ProcessedProbas:" +part+" " + "Sum:"+sum;  
		}
		
		String fileAddr = System.getProperty("user.dir") + "/traces/"+ traceFile;
		try
		{
			boolean erase = false; //true;
			FileWriter fw = new FileWriter(fileAddr, !erase);
			BufferedWriter output = new BufferedWriter(fw);
			output.write(outputString+"\n");
			output.flush(); // Send to the file
			output.close();
		} 
		catch(IOException ioe){
			System.out.print("Error: ");
			ioe.printStackTrace();
		}
		
	}
	
	public void traceIteration(String f)
	{
		String outputstring = "";
		
		
		//file address
		String fileAddr = System.getProperty("user.dir") + "/traces/" + f;
		try
		{
			boolean erase = false;
			FileWriter fw = new FileWriter(fileAddr, !erase);
			BufferedWriter output = new BufferedWriter(fw);
			output.write(outputstring + "\n");
			output.flush();
			output.close();
		}
		catch(IOException ioe)
		{
			System.out.println("Error: ");
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Trace the state distribution computed by the model of a given queue
	 * @param traceFile: selected output trace file (erased each time the method is called)
	 * @param i: index of the queue
	 */
	public void traceDistribution(String traceFile, int i) {
		// Trace format: N K lambda_i mu_i pi_0,0 pi_0,1 pi_1,0 pi_1,1 ... pi_K,0, pi_K_1
		double lambda = this.queues.get(i).getLambda();
		double mu = this.queues.get(i).getMu();
		double pi;
		String outputString = ""+N+" "+this.queues.get(i).getK()+" "+lambda+" "+mu;
		for (int k=0; k<(this.queues.get(i).getK()+1); k++) {
			pi = this.queues.get(i).getState(k,0).getProba();
			outputString = outputString+" "+pi;
			pi = this.queues.get(i).getState(k,1).getProba();
			outputString = outputString+" "+pi;
		}
		String fileAddr = System.getProperty("user.dir") + "/traces/"+ traceFile;
		try
		{
			boolean erase = true; //true;
			FileWriter fw = new FileWriter(fileAddr, !erase);
			BufferedWriter output = new BufferedWriter(fw);
			output.write(outputString+"\n");
			output.flush(); // Send to the file
			output.close();
		}
		catch(IOException ioe){
			System.out.print("Error: ");
			ioe.printStackTrace();
		}
	}

	
	/**
	 * Compute the system global arrival rate Lambda_in
	 */
	public double computeSystemInputLoad() {
		double result = 0;
		for (int i = 0; i<N ; i++) {
			result += this.queues.get(i).getLambda();
		}
		for (int i = 0; i<N ; i++) {
			this.queues.get(i).setSystemInputLoad(result);
		}		
		
		return result;
	}
	
	
	/***************************************************************************/
	/*
	 *	INNER CLASSES USED TO CHARACTERIZE EVERY QUEUE AND MARKOV CHAIN STATE  
	 */
	/***************************************************************************/
	
	// QueueInfo inner class
	private class QueueInfo {
		
		// Attributes
		private int K; // queue capacity
		private double lambda; // queue arrival rate
		private double mu; // queue service rate
		private QueueState[][] states; // queue states
		private QueueState[][] oldStates; // queue states at the previous model iteration
		
		private double of = 0.5;
		private double alpha; // alpha estimate
		private double gamma; // gamma estimate
		private double f; // f estimate
		private double g; // g estimate
		private double epsilon; // epsilon   P(0,E)/Sum(E and R)
		
		private double systemInputLoad; // system global arrival rate Lambda_in
		private double serverUtilization; // server utilization rate U
		
		// Constructor
		public QueueInfo(int K_, double lambda_) {
			this.K = K_;
			this.lambda = lambda_;
			this. alpha = 1; // Initialized with 1 to avoid zero division
			this. gamma = 1; // Initialized with 1 to avoid zero division
			
			this.f = 0.5; // Initialized with 1/2
			this.g = 0.5; // Initialized with 1/2
			
			this.systemInputLoad = 0.5; // Initialized with 1/2
			this.serverUtilization = 0.5; // Initialized with 1/2
			this.states = new QueueState[K+1][2]; // Table of states that will be initialized later on
			this.oldStates = new QueueState[K+1][2]; // Table of states that will be initialized later on
		}
		
		// Methods
					
		/**
		 * Initialization methods
		 */
		public void init() {
			for (int k = 0 ; k<this.K+1 ; k++) {
				this.states[k][0] = new QueueState(0.5); // PROCESSING STAGE (in the Markov Chain)
				this.states[k][1] = new QueueState(0.5); // VACATION STAGE (in the Markov Chain)
				this.oldStates[k][0] = new QueueState(0.99);
				this.oldStates[k][1] = new QueueState(0.99);
			}
			this.normalize(); // DO NOT AFFECT OLDSTATES
		}
		
		
		// The get methods
		
		/**
		 * Get the queue capacity
		 * @return K
		 */
		public int getK() {
			return this.K;
		}
		
		/**
		 * Get the queue arrival rate
		 * @return lambda
		 */
		public double getLambda() {
			return this.lambda;
		}
		
		/**
		 * Get the queue service rate
		 * @return mu
		 */
		public double getMu() {
			return this.mu;
		}
		
		/**
		 * 
		 * @return epsilon
		 */
		public double getEpsilon()
		{
			return this.epsilon;
		}
		
		/**
		 * Get the queue alpha rate
		 * @return alpha
		 */
		public double getAlpha() {
			return this.alpha;
		}
		
		/**
		 * Get the queue gamma rate
		 * @return alpha
		 */
		public double getGamma() {
			return this.gamma;
		}
		
		
		/**
		 * Get the queue proba. f
		 * @return f
		 */
		public double getF() {
			return this.f;
		}
		
		/**
		 * Get the queue proba. g
		 * @return f
		 */
		public double getG() {
			return this.g;
		}
		
		/**
		 * Get the new 0F
		 * @return of
		 */
		public double getOf(){
			return this.of;
		}
		
		/**
		 * Get the value of the server utilization rate
		 * @return U
		 */
		public double getServerUtilization() {
			return this.serverUtilization;
		}
		
		/**
		 * Get the value of Lambda_in
		 * @return Lambda_in
		 */
		public double getSystemInputLoad() {
			return this.systemInputLoad;
		}		
		
		/**
		 * Get a state in the associated Markov chain
		 * @param k: number of packets in the queue
		 * @param stage: 0 (upper line) / 1 (bottom line)
		 * @return
		 */
		public QueueState getState(int k, int stage) {
			return this.states[k][stage];
		}
		
		/**
		 * Get the sum of the state probas.
		 * @return normally..1 !
		 */
		public double getSumProbas() {
			double S=0;
			for (int k = 0 ; k<(this.K+1) ; k++) {
				S += (this.getState(k,0).getProba()+this.getState(k,1).getProba());
			}
			return S;
		}
		
		/**
		 * Get the sum of the blue state probas
		 * @return the sum of those state probas
		 */
		public double getSumProcessedStateProbas()
		{
			double sum = 0;
			for(int k = 1 ; k <= this.K ; k++)
			{
				sum += this.getState(k, 0).getProba();
			}
			return sum;
		}
		
		/**
		 * Get the average queue size
		 * @return Q
		 */
		public double getQ() {
			double sum = 0;
			for (int k = 0 ; k<(this.K+1) ; k++) {
				sum += k*(this.getState(k, 0).getProba()+this.getState(k, 1).getProba());
			}
			return sum;
		}
		

		// The get methods for checking the convergence criteria
		
		/**
		 * Get the variation between iteration t-1 and iteration t of the average queue size estimate
		 * @return relative difference between Q(t-1) and Q(t) 
		 */
		public double getVarQ() {
			double result = 0;
			double n_old = 0;
			double n = 0;
			for (int k = 0 ; k<this.K+1 ; k++) {
				n_old += k*(this.oldStates[k][0].getProba()+this.oldStates[k][1].getProba());
				n += k*(this.states[k][0].getProba()+this.states[k][1].getProba());
			}	
			result = Math.abs(n_old - n)/n;
			return result;
		}
		
		/**
		 * Get the max. variation between iteration t-1 and iteration t of queue state probas
		 * @return max relative difference between pi(t-1) and pi(t) 
		 */
		public double getMaxVar() {
			///////
			int g = 0;
			int s = 0;
			double newProb = 0;
			double oldProb = 0;
			///////
			
			double result = 0;
			double test = 0;
			for (int k = 0 ; k<this.K+1 ; k++) {
				test = Math.abs(this.oldStates[k][0].getProba() - this.states[k][0].getProba())/this.oldStates[k][0].getProba();
				if (test>result) {
					result = test;
					newProb = this.states[k][0].getProba();
					oldProb = this.oldStates[k][0].getProba();
					g = k;
					s = 0;
				}
				test = Math.abs(this.oldStates[k][1].getProba() - this.states[k][1].getProba())/this.states[k][1].getProba();
				if (test>result) {
					result = test;
					newProb = this.states[k][1].getProba();
					oldProb = this.oldStates[k][1].getProba();
					g = k;
					s = 1;
				}
			}
			
			System.out.println("New Proba: " +newProb + " location: " +g+" "+s);
			System.out.println("Old Proba: " +oldProb);
			return result;
		}
		
		/**
		 * Get the max differences between two iteration
		 * @return the difference
		 */
		public double getDiff()
		{
			double result = 0;
			double test = 0;
			for (int k = 0 ; k<this.K+1 ; k++) {
				test = Math.abs(this.oldStates[k][0].getProba() - this.states[k][0].getProba());
				if (test>result) {
					result = test;
				}
				test = Math.abs(this.oldStates[k][1].getProba() - this.states[k][1].getProba());
				if (test>result) {
					result = test;
				}
			}
			
			return result;
		}
		
		
		// The set methods
		/**
		 * Set the value of the Service rate depending the input rate
		 * @param value : new value
		 */
		public void setMu(double value)
		{
			double oldMu = this.getMu();
			
			if (value <= 0) this.mu = oldMu/2;
			else this.mu = value;
		}
		
		/**
		 * Set the value of the input load for a queue
		 * @param value
		 */
		
		public void setLambda(double value)
		{
			this.lambda = value;
		}
		

		
		/**
		 * Set the value of the alpha rate.
		 * @param value: new value
		 */
		public void setAlpha(double value) {
			this.alpha = value;
		}
		
		/**
		 * Set the value of the gamma rate.
		 * @param value: new value
		 */
		public void setGamma(double value) {
			this.gamma = value;
		}
		
		/**
		 * Set the value of the f proba.
		 * @param value: new value
		 */
		public void setF(double value) {
			this.f = value;
		}
		
		/**
		 * Set the value of the g proba.
		 * @param value: new value
		 */
		public void setG(double value) {
			this.g = value;
		}
		
		/**
		 * Set the state (0,F) using the new equation:
		 * (0,F)+ { (K,P) | K > 0 } = 1  
		 * @param value: new value
		 */
		public void set0F(double value )
		{
			double oldValue = this.getOf();
			if (value >= 1)
				value = (1+oldValue)/2;
			if (value <= 0)
				value = oldValue/2;
			
			this.of = value;
			this.states[0][0].setProba(value);
		}
		
		/**
		 * Set epsilon between 0 and 1
		 * @param value
		 */
		public void setEpsilon(double value)
		{
			double oldEpsilon = this.getEpsilon();
			
			if (value <= 0) this.epsilon = oldEpsilon/2;
			else if (value >= 1) this.epsilon = (this.epsilon + 1)/2;
			else this.epsilon = value;
		}
		
		/**
		 * Set the value of Lambda_in
		 * @param value: new value
		 */
		public void setSystemInputLoad(double value) {
			this.systemInputLoad = value;
		}

		/**
		 * Set the value of the server utilization rate U
		 * @param the new value
		 */
		public void setServerUtilization(double value) {
			this.serverUtilization = value;
		}
		
		
		// Methods used to solve the fix-point problem
		
		/**
		 * Backup states obtained during the current (will become previous) fix-point iteration
		 */
		public void backupStates() {
			for (int k = 0 ; k<this.K+1 ; k++) {
				this.oldStates[k][0].setProba(this.states[k][0].getProba());
				this.oldStates[k][1].setProba(this.states[k][1].getProba());
			}
		}
				
		/**
		 * Normalization method.
		 */
		public void normalize() {
			double S = this.getSumProbas();
			for (int k = 0 ; k<(this.K+1) ; k++) {
				this.getState(k,0).setProba(this.getState(k,0).getProba()/S);
				this.getState(k,1).setProba(this.getState(k,1).getProba()/S);
			}
			System.out.println("Normalize from sum probas = "+S);
		}

		/**
		 * Method that solves the Markov chain
		 */
		public void solve() {
			
			this.backupStates();
			
			double p00 = this.states[0][0].getProba();

			
			double p01 = (1.0/ (this.getGamma()+this.getF()*this.getLambda()))*p00*(this.getSystemInputLoad()-this.getF()*this.getLambda());
			this.getState(0,1).setProba(p01);
			System.out.println("Proba PI_O,E = "+p01);
			
			double p10 = (this.getState(0,1).getProba()+this.getState(0,0).getProba())*this.lambda/this.mu;
			this.getState(1,0).setProba(p10);
			System.out.println("Proba PI_1,P = "+p10);
			
			double p11 = (this.getState(1,0).getProba()*(1-this.getG())+this.getState(0,1).getProba())*this.getLambda()*1.0/(this.getAlpha()+this.getG()*this.getLambda());
			this.getState(1,1).setProba( p11 );
			
			double p20 = (this.getState(1,1).getProba()+this.getState(1,0).getProba())*this.lambda/this.mu;
			this.getState(2,0).setProba( p20 );
			
			// GENERAL CASE k >= 2
			for (int k = 2; k<this.K ;k++) {
				
				double pk1 = (this.getState(k-1,1).getProba() + this.getState(k,0).getProba()*(1-this.getG()))*this.getLambda()/( this.getAlpha() + this.getG()*this.getLambda() );
				this.getState(k,1).setProba( pk1 );
				
				double pkp10 = ( this.getState(k,0).getProba() + this.getState(k,1).getProba() )*this.lambda/this.mu;
				this.getState(k+1,0).setProba( pkp10 );
								
			}
			
			double pK1 = this.getState(this.K-1,1).getProba()*this.lambda/this.alpha;
			
			this.getState(this.K,1).setProba(pK1);
			
			System.out.println("[Markov chain] before norm. p00 = "+this.getState(0, 0).getProba());
			
			// NORMALIZATION STEP
			this.normalize();
			
			System.out.println("[Markov chain] after norm. p00 = "+this.getState(0, 0).getProba());
		}
				
	}
	
	// QueueState inner class
	private class QueueState {
		
		// Attributes
		private double proba; // state proba
		
		// Constructor
		public QueueState(double init_value) {
			this.proba = init_value;
		}
		
		// Methods
		
		/**
		 * Get the value of the state proba.
		 * @return proba
		 */
		public double getProba() {
			return this.proba;
		}
		
		/**
		 * Set the value of the state proba.
		 * @param the new value
		 */
		public void setProba(double value) {
			//control the value super 0
			
			if(value <= 0) this.proba /= 2;
			else this.proba = value;
		}
	}
	
}