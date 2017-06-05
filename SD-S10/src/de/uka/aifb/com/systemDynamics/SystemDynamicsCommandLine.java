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

package de.uka.aifb.com.systemDynamics;

import de.uka.aifb.com.systemDynamics.csv.CSVExport;
import de.uka.aifb.com.systemDynamics.gui.ModelExecutionChartPanel;
import de.uka.aifb.com.systemDynamics.model.*;
import de.uka.aifb.com.systemDynamics.xml.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This class implements a command line command for model execution and value export.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @author Pradeep Jawahar Georgia Institute of Technology
 * @author Subbu Ramanathan Georgia Institute of Technology
 * @author Gopi Krishnan Nambiar Georgia Institute of Technology
 * @version 2.0
 */
public class SystemDynamicsCommandLine {

	public static String xmlModelFileName;
	public static int numberRounds;
	public static boolean exportCSV;
	public static String exportFileName;

	/**
	 * Constructor.
	 * 
	 * @param xmlModelFileName XML model file name
	 * @param numberRounds number of rounds to execute
	 * @param exportCSV export as CSV? (otherwise as XML)
	 * @param exportFileName export file name
	 */
	public SystemDynamicsCommandLine(String xmlModelFileName, int numberRounds, boolean exportCSV,
			String exportFileName) {
		if (xmlModelFileName == null) {
			throw new IllegalArgumentException("'xmlModelFileName' must not be null.");
		}
		if (numberRounds < 1) {
			throw new IllegalArgumentException("'numberRounds' must be at least 1.");
		}
		if (exportFileName == null) {
			throw new IllegalArgumentException("'exportFileName' must not be null.");
		}

		SystemDynamicsCommandLine.xmlModelFileName = xmlModelFileName;
		SystemDynamicsCommandLine.numberRounds = numberRounds;
		SystemDynamicsCommandLine.exportCSV = exportCSV;
		SystemDynamicsCommandLine.exportFileName = exportFileName;
	}

