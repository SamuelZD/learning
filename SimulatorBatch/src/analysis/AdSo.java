/**
 * 
 * @author samuel
 *
 */

package analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.*;

import scenario.Configuration;


public class AdSo {
 //total system include all the queues
	
	/* 
	 * arrange the mus
	 * arrange the probas and compute the probas
	 * also the probas is the problem of convergence
	 */
	
	private int nbQ;
	private int capacity; //capacity for every queue
	private double[] lambda;
	private double[] mus;
	private double[] probas;
	private double[] backup; // back up of probas
	private Queue[] queues; //the queues
	
	public AdSo(int nbq, int cap, double[] lambda, double[] mus)
	{
		this.nbQ = nbq ;
		this.capacity = cap ;
		this.lambda = new double[this.nbQ];
		this.lambda = lambda;
		this.mus = new double[this.nbQ];
		this.mus = mus;
		this.probas = new double[this.nbQ]; //attention probas is bigger than probas in queue
		this.backup = new double[this.nbQ];
		this.queues = new Queue[this.nbQ];
	
		//initialize the proba no empty and the different value with backup
		for(int i = 0; i < this.nbQ ; i++)
		{	
			this.probas[i] = 0.5;
			this.backup[i] = 0 ;
		
		}
		
		//initialize the queues
		for(int i = 0 ; i < this.nbQ ; i++)
		{
			queues[i] = new Queue(this.nbQ, this.capacity, this.lambda[i]);
		}
	}
	
	//TODO : Compute
	public void compute()
	{
		
		int nbIter = 1;
		while( this.checkCriteria(0.000001) && nbIter < 1000 )
				//nbIter != 1000)
		{
			//first : backup new proba
			for(int i = 0; i < this.nbQ ; i++)
			{
				this.backup[i] = this.probas[i];
			}
			
			System.out.println( "no of iteration " + nbIter++ +  " ///////////////////" );
			
			for(int i = 0; i < this.nbQ ; i++)
			{
				//arrange Mu and probas
				double tmpMus[] = new double[this.nbQ];
				double tmpProbs[]= new double[this.nbQ -1];
				tmpMus = this.arrangeMus(i + 1);
				tmpProbs = this.arrangeProbas(i + 1);
				
				//compute
				this.queues[i].setMus(tmpMus);
				this.queues[i].setProbas(tmpProbs);
				this.queues[i].compute();
				this.probas[i] = 1 - this.queues[i].getProbaEmpty();
				
				
				System.out.println("no of queue " + i + "...................");
				System.out.println(this.probas[i]);
				System.out.println(this.backup[i]);
				System.out.println("loss probablity : " + this.queues[i].getProbaBlock());
				System.out.println("empty proba   : " +  this.queues[i].getProbaEmpty());
				System.out.println("average Size : " + this.queues[i].getAverageSize());
				
			}
				
			
			
		}
		
		
		
	}
	
	//i i+1 .. nbQueue 1 2 ..
	//TODO: wrong
	private double[] arrangeMus(int no)
	{
		double[] tmp = new double[this.nbQ];
		
		//copy as the new order
		for( int i = 0 ; i < this.nbQ ; i++ )
		{
			tmp[i] = this.mus[ ( no-1 + i ) % this.nbQ ];
		}
		
		return tmp;
	}
	
	//i+1 i+2 ... 1 2 ..  i-1 bouclage  no start from 1
	
	//wrong
	private double[] arrangeProbas(int no_)
	{
		int no = no_;
		
		double[] tmp = new double[ this.nbQ - 1 ];
		
		//copy as the new order
		for( int i = 0 ; i < this.nbQ - 1 ; i++ )
		{
			tmp[i] = this.probas[ ( no++ ) % this.nbQ ];
		}
		
		return tmp;
	}
	
	/**
	 * if there is one difference of one's proba is bigger than standard
	 * It return true ; ohter false
	 * @param standard
	 * @return
	 */
	private boolean checkCriteria(double standard)
	{
		for(int i = 0 ; i < this.nbQ ; i++)
		{
			if( Math.abs(this.probas[i] - this.backup[i]) > standard  )
				return true;
		}
		return false;
	}
	
