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

}
