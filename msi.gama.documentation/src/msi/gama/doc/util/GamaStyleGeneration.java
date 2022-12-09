/*******************************************************************************************************
 *
 * GamaStyleGeneration.java, in msi.gama.documentation, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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

/**
 * The Class GamaStyleGeneration.
 */
public class GamaStyleGeneration {

	/** The keyword file. */
	public static String KEYWORD_FILE = Constants.WIKI_FOLDER + "/keywords.xml";
	
	/** The gama style file. */
	public static String GAMA_STYLE_FILE = "files/input/pandocPDF/gama_style.sty";
	
	/** The list category. */
	// BEWARE !! The order of the list_category is important !! The first one will be prioritary
	public static String[] LIST_CATEGORY = { "statement", "type", "operator", "facet", "literal" };
	
	/** The list forbidden char. */
	public static String[] LIST_FORBIDDEN_CHAR = { "-", ":", "!", "?", "/", ".", "^", "@", "*", "+", "<", ">", "=" };
	
	/** The list undetected statement. */
	public static String[] LIST_UNDETECTED_STATEMENT = { "species", "global", "grid", "model", "import", "output" };
	
	/** The list literal. */
	public static String[] LIST_LITERAL = { "true", "false", "unknown", "nil" };

	/** The keyword map. */
	private static Map<String, ArrayList<String>> keywordMap = new HashMap<>();

	/**
	 * Generate gama style.
	 *
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Contains forbidden char.
	 *
	 * @param keyword the keyword
	 * @return true, if successful
	 */
	public static boolean containsForbiddenChar(final String keyword) {
		boolean result = false;
		for (final String str : LIST_FORBIDDEN_CHAR) {
			if (keyword.contains(str)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Creates the subpart files.
	 *
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void createSubpartFiles() throws ParserConfigurationException, SAXException, IOException {
		final Document doc = XMLUtils.createDoc("oj");
		final NodeList nl = doc.getElementsByTagName("subpart");

		for (int i = 0; i < nl.getLength(); i++) {
			final String subpartName = ((Element) nl.item(i)).getAttribute("name");
			final File subpartFile =
					new File(Constants.TOC_GEN_FOLDER + File.separator + subpartName.replaceAll(" ", "_") + ".md");

			// copy the content of the wiki file in the new file.
			final String wikiPagePath =
					Constants.WIKI_FOLDER + File.separatorChar + ((Element) nl.item(i)).getAttribute("file") + ".md";
			final File wikiFile = new File(wikiPagePath);

			try (BufferedReader br = new BufferedReader(new FileReader(wikiFile));
					FileWriter fw = new FileWriter(subpartFile);
					BufferedWriter partBw = new BufferedWriter(fw);) {

				String line = null;
				boolean titleWritten = false;
				while ((line = br.readLine()) != null) {
					// change the title of the page (# Title) to the correct latex title
					if (line.startsWith("#") && !titleWritten) {
						// write latex content to make the content bigger.
						partBw.write("\\begingroup\n");
						partBw.write("\\fontsize{28}{34}\\selectfont\n");
						partBw.write("\\textbf{" + subpartName + "}\n");
						partBw.write("\\endgroup\n");
						partBw.write("\\vspace{20mm}\n");
						titleWritten = true;
					} else {
						partBw.write(line);
						partBw.newLine();
					}
				}

			}
		}
	}
}
