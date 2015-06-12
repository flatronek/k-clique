package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import controller.CliqueManager;
import exceptions.ParametersException;
import exceptions.UninitializedGraphException;
import graph.*;

public class CliqueFinder {
	
	
	private static CliqueFinder cf = null;
	
	private final double MUTATION_PROB = 0.05;
	
	public static final String ROULETTE_SELECTION = "roulette";
	public static final String TOURNAMENT_SELECTION = "tournament";
	
	private boolean hasDelay;
	private int delayTime;
	
	private CliqueManager cm;
	
	public static CliqueFinder getCliqueFinder(){
		if( cf == null )
			cf = new CliqueFinder();
		return cf;
	}
	
	public void setManager(CliqueManager cm){
		this.cm = cm;
	}
	
	private CliqueFinder(){
		this.cm = null;
		this.hasDelay = false;
		this.delayTime = 500;
	}
	
	/**
	 * Searches for a clique in a given graph. Iterates @see{maxItSize} time, generating following populations. 
	 * Searching process comprises parents' selection and crossing-over. Two selection types are available:
	 * roulette and tournament, what can be modyfied by a @see{selectionMode} param. If a child is generated
	 * with a duplicated gene, whole selection and crossing-over process is repeated.
	 * In case a clique is found mid-search, the function returns. 
	 * Otherwise, the last generation is returned, and the fittest subgraph is passed to @see{CliqueManager} class.
	 * Anytime a new individual with the highest fitness is selected it is passed into @see{CliqueManager} class,
	 * so that the search process can be simulated.
	 * Parameters conditions: population size >= 4, clique size >= 1 and less than graph size, iteration counter >= 1.
	 * @param graph input graph represented by an adjacency matrix.
	 * @param populationSize size of each generated population.
	 * @param cliqueSize size of a clique to be found.
	 * @param maxItCount maximum number of iterations in the search process.
	 * @param selectionMode string "roulette" or "tournament", both can be staticly accessed in CliqueFinder class.
	 * @return A list of individual: current generation when the searching ends, either finding a clique or reaching the max iteration count.
	 * @throws UninitializedGraphException if given graph is null.
	 * @throws ParametersException if any parameter condition is not met the exception is thrown.
	 */
	public synchronized LinkedList<Individual> findClique(InputGraph graph,
			int populationSize, int cliqueSize, int maxItCount, String selectionMode) throws UninitializedGraphException, ParametersException {
		
		LinkedList<Individual> currGeneration;
		Individual theFittest, tmp;
		String mode;
		
		if (graph == null)
			throw new UninitializedGraphException();
		
		if ((populationSize < 4) || (cliqueSize < 1) || (cliqueSize > graph.getAdjMatrix().length) ||
				(maxItCount < 1))
			throw new ParametersException();
		
		mode = new String(selectionMode);
		
		currGeneration = findInitialPopulation(graph, populationSize, cliqueSize);
		theFittest = findFittestIndividual(currGeneration);
		
		cm.setTheFittest(theFittest);
		cm.setCurrentGeneration(currGeneration);
		
		for (int i = 0; i < maxItCount; i++){
			currGeneration = nextGeneration(currGeneration, graph, mode);
			cm.setCurrentGeneration(currGeneration);
			
			tmp = findFittestIndividual(currGeneration);
			if (theFittest.getFitness() < tmp.getFitness()){
				theFittest = tmp;

				cm.setTheFittest(theFittest);
			}		
			
			if (theFittest.getFitness() == 1)
				break;
			
			delay();
		}
		
		showIndividual(theFittest, "The fittest");
		
		cm.addSearchResult(theFittest);
		return currGeneration;
	}

