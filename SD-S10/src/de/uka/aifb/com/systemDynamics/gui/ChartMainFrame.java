package de.uka.aifb.com.systemDynamics.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.SystemDynamicsGraph;

public class ChartMainFrame extends JFrame{
	JFileChooser fileChooser;
	private static final String FILE_ICON = "resources/icon.png";
	private static final String FILE_OPEN_ICON = "resources/folder_page_white.png";
	private Action newAction;
	private Action openAction;
	private Action newChartLevelNodeAction;
	private Action newChartPlanNodeAction;
	private Action newPlanNodeAction;
	private Action newPlanNodeIncrementAction;
	private JPanel contentPanel;
	private JPanel modelPanel;
	private JScrollPane chartLevelNodePanel;
	private JScrollPane chartPlanNodePanel;
	private JScrollPane PlanNodePanel;
	private JScrollPane PlanNodeIncrementPanel;
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3;
	private JPanel panel4;
	
	private static final String FILE_NEW_ICON = "resources/page_white.png";
	public ChartMainFrame(){		

		fileChooser = new JFileChooser();

		//allows JFileChooser to select both files and directories.
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		setTitle("Chart Designer");

		// set frame size and location
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int)(dimension.getWidth() * 0.75), (int)(dimension.getHeight() * 0.75));
		setLocation((int)(dimension.getWidth() * 0.125), (int)(dimension.getHeight() * 0.125));

		// set icon
		setIconImage(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ICON)).getImage());

		// initialize actions
		initializeActions();

		// create tool bar
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createToolBar(), BorderLayout.PAGE_START);
		
		panel1 = new JPanel(new BorderLayout());
		panel1.setBackground(Color.red);
		
		panel2 = new JPanel(new BorderLayout());
		panel2.setBackground(Color.white);
		
		panel3 = new JPanel(new BorderLayout());
		panel3.setBackground(Color.green);
	
		panel4 = new JPanel(new BorderLayout());
		panel4.setBackground(Color.yellow);
		
		contentPanel = new JPanel(new GridLayout(2,2));
		
		
		chartLevelNodePanel = new JScrollPane(panel1);
		
		
		chartPlanNodePanel = new JScrollPane(panel2);
		
	
		PlanNodePanel = new JScrollPane(panel3);
		
		
		PlanNodeIncrementPanel = new JScrollPane(panel4);
		
		
		getContentPane().add(contentPanel, BorderLayout.CENTER);


		setVisible(true);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}
	private void initializeActions() {
		// TODO Auto-generated method stub
		newAction = new NewAction("New Action",
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_ICON)),
				"New Action");
		openAction = new openAction("Open",
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_OPEN_ICON)),
				"Open");
		newChartLevelNodeAction = new newChartLevelNodeAction("New Chart Level Node", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_ICON)),
				"New Chart Plan Node");
		newChartPlanNodeAction = new newChartPlanNodeAction("New Chart Plan Node", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_ICON)),
				"New Chart Plan Node");
		newPlanNodeAction = new newPlanNodeAction("New Plan Node", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_ICON)),
				"New Plan Node");
		newPlanNodeIncrementAction = new newPlanNodeIncrementAction("New Plan Node Increment", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_ICON)),
				"New Plan Node Increment");
		
	}
	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		toolBar.add(newAction);
		toolBar.add(openAction);
		toolBar.add(newChartLevelNodeAction);
		toolBar.add(newChartPlanNodeAction);
		toolBar.add(newPlanNodeAction);
		toolBar.add(newPlanNodeIncrementAction);
		return toolBar;

	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private class NewAction extends AbstractAction{
		public NewAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);

		}
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null, "TEST");
			// TODO Auto-generated method stub
			//modelPanel.add(chartLevelNodePanel);
			//modelPanel.add(PlanNodePanel);
			//modelPanel.add(PlanNodeIncrementPanel);
			chartLevelNodePanel.setVisible(true);
			chartPlanNodePanel.setVisible(true);
			PlanNodePanel.setVisible(true);
			PlanNodeIncrementPanel.setVisible(true);
			contentPanel.setLayout(new GridLayout(2,2));
			contentPanel.add(chartLevelNodePanel);
			
			contentPanel.add(chartLevelNodePanel);
			
			contentPanel.add(chartPlanNodePanel);
			
			contentPanel.add(PlanNodePanel);
			contentPanel.add(PlanNodeIncrementPanel);
			
			//contentPanel.add(modelPanel);
			
			contentPanel.setVisible(true);
			
			chartLevelNodePanel.setVisible(true);
			contentPanel.revalidate();
		}

	}
	private class openAction extends AbstractAction{
		public openAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);

		}
		@Override
		public void actionPerformed(ActionEvent e) {


		}

	}

	private class newChartLevelNodeAction extends AbstractAction{
		public newChartLevelNodeAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		@Override
		public void actionPerformed(ActionEvent e){

		}
	}
	private class newChartPlanNodeAction extends AbstractAction{
		public newChartPlanNodeAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		@Override
		public void actionPerformed(ActionEvent e){

		}
	}
	private class newPlanNodeAction extends AbstractAction{
		public newPlanNodeAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		@Override
		public void actionPerformed(ActionEvent e){

		}
	}
	private class newPlanNodeIncrementAction extends AbstractAction{
		public newPlanNodeIncrementAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		@Override
		public void actionPerformed(ActionEvent e){

		}
	}
}

