package graph;

import java.util.LinkedList;


public class Vertex implements Comparable<Vertex> {
	
	private int index;
	private int degree;
	
	private LinkedList<Edge> edges;
	
	public Vertex(int index){
		this.setIndex(index);
		edges = new LinkedList<Edge>();
		setDegree();
	}
	
	public void addEdge(Edge e){
		edges.add(e);
		setDegree();
	}
	
	public void setEdges(LinkedList<Edge> edges){
		this.edges = edges;
		setDegree();
	}
	
	public LinkedList<Edge> getEdges(){
		return edges;
	}

	public int getDegree() {
		return degree;
	}

	private void setDegree() {
		this.degree = edges.size();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public void removeEdgeToIndex(int index){
		Edge rem = null;
		for(Edge e: edges){
			if( e.getToIndex() == index )
				rem = e;
		}
		edges.remove(rem);
		
		setDegree();
	}

	@Override
	public int compareTo(Vertex arg0) {
		
		if (this.index > arg0.getIndex())
			return 1;
		if (this.index < arg0.getIndex())
			return -1;
		
		return 0;
	}
	
}
