/*********************************************************************************************
 * 
 *
 * 'MulticoreTask.java', in plugin 'msi.gama.hpc', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.hpc.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MulticoreTask implements Runnable {
	
	private String path;
	private String inpFile;
	private String outDir;
	
	private MulticoreRuntime mr;
	
	public MulticoreTask(MulticoreRuntime mr, String path, String inpFile, String outDir) {
		this.path = path;
		this.inpFile = inpFile;
		this.outDir = outDir;
		this.setMr(mr);
	}

	public void startObservator() {
		mr.start();
	}
	
	public void stopObservator() {
		mr.stop();
	}
	
	public String getName(){
		return inpFile;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		mr.start();
		System.out.println("start at " + Calendar.getInstance().getTimeInMillis());
		launchCommandLineGama(path, inpFile, outDir);
		System.out.println("stop at " + Calendar.getInstance().getTimeInMillis());
		mr.stop();
	}
	
	public void launchCommandLineGama(String path, String inpFile, String outDir) {
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
	
	public MulticoreRuntime getMr() {
		return mr;
	}

	public void setMr(MulticoreRuntime mr) {
		this.mr = mr;
	}

}
