package de.uka.aifb.com.systemDynamics.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.SystemDynamicsGraph;
import de.uka.aifb.com.systemDynamics.model.ChartLevelNode;
import de.uka.aifb.com.systemDynamics.model.ChartModel;
import de.uka.aifb.com.systemDynamics.model.ChartPlanNode;
import de.uka.aifb.com.systemDynamics.model.LevelNode;
import de.uka.aifb.com.systemDynamics.model.Model;
import de.uka.aifb.com.systemDynamics.model.PlanNode;
import de.uka.aifb.com.systemDynamics.model.PlanNodeIncrement;
import de.uka.aifb.com.systemDynamics.xml.ChartXMLModelReader;
import de.uka.aifb.com.systemDynamics.xml.ChartXMLModelWriter;
import de.uka.aifb.com.systemDynamics.xml.XMLModelReaderWriterException;

public class ChartMainFrame extends JFrame{
	JFileChooser fileChooser;
	private static final String FILE_ICON = "resources/icon.png";
	private static final String FILE_OPEN_ICON = "resources/folder_page_white.png";
	private static final String SUBMODEL_ICON = "resources/submodel.png";
	private static final String FILE_SAVE_ICON = "resources/disk.png";
	private Action newAction;
	private Action openAction;
	private Action saveAction;
	private Action newChartLevelNodeAction;
	private Action newChartPlanNodeAction;
	private Action newPlanNodeAction;
	private Action newPlanNodeIncrementAction;
	private Action addChartAction;
	private Action editChartAction;
	private JPanel contentPanel;
	private JPanel chartsPanel;
	private JScrollPane chartScrollPanel;
	private JPanel panel1;
	private static final String FILE_NEW_LN_ICON = "resources/new_level_node_en_US.png";
	private static final String CHART_PN_ICON = "resources/ChartPlanNodeIcon.png";
	private static final String PN_ICON = "resources/PlanNodeIcon.png";
	private static final String PNI_ICON = "resources/PlanNodeIncrementIcon.png";
	private ArrayList<ChartModel> chart;
	private ArrayList<JPanel> chartPanels;
	private ArrayList<JList> pnlists;
	private ArrayList<JList> lnlists;
	private JList pnlist;
	private JList pnilist;
	private JLabel GraphNumber = new JLabel("");
	private GridBagConstraints gbc;
	private String SPACE="     ";
	private static final String FILE_NEW_ICON = "resources/page_white.png";
	private ArrayList<Model> localModel;
	public ChartMainFrame(ArrayList<Model> model){		
		localModel =  model;
		fileChooser = new JFileChooser();

		//allows JFileChooser to select both files and directories.
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chart = new ArrayList<ChartModel>();
		chartPanels = new ArrayList<JPanel>();
		pnlists = new ArrayList<JList>();
		lnlists = new ArrayList<JList>();
		
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

		Border chartBorder = BorderFactory.createLineBorder(Color.black,5);

		panel1 = new JPanel(new BorderLayout());
		panel1.setBorder(chartBorder);
//		chartPanels.add(panel1);

		
		contentPanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		chartScrollPanel = new JScrollPane(panel1);

		getContentPane().add(contentPanel, BorderLayout.CENTER);

		setVisible(true);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}
	private void initializeActions() {
		newAction = new NewAction("New Action",
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_ICON)),
				"New Action");
		openAction = new openAction("Open",
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_OPEN_ICON)),
				"Open");
		newChartLevelNodeAction = new newChartLevelNodeAction("New Chart Level Node", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_LN_ICON)),
				"New Chart Level Node");
		newChartPlanNodeAction = new newChartPlanNodeAction("New Chart Plan Node", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(CHART_PN_ICON)),
				"New Chart Plan Node");
		newPlanNodeAction = new newPlanNodeAction("New Plan Node", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(PN_ICON)),
				"New Plan Node");
		newPlanNodeIncrementAction = new newPlanNodeIncrementAction("New Plan Node Increment", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(PNI_ICON)),
				"New Plan Node Increment");
		addChartAction = new addChartAction("Add Chart", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(SUBMODEL_ICON)),
				"Add Chart");
		saveAction = new saveAction("Save", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_SAVE_ICON)),
				"Save Chart");
		editChartAction = new editChartAction("Edit Chart", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_SAVE_ICON)), "Edit Chart");
		addChartAction.setEnabled(false);
		newChartLevelNodeAction.setEnabled(false);
		newChartPlanNodeAction.setEnabled(false);
		newPlanNodeAction.setEnabled(false);
		newPlanNodeIncrementAction.setEnabled(false);


	}
	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		toolBar.add(newAction);
		toolBar.add(addChartAction);
		toolBar.addSeparator();
		toolBar.add(openAction);
		toolBar.add(saveAction);
		toolBar.add(newChartLevelNodeAction);
		toolBar.add(newChartPlanNodeAction);
		toolBar.add(newPlanNodeAction);
		toolBar.add(newPlanNodeIncrementAction);