	//Average_1 block_1 Average_2 block_2
	public void traceResults(String file)
	{
		String outputString = "";
		for(int i = 0 ; i < this.nbQ ; i++)
		{
			outputString = outputString + " " + this.queues[i].getAverageSize() + " " + this.queues[i].getProbaBlock();
		}
		
		
		String fileAddr = System.getProperty("user.dir") + "/traces/"+ file;
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
	 * 
	 * @author samuel
	 * every queue , it work for the convergence and compute the states probabilities
	 */
	private class Queue
	//single queue
	/**
	 * mus and probas should be assigned as new order
	 */
	{
		
		private double standard = 0.0000000001; //for convergence 
		
		private double[][] store;
		private double[][] backup;
		private int nbQ;
		private int capacity;
		private int nbColone;
		private int nbLine;
		private double lambda;
		private double[] mus;  //other order i i+1 .. nbQueue 1 2 ..
		private double eps;
		private double[] probas; // i+1 i+2 ... 1 2 ..  i-1 bouclage
		

		
		public Queue( int nbQ , int capacity, double lambda)
		{
			this.nbQ = nbQ;
			
			this.capacity = capacity;
			
			this.lambda = lambda;
			
			this.mus = new double [ this.nbQ ];
			
			//this.eps = 5;
			this.eps = Configuration.SWITCHRATE;
			//this.mus = mus;
			
			this.probas = new double[ this.nbQ - 1 ];
			
			//this.probas = probas;
			
			//create the arraylist
			this.nbColone = this.capacity + 1 ;
			this.nbLine = 2*this.nbQ ;
			store = new double[ this.nbLine ][ this.nbColone ];
			backup = new double[ this.nbLine ][ this.nbColone ];
			
			
			//initialize store
			for( int m = 0 ; m < this.nbLine ; m++ )
			{
				for( int n = 0 ; n < this.nbColone ; n++ )
				{
					this.store[m][n] = 1 ;
					this.backup[m][n] = 0 ;
				}
			}
			this.store[0][0] = 0;
			this.backup[0][0] = 0;
			
			
			
		}
		
		public void setMus(double[] mus_)
		{
			for(int i = 0 ; i < this.nbQ ; i++)
			{
				this.mus[i] = mus_[i];
			}
			
		}
		
		public void setProbas(double[] probas_)
		{
			for(int i = 0; i < this.nbQ - 1 ; i++)
			{
				this.probas[i] = probas_[i];
			}
		}
		
		//recycle for convergence for a queue
		//TODO:COMPUTE
		public void compute()
		{
			
			//recycle until convergence
			int nbIterQ = 1;
			while( this.checkCriteria(0.000001) && nbIterQ < 1000000)
			{
				
				//System.out.println("no iteration in queue :" +  nbIterQ++ +"........" );
				//first phase: backup store
				for(int m = 0 ; m < this.nbLine ; m ++ )
				{
					for(int n = 0 ; n < this.nbColone ; n++)
					{
						this.backup[m][n] = this.store[m][n] ;
					}
				}
				
				//calculate proba of every state
				
				//the two lines queue own
				this.zeroLine();
				this.oneLine();
				
				//the others lines
				for(int seq = 0 ; seq < this.nbQ - 1 ; seq ++)
				{
					this.twoLines(seq);
				}
				
				this.normalize();   
				
			}
			
			//initialize backup
			for(int m = 0 ; m < this.nbLine ; m ++)
			{
				for(int n = 0 ; n < this.nbColone ; n++)
				{
					this.backup[m][n] = 0;
				}
			}
			
			/*
			this.printProba();
			System.out.println("Backup......");
			this.printBProba();
			*/
		}
		
		//get the proba of the queue is empty, for the lists of proba
		public double getProbaEmpty()
		{
			//////////papier
			/*
			double sum = 0;
			for(int i = 1 ; i < this.nbLine ; i++)
			{
				sum += this.store[i][0];
			}
			
			return sum;
			*/
			
			
			/////////Bruno
			
			double sum = 0;
			for(int i = 0 ; i < this.nbColone ; i++)
				sum += this.store[this.nbLine - 1][i] ;
			
			return this.store[this.nbLine - 1][0] / sum;
			
		}
		
		//get the proba of the blocking ( losses )
		public double getProbaBlock()
		{
			double sum = 0;
			for(int i = 0 ; i < this.nbLine ; i++)
			{
				sum += this.store[i][this.nbColone - 1];
			}
			
			return sum;
		}
		
		//get the average size of the queue
		public double getAverageSize()
		{
			double sum = 0;
			for(int m = 1 ; m < this.nbColone ; m++)
			{
				for(int n = 0; n < this.nbLine ; n++)
				{
					sum += this.store[n][m]*m;
				}
			}
			
			return sum;
		}
		
		
		//method for the zero line
		//attention : we need new ordre de mus and the first element don't use
		private void zeroLine()
		{
			//the first one
			this.store[0][1] = this.store[this.nbLine - 1][1]*eps / (this.mus[0] + this.lambda) ;
			
			//the other one
			for( int i = 2 ; i < this.nbColone -1 ; i++)
			{
				this.store[0][i] = (this.store[0][i - 1]*this.lambda + this.store[this.nbLine - 1][i]*eps ) / (this.mus[0] + this.lambda) ; 
				this.limite(0, i);
			}
			
			//the last one
			this.store[0][this.nbColone - 1] = ( this.store[0][this.nbColone - 2]*this.lambda + this.store[this.nbLine - 1][this.nbColone - 1]*eps )
					/ this.mus[0];
		}
		
		//method for the one line : in this line, the first is different
		private void oneLine()
		{
			//the first element
			this.store[1][0] = ( this.store[this.nbLine - 1][0]*this.eps + this.store[0][1]*this.mus[0] ) 
					/ ( this.lambda + this.eps );
			this.limite(1, 0);
			
			//the other element
			for( int i = 1 ; i < this.nbColone - 1 ; i++ )
			{
				this.store[1][i] = ( this.store[0][i+1]*this.mus[0] + this.store[1][i-1]*this.lambda )
						/ ( this.lambda + this.eps );
				this.limite(1, i);
			}
			
			//the last element
			this.store[1][this.nbColone - 1] = this.store[1][this.nbColone - 2]*this.lambda / this.eps;
			this.limite(1, this.nbColone - 1);
			
		}
		
		//method for other pair : lines with other queue
		//seq from 0
		private void twoLines(int seq_)
		{
			int seq = 0;
			seq = 2*seq_ ;
			
			////////////odd line the first
			
			//the first element			
			this.store[seq + 2][0] = this.store[seq + 1][0]*this.probas[seq/2]*this.eps / 
					( this.lambda + this.mus[seq/2 + 1] );
			this.limite(seq +2, 0);
			
			//other elements
			for(int i = 1 ; i < this.nbColone - 1 ; i ++)
			{
				this.store[seq + 2][i] = ( this.store[seq + 1][i]*this.probas[seq/2]*this.eps + this.store[seq + 2][i-1]*this.lambda ) /
						(this.lambda + this.mus[seq/2+1]);
				
				this.limite(seq + 2, i);
			}
			
			//the last element
			this.store[seq + 2][this.nbColone - 1] =  ( this.store[seq + 1][this.nbColone - 1]*this.probas[seq/2]*this.eps + this.store[seq + 2][this.nbColone - 2]*this.lambda ) /
					this.mus[seq/2+1];
			this.limite(seq + 2 ,this.nbColone - 1);
			
			
			//even line the second
			//the first element
			this.store[seq + 3][0] = ( this.store[seq + 1][0]*(1-this.probas[seq/2])*this.eps + this.store[seq + 2][0]*this.mus[seq/2 + 1] ) /
					(this.lambda + this.eps);
			this.limite(seq + 3, 0);
			
			//the other elements
			for(int i = 1 ; i < this.nbColone - 1 ; i++)
			{
				this.store[seq + 3][i] = ( this.store[seq+3][i-1]*this.lambda + this.store[seq + 1][i]*(1-this.probas[seq/2])*this.eps + this.store[seq + 2][i]*this.mus[seq/2+1] )
						/ (this.lambda + this.eps);
				
				this.limite(seq + 3, i);
			}
			
			//the last element
			this.store[seq + 3][this.nbColone - 1] = ( this.store[seq+3][this.nbColone - 2]*this.lambda + this.store[seq + 1][this.nbColone - 1]*(1-this.probas[seq/2])*this.eps + this.store[seq + 2][this.nbColone - 1]*this.mus[seq/2+1] )
					/ this.eps;
			
			this.limite(seq + 3, this.nbColone - 1);
			
		}
		
		/*
		 * Check the convergence criteria
		 */
		private boolean checkCriteria(double stand)
		{
			for(int m = 0 ; m < this.nbColone ; m++)
			{
				for(int n = 0 ; n < this.nbLine ; n++ )
				{
					if( Math.abs(this.store[n][m] - this.backup[n][m] ) / this.store[n][m] > stand )
						return true;
				}
			}
						
			return false;
			
		}
		
		/*
		 * normalize
		 */
		private void normalize()
		{
			double sum = 0;
			
			for(int m = 0 ; m < this.nbLine ; m++)
			{
				for(int n = 0 ; n < this.nbColone ; n++ )
				{
					if( m == 0 && n == 0) continue;
					
					sum += this.store[m][n];
				}
			}
			
			for(int m = 0 ; m < this.nbLine ; m++)
			{
				for(int n = 0; n < this.nbColone ; n++)
				{
					this.store[m][n] = this.store[m][n] / sum ;
				}
			}
		}
		
		/**
		 * set the limite of the proba
		 */
		private void limite( int m , int n )
		{
			
			if(this.store[m][n] > 1)
			{
				this.store[m][n] = ( this.backup[m][n] + 1 ) / 2;
			}
			if(this.store[m][n] < 0)
			{
				this.store[m][n] = this.backup[m][n] / 2;
			}
			
			
		}
		
		
		/**
		 * print those probablistes
		 */
		
		private void printProba()
		{
			for(int m = 0 ; m < this.nbLine ; m++)
			{
				for(int n = 0; n < this.nbColone ; n ++)
				{
					System.out.print(this.store[m][n]+ " ");
				}
				System.out.println(" ");
			}
		}
		
		private void printBProba()
		{
			for(int m = 0 ; m < this.nbLine ; m++)
			{
				for(int n = 0; n < this.nbColone ; n ++)
				{
					System.out.print(this.backup[m][n]+ " ");
				}
				System.out.println(" ");
			}
		}
		
		
		
		
		
	} 
	
	
}
