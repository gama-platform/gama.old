package msi.gama.precompiler.doc;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.precompiler.doc.utils.XMLElements;

public class Documentation  implements IElement {

	Document doc;

	String result;
	List<DocUsage> usages;
	List<String> seeAlso;
	
	public Documentation(final Document _doc) {
		this(_doc,"");
	}

	public Documentation(final Document _doc, final String res) {
		doc = _doc;
		result = res;
		
		usages  = new ArrayList<>();
		seeAlso = new ArrayList<>();
	}

	public void setResult(String r) {
		result = r;		
	}	
	
	public void addSee(String see) {
		seeAlso.add(see);
	}
	
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