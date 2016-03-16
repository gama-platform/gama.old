package msi.gama.doc.websiteGen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import msi.gama.doc.websiteGen.utilClasses.ConceptManager;
import msi.gama.doc.websiteGen.utilClasses.MetadataStructure;
import msi.gama.doc.websiteGen.utilClasses.ScreenshotStructure;
import msi.gama.doc.websiteGen.utilClasses.Utils;

public class modelLibraryGenerator {
	
	// inputs / outputs
	static String[] inputPathToModelLibrary = {"F:/Gama/GamaSource/msi.gama.models/models/",
			"F:/Gama/GamaSource/ummisco.gaml.extensions.maths/models",
			"F:/Gama/GamaSource/msi.gaml.extensions.fipa/models",
			"F:/Gama/GamaSource/simtools.gaml.extensions.physics/models"};
	static String outputPathToModelLibrary = "F:/gama_doc_17.wiki/References/ModelLibrary";
	static String inputFileForHeadlessExecution = "F:/gama_doc_17.wiki/tempInputForHeadless.xml";
	static String inputModelScreenshot = "F:/gama_doc_17.wiki/modelScreenshot.xml";
	
	static HashMap<String,ScreenshotStructure> mapModelScreenshot;
	static HashMap<String,String> mainKeywordsMap; // the key is the name of the model, the value is the metadata formated which contains all the important keywords of the model.
	static List<String> expeUsedFromTheXML = new ArrayList<String>(); // this variable is just here to verify if the modelScreenshot.xml is well formed, and if all
	// the experiments have been used.

	public static void main(String[] args) throws IOException {
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		// parse all the models of the model library, in order to build "input" files for a headless execution.
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// get all the gaml files in the model folder
		ArrayList<File> listFiles = new ArrayList<File>();
		for (String path : inputPathToModelLibrary) {
			ArrayList<File> listFilesTmp = new ArrayList<File>();
			Utils.getFilesFromFolder(path,listFilesTmp);
			for (File f : listFilesTmp) {
				listFiles.add(f);
			}
		}
		ArrayList<File> gamlFiles = Utils.filterFilesByExtension(listFiles,"gaml");
		
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
		// copy-paste all the generated images in the write folder, with the right names
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		
		System.out.println("----- Move the generated images to the write folder, with the right name -----");
		System.out.println("----> NOT IMPLEMENTED YET");
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// read all the metadatas of the model files, and extract only the GAML keywords "important".
		// Store those data in the map mainKeywordsMap.
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		
		System.out.println("----- Read all the meta files to generate the map of main keywords for each model -----");
		prepareMainKeywordMap(gamlFiles);
		System.out.println("----> selection of main keywords effectued !");
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// browse a second time all the models, build the md file, including the screenshots computed from the
		// headless execution, informations in the header of each model, and gaml keywords read from mainKeywordsMap.
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		
		System.out.println("----- Start to write md content -----");
		writeMdContent(gamlFiles);
		System.out.println("----> MD content generated !");
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// print further informations
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		
		ConceptManager.printStatistics();

	}
	
