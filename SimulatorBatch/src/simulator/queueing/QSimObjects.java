package simulator.queueing;

import simulator.*;
import simulator.queueing.object.*;
import simulator.queueing.entry.*;

import java.util.LinkedHashMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import scenario.Configuration;

/**
 * QSimObjects class.
 * @author Guillaume Artero Gallardo - LIP/ENSL
 *
 */
public class QSimObjects implements SimObjects {

	// ATTRIBUTES
	Map<Source, SourceEntry> sources;
	Map<Queue, QueueEntry> queues;
	Map<Server, ServerEntry> servers;
		
	// CONSTRUCTOR
	public QSimObjects() {
		this.sources = new LinkedHashMap<Source, SourceEntry>();
		this.queues = new LinkedHashMap<Queue, QueueEntry>();
		this.servers = new LinkedHashMap<Server, ServerEntry>();
	}

	@Override
	public void init(DES simu) {
		// Initialize the sources
		Iterator<Source> sourcesIter = this.sources.keySet().iterator();
		while (sourcesIter.hasNext()) {
			sourcesIter.next().init((QDES) simu);
		}
		
		// Initialize the queues
		Iterator<Queue> queuesIter = this.queues.keySet().iterator();
		while (queuesIter.hasNext()) {
			queuesIter.next().init((QDES) simu);
		}
				
		// Initialize the servers
		Iterator<Server> serversIter = this.servers.keySet().iterator();
		while (serversIter.hasNext()) {
			serversIter.next().init((QDES) simu);
		}
	}

	public void register(Source source) {
		if (!this.sources.containsKey(source)) {
			SourceEntry entry = new SourceEntry(source);
			this.sources.put(source, entry);
			source.addObserver(entry);
		}
	}
	
	public void register(Queue queue) {
		if (!this.queues.containsKey(queue)) {
			QueueEntry entry = new QueueEntry(queue);
			this.queues.put(queue, entry);
			queue.addObserver(entry);
		}
	}
	
	public void register(Server server) {
		if (!this.servers.containsKey(server)) {
			ServerEntry entry = new ServerEntry(server);
			this.servers.put(server, entry);
			server.addObserver(entry);
		}
	}
	
	public SourceEntry lookAt(Source source) {
		if (this.sources.containsKey(source)) {
			return this.sources.get(source);
		} else {
			return null;
		}
	}
	
	public QueueEntry lookAt(Queue queue) {
		if (this.queues.containsKey(queue)) {
			return this.queues.get(queue);
		} else {
			return null;
		}
	}
	
	public ServerEntry lookAt(Server server) {
		if (this.servers.containsKey(server)) {
			return this.servers.get(server);
		} else {
			return null;
		}
	}
	
	public void attach(Source source, Queue queue) { // single attachment
		this.sources.get(source).attach(queue);
	}
	
	public void attach(Queue queue, Server server) { // double attachment
		this.queues.get(queue).attach(server);
		this.servers.get(server).attach(queue);
		queue.addObserver(server); // ADDED FOR NOTIFYING ARRIVALS AT THE SERVERS (RELATIVE TO THE TIMER)
	}
	
	@Override
	public void getResults() {
		
		/* TEST ON keySet() ORDERING => SHOULD USE LinkedHashMap
		System.out.println(this.queues.keySet());
		END OF TEST ON keySet() ORDERING */ 
		
		// Look at results of the sources
		Iterator<Source> sourcesIter = this.sources.keySet().iterator();
		while (sourcesIter.hasNext()) {
			this.sources.get(sourcesIter.next()).getResults();
		}
				
		// Look at results of the queues
		Iterator<Queue> queuesIter = this.queues.keySet().iterator();
		while (queuesIter.hasNext()) {
			this.queues.get(queuesIter.next()).getResults();
		}
		
		// Look at results of the servers
		Iterator<Server> serversIter = this.servers.keySet().iterator();
		while (serversIter.hasNext()) {
			this.servers.get(serversIter.next()).getResults();
		}
				
	}
	
