/*******************************************************************************************************
 *
 * HomeToTOC.java, in msi.gama.documentation, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.doc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.precompiler.doc.utils.Constants;

/**
 * The Class HomeToTOC.
 */
public class HomeToTOC {

	/** The Constant MD_LEVEL_ERR. */
	public final static int MD_LEVEL_ERR = -1; // no header

	/** The Constant MD_LEVEL0. */
	public final static int MD_LEVEL0 = 0; // empty line

	/** The Constant MD_LEVEL1. */
	public final static int MD_LEVEL1 = 1; // #

	/** The Constant MD_LEVEL2. */
	public final static int MD_LEVEL2 = 2; // ##

	/** The Constant MD_LEVEL3. */
	public final static int MD_LEVEL3 = 3; // 1.

	/** The Constant MD_LEVEL4. */
	public final static int MD_LEVEL4 = 4; // 1.

	/** The Constant XML_ELT_TOC. */
	// XML Elements
	public final static String XML_ELT_TOC = "toc";

	/** The Constant XML_ATTR_TITLE. */
	public final static String XML_ATTR_TITLE = "title";

	/** The Constant XML_ELT_PART. */
	public final static String XML_ELT_PART = "part";

	/** The Constant XML_ELT_SUBPART. */
	public final static String XML_ELT_SUBPART = "subpart";

	/** The Constant XML_ELT_CHAPTER. */
	public final static String XML_ELT_CHAPTER = "chapter";

	/** The Constant XML_ATTR_NAME. */
	public final static String XML_ATTR_NAME = "name";

	/** The Constant XML_ATTR_FILE. */
	public final static String XML_ATTR_FILE = "file";

	/** The Constant TITLE_MESSAGE. */
	// Toc file name
	public final static String TITLE_MESSAGE = "Full Documentation of GAMA " + Constants.GAMA_VERSION;

	/** The Constant DEFAULT_FILE. */
	public final static String DEFAULT_FILE = "G__BlankPage";

	/** The Constant MD_EXTENSION. */
	public final static String MD_EXTENSION = ".md";