//		toolBar.add(editChartAction);
		GraphNumber.setFont(new Font(GraphNumber.getFont().getName(), Font.PLAIN, 30));
		toolBar.add(GraphNumber);

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
			// TODO Auto-generated method stub
			//modelPanel.add(chartLevelNodePanel);
			//modelPanel.add(PlanNodePanel);
			//modelPanel.add(PlanNodeIncrementPanel);
			String name = JOptionPane.showInputDialog(null,"Chart Name:","Name",JOptionPane.PLAIN_MESSAGE);
			String id = JOptionPane.showInputDialog(null,"Chart Id: ","Id",JOptionPane.PLAIN_MESSAGE);
			String file = JOptionPane.showInputDialog(null,"Chart File","Chart File",JOptionPane.PLAIN_MESSAGE);
			String xAxisLabel = JOptionPane.showInputDialog(null,"X Axis Label","X Axis Label",JOptionPane.PLAIN_MESSAGE);
			String yAxisLabel = JOptionPane.showInputDialog(null,"Y Axis Label","Y Axis Label",JOptionPane.PLAIN_MESSAGE);
			String global = JOptionPane.showInputDialog(null,"global","global",JOptionPane.PLAIN_MESSAGE);
			int PRChoice = JOptionPane.showConfirmDialog(null, "Add a PR?");
			String pr = "";
			if(PRChoice == JOptionPane.YES_OPTION){
				pr = JOptionPane.showInputDialog(null,"enter PR","PR",JOptionPane.PLAIN_MESSAGE);
			}
			
			if(name!=null && id!=null && file!=null && xAxisLabel!=null && yAxisLabel!=null && global!=null && pr!=null){
				for(int i=0;i<chart.size();i++){
					chart.remove(i);
				}
				contentPanel.removeAll();
				ChartModel cm = new ChartModel(name,id,file,xAxisLabel,yAxisLabel, global, pr);
				chart.add(cm);
	
				chartsPanel = new JPanel(new GridLayout(0,2));
				
				gbc.fill=GridBagConstraints.BOTH;
				gbc.gridx = 0;
				gbc.gridy = 0;
				gbc.gridheight=1;
				gbc.gridwidth=1;
				gbc.weightx=0.1;
				gbc.weighty=1.0;
				gbc.insets=new Insets(0,0,10,20);
				gbc.anchor = GridBagConstraints.NORTHWEST;
				contentPanel.add(createPlanNodesPanel(cm),gbc);
				
				panel1.add(createChartPanel(cm));
				chartPanels.add(panel1);
				chartScrollPanel = new JScrollPane(panel1);
				chartScrollPanel.setVisible(true);
	
				gbc.gridx = 1;
				gbc.gridy = 0;
				gbc.gridheight=1;
				gbc.gridwidth=2;
				gbc.weightx=1.0;
				gbc.weighty=1.0;
				gbc.insets=new Insets(0,0,10,0);
				chartsPanel.add(chartScrollPanel);
				contentPanel.add(chartsPanel,gbc);
				
				GraphNumber.setText("1");
				contentPanel.setVisible(true);
				addChartAction.setEnabled(true);
				newChartLevelNodeAction.setEnabled(true);
				newPlanNodeAction.setEnabled(true);
	
				contentPanel.revalidate();
			}
		}

	}
	private class openAction extends AbstractAction{
		public openAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);

		}
		@Override
		public void actionPerformed(ActionEvent e) {
//			chartPanels.remove(0);
			contentPanel.removeAll();
			int returnVal = fileChooser.showOpenDialog(ChartMainFrame.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// file was selected and 'OK' was pressed
				File file = fileChooser.getSelectedFile();
				try {
					chart = ChartXMLModelReader.readXMLModel(file.getAbsolutePath());

				}
				catch(Exception e3){
					e3.printStackTrace();
				}
				chartsPanel = new JPanel(new GridLayout(0,2));
					
				gbc.fill=GridBagConstraints.BOTH;
				gbc.gridx = 0;
				gbc.gridy = 0;
				gbc.gridheight=1;
				gbc.gridwidth=1;
				gbc.weightx=0.1;
				gbc.weighty=1.0;
				gbc.insets=new Insets(0,0,10,20);
				gbc.anchor = GridBagConstraints.NORTHWEST;
				contentPanel.add(createPlanNodesPanel(chart.get(0)),gbc);

				gbc.gridx = 1;
				gbc.gridy = 0;
				gbc.gridheight=1;
				gbc.gridwidth=2;
				gbc.weightx=1.0;
				gbc.weighty=1.0;
				gbc.insets=new Insets(0,0,10,0);
				contentPanel.add(chartsPanel,gbc);
				
				contentPanel.setVisible(true);
				addChartAction.setEnabled(true);
				newChartLevelNodeAction.setEnabled(true);
				newPlanNodeAction.setEnabled(true);

				contentPanel.revalidate();
				
				for(int i=0;i<chart.size();i++){

					Border chartBorder = BorderFactory.createLineBorder(Color.black,5);

					final JPanel panel2 = new JPanel(new BorderLayout());
					/*
					JTextArea newTextArea = new JTextArea(10,5);

					newTextArea.addMouseListener(new MouseListener(){

						@Override
						public void mouseClicked(MouseEvent e) {}

						@Override
						public void mouseEntered(MouseEvent e) {
							//Change label on toolbar to show graph number
							for(int i=0;i<chartPanels.size();i++){
								if(chartPanels.get(i).equals(panel2)){
									GraphNumber.setText(Integer.toString(i+1));
								}
							}

						}

						@Override
						public void mouseExited(MouseEvent e) {}

						@Override
						public void mousePressed(MouseEvent e) {}

						@Override
						public void mouseReleased(MouseEvent e) {}

					});
					panel2.add(newTextArea);*/
					
					
					
					panel2.add(createChartPanel(chart.get(i)));
					panel2.setBorder(chartBorder);
					chartPanels.add(panel2);

					JScrollPane submodelScroll = new JScrollPane(chartPanels.get(chartPanels.size()-1));
					GraphNumber.setText(Integer.toString(chartPanels.size()));
					chartsPanel.add(submodelScroll);
					
					
					newChartPlanNodeAction.setEnabled(true);
					newPlanNodeAction.setEnabled(true);
					newPlanNodeIncrementAction.setEnabled(true);
					newChartLevelNodeAction.setEnabled(true);
				}
				if(chartPanels.size()>4){
					contentPanel.setLayout(new GridLayout(chartPanels.size()/4+1,4));
				}
				//force layout to recalculate now that a new component has been added.
				contentPanel.revalidate();

			}

		}

	}
	private class saveAction extends AbstractAction{
		public saveAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);

		}
		@Override
		public void actionPerformed(ActionEvent e) {
			File file = null;
			fileChooser.setDialogTitle("Save Chart As");
			int returnVal = fileChooser.showSaveDialog(ChartMainFrame.this);
			int selectedOption = 0;
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// file was selected and 'OK' was pressed
				file = fileChooser.getSelectedFile();

				// file name should end with ".xml"
				if (!file.getName().toLowerCase().endsWith(".xml")) {
					file = new File(file.getAbsolutePath() + ".xml");
				}

				// check if existing file should be overwritten -> ask for confirmation!
				if (file.exists()) {
					PrintWriter writer = null;
					try {
						writer = new PrintWriter(file);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					writer.print("");
					writer.close();
					Object[] options = { "Yes", "No"};
					selectedOption = JOptionPane.showOptionDialog(ChartMainFrame.this,
							"Are you sure you want to overwrite?",
							"Overwrite file",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null, // don't use a custom Icon
							options,
							options[1]); // default button title

					if (selectedOption == 1) {
						// do not save
						return;
					}
				}
			} else {
				// no file selected
				return;
			}
			try {
				ChartXMLModelWriter.writeXMLModel(chart, file.getAbsolutePath());
				JOptionPane.showMessageDialog(null, "Save Successful!");
			} catch (XMLModelReaderWriterException e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "Save error");
				e1.printStackTrace();
			}
		}

	}
	private class newChartLevelNodeAction extends AbstractAction{
		public newChartLevelNodeAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		@Override
		public void actionPerformed(ActionEvent e){
			ArrayList<Integer> chartNumbers = new ArrayList<Integer>();
			for(int i=1;i<=chartPanels.size();i++){
				chartNumbers.add(i);
			}
			HashSet<LevelNode> allLevel = new HashSet<LevelNode>();
			for(int i=0;i<localModel.size();i++){
				allLevel.addAll(localModel.get(i).getLevelNodes());
			}
			
			String[] options = new String[allLevel.size()];
			
			
			int i=0;
			for(int j=0;i<localModel.size();j++){
				for(LevelNode node:localModel.get(j).getLevelNodes()){
					options[i] = "SM"+j+":"+node.getNodeName();
					i++;
				}
			}
			
			
			i=0;
			if(chartPanels.size()==1){
				
				String levelNodeIdRef = (String)JOptionPane.showInputDialog(null, "LevelNodeIdRef:", "new ChartLevelNode", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				String Label = (String)JOptionPane.showInputDialog(null, "Label:", "new ChartLevelNode", JOptionPane.PLAIN_MESSAGE);
				chart.get(0).createChartLevelNode(levelNodeIdRef, Label);

				((DefaultListModel)lnlists.get(0).getModel()).addElement(Label+"("+levelNodeIdRef+")");
				
				/*JTextArea chartText = (JTextArea)chartPanels.get(0).getComponent(0);
				HashSet<ChartLevelNode> levelNodes = chart.get(0).getChartLevelNodes();
				if(levelNodes.size()>1){
					chartText.append("\nChartLevelNode(LevelNodeIdRef: \""+levelNodeIdRef+"\", Label: \""+Label+"\")");
				}
				else{
					chartText.append("ChartLevelNode(LevelNodeIdRef: \""+levelNodeIdRef+"\", Label: \""+Label+"\")");
				}*/
			}
			else{
				try{
					JFrame frame = new JFrame("InputDialog");
					Object[] choices = chartNumbers.toArray();
					int chartIndex = (Integer)JOptionPane.showInputDialog(frame,"To which Chart?","Chart Level Node",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
					String levelNodeIdRef = (String)JOptionPane.showInputDialog(null, "LevelNodeIdRef:", "new ChartLevelNode", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					String Label = (String)JOptionPane.showInputDialog(null, "Label:", "new ChartLevelNode", JOptionPane.PLAIN_MESSAGE);
					chart.get(chartIndex-1).createChartLevelNode(levelNodeIdRef, Label);

					((DefaultListModel)lnlists.get(chartIndex-1).getModel()).addElement(Label+"("+levelNodeIdRef+")");

					/*
					JTextArea chartText = (JTextArea)chartPanels.get(chartIndex-1).getComponent(0);
					HashSet<ChartLevelNode> levelNodes = chart.get(chartIndex-1).getChartLevelNodes();
					if(levelNodes.size()>1){
						chartText.append("\nChartLevelNode(LevelNodeIdRef: \""+levelNodeIdRef+"\", Label: \""+Label+")");
					}
					else{
						chartText.append("ChartLevelNode(LevelNodeIdRef: \""+levelNodeIdRef+"\", Label: \""+Label+")");
					}*/

				}catch(Exception ex){

				}
			}

		}
	}
	private class newChartPlanNodeAction extends AbstractAction{
		public newChartPlanNodeAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		@Override
		public void actionPerformed(ActionEvent e){

			HashSet<PlanNode> PlanNodes = new HashSet<PlanNode>();

			ArrayList<Integer> chartNumbers = new ArrayList<Integer>();
			for(int i=1;i<=chartPanels.size();i++){
				chartNumbers.add(i);
			}

			if(chartPanels.size()==1){
				PlanNodes = chart.get(0).getPlanNodes();
				ArrayList<String> chartPlanNodeNames = new ArrayList<String>();

				for(PlanNode t: PlanNodes){
					chartPlanNodeNames.add(t.getId());
				}

				JFrame frame = new JFrame("InputDialog");
				Object[] choices = chartPlanNodeNames.toArray();
				String chosenPlanNode = (String)JOptionPane.showInputDialog(frame,"Which Plan Node Would you Like to add?","Chart PlanNode",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
				String Label = (String)JOptionPane.showInputDialog(null, "Label:", "new chart PlanNode", JOptionPane.PLAIN_MESSAGE);
				chart.get(0).createChartPlanNode(chosenPlanNode, Label);
				((DefaultListModel)pnlists.get(0).getModel()).addElement(Label+"("+chosenPlanNode+")");
				

			}
			else{
				try{

					JFrame frame = new JFrame("InputDialog");
					Object[] choices = chartNumbers.toArray();
					int chartIndex = (Integer)JOptionPane.showInputDialog(frame,"To which Chart?","Chart Level Node",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);

					PlanNodes = chart.get(0).getPlanNodes();
					ArrayList<String> chartPlanNodeNames = new ArrayList<String>();

					for(PlanNode t: PlanNodes){
						chartPlanNodeNames.add(t.getName());
					}

					JFrame frame2 = new JFrame("InputDialog");
					Object[] choices2 = chartPlanNodeNames.toArray();
					String chosenPlanNode = (String)JOptionPane.showInputDialog(frame2,"Which Plan Node Would you Like to add?","Chart PlanNode",JOptionPane.PLAIN_MESSAGE,null,choices2,choices2[0]);
					String Label = (String)JOptionPane.showInputDialog(null, "Label:", "new chart PlanNode", JOptionPane.PLAIN_MESSAGE);
					chart.get(chartIndex-1).createChartPlanNode(chosenPlanNode, Label);

					((DefaultListModel)pnlists.get(chartIndex-1).getModel()).addElement(Label+"("+chosenPlanNode+")");
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}			

		}
	}
	private class newPlanNodeAction extends AbstractAction{
		public newPlanNodeAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		@Override
		public void actionPerformed(ActionEvent e){
			ArrayList<Integer> chartNumbers = new ArrayList<Integer>();
			for(int i=1;i<=chartPanels.size();i++){
				chartNumbers.add(i);
			}

			String id = (String)JOptionPane.showInputDialog(null, "Id:", "new PlanNode", JOptionPane.PLAIN_MESSAGE);
			String name = (String)JOptionPane.showInputDialog(null, "Name:", "new PlanNode", JOptionPane.PLAIN_MESSAGE);
			try{
				Double startValue = Double.parseDouble(JOptionPane.showInputDialog(null, "Start Value:", "new PlanNode", JOptionPane.PLAIN_MESSAGE));

				chart.get(0).createPlanNode(id, name, startValue);
				JOptionPane.showMessageDialog(null, "Plan Node Successfully Added to Plan Node Library");
				((DefaultListModel) pnlist.getModel()).addElement(name);
				
				
				
				
				newChartPlanNodeAction.setEnabled(true);
				newPlanNodeIncrementAction.setEnabled(true);
			
			
			}
			catch(Exception e3){
				JOptionPane.showMessageDialog(null, "Start Value must be in decimal format");
			}

		}
	}
	private class newPlanNodeIncrementAction extends AbstractAction{
		public newPlanNodeIncrementAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		@Override
		public void actionPerformed(ActionEvent e){
			HashSet<PlanNode> PlanNodes = new HashSet<PlanNode>();


			PlanNodes = chart.get(0).getPlanNodes();
			ArrayList<String> chartPlanNodeNames = new ArrayList<String>();

			for(PlanNode t: PlanNodes){
				chartPlanNodeNames.add(t.getName());	
			}

			JFrame frame = new JFrame("InputDialog");
			Object[] choices = chartPlanNodeNames.toArray();
			String chosenPlanNode = (String)JOptionPane.showInputDialog(frame,"Add PlanNodeIncrement to which PlanNode?","PlanNodeIncrement",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);


			String id = (String)JOptionPane.showInputDialog(null, "Label:", "new PlanNodeIncrement", JOptionPane.PLAIN_MESSAGE);
			try{
				Double length = Double.parseDouble(JOptionPane.showInputDialog(null, "Length:", "new PlanNodeIncrement", JOptionPane.PLAIN_MESSAGE));
				Double slope = Double.parseDouble(JOptionPane.showInputDialog(null, "Slope", "new PlanNodeIncrement", JOptionPane.PLAIN_MESSAGE));

				//display plan node and plannode increments too.

				for(PlanNode t: PlanNodes){
					if(t.getName()==chosenPlanNode){
						t.createPlanNodeIncrement(id, length, slope);
						if(((String)pnlist.getSelectedValue()).equals(chosenPlanNode))
							((DefaultListModel) pnilist.getModel()).addElement(id);
					}
				}
				JOptionPane.showMessageDialog(null, "PlanNodeIncrement Sucessfully created");
			}
			catch(Exception e2){
				JOptionPane.showMessageDialog(null, "Length and Slope must be in decimal format");
			}				

			/*
			else{
				try{

					JFrame frame = new JFrame("InputDialog");
					Object[] choices = chartNumbers.toArray();
					int chartIndex = (Integer)JOptionPane.showInputDialog(frame,"To which Chart?","Chart Level Node",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);

					PlanNodes = chart.get(chartIndex-1).getPlanNodes();
					ArrayList<String> chartPlanNodeNames = new ArrayList<String>();

					for(PlanNode t: PlanNodes){
						chartPlanNodeNames.add(t.getName());	
					}

					JFrame frame2 = new JFrame("InputDialog");
					Object[] choices2 = chartPlanNodeNames.toArray();
					String chosenPlanNode = (String)JOptionPane.showInputDialog(frame2,"Add PlanNodeIncrement to which PlanNode?","PlanNodeIncrement",JOptionPane.PLAIN_MESSAGE,null,choices2,choices2[0]);

					String id = (String)JOptionPane.showInputDialog(null, "Label:", "new PlanNodeIncrement", JOptionPane.PLAIN_MESSAGE);
					try{
						Double length = Double.parseDouble(JOptionPane.showInputDialog(null, "Length:", "new PlanNodeIncrement", JOptionPane.PLAIN_MESSAGE));
						Double slope = Double.parseDouble(JOptionPane.showInputDialog(null, "Slope", "new PlanNodeIncrement", JOptionPane.PLAIN_MESSAGE));

						//display plan node and plannode increments too.

						for(PlanNode t: PlanNodes){
							if(t.getName()==chosenPlanNode){
								t.createPlanNodeIncrement(id, length, slope);
							}
						}
						JOptionPane.showMessageDialog(null, "PlanNodeIncrement Sucessfully created");
					}
					catch(Exception e3){
						JOptionPane.showMessageDialog(null, "Length or Slope not in decimal format");
					}

				}catch(Exception ex){
					ex.printStackTrace();
				}
			}*/			
		}
	}
	private class editChartAction extends AbstractAction{
		public editChartAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		@Override
		
		public void actionPerformed(ActionEvent e){
			String[] options = new String[chart.size()];
			for(int i=0;i<chart.size();i++){
				options[i] = Integer.toString(i+1);
			}
			int chartNum= Integer.parseInt((String) JOptionPane.showInputDialog(null, "Which Chart would you like to edit?", "Edit Chart", JOptionPane.PLAIN_MESSAGE, null, options, options[0]));
			String name = (String) JOptionPane.showInputDialog(null,"Chart Name:","Name",JOptionPane.PLAIN_MESSAGE, null, null, chart.get(chartNum-1).getchartName());
			String id = (String) JOptionPane.showInputDialog(null,"Chart Id: ","Id",JOptionPane.PLAIN_MESSAGE, null, null, chart.get(chartNum-1).getchartId());
			String file = (String) JOptionPane.showInputDialog(null,"Chart File","Chart File",JOptionPane.PLAIN_MESSAGE, null, null, chart.get(chartNum-1).getfile());
			String xAxisLabel = (String) JOptionPane.showInputDialog(null,"X Axis Label","X Axis Label",JOptionPane.PLAIN_MESSAGE, null, null, chart.get(chartNum-1).getxAxisLabel());
			String yAxisLabel = (String) JOptionPane.showInputDialog(null,"Y Axis Label","Y Axis Label",JOptionPane.PLAIN_MESSAGE, null, null, chart.get(chartNum-1).getyAxisLabel());
			String global = (String) JOptionPane.showInputDialog(null,"global","global",JOptionPane.PLAIN_MESSAGE, null, null, chart.get(chartNum-1).getGlobal());
			String pr = "";
			if(chart.get(chartNum-1).getPr() == ""){
				int PRChoice = JOptionPane.showConfirmDialog(null, "Add a PR?");
				if(PRChoice == JOptionPane.YES_OPTION){
					pr = JOptionPane.showInputDialog(null,"enter PR","PR",JOptionPane.PLAIN_MESSAGE);
				}
			}
			else{
				pr = (String) JOptionPane.showInputDialog(null,"enter PR","PR",JOptionPane.PLAIN_MESSAGE, null, null, chart.get(chartNum-1).getPr());
			}
			try{
				chart.get(chartNum-1).setChartName(name);
				chart.get(chartNum-1).setChartId(id);
				chart.get(chartNum-1).setChartFile(file);
				chart.get(chartNum-1).setxAxisLabel(xAxisLabel);
				chart.get(chartNum-1).setyAxisLabel(yAxisLabel);
				chart.get(chartNum-1).setPR(pr);
				chart.get(chartNum-1).setglobal(global);
			}
			catch(Exception e2){
				
			}
		}
	}
	private class addChartAction extends AbstractAction{
		public addChartAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		@Override

		public void actionPerformed(ActionEvent e){

			Border chartBorder = BorderFactory.createLineBorder(Color.black,5);

			final JPanel panel2 = new JPanel(new BorderLayout());
//			JTextArea newTextArea = new JTextArea(10,5);
			String name = JOptionPane.showInputDialog(null,"Chart Name:","Name",JOptionPane.PLAIN_MESSAGE);
			String id = JOptionPane.showInputDialog(null,"Chart Id: ","Id",JOptionPane.PLAIN_MESSAGE);
			String file = JOptionPane.showInputDialog(null,"Chart File","Chart File",JOptionPane.PLAIN_MESSAGE);
			String xAxisLabel = JOptionPane.showInputDialog(null,"X Axis Label","X Axis Label",JOptionPane.PLAIN_MESSAGE);
			String yAxisLabel = JOptionPane.showInputDialog(null,"Y Axis Label","Y Axis Label",JOptionPane.PLAIN_MESSAGE);
			String global = JOptionPane.showInputDialog(null,"global","global",JOptionPane.PLAIN_MESSAGE);
			int PRChoice = JOptionPane.showConfirmDialog(null, "Add a PR?");
			String pr = "";
			if(PRChoice == JOptionPane.YES_OPTION){
				pr = JOptionPane.showInputDialog(null,"enter PR","PR",JOptionPane.PLAIN_MESSAGE);
			}

			ChartModel cm = new ChartModel(name, id, file, xAxisLabel, yAxisLabel, global, pr);
			chart.add(cm);
/*
			newTextArea.addMouseListener(new MouseListener(){

				@Override
				public void mouseClicked(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {
					//Change label on toolbar to show graph number
					for(int i=0;i<chartPanels.size();i++){
						if(chartPanels.get(i).equals(panel2)){
							GraphNumber.setText(Integer.toString(i+1));
						}
					}

				}

				@Override
				public void mouseExited(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseReleased(MouseEvent e) {}

			});
			panel2.add(newTextArea);*/
			panel2.setBorder(chartBorder);

			panel2.add(createChartPanel(cm));
			
			chartPanels.add(panel2);

			JScrollPane submodelScroll = new JScrollPane(chartPanels.get(chartPanels.size()-1));
			GraphNumber.setText(Integer.toString(chartPanels.size()));
			chartsPanel.add(submodelScroll);
//			if(chartPanels.size()>2){
//				chartsPanel.setLayout(new GridLayout(chartPanels.size()/3+1,2));
//			}
			//force layout to recalculate now that a new component has been added.
			contentPanel.revalidate();
		}
	}
	
	
	private JPanel createChartPanel(final ChartModel cm){
		
		final JPanel chartPanel = new JPanel();
		chartPanel.setLayout(new BorderLayout());//new GridLayout(1,2));
		final int idx = chartPanels.size()+1;
		chartPanel.addMouseListener(new MouseListener(){
		
			@Override
			public void mouseClicked(MouseEvent e) {
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

			@Override
			public void mouseEntered(MouseEvent e) {
				

				
				GraphNumber.setText(String.valueOf(idx));
					
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		final JPanel chartInfoPanel = new JPanel(new GridLayout(2,8));
		
		TitledBorder border = BorderFactory.createTitledBorder("Chart Information");
		border.setTitleColor(Color.gray);
		border.setTitleFont(new Font(border.getTitleFont().getFontName(),Font.PLAIN,11));
		chartInfoPanel.setBorder(border);
		

		final ArrayList<JTextField> textFields = new ArrayList<JTextField>();
		JPanel editPanel = new JPanel();
		String [] titles = {"Name","ID","File","X Axis","Y Axis","GLobal","PR"};
		final String [] texts = {cm.getchartName(),cm.getchartId(),cm.getfile(),cm.getxAxisLabel(),cm.getyAxisLabel(),cm.getGlobal(),cm.getPr()};
		
		editPanel.setLayout(new GridLayout(titles.length,2));
		for(int i=0;i<titles.length;i++){
			JLabel label = new JLabel(titles[i]);
			JTextField text = new JTextField(texts[i]);
			text.setEditable(false);
			textFields.add(text);
			editPanel.add(label);
			editPanel.add(text);
		}
		
	      final JPanel buttonPanel = new JPanel();
	      final JPanel buttonPanel2 = new JPanel();
	      final CardLayout card = new CardLayout();
	      final JPanel buttonsPanel = new JPanel(card);
	      
	      JButton okButton = new JButton("OK");
	      okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(int i=0;i<textFields.size();i++){
						textFields.get(i).setEditable(false);
						String str = textFields.get(i).getText();
						switch(i){
							case 0: {cm.setChartName(str); texts[0]=str;}
							case 1: {cm.setChartId(str); texts[1]=str;}
							case 2: {cm.setChartFile(str);texts[2]=str;}
							case 3: {cm.setxAxisLabel(str);texts[3]=str;}
							case 4: {cm.setyAxisLabel(str);texts[4]=str;}
							case 5: {cm.setglobal(str);texts[5]=str;}
							case 6: {cm.setPR(str);texts[6]=str;}
						}
					}

					card.previous(buttonsPanel);      
				}
			});
		
	      buttonPanel.add(okButton);
	      JButton cancelButton = new JButton("Cancel");
	      cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(int i=0;i<textFields.size();i++){
						textFields.get(i).setEditable(false);
						textFields.get(i).setText(texts[i]);
					}


					card.previous(buttonsPanel);
				}
			});
	      buttonPanel.add(cancelButton);
		

	      JButton editButton = new JButton("Edit");
	      editButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(int i=0;i<textFields.size();i++){
						textFields.get(i).setEditable(true);
					}

					card.next(buttonsPanel);
				}
			});
	      
	    buttonPanel2.add(editButton);
	    buttonsPanel.add("bp2",buttonPanel2);
	    buttonsPanel.add("bp1",buttonPanel);
	    
	    
		chartInfoPanel.add(editPanel);
		chartInfoPanel.add(buttonsPanel);
		

		chartPanel.add(chartInfoPanel,BorderLayout.CENTER);

		JPanel listsPanel = new JPanel(new GridLayout(1,2));
		
		JPanel pnListPanel = new JPanel();
		
		TitledBorder border2 = BorderFactory.createTitledBorder("Plan Node List");
		border2.setTitleColor(Color.gray);
		border2.setTitleFont(new Font(border.getTitleFont().getFontName(),Font.PLAIN,11));
		pnListPanel.setBorder(border2);
		
		DefaultListModel pnmodel = new DefaultListModel();
		HashSet<ChartPlanNode> pnSet = cm.getChartPlanNodes();
		for(ChartPlanNode cpn:pnSet){
			pnmodel.addElement(cpn.getchartPlanNodeIdRef());
		}

		JList pns = new JList(pnmodel);
		final int chartIndex = pnlists.size();
		
		pns.addMouseListener( new MouseAdapter() {
		    public void mousePressed(MouseEvent e){
		        if (e.isPopupTrigger())
		            doPop(e);
		    }

		    public void mouseReleased(MouseEvent e){
		        if (e.isPopupTrigger())
		            doPop(e);
		    }

		    private void doPop(MouseEvent e){
		    	final JList list =(JList) e.getSource();
		    	final int index = list.locationToIndex(e.getPoint());
		        if (index >= 0) {
		            String o = ((String) list.getModel().getElementAt(index));
		            
		            final String[] temp = o.split("\\(");
		            
		            JPopupMenu menu = new JPopupMenu();
		            
		            JMenuItem anItem = new JMenuItem("Delete");
		            anItem.addActionListener(new ActionListener() {
		    	       public void actionPerformed(ActionEvent e) {
		    	    	   chart.get(chartIndex).removeChartPlanNode(temp[1].substring(0, temp[1].length()-1), temp[0]);
		    	    	   ((DefaultListModel)list.getModel()).remove(index);
		    	       }
		            });
		            menu.add(anItem);
		            menu.show(e.getComponent(), e.getX(), e.getY());
		        }
		    }
		});
		pnlists.add(pns);
		pnListPanel.add(pns);
		listsPanel.add(pnListPanel);

		JPanel lnPanel = new JPanel();

		TitledBorder border3 = BorderFactory.createTitledBorder("Level Node List");
		border3.setTitleColor(Color.gray);
		border3.setTitleFont(new Font(border.getTitleFont().getFontName(),Font.PLAIN,11));
		lnPanel.setBorder(border3);
		
		DefaultListModel lnmodel = new DefaultListModel();
		HashSet<ChartLevelNode> lnSet = cm.getChartLevelNodes();
		for(ChartLevelNode cln:lnSet){
			lnmodel.addElement(cln.getLevelNodeIdRef());
		}

		JList lns = new JList(lnmodel);
		
		lns.addMouseListener( new MouseAdapter() {
		    public void mousePressed(MouseEvent e){
		        if (e.isPopupTrigger())
		            doPop(e);
		    }

		    public void mouseReleased(MouseEvent e){
		        if (e.isPopupTrigger())
		            doPop(e);
		    }

		    private void doPop(MouseEvent e){
		    	final JList list =(JList) e.getSource();
		    	final int index = list.locationToIndex(e.getPoint());
		        if (index >= 0) {
		            String o = ((String) list.getModel().getElementAt(index));
		            
		            final String[] temp = o.split("\\(");
		            
		            JPopupMenu menu = new JPopupMenu();
		            
		            JMenuItem anItem = new JMenuItem("Delete");
		            anItem.addActionListener(new ActionListener() {
		    	       public void actionPerformed(ActionEvent e) {
		    	    	   chart.get(chartIndex).removeChartLevelNode(temp[1].substring(0, temp[1].length()-1), temp[0]);
		    	    	   ((DefaultListModel)list.getModel()).remove(index);
		    	       }
		            });
		            menu.add(anItem);
		            menu.show(e.getComponent(), e.getX(), e.getY());
		        }
		    }
		});
		
		lnlists.add(lns);
		lnPanel.add(lns);
		listsPanel.add(lnPanel);
		
		chartPanel.add(listsPanel,BorderLayout.SOUTH);
		return chartPanel;
	}

