/*********************************************************************************************
 * 
 *
 * 'DocProcessorAnnotations.java', in plugin 'msi.gama.processor', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit http://gama-platform.googlecode.com for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.precompiler.doc;

import java.util.ArrayList;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.doc.utils.TypeConverter;
import msi.gama.precompiler.doc.utils.XMLElements;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class DocProcessorAnnotations {
	
	public static final String PREFIX_CONSTANT = "#";
	
	public static org.w3c.dom.Element getDocElt(doc docAnnot, Document doc, Messager mes, String eltName, TypeConverter tc, ExecutableElement e){ 
		return DocProcessorAnnotations.getDocElt(docAnnot, doc, null, mes, eltName, tc, e);
	}
	
	public static org.w3c.dom.Element getDocElt(doc[] docAnnotTab, Document doc, Messager mes, String eltName, TypeConverter tc, ExecutableElement e){ // e.getSimpleName() 
		if(docAnnotTab == null || docAnnotTab.length == 0){
			return DocProcessorAnnotations.getDocElt(null, doc, null, mes, eltName, tc, e);
		} else {
			return DocProcessorAnnotations.getDocElt(docAnnotTab[0], doc, null, mes, eltName, tc, e);
		}
	}
	
	public static org.w3c.dom.Element getDocElt(doc docAnnot, Document doc, org.w3c.dom.Element docElement, Messager mes, String eltName, TypeConverter tc, ExecutableElement e){ // e.getSimpleName() 
		org.w3c.dom.Element docElt = docElement;

		if ( docAnnot == null ) {
			mes.printMessage(Kind.ERROR, "The element __" + eltName + "__ is not documented.");
		} else {	
			if(docElt == null){
				docElt = doc.createElement(XMLElements.DOCUMENTATION);
			} 
	
			// Parse result
			String value = docAnnot.value();
			boolean masterDoc = docAnnot.masterDoc();
			if(value != ""){
				if(docElt.getElementsByTagName(XMLElements.RESULT).getLength() != 0){
					org.w3c.dom.Element resultElt = (org.w3c.dom.Element) docElt.getElementsByTagName(XMLElements.RESULT).item(0);
					if(("true".equals(resultElt.getAttribute(XMLElements.ATT_RES_MASTER)) && masterDoc) || (!("true".equals(resultElt.getAttribute(XMLElements.ATT_RES_MASTER))) && !masterDoc)){ 
						resultElt.setTextContent(resultElt.getTextContent() + value);							
					} else if (!resultElt.hasAttribute(XMLElements.ATT_RES_MASTER) && masterDoc) {
						resultElt.setTextContent(value);			
						resultElt.setAttribute(XMLElements.ATT_RES_MASTER, "true");
					} 
					// eltResult.setTextContent(eltResult.getTextContent() + value);
					//	docElt.getElementsByTagName("result").item(0)
					//		.setTextContent(docElt.getElementsByTagName("result").item(0).getTextContent() + value);					
				} else {
					org.w3c.dom.Element resultElt = doc.createElement(XMLElements.RESULT);
					resultElt.setTextContent(value);
					if(masterDoc) {resultElt.setAttribute(XMLElements.ATT_RES_MASTER, "true");	}				
					docElt.appendChild(resultElt);
				}
			}
			
			// Parse comment
			String comment = docAnnot.comment();
			if(!"".equals(comment)){
				if(docElt.getElementsByTagName(XMLElements.COMMENT).getLength() != 0){
					docElt.getElementsByTagName(XMLElements.COMMENT).item(0)
						.setTextContent(docElt.getElementsByTagName(XMLElements.COMMENT).item(0).getTextContent() + comment);					
				} else {
					org.w3c.dom.Element commentElt = doc.createElement(XMLElements.COMMENT);
					commentElt.setTextContent(comment);
					docElt.appendChild(commentElt);
				}
			}
		
			// Parse: seeAlso
			org.w3c.dom.Element seeAlsoElt;
			if(docElt.getElementsByTagName(XMLElements.SEEALSO).getLength() != 0){
				seeAlsoElt = (org.w3c.dom.Element) docElt.getElementsByTagName(XMLElements.SEEALSO).item(0);				
			} else {
				seeAlsoElt = doc.createElement(XMLElements.SEEALSO);
			}	
			for ( String see : docAnnot.see() ) {
				NodeList nLSee = seeAlsoElt.getElementsByTagName(XMLElements.SEE);
				int i = 0;
				boolean seeAlreadyInserted = false;
				while(i < nLSee.getLength() && !seeAlreadyInserted){
					if(((org.w3c.dom.Element) nLSee.item(i)).getAttribute(XMLElements.ATT_SEE_ID).equals(see) ){
						seeAlreadyInserted = true;
					}
					i++;
				}
				if(!seeAlreadyInserted){
					org.w3c.dom.Element seesElt = doc.createElement(XMLElements.SEE);
					seesElt.setAttribute(XMLElements.ATT_SEE_ID, see);
					seeAlsoElt.appendChild(seesElt);
				}
			}
			if(docAnnot.see().length != 0) {docElt.appendChild(seeAlsoElt);}
			
			// Parse: usages
			
			org.w3c.dom.Element usagesElt;
			org.w3c.dom.Element usagesExampleElt;
			org.w3c.dom.Element usagesNoExampleElt;
			if(docElt.getElementsByTagName(XMLElements.USAGES).getLength() != 0){
				usagesElt = (org.w3c.dom.Element) docElt.getElementsByTagName(XMLElements.USAGES).item(0);				
			} else {
				usagesElt = doc.createElement(XMLElements.USAGES);
			}	
			if(docElt.getElementsByTagName(XMLElements.USAGES_EXAMPLES).getLength() != 0){
				usagesExampleElt = (org.w3c.dom.Element) docElt.getElementsByTagName(XMLElements.USAGES_EXAMPLES).item(0);				
			} else {
				usagesExampleElt = doc.createElement(XMLElements.USAGES_EXAMPLES);
			}	
			if(docElt.getElementsByTagName(XMLElements.USAGES_NO_EXAMPLE).getLength() != 0){
				usagesNoExampleElt = (org.w3c.dom.Element) docElt.getElementsByTagName(XMLElements.USAGES_NO_EXAMPLE).item(0);				
			} else {
				usagesNoExampleElt = doc.createElement(XMLElements.USAGES_NO_EXAMPLE);
			}			
			int numberOfUsages = 0;			
			int numberOfUsagesWithExamplesOnly = 0;
			int numberOfUsagesWithoutExample = 0;
			for ( usage usage : docAnnot.usages() ) {
				org.w3c.dom.Element usageElt = doc.createElement(XMLElements.USAGE);			
				
				// Among the usages, we consider the ones without value
				if ("".equals(usage.value())) {
					numberOfUsagesWithExamplesOnly++;

					org.w3c.dom.Element examplesUsageElt = DocProcessorAnnotations.getExamplesElt(usage.examples(), doc, e, tc);
					usageElt.appendChild(examplesUsageElt);
					usagesExampleElt.appendChild(usageElt);				
				} 
				// Among the usages, we consider the ones with only the value				
				else if(usage.examples().length == 0){
					numberOfUsagesWithoutExample++;
					
					usageElt.setAttribute(XMLElements.ATT_USAGE_DESC, usage.value());
					usagesNoExampleElt.appendChild(usageElt);						
				}
				// Otherwise, when we have both value and examples
				else {
					numberOfUsages++;	
					
					usageElt.setAttribute(XMLElements.ATT_USAGE_DESC, usage.value());
					org.w3c.dom.Element examplesUsageElt = DocProcessorAnnotations.getExamplesElt(usage.examples(), doc, e, tc);
					usageElt.appendChild(examplesUsageElt);
					usagesElt.appendChild(usageElt);					
				}
			}
			
			// Let's continue with examples and special cases
			//  - special cases are equivalent to usage without examples
			//  - examples are equivalent to usage with only examples
			// Parse examples	
			if (docAnnot.examples().length != 0) {
				org.w3c.dom.Element usageExElt = doc.createElement(XMLElements.USAGE);
				// org.w3c.dom.Element examplesElt = doc.createElement(XMLElements.EXAMPLES);	
				
				org.w3c.dom.Element examplesElt = DocProcessorAnnotations.getExamplesElt(docAnnot.examples(), doc, e, tc);
				// usageElt.appendChild(examplesUsageElt);
				
				/* for ( example example : docAnnot.examples() ) {
					org.w3c.dom.Element exampleElt = doc.createElement(XMLElements.EXAMPLE);
					// TODO: 
					// org.w3c.dom.Element examplesUsageElt = DocProcessorAnnotations.getExamplesElt(usage.examples(), doc, e, tc);
					// usageElt.appendChild(examplesUsageElt);
					exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_CODE, example);
					exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_TEST_ONLY, "false");
					exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_EXECUTABLE, "true");
					
					examplesElt.appendChild(exampleElt);	

					numberOfUsagesWithExamplesOnly++;				
				}
				*/
				numberOfUsagesWithExamplesOnly += docAnnot.examples().length; 
				usageExElt.appendChild(examplesElt);
				usagesExampleElt.appendChild(usageExElt);				
			}
							
			
			// Parse specialCases
			for ( String cases : docAnnot.special_cases() ) {
				if ( !"".equals(cases) ) {
					org.w3c.dom.Element caseElt = doc.createElement(XMLElements.USAGE);
					caseElt.setAttribute(XMLElements.ATT_USAGE_DESC, cases);
					usagesNoExampleElt.appendChild(caseElt);
					numberOfUsagesWithoutExample++;
				}
			}
			
			if(numberOfUsagesWithExamplesOnly != 0) {docElt.appendChild(usagesExampleElt);}
			if(numberOfUsagesWithoutExample != 0) {docElt.appendChild(usagesNoExampleElt);}
			if(numberOfUsages != 0) {docElt.appendChild(usagesElt);}
			
