/* ======================================================================================================
 * SystemDynamics: Java application for modeling, visualization and execution of System Dynamics models
 * ======================================================================================================
 *
 * (C) Copyright 2007-2008, Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 *
 * Project Info:  http://sourceforge.net/projects/system-dynamics
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */

package de.uka.aifb.com.systemDynamics.gui;

import de.uka.aifb.com.systemDynamics.SystemDynamics;
import de.uka.aifb.com.systemDynamics.model.*;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.RangeType;
import org.jfree.data.xy.*;
import org.jfree.ui.*;
import org.jfree.util.ArrayUtilities;

/*
 * Changes:
 * ========
 *
 * 2008-01-24: createPanel was rewritten: table column names internationalization
 */

/**
 * This class implements a panel for drawing the charts of the model execution.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.2
 */
public class ModelExecutionChartPanel extends JPanel implements FocusListener {
  
   private static final long serialVersionUID = 1L;
   
   private Locale locale;
   private ResourceBundle messages;
   private int submodelCounter;
   private NumberFormat integerNumberFormatter;
   
   private Model model;
   private LevelNode[] levelNodes;
   private SharedNode[] sharedLevelNodes;

   private String[] SelectedNames;
   private String[] SelectedNames2;
   
   private XYSeries[] xySeriesArray;

   private JFreeChart chart;
   private int nextRound;
   
   private JButton axesButton;
   private JButton executionButton;
   private JButton changeValueButton;
   public int submodelNumber;
   private NumberFormat formatter;
   
   private JPanel chartsPanel;


//private JFreeChart chart2;
   /**
    * Constructor.
    * 
    * @param start {@link de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param model {@link de.uka.aifb.com.systemDynamics.model.Model} instance
    */
   public ModelExecutionChartPanel(SystemDynamics start, Model model, int submodelCounter) {
      super(null);
      this.submodelCounter = submodelCounter;
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null");
      }
      if (model == null) {
         throw new IllegalArgumentException("'model' must not be null");
      }
      
      this.model = model;
      
      locale = start.getLocale();
      messages = start.getMessages();
      
