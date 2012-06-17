package msi.gama.precompiler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import msi.gama.precompiler.GamlAnnotations.operator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
			System.err.println("Impossible de créer un DocumentBuilder.");
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
//		for ( Map.Entry<String, String> entry : props.filterFirst(SKILL_PREFIX).entrySet() ) {
//			writeSkill(sb, entry.getKey(), entry.getValue());
//		}
//		for ( Map.Entry<String, String> entry : props.filterFirst(SPECIES_PREFIX).entrySet() ) {
//			writeSpecies(sb, entry.getKey(), entry.getValue());
//		}
//
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
