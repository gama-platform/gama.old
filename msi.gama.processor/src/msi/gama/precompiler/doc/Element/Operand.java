package msi.gama.precompiler.doc.Element;

import msi.gama.precompiler.doc.utils.XMLElements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Operand implements IElement {

	Document doc;
	String name;
	int position;
	String type;
	
	public Operand(Document _doc){
		doc = _doc;
	}
	
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
