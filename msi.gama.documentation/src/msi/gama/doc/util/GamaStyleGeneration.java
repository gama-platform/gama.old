package msi.gama.doc.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import msi.gama.precompiler.doc.utils.Constants;
import msi.gama.precompiler.doc.utils.XMLUtils;

public class GamaStyleGeneration {

	public static String KEYWORD_FILE = Constants.WIKI_FOLDER + "/keywords.xml";
	public static String GAMA_STYLE_FILE = "files/input/pandocPDF/gama_style.sty";
	// BEWARE !! The order of the list_category is important !! The first one will be prioritary
	public static String[] LIST_CATEGORY = { "statement", "type", "facet", "operator", "literal" };
	public static String[] LIST_FORBIDDEN_CHAR = { "-", ":", "!", "?", "/", ".", "^", "@", "*", "+", "<", ">", "=" };
	public static String[] LIST_UNDETECTED_STATEMENT = { "species", "global", "grid", "model", "import", "output" };
	public static String[] LIST_LITERAL = { "true", "false", "unknown", "nil" };

	private static Map<String, ArrayList<String>> keywordMap = new HashMap<>();

	public static void generateGamaStyle() throws ParserConfigurationException, SAXException, IOException {
		final File keywordFile = new File(KEYWORD_FILE);
		if (!keywordFile.exists()) {
			System.out.println("WARNING : Impossible to find the file " + keywordFile.getAbsolutePath()
					+ ". Please generate it from the Processor before running it !");
			return;
		}

		// store all the words in map
		final Document doc = XMLUtils.createDoc(keywordFile);
		final NodeList nl = doc.getElementsByTagName("keyword");
		for (int i = 0; i < nl.getLength(); i++) {
			final String category = ((Element) nl.item(i)).getElementsByTagName("category").item(0).getTextContent();
			final String name = ((Element) nl.item(i)).getElementsByTagName("name").item(0).getTextContent();
			if (!keywordMap.containsKey(category)) {
				final ArrayList<String> elemToAdd = new ArrayList<>();
				elemToAdd.add(name);
				keywordMap.put(category, elemToAdd);
			} else {
				final ArrayList<String> elemToAdd = keywordMap.get(category);
				elemToAdd.add(name);
				keywordMap.put(category, elemToAdd);
			}
		}

		// read and write the file gama_style

		// copy the gama_style.sty
		final File gamaStyleFile = new File(GAMA_STYLE_FILE);
		final File gamaStyleFileCopy = new File("tempFile.md");
		Files.deleteIfExists(gamaStyleFileCopy.toPath());
		gamaStyleFileCopy.createNewFile();
		FileUtils.copyFile(gamaStyleFile, gamaStyleFileCopy);

		// read the temporary file line after line
		try (final BufferedReader in = new BufferedReader(new FileReader(gamaStyleFileCopy));
				final FileWriter fw = new FileWriter(gamaStyleFile);
				final BufferedWriter out = new BufferedWriter(fw);) {

			String line = null;
			boolean automaticGeneratedPart = false;
			while ((line = in.readLine()) != null) {
				// change the title of the page (# Title) to the correct latex title
				if (line.contains("% end of the automatically generated part")) {
					automaticGeneratedPart = false;
				}
				if (line.contains("% this part is automatically generated")) {
					automaticGeneratedPart = true;
					// generate automatically the text from the map
					// write the first line
					out.write(line);
					out.newLine();
					// write all the categories
					for (int i = 0; i < LIST_CATEGORY.length; i++) {
						ArrayList<String> listKeywords = keywordMap.get(LIST_CATEGORY[i]);
						if (listKeywords == null) {
							listKeywords = new ArrayList<>();
						}
						out.write("% list of " + LIST_CATEGORY[i] + "\n");
						if (i == 0) {
							out.write("morekeywords={\n");
						} else {
							out.write("morekeywords=[" + (i + 1) + "]{\n");
						}
						boolean firstWordWritten = false;
						if (LIST_CATEGORY[i] == "statement") {
							for (final String undetectStatement : LIST_UNDETECTED_STATEMENT) {
								if (firstWordWritten) {
									out.write(", ");
								}
								firstWordWritten = true;
								out.write(undetectStatement);
							}
						}
						if (LIST_CATEGORY[i] == "literal") {
							for (final String literal : LIST_LITERAL) {
								if (firstWordWritten) {
									out.write(", ");
								}
								firstWordWritten = true;
								out.write(literal);
							}
						}
						for (final String keyword : listKeywords) {
							if (!containsForbiddenChar(keyword)) {
								if (firstWordWritten) {
									out.write(", ");
								}
								firstWordWritten = true;
								out.write(keyword);
							}
						}
						out.newLine();
						out.write("},\n");
					}
				}
				if (!automaticGeneratedPart) {
					out.write(line);
					out.newLine();
				}
			}

		}

		// delete the temporary file
		Files.deleteIfExists(gamaStyleFileCopy.toPath());
	}

	public static boolean containsForbiddenChar(final String keyword) {
		boolean result = false;
		for (final String str : LIST_FORBIDDEN_CHAR) {
			if (keyword.contains(str)) {
				result = true;
			}
		}
		return result;
	}

}
