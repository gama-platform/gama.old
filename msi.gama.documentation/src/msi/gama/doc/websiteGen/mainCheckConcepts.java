package msi.gama.doc.websiteGen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import msi.gama.doc.websiteGen.utilClasses.ConceptManager;
import msi.gama.doc.websiteGen.utilClasses.Utils;
import msi.gama.doc.websiteGen.utilClasses.ConceptManager.WebsitePart;

public class mainCheckConcepts {
	// this class will check if all the concepts present in the documentations are conform. It will then build a report about repartition of concept keywords.
	
	public static String PATH_TO_MODEL_LIBRARY = "F:/gama_doc_17.wiki/References/ModelLibrary";
	public static String PATH_TO_GAML_REFERENCES = "F:/gama_doc_17.wiki/References/GAMLReferences";
	public static String PATH_TO_DOCUMENTATION = "F:/gama_doc_17.wiki/Tutorials";
	
	public static String PATH_TO_KEYWORDS_XML = "F:/gama_doc_17.wiki/keywords.xml";
	
	public static String PATH_TO_MD_REPORT = "F:/gama_doc_17.wiki/WikiOnly/DevelopingExtensions/WebsiteGeneration.md";
	
	public static void main(String[] args) throws IOException, IllegalArgumentException, IllegalAccessException {
		// get all the concepts.
		ConceptManager.loadConcepts();
		
		// browse all the files of the model library.
		executeForAWebsitePart(PATH_TO_MODEL_LIBRARY, WebsitePart.MODEL_LIBRARY.toString());
		
		// browse all the files of the documentation.
		executeForAWebsitePart(PATH_TO_DOCUMENTATION, WebsitePart.DOCUMENTATION.toString());
		
		// browse keywords.xml to find which concepts is linked with which keywords.
		browseKeywords(PATH_TO_KEYWORDS_XML);
		
		// print statistics		
		ConceptManager.printStatistics();
		
		// write report in websiteGeneration file
		writeReport(PATH_TO_MD_REPORT);
	}
	
	private static void executeForAWebsitePart(String path, String websitePart) {
		ArrayList<File> listFiles = new ArrayList<File>();
		Utils.getFilesFromFolder(path,listFiles);
		ArrayList<File> gamlFiles = Utils.filterFilesByExtension(listFiles,"md");
		
		ArrayList<String> listConcept = new ArrayList<String>();
		
		for (File file : gamlFiles) {
			try {
				listConcept = Utils.getConceptKeywords(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (String concept : listConcept) {
				if (!ConceptManager.conceptIsPossibleToAdd(concept)) {
					System.out.println("WARNING : The concept "+concept+" is not a predefined concept !!");
				}
				else ConceptManager.addOccurrenceOfConcept(concept, websitePart);
			}
		}
	}
	
	private static void browseKeywords(String path) {
	    try {
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(path);
	    			
	    	doc.getDocumentElement().normalize();
	    			
	    	NodeList nList = doc.getElementsByTagName("keyword");

	    	for (int temp = 0; temp < nList.getLength(); temp++) {
	    		Node nNode = nList.item(temp);
	    		Element eElement = (Element) nNode;
	    		String category = eElement.getElementsByTagName("category").item(0).getTextContent();
	    		String conceptName = eElement.getElementsByTagName("name").item(0).getTextContent();
	    		if (category.equals("concept")) {
		    		if (ConceptManager.conceptIsPossibleToAdd(conceptName)) {
		    			for (int i=0; i < eElement.getElementsByTagName("associatedKeyword").getLength(); i++) {
		    				ConceptManager.addOccurrenceOfConcept(conceptName, WebsitePart.GAML_REFERENCES.toString());
		    			}
	    			}
	    		}
	    	}
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	private static void writeReport(String file) throws IOException {
		String result = "";
		
		// read the file
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String line = null;
		
		while ((line = br.readLine()) != null) {
			if (line.contains("__________________________________")) {
				result += line+"\n";
				break;
			}
			else {
				result += line+"\n";
			}
		}
		br.close();
		result += "\n\n";
		
		// add the statistics
		result += ConceptManager.getExtendedStatistics();
		
		// write the file
		File outputFile = new File(file);
		FileOutputStream fileOut = new FileOutputStream(outputFile);
		fileOut.write(result.getBytes());
		fileOut.close();
	}
}
