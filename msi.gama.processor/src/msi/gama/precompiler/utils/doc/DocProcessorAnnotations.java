package msi.gama.precompiler.utils.doc;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic.Kind;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;

import org.w3c.dom.Document;

public class DocProcessorAnnotations {
	
	public static org.w3c.dom.Element getDocElt(doc docAnnot, Document doc, Messager mes, String eltName, TypeConverter tc){ 
		return DocProcessorAnnotations.getDocElt(docAnnot, doc, null, mes, eltName, tc);
	}
	
	public static org.w3c.dom.Element getDocElt(doc[] docAnnotTab, Document doc, Messager mes, String eltName, TypeConverter tc){ // e.getSimpleName() 
		if(docAnnotTab == null || docAnnotTab.length == 0){
			return DocProcessorAnnotations.getDocElt(null, doc, null, mes, eltName, tc);
		} else {
			return DocProcessorAnnotations.getDocElt(docAnnotTab[0], doc, null, mes, eltName,tc );
		}
	}
	
	public static org.w3c.dom.Element getDocElt(doc docAnnot, Document doc, org.w3c.dom.Element docElement, Messager mes, String eltName, TypeConverter tc){ // e.getSimpleName() 
		org.w3c.dom.Element docElt = docElement;

		if ( docAnnot == null ) {
			mes.printMessage(Kind.ERROR, "The element __" + eltName + "__ is not documented.");
		} else {	
			if(docElt == null){
				docElt = doc.createElement("documentation");
			} 
	
			// Parse result
			String value = docAnnot.value();
			if(value != ""){
				if(docElt.getElementsByTagName("result").getLength() != 0){
					docElt.getElementsByTagName("result").item(0)
						.setTextContent(docElt.getElementsByTagName("result").item(0).getTextContent() + value);					
				} else {
					org.w3c.dom.Element resultElt = doc.createElement("result");
					resultElt.setTextContent(value);
					docElt.appendChild(resultElt);
				}
			}
			
			// Parse comment
			String comment = docAnnot.comment();
			if(!"".equals(comment)){
				if(docElt.getElementsByTagName("comment").getLength() != 0){
					docElt.getElementsByTagName("comment").item(0)
						.setTextContent(docElt.getElementsByTagName("comment").item(0).getTextContent() + comment);					
				} else {
					org.w3c.dom.Element commentElt = doc.createElement("comment");
					commentElt.setTextContent(comment);
					docElt.appendChild(commentElt);
				}
			}
	
			// Parse specialCases
			org.w3c.dom.Element specialCasesElt;
			if(docElt.getElementsByTagName("specialCases").getLength() != 0){
				specialCasesElt = (org.w3c.dom.Element) docElt.getElementsByTagName("specialCases").item(0);				
			} else {
				specialCasesElt = doc.createElement("specialCases");
			}
			for ( String cases : docAnnot.special_cases() ) {
				if ( !"".equals(cases) ) {
					org.w3c.dom.Element caseElt = doc.createElement("case");
					caseElt.setAttribute("item", cases);
					specialCasesElt.appendChild(caseElt);
				}
			}
			if(docAnnot.special_cases().length != 0) {docElt.appendChild(specialCasesElt);}
	
			// Parse examples
			org.w3c.dom.Element examplesElt;
			if(docElt.getElementsByTagName("examples").getLength() != 0){
				examplesElt = (org.w3c.dom.Element) docElt.getElementsByTagName("examples").item(0);				
			} else {
				examplesElt = doc.createElement("examples");
			}	
			for ( String example : docAnnot.examples() ) {
				org.w3c.dom.Element exampleElt = doc.createElement("example");
				exampleElt.setAttribute("code", example);
				examplesElt.appendChild(exampleElt);
			}
			if(docAnnot.examples().length != 0) {docElt.appendChild(examplesElt);}
			
			// Parse: seeAlso
			org.w3c.dom.Element seeAlsoElt;
			if(docElt.getElementsByTagName("seeAlso").getLength() != 0){
				seeAlsoElt = (org.w3c.dom.Element) docElt.getElementsByTagName("seeAlso").item(0);				
			} else {
				seeAlsoElt = doc.createElement("seeAlso");
			}	
			for ( String see : docAnnot.see() ) {
				org.w3c.dom.Element seesElt = doc.createElement("see");
				seesElt.setAttribute("id", see);
				seeAlsoElt.appendChild(seesElt);
			}
			if(docAnnot.see().length != 0) {docElt.appendChild(seeAlsoElt);}
		}
		return docElt;
	}

