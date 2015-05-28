/*********************************************************************************************
 * 
 *
 * 'WikiCleaner.java', in plugin 'msi.gama.documentation', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Config;
import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

public class WikiCleaner {

	public static void cleanFolder(String repName){
		cleanFolder(repName,repName);
	}
	public static void cleanFolder(String repName, String destRepName){
		File rep = new File(repName);
		for(File f : rep.listFiles()) {
			if(f.getName().endsWith(".wiki")){
				cleanFile(f.getAbsolutePath(),destRepName + File.separator + f.getName() );
			}
		}
	}
	
	public static void cleanFile(String fileName) {
		String destFileName;		
		
		destFileName = fileName.substring(0, fileName.lastIndexOf("." )); 
		System.out.println("DestFileName: " + destFileName);
		destFileName = destFileName + "-cleaned.wiki";
		System.out.println("DestFileName: " + destFileName);		
		
		cleanFile(fileName, destFileName);
	}
	
	public static void cleanFile(String fileName, String destFileName){
		String line = "";
		BufferedReader fileText;
		BufferedWriter destFileText;		
		
		try {
			fileText = new BufferedReader(new FileReader(new File(fileName)));
			destFileText = new BufferedWriter(new FileWriter(destFileName, false));		
			
			line = fileText.readLine();
			while (line != null) {
				String cleanedLine = cleanLine(line);
				// System.out.println(line);
				// System.out.println("J: "+ cleanedLine);
				if(!"".equals(cleanedLine)){
					destFileText.write(cleanedLine + "\n");
				}
			    line = fileText.readLine();
			} 
			System.out.println(fileName + " has been cleaned ---> " + destFileName + " has been created.");
			
			fileText.close();			
			destFileText.flush();			
			destFileText.close();		
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static String cleanLine(String line){
		// - Get a line from the file
		// - encode all special character from the line --> aim: encoding several whitespaces into &nsbp;
		// - decode the line in order to get parsable characters like < or > but keep the &nsbp; 
		//     (thank to the config.ConvertNonBreakingSpaces) to keep the multiple whitespaces
		// - Set the MaxLineLength of the Renderer
		// - do the job (remove the markups) 
		// -.... 
		Config.ConvertNonBreakingSpaces = false;
		//System.out.println(line);
		line = CharacterReference.encodeWithWhiteSpaceFormatting(line);
		line = CharacterReference.decode(line);
		//System.out.println(line);
	    
		Source htmlSrc = new Source(line);
	    Segment htmlSeg = new Segment(htmlSrc, 0, htmlSrc.length());
	    Renderer htmlRend = new Renderer(htmlSeg);
	    htmlRend.setMaxLineLength(line.length() + 1);
		Config.ConvertNonBreakingSpaces = true;	    
		//System.out.println(htmlRend.toString());

		//return htmlRend.toString();
		return line;
	}
	
	public static void selectWikiFiles() throws JDOMException, IOException{
	 	SAXBuilder builder = new SAXBuilder();      	
       	Document docTOC = (Document) builder.build(Constants.TOC_FILE);	
       	
       	String srcFolder = (Constants.ONLINE) ? Constants.SVN_FOLDER : Constants.WIKI_FOLDER;
       	
       	for(Element e : docTOC.getRootElement().getChildren()){
       		if("chapter".equals(e.getName())){
	       		String fileToClean = srcFolder + File.separator + e.getAttributeValue("file") + ".wiki";
	       		String destFile = Constants.WIKI2WIKI_FOLDER + File.separator + e.getAttributeValue("file") + ".wiki";
	       		cleanFile(fileToClean, destFile);
       		}
       	}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static void main(String[] args) throws JDOMException, IOException {
		// Test Preparation
		// PrepareEnv.prepareDocumentation();
		
		// Test parcours d'un folder
		// cleanFolder(Constants.SVN_FOLDER, Constants.WIKI2WIKI_FOLDER);
		
		// Test cleaner
		//String file = "src/Types15.wiki";
		//cleanFile(file);
		
		selectWikiFiles();
	}
}
