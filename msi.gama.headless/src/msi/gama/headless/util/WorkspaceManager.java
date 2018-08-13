package msi.gama.headless.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ummisco.gama.dev.utils.DEBUG;

/**
 * @author bgaudou
 *
 */
public class WorkspaceManager {

	static {
		DEBUG.ON();
	}
	private final File wsFile;

	public WorkspaceManager(final String location) throws IOException {
		wsFile = new File(new File(location).getCanonicalPath());
	}

	public File getFile() {
		return wsFile;
	}

	public File getPluginFolder(final String plugin) {
		return new File(wsFile.getAbsolutePath() + File.separator + plugin);
	}

	public File getFeatureFile(final String feature) {
		return new File(wsFile.getAbsolutePath() + File.separator + feature + File.separator + "feature.xml");
	}

	public File getDocFile(final String plugin) {
		return new File(wsFile.getAbsolutePath() + File.separator + plugin + File.separator + Constants.DOCGAMA_FILE);
	}

	public File getProductFile() throws IOException {
		final File productFile = new File(wsFile.getAbsolutePath() + File.separator + Constants.RELEASE_APPLICATION
				+ File.separator + Constants.RELEASE_PRODUCT);
		if (!productFile.exists()) { throw new IOException("Product file do not exist"); }
		return productFile;
	}

	/**
	 * @param pluginName
	 * @return true whether the pluginName plugin exists in the workspace
	 */
	public boolean isGAMAPlugin(final String pluginName) {
		final File plugin = getPluginFolder(pluginName);
		return plugin.exists();
	}

	public boolean isFeature(final String pluginName) {
		if (!isGAMAPlugin(pluginName)) { return false; }
		final File feature = getFeatureFile(pluginName);
		return feature.exists();
	}

	public boolean hasPluginDoc(final String pluginName) {
		final File pluginDoc = getDocFile(pluginName);
		return pluginDoc.exists();
	}

	/**
	 * This method will parse the Eclipse workspace to find project that have a file "docGama.xml".
	 * 
	 * @return It will then return the HashMap containing all their project name with their associated files associated
	 * @throws IOException
	 */
	public HashMap<String, File> getAllDocFiles() throws IOException {
		final HashMap<String, File> hmFilesPackages = new HashMap<>();

		for (final File f : wsFile.listFiles()) {
			final File docGamaFile = new File(f.getAbsolutePath() + File.separator + Constants.DOCGAMA_FILE);
			if (docGamaFile.exists()) {
				hmFilesPackages.put(f.getName(), docGamaFile);
			}
		}
		return hmFilesPackages;
	}

	public HashMap<String, File> getProductDocFiles() throws IOException, ParserConfigurationException, SAXException {
		final HashMap<String, File> hmFilesPackages = getAllDocFiles();
		final List<String> pluginsProduct = getAllGAMAPluginsInProduct();
		final HashMap<String, File> hmFilesRes = new HashMap<>();

		for (final Entry<String, File> eSF : hmFilesPackages.entrySet()) {
			if (pluginsProduct.contains(eSF.getKey())) {
				hmFilesRes.put(eSF.getKey(), eSF.getValue());
			}
		}

		return hmFilesRes;
	}

	public HashMap<String, File> getExtensionsDocFiles()
			throws IOException, ParserConfigurationException, SAXException {
		final HashMap<String, File> hmFilesPackages = getAllDocFiles();
		final List<String> pluginsProduct = getAllGAMAPluginsInProduct();
		final HashMap<String, File> hmFilesRes = new HashMap<>();

		for (final Entry<String, File> eSF : hmFilesPackages.entrySet()) {
			if (!pluginsProduct.contains(eSF.getKey())) {
				hmFilesRes.put(eSF.getKey(), eSF.getValue());
			}
		}

		return hmFilesRes;
	}

