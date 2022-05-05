/*******************************************************************************************************
 *
 * Operator.java, in msi.gama.processor, is part of the source code of the
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

import msi.gama.precompiler.Constants;
import msi.gama.precompiler.doc.utils.XMLElements;

/**
 * The Class Operator.
 */
public class Operator implements IElement {

	/** The doc. */
	Document doc;

	/** The category. */
	String category;
	
	/** The concepts. */
	String[] concepts;
	
	/** The name. */
	String name;
	
	/** The combi IO. */
	// Operands operands;
	List<Operands> combiIO;	

/** The documentation. */
//	String documentation;
	Documentation documentation;
	
	/** The has example. */
	boolean has_example = false;

	/**
	 * Instantiates a new operator.
	 *
	 * @param _doc the doc
	 */
	public Operator(final Document _doc) {
		doc = _doc;
		combiIO = new ArrayList<>();
		documentation = new Documentation(_doc);
//		operands = new Operands(_doc);
	}

	/**
	 * Instantiates a new operator.
	 *
	 * @param _doc the doc
	 * @param _category the category
	 * @param _concepts the concepts
	 * @param _name the name
	 */
	public Operator(final Document _doc, final String _category, final String[] _concepts, final String _name) {
		this(_doc);
		category = _category;
		name = _name;
		concepts = _concepts;
	}

	/**
	 * Instantiates a new operator.
	 *
	 * @param _doc the doc
	 * @param _category the category
	 * @param _concepts the concepts
	 * @param _name the name
	 * @param _documentation the documentation
	 */
	public Operator(final Document _doc, final String _category, final String[] _concepts, final String _name,
			final String _documentation) {
		this(_doc);
		category = _category;
		name = _name;
		documentation = new Documentation(doc,_documentation);
		concepts = _concepts;
	}

	/**
	 * Sets the documentation.
	 *
	 * @param d the new documentation
	 */
	public void setDocumentation(final String d) {
		documentation.setResult(d);
	}
	
	/**
	 * Adds the operands.
	 *
	 * @param ops the ops
	 */
	public void addOperands(Operands ops) {
		combiIO.add(ops);
	}	
	

	/**
	 * Adds the see also.
	 *
	 * @param see the see
	 */
	public void addSeeAlso(String see) {
		documentation.addSee(see);
	}
	
	/**
	 * Adds the usage.
	 *
	 * @param descUsage the desc usage
	 * @param exElt the ex elt
	 */
	public void addUsage(String descUsage, org.w3c.dom.Element exElt) {
		if((exElt != null) && (exElt.hasChildNodes()) ) {
			has_example = true;
		}
		documentation.addUsage(descUsage, exElt);
	}	
	
	/**
	 * Adds the usage.
	 *
	 * @param descUsage the desc usage
	 */
	public void addUsage(String descUsage) {
		this.addUsage(descUsage, null);
	}

	
	@Override
	public Element getElementDOM() {
		// TODO to finish
		final org.w3c.dom.Element eltOp = doc.createElement(XMLElements.OPERATOR);
		// eltOp.setAttribute(XMLElements.ATT_OP_CATEGORY, category);
		eltOp.setAttribute(XMLElements.ATT_OP_ID, name);
		eltOp.setAttribute(XMLElements.ATT_OP_NAME, name);
		eltOp.setAttribute(XMLElements.ATT_ALPHABET_ORDER, Constants.getAlphabetOrder(name));
		if(has_example) {
			eltOp.setAttribute("HAS_TEST", "true");			
		}
			
		// Categories
		final org.w3c.dom.Element categoriesElt = doc.createElement(XMLElements.OPERATOR_CATEGORIES);
		final org.w3c.dom.Element catElt = doc.createElement(XMLElements.CATEGORY);
		catElt.setAttribute(XMLElements.ATT_CAT_ID, category);
		categoriesElt.appendChild(catElt);
		eltOp.appendChild(categoriesElt);
		
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
		for(Operands ops : combiIO) {
			combiElt.appendChild(ops.getElementDOM());
		}
		eltOp.appendChild(combiElt);

		// Documentation
		eltOp.appendChild(documentation.getElementDOM());

		return eltOp;
	}


}