	@Override
	public void traceResults(String traceFile) {
		
		// Trace the performances achieved in the simulation
		// Trace format: Q_1 B_1 .. Q_N B_N
		String outputString = "";
		
		String outputStringDist = "";
		
		double Q;
		double B;
		double S; //standard of queue size
		double T; //throughput
		double M; //mu for each queue
		
		Iterator<Queue> queuesIter = this.queues.keySet().iterator();
		Queue q;
		while (queuesIter.hasNext()) {
			q = queuesIter.next();
			Q = this.lookAt(q).getAverageNbCustomers();
			B = this.lookAt(q).getBlockingProbability();
			S = this.lookAt(q).getSatandardNbCustomers();
			T = this.lookAt(q).getThroughput();
			M = 1/this.lookAt(q).getAverageServiceTime();
			//M = 1/this.lookAt(q).getAverageServiceTimeBatch();
			outputString = outputString +Q+" "+B+" "+S+" "+T+" "+M+" ";
			outputStringDist = outputStringDist+this.lookAt(q).getDistribution(q.getCapacity())+"\n";
		}
		
		//I want to set the first q's average arrive number in the last one
		queuesIter = this.queues.keySet().iterator();
		while(queuesIter.hasNext())
		{
			q = queuesIter.next();
			outputString += this.lookAt(q).getAverageNbClientInstant() + " ";
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
		
		
		// Trace the states distribution achieved in the simulation => Trace format in the QueueEntry class		
		
		fileAddr = System.getProperty("user.dir") + "/traces/"+ traceFile+"Dist";
		
		try
		{
			boolean erase = false; //true;
			FileWriter fw = new FileWriter(fileAddr, !erase);
			BufferedWriter output = new BufferedWriter(fw);
			
			output.write(outputStringDist+"\n");
			
			output.flush(); // Send to the file
			output.close();
				
		}
		catch(IOException ioe){
			System.out.print("Error: ");
			ioe.printStackTrace();
		}
			
						
	}
	
	//create another file for computing the Pi,A
	public void traceProbaInstant(String traceFile)
	{
		Iterator<Queue> iter = this.queues.keySet().iterator();
		
		//just for the 3th q
		int i = 0 ;
		Queue q = new Queue(); 
		while(i < 1)
		{
			q = iter.next();
			i++;
		}
		
		String fileAddr = System.getProperty("user.dir") + "/traces/"+ traceFile+"Proba";
		
		try
		{
			boolean earse = false ;//true will brngs a lot of problems
			FileWriter fw = new FileWriter(fileAddr, !earse);
			BufferedWriter output = new BufferedWriter(fw);
			
			for( int k = 0 ; k <= 128 ; k++)
			{
				String outputstring = k + " " + this.lookAt(q).getProbablityInstante()[k] + "\n";
				output.write(outputstring);
				output.flush();
			}
			output.close();
		}
		catch(IOException ioe)
		{
			System.out.print("Error Instant Probability:");
			ioe.printStackTrace();
		}
	}
	
	/**
	 * get the mu for every queue
	 */
	public Vector<Double> getMus()
	{
		Vector<Double> mus = new Vector<Double>();
		
		Iterator<Queue> queuesIter = this.queues.keySet().iterator();
		
		Queue q;
		
		while(queuesIter.hasNext())
		{
			q = queuesIter.next();
			mus.add(  1/this.lookAt(q).getAverageServiceTime()  );
		}
		
		return mus;
	}
	
	public Vector<Double> getBatchSize()
	{
		Vector<Double> batchSizes = new Vector<Double> ();
		
		Iterator<Queue> queuesIter = this.queues.keySet().iterator();
		
		Queue q;
		while(queuesIter.hasNext())
		{
			q = queuesIter.next();
			batchSizes.addElement(this.lookAt(q).getBatchSize());
		}
			
		
		return batchSizes;
	}
	
	
	/**
	 * for all queues
	 * @return all the probas instant for all k
	 */
	public Vector<double[]> getProbaInstant()
	{
		Vector<double[]> probas = new Vector<double[]>();
		Iterator<Queue> iter = this.queues.keySet().iterator();
		
		Queue q = new Queue();
		while(iter.hasNext())
		{
			q = iter.next();
			probas.add( this.lookAt(q).getProbablityInstante());
		}
		
		return probas;
	}
	
	/**
	 * for all queues
	 * @return all the probas instant for all k
	 */
	public Vector<double[]> getProbaInstantStrange()
	{
		Vector<double[]> probas = new Vector<double[]>();
		Iterator<Queue> iter = this.queues.keySet().iterator();
		
		Queue q = new Queue();
		while(iter.hasNext())
		{
			q = iter.next();
			probas.add( this.lookAt(q).getProbabilityStrange());
		}
		
		return probas;
	}
	/**
	 * get the mu for every queue by method batch
	 */
	
	public Vector<Double> getMusBatch()
	{
		Vector<Double> mus = new Vector<Double>();
		
		Iterator<Queue> queuesIter = this.queues.keySet().iterator();
		
		Queue q;
		
		while(queuesIter.hasNext())
		{
			q = queuesIter.next();
			mus.add(  1/this.lookAt(q).getAverageServiceTimeBatch()  );
		}
		
		return mus;
	}
	
}
