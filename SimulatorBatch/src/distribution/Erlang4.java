package distribution;

import distribution.Exponential;


//erlang call the class exponential : 4 plus
public class Erlang4 extends Distribution {

	
	private double lambda ;
	
	private Exponential e;
	
	public Erlang4( double lam_ )
	{
		//iher
		this.type = Distribution.Type.ERLANG4;
		
		this.lambda = lam_;
		
		e = new Exponential(4*this.lambda);
		
	}
	
	@Override
	public double nextDouble() {
		// TODO Auto-generated method stub
		
		double sum = 0;
		
		for(int i = 0 ;  i < 4 ; i++ )
		{
			sum+= e.nextDouble();
		}
		
		return sum;

	}

	@Override
	public double getMean() {
		// TODO Auto-generated method stub
		return 1/this.lambda;
	}
	
	public double getLambda()
	{
		return this.lambda;
	}

}
