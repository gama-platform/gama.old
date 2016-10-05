package msi.gama.doc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import msi.gama.doc.websiteGen.utilClasses.ConceptManager;
import msi.gama.doc.websiteGen.utilClasses.ConceptManager.WebsitePart;
import msi.gama.doc.websiteGen.utilClasses.Utils;

public class CheckConcepts {
	// this class will check if all the concepts present in the documentations
	// are conform. It will then build a report about repartition of concept
	// keywords.

	public static String PATH_TO_MODEL_LIBRARY = Constants.WIKI_FOLDER + File.separator + "References" + File.separator
			+ "ModelLibrary";
	public static String PATH_TO_GAML_REFERENCES = Constants.WIKI_FOLDER + File.separator + "References"
			+ File.separator + "GAMLReferences";
	public static String PATH_TO_DOCUMENTATION = Constants.WIKI_FOLDER + File.separator + "Tutorials";

	public static String PATH_TO_MD_REPORT = Constants.WIKI_FOLDER + File.separator + "WikiOnly" + File.separator
			+ "DevelopingExtensions" + File.separator + "WebsiteGeneration.md";

	public static void DoCheckConcepts() throws IOException, IllegalArgumentException, IllegalAccessException {
		// get all the concepts.
		ConceptManager.loadConcepts();

		// browse all the files of the model library.
		executeForAWebsitePart(PATH_TO_MODEL_LIBRARY, WebsitePart.MODEL_LIBRARY.toString());

		// browse all the files of the documentation.
		executeForAWebsitePart(PATH_TO_DOCUMENTATION, WebsitePart.DOCUMENTATION.toString());

		// browse keywords.xml to find which concepts is linked with which
		// keywords.
		browseKeywords(Constants.PATH_TO_KEYWORDS_XML);

		// print statistics
		ConceptManager.printStatistics();

		// write report in websiteGeneration file
		writeReport(PATH_TO_MD_REPORT);
	}

	private static void executeForAWebsitePart(final String path, final String websitePart) {
		final ArrayList<File> listFiles = new ArrayList<File>();
		Utils.getFilesFromFolder(path, listFiles);
		final ArrayList<File> gamlFiles = Utils.filterFilesByExtension(listFiles, "md");

		ArrayList<String> listConcept = new ArrayList<String>();

		for (final File file : gamlFiles) {
			try {
				listConcept = Utils.getConceptKeywords(file);
			} catch (final IOException e) {
				e.printStackTrace();
			}
			for (final String concept : listConcept) {
				if (!ConceptManager.conceptIsPossibleToAdd(concept)) {
					System.out.println("WARNING : The concept " + concept + " is not a predefined concept !!");
				} else
					ConceptManager.addOccurrenceOfConcept(concept, websitePart);
			}
		}
	}

	private static void browseKeywords(final String path) {
		try {
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(path);

			doc.getDocumentElement().normalize();

			final NodeList nList = doc.getElementsByTagName("keyword");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				final Node nNode = nList.item(temp);
				final Element eElement = (Element) nNode;
				final String category = eElement.getElementsByTagName("category").item(0).getTextContent();
				final String conceptName = eElement.getElementsByTagName("name").item(0).getTextContent();
				if (category.equals("concept")) {
					if (ConceptManager.conceptIsPossibleToAdd(conceptName)) {
						for (int i = 0; i < eElement.getElementsByTagName("associatedKeyword").getLength(); i++) {
							ConceptManager.addOccurrenceOfConcept(conceptName, WebsitePart.GAML_REFERENCES.toString());
						}
					}
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeReport(final String file) throws IOException {
		String result = "";

		// read the file
		final FileInputStream fis = new FileInputStream(file);
		final BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;

		while ((line = br.readLine()) != null) {
			if (line.contains("__________________________________")) {
				result += line + "\n";
				break;
			}
			result += line + "\n";
		}
		br.close();
		result += "\n\n";

		// add the statistics
		result += ConceptManager.getExtendedStatistics();

		// write the file
		final File outputFile = new File(file);
		final FileOutputStream fileOut = new FileOutputStream(outputFile);
		fileOut.write(result.getBytes());
		fileOut.close();
	}
}
