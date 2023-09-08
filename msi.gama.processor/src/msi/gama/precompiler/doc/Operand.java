/*******************************************************************************************************
 *
 * Operand.java, in msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler.doc;

import msi.gama.precompiler.doc.utils.XMLElements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class Operand.
 */
public class Operand implements IElement {

	/** The doc. */
	Document doc;
	
	/** The name. */
	String name;
	
	/** The position. */
	int position;
	
	/** The type. */
	String type;
	
	/**
	 * Instantiates a new operand.
	 *
	 * @param _doc the doc
	 */
	public Operand(Document _doc){
		doc = _doc;
	}
	
	/**
	 * Instantiates a new operand.
	 *
	 * @param _doc the doc
	 * @param _name the name
	 * @param _position the position
	 * @param _type the type
	 */
	public Operand(Document _doc, String _name, int _position, String _type){
		doc = _doc;
		name = _name;
		position = _position;
		type = _type;
	}
	
	@Override
	public Element getElementDOM() {
		org.w3c.dom.Element operandElt = doc.createElement(XMLElements.OPERAND);
		operandElt.setAttribute(XMLElements.ATT_OPERAND_NAME, ("".equals(name))?"val":name);
		operandElt.setAttribute(XMLElements.ATT_OPERAND_POSITION, ""+position);
		operandElt.setAttribute(XMLElements.ATT_OPERAND_TYPE, type);		
		
		return operandElt;
	}

}