      integerNumberFormatter = NumberFormat.getIntegerInstance(locale);
      formatter = DecimalFormat.getInstance();
      formatter.setMinimumFractionDigits(10);
      createPanel();
   }
   
   public Model getModel(){
	   return this.model;
   }
   
   
   /**
    * Gets the execution button.
    * 
    * @return execution button
    */
   public JButton getExecutionButton() {
      return executionButton;
   }
   
   /**
    * Creates panel.
    */
   private void createPanel() {
      setLayout(new BorderLayout());
      JFreeChart newchart = createChart();
      // CENTER: chart
      ChartPanel chartPanel = new ChartPanel(newchart);

      // no context menu
      chartPanel.setPopupMenu(null);

      // not zoomable
      chartPanel.setMouseZoomable(false);
      
      chartsPanel = new JPanel();
      JScrollPane scroll = new JScrollPane(chartsPanel);
      add(scroll, BorderLayout.CENTER);
      chartsPanel.setLayout(new GridLayout(0,1));
      chartsPanel.add(new JScrollPane(chartPanel));
      
      
      // LINE_END: series table
      JPanel tablePanel = new JPanel(new GridBagLayout());
      String[] columnNames = { messages.getString("ModelExecutionChartPanel.Table.ColumnNames.ExtraAxis"),
                               messages.getString("ModelExecutionChartPanel.Table.ColumnNames.LevelNode") };
      final MyTableModel tableModel = new MyTableModel(columnNames, xySeriesArray.length);
      for (int i = 0; i < xySeriesArray.length; i++) {
         tableModel.addEntry((String)xySeriesArray[i].getKey());
      }
      JTable table = new JTable(tableModel);
      table.setRowSelectionAllowed(false);
      JScrollPane tableScrollPane = new JScrollPane(table);
      int width = (int)Math.min(300, table.getPreferredSize().getWidth());
      int height = (int)Math.min(200, table.getPreferredSize().getHeight());
      tableScrollPane.getViewport().setPreferredSize(new Dimension(width, height));
      tableScrollPane.setMaximumSize(tableScrollPane.getViewport().getPreferredSize());
      axesButton = new JButton(messages.getString("ModelExecutionChartPanel.AxesButton.Text"));
      axesButton.setToolTipText(messages.getString("ModelExecutionChartPanel.AxesButton.ToolTipText"));
      axesButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            // create XYSeriesCollections (and renderer)
            XYSeriesCollection standardData = new XYSeriesCollection();
            XYLineAndShapeRenderer standardRenderer = new XYLineAndShapeRenderer(true, false);
            LinkedList<XYSeriesCollection> extraDataList = new LinkedList<XYSeriesCollection>();
            LinkedList<XYLineAndShapeRenderer> extraRendererList = new LinkedList<XYLineAndShapeRenderer>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
               if (tableModel.getValueAt(i, 0).equals(Boolean.FALSE)) {
                  standardData.addSeries(xySeriesArray[i]);
                  standardRenderer.setSeriesPaint(standardData.getSeriesCount() - 1, DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE[i % DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE.length]);
               } else {
                  // extra axis
                  XYSeriesCollection extraData = new XYSeriesCollection();
                  extraData.addSeries(xySeriesArray[i]);
                  extraDataList.add(extraData);
                  XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
                  extraRendererList.add(renderer);
                  renderer.setSeriesPaint(0, DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE[i % DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE.length]);
               }
            }
            LinkedList<XYSeriesCollection> dataList = new LinkedList<XYSeriesCollection>();
            LinkedList<XYLineAndShapeRenderer> rendererList = new LinkedList<XYLineAndShapeRenderer>();
            if (!standardData.getSeries().isEmpty()) {
               dataList.add(standardData);
               rendererList.add(standardRenderer);
            }
            for (XYSeriesCollection data : extraDataList) {
               dataList.add(data);
            }
            for (XYLineAndShapeRenderer renderer : extraRendererList) {
               rendererList.add(renderer);
            }
            
            // creates axes
            LinkedList<NumberAxis> axesList = new LinkedList<NumberAxis>();
            if (!standardData.getSeries().isEmpty()) {
               NumberAxis axis = new NumberAxis(messages.getString("ModelExecutionChartPanel.Value"));
               axis.setNumberFormatOverride(NumberFormat.getInstance(locale));
               axesList.add(axis);
            }
            for (XYSeriesCollection data : extraDataList) {
               NumberAxis axis = new NumberAxis((String)data.getSeries(0).getKey());
               axis.setNumberFormatOverride(NumberFormat.getInstance(locale));
               axesList.add(axis);
            }
            
            // store data and axes in plot
            XYPlot plot = chart.getXYPlot();
            plot.clearRangeAxes();
            plot.setRangeAxes(axesList.toArray(new NumberAxis[0]));
            for (int i = 0; i < plot.getDatasetCount(); i++) {
               plot.setDataset(i, null);
            }
            int datasetIndex = 0;
            Iterator<XYSeriesCollection> datasetIterator = dataList.iterator();
            Iterator<XYLineAndShapeRenderer> rendererIterator = rendererList.iterator();
            while (datasetIterator.hasNext()) {
               plot.setDataset(datasetIndex, datasetIterator.next());
               plot.setRenderer(datasetIndex, rendererIterator.next());
               datasetIndex++;
            }
            for (int i = 0; i < plot.getDatasetCount(); i++) {
               plot.mapDatasetToRangeAxis(i, i);
            }
         }
      });
      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.CENTER;
      c.gridx = 0;
      c.gridy = 0;
      c.insets = new Insets(0, 0, 10, 0);
      tablePanel.add(tableScrollPane, c);
      c.gridx = 0;
      c.gridy = 1;
      tablePanel.add(axesButton, c);
      add(tablePanel, BorderLayout.LINE_END);
      
      // PAGE_END: number of rounds and execution button
      JPanel commandPanel = new JPanel();
      JLabel label = new JLabel (messages.getString("ModelExecutionChartPanel.NumberRounds"));
      label.setVisible(false);
      commandPanel.add(label);
      final JTextField numberRoundsField = new JTextField("1", 5);
      
      //Giving people the ability to change LevelNode values
      HashSet<LevelNode> LevelNodes = model.getLevelNodes();
      HashSet<ConstantNode> ConstantNodes = model.getConstantNodes();
      HashSet<SharedNode> sharedNodesModel = model.getSharedNodes();
      
      final ArrayList<JTextField> LevelNodeChangeFields = new ArrayList<JTextField>();
      final ArrayList<JLabel> LevelNodeLabels = new ArrayList<JLabel>();
      final ArrayList<LevelNode> LearnerChangeableNodes = new ArrayList<LevelNode>();
      final ArrayList<SharedNode> sharedNodes = new ArrayList<SharedNode>();
      for(SharedNode shared:sharedNodesModel){
    	  sharedNodes.add(shared);
      }
      for(LevelNode level:LevelNodes) {
    	  if(level.getLearnerChangeable()) {
    		  JTextField newField = new JTextField(Double.toString(level.getCurrentValue()), 10);
    		  Color pastelYellow = new Color(255,253,16);
    		  newField.setBackground(pastelYellow);
    		  LevelNodeChangeFields.add(newField);
    		  LevelNodeLabels.add(new JLabel(level.getNodeName()));
    		  LearnerChangeableNodes.add(level);
    	  }
      }
      
      final ArrayList<JTextField> ConstantNodeChangeFields = new ArrayList<JTextField>();
      final ArrayList<JLabel> ConstantNodeLabels = new ArrayList<JLabel>();
      final ArrayList<ConstantNode> LearnerChangeableConstantNodes = new ArrayList<ConstantNode>();
      for(ConstantNode constant:ConstantNodes) {
    	  if(constant.getLearnerChangeable()) {
    		  ConstantNodeChangeFields.add(new JTextField(Double.toString(constant.getCurrentValue()), 10));
    		  ConstantNodeLabels.add(new JLabel(constant.getNodeName()));
    		  LearnerChangeableConstantNodes.add(constant);
    	  }
      }
      
      numberRoundsField.addFocusListener(this);
      commandPanel.add(numberRoundsField);
      for(int i=0;i<LevelNodeChangeFields.size();i++) {
    	  commandPanel.add(LevelNodeLabels.get(i));
    	  commandPanel.add(LevelNodeChangeFields.get(i));
      }
      for(int i=0;i<ConstantNodeChangeFields.size();i++) {
    	  commandPanel.add(ConstantNodeLabels.get(i));
    	  commandPanel.add(ConstantNodeChangeFields.get(i));
      }
      
      changeValueButton = new JButton("Change node values");
      changeValueButton.addActionListener(new ActionListener() {
    	 public void actionPerformed(ActionEvent e) {
    		 for(int i=0;i<LearnerChangeableNodes.size();i++) {
    			 try {    				 
    				 model.setStartValue(LearnerChangeableNodes.get(i), Double.parseDouble(LevelNodeChangeFields.get(i).getText()));
    				 if(sharedNodes.size()!=0){
    					 model.setSharedValue(sharedNodes.get(i), Double.parseDouble(LevelNodeChangeFields.get(i).getText()));
    				 }
    				 JOptionPane.showMessageDialog(null, "Value change for Level Nodes completed");	
    			 }
    			 catch(Exception e1) {
    				 JOptionPane.showMessageDialog(null, e1);
    				 break;
    			 }
    		 }
    		 for(int i=0;i<LearnerChangeableConstantNodes.size();i++) {
    			 try {
    				 model.setConstantValue(LearnerChangeableConstantNodes.get(i), Double.parseDouble(ConstantNodeChangeFields.get(i).getText()));
    				 model.setSharedValue(sharedNodes.get(i), Double.parseDouble(ConstantNodeChangeFields.get(i).getText()));
    				 JOptionPane.showMessageDialog(null, "Value change for Constant Nodes completed");
    				 
    			 }
    			 catch(Exception e2) {
    				 JOptionPane.showMessageDialog(null, e2);
    				 break;
    			 }
    		 }
    		 
    	 }
      });