	/**
	 * Md 2 toc.
	 *
	 * @param tocMDFile
	 *            the toc MD file
	 * @param savePath
	 *            the save path
	 * @param wikiFolder
	 *            the wiki folder
	 */
	public static void md2toc(final String tocMDFile, final String savePath, final String wikiFolder) {
		String line = "";
		Document doc = createDocument();

		// root element
		Element root = doc.createElement(HomeToTOC.XML_ELT_TOC);
		root.setAttribute(HomeToTOC.XML_ATTR_TITLE, HomeToTOC.TITLE_MESSAGE);

		Element currentPart = null;
		Element currentSubPart = null;

		try (BufferedReader br = new BufferedReader(new FileReader(tocMDFile));) {
			while ((line = br.readLine()) != null) {

				switch (getLineHeader(line)) {
					case HomeToTOC.MD_LEVEL2:
						if (currentPart != null) {
							if (currentSubPart != null) { currentPart.appendChild(currentSubPart); }
							root.appendChild(currentPart);
						}
						// Create a new part
						currentPart = doc.createElement(HomeToTOC.XML_ELT_PART);
						currentPart.setAttribute(HomeToTOC.XML_ATTR_NAME, getLineTitle(line));
						// Create also a new subpart
						currentSubPart = doc.createElement(HomeToTOC.XML_ELT_SUBPART);
						currentSubPart.setAttribute(HomeToTOC.XML_ATTR_NAME, getLineTitle(line));
						currentSubPart.setAttribute(HomeToTOC.XML_ATTR_FILE, getLineFilePath(line, wikiFolder));
						break;
					case HomeToTOC.MD_LEVEL3:
						if (currentPart != null && currentSubPart != null) { currentPart.appendChild(currentSubPart); }
						currentSubPart = doc.createElement(HomeToTOC.XML_ELT_SUBPART);
						currentSubPart.setAttribute(HomeToTOC.XML_ATTR_NAME, getLineTitle(line));
						currentSubPart.setAttribute(HomeToTOC.XML_ATTR_FILE, getLineFilePath(line, wikiFolder));
						break;
					case HomeToTOC.MD_LEVEL4:
						Element chapterElt = doc.createElement(HomeToTOC.XML_ELT_CHAPTER);
						chapterElt.setAttribute(HomeToTOC.XML_ATTR_NAME, getLineTitle(line));
						chapterElt.setAttribute(HomeToTOC.XML_ATTR_FILE, getLineFilePath(line, wikiFolder));
						if (currentSubPart != null) { currentSubPart.appendChild(chapterElt); }
						break;
					default:
						break;
				}
			}

			doc.appendChild(root);
			saveDocument(doc, savePath);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the document.
	 *
	 * @return the document
	 */
	private static Document createDocument() {
		Document doc = null;

		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			doc = documentBuilder.newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}

	/**
	 * Save document.
	 *
	 * @param doc
	 *            the doc
	 * @param xmlFilePath
	 *            the xml file path
	 */
	private static void saveDocument(final Document doc, final String xmlFilePath) {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;

		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}

		DOMSource domSource = new DOMSource(doc);
		StreamResult streamResult = new StreamResult(new File(xmlFilePath));

		try {
			if (transformer != null) { transformer.transform(domSource, streamResult); }
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		System.out.println("Done saving XML File");
	}

	// From a line of the Home.md file (e.g. ## [Home](Home)), computes the header level of the line
	/**
	 * Gets the line header.
	 *
	 * @param line
	 *            the line
	 * @return the line header
	 */
	// or whether it is an empty line.
	private static int getLineHeader(final String line) {
		if (line.startsWith("## ")) return HomeToTOC.MD_LEVEL2;

		Pattern patternEnum = Pattern.compile("^(\\d+.*)");
		Matcher matcher = patternEnum.matcher(line);
		if (matcher.matches()) return HomeToTOC.MD_LEVEL3;

		Pattern patternEnumLevel2 = Pattern.compile("^(\\s\\s\\d+.*|\\t\\d+.*)");
		Matcher matcherLevel2 = patternEnumLevel2.matcher(line);
		if (matcherLevel2.matches()) return HomeToTOC.MD_LEVEL4;

		Pattern patternWhiteSpace = Pattern.compile("[\\s|\\t|\\n]*");
		Matcher matcherSWhite = patternWhiteSpace.matcher(line);
		if (matcherSWhite.matches()) return HomeToTOC.MD_LEVEL0;

		return HomeToTOC.MD_LEVEL_ERR;
	}

	// From any line such as: ## [Introduction](Overview)
	// returns the title, i.e. the string between brackets (Introduction here)
	//
	/**
	 * Gets the line title.
	 *
	 * @param line
	 *            the line
	 * @return the line title
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	// ## [Introduction](Overview) -----> Introduction
	private static String getLineTitle(final String line) throws IOException {
		if (line.contains("[")) {
			int indexBegin = line.indexOf("[");
			int indexEnd = line.lastIndexOf("]");

			return line.substring(indexBegin + 1, indexEnd);
		}
		return line;
	}

	// From any line such as: ## [Introduction](Overview)
	// returns the file name, i.e. the string between parentheses (Overview here)
	//
	/**
	 * Gets the line file.
	 *
	 * @param line
	 *            the line
	 * @return the line file
	 */
	// ## [Introduction](Overview) -----> Overview
	private static String getLineFile(final String line) {
		if (line.contains("[")) {
			int indexEndTitle = line.lastIndexOf("]");
			String endLine = line.substring(indexEndTitle + 1);

			int indexBegin = endLine.indexOf("(");
			int indexEnd = endLine.lastIndexOf(")");

			return endLine.substring(indexBegin + 1, indexEnd);
		}
		return DEFAULT_FILE;
	}

	// From any line such as: ## [Introduction](Overview)
	// returns the file relative path
	//
	// ## [Inspectors and Monitors](InspectorsAndMonitors) ----->
	/**
	 * Gets the line file path.
	 *
	 * @param line
	 *            the line
	 * @param wikiFolder
	 *            the wiki folder
	 * @return the line file path
	 */
	private static String getLineFilePath(final String line, final String wikiFolder) {
		String lineFile = getLineFile(line);
		return getRelativePathToWiki(lineFile, wikiFolder);
	}

	/**
	 * Gets the relative path to wiki.
	 *
	 * @param fileName
	 *            the file name
	 * @param wikiFolder
	 *            the wiki folder
	 * @return the relative path to wiki
	 */
	private static String getRelativePathToWiki(final String fileName, final String wikiFolder) {
		String resPath = "";

		try (Stream<Path> stream = Files.find(Paths.get(wikiFolder), 5,
				(path, attr) -> (fileName + HomeToTOC.MD_EXTENSION).equals(path.getFileName().toString()))) {
			Optional<Path> val = stream.findFirst();
			String path = val.isPresent() ? val.get().toString() : "";

			// System.out.println(path);

			resPath = path.substring((wikiFolder + File.separator).length(),
					path.length() - HomeToTOC.MD_EXTENSION.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resPath;
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		final String tocMDFile = Constants.TOC_SIDEBAR_FILE;

		HomeToTOC.md2toc(tocMDFile, Constants.TOC_FILE_PATH, Constants.WIKI_FOLDER);

		String s = "InspectorsAndMonitors";

		System.out.println(getRelativePathToWiki(s, Constants.WIKI_FOLDER));

	}

}