	/**
	 * Executes model and exports values.
	 */
	public HashMap<String,String> executeCommand() {
		HashMap<String,String> levelNodeMap = new HashMap<String,String>();
		// (1) check if export file already exists
		if (new File(exportFileName).exists()) {
			System.out.println("ERROR: Export file already exists.");
			System.exit(1);
		}
		// (2) read model from XML file
		ArrayList<Model> model = null;
		try {
			model = XMLModelReader.readXMLModel(xmlModelFileName);
			
			levelNodeMap = XMLModelReader.getLevelNodes(); 
		} catch (AuxiliaryNodesCycleDependencyException e) {
			System.out.println("ERROR: Invalid XML file: The model's auxiliary nodes have a cycle dependency.");
			System.exit(1);
		} catch (XMLModelReaderWriterException e) {
			System.out.println("ERROR: An error occured during reading from the XML file: " + e.getException().getMessage());
			System.exit(1);
		} catch (XMLNodeParameterOutOfRangeException e) {
			System.out.println("ERROR: Invalid XML file: A parameter of the node with the ID " + e.getXMLNodeId() + "is out of range.");
			System.exit(1);
		} catch (XMLRateNodeFlowException e) {
			System.out.println("ERROR: Invalid XML file: The rate node with the ID" + e.getXMLNodeId() + "has no incoming or no outgoing flow.");
			System.exit(1);
		} catch (XMLUselessNodeException e) {
			System.out.println("ERROR: Invalid XML file: The node with the ID" + e.getXMLNodeId() + "is useless for the model.");
			System.exit(1);
		}

		System.out.println("Model successfully read from XML file.");
		
		
				
		// (3) validate model and set unchangeable
		
		ArrayList<String> headers = new ArrayList<String>();
		ArrayList<double[]> values = new ArrayList<double[]>();
		
		int numberOfLevelNodes = 0;
		int [] start = new int[model.size()];
		
		for(int i=0;i<model.size();i++){
			start[i]=numberOfLevelNodes;
			numberOfLevelNodes+=model.get(i).getLevelNodes().size();
		}
		
		for(int k=0;k<numberRounds+1;k++){
			values.add(new double[numberOfLevelNodes]);
		}
		
		ArrayList<Model> orderedModels = executeInOrder(model);
		
		
		for(int k=0;k<orderedModels.size();k++){
			try {
				orderedModels.get(k).validateModelAndSetUnchangeable(0);
			} catch (Exception e) {
				// There should be no exceptions here because the model was already validated while
				// reading from XML file.
				System.out.println("ERROR: Model is not valide.");
				System.exit(1);
			}

			// (4) execute model and export values
			LevelNode[] levelNodes = orderedModels.get(k).getLevelNodes().toArray(new LevelNode[0]);
			// sort level nodes alphabetically

			Arrays.sort(levelNodes);
			
			for(int i=0;i<levelNodes.length;i++){
//				if(i==0)
					headers.add("SM"+(model.indexOf(orderedModels.get(k))+1)+":"+levelNodes[i].getNodeName());
		        	System.out.println("*************** execute values "+levelNodes[i].getNodeName()+" "+levelNodes[i].getStartValue()+" "+levelNodes[i].getCurrentValue());
//				else
//					headers.add(levelNodes[i].getNodeName());
			}
			
			
			for (int j = 0; j < levelNodes.length; j++) {
				values.get(0)[start[model.indexOf(orderedModels.get(k))]+j]=levelNodes[j].getCurrentValue();
			}
			
			for (int i = 0; i < numberRounds; i++) {
				
				for(SharedNode sn: orderedModels.get(k).getSharedLevelNodes()){
	            	sn.setCurrentValueFromExecutionCache(i+1);
	            }
				orderedModels.get(k).computeNextValues();
				
				for (int j = 0; j < levelNodes.length; j++) {
					values.get(i+1)[start[model.indexOf(orderedModels.get(k))]+j]=levelNodes[j].getCurrentValue();
		        	System.out.println("*************** compute next values "+levelNodes[j].getNodeName()+" "+levelNodes[j].getStartValue()+" "+levelNodes[j].getCurrentValue());
				}

				System.out.println("&&&&&&&&&&&&  "+values.get(i+1).length);
			}
			

			System.out.println();
			System.out.println("Export finished successfully.");
//			return levelNodeMap;
		}
			
		Collections.sort(headers);
		
		try {
			if (exportCSV) {
				// (2a) CSV export
//				String[] columnNames = new String[levelNodes.length];
//				for (int i = 0; i < columnNames.length; i++) {
//					columnNames[i] = ;
//				}
				CSVExport csvExport =  new CSVExport(exportFileName, model.get(0).getModelName(), headers.toArray(new String[0]));
				
				System.out.println("EXPORTERRORPOTENTIAL");
//				csvExport.writeComment(CSVExport.COMMENT_START_SYMBOL+"SubModel ID : "+model.get(k).getModelID());
				csvExport.writeColumns(headers.toArray(new String[0]));
				System.out.println("AFTEREXPORTERRORPOTENTIAL");
				int percent = 0;
				System.out.print("Export: 00%");

//				double[] values = new double[levelNodes.length];
//				for (int j = 0; j < values.length; j++) {
//					values[j] = levelNodes[j].getCurrentValue();
//				}
				
				for(int i=0;i<values.size();i++){
					System.out.println("&&&&&&&&&&&&&&&&&&&&&&    "+values.get(i).length);
					csvExport.write(values.get(i));}
				csvExport.close();
//				for (int i = 0; i < numberRounds; i++) {
//					int newPercent = 100 * i / numberRounds;
//					if (newPercent > percent) {
//						percent = newPercent;
//						// Backspaces do not work within Eclipse IDE - but on normal console!
//						System.out.print("\b\b\b");
//						if (percent < 10) {
//							System.out.print("0");
//						}
//						System.out.print(percent + "%");
//					}
//					model.get(k).computeNextValues();
//					values = new double[levelNodes.length];
//					for (int j = 0; j < values.length; j++) {
//						values[j] = levelNodes[j].getCurrentValue();
//					}
//					csvExport.write(values);
//				}
			} else {
				// (2b) XML export
//				String[] nodeNames = new String[levelNodes.length];
//				for (int i = 0; i < nodeNames.length; i++) {
//					nodeNames[i] = levelNodes[i].getNodeName();
//				}

				XMLExport xmlExport = new XMLExport(exportFileName, model.get(0).getModelName(), numberRounds, headers.toArray(new String[0]));

				int percent = 0;
				System.out.print("Export: 00%");

//				double[] values = new double[levelNodes.length];
//				for (int j = 0; j < values.length; j++) {
//					values[j] = levelNodes[j].getCurrentValue();
//				}
//				xmlExport.write(values);
				for (int i = 0; i < numberRounds; i++) {
//					int newPercent = 100 * i / numberRounds;
//					if (newPercent > percent) {
//						percent = newPercent;
//						// Backspaces do not work within Eclipse IDE - but on normal console!
//						System.out.print("\b\b\b");
//						if (percent < 10) {
//							System.out.print("0");
//						}
//						System.out.print(percent + "%");
//					}
//					model.get(k).computeNextValues();
//					values = new double[levelNodes.length];
//					for (int j = 0; j < values.length; j++) {
//						values[j] = levelNodes[j].getCurrentValue();
//					}
					xmlExport.write(values.get(i));
				}
				xmlExport.close();
			}
		} catch (IOException e) {
			System.out.println("ERROR: An IOException occured during export.");
			System.exit(1);
		}
		
		return levelNodeMap;
	}
	
	
	private ArrayList<Model> executeInOrder(ArrayList<Model> models){
   	 ArrayList<Model> orderedModels = new ArrayList<Model>();
   	 HashMap<Model, HashSet<Model>> adjacentList = getAdjacentListOfModel(models);
   	 HashMap<Model, Integer> numberOfPredecessorsMap = getNumberOfPredecessorsMap(models);
   	      
   	      while (!numberOfPredecessorsMap.isEmpty()) {
   	         for (Model m : numberOfPredecessorsMap.keySet()) {
   	            if (numberOfPredecessorsMap.get(m) == 0) {
   	            	orderedModels.add(m);
   
   	               HashSet<Model> dependantModel = adjacentList.get(m);
   	               if (dependantModel != null) {
   	                  for (Model model : dependantModel) {
   	                     int numberOfPredecessors = numberOfPredecessorsMap.get(model);
   	                     numberOfPredecessors--;
   	                     numberOfPredecessorsMap.put(model, numberOfPredecessors);
   	                  }
   	               }
   	               
   	               numberOfPredecessorsMap.remove(m);
   	               
   	               break;
   	            }
   	         }
   	      }
   	      
   	 return orderedModels;
   	 
    }

