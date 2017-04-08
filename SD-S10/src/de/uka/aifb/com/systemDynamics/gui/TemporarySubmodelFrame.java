package de.uka.aifb.com.systemDynamics.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uka.aifb.com.systemDynamics.SystemDynamics;
import de.uka.aifb.com.systemDynamics.event.SystemDynamicsGraphModifiedEventListener;
import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.SystemDynamicsGraph;
import de.uka.aifb.com.systemDynamics.model.ConstantNode;
import de.uka.aifb.com.systemDynamics.model.LevelNode;

public class TemporarySubmodelFrame extends JFrame implements ActionListener, ChangeListener,
SystemDynamicsGraphModifiedEventListener{

	private static final double DEFAULT_COORDINATE = 20.0;
	
	private Action saveAction;
	private JLabel GraphNumber;

	private JPanel contentPanel;
	private JScrollPane scrollPane;
	private JPanel modelPanel;
	
	private JScrollPane preContainer;

	private Action newAuxiliaryNodeAction;
	private Action newConstantNodeAction;
	private Action newLevelNodeAction;
	private Action newRateNodeAction;
	private Action newSourceSinkNodeAction;
	private Action toggleAddFlowAction;

	private static final String FILE_NEW_AN_ICON = "resources/new_auxiliary_node_en_US.png";
	private static final String FILE_NEW_CN_ICON = "resources/new_constant_node_en_US.png";
	private static final String FILE_NEW_LN_ICON = "resources/new_level_node_en_US.png";
	private static final String FILE_NEW_RN_ICON = "resources/new_rate_node_en_US.png";
	private static final String FILE_NEW_SSN_ICON = "resources/new_source_sink_node_en_US.png";
	
	private static final String FILE_SAVE_ICON = "resources/disk.png";

	private static final String FILE_ENTER_ADD_FLOW_MODE_ICON = "resources/disconnect.png";
	private static final String FILE_LEAVE_ADD_FLOW_MODE_ICON = "resources/connect.png";
	

	private SystemDynamics start;
	private SystemDynamicsGraph graph;

	private ResourceBundle messages;
	
//	private JFrame thisframe;

	
	public TemporarySubmodelFrame(SystemDynamics start, SystemDynamicsGraph graph, int subIndex, JScrollPane preContainer){
		

		this.start = start;
		messages = start.getMessages();
		this.graph = graph;
		this.preContainer=preContainer;
//		this.thisframe = this;
		setTitle("Submodel View");

		// set frame size and location

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int)(dimension.getWidth() * 0.75), (int)(dimension.getHeight() * 0.75));
		setLocation((int)(dimension.getWidth() * 0.125), (int)(dimension.getHeight() * 0.125));


		// initialize actions
		initializeActions();


		// create tool bar
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createToolBar(), BorderLayout.PAGE_START);
		contentPanel = new JPanel(new BorderLayout());
		modelPanel = new JPanel(new GridLayout(1,1));
		scrollPane = new JScrollPane(graph);
		
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		modelPanel.add(scrollPane);
		contentPanel.add(modelPanel,BorderLayout.CENTER);
		getContentPane().validate();
		
		setVisible(true);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		

	}
	
	private void initializeActions() {
		saveAction = new SaveAction(messages.getString("MainFrame.MenuBar.File.Save"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_SAVE_ICON)),
				messages.getString("MainFrame.MenuBar.File.Save"));
		
		newAuxiliaryNodeAction = new NewAuxiliaryNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewAuxiliaryNode"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_AN_ICON)),
				messages.getString("MainFrame.MenuBar.Edit.NewAuxiliaryNode"));
		newConstantNodeAction = new NewConstantNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewConstantNode"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_CN_ICON)),
				messages.getString("MainFrame.MenuBar.Edit.NewConstantNode"));
		newLevelNodeAction = new NewLevelNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewLevelNode"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_LN_ICON)),
				messages.getString("MainFrame.MenuBar.Edit.NewLevelNode"));
		newRateNodeAction = new NewRateNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewRateNode"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_RN_ICON)),
				messages.getString("MainFrame.MenuBar.Edit.NewRateNode"));
		newSourceSinkNodeAction = new NewSourceSinkNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewSourceSinkNode"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_SSN_ICON)),
				messages.getString("MainFrame.MenuBar.Edit.NewSourceSinkNode"));
		
		toggleAddFlowAction = new ToggleAddFlowAction(messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.EnterAddFlowMode"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ENTER_ADD_FLOW_MODE_ICON)),
				messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.EnterAddFlowMode"));
		
	}
	
	
	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		toolBar.add(saveAction);
		
		toolBar.addSeparator();

		toolBar.add(newAuxiliaryNodeAction);
		toolBar.add(newConstantNodeAction);
		toolBar.add(newLevelNodeAction);
		toolBar.add(newRateNodeAction);
		toolBar.add(newSourceSinkNodeAction);
		toolBar.add(toggleAddFlowAction);
		
		
