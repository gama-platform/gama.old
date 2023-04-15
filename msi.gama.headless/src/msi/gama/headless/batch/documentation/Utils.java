/*******************************************************************************************************
 *
 * Utils.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.batch.documentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
	public static void getFilesFromFolder(final String folderPath, final List<File> files) {
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
	 * Filter files by extensions.
	 *
	 * @param inputList the input list
	 * @param exts the exts
	 * @return the array list
	 */
	public static ArrayList<File> filterFilesByExtensions(final List<File> inputList, final String... exts) {
		final ArrayList<File> result = new ArrayList<>();
		for (int i = 0; i < inputList.size(); i++) {
			for (final String ext : exts) {
				if (inputList.get(i).getName().endsWith(ext)) {
					result.add(inputList.get(i));
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
				if (!"".equals(concept)) {
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

}
