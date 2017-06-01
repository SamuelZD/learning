package analysis;

import java.util.Vector;

/**
 * helper for find the lambda
 * 
 * trace Lambda and find the Lambda optimal.
 * 
 * 
 * Proposed by Thomas
 * is writing
 * 
 */
public class Solver1LimitedPollingHelperThomas {

	//definition parameters
	private Solver1LimitedPollingEThomas solver;
	private Vector<Double> percentages;
	private double currentLambda;
	public double LambdaC; //debug
	private Vector<Double> maxMus;
	private Vector<Integer> K;
	private int N;
	private double probaThreshold;
	private double diffThreshold = 0.001;
	private int M;
	
	public Solver1LimitedPollingHelperThomas(double Lambda)
	{
		this.maxMus = new Vector<Double>();
		this.percentages = new Vector<Double>();
		this.K = new Vector<Integer>();
		this.currentLambda = Lambda;
	}
	
	/**
	 * 
	 * @param k_
	 * @param lam_
	 * @param mu_ the max mu for the batch and cache
	 */
	public void addQueue(int k_, double percentage_, double mu_)
	{
		N++;
		this.maxMus.add(mu_);
		this.percentages.add(percentage_);
		this.K.add(k_);
		
	}
	
	public void setProbaThreshold(double proba)
	{
		this.probaThreshold = proba;
	}
	
	public void setDiffThreshold(double diff_)
	{
		this.diffThreshold = diff_;
	}
	
	public void setQueueLambda(int q, double lambda_)
	{
		solver.setQueueLambda(q, lambda_);
	}
	
	public void setBatchSize(int m)
	{
		this.M = m;
	}
	
	private double dichotomy( double lowers, double uppers)
	{
		double med =  0;
		
		
		
		// return

		boolean ifReturn = true;


		if (Math.abs(uppers - lowers) > this.diffThreshold/100) {ifReturn = false;}	

		med = ( lowers + uppers ) / 2;
	
		//debug
		System.out.print(" med : " + med);

		System.out.println("");
		
		if(ifReturn)
		{
			return med;
		}
		
		// recursive
		this.solver  = new Solver1LimitedPollingEThomas();
		for(int i = 0 ; i< this.N ; i++)
		{
			this.solver.addQueue(this.K.get(i), med*this.percentages.get(i), this.maxMus.get(i));
			//this.setQueueLambda(i, med*this.percentages.get(i)); //IMPORTANT
		}
		this.solver.init();
		this.solver.run(0.00000001, false, 100000);
		
		double[] probas = new double[this.N]; //can be modify
		double tempLowers = lowers;
		double tempUppers = uppers;
		ifReturn = true;
		for(int i = 2 ; i < this.N ; i ++) //i = 2 just compare the 3th port
		{
			probas[i] = this.solver.computeProbaM(i, this.M);
			
			double dif = probas[i] - this.probaThreshold;
			
			
			if(Math.abs(dif)>this.diffThreshold)
			{
				ifReturn &= false;
				if (dif <= 0)
				{
					tempLowers = med;
				}
				else
				{
					tempUppers = med;
				}
			}		
	
		}
		
		if(ifReturn) return med;
		
		return dichotomy(tempLowers, tempUppers);
		
	}
	
	private double calculateLambda()
	{
		
		double lower = 0;
		double upper = 100;
		
		return this.dichotomy(lower, upper);		
		
	}
	
	public double[] calculateMus( double minMu )
	{
		double[] currentmus = new double[this.N];
		
		//point C
		double Lambda = this.calculateLambda();
		

		
		for(int i = 0 ; i < this.N ; i++)
		{
			if(this.currentLambda >= Lambda)
			{
				currentmus[i] = this.maxMus.get(i);
			}
			else
			{
				currentmus[i] = minMu + ( this.maxMus.get(i) - minMu ) / Lambda * this.currentLambda ;
			}
		}
		
		return currentmus;
	}
	
	public double[] calculateProcessingTime(double maxTime)
	{
		double[] currentTimes = new double[this.N];
		
		//point C
		double Lambda = this.calculateLambda();
		
		//debug
		this.LambdaC = Lambda;
		
		for(int i = 0; i < this.N ; i++)
		{
			if(this.currentLambda >= Lambda)
			{
				currentTimes[i] = 1/this.maxMus.get(i);
			}
			else
			{
				currentTimes[i] = maxTime - (maxTime - 1/this.maxMus.get(i)) / Lambda * this.currentLambda;
			}
		}
		
		return currentTimes;
	}
	
	
}