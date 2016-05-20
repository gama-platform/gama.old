package msi.gama.doc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class ConvertToPDF {
	
	
	public static String getCommandLine() 
			throws ParserConfigurationException, SAXException, IOException{
		FileInputStream in;
		Properties prop2 = new Properties();
		try {
			in = new FileInputStream(Constants.PANDOC_FOLDER+ File.separator + "param.properties");
			prop2.load(in);
		} catch (IOException e) {
    		e.printStackTrace();
		}
		
		TOCManager toc = new TOCManager(Constants.TOC_FILE);
		toc.createPartFiles();
		String files = toc.getTocFilesString();

		File template = new File(Constants.PANDOC_FOLDER+File.separator+"mytemplate.tex");
		File pdfFile = new File(Constants.DOCGAMA_PDF);
		
		String command = Constants.CMD_PANDOC+" --template="+template.getAbsolutePath()+" --latex-engine="+Constants.CMD_PDFLATEX+" --listings --toc";
		command = command + " " + files;
		for(Object s : prop2.keySet()) {
			command = command + " -s " + "--variable " + s + "=" + prop2.getProperty(s.toString());
		}
		command = command + " -o " + pdfFile.getAbsolutePath() ;
		
		System.out.println("Command " + command);
		
		return command;
		
	}
	
	public static void convert(){
		String line;
		try {
			String[] env = { Constants.PATH };
			
			// build file .bat
			File batFile = new File("batFile.bat");
			Files.deleteIfExists(batFile.toPath());
			if (batFile.createNewFile() == false) {
				System.err.println("Impossible to create the batFile...");
				return;
			}
			List<String> lines = Arrays.asList("cd "+Constants.WIKI_FOLDER+" && "+getCommandLine()+"\"" );
			Path file = Paths.get("batFile.bat");
			Files.write(file, lines, Charset.forName("UTF-8"));
			
			// run the bat file
			Process p = Runtime.getRuntime().exec("cmd /c start batFile.bat && exit");

			BufferedReader bri = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			while ((line = bri.readLine()) != null) {
				System.out.println(line);
			}
			bri.close();
			while ((line = bre.readLine()) != null) {
				System.out.println(line);
			}
			bre.close();
			p.waitFor();
			System.out.println("PDF generated.");
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	public static void main(String[] argc) throws ParserConfigurationException, SAXException, IOException{
		
		convert();
	
	}
}
