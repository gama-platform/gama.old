package msi.gama.precompiler.doc.Element;

import msi.gama.precompiler.GamlDocProcessor;
import msi.gama.precompiler.doc.utils.XMLElements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Operator implements IElement {

	Document doc;
	
	String category;
	String name;
	Operands operands;
	String documentation;
	
	public Operator(Document _doc){
		doc = _doc;
		operands = new Operands(_doc);
	}

	public Operator(Document _doc, String _category, String _name){
		this(_doc);
		category = _category;
		name = _name;
	}

	public Operator(Document _doc, String _category, String _name, String _documentation){
		this(_doc);
		category = _category;
		name = _name;
		documentation = _documentation;
	}
	
	public void setDocumentation(String d){
		documentation = d;
	}
	
	public void setOperands(String _classe, String _content_type, String _return_type, String _type){
		operands = 	new Operands(doc, _classe, _content_type, _return_type, _type);
	}
	
	public void addOperand(Operand op){
		operands.addOperand(op);
	}
	
	@Override
	public Element getElementDOM() {
		// TODO to finish
		org.w3c.dom.Element eltOp = doc.createElement(XMLElements.OPERATOR);
		// eltOp.setAttribute(XMLElements.ATT_OP_CATEGORY, category);
		eltOp.setAttribute(XMLElements.ATT_OP_ID, name);
		eltOp.setAttribute(XMLElements.ATT_OP_NAME, name);	
		eltOp.setAttribute(XMLElements.ATT_ALPHABET_ORDER, GamlDocProcessor.getAlphabetOrder(name));

		// Categories
		org.w3c.dom.Element categoriesElt = doc.createElement(XMLElements.OPERATOR_CATEGORIES);
		org.w3c.dom.Element catElt = doc.createElement(XMLElements.CATEGORY);
		catElt.setAttribute(XMLElements.ATT_CAT_ID, category);
		categoriesElt.appendChild(catElt);
		
		eltOp.appendChild(categoriesElt);
		
		// Combinaison IO
		org.w3c.dom.Element combiElt = doc.createElement(XMLElements.COMBINAISON_IO);
		combiElt.appendChild(operands.getElementDOM());
		
		eltOp.appendChild(combiElt);			
	
		// Documentation
		org.w3c.dom.Element docElt = doc.createElement(XMLElements.DOCUMENTATION);
		org.w3c.dom.Element resultElt = doc.createElement(XMLElements.RESULT);
		resultElt.setTextContent(documentation);

		docElt.appendChild(resultElt);
		eltOp.appendChild(docElt);
				
		return eltOp;
	}
}
