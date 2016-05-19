/*********************************************************************************************
 * 
 *
 * 'XMLWriter.java', in plugin 'msi.gama.processor', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.precompiler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLWriter {

	ProcessingEnvironment processingEnv;
	Messager mes;
	
	public XMLWriter(final ProcessingEnvironment procEnv) {
		processingEnv = procEnv;
		mes = processingEnv.getMessager();
	}
	
	public void write(final Writer out, final GamlProperties props) {
		DocumentBuilder docBuilder = null;

		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.err.println("Impossible de cr�er un DocumentBuilder.");
			System.exit(1);
		}

		Document doc = docBuilder.newDocument();

		// Set<? extends Element> setRoot = env.getRootElements();
		org.w3c.dom.Element root = doc.createElement("doc");
		
		
//		writeHeader(sb, packageName);
//
//		for ( Map.Entry<String, String> entry : props.filterFirst(TYPE_PREFIX).entrySet() ) {
//			writeType(sb, entry.getKey(), entry.getValue());
//		}
//		writeFactoriesAddition(sb, props.filterFirst(FACTORY_PREFIX));
//		for ( Map.Entry<String, String> entry : props.filterFirst(SYMBOL_PREFIX).entrySet() ) {
//			writeSymbolAddition(sb, entry.getKey(), entry.getValue());
//		}
//		for ( Map.Entry<String, String> entry : props.filterFirst(VAR_PREFIX).entrySet() ) {
//			writeVarAddition(sb, entry.getKey(), entry.getValue());
//		}
		
		org.w3c.dom.Element operatorsElt = doc.createElement("operators");		
		for ( Map.Entry<String, String> entry : props.filterFirst(JavaWriter.OPERATOR_PREFIX).entrySet() ) {
			operatorsElt.appendChild(writeOperator(doc, entry.getKey(), entry.getValue()));			
		}
		root.appendChild(operatorsElt);
		
//		for ( Map.Entry<String, String> entry : props.filterFirst(ACTION_PREFIX).entrySet() ) {
//			writeActionAddition(sb, entry.getKey(), entry.getValue());
//		}
		
		org.w3c.dom.Element skillsElt = doc.createElement("skills");
		for ( Map.Entry<String, String> entry : props.filterFirst(JavaWriter.SKILL_PREFIX).entrySet() ) {
			skillsElt.appendChild(writeSkill(doc, entry.getKey(), entry.getValue()));
		}
		root.appendChild(skillsElt);
		
		org.w3c.dom.Element speciessElt = doc.createElement("speciess");
		for ( Map.Entry<String, String> entry : props.filterFirst(JavaWriter.SPECIES_PREFIX).entrySet() ) {
			speciessElt.appendChild(writeSpecies(doc, entry.getKey(), entry.getValue()));
		}
		root.appendChild(speciessElt);
		
//		writeFooter(sb);
	
		// //////////////////////
		// Final step:
		doc.appendChild(root);

		// ////////////////////////////////////////////////

		try {
			// Creation of the DOM source
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


	private Node writeSpecies(Document doc, String key, String value) {
		org.w3c.dom.Element speciesElt = doc.createElement("species");
		
		String[] segments = key.split("\\$");
		String name = segments[0];
		String clazz = segments[1];
		
		speciesElt.setAttribute("id", name);
		speciesElt.setAttribute("name", name);
		
		return speciesElt;
	}

	private Node writeSkill(Document doc, String key, String value) {	
		org.w3c.dom.Element skillElt = doc.createElement("skill");
		
		String[] segments = key.split("\\$");
		String name = segments[0];
		// TODO : an "id" of the skill class
		String clazz = segments[1];
		// String clazz = new JavaWriter().toClassObject(segments[1]);
		
		// TODO : id should be precised
		skillElt.setAttribute("id", name);
		skillElt.setAttribute("name", name);

		// get extends
		skillElt.setAttribute("class", clazz);
//		skillElt.setAttribute("extends", ((TypeElement) e).getSuperclass().toString());
//
//		if ( e.getAnnotation(doc.class) != null ) {
//			org.w3c.dom.Element docElt = doc.createElement("description");
//			docElt.setTextContent(e.getAnnotation(doc.class).value());
//			skillElt.appendChild(docElt);
//		} else {
//			mes.printMessage(Kind.WARNING, "The skill __" + e.getAnnotation(skill.class).name() +
//				"__ is not described. Add a @doc block");
//		}

		// Parsing of vars
//		if ( e.getAnnotation(vars.class) != null ) {
//			org.w3c.dom.Element varsElt = doc.createElement("vars");
//			for ( var v : e.getAnnotation(vars.class).value() ) {
//				org.w3c.dom.Element varElt = doc.createElement("var");
//				varElt.setAttribute("name", v.name());
//				varElt.setAttribute("type", v.type());
//				varElt.setAttribute("constant", "" + v.constant());
//				// FIXME For the moment, very simple parsing of the doc attached (only the text
//				// is grabbed).
//				if ( v.doc().length > 0 ) {
//					varElt.setAttribute("doc", v.doc()[0].value());
//				}
//				String dependsOn = new String();
//				for ( String dependElement : v.depends_on() ) {
//					dependsOn = ("".equals(dependsOn) ? "" : dependsOn + ",") + dependElement;
//				}
//				varElt.setAttribute("depends_on", dependsOn);
//				varsElt.appendChild(varElt);
//			}
//			skillElt.appendChild(varsElt);
//		}

		// Parsing of actions
		org.w3c.dom.Element actionsElt = doc.createElement("actions");

//		for ( Element eltMethod : e.getEnclosedElements() ) {
//			if ( eltMethod.getAnnotation(action.class) != null ) {
//				org.w3c.dom.Element actionElt = doc.createElement("action");
//				actionElt.setAttribute("name", eltMethod.getAnnotation(action.class).name());
//				actionElt.setAttribute("returnType", ((ExecutableElement) eltMethod)
//					.getReturnType().toString());
//
//				if ( eltMethod.getAnnotation(args.class) != null ) {
//					org.w3c.dom.Element argsElt = doc.createElement("args");
//					for ( String argAction : eltMethod.getAnnotation(args.class).names() ) {
//						org.w3c.dom.Element argElt = doc.createElement("arg");
//						argElt.setAttribute("name", argAction);
//						argsElt.appendChild(argElt);
//					}
//					actionElt.appendChild(argsElt);
//				}
//				actionsElt.appendChild(actionElt);
//			}
//		}

		// if(actionsElt.getElementsByTagName("action").getLength() != 0){
		skillElt.appendChild(actionsElt);
		// }


		// Skills now have only one name

		// // Addition of other skills for alternative names of the species
		// for ( int i = 1; i < e.getAnnotation(skill.class).name().length; i++ ) {
		// org.w3c.dom.Element skillAlt = doc.createElement("skill");
		// skillAlt.setAttribute("id", e.getAnnotation(skill.class).name()[i]);
		// skillAlt.setAttribute("name", e.getAnnotation(skill.class).name()[i]);
		// skillAlt.setAttribute("alternativeNameOfSkill", id);
		// skills.appendChild(skillAlt);
		// }
		return skillElt;
	}
	// check the inheritance between Skills
//	NodeList nlSkill = skills.getElementsByTagName("skill");
//	for ( int i = 0; i < nlSkill.getLength(); i++ ) {
//		org.w3c.dom.Element elt = (org.w3c.dom.Element) nlSkill.item(i);
//		if ( elt.hasAttribute("extends") ) {
//			if ( BASIC_SKILL.equals(elt.getAttribute("extends")) ) {
//				elt.setAttribute("extends", "");
//			} else {
//				for ( int j = 0; j < nlSkill.getLength(); j++ ) {
//					org.w3c.dom.Element testedElt = (org.w3c.dom.Element) nlSkill.item(j);
//					if ( testedElt.getAttribute("class").equals(elt.getAttribute("extends")) ) {
//						elt.setAttribute("extends", testedElt.getAttribute("name"));
//					}
//				}
//			}
//		}
//	}


	
	// TODO To polish
	private Node writeOperator(Document doc, String key, String value) {
		// TODO : Look for an already parsed operator with the same name
			org.w3c.dom.Element operator = doc.createElement("operator");
			String[] segments = key.split("\\$");
			boolean unary = segments[1].equals("");
			String l = segments[0];
			String r = segments[1];
			String canBeConst = toBoolean(segments[2]);
			String type = segments[3];
			String contentType = segments[4];
			boolean iterator = segments[5].equals("true");
			String priority = segments[6];
			String ret = segments[7];
			String m = segments[8];
			boolean stat = segments[9].equals("true");
			boolean scope = segments[10].equals("true");

			// if("".equals(operator.getAttribute("id"))){
			operator.setAttribute("id", segments[11]);
			operator.setAttribute("name", segments[11]);
			// }
			operator.setAttribute("category",m);

			// Parse the alternative names of the operator
			if(segments.length > 11) {
				org.w3c.dom.Element alternativeNameElt = doc.createElement("alternativeName");			
				for ( int i = 12; i < segments.length; i++ ) {
					org.w3c.dom.Element nameElt = doc.createElement("altName");
					nameElt.setAttribute("name", segments[i]);
					alternativeNameElt.appendChild(nameElt);
				}
				operator.appendChild(alternativeNameElt);
			}
			
			// Parse the combinaison operands / result
			org.w3c.dom.Element combinaisonOpResElt;
			if ( operator.getElementsByTagName("combinaisonIO").getLength() == 0 ) {
				combinaisonOpResElt = doc.createElement("combinaisonIO");
			} else {
				combinaisonOpResElt =
					(org.w3c.dom.Element) operator.getElementsByTagName("combinaisonIO").item(0);
			}

			org.w3c.dom.Element operands = doc.createElement("operands");
			operands.setAttribute("returnType", ret);
			operands.setAttribute("contentType", contentType);
			operands.setAttribute("type", type);

			if(unary){
				org.w3c.dom.Element operand = doc.createElement("operand");
				operand.setAttribute("type", l);
				operand.setAttribute("position", "" + 0);
				// TODO replace the "unknown"
				operand.setAttribute("name", "unknown");
				operands.appendChild(operand);				
			}
			else {
				// left operand
				org.w3c.dom.Element operandL = doc.createElement("operand");
				operandL.setAttribute("type", l);
				operandL.setAttribute("position", "" + 0);
				// TODO replace the "unknown"
				operandL.setAttribute("name", "unknown");
				operands.appendChild(operandL);
				
				// Right operand
				org.w3c.dom.Element operandR = doc.createElement("operand");
				operandR.setAttribute("type", r);
				operandR.setAttribute("position", "" + 1);
				// TODO replace the "unknown"
				operandR.setAttribute("name", "unknown");
				operands.appendChild(operandR);					
			}

			combinaisonOpResElt.appendChild(operands);
			operator.appendChild(combinaisonOpResElt);

			// /////////////////////////////////////////////////////
			// Parsing of the documentation
			
		return operator;
	}
	
	String toBoolean(final String s) {
		return s.equals("true") ? "T" : "F";
	}
	
}
