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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.precompiler;

import java.io.*;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import org.w3c.dom.*;

@SupportedAnnotationTypes({ "msi.gama.precompiler.GamlAnnotations.*" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class GamlDocProcessor {

	public static final String BASIC_SKILL = "msi.gaml.skills.Skill";

	ProcessingEnvironment processingEnv;
	Messager mes;
	HashMap<String, String> properNameTypeMap;
	HashMap<String, String> properCategoryNameMap;

	boolean firstParsing;

	// Statistiques values
	int nbrOperators;
	int nbrOperatorsDoc;
	int nbrSkills;
	int nbrSymbols;

	public GamlDocProcessor(final ProcessingEnvironment procEnv) {
		processingEnv = procEnv;
		mes = processingEnv.getMessager();
		firstParsing = true;
		nbrOperators = 0;
		nbrOperatorsDoc = 0;
		nbrSkills = 0;
		nbrSymbols = 0;
		properNameTypeMap = initProperNameTypeMap();
		properCategoryNameMap = initProperNameCategoriesMap();
	}

	public void processDocXML(final RoundEnvironment env, final Writer out) {

		DocumentBuilder docBuilder = null;

		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.err.println("Impossible to create a DocumentBuilder.");
			System.exit(1);
		}

		Document doc = docBuilder.newDocument();

		// Set<? extends Element> setRoot = env.getRootElements();
		org.w3c.dom.Element root = doc.createElement("doc");

		// ////////////////////////////////////////////////
		// /// Parsing of Operators Categories
		Set<? extends ExecutableElement> setOperatorsCategories =
			(Set<? extends ExecutableElement>) env.getElementsAnnotatedWith(operator.class);
		root.appendChild(this.processDocXMLOperatorsCategories(setOperatorsCategories, doc));

		// ////////////////////////////////////////////////
		// /// Parsing of Operators
		Set<? extends ExecutableElement> setOperators =
			(Set<? extends ExecutableElement>) env.getElementsAnnotatedWith(operator.class);
		root.appendChild(this.processDocXMLOperators(setOperators, doc));

		// ////////////////////////////////////////////////
		// /// Parsing of Skills
		Set<? extends Element> setSkills = env.getElementsAnnotatedWith(skill.class);
		root.appendChild(this.processDocXMLSkills(setSkills, doc));

		// ////////////////////////////////////////////////
		// /// Parsing of Species
		Set<? extends Element> setSpecies = env.getElementsAnnotatedWith(species.class);
		root.appendChild(this.processDocXMLSpecies(setSpecies, doc));

		// ////////////////////////////////////////////////
		// /// Parsing of Statements
		Set<? extends Element> setCmds = env.getElementsAnnotatedWith(symbol.class);
		root.appendChild(this.processDocXMLStatements(setCmds, doc));

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
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

			transformer.transform(source, result);
			String stringResult = writer.toString();

			final PrintWriter docWriterXML = new PrintWriter(out);
			docWriterXML.append(stringResult).println("");
			docWriterXML.close();

		} catch (Exception e) {
			throw new NullPointerException("Erreur dans le Processor ");
		}
	}

	private static org.w3c.dom.Element getOperatorElement(final org.w3c.dom.Element operators,
		final String eltName) {
		NodeList nL = operators.getElementsByTagName("operator");
		int i = 0;
		boolean found = false;
		while (!found && i < nL.getLength()) {
			org.w3c.dom.Element elt = (org.w3c.dom.Element) nL.item(i);
			if ( eltName.equals(elt.getAttribute("id")) ) { return elt; }
			i++;
		}
		return null;
	}

	private org.w3c.dom.Element processDocXMLOperatorsCategories(
		final Set<? extends ExecutableElement> set, final Document doc) {
		org.w3c.dom.Element operatorsCategories = doc.createElement("operatorsCategories");
		for ( ExecutableElement e : set ) {

			String categoryName =
				getProperCategory(e.getEnclosingElement().getSimpleName().toString());

			NodeList nL = operatorsCategories.getElementsByTagName("category");
			int i = 0;
			boolean found = false;
			while (!found && i < nL.getLength()) {
				org.w3c.dom.Element elt = (org.w3c.dom.Element) nL.item(i);
				if ( categoryName.equals(getProperCategory(elt.getAttribute("id"))) ) {
					found = true;
				}
				i++;
			}

			if ( !found ) {
				org.w3c.dom.Element category;
				category = doc.createElement("category");
				category.setAttribute("id", categoryName);
				operatorsCategories.appendChild(category);
			}
		}
		return operatorsCategories;
	}

	private org.w3c.dom.Element processDocXMLOperators(final Set<? extends ExecutableElement> set,
		final Document doc) {
		org.w3c.dom.Element operators = doc.createElement("operators");

		for ( ExecutableElement e : set ) {
			nbrOperators++;
			List<? extends VariableElement> args = e.getParameters();
			Set<Modifier> m = e.getModifiers();
			boolean isStatic = m.contains(Modifier.STATIC);
			int arity = 0;
			boolean firstInstanceOperator = false;

			if ( e.getAnnotation(doc.class) != null &&
				!"".equals(e.getAnnotation(doc.class).deprecated()) ) {
				// We just omit it
				// String strDeprecated = e.getAnnotation(doc.class).deprecated();
				// mes.printMessage(Kind.ERROR, "The deprecative message __" + strDeprecated );
			} else {
				// Look for an already parsed operator with the same name
				org.w3c.dom.Element operator =
					getOperatorElement(operators, e.getAnnotation(operator.class).value()[0]);
				if ( operator == null ) {
					operator = doc.createElement("operator");
					firstInstanceOperator = true;
				}

				if ( firstInstanceOperator ) {
					// if("".equals(operator.getAttribute("id"))){
					operator.setAttribute("id",
						getProperOperatorName(e.getAnnotation(operator.class).value()[0]));
					operator.setAttribute("name",
						getProperOperatorName(e.getAnnotation(operator.class).value()[0]));
					// }
					operator.setAttribute("category", getProperCategory(e.getEnclosingElement()
						.getSimpleName().toString()));
				}
				// Parse the alternative names of the operator
				// we will create one operator markup per alternative name
				for ( String name : e.getAnnotation(operator.class).value() ) {
					if ( !"".equals(name) &&
						!name.equals(e.getAnnotation(operator.class).value()[0]) ) {
						// Look for an already parsed operator with the same name
						org.w3c.dom.Element altElt = getOperatorElement(operators, name);
						if ( altElt == null ) {
							altElt = doc.createElement("operator");
							altElt.setAttribute("id", name);
							altElt.setAttribute("name", name);
							altElt.setAttribute("category", getProperCategory(e
								.getEnclosingElement().getSimpleName().toString()));
							altElt.setAttribute("alternativeNameOf", e
								.getAnnotation(operator.class).value()[0]);
							operators.appendChild(altElt);
						} else {
							// Show an error in the case where two alternative names do not refer to
							// the same operator
							if ( !e.getAnnotation(operator.class).value()[0].equals(altElt
								.getAttribute("alternativeNameOf")) ) {
								mes.printMessage(Kind.ERROR,
									"The alternative name __" + name +
										"__ is used for two different operators: " +
										e.getAnnotation(operator.class).value()[0] + " and " +
										altElt.getAttribute("alternativeNameOf"));
							}
						}
					}
				}

				// Parse the combinaison operands / result
				org.w3c.dom.Element combinaisonOpResElt;
				if ( operator.getElementsByTagName("combinaisonIO").getLength() == 0 ) {
					combinaisonOpResElt = doc.createElement("combinaisonIO");
				} else {
					combinaisonOpResElt =
						(org.w3c.dom.Element) operator.getElementsByTagName("combinaisonIO")
							.item(0);
				}

				org.w3c.dom.Element operands = doc.createElement("operands");
				operands.setAttribute("returnType", getProperType(e.getReturnType().toString()));
				operands.setAttribute("contentType", "" +
					e.getAnnotation(operator.class).content_type());
				operands.setAttribute("type", "" + e.getAnnotation(operator.class).type());

				// To specify where we can find the source code of the class defining the operator
				String pkgName = "" + ((TypeElement) e.getEnclosingElement()).getQualifiedName();
				// Now we have to deal with Spatial operators, that are defined in inner classes
				if ( pkgName.contains("Spatial") ) {
					// We do not take into account what is after 'Spatial'
					pkgName = pkgName.split("Spatial")[0] + "Spatial";
				}
				pkgName = pkgName.replace('.', '/');
				pkgName = pkgName + ".java";
				operands.setAttribute("class", pkgName);

				if ( !isStatic ) {
					org.w3c.dom.Element operand = doc.createElement("operand");
					operand.setAttribute("type", getProperType(e.getEnclosingElement().asType()
						.toString()));
					operand.setAttribute("position", "" + arity);
					arity++;
					operand.setAttribute("name", e.getEnclosingElement().asType().toString()
						.toLowerCase());
					operands.appendChild(operand);
				}
				if ( args.size() > 0 ) {
					int first_index = args.get(0).asType().toString().contains("IScope") ? 1 : 0;
					for ( int i = first_index; i <= args.size() - 1; i++ ) {
						org.w3c.dom.Element operand = doc.createElement("operand");
						operand
							.setAttribute("type", getProperType(args.get(i).asType().toString()));
						operand.setAttribute("position", "" + arity);
						arity++;
						operand.setAttribute("name", args.get(i).getSimpleName().toString());
						operands.appendChild(operand);
					}
				}
				// operator.setAttribute("arity", ""+arity);
				combinaisonOpResElt.appendChild(operands);
				operator.appendChild(combinaisonOpResElt);

				// /////////////////////////////////////////////////////
				// Parsing of the documentation
				doc docAnnot = e.getAnnotation(doc.class);

				if ( docAnnot == null ) {
					mes.printMessage(Kind.ERROR, "The operator __" +
						e.getAnnotation(operator.class).value()[0] + "__ is not documented.");
				} else {
					nbrOperatorsDoc++;
					boolean firstdocElt = true;
					org.w3c.dom.Element docElt; // = doc.createElement("documentation");

					if ( operator.getElementsByTagName("documentation").getLength() == 0 ) {
						docElt = doc.createElement("documentation");
						firstdocElt = true;
					} else {
						docElt =
							(org.w3c.dom.Element) operator.getElementsByTagName("documentation")
								.item(0);
						firstdocElt = false;
					}

					if ( firstdocElt ) {
						org.w3c.dom.Element resultElt = doc.createElement("result");
						resultElt.setTextContent(docAnnot.value());
						docElt.appendChild(resultElt);

						org.w3c.dom.Element commentElt = doc.createElement("comment");
						commentElt.setTextContent(docAnnot.comment());
						docElt.appendChild(commentElt);
					} else {
						docElt
							.getElementsByTagName("result")
							.item(0)
							.setTextContent(
								docElt.getElementsByTagName("result").item(0).getTextContent() +
									docAnnot.value());
						docElt
							.getElementsByTagName("comment")
							.item(0)
							.setTextContent(
								docElt.getElementsByTagName("comment").item(0).getTextContent() +
									docAnnot.comment());
					}

					org.w3c.dom.Element specialCasesElt;
					if ( firstdocElt ) {
						specialCasesElt = doc.createElement("specialCases");
					} else {
						specialCasesElt =
							(org.w3c.dom.Element) docElt.getElementsByTagName("specialCases").item(
								0);
					}

					// org.w3c.dom.Element specialCasesElt = doc.createElement("specialCases");
					for ( String cases : docAnnot.special_cases() ) {
						if ( !"".equals(cases) ) {
							org.w3c.dom.Element caseElt = doc.createElement("case");
							caseElt.setAttribute("item", cases);
							specialCasesElt.appendChild(caseElt);
						}
					}
					docElt.appendChild(specialCasesElt);

					org.w3c.dom.Element examplesElt;
					if ( firstdocElt ) {
						examplesElt = doc.createElement("examples");
					} else {
						examplesElt =
							(org.w3c.dom.Element) docElt.getElementsByTagName("examples").item(0);
					}

					// org.w3c.dom.Element examplesElt = doc.createElement("examples");
					for ( String example : docAnnot.examples() ) {
						org.w3c.dom.Element exampleElt = doc.createElement("example");
						exampleElt.setAttribute("code", example);
						examplesElt.appendChild(exampleElt);
					}
					docElt.appendChild(examplesElt);

					org.w3c.dom.Element seeAlsoElt;
					if ( firstdocElt ) {
						seeAlsoElt = doc.createElement("seeAlso");
					} else {
						seeAlsoElt =
							(org.w3c.dom.Element) docElt.getElementsByTagName("seeAlso").item(0);
					}
					// org.w3c.dom.Element seeAlsoElt = doc.createElement("seeAlso");
					for ( String see : docAnnot.see() ) {
						org.w3c.dom.Element seesElt = doc.createElement("see");
						seesElt.setAttribute("id", see);
						seeAlsoElt.appendChild(seesElt);
					}
					docElt.appendChild(seeAlsoElt);
					operator.appendChild(docElt);
				}
				operators.appendChild(operator);
			}
		}
		return operators;
	}

	private org.w3c.dom.Element processDocXMLSkills(final Set<? extends Element> setSkills,
		final Document doc) {

		org.w3c.dom.Element skills = doc.createElement("skills");

		for ( Element e : setSkills ) {
			nbrSkills++;
			org.w3c.dom.Element skillElt = doc.createElement("skill");
			// TODO : id should be precised
			String id = e.getAnnotation(skill.class).name();

			skillElt.setAttribute("id", id);
			skillElt.setAttribute("name", e.getAnnotation(skill.class).name());

			// get extends
			skillElt.setAttribute("class", ((TypeElement) e).getQualifiedName().toString());
			skillElt.setAttribute("extends", ((TypeElement) e).getSuperclass().toString());

			if ( e.getAnnotation(doc.class) != null ) {
				org.w3c.dom.Element docElt = doc.createElement("description");
				docElt.setTextContent(e.getAnnotation(doc.class).value());
				skillElt.appendChild(docElt);
			} else {
				mes.printMessage(Kind.ERROR, "The skill __" + e.getAnnotation(skill.class).name() +
					"__ is not described. Add a @doc block");
			}

			// Parsing of vars
			if ( e.getAnnotation(vars.class) != null ) {
				org.w3c.dom.Element varsElt = doc.createElement("vars");
				for ( var v : e.getAnnotation(vars.class).value() ) {
					org.w3c.dom.Element varElt = doc.createElement("var");
					varElt.setAttribute("name", v.name());
					varElt.setAttribute("type", String.valueOf(v.type()));
					varElt.setAttribute("constant", "" + v.constant());
					// FIXME For the moment, very simple parsing of the doc attached (only the text
					// is grabbed).
					if ( v.doc().length > 0 ) {
						varElt.setAttribute("doc", v.doc()[0].value());
					}
					String dependsOn = new String();
					for ( String dependElement : v.depends_on() ) {
						dependsOn = ("".equals(dependsOn) ? "" : dependsOn + ",") + dependElement;
					}
					varElt.setAttribute("depends_on", dependsOn);
					varsElt.appendChild(varElt);
				}
				skillElt.appendChild(varsElt);
			}

			// Parsing of actions
			org.w3c.dom.Element actionsElt = doc.createElement("actions");

			for ( Element eltMethod : e.getEnclosedElements() ) {
				if ( eltMethod.getAnnotation(action.class) != null ) {
					org.w3c.dom.Element actionElt = doc.createElement("action");
					actionElt.setAttribute("name", eltMethod.getAnnotation(action.class).name());
					actionElt.setAttribute("returnType", ((ExecutableElement) eltMethod)
						.getReturnType().toString());

					if ( eltMethod.getAnnotation(args.class) != null ) {
						org.w3c.dom.Element argsElt = doc.createElement("args");
						for ( String argAction : eltMethod.getAnnotation(args.class).names() ) {
							org.w3c.dom.Element argElt = doc.createElement("arg");
							argElt.setAttribute("name", argAction);
							argsElt.appendChild(argElt);
						}
						actionElt.appendChild(argsElt);
					}
					actionsElt.appendChild(actionElt);
				}
			}

			// if(actionsElt.getElementsByTagName("action").getLength() != 0){
			skillElt.appendChild(actionsElt);
			// }

			skills.appendChild(skillElt);

			// Skills now have only one name

			// // Addition of other skills for alternative names of the species
			// for ( int i = 1; i < e.getAnnotation(skill.class).name().length; i++ ) {
			// org.w3c.dom.Element skillAlt = doc.createElement("skill");
			// skillAlt.setAttribute("id", e.getAnnotation(skill.class).name()[i]);
			// skillAlt.setAttribute("name", e.getAnnotation(skill.class).name()[i]);
			// skillAlt.setAttribute("alternativeNameOfSkill", id);
			// skills.appendChild(skillAlt);
			// }
		}
		// check the inheritance between Skills
		NodeList nlSkill = skills.getElementsByTagName("skill");
		for ( int i = 0; i < nlSkill.getLength(); i++ ) {
			org.w3c.dom.Element elt = (org.w3c.dom.Element) nlSkill.item(i);
			if ( elt.hasAttribute("extends") ) {
				if ( BASIC_SKILL.equals(elt.getAttribute("extends")) ) {
					elt.setAttribute("extends", "");
				} else {
					for ( int j = 0; j < nlSkill.getLength(); j++ ) {
						org.w3c.dom.Element testedElt = (org.w3c.dom.Element) nlSkill.item(j);
						if ( testedElt.getAttribute("class").equals(elt.getAttribute("extends")) ) {
							elt.setAttribute("extends", testedElt.getAttribute("name"));
						}
					}
				}
			}
		}

		return skills;
	}

	private org.w3c.dom.Element processDocXMLSpecies(final Set<? extends Element> setSpecies,
		final Document doc) {
		org.w3c.dom.Element species = doc.createElement("speciess");

		for ( Element e : setSpecies ) {
			org.w3c.dom.Element spec = doc.createElement("species");
			spec.setAttribute("id", e.getAnnotation(species.class).name());
			spec.setAttribute("name", e.getAnnotation(species.class).name());

			species.appendChild(spec);
		}
		return species;
	}

	private org.w3c.dom.Element processDocXMLStatements(final Set<? extends Element> setCommand,
		final Document doc) {
		org.w3c.dom.Element cmdsElt = doc.createElement("statements");

		for ( Element e : setCommand ) {
			nbrSymbols++;
			org.w3c.dom.Element cmdElt = doc.createElement("statement");
			if ( e.getAnnotation(symbol.class).name().length != 0 ) {
				cmdElt.setAttribute("id", e.getAnnotation(symbol.class).name()[0]);
				cmdElt.setAttribute("name", e.getAnnotation(symbol.class).name()[0]);
			} else {
				// TODO : case of variables declarations ... Variable, ContainerVariable,
				// NumberVariable
			}
			cmdElt.setAttribute("kind", "" + e.getAnnotation(symbol.class).kind());

			// Parsing of facets
			if ( e.getAnnotation(facets.class) != null ) {
				org.w3c.dom.Element facetsElt = doc.createElement("facets");
				for ( facet f : e.getAnnotation(facets.class).value() ) {
					org.w3c.dom.Element facetElt = doc.createElement("facet");
					facetElt.setAttribute("name", f.name());
					// TODO : check several types
					facetElt.setAttribute("type", String.valueOf(f.type()[0]));
					// FIXME Very simple documentation parsing as only the text is grabbed.
					facetElt.setAttribute("optional", "" + f.optional());
					if ( f.doc().length > 0 ) {
						facetElt.setAttribute("doc", f.doc()[0].value());
					}
					facetElt.setAttribute("omissible",
						f.name().equals(e.getAnnotation(facets.class).omissible()) ? "true"
							: "false");
					facetsElt.appendChild(facetElt);
				}
				cmdElt.appendChild(facetsElt);
			}

			cmdsElt.appendChild(cmdElt);
		}
		return cmdsElt;
	}

	private HashMap<String, String> initProperNameTypeMap() {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("msi.gama.metamodel.shape.IShape", "shape");
		hm.put("msi.gama.util.matrix.IMatrix<T>", "matrix");
		hm.put("msi.gama.util.matrix.IMatrix", "matrix");
		hm.put("java.lang.Integer", "int");
		hm.put("java.lang.Double", "float");
		hm.put("msi.gama.util.file.IGamaFile", "file");
		hm.put("msi.gama.util.GamaColor", "rgb");
		hm.put("msi.gama.util.IList", "list");
		hm.put("msi.gama.util.GamaList", "list");
		hm.put("java.util.List", "list");
		hm.put("java.util.List<T>", "list");
		hm.put("msi.gama.util.IList<T>", "list");
		hm.put("msi.gama.util.IList<msi.gama.util.IList<T>>", "list of lists");
		hm.put("msi.gama.util.IList<msi.gama.metamodel.shape.IShape>", "list of shapes");
		hm.put("msi.gama.util.IList<msi.gama.metamodel.shape.GamaPoint>", "list of points");
		hm.put("msi.gama.util.IList<msi.gama.metamodel.agent.IAgent>", "list of agents");
		hm.put("msi.gama.util.GamaList<msi.gama.metamodel.agent.IAgent>", "list of agents");
		hm.put("msi.gama.util.GamaList<msi.gama.metamodel.shape.IShape>", "list of shapes");
		hm.put("msi.gama.metamodel.shape.GamaPoint", "point");
		hm.put("msi.gama.metamodel.shape.ILocation", "point");
		hm.put("java.lang.Object", "any");
		hm.put("msi.gama.util.GamaPair", "pair");
		hm.put("java.lang.Boolean", "bool");
		hm.put("msi.gama.metamodel.agent.IAgent", "agent");
		hm.put("java.lang.String", "string");
		hm.put("msi.gama.util.graph.IGraph", "graph");
		hm.put("msi.gama.util.graph.GamaGraph", "graph");
		hm.put("msi.gama.metamodel.topology.ITopology", "topology");
		hm.put("msi.gama.util.IPath", "path");
		hm.put("msi.gama.util.GamaMap", "map");
		hm.put("msi.gaml.expressions.IExpression", "any expression");
		hm.put("msi.gaml.species.ISpecies", "species");
		hm.put("msi.gama.util.IContainer", "container");
		hm.put("msi.gama.util.IContainer<KeyType,ValueType>", "container");
		hm.put("msi.gama.util.IContainer<?,msi.gama.metamodel.shape.IShape>", "container of shapes");
		hm.put("java.util.Map", "map");
		hm.put("java.util.Map<java.lang.String,java.lang.Object>", "map");
		return hm;
	}

	private String getProperType(String rawName) {
		if ( properNameTypeMap.containsKey(rawName) ) {
			return properNameTypeMap.get(rawName);
		} else {
			return rawName;
		}
	}

	private static String getProperOperatorName(String opName) {
		// if("*".equals(opName)) return "`*`";
		return opName;
	}

	private String getProperCategory(String rawName) {
		if ( properCategoryNameMap.containsKey(rawName) ) {
			return properCategoryNameMap.get(rawName);
		} else {
			return rawName;
		}
	}

	private HashMap<String, String> initProperNameCategoriesMap() {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("Cast", "Casting operators");
		hm.put("Colors", "Mathematics operators");
		hm.put("Comparison", "Comparison operators");
		hm.put("IContainer", "Containers-related operators");
		hm.put("Containers", "Containers-related operators");
		hm.put("GamaMap", "Containers-related operators");
		hm.put("Files", "Files-related operators");
		hm.put("GamaFileType", "Files-related operators");
		hm.put("Graphs", "Graphs-related operators");
		hm.put("GraphsGraphstream", "Graphs-related operators");
		hm.put("Logic", "Logical operators");
		hm.put("Maths", "Mathematics operators");
		hm.put("IMatrix", "Matrix-related operators");
		hm.put("Creation", "Spatial operators");
		hm.put("Operators", "Spatial operators");
		hm.put("Points", "Spatial operators");
		hm.put("Properties", "Spatial operators");
		hm.put("Punctal", "Spatial operators");
		hm.put("Queries", "Spatial operators");
		hm.put("ThreeD", "Spatial operators");
		hm.put("Random", "Random operators");
		hm.put("Statistics", "Spatial operators");
		hm.put("Strings", "Strings-related operators");
		hm.put("Transformations", "Spatial operators");
		hm.put("Relations", "Spatial operators");
		hm.put("Stats", "Statistical operators");
		hm.put("System", "System");
		return hm;
	}
}