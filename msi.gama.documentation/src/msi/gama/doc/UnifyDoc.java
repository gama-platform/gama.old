package msi.gama.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class UnifyDoc {

	private static String[] tabEltXML = {"operatorsCategories","operators", "skills","speciess","statements"};	
	
	public static void unify() throws IOException, JDOMException {
		HashMap<File,String> hmFilesPackages = getMapFiles(".");
		Document doc = mergeFiles(hmFilesPackages);
		
		// TODO : FAIRE un mode debug!
   		System.out.println(""+hmFilesPackages);
		
		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
        // sortie.output(doc, System.out);
        sortie.output(doc, new FileOutputStream(Constants.DOCGAMA_GLOBAL_FILE));		
	}
	
	// This method will parse the Eclipse workspace to find project that have a file "docGama.xml"
	// It will then return the HashMap containing all these files associated with their project name 
 	private static HashMap<File, String> getMapFiles(String folderName) throws IOException{
 		File mainFile = new File((new File(folderName)).getCanonicalPath());				
		File parentFile = new File(mainFile.getParent());	
		HashMap<File,String> hmFilesPackages = new HashMap<File, String>();
		
		for(File f : parentFile.listFiles()){			
			File docGamaFile = new File(f.getAbsolutePath() + File.separator + Constants.DOCGAMA_FILE);
			if(docGamaFile.exists()){
				hmFilesPackages.put(docGamaFile, f.getName());
			}
		}
		return hmFilesPackages;
 	}
	
	private static Document mergeFiles(HashMap<File,String> hmFilesPackages) throws JDOMException, IOException{
       	SAXBuilder builder = new SAXBuilder();      	
       	Document doc = null;
       	
       	for(Entry<File, String> fileDoc : hmFilesPackages.entrySet()){
	    	if(doc == null){
				doc = (Document) builder.build(fileDoc.getKey());	
				for(String catXML : tabEltXML){
					for(Element e : doc.getRootElement().getChild(catXML).getChildren()) {
						e.setAttribute("projectName", fileDoc.getValue());
					}	
				}
			}
	    	else {
				Document docTemp = (Document) builder.build(fileDoc.getKey());
				
				for(String catXML : tabEltXML){
					for(Element e : docTemp.getRootElement().getChild(catXML).getChildren()) {
						e.setAttribute("projectName", fileDoc.getValue());
						doc.getRootElement().getChild(catXML).addContent(e.clone());
					}	
				}
	    	}
       	}
       	
       	return doc;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static void main(String[] args) {
		try {
			unify();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}
}
