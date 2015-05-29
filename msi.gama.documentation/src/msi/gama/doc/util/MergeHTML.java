/*********************************************************************************************
 * 
 *
 * 'MergeHTML.java', in plugin 'msi.gama.documentation', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class MergeHTML {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static void main(String[] args) throws JDOMException, IOException {
		mergeHTMLFiles();
	}
	
	public static void mergeHTMLFiles() throws JDOMException, IOException{
	 	SAXBuilder builder = new SAXBuilder();      	
       	Document docTOC = (Document) builder.build(Constants.TOC_FILE);	    	
      	Document docRes = null;
      	Element bodyEltRes = null;
      	
       	for(Element e : docTOC.getRootElement().getChildren()){
       		if("part".equals(e.getName())){
       			if(docRes != null){
    	    		Element partElt = new Element("part");
    	    		partElt.setAttribute(new Attribute("name", e.getAttributeValue("name")));	
    	    		bodyEltRes.addContent(partElt);
       			}
       		}
       		else {
	       		String fileToMerge = Constants.WIKI2WIKI_FOLDER + File.separator + e.getAttributeValue("file") + ".html";
	       		Document docToMerge = (Document) builder.build(fileToMerge);	 
	  
	    		Element chapElt = new Element("chapter");
	    		chapElt.setAttribute(new Attribute("name", e.getAttributeValue("name")));
	    		chapElt.setAttribute(new Attribute("fileName", e.getAttributeValue("file")));
	       		// TODO Add a new anchor
	    		Element anchorElt = new Element("a");
	    		anchorElt.setAttribute(new Attribute("name", e.getAttributeValue("file")));
	    		chapElt.addContent(anchorElt);
	    		
	    		Element divMainEltElement = null;
	    		for(Element elt : docToMerge.getRootElement().getChild("body").getChildren()) {
	    			if("main".equals(elt.getAttributeValue("id"))) {
	    				divMainEltElement = elt; 
	    			}
	    		}
	
	    		for(Element eltInMain : divMainEltElement.getChildren()) {
	    			chapElt.addContent(eltInMain.clone());
	    		}		
	    		
	       		if(docRes == null){
	       			docRes = (Document) builder.build(fileToMerge);
	       			String docTitle = docTOC.getRootElement().getAttributeValue("title");
	       			docRes.getRootElement().getChild("head").getChild("title").removeContent();
	       			docRes.getRootElement().getChild("head").getChild("title").addContent(docTitle);
	       			
	       			bodyEltRes = docRes.getRootElement().getChild("body");
	    			bodyEltRes.removeContent();
	    		}
	    		bodyEltRes.addContent(chapElt);
       		}
       	}
       	
        XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
        // sortie.output(docRes, System.out);
        sortie.output(docRes, new FileOutputStream(Constants.DOCGAMA_FINAL));
	}
}

