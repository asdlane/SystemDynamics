package de.uka.aifb.com.systemDynamics;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import com.csvreader.CsvReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class PreProcess {
	String new_filename = null;

	/**
	 * @param fname
	 * @param run
	 * @throws Exception
	 */
	public void checkRun(String fname, int run) throws Exception {

		// take care of Init Phase
		if (run == 0 || run == -1) {
			StringTokenizer st = new StringTokenizer(fname, ".");
			new_filename = st.nextToken() + "_" + String.valueOf(0) + ".xml";
		} else {
			int index = fname.lastIndexOf('_');
			String file = fname.substring(0, index);
			new_filename = file + "_" + String.valueOf((2 * run)) + ".xml";

		}
	}

	/**
	 * @param nRounds
	 * @param outputName
	 * @return HashMap<String, String>
	 * @throws Exception
	 */
	public HashMap<String, String> phase2PreProcess(
			String outputName) throws Exception {

		CsvReader modelOutput = new CsvReader(outputName, ';');
		modelOutput.readHeaders();
		modelOutput.getCurrentRecord();
		modelOutput.readRecord();
		int nHeader = modelOutput.getColumnCount();
		String headers[] = new String[nHeader];
		
		for (int index = 0; index < nHeader; index++) {
			headers[index] = modelOutput.get(index);
//			System.out.println("+++++++++++++++++++++++++++++     phase2PreProcess");
//			System.out.println(headers[index]);
//			System.out.println("+++++++++++++++++++++++++++++");
		}
		
		int index = 0;
		//Read last run length from Xtime file
		BufferedReader br = new BufferedReader(new FileReader("xTime.txt"));
		int nRounds = Integer.parseInt(br.readLine());
		while (index != nRounds) {
			modelOutput.readRecord();

			index++;
		}

		// Get Variables
		HashMap<String, String> map = new HashMap<String, String>();
		modelOutput.readRecord();
		long line = modelOutput.getCurrentRecord();
		for (index = 0; index < nHeader; index++) {
			map.put(headers[index], modelOutput.get(index));
		}
		

//		System.out.println("+++++++++++++++++++++++++++++   map entrySet in phase2preprocess for 0 2");
//		for(Map.Entry<String, String> e : map.entrySet() ){
//			System.out.println("key  "+e.getKey()+"   value  "+e.getValue());
//		}
//		System.out.println("+++++++++++++++++++++++++++++");
		
		return map;
	}

	public HashMap<String, String> cycle0PreProcess(String outputName, int nRounds) throws Exception {

		CsvReader modelOutput = new CsvReader(outputName, ';');
		modelOutput.readHeaders();
		modelOutput.getCurrentRecord();
		modelOutput.readRecord();
		int nHeader = modelOutput.getColumnCount();
		String headers[] = new String[nHeader];
		
		for (int index = 0; index < nHeader; index++) {
			headers[index] = modelOutput.get(index);
//			System.out.println("+++++++++++++++++++++++++++++     cycle0PreProcess");
//			System.out.println(headers[index]);
//			System.out.println("+++++++++++++++++++++++++++++");
		}
		
		int index = 0;
		
		while (index != nRounds) {
			modelOutput.readRecord();

			index++;
		}

		// Get Variables
		HashMap<String, String> map = new HashMap<String, String>();
		modelOutput.readRecord();
		long line = modelOutput.getCurrentRecord();
		for (index = 0; index < nHeader; index++) {
			map.put(headers[index], modelOutput.get(index));
		}
		

//		System.out.println("+++++++++++++++++++++++++++++   map entrySet in phase2preprocess for 0 2");
//		for(Map.Entry<String, String> e : map.entrySet() ){
//			System.out.println("key  "+e.getKey()+"   value  "+e.getValue());
//		}
//		System.out.println("+++++++++++++++++++++++++++++");
		
		return map;
	}
	/**
	 * @param fname
	 * @param clist
	 * @param run
	 * @return
	 * @throws Exception
	 */
	public String preprocess(String fname, HashMap<String, String> clist, HashMap<String, String> glist, HashMap<String, String> prelist, int run, int flag) throws Exception {

		// Check run
		checkRun(fname, run);

		String filename = fname;
		HashMap<String, String> hiring_map = new HashMap<String, String>();

		File fXmlFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		
		HashMap<String, String> tmplist = new HashMap(glist);
		
		for(Map.Entry<String, String> e : clist.entrySet() ){
			if(e.getValue()!="")
				tmplist.put(e.getKey(), e.getValue());
		}
		
//
//		System.out.println("+++++++++++++++++++++++++++++   tmp_map entrySet in preprocess");
//		for(Map.Entry<String, String> e : tmplist.entrySet() ){
//			System.out.println("key  "+e.getKey()+"   value  "+e.getValue());
//		}
//		System.out.println("+++++++++++++++++++++++++++++");
		
		// System.out.println("Root element :"
		// + doc.getDocumentElement().getNodeName());
		// Preprocess Constant nodes
		NodeList nList = doc.getElementsByTagName("ConstantNode");
		
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				setAttributeValue(tmplist, nNode);
			}
		}
		
		
		// Prerocess level nodes
		NodeList lnList = doc.getElementsByTagName("LevelNode");

		Map<String,Double> oldValueMap = new HashMap<String,Double>();
		for (int temp = 0; temp < lnList.getLength(); temp++) {
			Node nNode = lnList.item(temp);
			Node submodel = nNode.getParentNode().getParentNode().getParentNode();
			int submodelId = -1;
			for (int i = 0; i < submodel.getAttributes().getLength(); i++) {
				Attr attr = (Attr) submodel.getAttributes().item(i);
				if(attr.getName().equals("SubmodelId"))
					submodelId = Integer.parseInt(attr.getValue())+1;
			}
			
			double oldvalue = 0;
			String name = "";
			for (int i = 0; i < nNode.getAttributes().getLength(); i++) {
				Attr attr = (Attr) nNode.getAttributes().item(i);
				if(attr.getName().equals("startValue"))
					oldvalue = Double.parseDouble(attr.getValue());
				if(attr.getName().equals("name"))
					name = attr.getValue();
			}
			
//			System.out.println("========================== ******** "+name+" "+prelist.containsKey(name));
			if(prelist.containsKey("SM"+submodelId+":"+name)){
				
				oldvalue = Double.parseDouble(prelist.get("SM"+submodelId+":"+name));
			}
			oldValueMap.put("SM"+submodelId+":"+name,oldvalue);
		}