	private static void prepareMainKeywordMap(ArrayList<File> files) throws IOException {
		//read all the metadatas of the model files, and extract only the "important" GAML keywords.
		// Store those data in the map mainKeywordsMap.
		mainKeywordsMap = new HashMap<String,String>();
		HashMap<String,Integer> occurenceOfKeywords = new HashMap<String,Integer>(); // key is gaml world, value is occurence.
		ArrayList<String> mostSignificantKeywords = new ArrayList<String>(); // the list of the less employed gaml keywords.
		int maxOccurenceNumber = 20; // the maximum number of occurrence for the "mostSignificantKeywordsList".
		ArrayList<String> modelCategory = getSectionName();
		
		// store all the keywords in a list
		for (int fileIdx=0; fileIdx < files.size(); fileIdx++) {
			String absPath = files.get(fileIdx).getAbsolutePath();
			String absPathMeta = "";
			
			for (String modCat : modelCategory) {
				absPath = absPath.replace("\\", "/");
				absPathMeta = absPath.replace("models/"+modCat,"models/"+modCat+"/.metadata");
				absPathMeta = absPathMeta+".meta";
				
				// we have the meta file.
				File metaFile = new File(absPathMeta);
				if (metaFile.exists()) {
					ArrayList<String> gamlWords = getGAMLWords(new File(absPathMeta));
					for (String gamlWord : gamlWords) {
						if (occurenceOfKeywords.containsKey(gamlWord)) {
							// we increment the number of occurrence of the gaml word
							int oldVal = occurenceOfKeywords.get(gamlWord);
							occurenceOfKeywords.put(gamlWord, oldVal+1);
						}
						else {
							occurenceOfKeywords.put(gamlWord, 1);
						}
					}
				}
			}
		}
		
		// remove from the list the keywords which are not "important"
		for (String keyword : occurenceOfKeywords.keySet()) {
			if (occurenceOfKeywords.get(keyword)<maxOccurenceNumber) {
				mostSignificantKeywords.add(keyword);
			}
		}
		
		// browse a second time all the meta files, and store the most important keyword in the map
		for (int fileIdx=0; fileIdx < files.size(); fileIdx++) {
			String absPath = files.get(fileIdx).getAbsolutePath();
			String absPathMeta = "";
			for (String modCat : modelCategory) {
				absPath = absPath.replace("\\", "/");
				absPathMeta = absPath.replace("models/"+modCat,"models/"+modCat+"/.metadata");
				absPathMeta = absPathMeta+".meta";
				
				// we have the meta file.
				File metaFile = new File(absPathMeta);
				if (metaFile.exists()) {
					ArrayList<String> gamlWords = getGAMLWords(new File(absPathMeta));
					String metadataKeyword = "";
					for (String gamlWord : gamlWords) {
						if (mostSignificantKeywords.contains(gamlWord)) {
							metadataKeyword+="[//]: # (keyword|"+gamlWord+")\n";
						}
					}
					String modelKey = (absPathMeta.replace("/.metadata", "")).replace(".meta", "");
					mainKeywordsMap.put(modelKey, metadataKeyword);
				}
			}
		}
	}
	
