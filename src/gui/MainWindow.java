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
						.addComponent(graphPanel, GroupLayout.DEFAULT_SIZE, 400, GroupLayout.PREFERRED_SIZE)
				));
		
		gl.setVerticalGroup(gl.createSequentialGroup()
				.addComponent(centralPanel, 80, 80, 80) 
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(inputPanel, 400, 400, GroupLayout.PREFERRED_SIZE)
						.addComponent(graphPanel, 400, 400, GroupLayout.PREFERRED_SIZE)
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
