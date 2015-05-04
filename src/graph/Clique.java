package graph;

import java.util.LinkedList;

public class Clique {
	private LinkedList<Vertex> vertices;
	
	public Clique(){
		vertices = new LinkedList<Vertex>();
	}
	
	public Clique(Graph graph){
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
			//		System.out.println("Added edge: " + vIndex + " " + toIndex);
				}
			}	
			addVertex(v);
		//	System.out.println("Vertex: " + v.getIndex() + " degree is: " + v.getDegree());
		}
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
	
	public LinkedList<Vertex> getVertexes(){
		return vertices;
	}
	
	public boolean isClique(){
		for( Vertex v: vertices ){
			if( v.getDegree() != (vertices.size() - 1) )
				return false;
		}
		
		return true;
	}
}
