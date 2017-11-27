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

public class Utils {

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

	public static ArrayList<File> filterFilesByExtensions(final List<File> inputList, final String... exts) {
		final ArrayList<File> result = new ArrayList<File>();
		for (int i = 0; i < inputList.size(); i++) {
			for (final String ext : exts)
				if (inputList.get(i).getName().endsWith(ext))
					result.add(inputList.get(i));
		}
		return result;
	}

	public static String getModelName(final File file) throws IOException {
		// returns the name of the model
		String result = "";

		final FileInputStream fis = new FileInputStream(file);
		final BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;

		while ((line = br.readLine()) != null) {
			result = findAndReturnRegex(line, "^model (\\w+)");
			if (result != "") {
				break;
			}
		}
		br.close();

		return result;
	}

	public static ArrayList<String> getExpeNames(final File file) throws IOException {
		// returns the list of experiments
		final ArrayList<String> result = new ArrayList<String>();
		String expeName = "";

		final FileInputStream fis = new FileInputStream(file);
		final BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;

		while ((line = br.readLine()) != null) {
			expeName = Utils.findAndReturnRegex(line, "^experiment (\\w+)");
			if (expeName != "") {
				result.add(expeName);
				expeName = "";
			}
		}
		br.close();

		return result;
	}

	public static ArrayList<String> getConceptKeywords(final File file) throws IOException {
		// returns the list of concept keywords
		final ArrayList<String> result = new ArrayList<String>();
		String concept = "";

		final FileInputStream fis = new FileInputStream(file);
		final BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;

		while ((line = br.readLine()) != null) {
			concept = Utils.findAndReturnRegex(line, "\\[//\\]: # \\(keyword\\|concept_(.*)\\)");
			if (concept != "") {
				result.add(concept);
				concept = "";
			}
		}
		br.close();

		return result;
	}

	public static String findAndReturnRegex(final String line, final String regex) {
		String str = "";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			str = matcher.group(1);
		}
		return str;
	}

	public static boolean IsInList(final String element, final String[] list) {
		boolean result = false;
		for (final String str : list) {
			if (element.equals(str)) {
				result = true;
			}
		}
		return result;
	}

	public static void CreateFolder(final File file) {
		if (!file.mkdir() && !file.exists()) {
			CreateFolder(file.getParentFile());
			file.mkdir();
		}
		return;
	}

	public static String getUrlFromName(String str) {
		String result = "";
		str = str.toLowerCase();
		str = str.replace("-", " ");
		str = str.replace(".", " ");
		str = str.replace(",", " ");
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