//      commandPanel.add(changeValueButton);
      executionButton = new JButton(messages.getString("ModelExecutionChartPanel.ExecutionButton.Text"));
      executionButton.setToolTipText(messages.getString("ModelExecutionChartPanel.ExecutionButton.ToolTipText"));
      executionButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            int numberRounds = 0;
            boolean correctNumber = false;
            try {
               numberRounds = integerNumberFormatter.parse(numberRoundsField.getText()).intValue();
            } catch (ParseException parseExcep) {
               // do nothing
            }
            
            if (numberRounds >= 1) {
               correctNumber = true;
            }
            
            if (correctNumber) {
            	// execute using thread
               ModelExecutionThread executionThread = new ModelExecutionThread(numberRounds);
               executionThread.start(); 
               try {
            	   executionThread.join();
               } catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
               }
            	
//            	execute(numberRounds);
            } else {
               JOptionPane.showMessageDialog(null,
                                             messages.getString("ModelExecutionChartPanel.Error.Message"),
                                             messages.getString("ModelExecutionChartPanel.Error.Title"),
                                             JOptionPane.ERROR_MESSAGE);
            }
         }
         
         private void execute(int numberRounds ){
        	 NumberFormat numberFormatter = NumberFormat.getIntegerInstance(locale);
             executionButton.setEnabled(false);
             setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
             
             ProgressMonitor progressMonitor = new ProgressMonitor(ModelExecutionChartPanel.this,
                                                                   messages.getString("ModelExecutionThread.ProgressMonitor.Text"),
                                                                   "",
                                                                   0, numberRounds);
             for (int i = 0; i < numberRounds; i++) {
                if (progressMonitor.isCanceled()) {
                   // stop execution (i.e. for loop)
                   break;
                }
                progressMonitor.setNote(numberFormatter.format(i) + " " + messages.getString("ModelExecutionThread.ProgressMonitor.Note.Text1") + " " + numberFormatter.format(numberRounds) + " " + messages.getString("ModelExecutionThread.ProgressMonitor.Note.Text2"));
                progressMonitor.setProgress(i);
                
                for(SharedNode sn: model.getSharedLevelNodes()){
                	sn.setCurrentValueFromExecutionCache(nextRound);
                }
                
                model.computeNextValues();
                
                
                for (int j = 0; j < xySeriesArray.length; j++) {              
                	if(j<levelNodes.length){
                		xySeriesArray[j].add(nextRound, levelNodes[j].getCurrentValue());
                	}
                	else {
                		xySeriesArray[j].add(nextRound, sharedLevelNodes[j-levelNodes.length].getCurrentValue());
                	}
                }
                nextRound++;
             }
             progressMonitor.close();
             
             setCursor(null);
             executionButton.setEnabled(true);
          }
         
      });
      commandPanel.add(executionButton);
      add(commandPanel, BorderLayout.PAGE_END);
   }
   
   /**
    * Create a new chart above the original one when changing the levelnode value
    * 
    * @return
    */
   public void addNewChartPanel(String name){

	      JFreeChart newchart = createChartWithoutDialog(name);
	      // CENTER: chart
	      ChartPanel chartPanel = new ChartPanel(newchart);
	      
	      // no context menu
	      chartPanel.setPopupMenu(null);
	      
	      // not zoomable
	      chartPanel.setMouseZoomable(false);
	      
	      chartsPanel.add(chartPanel);
	      
   }
   
   private JFreeChart createChartWithoutDialog(String name){

//	      
//	      int i = 0;
//	      int f = 0;
//	      
//	      String[] levelNodeList = new String[model.getLevelNodes().size()];
//	      String[] sharedLevelNodeList = new String[model.getSharedNodes().size()];
//	      
//	      int k=0;
//	      int t=0;
//	      for (LevelNode levelNode : model.getLevelNodes()) {
//	    	  levelNodeList[k++] = levelNode.getNodeName();
//	      }
//	      for(SharedNode sharedNode: model.getSharedLevelNodes()){
//	    	  sharedLevelNodeList[t++] = sharedNode.getSharedPointer();
//	      }
//	      
//	      HashMap<LevelNode,HashSet<AbstractNode>> dependencies = new HashMap<LevelNode,HashSet<AbstractNode>>();
//	      int j=0;
//	      int z=0;
//	      
//	      for (LevelNode levelNode : model.getLevelNodes()) {
//	    		 if(name == levelNode.getNodeName()){
//	    			 getAllDependencyNodes(levelNode,dependencies, new HashSet<AbstractNode>());
//	    			 levelNodes[i++] = levelNode;
//	    			 
//	    		 }
//	      }
//	      
//	      for(SharedNode sharedNode : model.getSharedNodes()){
//	    		  if(name == sharedNode.getSharedPointer()){
//	    			  sharedLevelNodes[f++] = sharedNode;
//	    		  }
//	      }
//
//
	   

//		for(LevelNode ln: model.getLevelNodes()){
//			ln.reset();
//		}
//
//		for(SharedNode sn: model.getSharedLevelNodes()){
//	    	sn.clearExecutionCache();
//	    }
		
	      // sort level nodes alphabetically
	      
	      xySeriesArray = new XYSeries[levelNodes.length+sharedLevelNodes.length];
	      
	      XYSeriesCollection data = new XYSeriesCollection();
	      
	      for (int i = 0; i < levelNodes.length; i++) {
	    	 
	         XYSeries xySeries = new XYSeries(levelNodes[i].getNodeName());
	         xySeries.add(0.0, levelNodes[i].getStartValue());
	         data.addSeries(xySeries);
	         xySeriesArray[i] = xySeries;
	      }
	      for (int i = 0; i < sharedLevelNodes.length; i++) {
	     	 
	          XYSeries xySeries = new XYSeries(sharedLevelNodes[i].getSharedPointer());
	          xySeries.add(0.0, ((LevelNode)sharedLevelNodes[i].getSource()).getStartValue());
	          data.addSeries(xySeries);
	          xySeriesArray[i+levelNodes.length] = xySeries;
	       }
	      
	      nextRound = 1;
	      
	      chart = ChartFactory.createXYLineChart(null,
	                                             messages.getString("ModelExecutionChartPanel.Round"), 
	                                             messages.getString("ModelExecutionChartPanel.Value"), 
	                                             data,
	                                             PlotOrientation.VERTICAL,
	                                             true, true, false);
	      XYPlot plot = chart.getXYPlot();
	      
	      
	      // add tooltip to each point
	      XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
	      renderer.setBaseToolTipGenerator(
	          new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,new DecimalFormat("0.0"),new DecimalFormat("0.0000000000")));
	      
