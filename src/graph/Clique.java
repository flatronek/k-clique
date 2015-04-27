package graph;

import java.util.LinkedList;

public class Clique {
	private LinkedList<Vertex> vertexes;
	
	public Clique(){
		vertexes = new LinkedList<Vertex>();
	}
	
	public void addVertex(Vertex v){
		vertexes.add(v);
	}
	
	public void removeVertexWithIndex(int i){
		for(Vertex v: vertexes){
			if( v.getIndex() == i )
				vertexes.remove(v);
		}
	}
	
	public void removeVertex(Vertex v){
		vertexes.remove(v);
	}
	
	public LinkedList<Vertex> getVertexes(){
		return vertexes;
	}
	
}
