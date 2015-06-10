package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.collections15.Transformer;

import controller.CliqueManager;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class MainWindow extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6299554134217402886L;
	public static final String ROULETTE_SELECTION = "Roulette";
	public static final String TOURNAMENT_SELECTION = "Tournament";

	AdjacencyCell[][] adjMatrix;
	
	CliqueManager cm;
	
	Graph<Integer, String> graph;
	Layout<Integer, String> graphLayout;
	BasicVisualizationServer<Integer,String> graphVisualisation;
	
	GenerateWindow generateWindow;
	GraphEditor graphEditor;
	
	JMenuBar menuBar;
	
	JMenu fileMenu;
	JMenuItem openFile;
	JMenuItem generateMatrix;
	JMenuItem drawGraph;

	JMenu layoutMenu;
	JRadioButtonMenuItem circleLayoutMenuItem;
	JRadioButtonMenuItem frLayoutMenuItem;
	ButtonGroup layoutMenuGroup;
	
	GroupLayout optionsLayout;
	
	JPanel optionsPanel;
	JPanel infoPanel;
	JPanel graphPanel;
	
	DefaultListModel<String> listModel;
	JList<String> searchesList;
	JScrollPane listScroller;
	JTextArea searchInfo;
	
	JLabel selectionTypeLb;
	JComboBox<String> selectionType;
	
	JLabel populationSizeLb;
	JSpinner populationSize;
	
	JLabel itCountLb;
	JSpinner itCount;
	
	JLabel cliqueSizeLb;
	JSpinner cliqueSize;
	
	JCheckBox delay;
	JSpinner delayTimeSpinner;
	JSlider delayTime;
	
	JSpinner inputSizeSpinner;
	JButton generateInputMatrixButton;
	
	JButton findCliqueButton;
	JButton stopButton;
	JButton resetButton;
	
	public MainWindow(CliqueManager cm){
		this.cm = cm;
		
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
		
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu("Graph");
		fileMenu.setMnemonic(KeyEvent.VK_G);
		menuBar.add(fileMenu);
		
		openFile = new JMenuItem("Open file");
		openFile.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openFile.getAccessibleContext().setAccessibleDescription(
				"Opens a text file with adjacency matrix.");
		fileMenu.add(openFile);
		
		generateMatrix = new JMenuItem("Generate matrix");
		generateMatrix.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		generateMatrix.getAccessibleContext().setAccessibleDescription(
				"Randomly generates a matrix with given size.");
		fileMenu.add(generateMatrix);
		
		drawGraph = new JMenuItem("Draw graph");
		drawGraph.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		drawGraph.getAccessibleContext().setAccessibleDescription(
				"Manually draw a graph.");
		fileMenu.add(drawGraph);
		
		layoutMenu = new JMenu("Layout");
		layoutMenu.setMnemonic(KeyEvent.VK_L);
		menuBar.add(layoutMenu);
		
		layoutMenuGroup = new ButtonGroup();
		circleLayoutMenuItem = new JRadioButtonMenuItem("Circle layout");
		circleLayoutMenuItem.setSelected(true);
		circleLayoutMenuItem.addChangeListener(new ChangeListener() {		
			@Override
			public void stateChanged(ChangeEvent e) {
				if (circleLayoutMenuItem.isSelected())
					updateGraphLayout();
			}
		});
		layoutMenuGroup.add(circleLayoutMenuItem);
		layoutMenu.add(circleLayoutMenuItem);

		frLayoutMenuItem = new JRadioButtonMenuItem("FR layout");
		frLayoutMenuItem.addChangeListener(new ChangeListener() {	
			@Override
			public void stateChanged(ChangeEvent e) {
				updateGraphLayout();
			}
		});
		layoutMenuGroup.add(frLayoutMenuItem);
		layoutMenu.add(frLayoutMenuItem);
		
		this.setJMenuBar(menuBar);
		
		inputSizeSpinner = new JSpinner();
		inputSizeSpinner.setPreferredSize(new Dimension(30, 30));
		
		generateInputMatrixButton = new JButton();
		generateInputMatrixButton.setPreferredSize(new Dimension(100, 28));
		generateInputMatrixButton.setText("Generate");

		findCliqueButton = new JButton();
		findCliqueButton.setPreferredSize(new Dimension(100, 28));
		findCliqueButton.setText("Find clique!");
		
		stopButton = new JButton("Stop");
		resetButton = new JButton("Reset");
		
		selectionTypeLb = new JLabel("Selection type");
		selectionType = new JComboBox<>();
		selectionType.addItem(ROULETTE_SELECTION);
		selectionType.addItem(TOURNAMENT_SELECTION);
		
		populationSizeLb = new JLabel("Population size");
		populationSize = new JSpinner();
		populationSize.setPreferredSize(new Dimension(50, 30));
		populationSize.setValue(20);
		
		itCountLb = new JLabel("Iteration count");
		itCount = new JSpinner();
		itCount.setPreferredSize(new Dimension(50, 30));
		itCount.setValue(20);
		
		cliqueSizeLb = new JLabel("Clique size");
		cliqueSize = new JSpinner();
		cliqueSize.setPreferredSize(new Dimension(50, 30));
		
		delay = new JCheckBox("Delay");
		delay.setSelected(true);
		delay.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (delay.isSelected()){
					delayTimeSpinner.setEnabled(true);
					delayTime.setEnabled(true);
				}
				else {
					delayTimeSpinner.setEnabled(false);
					delayTime.setEnabled(false);
				}
			}
		});
		
		delayTimeSpinner = new JSpinner();
		delayTimeSpinner.setValue((int)250);
		delayTimeSpinner.setPreferredSize(new Dimension(50, 30));
		delayTimeSpinner.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				delayTime.setValue((int) delayTimeSpinner.getValue());		
			}
		});
		
		delayTime = new JSlider(JSlider.HORIZONTAL, 0, 500, 250);
		delayTime.setMajorTickSpacing(250);
		delayTime.setMinorTickSpacing(50);
		delayTime.setPaintTicks(true);
		delayTime.setPaintLabels(true);
		delayTime.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				delayTimeSpinner.setValue((int) delayTime.getValue());		
			}
		});
		
		optionsPanel = new JPanel();
		infoPanel = new JPanel();
		graphPanel = new JPanel();
		
		optionsLayout = new GroupLayout(optionsPanel);
		optionsLayout.setAutoCreateContainerGaps(true);
		optionsLayout.setAutoCreateGaps(true);
		
		optionsPanel.add(selectionTypeLb);
		optionsPanel.add(selectionType);
		optionsPanel.setPreferredSize(new Dimension(200,600));
		optionsPanel.setLayout(optionsLayout);		
		createOptionsLayout();
	
		listModel = new DefaultListModel<String>();
		searchesList = new JList<String>(listModel); //data has type Object[]
		searchesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		searchesList.setLayoutOrientation(JList.VERTICAL);
		searchesList.setVisibleRowCount(-1);
		searchesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if ((e.getValueIsAdjusting() == false) && (!searchesList.isSelectionEmpty()))
					cm.getSearchInfo(searchesList.getSelectedValue());
			}
		});

		listScroller = new JScrollPane(searchesList);
		listScroller.setPreferredSize(new Dimension(250, 200));
		
		searchInfo = new JTextArea();
		searchInfo.setEditable(false);
		searchInfo.setPreferredSize(new Dimension(250, 150));
		
		infoPanel.add(listScroller);
		infoPanel.add(searchInfo);
		infoPanel.setLayout(new GridLayout(3, 1));
		
		graphPanel.setPreferredSize(new Dimension(600,600));
		
		optionsPanel.setBorder(BorderFactory.createTitledBorder(null, "Options", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", 3, 14)));
		infoPanel.setBorder(BorderFactory.createTitledBorder(null, "Searches", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", 3, 14)));
		graphPanel.setBorder(BorderFactory.createTitledBorder(null, "Graph", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", 3, 14)));
		
		gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)				
				.addGroup(gl.createSequentialGroup()
						.addComponent(optionsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(graphPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, 1000)
						.addComponent(infoPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, 1000)
						.addPreferredGap(ComponentPlacement.UNRELATED)
				));
		
		gl.setVerticalGroup(gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(optionsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE) 
						.addComponent(graphPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, 1000)	
						.addComponent(infoPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, 1000)	
				));
		
		generateInputMatrixButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//generateInputMatrix((int) inputSizeSpinner.getValue());
			}
		});
		
		findCliqueButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				findClique();
			}
		});
		
		generateMatrix.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				generateMatrixAction();
			}
		});
		
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetAction();
			}
		});
		
		openFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openFileAction();
			}
		});
		
		drawGraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawGraphAction();
			}
		});
	}
	
	protected void drawGraphAction() {
		graphEditor = new GraphEditor(cm);
	}

	protected void openFileAction() {
		JFileChooser fBrowser = new JFileChooser();
		fBrowser.setDialogTitle("Open");
        fBrowser.showOpenDialog(null);
        
        try{
        	File f = fBrowser.getSelectedFile();
        	String filename = f.getAbsolutePath();
        	cm.initGraphFromFile(filename);
        } catch (NullPointerException e){
        	
        }
		
	}

	protected void resetAction() {
		drawInputGraph();
		
		clearList();
	}

	private void createOptionsLayout(){
		optionsLayout.setHorizontalGroup(optionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)				
				.addGroup(optionsLayout.createSequentialGroup()
						.addGroup(optionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(selectionTypeLb, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(optionsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(selectionType, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				)
				.addGroup(optionsLayout.createSequentialGroup()
						.addGroup(optionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(populationSizeLb, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(optionsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(populationSize, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				)
				.addGroup(optionsLayout.createSequentialGroup()
						.addComponent(itCountLb, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(itCount, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(optionsLayout.createSequentialGroup()
						.addComponent(cliqueSizeLb, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(cliqueSize, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(optionsLayout.createSequentialGroup()
						.addComponent(delay, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(delayTimeSpinner, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(optionsLayout.createSequentialGroup()
						.addComponent(delayTime, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(optionsLayout.createSequentialGroup()
						.addComponent(findCliqueButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(stopButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(resetButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				));
		
		optionsLayout.setVerticalGroup(optionsLayout.createSequentialGroup()
				.addGroup(optionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(optionsLayout.createSequentialGroup()
								.addComponent(selectionTypeLb, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(optionsLayout.createSequentialGroup()
								.addComponent(selectionType, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				)
				.addGroup(optionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(optionsLayout.createSequentialGroup()
								.addComponent(populationSizeLb, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(optionsLayout.createSequentialGroup()
								.addComponent(populationSize, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				)
				.addGroup(optionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(itCountLb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE) 
						.addComponent(itCount, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(optionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(cliqueSizeLb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE) 
						.addComponent(cliqueSize, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(optionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(delay, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE) 
						.addComponent(delayTimeSpinner, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(optionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(delayTime, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(optionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(findCliqueButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(stopButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(resetButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				));
	}
	
	public void drawInputGraph(){
		graphPanel.removeAll();
		
		graph = cm.getInputGraph();
			
		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<Integer, String> layout = createGraphLayout();
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		graphVisualisation = new BasicVisualizationServer<Integer,String>(layout);
		graphVisualisation.setPreferredSize(new Dimension(550,550)); //Sets the viewing area size

		graphVisualisation.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		graphVisualisation.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
		graphPanel.add(graphVisualisation);
		graphPanel.repaint();
		graphPanel.revalidate();
	}
	
	public void updateGraph(){
		LinkedList<Integer> vertices = cm.getTheFittestVertices();

		Transformer<Integer,Paint> vertexPaint = new Transformer<Integer,Paint>() {
			public Paint transform(Integer i) {
				if (vertices.contains(i))
					return Color.GREEN;
				return Color.RED;
			}
		}; 
		
		graphVisualisation.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		graphVisualisation.repaint();
		graphVisualisation.revalidate();
	}
	
	public void updateGraphLayout(){
		if (graph == null)
			return;
					
		graphVisualisation.setGraphLayout(createGraphLayout());
		
		graphPanel.repaint();
		graphPanel.revalidate();
	}
	
	private Layout<Integer, String> createGraphLayout(){
		Layout<Integer, String> layout;
		
		if (circleLayoutMenuItem.isSelected())
			layout = new CircleLayout<Integer, String>(graph);
		else
			layout = new FRLayout<Integer, String>(graph);
		
		layout.setSize(new Dimension(550,550));
		
		return layout;
	}
	
	public void updateSearchesList(String entry) {
		listModel.addElement(entry);
		
		searchesList.repaint();
		searchesList.revalidate();
	}
	
	public void updateSearchInfo(String text){
		searchInfo.setText(text);
		
		searchInfo.repaint();
		searchInfo.revalidate();
	}
	
	public void clearList(){
		listModel.removeAllElements();
		searchInfo.setText("");
		
		searchesList.repaint();
		searchesList.revalidate();
	}
	
	private void generateMatrixAction(){
		generateWindow = new GenerateWindow(cm);
	}

	protected void findClique() {		
		cm.setDelay(delay.isSelected(), (int) delayTimeSpinner.getValue()); 
		cm.findClique((int) populationSize.getValue(), (int) cliqueSize.getValue(),
				(int)itCount.getValue(), selectionType.getSelectedItem().toString());
	}

/*	protected void generateInputMatrix(int size) {
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
	}*/
}
