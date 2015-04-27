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
	public void setToIndex(int toIndex) {
		this.toIndex = toIndex;
	}
	public int getFromIndex() {
		return fromIndex;
	}
	public void setFromIndex(int fromIndex) {
		this.fromIndex = fromIndex;
	}
	
}