//		System.out.println("========================== "+prelist.get("SM1-ACWP_S0"));
		for (int temp = 0; temp < lnList.getLength(); temp++) {

			Node nNode = lnList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				setAttributeValue1(tmplist, nNode, prelist, flag, oldValueMap);
			}
		}
		// new_filename = "inter_model.xml";
		transformXML(doc, new_filename);

		return new_filename;
	}

	/**
	 * @param doc
	 * @param filename
	 * @throws Exception
	 */
	private void transformXML(Document doc, String filename) throws Exception {
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		// initialize StreamResult with File object to save to file
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);

		String xmlString = result.getWriter().toString();
//		 System.out.println("********************************************* "+filename);
		FileWriter writer = new FileWriter(filename);
		writer.write(xmlString);
		writer.flush();
		writer.close();
		
	}

	/**
	 * @param cl_map
	 * @param nNode
	 */
	private void setAttributeValue(HashMap<String, String> cl_map, Node nNode) {

		NamedNodeMap attrs = nNode.getAttributes();
		
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attribute = (Attr) attrs.item(i);
			
//			if(flag == 0)
	//		{	
				if (attribute.getName().equals("name")
						&& (cl_map.get(attribute.getValue()) != null)) {
					// System.out.println(attribute.getValue());
					
					String processing = cl_map.get(attribute.getValue());
					if(processing.endsWith("%"))
					{
						Attr tempAttr = (Attr) attrs.item(i - 2);
						double oldValue = Double.parseDouble(tempAttr.getValue());
						String changeString = processing.substring(0, processing.length()-1);
						double change = (Double.parseDouble(changeString)/100)*oldValue;
						double newValue = oldValue + change;
						tempAttr.setValue(Double.toString(newValue));
					}
					else
					{
						Attr tempAttr = (Attr) attrs.item(i - 2);
						tempAttr.setValue(cl_map.get(attribute.getValue()));					
					}
				}				
			//}
/*			else
			{
				if (attribute.getName().equals("name")
						&& (pre_map.get(attribute.getValue()) != null)) {
					// System.out.println(attribute.getValue());
					
					if(cl_map.get(attribute.getValue()) != null)
					{
						String processing = cl_map.get(attribute.getValue());
						if(processing.endsWith("%"))
						{
							Attr tempAttr = (Attr) attrs.item(i - 2);
							double oldValue = Double.parseDouble(pre_map.get(attribute.getValue()));
							String changeString = processing.substring(0, processing.length()-1);
							double change = (Double.parseDouble(changeString)/100)*oldValue;
							double newValue = oldValue + change;
							tempAttr.setValue(Double.toString(newValue));
						}
						else
						{
							Attr tempAttr = (Attr) attrs.item(i - 2);
							tempAttr.setValue(cl_map.get(attribute.getValue()));					
						}						
					}
					else
					{
						Attr tempAttr = (Attr) attrs.item(i - 2);
						tempAttr.setValue(pre_map.get(attribute.getValue()));											
					}
				}				
			}
*/
		}
	}

	/**
	 * @param cl_map
	 * @param nNode
	 */
	private void setAttributeValue1(HashMap<String, String> cl_map, Node nNode, HashMap<String, String> pre_map, int flag, Map<String, Double> oldValueMap) {

		NamedNodeMap attrs = nNode.getAttributes();

//		System.out.println("+++++++++++++++++++++++++++++   cl_map entrySet in setAttr1");
//		for(Map.Entry<String, String> e : cl_map.entrySet() ){
//			System.out.println("key  "+e.getKey()+"   value  "+e.getValue());
//		}
//		System.out.println("+++++++++++++++++++++++++++++");
//		
//
//		System.out.println("+++++++++++++++++++++++++++++   pre_map entrySet in setAttr1");
//		for(Map.Entry<String, String> e : pre_map.entrySet() ){
//			System.out.println("key  "+e.getKey()+"   value  "+e.getValue());
//		}
//		System.out.println("+++++++++++++++++++++++++++++");
		
		
		
		Node submodel = nNode.getParentNode().getParentNode().getParentNode();
		int submodelId = -1;
		for (int i = 0; i < submodel.getAttributes().getLength(); i++) {
			Attr attr = (Attr) submodel.getAttributes().item(i);
			if(attr.getName().equals("SubmodelId"))
				submodelId = Integer.parseInt(attr.getValue())+1;
		}
		
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attribute = (Attr) attrs.item(i);
			String attr = "SM"+submodelId+":"+attribute.getValue();

		
			int offset = 2;
			if(flag == 0)
			{
//				System.out.println("========================== enter here 0 1");
				if (attribute.getName().equals("name")
						&& (cl_map.get(attr) != null)) {
					// System.out.println(attribute.getValue());


//					System.out.println("========================== enter here 0 2");
					String processing = cl_map.get(attr);
					if(processing.endsWith("%"))
					{
						Attr tempAttr = (Attr) attrs.item(i + offset);
						double oldValue = Double.parseDouble(tempAttr.getValue());
						String changeString = processing.substring(0, processing.length()-1);
						double change = (Double.parseDouble(changeString)/100)*oldValue;
						double newValue = oldValue + change;
						tempAttr.setValue(Double.toString(newValue));
					}
					else if(!processing.equals(""))
					{

//						System.out.println("========================== enter here 0 3");
						Attr tempAttr = (Attr) attrs.item(i + offset);
						tempAttr.setValue(calculate(cl_map.get(attr),Double.parseDouble(tempAttr.getValue()),oldValueMap));					
					}
				}				
			}
			else
			{// enter when 0 2
				
				
				if (attribute.getName().equals("name")
						&& (pre_map.get(attr) != null)) {
//					System.out.println("========================== enter here 1 "+attr+"  ***  "+pre_map.get(attr));
					
					if(cl_map.get(attr) != null && !cl_map.get(attr).equals("") )
					{
//						System.out.println("========================== enter here 2 "+cl_map.get(attr));
						String processing = cl_map.get(attr);
						if(processing.endsWith("%"))
						{
							Attr tempAttr = (Attr) attrs.item(i + offset);
							double oldValue = Double.parseDouble(pre_map.get(attr));
							String changeString = processing.substring(0, processing.length()-1);
							double change = (Double.parseDouble(changeString)/100)*oldValue;
							double newValue = oldValue + change;
							tempAttr.setValue(Double.toString(newValue));
						}
						else if(!processing.equals(""))
						{	

//							System.out.println("========================== enter here 3");
							Attr tempAttr = (Attr) attrs.item(i + offset);
//							System.out.println("========================== enter here 3 "+pre_map.get(attr)+oldValueMap.get(attr));
							
							tempAttr.setValue(calculate(cl_map.get(attr),Double.parseDouble(tempAttr.getValue()),oldValueMap));				
						}						
					}
					
					else if(!pre_map.get(attr).equals(""))
					{
						Attr tempAttr = (Attr) attrs.item(i + offset);
						tempAttr.setValue(pre_map.get(attr));											
					}
				}				
			}
		}
	}
	
	
	private String calculate(String expr, double oldValue, Map<String,Double> oldValueMap){
		double newValue= oldValue;

		System.out.println("*****    "+expr+"     *****");
		double change = 0;
		if(expr.startsWith("+")){
			change = Double.parseDouble(expr.substring(1, expr.length()));
			newValue +=change;
		}
		else if(expr.startsWith("IF(")){	//VAR1, IF(VAR2 < CONST1; CONST2; CONST3)
			String temp = expr.substring(3,expr.length()-1);
			String[] vars = temp.split(";");
			String[] compare = vars[0].split("<");
//			System.out.println(temp+ " vars "+ vars[0]+" compare "+compare[0]);
			
			Double var2 = oldValueMap.get(compare[0].trim());
			
//			System.out.println(temp+ " vars "+ vars[0]+" var2 "+var2);
			
			Double const1 = compare[1].trim().startsWith("SM")?oldValueMap.get(compare[1].trim()) : Double.parseDouble(compare[1].trim());
//			Double const2 = vars[1].trim().startsWith("SM")?oldValueMap.get(vars[1].trim()) : Double.parseDouble(vars[1].trim());
//			Double const3 = vars[2].trim().startsWith("SM")?oldValueMap.get(vars[2].trim()) : Double.parseDouble(vars[2].trim());

			System.out.println("*****    "+vars[1].trim().startsWith("SM")+"    *****"+vars[2]+"   *****"+oldValueMap.get(vars[1].trim()));
			Double const2 = vars[1].trim().startsWith("SM")? Double.parseDouble(calculate(vars[1],newValue,oldValueMap)) : Double.parseDouble(vars[1].trim());
			Double const3 = vars[2].trim().startsWith("SM")? Double.parseDouble(calculate(vars[1],newValue,oldValueMap)) : Double.parseDouble(vars[2].trim());
			
			System.out.println("*****  const  "+const2+"    *****"+const3+"   *****"+oldValueMap.get(vars[1].trim()));
			
			if(var2 < const1)
				newValue = const2;
			else
				newValue = const3;
		}
		else if(expr.contains("+")){
			String[] vars = expr.split("\\+");
			newValue = oldValueMap.get(vars[0])+oldValueMap.get(vars[1]);
			System.out.println("*****  +newvalue  "+newValue);
			
		}
		else if(expr.contains("-") && !expr.startsWith("-")){
			String[] vars = expr.split("-");
			System.out.println("*****  get  "+oldValueMap.get(vars[0])+"    *****"+oldValueMap.get(vars[1]));
			
			newValue = oldValueMap.get(vars[0])-oldValueMap.get(vars[1]);
		}
		else if(expr.contains("*")){
			String[] vars = expr.split("\\*");
			System.out.println("*****  get  "+oldValueMap.get(vars[0])+"    *****"+oldValueMap.get(vars[1]));
			
			newValue = oldValueMap.get(vars[0])*oldValueMap.get(vars[1]);
		}
		else if(expr.contains("/")){
			String[] vars = expr.split("/");
			System.out.println("*****  get  "+oldValueMap.get(vars[0])+"    *****"+oldValueMap.get(vars[1]));
			
			newValue = oldValueMap.get(vars[0])/oldValueMap.get(vars[1]);
		}
		else{
			newValue = Double.parseDouble(expr);
		}
		return String.valueOf(newValue);
	}

}


class CommandProcessor{
	
	public CommandProcessor(){
		
	}
	
	public String process(String expr, double oldValue){
		String res = null;
		
		return res;
	}
	
	private String calculate(String expr, double oldValue){
		double newValue= oldValue;
		
		double change = 0;
		if(expr.startsWith("+")){
			change = Double.parseDouble(expr.substring(1, expr.length()));
			newValue +=change;
		}
		else{
			newValue = Double.parseDouble(expr);
		}
		
		
		
		return String.valueOf(newValue);
	}
}