//		GraphNumber.setFont(new Font(GraphNumber.getFont().getName(), Font.PLAIN, 30));
//		toolBar.add(GraphNumber);

		return toolBar;

	}
	
	private class SaveAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SaveAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			preContainer.setViewportView(graph);
			TemporarySubmodelFrame.this.dispose();
		}
	}

	public void performGraphModifiedEvent() {
		// TODO Auto-generated method stub
		
	}

	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	

	class NewAuxiliaryNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewAuxiliaryNodeAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			String nodeName =
					NodeNameDialog.showNodeNameDialog(start, TemporarySubmodelFrame.this,
							messages.getString("MainFrame.MenuBar.Edit.NewAuxiliaryNode"),
							messages.getString("MainFrame.MenuBar.Edit.NewNodeName"));

			if (nodeName != null) {
				//gets the reference numbers for all submodels and adds them an array to be translated to an object later (in the if portion of the if...else statement that follows).
				ArrayList<Integer> SubmodelNumbers = new ArrayList<Integer>();
				
				graph.createAuxiliaryNodeGraphCell(nodeName, TemporarySubmodelFrame.DEFAULT_COORDINATE, TemporarySubmodelFrame.DEFAULT_COORDINATE, false, false);


			}

		}
	}

	class NewConstantNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewConstantNodeAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			NodeNameParameterDialog.NodeNameParameter newNodeNameParameter =
					NodeNameParameterDialog.showNodeNameDialog(start, TemporarySubmodelFrame.this,
							messages.getString("MainFrame.MenuBar.Edit.NewConstantNode"),
							messages.getString("MainFrame.MenuBar.Edit.NewNodeName"),
							messages.getString("MainFrame.MenuBar.Edit.NewNodeParameter.ConstantNode"),
							ConstantNode.MIN_CONSTANT,
							ConstantNode.MAX_CONSTANT);

			if (newNodeNameParameter != null) {
				
				JFrame LearnerDecidableframe = new JFrame("InputDialog");
				Object[] LeanerDecidablechoices = {"no","yes"};
				String LearnerDecidable = (String) JOptionPane.showInputDialog(LearnerDecidableframe,"Should this node be Learner Decidable?","Leaner Decidable?",JOptionPane.PLAIN_MESSAGE,null,LeanerDecidablechoices,LeanerDecidablechoices[0]);
				
				if(LearnerDecidable.equals("yes")){
					graph.createConstantNodeGraphCell(newNodeNameParameter.getNodeName(),
							newNodeNameParameter.getNodeParameter(),
							TemporarySubmodelFrame.DEFAULT_COORDINATE, TemporarySubmodelFrame.DEFAULT_COORDINATE, true, false);
				}else{
					graph.createConstantNodeGraphCell(newNodeNameParameter.getNodeName(),
							newNodeNameParameter.getNodeParameter(),
							TemporarySubmodelFrame.DEFAULT_COORDINATE, TemporarySubmodelFrame.DEFAULT_COORDINATE, false, false);
				}
				
			}
		}
	}

	class NewLevelNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewLevelNodeAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			NodeNameParameterDialogLN.NodeNameParameterLN newNodeNameParameter =
					NodeNameParameterDialogLN.showNodeNameDialog(start, TemporarySubmodelFrame.this,
							messages.getString("MainFrame.MenuBar.Edit.NewLevelNode"),
							messages.getString("MainFrame.MenuBar.Edit.NewNodeName"),
							messages.getString("MainFrame.MenuBar.Edit.NewNodeParameter.LevelNode"),
							"Minimum Value",
							"Maximum Value",
							"Non-Decreasing",
							"Non-Increasing",
							"NULL",
							LevelNode.MIN_START_VALUE,
							LevelNode.MAX_START_VALUE);
			if (newNodeNameParameter != null) {
				
				JFrame LearnerDecidableframe = new JFrame("InputDialog");
				Object[] LeanerDecidablechoices = {"no","yes"};
				String LearnerDecidable = (String) JOptionPane.showInputDialog(LearnerDecidableframe,"Should this node be Learner Decidable?","Leaner Decidable?",JOptionPane.PLAIN_MESSAGE,null,LeanerDecidablechoices,LeanerDecidablechoices[0]);
				
				
				if(LearnerDecidable.equals("yes")){
					graph.createLevelNodeGraphCell(newNodeNameParameter.getNodeName(),
							newNodeNameParameter.getNodeParameter(),
							newNodeNameParameter.getMinParameter(),
							newNodeNameParameter.getMaxParameter(),
							newNodeNameParameter.getCurveParameter(),
							TemporarySubmodelFrame.DEFAULT_COORDINATE, TemporarySubmodelFrame.DEFAULT_COORDINATE, true, false);
				}
				else{
					graph.createLevelNodeGraphCell(newNodeNameParameter.getNodeName(),
							newNodeNameParameter.getNodeParameter(),
							newNodeNameParameter.getMinParameter(),
							newNodeNameParameter.getMaxParameter(),
							newNodeNameParameter.getCurveParameter(),
							TemporarySubmodelFrame.DEFAULT_COORDINATE, TemporarySubmodelFrame.DEFAULT_COORDINATE, false, false);
				}


			}
		}
	}

	class NewRateNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewRateNodeAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			String nodeName =
					NodeNameDialog.showNodeNameDialog(start, TemporarySubmodelFrame.this,
							messages.getString("MainFrame.MenuBar.Edit.NewRateNode"),
							messages.getString("MainFrame.MenuBar.Edit.NewNodeName"));

			if (nodeName != null) {
					
						graph.createRateNodeGraphCell(nodeName, TemporarySubmodelFrame.DEFAULT_COORDINATE, TemporarySubmodelFrame.DEFAULT_COORDINATE, false, false);
					
			}
				

			
		}
	}

	class NewSourceSinkNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewSourceSinkNodeAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			
				graph.createSourceSinkNodeGraphCell(TemporarySubmodelFrame.DEFAULT_COORDINATE, TemporarySubmodelFrame.DEFAULT_COORDINATE, false, "SN");
			
		}
	}
	
	class ToggleAddFlowAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private boolean inAddFlowMode;

		public ToggleAddFlowAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);

			inAddFlowMode = false;
		}

		public boolean isInAddFlowMode() {
			return inAddFlowMode;
		}

		public void actionPerformed(ActionEvent e) {
			

			graph.setPortsVisible(!graph.isPortsVisible());
			inAddFlowMode = !inAddFlowMode;
			if (inAddFlowMode) {
				putValue(Action.SMALL_ICON, new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_LEAVE_ADD_FLOW_MODE_ICON)));
				putValue(Action.NAME, messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.LeaveAddFlowMode"));
				putValue(Action.SHORT_DESCRIPTION, messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.LeaveAddFlowMode"));
				
				contentPanel.revalidate();
				contentPanel.repaint();
				modelPanel.revalidate();
				modelPanel.repaint();
			} else {
				
				contentPanel.remove(modelPanel);
				contentPanel.add(modelPanel);
				contentPanel.revalidate();
				putValue(Action.SMALL_ICON, new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ENTER_ADD_FLOW_MODE_ICON)));
				putValue(Action.NAME, messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.EnterAddFlowMode"));
				putValue(Action.SHORT_DESCRIPTION, messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.EnterAddFlowMode"));
				
			}
			modelPanel.revalidate();
			modelPanel.repaint();
			contentPanel.revalidate();
			contentPanel.repaint();

		}
	}
}
