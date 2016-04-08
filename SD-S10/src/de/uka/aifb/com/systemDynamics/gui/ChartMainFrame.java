package de.uka.aifb.com.systemDynamics.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

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
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.SystemDynamicsGraph;
import de.uka.aifb.com.systemDynamics.model.ChartModel;
import de.uka.aifb.com.systemDynamics.model.PlanNode;
import de.uka.aifb.com.systemDynamics.model.PlanNodeIncrement;
import de.uka.aifb.com.systemDynamics.xml.ChartXMLModelReader;
import de.uka.aifb.com.systemDynamics.xml.ChartXMLModelWriter;

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
	private JPanel contentPanel;
	private JScrollPane chartScrollPanel;
	private JPanel panel1;
	private static final String FILE_NEW_LN_ICON = "resources/new_level_node_en_US.png";
	private static final String CHART_PN_ICON = "resources/ChartPlanNodeIcon.png";
	private static final String PN_ICON = "resources/PlanNodeIcon.png";
	private static final String PNI_ICON = "resources/PlanNodeIncrementIcon.png";
	private ArrayList<ChartModel> chart;
	private ArrayList<JPanel> chartPanels;
	private JLabel GraphNumber = new JLabel("");


	private static final String FILE_NEW_ICON = "resources/page_white.png";
	public ChartMainFrame(){		

		fileChooser = new JFileChooser();

		//allows JFileChooser to select both files and directories.
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chart = new ArrayList<ChartModel>();
		chartPanels = new ArrayList<JPanel>();

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
		JTextArea newTextArea = new JTextArea(10,5);
		newTextArea.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {
				//Change label on toolbar to show graph number
				for(int i=0;i<chartPanels.size();i++){
					if(chartPanels.get(i).equals(panel1)){
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
		panel1.add(newTextArea);
		panel1.setBorder(chartBorder);
		chartPanels.add(panel1);

		contentPanel = new JPanel(new GridLayout(1,4));


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
			
			chart.add(new ChartModel(name,id,file,xAxisLabel,yAxisLabel));
			chartScrollPanel = new JScrollPane(panel1);
			contentPanel.add(chartScrollPanel);
			chartScrollPanel.setVisible(true);
			GraphNumber.setText("1");
			contentPanel.setVisible(true);
			addChartAction.setEnabled(true);
			newChartLevelNodeAction.setEnabled(true);
			newPlanNodeAction.setEnabled(true);

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
			int returnVal = fileChooser.showOpenDialog(ChartMainFrame.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// file was selected and 'OK' was pressed
				File file = fileChooser.getSelectedFile();
				try {
					ArrayList<ChartModel> chart = ChartXMLModelReader.readXMLModel(file.getAbsolutePath());
					System.out.println(chart.size());
				}
				catch(Exception e3){
					e3.printStackTrace();
				}
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
			ChartXMLModelWriter.writeXMLModel(chart, file.getAbsolutePath());
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
			if(chartPanels.size()==1){
				String levelNodeIdRef = (String)JOptionPane.showInputDialog(null, "LevelNodeIdRef:", "new ChartLevelNode", JOptionPane.PLAIN_MESSAGE);
				String Label = (String)JOptionPane.showInputDialog(null, "Label:", "new ChartLevelNode", JOptionPane.PLAIN_MESSAGE);
				chart.get(0).createChartLevelNode(levelNodeIdRef, Label);
				JTextArea chartText = (JTextArea)chartPanels.get(0).getComponent(0);
				chartText.append("ChartLevelNode(LevelNodeIdRef: \""+levelNodeIdRef+"\", Label: \""+Label+")");
			}
			else{
				try{
					JFrame frame = new JFrame("InputDialog");
					Object[] choices = chartNumbers.toArray();
					int chartIndex = (Integer)JOptionPane.showInputDialog(frame,"To which Chart?","Chart Level Node",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
					String levelNodeIdRef = (String)JOptionPane.showInputDialog(null, "LevelNodeIdRef:", "new ChartLevelNode", JOptionPane.PLAIN_MESSAGE);
					String Label = (String)JOptionPane.showInputDialog(null, "Label:", "new ChartLevelNode", JOptionPane.PLAIN_MESSAGE);
					chart.get(chartIndex-1).createChartLevelNode(levelNodeIdRef, Label);
					JTextArea chartText = (JTextArea)chartPanels.get(chartIndex-1).getComponent(0);
					chartText.append("ChartLevelNode(LevelNodeIdRef: \""+levelNodeIdRef+"\", Label: \""+Label+")");

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
					chartPlanNodeNames.add(t.getName());
				}

				JFrame frame = new JFrame("InputDialog");
				Object[] choices = chartPlanNodeNames.toArray();
				String chosenPlanNode = (String)JOptionPane.showInputDialog(frame,"Which Plan Node Would you Like to add?","Chart PlanNode",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
				String Label = (String)JOptionPane.showInputDialog(null, "Label:", "new chart PlanNode", JOptionPane.PLAIN_MESSAGE);
				chart.get(0).createChartPlanNode(chosenPlanNode, Label);
				JTextArea chartText = (JTextArea)chartPanels.get(0).getComponent(0);				
				chartText.append("\n ChartPlanNode(planNodeIdRef: \""+chosenPlanNode+"\", Label: \""+Label+")");

				//display plan node and plannode increments too.
				String PlanNodeName = "";
				String PlanNodeId = "";
				for(PlanNode t: PlanNodes){
					if(t.getName()==chosenPlanNode){
						PlanNodeName = t.getName();
						PlanNodeId = t.getId();
					}
				}
				if(PlanNodeName!=""){
					chartText.append("\n PlanNode(Name: \""+PlanNodeName+"\", Id=\""+PlanNodeId+"\")");
				}

				for(PlanNode t: PlanNodes){
					if(t.getName()==chosenPlanNode){
						HashSet<PlanNodeIncrement> increments = t.getPlanNodeIncrements();
						for(PlanNodeIncrement increment: increments){
							chartText.append("\n\tPlanNodeIncrement(Id=\""+increment.getId()+"\", Length=\""+increment.getLength()+"\", Slope=\""+increment.getSlope()+"\")");
						}
					}
				}


			}
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
					String chosenPlanNode = (String)JOptionPane.showInputDialog(frame2,"Which Plan Node Would you Like to add?","Chart PlanNode",JOptionPane.PLAIN_MESSAGE,null,choices2,choices2[0]);
					String Label = (String)JOptionPane.showInputDialog(null, "Label:", "new chart PlanNode", JOptionPane.PLAIN_MESSAGE);
					chart.get(chartIndex-1).createChartPlanNode(chosenPlanNode, Label);
					JTextArea chartText = (JTextArea)chartPanels.get(chartIndex-1).getComponent(0);				
					chartText.append("\n ChartPlanNode(planNodeIdRef: \""+chosenPlanNode+"\", Label: \""+Label+")");

					//display plan node and plannode increments too.
					String PlanNodeName = "";
					String PlanNodeId = "";
					for(PlanNode t: PlanNodes){
						if(t.getName()==chosenPlanNode){
							PlanNodeName = t.getName();
							PlanNodeId = t.getId();
						}
					}
					if(PlanNodeName!=""){
						chartText.append("\n PlanNode(Name: \""+PlanNodeName+"\", Id=\""+PlanNodeId+"\")");
					}	

					for(PlanNode t: PlanNodes){
						if(t.getName()==chosenPlanNode){
							HashSet<PlanNodeIncrement> increments = t.getPlanNodeIncrements();
							for(PlanNodeIncrement increment: increments){
								chartText.append("\n\tPlanNodeIncrement(Id=\""+increment.getId()+"\", Length=\""+increment.getLength()+"\", Slope=\""+increment.getSlope()+"\")");
							}
						}
					}
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
			if(chartPanels.size()==1){
				String id = (String)JOptionPane.showInputDialog(null, "Id:", "new PlanNode", JOptionPane.PLAIN_MESSAGE);
				String name = (String)JOptionPane.showInputDialog(null, "Name:", "new PlanNode", JOptionPane.PLAIN_MESSAGE);
				try{
				Double startValue = Double.parseDouble(JOptionPane.showInputDialog(null, "Start Value:", "new PlanNode", JOptionPane.PLAIN_MESSAGE));

				chart.get(0).createPlanNode(id, name, startValue);
				JOptionPane.showMessageDialog(null, "Plan Node Successfully Added to Plan Node Library");
				}
				catch(Exception e3){
					JOptionPane.showMessageDialog(null, "Start Value must be in decimal format");
				}
			}
	/*		else{
				try{
					JFrame frame = new JFrame("InputDialog");
					Object[] choices = chartNumbers.toArray();
					int chartIndex = (Integer)JOptionPane.showInputDialog(frame,"To which Chart?","Chart Level Node",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);

					String id = (String)JOptionPane.showInputDialog(null, "Id:", "new PlanNode", JOptionPane.PLAIN_MESSAGE);
					String name = (String)JOptionPane.showInputDialog(null, "Name:", "new PlanNode", JOptionPane.PLAIN_MESSAGE);
					try{
					Double startValue = Double.parseDouble(JOptionPane.showInputDialog(null, "Start Value:", "new PlanNode", JOptionPane.PLAIN_MESSAGE));

					chart.get(chartIndex-1).createPlanNode(id, name, startValue);
					JOptionPane.showMessageDialog(null, "Plan Node Successfully Added to Chart"+ chartIndex+ "'s Plan Node Library");
					}
					catch(Exception e4){
						JOptionPane.showMessageDialog(null, "Start Value must be in decimal format");
					}


				}catch(Exception ex){

				}
			}
*/
			newChartPlanNodeAction.setEnabled(true);
			newPlanNodeIncrementAction.setEnabled(true);
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

			ArrayList<Integer> chartNumbers = new ArrayList<Integer>();
			for(int i=1;i<=chartPanels.size();i++){
				chartNumbers.add(i);
			}

			if(chartPanels.size()==1){
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
						}
					}
					JOptionPane.showMessageDialog(null, "PlanNodeIncrement Sucessfully created");
				}
				catch(Exception e2){
					JOptionPane.showMessageDialog(null, "Length and Slope must be in decimal format");
				}				

			}
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
			JTextArea newTextArea = new JTextArea(10,5);
			String name = JOptionPane.showInputDialog(null,"Chart Name:","Name",JOptionPane.PLAIN_MESSAGE);
			String id = JOptionPane.showInputDialog(null,"Chart Id: ","Id",JOptionPane.PLAIN_MESSAGE);
			String file = JOptionPane.showInputDialog(null,"Chart File","Chart File",JOptionPane.PLAIN_MESSAGE);
			String xAxisLabel = JOptionPane.showInputDialog(null,"X Axis Label","X Axis Label",JOptionPane.PLAIN_MESSAGE);
			String yAxisLabel = JOptionPane.showInputDialog(null,"Y Axis Label","Y Axis Label",JOptionPane.PLAIN_MESSAGE);
			
			chart.add(new ChartModel(name, id, file, xAxisLabel, yAxisLabel));

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
			panel2.add(newTextArea);
			panel2.setBorder(chartBorder);
			chartPanels.add(panel2);

			JScrollPane submodelScroll = new JScrollPane(chartPanels.get(chartPanels.size()-1));
			GraphNumber.setText(Integer.toString(chartPanels.size()));
			contentPanel.add(submodelScroll);
			if(chartPanels.size()>=4){
				contentPanel.setLayout(new GridLayout(2,4));
			}
			//force layout to recalculate now that a new component has been added.
			contentPanel.revalidate();
		}
	}
}

