package msi.gama.headless.batch.documentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import msi.gama.headless.common.Globals;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.runtime.Application;

public class ModelLibraryGenerator {
	// inputs / outputs
	static String wikiFolder = "F:/Gama/GamaWiki/";
	static String sourceFolder = "F:/Gama/GamaSource/";
	static String wikiFolderOnOVH = "http://vps226121.ovh.net/gm_wiki/";
	static String[] inputPathToModelLibrary =
			{ sourceFolder + "msi.gama.models/models/", sourceFolder + "ummisco.gaml.extensions.maths/models",
			/* sourceFolder+"msi.gaml.extensions.fipa/models", */ // commented
																	// because
																	// unable to
																	// find
																	// statements
																	// otherwise.
			/* sourceFolder+"simtools.gaml.extensions.physics/models", */ // commented
																			// because
																			// unable
																			// to
																			// find
																			// statements
																			// otherwise.
			/* sourceFolder+"msi.gaml.architecture.simplebdi/models" */ }; // commented
																			// because
																			// unable
																			// to
																			// find
																			// statements
																			// otherwise.
	static String outputPathToModelLibrary = wikiFolder + "References/ModelLibrary";
	static String modelLibraryImagesPath = wikiFolder + "resources/images/modelLibraryScreenshots";
	static String inputFileForHeadlessExecution = wikiFolder + "tempInputForHeadless.xml";
	static String inputModelScreenshot = wikiFolder + "modelScreenshot.xml";
	static String headlessBatPath = wikiFolder + "headless.bat";

	static String[] listNoScreenshot = { "msi.gama.models/models/Syntax", // no
																			// need
																			// to
																			// run
																			// those
																			// models
			"msi.gama.models/models/Features/3D Visualization", // opengl
																// advanced
																// setting ->
																// not available
																// yet on
																// headless
			// "msi.gama.models/models/Features/Co-model Usage", // cause an
			// error of path
			// "msi.gama.models/models/Features/Unit Test", // cause an error
			// "msi.gama.models/models/Features/Database Usage", // cause an
			// error
			// "msi.gama.models/models/Features/Data Importation", // cause an
			// error
			// "msi.gama.models/models/Features/Driving Skill", // cause an
			// error
			// "msi.gama.models/models/Features/Spatial Operators/models/Spatial
			// Operators", // cause an error
			// "msi.gama.models/models/Toy Models/Co-model Example"
	};

	static HashMap<String, ScreenshotStructure> mapModelScreenshot;
	static HashMap<String, String> mainKeywordsMap; // the key is the name of
													// the model, the value is
													// the metadata formated
													// which contains all the
													// important keywords of the
													// model.
	static List<String> expeUsedFromTheXML = new ArrayList<String>(); // this
																		// variable
																		// is
																		// just
																		// here
																		// to
																		// verify
																		// if
																		// the
																		// modelScreenshot.xml
																		// is
																		// well
																		// formed,
																		// and
																		// if
																		// all
	// the experiments have been used.
	static List<Path> imagesCreatedPath = new ArrayList<Path>();

	private static void updatePath() {
		outputPathToModelLibrary = wikiFolder + "/References/ModelLibrary";
		modelLibraryImagesPath = wikiFolder + "/resources/images/modelLibraryScreenshots";
		inputFileForHeadlessExecution = wikiFolder + "/tempInputForHeadless.xml";
		inputModelScreenshot = wikiFolder + "/modelScreenshot.xml";
		headlessBatPath = wikiFolder + "/headless.bat";
	}

