/*********************************************************************************************
 *
 * 'GamlDocProcessor.java, in plugin msi.gama.processor, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.precompiler.doc;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import msi.gama.precompiler.Constants;
import msi.gama.precompiler.ElementProcessor;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IConstantCategory;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ProcessorContext;
import msi.gama.precompiler.constants.ColorCSS;
import msi.gama.precompiler.doc.utils.TypeConverter;
import msi.gama.precompiler.doc.utils.XMLElements;

public class DocProcessor extends ElementProcessor<doc> {

	Messager mes;
	TypeConverter tc;

	// boolean firstParsing;

	// Statistic values
	int nbrOperators;
	int nbrOperatorsDoc;
	int nbrSkills;
	int nbrSymbols;

	public DocProcessor() {
		// firstParsing = true;
		nbrOperators = 0;
		nbrOperatorsDoc = 0;
		nbrSkills = 0;
		nbrSymbols = 0;
		tc = new TypeConverter();
	}

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final Document doc,
			final doc action, final org.w3c.dom.Element node) {
		// Nothing to do, as this processor is a bit different from the others
	}

	@Override
	protected Class<doc> getAnnotationClass() {
		return doc.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {
		// Nothing to do
	}

	@Override
	public void processXML(final ProcessorContext context) {
		if (!context.shouldProduceDoc())
			return;
		document = getBuilder().newDocument();
		// if (!firstParsing)
		// return;
		// firstParsing = false;
		mes = context.getMessager();

		final org.w3c.dom.Element root = document.createElement("doc");

		// ////////////////////////////////////////////////
		// /// Parsing of Constants Categories
		final Set<? extends Element> setConstants = context.getElementsAnnotatedWith(constant.class);

		root.appendChild(this.processDocXMLCategories(setConstants, XMLElements.CONSTANTS_CATEGORIES));

		// ////////////////////////////////////////////////
		// /// Parsing of Concepts
		final Field[] conceptArray = IConcept.class.getFields();

		root.appendChild(this.processDocXMLConcepts(conceptArray, XMLElements.CONCEPT_LIST));

		// ////////////////////////////////////////////////
		// /// Parsing of Constants
		root.appendChild(this.processDocXMLConstants(setConstants));

		// ////////////////////////////////////////////////
		// /// Parsing of Operators Categories
		@SuppressWarnings ("unchecked") 
		final Set<? extends ExecutableElement> setOperatorsCategories =
				(Set<? extends ExecutableElement>) context.getElementsAnnotatedWith(operator.class);
		root.appendChild(this.processDocXMLCategories(setOperatorsCategories, XMLElements.OPERATORS_CATEGORIES));

		// ////////////////////////////////////////////////
		// /// Parsing of Operators
		@SuppressWarnings ("unchecked") 
		final Set<? extends ExecutableElement> setOperators =
				(Set<? extends ExecutableElement>) context.getElementsAnnotatedWith(operator.class);
		root.appendChild(this.processDocXMLOperators(setOperators));

		// ////////////////////////////////////////////////
		// /// Parsing of Skills
		final Set<? extends Element> setSkills = context.getElementsAnnotatedWith(skill.class);
		root.appendChild(this.processDocXMLSkills(setSkills, context));

		// ////////////////////////////////////////////////
		// /// Parsing of Architectures
		final Set<? extends Element> setArchitectures = context.getElementsAnnotatedWith(skill.class);
		root.appendChild(this.processDocXMLArchitectures(setArchitectures, context));

		// ////////////////////////////////////////////////
		// /// Parsing of Species
		final Set<? extends Element> setSpecies = context.getElementsAnnotatedWith(species.class);
		root.appendChild(this.processDocXMLSpecies(setSpecies));

		// ////////////////////////////////////////////////
		// /// Parsing of Inside statements (kinds and symbols)
		final Set<? extends Element> setStatements = context.getElementsAnnotatedWith(symbol.class);
		root.appendChild(this.processDocXMLStatementsInsideKind(setStatements));
		root.appendChild(this.processDocXMLStatementsInsideSymbol(setStatements));

		// ////////////////////////////////////////////////
		// /// Parsing of Statements
		root.appendChild(this.processDocXMLStatementsKinds(setStatements));
		root.appendChild(this.processDocXMLStatements(setStatements));

		// ////////////////////////////////////////////////
		// /// Parsing of Types to get operators
		final Set<? extends Element> setOperatorsTypes = context.getElementsAnnotatedWith(type.class);
		final ArrayList<org.w3c.dom.Element> listEltOperatorsFromTypes =
				this.processDocXMLOperatorsFromTypes(setOperatorsTypes);

		final org.w3c.dom.Element eltOperators =
				(org.w3c.dom.Element) root.getElementsByTagName(XMLElements.OPERATORS).item(0);
		for (final org.w3c.dom.Element eltOp : listEltOperatorsFromTypes) {
			eltOperators.appendChild(eltOp);
		}

		// ////////////////////////////////////////////////
		// /// Parsing of Files to get operators
		final Set<? extends Element> setFilesOperators = context.getElementsAnnotatedWith(file.class);
		final ArrayList<org.w3c.dom.Element> listEltOperatorsFromFiles = this.processDocXMLOperatorsFromFiles(setFilesOperators);

		for (final org.w3c.dom.Element eltOp : listEltOperatorsFromFiles) {
			eltOperators.appendChild(eltOp);
		}

		// ////////////////////////////////////////////////
		// /// Parsing of Files
		
		// TODO : manage to get the documentation...
		final Set<? extends Element> setFiles = context.getElementsAnnotatedWith(file.class);
		root.appendChild(this.processDocXMLTypes(setFiles));	
		
		// ////////////////////////////////////////////////
		// /// Parsing of Types
		final Set<? extends Element> setTypes = context.getElementsAnnotatedWith(type.class);
		root.appendChild(this.processDocXMLTypes(setTypes, context));

		// //////////////////////
		// Final step:
		document.appendChild(root);

		// ////////////////////////////////////////////////

		try (final PrintWriter out = new PrintWriter(context.createWriter("docGAMA.xml"));) {
			final TransformerFactory tf = TransformerFactory.newInstance();
			final Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1"); // "UTF-8");//
			transformer.transform(new DOMSource(document), new StreamResult(out));
		} catch (final TransformerException e) {
			context.emitError("XML Error when producing the documentation: " + e.getMessage(), null);
		}
	}

	private org.w3c.dom.Element processDocXMLConstants(final Set<? extends Element> set) {
		final org.w3c.dom.Element eltConstants = document.createElement(XMLElements.CONSTANTS);
		for (final Element e : set) {
			if (e.getAnnotation(constant.class).value().equals(e.getSimpleName().toString())) {
				final org.w3c.dom.Element eltConstant =
						getConstantElt(e.getAnnotation(constant.class), document, e, mes, tc);

				// Concept
				org.w3c.dom.Element conceptsElt;
				if (eltConstant.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0) {
					conceptsElt = getConcepts(e, document, document.createElement(XMLElements.CONCEPTS), tc);
				} else {
					conceptsElt = getConcepts(e, document,
							(org.w3c.dom.Element) eltConstant.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
				}

				eltConstant.appendChild(conceptsElt);

				eltConstants.appendChild(eltConstant);
			}

			if (e.getAnnotation(constant.class).category() != null
					&& IConstantCategory.COLOR_CSS.equals(e.getAnnotation(constant.class).category()[0])) {
				final Object[] colorTab = ColorCSS.array;
				for (int i = 0; i < colorTab.length; i += 2) {
					final org.w3c.dom.Element constantElt = document.createElement(XMLElements.CONSTANT);
					constantElt.setAttribute(XMLElements.ATT_CST_NAME, PREFIX_CONSTANT + colorTab[i]);
					constantElt.setAttribute(XMLElements.ATT_CST_VALUE,
							"r=" + ((int[]) colorTab[i + 1])[0] + ", g=" + ((int[]) colorTab[i + 1])[1] + ", b="
									+ ((int[]) colorTab[i + 1])[2] + ", alpha=" + ((int[]) colorTab[i + 1])[3]);
					constantElt.appendChild(
							getCategories(e, document, document.createElement(XMLElements.CATEGORIES), tc));

					// Concept
					org.w3c.dom.Element conceptsElt;
					if (constantElt.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0) {
						conceptsElt = getConcepts(e, document, document.createElement(XMLElements.CONCEPTS), tc);
					} else {
						conceptsElt = getConcepts(e, document,
								(org.w3c.dom.Element) constantElt.getElementsByTagName(XMLElements.CONCEPTS).item(0),
								tc);
					}
					constantElt.appendChild(conceptsElt);

					eltConstants.appendChild(constantElt);
				}
			}
		}
		return eltConstants;
	}

	private ArrayList<org.w3c.dom.Element> processDocXMLOperatorsFromTypes(final Set<? extends Element> set) {

		// Parcours de tous les types
		// creation d'ojets types dans le XML
		// ajout d'

		final ArrayList<org.w3c.dom.Element> eltOpFromTypes = new ArrayList<org.w3c.dom.Element>();
		for (final Element e : set) {
			// Operators to be created:
			// - name_type: converts the parameter into the type name_type
			final Operator op = new Operator(document, tc.getProperCategory("Types"),
					e.getAnnotation(type.class).concept(), e.getAnnotation(type.class).name());
			op.setOperands(((TypeElement) e).getQualifiedName().toString(), "", e.getAnnotation(type.class).name(), "");
			op.addOperand(new Operand(document, "val", 0, "any"));
			op.setDocumentation("Casts the operand into the type " + e.getAnnotation(type.class).name());

			eltOpFromTypes.add(op.getElementDOM());
		}

		return eltOpFromTypes;
	}
	
	private org.w3c.dom.Element processDocXMLTypes(final Set<? extends Element> setFiles) {
		final org.w3c.dom.Element files = document.createElement(XMLElements.FILES);

/*@file (
		name = "csv",
		extensions = { "csv", "tsv" },
		buffer_type = IType.MATRIX,
		buffer_index = IType.POINT,
		concept = { IConcept.CSV, IConcept.FILE },
		doc = @doc ("A type of text file that contains comma-separated values"))
*/		
		
		for (final Element e : setFiles) {
			if (e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated())) {
				// Just omit it
			} else {
				final org.w3c.dom.Element file = document.createElement(XMLElements.FILE);
				file.setAttribute(XMLElements.ATT_FILE_NAME, e.getAnnotation(file.class).name());
				file.setAttribute(XMLElements.ATT_FILE_BUFFER_TYPE, tc.getProperType(""+e.getAnnotation(file.class).buffer_type()));
				file.setAttribute(XMLElements.ATT_FILE_BUFFER_INDEX, tc.getProperType(""+e.getAnnotation(file.class).buffer_index()));
				file.setAttribute(XMLElements.ATT_FILE_BUFFER_CONTENT, tc.getProperType(""+e.getAnnotation(file.class).buffer_content()));
				
				// Parsing extensions
				org.w3c.dom.Element extensions = document.createElement(XMLElements.EXTENSIONS);
				for(final String ext : e.getAnnotation(file.class).extensions()) {
					final org.w3c.dom.Element extElt = document.createElement(XMLElements.EXTENSION);
					extElt.setAttribute(XMLElements.ATT_NAME, ext);
					extensions.appendChild(extElt);					
				}
				file.appendChild(extensions);
				
				
				// Parsing of concept
				org.w3c.dom.Element conceptsElt;
				if (file.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0) {
					conceptsElt = getConcepts(e, document, document.createElement(XMLElements.CONCEPTS), tc);
				} else {
					conceptsElt = getConcepts(e, document,
							(org.w3c.dom.Element) file.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
				}
				file.appendChild(conceptsElt);
				
				files.appendChild(file);
			}
		}
		return files;
	}	

	private ArrayList<org.w3c.dom.Element> processDocXMLOperatorsFromFiles(final Set<? extends Element> set) {

		final ArrayList<org.w3c.dom.Element> eltOpFromTypes = new ArrayList<org.w3c.dom.Element>();
		for (final Element e : set) {
			// Operators to be created:
			// - "is_"+name : test whether the operand parameter is of the given
			// kind of file
			// - name+"_file": converts the parameter into the type name_type
			final Operator op_is = new Operator(document, tc.getProperCategory("Files"),
					e.getAnnotation(file.class).concept(), "is_" + e.getAnnotation(file.class).name(),
					"Tests whether the operand is a " + e.getAnnotation(file.class).name() + " file.");
			op_is.setOperands(((TypeElement) e).getQualifiedName().toString(), "", "bool", "");
			op_is.addOperand(new Operand(document, "val", 0, "any"));
			// op_is.setDocumentation("Tests whether the operand is a "+
			// e.getAnnotation(file.class).name() + " file.");

			final Operator op_file = new Operator(document, tc.getProperCategory("Files"),
					e.getAnnotation(file.class).concept(), e.getAnnotation(file.class).name() + "_file");
			op_file.setOperands(((TypeElement) e).getQualifiedName().toString(), "", "file", "");
			op_file.addOperand(new Operand(document, "val", 0, "string"));

			final String[] tabExtension = e.getAnnotation(file.class).extensions();
			String listExtension = "";
			if (tabExtension.length > 0) {
				listExtension = tabExtension[0];
				if (tabExtension.length > 1) {
					for (int i = 1; i < tabExtension.length; i++) {
						listExtension = listExtension + ", " + tabExtension[i];
					}
				}
			}
			op_file.setDocumentation("Constructs a file of type " + e.getAnnotation(file.class).name()
					+ ". Allowed extensions are limited to " + listExtension);

			eltOpFromTypes.add(op_is.getElementDOM());
			eltOpFromTypes.add(op_file.getElementDOM());
		}

		return eltOpFromTypes;
	}

	private org.w3c.dom.Element processDocXMLCategories(final Set<? extends Element> set, final String typeElement) {
		final org.w3c.dom.Element categories = document.createElement(typeElement);

		// When we parse categories of operators, we add the iterator category.
		if (XMLElements.OPERATORS_CATEGORIES.equals(typeElement)) {
			org.w3c.dom.Element category;
			category = document.createElement(XMLElements.CATEGORY);
			category.setAttribute(XMLElements.ATT_CAT_ID, IOperatorCategory.ITERATOR);
			this.appendChild(categories, category);
		}

		for (final Element e : set) {
			String[] categoryNames = new String[1];
			// String categoryName;
			if (e.getAnnotation(operator.class) != null && e.getAnnotation(operator.class).category().length > 0) {
				categoryNames = e.getAnnotation(operator.class).category();
			} else if (e.getAnnotation(constant.class) != null
					&& e.getAnnotation(constant.class).category().length > 0) {
				categoryNames = e.getAnnotation(constant.class).category();
			} else {
				categoryNames[0] = tc.getProperCategory(e.getEnclosingElement().getSimpleName().toString());
			}

			final NodeList nL = categories.getElementsByTagName(XMLElements.CATEGORY);

			for (final String categoryName : categoryNames) {
				if (!IOperatorCategory.DEPRECATED.equals(categoryName)) {
					int i = 0;
					boolean found = false;
					while (!found && i < nL.getLength()) {
						final org.w3c.dom.Element elt = (org.w3c.dom.Element) nL.item(i);
						if (categoryName.equals(tc.getProperCategory(elt.getAttribute(XMLElements.ATT_CAT_ID)))) {
							found = true;
						}
						i++;
					}

					if (!found) {
						org.w3c.dom.Element category;
						category = document.createElement(XMLElements.CATEGORY);
						category.setAttribute(XMLElements.ATT_CAT_ID, categoryName);
						this.appendChild(categories, category);
					}
				}
			}
		}
		return categories;
	}

	private org.w3c.dom.Element processDocXMLConcepts(final Field[] conceptArray, final String typeElement) {
		final org.w3c.dom.Element concepts = document.createElement(typeElement);
		for (final Field field : conceptArray) {
			org.w3c.dom.Element conceptElem;
			conceptElem = document.createElement(XMLElements.CONCEPT);
			try {
				conceptElem.setAttribute(XMLElements.ATT_CAT_ID, field.get(new Object()).toString());
			} catch (final DOMException e) {
				e.printStackTrace();
			} catch (final IllegalArgumentException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			}
			concepts.appendChild(conceptElem);
		}
		return concepts;
	}

	private org.w3c.dom.Element processDocXMLOperators(final Set<? extends ExecutableElement> set) {
		final org.w3c.dom.Element operators = document.createElement(XMLElements.OPERATORS);

		for (final ExecutableElement e : set) {
			if (e.getAnnotation(operator.class).internal() == true
					|| e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated())) {
				// Just omit it
			} else {

				nbrOperators++;
				final List<? extends VariableElement> args = e.getParameters();
				final Set<Modifier> m = e.getModifiers();
				final boolean isStatic = m.contains(Modifier.STATIC);
				int arity = 0;

				if (e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated())) {
					// We just omit it
				} else {
					// Look for an already parsed operator with the same name
					org.w3c.dom.Element operator =
							getOperatorElement(operators, e.getAnnotation(operator.class).value()[0]);
					if (operator == null) {
						operator = document.createElement(XMLElements.OPERATOR);
						operator.setAttribute(XMLElements.ATT_OP_ID,
								tc.getProperOperatorName(e.getAnnotation(operator.class).value()[0]));
						operator.setAttribute(XMLElements.ATT_OP_NAME,
								tc.getProperOperatorName(e.getAnnotation(operator.class).value()[0]));

						operator.setAttribute(XMLElements.ATT_ALPHABET_ORDER,
								Constants.getAlphabetOrder(e.getAnnotation(operator.class).value()[0]));
					}
					// Parse the alternative names of the operator
					// we will create one operator markup per alternative name
					for (final String name : e.getAnnotation(operator.class).value()) {
						if (!"".equals(name) && !name.equals(e.getAnnotation(operator.class).value()[0])) {
							// Look for an already parsed operator with the same
							// name
							org.w3c.dom.Element altElt = getOperatorElement(operators, name);
							if (altElt == null) {
								altElt = document.createElement(XMLElements.OPERATOR);
								altElt.setAttribute(XMLElements.ATT_OP_ID, name);
								altElt.setAttribute(XMLElements.ATT_OP_NAME, name);
								altElt.setAttribute(XMLElements.ATT_OP_ALT_NAME,
										e.getAnnotation(operator.class).value()[0]);
								altElt.setAttribute(XMLElements.ATT_ALPHABET_ORDER, Constants.getAlphabetOrder(name));

								altElt.appendChild(getCategories(e, document, tc));
								operators.appendChild(altElt);
							} else {
								// Show an error in the case where two
								// alternative names do not refer to
								// the same operator
								if (!e.getAnnotation(operator.class).value()[0]
										.equals(altElt.getAttribute(XMLElements.ATT_OP_ALT_NAME))) {
									mes.printMessage(Kind.WARNING,
											"The alternative name __" + name
													+ "__ is used for two different operators: "
													+ e.getAnnotation(operator.class).value()[0] + " and "
													+ altElt.getAttribute("alternativeNameOf"));
								}
							}
						}
					}

					// Parse of categories

					// Category
					org.w3c.dom.Element categoriesElt;
					if (operator.getElementsByTagName(XMLElements.OPERATOR_CATEGORIES).getLength() == 0) {
						categoriesElt =
								getCategories(e, document, document.createElement(XMLElements.OPERATOR_CATEGORIES), tc);
					} else {
						categoriesElt = getCategories(e, document, (org.w3c.dom.Element) operator
								.getElementsByTagName(XMLElements.OPERATOR_CATEGORIES).item(0), tc);
					}
					operator.appendChild(categoriesElt);

					// Concept
					org.w3c.dom.Element conceptsElt;

					if (operator.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0) {
						conceptsElt = getConcepts(e, document, document.createElement(XMLElements.CONCEPTS), tc);
					} else {
						conceptsElt = getConcepts(e, document,
								(org.w3c.dom.Element) operator.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
					}
					operator.appendChild(conceptsElt);

					// Parse the combination operands / result
					org.w3c.dom.Element combinaisonOpResElt;
					if (operator.getElementsByTagName(XMLElements.COMBINAISON_IO).getLength() == 0) {
						combinaisonOpResElt = document.createElement(XMLElements.COMBINAISON_IO);
					} else {
						combinaisonOpResElt =
								(org.w3c.dom.Element) operator.getElementsByTagName(XMLElements.COMBINAISON_IO).item(0);
					}

					final org.w3c.dom.Element operands = document.createElement(XMLElements.OPERANDS);
					operands.setAttribute("returnType", tc.getProperType(e.getReturnType().toString()));
					operands.setAttribute("contentType", "" + e.getAnnotation(operator.class).content_type());
					operands.setAttribute("type", "" + e.getAnnotation(operator.class).type());

					// To specify where we can find the source code of the class
					// defining the operator
					String pkgName = "" + ((TypeElement) e.getEnclosingElement()).getQualifiedName();
					// Now we have to deal with Spatial operators, that are
					// defined in inner classes
					if (pkgName.contains("Spatial")) {
						// We do not take into account what is after 'Spatial'
						pkgName = pkgName.split("Spatial")[0] + "Spatial";
					}
					pkgName = pkgName.replace('.', '/');
					pkgName = pkgName + ".java";
					operands.setAttribute("class", pkgName);

					if (!isStatic) {
						final org.w3c.dom.Element operand = document.createElement(XMLElements.OPERAND);
						operand.setAttribute(XMLElements.ATT_OPERAND_TYPE,
								tc.getProperType(e.getEnclosingElement().asType().toString()));
						operand.setAttribute(XMLElements.ATT_OPERAND_POSITION, "" + arity);
						arity++;
						operand.setAttribute(XMLElements.ATT_OPERAND_NAME,
								e.getEnclosingElement().asType().toString().toLowerCase());
						operands.appendChild(operand);
					}
					if (args.size() > 0) {
						final int first_index = args.get(0).asType().toString().contains("IScope") ? 1 : 0;
						for (int i = first_index; i <= args.size() - 1; i++) {
							final org.w3c.dom.Element operand = document.createElement(XMLElements.OPERAND);
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
					if (operator.getElementsByTagName(XMLElements.DOCUMENTATION).getLength() == 0) {
						docElt = getDocElt(e.getAnnotation(doc.class), document, mes,
								"Operator " + operator.getAttribute("name"), tc, e);
					} else {
						docElt = getDocElt(e.getAnnotation(doc.class), document,
								(org.w3c.dom.Element) operator.getElementsByTagName(XMLElements.DOCUMENTATION).item(0),
								mes, "Operator " + operator.getAttribute("name"), tc, e);
					}

					if (docElt != null) {
						operator.appendChild(docElt);
					}

					operators.appendChild(operator);
				}
			}
		}
		return operators;
	}

	private org.w3c.dom.Element processDocXMLArchitectures(final Set<? extends Element> setArchis,
			final RoundEnvironment env) {
		final org.w3c.dom.Element archis = document.createElement(XMLElements.ARCHITECTURES);

		for (final Element e : setArchis) {
			if (e.getAnnotation(skill.class).internal() == true
					|| e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated())) {
				// Just omit it
			} else if (ElementTypeUtils.isArchitectureElement((TypeElement) e, mes)) {
				final org.w3c.dom.Element archiElt = document.createElement(XMLElements.ARCHITECTURE);

				archiElt.setAttribute(XMLElements.ATT_ARCHI_ID, e.getAnnotation(skill.class).name());
				archiElt.setAttribute(XMLElements.ATT_ARCHI_NAME, e.getAnnotation(skill.class).name());

				final org.w3c.dom.Element docEltArchi =
						getDocElt(e.getAnnotation(doc.class), document, mes, e.getSimpleName().toString(), tc, null);
				if (docEltArchi != null) {
					archiElt.appendChild(docEltArchi);
				}

				// Parsing of vars
				final org.w3c.dom.Element varsElt =
						getVarsElt(e.getAnnotation(vars.class), document, mes, archiElt.getAttribute("name"), tc);

				if (varsElt != null) {
					archiElt.appendChild(varsElt);
				}

				// Parsing of actions
				final org.w3c.dom.Element actionsElt = document.createElement(XMLElements.ACTIONS);

				for (final Element eltMethod : e.getEnclosedElements()) {
					final org.w3c.dom.Element actionElt =
							getActionElt(eltMethod.getAnnotation(action.class), document, mes, eltMethod, tc);

					if (actionElt != null) {
						actionsElt.appendChild(actionElt);
					}
				}
				archiElt.appendChild(actionsElt);

				// Concept
				org.w3c.dom.Element conceptsElt;

				if (archiElt.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0) {
					conceptsElt = getConcepts(e, document, document.createElement(XMLElements.CONCEPTS), tc);
				} else {
					conceptsElt = getConcepts(e, document,
							(org.w3c.dom.Element) archiElt.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
				}
				archiElt.appendChild(conceptsElt);

				archis.appendChild(archiElt);
			}
		}

		return archis;
	}

	private org.w3c.dom.Element processDocXMLSkills(final Set<? extends Element> setSkills,
			final RoundEnvironment env) {

		final org.w3c.dom.Element skills = document.createElement(XMLElements.SKILLS);

		for (final Element e : setSkills) {
			boolean emptySkill = true;

			if (e.getAnnotation(skill.class).internal() == true
					|| e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated())) {
				// Just omit it
			} else if (!ElementTypeUtils.isArchitectureElement((TypeElement) e, mes)) {

				nbrSkills++;
				final org.w3c.dom.Element skillElt = document.createElement(XMLElements.SKILL);

				skillElt.setAttribute(XMLElements.ATT_SKILL_ID, e.getAnnotation(skill.class).name());
				skillElt.setAttribute(XMLElements.ATT_SKILL_NAME, e.getAnnotation(skill.class).name());

				// get extends
				skillElt.setAttribute(XMLElements.ATT_SKILL_CLASS, ((TypeElement) e).getQualifiedName().toString());
				skillElt.setAttribute(XMLElements.ATT_SKILL_EXTENDS, ((TypeElement) e).getSuperclass().toString());

				final org.w3c.dom.Element docEltSkill =
						getDocElt(e.getAnnotation(doc.class), document, mes, e.getSimpleName().toString(), tc, null);
				if (docEltSkill != null) {
					skillElt.appendChild(docEltSkill);
					emptySkill = false;
				}

				// Parsing of vars
				final org.w3c.dom.Element varsElt =
						getVarsElt(e.getAnnotation(vars.class), document, mes, skillElt.getAttribute("name"), tc);

				if (varsElt != null) {
					skillElt.appendChild(varsElt);

					if (varsElt.getElementsByTagName(XMLElements.VAR).getLength() != 0) {
						emptySkill = false;
					}
				}

				// Parsing of actions
				final org.w3c.dom.Element actionsElt = document.createElement(XMLElements.ACTIONS);

				for (final Element eltMethod : e.getEnclosedElements()) {
					final org.w3c.dom.Element actionElt =
							getActionElt(eltMethod.getAnnotation(action.class), document, mes, eltMethod, tc);

					if (actionElt != null) {
						actionsElt.appendChild(actionElt);
						emptySkill = false;
					}
				}
				skillElt.appendChild(actionsElt);

				// Concept
				org.w3c.dom.Element conceptsElt;

				if (skillElt.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0) {
					conceptsElt = getConcepts(e, document, document.createElement(XMLElements.CONCEPTS), tc);
				} else {
					conceptsElt = getConcepts(e, document,
							(org.w3c.dom.Element) skillElt.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
				}
				skillElt.appendChild(conceptsElt);

				if (!emptySkill) {
					skills.appendChild(skillElt);
				}
			}
		}
		// check the inheritance between Skills
		final NodeList nlSkill = skills.getElementsByTagName(XMLElements.SKILL);
		for (int i = 0; i < nlSkill.getLength(); i++) {
			final org.w3c.dom.Element elt = (org.w3c.dom.Element) nlSkill.item(i);
			if (elt.hasAttribute(XMLElements.ATT_SKILL_EXTENDS)) {
				if (BASIC_SKILL.equals(elt.getAttribute(XMLElements.ATT_SKILL_EXTENDS))) {
					elt.setAttribute(XMLElements.ATT_SKILL_EXTENDS, "");
				} else {
					for (int j = 0; j < nlSkill.getLength(); j++) {
						final org.w3c.dom.Element testedElt = (org.w3c.dom.Element) nlSkill.item(j);
						if (testedElt.getAttribute(XMLElements.ATT_SKILL_CLASS)
								.equals(elt.getAttribute(XMLElements.ATT_SKILL_EXTENDS))) {
							elt.setAttribute(XMLElements.ATT_SKILL_EXTENDS,
									testedElt.getAttribute(XMLElements.ATT_SKILL_NAME));
						}
					}
				}
			}
		}

		return skills;
	}

	private org.w3c.dom.Element processDocXMLSpecies(final Set<? extends Element> setSpecies) {
		final org.w3c.dom.Element species = document.createElement(XMLElements.SPECIESS);

		for (final Element e : setSpecies) {
			if (e.getAnnotation(species.class).internal() == true
					|| e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated())) {
				// Just omit it
			} else {
				final org.w3c.dom.Element spec = document.createElement(XMLElements.SPECIES);
				spec.setAttribute(XMLElements.ATT_SPECIES_ID, e.getAnnotation(species.class).name());
				spec.setAttribute(XMLElements.ATT_SPECIES_NAME, e.getAnnotation(species.class).name());

				final org.w3c.dom.Element docEltSpecies =
						getDocElt(e.getAnnotation(doc.class), document, mes, e.getSimpleName().toString(), tc, null);
				if (docEltSpecies != null) {
					spec.appendChild(docEltSpecies);
				}

				// Parsing of actions
				final org.w3c.dom.Element actionsElt = document.createElement(XMLElements.ACTIONS);
				for (final Element eltMethod : e.getEnclosedElements()) {
					final org.w3c.dom.Element actionElt =
							getActionElt(eltMethod.getAnnotation(action.class), document, mes, eltMethod, tc);

					if (actionElt != null) {
						actionsElt.appendChild(actionElt);
					}
				}
				spec.appendChild(actionsElt);

				// Parsing of skills
				final org.w3c.dom.Element skillsElt = document.createElement(XMLElements.SPECIES_SKILLS);
				for (final String eltSkill : e.getAnnotation(species.class).skills()) {
					final org.w3c.dom.Element skillElt = document.createElement(XMLElements.SPECIES_SKILL);
					skillElt.setAttribute(XMLElements.ATT_SPECIES_SKILL, eltSkill);
					skillsElt.appendChild(skillElt);
				}
				spec.appendChild(skillsElt);

				// Parsing of vars
				final org.w3c.dom.Element varsElt =
						getVarsElt(e.getAnnotation(vars.class), document, mes, spec.getAttribute("name"), tc);
				if (varsElt != null) {
					spec.appendChild(varsElt);
				}

				// Parsing of concept
				org.w3c.dom.Element conceptsElt;
				if (spec.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0) {
					conceptsElt = getConcepts(e, document, document.createElement(XMLElements.CONCEPTS), tc);
				} else {
					conceptsElt = getConcepts(e, document,
							(org.w3c.dom.Element) spec.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
				}
				spec.appendChild(conceptsElt);

				species.appendChild(spec);
			}
		}
		return species;
	}

	private org.w3c.dom.Element processDocXMLStatementsInsideSymbol(final Set<? extends Element> setStatement) {
		final org.w3c.dom.Element statementsInsideSymbolElt = document.createElement(XMLElements.INSIDE_STAT_SYMBOLS);
		final ArrayList<String> insideStatementSymbol = new ArrayList<String>();

		for (final Element e : setStatement) {
			if (e.getAnnotation(symbol.class).internal() == true
					|| e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated())) {
				// We just omit it
			} else {
				final inside insideAnnot = e.getAnnotation(inside.class);

				if (insideAnnot != null) {
					for (final String sym : insideAnnot.symbols()) {
						if (!insideStatementSymbol.contains(sym)) {
							insideStatementSymbol.add(sym);
						}
					}
				}
			}
		}

		for (final String insName : insideStatementSymbol) {
			final org.w3c.dom.Element insideStatElt = document.createElement(XMLElements.INSIDE_STAT_SYMBOL);
			insideStatElt.setAttribute(XMLElements.ATT_INSIDE_STAT_SYMBOL, insName);
			statementsInsideSymbolElt.appendChild(insideStatElt);
		}

		return statementsInsideSymbolElt;
	}

	private org.w3c.dom.Element processDocXMLStatementsInsideKind(final Set<? extends Element> setStatement) {
		final org.w3c.dom.Element statementsInsideKindElt = document.createElement(XMLElements.INSIDE_STAT_KINDS);
		final ArrayList<String> insideStatementKind = new ArrayList<String>();

		for (final Element e : setStatement) {
			if (e.getAnnotation(symbol.class).internal() == true
					|| e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated())) {
				// We just omit it
			} else {
				final inside insideAnnot = e.getAnnotation(inside.class);

				if (insideAnnot != null) {
					for (final int kind : insideAnnot.kinds()) {
						final String kindStr = tc.getSymbolKindStringFromISymbolKind(kind);
						if (!insideStatementKind.contains(kindStr)) {
							insideStatementKind.add(kindStr);
						}
					}
				}
			}
		}

		for (final String insName : insideStatementKind) {
			final org.w3c.dom.Element insideStatElt = document.createElement(XMLElements.INSIDE_STAT_KIND);
			insideStatElt.setAttribute(XMLElements.ATT_INSIDE_STAT_SYMBOL, insName);
			statementsInsideKindElt.appendChild(insideStatElt);
		}

		return statementsInsideKindElt;
	}

	private Node processDocXMLStatementsKinds(final Set<? extends Element> setStatements) {
		final org.w3c.dom.Element statementsKindsElt = document.createElement(XMLElements.STATEMENT_KINDS);
		final ArrayList<String> statementKinds = new ArrayList<String>();

		for (final Element e : setStatements) {
			if (e.getAnnotation(symbol.class).internal() == true
					|| e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated())) {
				// We just omit it
			} else {
				final int kindAnnot = e.getAnnotation(symbol.class).kind();
				final String kindStr = tc.getSymbolKindStringFromISymbolKind(kindAnnot);
				if (!statementKinds.contains(kindStr)) {
					statementKinds.add(kindStr);
				}
			}
		}

		for (final String kindName : statementKinds) {
			final org.w3c.dom.Element kindStatElt = document.createElement(XMLElements.KIND);
			kindStatElt.setAttribute(XMLElements.ATT_KIND_STAT, kindName);
			statementsKindsElt.appendChild(kindStatElt);
		}

		return statementsKindsElt;
	}

	private org.w3c.dom.Element processDocXMLTypes(final Set<? extends Element> setTypes, final RoundEnvironment env) {
		final org.w3c.dom.Element types = document.createElement(XMLElements.TYPES);

		for (final Element t : setTypes) {
			if (!t.getAnnotation(type.class).internal()) {
				final org.w3c.dom.Element typeElt = document.createElement(XMLElements.TYPE);

				typeElt.setAttribute(XMLElements.ATT_TYPE_NAME, t.getAnnotation(type.class).name());
				typeElt.setAttribute(XMLElements.ATT_TYPE_ID, "" + t.getAnnotation(type.class).id());
				typeElt.setAttribute(XMLElements.ATT_TYPE_KIND, "" + t.getAnnotation(type.class).kind());

				// /////////////////////////////////////////////////////
				// Parsing of the documentation
				if (t.getAnnotation(type.class).doc().length != 0) {
					final org.w3c.dom.Element docElt = getDocElt(t.getAnnotation(type.class).doc()[0], document, mes,
							t.getAnnotation(type.class).name(), tc, null);
					typeElt.appendChild(docElt);
				}

				// Parsing of concept
				org.w3c.dom.Element conceptsElt;
				if (typeElt.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0) {
					conceptsElt = getConcepts(t, document, document.createElement(XMLElements.CONCEPTS), tc);
				} else {
					conceptsElt = getConcepts(t, document,
							(org.w3c.dom.Element) typeElt.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
				}
				typeElt.appendChild(conceptsElt);

				types.appendChild(typeElt);
			}
		}

		return types;
	}

	private org.w3c.dom.Element processDocXMLStatements(final Set<? extends Element> setStatement) {
		final org.w3c.dom.Element statementsElt = document.createElement(XMLElements.STATEMENTS);

		for (final Element e : setStatement) {
			if (e.getAnnotation(symbol.class).internal() == true
					|| e.getAnnotation(doc.class) != null && !"".equals(e.getAnnotation(doc.class).deprecated())) {
				// We just omit it
			} else {
				nbrSymbols++;
				final org.w3c.dom.Element statElt = document.createElement(XMLElements.STATEMENT);
				if (e.getAnnotation(symbol.class).name().length != 0) {
					statElt.setAttribute(XMLElements.ATT_STAT_ID, e.getAnnotation(symbol.class).name()[0]);
					statElt.setAttribute(XMLElements.ATT_STAT_NAME, e.getAnnotation(symbol.class).name()[0]);
				} else {
					final String kind = tc.getSymbolKindStringFromISymbolKind(e.getAnnotation(symbol.class).kind())
							.replace("(", "").replace(")", "").replace(" ", "_");
					statElt.setAttribute(XMLElements.ATT_STAT_ID, kind);
					statElt.setAttribute(XMLElements.ATT_STAT_NAME, kind);
				}
				statElt.setAttribute(XMLElements.ATT_STAT_KIND,
						tc.getSymbolKindStringFromISymbolKind(e.getAnnotation(symbol.class).kind()));

				// Parsing of facets
				final org.w3c.dom.Element facetsElt = getFacetsElt(e.getAnnotation(facets.class), document, mes,
						statElt.getAttribute(XMLElements.ATT_STAT_NAME), tc);
				if (facetsElt != null) {
					statElt.appendChild(facetsElt);
				}

				// Parsing of documentation
				final org.w3c.dom.Element docstatElt = getDocElt(e.getAnnotation(doc.class), document, mes,
						"Statement " + statElt.getAttribute(XMLElements.ATT_STAT_NAME), tc, null);
				if (docstatElt != null) {
					statElt.appendChild(docstatElt);
				}

				// Parsing of inside
				final org.w3c.dom.Element insideElt = getInsideElt(e.getAnnotation(inside.class), document, tc);
				if (insideElt != null) {
					statElt.appendChild(insideElt);
				}

				// Parsing of concept
				org.w3c.dom.Element conceptsElt;
				if (statElt.getElementsByTagName(XMLElements.CONCEPTS).getLength() == 0) {
					conceptsElt = getConcepts(e, document, document.createElement(XMLElements.CONCEPTS), tc);
				} else {
					conceptsElt = getConcepts(e, document,
							(org.w3c.dom.Element) statElt.getElementsByTagName(XMLElements.CONCEPTS).item(0), tc);
				}
				statElt.appendChild(conceptsElt);

				statementsElt.appendChild(statElt);
			}
		}
		return statementsElt;
	}

	public static final String PREFIX_CONSTANT = "#";

	public org.w3c.dom.Element getDocElt(final doc docAnnot, final Document doc, final Messager mes,
			final String eltName, final TypeConverter tc, final ExecutableElement e) {
		return getDocElt(docAnnot, doc, null, mes, eltName, tc, e);
	}

	public org.w3c.dom.Element getDocElt(final doc[] docAnnotTab, final Document doc, final Messager mes,
			final String eltName, final TypeConverter tc, final ExecutableElement e) { // e.getSimpleName()
		if (docAnnotTab == null || docAnnotTab.length == 0) { return getDocElt(null, doc, null, mes, eltName, tc, e); }
		return getDocElt(docAnnotTab[0], doc, null, mes, eltName, tc, e);
	}

	public org.w3c.dom.Element getDocElt(final doc docAnnot, final Document doc, final org.w3c.dom.Element docElement,
			final Messager mes, final String eltName, final TypeConverter tc, final ExecutableElement e) { // e.getSimpleName()
		org.w3c.dom.Element docElt = docElement;

		if (docAnnot == null) {
			// mes.printMessage(Kind.WARNING, "The element __" + eltName + "__ is not documented.");
		} else {
			if (docElt == null) {
				docElt = doc.createElement(XMLElements.DOCUMENTATION);
			}

			// Parse result
			final String value = docAnnot.value();
			final boolean masterDoc = docAnnot.masterDoc();
			if (value != "") {
				if (docElt.getElementsByTagName(XMLElements.RESULT).getLength() != 0) {
					final org.w3c.dom.Element resultElt =
							(org.w3c.dom.Element) docElt.getElementsByTagName(XMLElements.RESULT).item(0);
					if ("true".equals(resultElt.getAttribute(XMLElements.ATT_RES_MASTER)) && masterDoc
							|| !"true".equals(resultElt.getAttribute(XMLElements.ATT_RES_MASTER)) && !masterDoc) {
						resultElt.setTextContent(resultElt.getTextContent() + "\n" + value);
					} else if (!resultElt.hasAttribute(XMLElements.ATT_RES_MASTER) && masterDoc) {
						resultElt.setTextContent(value);
						resultElt.setAttribute(XMLElements.ATT_RES_MASTER, "true");
					}
				} else {
					final org.w3c.dom.Element resultElt = doc.createElement(XMLElements.RESULT);
					resultElt.setTextContent(value);
					if (masterDoc) {
						resultElt.setAttribute(XMLElements.ATT_RES_MASTER, "true");
					}
					docElt.appendChild(resultElt);
				}
			}

			// Parse comment
			final String comment = docAnnot.comment();
			if (!"".equals(comment)) {
				if (docElt.getElementsByTagName(XMLElements.COMMENT).getLength() != 0) {
					docElt.getElementsByTagName(XMLElements.COMMENT).item(0).setTextContent(
							docElt.getElementsByTagName(XMLElements.COMMENT).item(0).getTextContent() + comment);
				} else {
					final org.w3c.dom.Element commentElt = doc.createElement(XMLElements.COMMENT);
					commentElt.setTextContent(comment);
					docElt.appendChild(commentElt);
				}
			}

			// Parse: seeAlso
			org.w3c.dom.Element seeAlsoElt;
			if (docElt.getElementsByTagName(XMLElements.SEEALSO).getLength() != 0) {
				seeAlsoElt = (org.w3c.dom.Element) docElt.getElementsByTagName(XMLElements.SEEALSO).item(0);
			} else {
				seeAlsoElt = doc.createElement(XMLElements.SEEALSO);
			}
			for (final String see : docAnnot.see()) {
				final NodeList nLSee = seeAlsoElt.getElementsByTagName(XMLElements.SEE);
				int i = 0;
				boolean seeAlreadyInserted = false;
				while (i < nLSee.getLength() && !seeAlreadyInserted) {
					if (((org.w3c.dom.Element) nLSee.item(i)).getAttribute(XMLElements.ATT_SEE_ID).equals(see)) {
						seeAlreadyInserted = true;
					}
					i++;
				}
				if (!seeAlreadyInserted) {
					final org.w3c.dom.Element seesElt = doc.createElement(XMLElements.SEE);
					seesElt.setAttribute(XMLElements.ATT_SEE_ID, see);
					seeAlsoElt.appendChild(seesElt);
				}
			}
			if (docAnnot.see().length != 0) {
				docElt.appendChild(seeAlsoElt);
			}

			// Parse: usages

			org.w3c.dom.Element usagesElt;
			org.w3c.dom.Element usagesExampleElt;
			org.w3c.dom.Element usagesNoExampleElt;
			if (docElt.getElementsByTagName(XMLElements.USAGES).getLength() != 0) {
				usagesElt = (org.w3c.dom.Element) docElt.getElementsByTagName(XMLElements.USAGES).item(0);
			} else {
				usagesElt = doc.createElement(XMLElements.USAGES);
			}
			if (docElt.getElementsByTagName(XMLElements.USAGES_EXAMPLES).getLength() != 0) {
				usagesExampleElt =
						(org.w3c.dom.Element) docElt.getElementsByTagName(XMLElements.USAGES_EXAMPLES).item(0);
			} else {
				usagesExampleElt = doc.createElement(XMLElements.USAGES_EXAMPLES);
			}
			if (docElt.getElementsByTagName(XMLElements.USAGES_NO_EXAMPLE).getLength() != 0) {
				usagesNoExampleElt =
						(org.w3c.dom.Element) docElt.getElementsByTagName(XMLElements.USAGES_NO_EXAMPLE).item(0);
			} else {
				usagesNoExampleElt = doc.createElement(XMLElements.USAGES_NO_EXAMPLE);
			}
			int numberOfUsages = 0;
			int numberOfUsagesWithExamplesOnly = 0;
			int numberOfUsagesWithoutExample = 0;
			for (final usage usage : docAnnot.usages()) {
				final org.w3c.dom.Element usageElt = doc.createElement(XMLElements.USAGE);

				// Among the usages, we consider the ones without value
				if ("".equals(usage.value())) {
					numberOfUsagesWithExamplesOnly++;

					final org.w3c.dom.Element examplesUsageElt = getExamplesElt(usage.examples(), doc, e, tc);
					usageElt.appendChild(examplesUsageElt);
					usagesExampleElt.appendChild(usageElt);
				}
				// Among the usages, we consider the ones with only the value
				else if (usage.examples().length == 0) {
					numberOfUsagesWithoutExample++;

					usageElt.setAttribute(XMLElements.ATT_USAGE_DESC, usage.value());
					usagesNoExampleElt.appendChild(usageElt);
				}
				// Otherwise, when we have both value and examples
				else {
					numberOfUsages++;

					usageElt.setAttribute(XMLElements.ATT_USAGE_DESC, usage.value());
					final org.w3c.dom.Element examplesUsageElt = getExamplesElt(usage.examples(), doc, e, tc);
					usageElt.appendChild(examplesUsageElt);
					usagesElt.appendChild(usageElt);
				}
			}

			// Let's continue with examples and special cases
			// - special cases are equivalent to usage without examples
			// - examples are equivalent to usage with only examples
			// Parse examples
			if (docAnnot.examples().length != 0) {
				final org.w3c.dom.Element usageExElt = doc.createElement(XMLElements.USAGE);

				final org.w3c.dom.Element examplesElt = getExamplesElt(docAnnot.examples(), doc, e, tc);

				numberOfUsagesWithExamplesOnly += docAnnot.examples().length;
				usageExElt.appendChild(examplesElt);
				usagesExampleElt.appendChild(usageExElt);
			}

			// Parse specialCases
			for (final String cases : docAnnot.special_cases()) {
				if (!"".equals(cases)) {
					final org.w3c.dom.Element caseElt = doc.createElement(XMLElements.USAGE);
					caseElt.setAttribute(XMLElements.ATT_USAGE_DESC, cases);
					usagesNoExampleElt.appendChild(caseElt);
					numberOfUsagesWithoutExample++;
				}
			}

			if (numberOfUsagesWithExamplesOnly != 0) {
				docElt.appendChild(usagesExampleElt);
			}
			if (numberOfUsagesWithoutExample != 0) {
				docElt.appendChild(usagesNoExampleElt);
			}
			if (numberOfUsages != 0) {
				docElt.appendChild(usagesElt);
			}

		}
		return docElt;
	}

	public org.w3c.dom.Element getExamplesElt(final example[] examples, final Document doc, final ExecutableElement e,
			final TypeConverter tc) {
		final org.w3c.dom.Element examplesElt = doc.createElement(XMLElements.EXAMPLES);
		for (final example example : examples) {
			examplesElt.appendChild(getExampleElt(example, doc, e, tc));
		}
		return examplesElt;
	}

	public org.w3c.dom.Element getExampleElt(final example example, final Document doc, final ExecutableElement e,
			final TypeConverter tc) {
		final org.w3c.dom.Element exampleElt = doc.createElement(XMLElements.EXAMPLE);
		exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_CODE, example.value());
		if (!"".equals(example.var())) {
			exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_VAR, example.var());
		}
		if (!"".equals(example.equals())) {
			exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_EQUALS, example.equals());
		}
		if (!"".equals(example.isNot())) {
			exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_NOT, example.isNot());
		}
		if (!"".equals(example.raises())) {
			exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_RAISES, example.raises());
		}
		exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_TEST_ONLY, "" + example.isTestOnly());
		exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_EXECUTABLE, "" + example.isExecutable());
		if (!example.isExecutable()) {
			exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_TESTABLE, "false");
		} else {
			exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_TESTABLE, "" + example.test());
		}
		if (!"".equals(example.returnType())) {
			exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_TYPE, example.returnType());
		} else {
			if (e != null) {
				exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_TYPE, tc.getProperType(e.getReturnType().toString()));
			}
		}
		return exampleElt;
	}

	public org.w3c.dom.Element getConstantElt(final constant constant, final Document doc, final Element e,
			final Messager mes, final TypeConverter tc) {
		final org.w3c.dom.Element constantElt = doc.createElement(XMLElements.CONSTANT);

		constantElt.setAttribute(XMLElements.ATT_CST_NAME, PREFIX_CONSTANT + constant.value());
		// constantElt.setAttribute(XMLElements.ATT_CST_VALUE,
		// ((VariableElement)e).getConstantValue().toString());
		final Object valCst = ((VariableElement) e).getConstantValue();
		final String valCstStr = valCst == null ? "No Default Value" : valCst.toString();
		constantElt.setAttribute(XMLElements.ATT_CST_VALUE, valCstStr);

		String names = "";
		for (final String n : constant.altNames()) {
			names = "".equals(names) ? PREFIX_CONSTANT + n : names + "," + PREFIX_CONSTANT + n;
		}
		if (!"".equals(names))
			constantElt.setAttribute(XMLElements.ATT_CST_NAMES, names);

		constantElt.appendChild(getCategories(e, doc, doc.createElement(XMLElements.CATEGORIES), tc));

		final org.w3c.dom.Element docConstantElt =
				getDocElt(constant.doc(), doc, mes, e.getSimpleName().toString(), null, null);
		if (docConstantElt != null) {
			constantElt.appendChild(docConstantElt);
		}

		return constantElt;
	}

	public org.w3c.dom.Element getVarsElt(final vars varsAnnot, final Document doc, final Messager mes,
			final String skillName, final TypeConverter tc) {
		if (varsAnnot != null) {
			final org.w3c.dom.Element varsElt = doc.createElement(XMLElements.VARS);
			for (final var v : varsAnnot.value()) {
				final org.w3c.dom.Element varElt = doc.createElement(XMLElements.VAR);
				varElt.setAttribute(XMLElements.ATT_VAR_NAME, v.name());
				varElt.setAttribute(XMLElements.ATT_VAR_TYPE, tc.getTypeString(Integer.valueOf(v.type())));
				varElt.setAttribute(XMLElements.ATT_VAR_CONSTANT, "" + v.constant());

				final org.w3c.dom.Element docEltVar =
						getDocElt(v.doc(), doc, mes, "Var " + v.name() + " from " + skillName, tc, null);
				if (docEltVar != null) {
					varElt.appendChild(docEltVar);
				}

				String dependsOn = new String();
				for (final String dependElement : v.depends_on()) {
					dependsOn = ("".equals(dependsOn) ? "" : dependsOn + ",") + dependElement;
				}
				varElt.setAttribute(XMLElements.ATT_VAR_DEPENDS_ON, dependsOn);
				varsElt.appendChild(varElt);
			}
			return varsElt;
		}
		return null;
	}

	public org.w3c.dom.Element getActionElt(final action actionAnnot, final Document doc, final Messager mes,
			final Element e, final TypeConverter tc) {
		if (!(e instanceof ExecutableElement) || actionAnnot == null) { return null; }

		final ExecutableElement eltMethod = (ExecutableElement) e;
		final org.w3c.dom.Element actionElt = doc.createElement(XMLElements.ACTION);
		actionElt.setAttribute(XMLElements.ATT_ACTION_NAME, actionAnnot.name());
		actionElt.setAttribute(XMLElements.ATT_ACTION_RETURNTYPE,
				tc.getProperType(eltMethod.getReturnType().toString()));

		final org.w3c.dom.Element argsElt = doc.createElement(XMLElements.ARGS);
		for (final arg eltArg : actionAnnot.args()) {
			final org.w3c.dom.Element argElt = doc.createElement(XMLElements.ARG);
			argElt.setAttribute(XMLElements.ATT_ARG_NAME, eltArg.name());

			final String tabType = tc.getTypeString(eltArg.type());
			// for (int i = 0; i < eltArg.type().length; i++) {
			// tabType = tabType + (i < eltArg.type().length - 1 ? tc.getTypeString(eltArg.type()[i]) + ","
			// : tc.getTypeString(eltArg.type()[i]));
			// }
			argElt.setAttribute(XMLElements.ATT_ARG_TYPE, tabType);
			argElt.setAttribute(XMLElements.ATT_ARG_OPTIONAL, "" + eltArg.optional());
			final org.w3c.dom.Element docEltArg = getDocElt(eltArg.doc(), doc, mes,
					"Arg " + eltArg.name() + " from " + eltMethod.getSimpleName(), tc, null);
			if (docEltArg != null) {
				argElt.appendChild(docEltArg);
			}

			argsElt.appendChild(argElt);
		}
		actionElt.appendChild(argsElt);

		final org.w3c.dom.Element docEltAction =
				getDocElt(actionAnnot.doc(), doc, mes, eltMethod.getSimpleName().toString(), tc, null);
		if (docEltAction != null) {
			actionElt.appendChild(docEltAction);
		}

		return actionElt;
	}

	public org.w3c.dom.Element getFacetsElt(final facets facetsAnnot, final Document doc, final Messager mes,
			final String statName, final TypeConverter tc) {
		if (facetsAnnot == null) { return null; }

		final org.w3c.dom.Element facetsElt = doc.createElement(XMLElements.FACETS);

		for (final facet f : facetsAnnot.value()) {
			final org.w3c.dom.Element facetElt = doc.createElement(XMLElements.FACET);
			facetElt.setAttribute(XMLElements.ATT_FACET_NAME, f.name());
			facetElt.setAttribute(XMLElements.ATT_FACET_TYPE, tc.getTypeString(f.type()));
			facetElt.setAttribute(XMLElements.ATT_FACET_OPTIONAL, "" + f.optional());
			if (f.values().length != 0) {
				String valuesTaken = ", takes values in: {" + f.values()[0];
				for (int i = 1; i < f.values().length; i++) {
					valuesTaken += ", " + f.values()[i];
				}
				valuesTaken += "}";
				facetElt.setAttribute(XMLElements.ATT_FACET_VALUES, valuesTaken);
			}
			facetElt.setAttribute(XMLElements.ATT_FACET_OMISSIBLE,
					f.name().equals(facetsAnnot.omissible()) ? "true" : "false");
			final org.w3c.dom.Element docFacetElt =
					getDocElt(f.doc(), doc, mes, "Facet " + f.name() + " from Statement" + statName, tc, null);
			if (docFacetElt != null) {
				facetElt.appendChild(docFacetElt);
			}

			facetsElt.appendChild(facetElt);
		}
		return facetsElt;
	}

	public org.w3c.dom.Element getInsideElt(final inside insideAnnot, final Document doc, final TypeConverter tc) {
		if (insideAnnot == null) { return null; }

		final org.w3c.dom.Element insideElt = doc.createElement(XMLElements.INSIDE);

		final org.w3c.dom.Element symbolsElt = doc.createElement(XMLElements.SYMBOLS);
		for (final String sym : insideAnnot.symbols()) {
			final org.w3c.dom.Element symElt = doc.createElement(XMLElements.SYMBOL);
			symElt.setTextContent(sym);
			symbolsElt.appendChild(symElt);
		}
		insideElt.appendChild(symbolsElt);

		final org.w3c.dom.Element kindsElt = doc.createElement(XMLElements.KINDS);
		for (final int kind : insideAnnot.kinds()) {
			final org.w3c.dom.Element kindElt = doc.createElement(XMLElements.KIND);
			kindElt.setTextContent(tc.getSymbolKindStringFromISymbolKind(kind));
			kindsElt.appendChild(kindElt);
		}
		insideElt.appendChild(kindsElt);

		return insideElt;
	}

	public org.w3c.dom.Element getOperatorElement(final org.w3c.dom.Element operators, final String eltName) {
		final NodeList nL = operators.getElementsByTagName(XMLElements.OPERATOR);
		int i = 0;
		final boolean found = false;
		while (!found && i < nL.getLength()) {
			final org.w3c.dom.Element elt = (org.w3c.dom.Element) nL.item(i);
			if (eltName.equals(elt.getAttribute(XMLElements.ATT_OP_ID))) { return elt; }
			i++;
		}
		return null;
	}

	public org.w3c.dom.Element getCategories(final Element e, final Document doc,
			final org.w3c.dom.Element categoriesElt, final TypeConverter tc) {
		final ArrayList<String> categories = new ArrayList<String>();
		String[] categoriesTab = null;
		final NodeList nL = categoriesElt.getElementsByTagName(XMLElements.CATEGORY);
		for (int i = 0; i < nL.getLength(); i++) {
			categories.add(((org.w3c.dom.Element) nL.item(i)).getAttribute(XMLElements.ATT_CAT_ID));
		}

		// To be able to deal with various annotations....
		if (e.getAnnotation(operator.class) != null) {
			categoriesTab = e.getAnnotation(operator.class).category();
		} else if (e.getAnnotation(constant.class) != null) {
			categoriesTab = e.getAnnotation(constant.class).category();
		}

		if (e.getAnnotation(operator.class) != null && e.getAnnotation(operator.class).category().length > 0
				|| e.getAnnotation(constant.class) != null && e.getAnnotation(constant.class).category().length > 0) {
			if (categoriesTab != null)
				for (final String categoryName : categoriesTab) {
					if (!categories.contains(categoryName)) {
						categories.add(categoryName);

						final org.w3c.dom.Element catElt = doc.createElement(XMLElements.CATEGORY);
						catElt.setAttribute(XMLElements.ATT_CAT_ID, categoryName);
						appendChild(categoriesElt, catElt);
					}
				}
		} else {
			if (!categories.contains(tc.getProperCategory(e.getEnclosingElement().getSimpleName().toString()))) {
				final org.w3c.dom.Element catElt = doc.createElement(XMLElements.CATEGORY);
				catElt.setAttribute(XMLElements.ATT_CAT_ID,
						tc.getProperCategory(e.getEnclosingElement().getSimpleName().toString()));
				appendChild(categoriesElt, catElt);
			}
		}

		// We had a particular category that is read from the iterator
		if (e.getAnnotation(operator.class) != null && e.getAnnotation(operator.class).iterator()) {
			final org.w3c.dom.Element catElt = doc.createElement(XMLElements.CATEGORY);
			catElt.setAttribute(XMLElements.ATT_CAT_ID, IOperatorCategory.ITERATOR);
			appendChild(categoriesElt, catElt);
		}

		return categoriesElt;
	}

	public org.w3c.dom.Element getCategories(final Element e, final Document doc, final TypeConverter tc) {
		final org.w3c.dom.Element categoriesElt = doc.createElement(XMLElements.OPERATORS_CATEGORIES);

		return getCategories(e, doc, categoriesElt, tc);
	}

	public org.w3c.dom.Element getConcepts(final Element e, final Document doc, final org.w3c.dom.Element conceptElt,
			final TypeConverter tc) {
		final ArrayList<String> concepts = new ArrayList<String>();
		String[] conceptsTab = null;
		final NodeList nL = conceptElt.getElementsByTagName(XMLElements.CONCEPT);
		for (int i = 0; i < nL.getLength(); i++) {
			concepts.add(((org.w3c.dom.Element) nL.item(i)).getAttribute(XMLElements.ATT_CAT_ID));
		}

		// To be able to deal with various annotations....
		if (e.getAnnotation(operator.class) != null) {
			conceptsTab = e.getAnnotation(operator.class).concept();
		} else if (e.getAnnotation(constant.class) != null) {
			conceptsTab = e.getAnnotation(constant.class).concept();
		} else if (e.getAnnotation(type.class) != null) {
			conceptsTab = e.getAnnotation(type.class).concept();
		} else if (e.getAnnotation(species.class) != null) {
			conceptsTab = e.getAnnotation(species.class).concept();
		} else if (e.getAnnotation(symbol.class) != null) {
			conceptsTab = e.getAnnotation(symbol.class).concept();
		} else if (e.getAnnotation(skill.class) != null) {
			conceptsTab = e.getAnnotation(skill.class).concept();
		}

		if (e.getAnnotation(operator.class) != null && e.getAnnotation(operator.class).concept().length > 0
				|| e.getAnnotation(constant.class) != null && e.getAnnotation(constant.class).concept().length > 0
				|| e.getAnnotation(type.class) != null && e.getAnnotation(type.class).concept().length > 0
				|| e.getAnnotation(skill.class) != null && e.getAnnotation(skill.class).concept().length > 0
				|| e.getAnnotation(species.class) != null && e.getAnnotation(species.class).concept().length > 0
				|| e.getAnnotation(symbol.class) != null && e.getAnnotation(symbol.class).concept().length > 0) {
			if (conceptsTab != null)
				for (final String conceptName : conceptsTab) {
					if (!concepts.contains(conceptName)) {
						concepts.add(conceptName);

						final org.w3c.dom.Element catElt = doc.createElement(XMLElements.CONCEPT);
						catElt.setAttribute(XMLElements.ATT_CAT_ID, conceptName);
						conceptElt.appendChild(catElt);
					}
				}
		}

		// We had a particular category that is red from the iterator
		if (e.getAnnotation(operator.class) != null && e.getAnnotation(operator.class).iterator()) {
			final org.w3c.dom.Element catElt = doc.createElement(XMLElements.CONCEPT);
			catElt.setAttribute(XMLElements.ATT_CAT_ID, IOperatorCategory.ITERATOR);
			conceptElt.appendChild(catElt);
		}

		return conceptElt;
	}

}