	/**
	 * Finds initial population in the search process. Population consists of @see{populationSize} subgraphs that have @see{cliqueSize} size.
	 * Each vertex has exactly the same probability of being selected.
	 * @param graph input graph.
	 * @param populationSize size of population that is to be found.
	 * @param cliqueSize size of each single individual.
	 * @return A list of found individuals.
	 */
	public LinkedList<Individual> findInitialPopulation(InputGraph graph, int populationSize, int cliqueSize){
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
	
	/**
	 * Generates next generation based on @see{currGeneration} using the specified selection mode.
	 * Algorithm uses singlepoint-crossover method with two children.
	 * @param currGeneration population that the parents will be selected from.
	 * @param graph input graph.
	 * @param selectionMode string "roulette" or "tournament", both can be staticly accessed in CliqueFinder class.
	 * @return created generation: a list with individuals.
	 */
	public LinkedList<Individual> nextGeneration(LinkedList<Individual> currGeneration, InputGraph graph, String selectionMode){
		LinkedList<Individual> nextGeneration, parents, offspring;
		int populationSize;
		
		populationSize = currGeneration.size();
		nextGeneration = new LinkedList<Individual>();
		
		while (nextGeneration.size() < populationSize){
			do {
				switch (selectionMode){
					case TOURNAMENT_SELECTION:
						parents = tournamentSelection(currGeneration);
						break;
					case ROULETTE_SELECTION:
						parents = rouletteSelection(currGeneration);
						break;
					default:
						parents = rouletteSelection(currGeneration);
				}
				offspring = singlepointCrossover(parents, graph);
			}
			while (hasDuplicatedGene(offspring));
			
			nextGeneration.addAll(offspring);	
		}
			
		return nextGeneration;
	}
	
	/**
	 * Selects two parents that will be used to in the crossing-over process.
	 * Individual has the higher chance of being selected the higher his' fitness is.
	 * @param population population that the parents are being selected from.
	 * @return a list with two parents.
	 */
	public LinkedList<Individual> rouletteSelection(LinkedList<Individual> population){
		LinkedList<Individual> parents = new LinkedList<Individual>();
		
		int populationSize = population.size();
		boolean found = false;
		
		int fIndex = 0;
		int sIndex = 0;
		
		while (!found){
			fIndex = (int) (populationSize*Math.random());
			double rand = Math.random();
			if (rand <= population.get(fIndex).getFitness())
				break;
		}
		
		while (!found){
			sIndex = (int) (populationSize*Math.random());
			if ((Math.random() <= population.get(sIndex).getFitness()) && (sIndex != fIndex))
				break;
		}
		
		parents.add(population.get(fIndex));
		parents.add(population.get(sIndex));
		
		return parents;
	}
	
	/**
	 * Selects two parents that will be used to in the crossing-over process.
	 * Population is divided into groups of four, and then two individuals with the highest fitness are selected.
	 * @param population population that the parents are being selected from.
	 * @return a list with two parents.
	 */
	public LinkedList<Individual> tournamentSelection(LinkedList<Individual> population){
		LinkedList<Individual> parents = new LinkedList<Individual>();
		LinkedList<Individual> tournamentGroup = new LinkedList<Individual>();
		
		int populationSize = population.size();
		
		for (Integer i: generateUniqueNumbers(4, populationSize)){
			tournamentGroup.add(population.get(i));
		}
		
		Collections.sort(tournamentGroup);
		
		parents.add(tournamentGroup.get(2));
		parents.add(tournamentGroup.get(3));
		
		return parents;
	}
	
	/**
	 * Creates children from @see{parents}. 
	 * Uses singlepoint crossover method with two children.
	 * A random point is selected and then each gene from the first parent up to crossover point are passed to first child,
	 * the rest genes are genes from the second parent starting from crossover point.
	 * If @see{parents} contains more than two individuals only the first and the second are taken into account.
	 * @param parents a list containing two parents.
	 * @param graph input graph, used in the mutation process.
	 * @return a list with two children.
	 */
	public LinkedList<Individual> singlepointCrossover(LinkedList<Individual> parents, InputGraph graph){
		LinkedList<Individual> offspring;
		Individual fChild, sChild;
		
		int i = 0;
		int coPoint = new Random().nextInt(parents.get(0).getVertices().size());
		
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
	
	/**
	 * Mutates given individual.
	 * Each vertex can be mutated separately, replacing him with any of the vertexes in the input graph.
	 * @param ind individual to have genes mutated.
	 * @param graph graph the vertex are being selected from.
	 */
	public void mutate(Individual ind, InputGraph graph){
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
			Collections.sort(vertices);
			
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
	
	private Individual findFittestIndividual(LinkedList<Individual> list){
		LinkedList<Individual> temp;
		
		temp = new LinkedList<Individual>(list);
		Collections.sort(temp);
		
		return temp.getLast();
	}
	
	private void delay() {
		if (hasDelay)
			try {
				Thread.sleep(delayTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	public void showIndividual(Individual ind, String text){
		
		System.out.println("\n\n"+text+" has fitness: " + ind.getFitness());
		for(Vertex v: ind.getVertices()){
			System.out.print("\nVertex no: " + v.getIndex() + "\n" + 
					" Connected to: ");
			for(Edge e: v.getEdges())
				System.out.print(e.getToIndex() + "; ");		
		}
		
		System.out.println("\n");
	}
	
	public void showFitness(LinkedList<Individual> population, String text){
		Collections.sort(population);

		for (Individual ind: population){
			System.out.println("\n\n"+text+" has fitness: " + ind.getFitness() +
					" and vertices: ");
			for (Vertex v: ind.getVertices()){
				System.out.print(v.getIndex()+", ");
			}
		};
	}
	
	/**
	 * Specifies the delay time of the delay.
	 * @param dTime
	 */
	public void setDelayTime(int dTime){
		this.delayTime = dTime;
	}
	
	/**
	 * Defines whether there is a delay between each iteration in the search process.
	 * @param delay true or false.
	 */
	public void setDelay(boolean delay){
		this.hasDelay = delay;
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
