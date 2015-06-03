package msi.gama.doc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConvertToPDF {
	
	
	public static String getCommandLine() 
			throws ParserConfigurationException, SAXException, IOException{
		FileInputStream in;
		Properties prop2 = new Properties();
		try {
			in = new FileInputStream(Constants.PANDOC_FOLDER+ File.separator + "param.properties");
			prop2.load(in);
			System.out.println("Properties file loaded");
		} catch (IOException e) {
    		e.printStackTrace();
		}
		
		TOCManager toc = new TOCManager(Constants.TOC_FILE);
		String files = toc.getTocFilesString();
		
		for(Object s : prop2.keySet()) {
			System.out.println((s.toString()));			
			System.out.println(prop2.getProperty(s.toString()));			
		}

//		pandoc -N --template=mytemplate.tex --variable mainfont=Georgia --variable sansfont=Arial 
//		--variable fontsize=12pt --variable version=1.7 --variable toc-depth=1 G__Operators.md G__KeyConcepts.md 
//				--variable documentclass=book --latex-engine=xelatex --toc -o example14.pdf

		String command = "/usr/local/bin/pandoc --template="+Constants.PANDOC_FOLDER+File.separator+"mytemplate.tex --latex-engine=/usr/texbin/xelatex --toc";
		command = command + " " + files;
		for(Object s : prop2.keySet()) {
			command = command + " " + "--variable " + s + "=" + prop2.getProperty(s.toString());
		}
		command = command + " -o " + Constants.PDF_FOLDER + File.separator + "docGAMAv17.pdf";
		
		System.out.println("Command " + command);
		
		return command;
		
	}
	
	public static void convert(){
	      String line;
	      try {
	      // Process p = Runtime.getRuntime().exec("/usr/local/bin/pandoc --help");
	      Process p = Runtime.getRuntime().exec(getCommandLine());
		  //    Process p = Runtime.getRuntime().exec("ls ../../gama.wiki");

	      BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
	      BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	      while ((line = bri.readLine()) != null) {
	        System.out.println(line);
	      }
	      bri.close();
	      while ((line = bre.readLine()) != null) {
	        System.out.println(line);
	      }
	      bre.close();
	      p.waitFor();
	      System.out.println("Done.");
	    }
	    catch (Exception err) {
	      err.printStackTrace();
	    }		
	}
	
	public static void main(String[] argc) throws ParserConfigurationException, SAXException, IOException{
		
		convert();
		
		System.out.println("Done");
		
//	    try {
//	    	
//	    		FileInputStream in;
//	    		Properties prop2 = new Properties();
//	    		try {
//	    		in = new FileInputStream("param.properties");
//	    		prop2.load(in);
//	    		in.close();
//	    		} catch (FileNotFoundException e) {
//	    		e.printStackTrace();
//	    		} catch (IOException e) {
//	    		e.printStackTrace();
//	    		}
//	    		// Extraction des propriétés
//	    		String url = prop2.getProperty("NB_LOUPS").toString();
//	    		String user = prop2.getProperty("NB_MOUTONS");
//	    		System.out.println(url + " eb " + user);
//	    	
//	    	
//		      String line;
//		      Process p = Runtime.getRuntime().exec("/usr/local/bin/pandoc --help");
//		      BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
//		      BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//		      while ((line = bri.readLine()) != null) {
//		        System.out.println(line);
//		      }
//		      bri.close();
//		      while ((line = bre.readLine()) != null) {
//		        System.out.println(line);
//		      }
//		      bre.close();
//		      p.waitFor();
//		      System.out.println("Done.");
//		    }
//		    catch (Exception err) {
//		      err.printStackTrace();
//		    }		
	}
}