	private HashMap<Model, Integer> getNumberOfPredecessorsMap(ArrayList<Model> submodels) {
		HashMap<Model, Integer> map = new HashMap<Model, Integer>();
		
		for (Model model : submodels) {
	    	  
	    	  HashSet<Model> allChartPanelsThisDependsOn= new HashSet<Model>();
	    	  
	    	  for(SharedNode sn: model.getSharedNodes()){
	    		  for(int i=0;i<submodels.size();i++){
	    				for(AuxiliaryNode an: submodels.get(i).getAuxiliaryNodes()){
	    		 	  	  if(sn.getSource() == an){
	    		 	  		allChartPanelsThisDependsOn.add(submodels.get(i));
	    		 	  	  }
	    		 	  	  break;
	    				 }
	    				for(LevelNode ln: submodels.get(i).getLevelNodes()){
		    		 	  if(sn.getSource() == ln){
		    		 	  		allChartPanelsThisDependsOn.add(submodels.get(i));
		    		 	  }
		    		 	  break;
	    				}
	    		  }
	    	  }
	    	  
	    	  map.put(model, allChartPanelsThisDependsOn.size());
	      }
		
		return map;
	}

	private HashMap<Model, HashSet<Model>> getAdjacentListOfModel(ArrayList<Model> models) {
		HashMap<Model, HashSet<Model>> adjacentList =
		         new HashMap<Model, HashSet<Model>>();
		      
		      for (Model model : models) {
		    	  HashSet<Model> allModelsDependOnThis= new HashSet<Model>();
		    	  
		    	  for(int i=0;i<models.size();i++){	
		    		  for(SharedNode sn: models.get(i).getSharedNodes()){
		    				for(AuxiliaryNode an: model.getAuxiliaryNodes()){
		    		 	  	  if(sn.getSource() == an){
		    		 	  		allModelsDependOnThis.add(models.get(i));
		    		 	  	  }
		    		 	  	  break;
		    				 }
		    				for(LevelNode ln: model.getLevelNodes()){
			    		 	  if(sn.getSource() == ln){
			    		 		 allModelsDependOnThis.add(models.get(i));
			    		 	  }
			    		 	  break;
		    				}
		    		  }
		    	  }
		    	  
		    	  adjacentList.put(model, allModelsDependOnThis);
		      }
		return adjacentList;
	}
	
	
	private double[] toArray(ArrayList<Double> list){
		double[] res = new double[list.size()];
		for(int i=0;i<list.size();i++){
			res[i]=list.get(i);
		}
		return res;
	}
		
