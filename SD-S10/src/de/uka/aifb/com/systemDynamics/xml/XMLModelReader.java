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

package de.uka.aifb.com.systemDynamics.xml;

import de.uka.aifb.com.systemDynamics.SystemDynamics;
import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.*;
import de.uka.aifb.com.systemDynamics.model.*;

import java.awt.Color;
import java.awt.geom.*;
import java.io.File;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import javax.xml.xpath.*;

import org.jgraph.graph.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/*
 * Changes:
 * ========
 *
 * 2007-06-20: createModelFromXML was rewritten: root element name as new parameter
 * 2007-08-30: createModelFromXML was rewritten: XMLUselessNodeException can also be caused by
 *             a useless source/sink node
 * 2007-08-30: createGraphFromXML was rewritten: XMLUselessNodeException can also be caused by
 *             a useless source/sink node
 * 2007-09-17: createGraphFromXML was rewritten: using automatic graph layout, if no node positions
 *             are specified in the XML file
 * 2011-12-05: createBinaryFormula was rewritten: added new operations: Divide, Min, Max and Round
 */

/**
 * This class implements an XML input for System Dynamics models stored in an XML file.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.1
 */
/**
 * This class implements an XML input for System Dynamics models stored in an XML file.
 * 
 * @author Pradeep Jawahar, Georgia Institute of Technology, Atlanta
 * @version 1.2
 */
public class XMLModelReader {

	private static HashMap<String,String> levelNodeInfo;
	private static final String XSD_FILE_NAME = "xsd/model1.xsd";

	/**
	 * Reads a System Dynamics model stored in an XML file. The method checks whether the XML file
	 * is well-formed and Schema compliant. Afterwards, the method
	 * {@link de.uka.aifb.com.systemDynamics.model.Model#validateModel()}
	 * is invoked to check for model errors that cannot be expressed in the XML Schema.
	 * 
	 * @param fileName file name
	 * @return System Dynamics model
	 * @throws AuxiliaryNodesCycleDependencyException if the model's auxiliary nodes have a cycle
	 *                                                dependency
	 * @throws XMLModelReaderWriterException if there is any exception (wrapper for inner exception)
	 * @throws XMLNodeParameterOutOfRangeException if a node parameter is out of range
	 * @throws XMLRateNodeFlowException if a rate node has no incoming or no outgoing flow
	 * @throws XMLUselessNodeException if a node has no influence on a level node
	 */
	public static ArrayList<Model> readXMLModel(String fileName) throws AuxiliaryNodesCycleDependencyException,
	XMLModelReaderWriterException,
	XMLNodeParameterOutOfRangeException,
	XMLRateNodeFlowException,
	XMLUselessNodeException {
		if (fileName == null) {
			throw new IllegalArgumentException("'fileName' must not be null.");
		}

		ArrayList<Model> model = new ArrayList<Model>();

		HashMap<String, AuxiliaryNode> id2auxiliaryNode = new HashMap<String, AuxiliaryNode>();
		HashMap<String, ConstantNode> id2constantNode = new HashMap<String, ConstantNode>();
		HashMap<String, LevelNode> id2levelNode = new HashMap<String, LevelNode>();
		HashMap<String, RateNode> id2rateNode = new HashMap<String, RateNode>();
		HashMap<String, SourceSinkNode> id2sourceSinkNode = new HashMap<String, SourceSinkNode>();
		HashMap<String, SharedNode> id2SharedNode = new HashMap<String, SharedNode>();
		/*String currentdir = System.getProperty("user.dir");
      System.out.println(currentdir);
      File dir = new File(currentdir);
      System.out.println("Current Working Directory : "+ dir);*/

		createModelFromXML(fileName, XSD_FILE_NAME, model, "Model", id2auxiliaryNode, id2constantNode,
				id2levelNode, id2rateNode, id2sourceSinkNode, id2SharedNode);

		setLevelNodes(id2levelNode);
		return model;
	}

	public static void setLevelNodes(HashMap<String, LevelNode> id2levelNode){
		HashMap<String,String> levelNodes = new HashMap<String,String>();
		Iterator itMap= id2levelNode.entrySet().iterator();
		while(itMap.hasNext()){
			Map.Entry pairs = (Map.Entry)itMap.next();
			LevelNode lNode = (LevelNode)pairs.getValue();
			levelNodes.put(pairs.getKey().toString(), lNode.getNodeName());
		}

		levelNodeInfo = (HashMap<String,String>)levelNodes.clone();
	}
	/**
	 * Added by Pradeep
	 */
	public static HashMap<String,String> getLevelNodes(){

		return levelNodeInfo;
	}

	/**
	 * Reads a System Dynamics graph stored in an XML file. The method checks whether the XML file
	 * is well-formed and Schema compliant. Afterwards, the method
	 * {@link de.uka.aifb.com.systemDynamics.model.Model#validateModel()}
	 * is invoked to check for model errors that cannot be expressed in the XML Schema.
	 * 
	 * @param fileName file name
	 * @param start @link{de.uka.aifb.com.systemDynamics.SystemDynamics} instance
	 * @param frame frame in which the graph will be displayed
	 * @return System Dynamics graph
	 * @throws AuxiliaryNodesCycleDependencyException if the model's auxiliary nodes have a cycle
	 *                                                dependency
	 * @throws XMLModelReaderWriterException if there is any exception (wrapper for inner exception)
	 * @throws XMLNodeParameterOutOfRangeException if a node parameter is out of range
	 * @throws XMLRateNodeFlowException if a rate node has no incoming or no outgoing flow
	 * @throws XMLUselessNodeException if a node has no influence on a level node
	 */
	public static ArrayList<SystemDynamicsGraph> readXMLSystemDynamicsGraph(String fileName, SystemDynamics start,
			JFrame frame)
					throws AuxiliaryNodesCycleDependencyException,
					XMLModelReaderWriterException,
					XMLNodeParameterOutOfRangeException,
					XMLRateNodeFlowException,
					XMLUselessNodeException {
		if (fileName == null) {
			throw new IllegalArgumentException("'fileName' must not be null.");
		}
		if (start == null) {
			throw new IllegalArgumentException("'start' must not be null.");
		}
		if (frame == null) {
			throw new IllegalArgumentException("'frame' must not be null.");
		}
		ArrayList<SystemDynamicsGraph> graph = new ArrayList<SystemDynamicsGraph>();
		
		HashMap<String, AuxiliaryNodeGraphCell> id2auxiliaryNode =
				new HashMap<String, AuxiliaryNodeGraphCell>();
		HashMap<String, ConstantNodeGraphCell> id2constantNode =
				new HashMap<String, ConstantNodeGraphCell>();
		HashMap<String, LevelNodeGraphCell> id2levelNode =
				new HashMap<String, LevelNodeGraphCell>();
		HashMap<String, RateNodeGraphCell> id2rateNode =
				new HashMap<String, RateNodeGraphCell>();
		HashMap<String, SourceSinkNodeGraphCell> id2sourceSinkNode =
				new HashMap<String, SourceSinkNodeGraphCell>();
		HashMap<String, SharedNodeGraphCell> id2SharedNode =
				new HashMap<String, SharedNodeGraphCell>();

		Thread.currentThread();

		graph = createGraphFromXML(fileName, "xsd/model1.xsd", graph, id2auxiliaryNode, id2constantNode,
				id2levelNode, id2rateNode, id2sourceSinkNode, id2SharedNode, start, frame);

		System.out.println("ID TO LEVEL SIZE" + id2levelNode.size());

		return graph;
	}