//	      String text = getDependencies(dependencies);
	      
	      
//	      TextTitle legendText = new TextTitle(text);
//	      legendText.setPosition(RectangleEdge.TOP);
//	      chart.addSubtitle(legendText);
	      
	      
	      // horizontal axis range: 0 ... maximal rounds
	      ((NumberAxis)(chart.getXYPlot().getDomainAxis())).setRangeType(RangeType.POSITIVE);
	      plot.getDomainAxis().setAutoRangeMinimumSize(20);
	      
	      // only integer values as labels for horizontal axis
	      plot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	      
	      // number formatting according to current locale
	      ((NumberAxis)(plot.getDomainAxis())).setNumberFormatOverride(NumberFormat.getIntegerInstance(locale));
	      ((NumberAxis)(plot.getRangeAxis())).setNumberFormatOverride(NumberFormat.getInstance(locale));
	      chart.getLegend().setPosition(RectangleEdge.RIGHT);
	      return chart;
   }
   
   /**
    * Creates the XY line chart.
    * 
    * @return XY line chart
    */
   private JFreeChart createChart() {
      
      
      int i = 0;
      String[] levelNodeList = new String[model.getLevelNodes().size()];
      String[] sharedLevelNodeList = new String[model.getSharedNodes().size()];
      
      int k=0;
      int t=0;
      for (LevelNode levelNode : model.getLevelNodes()) {
    	  levelNodeList[k++] = levelNode.getNodeName();
      }
      for(SharedNode sharedNode: model.getSharedLevelNodes()){
    	  sharedLevelNodeList[t++] = sharedNode.getSharedPointer();
      }
      
      Arrays.sort(levelNodeList);
      Arrays.sort(sharedLevelNodeList);
      
      JPanel listPanel = new JPanel();
      JList list = new JList(levelNodeList);
      JList list2 = new JList(sharedLevelNodeList);
      
      
      
      GridLayout layout = new GridLayout(3,1);
      listPanel.setLayout(layout);
      JLabel label = new JLabel("which variables would you like to graph? (Submodel " + this.submodelCounter + ")");
      JScrollPane scrollPane = new JScrollPane(list);
      JScrollPane scrollPane2 = new JScrollPane(list2);
      listPanel.add(label);
      listPanel.add(scrollPane);
      listPanel.add(scrollPane2);
      JOptionPane.showMessageDialog(
        null, listPanel, "Submodel", JOptionPane.PLAIN_MESSAGE);
      
      System.out.println(Arrays.toString(list.getSelectedIndices()));
      
      levelNodes = new LevelNode[list.getSelectedIndices().length];
      sharedLevelNodes = new SharedNode[list2.getSelectedIndices().length];
      SelectedNames = new String[list.getSelectedIndices().length];
      SelectedNames2 = new String[list2.getSelectedIndices().length];
      HashMap<LevelNode,HashSet<AbstractNode>> dependencies = new HashMap<LevelNode,HashSet<AbstractNode>>();
      int j=0;
      int z=0;
      for (int index : list.getSelectedIndices()){
    	 SelectedNames[j++] = levelNodeList[index]; 
      }
      for(int index2 : list2.getSelectedIndices()){
    	  SelectedNames2[z++] = sharedLevelNodeList[index2];
      }
      for (LevelNode levelNode : model.getLevelNodes()) {
    	 for(int f=0;f<SelectedNames.length;f++){
    		 if(SelectedNames[f] == levelNode.getNodeName()){
    			 getAllDependencyNodes(levelNode,dependencies, new HashSet<AbstractNode>());
    			 levelNodes[i++] = levelNode;
    			 
    		 }
    	 }
      }
      for(SharedNode sharedNode : model.getSharedNodes()){
    	  for(int f=0;f<SelectedNames2.length;f++){
    		  if(SelectedNames2[f] == sharedNode.getSharedPointer()){
    			  sharedLevelNodes[f++] = sharedNode;
    		  }
    	  }
      }

      
      // sort level nodes alphabetically
      
      xySeriesArray = new XYSeries[levelNodes.length+sharedLevelNodes.length];
      
      XYSeriesCollection data = new XYSeriesCollection();
      
      for (i = 0; i < levelNodes.length; i++) {
    	 
         XYSeries xySeries = new XYSeries(levelNodes[i].getNodeName());
         xySeries.add(0.0, levelNodes[i].getStartValue());
         data.addSeries(xySeries);
         xySeriesArray[i] = xySeries;
      }
      for (i = 0; i < sharedLevelNodes.length; i++) {
     	 
          XYSeries xySeries = new XYSeries(sharedLevelNodes[i].getSharedPointer());
          xySeries.add(0.0, ((LevelNode)sharedLevelNodes[i].getSource()).getStartValue());
          data.addSeries(xySeries);
          xySeriesArray[i+levelNodes.length] = xySeries;
       }
      
      nextRound = 1;
      
      chart = ChartFactory.createXYLineChart(null,
                                             messages.getString("ModelExecutionChartPanel.Round"), 
                                             messages.getString("ModelExecutionChartPanel.Value"), 
                                             data,
                                             PlotOrientation.VERTICAL,
                                             true, true, false);
      XYPlot plot = chart.getXYPlot();
      
      
      // add tooltip to each point
      XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
      renderer.setBaseToolTipGenerator(
          new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,new DecimalFormat("0.0"),new DecimalFormat("0.0000000000")));
      
      String text = getDependencies(dependencies);
      
      
      TextTitle legendText = new TextTitle(text);
      legendText.setPosition(RectangleEdge.TOP);
      chart.addSubtitle(legendText);
      
      
      // horizontal axis range: 0 ... maximal rounds
      ((NumberAxis)(chart.getXYPlot().getDomainAxis())).setRangeType(RangeType.POSITIVE);
      plot.getDomainAxis().setAutoRangeMinimumSize(20);
      
      // only integer values as labels for horizontal axis
      plot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
      
      // number formatting according to current locale
      ((NumberAxis)(plot.getDomainAxis())).setNumberFormatOverride(NumberFormat.getIntegerInstance(locale));
      ((NumberAxis)(plot.getRangeAxis())).setNumberFormatOverride(NumberFormat.getInstance(locale));
      chart.getLegend().setPosition(RectangleEdge.RIGHT);
      return chart;
   }
   

   
   private String getDependencies(HashMap<LevelNode,HashSet<AbstractNode>> m){

	   String dependency = "Dependency :   ";
	   for (Entry<LevelNode, HashSet<AbstractNode>> entry : m.entrySet()) {
		   dependency += entry.getKey().getNodeName();
		   dependency += " -> ";
		   for(AbstractNode node: entry.getValue()){
		   
			   dependency += node.getNodeName();
			   dependency += " ";
			   boolean isIncreasing = false;
			   if(node instanceof LevelNode){
				   isIncreasing = !(((LevelNode)node).isIncreasing() ^ ((LevelNode)entry.getKey()).isIncreasing());
			   }
			   else{

				   isIncreasing = ((ConstantNode)node).isIncreasing();
			   }
			   if(isIncreasing)
				   dependency += "(+)";
			   else
				   dependency += "(-)";
			   dependency += " ";
		   }
		   dependency += "  |  ";
	   }
	   
	   return dependency;
   }
   
   private void getAllDependencyNodes(LevelNode ln, HashMap<LevelNode,HashSet<AbstractNode>> m, HashSet<AbstractNode> visitedLN){
	   if(m.containsKey(ln))
		   return;
	   
	   if(visitedLN.contains(ln))
		   return;
	   visitedLN.add(ln);
	   
	   HashSet<AbstractNode> dependencyNodes = new HashSet<AbstractNode>();
	   
	   HashSet<RateNode> dependencyRateNodes = ln.getIncomingFlows();   
	   for(RateNode rn: dependencyRateNodes){
		   /*AbstractNode source  = rn.getFlowSource();
		   if(source instanceof LevelNode){
			   if(!dependencyNodes.contains(source)){
				   getAllDependencyNodes((LevelNode)source, m);
				   dependencyNodes.add(source);
			   }
		   }*/
		   HashSet<AbstractNode> nodes = rn.getAllNodesThisOneDependsOn();
		   for(AbstractNode node: nodes){
			   if(node instanceof ConstantNode){
				   dependencyNodes.add(node);
			   }
			   else if(node instanceof AuxiliaryNode){
				   AuxiliaryNode an = (AuxiliaryNode) node;
				   HashSet<AbstractNode> dependencyNodesOfAuxiliaryNodes = an.getAllNodesThisOneDependsOn();
				   for(AbstractNode n: dependencyNodesOfAuxiliaryNodes){
					   if(n instanceof ConstantNode){
						   dependencyNodes.add(n);
					   }
					   else if(node instanceof LevelNode){
							   dependencyNodes.add(node);
						   if(!visitedLN.contains(node)){
							   getAllDependencyNodes((LevelNode)node,m,visitedLN);
	//						   dependencyNodes.addAll(m.get(node));
						   }
					   }
				   }
			   }
			   else if(node instanceof LevelNode){
					   dependencyNodes.add(node);
				   if(!visitedLN.contains(node) || node == ln){
					   getAllDependencyNodes((LevelNode)node,m,visitedLN);
				   }
//				   dependencyNodes.addAll(m.get(node));
				   
			   }
		   }
	   }
	   m.put(ln, dependencyNodes);
	   
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                             methods of interface FocusListener
////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Performs a gained focus event.
    * 
    * @param e event
    */
   public void focusGained(FocusEvent e) {
      Component c = e.getComponent();
      if (c instanceof JTextField) {
         ((JTextField)c).selectAll();
      }
   }

   /**
    * Performs a lost focus event.
    * 
    * @param e event
    */
   public void focusLost(FocusEvent e) {
      // do nothing
   }
   
   /**
    * This inner class implements a table model with a boolean and a String column.
    */
   private class MyTableModel extends AbstractTableModel {
      
      private static final long serialVersionUID = 1L;
      
      private String[] columnNames;
      private Object[][] data;
      private int nextRow;
      
      /**
       * Constructor.
       * 
       * @param columnNames column names
       * @param numberRows number of rows
       */
      private MyTableModel(String[] columnNames, int numberRows) {
         if (columnNames == null) {
            throw new IllegalArgumentException("'columnNames' must not be null.");
         }
         
         this.columnNames = columnNames;
         data = new Object[numberRows][columnNames.length];
      }
      
      /**
       * Adds the specified entry (with boolean value <code>false</close> to the next free row.
       * 
       * @param entryName entry name
       */
      private void addEntry(String entryName) {
         if (entryName == null) {
            throw new IllegalArgumentException("'entryName' must not be null.");
         }
         
         data[nextRow][0] = false;
         data[nextRow][1] = entryName;
         
         nextRow++;
      }

      /**
       * Gets the number of columns.
       * 
       * @return number of columns
       */
      public int getColumnCount() {
         return columnNames.length;
      }

      /**
       * Gets the number of rows.
       * 
       * @return number of rows
       */
      public int getRowCount() {
         return data.length;
      }

      /**
       * Gets the column name of the specified column.
       * 
       * @param col column index
       * @return column name
       */
      @Override
	public String getColumnName(int col) {
         return columnNames[col];
      }

      /**
       * Gets the value stored at the specified position.
       * 
       * @param row row index
       * @param col column index
       * @return stored value
       */
      public Object getValueAt(int row, int col) {
         return data[row][col];
      }
      
      /**
       * Sets the specified value at the given position.
       * 
       * @param value value to store
       * @param row row index
       * @param col column index
       */
      @Override
	public void setValueAt(Object value, int row, int col) {
         if (col == 0) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
         }
      }

      /**
       * Gets the <code>Class</code> type of the specified column.
       * 
       * @param c column index
       * @return <code>Class</code> type
       */
      @Override
	public Class getColumnClass(int c) {
         if (c < 0 || c > 1) {
            throw new ArrayIndexOutOfBoundsException();
         }
          
         if (c == 0) {
            return Boolean.class;
         } else {
            return String.class;
         }
      }
      
      /**
       * Checks whether the specified cell is editable.
       * 
       * @param row row index
       * @param col column index
       * @return <code>true</code> iff the specified cell is editable
       */
      @Override
	public boolean isCellEditable(int row, int col) {
         return (col == 0);
      }
   }
   
   /**
    * This inner class implements a thread for model execution within the swing GUI.
    */
   private class ModelExecutionThread extends Thread {
      
      private int numberRounds;
      
      /**
       * Constructor.
       * 
       * @param numberRounds number of rounds to execute
       */
      private ModelExecutionThread(int numberRounds) {
         if (numberRounds < 1) {
            throw new IllegalArgumentException("'numberRounds' must be at least 1.");
         }
         
         this.numberRounds = numberRounds;
      }
      
      /**
       * Executes the model for the specified number of rounds. A progress monitor shows progress
       * information.
       */
      @Override
	public void run() {
         NumberFormat numberFormatter = NumberFormat.getIntegerInstance(locale);
         executionButton.setEnabled(false);
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         
         ProgressMonitor progressMonitor = new ProgressMonitor(ModelExecutionChartPanel.this,
                                                               messages.getString("ModelExecutionThread.ProgressMonitor.Text"),
                                                               "",
                                                               0, numberRounds);
         for (int i = 0; i < numberRounds; i++) {
            if (progressMonitor.isCanceled()) {
               // stop execution (i.e. for loop)
               break;
            }
            progressMonitor.setNote(numberFormatter.format(i) + " " + messages.getString("ModelExecutionThread.ProgressMonitor.Note.Text1") + " " + numberFormatter.format(numberRounds) + " " + messages.getString("ModelExecutionThread.ProgressMonitor.Note.Text2"));
            progressMonitor.setProgress(i);
            
            for(SharedNode sn: model.getSharedLevelNodes()){
            	sn.setCurrentValueFromExecutionCache(nextRound);
            }
            
            model.computeNextValues();
            
            
            for (int j = 0; j < xySeriesArray.length; j++) {              
            	if(j<levelNodes.length){
            		xySeriesArray[j].add(nextRound, levelNodes[j].getCurrentValue());
            	}
            	else {
            		xySeriesArray[j].add(nextRound, sharedLevelNodes[j-levelNodes.length].getCurrentValue());
            	}
            }
            nextRound++;
         }
         progressMonitor.close();
         
         setCursor(null);
         executionButton.setEnabled(true);
      }
   }

