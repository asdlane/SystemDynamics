package de.uka.aifb.com.systemDynamics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import com.csvreader.CsvReader;

import en.gt.ti.com.systemDynamics.graphUtil.ChartLevelNode;
import en.gt.ti.com.systemDynamics.graphUtil.Increment;
import en.gt.ti.com.systemDynamics.graphUtil.LevelNodeGraphInfo;
import en.gt.ti.com.systemDynamics.graphUtil.PlannedRef;
import en.gt.ti.com.systemDynamics.graphUtil.PlannedVariable;
import en.gt.ti.com.systemDynamics.graphUtil.PlannedVariableExt;
import en.gt.ti.com.systemDynamics.graphUtil.PlannedXML;
import en.gt.ti.com.systemDynamics.graphUtil.SysDynChart;

public class DrawGraphs {

	private static final String SYSTEM_DYNAMICS_PROPERTIES_FILE = "systemDynamics.properties";

	HashMap<String, PlannedVariable> plannedMap;
	HashMap<String, SysDynChart> chartMap;
	HashMap<String, String> levelNodes;
	int runValue;
	HashMap<String, double[]> planOutput;

	public DrawGraphs(HashMap<String, String> levelNodes,String chartFilename) {
		PlannedXML planned = new PlannedXML(chartFilename);
		plannedMap = planned.readPlannedVariable();
		chartMap = planned.buildChart();
		this.levelNodes = levelNodes;
		planOutput = new HashMap<String, double[]>();
		try
		{
			runValue = 0;
		}
		catch(Exception e)
		{
			
		}
	}

	/**
	 * @param run
	 * @param fname
	 * @param folder
	 * @throws Exception
	 */
	public void drawGraphs(int run, String fname, String folder) throws Exception {
		int xValue = 0;
		Iterator it = chartMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
//			System.out.println(pairs.getKey());
			SysDynChart chartObj = (SysDynChart) pairs.getValue();
			xValue = this.createGraph(chartObj, fname, run, folder);
		}

		String planFile = folder + "/plan_" + (run-1) + ".txt";
		File planOutputFile = new File(planFile);
		BufferedWriter writer= new BufferedWriter(new FileWriter(planFile));
		String names = new String();
		Iterator itPlanNames = planOutput.entrySet().iterator();
		while (itPlanNames.hasNext()) {			
			Map.Entry pairs = (Map.Entry) itPlanNames.next();
			names+=((String)pairs.getKey());
			names+=(";");
		}
		writer.write(names);
		writer.newLine();
		for(int i = 0; i < 1891; i++)
		{
			String toPut = new String();
			Iterator itPlan = planOutput.entrySet().iterator();
			while (itPlan.hasNext()) {			
				Map.Entry pairs = (Map.Entry) itPlan.next();
				double [] temp = (double[]) pairs.getValue();
				toPut+=(Double.toString(temp[i]));
				toPut+=(";");
			}
			writer.write(toPut);
			writer.newLine();
		}
		writer.close();

