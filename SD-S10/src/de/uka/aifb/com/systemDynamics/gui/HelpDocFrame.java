package de.uka.aifb.com.systemDynamics.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class HelpDocFrame extends JFrame{

//	private JPanel contentPanel;
	private JTextArea instruction = new JTextArea();
	ArrayList<String> commands = new ArrayList<String>();
	
	
	public HelpDocFrame(){
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int)(dimension.getWidth() * 0.5), (int)(dimension.getHeight() * 0.5));
		setLocation((int)(dimension.getWidth() * 0.25), (int)(dimension.getHeight() * 0.25));

		setTitle("Global File Editor Help Documentation");
//		contentPanel = new JPanel(new BorderLayout());
		
		JScrollPane scroll = new JScrollPane (instruction);
		
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scroll, BorderLayout.CENTER);

		setVisible(true);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		addCommands();
	}
	
	private void addCommands(){
		commands.add("SM1:LN, Constant");
		commands.add("The value of LN in Submodel 1 equals the value of the Constant");
		commands.add("SM1:LN, Constant%");
		commands.add("The value of LN in Submodel 1 is increased by Constant value percentage");
		commands.add("SM1:LN, SM1:LN+SM2:LN21");
		commands.add("The value of LN in Submodel 1 equals the sum of LN and LN21 in Submodel 2");
		commands.add("SM1:LN, SM1:LN-SM2:LN21");
		commands.add("The value of LN in Submodel 1 equals the subtraction of LN and LN21 in Submodel 2");
		commands.add("SM1:LN, SM1:LN*SM2:LN21");
		commands.add("The value of LN in Submodel 1 equals the multiplication of LN and LN21 in Submodel 2");
		commands.add("SM1:LN, SM1:LN/SM2:LN21");
		commands.add("The value of LN in Submodel 1 equals the division of LN and LN21 in Submodel 2");
		commands.add("SM1:LN, IF(VAR1 < VAR2; VAR3 ; VAR4)");
		commands.add("The value of LN in Submodel 1 equals VAR3 if VAR1 is less than VAR2. Otherwise, it will equal VAR4. VAR can be either a Levelnode or a constant");
		addInstruction();
		instruction.setEditable(false);
		instruction.setLineWrap(true);
	}
	
	private void addInstruction(){
		for(int i=0; i<commands.size(); i+=2){
			instruction.append(commands.get(i)+"\n");
			instruction.append(commands.get(i+1)+"\n\n");
		}
	}
}
