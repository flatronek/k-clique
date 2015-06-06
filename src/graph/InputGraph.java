package graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class InputGraph {
	
	private int adjMatrix[][];
	
	public InputGraph(){
		adjMatrix = null;
	}
	
	public InputGraph(int tab[][]){
		this.setAdjMatrix(tab);
	}
	
	public InputGraph(String path){
		initFromFile(path);
	}
	
	public InputGraph(int size){
		randomizeMatrix(size);
	}

	public int[][] getAdjMatrix() {
		return adjMatrix;
	}

	public void setAdjMatrix(int adjMatrix[][]) {
		this.adjMatrix = adjMatrix;
	}
	
	
	/**
	 * Read matrix from file
	 * @param path
	 */
	public void initFromFile(String path) {
		File file = createFile(path);
		BufferedReader bf = null;

		String line = null;
		int size=0;
		int[][] tab = null;
		   try{
			   bf = new BufferedReader(new FileReader(file));
			   size = bf.readLine().length();
			   System.out.println(size);
			   tab = new int[size][size];
			   bf.close();
			   
			   bf = new BufferedReader(new FileReader(file));
			   for( int i=0; i<size; i++ ){
				   line = new String(bf.readLine());
				   for( int j=0; j<size; j++ ){
					   int read = Integer.parseInt(line.substring(j, j+1));
					   tab[i][j] = read;
				   }
			   }
			   setAdjMatrix(tab);
		   }catch(IOException e) {
		      System.out.print("Exception");
		   }finally {
			   try {
					bf.close();
				//	input.close();
				} catch (IOException e) {
					System.out.println(e.toString());
				}  
		   }
			for( int i=0; i<size; i++ ){
				for( int j=0; j<size; j++ ){
					System.out.print(tab[i][j]);
					System.out.print(" ");
				}
				System.out.println();
			}
	}
	/**
	 * Randomizing a matrix and save it in file
	 * @param path file
	 * @param size size of matrix
	 */
	public void randomMatrixToFile(String path, int size) {
		createFile(path);
		PrintWriter fOut = null;
		Random r = new Random();

		int[][] tab = new int[size][size];
		
		for( int i=0; i<size; i++ )
			for( int j=0; j<size; j++ )
				tab[i][j] = -1; 
		
	  	try{
		  fOut = new PrintWriter(path);
		  for( int i=0; i<size; i++ ){
			  for( int j=0; j<size; j++ ){
				  if( i==j ){
					  tab[i][j] = 0;
					  continue;
				  }
				  if( tab[j][i] != -1 ){
					  tab[i][j] = tab[j][i];
					  continue;
				  }
				  tab[i][j] = r.nextInt(2);
			  }
		  }
	   }catch(IOException e) {
	      System.out.print("Exception");
	   }
	  	
		for( int i=0; i<size; i++ ){
			for( int j=0; j<size; j++ ){
				fOut.print(tab[i][j]);
				fOut.print(" ");
			}
			fOut.println();
		}
		fOut.close();
	}
	
	public void randomizeMatrix(int size){
		Random r = new Random();
		adjMatrix = new int[size][size];
		
		for( int i=0; i<size; i++ )
			for( int j=0; j<size; j++ )
				adjMatrix[i][j] = -1; 
		
		for( int i=0; i<size; i++ ){
			  for( int j=0; j<size; j++ ){
				  if( i==j ){
					  adjMatrix[i][j] = 0;
					  continue;
				  }
				  if( adjMatrix[j][i] != -1 ){
					  adjMatrix[i][j] = adjMatrix[j][i];
					  continue;
				  }
				  adjMatrix[i][j] = r.nextInt(2);
			  }
		}
		
		for( int i=0; i<size; i++ ){
			for( int j=0; j<size; j++ ){
				System.out.print(adjMatrix[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
	}
	
	
	public static File createFile(String path) {
		File file = null;
		file = new File(path);
			if (!file.exists())
				try {
					file.createNewFile();
				} catch (IOException e) {
					System.out.println(e.toString());
				}
		
		return file;
	}
}
