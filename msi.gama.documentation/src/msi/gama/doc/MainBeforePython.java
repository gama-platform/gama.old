/*********************************************************************************************
 * 
 *
 * 'MainBeforePython.java', in plugin 'msi.gama.documentation', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;

import msi.gama.doc.util.Constants;
import msi.gama.doc.util.PrepareEnv;
import msi.gama.doc.util.WikiCleaner;

public class MainBeforePython {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("GENERATION OF THE DOCUMENTATION - STEP 1/3");
	
		try {
			System.out.print("Preparation of the folders.......................");
			PrepareEnv.prepareDocumentation(Constants.ONLINE);
			System.out.println("DONE");
			
			if(Constants.ONLINE){
				System.out.print("Checkout Wiki Files from GAMA SVN................PLEASE WAIT");	
				// Ben: remove SVN, get from GIT
				// SVNUtils.checkoutSVNGamaDoc();	
				System.out.println("Checkout Wiki Files from GAMA SVN................DONE");	
			} else {
				System.out.println("NO CHECKOUT DONE  ... then will copy all files if availables from the WIKI folder");
			}
			System.out.print("Select and clean some wiki files.................");
			WikiCleaner.selectWikiFiles();
			System.out.println("DONE");			
		} catch(Exception e){
			System.out.println("ERROR: Impossible connection to the SVN repository.");
			System.out.println(e);
		}	
		
		System.out.println("");
		System.out.println("This is the end of the step 1. ");
		System.out.println("Please run the python file 'statwiki.py' in the python folder with arguments: --build --d=../../files/gen/wiki2wiki ");

		// System.exec"--build --d=../../files/gen/wiki2wiki"
		
	}
	
	public static void launchCommandLineGama(String path, String inpFile, String outDir) {
		//outDir = outDir + File.separator + getDirName(inpFile);
		System.out.println("inpFile " + inpFile);
		System.out.println("newOutDir " + outDir);
		String os = System.getProperty("os.name");

		List<String> commands;
		if ( os.startsWith("Windows") ) {
			commands =
				new ArrayList<String>((Arrays.asList("cmd.exe", "/C", "start gamaHeadless.bat " +
					inpFile + " " + outDir)));
		} else {

			commands =
				new ArrayList<String>((Arrays.asList("sh", "gamaHeadless.sh", inpFile, outDir)));
		}

		Process process = null;
		
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.directory(new File(path));
			pb.command(commands);
			process = pb.start();
			
			InputStream is = process.getInputStream();
			InputStream err = process.getErrorStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String line;
			System.out.printf("Output of running %s is:\n", Arrays.toString(commands.toArray()));
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}

			InputStreamReader isrerr = new InputStreamReader(err);
			BufferedReader brerr = new BufferedReader(isrerr);

			System.out.printf("Error of running %s is:\n", Arrays.toString(commands.toArray()));
			while ((line = brerr.readLine()) != null) {
				System.out.println(line);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			  try {
				  process.waitFor();
				  process.getInputStream().close();
				  process.getOutputStream().close();
				  process.getErrorStream().close(); 
				  process.destroy();

			  } catch (Exception ioe) {
				  ioe.printStackTrace();
			  }
		}
	}
}
