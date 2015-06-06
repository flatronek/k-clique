package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class MainWindow extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6299554134217402886L;

	AdjacencyCell[][] adjMatrix;
	
	JPanel centralPanel;
	JPanel inputPanel;
	JPanel graphPanel;
	
	JSpinner inputSizeSpinner;
	JButton generateInputMatrixButton;
	
	JSpinner cliqueSizeSpinner;
	JButton findCliqueButton;
	
	public static void main(String[] args){
		
		EventQueue.invokeLater(new Runnable(){

			@Override
			public void run() {
				@SuppressWarnings("unused")
				MainWindow mw =  new MainWindow();
			}
			
		});	
	}
	
	public MainWindow(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		drawUi();
		pack();
		setLocationRelativeTo(null);
		setTitle("K-clique finder");
	}

	private void drawUi() {
		JPanel pane = (JPanel) getContentPane();
		GroupLayout gl = new GroupLayout(pane);
		pane.setLayout(gl);
		
		Graph<Integer, String> testGraph;
		
		testGraph = new SparseGraph<Integer, String>();
		testGraph.addVertex((Integer) 1);
		testGraph.addVertex((Integer) 2);
		testGraph.addVertex((Integer) 3);
		testGraph.addVertex((Integer) 4);
		
		testGraph.addEdge("Edge-A", 1, 2); // Note that Java 1.5 auto-boxes primitives
		testGraph.addEdge("Edge-B", 2, 3);
		testGraph.addEdge("Edge-C", 3, 1);
		testGraph.addEdge("Edge-D", 3, 4);
		
		
		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<Integer, String> layout = new FRLayout<Integer, String>(testGraph);
		layout.setSize(new Dimension(300,300)); // sets the initial size of the space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		BasicVisualizationServer<Integer,String> vv =
				new BasicVisualizationServer<Integer,String>(layout);
		vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size

	/*	Transformer<Integer,Paint> vertexPaint = new Transformer<Integer,Paint>() {
			 public Paint transform(Integer i) {
				 return Color.GREEN;
			 }
		}; */
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
			 
		adjMatrix = null;
		
		centralPanel = new JPanel();
		inputPanel = new JPanel();
		graphPanel = new JPanel();
		
		inputSizeSpinner = new JSpinner();
		inputSizeSpinner.setPreferredSize(new Dimension(30, 30));
		
		generateInputMatrixButton = new JButton();
		generateInputMatrixButton.setPreferredSize(new Dimension(100, 28));
		generateInputMatrixButton.setText("Generate");
		
		cliqueSizeSpinner = new JSpinner();
		cliqueSizeSpinner.setPreferredSize(new Dimension(30, 30));
		
		findCliqueButton = new JButton();
		findCliqueButton.setPreferredSize(new Dimension(100, 28));
		findCliqueButton.setText("Find clique!");
		
		centralPanel.add(inputSizeSpinner);
		centralPanel.add(generateInputMatrixButton);
		centralPanel.add(cliqueSizeSpinner);
		centralPanel.add(findCliqueButton);
		
		centralPanel.setBorder(BorderFactory.createTitledBorder(null, "Central Panel", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", 3, 14)));
		inputPanel.setBorder(BorderFactory.createTitledBorder(null, "Input", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", 3, 14)));
		graphPanel.setBorder(BorderFactory.createTitledBorder(null, "Graph", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", 3, 14)));
		
		gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(centralPanel, 800, 800, GroupLayout.PREFERRED_SIZE)
				.addGroup(gl.createSequentialGroup()
						.addComponent(inputPanel, GroupLayout.DEFAULT_SIZE, 400, GroupLayout.PREFERRED_SIZE)
						.addComponent(vv, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				));
		
		gl.setVerticalGroup(gl.createSequentialGroup()
				.addComponent(centralPanel, 80, 80, 80) 
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(inputPanel, 400, 400, GroupLayout.PREFERRED_SIZE)
						.addComponent(vv, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				));
		
		generateInputMatrixButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateInputMatrix((int) inputSizeSpinner.getValue());
			}
		});
		
		findCliqueButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				findClique();
			}
		});
		
	}

	protected void findClique() {
		int size = adjMatrix.length;
		int numericMatrix[][] = new int[size][size];
		
		for(int i = 0; i < size + 1; i++){
			for(int j = 0; j < size + 1; j++){
				if (adjMatrix[i][j].getBackground() == AdjacencyCell.getClickedColor())
					numericMatrix[i][j] = 1;
				else 
					numericMatrix[i][j] = 0;
			}			
		}
	}

	protected void generateInputMatrix(int size) {
		if (size < 2)
			return;
		
		GridLayout layout = new GridLayout(size+1, size+1);
		adjMatrix = new AdjacencyCell[size+1][size+1];
		
		inputPanel.removeAll();
		inputPanel.repaint();
		inputPanel.setLayout(layout);
		
		for(int i = 0; i < size + 1; i++){
			for(int j = 0; j < size + 1; j++){
				if (i == 0)
					adjMatrix[i][j] = new AdjacencyCell(j);
				if (j == 0)
					adjMatrix[i][j] = new AdjacencyCell(i);
				if ((i != 0) && (j != 0)){
					if (adjMatrix[j][i] == null) adjMatrix[j][i] = new AdjacencyCell();
					
					if (adjMatrix[i][j] == null) adjMatrix[i][j] = new AdjacencyCell();
					adjMatrix[i][j].setSymmetric(adjMatrix[j][i]);
				}
				
				inputPanel.add(adjMatrix[i][j]);
			}			
		}

		layout.layoutContainer(inputPanel);
	}
}
