package msi.gama.doc.websiteGen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class modelLibraryGenerator {
	
	// inputs / outputs
	static String inputPathToModelLibrary = "F:/Gama/GamaSource/msi.gama.models/models";
	static String outputPathToModelLibrary = "F:/gama_doc_17.wiki/References/ModelLibrary";
	static String inputFolderForHeadlessExecution = "F:/gama_doc_17.wiki/tempInputForHeadless"; // is deleted at the end of the execution

	public static void main(String[] args) throws IOException {
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		// parse all the models of the model library, in order to build "input" files for a headless execution.
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// delete the input folder for headless execution
		File directory = new File(inputFolderForHeadlessExecution);
		FileUtils.cleanDirectory(directory);
		directory.delete();
		
		// create empty folder
		boolean success = (new File(inputFolderForHeadlessExecution)).mkdirs();
		if (!success) {
		    System.out.println("FAIL");
		}
		
		// get all the gaml files in the model folder
		ArrayList<File> listFiles = new ArrayList<File>();
		getFilesFromFolder(inputPathToModelLibrary,listFiles);
		ArrayList<File> gamlFiles = filterFilesByExtension(listFiles,"gaml");
		
		// create input file if experiment is found
		prepareInputFilesForHeadless(gamlFiles);
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// browse a second time all the models, and call the headless script for each one of the models. 
		//For each models, build the md file, including the screenshot computed from the heatless execution.
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		writeMdContent(gamlFiles);

	}
	
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
	
	private static void writeInputFile(File model, String name, String experiment, ArrayList<String> displays) throws IOException {

        String outputFileName = inputFolderForHeadlessExecution + "/" + model.getName().replace(" ", "_") + "_" + name + "_" + experiment + ".xml";
        File outputFile = new File(outputFileName);
		FileOutputStream fileOut = new FileOutputStream(outputFile);
        fileOut.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
        fileOut.write(new String("<Simulation id=\"2\" sourcePath=\""+model.getAbsoluteFile()+"\" finalstep=\"100\" experiment=\""+experiment+"\">\n").getBytes());
        fileOut.write("  <Outputs>\n".getBytes());
        for (int idx = 0 ; idx < displays.size() ; idx++) {
        	fileOut.write(new String("    <Output id=\""+idx+1+"\" name=\""+displays.get(idx)+"\" framerate=\"10\" />\n").getBytes());
        }
        fileOut.write("  <\\Outputs>\n".getBytes());
        fileOut.write("<\\Simulation>\n".getBytes());
        fileOut.close();
	}
	
	private static void prepareInputFilesForHeadless(ArrayList<File> files) throws IOException {
		for (int idx = 0 ; idx < files.size(); idx++) {
			String modelName = "";
			ArrayList<String> expeNames = new ArrayList<String>();
			ArrayList<String> displayNames = new ArrayList<String>();
			
			modelName = getModelName(files.get(idx));
			expeNames = getExpeNames(files.get(idx));
			
			for (int expeIdx = 0; expeIdx < expeNames.size(); expeIdx++) {
				displayNames = getDisplayNamesByExpe(files.get(idx),expeNames.get(expeIdx));
				writeInputFile(files.get(idx),modelName,expeNames.get(expeIdx),displayNames);
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
				fileName = fileName.replace(" ", "_");
				String outputFileName = outputPathToModelLibrary + "/" + fileName;
				outputFileName = outputFileName.replace(".gaml", ".md");
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
}
