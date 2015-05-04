package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class AdjacencyCell extends JPanel implements MouseListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5915937280650786265L;
	private static final Color clickedColor = Color.MAGENTA;
	private static final Color defaultColor = Color.WHITE;
	
	int value;
	
	AdjacencyCell symmetric;
	JLabel label;
	
	public AdjacencyCell(){
		label = null;
		
		setPreferredSize(new Dimension(20,20));
		setBorder(new LineBorder(Color.BLACK));
		setBackground(defaultColor);
		addMouseListener(this);		
	}
	
	public AdjacencyCell(AdjacencyCell symmetric){
		label = null;
		this.symmetric = symmetric;
		
		setPreferredSize(new Dimension(20,20));
		setBorder(new LineBorder(Color.BLACK));
		setBackground(defaultColor);
		addMouseListener(this);		
	}
	
	public AdjacencyCell(int index){
		symmetric = null;
		
		label = new JLabel(String.valueOf(index));
		label.setFont(new Font("Verdana", Font.PLAIN, 16));
		
		setVisible(true);
		setPreferredSize(new Dimension(20,20));
		this.add(label);
		repaint();
	//	System.out.println(label.getText());
	}
	
	public void setSymmetric(AdjacencyCell symmetric){
		this.symmetric = symmetric;
	}
	
	public static Color getDefaultColor(){
		return defaultColor;
	}
	
	public static Color getClickedColor(){
		return clickedColor;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		changeColor();
		symmetric.changeColor();
	}

	public void changeColor() {
		if (this.getBackground() == defaultColor)
			this.setBackground(clickedColor);
		else 
			this.setBackground(defaultColor);
		
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