	/**
	 * From a product file, get all the features
	 * 
	 * @param feature
	 * @return the list of the name of all features included in the product
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private List<String> getPluginsFromProduct(final File product)
			throws ParserConfigurationException, SAXException, IOException {
		final ArrayList<String> listPlugins = new ArrayList<>();

		// Creation of the DOM source
		final org.w3c.dom.Document document = XMLUtils.createDoc(product);

		// Test whether the product is based on features (we do not consider
		// plugin-based product)
		final NodeList nLProduct = document.getElementsByTagName("product");
		final org.w3c.dom.Element eltProduct = (org.w3c.dom.Element) nLProduct.item(0);
		if (!eltProduct.getAttribute("useFeatures")
				.equals("true")) { throw new IOException("Plugin-based products are not managed"); }

		// We get the features from the product
		final NodeList nLFeatures = document.getElementsByTagName("feature");
		for (int j = 0; j < nLFeatures.getLength(); j++) {
			final org.w3c.dom.Element eltFeature = (org.w3c.dom.Element) nLFeatures.item(j);
			listPlugins.add(eltFeature.getAttribute("id"));
		}

		return listPlugins;
	}

	/**
	 * From a feature file, get all the plugins
	 * 
	 * @param feature
	 * @return the list of the name of all plugins included in the feature
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private List<String> getPluginsFromFeature(final File feature)
			throws ParserConfigurationException, SAXException, IOException {
		final ArrayList<String> listPlugins = new ArrayList<>();

		// Creation of the DOM source
		final org.w3c.dom.Document document = XMLUtils.createDoc(feature);

		// We get the plugins from the feature
		final NodeList nlPlugins = document.getElementsByTagName("plugin");
		for (int j = 0; j < nlPlugins.getLength(); j++) {
			final org.w3c.dom.Element eltPlugin = (org.w3c.dom.Element) nlPlugins.item(j);
			listPlugins.add(eltPlugin.getAttribute("id"));
		}

		// We get the included features from the feature
		final NodeList nlFeatures = document.getElementsByTagName("includes");
		for (int j = 0; j < nlFeatures.getLength(); j++) {
			final org.w3c.dom.Element eltFeature = (org.w3c.dom.Element) nlFeatures.item(j);
			listPlugins.add(eltFeature.getAttribute("id"));
		}

		return listPlugins;
	}

	public List<String> getAllGAMAPluginsInProduct() throws ParserConfigurationException, SAXException, IOException {
		final ArrayList<String> listPlugins = new ArrayList<>();
		final List<String> initPluginList = getPluginsFromProduct(getProductFile());
		for (final String plugin : initPluginList) {
			listPlugins.addAll(getList(plugin));
		}

		return listPlugins;
	}

	private List<String> getList(final String plugin) throws ParserConfigurationException, SAXException, IOException {
		final ArrayList<String> listPlugins = new ArrayList<>();
		if (isFeature(plugin)) {
			final List<String> pluginsFromFeature = getPluginsFromFeature(getFeatureFile(plugin));
			for (final String name : pluginsFromFeature) {
				listPlugins.addAll(getList(name));
			}

		} else {
			if (isGAMAPlugin(plugin)) {
				listPlugins.add(plugin);
			}
		}
		return listPlugins;
	}

	/*****************************************************************************************************
	 * 
	 * 
	 */
	public ArrayList<String> getModelLibrary() {
		final ArrayList<String> modelList = readDirectory(
				wsFile.getAbsolutePath() + File.separator + "msi.gama.models" + File.separator + "models");
		return modelList;
	}

	public static ArrayList<String> readDirectory(final String dir) {
		final ArrayList<String> listFiles = new ArrayList<>();
		final File rep = new File(dir);

		if (rep.isDirectory()) {
			final String t[] = rep.list();

			if (t != null) {
				for (final String fName : t) {
					final ArrayList<String> newList = readDirectory(rep.getAbsolutePath() + File.separator + fName);
					listFiles.addAll(newList);
				}
			}
		} else {
			if ("gaml".equals(WorkspaceManager.getFileExtension(rep.getAbsolutePath()))) {
				listFiles.add(rep.getAbsolutePath());
			}
		}

		return listFiles;
	}

	public static String getFileExtension(final String fileName) {
		String extension = null;
		try {
			extension = fileName.substring(fileName.lastIndexOf(".") + 1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return extension;
	}

	public static void main(final String[] arg) throws IOException, ParserConfigurationException, SAXException {
		final WorkspaceManager ws = new WorkspaceManager(".");
		List<String> l = ws.getAllGAMAPluginsInProduct();
		if (DEBUG.IS_ON()) {
			for (final String name : l) {
				DEBUG.OUT(name);
			}

			DEBUG.LINE();

			HashMap<String, File> hm = ws.getAllDocFiles();
			for (final Entry<String, File> e : hm.entrySet()) {
				DEBUG.OUT(e.getKey());
			}
			DEBUG.LINE();
			hm = ws.getProductDocFiles();
			for (final Entry<String, File> e : hm.entrySet()) {
				DEBUG.OUT(e.getKey());
			}
			DEBUG.LINE();
			hm = ws.getExtensionsDocFiles();
			for (final Entry<String, File> e : hm.entrySet()) {
				DEBUG.OUT(e.getKey());
			}
			DEBUG.LINE();

			DEBUG.LINE();

			l = ws.getModelLibrary();
			for (final String name : l) {
				DEBUG.OUT(name);
			}
			DEBUG.OUT(String.valueOf(l.size()));
			DEBUG.OUT("----------");
		}
	}
}
