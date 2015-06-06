package controller;

import java.awt.EventQueue;
import java.util.LinkedList;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import algorithm.CliqueFinder;
import graph.*;
import gui.MainWindow;

public class CliqueManager {
	
	private Individual theFittest;
	private LinkedList<Individual> currGeneration;
	private InputGraph inputGraph;
	
	private CliqueFinder cf;
	private MainWindow view;
	
	private Thread searching;
	
	public static void main(String[] args){
		CliqueManager cm = new CliqueManager();
		long start, stop;
	
		cm.generateRandomGraph(50);
	//	cm.setDelay(true, 25);
		
		start = System.nanoTime();
		cm.findClique(30, 7, 100, "roulette");
		stop = System.nanoTime();
		
		//System.out.println("Input: " + cm.getInputGraph().toString());
		System.out.println("Fittest: \n" + cm.getTheFittest().toString());
		
		System.out.println("\nStart: " + start + ". Stop: " + stop + ". Elapsed: " + (stop-start) + "ns.");
	}
	
	public CliqueManager(){
		theFittest = null;
		currGeneration = null;
		searching = null;
		inputGraph = null;
		
		cf = CliqueFinder.getCliqueFinder();
		cf.setManager(this);
		
		//showWindow();
	}
	
	public synchronized void findClique(int populationSize, int cliqueSize, int maxItCount, String selectionMode){
		
		searching = new Thread(new Runnable(){

			@Override
			public void run() {
				cf.findClique(inputGraph, populationSize, cliqueSize, maxItCount, "roulette");
			}
			
		});
		
		searching.start();
		try {
			searching.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cancel(){
		searching.interrupt();
		
		theFittest = null;
		currGeneration = null;
	}
	
	public void showWindow(){
		
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run() {
				view = new MainWindow();			
			}
		});
	}
	
	public void setTheFittest(Individual fittest){
		this.theFittest = fittest;
		
		// view.updateGraph();
		//cf.showIndividual(theFittest, "Fittest from CM: ");
	}
	
	public Graph<Integer, String> getTheFittest(){
		Graph<Integer, String> result;
		LinkedList<Integer> vertices;
		LinkedList<Edge> edges;
		
		result = new SparseGraph<Integer, String>();
		vertices = getTheFittestVertices();
		edges = getTheFittestEdges();
		
		for (Integer i: vertices){
			result.addVertex(i);
		}
		
		for (Edge e: edges){
			result.addEdge("Edge "+e.getFromIndex()+"-to-"+e.getToIndex(), e.getFromIndex(), e.getToIndex());
		}
		
		return result;
	}
	
	private LinkedList<Integer> getTheFittestVertices(){
		LinkedList<Integer> result;
		LinkedList<Vertex> vertices;
		
		vertices = theFittest.getVertices();
		
		result = new LinkedList<Integer>();
		for (Vertex v: vertices)
			result.add(new Integer(v.getIndex()));
		
		return result;
	}
	
	private LinkedList<Edge> getTheFittestEdges(){
		LinkedList<Edge> edges, result;
		LinkedList<Vertex> vertices;
		
		vertices = theFittest.getVertices();
		
		result = new LinkedList<Edge>();
		for (Vertex v: vertices){
			edges = v.getEdges();
			
			for (Edge e: edges){
				if (!hasEdge(result, e))
					result.add(new Edge(e.getFromIndex(), e.getToIndex()));
			}
		}
		
		return result;
	}
	
	public Graph<Integer, String> getInputGraph(){
		Graph<Integer, String> result;
		LinkedList<Integer> vertices;
		LinkedList<Edge> edges;
		
		result = new SparseGraph<Integer, String>();
		vertices = getInputVertices();
		edges = getInputEdges();
		
		for (Integer i: vertices){
			result.addVertex(i);
		}
		
		for (Edge e: edges){
			result.addEdge("Edge "+e.getFromIndex()+"-to-"+e.getToIndex(), e.getFromIndex(), e.getToIndex());
		}
		
		return result;
	}
	
	private LinkedList<Integer> getInputVertices(){
		LinkedList<Integer> result;
		int size;
		
		if (inputGraph == null)
			return null;
		
		size = inputGraph.getAdjMatrix().length;
		result = new LinkedList<Integer>();
		
		for (int i = 0; i < size; i++)
			result.add(new Integer(i));
		
		return result;
	}
	
	private LinkedList<Edge> getInputEdges(){
		LinkedList<Edge> result;
		int[][] adjMatrix;
		int size;
		
		adjMatrix = inputGraph.getAdjMatrix();
		size = adjMatrix.length;
		result = new LinkedList<Edge>();
		
		for (int y = 0; y < size; y++){
			for (int x = (y+1); x < size; x++){
				if (adjMatrix[y][x] == 1)
					result.add(new Edge(x, y));
			}
		}
		
		return result;
	}
	
	private boolean hasEdge(LinkedList<Edge> list, Edge argEdge){
		int fIndex, tIndex;
		
		for (Edge lEdge: list){
			fIndex = lEdge.getFromIndex();
			tIndex = lEdge.getToIndex();
			
			if ((fIndex == argEdge.getFromIndex()) && (tIndex == argEdge.getToIndex()))
				return true;
			
			if ((fIndex == argEdge.getToIndex()) && (tIndex == argEdge.getFromIndex()))
				return true;
		}
		
		return false;
	}
	
	public void initGraphFromFile(String fname){
		inputGraph = new InputGraph(fname);
		
		// view.drawInputGraph();
	}
	
	public void generateRandomGraph(int size){
		inputGraph = new InputGraph(size);
		
		// view.drawInputGraph();
	}
	
	public void setDelay(boolean delay, int dTime){
		cf.setDelay(delay);
		cf.setDelayTime(dTime);
	}
	
	public void setCurrentGeneration(LinkedList<Individual> generation){
		this.currGeneration = generation;
	}
	
	public LinkedList<Individual> getCurrentGeneration(){
		return currGeneration;
	}

	public void setInputGraph(InputGraph inputGraph) {
		this.inputGraph = inputGraph;
	}
}