private JPanel createPlanNodesPanel(final ChartModel cm){

	final JPanel pnsPanel = new JPanel();
	pnsPanel.setLayout(new GridLayout(2,2));
	
	final ArrayList<PlanNode> pns = new ArrayList<PlanNode>(cm.getPlanNodes());
	DefaultListModel pnNames = new DefaultListModel();
	final DefaultListModel pniNames = new DefaultListModel();
	
	for(int i=0;i<pns.size();i++){
		pnNames.addElement(pns.get(i).getName());
	}
	
//	pnNames.addElement("Plan Node 1");
//	pnNames.addElement("Plan Node 2");
//	pniNames.addElement("Plan Node Increment 1");
//	pniNames.addElement("Plan Node Increment 2");
	pnlist = new JList(pnNames);
	
	pnilist = new JList(pniNames);

	final JPanel pnPanel = new JPanel();
	final JPanel pniPanel = new JPanel();
	TitledBorder border = BorderFactory.createTitledBorder("Plan Node");
	border.setTitleColor(Color.gray);
	border.setTitleFont(new Font(border.getTitleFont().getFontName(),Font.PLAIN,11));
	pnPanel.setBorder(border);
	pnPanel.add(createPlanNodePanel());
	
	
	TitledBorder border2 = BorderFactory.createTitledBorder("Plan Node Increment");
	border2.setTitleColor(Color.gray);
	border2.setTitleFont(new Font(border.getTitleFont().getFontName(),Font.PLAIN,11));
	pniPanel.setBorder(border2);
	pniPanel.add(createPlanNodeIncrementPanel());
	
	pnsPanel.add(new JScrollPane(pnlist));
	pnsPanel.add(new JScrollPane(pnilist));
	
	pnlist.addListSelectionListener(new ListSelectionListener(){

		@Override
		public void valueChanged(ListSelectionEvent e) {
			String pnName = (String) pnlist.getSelectedValue();
			if(pnName!=null){
				for(PlanNode pn:chart.get(0).getPlanNodes()){
					if(pnName.equals(pn.getName())){
						HashSet<PlanNodeIncrement> pnis = pn.getPlanNodeIncrements();
						pniNames.removeAllElements();
						for(PlanNodeIncrement pni : pnis){
							pniNames.addElement(pni.getId());
						}
						
						JTextField name = (JTextField) ((JPanel)((JPanel)pnPanel.getComponent(0)).getComponent(0)).getComponent(1);
						JTextField id = (JTextField) ((JPanel)((JPanel)pnPanel.getComponent(0)).getComponent(0)).getComponent(3);
						JTextField sv = (JTextField) ((JPanel)((JPanel)pnPanel.getComponent(0)).getComponent(0)).getComponent(5);
						name.setText(pnName);
						id.setText(pn.getId());
						sv.setText(pn.getStartValue().toString());
						
	
						JTextField pniId = (JTextField) ((JPanel)((JPanel)pniPanel.getComponent(0)).getComponent(0)).getComponent(1);
						JTextField length = (JTextField) ((JPanel)((JPanel)pniPanel.getComponent(0)).getComponent(0)).getComponent(3);
						JTextField slope = (JTextField) ((JPanel)((JPanel)pniPanel.getComponent(0)).getComponent(0)).getComponent(5);
						pniId.setText("");
						length.setText("");
						slope.setText("");
						
						break;
					}
				}
			}
		}
		
	});
	
	pnlist.addMouseListener( new MouseAdapter() {
	    public void mousePressed(MouseEvent e){
	        if (e.isPopupTrigger())
	            doPop(e);
	    }

	    public void mouseReleased(MouseEvent e){
	        if (e.isPopupTrigger())
	            doPop(e);
	    }

	    private void doPop(MouseEvent e){
	    	final JList list =(JList) e.getSource();
	    	final int index = list.locationToIndex(e.getPoint());
	        if (index >= 0) {
	            final String o = ((String) list.getModel().getElementAt(index));
	            
	            JPopupMenu menu = new JPopupMenu();
	            
	            JMenuItem anItem = new JMenuItem("Delete");
	            anItem.addActionListener(new ActionListener() {
	    	       public void actionPerformed(ActionEvent e) {
	    	    	   chart.get(0).removePlanNode(o);
	    	    	   ((DefaultListModel)list.getModel()).remove(index);
	    	       }
	            });
	            menu.add(anItem);
	            menu.show(e.getComponent(), e.getX(), e.getY());
	            
	            
	            if(chart.get(0).getPlanNodes().size()==0){
					newChartPlanNodeAction.setEnabled(false);
					newPlanNodeIncrementAction.setEnabled(false);
	            }
	        }
	    }
	});
	
	pnilist.addListSelectionListener(new ListSelectionListener(){

		@Override
		public void valueChanged(ListSelectionEvent e) {
			String pnName = (String) pnlist.getSelectedValue();
			String pniName = (String) pnilist.getSelectedValue();
			if(pniName!=null){
				for(PlanNode pn:chart.get(0).getPlanNodes()){
					if(pnName.equals(pn.getName())){
						HashSet<PlanNodeIncrement> pnis = pn.getPlanNodeIncrements();
						for(PlanNodeIncrement pni : pnis){
							if(pni.getId().equals(pniName)){
	
							JTextField id = (JTextField) ((JPanel)((JPanel)pniPanel.getComponent(0)).getComponent(0)).getComponent(1);
							JTextField length = (JTextField) ((JPanel)((JPanel)pniPanel.getComponent(0)).getComponent(0)).getComponent(3);
							JTextField slope = (JTextField) ((JPanel)((JPanel)pniPanel.getComponent(0)).getComponent(0)).getComponent(5);
							id.setText(pni.getId());
							length.setText(String.valueOf(pni.getLength()));
							slope.setText(String.valueOf(pni.getSlope()));
							
							break;
							}
						}
					}
				}
			}
		}
		
	});
	

	pnilist.addMouseListener( new MouseAdapter() {
	    public void mousePressed(MouseEvent e){
	        if (e.isPopupTrigger())
	            doPop(e);
	    }

	    public void mouseReleased(MouseEvent e){
	        if (e.isPopupTrigger())
	            doPop(e);
	    }

	    private void doPop(MouseEvent e){
	    	

			final String pnName = (String) pnlist.getSelectedValue();
			
			
	    	final JList list =(JList) e.getSource();
	    	final int index = list.locationToIndex(e.getPoint());
	        if (index >= 0) {
	            final String pniName = ((String) list.getModel().getElementAt(index));
	            
	            JPopupMenu menu = new JPopupMenu();
	            
	            JMenuItem anItem = new JMenuItem("Delete");
	            anItem.addActionListener(new ActionListener() {
	    	       public void actionPerformed(ActionEvent e) {
	    	    	   chart.get(0).removePlanNodeIncrement(pnName,pniName);
	    	    	   ((DefaultListModel)list.getModel()).remove(index);
	    	       }
	            });
	            menu.add(anItem);
	            menu.show(e.getComponent(), e.getX(), e.getY());
	            
	        }
	    }
	});
	
	pnsPanel.add(pnPanel);
	pnsPanel.add(pniPanel);
	
	return pnsPanel;
	
}

