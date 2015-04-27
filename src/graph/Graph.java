package graph;

public class Graph {
	
	private int adjMatrix[][];
	
	public Graph(int tab[][]){
		this.setAdjMatrix(tab);
	}
	
	public Graph(String path){
		initFromFile(path);
	}

	public int[][] getAdjMatrix() {
		return adjMatrix;
	}

	public void setAdjMatrix(int adjMatrix[][]) {
		this.adjMatrix = adjMatrix;
	}
	
	public void initFromFile(String path){
		
	}
}
