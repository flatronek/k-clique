package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import controller.CliqueManager;

public class GenerateWindow extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2256781610921331487L;
	
	private JLabel sizeLb;
    private JSpinner size;
    private JButton okButton;
    
    private CliqueManager cm;
    
	public GenerateWindow(CliqueManager cm){
		this.cm = cm;
		
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		
		setVisible(true);
		ui();
		pack();
		setSize(270, 100);
		setLocationRelativeTo(null);
		setTitle("Generate Matrix");
	}
	
	private void ui(){
		JPanel pane = (JPanel) getContentPane();
		GroupLayout gl = new GroupLayout(pane);
		pane.setLayout(gl);
		
		new javax.swing.JPanel();
        sizeLb = new javax.swing.JLabel("Matrix size");
        size = new javax.swing.JSpinner();
        okButton = new javax.swing.JButton("OK");
        
        sizeLb.setFont(new Font("Verdana", Font.PLAIN, 16));
        size.setPreferredSize(new Dimension(50, 30));
        
        gl.setHorizontalGroup( 
        		gl.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
        		.addComponent(sizeLb, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        		.addGroup(gl.createSequentialGroup()
        			.addComponent(size, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        
        gl.setVerticalGroup( 
        		gl.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
        		.addGroup(gl.createSequentialGroup() 
        				.addComponent(sizeLb, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addGroup(gl.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER) 
        						.addComponent(size, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE) 
        						.addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)))
        );
        
        okButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				createDirAction();
			}
        	
        });
	}

	private void createDirAction() {
		int value = (int) size.getValue();
		if (value > 5)
			cm.generateRandomGraph(value);
		dispose();
	}
}
