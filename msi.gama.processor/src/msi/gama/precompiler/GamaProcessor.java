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

import java.io.*;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;
import javax.tools.*;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.type;

@SupportedAnnotationTypes({ "msi.gama.precompiler.GamlAnnotations.*" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class GamaProcessor extends AbstractProcessor {

	private final List<String> lreserved = new ArrayList<String>();

	private final Map<String, GamlProperties> store = new HashMap<String, GamlProperties>();

	public GamaProcessor() {}

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		for ( String f : GamlProperties.FILES ) {
			store.put(f, initProperties(f)); // TODO Necessary ??
		}
	}

	private GamlProperties initProperties(final String name) {
		GamlProperties prop = new GamlProperties();
		Filer filer = processingEnv.getFiler();

		try {
			FileObject f = filer.getResource(StandardLocation.SOURCE_OUTPUT, "", name);
			prop.load(f.openReader(true));
			// f.delete();
		} catch (IOException e) {}
		return prop;
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
		if ( env.processingOver() ) {
			dumpFiles();
		} else {

			processTypes(env.getElementsAnnotatedWith(type.class));
			processOperators(env.getElementsAnnotatedWith(operator.class));
			processSkills(env.getElementsAnnotatedWith(skill.class));
			Set<Element> symbols = (Set<Element>) env.getElementsAnnotatedWith(symbol.class);
			processSymbols(symbols);
			// processParents(symbols);
			symbols = new HashSet<Element>(env.getElementsAnnotatedWith(getter.class));
			symbols.addAll(env.getElementsAnnotatedWith(setter.class));
			symbols.addAll(env.getElementsAnnotatedWith(action.class));
			symbols.addAll(env.getElementsAnnotatedWith(skill.class));
			symbols.addAll(env.getElementsAnnotatedWith(species.class));
			processClasses(symbols);
			processFactories(env.getElementsAnnotatedWith(handles.class));
			// processVar(env.getElementsAnnotatedWith(vars.class));
			// processAction(env.getElementsAnnotatedWith(action.class));
			processArgs(env.getElementsAnnotatedWith(args.class));
			// processReserved(env.getElementsAnnotatedWith(reserved.class));

			if ( "true".equals(processingEnv.getOptions().get("doc")) ) {
				Messager m = processingEnv.getMessager();
				m.printMessage(Kind.ERROR, "Beginning of the documentation processing" +
					processingEnv.getOptions().get("doc"));

				new docProcessor(processingEnv).processDocXML(env, createWriter("doc.xml"));

				m.printMessage(Kind.NOTE, "End of the documentation processing");
			}

		}
		return false;
	}

	// private void processReserved(final Set<? extends Element> set) {
	// Set<String> svars = new HashSet<String>();
	// for ( Element e : set ) {
	// for ( String a : e.getAnnotation(reserved.class).value() ) {
	// svars.add(a);
	// }
	// }
	// store.get(GamlProperties.VARS).put("reserved", svars);
	// }

	private void processArgs(final Set<? extends Element> set) {
		Set<String> svars = new HashSet<String>();
		for ( Element e : set ) {
			for ( String a : e.getAnnotation(args.class).value() ) {
				svars.add(a);
			}
		}
		// store.get(GamlProperties.VARS).put("actions_args", svars);
	}

	// private void processAction(final Set<? extends Element> set) {
	// Set<String> sactions = new HashSet<String>();
	// for ( Element e : set ) {
	// sactions.add(e.getAnnotation(action.class).value());
	// }
	// store.get(GamlProperties.VARS).put("actions", sactions);
	// }

	// private void processVar(final Set<? extends Element> set) {
	// Set<String> svars = new HashSet<String>();
	// for ( Element e : set ) {
	// for ( var v : e.getAnnotation(vars.class).value() ) {
	// svars.add(v.name());
	// }
	// }
	// store.get(GamlProperties.VARS).put("vars", svars);
	// }

	private void processFactories(final Set<? extends Element> types) {
		for ( Element element : types ) {
			Element e = element;
			if ( e == null ) {
				continue;
			}
			String className = ((TypeElement) e).getQualifiedName().toString();
			handles fact_annot = e.getAnnotation(handles.class);
			if ( fact_annot != null ) {
				int[] kinds = fact_annot.value();
				for ( int kind : kinds ) {
					store.get(GamlProperties.FACTORIES).put(String.valueOf(kind), className);
				}
			}
		}
	}

	private void processSkills(final Set<? extends Element> skills) {
		for ( Element element : skills ) {
			Element e = element;
			String className = ((TypeElement) e).getQualifiedName().toString();
			skill skill_annot = e.getAnnotation(skill.class);
			String[] skill_names = skill_annot.value();
			String[] attach_to = skill_annot.attach_to();
			for ( String skill_name : skill_names ) {
				store.get(GamlProperties.SKILLS).put(className, skill_name);
				for ( String species_name : attach_to ) {
					store.get(GamlProperties.SKILLS).put(className, species_name);
				}
			}

		}
	}

	private void processClasses(final Set<? extends Element> types) {
		for ( Element element : types ) {
			Element e = element;
			if ( e == null ) {
				continue;
			}
			if ( !(e instanceof TypeElement) ) {
				e = e.getEnclosingElement();
			}
			String className = ((TypeElement) e).getQualifiedName().toString();

			species species_annot = e.getAnnotation(species.class);
			if ( species_annot != null ) {
				String species_name = species_annot.value();
				store.get(GamlProperties.SKILLS).put(className, species_name);
				store.get(GamlProperties.SPECIES).put(className, species_name);
				String[] skills = species_annot.skills();
				GamlProperties gp = store.get(GamlProperties.SKILLS);
				for ( String skill_name : skills ) {
					for ( Map.Entry<String, LinkedHashSet<String>> entry : gp.entrySet() ) {
						if ( entry.getValue().contains(skill_name) ) {
							entry.getValue().add(species_name);
						}
					}
					// store.get(GamlProperties.SPECIES_SKILLS).put(species_name, skill_name);
				}
			}

		}
	}

	private void processTypes(final Set<? extends Element> elements) {
		for ( Element element : elements ) {
			if ( element instanceof TypeElement ) {
				String className = ((TypeElement) element).getQualifiedName().toString();
				type type_annot = element.getAnnotation(type.class);
				if ( type_annot != null ) {
					String typeName = type_annot.value();
					String typeKind = String.valueOf(type_annot.kind());
					store.get(GamlProperties.TYPES).put(className, typeKind);
					store.get(GamlProperties.TYPES).put(className, typeName);
				}
			}
		}
	}

	private void processOperators(final Set<? extends Element> elements) {
		for ( Element element : elements ) {
			operator op = element.getAnnotation(operator.class);
			String[] op_names = op.value();
			String keyName = null;
			for ( String opName : op_names ) {
				// String storeName = getStoreNameForOperator((ExecutableElement) element);
				TypeElement theClass = (TypeElement) element.getEnclosingElement();
				NestingKind kind = theClass.getNestingKind();
				if ( kind == NestingKind.TOP_LEVEL ) {
					keyName = theClass.getQualifiedName().toString();
				} else {
					String simpleName = theClass.getSimpleName().toString();
					TypeElement theEnclosingClass = (TypeElement) theClass.getEnclosingElement();
					String enclosingName = theEnclosingClass.getQualifiedName().toString();
					keyName = enclosingName + "$" + simpleName;
				}
				store.get(GamlProperties.OPERATORS).put(keyName, opName);
			}
		}
	}

	// private String getStoreNameForOperator(final ExecutableElement element) {
	// Set<Modifier> m = element.getModifiers();
	// List<? extends VariableElement> args = element.getParameters();
	// boolean isStatic = m.contains(Modifier.STATIC);
	// if ( args.size() == 0 && !isStatic ) { return GamlProperties.UNARIES; }
	// boolean contextual = args.get(0).asType().toString().contains("IScope");
	// if ( args.size() == 1 && !isStatic && contextual ) { return GamlProperties.UNARIES; }
	// if ( args.size() == 1 && isStatic ) { return GamlProperties.UNARIES; }
	// if ( args.size() == 2 && isStatic && contextual ) { return GamlProperties.UNARIES; }
	// return GamlProperties.BINARIES;
	// }

	private void processSymbols(final Set<? extends Element> types) {
		// We loop on the classes annotated with "symbol"
		// Set<String> facet_values = new HashSet<String>();

		for ( Element element : types ) {
			// boolean isDefinition = false;
			String className = ((TypeElement) element).getQualifiedName().toString();
			symbol mirror = element.getAnnotation(symbol.class);
			// Set<String> facet_names = new HashSet<String>();
			// facets facets_decl = element.getAnnotation(facets.class);
			// if ( facets_decl != null ) {
			// String omissibleFacet = facets_decl.omissible();
			// facet[] all_facets = facets_decl.value();
			// for ( facet f : all_facets ) {
			// String name = f.name();
			// if ( name.equals(omissibleFacet) ) {
			// String type = f.type()[0];
			// isDefinition = isDefinition(name, type);
			// }
			// for ( String v : f.values() ) {
			// facet_values.add(v);
			// }
			// }
			// }
			store.get(GamlProperties.SYMBOLS).put(className, String.valueOf(mirror.kind()));
			for ( String k : mirror.name() ) {
				store.get(GamlProperties.SYMBOLS).put(className, k);
			}

			// store.get(GamlProperties.KINDS).put(kind, className);
			// Set<String> type_names = store.get(GamlProperties.TYPES_NAMES).get(kind);
			// if ( type_names != null ) {
			// for ( String k : type_names ) {
			// store.get(isDefinition ? GamlProperties.DEFINITIONS : GamlProperties.SYMBOLS)
			// .put(className, k);
			// }
			// }
			// store.get(GamlProperties.VARS).put("facets_values", facet_values);
		}
	}

	//
	// boolean isDefinition(final String facetName, final String facetType) {
	// if ( !facetName.equals("name") && !facetName.equals("var") ) { return false; }
	// if ( !facetType.equals(IFacetType.ID) && !facetType.equals(IFacetType.NEW_TEMP_ID) &&
	// !facetType.equals(IFacetType.NEW_VAR_ID) && !facetType.equals(IFacetType.LABEL) ) { return
	// false; }
	// return true;
	// }

	// private void processParents(final Set<? extends Element> types) {
	// // We loop on the classes annotated with "symbol" to establish the parent/children
	// // relationships
	// for ( Element element : types ) {
	// symbol mirror = element.getAnnotation(symbol.class);
	// inside parent_decl = element.getAnnotation(inside.class);
	// String[] k_names = mirror.name();
	// if ( parent_decl != null ) {
	// for ( String k : k_names ) {
	// String[] parents = parent_decl.symbols();
	// for ( String p : parents ) {
	// store.get(GamlProperties.CHILDREN).put(p, k);
	// }
	// int[] parent_kinds = parent_decl.kinds();
	// for ( int i : parent_kinds ) {
	// Set<String> classes_in_kind =
	// store.get(GamlProperties.KINDS).get(String.valueOf(i));
	// if ( classes_in_kind != null ) {
	// for ( String class_in_kind : classes_in_kind ) {
	// Set<String> keywords_in_kind =
	// store.get(GamlProperties.SYMBOLS).get(class_in_kind);
	// if ( keywords_in_kind == null ) {
	// keywords_in_kind =
	// store.get(GamlProperties.DEFINITIONS).get(class_in_kind);
	// }
	// for ( String keyword_in_kind : keywords_in_kind ) {
	// store.get(GamlProperties.CHILDREN).put(keyword_in_kind, k);
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// }

	private void dumpFiles() {
		for ( String file : GamlProperties.FILES ) {
			store.get(file).store(createWriter(file));
		}
		// final PrintWriter grammarWriter = new PrintWriter(createWriter(GamlProperties.GRAMMAR));
		// grammarWriter.append("model std").append('\n').println("_gaml {");
		//
		// printPropsNoDoublons(grammarWriter, "Reserved keywords (skills)", "_reserved",
		// store.get(GamlProperties.SKILLS));
		// printPropsNoDoublons(grammarWriter, "Reserved keywords (types)", "_reserved",
		// store.get(GamlProperties.TYPES));
		// // printPropsNoDoublons(grammarWriter, "Reserved keywords (vars)", "_reserved",
		// // store.get(GamlProperties.VARS));
		// printPropsNoDoublons(grammarWriter, "Reserved keywords (species)", "_reserved",
		// store.get(GamlProperties.SPECIES));
		// grammarWriter.println("}");
		// grammarWriter.close();
	}

	// private void printPropsNoDoublons(final PrintWriter writer, final String t, final String
	// prop,
	// final GamlProperties map) {
	// writer.append("\n//").println(t);
	//
	// for ( String s : map.keySet() ) {
	// writer.println("// " + s);
	// for ( String name : map.get(s) ) {
	// if ( !lreserved.contains(name) ) {
	// writer.append("\t ").append(prop).append(" &").append(name).println("&;");
	// lreserved.add(s);
	// }
	// }
	// }

	// for ( String s : map.values() ) {
	// if ( !lreserved.contains(s) ) {
	// // exclude the forbidden name & doublons (example: unaries->casting & types)
	// writer.append("\t ").append(prop).append(" &").append(s).println("&;");
	// lreserved.add(s);
	// }
	// }
	// }

	//
	// private void printProps(final PrintWriter writer, final String t, final String prop,
	// final GamlProperties map) {
	// writer.append("\n//").println(t);
	// for ( String s : map.values() ) {
	// if ( s != null && !FORBIDDEN_OPERATORS.contains(s) ) {
	// writer.append("\t ").append(prop).append(" &").append(s).println("&;");
	// }
	// }
	// }

	private Writer createWriter(final String name) {
		FileObject file = null;
		Filer filer = processingEnv.getFiler();
		try {
			file = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", name, new Element[] {});
		} catch (FilerException e2) {
			try {
				file = filer.getResource(StandardLocation.SOURCE_OUTPUT, "", name);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		try {
			return file.openWriter();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
