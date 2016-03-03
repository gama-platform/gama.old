package msi.gama.doc.websiteGen;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import msi.gama.doc.websiteGen.utilClasses.LearningConcept;
import msi.gama.doc.websiteGen.utilClasses.Topic;

import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class learningGraphDatabaseGenerator {
	
	static List<LearningConcept> learningConceptList = new ArrayList<LearningConcept>();
	static List<Topic> topicList = new ArrayList<Topic>();

	public static void main(String[] args) throws FileNotFoundException {
		
		String inputFilePath = "F:/gama_doc_17.wiki/learningGraph.xml";
		String outputFilePath = "F:/gama_doc_17.wiki/nodesDatabase.js";
		String imageFolder = "resources/images/miniaturesLearningGraph/";
		
		///////// read the xml ///////////
		float coeff = 700;
		
	    try {
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(inputFilePath);
	    			
	    	doc.getDocumentElement().normalize();
	    			
	    	NodeList nList = doc.getElementsByTagName("learningConcept");

	    	for (int temp = 0; temp < nList.getLength(); temp++) {

	    		Node nNode = nList.item(temp);
	    				
	    		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

	    			Element eElement = (Element) nNode;
	    			
	    			String id = eElement.getAttribute("id");
	    			float x = Float.valueOf(eElement.getAttribute("x"))*coeff;
	    			float y = Float.valueOf(eElement.getAttribute("y"))*coeff;
	    			String name = eElement.getElementsByTagName("name").item(0).getTextContent();
	    			String description = eElement.getElementsByTagName("description").item(0).getTextContent();
	    			List<String> listPrerequisite = new ArrayList<String>();
	    			for (int i=0; i < eElement.getElementsByTagName("prerequisite").getLength(); i++) {
		    			listPrerequisite.add(eElement.getElementsByTagName("prerequisite").item(i).getTextContent());
	    			}
	    			LearningConcept newLearningConcept = new LearningConcept(id,name,description,x,y,listPrerequisite);
	    			learningConceptList.add(newLearningConcept);
	    		}
	    	}
	    	nList = doc.getElementsByTagName("topic");

	    	for (int temp = 0; temp < nList.getLength(); temp++) {

	    		Node nNode = nList.item(temp);
	    				
	    		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

	    			Element eElement = (Element) nNode;
	    			
	    			String id = eElement.getAttribute("id");
	    			float x = Float.valueOf(eElement.getAttribute("x"))*coeff;
	    			float y = Float.valueOf(eElement.getAttribute("y"))*coeff;
	    			float xBigHallow = Float.valueOf(eElement.getAttribute("xBigHallow"))*coeff;
	    			float yBigHallow = Float.valueOf(eElement.getAttribute("yBigHallow"))*coeff;
	    			float sizeBigHallow = Float.valueOf(eElement.getAttribute("sizeBigHallow"));
	    			String name = eElement.getElementsByTagName("name").item(0).getTextContent();
	    			List<String> associatedLearningConceptList = new ArrayList<String>();
	    			for (int i=0; i < eElement.getElementsByTagName("learningConceptAssociated").getLength(); i++) {
	    				associatedLearningConceptList.add(eElement.getElementsByTagName("learningConceptAssociated").item(i).getTextContent());
	    			}
	    			String color = eElement.getAttribute("color");
	    			Topic newTopic = new Topic(id,name,x,y,xBigHallow,yBigHallow,sizeBigHallow,associatedLearningConceptList,color);
	    			topicList.add(newTopic);
	    		}
	    	}
	    	} catch (Exception e) {
	    	e.printStackTrace();
	        }
	    
	    ///////// check the values ///////////
	    
	    List<String> listConceptNames = new ArrayList<String>();
	    for (int i=0; i<learningConceptList.size(); i++) {
	    	listConceptNames.add(learningConceptList.get(i).m_id);
	    }
	    for (int i=0; i<learningConceptList.size(); i++) {
	    	// check if all the "prerequisite" are learningConceptID.
	    	for (int j=0; j<learningConceptList.get(i).m_prerequisitesList.size(); j++) {
	    		if (!listConceptNames.contains(learningConceptList.get(i).m_prerequisitesList.get(j))) {
	    			System.out.println("ERROR !!!! The name "
	    		+learningConceptList.get(i).m_prerequisitesList.get(j)+" is not a learning concept id");
	    		}
	    	}
	    }
	    for (int i=0; i<topicList.size(); i++) {
	    	// check if all the "prerequisite" are learningConceptID.
	    	for (int j=0; j<topicList.get(i).m_associatedLearningConceptList.size(); j++) {
	    		if (!listConceptNames.contains(topicList.get(i).m_associatedLearningConceptList.get(j))) {
	    			System.out.println("ERROR !!!! The name "
	    		+topicList.get(i).m_associatedLearningConceptList.get(j)+" is not a learning concept id");
	    		}
	    	}
	    }
	    
	    ///////// build the js ///////////
	    
        // write the new String with the replaced line OVER the same file
	    File file = new File(outputFilePath);
	    
	    String input = "";
	    
	    // write the introduction
	    input += "// file generated automatically, do not edit it !!\n";
	    input += "var nodes = [\n";
	    
	    // BUID THE NODES
	    // write the learning concepts
	    input += "// learning concepts :\n";
	    int idNb = 0;
	    for (int i=0; i<learningConceptList.size(); i++) {
	    	int idNode = idNb+200;
	    	int idHallow = idNb+100;
	    	int idNodeWithLabel = idNb+300;
	    	LearningConcept lc = learningConceptList.get(i);
	    	input += "{id:"+idNode+", label:''"
	    			+", x:"+lc.m_xPos+", y:"+lc.m_yPos
	    			+", category:'learningConcept'},\n";
	    	input += "{id:"+idHallow+", label:''"
	    			+", x:"+lc.m_xPos+", y:"+lc.m_yPos
	    			+", category:'highlightLearningConcept'},\n";
	    	input += "{id:"+idNodeWithLabel+", label:'"+lc.m_name
	    			+"', x:"+lc.m_xPos+", y:"+lc.m_yPos
	    			+", category:'learningConceptWithLabel', imagePath:'"+imageFolder+lc.m_id+"', description:'"+lc.m_description+"'},\n";
	    	idNb++;
	    }
	    // write the topics
	    input += "// topics :\n";
	    for (int i=0; i<topicList.size(); i++) {
	    	int idNode = idNb+200;
	    	int idHallow = idNb+100;
	    	int idBigHallow = idNb;
	    	Topic tp = topicList.get(i);
	    	input += "{id:"+idNode+", label:'"+tp.m_name
	    			+"', x:"+tp.m_xPos+", y:"+tp.m_yPos
	    			+", category:'topic', color:'"+tp.m_color+"'},\n";
	    	String colorWithSmoothAlpha = tp.m_color.split(",")[0]+","+tp.m_color.split(",")[1]+","+tp.m_color.split(",")[2]+",0.5)";
	    	input += "{id:"+idHallow+", label:''"
	    			+", x:"+tp.m_xPos+", y:"+tp.m_yPos
	    			+", category:'highlightTopic', color:'"+colorWithSmoothAlpha+"'},\n";
	    	input += "{id:"+idBigHallow+", label:''"
	    			+", x:"+tp.m_xPosBigHallow+", y:"+tp.m_yPosBigHallow+", size:"+tp.m_sizeBigHallow
	    			+", category:'topicBigHallow', color:'"+colorWithSmoothAlpha+"'},\n";
	    	idNb++;
	    }
	    // write fake nodes to affect the zoom
	    input += "{id:2000, label:'', x:0, y:0, category:'', size:0.1},\n";
	    input += "{id:2001, label:'', x:"+coeff+", y:"+coeff+", category:'', size:0.1},\n";

	    input += "];\n";
	    input += "var edges = [\n";
	    
	    // BUILD THE EDGES
	    // build the edges between learning concepts
	    for (int i=0; i<learningConceptList.size(); i++) {
	    	int idCurrentNode = i+200;
	    	for (int j=0; j<learningConceptList.get(i).m_prerequisitesList.size(); j++) {
	    		int idNodePrerequisite = listConceptNames.
	    				indexOf(learningConceptList.get(i).m_prerequisitesList.get(j))+200;
	    		input += "{from:"+idNodePrerequisite+", to:"+idCurrentNode+"},\n";
	    	}
	    }
	    
	    // build the edges between learning concepts and topics
	    for (int i=0; i<topicList.size(); i++) {
	    	int idTopic = learningConceptList.size()+i+200;
	    	for (int j=0; j<topicList.get(i).m_associatedLearningConceptList.size(); j++) {
	    		int idLearningConcept = listConceptNames.
	    				indexOf(topicList.get(i).m_associatedLearningConceptList.get(j))+200;
	    		input += "{from:"+idLearningConcept+", to:"+idTopic+"},\n";
	    	}
	    }
	    
	    input += "];\n";
	    
        FileOutputStream fileOut = new FileOutputStream(file);
        try {
			fileOut.write(input.getBytes());
	        fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
        System.out.println("--- Finish ! ---");
	}

}
