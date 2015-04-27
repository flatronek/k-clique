package algorithm;

import graph.*;

public class CliqueFinder {
	private Graph graph;
	private Clique clique;
	
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
		
		cf.setGraph(graph);
		result = cf.findClique();
		
		for(Vertex v: result.getVertexes()){
			System.out.print("\nVertex no: " + v.getIndex() + "\n" + 
					" Connected to: ");
			for(Edge e: v.getEdges())
				System.out.print(e.getToIndex() + "; ");		
		}
	}
	
	private CliqueFinder(){
		graph = null;
		clique = null;
	}
	
	public Clique findClique(){
		if( graph == null )
			return null;
		
		initClique();
			
		return clique;
	}
	
	private void initClique() {
		int [][] adjMatrix;
		int verticesNumber;
		
		clique = new Clique();
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
		
	}

	public void setGraph(Graph graph){
		this.graph = graph;
	}
	
	public void setGraph(int tab[][]){
		graph = new Graph(tab);
	}
	
	public void initGraphFromFile(String path){
		graph = new Graph(path);
	}
}
