package graph;

public class Edge {
	
	private int fromIndex;
	private int toIndex;
	
	public Edge(int fromIndex, int toIndex){
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
	}
	
	public int getToIndex() {
		return toIndex;
	}
	public int getFromIndex() {
		return fromIndex;
	}
}
