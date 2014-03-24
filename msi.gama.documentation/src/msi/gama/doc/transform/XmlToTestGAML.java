package msi.gama.doc.transform;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import msi.gama.doc.Constants;
import msi.gama.doc.util.DocTransformer;
import msi.gama.doc.util.XMLUtils;
import msi.gama.precompiler.doc.utils.XMLElements;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlToTestGAML {

	public static void createAllTests() 
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		Document document = XMLUtils.createDoc(Constants.DOCGAMA_GLOBAL_FILE);
		document = cleanDocumentTest(document);		
		
		System.out.println("Beginning of the transformation");

		//////////////////////////////////////////////////////////////////////////////////
		System.out.print("Creation of the test models for Operators.....");
		File dirOperators = new File(Constants.TEST_FOLDER + File.separator + Constants.TEST_OPERATORS_FOLDER);
		dirOperators.mkdir();
		
		createTest(document,
				Constants.XSL_XML2TEST_FOLDER + File.separator + "testGaml-Operators-xml2test.xsl",
				dirOperators.getCanonicalPath() ); 
		System.out.println("Done");	
		
		//////////////////////////////////////////////////////////////////////////////////
		System.out.print("Creation of the master test model.....");
		createMasterTest(document,
				Constants.XSL_XML2TEST_FOLDER + File.separator + "testGaml-Master-xml2test.xsl",
				Constants.TEST_FOLDER ); 
		System.out.println("Done");			
		//
		System.out.println("End of the transformation");		
	}

	private static void createMasterTest(Document document, String xsl, String targetFolder) 
			throws ParserConfigurationException, SAXException, IOException {		
		DocTransformer.transformDocument(document, xsl, targetFolder + File.separator + "masterTest.gaml"); 		
	}
	
	private static void createTest(Document document, String xsl, String targetFolder) 
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = fabriqueD.newDocumentBuilder();

		NodeList nLCategoriesOp = document.getElementsByTagName(XMLElements.OPERATORS_CATEGORIES);
		NodeList nLCategories = ((org.w3c.dom.Element) nLCategoriesOp.item(0)).getElementsByTagName(XMLElements.CATEGORY);
		
		NodeList nLOperators = document.getElementsByTagName(XMLElements.OPERATOR);
		
		for(int i =0; i< nLCategories.getLength() ; i++){
			org.w3c.dom.Element eltCategory = (org.w3c.dom.Element) nLCategories.item(i);
						
			Document docTemp = builder.newDocument();
			org.w3c.dom.Element root = docTemp.createElement(XMLElements.DOC);	
			org.w3c.dom.Element rootOperators = docTemp.createElement(XMLElements.OPERATORS);			
		
			for(int j = 0; j < nLOperators.getLength(); j++){
				org.w3c.dom.Element eltOperator = (org.w3c.dom.Element) nLOperators.item(j);
				NodeList nLOperatorCategories = eltOperator.getElementsByTagName(XMLElements.CATEGORY);
				
				int k = 0;
				boolean categoryFound = false;
				while(k < nLOperatorCategories.getLength() && !categoryFound) {
					if(eltCategory.getAttribute(XMLElements.ATT_CAT_ID).equals(((org.w3c.dom.Element) nLOperatorCategories.item(k)).getAttribute("id"))){
						Node importedOpElt = docTemp.importNode(eltOperator.cloneNode(true), true);
						rootOperators.appendChild(importedOpElt);						
						categoryFound = true;
					}
					k++;
				}
				
				//if(eltCategory.getAttribute(XMLElements.ATT_CAT_ID).equals(eltOperator.getAttribute(XMLElements.CATEGORY))){
				//	Node importedOpElt = docTemp.importNode(eltOperator.cloneNode(true), true);
				//	rootOperators.appendChild(importedOpElt);
				//}
			}
			root.appendChild(rootOperators);
			docTemp.appendChild(root);
			
			DocTransformer.transformDocument(docTemp, xsl, targetFolder + File.separator + "Op"+eltCategory.getAttribute("id")+"Test.gaml"); 		
		}
	}
	
	// Cleaning means:
	// - Category: remove space and minus characters in the category name to be able to use it in the model	
	// - Operators: replace special characters like +, -, *, /
	public static Document cleanDocumentTest(Document doc){
		NameOperatorConverter nameConverter = new NameOperatorConverter();
		NodeList nLCategories = doc.getElementsByTagName(XMLElements.CATEGORY);
		NodeList nLOperators = doc.getElementsByTagName(XMLElements.OPERATOR);
		
		for(int i =0; i< nLCategories.getLength() ; i++){
			org.w3c.dom.Element eltCategory = (org.w3c.dom.Element) nLCategories.item(i);
			eltCategory.setAttribute(XMLElements.ATT_CAT_ID,eltCategory.getAttribute(XMLElements.ATT_CAT_ID).replaceAll(" ", "__").replaceAll("-", "_"));
		}		
		
		for(int j = 0; j < nLOperators.getLength(); j++){
			org.w3c.dom.Element eltOperator = (org.w3c.dom.Element) nLOperators.item(j);
			// eltOperator.setAttribute("category",eltOperator.getAttribute("category").replaceAll(" ", "__").replaceAll("-", "_"));
			eltOperator.setAttribute(XMLElements.ATT_OP_ID, nameConverter.getProperOperatorName(eltOperator.getAttribute(XMLElements.ATT_OP_ID)));
			eltOperator.setAttribute(XMLElements.ATT_OP_NAME, nameConverter.getProperOperatorName(eltOperator.getAttribute(XMLElements.ATT_OP_NAME)));
			eltOperator.setAttribute(XMLElements.ATT_OP_ALT_NAME, nameConverter.getProperOperatorName(eltOperator.getAttribute(XMLElements.ATT_OP_ALT_NAME)));
			
			NodeList nLExamples = eltOperator.getElementsByTagName(XMLElements.EXAMPLE);
			for(int k = 0; k < nLExamples.getLength(); k++){
				org.w3c.dom.Element eltExample = (org.w3c.dom.Element) nLExamples.item(k);
				eltExample.setAttribute(XMLElements.ATT_EXAMPLE_INDEX, ""+k);
			}
		}		
		
		return doc;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		createAllTests();
	}
	
	static class NameOperatorConverter{
		HashMap<String, String> properNameOperatorMap;

		public NameOperatorConverter(){
			properNameOperatorMap = initProperNameOperatorMap();
		}
		
		private HashMap<String, String> initProperNameOperatorMap() {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("*", "Multiply");
			hm.put("-", "Minus");
			hm.put("/", "Divide");
			hm.put("+", "Plus");
			hm.put("^", "Power");
			return hm;
		}
		
		public String getProperOperatorName(String opName) {
			if(properNameOperatorMap.containsKey(opName)){
				return properNameOperatorMap.get(opName);				
			}
			else {
				return opName;
			}
		}
	}
}
