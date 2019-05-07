package msi.gama.precompiler.doc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.precompiler.doc.utils.XMLElements;


public class DocUsage  implements IElement {

	Document doc;
	String descriptionUsage;

	public DocUsage(final Document _doc) {
		this(_doc,"");
	}

	public DocUsage(final Document _doc, final String desc) {
		doc = _doc;
		descriptionUsage = desc;
	}
	
	@Override
	public Element getElementDOM() {
		
		final org.w3c.dom.Element usageElt = doc.createElement(XMLElements.USAGE);
		usageElt.setAttribute(XMLElements.ATT_USAGE_DESC, descriptionUsage);
	
		
//		usageElt.setAttribute(XMLElements.ATT_USAGE_DESC, usage.value());
//		final org.w3c.dom.Element examplesUsageElt =
//				getExamplesElt(usage.examples(), doc, e, tc, parentElement);
//		usageElt.appendChild(examplesUsageElt);
//		usagesElt.appendChild(usageElt);

		return usageElt;
	}	
}