//			public static @interface usages {
//				String value() default "";
//				String[] examples() default {};}
			
		}
		return docElt;
	}

	public static org.w3c.dom.Element getExamplesElt(example[] examples, Document doc, ExecutableElement e, TypeConverter tc){ 
		org.w3c.dom.Element examplesElt = doc.createElement(XMLElements.EXAMPLES);			
		for ( example example : examples ) { 
			examplesElt.appendChild(getExampleElt(example, doc, e, tc));
		}
		return examplesElt;		
	}	
	
	public static org.w3c.dom.Element getExampleElt(example example, Document doc, ExecutableElement e, TypeConverter tc){ 
		org.w3c.dom.Element exampleElt = doc.createElement(XMLElements.EXAMPLE);			
		exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_CODE, example.value());
		if(!"".equals(example.var())) {exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_VAR, example.var());}
		if(!"".equals(example.equals())) {exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_EQUALS, example.equals());}
		if(!"".equals(example.isNot())) {exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_NOT, example.isNot());}
		if(!"".equals(example.raises())) {exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_RAISES, example.raises());}
		exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_TEST_ONLY, ""+example.isTestOnly());
		exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_EXECUTABLE, ""+example.isExecutable());
		if(!example.isExecutable()) {
			exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_TESTABLE, "false");					
		} else {
			exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_TESTABLE, ""+example.test());					
		}
		if(!"".equals(example.returnType())) {
			exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_TYPE, example.returnType());
		} else {
			if (e != null){
				exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_TYPE, tc.getProperType(e.getReturnType().toString()));
			}
		}
		return exampleElt;
