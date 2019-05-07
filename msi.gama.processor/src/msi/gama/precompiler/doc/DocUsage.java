package msi.gama.precompiler.doc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.precompiler.doc.utils.XMLElements;


public class DocUsage implements IElement {

	Document doc;
	String descriptionUsage;
	org.w3c.dom.Element exElt;

	public DocUsage(final Document _doc) {
		this(_doc,"",null);
	}

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
