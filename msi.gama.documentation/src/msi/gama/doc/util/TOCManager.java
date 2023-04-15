/*******************************************************************************************************
 *
 * TOCManager.java, in msi.gama.documentation, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import msi.gama.precompiler.doc.utils.Constants;
import msi.gama.precompiler.doc.utils.XMLUtils;

/**
 * The Class TOCManager.
 */
public class TOCManager {

	/** The toc file. */
	String tocFile;

	/**
	 * Instantiates a new TOC manager.
	 *
	 * @param toc the toc
	 */
	public TOCManager(final String toc) {
		tocFile = toc;
	}

	/**
	 * Creates the part files.
	 *
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void createPartFiles() throws ParserConfigurationException, SAXException, IOException {
		final Document doc = XMLUtils.createDoc(tocFile);
		final NodeList nl = doc.getElementsByTagName("part");

		for (int i = 0; i < nl.getLength(); i++) {
			final String partName = ((Element) nl.item(i)).getAttribute("name");
			final File partFile =
					new File(Constants.TOC_GEN_FOLDER + File.separator + partName.replaceAll(" ", "_") + ".md");

			try (final FileWriter fw = new FileWriter(partFile);
					final BufferedWriter partBw = new BufferedWriter(fw);) {

				partBw.newLine();
				partBw.write("\\part{" + partName + "}");
				partBw.newLine();
			}
		}
	}

	/**
	 * Creates the subpart files.
	 *
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void createSubpartFiles() throws ParserConfigurationException, SAXException, IOException {
		final Document doc = XMLUtils.createDoc(tocFile);
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
						partBw.write("\\newpage\n");
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

	/**
	 * Gets the toc files list.
	 *
	 * @return the toc files list
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<String> getTocFilesList() throws ParserConfigurationException, SAXException, IOException {
		final List<String> lFile = new ArrayList<>();
		final Document doc = XMLUtils.createDoc(tocFile);

		final NodeList nlPart = doc.getElementsByTagName("part");
		for (int i = 0; i < nlPart.getLength(); i++) {
			Element eltPart = (Element) nlPart.item(i);
			File fPart = new File(Constants.TOC_GEN_FOLDER + File.separator
					+ eltPart.getAttribute("name").replaceAll(" ", "_") + ".md");
			lFile.add(fPart.getAbsolutePath());

			final NodeList nlSubpart = eltPart.getElementsByTagName("subpart");
			for (int j = 0; j < nlSubpart.getLength(); j++) {
				eltPart = (Element) nlSubpart.item(j);
				//fPart = new File(Constants.TOC_GEN_FOLDER + File.separator
				//		+ eltPart.getAttribute("name").replaceAll(" ", "_") + ".md");
				//
				fPart = new File(Constants.WIKI_FOLDER + File.separator
						+ eltPart.getAttribute("file") + ".md");
				//
				lFile.add(fPart.getAbsolutePath());
				final NodeList chapterList = eltPart.getElementsByTagName("chapter");
				for (int k = 0; k < chapterList.getLength(); k++) {
					final File f = new File(Constants.WIKI_FOLDER + File.separator
							+ ((Element) chapterList.item(k)).getAttribute("file") + ".md");
					lFile.add(f.getAbsolutePath());
				}
			}
		}

		return lFile;
	}

	/**
	 * Gets the toc files string.
	 *
	 * @return the toc files string
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String getTocFilesString() throws ParserConfigurationException, SAXException, IOException {
		final List<String> lf = getTocFilesList();
		final File blankPage = new File(Constants.MD_BLANK_PAGE);
		String files = "";

		// the files have to be in relative, otherwise it does not work (for some obscure reason...)
		for (final String f : lf) {
			files = files + getRelativePathFromWiki(f) + " " + blankPage + " ";
		}
		return files;
	}

	/**
	 * Gets the relative path from wiki.
	 *
	 * @param targetPath the target path
	 * @return the relative path from wiki
	 */
	public static String getRelativePathFromWiki(final String targetPath/* , String basePath, String pathSeparator */) {

		final File tmp = new File(Constants.WIKI_FOLDER);
		final String basePath = tmp.getAbsolutePath();
		final String pathSeparator = "/";

		// Normalize the paths
		String normalizedTargetPath = FilenameUtils.normalizeNoEndSeparator(targetPath);
		String normalizedBasePath = FilenameUtils.normalizeNoEndSeparator(basePath);

		// Undo the changes to the separators made by normalization
		if (pathSeparator.equals("/")) {
			normalizedTargetPath = FilenameUtils.separatorsToUnix(normalizedTargetPath);
			normalizedBasePath = FilenameUtils.separatorsToUnix(normalizedBasePath);

		} else if (pathSeparator.equals("\\")) {
			normalizedTargetPath = FilenameUtils.separatorsToWindows(normalizedTargetPath);
			normalizedBasePath = FilenameUtils.separatorsToWindows(normalizedBasePath);

		} else {
			throw new IllegalArgumentException("Unrecognised dir separator '" + pathSeparator + "'");
		}

		final String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
		final String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));

		// First get all the common elements. Store them as a string,
		// and also count how many of them there are.
		final StringBuffer common = new StringBuffer();

		int commonIndex = 0;
		while (commonIndex < target.length && commonIndex < base.length
				&& target[commonIndex].equals(base[commonIndex])) {
			common.append(target[commonIndex] + pathSeparator);
			commonIndex++;
		}

		if (commonIndex == 0) {
			// No single common path element. This most
			// likely indicates differing drive letters, like C: and D:.
			// These paths cannot be relativized.
			throw new PathResolutionException(
					"No common path element found for '" + normalizedTargetPath + "' and '" + normalizedBasePath + "'");
		}

		// The number of directories we have to backtrack depends on whether the base is a file or a dir
		// For example, the relative path from
		//
		// /foo/bar/baz/gg/ff to /foo/bar/baz
		//
		// ".." if ff is a file
		// "../.." if ff is a directory
		//
		// The following is a heuristic to figure out if the base refers to a file or dir. It's not perfect, because
		// the resource referred to by this path may not actually exist, but it's the best I can do
		boolean baseIsFile = true;

		final File baseResource = new File(normalizedBasePath);

		if (baseResource.exists()) {
			baseIsFile = baseResource.isFile();

		} else if (basePath.endsWith(pathSeparator)) {
			baseIsFile = false;
		}

		final StringBuffer relative = new StringBuffer();

		if (base.length != commonIndex) {
			final int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;

			for (int i = 0; i < numDirsUp; i++) {
				relative.append(".." + pathSeparator);
			}
		}
		relative.append(normalizedTargetPath.substring(common.length()));
		return relative.toString();
	}

	/**
	 * The Class PathResolutionException.
	 */
	static class PathResolutionException extends RuntimeException {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new path resolution exception.
		 *
		 * @param msg the msg
		 */
		PathResolutionException(final String msg) {
			super(msg);
		}
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(final String[] args) throws ParserConfigurationException, SAXException, IOException {
		final TOCManager t = new TOCManager(Constants.TOC_FILE);
		System.out.println(t.getTocFilesString());

		// t.createPartFiles();
		System.out.println(t.getTocFilesString());
	}

}
