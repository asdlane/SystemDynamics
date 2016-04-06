package de.uka.aifb.com.systemDynamics.xml;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uka.aifb.com.systemDynamics.model.AuxiliaryNodesCycleDependencyException;
import de.uka.aifb.com.systemDynamics.model.ChartLevelNode;
import de.uka.aifb.com.systemDynamics.model.ChartModel;
import de.uka.aifb.com.systemDynamics.model.ChartPlanNode;
import de.uka.aifb.com.systemDynamics.model.NoFormulaException;
import de.uka.aifb.com.systemDynamics.model.NoLevelNodeException;
import de.uka.aifb.com.systemDynamics.model.PlanNode;
import de.uka.aifb.com.systemDynamics.model.PlanNodeIncrement;
import de.uka.aifb.com.systemDynamics.model.RateNodeFlowException;
import de.uka.aifb.com.systemDynamics.model.UselessNodeException;

public class ChartXMLModelWriter {
	public static void writeXMLModel(ArrayList<ChartModel> chart, String filename){
		if (chart == null) {
			throw new IllegalArgumentException("'model' must not be null.");
		}
		if (filename == null) {
			throw new IllegalArgumentException("'fileName' must not be null.");
		}
		
		ArrayList<Document> document = new ArrayList<Document>();
		
		try {
			for(int i=0;i<chart.size();i++){
				document.add(createDocumentForModel(chart.get(i)));
			}
		} catch (Exception e){
			e.printStackTrace();
		}
					
		// XML output
		for(int i=0;i<document.size();i++){
			writeDocumentToXMLFile(document.get(i), filename);
		}

	}

	private static void writeDocumentToXMLFile(Document document, String filename) {
		if (document == null) {
			throw new IllegalArgumentException("'document' must not be null.");
		}
		if (filename == null) {
			throw new IllegalArgumentException("'fileName' must not be null.");
		}
		// get root element
		Element rootElement = document.getDocumentElement();
		// XML output
				FileOutputStream fileOutputStream = null;
				OutputStreamWriter outputStreamWriter = null;
				try {
					try {
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						// work around for indentation (m"aybe XML parser implementation dependent!)
						transformerFactory.setAttribute("indent-number", new Integer(2));

						Transformer transformer = transformerFactory.newTransformer();
						transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");




						fileOutputStream = new FileOutputStream(new File(filename), true);                    

						outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");

						Result result = new StreamResult(outputStreamWriter);
						transformer.transform(new DOMSource(rootElement), result);
					} catch (Exception e) {
						// should not happen -> throw XMLModelWriterException
						e.printStackTrace();
					} finally {
						if (outputStreamWriter != null) {
							outputStreamWriter.close();
						}
						if (fileOutputStream != null) {
							fileOutputStream.close();
						}
					}
				} catch (IOException e) {
					// catch IOException of .close()' -> do nothing
				}

	}

	protected static Document createDocumentForModel(ChartModel model)
			throws AuxiliaryNodesCycleDependencyException, NoFormulaException, NoLevelNodeException,
			RateNodeFlowException, UselessNodeException, XMLModelReaderWriterException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();

		} catch (ParserConfigurationException e) {
			throw new XMLModelReaderWriterException(e);
		}

		Document document = builder.newDocument();
		Element modelElementTop = document.createElement("Charts");
		document.appendChild(modelElementTop);
		
		Element modelElement = document.createElement("Chart");
		modelElement.setAttribute("id", model.getchartId());
		modelElement.setAttribute("name", model.getchartName());
		modelElement.setAttribute("file",model.getfile());
		modelElement.setAttribute("yAxisLabel", model.getyAxisLabel());
		modelElement.setAttribute("xAxisLabel", model.getxAxisLabel());
		modelElementTop.appendChild(modelElement);
		
		Element ChartLevelNodes = document.createElement("ChartLevelNodes");
		modelElement.appendChild(ChartLevelNodes);
		HashSet<ChartLevelNode> levelNodes = model.getChartLevelNodes();
		for(ChartLevelNode node :levelNodes){
			Element chartLevelNode = document.createElement("ChartLevelNode");
			ChartLevelNodes.appendChild(chartLevelNode);
			chartLevelNode.setAttribute("levelNodeIdRef", node.getLevelNodeIdRef());
			chartLevelNode.setAttribute("label", node.getLabel());
		}
		
		HashSet<ChartPlanNode> chartPlanNodesSet = model.getChartPlanNodes();
		Element chartPlanNodes = document.createElement("ChartPlanNodes");
		modelElement.appendChild(chartPlanNodes);
		if(!chartPlanNodesSet.isEmpty()){
			for(ChartPlanNode planNode:chartPlanNodesSet){
				Element chartPlanNode = document.createElement("ChartPlanNode");
				chartPlanNodes.appendChild(chartPlanNode);
				chartPlanNode.setAttribute("planNodeIdRef",planNode.getchartPlanNodeIdRef());
				chartPlanNode.setAttribute("label", planNode.getLabel());
			}
		}
		
		HashSet<PlanNode> planNodeSet = model.getPlanNodes();
		Element planNodes = document.createElement("PlanNodes");
		modelElementTop.appendChild(planNodes);
		
		if(!planNodeSet.isEmpty()){
			for(PlanNode planNode:planNodeSet){
				Element planNodeElement = document.createElement("PlanNode");
				planNodes.appendChild(planNodeElement);
				planNodeElement.setAttribute("id", planNode.getId());
				planNodeElement.setAttribute("name", planNode.getName());
				planNodeElement.setAttribute("startValue", Double.toString(planNode.getStartValue()));
				
				HashSet<PlanNodeIncrement>PlanNodeIncrementSet = planNode.getPlanNodeIncrements();
				for(PlanNodeIncrement planNodeIncrement:PlanNodeIncrementSet){
					Element planNodeIncrementElement = document.createElement("PlanNodeIncrement");
					planNodeElement.appendChild(planNodeIncrementElement);
					planNodeIncrementElement.setAttribute("id", planNodeIncrement.getId());
					planNodeIncrementElement.setAttribute("length", Double.toString(planNodeIncrement.getLength()));
					planNodeIncrementElement.setAttribute("slope", Double.toString(planNodeIncrement.getSlope()));
				}
			}
		}
		
		return document;
	}
}
