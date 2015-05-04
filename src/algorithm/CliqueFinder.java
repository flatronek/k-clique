package algorithm;

import java.util.LinkedList;
import java.util.Random;

import graph.*;

public class CliqueFinder {
	
	
	private static CliqueFinder cf = null;
	
	public static CliqueFinder getCliqueFinder(){
		if( cf == null )
			cf = new CliqueFinder();
		return cf;
	}
	
	public static void main(String[] args){
		CliqueFinder cf = CliqueFinder.getCliqueFinder();
		Clique result;
		
		int[][] graph = new int[5][5];	
		for(int i=0; i<5; i++){
			for(int j=0; j<5; j++){
				graph[i][j] = 1;
			}
		}
		
		result = cf.findClique(new Graph(graph));
		
		for(Vertex v: result.getVertexes()){
			System.out.print("\nVertex no: " + v.getIndex() + "\n" + 
					" Connected to: ");
			for(Edge e: v.getEdges())
				System.out.print(e.getToIndex() + "; ");		
		}
	}
	
	private CliqueFinder(){
		
	}
	
	public Clique findClique(Graph graph){
		if( graph == null )
			return null;
		
		Clique clique = new Clique(graph);
		
		while( !clique.isClique() )
			removeRandom(clique, getSmallestDegreeVertexes(clique));
			
		return clique;
	}
	
	
	
	private void removeRandom(Clique clique,
			LinkedList<Vertex> smallestDegreeVertexes) {

		Random rand;
		Vertex v;
		int index;
		
		rand = new Random();
		index = rand.nextInt(smallestDegreeVertexes.size());
		v = smallestDegreeVertexes.get(index);
		System.out.println("Removed: " + v.getIndex());
		
		clique.removeVertex(v);
	}

	private LinkedList<Vertex> getSmallestDegreeVertexes(Clique clique){
		LinkedList<Vertex> result = new LinkedList<Vertex>();
		LinkedList<Vertex> input = clique.getVertexes();
		
		int theSmallest = 100;
		int secondSmallest = 100;
		
		for(Vertex v: input){
			if( v.getDegree() < theSmallest ){
				theSmallest = v.getDegree();
				continue;
			}
			
			if( v.getDegree() < secondSmallest )
				secondSmallest = v.getDegree();
		}
		
		for(Vertex v: input){
			if( (v.getDegree() == theSmallest) || (v.getDegree() == secondSmallest) )
				result.add(v);
		}
		
		return result;
	}
	
/*	private void initClique(Clique clique, Graph graph) {
		int [][] adjMatrix;
		int verticesNumber;
	
		adjMatrix = graph.getAdjMatrix();
		verticesNumber = adjMatrix.length;
			
		for(int vIndex = 0; vIndex<verticesNumber; vIndex++){
			int tab[] = adjMatrix[vIndex];
			Vertex v = new Vertex(vIndex);
			
			for(int toIndex = 0; toIndex<tab.length; toIndex++){
				if( (tab[toIndex] == 1) && ( vIndex != toIndex ) ){
					v.addEdge(new Edge(vIndex, toIndex));
			//		System.out.println("Added edge: " + vIndex + " " + toIndex);
				}
			}	
			clique.addVertex(v);
		//	System.out.println("Vertex: " + v.getIndex() + " degree is: " + v.getDegree());
		}
		
	} */
}