	/**
	 * Creates (i.e. completes) a model (given as an input parameter) from the specified XML file.
	 * The model is validated at the end of this method. 
	 * 
	 * @param fileString XML file name
	 * @param xsdFileString XSD file name
	 * @param model (empty) model
	 * @param rootElementName name of root element
	 * @param id2auxiliaryNode id to auxiliary node mapping
	 * @param id2constantNode id to constant node mapping
	 * @param id2levelNode id to level node mapping
	 * @param id2rateNode id to rate node mapping
	 * @param id2sourceSinkNode id to source/sink node mapping
	 * @throws AuxiliaryNodesCycleDependencyException if the model's auxiliary nodes have a cycle
	 *                                                dependency
	 * @throws XMLModelReaderWriterException if there is any exception (wrapper for inner exception)
	 * @throws XMLNodeParameterOutOfRangeException if a node parameter is out of range
	 * @throws XMLRateNodeFlowException if a rate node has no incoming or no outgoing flow
	 * @throws XMLUselessNodeException if a node has no influence on a level node
	 */
	protected static void createModelFromXML(String fileString, String xsdFileString, ArrayList<Model> model,
			String rootElementName,
			HashMap<String, AuxiliaryNode> id2auxiliaryNode,
			HashMap<String, ConstantNode> id2constantNode,
			HashMap<String, LevelNode> id2levelNode,
			HashMap<String, RateNode> id2rateNode,
			HashMap<String, SourceSinkNode> id2sourceSinkNode,
			HashMap<String, SharedNode> id2SharedNode)
					throws AuxiliaryNodesCycleDependencyException,
					XMLModelReaderWriterException,
					XMLNodeParameterOutOfRangeException,
					XMLRateNodeFlowException,
					XMLUselessNodeException {
		if (fileString == null) {
			throw new IllegalArgumentException("'fileString' must not be null.");
		}
		if (xsdFileString == null) {
			throw new IllegalArgumentException("'xsdFileString' must not be null.");
		}
		if (model == null) {
			throw new IllegalArgumentException("'model' must not be null.");
		}
		if (rootElementName == null) {
			throw new IllegalArgumentException("'rootElementName' must not be null.");
		}
		if (id2auxiliaryNode == null) {
			throw new IllegalArgumentException("'id2auxiliaryNode' must not be null.");
		}
		if (id2constantNode == null) {
			throw new IllegalArgumentException("'id2constantNode' must not be null.");
		}
		if (id2levelNode == null) {
			throw new IllegalArgumentException("'id2levelNode' must not be null.");
		}
		if (id2rateNode == null) {
			throw new IllegalArgumentException("'id2rateNode' must not be null.");
		}
		if (id2sourceSinkNode == null) {
			throw new IllegalArgumentException("'id2sourceSinkNode' must not be null.");
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  // can throw FactoryConfiguration Error

		// create schema
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		try {
			schema = schemaFactory.newSchema(new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(xsdFileString)));
		} catch (SAXException e) {
			System.out.println("Pradeep");
			// exception should not happen because schema is correct!
			throw new XMLModelReaderWriterException(e);
		}

		factory.setSchema(schema);

		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// exception should not happen
			throw new XMLModelReaderWriterException(e);
		}

		// set own error handler that throws exception if XML file is not Schema compliant
		builder.setErrorHandler(new MyErrorHandler());
		System.out.println("GOT HERE");
		Document document = null;
		try {
			document = builder.parse(new File(fileString));  // can throw IOException
			// can throw SAXException
		} catch (Exception e) {
			System.out.println("GOT HERE");
			throw new XMLModelReaderWriterException(e);
		}		


		// create XPath object
		XPath xpath = XPathFactory.newInstance().newXPath();
		String modelName = "";
		// set model name
		try {
			Element modelElement = (Element)xpath.evaluate("/Model", document, XPathConstants.NODE);
			modelName = modelElement.getAttribute("name");
			Model blankModel = new Model();
			model.add(blankModel);
			model.get(0).setModelName(modelName);
		} catch (XPathExpressionException e) {
			// correct xpath expression -> no exception
			throw new XMLModelReaderWriterException(e);
		}

		NodeList Submodels = null;

		try {
			Submodels = (NodeList)xpath.evaluate("/Model/SubModel", document, XPathConstants.NODESET);



		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block

			e1.printStackTrace();
		}
		for(int i=1;i<Submodels.getLength();i++){
			Model blankModel = new Model();
			model.add(blankModel);
		}

