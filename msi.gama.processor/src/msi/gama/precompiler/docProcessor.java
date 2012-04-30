/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.precompiler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import msi.gama.precompiler.GamlAnnotations.gamlDoc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


@SupportedAnnotationTypes({ "msi.gama.precompiler.GamlAnnotations.*" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class docProcessor {

	ProcessingEnvironment processingEnv;
	
	public docProcessor(ProcessingEnvironment procEnv){
		processingEnv = procEnv;
	}
	
	public void processDocXML(final RoundEnvironment env, Writer out) {

        DocumentBuilder docBuilder = null;
        
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch(ParserConfigurationException e) {
            System.err.println("Impossible de créer un DocumentBuilder.");
            System.exit(1);
        }
         
        Document doc = docBuilder.newDocument();
        
        Set<? extends Element> setRoot = env.getRootElements();        
        org.w3c.dom.Element root = doc.createElement("DOC"+setRoot.size());

        //////////////////////////////////////////////////
        ///// Parsing of Operators 
        Set<? extends ExecutableElement> setOperators = 
        		(Set<? extends ExecutableElement>) env.getElementsAnnotatedWith(operator.class);
        root.appendChild(this.processDocXMLOperators(setOperators, doc));	
        
        //////////////////////////////////////////////////
        ///// Parsing of Skills 
        Set<? extends Element> setSkills = 
        		(Set<? extends Element>) env.getElementsAnnotatedWith(skill.class);
        root.appendChild(this.processDocXMLSkills(setSkills, doc));	
 
        //////////////////////////////////////////////////
        ///// Parsing of Species        
        Set<? extends Element> setSpecies = 
        		(Set<? extends Element>) env.getElementsAnnotatedWith(species.class);
        root.appendChild(this.processDocXMLSpecies(setSpecies, doc));	        
        
        ////////////////////////
        // Final step:      
        doc.appendChild(root);
        
		//////////////////////////////////////////////////
		
        try {
		// Création de la source DOM
		DOMSource source = new DOMSource(doc);
		
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(source, result);
		String stringResult = writer.toString(); 
		
		final PrintWriter docWriterXML = new PrintWriter(out);
		docWriterXML.append(stringResult).println("");
		docWriterXML.close();		
		
		} catch (Exception e) {
			throw new NullPointerException("Erreur dans le Processor ");
		}
	}

	private static org.w3c.dom.Element getOperatorElement(org.w3c.dom.Element operators, String eltName){
		NodeList nL = operators.getElementsByTagName("operator");		
		int i = 0;
		boolean found = false;
		while(!found && i < nL.getLength()){
			org.w3c.dom.Element elt = (org.w3c.dom.Element) nL.item(i);
			if(eltName.equals(elt.getAttribute("id")))
			{ return elt;}
			else {i++;}
		}		
	return null;
	}

	private org.w3c.dom.Element processDocXMLOperators(
			Set<? extends ExecutableElement> set, Document doc) {
        org.w3c.dom.Element operators = doc.createElement("Operators"+set.size());
		Messager mes = processingEnv.getMessager();

		for ( ExecutableElement e : set ) {
			List<? extends VariableElement> args = e.getParameters();
			Set<Modifier> m = e.getModifiers();
			boolean isStatic = m.contains(Modifier.STATIC);
			int arity = 0;
			boolean firstInstanceOperator = false;

			// Look for an already parsed operator with the same name 
			org.w3c.dom.Element operator = getOperatorElement(operators, e.getAnnotation(operator.class).value()[0]);
			if(operator == null) {
				operator = doc.createElement("operator");
				firstInstanceOperator = true;}
								
			if(firstInstanceOperator) {
				// if("".equals(operator.getAttribute("id"))){
				operator.setAttribute("id", e.getAnnotation(operator.class).value()[0]);
				operator.setAttribute("name", e.getAnnotation(operator.class).value()[0]);				
				// }
				operator.setAttribute("category", e.getEnclosingElement().getSimpleName().toString());
		
				// Parse the alternative names of the operator
				org.w3c.dom.Element alternativeNameElt; 
				if(operator.getElementsByTagName("alternativeName").getLength() == 0) {
					alternativeNameElt = doc.createElement("alternativeName");
				}
				else {
					alternativeNameElt = (org.w3c.dom.Element) operator.getElementsByTagName("alternativeName").item(0);	
				}
				for(String names : e.getAnnotation(operator.class).value()){
					if(! "".equals(names) && (!names.equals(e.getAnnotation(operator.class).value()[0]))){
						org.w3c.dom.Element nameElt = doc.createElement("altName"); 
						nameElt.setAttribute("name", names);	
						alternativeNameElt.appendChild(nameElt);
					}
				}
				operator.appendChild(alternativeNameElt);	
			}
			
			// Parse the combinaison operands / result
			org.w3c.dom.Element combinaisonOpResElt; 
			if(operator.getElementsByTagName("combinaisonIO").getLength() == 0) {
				combinaisonOpResElt = doc.createElement("combinaisonIO");
			}
			else {
				combinaisonOpResElt = (org.w3c.dom.Element) operator.getElementsByTagName("combinaisonIO").item(0);	
			}
			
			org.w3c.dom.Element operands = doc.createElement("operands");	        
			operands.setAttribute("returnType", e.getReturnType().toString());
			operands.setAttribute("contentType", ""+e.getAnnotation(operator.class).content_type());
			operands.setAttribute("type", ""+e.getAnnotation(operator.class).type());
	        
	        if(!isStatic){
				org.w3c.dom.Element operand = doc.createElement("operand");
		        operand.setAttribute("type", e.getEnclosingElement().asType().toString());	
		        operand.setAttribute("position", ""+arity);	
		        arity++;
		        operand.setAttribute("name", e.getEnclosingElement().asType().toString().toLowerCase());
		        operands.appendChild(operand);	        	
	        }
	        if(args.size() > 0) {
		        int first_index = (args.get(0).asType().toString().contains("IScope") ) ? 1 : 0;
		        for(int i = first_index ; i <= args.size() -1; i++){
					org.w3c.dom.Element operand = doc.createElement("operand");
			        operand.setAttribute("type", args.get(i).asType().toString());	
			        operand.setAttribute("position", ""+arity);		
			        arity++;
			        operand.setAttribute("name", args.get(i).getSimpleName().toString());
			        operands.appendChild(operand);		        	
		        }
	        }	        	
	        // operator.setAttribute("arity", ""+arity);
	        combinaisonOpResElt.appendChild(operands);	
	        operator.appendChild(combinaisonOpResElt);
	        
	        ///////////////////////////////////////////////////////
	        // Parsing of the documentation
			gamlDoc docAnnot = e.getAnnotation(gamlDoc.class);

			if(docAnnot == null) {
				mes.printMessage(Kind.ERROR, "The operator __" + 
						e.getAnnotation(operator.class).value()[0] + "__ is not documented.");
			}
			else {
				boolean firstdocElt = true;				
				org.w3c.dom.Element docElt; //= doc.createElement("documentation"); 
				
				if(operator.getElementsByTagName("documentation").getLength() == 0) {
					docElt = doc.createElement("documentation");
					firstdocElt = true;
				}
				else {
					docElt = (org.w3c.dom.Element) operator.getElementsByTagName("documentation").item(0);	
					firstdocElt = false;
				}				
				
				if(firstdocElt){
					org.w3c.dom.Element resultElt = doc.createElement("result"); 
					resultElt.setTextContent(docAnnot.result());
					docElt.appendChild(resultElt);
	
					org.w3c.dom.Element commentElt = doc.createElement("comment"); 
					commentElt.setTextContent(docAnnot.comment());
					docElt.appendChild(commentElt);
				}
				else {
					docElt.getElementsByTagName("result").item(0).setTextContent(
							docElt.getElementsByTagName("result").item(0).getTextContent() + docAnnot.result());
					docElt.getElementsByTagName("comment").item(0).setTextContent(
							docElt.getElementsByTagName("comment").item(0).getTextContent() + docAnnot.comment());					
				}
	
				org.w3c.dom.Element specialCasesElt; 
				if(firstdocElt) {
					specialCasesElt = doc.createElement("specialCases"); 
				}
				else {
					specialCasesElt = (org.w3c.dom.Element) docElt.getElementsByTagName("specialCases").item(0);	
				}	
				
				// org.w3c.dom.Element specialCasesElt = doc.createElement("specialCases"); 
				for(String cases : docAnnot.specialCases()){
					if(! "".equals(cases)){
						org.w3c.dom.Element caseElt = doc.createElement("case"); 
						caseElt.setAttribute("item", cases);	
						specialCasesElt.appendChild(caseElt);
					}
				}
				docElt.appendChild(specialCasesElt);			
				
				
				org.w3c.dom.Element examplesElt; 
				if(firstdocElt) {examplesElt = doc.createElement("examples");}
				else {examplesElt = (org.w3c.dom.Element) docElt.getElementsByTagName("examples").item(0);}
				
				// org.w3c.dom.Element examplesElt = doc.createElement("examples"); 
				for(String example : docAnnot.examples()){
					org.w3c.dom.Element exampleElt = doc.createElement("example"); 
			        exampleElt.setAttribute("code", example);	
					examplesElt.appendChild(exampleElt);				
				}
				docElt.appendChild(examplesElt);
	
				org.w3c.dom.Element seeAlsoElt; 
				if(firstdocElt) {seeAlsoElt = doc.createElement("seeAlso");}
				else {seeAlsoElt = (org.w3c.dom.Element) docElt.getElementsByTagName("seeAlso").item(0);}				
				//org.w3c.dom.Element seeAlsoElt = doc.createElement("seeAlso"); 
				for(String see : docAnnot.seeAlso()){
					org.w3c.dom.Element seesElt = doc.createElement("see"); 
					seesElt.setAttribute("id", see);	
					seeAlsoElt.appendChild(seesElt);				
				}
				docElt.appendChild(seeAlsoElt);			
		        operator.appendChild(docElt);	
			}	        
			operators.appendChild(operator);
		}
        return operators;
	}


	private org.w3c.dom.Element processDocXMLSkills(
			Set<? extends Element> setSkills, Document doc) {
		
        org.w3c.dom.Element skills = doc.createElement("Skills"+setSkills.size());
        
		for ( Element e : setSkills ) {
			org.w3c.dom.Element skill = doc.createElement("skill");
			// TODO : id should be precised
			String id = e.getAnnotation(skill.class).value()[0];
			
	        skill.setAttribute("id", id);
	        skill.setAttribute("name", e.getAnnotation(skill.class).value()[0]);
	        // TODO : should be completed

	        // (Set<? extends ExecutableElement>) env.getElementsAnnotatedWith(operator.class);
	        // e.getAnnotation(skill.class);
	        
	        skills.appendChild(skill);
	        
	        // Addition of other skills for alternative names of the species
	        for(int i = 1; i < e.getAnnotation(skill.class).value().length ; i++){
				org.w3c.dom.Element skillAlt = doc.createElement("skill");
		        skill.setAttribute("name", e.getAnnotation(skill.class).value()[i]);				
				skillAlt.setAttribute("alternativeNameOfSkill", id);
		        skills.appendChild(skillAlt);	        
		        }
		}
		return skills;
	}


	private org.w3c.dom.Element processDocXMLSpecies(
			Set<? extends Element> setSpecies, Document doc) {
        org.w3c.dom.Element species = doc.createElement("Species"+setSpecies.size());
        
		for ( Element e : setSpecies ) {
			org.w3c.dom.Element spec = doc.createElement("species");
	        spec.setAttribute("id", e.getAnnotation(species.class).value());
	        spec.setAttribute("name", e.getAnnotation(species.class).value());
			
	        species.appendChild(spec);
		}
		return species;
	}

}
