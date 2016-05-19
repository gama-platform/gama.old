/*********************************************************************************************
 *
 *
 * 'GamlDocProcessor.java', in plugin 'msi.gama.processor', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.precompiler;

import java.io.*;
import java.lang.reflect.Field;
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
import org.w3c.dom.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.constants.ColorCSS;
import msi.gama.precompiler.doc.DocProcessorAnnotations;
import msi.gama.precompiler.doc.Element.*;
import msi.gama.precompiler.doc.utils.*;

@SupportedAnnotationTypes({ "msi.gama.precompiler.GamlAnnotations.*" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class GamlDocProcessor {

	public static final String BASIC_SKILL = "msi.gaml.skills.Skill";

	public static final Character[] cuttingLettersOperatorDoc = { 'd','n' };

	ProcessingEnvironment processingEnv;
	Messager mes;
	TypeConverter tc;

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
		tc = new TypeConverter();
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

		org.w3c.dom.Element root = doc.createElement("doc");

		// ////////////////////////////////////////////////
		// /// Parsing of Constants Categories
		Set<? extends Element> setConstants = env.getElementsAnnotatedWith(constant.class);

		root.appendChild(this.processDocXMLCategories(setConstants, doc, XMLElements.CONSTANTS_CATEGORIES));
		
		// ////////////////////////////////////////////////
		// /// Parsing of Concepts
		Field [] conceptArray = IConcept.class.getFields();
		
		root.appendChild(this.processDocXMLConcepts(conceptArray, doc, XMLElements.CONCEPT_LIST));

		// ////////////////////////////////////////////////
		// /// Parsing of Constants
		root.appendChild(this.processDocXMLConstants(setConstants, doc));

		// ////////////////////////////////////////////////
		// /// Parsing of Operators Categories
		@SuppressWarnings("unchecked")
		Set<? extends ExecutableElement> setOperatorsCategories =
			(Set<? extends ExecutableElement>) env.getElementsAnnotatedWith(operator.class);
		root.appendChild(this.processDocXMLCategories(setOperatorsCategories, doc, XMLElements.OPERATORS_CATEGORIES));

		// ////////////////////////////////////////////////
		// /// Parsing of Operators
		@SuppressWarnings("unchecked")
		Set<? extends ExecutableElement> setOperators =
			(Set<? extends ExecutableElement>) env.getElementsAnnotatedWith(operator.class);
		root.appendChild(this.processDocXMLOperators(setOperators, doc));

		// ////////////////////////////////////////////////
		// /// Parsing of Skills
		Set<? extends Element> setSkills = env.getElementsAnnotatedWith(skill.class);
		root.appendChild(this.processDocXMLSkills(setSkills, doc, env));

		// ////////////////////////////////////////////////
		// /// Parsing of Architectures
		Set<? extends Element> setArchitectures = env.getElementsAnnotatedWith(skill.class);
		root.appendChild(this.processDocXMLArchitectures(setArchitectures, doc, env));

		// ////////////////////////////////////////////////
		// /// Parsing of Species
		Set<? extends Element> setSpecies = env.getElementsAnnotatedWith(species.class);
		root.appendChild(this.processDocXMLSpecies(setSpecies, doc));

		// ////////////////////////////////////////////////
		// /// Parsing of Inside statements (kinds and symbols)
		Set<? extends Element> setStatements = env.getElementsAnnotatedWith(symbol.class);
		root.appendChild(this.processDocXMLStatementsInsideKind(setStatements, doc));
		root.appendChild(this.processDocXMLStatementsInsideSymbol(setStatements, doc));

		// ////////////////////////////////////////////////
		// /// Parsing of Statements
		root.appendChild(this.processDocXMLStatementsKinds(setStatements, doc));
		root.appendChild(this.processDocXMLStatements(setStatements, doc));

		// ////////////////////////////////////////////////
		// /// Parsing of Types to get operators
		Set<? extends Element> setOperatorsTypes = env.getElementsAnnotatedWith(type.class);
		ArrayList<org.w3c.dom.Element> listEltOperatorsFromTypes = this.processDocXMLOperatorsFromTypes(setOperatorsTypes, doc);

		org.w3c.dom.Element eltOperators =
			(org.w3c.dom.Element) root.getElementsByTagName(XMLElements.OPERATORS).item(0);
		for ( org.w3c.dom.Element eltOp : listEltOperatorsFromTypes ) {
			eltOperators.appendChild(eltOp);
		}

		// ////////////////////////////////////////////////
		// /// Parsing of Files to get operators
		Set<? extends Element> setFiles = env.getElementsAnnotatedWith(file.class);
		ArrayList<org.w3c.dom.Element> listEltOperatorsFromFiles = this.processDocXMLOperatorsFromFiles(setFiles, doc);

		for ( org.w3c.dom.Element eltOp : listEltOperatorsFromFiles ) {
			eltOperators.appendChild(eltOp);
		}

		// ////////////////////////////////////////////////
		// /// Parsing of Types 
		Set<? extends Element> setTypes = env.getElementsAnnotatedWith(type.class);
		root.appendChild(this.processDocXMLTypes(setTypes, doc, env));
		
		
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
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1"); // "UTF-8");//

			transformer.transform(source, result);
			String stringResult = writer.toString();

			final PrintWriter docWriterXML = new PrintWriter(out);
			docWriterXML.append(stringResult).println("");
			docWriterXML.close();

		} catch (Exception e) {
			throw new NullPointerException("Error in the Processor ");
		}
	}

	private org.w3c.dom.Element processDocXMLConstants(final Set<? extends Element> set, final Document doc) {
		org.w3c.dom.Element eltConstants = doc.createElement(XMLElements.CONSTANTS);
		for ( Element e : set ) {
			if ( e.getAnnotation(constant.class).value().equals(e.getSimpleName().toString()) ) {
				org.w3c.dom.Element eltConstant =
					DocProcessorAnnotations.getConstantElt(e.getAnnotation(constant.class), doc, e, mes, tc);
				
				// Concept
				org.w3c.dom.Element conceptsElt;
				if ( eltConstant.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0 ) {
					conceptsElt = DocProcessorAnnotations.getConcepts(e, doc,
							doc.createElement(XMLElements.CONCEPTS), tc);
				} else {
					conceptsElt = DocProcessorAnnotations.getConcepts(e, doc, (org.w3c.dom.Element) eltConstant
						.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
				}
				
				eltConstant.appendChild(conceptsElt);
				
				eltConstants.appendChild(eltConstant);
			}

			if ( e.getAnnotation(constant.class).category() != null &&
				IConstantCategory.COLOR_CSS.equals(e.getAnnotation(constant.class).category()[0]) ) {
				Object[] colorTab = ColorCSS.array;
				for ( int i = 0; i < colorTab.length; i += 2 ) {
					org.w3c.dom.Element constantElt = doc.createElement(XMLElements.CONSTANT);
					constantElt.setAttribute(XMLElements.ATT_CST_NAME,
						DocProcessorAnnotations.PREFIX_CONSTANT + colorTab[i]);
					constantElt.setAttribute(XMLElements.ATT_CST_VALUE,
						"r=" + ((int[]) colorTab[i + 1])[0] + ", g=" + ((int[]) colorTab[i + 1])[1] + ", b=" +
							((int[]) colorTab[i + 1])[2] + ", alpha=" + ((int[]) colorTab[i + 1])[3]);
					constantElt.appendChild(
						DocProcessorAnnotations.getCategories(e, doc, doc.createElement(XMLElements.CATEGORIES), tc));					
					
					// Concept
					org.w3c.dom.Element conceptsElt;
					if ( constantElt.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0 ) {
						conceptsElt = DocProcessorAnnotations.getConcepts(e, doc,
								doc.createElement(XMLElements.CONCEPTS), tc);
					} else {
						conceptsElt = DocProcessorAnnotations.getConcepts(e, doc, (org.w3c.dom.Element) constantElt
							.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
					}
					constantElt.appendChild(conceptsElt);

					eltConstants.appendChild(constantElt);
				}
			}
		}
		return eltConstants;
	}

	private ArrayList<org.w3c.dom.Element> processDocXMLOperatorsFromTypes(final Set<? extends Element> set,
		final Document doc) {

		// Parcours de tous les types 
		// creation d'ojets types dans le XML
		// ajout d'
		
		
		ArrayList<org.w3c.dom.Element> eltOpFromTypes = new ArrayList<org.w3c.dom.Element>();
		for ( Element e : set ) {
			// Operators to be created:
			// - name_type: converts the parameter into the type name_type
			Operator op = new Operator(doc, tc.getProperCategory("Types"), e.getAnnotation(type.class).concept(), e.getAnnotation(type.class).name());
			op.setOperands(((TypeElement) e).getQualifiedName().toString(), "", e.getAnnotation(type.class).name(), "");
			op.addOperand(new Operand(doc, "val", 0, "any"));
			op.setDocumentation("Casts the operand into the type " + e.getAnnotation(type.class).name());

			eltOpFromTypes.add(op.getElementDOM());
		}

		return eltOpFromTypes;
	}

	private ArrayList<org.w3c.dom.Element> processDocXMLOperatorsFromFiles(final Set<? extends Element> set,
		final Document doc) {

		ArrayList<org.w3c.dom.Element> eltOpFromTypes = new ArrayList<org.w3c.dom.Element>();
		for ( Element e : set ) {
			// Operators to be created:
			// - "is_"+name : test whether the operand parameter is of the given kind of file
			// - name+"_file": converts the parameter into the type name_type
			Operator op_is =
				new Operator(doc, tc.getProperCategory("Files"), e.getAnnotation(file.class).concept() , "is_" + e.getAnnotation(file.class).name(),
					"Tests whether the operand is a " + e.getAnnotation(file.class).name() + " file.");
			op_is.setOperands(((TypeElement) e).getQualifiedName().toString(), "", "bool", "");
			op_is.addOperand(new Operand(doc, "val", 0, "any"));
			// op_is.setDocumentation("Tests whether the operand is a "+ e.getAnnotation(file.class).name() + " file.");

			Operator op_file =
				new Operator(doc, tc.getProperCategory("Files"), e.getAnnotation(file.class).concept(), e.getAnnotation(file.class).name() + "_file");
			op_file.setOperands(((TypeElement) e).getQualifiedName().toString(), "", "file", "");
			op_file.addOperand(new Operand(doc, "val", 0, "string"));

			String[] tabExtension = e.getAnnotation(file.class).extensions();
			String listExtension = "";
			if ( tabExtension.length > 0 ) {
				listExtension = tabExtension[0];
				if ( tabExtension.length > 1 ) {
					for ( int i = 1; i < tabExtension.length; i++ ) {
						listExtension = listExtension + ", " + tabExtension[i];
					}
				}
			}
			op_file.setDocumentation("Constructs a file of type " + e.getAnnotation(file.class).name() +
				". Allowed extensions are limited to " + listExtension);

			eltOpFromTypes.add(op_is.getElementDOM());
			eltOpFromTypes.add(op_file.getElementDOM());
		}

		return eltOpFromTypes;
	}

	private org.w3c.dom.Element processDocXMLCategories(final Set<? extends Element> set, final Document doc,
		final String typeElement) {
		org.w3c.dom.Element categories = doc.createElement(typeElement);

		// When we parse categories of operators, we add the iterator category.
		if ( XMLElements.OPERATORS_CATEGORIES.equals(typeElement) ) {
			org.w3c.dom.Element category;
			category = doc.createElement(XMLElements.CATEGORY);
			category.setAttribute(XMLElements.ATT_CAT_ID, IOperatorCategory.ITERATOR);
			categories.appendChild(category);
		}

		for ( Element e : set ) {
			String[] categoryNames = new String[1];
			// String categoryName;
			if ( e.getAnnotation(operator.class) != null && e.getAnnotation(operator.class).category().length > 0 ) {
				categoryNames = e.getAnnotation(operator.class).category();
			} else if ( e.getAnnotation(constant.class) != null &&
				e.getAnnotation(constant.class).category().length > 0 ) {
				categoryNames = e.getAnnotation(constant.class).category();
			} else {
				categoryNames[0] = tc.getProperCategory(e.getEnclosingElement().getSimpleName().toString());
			}

			NodeList nL = categories.getElementsByTagName(XMLElements.CATEGORY);

			for ( String categoryName : categoryNames ) {
				if ( !IOperatorCategory.DEPRECATED.equals(categoryName) ) {
					int i = 0;
					boolean found = false;
					while (!found && i < nL.getLength()) {
						org.w3c.dom.Element elt = (org.w3c.dom.Element) nL.item(i);
						if ( categoryName.equals(tc.getProperCategory(elt.getAttribute(XMLElements.ATT_CAT_ID))) ) {
							found = true;
						}
						i++;
					}

					if ( !found ) {
						org.w3c.dom.Element category;
						category = doc.createElement(XMLElements.CATEGORY);
						category.setAttribute(XMLElements.ATT_CAT_ID, categoryName);
						categories.appendChild(category);
					}
				}
			}
		}
		return categories;
	}
	
	private org.w3c.dom.Element processDocXMLConcepts(final Field[] conceptArray, final Document doc, final String typeElement) {
		org.w3c.dom.Element concepts = doc.createElement(typeElement);
		for (Field field : conceptArray) {
			org.w3c.dom.Element conceptElem;
			conceptElem = doc.createElement(XMLElements.CONCEPT);
			try {
				conceptElem.setAttribute(XMLElements.ATT_CAT_ID, field.get(new Object()).toString());
			} catch (DOMException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			concepts.appendChild(conceptElem);
		}
		return concepts;
	}

	private org.w3c.dom.Element processDocXMLOperators(final Set<? extends ExecutableElement> set, final Document doc) {
		org.w3c.dom.Element operators = doc.createElement(XMLElements.OPERATORS);

		for ( ExecutableElement e : set ) {
			if ( e.getAnnotation(operator.class).internal() == true ||
				e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated()) ) {
				// Just omit it
			} else {

				nbrOperators++;
				List<? extends VariableElement> args = e.getParameters();
				Set<Modifier> m = e.getModifiers();
				boolean isStatic = m.contains(Modifier.STATIC);
				int arity = 0;

				if ( e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated()) ) {
					// We just omit it
				} else {
					// Look for an already parsed operator with the same name
					org.w3c.dom.Element operator = DocProcessorAnnotations.getOperatorElement(operators,
						e.getAnnotation(operator.class).value()[0]);
					if ( operator == null ) {
						operator = doc.createElement(XMLElements.OPERATOR);
						operator.setAttribute(XMLElements.ATT_OP_ID,
							tc.getProperOperatorName(e.getAnnotation(operator.class).value()[0]));
						operator.setAttribute(XMLElements.ATT_OP_NAME,
							tc.getProperOperatorName(e.getAnnotation(operator.class).value()[0]));

						operator.setAttribute(XMLElements.ATT_ALPHABET_ORDER,
							getAlphabetOrder(e.getAnnotation(operator.class).value()[0]));
					}
					// Parse the alternative names of the operator
					// we will create one operator markup per alternative name
					for ( String name : e.getAnnotation(operator.class).value() ) {
						if ( !"".equals(name) && !name.equals(e.getAnnotation(operator.class).value()[0]) ) {
							// Look for an already parsed operator with the same name
							org.w3c.dom.Element altElt = DocProcessorAnnotations.getOperatorElement(operators, name);
							if ( altElt == null ) {
								altElt = doc.createElement(XMLElements.OPERATOR);
								altElt.setAttribute(XMLElements.ATT_OP_ID, name);
								altElt.setAttribute(XMLElements.ATT_OP_NAME, name);
								altElt.setAttribute(XMLElements.ATT_OP_ALT_NAME,
									e.getAnnotation(operator.class).value()[0]);
								altElt.setAttribute(XMLElements.ATT_ALPHABET_ORDER, getAlphabetOrder(name));

								altElt.appendChild(DocProcessorAnnotations.getCategories(e, doc, tc));
								operators.appendChild(altElt);
							} else {
								// Show an error in the case where two alternative names do not refer to
								// the same operator
								if ( !e.getAnnotation(operator.class).value()[0]
									.equals(altElt.getAttribute(XMLElements.ATT_OP_ALT_NAME)) ) {
									mes.printMessage(Kind.ERROR,
										"The alternative name __" + name + "__ is used for two different operators: " +
											e.getAnnotation(operator.class).value()[0] + " and " +
											altElt.getAttribute("alternativeNameOf"));
								}
							}
						}
					}

					// Parse of categories

					// Category
					org.w3c.dom.Element categoriesElt;
					if ( operator.getElementsByTagName(XMLElements.OPERATOR_CATEGORIES).getLength() == 0 ) {
						categoriesElt = DocProcessorAnnotations.getCategories(e, doc,
							doc.createElement(XMLElements.OPERATOR_CATEGORIES), tc);
					} else {
						categoriesElt = DocProcessorAnnotations.getCategories(e, doc, (org.w3c.dom.Element) operator
							.getElementsByTagName(XMLElements.OPERATOR_CATEGORIES).item(0), tc);
					}
					operator.appendChild(categoriesElt);
					
					// Concept
					org.w3c.dom.Element conceptsElt;
					
					if ( operator.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0 ) {
						conceptsElt = DocProcessorAnnotations.getConcepts(e, doc,
								doc.createElement(XMLElements.CONCEPTS), tc);
					} else {
						conceptsElt = DocProcessorAnnotations.getConcepts(e, doc, (org.w3c.dom.Element) operator
							.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
					}
					operator.appendChild(conceptsElt);

					// Parse the combination operands / result
					org.w3c.dom.Element combinaisonOpResElt;
					if ( operator.getElementsByTagName(XMLElements.COMBINAISON_IO).getLength() == 0 ) {
						combinaisonOpResElt = doc.createElement(XMLElements.COMBINAISON_IO);
					} else {
						combinaisonOpResElt =
							(org.w3c.dom.Element) operator.getElementsByTagName(XMLElements.COMBINAISON_IO).item(0);
					}

					org.w3c.dom.Element operands = doc.createElement(XMLElements.OPERANDS);
					operands.setAttribute("returnType", tc.getProperType(e.getReturnType().toString()));
					operands.setAttribute("contentType", "" + e.getAnnotation(operator.class).content_type());
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
						org.w3c.dom.Element operand = doc.createElement(XMLElements.OPERAND);
						operand.setAttribute(XMLElements.ATT_OPERAND_TYPE,
							tc.getProperType(e.getEnclosingElement().asType().toString()));
						operand.setAttribute(XMLElements.ATT_OPERAND_POSITION, "" + arity);
						arity++;
						operand.setAttribute(XMLElements.ATT_OPERAND_NAME,
							e.getEnclosingElement().asType().toString().toLowerCase());
						operands.appendChild(operand);
					}
					if ( args.size() > 0 ) {
						int first_index = args.get(0).asType().toString().contains("IScope") ? 1 : 0;
						for ( int i = first_index; i <= args.size() - 1; i++ ) {
							org.w3c.dom.Element operand = doc.createElement(XMLElements.OPERAND);
							operand.setAttribute(XMLElements.ATT_OPERAND_TYPE,
								tc.getProperType(args.get(i).asType().toString()));
							operand.setAttribute(XMLElements.ATT_OPERAND_POSITION, "" + arity);
							arity++;
							operand.setAttribute(XMLElements.ATT_OPERAND_NAME, args.get(i).getSimpleName().toString());
							operands.appendChild(operand);
						}
					}
					// operator.setAttribute("arity", ""+arity);
					combinaisonOpResElt.appendChild(operands);
					operator.appendChild(combinaisonOpResElt);

					// /////////////////////////////////////////////////////
					// Parsing of the documentation
					org.w3c.dom.Element docElt;
					if ( operator.getElementsByTagName(XMLElements.DOCUMENTATION).getLength() == 0 ) {
						docElt = DocProcessorAnnotations.getDocElt(e.getAnnotation(doc.class), doc, mes,
							"Operator " + operator.getAttribute("name"), tc, e);
					} else {
						docElt = DocProcessorAnnotations.getDocElt(e.getAnnotation(doc.class), doc,
							(org.w3c.dom.Element) operator.getElementsByTagName(XMLElements.DOCUMENTATION).item(0), mes,
							"Operator " + operator.getAttribute("name"), tc, e);
					}

					if ( docElt != null ) {
						operator.appendChild(docElt);
					}

					operators.appendChild(operator);
				}
			}
		}
		return operators;
	}

	private org.w3c.dom.Element processDocXMLArchitectures(final Set<? extends Element> setArchis, final Document doc,
		final RoundEnvironment env) {
		org.w3c.dom.Element archis = doc.createElement(XMLElements.ARCHITECTURES);

		for ( Element e : setArchis ) {
			if ( e.getAnnotation(skill.class).internal() == true ||
				e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated()) ) {
				// Just omit it
			} else if ( ElementTypeUtils.isArchitectureElement((TypeElement) e, mes) ) {
				org.w3c.dom.Element archiElt = doc.createElement(XMLElements.ARCHITECTURE);

				archiElt.setAttribute(XMLElements.ATT_ARCHI_ID, e.getAnnotation(skill.class).name());
				archiElt.setAttribute(XMLElements.ATT_ARCHI_NAME, e.getAnnotation(skill.class).name());

				org.w3c.dom.Element docEltArchi = DocProcessorAnnotations.getDocElt(e.getAnnotation(doc.class), doc,
					mes, e.getSimpleName().toString(), tc, null);
				if ( docEltArchi != null ) {
					archiElt.appendChild(docEltArchi);
				}

				// Parsing of vars
				org.w3c.dom.Element varsElt = DocProcessorAnnotations.getVarsElt(e.getAnnotation(vars.class), doc, mes,
					archiElt.getAttribute("name"), tc);

				if ( varsElt != null ) {
					archiElt.appendChild(varsElt);
				}

				// Parsing of actions
				org.w3c.dom.Element actionsElt = doc.createElement(XMLElements.ACTIONS);

				for ( Element eltMethod : e.getEnclosedElements() ) {
					org.w3c.dom.Element actionElt = DocProcessorAnnotations
						.getActionElt(eltMethod.getAnnotation(action.class), doc, mes, eltMethod, tc);

					if ( actionElt != null ) {
						actionsElt.appendChild(actionElt);
					}
				}
				archiElt.appendChild(actionsElt);
				
				// Concept
				org.w3c.dom.Element conceptsElt;
				
				if ( archiElt.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0 ) {
					conceptsElt = DocProcessorAnnotations.getConcepts(e, doc,
							doc.createElement(XMLElements.CONCEPTS), tc);
				} else {
					conceptsElt = DocProcessorAnnotations.getConcepts(e, doc, (org.w3c.dom.Element) archiElt
						.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
				}
				archiElt.appendChild(conceptsElt);

				archis.appendChild(archiElt);
			}
		}

		return archis;
	}

	private org.w3c.dom.Element processDocXMLSkills(final Set<? extends Element> setSkills, final Document doc,
		final RoundEnvironment env) {

		org.w3c.dom.Element skills = doc.createElement(XMLElements.SKILLS);

		for ( Element e : setSkills ) {
			boolean emptySkill = true;

			if ( e.getAnnotation(skill.class).internal() == true ||
				e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated()) ) {
				// Just omit it
			} else if ( !ElementTypeUtils.isArchitectureElement((TypeElement) e, mes) ) {

				nbrSkills++;
				org.w3c.dom.Element skillElt = doc.createElement(XMLElements.SKILL);

				skillElt.setAttribute(XMLElements.ATT_SKILL_ID, e.getAnnotation(skill.class).name());
				skillElt.setAttribute(XMLElements.ATT_SKILL_NAME, e.getAnnotation(skill.class).name());

				// get extends
				skillElt.setAttribute(XMLElements.ATT_SKILL_CLASS, ((TypeElement) e).getQualifiedName().toString());
				skillElt.setAttribute(XMLElements.ATT_SKILL_EXTENDS, ((TypeElement) e).getSuperclass().toString());

				org.w3c.dom.Element docEltSkill = DocProcessorAnnotations.getDocElt(e.getAnnotation(doc.class), doc,
					mes, e.getSimpleName().toString(), tc, null);
				if ( docEltSkill != null ) {
					skillElt.appendChild(docEltSkill);
					emptySkill = false;
				}

				// Parsing of vars
				org.w3c.dom.Element varsElt = DocProcessorAnnotations.getVarsElt(e.getAnnotation(vars.class), doc, mes,
					skillElt.getAttribute("name"), tc);

				if ( varsElt != null ) {
					skillElt.appendChild(varsElt);

					if ( varsElt.getElementsByTagName(XMLElements.VAR).getLength() != 0 ) {
						emptySkill = false;
					}
				}

				// Parsing of actions
				org.w3c.dom.Element actionsElt = doc.createElement(XMLElements.ACTIONS);

				for ( Element eltMethod : e.getEnclosedElements() ) {
					org.w3c.dom.Element actionElt = DocProcessorAnnotations
						.getActionElt(eltMethod.getAnnotation(action.class), doc, mes, eltMethod, tc);

					if ( actionElt != null ) {
						actionsElt.appendChild(actionElt);
						emptySkill = false;
					}
				}
				skillElt.appendChild(actionsElt);
				
				// Concept
				org.w3c.dom.Element conceptsElt;
				
				if ( skillElt.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0 ) {
					conceptsElt = DocProcessorAnnotations.getConcepts(e, doc,
							doc.createElement(XMLElements.CONCEPTS), tc);
				} else {
					conceptsElt = DocProcessorAnnotations.getConcepts(e, doc, (org.w3c.dom.Element) skillElt
						.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
				}
				skillElt.appendChild(conceptsElt);

				if ( !emptySkill ) {
					skills.appendChild(skillElt);
				}
			}
		}
		// check the inheritance between Skills
		NodeList nlSkill = skills.getElementsByTagName(XMLElements.SKILL);
		for ( int i = 0; i < nlSkill.getLength(); i++ ) {
			org.w3c.dom.Element elt = (org.w3c.dom.Element) nlSkill.item(i);
			if ( elt.hasAttribute(XMLElements.ATT_SKILL_EXTENDS) ) {
				if ( BASIC_SKILL.equals(elt.getAttribute(XMLElements.ATT_SKILL_EXTENDS)) ) {
					elt.setAttribute(XMLElements.ATT_SKILL_EXTENDS, "");
				} else {
					for ( int j = 0; j < nlSkill.getLength(); j++ ) {
						org.w3c.dom.Element testedElt = (org.w3c.dom.Element) nlSkill.item(j);
						if ( testedElt.getAttribute(XMLElements.ATT_SKILL_CLASS)
							.equals(elt.getAttribute(XMLElements.ATT_SKILL_EXTENDS)) ) {
							elt.setAttribute(XMLElements.ATT_SKILL_EXTENDS,
								testedElt.getAttribute(XMLElements.ATT_SKILL_NAME));
						}
					}
				}
			}
		}

		return skills;
	}

	private org.w3c.dom.Element processDocXMLSpecies(final Set<? extends Element> setSpecies, final Document doc) {
		org.w3c.dom.Element species = doc.createElement(XMLElements.SPECIESS);

		for ( Element e : setSpecies ) {
			if ( e.getAnnotation(species.class).internal() == true ||
				e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated()) ) {
				// Just omit it
			} else {
				org.w3c.dom.Element spec = doc.createElement(XMLElements.SPECIES);
				spec.setAttribute(XMLElements.ATT_SPECIES_ID, e.getAnnotation(species.class).name());
				spec.setAttribute(XMLElements.ATT_SPECIES_NAME, e.getAnnotation(species.class).name());

				org.w3c.dom.Element docEltSpecies = DocProcessorAnnotations.getDocElt(e.getAnnotation(doc.class), doc,
					mes, e.getSimpleName().toString(), tc, null);
				if ( docEltSpecies != null ) {
					spec.appendChild(docEltSpecies);
				}

				// Parsing of actions
				org.w3c.dom.Element actionsElt = doc.createElement(XMLElements.ACTIONS);
				for ( Element eltMethod : e.getEnclosedElements() ) {
					org.w3c.dom.Element actionElt = DocProcessorAnnotations
						.getActionElt(eltMethod.getAnnotation(action.class), doc, mes, eltMethod, tc);

					if ( actionElt != null ) {
						actionsElt.appendChild(actionElt);
					}
				}
				spec.appendChild(actionsElt);
				
				
				// Parsing of skills
				org.w3c.dom.Element skillsElt = doc.createElement(XMLElements.SPECIES_SKILLS);
				for ( String eltSkill : e.getAnnotation(species.class).skills() ) {
					org.w3c.dom.Element skillElt = doc.createElement(XMLElements.SPECIES_SKILL);
					skillElt.setAttribute(XMLElements.ATT_SPECIES_SKILL, eltSkill);
					if ( skillElt != null ) {
						skillsElt.appendChild(skillElt);
					}
				}
				spec.appendChild(skillsElt);	
				
				// Parsing of vars
				org.w3c.dom.Element varsElt = DocProcessorAnnotations.getVarsElt(e.getAnnotation(vars.class), doc, mes,
						spec.getAttribute("name"), tc);	
				if(varsElt != null) {
					spec.appendChild(varsElt);						
				}
				
				// Parsing of concept
				org.w3c.dom.Element conceptsElt;	
				if ( spec.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0 ) {
					conceptsElt = DocProcessorAnnotations.getConcepts(e, doc,
							doc.createElement(XMLElements.CONCEPTS), tc);
				} else {
					conceptsElt = DocProcessorAnnotations.getConcepts(e, doc, (org.w3c.dom.Element) spec
						.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
				}
				spec.appendChild(conceptsElt);
				
				species.appendChild(spec);
			}
		}
		return species;
	}

	private org.w3c.dom.Element processDocXMLStatementsInsideSymbol(final Set<? extends Element> setStatement,
		final Document doc) {
		org.w3c.dom.Element statementsInsideSymbolElt = doc.createElement(XMLElements.INSIDE_STAT_SYMBOLS);
		ArrayList<String> insideStatementSymbol = new ArrayList<String>();

		for ( Element e : setStatement ) {
			if ( e.getAnnotation(symbol.class).internal() == true ||
				e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated()) ) {
				// We just omit it
			} else {
				inside insideAnnot = e.getAnnotation(inside.class);

				if ( insideAnnot != null ) {
					for ( String sym : insideAnnot.symbols() ) {
						if ( !insideStatementSymbol.contains(sym) ) {
							insideStatementSymbol.add(sym);
						}
					}
				}
			}
		}

		for ( String insName : insideStatementSymbol ) {
			org.w3c.dom.Element insideStatElt = doc.createElement(XMLElements.INSIDE_STAT_SYMBOL);
			insideStatElt.setAttribute(XMLElements.ATT_INSIDE_STAT_SYMBOL, insName);
			statementsInsideSymbolElt.appendChild(insideStatElt);
		}

		return statementsInsideSymbolElt;
	}

	private org.w3c.dom.Element processDocXMLStatementsInsideKind(final Set<? extends Element> setStatement,
		final Document doc) {
		org.w3c.dom.Element statementsInsideKindElt = doc.createElement(XMLElements.INSIDE_STAT_KINDS);
		ArrayList<String> insideStatementKind = new ArrayList<String>();

		for ( Element e : setStatement ) {
			if ( e.getAnnotation(symbol.class).internal() == true ||
				e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated()) ) {
				// We just omit it
			} else {
				inside insideAnnot = e.getAnnotation(inside.class);

				if ( insideAnnot != null ) {
					for ( int kind : insideAnnot.kinds() ) {
						String kindStr = tc.getSymbolKindStringFromISymbolKind(kind);
						if ( !insideStatementKind.contains(kindStr) ) {
							insideStatementKind.add(kindStr);
						}
					}
				}
			}
		}

		for ( String insName : insideStatementKind ) {
			org.w3c.dom.Element insideStatElt = doc.createElement(XMLElements.INSIDE_STAT_KIND);
			insideStatElt.setAttribute(XMLElements.ATT_INSIDE_STAT_SYMBOL, insName);
			statementsInsideKindElt.appendChild(insideStatElt);
		}

		return statementsInsideKindElt;
	}

	private Node processDocXMLStatementsKinds(final Set<? extends Element> setStatements, final Document doc) {
		org.w3c.dom.Element statementsKindsElt = doc.createElement(XMLElements.STATEMENT_KINDS);
		ArrayList<String> statementKinds = new ArrayList<String>();

		for ( Element e : setStatements ) {
			if ( e.getAnnotation(symbol.class).internal() == true ||
				e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated()) ) {
				// We just omit it
			} else {
				int kindAnnot = e.getAnnotation(symbol.class).kind();
				String kindStr = tc.getSymbolKindStringFromISymbolKind(kindAnnot);
				if ( !statementKinds.contains(kindStr) ) {
					statementKinds.add(kindStr);
				}
			}
		}

		for ( String kindName : statementKinds ) {
			org.w3c.dom.Element kindStatElt = doc.createElement(XMLElements.KIND);
			kindStatElt.setAttribute(XMLElements.ATT_KIND_STAT, kindName);
			statementsKindsElt.appendChild(kindStatElt);
		}

		return statementsKindsElt;
	}

	
	private org.w3c.dom.Element processDocXMLTypes(final Set<? extends Element> setTypes, final Document doc,
		final RoundEnvironment env) {
		org.w3c.dom.Element types = doc.createElement(XMLElements.TYPES);

		for ( Element t : setTypes ) {
			if(! t.getAnnotation(type.class).internal()) {
				org.w3c.dom.Element typeElt = doc.createElement(XMLElements.TYPE);		
				
				typeElt.setAttribute(XMLElements.ATT_TYPE_NAME, t.getAnnotation(type.class).name());
				typeElt.setAttribute(XMLElements.ATT_TYPE_ID, ""+t.getAnnotation(type.class).id());
				typeElt.setAttribute(XMLElements.ATT_TYPE_KIND, ""+t.getAnnotation(type.class).kind());
				
				// /////////////////////////////////////////////////////
				// Parsing of the documentation
				if ( t.getAnnotation(type.class).doc().length != 0 ) {
					org.w3c.dom.Element docElt = DocProcessorAnnotations.getDocElt(t.getAnnotation(type.class).doc()[0],doc, mes, 
							t.getAnnotation(type.class).name(), tc, null);
					typeElt.appendChild(docElt);
				}
				
				// Parsing of concept
				org.w3c.dom.Element conceptsElt;	
				if ( typeElt.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0 ) {
					conceptsElt = DocProcessorAnnotations.getConcepts(t, doc,
							doc.createElement(XMLElements.CONCEPTS), tc);
				} else {
					conceptsElt = DocProcessorAnnotations.getConcepts(t, doc, (org.w3c.dom.Element) typeElt
						.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
				}
				typeElt.appendChild(conceptsElt);
				
				types.appendChild(typeElt);
			}
		}
		
		return types;
	}
	
	
	
	private org.w3c.dom.Element processDocXMLStatements(final Set<? extends Element> setStatement, final Document doc) {
		org.w3c.dom.Element statementsElt = doc.createElement(XMLElements.STATEMENTS);

		for ( Element e : setStatement ) {
			if ( e.getAnnotation(symbol.class).internal() == true ||
				e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated()) ) {
				// We just omit it
			} else {
				nbrSymbols++;
				org.w3c.dom.Element statElt = doc.createElement(XMLElements.STATEMENT);
				if ( e.getAnnotation(symbol.class).name().length != 0 ) {
					statElt.setAttribute(XMLElements.ATT_STAT_ID, e.getAnnotation(symbol.class).name()[0]);
					statElt.setAttribute(XMLElements.ATT_STAT_NAME, e.getAnnotation(symbol.class).name()[0]);
				} else {
					String kind = tc.getSymbolKindStringFromISymbolKind(e.getAnnotation(symbol.class).kind())
						.replace("(", "").replace(")", "").replace(" ", "_");
					statElt.setAttribute(XMLElements.ATT_STAT_ID, kind);
					statElt.setAttribute(XMLElements.ATT_STAT_NAME, kind);
				}
				statElt.setAttribute(XMLElements.ATT_STAT_KIND,
					tc.getSymbolKindStringFromISymbolKind(e.getAnnotation(symbol.class).kind()));

				// Parsing of facets
				org.w3c.dom.Element facetsElt = DocProcessorAnnotations.getFacetsElt(e.getAnnotation(facets.class), doc,
					mes, statElt.getAttribute(XMLElements.ATT_STAT_NAME), tc);
				if ( facetsElt != null ) {
					statElt.appendChild(facetsElt);
				}

				// Parsing of documentation
				org.w3c.dom.Element docstatElt = DocProcessorAnnotations.getDocElt(e.getAnnotation(doc.class), doc, mes,
					"Statement " + statElt.getAttribute(XMLElements.ATT_STAT_NAME), tc, null);
				if ( docstatElt != null ) {
					statElt.appendChild(docstatElt);
				}

				// Parsing of inside
				org.w3c.dom.Element insideElt =
					DocProcessorAnnotations.getInsideElt(e.getAnnotation(inside.class), doc, tc);
				if ( insideElt != null ) {
					statElt.appendChild(insideElt);
				}
				
				// Parsing of concept
				org.w3c.dom.Element conceptsElt;	
				if ( statElt.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0 ) {
					conceptsElt = DocProcessorAnnotations.getConcepts(e, doc,
							doc.createElement(XMLElements.CONCEPTS), tc);
				} else {
					conceptsElt = DocProcessorAnnotations.getConcepts(e, doc, (org.w3c.dom.Element) statElt
						.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
				}
				statElt.appendChild(conceptsElt);

				statementsElt.appendChild(statElt);
			}
		}
		return statementsElt;
	}

	public static String getAlphabetOrder(final String name) {
		String order = "";
		String lastChar = "z";

		for ( int i = 0; i < cuttingLettersOperatorDoc.length; i++ ) {
			Character previousChar = i == 0 ? 'a' : cuttingLettersOperatorDoc[i - 1];
			Character c = cuttingLettersOperatorDoc[i];

			if ( i == 0 && name.compareTo(c.toString()) < 0 ||
				name.compareTo(previousChar.toString()) >= 0 && name.compareTo(c.toString()) < 0 ) { // name is < to cutting letter
				order = previousChar.toString() + ((Character) Character.toChars(c - 1)[0]).toString();
			}
		}
		if ( "".equals(order) ) {
			order = cuttingLettersOperatorDoc[cuttingLettersOperatorDoc.length - 1].toString() + lastChar;
		}

		return order;
	}

}