		for(int k=0;k<Submodels.getLength();k++){

			// (1) create nodes
			// (1a) create level nodes
			try {
				NodeList levelNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/LevelNodes/LevelNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < levelNodeElements.getLength(); i++) {
					Element levelNodeElement = (Element)levelNodeElements.item(i);
					String id = levelNodeElement.getAttribute("id");
					String nodeName = levelNodeElement.getAttribute("name");
					double startValue = new Double(levelNodeElement.getAttribute("startValue"));
					String minName, maxName, curveName;
					double minValue, maxValue, curve;
					minName = levelNodeElement.getAttribute("minValue");
					if(minName.length() == 0)
						minValue = 0;
					else
						minValue = new Double(minName);
					maxName = levelNodeElement.getAttribute("maxValue");
					if(maxName.length() == 0)
						maxValue = 0;
					else
						maxValue = new Double(maxName);
					curveName = levelNodeElement.getAttribute("curve");
					if(curveName.length() == 0)
						curve = 0;
					else
						curve = new Double(curveName);
					boolean learnerDecidable = new Boolean(levelNodeElement.getAttribute("learnerChangeable"));

					LevelNode levelNode = null;
					try {
						levelNode = model.get(k).createLevelNode(nodeName, startValue, minValue, maxValue, curve, learnerDecidable, false);
					} catch (NodeParameterOutOfRangeException e) {
						throw new XMLNodeParameterOutOfRangeException(id, e.getMinValue(), e.getMaxValue());
					}
					id2levelNode.put(id, levelNode);
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (1b) create source/sink nodes
			try {
				NodeList sourceSinkNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/SourceSinkNodes/SourceSinkNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < sourceSinkNodeElements.getLength(); i++) {
					Element sourceSinkNodeElement = (Element)sourceSinkNodeElements.item(i);
					String id = sourceSinkNodeElement.getAttribute("id");

					SourceSinkNode sourceSinkNode = model.get(k).createSourceSinkNode(false);
					id2sourceSinkNode.put(id, sourceSinkNode);
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}
			// (1b) create shared nodes
			try {
				NodeList sharedNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/SharedNodes/SharedNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < sharedNodeElements.getLength(); i++) {
					Element SharedElement = (Element)sharedNodeElements.item(i);
					String id = SharedElement.getAttribute("sharedPointer");
					String value = SharedElement.getAttribute("value");
					SharedNode sharedNode = model.get(k).createSharedNode(id, Double.parseDouble(value));
					id2SharedNode.put(id, sharedNode);
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (1c) create rate nodes
			try {
				NodeList rateNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/RateNodes/RateNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < rateNodeElements.getLength(); i++) {
					Element rateNodeElement = (Element)rateNodeElements.item(i);
					String id = rateNodeElement.getAttribute("id");
					String nodeName = rateNodeElement.getAttribute("name");
					boolean learnerDecidable = new Boolean(rateNodeElement.getAttribute("learnerChangeable"));
					RateNode rateNode = model.get(k).createRateNode(nodeName, learnerDecidable, false);
					id2rateNode.put(id, rateNode);
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (1d) create auxiliary nodes
			try {
				NodeList auxiliaryNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/AuxiliaryNodes/AuxiliaryNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < auxiliaryNodeElements.getLength(); i++) {
					Element auxiliaryNodeElement = (Element)auxiliaryNodeElements.item(i);
					String id = auxiliaryNodeElement.getAttribute("id");
					String nodeName = auxiliaryNodeElement.getAttribute("name");
					boolean learnerDecidable = new Boolean(auxiliaryNodeElement.getAttribute("learnerChangeable"));
					AuxiliaryNode auxiliaryNode = model.get(k).createAuxiliaryNode(nodeName, learnerDecidable, false);
					id2auxiliaryNode.put(id, auxiliaryNode);
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (1e) create constant nodes
			try {
				NodeList constantNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/ConstantNodes/ConstantNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < constantNodeElements.getLength(); i++) {
					Element constantNodeElement = (Element)constantNodeElements.item(i);
					String id = constantNodeElement.getAttribute("id");
					String nodeName = constantNodeElement.getAttribute("name");
					double constantValue = new Double(constantNodeElement.getAttribute("constantValue"));
					boolean learnerDecidable = new Boolean(constantNodeElement.getAttribute("learnerChangeable"));
					ConstantNode constantNode = null;
					try {
						constantNode = model.get(k).createConstantNode(nodeName, constantValue, learnerDecidable, false);
					} catch (NodeParameterOutOfRangeException e) {
						throw new XMLNodeParameterOutOfRangeException(id, e.getMinValue(), e.getMaxValue());
					}
					id2constantNode.put(id, constantNode);
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (2) set formulas
			// (2a) set formulas of auxiliary nodes
			try {
				NodeList auxiliaryNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/AuxiliaryNodes/AuxiliaryNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < auxiliaryNodeElements.getLength(); i++) {
					Element auxiliaryNodeElement = (Element)auxiliaryNodeElements.item(i);
					String id = auxiliaryNodeElement.getAttribute("id");
					Element formulaElement = (Element)xpath.evaluate("./Formula", auxiliaryNodeElement,
							XPathConstants.NODE);
					ASTElement formula = createFormula(formulaElement, id2auxiliaryNode, id2constantNode,
							id2levelNode, id2rateNode);
					model.get(k).setFormula(id2auxiliaryNode.get(id), formula);
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (2b) set formulas of rate nodes
			try {
				NodeList rateNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/RateNodes/RateNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < rateNodeElements.getLength(); i++) {
					Element rateNodeElement = (Element)rateNodeElements.item(i);
					String id = rateNodeElement.getAttribute("id");
					Element formulaElement = (Element)xpath.evaluate("./Formula", rateNodeElement,
							XPathConstants.NODE);
					ASTElement formula = createFormula(formulaElement, id2auxiliaryNode, id2constantNode,
							id2levelNode, id2rateNode);
					model.get(k).setFormula(id2rateNode.get(id), formula);
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (3) create flows
			// (3a) incoming flows of level nodes
			try {
				NodeList incomingFlowElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Flows/RateNode2LevelNodeFlow", document,
								XPathConstants.NODESET);
				for (int i = 0; i < incomingFlowElements.getLength(); i++) {
					Element incomingFlowElement = (Element)incomingFlowElements.item(i);
					String rateNodeId = incomingFlowElement.getAttribute("fromRateNodeIdRef");
					String levelNodeId = incomingFlowElement.getAttribute("toLevelNodeIdRef");

					RateNode rateNode = id2rateNode.get(rateNodeId);
					LevelNode levelNode = id2levelNode.get(levelNodeId);
					model.get(k).addFlowFromRateNode2LevelNode(rateNode, levelNode);
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (3b) outgoing flows of level nodes
			try {
				NodeList outgoingFlowElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Flows/LevelNode2RateNodeFlow", document,
								XPathConstants.NODESET);
				for (int i = 0; i < outgoingFlowElements.getLength(); i++) {
					Element outgoingFlowElement = (Element)outgoingFlowElements.item(i);
					String levelNodeId = outgoingFlowElement.getAttribute("fromLevelNodeIdRef");
					String rateNodeId = outgoingFlowElement.getAttribute("toRateNodeIdRef");

					LevelNode levelNode = id2levelNode.get(levelNodeId);
					RateNode rateNode = id2rateNode.get(rateNodeId);
					model.get(k).addFlowFromLevelNode2RateNode(levelNode, rateNode);
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (3c) incoming flows of source/sink nodes
			try {
				NodeList incomingFlowElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Flows/RateNode2SourceSinkNodeFlow", document,
								XPathConstants.NODESET);
				for (int i = 0; i < incomingFlowElements.getLength(); i++) {
					Element incomingFlowElement = (Element)incomingFlowElements.item(i);
					String rateNodeId = incomingFlowElement.getAttribute("fromRateNodeIdRef");
					String sourceSinkNodeId = incomingFlowElement.getAttribute("toSourceSinkNodeIdRef");

					RateNode rateNode = id2rateNode.get(rateNodeId);
					SourceSinkNode sourceSinkNode = id2sourceSinkNode.get(sourceSinkNodeId);
					model.get(k).addFlowFromRateNode2SourceSinkNode(rateNode, sourceSinkNode);
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (3d) outgoing flows of source/sink
			try {
				NodeList outgoingFlowElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Flows/SourceSinkNode2RateNodeFlow", document,
								XPathConstants.NODESET);
				for (int i = 0; i < outgoingFlowElements.getLength(); i++) {
					Element outgoingFlowElement = (Element)outgoingFlowElements.item(i);
					String sourceSinkNodeId = outgoingFlowElement.getAttribute("fromSourceSinkNodeIdRef");
					String rateNodeId = outgoingFlowElement.getAttribute("toRateNodeIdRef");

					SourceSinkNode sourceSinkNode = id2sourceSinkNode.get(sourceSinkNodeId);
					RateNode rateNode = id2rateNode.get(rateNodeId);
					model.get(k).addFlowFromSourceSinkNode2RateNode(sourceSinkNode, rateNode);
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// is model valide? (check for errors that cannot be expressed in the XML Schema)
			try {
				model.get(k).validateModel(0);
			} catch (RateNodeFlowException e) {
				// search problematic rate node ID
				RateNode problematicRateNode = e.getProblematicRateNode();
				for (String id : id2rateNode.keySet()) {
					if (id2rateNode.get(id) == problematicRateNode) {
						throw new XMLRateNodeFlowException(id);
					}
				}
			} catch (UselessNodeException e) {
				// search problematic node ID (only constant, auxiliary or source/sink node possible!)
				AbstractNode problematicNode = e.getUselessNode();
				for (String id : id2constantNode.keySet()) {
					if (id2constantNode.get(id) == problematicNode) {
						throw new XMLUselessNodeException(id);
					}
				}
				for (String id : id2auxiliaryNode.keySet()) {
					if (id2auxiliaryNode.get(id) == problematicNode) {
						throw new XMLUselessNodeException(id);
					}
				}
				for (String id : id2sourceSinkNode.keySet()) {
					if (id2sourceSinkNode.get(id) == problematicNode) {
						throw new XMLUselessNodeException(id);
					}
				}
			} catch (NoFormulaException e) {
				// that must not happen -> SAXException is thrown earlier
				throw new XMLModelReaderWriterException(e);
			} catch (NoLevelNodeException e) {
				// that must not happen -> SAXException is thrown earlier
				throw new XMLModelReaderWriterException(e);
			}
		}
	}

	/**
	 * Creates (i.e. completes) a model (given as an input parameter) from the specified XML file.
	 * The model is validated at the end of this method. 
	 * 
	 * @param fileString XML file name
	 * @param xsdFileString XSD file name
	 * @param graph (empty) System Dynamics graph
	 * @param id2auxiliaryNodeGraphCell id to auxiliary node graph cell mapping
	 * @param id2constantNodeGraphCell id to constant node graph cell mapping
	 * @param id2levelNodeGraphCell id to level node graph cell mapping
	 * @param id2rateNodeGraphCell id to rate node graph cell mapping
	 * @param id2sourceSinkNodeGraphCell id to source/sink node graph cell mapping
	 * @throws AuxiliaryNodesCycleDependencyException if the model's auxiliary nodes have a cycle
	 *                                                dependency
	 * @throws XMLModelReaderWriterException if there is any exception (wrapper for inner exception)
	 * @throws XMLNodeParameterOutOfRangeException if a node parameter is out of range
	 * @throws XMLRateNodeFlowException if a rate node has no incoming or no outgoing flow
	 * @throws XMLUselessNodeException if a node has no influence on a level node
	 */
	protected static ArrayList<SystemDynamicsGraph> createGraphFromXML(String fileString, String xsdFileString,
			ArrayList<SystemDynamicsGraph> graph,
			HashMap<String, AuxiliaryNodeGraphCell> id2auxiliaryNodeGraphCell,
			HashMap<String, ConstantNodeGraphCell> id2constantNodeGraphCell,
			HashMap<String, LevelNodeGraphCell> id2levelNodeGraphCell,
			HashMap<String, RateNodeGraphCell> id2rateNodeGraphCell,
			HashMap<String, SourceSinkNodeGraphCell> id2sourceSinkNodeGraphCell,
			HashMap<String, SharedNodeGraphCell> id2SharedNodeGraphCell, SystemDynamics start,
			JFrame frame)
					throws AuxiliaryNodesCycleDependencyException,
					XMLModelReaderWriterException,
					XMLNodeParameterOutOfRangeException,
					XMLRateNodeFlowException,
					XMLUselessNodeException {
		
		if (fileString == null) {
			throw new IllegalArgumentException("'fileString' must not be null.");
		}
		if (xsdFileString == null) {
			throw new IllegalArgumentException("'xsdFileString' must not be null.");
		}
		if (graph == null) {
			throw new IllegalArgumentException("'graph' must not be null.");
		}
		if (id2auxiliaryNodeGraphCell == null) {
			throw new IllegalArgumentException("'id2auxiliaryNodeGraphCell' must not be null.");
		}
		if (id2constantNodeGraphCell == null) {
			throw new IllegalArgumentException("'id2constantNodeGraphCell' must not be null.");
		}
		if (id2levelNodeGraphCell == null) {
			throw new IllegalArgumentException("'id2levelNodeGraphCell' must not be null.");
		}
		if (id2rateNodeGraphCell == null) {
			throw new IllegalArgumentException("'id2rateNodeGraphCell' must not be null.");
		}
		if (id2sourceSinkNodeGraphCell == null) {
			throw new IllegalArgumentException("'id2sourceSinkNodeGraphCell' must not be null.");
		}

		HashMap<DefaultGraphCell, AutomaticGraphLayout.Vertex> graphCell2Vertex =
				new HashMap<DefaultGraphCell, AutomaticGraphLayout.Vertex>();
		HashMap<AbstractNode, AutomaticGraphLayout.Vertex> abstractNode2Vertex =
				new HashMap<AbstractNode, AutomaticGraphLayout.Vertex>();
		AutomaticGraphLayout graphLayout = new AutomaticGraphLayout(800, 500);

		boolean automaticGraphLayoutNecessary = true;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  // can throw FactoryConfiguration Error

		// create schema
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		try {
			schema = schemaFactory.newSchema(new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(xsdFileString)));
		} catch (SAXException e) {
			System.out.println("\nSubbu\n");
			// exception should not happen because schema is correct!
			throw new XMLModelReaderWriterException(e);
		}
		
		factory.setSchema(schema);
		
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// exception should not happen
			throw new XMLModelReaderWriterException(e);
		}
		
		// set own error handler that throws exception if XML file is not Schema compliant
		builder.setErrorHandler(new MyErrorHandler());

		Document document = null;
		
		try {

			document = builder.parse(new File(fileString));  // can throw IOException
			// can throw SAXException
		} catch (Exception e) {
			throw new XMLModelReaderWriterException(e);
		}

		
		// create XPath object
		XPath xpath = XPathFactory.newInstance().newXPath();
		String modelName = "";
		// set model name
		try {
			Element modelElement = (Element)xpath.evaluate("/Model", document, XPathConstants.NODE);
			modelName = modelElement.getAttribute("name");

		} catch (XPathExpressionException e) {
			// correct xpath expression -> no exception
			throw new XMLModelReaderWriterException(e);
		}

		NodeList Submodels = null;

		try {
			Submodels = (NodeList)xpath.evaluate("/Model/SubModel", document, XPathConstants.NODESET);



		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for(int i=0;i<Submodels.getLength();i++){

			String SubmodelColor = Submodels.item(i).getAttributes().getNamedItem("color").getNodeValue();

			String[] test = new String[3];
			test = SubmodelColor.split(", ");
			Color SubmodelBorder = new Color(Integer.parseInt(test[0]), Integer.parseInt(test[1]),Integer.parseInt(test[2]));
			SystemDynamicsGraph SubmodelGraph = new SystemDynamicsGraph(start,frame);
			Border SubmodelColorBorder = BorderFactory.createLineBorder(SubmodelBorder,15);
			SubmodelGraph.setBorder(SubmodelColorBorder);
			SubmodelGraph.borderColor = SubmodelBorder;

			SubmodelGraph.setSize(400,400);
			if(i==0){
				SubmodelGraph.setModelName(modelName);	
			}
			graph.add(SubmodelGraph);

		}
		
		
		for(int k=0;k<graph.size();k++){// (1) create nodes
			// (1a) create level nodes
			try {

				NodeList levelNodeElements = 
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/LevelNodes/LevelNode", document,
								XPathConstants.NODESET);


				for (int i = 0; i < levelNodeElements.getLength(); i++) {
					Element levelNodeElement = (Element)levelNodeElements.item(i);
					String id = levelNodeElement.getAttribute("id");
					String nodeName = levelNodeElement.getAttribute("name");
					double startValue = new Double(levelNodeElement.getAttribute("startValue"));
					double minValue = new Double(levelNodeElement.getAttribute("minValue"));
					double maxValue = new Double(levelNodeElement.getAttribute("maxValue"));
					double curve = new Double(levelNodeElement.getAttribute("curve"));
					double xCoordinate = new Double(levelNodeElement.getAttribute("xCoordinate"));
					double yCoordinate = new Double(levelNodeElement.getAttribute("yCoordinate"));
					boolean learnerDecidable = new Boolean(levelNodeElement.getAttribute("learnerChangeable"));
					boolean shared = new Boolean(levelNodeElement.getAttribute("shared"));

					LevelNodeGraphCell levelNode = null;
					try {

						levelNode = graph.get(k).createLevelNodeGraphCell(nodeName, startValue, minValue, maxValue, curve, xCoordinate, yCoordinate, learnerDecidable, shared);
						graph.get(k).model.createLevelNode(nodeName, startValue, minValue, maxValue, curve, learnerDecidable, shared);
					} catch (NodeParameterOutOfRangeException e) {
						throw new XMLNodeParameterOutOfRangeException(id, e.getMinValue(), e.getMaxValue());
					}
					id2levelNodeGraphCell.put(id, levelNode);
					
					AutomaticGraphLayout.Vertex vertex = graphLayout.createVertex();
					graphCell2Vertex.put(levelNode, vertex);
					

					abstractNode2Vertex.put(graph.get(k).getModelNode(levelNode), vertex);
					
					if (xCoordinate != 10.0 && yCoordinate != 10.0) {
						automaticGraphLayoutNecessary = false;
					}
				}
			}catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}
			// (1b) create shared nodes
						try {
							NodeList sourceSinkNodeElements =
									(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/SharedNodes/SharedNode", document,
											XPathConstants.NODESET);
							for (int i = 0; i < sourceSinkNodeElements.getLength(); i++) {
								Element SharedElement = (Element)sourceSinkNodeElements.item(i);
								String id = SharedElement.getAttribute("sharedPointer");
								String NodeType = SharedElement.getAttribute("sharedPointerid");
								String value = SharedElement.getAttribute("value");
								SharedNodeGraphCell sharedNode = null;
								if(NodeType.contains("LN")) {
									sharedNode = graph.get(k).createSharedNodeGraphCell(id, "Level", Double.parseDouble(value));
								}
								else if(NodeType.contains("CN")) {
									sharedNode = graph.get(k).createSharedNodeGraphCell(id, "Constant", Double.parseDouble(value));
								}
								else if(NodeType.contains("AN")) {
									sharedNode = graph.get(k).createSharedNodeGraphCell(id, "Auxiliary", Double.parseDouble(value));
								}
								else{
									sharedNode = graph.get(k).createSharedNodeGraphCell(id, "other", Double.parseDouble(value));
								}
								graph.get(k).model.createSharedNode(id, Double.parseDouble(value));
								id2SharedNodeGraphCell.put(id, sharedNode);
							}
						} catch (XPathExpressionException e) {
							// correct xpath expression -> no exception
							throw new XMLModelReaderWriterException(e);
						}
			// (1b) create source/sink nodes
			try {
				NodeList sourceSinkNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/SourceSinkNodes/SourceSinkNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < sourceSinkNodeElements.getLength(); i++) {
					Element sourceSinkNodeElement = (Element)sourceSinkNodeElements.item(i);
					String id = sourceSinkNodeElement.getAttribute("id");
					double xCoordinate = new Double(sourceSinkNodeElement.getAttribute("xCoordinate"));
					double yCoordinate = new Double(sourceSinkNodeElement.getAttribute("yCoordinate"));
					boolean shared = new Boolean(sourceSinkNodeElement.getAttribute("shared"));

					SourceSinkNodeGraphCell sourceSinkNode = graph.get(k).createSourceSinkNodeGraphCell(xCoordinate,
							yCoordinate, shared);
					id2sourceSinkNodeGraphCell.put(id, sourceSinkNode);
					AutomaticGraphLayout.Vertex vertex = graphLayout.createVertex();
					graphCell2Vertex.put(sourceSinkNode, vertex);
					abstractNode2Vertex.put(graph.get(k).getModelNode(sourceSinkNode), vertex);
					if (xCoordinate != 10.0 && yCoordinate != 10.0) {
						automaticGraphLayoutNecessary = false;
					}
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}
			//CUT FROM HERE
			// (1c) create rate nodes
			try {
				NodeList rateNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/RateNodes/RateNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < rateNodeElements.getLength(); i++) {
					Element rateNodeElement = (Element)rateNodeElements.item(i);
					String id = rateNodeElement.getAttribute("id");
					String nodeName = rateNodeElement.getAttribute("name");
					double xCoordinate = new Double(rateNodeElement.getAttribute("xCoordinate"));
					double yCoordinate = new Double(rateNodeElement.getAttribute("yCoordinate"));
					boolean learnerDecidable = new Boolean(rateNodeElement.getAttribute("learnerChangeable"));
					boolean shared = new Boolean(rateNodeElement.getAttribute("shared"));

					RateNodeGraphCell rateNode = graph.get(k).createRateNodeGraphCell(nodeName, xCoordinate,
							yCoordinate, learnerDecidable, shared);
					id2rateNodeGraphCell.put(id, rateNode);
					AutomaticGraphLayout.Vertex vertex = graphLayout.createVertex();
					graphCell2Vertex.put(rateNode, vertex);
					abstractNode2Vertex.put(graph.get(k).getModelNode(rateNode), vertex);
					if (xCoordinate != 10.0 && yCoordinate != 10.0) {
						automaticGraphLayoutNecessary = false;
					}
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (1d) create auxiliary nodes
			try {
				NodeList auxiliaryNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/AuxiliaryNodes/AuxiliaryNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < auxiliaryNodeElements.getLength(); i++) {
					Element auxiliaryNodeElement = (Element)auxiliaryNodeElements.item(i);
					String id = auxiliaryNodeElement.getAttribute("id");
					String nodeName = auxiliaryNodeElement.getAttribute("name");
					double xCoordinate = new Double(auxiliaryNodeElement.getAttribute("xCoordinate"));
					double yCoordinate = new Double(auxiliaryNodeElement.getAttribute("yCoordinate"));
					boolean learnerDecidable = new Boolean(auxiliaryNodeElement.getAttribute("learnerChangeable"));
					boolean shared = new Boolean(auxiliaryNodeElement.getAttribute("shared"));
					//
					AuxiliaryNodeGraphCell auxiliaryNode = graph.get(k).createAuxiliaryNodeGraphCell(nodeName,
							xCoordinate,
							yCoordinate, learnerDecidable, false);
					id2auxiliaryNodeGraphCell.put(id, auxiliaryNode);
					AutomaticGraphLayout.Vertex vertex = graphLayout.createVertex();
					graphCell2Vertex.put(auxiliaryNode, vertex);
					abstractNode2Vertex.put(graph.get(k).getModelNode(auxiliaryNode), vertex);
					if (xCoordinate != 10.0 && yCoordinate != 10.0) {
						automaticGraphLayoutNecessary = false;
					}
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (1e) create constant nodes
			try {
				NodeList constantNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/ConstantNodes/ConstantNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < constantNodeElements.getLength(); i++) {
					Element constantNodeElement = (Element)constantNodeElements.item(i);
					String id = constantNodeElement.getAttribute("id");
					String nodeName = constantNodeElement.getAttribute("name");
					double constantValue = new Double(constantNodeElement.getAttribute("constantValue"));
					double xCoordinate = new Double(constantNodeElement.getAttribute("xCoordinate"));
					double yCoordinate = new Double(constantNodeElement.getAttribute("yCoordinate"));
					boolean learnerDecidable = new Boolean(constantNodeElement.getAttribute("learnerChangeable"));
					boolean shared = new Boolean(constantNodeElement.getAttribute("shared"));
					ConstantNodeGraphCell constantNode = null;
					try {

						constantNode = graph.get(k).createConstantNodeGraphCell(nodeName, constantValue,
								xCoordinate, yCoordinate, learnerDecidable, shared);
					} catch (NodeParameterOutOfRangeException e) {
						throw new XMLNodeParameterOutOfRangeException(id, e.getMinValue(), e.getMaxValue());
					}
					id2constantNodeGraphCell.put(id, constantNode);
					AutomaticGraphLayout.Vertex vertex = graphLayout.createVertex();
					graphCell2Vertex.put(constantNode, vertex);
					abstractNode2Vertex.put(graph.get(k).getModelNode(constantNode), vertex);
					if (xCoordinate != 10.0 && yCoordinate != 10.0) {
						automaticGraphLayoutNecessary = false;
					}
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			HashMap<String, AuxiliaryNode> id2auxiliaryNode = new HashMap<String, AuxiliaryNode>();
			for (String id : id2auxiliaryNodeGraphCell.keySet()) {
				id2auxiliaryNode.put(id, (AuxiliaryNode)graph.get(k).getModelNode(id2auxiliaryNodeGraphCell.get(id)));
			}
			HashMap<String, ConstantNode> id2constantNode = new HashMap<String, ConstantNode>();
			for (String id : id2constantNodeGraphCell.keySet()) {
				id2constantNode.put(id, (ConstantNode)graph.get(k).getModelNode(id2constantNodeGraphCell.get(id)));
			}
			HashMap<String, LevelNode> id2levelNode = new HashMap<String, LevelNode>();
			for (String id : id2levelNodeGraphCell.keySet()) {
				id2levelNode.put(id, (LevelNode)graph.get(k).getModelNode(id2levelNodeGraphCell.get(id)));
			}
			HashMap<String, RateNode> id2rateNode = new HashMap<String, RateNode>();
			for (String id : id2rateNodeGraphCell.keySet()) {
				id2rateNode.put(id, (RateNode)graph.get(k).getModelNode(id2rateNodeGraphCell.get(id)));
			}
			HashMap<String, SourceSinkNode> id2sourceSinkNode = new HashMap<String, SourceSinkNode>();
			for (String id : id2sourceSinkNodeGraphCell.keySet()) {
				id2sourceSinkNode.put(id, (SourceSinkNode)graph.get(k).getModelNode(id2sourceSinkNodeGraphCell.get(id)));
			}

			// (2) set formulas (and dependency edges)
			// (2a) set formulas of auxiliary nodes
			try {
				NodeList auxiliaryNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/AuxiliaryNodes/AuxiliaryNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < auxiliaryNodeElements.getLength(); i++) {
					Element auxiliaryNodeElement = (Element)auxiliaryNodeElements.item(i);
					String id = auxiliaryNodeElement.getAttribute("id");
					Element formulaElement = (Element)xpath.evaluate("./Formula", auxiliaryNodeElement,
							XPathConstants.NODE);
					ASTElement formula = createFormula(formulaElement, id2auxiliaryNode, id2constantNode,
							id2levelNode, id2rateNode);
					graph.get(k).setFormula(id2auxiliaryNodeGraphCell.get(id), formula, false);
					for (AbstractNode node : formula.getAllNodesInASTSubtree()) {
						graphLayout.createEdge(abstractNode2Vertex.get(node), graphCell2Vertex.get(id2auxiliaryNodeGraphCell.get(id)));
					}
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}
			
			// (2b) set formulas of rate nodes
			try {
				NodeList rateNodeElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Nodes/RateNodes/RateNode", document,
								XPathConstants.NODESET);
				for (int i = 0; i < rateNodeElements.getLength(); i++) {
					Element rateNodeElement = (Element)rateNodeElements.item(i);
					String id = rateNodeElement.getAttribute("id");
					Element formulaElement = (Element)xpath.evaluate("./Formula", rateNodeElement,
							XPathConstants.NODE);
					ASTElement formula = createFormula(formulaElement, id2auxiliaryNode, id2constantNode,
							id2levelNode, id2rateNode);
					graph.get(k).setFormula(id2rateNodeGraphCell.get(id), formula, false);
					for (AbstractNode node : formula.getAllNodesInASTSubtree()) {
						graphLayout.createEdge(abstractNode2Vertex.get(node), graphCell2Vertex.get(id2rateNodeGraphCell.get(id)));
					}
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}
			
			// (3) create flows
			// (3a) incoming flows of level nodes
			try {
				NodeList incomingFlowElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Flows/RateNode2LevelNodeFlow", document,
								XPathConstants.NODESET);
				for (int i = 0; i < incomingFlowElements.getLength(); i++) {
					Element incomingFlowElement = (Element)incomingFlowElements.item(i);
					String rateNodeId = incomingFlowElement.getAttribute("fromRateNodeIdRef");
					String levelNodeId = incomingFlowElement.getAttribute("toLevelNodeIdRef");

					RateNodeGraphCell rateNode = id2rateNodeGraphCell.get(rateNodeId);
					LevelNodeGraphCell levelNode = id2levelNodeGraphCell.get(levelNodeId);
					graph.get(k).addFlow(rateNode, levelNode);
					graphLayout.createEdge(graphCell2Vertex.get(rateNode), graphCell2Vertex.get(levelNode));

					// control points
					LinkedList<Point2D> points = new LinkedList<Point2D>();
					NodeList additionalControlPointsNodeList = incomingFlowElement.getElementsByTagName("AdditionalControlPoints");
					if (additionalControlPointsNodeList != null && additionalControlPointsNodeList.getLength() > 0) {
						// has additional control points
						Element additionalControlPointsElement = (Element)additionalControlPointsNodeList.item(0);
						NodeList nodeList = additionalControlPointsElement.getElementsByTagName("AdditionalControlPoint");
						if (nodeList != null) {
							for (int j = 0; j < nodeList.getLength(); j++) {
								Element additionalControlPointElement = (Element)nodeList.item(j);
								double xCoordinate = new Double(additionalControlPointElement.getAttribute("xCoordinate"));
								double yCoordinate = new Double(additionalControlPointElement.getAttribute("yCoordinate"));
								points.add(new Point2D.Double(xCoordinate, yCoordinate));
								graph.get(k).changeFlowEdgeControlPoints(rateNode, levelNode, points);
							}
						}
					}        
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (3b) outgoing flows of level nodes
			try {
				NodeList outgoingFlowElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Flows/LevelNode2RateNodeFlow", document,
								XPathConstants.NODESET);
				for (int i = 0; i < outgoingFlowElements.getLength(); i++) {
					Element outgoingFlowElement = (Element)outgoingFlowElements.item(i);
					String levelNodeId = outgoingFlowElement.getAttribute("fromLevelNodeIdRef");
					String rateNodeId = outgoingFlowElement.getAttribute("toRateNodeIdRef");

					LevelNodeGraphCell levelNode = id2levelNodeGraphCell.get(levelNodeId);
					RateNodeGraphCell rateNode = id2rateNodeGraphCell.get(rateNodeId);
					graph.get(k).addFlow(levelNode, rateNode);
					graphLayout.createEdge(graphCell2Vertex.get(levelNode), graphCell2Vertex.get(rateNode));

					// control points
					LinkedList<Point2D> points = new LinkedList<Point2D>();
					NodeList additionalControlPointsNodeList = outgoingFlowElement.getElementsByTagName("AdditionalControlPoints");
					if (additionalControlPointsNodeList != null && additionalControlPointsNodeList.getLength() > 0) {
						// has additional control points
						Element additionalControlPointsElement = (Element)additionalControlPointsNodeList.item(0);
						NodeList nodeList = additionalControlPointsElement.getElementsByTagName("AdditionalControlPoint");
						if (nodeList != null) {
							for (int j = 0; j < nodeList.getLength(); j++) {
								Element additionalControlPointElement = (Element)nodeList.item(j);
								double xCoordinate = new Double(additionalControlPointElement.getAttribute("xCoordinate"));
								double yCoordinate = new Double(additionalControlPointElement.getAttribute("yCoordinate"));
								points.add(new Point2D.Double(xCoordinate, yCoordinate));
								graph.get(k).changeFlowEdgeControlPoints(levelNode, rateNode, points);
							}
						}
					}        
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (3c) incoming flows of source/sink nodes
			try {
				NodeList incomingFlowElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Flows/RateNode2SourceSinkNodeFlow", document,
								XPathConstants.NODESET);
				for (int i = 0; i < incomingFlowElements.getLength(); i++) {
					Element incomingFlowElement = (Element)incomingFlowElements.item(i);
					String rateNodeId = incomingFlowElement.getAttribute("fromRateNodeIdRef");
					String sourceSinkNodeId = incomingFlowElement.getAttribute("toSourceSinkNodeIdRef");

					RateNodeGraphCell rateNode = id2rateNodeGraphCell.get(rateNodeId);
					SourceSinkNodeGraphCell sourceSinkNode = id2sourceSinkNodeGraphCell.get(sourceSinkNodeId);
					graph.get(k).addFlow(rateNode, sourceSinkNode);
					graphLayout.createEdge(graphCell2Vertex.get(rateNode), graphCell2Vertex.get(sourceSinkNode));

					// control points
					LinkedList<Point2D> points = new LinkedList<Point2D>();
					NodeList additionalControlPointsNodeList = incomingFlowElement.getElementsByTagName("AdditionalControlPoints");
					if (additionalControlPointsNodeList != null && additionalControlPointsNodeList.getLength() > 0) {
						// has additional control points
						Element additionalControlPointsElement = (Element)additionalControlPointsNodeList.item(0);
						NodeList nodeList = additionalControlPointsElement.getElementsByTagName("AdditionalControlPoint");
						if (nodeList != null) {
							for (int j = 0; j < nodeList.getLength(); j++) {
								Element additionalControlPointElement = (Element)nodeList.item(j);
								double xCoordinate = new Double(additionalControlPointElement.getAttribute("xCoordinate"));
								double yCoordinate = new Double(additionalControlPointElement.getAttribute("yCoordinate"));
								points.add(new Point2D.Double(xCoordinate, yCoordinate));
								graph.get(k).changeFlowEdgeControlPoints(rateNode, sourceSinkNode, points);
							}
						}
					}        
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}
			// (3d) outgoing flows of source/sink
			try {
				NodeList outgoingFlowElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Flows/SourceSinkNode2RateNodeFlow", document,
								XPathConstants.NODESET);
				for (int i = 0; i < outgoingFlowElements.getLength(); i++) {
					Element outgoingFlowElement = (Element)outgoingFlowElements.item(i);
					String sourceSinkNodeId = outgoingFlowElement.getAttribute("fromSourceSinkNodeIdRef");
					String rateNodeId = outgoingFlowElement.getAttribute("toRateNodeIdRef");

					SourceSinkNodeGraphCell sourceSinkNode = id2sourceSinkNodeGraphCell.get(sourceSinkNodeId);
					RateNodeGraphCell rateNode = id2rateNodeGraphCell.get(rateNodeId);
					graph.get(k).addFlow(sourceSinkNode, rateNode);
					graphLayout.createEdge(graphCell2Vertex.get(sourceSinkNode), graphCell2Vertex.get(rateNode));

					// control points
					LinkedList<Point2D> points = new LinkedList<Point2D>();
					NodeList additionalControlPointsNodeList = outgoingFlowElement.getElementsByTagName("AdditionalControlPoints");
					if (additionalControlPointsNodeList != null && additionalControlPointsNodeList.getLength() > 0) {
						// has additional control points
						Element additionalControlPointsElement = (Element)additionalControlPointsNodeList.item(0);
						NodeList nodeList = additionalControlPointsElement.getElementsByTagName("AdditionalControlPoint");
						if (nodeList != null) {
							for (int j = 0; j < nodeList.getLength(); j++) {
								Element additionalControlPointElement = (Element)nodeList.item(j);
								double xCoordinate = new Double(additionalControlPointElement.getAttribute("xCoordinate"));
								double yCoordinate = new Double(additionalControlPointElement.getAttribute("yCoordinate"));
								points.add(new Point2D.Double(xCoordinate, yCoordinate));
								graph.get(k).changeFlowEdgeControlPoints(sourceSinkNode, rateNode, points);
							}
						}
					}        
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}
			
			// automatic graph layout (if necessary)
			if (automaticGraphLayoutNecessary) {
				graphLayout.doLayout();

				for (DefaultGraphCell graphCell : graphCell2Vertex.keySet()) {
					AutomaticGraphLayout.Vertex vertex = graphCell2Vertex.get(graphCell);

					Rectangle2D oldBounds = GraphConstants.getBounds(graphCell.getAttributes());
					Rectangle2D bounds = new Rectangle2D.Double(vertex.getX(), vertex.getY(), oldBounds.getWidth(), oldBounds.getHeight());
					GraphConstants.setBounds(graphCell.getAttributes(), bounds);
					VertexView vertexView = (VertexView)graph.get(k).getGraphLayoutCache().getMapping(graphCell, false);
					graph.get(k).getGraphLayoutCache().refresh(vertexView, false);
				}
			}

			// (4) change dependency edge control points
			// (4a) auxiliary node 2 auxiliay node dependency edges
			try {
				NodeList auxiliaryNode2auxiliaryNodeDependencyElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Dependencies/AuxiliaryNode2AuxiliaryNodeDependency",
								document, XPathConstants.NODESET);
				for (int i = 0; i < auxiliaryNode2auxiliaryNodeDependencyElements.getLength(); i++) {
					Element auxiliaryNode2auxiliaryNodeDependencyElement =
							(Element)auxiliaryNode2auxiliaryNodeDependencyElements.item(i);
					String auxiliaryNode1Id =
							auxiliaryNode2auxiliaryNodeDependencyElement.getAttribute("fromAuxiliaryNodeIdRef");
					String auxiliaryNode2Id =
							auxiliaryNode2auxiliaryNodeDependencyElement.getAttribute("toAuxiliaryNodeIdRef");

					AuxiliaryNodeGraphCell auxiliaryNode1 = id2auxiliaryNodeGraphCell.get(auxiliaryNode1Id);
					AuxiliaryNodeGraphCell auxiliaryNode2 = id2auxiliaryNodeGraphCell.get(auxiliaryNode2Id);

					// control points
					LinkedList<Point2D> points = new LinkedList<Point2D>();
					NodeList additionalControlPointsNodeList =
							auxiliaryNode2auxiliaryNodeDependencyElement.getElementsByTagName("AdditionalControlPoints");
					if (additionalControlPointsNodeList != null && additionalControlPointsNodeList.getLength() > 0) {
						// has additional control points
						Element additionalControlPointsElement = (Element)additionalControlPointsNodeList.item(0);
						NodeList nodeList = additionalControlPointsElement.getElementsByTagName("AdditionalControlPoint");
						if (nodeList != null) {
							for (int j = 0; j < nodeList.getLength(); j++) {
								Element additionalControlPointElement = (Element)nodeList.item(j);
								double xCoordinate = new Double(additionalControlPointElement.getAttribute("xCoordinate"));
								double yCoordinate = new Double(additionalControlPointElement.getAttribute("yCoordinate"));
								points.add(new Point2D.Double(xCoordinate, yCoordinate));
								graph.get(k).changeDependencyEdgeControlPoints(auxiliaryNode1, auxiliaryNode2, points);
							}
						}
					}        
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (4b) auxiliary node 2 rate node dependency edges
			try {
				NodeList auxiliaryNode2rateNodeDependencyElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Dependencies/AuxiliaryNode2RateNodeDependency",
								document, XPathConstants.NODESET);
				for (int i = 0; i < auxiliaryNode2rateNodeDependencyElements.getLength(); i++) {
					Element auxiliaryNode2rateNodeDependencyElement =
							(Element)auxiliaryNode2rateNodeDependencyElements.item(i);
					String auxiliaryNodeId =
							auxiliaryNode2rateNodeDependencyElement.getAttribute("fromAuxiliaryNodeIdRef");
					String rateNodeId =
							auxiliaryNode2rateNodeDependencyElement.getAttribute("toRateNodeIdRef");

					AuxiliaryNodeGraphCell auxiliaryNode = id2auxiliaryNodeGraphCell.get(auxiliaryNodeId);
					RateNodeGraphCell rateNode = id2rateNodeGraphCell.get(rateNodeId);

					// control points
					LinkedList<Point2D> points = new LinkedList<Point2D>();
					NodeList additionalControlPointsNodeList =
							auxiliaryNode2rateNodeDependencyElement.getElementsByTagName("AdditionalControlPoints");
					if (additionalControlPointsNodeList != null && additionalControlPointsNodeList.getLength() > 0) {
						// has additional control points
						Element additionalControlPointsElement = (Element)additionalControlPointsNodeList.item(0);
						NodeList nodeList = additionalControlPointsElement.getElementsByTagName("AdditionalControlPoint");
						if (nodeList != null) {
							for (int j = 0; j < nodeList.getLength(); j++) {
								Element additionalControlPointElement = (Element)nodeList.item(j);
								double xCoordinate = new Double(additionalControlPointElement.getAttribute("xCoordinate"));
								double yCoordinate = new Double(additionalControlPointElement.getAttribute("yCoordinate"));
								points.add(new Point2D.Double(xCoordinate, yCoordinate));
								graph.get(k).changeDependencyEdgeControlPoints(auxiliaryNode, rateNode, points);
							}
						}
					}
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (4c) constant node 2 auxiliary node dependency edges
			try {
				NodeList constantNode2auxiliaryNodeDependencyElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Dependencies/ConstantNode2AuxiliaryNodeDependency",
								document, XPathConstants.NODESET);
				for (int i = 0; i < constantNode2auxiliaryNodeDependencyElements.getLength(); i++) {
					Element constantNode2auxiliaryNodeDependencyElement =
							(Element)constantNode2auxiliaryNodeDependencyElements.item(i);
					String constantNodeId =
							constantNode2auxiliaryNodeDependencyElement.getAttribute("fromConstantNodeIdRef");
					String auxiliaryNodeId =
							constantNode2auxiliaryNodeDependencyElement.getAttribute("toAuxiliaryNodeIdRef");

					ConstantNodeGraphCell constantNode = id2constantNodeGraphCell.get(constantNodeId);
					AuxiliaryNodeGraphCell auxiliaryNode = id2auxiliaryNodeGraphCell.get(auxiliaryNodeId);

					// control points
					LinkedList<Point2D> points = new LinkedList<Point2D>();
					NodeList additionalControlPointsNodeList =
							constantNode2auxiliaryNodeDependencyElement.getElementsByTagName("AdditionalControlPoints");
					if (additionalControlPointsNodeList != null && additionalControlPointsNodeList.getLength() > 0) {
						// has additional control points
						Element additionalControlPointsElement = (Element)additionalControlPointsNodeList.item(0);
						NodeList nodeList = additionalControlPointsElement.getElementsByTagName("AdditionalControlPoint");
						if (nodeList != null) {
							for (int j = 0; j < nodeList.getLength(); j++) {
								Element additionalControlPointElement = (Element)nodeList.item(j);
								double xCoordinate = new Double(additionalControlPointElement.getAttribute("xCoordinate"));
								double yCoordinate = new Double(additionalControlPointElement.getAttribute("yCoordinate"));
								points.add(new Point2D.Double(xCoordinate, yCoordinate));
								graph.get(k).changeDependencyEdgeControlPoints(constantNode, auxiliaryNode, points);
							}
						}
					}        
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (4d) constant node 2 rate node dependency edges
			try {
				NodeList constantNode2rateNodeDependencyElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Dependencies/ConstantNode2RateNodeDependency",
								document, XPathConstants.NODESET);
				for (int i = 0; i < constantNode2rateNodeDependencyElements.getLength(); i++) {
					Element constantNode2rateNodeDependencyElement =
							(Element)constantNode2rateNodeDependencyElements.item(i);
					String constantNodeId =
							constantNode2rateNodeDependencyElement.getAttribute("fromConstantNodeIdRef");
					String rateNodeId =
							constantNode2rateNodeDependencyElement.getAttribute("toRateNodeIdRef");

					ConstantNodeGraphCell constantNode = id2constantNodeGraphCell.get(constantNodeId);
					RateNodeGraphCell rateNode = id2rateNodeGraphCell.get(rateNodeId);

					// control points
					LinkedList<Point2D> points = new LinkedList<Point2D>();
					NodeList additionalControlPointsNodeList =
							constantNode2rateNodeDependencyElement.getElementsByTagName("AdditionalControlPoints");
					if (additionalControlPointsNodeList != null && additionalControlPointsNodeList.getLength() > 0) {
						// has additional control points
						Element additionalControlPointsElement = (Element)additionalControlPointsNodeList.item(0);
						NodeList nodeList = additionalControlPointsElement.getElementsByTagName("AdditionalControlPoint");
						if (nodeList != null) {
							for (int j = 0; j < nodeList.getLength(); j++) {
								Element additionalControlPointElement = (Element)nodeList.item(j);
								double xCoordinate = new Double(additionalControlPointElement.getAttribute("xCoordinate"));
								double yCoordinate = new Double(additionalControlPointElement.getAttribute("yCoordinate"));
								points.add(new Point2D.Double(xCoordinate, yCoordinate));
								graph.get(k).changeDependencyEdgeControlPoints(constantNode, rateNode, points);
							}
						}
					}        
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}
			
			// (4e) level node 2 auxiliary node dependency edges
			try {
				NodeList levelNode2auxiliaryNodeDependencyElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Dependencies/LevelNode2AuxiliaryNodeDependency",
								document, XPathConstants.NODESET);
				for (int i = 0; i < levelNode2auxiliaryNodeDependencyElements.getLength(); i++) {
					Element levelNode2auxiliaryNodeDependencyElement =
							(Element)levelNode2auxiliaryNodeDependencyElements.item(i);
					String levelNodeId =
							levelNode2auxiliaryNodeDependencyElement.getAttribute("fromLevelNodeIdRef");
					String auxiliaryNodeId =
							levelNode2auxiliaryNodeDependencyElement.getAttribute("toAuxiliaryNodeIdRef");

					LevelNodeGraphCell levelNode = id2levelNodeGraphCell.get(levelNodeId);
					AuxiliaryNodeGraphCell auxiliaryNode = id2auxiliaryNodeGraphCell.get(auxiliaryNodeId);

					// control points
					LinkedList<Point2D> points = new LinkedList<Point2D>();
					NodeList additionalControlPointsNodeList =
							levelNode2auxiliaryNodeDependencyElement.getElementsByTagName("AdditionalControlPoints");
					if (additionalControlPointsNodeList != null && additionalControlPointsNodeList.getLength() > 0) {
						// has additional control points
						Element additionalControlPointsElement = (Element)additionalControlPointsNodeList.item(0);
						NodeList nodeList = additionalControlPointsElement.getElementsByTagName("AdditionalControlPoint");
						if (nodeList != null) {
							for (int j = 0; j < nodeList.getLength(); j++) {
								Element additionalControlPointElement = (Element)nodeList.item(j);
								double xCoordinate = new Double(additionalControlPointElement.getAttribute("xCoordinate"));
								double yCoordinate = new Double(additionalControlPointElement.getAttribute("yCoordinate"));
								points.add(new Point2D.Double(xCoordinate, yCoordinate));
								graph.get(k).changeDependencyEdgeControlPoints(levelNode, auxiliaryNode, points);
							}
						}
					}        
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}

			// (4f) level node 2 rate node dependency edges
			try {
				NodeList levelNode2rateNodeDependencyElements =
						(NodeList)xpath.evaluate("/Model/SubModel[@SubmodelId='"+Integer.toString(k)+"']/Dependencies/LevelNode2RateNodeDependency",
								document, XPathConstants.NODESET);
				for (int i = 0; i < levelNode2rateNodeDependencyElements.getLength(); i++) {
					Element levelNode2rateNodeDependencyElement =
							(Element)levelNode2rateNodeDependencyElements.item(i);
					String levelNodeId =
							levelNode2rateNodeDependencyElement.getAttribute("fromLevelNodeIdRef");
					String rateNodeId =
							levelNode2rateNodeDependencyElement.getAttribute("toRateNodeIdRef");

					LevelNodeGraphCell levelNode = id2levelNodeGraphCell.get(levelNodeId);
					RateNodeGraphCell rateNode = id2rateNodeGraphCell.get(rateNodeId);

					// control points
					LinkedList<Point2D> points = new LinkedList<Point2D>();
					NodeList additionalControlPointsNodeList =
							levelNode2rateNodeDependencyElement.getElementsByTagName("AdditionalControlPoints");
					if (additionalControlPointsNodeList != null && additionalControlPointsNodeList.getLength() > 0) {
						// has additional control points
						Element additionalControlPointsElement = (Element)additionalControlPointsNodeList.item(0);
						NodeList nodeList = additionalControlPointsElement.getElementsByTagName("AdditionalControlPoint");
						if (nodeList != null) {
							for (int j = 0; j < nodeList.getLength(); j++) {
								Element additionalControlPointElement = (Element)nodeList.item(j);
								double xCoordinate = new Double(additionalControlPointElement.getAttribute("xCoordinate"));
								double yCoordinate = new Double(additionalControlPointElement.getAttribute("yCoordinate"));
								points.add(new Point2D.Double(xCoordinate, yCoordinate));
								graph.get(k).changeDependencyEdgeControlPoints(levelNode, rateNode, points);
							}
						}
					}        
				}
			} catch (XPathExpressionException e) {
				// correct xpath expression -> no exception
				throw new XMLModelReaderWriterException(e);
			}
		}
		
		return graph;


	}
	/**
	 * Creates the formula according to the specified XML 'Formula' element tag.
	 * 
	 * @param formulaElement XML 'Formula' element tag
	 * @param id2auxiliaryNode id to auxiliary node mapping
	 * @param id2constantNode id to constant node mapping
	 * @param id2levelNode id to level node mapping
	 * @param id2rateNode id to rate node mapping
	 * @return formula
	 */
	private static ASTElement createFormula(Element formulaElement,
			HashMap<String, AuxiliaryNode> id2auxiliaryNode,
			HashMap<String, ConstantNode> id2constantNode,
			HashMap<String, LevelNode> id2levelNode,
			HashMap<String, RateNode> id2rateNode) {
		if (formulaElement == null) {
			throw new IllegalArgumentException("'formulaElement' must not be null.");
		}
		if (id2auxiliaryNode == null) {
			throw new IllegalArgumentException ("'id2auxiliaryNode' must not be null.");
		}
		if (id2constantNode == null) {
			throw new IllegalArgumentException ("'id2constantNode' must not be null.");
		}
		if (id2levelNode == null) {
			throw new IllegalArgumentException ("'id2levelNode' must not be null.");
		}
		if (id2rateNode == null) {
			throw new IllegalArgumentException ("'id2rateNode' must not be null.");
		}

		NodeList children = formulaElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element)child;
				String tagName = childElement.getTagName();

				// what kind of child?
				if (tagName.endsWith("Node")) {
					return createNodeFormula(childElement, id2auxiliaryNode, id2constantNode,
							id2levelNode, id2rateNode);
				}
				else {
					// ASTPlus, ASTMinus,ASTMultiply, ASTDivide, ASTRound, ASTMax, ASTMin -> binary operation
					return createBinaryOperationFormula(childElement, id2auxiliaryNode, id2constantNode,
							id2levelNode, id2rateNode);
				}
			}
		}

		// will never be reached -> only for compiler!
		return null;
	}

	/**
	 * Creates the part of a formula for the specified node.
	 * 
	 * @param nodeElement node XML tag
	 * @param id2auxiliaryNode id to auxiliary node mapping
	 * @param id2constantNode id to constant node mapping
	 * @param id2levelNode id to level node mapping
	 * @param id2rateNode id to rate node mapping
	 * @return part of formula for the specified node
	 */
	private static ASTElement createNodeFormula(Element nodeElement,
			HashMap<String, AuxiliaryNode> id2auxiliaryNode,
			HashMap<String, ConstantNode> id2constantNode,
			HashMap<String, LevelNode> id2levelNode,
			HashMap<String, RateNode> id2rateNode) {
		if (nodeElement == null) {
			throw new IllegalArgumentException("'nodeElement' must not be null.");
		}
		if (id2auxiliaryNode == null) {
			throw new IllegalArgumentException ("'id2auxiliaryNode' must not be null.");
		}
		if (id2constantNode == null) {
			throw new IllegalArgumentException ("'id2constantNode' must not be null.");
		}
		if (id2levelNode == null) {
			throw new IllegalArgumentException ("'id2levelNode' must not be null.");
		}
		if (id2rateNode == null) {
			throw new IllegalArgumentException ("'id2rateNode' must not be null.");
		}

		String tagName = nodeElement.getTagName();

		if (tagName.equals("ASTAuxiliaryNode")) {
			String id = nodeElement.getAttribute("auxiliaryNodeIdRef");
			return id2auxiliaryNode.get(id);
		}
		if (tagName.equals("ASTConstantNode")) {
			String id = nodeElement.getAttribute("constantNodeIdRef");
			return id2constantNode.get(id);
		}
		if (tagName.equals("ASTLevelNode")) {
			String id = nodeElement.getAttribute("levelNodeIdRef");
			return id2levelNode.get(id);
		}

		// will never be reached -> only for compiler!
		return null;
	}



	/**
	 * Creates the part of a formula for the specified binary operation.
	 * 
	 * @param binaryOperationElement binary operation XML tag
	 * @param id2auxiliaryNode id to auxiliary node mapping
	 * @param id2constantNode id to constant node mapping
	 * @param id2levelNode id to level node mapping
	 * @param id2rateNode id to rate node mapping
	 * @return part of the formula for the specified binary operation
	 */
	private static ASTElement createBinaryOperationFormula(Element binaryOperationElement,
			HashMap<String, AuxiliaryNode> id2auxiliaryNode,
			HashMap<String, ConstantNode> id2constantNode,
			HashMap<String, LevelNode> id2levelNode,
			HashMap<String, RateNode> id2rateNode) {
		if (binaryOperationElement == null) {
			throw new IllegalArgumentException("'binaryOperationElement' must not be null.");
		}
		if (id2auxiliaryNode == null) {
			throw new IllegalArgumentException ("'id2auxiliaryNode' must not be null.");
		}
		if (id2constantNode == null) {
			throw new IllegalArgumentException ("'id2constantNode' must not be null.");
		}
		if (id2levelNode == null) {
			throw new IllegalArgumentException ("'id2levelNode' must not be null.");
		}
		if (id2rateNode == null) {
			throw new IllegalArgumentException ("'id2rateNode' must not be null.");
		}

		boolean firstOperandCreated = false;

		ASTElement firstOperand = null;
		ASTElement secondOperand = null;

		NodeList children = binaryOperationElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element)child;
				String tagName = childElement.getTagName();

				// what kind of child?
				if (tagName.endsWith("Node")) {
					if (!firstOperandCreated) {
						firstOperand = createNodeFormula(childElement, id2auxiliaryNode, id2constantNode,
								id2levelNode, id2rateNode);
					} else {
						secondOperand = createNodeFormula(childElement, id2auxiliaryNode, id2constantNode,
								id2levelNode, id2rateNode);
					}
				} else {
					// ASTPlus, ASTMinus, ASTMultiply or ASTRound-> binary operation
					if (!firstOperandCreated) {
						firstOperand = createBinaryOperationFormula(childElement, id2auxiliaryNode,
								id2constantNode, id2levelNode,
								id2rateNode);
					} else {
						secondOperand = createBinaryOperationFormula(childElement, id2auxiliaryNode,
								id2constantNode, id2levelNode,
								id2rateNode);
					}
				}

				firstOperandCreated = true;
			}
		}

		String tagName = binaryOperationElement.getTagName();
		if (tagName.equals("ASTMinus")) {
			return new ASTMinus(firstOperand, secondOperand);
		}
		if (tagName.equals("ASTMultiply")) {
			return new ASTMultiply(firstOperand, secondOperand);
		}
		if (tagName.equals("ASTPlus")) {
			return new ASTPlus(firstOperand, secondOperand);
		}
		if (tagName.equals("ASTDivide")) {
			return new ASTDivide(firstOperand, secondOperand);
		}
		if (tagName.equals("ASTRound"))
			return new ASTRound(firstOperand, secondOperand);
		if (tagName.equals("ASTMax"))
			return new ASTMax(firstOperand, secondOperand);
		if (tagName.equals("ASTMin"))
			return new ASTMin(firstOperand, secondOperand);
		// will never be reached -> only for compiler!
		return null;
	}
}
