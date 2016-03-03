package msi.gama.doc.websiteGen.utilClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	
	public static void getFilesFromFolder(String folderPath, ArrayList<File> files) {
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
	
	public static ArrayList<File> filterFilesByExtension(ArrayList<File> inputList, String ext)
	{
		ArrayList<File> result = new ArrayList<File>();
		for (int i=0; i<inputList.size(); i++) {
			if (inputList.get(i).getAbsoluteFile().toString().endsWith(ext))
				result.add(inputList.get(i));
		}
		return result;
	}
	
	public static String getModelName(File file) throws IOException {
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
	
	public static ArrayList<String> getExpeNames(File file) throws IOException {
		// returns the list of experiments
		ArrayList<String> result = new ArrayList<String>();
		String expeName = "";
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String line = null;
		
		while ((line = br.readLine()) != null) {
			expeName = Utils.findAndReturnRegex(line,"experiment (\\w+)");
			if (expeName != "") {
				result.add(expeName);
				expeName = "";
			}
		}
		br.close();
		
		return result;
	}
	
	public static ArrayList<String> getConceptKeywords(File file) throws IOException {
		// returns the list of concept keywords
		ArrayList<String> result = new ArrayList<String>();
		String concept = "";
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String line = null;
		
		while ((line = br.readLine()) != null) {
			concept = Utils.findAndReturnRegex(line,"\\[//\\]: # \\(keyword\\|concept_(.*)\\)");
			if (concept != "") {
				result.add(concept);
				concept = "";
			}
		}
		br.close();
		
		return result;
	}
	
	public static String findAndReturnRegex(String line, String regex)
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