public void reset() {
	nextRound=1;
	
	for(LevelNode ln: model.getLevelNodes()){
		ln.reset();
	}
//	for(int i=0;i<levelNodes.length;i++){
//		levelNodes[i].reset();
//	}

	for(SharedNode sn: model.getSharedLevelNodes()){
    	sn.clearExecutionCache();
    }
	
	for (int j = 0; j < levelNodes.length; j++) {
        xySeriesArray[j].clear();
        xySeriesArray[j].add(0.0, levelNodes[j].getStartValue());
     }
	
	for (int j = levelNodes.length; j < xySeriesArray.length; j++) {
        xySeriesArray[j].clear();
        xySeriesArray[j].add(0.0, ((LevelNode)sharedLevelNodes[j-levelNodes.length].getSource()).getStartValue());
     }
}

public SharedNode[] getSharedLevelNodes() {
	
	return sharedLevelNodes;
}

   
//   private class LineLegendItemSource implements LegendItemSource {
//	    public LegendItemCollection getLegendItems() {
//	        LegendItemCollection itemCollection = new LegendItemCollection();
//	        for (Comparable comparable : legendKeys) {
//	           Paint paint = // get the paint you want
//	           LegendItem item = new LegendItem("string to display", 
//	                                            "description", 
//	                                            "tooltip", 
//	                                            "url", 
//	                                            new Line2D.Double(0, 5, 10, 5), paint);
//	           itemCollection.add(item);
//	        }
//	        return itemCollection; 
//	     }
//	   }
}