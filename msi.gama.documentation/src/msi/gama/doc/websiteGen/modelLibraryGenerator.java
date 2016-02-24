package msi.gama.doc.websiteGen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class modelLibraryGenerator {
	
	// inputs / outputs
	static String inputPathToModelLibrary = "F:/Gama/GamaSource/msi.gama.models/models/";
	static String outputPathToModelLibrary = "F:/gama_doc_17.wiki/References/ModelLibrary";
	static String inputFileForHeadlessExecution = "F:/gama_doc_17.wiki/tempInputForHeadless.xml";
	static String inputModelScreenshot = "F:/gama_doc_17.wiki/modelScreenshot.xml";
	
	static HashMap<String,ScreenshotStructure> mapModelScreenshot;
	static List<String> expeUsedFromTheXML = new ArrayList<String>(); // this variable is just here to verify if the modelScreenshot.xml is well formed, and if all
	// the experiments have been used.

	public static void main(String[] args) throws IOException {
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		// parse all the models of the model library, in order to build "input" files for a headless execution.
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// get all the gaml files in the model folder
		ArrayList<File> listFiles = new ArrayList<File>();
		getFilesFromFolder(inputPathToModelLibrary,listFiles);
		ArrayList<File> gamlFiles = filterFilesByExtension(listFiles,"gaml");
		
		// read modelScreenshot.xml
		System.out.println("----- Start to load the file "+inputModelScreenshot+" -----");
		loadModelScreenshot();
		System.out.println("----> file "+inputModelScreenshot+" loaded properly !");
		
		// create input file if experiment is found
		System.out.println("----- Start to write the input xml for headless -----");
		prepareInputFileForHeadless(gamlFiles);
		System.out.println("----> file "+inputFileForHeadlessExecution+" written properly !");
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// execute the headless
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		
		System.out.println("----- Execute the headless -----");
		System.out.println("----> NOT IMPLEMENTED YET");
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// copy-paste all the generated images in the write folder, with the write names
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		
		System.out.println("----- Move the generated images to the write folder, with the write name -----");
		System.out.println("----> NOT IMPLEMENTED YET");
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// browse a second time all the models, build the md file, including the screenshot computed from the headless execution.
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		
		System.out.println("----- Start to write md content -----");
		writeMdContent(gamlFiles);
		System.out.println("----> MD content generated !");

	}
	
	private static void loadModelScreenshot() {
		// read modelScreenshot.xml, and load it to mapModelScreenshot.
		//    the extended name of the experiment is the key, the pair {displayName,cycleNumber} is the value.
		mapModelScreenshot = new HashMap<String,ScreenshotStructure>();
	    try {
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(inputModelScreenshot);
	    			
	    	doc.getDocumentElement().normalize();
	    			
	    	NodeList nList = doc.getElementsByTagName("experiment");

	    	for (int temp = 0; temp < nList.getLength(); temp++) {

	    		Node nNode = nList.item(temp);
	    				
	    		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

	    			Element eElement = (Element) nNode;
	    			
	    			String id = eElement.getAttribute("id");
	    			ScreenshotStructure screenshot =  new ScreenshotStructure(id);
	    			for (int i=0; i < eElement.getElementsByTagName("display").getLength(); i++) {
	    				String displayName = ((Element) eElement.getElementsByTagName("display").item(i)).getAttribute("name");
	    				int cycleNumber = Integer.valueOf(((Element) eElement.getElementsByTagName("display").item(i)).getAttribute("cycle_number"));
	    				if (cycleNumber == 0)
	    					cycleNumber = 10;
	    				screenshot.addDisplay(displayName, cycleNumber);
	    			}
	    			mapModelScreenshot.put(id, screenshot);
	    		}
	    	}
	    	} catch (Exception e) {
	    	e.printStackTrace();
	        }
	}
	
	private static void prepareInputFileForHeadless(ArrayList<File> files) throws IOException {
		// prepare the output file
        File outputFile = new File(inputFileForHeadlessExecution);
		FileOutputStream fileOut = new FileOutputStream(outputFile);
		
        fileOut.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
        fileOut.write("<Experiment_plan>\n".getBytes());
        
        // browse all the model files
		for (int idx = 0 ; idx < files.size(); idx++) {
			String modelName = "";
			File modelFile = files.get(idx);
			ArrayList<String> expeNames = new ArrayList<String>();
			ArrayList<String> displayNames = new ArrayList<String>();
			
			modelName = getModelName(modelFile);
			expeNames = getExpeNames(modelFile);
			
			// browse all the experiments
			for (int expeIdx = 0; expeIdx < expeNames.size(); expeIdx++) {
				String experiment = expeNames.get(expeIdx);
				
				displayNames = getDisplayNamesByExpe(files.get(idx),experiment);
				
//				String formatedFileName = formatString(modelFile.getName());
				String formatedFileName = modelFile.getName().replace(".gaml", "");
				
		        String expeId = formatedFileName + " " + modelName + " " + experiment;
		        if (mapModelScreenshot.containsKey(expeId)) {
		        	expeUsedFromTheXML.add(expeId);
		        	if (mapModelScreenshot.get(expeId).checkDisplayName(displayNames)) {
		        		fileOut.write(mapModelScreenshot.get(expeId).getXMLContent(Integer.toString(idx*1000+expeIdx),modelFile.getAbsolutePath(),experiment).getBytes());
		        	}
		        }
		        else {
			        fileOut.write(new String("  <Simulation id=\""+expeIdx+"\" sourcePath=\""+modelFile.getAbsoluteFile()+"\" finalStep=\"100\" experiment=\""+experiment+"\">\n").getBytes());
			        fileOut.write("    <Outputs>\n".getBytes());
			        
			        // browse all the displays
			        for (int displayIdx = 0 ; displayIdx < displayNames.size() ; displayIdx++) {
			        	String display = displayNames.get(displayIdx);
			        	fileOut.write(new String("      <Output id=\""+idx+1+"\" name=\""+display+"\" framerate=\"10\" />\n").getBytes());
			        }
			        
			        fileOut.write("    <\\Outputs>\n".getBytes());
			        fileOut.write("  <\\Simulation>\n".getBytes());
		        }
			}
		}
        fileOut.write("<\\Experiment_plan>\n".getBytes());
        fileOut.close();
        
        // check if all the experiment id from the modelScreenshot have been used.
        Iterator<String> it = mapModelScreenshot.keySet().iterator();
        while (it.hasNext()) {
        	String id = it.next();
        	if (!expeUsedFromTheXML.contains(id)) {
        		System.out.println("WARNING : The experiment "+id+" has not been used because it does not exist !");
        	}
        }
	}
	
	private static ArrayList<String> getExpeNames(File file) throws IOException {
		// returns the list of experiments
		ArrayList<String> result = new ArrayList<String>();
		String expeName = "";
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String line = null;
		
		while ((line = br.readLine()) != null) {
			expeName = findAndReturnRegex(line,"^experiment (\\w+)");
			if (expeName != "") {
				result.add(expeName);
				expeName = "";
			}
		}
		br.close();
		
		return result;
	}
	
	private static ArrayList<String> getDisplayNamesByExpe(File file, String expeName) throws IOException {
		// returns the list of experiments
		ArrayList<String> result = new ArrayList<String>();
		String displayName = "";
		
		boolean inTheRightExperiment = false;
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String line = null;
		
		while ((line = br.readLine()) != null) {
			if (inTheRightExperiment) {
				if (findAndReturnRegex(line,"^experiment (\\w+)") != "") {
					// we are out of the right experiment. Return the result.
					br.close();
					return result;
				}
				displayName = findAndReturnRegex(line,"^[\\t,\\s]+display (\\w+)");
				if (displayName != "") {
					result.add(displayName);
					displayName = "";
				}
			}
			if (expeName.compareTo(findAndReturnRegex(line,"^experiment (\\w+)")) == 0) {
				inTheRightExperiment = true;
			}
		}
		br.close();
		
		return result;
	}
	
	private static void writeMdContent(ArrayList<File> gamlFiles) throws IOException {
		for (int idx = 0 ; idx < gamlFiles.size() ; idx++) {
			File gamlFile = gamlFiles.get(idx);
			String header = extractHeader(gamlFile);
			if (header != "") {
				// extract the header properties
				MetadataStructure metaStruct = new MetadataStructure(header);
				
				// prepare the output file
				String fileName = "";
				fileName = gamlFile.getAbsolutePath().replace("\\", "/");
				fileName = fileName.split(inputPathToModelLibrary)[1];
				fileName = fileName.replace("/", "_");
				fileName = formatString(fileName);
				String outputFileName = outputPathToModelLibrary + "/" + fileName;
				outputFileName = outputFileName+".md";
				File outputFile = new File(outputFileName);
				FileOutputStream fileOut = new FileOutputStream(outputFile);
				
				// write the header
				fileOut.write(metaStruct.getMdHeader().getBytes());
				
				// write the code
				fileOut.write("```\n".getBytes());
				FileInputStream fis = new FileInputStream(gamlFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String line = null;
				boolean inHeader = true;
				while ((line = br.readLine()) != null) {
					if (!inHeader) {
						// we are in the code
						fileOut.write(new String(line+"\n").getBytes());
					}
					if (line.startsWith("*/")) {
						// we are out of the header
						inHeader = false;
					}
				}
				fileOut.write("```\n".getBytes());
				fileOut.close();
				br.close();
			}
		}
	}
	
	private static String extractHeader(File file) throws IOException {
		// returns the header
		String result = "";
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String line = null;
		
		// check if the file contains a header
		if ((line = br.readLine()).startsWith("/**")) {
			result += line+"\n";
			while ((line = br.readLine()) != null) {
				result += line+"\n";
				if (line.startsWith("*/") || line.startsWith(" */")) {
					break;
				}
			}
		}
		else {
			br.close();
			System.out.println("WARNING : file "+file.getName()+" has no header !");
		}
		br.close();
		return result;
	}	
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Util functions
	//////////////////////////////////////////////////////////////////////////////////////
	
	private static void getFilesFromFolder(String folderPath, ArrayList<File> files) {
		File folder = new File(folderPath);
	    File[] fList = folder.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
	        	files.add(file);
	        } else if (file.isDirectory()) {
	        	getFilesFromFolder(file.getAbsolutePath(), files);
	        }
	    }
	}
	
	private static ArrayList<File> filterFilesByExtension(ArrayList<File> inputList, String ext)
	{
		ArrayList<File> result = new ArrayList<File>();
		for (int i=0; i<inputList.size(); i++) {
			if (inputList.get(i).getAbsoluteFile().toString().endsWith(ext))
				result.add(inputList.get(i));
		}
		return result;
	}
	
	private static String formatString(String str) {
		// remove extention, replace strange char by "_".
		return (((((str.replace(".gaml",""))
				.replace("(", "_")).replace(")", "_")).replace(",","_"))
				.replace(".", "_")).replace(" ", "_");
	}
	
	private static String getModelName(File file) throws IOException {
		// returns the name of the model
		String result="";
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String line = null;
		
		while ((line = br.readLine()) != null) {
			result = findAndReturnRegex(line,"^model (\\w+)");
			if (result != "") {
				break;
			}
		}
		br.close();
		
		return result;
	}
	
	private static String findAndReturnRegex(String line, String regex)
	{
		String str = "";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			str = matcher.group(1);
		}
		return str;
	}
}
