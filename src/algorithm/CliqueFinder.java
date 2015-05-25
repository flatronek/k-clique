package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import graph.*;

public class CliqueFinder {
	
	
	private static CliqueFinder cf = null;
	
	private final double MUTATION_PROB = 0.1;
	
	public static void main(String[] args){
		CliqueFinder cf = CliqueFinder.getCliqueFinder();
		LinkedList<Individual> result;
		
		int[][] matrix = new int[8][8];	
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				if (i != j) matrix[i][j] = 1;
			}
		}
		
		Graph graph = new Graph(matrix);
		
		result = cf.findClique(graph, 4, 4, 20);
		cf.showPopulation2(result, "Result");
		cf.showFitness(result, "Result");
	}
	
	public static CliqueFinder getCliqueFinder(){
		if( cf == null )
			cf = new CliqueFinder();
		return cf;
	}
	
	private CliqueFinder(){
		
	}
	
	public LinkedList<Individual> findClique(Graph graph, int populationSize, int cliqueSize, int maxItCount){
		LinkedList<Individual> currGeneration;
		
		currGeneration = findInitialPopulation(graph, populationSize, cliqueSize);
		for (int i = 0; i < maxItCount; i++){
			showPopulation2(currGeneration, "Generation "+i);
			currGeneration = nextGeneration(currGeneration, graph);
		}
		
		return currGeneration;
	}
	
	public LinkedList<Individual> nextGeneration(LinkedList<Individual> currGeneration, Graph graph){
		LinkedList<Individual> nextGeneration, parents, offspring;
		int populationSize;
		
		populationSize = currGeneration.size();
		nextGeneration = new LinkedList<Individual>();
		
		while (nextGeneration.size() < populationSize){

			do {
				parents = rouletteSelection(currGeneration);
				offspring = singlepointCrossover(parents, graph);
			}
			while (hasDuplicatedGene(offspring));
			
			nextGeneration.addAll(offspring);	
		//	showPopulation(parents, "Parents");
		//	showPopulation(offspring, "Offspring");
		}
			
		return nextGeneration;
	}
	
	public LinkedList<Individual> findInitialPopulation(Graph graph, int populationSize, int cliqueSize){
		LinkedList<Individual> population = new LinkedList<Individual>();
		
		for (int i=0; i<populationSize; i++){
			Individual individual = new Individual();
			ArrayList<Integer> vertInd = generateUniqueNumbers(cliqueSize, graph.getAdjMatrix().length);
			
			for (int j=0; j<cliqueSize; j++){
				individual.addVertex(new Vertex(vertInd.get(j)));
			}
			
			individual.updateEdges(graph);
			population.add(individual);
		}
		
		return population;
	}
	
	public LinkedList<Individual> rouletteSelection(LinkedList<Individual> population){
		LinkedList<Individual> parents = new LinkedList<Individual>();
		
		int populationSize = population.size();
		boolean found = false;
		
		int fIndex = 0;
		int sIndex = 0;
		
		while (!found){
			fIndex = (int) (populationSize*Math.random());
			if (Math.random() < population.get(fIndex).getFitness())
				break;
		}
		
		while (!found){
			sIndex = (int) (populationSize*Math.random());
			if ((Math.random() < population.get(sIndex).getFitness()) && (sIndex != fIndex))
				break;
		}
		
		parents.add(population.get(fIndex));
		parents.add(population.get(sIndex));
		
		return parents;
	}
	
	public LinkedList<Individual> singlepointCrossover(LinkedList<Individual> parents, Graph graph){
		LinkedList<Individual> offspring;
		Individual fChild, sChild;
		
		int i = 0;
		int coPoint = new Random().nextInt(parents.get(0).getVertices().size());
		System.out.println("\nCrossover point: " + coPoint);
		
		offspring = new LinkedList<Individual>();		
		fChild = new Individual();
		sChild = new Individual();
		
		for (Vertex v: parents.get(0).getVertices()){
			if (i < coPoint)
				fChild.addVertex(new Vertex(v.getIndex()));
			else
				sChild.addVertex(new Vertex(v.getIndex()));
			i++;
		}
		
		i = 0;
		
		for (Vertex v: parents.get(1).getVertices()){
			if (i < coPoint)
				sChild.addVertex(new Vertex(v.getIndex()));
			else
				fChild.addVertex(new Vertex(v.getIndex()));		
			i++;
		}
		
		mutate(fChild, graph);
		mutate(sChild, graph);
		fChild.updateEdges(graph);
		sChild.updateEdges(graph);
		offspring.add(fChild);
		offspring.add(sChild);
		
		return offspring;
	}
	
	public void mutate(Individual ind, Graph graph){
		LinkedList<Vertex> vertices = ind.getVertices();
		int [][]adjMatrix = graph.getAdjMatrix();
		
		Vertex v;
		int vIndex;
		
		for (int i = 0; i < vertices.size(); i++){
			v = vertices.get(i);
			if (Math.random() < MUTATION_PROB){
				vIndex = new Random().nextInt(adjMatrix.length);
				vertices.remove(v);
				vertices.add(new Vertex(vIndex));
			}
		}
	}
	
	private boolean hasDuplicatedGene(LinkedList<Individual> indList){
		LinkedList<Vertex> vertices;
		int previous, current;
		
		for (Individual ind: indList){		
			vertices = new LinkedList<Vertex>(ind.getVertices());
			vertices.sort(null);
			
			previous = -1;
			for (Vertex v: vertices){
				current = v.getIndex();
			
				if (previous == current)
					return true;
				previous = current;
			}
		}
		
		return false;
	}
	
	private ArrayList<Integer> generateUniqueNumbers(int quantity, int range){
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for(int i = 0; i<range; i++)
			numbers.add(new Integer(i));
		
		Collections.shuffle(numbers);
		
		for(int i = 0; i<quantity; i++)
			result.add(numbers.get(i));
		
		return result;
	}
	
	public void showPopulation(LinkedList<Individual> population, String text){
		int i = 1;
		
		for(Individual ind: population){
			System.out.println("\n\n"+text+" no: " + i);
			for(Vertex v: ind.getVertices()){
				System.out.print("\nVertex no: " + v.getIndex() + "\n" + 
						" Connected to: ");
				for(Edge e: v.getEdges())
					System.out.print(e.getToIndex() + "; ");		
			}
			
			i++;
		};
	}
	
	public void showPopulation2(LinkedList<Individual> population, String text){

		for (Individual ind: population){
			System.out.println("\n\n"+text+" has vertices: ");
			for (Vertex v: ind.getVertices()){
				System.out.print(v.getIndex()+", ");
			}
		};
	}
	
	public void showFitness(LinkedList<Individual> population, String text){
		population.sort(null);

		for (Individual ind: population){
			System.out.println("\n\n"+text+" has fitness: " + ind.getFitness() +
					" and vertices: ");
			for (Vertex v: ind.getVertices()){
				System.out.print(v.getIndex()+", ");
			}
		};
	}
	
/*	private void initIndividual(Individual Individual, Graph graph) {
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
			Individual.addVertex(v);
		//	System.out.println("Vertex: " + v.getIndex() + " degree is: " + v.getDegree());
		}
		
		public Individual findIndividual(Graph graph){
		if( graph == null )
			return null;
		
		Individual Individual = new Individual(graph);
		
		while( !Individual.isClique() )
			removeRandom(Individual, getSmallestDegreeVertexes(Individual));
			
		return Individual;
	}
	
	
	
	private void removeRandom(Individual Individual,
			LinkedList<Vertex> smallestDegreeVertexes) {

		Random rand;
		Vertex v;
		int index;
		
		rand = new Random();
		index = rand.nextInt(smallestDegreeVertexes.size());
		v = smallestDegreeVertexes.get(index);
		System.out.println("Removed: " + v.getIndex());
		
		Individual.removeVertex(v);
	}

	private LinkedList<Vertex> getSmallestDegreeVertexes(Individual Individual){
		LinkedList<Vertex> result = new LinkedList<Vertex>();
		LinkedList<Vertex> input = Individual.getVertexes();
		
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
		
	} */
}
