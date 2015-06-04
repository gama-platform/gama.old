package msi.gama.doc.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
	
	public void createPartFiles() 
			throws ParserConfigurationException, SAXException, IOException{
		Document doc = XMLUtils.createDoc(tocFile);
		NodeList nl = doc.getElementsByTagName("part");		
		
		for(int i = 0; i<nl.getLength(); i++){
			String partName = ((Element)nl.item(i)).getAttribute("name");
			File partFile = new File(Constants.TOC_GEN_FOLDER + File.separator + partName.replaceAll(" ", "_") + ".md");

			FileWriter fw=new FileWriter(partFile);
			BufferedWriter partBw= new BufferedWriter(fw);
			
			partBw.newLine();
			partBw.write("\\part{"+partName+"}");		
			partBw.newLine();
			partBw.close();
		}
	}
	
	public List<String> getTocFilesList() 
			throws ParserConfigurationException, SAXException, IOException{
		List<String> lFile = new ArrayList<String>();
		Document doc = XMLUtils.createDoc(tocFile);
		
		NodeList nlPart = doc.getElementsByTagName("part");		
		for(int i = 0; i<nlPart.getLength(); i++){
			Element eltPart = (Element)nlPart.item(i);
			File fPart = new File(Constants.TOC_GEN_FOLDER + File.separator + eltPart.getAttribute("name").replaceAll(" ", "_") + ".md");
			lFile.add( fPart.getAbsolutePath() );
			
			NodeList chapterList = eltPart.getElementsByTagName("chapter");
			for(int j = 0; j<chapterList.getLength(); j++){
				lFile.add( ((Element)chapterList.item(j)).getAttribute("file") + ".md");
			}			
		}

		return lFile;
	}	
	
	public String getTocFilesString() 
			throws ParserConfigurationException, SAXException, IOException {
		List<String> lf = getTocFilesList();
		File blankPage = new File(Constants.MD_BLANK_PAGE);
		String files = "";
		for(String f : lf){
			files = files + f + " " + blankPage.getAbsolutePath() + " ";
		}
		return files;
	}
	
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		TOCManager t = new TOCManager(Constants.TOC_FILE);
		System.out.println(t.getTocFilesString());
		
		// t.createPartFiles();
		System.out.println(t.getTocFilesString());
	}

}