	public static org.w3c.dom.Element getActionElt(action actionAnnot, Document doc, Messager mes, Element e, TypeConverter tc){
		if((!(e instanceof ExecutableElement)) || (actionAnnot == null)){
			return null;
		}
		
		ExecutableElement eltMethod = (ExecutableElement) e;
		org.w3c.dom.Element actionElt = doc.createElement("action");
		actionElt.setAttribute("name", actionAnnot.name());
		actionElt.setAttribute("returnType", tc.getProperType(eltMethod.getReturnType().toString())); 

		org.w3c.dom.Element argsElt = doc.createElement("args");
		for (arg eltArg : actionAnnot.args()){
			org.w3c.dom.Element argElt = doc.createElement("arg");
			argElt.setAttribute("name", eltArg.name());
			
			String tabType = "";
			for(int i = 0; i < eltArg.type().length ; i++){
				// tabType = tabType + ((i < eltArg.type().length - 1) ? typeStringFromIType.get(eltArg.type()[i]) + "," : typeStringFromIType.get(eltArg.type()[i]));
				tabType = tabType + ((i < eltArg.type().length - 1) ? tc.getTypeString(eltArg.type()[i]) + "," : tc.getTypeString(eltArg.type()[i]));
			}
			argElt.setAttribute("type", tabType);
			argElt.setAttribute("optional", ""+eltArg.optional());
			org.w3c.dom.Element docEltArg = 
					DocProcessorAnnotations.getDocElt(eltArg.doc(), doc, mes, "Arg " + eltArg.name() + " from " + eltMethod.getSimpleName(), tc);
			if(docEltArg != null){
				argElt.appendChild(docEltArg);
			}
			
			argsElt.appendChild(argElt);
		}
		actionElt.appendChild(argsElt);		
		
		org.w3c.dom.Element docEltAction = DocProcessorAnnotations.getDocElt(actionAnnot.doc(), doc, mes, eltMethod.getSimpleName().toString(), tc);
		if(docEltAction != null){
			actionElt.appendChild(docEltAction);
		}
		
		return actionElt;
	}
	
	public static org.w3c.dom.Element getFacetsElt(facets facetsAnnot, Document doc, Messager mes, String statName, TypeConverter tc){
		if(facetsAnnot == null){
			return null;
		}
		
		org.w3c.dom.Element facetsElt = doc.createElement("facets");

		for ( facet f : facetsAnnot.value() ) {
			org.w3c.dom.Element facetElt = doc.createElement("facet");
			facetElt.setAttribute("name", f.name());
			// TODO : check several types
			facetElt.setAttribute("type", String.valueOf(f.type()[0]));
			facetElt.setAttribute("optional", "" + f.optional());
			facetElt.setAttribute("omissible",
				f.name().equals(facetsAnnot.omissible()) ? "true" : "false");
			org.w3c.dom.Element docFacetElt = 
					DocProcessorAnnotations.getDocElt(f.doc(), doc, mes, "Facet " + f.name() + " from Statement" + statName, tc);
			if(docFacetElt != null){
				facetElt.appendChild(docFacetElt);
			}
			
			facetsElt.appendChild(facetElt);
		}
		return facetsElt;
	}
	
	
	public static org.w3c.dom.Element getInsideElt(inside insideAnnot, Document doc){
		if(insideAnnot == null){
			return null;
		}
		
		org.w3c.dom.Element insideElt = doc.createElement("inside");
		
		org.w3c.dom.Element symbolsElt = doc.createElement("symbols");
		for(String sym : insideAnnot.symbols()){
			org.w3c.dom.Element symElt = doc.createElement("symbol");
			symElt.setTextContent(sym);
			symbolsElt.appendChild(symElt);
		}
		insideElt.appendChild(symbolsElt);

		org.w3c.dom.Element kindsElt = doc.createElement("kinds");
		for(int kind : insideAnnot.kinds()){
			org.w3c.dom.Element kindElt = doc.createElement("kind");
			kindElt.setTextContent(""+kind);
			kindsElt.appendChild(kindElt);
		}
		insideElt.appendChild(kindsElt);

		
		return insideElt;
	}
}
