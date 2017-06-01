package analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

/**
 * This class is for the simple approximation
 * for example two queue system polling
 * mu1 = mu*p2(0) + mu/2*(1-p2(0)  )
 * 
 * use the method fix point
 * Vector
 * @author SU Zidong
 *hasNextEvent
 */

public class ApproximationSimple 
{
	//ATTRIBUTS
	private int N ; // Queue number
	private int K;  // Queue Capacity
	private double mu; 
	private double[] lams;
	private double[] Backup_mu; //old mu
	private double[] mus; //new mu
	private double[] Backup_proba; //old probability
	private double[] probas;
	
	
	//CONSTRUCTOR
	public ApproximationSimple(int N_, int K_, double mu_, double[] lams_)
	{
		N = N_;
		K = K_;
		mu = mu_;
		Backup_mu = new double[N];
		mus = new double[N];
		probas = new double[N];
		Backup_proba = new double[N];
		lams = lams_;
	}
	
	//METHOD
	
	
	//backup probabilties et mus
	private void backup()
	{
		for (int i = 0; i < N ; i++)
		{
			Backup_proba[i] = probas[i];
			Backup_mu[i] = mus[i];
		}
	}
	
	//initialize the probabilint e = 0 ; e < set.size() ; e++ities
	public void init()
	{
		//initialize mux
		for( int i = 0 ; i < N ; i++)
		{
			Backup_mu[i] = 0 ;
			mus[i] = mu/N;
			
			//Backup_proba[i] = 0;
			probas[i] = 0.5;
		}
		
		//initialize proba /int e = 0 ; e < set.size() ;
		for(int i = 0 ; i < N ; i++)
		{
			Backup_proba[i] = TheoreticalBox.getMM1KProbaO(lams[i] , Backup_mu[i], K);
		}
	}
	
	public void run(double eps)
	{
		int max_iter  = 20000;
		int nb_iter = 0;
		System.out.println("Beginning Compute mus ...");
		while ( !this.isConvergence(eps) &&	 nb_iter < max_iter )
		{
			computeMus();
			nb_iter ++ ;
			//System.out.println( nb_iter + " iteration fini");
		}
		System.out.println("convergenc reached");
		for(int i = 0 ; i < N ; i++ )
		{
			System.out.println(mus[i] + " " + Backup_mu[i] );
		}
	}
	
	//compute une fois
	public void computeMus()
	{
		//backup
		backup();
		
		
		//for each queue_i
		for(int i = 0 ; i < N ; i++)
		{
			
			
			/**
			 * FOR QUEUE i
			 * now we compute the probabilities :
			 * 1. O is working
			 * 2. 1 to N -1 working
			 */
			double tmpMu_iOne = Backup_mu[i];
			
			//1 : O is working 
			for(int t = 0 ; t < N ; t ++)
			{
				if( t == i ) continue ;
				tmpMu_iOne *= Backup_proba[t];
			}
			
			//2 : each number 1 from 1 to N - 1
			double tmpMu_iTwo = 0; // le deuxime partie de mu
			
			for(int k = 1 ; k <= N-1 ; k++)
			{ 

				
				double tmpSubProba = 0;
				Set<String> set = new HashSet<>();
				
				//creat input k 1s other are 0s
				String input = "";
				for(int m = 0; m < N - 1 ; m++)
				{
					if( m < k )
						input += "1";
					else
						input += "0";
				}
				
				//create the permutation of this condition
				this.permutation(input, "", set);
				
				//parcourir tous les element de set
				
				Iterator<String> it = set.iterator();
				
				while( it.hasNext() )
				{					
					double probaE = 1; //contient la produit proba pour un certain cas
					String e = it.next();
					//System.out.println("element : " + e);
					//le produit pariba de certain element
					for(int p = 0 ; p < N -1 ; p ++)
					{
						if( e.charAt(p) == '1' )
						{
							if( p < i )
								probaE *= 1 - Backup_proba[p];
							else
								probaE *= 1 -Backup_proba[p+1];
						}
						else if( e.charAt(p)== '0')
						{
							if( p < i )
								probaE *= Backup_proba[p];
							else probaE *= Backup_proba[p+1];
						}
					}
					
					tmpSubProba += probaE;
					
				}
				tmpMu_iTwo += tmpSubProba * mu/(k+1);
			}
			
			mus[i] = tmpMu_iOne + tmpMu_iTwo;		
			probas[i] = TheoreticalBox.getMM1KProbaO(lams[i], mus[i], K);
			
			/*
			System.out.println("Now mu " + mus[i]);
			System.out.println("Backup Mu " + Backup_mu[i]);
			System.out.println("Now proba " + probas[i]);
			System.out.println("Backup proba " + Backup_proba[i] );
			*/
			
			
		}
		
	}
	
	
	//test if convergence
	private boolean isConvergence( double criteria )
	{
		boolean result = false; // no convergence
		
		//find the max value
		double diff = 0;
		for(int i = 0 ; i < N ;i++)
		{
			double tmp = Math.abs(Backup_mu[i] - mus[i]);
			if( diff < tmp )
				diff = tmp ;
		}
		
		if( diff < criteria)
			result = true ;
		
		return result;
	}
	
	
	/**
	 * 
	 * @param input
	 * @param sofar
	 * @param set : get the results
	 * 
	 * This method is for getting all the combinations  
	 */
	private void permutation(String input, String sofar, Set<String> set)
	{
		
		if(input.equals(""))
		{
			set.add(sofar);
		}
		
		for(int i = 0 ; i < input.length(); i++)
		{
			char c = input.charAt(i);
			if(input.indexOf(c, i + 1) != -1)
					continue;
			permutation(input.substring(0,i) + input.substring(i + 1), sofar + c , set);
		}
	}
	
	//trace les résultats
	public void traceResults(String traceFile )
	{
		// Trace format: N lambda_1 mu_1 Q_1 B_1 ... lambda_N mu_N Q_N B_N
		String outputString = ""+N+"";
		double Q;
		double B;
		
		for(int i  = 0 ; i < N ; i++)
		{
			double[] QB = TheoreticalBox.printeMM1RsultsGenial(lams[i], mus[i], K);
			Q = QB[0];
			B = QB[1];
			outputString = outputString + " " + lams[i] + " " + mus[i] + " " + Q + " " + B;
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
	
	//
	
	/*
	public static void main(String args[])
	{
		int n = 4 ;
		int k = 5 ;
		double mu = 5 ;
		double[] lams = {0.3 , 0.3 , 0.3, 0.3}; 
		ApproximationSimple as = new ApproximationSimple( n , k , mu , lams );
		as.init();
		as.run(0.000000001);
	}
	*/
	
	

}