		writeTime(xValue);
		writeGlobal(run, folder);
	}

	/**
	 * @param chartObj
	 * @param fname
	 * @param run
	 * @param folder
	 * @return
	 * @throws Exception
	 */
	public int createGraph(SysDynChart chartObj, String fname, int run,
			String folder) throws Exception {
		// Using charts XML

		Vector<ChartLevelNode> levelNodesVector = chartObj.getChartLevelNodes();
		Vector<LevelNodeGraphInfo> levelVector = new Vector<LevelNodeGraphInfo>();

		double PDR_DATE = 0.0;
		double PRR_DATE = 0.0;
		double ISR_DATE = 0.0;
		double CDR_DATE = 0.0;
		double FRR_DATE = 0.0;
		
		String prNodeName = null;
		for (ChartLevelNode lNode : levelNodesVector) {
			LevelNodeGraphInfo levelNodeInfo = new LevelNodeGraphInfo();
			levelNodeInfo.setId(lNode.getLevelIdRef());
			levelNodeInfo.setNodeName(levelNodes.get(lNode.getLevelIdRef()));
			if(lNode.getLevelIdRef().equals(chartObj.getpr()))
				prNodeName = levelNodeInfo.getNodeName();
			levelNodeInfo.setSeries(new XYSeries(lNode.getLabel()));
			levelVector.add(levelNodeInfo);
		}

		Vector<Double> bounds = new Vector<Double>();
		bounds.add(0.0);
		int ib = 1;
		if(chartObj.getFStep() == 1)
		{
			//System.out.println(bounds.elementAt(0) + "\n");														
			Vector<PlannedRef> planNodesVector = chartObj.getPlannedNodes();
			for (PlannedRef planRef : planNodesVector) {
				PlannedVariable plan = plannedMap.get(planRef.getId());
				for(Increment inc : plan.getPlannedIncrement()){
					if(inc.getLength() == 1)
					{
						bounds.add(bounds.elementAt(ib-1)+inc.getSlope());
						ib++;
						//System.out.println(bounds.elementAt(ib-1) + "\n");														
					}
				}
			}
		}

		int xIntercept = 0, max_xIntercept = 0;
		int rValue = 0;
		double min = 0, max = 0;
		int set = 0, prSet = 0;
		String prID = chartObj.getpr();
		int stepSet = chartObj.getStep();
		if(chartObj.getGlobal() == 1)
			max_xIntercept = 1890;
		else
		{
			if(folder.equals("phase2"))
				max_xIntercept = 810;
			else
				max_xIntercept = 360;
		}
		double initValue = 0.001;

		HashMap<String,String> gl_map = new HashMap<String,String>();
		CsvReader globalFile = new CsvReader("xGlobal.txt",',');
		while(globalFile.readRecord())
		{
			gl_map.put(globalFile.get(0),globalFile.get(1));
		}
		globalFile.close();

		if(folder.equals("phase3") && (chartObj.getGlobal() == 1))
		{
			int prevPhRun = 0;
			String prevPh = "phase2";
			Iterator<Entry<String, String>> mapIterator = gl_map.entrySet().iterator();
			  
			while(mapIterator.hasNext())
			{
				Map.Entry pairs = mapIterator.next();
				if(prevPh.equals(((String)pairs.getKey())))
					prevPhRun = Integer.parseInt((String)pairs.getValue());
			} 			
		
			for (int j = -1; j < prevPhRun; j++) {
				String outputFile = getExtFileName(j, prevPh);
				CsvReader products = new CsvReader(outputFile, ';');
				products.skipLine();
				products.readHeaders();
				int iter = 0;

				while (products.readRecord()) {
					for (LevelNodeGraphInfo lnode : levelVector) {
						if(iter == 0){
							String records  = products.getRawRecord();
							String[] recordEntries = records.split(";");

							CDR_DATE = Double.valueOf(recordEntries[29]);//810 - CDR_DATE 
							PRR_DATE = Double.valueOf(recordEntries[72]);//1530 - PRR_DATE 
							PDR_DATE = Double.valueOf(recordEntries[71]);//0 - PDR_DATE
							ISR_DATE = Double.valueOf(recordEntries[70]);//1890 - ISR_DATE
							FRR_DATE = Double.valueOf(recordEntries[67]); //1170 - FRR_Date
							
						
							}
						double value = Double.parseDouble(products.get(lnode.getNodeName()));

						if(chartObj.getFStep() == 1)
						{
							int jbounds;
							for(jbounds = 0; jbounds < (bounds.capacity()-1); jbounds++)
							{
								if(value >= bounds.elementAt(jbounds) && value <= bounds.elementAt(jbounds+1))
									break;
								else
									continue;
							}
							if(value == bounds.lastElement())
								lnode.getSeries().add(xIntercept, bounds.lastElement());
							else
								lnode.getSeries().add(xIntercept, bounds.elementAt(jbounds));
						}
						else
							lnode.getSeries().add(xIntercept, value);						
						
						if(prSet == 0)
						{
							if(lnode.getNodeName().equals(prNodeName))
							{
								initValue = value;
								prSet++;
							}
							
						}

						if(set == 0)
						{
							min = value;
							max = value;
							set++;
						}
						else
						{
							if(min > value)
								min = value;
							if(max < value)
								max = value;
						}
					}
					xIntercept++;
				}
				products.close();
			}
		}
		
		if(folder.equals("phase4") && (chartObj.getGlobal() == 1))
		{
			int Ph2Run = 0, Ph3Run = 0;
			String Ph2 = "phase2";
			String Ph3 = "phase3";
			Iterator<Entry<String, String>> mapIterator = gl_map.entrySet().iterator();
			  
			while(mapIterator.hasNext())
			{
				Map.Entry pairs = mapIterator.next();
				if(Ph2.equals(((String)pairs.getKey())))
					Ph2Run = Integer.parseInt((String)pairs.getValue());
				if(Ph3.equals(((String)pairs.getKey())))
					Ph3Run = Integer.parseInt((String)pairs.getValue());
			} 			

			for (int j = -1; j < Ph2Run; j++) {
				String outputFile = getExtFileName(j, Ph2);
				CsvReader products = new CsvReader(outputFile, ';');
				products.skipLine();
				products.readHeaders();
				while (products.readRecord()) {
					for (LevelNodeGraphInfo lnode : levelVector) {
						double value = Double.parseDouble(products.get(lnode.getNodeName()));

						if(chartObj.getFStep() == 1)
						{
							int jbounds;
							for(jbounds = 0; jbounds < (bounds.capacity()-1); jbounds++)
							{
								if(value >= bounds.elementAt(jbounds) && value <= bounds.elementAt(jbounds+1))
									break;
								else
									continue;
							}
							if(value == bounds.lastElement())
								lnode.getSeries().add(xIntercept, bounds.lastElement());
							else
								lnode.getSeries().add(xIntercept, bounds.elementAt(jbounds));
						}
						else
							lnode.getSeries().add(xIntercept, value);						
						
						if(prSet == 0)
						{
							if(lnode.getNodeName().equals(prNodeName))
							{
								initValue = value;
								prSet++;
							}
							
						}

						if(set == 0)
						{
							min = value;
							max = value;
							set++;
						}
						else
						{
							if(min > value)
								min = value;
							if(max < value)
								max = value;
						}
					}
					xIntercept++;
				}
				products.close();
			}			
			for (int j = 0; j < Ph3Run; j++) {
				String outputFile = getExtFileName(j, Ph3);
				CsvReader products = new CsvReader(outputFile, ';');
				products.skipLine();
				products.readHeaders();
				while (products.readRecord()) {
					for (LevelNodeGraphInfo lnode : levelVector) {
						double value = Double.parseDouble(products.get(lnode.getNodeName()));

						if(chartObj.getFStep() == 1)
						{
							int jbounds;
							for(jbounds = 0; jbounds < (bounds.capacity()-1); jbounds++)
							{
								if(value >= bounds.elementAt(jbounds) && value <= bounds.elementAt(jbounds+1))
									break;
								else
									continue;
							}
							if(value == bounds.lastElement())
								lnode.getSeries().add(xIntercept, bounds.lastElement());
							else
								lnode.getSeries().add(xIntercept, bounds.elementAt(jbounds));
						}
						else
							lnode.getSeries().add(xIntercept, value);						
						
						if(prSet == 0)
						{
							if(lnode.getNodeName().equals(prNodeName))
							{
								initValue = value;
								prSet++;
							}
							
						}

						if(set == 0)
						{
							min = value;
							max = value;
							set++;
						}
						else
						{
							if(min > value)
								min = value;
							if(max < value)
								max = value;
						}
					}
					xIntercept++;
				}
				products.close();
			}			
		}

		if(folder.equals("phase5") && (chartObj.getGlobal() == 1))
		{
			int Ph2Run = 0, Ph3Run = 0, Ph4Run = 0;
			String Ph2 = "phase2";
			String Ph3 = "phase3";
			String Ph4 = "phase4";
			Iterator<Entry<String, String>> mapIterator = gl_map.entrySet().iterator();
			  
			while(mapIterator.hasNext())
			{
				Map.Entry pairs = mapIterator.next();
				if(Ph2.equals(((String)pairs.getKey())))
					Ph2Run = Integer.parseInt((String)pairs.getValue());
				if(Ph3.equals(((String)pairs.getKey())))
					Ph3Run = Integer.parseInt((String)pairs.getValue());
				if(Ph4.equals(((String)pairs.getKey())))
					Ph4Run = Integer.parseInt((String)pairs.getValue());
			} 			

			for (int j = -1; j < Ph2Run; j++) {
				String outputFile = getExtFileName(j, Ph2);
				CsvReader products = new CsvReader(outputFile, ';');
				products.skipLine();
				products.readHeaders();
				while (products.readRecord()) {
					for (LevelNodeGraphInfo lnode : levelVector) {
						double value = Double.parseDouble(products.get(lnode.getNodeName()));

						if(chartObj.getFStep() == 1)
						{
							int jbounds;
							for(jbounds = 0; jbounds < (bounds.capacity()-1); jbounds++)
							{
								if(value >= bounds.elementAt(jbounds) && value <= bounds.elementAt(jbounds+1))
									break;
								else
									continue;
							}
							if(value == bounds.lastElement())
								lnode.getSeries().add(xIntercept, bounds.lastElement());
							else
								lnode.getSeries().add(xIntercept, bounds.elementAt(jbounds));
						}
						else
							lnode.getSeries().add(xIntercept, value);						
						
						if(prSet == 0)
						{
							if(lnode.getNodeName().equals(prNodeName))
							{
								initValue = value;
								prSet++;
							}
							
						}

						if(set == 0)
						{
							min = value;
							max = value;
							set++;
						}
						else
						{
							if(min > value)
								min = value;
							if(max < value)
								max = value;
						}
					}
					xIntercept++;
				}
				products.close();
			}			
			for (int j = 0; j < Ph3Run; j++) {
				String outputFile = getExtFileName(j, Ph3);
				CsvReader products = new CsvReader(outputFile, ';');
				products.skipLine();
				products.readHeaders();
				while (products.readRecord()) {
					for (LevelNodeGraphInfo lnode : levelVector) {
						double value = Double.parseDouble(products.get(lnode.getNodeName()));

						if(chartObj.getFStep() == 1)
						{
							int jbounds;
							for(jbounds = 0; jbounds < (bounds.capacity()-1); jbounds++)
							{
								if(value >= bounds.elementAt(jbounds) && value <= bounds.elementAt(jbounds+1))
									break;
								else
									continue;
							}
							if(value == bounds.lastElement())
								lnode.getSeries().add(xIntercept, bounds.lastElement());
							else
								lnode.getSeries().add(xIntercept, bounds.elementAt(jbounds));
						}
						else
							lnode.getSeries().add(xIntercept, value);						
						
						if(prSet == 0)
						{
							if(lnode.getNodeName().equals(prNodeName))
							{
								initValue = value;
								prSet++;
							}
							
						}

						if(set == 0)
						{
							min = value;
							max = value;
							set++;
						}
						else
						{
							if(min > value)
								min = value;
							if(max < value)
								max = value;
						}
					}
					xIntercept++;
				}
				products.close();
			}						
			for (int j = 0; j < Ph4Run; j++) {
				String outputFile = getExtFileName(j, Ph4);
				CsvReader products = new CsvReader(outputFile, ';');
				products.skipLine();
				products.readHeaders();
				while (products.readRecord()) {
					for (LevelNodeGraphInfo lnode : levelVector) {
						double value = Double.parseDouble(products.get(lnode.getNodeName()));

						if(chartObj.getFStep() == 1)
						{
							int jbounds;
							for(jbounds = 0; jbounds < (bounds.capacity()-1); jbounds++)
							{
								if(value >= bounds.elementAt(jbounds) && value <= bounds.elementAt(jbounds+1))
									break;
								else
									continue;
							}
							if(value == bounds.lastElement())
								lnode.getSeries().add(xIntercept, bounds.lastElement());
							else
								lnode.getSeries().add(xIntercept, bounds.elementAt(jbounds));
						}
						else
							lnode.getSeries().add(xIntercept, value);						
						
						if(prSet == 0)
						{
							if(lnode.getNodeName().equals(prNodeName))
							{
								initValue = value;
								prSet++;
							}
							
						}

						if(set == 0)
						{
							min = value;
							max = value;
							set++;
						}
						else
						{
							if(min > value)
								min = value;
							if(max < value)
								max = value;
						}
					}
					xIntercept++;
				}
				products.close();
			}						
		}

		//handle the init phase separately for phase 2 alone
		int i = 0;
		if(folder.equals("phase2"))
			i = -1;
		for (; i < run; i++) {
			String outputFile = getFileName(i, fname);
			CsvReader products = new CsvReader(outputFile, ';');
			products.skipLine();
			products.readHeaders();
			int iter = 0;
			while (products.readRecord()) {
				for (LevelNodeGraphInfo lnode : levelVector) {
					if(iter == 0){
						String records  = products.getRawRecord();
						String[] recordEntries = records.split(";");

						CDR_DATE = Double.valueOf(recordEntries[29]);//810 - CDR_DATE 
						PRR_DATE = Double.valueOf(recordEntries[72]);//1530 - PRR_DATE 
						PDR_DATE = Double.valueOf(recordEntries[71]);//0 - PDR_DATE
						ISR_DATE = Double.valueOf(recordEntries[70]);//1890 - ISR_DATE
						FRR_DATE = Double.valueOf(recordEntries[67]); //1170 - FRR_Date
						
					
						}

					double value = Double.parseDouble(products.get(lnode.getNodeName()));
					if(chartObj.getFStep() == 1)
					{
						int jbounds;
						for(jbounds = 0; jbounds < (bounds.capacity()-1); jbounds++)
						{
							if(value >= bounds.elementAt(jbounds) && value <= bounds.elementAt(jbounds+1))
								break;
							else
								continue;
						}
						if(value == bounds.lastElement())
							lnode.getSeries().add(xIntercept, bounds.lastElement());
						else
							lnode.getSeries().add(xIntercept, bounds.elementAt(jbounds));
					}
					else
						lnode.getSeries().add(xIntercept, value);						
					
					if(prSet == 0)
					{
						if(lnode.getNodeName().equals(prNodeName))
						{
							initValue = value;
							prSet++;
						}
						
					}

					if(set == 0)
					{
						min = value;
						max = value;
						set++;
					}
					else
					{
						if(min > value)
							min = value;
						if(max < value)
							max = value;
					}
				}
				xIntercept++;
				rValue++;
				
			}
//			rValue =--xIntercept;
			rValue--;
			products.close();
		}

		int seriesNumber = -1;
		final XYSeriesCollection dataset = new XYSeriesCollection();
		for (LevelNodeGraphInfo lnode : levelVector) {
			dataset.addSeries(lnode.getSeries());
			seriesNumber++;
		}
		// Planned Variable
		int isPlan = 0;
		Vector<PlannedRef> planRefVector = chartObj.getPlannedNodes();
		Vector<PlannedVariableExt> planVector = new Vector<PlannedVariableExt>();
		for (PlannedRef planRef : planRefVector) {
			PlannedVariable plan = plannedMap.get(planRef.getId());
			PlannedVariableExt planExt = new PlannedVariableExt();
			planExt.setLabel(planRef.getLabel());
			planExt.setName(plan.getName());
			if(plan.getId().equals(prID))
				prNodeName = plan.getName();
			planExt.setStartValue(plan.getStartValue());
			planExt.setPlannedIncrement(plan.getPlannedIncrement());
			planVector.add(planExt);
		}

		// create the chart...
		int x1 = 0, x2 = 0;
		double y1, y2;
		Vector<XYTextAnnotation> xytextannotationList = new Vector<XYTextAnnotation>();
		for (PlannedVariableExt plannedExt : planVector) {
			double [] planValues = new double[1891];
			int planValueIndex = 0;
			if (x2 >= max_xIntercept)
				break;
			XYSeries planSeries = new XYSeries(plannedExt.getLabel());
			y1 = Double.parseDouble(plannedExt.getStartValue());
			planValues[planValueIndex] = y1;
			planValueIndex++;
			for (Increment inc : plannedExt.getPlannedIncrement()) {
				if (x2 >= max_xIntercept)
					break;
				for (i = 0; i < inc.getLength(); i++) {
					if (x2 >= max_xIntercept)
						break;
					y2 = inc.getSlope() * (x2 - x1) + y1;
					planSeries.add(x2, y2);
					if(inc.getAnnoLabel().isEmpty() == false)
					{
						XYTextAnnotation xytextanno;
						if(inc.getAnnoPosition() == 1)
						{
							xytextanno = new XYTextAnnotation(inc.getAnnoLabel(),x2,y2);
							xytextanno.setTextAnchor(TextAnchor.BOTTOM_LEFT);
						}
						else
						{
							xytextanno = new XYTextAnnotation(inc.getAnnoLabel(),x1,y1);
							xytextanno.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
						}
						xytextannotationList.add(xytextanno);
					}
					planValues[planValueIndex] = y2;
					planValueIndex++;
					if(prSet == 0)
					{
						if(plannedExt.getName().equals(prNodeName))
						{
							initValue = y2;
							prSet++;
						}
						
					}
					if(set == 0)
					{
						min = y2;
						max = y2;
						set++;
					}
					else
					{
						if(min > y2)
							min = y2;
						if(max < y2)
							max = y2;
					}
					x1 = x2;
					y1 = y2;
					x2++;
				}

			}
			dataset.addSeries(planSeries);
			seriesNumber++;
			isPlan = 1;
			planOutput.put(plannedExt.getName(), planValues);

		}
		final JFreeChart chart = ChartFactory.createXYLineChart(chartObj
				.getName(), // chart title
				chartObj.getXLabel(), // x axis label
				chartObj.getYLabel(), // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// final StandardLegend legend = (StandardLegend) chart.getLegend();
		// legend.setDisplaySeriesShapes(true);

		// get a reference to the plot for further customisation...
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		
		if(chartObj.getGlobal() == 1)
		{
			// Creating the vertical bars indicating different phases: PDR, CDR, FRR, PRR, ISR
			Marker pdr = new ValueMarker(PDR_DATE);
			pdr.setLabel("PDR");
			pdr.setStroke(new BasicStroke(5));
//			pdr.setPaint(Color.GRAY);
			pdr.setLabelOffset(new RectangleInsets(15,-15,15,-15));
			plot.addDomainMarker(pdr);
	
			Marker cdr = new ValueMarker(CDR_DATE);
			cdr.setLabel("CDR");
			cdr.setStroke(new BasicStroke(5));
//			cdr.setPaint(Color.GRAY);
			cdr.setLabelOffset(new RectangleInsets(15,15,15,15));
			plot.addDomainMarker(cdr);
	
			Marker frr = new ValueMarker(FRR_DATE);
			frr.setLabel("FRR");
			frr.setStroke(new BasicStroke(5));
//			frr.setPaint(Color.GRAY);
			frr.setLabelOffset(new RectangleInsets(15,15,15,15));
			plot.addDomainMarker(frr);
	
			Marker prr = new ValueMarker(PRR_DATE);
			prr.setLabel("PRR");
			prr.setStroke(new BasicStroke(5));
//			prr.setPaint(Color.GRAY);
			prr.setLabelOffset(new RectangleInsets(15,15,15,15));
			plot.addDomainMarker(prr);
	
			Marker isr = new ValueMarker(ISR_DATE);
			isr.setLabel("ISR");
			isr.setStroke(new BasicStroke(5));
//			isr.setPaint(Color.GRAY);
			isr.setLabelOffset(new RectangleInsets(15,15,15,15));
			plot.addDomainMarker(isr);
		}
		else if(folder.equals("phase2"))
		{
			Marker pdr = new ValueMarker(PDR_DATE);
			pdr.setLabel("PDR");
			pdr.setStroke(new BasicStroke(5));
//			pdr.setPaint(Color.GRAY);
			pdr.setLabelOffset(new RectangleInsets(15,-15,15,-15));
			plot.addDomainMarker(pdr);
	
			Marker cdr = new ValueMarker(CDR_DATE);
			cdr.setLabel("CDR");
			cdr.setStroke(new BasicStroke(5));
//			cdr.setPaint(Color.GRAY);
			cdr.setLabelOffset(new RectangleInsets(15,15,15,15));
			plot.addDomainMarker(cdr);			
		}
		else if(folder.equals("phase3"))
		{
			Marker cdr = new ValueMarker(0);
			cdr.setLabel("CDR");
			cdr.setStroke(new BasicStroke(5));
			cdr.setPaint(Color.GRAY);
			cdr.setLabelOffset(new RectangleInsets(15,-15,15,-15));
			plot.addDomainMarker(cdr);
	
			Marker frr = new ValueMarker(360);
			frr.setLabel("FRR");
			frr.setStroke(new BasicStroke(5));
			frr.setPaint(Color.GRAY);
			frr.setLabelOffset(new RectangleInsets(15,15,15,15));
			plot.addDomainMarker(frr);			
		}
		else if(folder.equals("phase4"))
		{
			Marker frr = new ValueMarker(0);
			frr.setLabel("FRR");
			frr.setStroke(new BasicStroke(5));
//			frr.setPaint(Color.CYAN);
			frr.setLabelOffset(new RectangleInsets(15,-15,15,-15));
			plot.addDomainMarker(frr);
	
			Marker prr = new ValueMarker(360);
			prr.setLabel("PRR");
			prr.setStroke(new BasicStroke(5));
//			prr.setPaint(Color.CYAN);
			prr.setLabelOffset(new RectangleInsets(15,15,15,15));
			plot.addDomainMarker(prr);			
		}
		else if(folder.equals("phase5"))
		{
			Marker prr = new ValueMarker(0);
			prr.setLabel("PRR");
			prr.setStroke(new BasicStroke(5));
//			prr.setPaint(Color.CYAN);
			prr.setLabelOffset(new RectangleInsets(15,-15,15,-15));
			plot.addDomainMarker(prr);
	
			Marker isr = new ValueMarker(360);
			isr.setLabel("ISR");
			isr.setStroke(new BasicStroke(5));
//			isr.setPaint(Color.CYAN);
			isr.setLabelOffset(new RectangleInsets(15,15,15,15));
			plot.addDomainMarker(isr);			
		}

		ValueAxis yAxis = plot.getRangeAxis();
/*		double extMin, extMax;
		if (max < 1)
		{
			extMin = 1 + Math.abs((0.1*min));
			extMax = 1 + Math.abs((0.1*max));			
		}
		else if(min > 2200)
		{
			extMin = Math.abs((0.01*min));
			extMax = Math.abs((0.01*max));						
		}
		else
		{
			extMin = Math.abs((0.1*min));
			extMax = Math.abs((0.1*max));			
		}
		if (min <= 0.5)
			extMin = extMax;
		min -= extMin;
		max += extMax;
*/
		double extend;
		if (max == 0)
			extend = Math.abs((0.1*min));			
		else if (max < 1){
			extend = Math.abs((0.1*max));
			min = min + extend;
		}
		else if(min > 2200)
			extend = Math.abs((0.01*max));						
		else
			extend = Math.abs((0.1*max));			
		min -= extend;
		max += extend;
		yAxis.setRange(min, max);
		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		// plot.setDomainGridlinePaint(Color.white);
		// plot.setRangeGridlinePaint(Color.white);
		ValueAxis xAxis = plot.getDomainAxis();
		if(chartObj.getGlobal() == 0 && isPlan == 0)
		{
			double maxRange = ((xIntercept-1)*.10)+(xIntercept-1);
/*			if(folder.equals("phase2"))
				xAxis.setRange(0+6, maxRange+6);
			else
*/			xAxis.setRange(0, maxRange);
		}
		else
		{
			double maxRange = ((max_xIntercept)*.10)+(max_xIntercept);
/*			if(chartObj.getGlobal() == 1 || folder.equals("phase2"))
				xAxis.setRange(0+6, maxRange+6);
			else
*/			xAxis.setRange(0, maxRange);
		}

		LegendItemCollection annoLegend = new LegendItemCollection();
		int iAnno = 0;
		String s = new String();
		for (XYTextAnnotation e : xytextannotationList) {
			iAnno++;
			s += Integer.toString(iAnno);
			s += " - ";
			s += e.getText();
			s += "\n";
			e.setText(Integer.toString(iAnno));
			plot.addAnnotation(e);
		}
		if(iAnno > 0)
		{
			TextTitle legendText = new TextTitle(s);
			legendText.setPosition(RectangleEdge.BOTTOM);
			legendText.setFont(chart.getLegend().getItemFont());
			chart.addSubtitle(legendText);
		}

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//		renderer.setSeriesLinesVisible(0, false);
//		renderer.setSeriesShapesVisible(1, false);
		renderer.setBaseLinesVisible(true);
		renderer.setBaseShapesVisible(false);
		renderer.setBaseStroke(new BasicStroke(8));
		if(isPlan == 1)
			renderer.setSeriesPaint(seriesNumber, Color.black);
		plot.setRenderer(renderer);

		if(stepSet == 1)
		{
			final XYStepRenderer stepRenderer = new XYStepRenderer();
			stepRenderer.setBaseStroke(new BasicStroke(8));
			plot.setRenderer(stepRenderer);
		}

		if(prSet == 1)
		{
			NumberAxis axis2 = new NumberAxis("Percentage");
			plot.setRangeAxis(1, axis2);
			plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
//			plot.setDataset(1, dataset);
//			plot.mapDatasetToRangeAxis(1, 1);
			ValueAxis yaxis2 = plot.getRangeAxis(1);
			yaxis2.setRange(((min*100)/initValue), ((max*100)/initValue));
			XYItemRenderer renderer2 = new StandardXYItemRenderer();
			renderer2.setBaseSeriesVisible(false);
			renderer2.setBaseSeriesVisibleInLegend(false);
			renderer2.setBaseStroke(new BasicStroke(8));
//			renderer2.setSeriesVisibleInLegend(0, false);
//			renderer2.setSeriesVisibleInLegend(1, false);
//			renderer2.setSeriesVisibleInLegend(2, false);
			plot.setRenderer(1, renderer2);
		}		
		
		if(chartObj.getXLabel().contains("Months"))
		{
			ValueAxis xAxisPrimary = plot.getDomainAxis(0);
//			xAxisPrimary.setTickLabelsVisible(false);
			xAxisPrimary.setVisible(false);
			NumberAxis Axis2 = new NumberAxis(chartObj.getXLabel());
			plot.setDomainAxis(1, Axis2);
			plot.setDomainAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
			ValueAxis xAxis2 = plot.getDomainAxis(1);
//			xAxis2.setRange(0, ((xIntercept-1)/30));
			if(chartObj.getGlobal() == 0 && isPlan == 0)
			{
				double maxRange = ((xIntercept-1)*.10)+(xIntercept-1);
				if(folder.equals("phase2"))
					xAxis2.setRange(0+6, (maxRange/30)+6);
				else
					xAxis2.setRange(0, (maxRange/30));
			}
//			xAxis2.setRange(0, ((xIntercept-1)/30));
			else
			{
				double maxRange = ((max_xIntercept)*.10)+(max_xIntercept);
				if(chartObj.getGlobal() == 1 || folder.equals("phase2"))
					xAxis2.setRange(0+6, (maxRange/30)+6);
				else
					xAxis2.setRange(0, (maxRange/30));
			}
			NumberAxis numberaxis = (NumberAxis) plot.getDomainAxis(1);
			numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//			xAxis2.setRange(0, (max_xIntercept/30));
			XYItemRenderer renderer3 = new StandardXYItemRenderer();
			renderer3.setBaseSeriesVisible(false);
			renderer3.setBaseSeriesVisibleInLegend(false);
			renderer3.setBaseStroke(new BasicStroke(8));
			plot.setRenderer(2, renderer3);
		}
		else if(chartObj.getXLabel().contains("Quarters"))
		{
			ValueAxis xAxisPrimary = plot.getDomainAxis(0);
//			xAxisPrimary.setTickLabelsVisible(false);
			xAxisPrimary.setVisible(false);
			NumberAxis Axis2 = new NumberAxis(chartObj.getXLabel());
			plot.setDomainAxis(1, Axis2);
			plot.setDomainAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
			ValueAxis xAxis2 = plot.getDomainAxis(1);
//			xAxis2.setRange(0, ((xIntercept-1)/30));
			if(chartObj.getGlobal() == 0 && isPlan == 0)
			{
				double maxRange = ((xIntercept-1)*.10)+(xIntercept-1);
				if(folder.equals("phase2"))
					xAxis2.setRange(0+6, (maxRange/90)+6);
				else
					xAxis2.setRange(0, (maxRange/90));
			}
//			xAxis2.setRange(0, ((xIntercept-1)/7));
			else
			{
				double maxRange = ((max_xIntercept)*.10)+(max_xIntercept);
				if(chartObj.getGlobal() == 1 || folder.equals("phase2"))
					xAxis2.setRange(0+6, (maxRange/90)+6);
				else
					xAxis2.setRange(0, (maxRange/90));
			}
			NumberAxis numberaxis = (NumberAxis) plot.getDomainAxis(1);
			numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//			xAxis2.setRange(0, (max_xIntercept/7));
			XYItemRenderer renderer3 = new StandardXYItemRenderer();
			renderer3.setBaseSeriesVisible(false);
			renderer3.setBaseStroke(new BasicStroke(8));
			renderer3.setBaseSeriesVisibleInLegend(false);
			plot.setRenderer(2, renderer3);
		}

		int width = 1000;
		int height = 727;
		
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(SYSTEM_DYNAMICS_PROPERTIES_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String resWidthString = properties.getProperty("width");
		String resHeightString = properties.getProperty("height");
		if(resWidthString != null){
			width = Integer.valueOf(resWidthString);
		}
		if(resHeightString != null){
			height = Integer.valueOf(resHeightString);
		}

		
		// change the auto tick unit selection to integer units only...
		// final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		// rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		File imgFile = new File(folder + "/" + chartObj.getFile() + "_" + run
				+ ".jpg");
		ChartUtilities.saveChartAsJPEG(imgFile, chart, width, height);
		//System.out.println("Run value:" + xIntercept);
		//writeTime(runValue);
		return rValue;
	}

	/*private int readRunValue() throws Exception {
		File timeFile = new File("xTime.txt");
		if(!timeFile.exists())
			return 0;
		BufferedReader reader = new BufferedReader(new BufferedReader(new FileReader(timeFile)));
		 return Integer.parseInt(reader.readLine());
	}*/

	public String getFileName(int run, String fname) {
		int index = fname.lastIndexOf('_');
		String file = fname.substring(0, index);
		String new_filename = file + "_" + String.valueOf(run);
		return new_filename;
	}
	private void writeTime(int intercept) throws Exception {
		File timeFile = new File("xTime.txt");
		if(timeFile.exists())
			timeFile.delete();
		BufferedWriter writer= new BufferedWriter(new FileWriter("xTime.txt"));
		writer.write(String.valueOf(intercept));
		writer.close();
	}

	private void writeGlobal(int run, String phase) throws Exception {
		HashMap<String,String> gl_map = new HashMap<String,String>();
		CsvReader globalFile = new CsvReader("xGlobal.txt",',');
		while(globalFile.readRecord())
		{
			gl_map.put(globalFile.get(0),globalFile.get(1));
		}
		globalFile.close();

		Iterator<Entry<String, String>> mapIterator = gl_map.entrySet().iterator();
		File timeFile = new File("xGlobal.txt");
		if(timeFile.exists())
			timeFile.delete();
		BufferedWriter writer= new BufferedWriter(new FileWriter("xGlobal.txt"));
		  
		while(mapIterator.hasNext())
		{
			Map.Entry pairs = mapIterator.next();
//			if(((String)pairs.getKey()) == phase)
			if(phase.equals(((String)pairs.getKey())))
				pairs.setValue(String.valueOf(run));
			writer.write((String)pairs.getKey() + "," + (String)pairs.getValue());
			writer.newLine();
		} 
		
		writer.close();
	}
	public String getExtFileName(int prevPhRun, String prevPh) {
		String new_filename = prevPh + "/output_" + String.valueOf(prevPhRun);
		return new_filename;
	}

}