	public static String doMain(String[] args,int run)
	{
		String modelName = null;   
		if (args.length == 1) {
			// test for "-h" or "--help"
			if (args[0].equals("-h") || args[0].equals("--help")) {
				printHelp();
			} else {
				System.out.println("ERROR: Wrong usage.");
				printHelp();

				// exit program
				System.exit(1);
			}
		} else {
			if ((args.length == 8) || (args[8].equals("-h"))) {

				// test for "normal" parameters
				String modelFileName = null;
				String initFilename = null;

				try {
					modelFileName = getModelFileName(args);
					SystemDynamicsCommandLine.numberRounds = getNumberRounds(args);
					exportCSV(args);
					initFilename = getExportFileName(args);
					SystemDynamicsCommandLine.exportFileName = getExportFileName(args);
					SystemDynamicsCommandLine.exportFileName = SystemDynamicsCommandLine.exportFileName + "_" + run;
				} catch (IllegalArgumentException e) {
					// wrong parameters -> show help
					System.out.println("ERROR: " + e.getMessage());
					printHelp();

					// exit program
					System.exit(1);
				}
				/* Modification*/
				/*@Author: Pradeep Jawahar*/
				/*Date: 04/23/2011*/
				/*Adding pre - processing step*/
				if(args.length > 8)
				{
					try {
						PreProcess preProcess = new PreProcess();
						HashMap<String,String> initMap = new HashMap<String,String>();
						HashMap<String,String>  clist = CommandLineHelper.convertCl(args[9]);
						
						HashMap<String, String> glist = run != 0 ? new HashMap<String,String>() : CommandLineHelper.convertGl("global"+args[12]+".txt");
						
						HashMap<String,String> preList = new HashMap<String,String>();
						int flag = 0;
						if(args[12].equals("2")&& args[11].equals("0")){
							initMap = preProcess.phase2PreProcess( 
									"phase2/"+ initFilename + "_-1");
							preList.putAll(initMap);
							flag = 1;
						}

//
//						System.out.println("+++++++++++++++++++++++++++++   clist entrySet");
//						for(Map.Entry<String, String> e : clist.entrySet() ){
//							System.out.println("key  "+e.getKey()+"   value  "+e.getValue());
//						}
//						System.out.println("+++++++++++++++++++++++++++++");
//						
						
						modelName = preProcess.preprocess(modelFileName, clist, glist, preList, run, flag);
					
						System.out.println("+++++++++++++++++++++++++++++   new transform file name in doMain");
						System.out.println(modelName);
						System.out.println("+++++++++++++++++++++++++++++");
						
						
					
					} catch (Exception e) {

						e.printStackTrace();
					}
				}

			} else {
				// wrong parameters -> show help
				System.out.println("ERROR: Wrong usage.");
				printHelp();

				// exit program
				System.exit(1);
			}
		}
		return modelName;
	}
	/**
	 * Main method.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) throws Exception {

		HashMap<String,String> levelNodeMap;
		if(args.length==8){ // stop before '-h'
			System.out.println("Simulating default System Dynamics");
			SystemDynamicsCommandLine.numberRounds = getNumberRounds(args);
			exportCSV(args);
			SystemDynamicsCommandLine.exportFileName = getExportFileName(args);
			SystemDynamicsCommandLine object = new SystemDynamicsCommandLine(args[1],
					numberRounds,
					exportCSV,
					exportFileName);
			/*Changed 03/10/2012 */
			//object.executeCommand();
			object.executeCommand();
			System.exit(1);

		}
		//Read Cycle  
		int phaseCycle = Integer.parseInt(args[11]);
		String phase = "phase"+args[12];
		String [] targs = new String[args.length];
		StringTokenizer st = new StringTokenizer(args[1],".");
		String mname = st.nextToken();
		String graphFileName = (args.length>13) && args[13].equals("-g") ? args[14] : "chart.xml";
		//Phase2 - init
		if((phaseCycle==-1) && (args[12].equals("2")))
		{
			System.out.println("Simulating Phase 2 init");
			String modelName = doMain(args,phaseCycle);
			System.out.println("HERE1");
			SystemDynamicsCommandLine object = new SystemDynamicsCommandLine(modelName,
					numberRounds,
					exportCSV,phase + "/"+
							exportFileName);
			System.out.println("HERE2");
			levelNodeMap = object.executeCommand();
			System.out.println("HERE3");
			PostProcess.postProcess(numberRounds, modelName, exportFileName, 0);
			System.out.println("HERE4");
			DrawGraphs_Init graphs = new DrawGraphs_Init(levelNodeMap,graphFileName);
			System.out.println("HERE5");
			System.out.println("Generating Graphs");
			graphs.drawGraphs(0, exportFileName,"phase2");
			System.out.println("Simulation completed succesfully");
			System.exit(1);
		}
		if(phaseCycle>0)
			args[1] = mname + "_" + ((phaseCycle*2)-1) + ".xml";
		String modelName = doMain(args,phaseCycle);
		SystemDynamicsCommandLine object = new SystemDynamicsCommandLine(modelName,
				numberRounds,
				exportCSV,phase + "/"+
						exportFileName);
		levelNodeMap = object.executeCommand(); 

		phaseCycle++;
		System.out.println("Generating graphs..");
		if(args[12].equals("2"))
		{
			PostProcess.postProcess(numberRounds, modelName, exportFileName, phaseCycle);
			DrawGraphs graphs = new DrawGraphs(levelNodeMap,graphFileName);
			graphs.drawGraphs(phaseCycle, exportFileName,"phase2");
		}
		else if(args[12].equals("3"))
		{
			PostProcess.postProcess(numberRounds, modelName, exportFileName, phaseCycle);
			DrawGraphs graphs = new DrawGraphs(levelNodeMap,graphFileName);
			graphs.drawGraphs(phaseCycle, exportFileName,"phase3");
		}
		else if(args[12].equals("4"))
		{
			PostProcess.postProcess(numberRounds, modelName, exportFileName, phaseCycle);
			DrawGraphs graphs = new DrawGraphs(levelNodeMap,graphFileName);
			graphs.drawGraphs(phaseCycle, exportFileName,"phase4");
		}
		else if(args[12].equals("5"))
		{
			PostProcess.postProcess(numberRounds, modelName, exportFileName, phaseCycle);
			DrawGraphs graphs = new DrawGraphs(levelNodeMap,graphFileName);
			graphs.drawGraphs(phaseCycle, exportFileName,"phase5");
		}
		System.out.println("Graphs Generated");
		phaseCycle--;
		PostProcess.writeInputPhase(args[9], exportFileName, numberRounds, Integer.parseInt(args[12]));
