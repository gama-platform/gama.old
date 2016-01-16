package msi.gama.headless.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class ConsoleReader {
	public static String END_OF_FILE="</Experiment_plan>";
	public static InputStream readOnConsole()
	{
		String entry = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String pp = new File(".").getAbsolutePath();
		System.out.println("************************** CURRENT PATH **********************************\n"+pp.substring(0, pp.length()-1)+"\n************************************************************\n");
	
		do
		{
			try {
				entry = entry + br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} while(!entry.contains(END_OF_FILE));
		
		
		return new ByteArrayInputStream(entry.getBytes());
		
	}
	
}
