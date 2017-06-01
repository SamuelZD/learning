package analysis;

public class Enumerator {
	private int M;
	private int N; //number of existing ports 
	private int[] enumerator;

	public Enumerator(int m, int n) {
		this.M = m;
		this.N = n;
		this.enumerator = new int[N + 1];
		this.run();
	}
	
	public double[] getProbas(){
		double[] probas = new double[N];
		
		//sum
		int sum = 0 ;
		for(int nb : this.enumerator){
			sum += nb;
		}
		for(int i = 0; i < N ; i++){
			probas[i] = ((double)this.enumerator[i+1])/((double)sum);
		}
		
		return probas;
	}

	private void run() {
		int[] ports = new int[N];

		int client = 1;

		dfs(client, ports);

		//debug
//		double sum = 0;
//		for (int nb : enumerator) {
//			sum += nb;
//		}
//		System.out.println("sum " + sum);
//
//		for (int nb : enumerator) {
//			System.out.println(nb);
//		}

	}

	// dfs client == 1
	private void dfs(int client, int[] ports) {
		if (client == M) {
			for (int port = 0; port < N; port++) {
				ports[port]++;
				enumerator[computeNbPortNotEmpty(ports)]++;
				ports[port]--;
			}
			return;
		}

		for (int port = 0; port < N; port++) {
			ports[port]++;
			dfs(client + 1, ports);
			ports[port]--;
		}

	}

	private int computeNbPortNotEmpty(int[] ports) {
		int nb = 0;
		for (int p : ports) {
			if (p != 0)
				nb++;
		}
		return nb;

	}

}
