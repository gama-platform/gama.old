/*********************************************************************************************
 *
 * 'Category.java, in plugin msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.precompiler.doc;

import msi.gama.precompiler.doc.utils.XMLElements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Category implements IElement {

	Document doc;
	String idCategory;
	
	public Category(Document _doc, String id){
		doc = _doc;
		idCategory = id;
	}
	
	@Override
	public Element getElementDOM() {
		Element eltCat = doc.createElement(XMLElements.CATEGORY);
		eltCat.setAttribute("id", idCategory);
		return eltCat;
	}

}