private JPanel createPlanNodePanel(){ 
	
	final JPanel pnPanel = new JPanel(new GridLayout(2,1));
	final JPanel editPanel = new JPanel();
	editPanel.setLayout(new GridLayout(4,2));
	JLabel label = new JLabel("Name");
	final JTextField text = new JTextField();
	JLabel label2 = new JLabel("Id");
	final JTextField text2 = new JTextField();
	JLabel label3 = new JLabel("Start Value"+SPACE);
	final JTextField text3 = new JTextField();
	
	
	final String[] attrs ={"","",""};
	
	text.setEditable(false);
	text2.setEditable(false);
	text3.setEditable(false);
	
	editPanel.add(label);
	editPanel.add(text);
	editPanel.add(label2);
	editPanel.add(text2);
	editPanel.add(label3);
	editPanel.add(text3);

	pnPanel.add(editPanel);
	
	
	final JPanel buttonPanel = new JPanel();
    final JPanel buttonPanel2 = new JPanel();
    final CardLayout card = new CardLayout();
    final JPanel buttonsPanel = new JPanel(card);
      
    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			String name=text.getText();
			String id=text2.getText();
			double sv=Double.parseDouble(text3.getText());
			
			String pnName = (String) pnlist.getSelectedValue();
			
			for(PlanNode pn:chart.get(0).getPlanNodes()){
				if(pnName.equals(pn.getName())){
					pn.setId(id);
					pn.setName(name);;
					pn.setStartValue(sv);
					break;
				}
			}
			
			DefaultListModel model = ((DefaultListModel)pnlist.getModel());
			model.removeElement(attrs[0]);
			model.addElement(name);
			text.setEditable(false);
			text2.setEditable(false);
			text3.setEditable(false);
			card.previous(buttonsPanel);
		}

	});
	
    buttonPanel.add(okButton);
    
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			text.setText(attrs[0]);
			text2.setText(attrs[1]);
			text3.setText(attrs[2]);
			text.setEditable(false);
			text2.setEditable(false);
			text3.setEditable(false);
			card.previous(buttonsPanel);
		}

    });
    buttonPanel.add(cancelButton);
	

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			attrs[0]=text.getText();
			attrs[1]=text2.getText();
			attrs[2]=text3.getText();
			text.setEditable(true);
			text2.setEditable(true);
			text3.setEditable(true);
			card.next(buttonsPanel);
		}

    });

    buttonPanel2.add(editButton);
    

    buttonsPanel.add("bp2",buttonPanel2);
    buttonsPanel.add("bp1",buttonPanel);
    pnPanel.add(buttonsPanel);
	
	return pnPanel;

}