	public static void start(final Application headlessApplication, final List<String> args)
			throws IOException, TransformerException {

		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		// parse all the models of the model library, in order to build "input"
		///////////////////////////////////////////////////////////////////////////////////////////////////////// files
		///////////////////////////////////////////////////////////////////////////////////////////////////////// for
		///////////////////////////////////////////////////////////////////////////////////////////////////////// a
		///////////////////////////////////////////////////////////////////////////////////////////////////////// headless
		///////////////////////////////////////////////////////////////////////////////////////////////////////// execution.
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		wikiFolder = args.get(args.size() - 2);
		sourceFolder = args.get(args.size() - 3);
		Globals.OUTPUT_PATH = args.get(args.size() - 1);
		updatePath();
		// get all the gaml files in the model folder
		final ArrayList<File> listFiles = new ArrayList<File>();
		for (final String path : inputPathToModelLibrary) {
			final ArrayList<File> listFilesTmp = new ArrayList<File>();
			Utils.getFilesFromFolder(path, listFilesTmp);
			for (final File f : listFilesTmp) {
				listFiles.add(f);
			}
		}
		final ArrayList<File> gamlFiles = Utils.filterFilesByExtensions(listFiles, "gaml");

		// read modelScreenshot.xml
		System.out.println("----- Start to load the file " + inputModelScreenshot + " -----");
		loadModelScreenshot();
		System.out.println("----> file " + inputModelScreenshot + " loaded properly !");

		for (final File s : gamlFiles) {
			System.out.println("path " + s.getAbsolutePath());
		}

		// create input file if experiment is found
		System.out.println("----- Start to write the input xml for headless -----");
		prepareInputFileForHeadless(gamlFiles, headlessApplication);
		System.out.println("----> file " + inputFileForHeadlessExecution + " written properly !");

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// execute the headless
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		// run the headless
		System.out.println("----- Run the headless -----" + inputFileForHeadlessExecution);
		headlessApplication.runXMLForModelLibrary(inputFileForHeadlessExecution);
		System.out.println("----- Headless executed properly ! -----");

		// delete the "output" directory
		final File outputFile = new File(Globals.OUTPUT_PATH);
		deleteDirectoryAndItsContent(outputFile);

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// read all the metadatas of the model files, and extract only the GAML
		/////////////////////////////////////////////////////////////////////////////////////////////////////// keywords
		/////////////////////////////////////////////////////////////////////////////////////////////////////// "important".
		// Store those data in the map mainKeywordsMap.
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		System.out.println("----- Read all the meta files to generate the map of main keywords for each model -----");
		prepareMainKeywordMap(gamlFiles);
		System.out.println("----> selection of main keywords effectued !");

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// browse a second time all the models, build the md file, including the
		/////////////////////////////////////////////////////////////////////////////////////////////////////// screenshots
		/////////////////////////////////////////////////////////////////////////////////////////////////////// computed
		/////////////////////////////////////////////////////////////////////////////////////////////////////// from
		/////////////////////////////////////////////////////////////////////////////////////////////////////// the
		// headless execution, informations in the header of each model, and
		/////////////////////////////////////////////////////////////////////////////////////////////////////// gaml
		/////////////////////////////////////////////////////////////////////////////////////////////////////// keywords
		/////////////////////////////////////////////////////////////////////////////////////////////////////// read
		/////////////////////////////////////////////////////////////////////////////////////////////////////// from
		/////////////////////////////////////////////////////////////////////////////////////////////////////// mainKeywordsMap.
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		System.out.println("----- Start to write md content -----");
		writeMdContent(gamlFiles);
		System.out.println("----> MD content generated !");

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// print further informations
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		ConceptManager.printStatistics();

	}

