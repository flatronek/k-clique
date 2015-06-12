package graph;

import java.util.LinkedList;

public class Individual implements Comparable<Individual> {
	private LinkedList<Vertex> vertices;
	
	private double fitness;
	
	public Individual(){
		vertices = new LinkedList<Vertex>();
	}
	
	public Individual(InputGraph graph){
		int [][] adjMatrix;
		int verticesNumber;
		
		vertices = new LinkedList<Vertex>();
		
		adjMatrix = graph.getAdjMatrix();
		verticesNumber = adjMatrix.length;
			
		for(int vIndex = 0; vIndex<verticesNumber; vIndex++){
			int tab[] = adjMatrix[vIndex];
			Vertex v = new Vertex(vIndex);
			
			for(int toIndex = 0; toIndex<tab.length; toIndex++){
				if( (tab[toIndex] == 1) && ( vIndex != toIndex ) ){
					v.addEdge(new Edge(vIndex, toIndex));
				}
			}	
			addVertex(v);
		}
		
		fitness = getEdgeCount() / (((vertices.size() - 1)*(vertices.size())) / 2);
	}
	
	public void addVertex(Vertex v){
		vertices.add(v);
	}
	
	public void removeVertexWithIndex(int i){
		Vertex rem = null;
		for(Vertex v: vertices){
			if( v.getIndex() == i )
				rem = v;
			else v.removeEdgeToIndex(i);
		}
		
		vertices.remove(rem);
	}
	
	public void removeVertex(Vertex v){
		for( Vertex vertex: vertices )
			vertex.removeEdgeToIndex(v.getIndex());
		
		vertices.remove(v);			
	}
	
	public LinkedList<Vertex> getVertices(){
		return vertices;
	}
	
	public boolean isClique(){
		for( Vertex v: vertices ){
			if( v.getDegree() != (vertices.size() - 1) )
				return false;
		}
		
		return true;
	}
	
	public int getEdgeCount(){
		int result = 0;
		
		for (Vertex v: vertices){
			result += v.getDegree();
		}
		result = result / 2;
		
		return result;
	}
	
	public double getFitness(){
		return fitness;
	}

	/**
	 * Sets a list of vertices that each vertex in the individual is connected to.
	 * Ignores the vertices that are not a part of this individual.
	 * @param graph input graph.
	 */
	public void updateEdges(InputGraph graph) {
		int adjMatrix[][] = graph.getAdjMatrix();
		LinkedList<Edge> edges;
		
		for(Vertex v: vertices){
			int fromIndex = v.getIndex();
			edges = new LinkedList<Edge>();
			
			for(Vertex n: vertices){
				int toIndex = n.getIndex();		
				
				if (adjMatrix[fromIndex][toIndex] == 1)
					edges.add(new Edge(fromIndex, toIndex));
			}
			
			v.setEdges(edges);
		}
		double indEdges = getEdgeCount();
		double cliqueEdges = (((vertices.size() - 1)*(vertices.size())) / 2);

		fitness = indEdges / cliqueEdges;
	}
	
	/**
	 * Parses information about this individual to String.
	 * Contains information about fitness and vertices included.
	 */
	@Override
	public String toString() {
		String result;
		result = new String("Individual has fitness: " + fitness + "\n" + 
				"Vertices: ");
		
		for (Vertex v: vertices){
			result = result.concat(v.getIndex() + ", ");
		}
		
		result = result.concat(new String("\n"));
		return result;
	}

	@Override
	public int compareTo(Individual arg0) {
		
		if (this.fitness > arg0.fitness)
			return 1;
		if (this.fitness < arg0.fitness)
			return -1;
		
		return 0;
	}
}
