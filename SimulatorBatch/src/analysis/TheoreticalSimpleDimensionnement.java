package analysis;

import java.math.*;
import analysis.TheoreticalBox;

/**
 * The scenario is for the simple dimensionnement.
 * The model is M/M/1/K. Every file is served by a CPU.
 * But a NIC can have many files.
 * 
 *
 */


/**
 * 
 * TRY TO USE THE ITERRATION METHOD FOR SOLVING THE PROBALIBITIES
 *
 */
public class TheoreticalSimpleDimensionnement {

	//ATTRIBUTES
	int numberOfCPU = 4; // total number of CPU
	int numberOfNIC = 4; // total number of NIC
	double mu = 8;       // service rate
	double[] lambdas;    // the list of arrive rate
	int K = 512;         // the capacity of the queue

	public TheoreticalSimpleDimensionnement ( int nbCPU, int nbNIC , double[] lams , double mu , int K)
	{
		this.numberOfCPU = nbCPU;
		this.numberOfNIC = nbNIC;
		this.lambdas = lams;
		this.mu = mu;
		this.K = K;
	}


	//METHOD for calculate the portion of CPU use
	public void calculeDimensionnementOfCPU(double constraitPortionOfCPU)
	{
		int[] nbCPUs = new int[this.numberOfNIC];
		 
		 //initialize array list, simple for forbiding some wrong
		 for(int i = 0 ; i < this.numberOfNIC ; i++)
		 {
			 nbCPUs[i] = 1;
		 }
		 
		 //calculate the portion and find the minimum number CPU used	 tmpQueue.run();
		 //and caculate the sum of CPUs used
		 int sumCPUs = 0;
		 for(int i = 0 ; i < this.numberOfNIC ; i++)
		 {
			 QueueInfo tmpQueue = new QueueInfo(K, this.lambdas[i]/nbCPUs[i] , this.mu );
			 tmpQueue.run();
			 double tmpTaux = tmpQueue.getPortionService();
			 while(tmpTaux > constraitPortionOfCPU)
			 {
				 nbCPUs[i] += 1;
				 tmpQueue.setLambda(this.lambdas[i]/nbCPUs[i]);
				 tmpQueue.run();
				 tmpTaux = tmpQueue.getPortionService();
				 //System.out.println(i + " " + nbCPUs[i] + " " + tmpTaux );
			 } 
			 
		 }
		 
		 //the sum of CPUs used
		 for(int i = 0 ; i < this.numberOfNIC ; i++)
		 {
			 sumCPUs += nbCPUs[i];
			 System.out.println(i + " NIC need " + nbCPUs[i] + " CPUs ");
		 }
		 
		 //test if the sum is bigger than the number total
		 if(sumCPUs > this.numberOfCPU)
		 {
			 System.out.println("we can find the solution, the minimum is " + sumCPUs);
		 }
		 else
		 {
			 System.out.println("we find the solution, the minimum CPUs used is " + sumCPUs);
		 }
		 
		 
	}

 
	//METHOD for calculate the portion of queue use
	public void calculeDimensionnementOfQueue(double contraintPortionOfQueueUsed)
	{
		 int[] nbCPUs = new int[this.numberOfNIC];
		 
		 //initialize array list, simple for forbiding some wrong
		 for(int i = 0 ; i < this.numberOfNIC ; i++)
		 {
			 nbCPUs[i] = 1;
		 }
		 
		 //calculate the portion and find the minimum number CPU used
		 //and caculate the sum of CPUs used
		 int sumCPUs = 0;
		 for(int i = 0 ; i < this.numberOfNIC ; i++)
		 {
			 QueueInfo tmpQueue = new QueueInfo(K, this.lambdas[i]/nbCPUs[i] , this.mu );
			 tmpQueue.run();
			 double tmpTaux = tmpQueue.getPortionQueue();
			 while(tmpTaux > contraintPortionOfQueueUsed)
			 {
				 nbCPUs[i] += 1;
				 tmpQueue.setLambda(this.lambdas[i]/nbCPUs[i]);
				 tmpQueue.run();
				 tmpTaux = tmpQueue.getPortionQueue();
				 //System.out.println(i + " " + nbCPUs[i] + " " + tmpTaux );
			 } 
			 
		 }
		 
		 //the sum of CPUs used
		 for(int i = 0 ; i < this.numberOfNIC ; i++)
		 {
			 sumCPUs += nbCPUs[i];
			 System.out.println(i + " NIC need " + nbCPUs[i] + " CPUs ");
		 }
		 
		 //test if the sum is bigger than the number total
		 if(sumCPUs > this.numberOfCPU)
		 {
			 System.out.println("we can find the solution, the minimum is " + sumCPUs);
		 }
		 else
		 {
			 System.out.println("we find the solution, the minimum CPUs used is " + sumCPUs);
		 }
		 

	}
	
	
	//for starting the dimensionnement
	/*
	public static void main(String[] args)
	{
		int numberCPU = 16;
		int numberNIC = 4;
		double contraintPortionUse = 0.65;
		double contraintPortionQueue = 0.25;
		double[] lams = {2, 3, 5, 10};
		double mu = 5;
		int K = 256;

		TheoreticalSimpleDimensionnement tsd = 
				new TheoreticalSimpleDimensionnement (numberCPU, numberNIC , lams , mu , K );
		//tsd.calculeDimensionnementOfCPU(contraintPortionUse);
		
		tsd.calculeDimensionnementOfQueue(contraintPortionQueue);
		
	}
	*/
	
	
	
	
	
