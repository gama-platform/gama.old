/*******************************************************************************************************
 *
 * DocUsage.java, in msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler.doc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.precompiler.doc.utils.XMLElements;


/**
 * The Class DocUsage.
 */
public class DocUsage implements IElement {

	/** The doc. */
	Document doc;
	
	/** The description usage. */
	String descriptionUsage;
	
	/** The ex elt. */
	org.w3c.dom.Element exElt;

	/**
	 * Instantiates a new doc usage.
	 *
	 * @param _doc the doc
	 */
	public DocUsage(final Document _doc) {
		this(_doc,"",null);
	}

	/**
	 * Instantiates a new doc usage.
	 *
	 * @param _doc the doc
	 * @param desc the desc
	 * @param _exElt the ex elt
	 */
	public DocUsage(final Document _doc, final String desc, org.w3c.dom.Element _exElt) {
		doc = _doc;
		descriptionUsage = desc;
		exElt = _exElt;
	}
	
	@Override
	public Element getElementDOM() {
		
		final org.w3c.dom.Element usageElt = doc.createElement(XMLElements.USAGE);
		usageElt.setAttribute(XMLElements.ATT_USAGE_DESC, descriptionUsage);
		if(exElt != null) {
			usageElt.appendChild(exElt);			
		}

		return usageElt;
	}	
}
