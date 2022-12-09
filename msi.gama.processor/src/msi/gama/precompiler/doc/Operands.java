/*******************************************************************************************************
 *
 * Operands.java, in msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler.doc;

import java.util.ArrayList;

import msi.gama.precompiler.doc.utils.XMLElements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class Operands.
 */
public class Operands implements IElement {

	/** The doc. */
	Document doc;
	
	/** The classe. */
	String classe;
	
	/** The content type. */
	String content_type;
	
	/** The return type. */
	String return_type;	
	
	/** The type. */
	String type;
	
	/** The list operand. */
	ArrayList<Operand> listOperand;
	
	/**
	 * Instantiates a new operands.
	 *
	 * @param _doc the doc
	 */
	public Operands(Document _doc){
		doc = _doc;
		listOperand = new ArrayList<Operand>();
	}
	
	/**
	 * Instantiates a new operands.
	 *
	 * @param _doc the doc
	 * @param _classe the classe
	 * @param _content_type the content type
	 * @param _return_type the return type
	 * @param _type the type
	 */
	public Operands(Document _doc, String _classe, String _content_type, String _return_type, String _type){
		this(_doc);
		classe = _classe;
		content_type = _content_type;
		return_type = _return_type;
		type = _type;
	}	
	
	/**
	 * Adds the operand.
	 *
	 * @param op the op
	 */
	public void addOperand(Operand op){
		listOperand.add(op);
	}
	
	@Override
	public Element getElementDOM() {
		org.w3c.dom.Element operandsElt = doc.createElement(XMLElements.OPERANDS);
		
		operandsElt.setAttribute(XMLElements.ATT_OPERANDS_CLASS, classe);  
		operandsElt.setAttribute(XMLElements.ATT_OPERANDS_CONTENT_TYPE, content_type);
		operandsElt.setAttribute(XMLElements.ATT_OPERANDS_RETURN_TYPE, return_type);
		operandsElt.setAttribute(XMLElements.ATT_OPERANDS_TYPE, type);
		
		for(Operand op : listOperand){
			operandsElt.appendChild(op.getElementDOM());
		}
		
		return operandsElt;
	}

}