	/*****************************************************************/
	/**
	 * This is the private class. It has the informations about a queue 
	 * 
	 *
	 */
	private class QueueInfo
	{
		//ATTRIBUTS
		private double lamda; //the arrive rate
		private double mu; // the service rate
		private int K; // the capacity of a queue
		private StateInfo[] states; //array list of states probabilities
		
		//CONSTRUCTOR
		public QueueInfo(int K , double lam, double mu)
		{
			this.K = K;
			this.lamda = lam;
			this.mu = mu;
			this.states = new StateInfo[K+1]; //it must have a case 0
		}
		
		//set lambda
		public void setLambda(double lam)
		{
			this.lamda = lam;
		}
		
		//METHODE
		private void init()
		{
			/*attention : we should use initialize all the case.
			 *	Because we use the our own class
			 */
			
			for(int i = 0 ; i <= K ; i++)
			{
				this.states[i] = new StateInfo(1);
			}
			
		}
		
		//start the calculate
		public void run()
		{
			this.init();
			this.calculateProba();
		}
		//get the occupe of the queue
		public double getPortionQueue()
		{
			
			double meanClient = this.getMeanNbClient();
			
			double tao = 0;
			tao = meanClient / this.K;
			
			return tao;
			
		}
		
		
		//intern method for calculate Prba
		private void calculateProba()
		{
			double rho = this.lamda / this.mu;
			for(int i = 1 ; i <= K ; i++)
			{
				this.states[i].setProba( this.states[i-1].getProba() * rho );
			}
			
			double sumProba = this.getSumProbas();
			this.normalize(sumProba);
		}
		
		//get the blocking probablity
		public double getBlockProba()
		{
			return this.states[K].getProba();
		}
		
		//get the portion service
		public double getPortionService()
		{
			return 1 - this.states[0].getProba();
		}
		
		private double getMeanNbClient()
		{
			double mean = 0;
			
			for(int i = 1 ; i <= K ; i++)
			{
				mean += i*this.states[i].getProba();
			}
			
			return mean;
		}
		
		public double getSumProbas()
		{
			double sumProba = 0;
			
			for(int i = 0 ; i <= K ; i++)
			{
				sumProba += this.states[i].getProba();
			}
			
			return sumProba;
		}
		
		private void normalize(double sum)
		{
			for(int i = 0 ; i <= K ; i++)
			{
				this.states[i].setProba(this.states[i].getProba()/sum);;
			}
		}
	}
	
	
	/**
	 * This is the private class for stock the probability information
	 */
	private class StateInfo
	{
		//ATTRIBUTS
		private double proba = 1.0;
		
		public StateInfo( double init_proba )
		{
			this.proba = init_proba;
		}
		
		public void setProba( double proba )
		{
			this.proba = proba;
		}
		
		public double getProba()
		{
			return	this.proba;
		}
	}


}



