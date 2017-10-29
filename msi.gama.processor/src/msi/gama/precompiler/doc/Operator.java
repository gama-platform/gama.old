/*********************************************************************************************
 *
 * 'Operator.java, in plugin msi.gama.processor, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.precompiler.doc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.precompiler.Constants;
import msi.gama.precompiler.doc.utils.XMLElements;

public class Operator implements IElement {

	Document doc;

	String category;
	String[] concepts;
	String name;
	Operands operands;
	String documentation;

	public Operator(final Document _doc) {
		doc = _doc;
		operands = new Operands(_doc);
	}

	public Operator(final Document _doc, final String _category, final String[] _concepts, final String _name) {
		this(_doc);
		category = _category;
		name = _name;
		concepts = _concepts;
	}

	public Operator(final Document _doc, final String _category, final String[] _concepts, final String _name,
			final String _documentation) {
		this(_doc);
		category = _category;
		name = _name;
		documentation = _documentation;
		concepts = _concepts;
	}

	public void setDocumentation(final String d) {
		documentation = d;
	}

	public void setOperands(final String _classe, final String _content_type, final String _return_type,
			final String _type) {
		operands = new Operands(doc, _classe, _content_type, _return_type, _type);
	}

	public void addOperand(final Operand op) {
		operands.addOperand(op);
	}

	@Override
	public Element getElementDOM() {
		// TODO to finish
		final org.w3c.dom.Element eltOp = doc.createElement(XMLElements.OPERATOR);
		// eltOp.setAttribute(XMLElements.ATT_OP_CATEGORY, category);
		eltOp.setAttribute(XMLElements.ATT_OP_ID, name);
		eltOp.setAttribute(XMLElements.ATT_OP_NAME, name);
		eltOp.setAttribute(XMLElements.ATT_ALPHABET_ORDER, Constants.getAlphabetOrder(name));

		// Categories
		final org.w3c.dom.Element categoriesElt = doc.createElement(XMLElements.OPERATOR_CATEGORIES);
		final org.w3c.dom.Element catElt = doc.createElement(XMLElements.CATEGORY);
		catElt.setAttribute(XMLElements.ATT_CAT_ID, category);
		categoriesElt.appendChild(catElt);

		// Concepts
		final org.w3c.dom.Element conceptsElt = doc.createElement(XMLElements.CONCEPTS);
		for (final String conceptName : concepts) {
			final org.w3c.dom.Element conceptElt = doc.createElement(XMLElements.CONCEPT);
			conceptElt.setAttribute(XMLElements.ATT_CAT_ID, conceptName);
			conceptsElt.appendChild(conceptElt);
		}
		eltOp.appendChild(conceptsElt);

		// Combinaison IO
		final org.w3c.dom.Element combiElt = doc.createElement(XMLElements.COMBINAISON_IO);
		combiElt.appendChild(operands.getElementDOM());

		eltOp.appendChild(combiElt);

		// Documentation
		final org.w3c.dom.Element docElt = doc.createElement(XMLElements.DOCUMENTATION);
		final org.w3c.dom.Element resultElt = doc.createElement(XMLElements.RESULT);
		resultElt.setTextContent(documentation);

		docElt.appendChild(resultElt);
		eltOp.appendChild(docElt);

		return eltOp;
	}
}
