package msi.gama.doc.checkURL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckURL {
	
	static final String localPathFileName = "localPath.txt";
	static final String githubContentUrl = "https://github.com/gama-platform/gama/wiki/Content";
	static final String githubGamaSourceUrl = "https://github.com/gama-platform/gama/tree/master";
	static String pathToContent;
	static Map<String,String> fileMap = new HashMap<String,String>();
	
	static Map<String,Integer> forbiddenSyntaxMap = new HashMap<String,Integer>();
	static final String[] listOfForbiddenSyntax = {"<img src","<a href","TODO","Under Construction"};
	static int numberOfErrorsDetected = 0;

	public static void main(String[] args) {
		// init forbiddenSyntaxMap
		for (int i=0; i<listOfForbiddenSyntax.length; i++)
		{
			forbiddenSyntaxMap.put(listOfForbiddenSyntax[i], 0);
		}
		
		// find the local path to the wiki content
		try {
			if (!loadPathToContent())
				return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (!checkIfFolderExists(pathToContent))
			return;
		
		// get all the md files in the content folder
		ArrayList<File> listFiles = new ArrayList<File>();
		getFilesFromFolder(pathToContent,listFiles);
		ArrayList<File> mdFiles = filterFilesByExtension(listFiles,"md");
		
		// init the map <fileName,relativePath>. If 2 fileNames are the same, write error message
		if (!initMap(mdFiles))
			return;

		// for each files, find the pattern [xxx](path), and check if the path exists. 
		//... If it is not correct, change it is possible, or write an error message if not possible.
		for (int i=0; i<mdFiles.size(); i++) {
			try {
				readAndRewriteMDFile(mdFiles.get(i));
			} catch (IOException e) {}
		}
		
		// display number of errors detected.
		printNumberOfErrorDetected();
		
		// display message if forbidden syntax has been found.
		printForbiddenSyntax();
	}
	
	private static boolean loadPathToContent() throws FileNotFoundException{
		boolean result=false;
		Path relativePath = Paths.get(localPathFileName);
		String relativePathString = relativePath.toAbsolutePath().toString();
		File f = new File(relativePathString);
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("The file "+f.getAbsolutePath()+" has been created.");
		}
		
		Scanner scanner = new Scanner(f);
		String content = "";
		BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()));     
		try {
			if (br.readLine() != null) {
				result = true;
				content = scanner.useDelimiter("\\Z").next();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String outputMsg = (result) ? "The path for your content directory is : "+content :
			"The file "+f.getAbsolutePath()+" is empty.\nPlease set the path to the content and run again the application.";
		pathToContent = content;
		
		System.out.println(outputMsg);
		scanner.close();
		return result;
	}
	
	private static boolean checkIfFolderExists(String folderPath) {
		File f = new File(folderPath);
		if (f.exists() && f.isDirectory())
			return true;
		System.out.println("FATAL ERROR : Folder "+folderPath+" does not exist.");
		return false;
	}
	
	private static boolean checkIfFileExists(String filePath) {
		File f = new File(filePath);
		if (f.exists() && !f.isDirectory())
			return true;
		System.out.println("----> ERROR : File "+filePath+" does not exist.");
		numberOfErrorsDetected++;
		return false;
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
	
	private static boolean initMap(ArrayList<File> fileList) {
		for (int i=0; i<fileList.size(); i++) {
			String filePathName = fileList.get(i).getAbsolutePath();
			String fileName;
			filePathName = filePathName.replace(pathToContent,githubContentUrl);
			String[] filePathNameSplitted = filePathName.split((File.separator+File.separator));
			fileName = filePathNameSplitted[filePathNameSplitted.length-1];
			// verify it the file does not exist yet in the map
			if (fileMap.containsKey(fileName)) {
				System.out.println("----> FATAL ERROR : filename "+fileName+"already exists !");
				return false;
			}
			fileMap.put(fileName, filePathName);
		}
		System.out.println("List of the referenced files : "+fileMap.keySet());
		return true;
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
	
	private static void readAndRewriteMDFile(File file) throws IOException {
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String line = null;
		String input = "";
		
		while ((line = br.readLine()) != null) {
			if (input == "")
				input = recursiveFindAndReplaceRegex(line,file,"");
			else
				input = input +'\n'+ recursiveFindAndReplaceRegex(line,file,"");
		}
		br.close();
		
        // write the new String with the replaced line OVER the same file
        FileOutputStream fileOut = new FileOutputStream(file);
        fileOut.write(input.getBytes());
        fileOut.close();
	}
	
	private static String recursiveFindAndReplaceRegex(String str, File file, String residus)
	{
		String path = file.getParent();
		Pattern pattern = Pattern.compile("(.*\\0133.*\\0135\\050)([^\\043].*?)(\\051.*)");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			String firstPart = recursiveFindAndReplaceRegex(matcher.group(1),file, matcher.group(2)+matcher.group(3));
			String stringMatched = matcher.group(2);
			stringMatched = formatString(stringMatched);
			String newURL = stringMatched;
			if (stringMatched.startsWith("images"+File.separator) || stringMatched.startsWith("images/")) {
				// case of image link. Image links are relative path
				checkIfFileExists(path+File.separator+matcher.group(2));
			}
			else if (stringMatched.startsWith(githubGamaSourceUrl))
			{
				// case of link to a file of gamaSource. Check if the file exists
				checkIfFileExists(getGamaSourceLocalPath()+stringMatched.split(githubGamaSourceUrl)[1]);
			}
			else if (stringMatched.contains("www."))
			{
				// case of web URL. Do nothing.
			}
			else
			{
				// case of other links, have to be in githubUrl format.
				// extract the filename
				String fileName = stringMatched.split(File.separator+File.separator)[stringMatched.split(File.separator+File.separator).length-1];
				
				// if the link contains an anchor, extract the anchor
				String anchor = "";
				if (fileName.contains("#"))
				{
					anchor = "#"+fileName.split("#")[1];
					fileName = fileName.split("#")[0];
				}
				
				// make changes (in case it is an old version)
				if (!fileName.endsWith(".md"))
					fileName = fileName + ".md";
				if (fileName.startsWith("G__"))
					fileName = fileName.replace("G__", "");
				if (fileName.startsWith("Tutorial__"))
					fileName = fileName.replace("Tutorial__", "");
				
				// check if the file exists in the map
				if (!fileMap.containsKey(fileName)) {
					System.out.println("----> ERROR in file "+file.getName()+": " + fileName + " is not a referenced file...");
					numberOfErrorsDetected++;
				}
				else {
					// find in the map the correct URL to put
					newURL = fileMap.get(fileName)+anchor;
					if (!newURL.equals(stringMatched))
						System.out.println("----> MODIFICATION : "+stringMatched+" changed into "+newURL);
				}
			}
			str = firstPart+newURL+matcher.group(3);
		}
		checkPresenceOfForbiddenSyntax(str);
		return str;
	}
	
	private static void checkPresenceOfForbiddenSyntax(String str)
	{
		for (int i=0; i<listOfForbiddenSyntax.length; i++)
		{
			if (str.contains(listOfForbiddenSyntax[i]))
			{
				int newValue = forbiddenSyntaxMap.get(listOfForbiddenSyntax[i])+1;
				forbiddenSyntaxMap.put(listOfForbiddenSyntax[i], newValue);
			}
		}
	}
	
	private static void printForbiddenSyntax()
	{
		for (int i=0; i<listOfForbiddenSyntax.length; i++)
		{
			if (forbiddenSyntaxMap.get(listOfForbiddenSyntax[i])>0)
			{
				System.out.println("WARNING : The forbidden syntax "+listOfForbiddenSyntax[i]+" has been detected "+forbiddenSyntaxMap.get(listOfForbiddenSyntax[i])+" times in the folder content...");
			}
		}
	}
	
	private static void printNumberOfErrorDetected()
	{
		System.out.println("---------------------");
		System.out.println(numberOfErrorsDetected+" links could not be verified.");
	}
	
	private static String getGamaSourceLocalPath()
	{
		return Paths.get("").toAbsolutePath().toString()+File.separator+"..";
	}
	
	private static String formatString(String str)
	{
		return str.replace("%20", " ");
	}
}