private JPanel createPlanNodeIncrementPanel(){ 
	
	final JPanel pniPanel = new JPanel(new GridLayout(2,1));
	final JPanel editPanel = new JPanel();
	
	editPanel.setLayout(new GridLayout(4,2));
	
	JLabel id = new JLabel("Id");
	final JTextField idfield = new JTextField();
	JLabel length = new JLabel("Number of Rounds");
	final JTextField lengthfield = new JTextField();
	JLabel slope = new JLabel("Slope Value"+SPACE);
	final JTextField slopefield = new JTextField();
	editPanel.add(id);
	editPanel.add(idfield);
	editPanel.add(length);
	editPanel.add(lengthfield);
	editPanel.add(slope);
	editPanel.add(slopefield);

	idfield.setEditable(false);
	lengthfield.setEditable(false);
	slopefield.setEditable(false);
	
	pniPanel.add(editPanel);
	
	final JPanel buttonPanel = new JPanel();
    final JPanel buttonPanel2 = new JPanel();
    final CardLayout card = new CardLayout();
    final JPanel buttonsPanel = new JPanel(card);
	
	final String[] attrs ={"","",""};
      
    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			String id=idfield.getText();
			double length=Double.parseDouble(lengthfield.getText());
			double slope=Double.parseDouble(slopefield.getText());
			
			String pnName = (String) pnlist.getSelectedValue();
			String pniName = (String) pnilist.getSelectedValue();
			
			for(PlanNode pn:chart.get(0).getPlanNodes()){
				if(pnName.equals(pn.getName())){
					for(PlanNodeIncrement pni:pn.getPlanNodeIncrements()){
						pni.setId(id);
						pni.setLength(length);
						pni.setSlope(slope);
						break;
					}
				}
			}
			
			DefaultListModel model = ((DefaultListModel)pnilist.getModel());
			model.removeElement(attrs[0]);
			model.addElement(id);

			idfield.setEditable(false);
			lengthfield.setEditable(false);
			slopefield.setEditable(false);
			
			card.previous(buttonsPanel);
			
		}

	});
	
    buttonPanel.add(okButton);
    
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {


			idfield.setText(attrs[0]);
			lengthfield.setText(attrs[1]);
			slopefield.setText(attrs[2]);
			
			idfield.setEditable(false);
			lengthfield.setEditable(false);
			slopefield.setEditable(false);
			
			card.previous(buttonsPanel);
		}

    });
    buttonPanel.add(cancelButton);
	

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			attrs[0]=idfield.getText();
			attrs[1]=lengthfield.getText();
			attrs[2]=slopefield.getText();
			idfield.setEditable(true);
			lengthfield.setEditable(true);
			slopefield.setEditable(true);
			card.next(buttonsPanel);

			
		}

    });

    buttonPanel2.add(editButton);
    
    
    buttonsPanel.add("bp2",buttonPanel2);
    buttonsPanel.add("bp1",buttonPanel);

    pniPanel.add(buttonsPanel);
    
	return pniPanel;
}
}

class PopUpMenu extends JPopupMenu {
    JMenuItem anItem;
    public PopUpMenu(){
        anItem = new JMenuItem("Delete");
        anItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              System.out.println("Menu item Test1");
            }
          });
        add(anItem);
    }
}

class PopClickListener extends MouseAdapter {
    public void mousePressed(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e){
    	PopUpMenu menu = new PopUpMenu();
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}