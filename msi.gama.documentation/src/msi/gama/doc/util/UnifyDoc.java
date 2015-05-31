/*********************************************************************************************
 * 
 *
 * 'UnifyDoc.java', in plugin 'msi.gama.documentation', is part of the source code of the 
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import msi.gama.precompiler.doc.utils.TypeConverter;
import msi.gama.precompiler.doc.utils.XMLElements;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

public class UnifyDoc {

	private static String[] tabEltXML = {
		XMLElements.OPERATORS_CATEGORIES,
		XMLElements.OPERATORS,
		XMLElements.SKILLS,
		XMLElements.ARCHITECTURES,
		XMLElements.SPECIESS,
		XMLElements.STATEMENTS,
		XMLElements.CONSTANTS_CATEGORIES,
		XMLElements.CONSTANTS,
		XMLElements.INSIDE_STAT_KINDS,
		XMLElements.INSIDE_STAT_SYMBOLS,
		XMLElements.STATEMENT_KINDS};	
	// among tebEltXML, categories do not need to have an additional projectName attribute
	private static String[] tabCategoriesEltXML = {
		XMLElements.OPERATORS_CATEGORIES,
		XMLElements.CONSTANTS_CATEGORIES,
		XMLElements.INSIDE_STAT_KINDS,
		XMLElements.INSIDE_STAT_SYMBOLS,
		XMLElements.STATEMENT_KINDS};	
	
	public static void unify() throws IOException, JDOMException, ParserConfigurationException, SAXException {
		WorkspaceManager ws = new WorkspaceManager(".");
		HashMap<String, File> hmFiles = ws.getProductDocFiles(); 
		
		Document doc = mergeFiles(hmFiles);
		
   		System.out.println(""+hmFiles);
		
		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
        sortie.output(doc, new FileOutputStream(Constants.DOCGAMA_GLOBAL_FILE));		
	}
	
	private static Document mergeFiles(HashMap<String,File> hmFilesPackages) throws JDOMException, IOException{
       	SAXBuilder builder = new SAXBuilder();      	
       	Document doc = null;
       	
		doc = new Document(new Element(XMLElements.DOC));
		for(String elt : tabEltXML) {
			doc.getRootElement().addContent(new Element(elt));
		}
       	
       	for(Entry<String, File> fileDoc : hmFilesPackages.entrySet()){
			Document docTemp = (Document) builder.build(fileDoc.getValue());
			
			for(String catXML : tabEltXML){
				if(docTemp.getRootElement().getChild(catXML) != null){

					List<Element> existingElt = doc.getRootElement().getChild(catXML).getChildren();

					for(Element e : docTemp.getRootElement().getChild(catXML).getChildren()) {
						// Do not add the projectName for every kinds of categories 
						if(!Arrays.asList(tabCategoriesEltXML).contains(catXML)) e.setAttribute("projectName", fileDoc.getKey());
						
						// Test whether the element is already in the merged doc
						boolean found = false;
						for(Element exElt : existingElt){
							boolean equals = (exElt.getName()).equals(e.getName());
							for(Attribute att : exElt.getAttributes()){
							 	 String valueExElt = (exElt.getAttribute(att.getName()) != null ) ? exElt.getAttributeValue(att.getName()) : "";
							 	 String valueE = (e.getAttribute(att.getName()) != null) ? e.getAttributeValue(att.getName()) : "";
								equals = equals && (valueExElt.equals(valueE));
							}		
							found = found || equals;
						}						
						// Add it if it is not already in the merged doc
						if( !found) {
							doc.getRootElement().getChild(catXML).addContent(e.clone());
						}
					}	
				}
			}
       	}
       	     
       	// Add an element for the generated types       	
       	doc.getRootElement().getChild(XMLElements.OPERATORS_CATEGORIES).addContent(
       			(new Element(XMLElements.CATEGORY)).setAttribute(XMLElements.ATT_CAT_ID, (new TypeConverter()).getProperCategory("Types")));
       	
       	return doc;
	}
		
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static void main(String[] args) {

	}
}