	public static void prepareInputFileForHeadless(final ArrayList<File> gamlFiles,
			final Application headlessApplication) {
		// set the output (which will not be used, we just need to specify one.
		// We will destroy it as soon as the headless execution is finish)
		// Globals.OUTPUT_PATH = "/F:/outputHeadless";
		// build the xml and run the headless
		final ArrayList<File> gamlFilesForScreenshot = new ArrayList<File>();
		for (final File gamlFile : gamlFiles) {
			final String gamlFilePath = gamlFile.getAbsoluteFile().toString().replace("\\", "/");
			gamlFilesForScreenshot.add(gamlFile);
			for (final String folderWithoutScreenshot : listNoScreenshot) {
				if (gamlFilePath.contains(sourceFolder + folderWithoutScreenshot)) {
					gamlFilesForScreenshot.remove(gamlFile);
				}
				if (gamlFilePath.split("/")[gamlFilePath.split("/").length - 2].compareTo("include") == 0
						|| gamlFilePath.split("/")[gamlFilePath.split("/").length - 2].compareTo("includes") == 0) {
					gamlFilesForScreenshot.remove(gamlFile);
				}
			}
		}

		try {
			// build the xml
			headlessApplication.buildXMLForModelLibrary(gamlFilesForScreenshot, inputFileForHeadlessExecution);
		} catch (ParserConfigurationException | TransformerException | IOException | GamaHeadlessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static boolean deleteDirectoryAndItsContent(final File file) {

		File[] flist = null;

		if (file == null) { return false; }

		if (file.isFile()) { return file.delete(); }

		if (!file.isDirectory()) { return false; }

		flist = file.listFiles();
		if (flist != null && flist.length > 0) {
			for (final File f : flist) {
				if (!deleteDirectoryAndItsContent(f)) { return false; }
			}
		}

		return file.delete();
	}

	private static void prepareMainKeywordMap(final ArrayList<File> files) throws IOException {
		// read all the metadatas of the model files, and extract only the
		// "important" GAML keywords.
		// Store those data in the map mainKeywordsMap.
		mainKeywordsMap = new HashMap<String, String>();
		final HashMap<String, Integer> occurenceOfKeywords = new HashMap<String, Integer>(); // key
																								// is
																								// gaml
																								// world,
																								// value
																								// is
																								// occurence.
		final ArrayList<String> mostSignificantKeywords = new ArrayList<String>(); // the
																					// list
																					// of
																					// the
																					// less
																					// employed
																					// gaml
																					// keywords.
		final int maxOccurenceNumber = 20; // the maximum number of occurrence
											// for the
											// "mostSignificantKeywordsList".
		final ArrayList<String> modelCategory = getSectionName();

		// store all the keywords in a list
		for (int fileIdx = 0; fileIdx < files.size(); fileIdx++) {
			String absPath = files.get(fileIdx).getAbsolutePath();
			String absPathMeta = "";

			for (final String modCat : modelCategory) {
				absPath = absPath.replace("\\", "/");
				absPathMeta = absPath.replace("models/" + modCat, "models/" + modCat + "/.metadata");
				absPathMeta = absPathMeta + ".meta";

				// we have the meta file.
				final File metaFile = new File(absPathMeta);
				if (metaFile.exists()) {
					final ArrayList<String> gamlWords = getGAMLWords(new File(absPathMeta));
					for (final String gamlWord : gamlWords) {
						if (occurenceOfKeywords.containsKey(gamlWord)) {
							// we increment the number of occurrence of the gaml
							// word
							final int oldVal = occurenceOfKeywords.get(gamlWord);
							occurenceOfKeywords.put(gamlWord, oldVal + 1);
						} else {
							occurenceOfKeywords.put(gamlWord, 1);
						}
					}
				}
			}
		}

		// remove from the list the keywords which are not "important"
		for (final String keyword : occurenceOfKeywords.keySet()) {
			if (occurenceOfKeywords.get(keyword) < maxOccurenceNumber) {
				mostSignificantKeywords.add(keyword);
			}
		}

		// browse a second time all the meta files, and store the most important
		// keyword in the map
		for (int fileIdx = 0; fileIdx < files.size(); fileIdx++) {
			String absPath = files.get(fileIdx).getAbsolutePath();
			String absPathMeta = "";
			for (final String modCat : modelCategory) {
				absPath = absPath.replace("\\", "/");
				absPathMeta = absPath.replace("models/" + modCat + "/", "models/" + modCat + "/.metadata/");
				absPathMeta = absPathMeta + ".meta";

				// we have the meta file.
				final File metaFile = new File(absPathMeta);
				if (metaFile.exists()) {
					final ArrayList<String> gamlWords = getGAMLWords(new File(absPathMeta));
					String metadataKeyword = "";
					for (final String gamlWord : gamlWords) {
						if (mostSignificantKeywords.contains(gamlWord)) {
							metadataKeyword += "[//]: # (keyword|" + gamlWord + ")\n";
						}
					}
					final String modelKey = absPathMeta.replace("/.metadata", "").replace(".meta", "");
					mainKeywordsMap.put(modelKey, metadataKeyword);
				}
			}
		}
	}

	private static ArrayList<String> getGAMLWords(final File file) throws IOException {
		// returns the list of experiments
		final ArrayList<String> result = new ArrayList<String>();
		String extractedStr = "";

		final FileInputStream fis = new FileInputStream(file);
		final BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;

		final String[] categoryKeywords = { "operator", "type", "statement", "skill", "architecture", "constant" };

		while ((line = br.readLine()) != null) {
			for (final String catKeywords : categoryKeywords) {
				extractedStr = Utils.findAndReturnRegex(line, catKeywords + "s=(.*)");
				final String[] keywordArray = extractedStr.split("~");
				for (String kw : keywordArray) {
					if (catKeywords.equals("constant")) {
						kw = "#" + kw;
					}
					kw = catKeywords + "_" + kw;
					result.add(kw);
				}
			}
		}
		br.close();

		return result;
	}

	private static void loadModelScreenshot() {
		// read modelScreenshot.xml, and load it to mapModelScreenshot.
		// the extended name of the experiment is the key, the pair
		// {displayName,cycleNumber} is the value.
		mapModelScreenshot = new HashMap<String, ScreenshotStructure>();
		try {
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(inputModelScreenshot);

			doc.getDocumentElement().normalize();

			final NodeList nList = doc.getElementsByTagName("experiment");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				final Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					final Element eElement = (Element) nNode;

					final String id = eElement.getAttribute("id");
					final ScreenshotStructure screenshot = new ScreenshotStructure(id);
					for (int i = 0; i < eElement.getElementsByTagName("display").getLength(); i++) {
						final String displayName =
								((Element) eElement.getElementsByTagName("display").item(i)).getAttribute("name");
						int cycleNumber = Integer.valueOf(((Element) eElement.getElementsByTagName("display").item(i))
								.getAttribute("cycle_number"));
						if (cycleNumber == 0)
							cycleNumber = 10;
						screenshot.addDisplay(displayName, cycleNumber);
					}
					mapModelScreenshot.put(id, screenshot);
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	// private static void prepareInputFileForHeadless(ArrayList<File> files)
	// throws IOException {
	// // prepare the output file
	// File outputFile = new File(inputFileForHeadlessExecution);
	// FileOutputStream fileOut = new FileOutputStream(outputFile);
	//
	// fileOut.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
	// fileOut.write("<Experiment_plan>\n".getBytes());
	//
	// int simIdx = 0;
	// int outputIdx = 0;
	//
	// // browse all the model files
	// for (int idx = 0 ; idx < files.size(); idx++) {
	// String modelName = "";
	// File modelFile = files.get(idx);
	// ArrayList<String> expeNames = new ArrayList<String>();
	// ArrayList<String> displayNames = new ArrayList<String>();
	//
	// modelName = Utils.getModelName(modelFile);
	// expeNames = Utils.getExpeNames(modelFile);
	//
	// boolean stop = false;
	// for (String str : listNoScreenshot) {
	// if (modelFile.getAbsolutePath().contains(str)) {
	// stop = true;
	// }
	// }
	//
	// if (!stop)
	// {
	// // browse all the experiments
	// for (int expeIdx = 0; expeIdx < expeNames.size(); expeIdx++) {
	// String experiment = expeNames.get(expeIdx);
	//
	// displayNames = getDisplayNamesByExpe(files.get(idx),experiment);
	//
	// String formatedFileName = modelFile.getName().replace(".gaml", "");
	//
	// String expeId = formatedFileName + " " + modelName + " " + experiment;
	//
	// if (mapModelScreenshot.containsKey(expeId)) {
	// expeUsedFromTheXML.add(expeId);
	// if (mapModelScreenshot.get(expeId).checkDisplayName(displayNames)) {
	// fileOut.write(mapModelScreenshot.get(expeId).getXMLContent(Integer.toString(idx*1000+expeIdx),modelFile.getAbsolutePath(),experiment).getBytes());
	// }
	// }
	// else {
	// String buffer = "";
	// boolean writeInFile = false;
	// buffer += " <Simulation id=\""+simIdx+"\"
	// sourcePath=\"/"+modelFile.getAbsoluteFile()+"\" finalStep=\"11\"
	// experiment=\""+experiment+"\">\n";
	// simIdx++;
	// buffer += " <Outputs>\n";
	//
	// // browse all the displays
	// for (int displayIdx = 0 ; displayIdx < displayNames.size() ;
	// displayIdx++) {
	// writeInFile = true; // the simulation contains output, turn the flag to
	// true
	// String display = displayNames.get(displayIdx);
	// String outputPath = modelFile.getAbsolutePath().replace(".gaml", "") +
	// "/" + display;
	// buffer += " <Output id=\""+outputIdx+"\" name=\""+display+"\"
	// output_path=\""+outputPath+"\" framerate=\"10\" />\n";
	// outputIdx++;
	// }
	//
	// buffer += " </Outputs>\n";
	// buffer += " </Simulation>\n";
	//
	// if (writeInFile) { // if the simulations contains outputs
	// fileOut.write(buffer.getBytes());
	// }
	// }
	// }
	// }
	// }
	// fileOut.write("</Experiment_plan>\n".getBytes());
	// fileOut.close();
	//
	// // check if all the experiment id from the modelScreenshot have been
	// used.
	// Iterator<String> it = mapModelScreenshot.keySet().iterator();
	// while (it.hasNext()) {
	// String id = it.next();
	// if (!expeUsedFromTheXML.contains(id)) {
	// System.err.println("The experiment "+id+" has not been used because it
	// does not exist !");
	// }
	// }
	// }

	private static ArrayList<String> getSectionName() {
		final ArrayList<String> result = new ArrayList<String>();
		for (final String path : inputPathToModelLibrary) {
			final File directory = new File(path);
			final String[] sectionNames = directory.list((current, name) -> new File(current, name).isDirectory());
			for (final String sectionName : sectionNames) {
				result.add(sectionName);
			}
		}
		return result;
	}

	private static ArrayList<String> getDisplayNamesByExpe(final File file, final String expeName) throws IOException {
		// returns the list of experiments
		final ArrayList<String> result = new ArrayList<String>();
		String displayName = "";

		boolean inTheRightExperiment = false;

		final FileInputStream fis = new FileInputStream(file);
		final BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;

		while ((line = br.readLine()) != null) {
			if (inTheRightExperiment) {
				if (line.startsWith("experiment") && (line.contains("type: gui") || line.contains("type:gui"))) {
					// if (Utils.findAndReturnRegex(line,"^[\\t,\\s]+experiment
					// (\\w+)") != "") {
					// we are out of the right experiment. Return the result.
					br.close();
					return result;
				}
				displayName = Utils.findAndReturnRegex(line, "^[\\t,\\s]+display (\\w+)");
				if (displayName != "") {
					result.add(displayName);
					displayName = "";
				}
			}
			if (line.startsWith("experiment " + expeName)
					&& (line.contains("type: gui") || line.contains("type:gui"))) {
				// if
				// (expeName.compareTo(Utils.findAndReturnRegex(line,"^[\\t,\\s]+experiment
				// (\\w+)")) == 0) {
				inTheRightExperiment = true;
			}
		}
		br.close();

		return result;
	}

	private static void writeMdContent(final ArrayList<File> gamlFiles) throws IOException {
		// load the concepts
		try {
			ConceptManager.loadConcepts();
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		}

		String sectionName = "";
		String subSectionName = "";

		for (int idx = 0; idx < gamlFiles.size(); idx++) {
			final File gamlFile = gamlFiles.get(idx);
			final String header = extractHeader(gamlFile);

			// extract the header properties
			final MetadataStructure metaStruct = new MetadataStructure(header);

			if (metaStruct.getName() != "") {
				// search if there are some images linked
				final ArrayList<File> listScreenshot = new ArrayList<File>();
				Utils.getFilesFromFolder(
						gamlFile.getAbsolutePath().substring(0, gamlFile.getAbsolutePath().length() - 4),
						listScreenshot);

				// prepare the output file
				String fileName = "";
				fileName = gamlFile.getAbsolutePath().replace("\\", "/");
				boolean isAdditionnalPlugin = false;
				for (final String path : inputPathToModelLibrary) {
					if (fileName.contains(path)) {
						if (!path.equals(inputPathToModelLibrary[0])) {
							isAdditionnalPlugin = true;
						}
						fileName = fileName.split(path)[1];
					}
				}
				final String modelName = metaStruct.getName();
				if (!fileName.contains("include") && !modelName.startsWith("_")) {
					final String newSubSectionName = fileName.split("/")[1];
					String newSectionName = fileName.split("/")[0];
					final String modelFileName =
							newSubSectionName + " " + fileName.split("/")[fileName.split("/").length - 1];
					if (isAdditionnalPlugin) {
						newSectionName = "Additionnal Plugins";
					}
					fileName = newSectionName + "/" + newSubSectionName + "/" + modelFileName;
					fileName = fileName.replace(".gaml", "");
					fileName = fileName.replace("/models", "");

					final ArrayList<String> listPathToScreenshots = new ArrayList<String>();

					for (final File f : listScreenshot) {
						if (f.getName().contains("-0.png")) { // we don't need
																// the
																// screenshot of
																// the step 0.
							f.delete();
						} else {
							final File tmp = new File(modelLibraryImagesPath + "/" + fileName + "/" + f.getName());
							tmp.getParentFile().mkdirs();
							Files.move(f.toPath(), tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
							listPathToScreenshots.add(tmp.toPath().toString());
						}
					}
					if (listScreenshot.size() != 0)
						listScreenshot.get(0).getParentFile().delete(); // delete
																		// the
																		// folder

					// manipulate section and subsection files
					// case of "sub-section" (ex : 3D Visualization, Agent
					// movement...)
					if (!subSectionName.equals(newSubSectionName)) {
						createSubSectionFile(
								outputPathToModelLibrary + "/" + newSectionName + "/" + newSubSectionName + ".md");
					}
					addModel(outputPathToModelLibrary + "/" + newSectionName + "/" + newSubSectionName + ".md",
							modelName, modelFileName.replace(".gaml", ""), listPathToScreenshots);
					// case of "section" (ex : Features, Toy Models...)
					if (!sectionName.equals(newSectionName)) {
						createSectionFile(outputPathToModelLibrary + "/" + newSectionName + ".md");
					}
					if (!subSectionName.equals(newSubSectionName)) {
						addSubSection(outputPathToModelLibrary + "/" + newSectionName + ".md", newSubSectionName);
					}
					subSectionName = newSubSectionName;
					sectionName = newSectionName;

					String outputFileName = outputPathToModelLibrary + "/" + fileName;
					outputFileName = outputFileName + ".md";
					final File outputFile = new File(outputFileName);

					Utils.CreateFolder(outputFile.getParentFile());
					outputFile.createNewFile();
					final FileOutputStream fileOut = new FileOutputStream(outputFile);

					// write the header
					fileOut.write(mainKeywordsMap.get(gamlFile.getAbsolutePath().replace("\\", "/")).getBytes());
					fileOut.write(metaStruct.getMdHeader().getBytes());

					// show the images (if there are some)
					for (String imagePath : listPathToScreenshots) {
						imagePath = imagePath.replace("\\", "/");
						final String urlToImage = imagePath.split(wikiFolder)[1];
						fileOut.write(getHTMLCodeForImage(imagePath).getBytes());
					}

					// write the input (if there are any)
					final List<String> inputFileList = searchInputListRecursive(gamlFile, new ArrayList<String>());
					if (inputFileList.size() > 0) {
						if (inputFileList.size() > 1) {
							fileOut.write(new String("Imported models : \n\n").getBytes());
						} else {
							fileOut.write(new String("Imported model : \n\n").getBytes());
						}
					}
					for (final String inputPath : inputFileList) {
						// write the code of the input files
						fileOut.write(getModelCode(new File(inputPath)).getBytes());
						fileOut.write(new String("\n\n").getBytes());
					}

					// write the code
					fileOut.write(new String("Code of the model : \n\n").getBytes());
					fileOut.write(getModelCode(gamlFile).getBytes());
					fileOut.close();
				}
			} else {
				System.out.println("WARNING : The model contained in the file " + gamlFile.getName()
						+ " has not been created because impossible to read the name or the header.");
			}
		}
	}

	private static void createSectionFile(final String pathToSectionFile) throws IOException {
		final File outputFile = new File(pathToSectionFile);
		Utils.CreateFolder(outputFile.getParentFile());
		outputFile.createNewFile();
		final FileOutputStream fileOut = new FileOutputStream(outputFile);

		final String sectionName =
				pathToSectionFile.split("/")[pathToSectionFile.split("/").length - 1].replace(".md", "");
		fileOut.write(new String("# " + sectionName + "\n\nThis section is composed of the following sub-section :\n\n")
				.getBytes());
		fileOut.close();
	}

	private static void createSubSectionFile(final String pathToSubSectionFile) throws IOException {
		final File outputFile = new File(pathToSubSectionFile);
		Utils.CreateFolder(outputFile.getParentFile());
		outputFile.createNewFile();
		final FileOutputStream fileOut = new FileOutputStream(outputFile);

		final String sectionName =
				pathToSubSectionFile.split("/")[pathToSubSectionFile.split("/").length - 1].replace(".md", "");
		fileOut.write(new String("# " + sectionName + "\n\nThis sub-section is composed of the following models :\n\n")
				.getBytes());
		fileOut.close();
	}

	private static void addSubSection(final String pathToSectionFile, final String subSectionName) throws IOException {
		final String urlToSubSection = subSectionName.replace(" ", "");
		Files.write(Paths.get(pathToSectionFile),
				new String("* [" + subSectionName + "](references#" + urlToSubSection + ")\n\n").getBytes(),
				StandardOpenOption.APPEND);
	}

	private static void addModel(final String pathToSubSectionFile, final String modelName, final String modelFileName,
			final List<String> screenshotPathList) throws IOException {
		final String urlToModel = modelFileName.replace(" ", "");
		Files.write(Paths.get(pathToSubSectionFile),
				new String("* [" + modelName + "](references#" + urlToModel + ")\n\n").getBytes(),
				StandardOpenOption.APPEND);
		// show the images (if there are some)
		for (final String imagePath : screenshotPathList) {
			// imagePath = imagePath.replace("\\", "/");
			// String urlToImage = imagePath.split(wikiFolder)[1];
			Files.write(Paths.get(pathToSubSectionFile), getHTMLCodeForImage(imagePath).getBytes(),
					StandardOpenOption.APPEND);
		}
	}

	private static String extractHeader(final File file) throws IOException {
		// returns the header
		String result = "";

		final FileInputStream fis = new FileInputStream(file);
		final BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;

		// check if the file contains a header
		if ((line = br.readLine()).startsWith("/**")) {
			result += line + "\n";
			while ((line = br.readLine()) != null) {
				result += line + "\n";
				if (line.startsWith("*/") || line.startsWith(" */")) {
					break;
				}
			}
		} else {
			br.close();
		}
		br.close();
		return result;
	}

	private static ArrayList<String> searchInputListRecursive(final File file, ArrayList<String> results)
			throws IOException {

		final FileInputStream fis = new FileInputStream(file);
		final BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;

		line = br.readLine();
		// search for a line that starts with "import"
		while (line != null) {
			final String regexMatch = Utils.findAndReturnRegex(line, "import \"(.*[^\"])\"");
			if (regexMatch != "") {
				results.add(0, file.getParentFile().getAbsolutePath().replace("\\", "/") + "/" + regexMatch);
				results = searchInputListRecursive(
						new File(file.getParentFile().getAbsolutePath().replace("\\", "/") + "/" + regexMatch),
						results);
			}
			line = br.readLine();
		}
		br.close();
		return results;
	}

	private static String getModelCode(final File gamlFile) throws IOException {
		// write the code
		String result = "";
		result = "```\n";
		final FileInputStream fis = new FileInputStream(gamlFile);
		final BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		boolean inHeader = true;
		while ((line = br.readLine()) != null) {
			if (!inHeader) {
				// we are in the code
				result += line + "\n";
			} else if (line.startsWith("*/") || line.startsWith(" */")) {
				// we are out of the header
				inHeader = false;
			} else if (line.startsWith("model")) {
				// we are in the code
				inHeader = false;
				result += line + "\n";
			}
		}
		result += "```\n";
		br.close();
		return result;
	}

	private static String getHTMLCodeForImage(String absolutePathForImage) {
		absolutePathForImage = absolutePathForImage.replace("\\", "/");
		final String realPath = "gm_wiki/" + absolutePathForImage.split(wikiFolder)[1];
		String result = "";
		result += "<p>";
		result += "<img src=\"" + realPath + "\" alt=\"Eclipse folder.\" title class=\"img-responsive\">";
		result += "</p>";
		return result;
	}
}
