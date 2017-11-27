/*********************************************************************************************
 *
 * 'Operands.java, in plugin msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.precompiler.doc;

import java.util.ArrayList;

import msi.gama.precompiler.doc.utils.XMLElements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Operands implements IElement {

	Document doc;
	String classe;
	String content_type;
	String return_type;	
	String type;
	ArrayList<Operand> listOperand;
	
	public Operands(Document _doc){
		doc = _doc;
		listOperand = new ArrayList<Operand>();
	}
	
	public Operands(Document _doc, String _classe, String _content_type, String _return_type, String _type){
		this(_doc);
		classe = _classe;
		content_type = _content_type;
		return_type = _return_type;
		type = _type;
	}	
	
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
