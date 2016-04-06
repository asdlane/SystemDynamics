package de.uka.aifb.com.systemDynamics.xml;

import java.io.File;
import java.util.ArrayList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uka.aifb.com.systemDynamics.model.ChartModel;
import de.uka.aifb.com.systemDynamics.model.Model;

public class ChartXMLModelReader {
	private static final String XSD_FILE_NAME = "xsd/chartModel.xsd";
	public static ArrayList<ChartModel> readXMLModel(String fileName){
		if (fileName == null) {
			throw new IllegalArgumentException("'fileName' must not be null.");
		}
		ArrayList<ChartModel> model = new ArrayList<ChartModel>();
		createModelFromXML(fileName,XSD_FILE_NAME, model, "Charts");
		return null;
		
	}
	protected static void createModelFromXML(String fileString, String xsdFileString, ArrayList<ChartModel> model,
			String rootElementName){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  // can throw FactoryConfiguration Error

		// create schema
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		try {
			schema = schemaFactory.newSchema(new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(xsdFileString)));
		} catch (SAXException e) {
			System.out.println("Pradeep");
			// exception should not happen because schema is correct!
			
		}

		factory.setSchema(schema);

		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// exception should not happen
			e.printStackTrace();
		}

		// set own error handler that throws exception if XML file is not Schema compliant
		builder.setErrorHandler(new MyErrorHandler());
		System.out.println("GOT HERE");
		Document document = null;
		try {
			document = builder.parse(new File(fileString));  // can throw IOException
			// can throw SAXException
		} catch (Exception e) {
			
			e.printStackTrace();
		}		
		XPath xpath = XPathFactory.newInstance().newXPath();
		String modelName = "";
		NodeList Charts = null;
		try {
			Charts = (NodeList)xpath.evaluate("/Charts/Chart", document, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=0;i<Charts.getLength();i++){
			Element chartElement = (Element)Charts.item(0);
					
			String name=chartElement.getAttribute("name");
			String id=chartElement.getAttribute("id");
			String file=chartElement.getAttribute("file");
			String xAxisLabel=chartElement.getAttribute("xAxisLabel");
			String yAxisLabel=chartElement.getAttribute("yAxisLabel");
			
			ChartModel blankModel = new ChartModel(name, id, file, xAxisLabel, yAxisLabel);
			model.add(blankModel);
		}
		
		

	}
}
