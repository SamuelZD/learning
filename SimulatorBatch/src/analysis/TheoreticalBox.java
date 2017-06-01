package analysis;

/**
 * TheoreticalBox class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class TheoreticalBox {
	
	public static void printMM1KResults(double lambda, double mu, int K) { // CAN BE NON OPTIMAL
		double rho = lambda/mu;
		double thBlockingProba = Math.pow(rho, K)*(1-rho)/(1- Math.pow(rho, K+1));
		double thAverageNbCustomers = 0;
		for (int k=0 ; k<=K ; k++) {
			thAverageNbCustomers += k*Math.pow(rho, k)*(1-rho)/(1-Math.pow(rho,K+1));
		}
		
		System.out.println("\nTheoretical results for M/M/1/K: ");
		System.out.println(" - input: lambda = "+lambda+" / mu = "+mu+" / K = "+K);
		System.out.println(" - output: blocking probability: "+thBlockingProba+" / averasamuelge number of cust.: "+thAverageNbCustomers+ " / average response time: "+thAverageNbCustomers/(lambda*(1-thBlockingProba)));
	}
	
	public static double[] printeMM1RsultsGenial(double lambda, double mu, int K)
	{
		double theAverageNbCustomers = 0;
		double theBlockingProba = 0;
		double rho = lambda/mu;
		double sum = 0;
		double pi[] = new double[K+1];
		for(int i = 0 ; i <= K; i++)
		{
			pi[i] = 0;
		}
		
		//calculate pi using pi[0] = 1
		pi[0] = 1;
		sum = pi[0];
		for(int i = 1 ; i <= K; i++)
		{
			pi[i] = pi[i-1]*rho;
			sum += pi[i];
		}
		
		//calculate pi by sum and calculate the average number of client
		for(int i = 0 ; i <= K ; i++)
		{
			pi[i] = pi[i]/sum;
			
			theAverageNbCustomers += pi[i]*i;
		}
		
		theBlockingProba = pi[K];
		
		System.out.println("\nTheoretical results for M/M/1/K Second methode: ");
		System.out.println(" - input: lambda = "+lambda+" / mu = "+mu+" / K = "+K);
		System.out.println(" - output: blocking probability: "+theBlockingProba+" / average number of cust.: "+theAverageNbCustomers+ " / average response time: "+theAverageNbCustomers/(lambda*(1-theBlockingProba)));
		
		double[] QB = { theAverageNbCustomers , theBlockingProba };
		
		return QB ;
				
		
	}
	
	public static double getMM1KProbaO(double lambda, double mu, int K)
	{
		double theAverageNbCustomers = 0;
		double theBlockingProba = 0;
		double rho = lambda/mu;
		double sum = 0;
		double pi[] = new double[K+1];
		for(int i = 0 ; i <= K; i++)
		{
			pi[i] = 0;
		}
		
		//calculate pi using pi[0] = 1
		pi[0] = 1;
		sum = pi[0];
		for(int i = 1 ; i <= K; i++)
		{
			pi[i] = pi[i-1]*rho;
			sum += pi[i];
		}
		
		//calculate pi by sum and calculate the average number of client
		for(int i = 0 ; i <= K ; i++)
		{
			pi[i] = pi[i]/sum;
			
			theAverageNbCustomers += pi[i]*i;
		}
		
		return pi[0];		
		
		
	}

}
