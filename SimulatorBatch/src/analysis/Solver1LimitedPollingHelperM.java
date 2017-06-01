package analysis;

import scenario.Configuration;

import java.util.Random;
import java.util.Vector;

/**
 * helper for find the lambda
 * 
 * trace Lambda and find the Lambda optimal.
 * 
 * Inferior borne
 * m -> T -> mu -> X -> m
 * 
 * 
 * Proposed by Bruno, written by SU Zidong
 * is writing
 * 
 */
public class Solver1LimitedPollingHelperM {

	//definition parameters
	private int N; // the number of ports
	private Vector<Double> lambdas;
	private Vector<Integer> K;
	private Vector<Double> old_ms;
	private Vector<Double> ms;
	private double criterion = 0.001;
	private Solver1LimitedPollingE finalsolver;
	
	private double timeM;
	private double timeH;
	private double timeR;
	
	
	public Solver1LimitedPollingHelperM()
	{
		//initialization
		this.old_ms = new Vector<Double>();
		this.ms = new Vector<Double>();
		this.lambdas = new Vector<Double>();
		this.K = new Vector<Integer>();
	}
	
	public void addQueue(int k_, double lambda_)
	{
		N++;
		K.add(k_);
		lambdas.add(lambda_);
		this.ms.add((double) (Configuration.EXTRALOT+1));
		this.old_ms.add(0.0);
	}
	
	public void setTimes(double tm, double th, double tr)
	{
		this.timeM = tm;
		this.timeH = th;
		this.timeR = tr;
	}
	
	public void iterative()
	{
		int iter = 0;
		while(this.chechkCriterion())
		{
			//debug
			System.out.println("****************************the greatest iteration :" + iter + "****************************");
			iter++;
			
			//iteration
			//m -> T
			Vector<Double> timeM = new Vector<Double>();
			for(double m : this.ms)
			{
				timeM.add(this.oneMiss(m));
			}
			
			//T -> mu
			double timeTotal = 0 ;
			for(double t : timeM) timeTotal += t;
			Vector<Double> mus = new Vector<Double>();
			for(int i = 0 ; i < this.N ; i++)
			{
				//backup m
				this.old_ms.set(i, this.ms.get(i)); //just for simple
				//mus.add(this.ms.get(i)/timeM.get(i));
				mus.add(this.ms.get(i)/timeM.get(i));
			}
			
			//run the solver
			//there is the problem: how we can get the throughput from the model
			Vector<Double> throught = this.computeThroughput(mus);
			
			//recompute the m

			
			for(int i = 0 ; i < this.N ; i++)
			{
				double tempM = (throught.get(i)*timeTotal > (Configuration.EXTRALOT + 1))?(Configuration.EXTRALOT + 1) : throught.get(i)*timeTotal;
				this.ms.set(i, tempM);
			}
			
			//debug
			System.out.println("throughput : " + throught);
			System.out.println("time " + timeM);
			System.out.println("m : " + ms);
			System.out.println("old m : " + old_ms);
			
		}
	}
	
	/**
	 * 
	 * @return throughput for computing m (the batch size)
	 */
	public Vector<Double> computeThroughput(Vector<Double> mus)
	{
		//create a solver
		Solver1LimitedPollingE solver = new Solver1LimitedPollingE();		
		for(int i = 0 ; i < this.N ; i++)
		{
			solver.addQueue(this.K.get(i), this.lambdas.get(i), mus.get(i));
		}
		solver.init();
		solver.run(0.00000001,false, 10000);
		
		//computing throughput
		Vector<Double> thr = new Vector<Double>();		
		Vector<Double> bs = solver.getB();
		for(int i = 0 ; i < this.N ; i++)
		{
			thr.add(this.lambdas.get(i)*(1 - bs.get(i)));
		}
		
		//for getting the final results
		this.finalsolver = solver;
		return thr;
		
	}

	
	private double oneMiss(double value)
	{
		double first = (value > 1)?1:value;
		double second = ((value - 1) > 0)?(value-1):0;
		return (first*this.timeM + second*this.timeH + value*this.timeR);
		
//		double time = 0;
//		time = this.timeM + (value-1)*this.timeH + this.timeR;
//		return time;
	}
	
	
	
	private boolean chechkCriterion()
	{
		for (int i = 0 ; i < this.N ; i++)
		{
			if (Math.abs(this.old_ms.get(i) - this.ms.get(i)) > this.criterion) return true;
		}
		
		return false;
	}
	
	public void traceResults(String traceFile)
	{
		this.finalsolver.traceResults(traceFile);
	}
	
}


