package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.commons.collections15.Factory;

import controller.CliqueManager;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class GraphEditor extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1097220164362420049L;

	CliqueManager cm;
	// factory sa wykorzystywane przez junga przy rysowaniu wlasnego grafu
	// sluza do nazywania tworzonych wierzcholkow/krawedzi
	Graph<Integer, String> graph;
	Factory<Integer> vertexFactory;
	Factory<String> edgeFactory;
	
	int nodeCount;
	int edgeCount;
	
	JMenuBar menuBar;
	JMenu modeMenu;
	JMenu graphMenu;
	
	public GraphEditor(CliqueManager cm) {
		this.cm = cm;
		this.nodeCount = 0;
		this.edgeCount = 0;
		
		vertexFactory = new Factory<Integer>(){
			@Override
			public Integer create() {
				int result = nodeCount;
				nodeCount++;
				return result;
			}
		};
		
		edgeFactory = new Factory<String>(){
			@Override
			public String create() {
				int result = edgeCount;
				edgeCount++;
				return new String("E" + result);
			}
		};
		
		graph = new SparseMultigraph<Integer, String>();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		ui();
		pack();
		setLocationRelativeTo(null);
		setTitle("Graph editor");
	}
	
	public void ui() {
		// z tutoriala
		// tworzy panel na ktorym mozemy tworzyc dowolny graf
		Layout<Integer, String> layout = new StaticLayout<Integer, String>(graph);
		layout.setSize(new Dimension(500,500));
		VisualizationViewer<Integer,String> vv =
				 new VisualizationViewer<Integer,String>(layout);
		vv.setPreferredSize(new Dimension(500,500));
		 // Show vertex and edge labels
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<String>());
		 // Create a graph mouse and add it to the visualization viewer
		 //
		EditingModalGraphMouse<Integer, String> gm =
				 new EditingModalGraphMouse<Integer, String>(vv.getRenderContext(), vertexFactory, edgeFactory);
		vv.setGraphMouse(gm);

		this.getContentPane().add(vv);

		 // Let's add a menu for changing mouse modes
		menuBar = new JMenuBar();
		graphMenu = new JMenu("Graph");
		// przycisk do konczenia edycji
		JMenuItem save = new JMenuItem("Save");
		save.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveAction();
			}
		});
		graphMenu.add(save);
		menuBar.add(graphMenu);
		// dodawanie do menu listy opcji generowanej przez junga
		// transformacji grafu, dodawania i przesuwania pojedynczych wierzcholkow
		modeMenu = gm.getModeMenu(); // Obtain mode menu from the mouse
		modeMenu.setText("Mouse mode");
		modeMenu.setIcon(null); // I'm using this in a main menu
		modeMenu.setPreferredSize(new Dimension(80,20)); // Change the size 
		menuBar.add(modeMenu);
		
		this.setJMenuBar(menuBar);
		
		gm.setMode(ModalGraphMouse.Mode.EDITING);
	}
	// akcja przycisku w menu lub ctrl+s
	// zamienia graf ktory wprowadzil uzytkownik w jungu
	// na macierz sasiedztwa
	// wywoluje w modelu funkcje inicjalizujaca graf
	// zamyka okno
	protected void saveAction() {
		Pair<Integer> p;
		int [][] adjMatrix;
		int size;
		
		size = graph.getVertexCount();
		adjMatrix = new int[size][size];
		
		for (String e: graph.getEdges()){
			p = new Pair<Integer>(graph.getEndpoints(e));
			adjMatrix[p.getFirst()][p.getSecond()] = 1;
			adjMatrix[p.getSecond()][p.getFirst()] = 1;
		}
		
		cm.initGraph(adjMatrix);
		dispose();		
	}
}
