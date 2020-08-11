package msi.gama.doc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.precompiler.doc.utils.Constants;

public class HomeToTOC {
	
	public final static  int MD_LEVEL_ERR = -1; // no header
	public final static  int MD_LEVEL0 = 0;   	// empty line
	public final static  int MD_LEVEL1 = 1;   	// #
	public final static  int MD_LEVEL2 = 2;   	// ##
	public final static  int MD_LEVEL3 = 3;   	// 1.  
	public final static  int MD_LEVEL4 = 4;	  	//   1.
	
	// XML  Elements
	public final static String XML_ELT_TOC  	= "toc";
	public final static String XML_ATTR_TITLE 	=  "title"; 
	
	public final static String XML_ELT_PART  	= "part";
	public final static String XML_ELT_SUBPART  = "subpart";
	public final static String XML_ELT_CHAPTER  = "chapter";

	public final static String XML_ATTR_NAME 	= "name"; 
	public final static String XML_ATTR_FILE 	= "file"; 
	
	// Toc file  name
	public final static String TITLE_MESSAGE 	= "Full Documentation of GAMA " + Constants.GAMA_VERSION;
	public final static String DEFAULT_FILE		= "G__BlankPage";
	
	public final static String MD_EXTENSION		= ".md";
	
	
	public static void md2toc(String tocMDFile, String savePath, String wikiFolder) {
		String  line = "";
		Document doc = createDocument();
		 
        // root element
        Element root = doc.createElement(HomeToTOC.XML_ELT_TOC);
        root.setAttribute(HomeToTOC.XML_ATTR_TITLE, HomeToTOC.TITLE_MESSAGE);
		
        Element currentPart = null;
        Element currentSubPart =  null;
        
		try (BufferedReader br = new BufferedReader(new FileReader(tocMDFile));) {
			while ((line = br.readLine()) != null) {

		        switch (getLineHeader(line)) {
	            	case HomeToTOC.MD_LEVEL2:  
	            		if(currentPart != null) {
		            		if(currentSubPart != null) {
		            			currentPart.appendChild(currentSubPart);
		            		}
	            			root.appendChild(currentPart);
	            		}
	            		// Create  a new part
	            		currentPart = doc.createElement(HomeToTOC.XML_ELT_PART);
	            		currentPart.setAttribute(HomeToTOC.XML_ATTR_NAME, getLineTitle(line));
	            		//  Create  also  a new subpart
	            		currentSubPart = doc.createElement(HomeToTOC.XML_ELT_SUBPART);
	            		currentSubPart.setAttribute(HomeToTOC.XML_ATTR_NAME, getLineTitle(line));
	            		currentSubPart.setAttribute(HomeToTOC.XML_ATTR_FILE, getLineFilePath(line,wikiFolder));
	                    break;
	            	case HomeToTOC.MD_LEVEL3:  
	            		if( (currentPart != null) && (currentSubPart != null) ) {
		            		currentPart.appendChild(currentSubPart);
	            		}	            		
	            		currentSubPart = doc.createElement(HomeToTOC.XML_ELT_SUBPART);
	            		currentSubPart.setAttribute(HomeToTOC.XML_ATTR_NAME, getLineTitle(line));
	            		currentSubPart.setAttribute(HomeToTOC.XML_ATTR_FILE, getLineFilePath(line,wikiFolder));		        
	                    break;
	            	case HomeToTOC.MD_LEVEL4:  
	            		Element chapterElt = doc.createElement(HomeToTOC.XML_ELT_CHAPTER);
	            		chapterElt.setAttribute(HomeToTOC.XML_ATTR_NAME, getLineTitle(line));
	            		chapterElt.setAttribute(HomeToTOC.XML_ATTR_FILE, getLineFilePath(line,wikiFolder));		        
	            		currentSubPart.appendChild(chapterElt);
	                    break;
	                default: 
	                	break;
		        }
			}
			
	        doc.appendChild(root);		            
            saveDocument(doc, savePath);
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private static Document createDocument() {
        Document doc = null;
	
        try  {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
	        doc = documentBuilder.newDocument();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return doc;	
	}
	
	private  static void saveDocument(Document doc, String xmlFilePath) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;

        try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
        
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File(xmlFilePath));

        try {
			transformer.transform(domSource, streamResult);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

        System.out.println("Done saving XML File");
	}
	
	
	// From  a  line of the Home.md  file (e.g. ## [Home](Home)), computes the header level of  the line
	//   or whether it is an empty line.
	private static int getLineHeader(String line) {
		if(line.startsWith("## "))  {
			return HomeToTOC.MD_LEVEL2;
		}
		
	    Pattern patternEnum = Pattern.compile("^(\\d+.*)");
	    Matcher matcher = patternEnum.matcher(line);
	    if(matcher.matches()) {
	    	return HomeToTOC.MD_LEVEL3;
	    }

	    Pattern patternEnumLevel2 = Pattern.compile("^(\\s\\s\\d+.*|\\t\\d+.*)");
	    Matcher matcherLevel2 = patternEnumLevel2.matcher(line);
	    if(matcherLevel2.matches()) {
	    	return HomeToTOC.MD_LEVEL4;
	    }	    
	    
	    Pattern patternWhiteSpace = Pattern.compile("[\\s|\\t|\\n]*");
	    Matcher matcherSWhite = patternWhiteSpace.matcher(line);
	    if(matcherSWhite.matches()) {
	    	return HomeToTOC.MD_LEVEL0;
	    }	    	    
		
		return HomeToTOC.MD_LEVEL_ERR;
	}

	// From any line such as: ## [Introduction](Overview)
	//   returns the  title, i.e. the string between brackets (Introduction here)
	//
	// ## [Introduction](Overview)   ----->  Introduction	
	private static String getLineTitle(String line) throws IOException {
		if( line.contains("[")) {
			int indexBegin 	= line.indexOf("[");
			int indexEnd 	= line.lastIndexOf("]");
			
			return line.substring(indexBegin+1, indexEnd);	
		} 
		return line;
	}
	
	// From any line such as: ## [Introduction](Overview)
	//   returns the  file name, i.e. the string between parentheses (Overview here)
	//
	// ## [Introduction](Overview)   ----->  Overview
	private static String getLineFile(String line) {
		if( line.contains("[")) {
			int indexEndTitle 	= line.lastIndexOf("]");
			String endLine = line.substring(indexEndTitle+1,line.length());
			
			int indexBegin 	= endLine.indexOf("(");
			int indexEnd 	= endLine.lastIndexOf(")");
			
			return endLine.substring(indexBegin+1, indexEnd);	
		} 
		return DEFAULT_FILE;
	}
	
	// From any line such as: ## [Introduction](Overview)
	//   returns the  file relative path
	//
	// ## [Inspectors and Monitors](InspectorsAndMonitors)   ----->  References/PlatformDocumentation/RunningExperiments/InspectorsAndMonitors
	private static String getLineFilePath(String line, String wikiFolder) {
		String lineFile = getLineFile(line);
		String lineFilePath = getRelativePathToWiki(lineFile,wikiFolder);
		return lineFilePath;
	}	
	
	
	private static String getRelativePathToWiki(String fileName, String wikiFolder) {
		String resPath = "";
		
		try (Stream<Path> stream = Files.find(Paths.get(wikiFolder), 5,
	            (path, attr) -> path.getFileName().toString().equals(fileName+HomeToTOC.MD_EXTENSION) )) {
			Optional<Path> val = stream.findFirst();
			String path = val.isPresent()?val.get().toString():"";
			
	//		System.out.println(path);
			
	        resPath = path.substring( (wikiFolder + File.separator).length(), path.length()-HomeToTOC.MD_EXTENSION.length());
		} catch (IOException e) {
	        e.printStackTrace();
		}	        
	    return resPath;
	}
		
	
	public static void main(String[] args) {
		final String tocMDFile = Constants.TOC_SIDEBAR_FILE;
		
		HomeToTOC.md2toc(tocMDFile,Constants.TOC_FILE_PATH,Constants.WIKI_FOLDER);
		
		String s = "InspectorsAndMonitors";
	 
		System.out.println(getRelativePathToWiki(s,Constants.WIKI_FOLDER));
		
	}

}