	private static ArrayList<String> getGAMLWords(File file) throws IOException {
		// returns the list of experiments
		ArrayList<String> result = new ArrayList<String>();
		String extractedStr = "";
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String line = null;
		
		String[] categoryKeywords = {"operator", "type", "statement", "skill", "architecture", "constant"};
		
		while ((line = br.readLine()) != null) {
			for (String catKeywords : categoryKeywords) {
				extractedStr = Utils.findAndReturnRegex(line,catKeywords+"s=(.*)");
				String[] keywordArray = extractedStr.split("~");
				for (String kw : keywordArray) {
					if (catKeywords.equals("constant")) {
						kw = "#"+kw;
					}
					kw = catKeywords+"_"+kw;
					result.add(kw);
				}
			}
		}
		br.close();
		
		return result;
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
			
			modelName = Utils.getModelName(modelFile);
			expeNames = Utils.getExpeNames(modelFile);
			
			// browse all the experiments
			for (int expeIdx = 0; expeIdx < expeNames.size(); expeIdx++) {
				String experiment = expeNames.get(expeIdx);
				
				displayNames = getDisplayNamesByExpe(files.get(idx),experiment);
				
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
        		System.err.println("The experiment "+id+" has not been used because it does not exist !");
        	}
        }
	}
	
	private static ArrayList<String> getSectionName() {
		ArrayList<String> result = new ArrayList<String>();
		for (String path : inputPathToModelLibrary) {
			File directory = new File(path);
			String[] sectionNames = directory.list(new FilenameFilter() {
				  @Override
				  public boolean accept(File current, String name) {
				    return new File(current, name).isDirectory();
				  }
				});
			for (String sectionName : sectionNames) {
				result.add(sectionName);
			}
		}
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
				if (Utils.findAndReturnRegex(line,"^[\\t,\\s]+experiment (\\w+)") != "") {
					// we are out of the right experiment. Return the result.
					br.close();
					return result;
				}
				displayName = Utils.findAndReturnRegex(line,"[\\t,\\s]+display (\\w+)");
				if (displayName != "") {
					result.add(displayName);
					displayName = "";
				}
			}
			if (expeName.compareTo(Utils.findAndReturnRegex(line,"^[\\t,\\s]+experiment (\\w+)")) == 0) {
				inTheRightExperiment = true;
			}
		}
		br.close();
		
		return result;
	}
	
	private static void writeMdContent(ArrayList<File> gamlFiles) throws IOException {
		// load the concepts
		try {
			ConceptManager.loadConcepts();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		String sectionName = "";
		String subSectionName = "";
		
		for (int idx = 0 ; idx < gamlFiles.size() ; idx++) {
			File gamlFile = gamlFiles.get(idx);
			String header = extractHeader(gamlFile);
			
			// extract the header properties
			MetadataStructure metaStruct = new MetadataStructure(header);
				
			if (metaStruct.getName() != "") {
				
				// prepare the output file
				String fileName = "";
				fileName = gamlFile.getAbsolutePath().replace("\\", "/");
				boolean isAdditionnalPlugin = false;
				for (String path : inputPathToModelLibrary) {
					if (fileName.contains(path)) {
						if (!path.equals(inputPathToModelLibrary[0])) {
							isAdditionnalPlugin = true;
						}
						fileName = fileName.split(path)[1];
					}
				}
				if (!fileName.contains("include")) {
					String newSubSectionName = fileName.split("/")[1];
					String newSectionName = fileName.split("/")[0];
					String modelFileName = fileName.split("/")[fileName.split("/").length-1];
					String modelName = metaStruct.getName();
					if (isAdditionnalPlugin) {
						newSectionName = "Additionnal Plugins";
					}
					fileName = newSectionName+"/"+newSubSectionName+"/"+modelFileName;
					fileName = fileName.replace(".gaml", "");
					fileName = fileName.replace("/models", "");
					
					// manipulate section and subsection files
					// case of "sub-section" (ex : 3D Visualization, Agent movement...)
					if (!subSectionName.equals(newSubSectionName)) {
						createSubSectionFile(outputPathToModelLibrary + "/" + newSectionName + "/" + newSubSectionName + ".md");
					}
					addModel(outputPathToModelLibrary + "/" + newSectionName + "/" + newSubSectionName + ".md",modelName,modelFileName.replace(".gaml", ""));
					// case of "section" (ex : Features, Toy Models...)
					if (!sectionName.equals(newSectionName)) {
						createSectionFile(outputPathToModelLibrary + "/" + newSectionName + ".md");
					}
					if (!subSectionName.equals(newSubSectionName)) {
						addSubSection(outputPathToModelLibrary + "/" + newSectionName + ".md",newSubSectionName);
					}
					subSectionName = newSubSectionName;
					sectionName = newSectionName;
					
					String outputFileName = outputPathToModelLibrary + "/" + fileName;
					outputFileName = outputFileName+".md";
					File outputFile = new File(outputFileName);
					
					Utils.CreateFolder(outputFile.getParentFile());
					outputFile.createNewFile();
					FileOutputStream fileOut = new FileOutputStream(outputFile);
					
					// write the header
					fileOut.write(mainKeywordsMap.get(gamlFile.getAbsolutePath().replace("\\", "/")).getBytes());
					fileOut.write(metaStruct.getMdHeader().getBytes());
					
					// write the input (if there are any)
					List<String> inputFileList = searchInputList(gamlFile);
					if (inputFileList.size() > 0) {
						if (inputFileList.size()>1) {
							fileOut.write(new String("Imported models : \n\n").getBytes());
						}
						else {
							fileOut.write(new String("Imported model : \n\n").getBytes());
						}
					}
					for (String inputPath : inputFileList) {
						// write the code of the input files
						fileOut.write(getModelCode(new File(inputPath)).getBytes());
						fileOut.write(new String("\n\n").getBytes());
					}
					
					// write the code
					fileOut.write(new String("Code of the model : \n\n").getBytes());
					fileOut.write(getModelCode(gamlFile).getBytes());
					fileOut.close();
				}
			}
		}
	}
	
	
	private static void createSectionFile(String pathToSectionFile) throws IOException {
		File outputFile = new File(pathToSectionFile);
		Utils.CreateFolder(outputFile.getParentFile());
		outputFile.createNewFile();
		FileOutputStream fileOut = new FileOutputStream(outputFile);
		
		String sectionName = pathToSectionFile.split("/")[pathToSectionFile.split("/").length-1].replace(".md", "");
		fileOut.write(new String("# "+sectionName+"\n\nThis section is composed of the following sub-section :\n\n").getBytes());
		fileOut.close();
	}
	
	private static void createSubSectionFile(String pathToSubSectionFile) throws IOException {
		File outputFile = new File(pathToSubSectionFile);
		Utils.CreateFolder(outputFile.getParentFile());
		outputFile.createNewFile();
		FileOutputStream fileOut = new FileOutputStream(outputFile);
		
		String sectionName = pathToSubSectionFile.split("/")[pathToSubSectionFile.split("/").length-1].replace(".md", "");
		fileOut.write(new String("# "+sectionName+"\n\nThis sub-section is composed of the following models :\n\n").getBytes());
		fileOut.close();
	}
	
	private static void addSubSection(String pathToSectionFile, String subSectionName) throws IOException {
//		String urlToSubSection = Utils.getUrlFromName(subSectionName);
		String urlToSubSection = subSectionName.replace(" ", "");
		Files.write(Paths.get(pathToSectionFile), new String("* ["+subSectionName+"](references#"+urlToSubSection+")\n\n").getBytes(), StandardOpenOption.APPEND);
	}
	
	private static void addModel(String pathToSubSectionFile, String modelName, String modelFileName) throws IOException {
//		String urlToModel = Utils.getUrlFromName(modelName);
		String urlToModel = modelFileName.replace(" ", "");
		Files.write(Paths.get(pathToSubSectionFile), new String("* ["+modelName+"](references#"+urlToModel+")\n\n").getBytes(), StandardOpenOption.APPEND);
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
	
	private static ArrayList<String> searchInputList(File file) throws IOException {
		// returns the header
		ArrayList<String> result = new ArrayList<String>();
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String line = null;
		
		line = br.readLine();
		// search for a line that starts with "import"
		while (line!=null)
		{
			String regexMatch = Utils.findAndReturnRegex(line,"import \"(.*[^\"])\"");
			if (regexMatch != "") {
				result.add(file.getParentFile().getAbsolutePath().replace("\\","/")+"/"+regexMatch);
			}
			line = br.readLine();
		}
		br.close();
		return result;
	}
	
	private static String getModelCode(File gamlFile) throws IOException {
		// write the code
		String result = "";
		result = "```\n";
		FileInputStream fis = new FileInputStream(gamlFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		boolean inHeader = true;
		while ((line = br.readLine()) != null) {
			if (!inHeader) {
				// we are in the code
				result += line+"\n";
			}
			else if (line.startsWith("*/") || line.startsWith(" */")) {
				// we are out of the header
				inHeader = false;
			}
			else if (line.startsWith("model")) {
				// we are in the code
				inHeader = false;
				result += line+"\n";
			}
		}
		result += "```\n";
		br.close();
		return result;
	}
}
