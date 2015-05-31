package msi.gama.doc.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author bgaudou
 *
 */
public class WorkspaceManager {
	private File wsFile;
	
	public WorkspaceManager(String location) throws IOException{
 		File mainFile = new File((new File(location)).getCanonicalPath());				
		wsFile = new File(mainFile.getParent());			
	}

	public File getPluginFolder(String plugin){
		return new File(wsFile.getAbsolutePath() + File.separator + plugin);
	}

	public File getFeatureFile(String feature) {
		return new File(wsFile.getAbsolutePath() + File.separator + feature + File.separator + "feature.xml");
	}
	
	public File getProductFile() throws IOException{
		File productFile = new File(wsFile.getAbsolutePath() + File.separator + Constants.RELEASE_APPLICATION + File.separator + Constants.RELEASE_PRODUCT);
		if(! productFile.exists()){
			throw new IOException("Product file do not exist");
		}
		return productFile;
	}	
	
	/**
	 * @param pluginName
	 * @return true whether the pluginName plugin exists in the workspace
	 */
	public boolean isGAMAPlugin(String pluginName){
		File plugin = getPluginFolder(pluginName);
		return plugin.exists();
	}
	
	public boolean isFeature(String pluginName){
		if(!isGAMAPlugin(pluginName)) return false;
		File feature = getFeatureFile(pluginName);
		return feature.exists();
	}	
	
 	/**
 	 * This method will parse the Eclipse workspace to find project that have a file "docGama.xml".
 	 * @return It will then return the HashMap containing all their project name with their associated files associated 
 	 * @throws IOException
 	 */
 	public HashMap<String, File> getDocFiles() throws IOException{
		HashMap<String, File> hmFilesPackages = new HashMap<String, File>();
		
		for(File f : wsFile.listFiles()){			
			File docGamaFile = new File(f.getAbsolutePath() + File.separator + Constants.DOCGAMA_FILE);
			if(docGamaFile.exists()){
				hmFilesPackages.put(f.getName(),docGamaFile);
			}
		}
		return hmFilesPackages;
 	}
	
	/**
	 * From a product file, get all the features
	 * @param feature
	 * @return the list of the name of all features included in the product
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public List<String> getPluginsFromProduct(File product) throws ParserConfigurationException, SAXException, IOException{
		ArrayList<String> listPlugins = new ArrayList<String>();
		
		// Creation of the DOM source
		org.w3c.dom.Document document = XMLUtils.createDoc(product);
		
		// Test whether the product is based on features (we do not consider plugin-based product)
		NodeList nLProduct = document.getElementsByTagName("product");
		org.w3c.dom.Element eltProduct = (org.w3c.dom.Element) nLProduct.item(0);
		if(!eltProduct.getAttribute("useFeatures").equals("true")) throw new IOException("Plugin-based products are not managed");	
		
		// We get the features from the product 
		NodeList nLFeatures = document.getElementsByTagName("feature");
		for(int j = 0; j < nLFeatures.getLength(); j++){
			org.w3c.dom.Element eltFeature = (org.w3c.dom.Element) nLFeatures.item(j);
			// System.out.println(eltFeature.getAttribute("id"));
			listPlugins.add(eltFeature.getAttribute("id"));
		}
		
		return listPlugins;
	}
	
	/**
	 * From a feature file, get all the plugins
	 * @param feature
	 * @return the list of the name of all plugins included in the feature
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public List<String> getPluginsFromFeature(File feature) throws ParserConfigurationException, SAXException, IOException{
		ArrayList<String> listPlugins = new ArrayList<String>();
		
		// Creation of the DOM source
		org.w3c.dom.Document document = XMLUtils.createDoc(feature);	

		// We get the plugins from the feature 
		NodeList nlPlugins = document.getElementsByTagName("plugin");
		for(int j = 0; j < nlPlugins.getLength(); j++){
			org.w3c.dom.Element eltPlugin = (org.w3c.dom.Element) nlPlugins.item(j);
			// System.out.println(eltPlugin.getAttribute("id"));
			listPlugins.add(eltPlugin.getAttribute("id"));
		}		

		// We get the included features from the feature 
		NodeList nlFeatures = document.getElementsByTagName("includes");
		for(int j = 0; j < nlFeatures.getLength(); j++){
			org.w3c.dom.Element eltFeature = (org.w3c.dom.Element) nlFeatures.item(j);
		//	System.out.println(eltFeature.getAttribute("id"));
			listPlugins.add(eltFeature.getAttribute("id"));
		}		
		
		return listPlugins;
	}
	
	public List<String> getAllGAMAPluginsInProduct() throws ParserConfigurationException, SAXException, IOException{
		ArrayList<String> listPlugins = new ArrayList<String>();
		List<String> initPluginList = getPluginsFromProduct(getProductFile());
		for(String plugin : initPluginList){
			listPlugins.addAll(getList(plugin));
		}
		
		return listPlugins;
	}
	
	private List<String> getList(String plugin) throws ParserConfigurationException, SAXException, IOException{
		ArrayList<String> listPlugins = new ArrayList<String>();
		if(isFeature(plugin)) {
			List<String> pluginsFromFeature = getPluginsFromFeature(getFeatureFile(plugin));
			for(String name : pluginsFromFeature) {
				listPlugins.addAll(getList(name));
			}
			
		} else {
			if(isGAMAPlugin(plugin)) listPlugins.add(plugin);
		}
		return listPlugins;
	}

	public static void main(String[] arg) throws IOException, ParserConfigurationException, SAXException{
		WorkspaceManager ws = new WorkspaceManager(".");
		List<String> l = ws.getAllGAMAPluginsInProduct();
		for(String name : l){
			System.out.println(name);
		}
	}
}