//		PostProcess.writeInput(exportFileName, numberRounds, Integer.parseInt(args[12]));
		//Graphs
		// DrawGraphs graphs = new DrawGraphs();
		//graphs.graphDriver(i, exportFileName);
		System.out.println("Run Completed");
	}

	/**
	 * Prints help message for correct usage.
	 */
	private static void printHelp() {
		System.out.println("Usage: systemdynamics [-m|--model] <model_file> [-r|--rounds] <number_rounds> [-t|--type] [csv|xml] [-o|--output] <output_file>");
		System.out.println("         (to execute model and export execution values)");
		System.out.println("   or  systemdynamics [-h|--help]");
		System.out.println("         (to show help)");
		System.out.println();
		System.out.println("where options are:");
		System.out.println("   [-h|--help]");
		System.out.println("     shows this help page");
		System.out.println("   [-m|--model]");
		System.out.println("     XML file with stored model to execute");
		System.out.println("   [-r|--rounds]");
		System.out.println("     number of rounds to execute");
		System.out.println("   [-t|--type]");
		System.out.println("     one of [cvs|xml]: CVS or XML export of execution values");
		System.out.println("   [-o|--output]");
		System.out.println("     output file for execution values");
		System.out.println("  [-h]");
		System.out.println("	Hiring  Parameters for this round");
	}

	/**
	 * Gets the model file name out of the command line arguments.
	 * <p>
	 * If there is no corresponding parameter, an <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param args command line arguments.
	 * @return model file name
	 */
	private static String getModelFileName(String[] args) {
		if (args == null) {
			throw new IllegalArgumentException("'args' must not be null.");
		}
		if(args.length < 8)
			throw new IllegalArgumentException("'args' must have length 8 for default run.");
		else if ((args.length > 8) && (!args[8].equals("-h"))) {
			throw new IllegalArgumentException("'args' must have hiring parameters.");
		}

		boolean parameterStringFound = false;
		String modelFileName = null;
		for (int i = 0; i < 4; i++) {
			if (args[2 * i].equals("-m") || args[2 * i].equals("--model")) {
				parameterStringFound = true;
				modelFileName = args[2 * i + 1];
				break;
			}
		}
		if (parameterStringFound) {
			return modelFileName;
		} else {
			throw new IllegalArgumentException("Parameter [-m|--model] not found.");
		}
	}

	/**
	 * Gets the number of rounds to execute out of the command line arguments.
	 * <p>
	 * If there is no corresponding parameter, an <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param args command line arguments.
	 * @return number of rounds
	 */
	private static int getNumberRounds(String[] args) {
		if (args == null) {
			throw new IllegalArgumentException("'args' must not be null.");
		}
		if(args.length < 8)
			throw new IllegalArgumentException("'args' must have length 8 for default run.");
		else if ((args.length > 8) && (!args[8].equals("-h"))) {
			throw new IllegalArgumentException("'args' must have hiring parameters.");
		}

		boolean parameterStringFound = false;
		int numberRounds = 0;
		for (int i = 0; i < 4; i++) {
			if (args[2 * i].equals("-r") || args[2 * i].equals("--rounds")) {
				parameterStringFound = true;
				String numberRoundsString = args[2 * i + 1];
				try {
					numberRounds = Integer.parseInt(numberRoundsString);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Value of parameter [-r|--round] not parseable.");
				}

				if (numberRounds < 1) {
					throw new IllegalArgumentException("Value of parameter [-r|--round] must be at least 1.");
				}

				break;
			}
		}
		if (parameterStringFound) {
			return numberRounds;
		} else {
			throw new IllegalArgumentException("Parameter [-r|--rounds] not found.");
		}
	}

	/**
	 * Gets the export type out of the command line arguments.
	 * <p>
	 * If there is no corresponding parameter, an <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param args command line arguments.
	 * @return <code>true</code> if export as CSV - <code>false</code> for export as XML
	 */
	private static boolean exportCSV(String[] args) {
		if (args == null) {
			throw new IllegalArgumentException("'args' must not be null.");
		}
		if(args.length < 8)
			throw new IllegalArgumentException("'args' must have length 8 for default run.");
		else if ((args.length > 8) && (!args[8].equals("-h"))) {
			throw new IllegalArgumentException("'args' must have hiring parameters.");
		}

		boolean parameterStringFound = false;
		boolean exportCSV = false;
		for (int i = 0; i < 4; i++) {
			if (args[2 * i].equals("-t") || args[2 * i].equals("--type")) {
				parameterStringFound = true;
				String exportCSVString = args[2 * i + 1].toLowerCase();
				if (exportCSVString.equals("true")) {
					exportCSV = true;
					SystemDynamicsCommandLine.exportCSV = true;
				} else {
					if (exportCSVString.equals("csv")) {
						exportCSV = true;
						SystemDynamicsCommandLine.exportCSV = true;
					} else {
						if (exportCSVString.equals("xml")) {
							exportCSV = false;
							SystemDynamicsCommandLine.exportCSV = false;
						} else {
							// value not parseable
							throw new IllegalArgumentException("Value of parameter [-t|--type] not parseable.");
						}
					}
				}

				break;
			}
		}
		if (parameterStringFound) {
			return exportCSV;
		} else {
			throw new IllegalArgumentException("Parameter [-t|--type] not found.");
		}
	}

	/**
	 * Gets the export file name out of the command line arguments.
	 * <p>
	 * If there is no corresponding parameter, an <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param args command line arguments.
	 * @return export file name
	 */
	private static String getExportFileName(String[] args) {
		if (args == null) {
			throw new IllegalArgumentException("'args' must not be null.");
		}
		if(args.length < 8)
			throw new IllegalArgumentException("'args' must have length 8 for default run.");
		else if ((args.length > 8) && (!args[8].equals("-h"))) {
			throw new IllegalArgumentException("'args' must have hiring parameters.");
		}

		boolean parameterStringFound = false;
		String exportFileName = null;
		for (int i = 0; i < 4; i++) {
			if (args[2 * i].equals("-o") || args[2 * i].equals("--output")) {
				parameterStringFound = true;
				exportFileName = args[2 * i + 1];
				break;
			}
		}
		if (parameterStringFound) {
			return exportFileName;
		} else {
			throw new IllegalArgumentException("Parameter [-o|--output] not found.");
		}
	}
}