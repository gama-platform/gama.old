package msi.gama.doc.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TOCManager {

	String tocFile;
	
	public TOCManager(String toc){
		tocFile = toc;
	}
	
	public List<String> getTocFilesList() 
			throws ParserConfigurationException, SAXException, IOException{
		List<String> lFile = new ArrayList<String>();
		Document doc = XMLUtils.createDoc(tocFile);
		NodeList nl = doc.getElementsByTagName("chapter");
		
		for(int i = 0; i<nl.getLength(); i++){
			lFile.add(".."+File.separator+".."+File.separator+"gama.wiki" + File.separator + ((Element)nl.item(i)).getAttribute("file") + ".md");
		}
		return lFile;
	}	
	
	public String getTocFilesString() 
			throws ParserConfigurationException, SAXException, IOException {
		List<String> lf = getTocFilesList();
		String files = "";
		for(String f : lf){
			files = files + f + " ";
		}
		return files;
	}
	
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		TOCManager t = new TOCManager(Constants.TOC_FILE);
		System.out.println(t.getTocFilesString());
	}

}
