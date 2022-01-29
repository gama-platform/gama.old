/*******************************************************************************************************
 *
 * Documentation.java, in msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler.doc;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.precompiler.doc.utils.XMLElements;

/**
 * The Class Documentation.
 */
public class Documentation  implements IElement {

	/** The doc. */
	Document doc;

	/** The result. */
	String result;
	
	/** The usages. */
	List<DocUsage> usages;
	
	/** The see also. */
	List<String> seeAlso;
	
	/**
	 * Instantiates a new documentation.
	 *
	 * @param _doc the doc
	 */
	public Documentation(final Document _doc) {
		this(_doc,"");
	}

	/**
	 * Instantiates a new documentation.
	 *
	 * @param _doc the doc
	 * @param res the res
	 */
	public Documentation(final Document _doc, final String res) {
		doc = _doc;
		result = res;
		
		usages  = new ArrayList<>();
		seeAlso = new ArrayList<>();
	}

	/**
	 * Sets the result.
	 *
	 * @param r the new result
	 */
	public void setResult(String r) {
		result = r;		
	}	
	
	/**
	 * Adds the see.
	 *
	 * @param see the see
	 */
	public void addSee(String see) {
		seeAlso.add(see);
	}
	
	/**
	 * Adds the usage.
	 *
	 * @param desc the desc
	 * @param exElt the ex elt
	 */
	public void addUsage(String desc, org.w3c.dom.Element exElt) {
		usages.add(new DocUsage(doc, desc, exElt));
	}
	
	@Override
	public Element getElementDOM() {
		// Documentation
		final org.w3c.dom.Element docElt = doc.createElement(XMLElements.DOCUMENTATION);
		
		// Result
		final org.w3c.dom.Element resultElt = doc.createElement(XMLElements.RESULT);
		resultElt.setTextContent(result);
		docElt.appendChild(resultElt);
		
		
		// Usages 
		if(usages.size() != 0) {
			final org.w3c.dom.Element usagesElt = doc.createElement(XMLElements.USAGES);
			for(DocUsage use : usages) {
				usagesElt.appendChild(use.getElementDOM());
			}	
			
			docElt.appendChild(usagesElt);			
		}

		
		
		// See also
		if (seeAlso.size() != 0) {
			org.w3c.dom.Element seeAlsoElt = doc.createElement(XMLElements.SEEALSO);
			
			for (final String see : seeAlso) {
				final org.w3c.dom.Element seesElt = doc.createElement(XMLElements.SEE);
				seesElt.setAttribute(XMLElements.ATT_SEE_ID, see);
				seeAlsoElt.appendChild(seesElt);
			}	

			docElt.appendChild(seeAlsoElt);
		}	

		return docElt;
	}

}