//		public static @interface example {
//			String value() default "";
//			String var() default "";			
//			String equals() default "";
//			String isnot() default "";
//			String raises() default "";	
//			boolean isTestOnly() default false;		
// 			boolean isExecutable() default true;		
//		}		
	}		

	public static org.w3c.dom.Element getConstantElt(constant constant, Document doc, Element e, Messager mes, TypeConverter tc){ 
		org.w3c.dom.Element constantElt = doc.createElement(XMLElements.CONSTANT);		
		
		constantElt.setAttribute(XMLElements.ATT_CST_NAME, PREFIX_CONSTANT+constant.value());
		constantElt.setAttribute(XMLElements.ATT_CST_VALUE, ((VariableElement)e).getConstantValue().toString());
		
		String names= "";
		for(String n : constant.altNames()){names=("".equals(names)) ? PREFIX_CONSTANT+n : names+","+PREFIX_CONSTANT+n;}
		if(!"".equals(names)) constantElt.setAttribute(XMLElements.ATT_CST_NAMES, names);
		
		constantElt.appendChild(getCategories(e, doc, doc.createElement(XMLElements.CATEGORIES),tc));
		
		org.w3c.dom.Element docConstantElt = getDocElt(constant.doc(), doc, mes, e.getSimpleName().toString(), null, null);
		if(docConstantElt != null) {constantElt.appendChild(docConstantElt);}

		return constantElt;
	}
	
	public static org.w3c.dom.Element getActionElt(action actionAnnot, Document doc, Messager mes, Element e, TypeConverter tc){
		if((!(e instanceof ExecutableElement)) || (actionAnnot == null)){
			return null;
		}
		
		ExecutableElement eltMethod = (ExecutableElement) e;
		org.w3c.dom.Element actionElt = doc.createElement(XMLElements.ACTION);
		actionElt.setAttribute(XMLElements.ATT_ACTION_NAME, actionAnnot.name());
		actionElt.setAttribute(XMLElements.ATT_ACTION_RETURNTYPE, tc.getProperType(eltMethod.getReturnType().toString())); 

		org.w3c.dom.Element argsElt = doc.createElement(XMLElements.ARGS);
		for (arg eltArg : actionAnnot.args()){
			org.w3c.dom.Element argElt = doc.createElement(XMLElements.ARG);
			argElt.setAttribute(XMLElements.ATT_ARG_NAME, eltArg.name());
			
			String tabType = "";
			for(int i = 0; i < eltArg.type().length ; i++){
				// tabType = tabType + ((i < eltArg.type().length - 1) ? typeStringFromIType.get(eltArg.type()[i]) + "," : typeStringFromIType.get(eltArg.type()[i]));
				tabType = tabType + ((i < eltArg.type().length - 1) ? tc.getTypeString(eltArg.type()[i]) + "," : tc.getTypeString(eltArg.type()[i]));
			}
			argElt.setAttribute(XMLElements.ATT_ARG_TYPE, tabType);
			argElt.setAttribute(XMLElements.ATT_ARG_OPTIONAL, ""+eltArg.optional());
			org.w3c.dom.Element docEltArg = 
					DocProcessorAnnotations.getDocElt(eltArg.doc(), doc, mes, "Arg " + eltArg.name() + " from " + eltMethod.getSimpleName(), tc, null);
			if(docEltArg != null){
				argElt.appendChild(docEltArg);
			}
			
			argsElt.appendChild(argElt);
		}
		actionElt.appendChild(argsElt);		
		
		org.w3c.dom.Element docEltAction = DocProcessorAnnotations.getDocElt(actionAnnot.doc(), doc, mes, eltMethod.getSimpleName().toString(), tc, null);
		if(docEltAction != null){
			actionElt.appendChild(docEltAction);
		}
		
		return actionElt;
	}
	
	public static org.w3c.dom.Element getFacetsElt(facets facetsAnnot, Document doc, Messager mes, String statName, TypeConverter tc){
		if(facetsAnnot == null){
			return null;
		}
		
		org.w3c.dom.Element facetsElt = doc.createElement(XMLElements.FACETS);

		for ( facet f : facetsAnnot.value() ) {
			org.w3c.dom.Element facetElt = doc.createElement(XMLElements.FACET);
			facetElt.setAttribute(XMLElements.ATT_FACET_NAME, f.name());
			// TODO : check several types
			String facetTypes = tc.getTypeString(f.type()[0]);
			for(int i = 1 ; i < f.type().length ; i ++) {facetTypes = facetTypes + ", " + tc.getTypeString(f.type()[i]); }
			facetElt.setAttribute(XMLElements.ATT_FACET_TYPE, facetTypes );
			facetElt.setAttribute(XMLElements.ATT_FACET_OPTIONAL, "" + f.optional());
			facetElt.setAttribute(XMLElements.ATT_FACET_OMISSIBLE,
				f.name().equals(facetsAnnot.omissible()) ? "true" : "false");
			org.w3c.dom.Element docFacetElt = 
					DocProcessorAnnotations.getDocElt(f.doc(), doc, mes, "Facet " + f.name() + " from Statement" + statName, tc, null);
			if(docFacetElt != null){
				facetElt.appendChild(docFacetElt);
			}
			
			facetsElt.appendChild(facetElt);
		}
		return facetsElt;
	}
	
	public static org.w3c.dom.Element getInsideElt(inside insideAnnot, Document doc, TypeConverter tc){
		if(insideAnnot == null){
			return null;
		}
		
		org.w3c.dom.Element insideElt = doc.createElement(XMLElements.INSIDE);
		
		org.w3c.dom.Element symbolsElt = doc.createElement(XMLElements.SYMBOLS);
		for(String sym : insideAnnot.symbols()){
			org.w3c.dom.Element symElt = doc.createElement(XMLElements.SYMBOL);
			symElt.setTextContent(sym);
			symbolsElt.appendChild(symElt);
		}
		insideElt.appendChild(symbolsElt);

		org.w3c.dom.Element kindsElt = doc.createElement(XMLElements.KINDS);
		for(int kind : insideAnnot.kinds()){
			org.w3c.dom.Element kindElt = doc.createElement(XMLElements.KIND);
			kindElt.setTextContent(tc.getSymbolKindStringFromISymbolKind(kind));
			kindsElt.appendChild(kindElt);
		}
		insideElt.appendChild(kindsElt);

		
		return insideElt;
	}

	public static org.w3c.dom.Element getOperatorElement(final org.w3c.dom.Element operators, final String eltName) {
		NodeList nL = operators.getElementsByTagName(XMLElements.OPERATOR);
		int i = 0;
		boolean found = false;
		while (!found && i < nL.getLength()) {
			org.w3c.dom.Element elt = (org.w3c.dom.Element) nL.item(i);
			if ( eltName.equals(elt.getAttribute(XMLElements.ATT_OP_ID)) ) { return elt; }
			i++;
		}
		return null;
	}
	
	
	public static org.w3c.dom.Element getCategories(final Element e, final Document doc, org.w3c.dom.Element categoriesElt, TypeConverter tc){	
		ArrayList<String> categories = new ArrayList<String>();
		String[] categoriesTab = null;
		NodeList nL = categoriesElt.getElementsByTagName(XMLElements.CATEGORY);
		for(int i = 0; i < nL.getLength() ; i++){
			categories.add(((org.w3c.dom.Element) nL.item(i)).getAttribute(XMLElements.ATT_CAT_ID));
		}
		
		// To be able to deal with various annotations....
		if(e.getAnnotation(operator.class) != null){
			categoriesTab = e.getAnnotation(operator.class).category();			
		} else if (e.getAnnotation(constant.class) != null) {
			categoriesTab = e.getAnnotation(constant.class).category();				
		} 
		
		if((e.getAnnotation(operator.class) != null && e.getAnnotation(operator.class).category().length > 0) || 
				(e.getAnnotation(constant.class) != null && e.getAnnotation(constant.class).category().length > 0)) {
			for(String categoryName : categoriesTab){
				if(!categories.contains(categoryName)){
					categories.add(categoryName);
					
					org.w3c.dom.Element catElt = doc.createElement(XMLElements.CATEGORY);
					catElt.setAttribute(XMLElements.ATT_CAT_ID, categoryName);
					categoriesElt.appendChild(catElt);
				}
			}			
		} else {
			if(!categories.contains(tc.getProperCategory(e.getEnclosingElement().getSimpleName().toString()))) {
				org.w3c.dom.Element catElt = doc.createElement(XMLElements.CATEGORY);
				catElt.setAttribute(XMLElements.ATT_CAT_ID, tc.getProperCategory(e.getEnclosingElement().getSimpleName().toString()));
				categoriesElt.appendChild(catElt);
			}			
		}
		
//		if(e.getAnnotation(operator.class) != null && e.getAnnotation(operator.class).category().length > 0) {
//			for(String categoryName : e.getAnnotation(operator.class).category()){
//				if(!categories.contains(categoryName)){
//					categories.add(categoryName);
//					
//					org.w3c.dom.Element catElt = doc.createElement(XMLElements.CATEGORY);
//					catElt.setAttribute(XMLElements.ATT_CAT_ID, categoryName);
//					categoriesElt.appendChild(catElt);
//				}
//			}
//		} else {
//			if(!categories.contains(tc.getProperCategory(e.getEnclosingElement().getSimpleName().toString()))) {
//				org.w3c.dom.Element catElt = doc.createElement(XMLElements.CATEGORY);
//				catElt.setAttribute(XMLElements.ATT_CAT_ID, tc.getProperCategory(e.getEnclosingElement().getSimpleName().toString()));
//				categoriesElt.appendChild(catElt);
//			}
//		}		
		return categoriesElt;
	}
	
	public static org.w3c.dom.Element getCategories(final Element e, final Document doc, TypeConverter tc){		
		org.w3c.dom.Element categoriesElt = doc.createElement(XMLElements.OPERATORS_CATEGORIES);
		
		return getCategories(e,doc,categoriesElt, tc);
	}	
}
