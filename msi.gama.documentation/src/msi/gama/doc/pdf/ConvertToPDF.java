package msi.gama.doc.pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import msi.gama.precompiler.doc.utils.Constants;
import msi.gama.precompiler.doc.utils.OSUtils;
import ummisco.gama.dev.utils.DEBUG;

public class ConvertToPDF {

	public static String getCommandLine() throws IOException {
		
		final Properties prop2 = new Properties();
		
		try (FileInputStream in = new FileInputStream(Constants.PANDOC_FOLDER + File.separator + "param.properties") ){
			prop2.load(in);
		} catch (final IOException e) {
			DEBUG.ERR("Cannot find the Pandoc properties file.",e);		
		}

		final TOCManager toc = new TOCManager(Constants.TOC_SIDEBAR_FILE, Constants.WIKI_FOLDER);
		final String files = toc.getTocFilesString();

		final File template = new File(Constants.PANDOC_FOLDER + File.separator + "mytemplate.tex");
		final File pdfFile = new File(Constants.DOCGAMA_PDF);

		String command = Constants.CMD_PANDOC + " --template=" + template.getAbsolutePath() + " --pdf-engine="
				+ Constants.CMD_PDFLATEX + " --listings --toc";
		command = command + " " + files;
		for (final Object s : prop2.keySet()) {
			command = command + " " + "--variable " + s + "=" + prop2.getProperty(s.toString());
		}
		command = command + " -o " + pdfFile.getAbsolutePath();

		DEBUG.LOG("Command " + command);

		return command;
	}	
	
	public static void convertMacOs() {
		DEBUG.LOG("Start of convert for MacOS");
		String line;
		try {
			final String[] env = { Constants.PATH };

			final Process p = Runtime.getRuntime().exec(getCommandLine(), env, new File(Constants.WIKI_FOLDER));

			try (BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
					BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));) {
				while ((line = bri.readLine()) != null) {
					DEBUG.LOG(line);
				}
				while ((line = bre.readLine()) != null) {
					DEBUG.LOG(line);
				}
				p.waitFor();
				DEBUG.LOG("PDF generated.");
			}
		} catch (final Exception err) {
			DEBUG.ERR("Error in executing the command line.",err);		
		}
	}

	public static void convertWindows() {
		DEBUG.LOG("Start of convert for Windows");

		String line;
		try {
			// build file .bat
			final File batFile = new File("batFile.bat");
			Files.deleteIfExists(batFile.toPath());
			if ( ! batFile.createNewFile()) {
				DEBUG.ERR("Impossible to create the batFile...");
				return;
			}
			final List<String> lines = Arrays.asList("cd " + Constants.WIKI_FOLDER + " && " + getCommandLine());
			final Path file = Paths.get("batFile.bat");
			Files.write(file, lines, StandardCharsets.UTF_8);

			// run the bat file
			final Process p = Runtime.getRuntime().exec("cmd /c start batFile.bat && exit");

			try (final BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
					final BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));) {
				while ((line = bri.readLine()) != null) {
					DEBUG.LOG(line);
				}
				while ((line = bre.readLine()) != null) {
					DEBUG.LOG(line);
				}
				p.waitFor();
				DEBUG.LOG("PDF generated.");
			}
		} catch (final Exception err) {
			DEBUG.ERR("Error in excecuting the command line under Windows.",err);		
		}
	}

	public static void convert() {
		if (OSUtils.isWindows()) {
			convertWindows();
		} else if (OSUtils.isMacOS()) {
			convertMacOs();
		} else {
			throw new RuntimeException("This OS is not managed yet.");
		}

	}

	public static void main(final String[] argc){

 		convert();

	}
}
