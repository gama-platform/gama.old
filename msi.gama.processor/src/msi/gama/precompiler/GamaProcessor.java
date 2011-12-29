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
import java.lang.reflect.Field;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.*;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.reserved;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;

@SupportedAnnotationTypes({ "msi.gama.precompiler.GamlAnnotations.*" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class GamaProcessor extends AbstractProcessor {

	static {

		for ( final Field f : IUnits.class.getDeclaredFields() ) {
			try {
				if ( f.getType().equals(double.class) ) {
					IUnits.UNITS.put(f.getName(), f.getDouble(IUnits.class));
				}
			} catch (final IllegalArgumentException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	// here util.MathUtils is a copy of msi.gama.util.MathUtils
	private final static Set<String> BUILT_IN_UNIT = IUnits.UNITS.keySet();
	private final static List<String> BUILT_IN_KEYWORDS = Arrays.asList("entities", "abstract");
	private final static List<String> FORBIDDEN_KEYWORDS = Arrays.asList("model", "set");
	private final static List<String> FORBIDDEN_OPERATORS = Arrays.asList("<->", "@", "div", ">=",
		"^", "**", "!", "or", "*", "+", ".", "/", "and", "-", "not", "//", "<>", "<=", "!=", ":",
		"::", "|", "?", ">", "=", "<", "3d");
	private final List<String> lreserved = new ArrayList<String>();

	private final Map<String, MultiProperties> store = new HashMap<String, MultiProperties>();

	public GamaProcessor() {}

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		for ( String f : MultiProperties.FILES ) {
			store.put(f, initProperties(f));
		}
	}

	private MultiProperties initProperties(final String name) {
		MultiProperties prop = new MultiProperties();
		Filer filer = processingEnv.getFiler();
		try {
			prop.load(filer.getResource(StandardLocation.SOURCE_OUTPUT, "", name).openReader(true));
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
			@SuppressWarnings("unchecked")
			Set<Element> symbols = (Set<Element>) env.getElementsAnnotatedWith(symbol.class);
			processSymbols(symbols);
			processParents(symbols);
			symbols = new HashSet<Element>(env.getElementsAnnotatedWith(getter.class));
			symbols.addAll(env.getElementsAnnotatedWith(setter.class));
			symbols.addAll(env.getElementsAnnotatedWith(action.class));
			symbols.addAll(env.getElementsAnnotatedWith(skill.class));
			symbols.addAll(env.getElementsAnnotatedWith(species.class));
			processClasses(symbols);
			processFactories(env.getElementsAnnotatedWith(handles.class));
			processVar(env.getElementsAnnotatedWith(vars.class));
			processAction(env.getElementsAnnotatedWith(action.class));
			processArgs(env.getElementsAnnotatedWith(args.class));
			processReserved(env.getElementsAnnotatedWith(reserved.class));
		}
		return false;
	}

	private void processReserved(final Set<? extends Element> set) {
		Set<String> svars = new HashSet<String>();
		for ( Element e : set ) {
			for ( String a : e.getAnnotation(reserved.class).value() ) {
				svars.add(a);
			}
		}
		store.get(MultiProperties.VARS).put("reserved", svars);
	}

	private void processArgs(final Set<? extends Element> set) {
		Set<String> svars = new HashSet<String>();
		for ( Element e : set ) {
			for ( String a : e.getAnnotation(args.class).value() ) {
				svars.add(a);
			}
		}
		store.get(MultiProperties.VARS).put("actions_args", svars);
	}

	private void processAction(final Set<? extends Element> set) {
		Set<String> sactions = new HashSet<String>();
		for ( Element e : set ) {
			sactions.add(e.getAnnotation(action.class).value());
		}
		store.get(MultiProperties.VARS).put("actions", sactions);
	}

	private void processVar(final Set<? extends Element> set) {
		Set<String> svars = new HashSet<String>();
		for ( Element e : set ) {
			for ( var v : e.getAnnotation(vars.class).value() ) {
				svars.add(v.name());
			}
		}
		store.get(MultiProperties.VARS).put("vars", svars);
	}

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
					store.get(MultiProperties.FACTORIES).put(String.valueOf(kind), className);
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
			skill skill_annot = e.getAnnotation(skill.class);
			if ( skill_annot != null ) {
				String[] skill_names = skill_annot.value();
				for ( String skill_name : skill_names ) {
					store.get(MultiProperties.SKILLS).put(className, skill_name);
				}
			} /* else */

			species species_annot = e.getAnnotation(species.class);
			if ( species_annot != null ) {
				store.get(MultiProperties.SKILLS).put(className, species_annot.value());
				store.get(MultiProperties.SPECIES).put(className, species_annot.value());
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
					store.get(MultiProperties.TYPES).put(className, typeName);
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
				String storeName = getStoreNameForOperator((ExecutableElement) element);
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
				store.get(storeName).put(keyName, opName);
			}
		}
	}

	private String getStoreNameForOperator(final ExecutableElement element) {
		Set<Modifier> m = element.getModifiers();
		List<? extends VariableElement> args = element.getParameters();
		boolean isStatic = m.contains(Modifier.STATIC);
		if ( args.size() == 0 && !isStatic ) { return MultiProperties.UNARIES; }
		boolean contextual = args.get(0).asType().toString().contains("IScope");
		if ( args.size() == 1 && !isStatic && contextual ) { return MultiProperties.UNARIES; }
		if ( args.size() == 1 && isStatic ) { return MultiProperties.UNARIES; }
		if ( args.size() == 2 && isStatic && contextual ) { return MultiProperties.UNARIES; }

		// Etendre cette mï¿½thode en faisant un check plus approfondi et en
		// ï¿½mettant une erreur si la mï¿½thode est mal formï¿½e.
		// L'ensemble des annotations pourrait etre checkï¿½ de cette facon.

		return MultiProperties.BINARIES;
	}

	private void processSymbols(final Set<? extends Element> types) {
		// We loop on the classes annotated with "symbol"
		Set<String> facet_values = new HashSet<String>();
		for ( Element element : types ) {
			String className = ((TypeElement) element).getQualifiedName().toString();
			symbol mirror = element.getAnnotation(symbol.class);
			Set<String> facet_names = new HashSet<String>();
			facets facets_decl = element.getAnnotation(facets.class);
			if ( facets_decl != null ) {
				facet[] all_facets = facets_decl.value();
				for ( facet f : all_facets ) {
					String name = f.name();
					if ( !name.isEmpty() ) {
						facet_names.add(name);
					}
					for ( String v : f.values() ) {
						facet_values.add(v);
					}
				}
			}
			String[] k_names = mirror.name();
			for ( String k : k_names ) {
				store.get(MultiProperties.FACETS).put(k, facet_names);
				store.get(MultiProperties.SYMBOLS).put(className, k);
				store.get(MultiProperties.KINDS).put(String.valueOf(mirror.kind()), className);
			}
			store.get(MultiProperties.VARS).put("facets_values", facet_values);
		}
	}

	private void processParents(final Set<? extends Element> types) {
		// We loop on the classes annotated with "symbol" to establish the parent/children
		// relationships
		for ( Element element : types ) {
			symbol mirror = element.getAnnotation(symbol.class);
			inside parent_decl = element.getAnnotation(inside.class);
			String[] k_names = mirror.name();
			if ( parent_decl != null ) {
				for ( String k : k_names ) {
					String[] parents = parent_decl.symbols();
					for ( String p : parents ) {
						store.get(MultiProperties.CHILDREN).put(p, k);
					}
					int[] parent_kinds = parent_decl.kinds();
					for ( int i : parent_kinds ) {
						Set<String> classes_in_kind =
							store.get(MultiProperties.KINDS).get(String.valueOf(i));
						if ( classes_in_kind != null ) {
							for ( String class_in_kind : classes_in_kind ) {
								Set<String> keywords_in_kind =
									store.get(MultiProperties.SYMBOLS).get(class_in_kind);
								for ( String keyword_in_kind : keywords_in_kind ) {
									store.get(MultiProperties.CHILDREN).put(keyword_in_kind, k);
								}
							}
						}
					}
				}
			}
		}
	}

	private void dumpFiles() {
		for ( String file : MultiProperties.FILES ) {
			store.get(file).store(createWriter(file));
		}

		final PrintWriter grammarWriter = new PrintWriter(createWriter(MultiProperties.GRAMMAR));
		grammarWriter.append("model std").append('\n').println("_gaml {");
		printProps(grammarWriter, "Facets keywords", "_facet", store.get(MultiProperties.FACETS));
		grammarWriter.println("// Symbol keywords");
		for ( String s : store.get(MultiProperties.SYMBOLS).values() ) {
			if ( !FORBIDDEN_KEYWORDS.contains(s) ) {
				grammarWriter.append("\t_keyword ").append(s).println(" {");
				printGamlList(grammarWriter, "\t\t_facets", store.get(MultiProperties.FACETS)
					.get(s));
				printGamlList(grammarWriter, "\t\t_children", store.get(MultiProperties.CHILDREN)
					.get(s));
				grammarWriter.println("\t}\n");
			}
		}
		for ( String s : BUILT_IN_KEYWORDS ) {
			grammarWriter.append("\t_keyword ").append(s).println(" {");
			printGamlList(grammarWriter, "\t\t_children", store.get(MultiProperties.CHILDREN)
				.get(s));
			grammarWriter.println("\t}\n");
		}

		for ( String s : BUILT_IN_UNIT ) {
			grammarWriter.append("\t_unit ").append(s).println(";");
		}

		printProps(grammarWriter, "Binary keywords", "_binary", store.get(MultiProperties.BINARIES));
		// shouldnt have doublons here:
		lreserved.addAll(FORBIDDEN_OPERATORS);
		printPropsNoDoublons(grammarWriter, "Reserved keywords (unaries)", "_reserved",
			store.get(MultiProperties.UNARIES));
		printPropsNoDoublons(grammarWriter, "Reserved keywords (skills)", "_reserved",
			store.get(MultiProperties.SKILLS));
		printPropsNoDoublons(grammarWriter, "Reserved keywords (types)", "_reserved",
			store.get(MultiProperties.TYPES));
		printPropsNoDoublons(grammarWriter, "Reserved keywords (vars)", "_reserved",
			store.get(MultiProperties.VARS));
		grammarWriter.println("}");
		grammarWriter.close();
	}

	private void printGamlList(final PrintWriter writer, final String string, final Set<String> set) {
		if ( set == null || set.isEmpty() ) { return; }
		writer.append(string).append(" [").append(MultiProperties.toStringWoSet(set)).println("]");
	}

	private void printPropsNoDoublons(final PrintWriter writer, final String t, final String prop,
		final MultiProperties map) {
		writer.append("\n//").println(t);
		for ( String s : map.values() ) {
			if ( !lreserved.contains(s) ) {
				// exclude the forbidden name & doublons (example: unaries->casting & types)
				writer.append("\t ").append(prop).append(" ").append(s).println(";");
				lreserved.add(s);
			}
		}
	}

	private void printProps(final PrintWriter writer, final String t, final String prop,
		final MultiProperties map) {
		writer.append("\n//").println(t);
		for ( String s : map.values() ) {
			if ( !FORBIDDEN_OPERATORS.contains(s) ) { // exclude the forbidden name
				writer.append("\t ").append(prop).append(" ").append(s).println(";");
			}
		}
	}

	// Ajouter reserved actions (+ facettes??)
	// Ajouter variables globales prï¿½dï¿½finies

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
