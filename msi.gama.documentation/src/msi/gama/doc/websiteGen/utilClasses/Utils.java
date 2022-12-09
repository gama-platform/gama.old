/*******************************************************************************************************
 *
 * Utils.java, in msi.gama.documentation, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.doc.websiteGen.utilClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class Utils.
 */
public class Utils {

	/**
	 * Gets the files from folder.
	 *
	 * @param folderPath the folder path
	 * @param files the files
	 * @return the files from folder
	 */
	public static void getFilesFromFolder(final String folderPath, final ArrayList<File> files) {
		final File folder = new File(folderPath);
		final File[] fList = folder.listFiles();
		if (fList != null) {
			for (final File file : fList) {
				if (file.isFile()) {
					files.add(file);
				} else if (file.isDirectory()) {
					getFilesFromFolder(file.getAbsolutePath(), files);
				}
			}
		}
	}

	/**
	 * Filter files by extension.
	 *
	 * @param inputList the input list
	 * @param ext the ext
	 * @return the array list
	 */
	public static ArrayList<File> filterFilesByExtension(final ArrayList<File> inputList, final String ext) {
		final ArrayList<File> result = new ArrayList<>();
		for (int i = 0; i < inputList.size(); i++) {
			if (inputList.get(i).getAbsoluteFile().toString().endsWith(ext)) {
				result.add(inputList.get(i));
			}
		}
		return result;
	}

	/**
	 * Gets the model name.
	 *
	 * @param file the file
	 * @return the model name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String getModelName(final File file) throws IOException {
		// returns the name of the model
		String result = "";

		try (FileInputStream fis = new FileInputStream(file);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));) {

			String line = null;

			while ((line = br.readLine()) != null) {
				result = findAndReturnRegex(line, "^model (\\w+)");
				if (result != "") {
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Gets the expe names.
	 *
	 * @param file the file
	 * @return the expe names
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ArrayList<String> getExpeNames(final File file) throws IOException {
		// returns the list of experiments
		final ArrayList<String> result = new ArrayList<>();
		String expeName = "";

		try (final FileInputStream fis = new FileInputStream(file);
				final BufferedReader br = new BufferedReader(new InputStreamReader(fis));) {

			String line = null;

			while ((line = br.readLine()) != null) {
				expeName = Utils.findAndReturnRegex(line, "^experiment (\\w+)");
				if (expeName != "") {
					result.add(expeName);
					expeName = "";
				}
			}
		}

		return result;
	}

	/**
	 * Gets the concept keywords.
	 *
	 * @param file the file
	 * @return the concept keywords
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ArrayList<String> getConceptKeywords(final File file) throws IOException {
		// returns the list of concept keywords
		final ArrayList<String> result = new ArrayList<>();
		String concept = "";

		try (final FileInputStream fis = new FileInputStream(file);
				final BufferedReader br = new BufferedReader(new InputStreamReader(fis));) {

			String line = null;

			while ((line = br.readLine()) != null) {
				concept = Utils.findAndReturnRegex(line, "\\[//\\]: # \\(keyword\\|concept_(.*)\\)");
				if (concept != "") {
					result.add(concept);
					concept = "";
				}
			}
		}

		return result;
	}

	/**
	 * Find and return regex.
	 *
	 * @param line the line
	 * @param regex the regex
	 * @return the string
	 */
	public static String findAndReturnRegex(final String line, final String regex) {
		String str = "";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			str = matcher.group(1);
		}
		return str;
	}

	/**
	 * Checks if is in list.
	 *
	 * @param element the element
	 * @param list the list
	 * @return true, if successful
	 */
	public static boolean IsInList(final String element, final String[] list) {
		boolean result = false;
		for (final String str : list) {
			if (element.equals(str)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Creates the folder.
	 *
	 * @param file the file
	 */
	public static void CreateFolder(final File file) {
		if (!file.mkdir() && !file.exists()) {
			CreateFolder(file.getParentFile());
			file.mkdir();
		}
		return;
	}

	/**
	 * Gets the url from name.
	 *
	 * @param param the param
	 * @return the url from name
	 */
	public static String getUrlFromName(final String param) {
		String result = "";
		final String str = param.toLowerCase().replace("-", " ").replace(".", " ").replace(",", " ");
		final String[] list = str.split(" ");
		if (list.length == 0) {
			result = String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1);
		} else {
			for (final String word : list) {
				if (word.length() > 0) {
					result += String.valueOf(word.charAt(0)).toUpperCase() + word.substring(1);
				}
			}
		}
		return result;
	}
}
