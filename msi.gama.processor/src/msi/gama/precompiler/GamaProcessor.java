/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.precompiler;

import java.io.*;
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
import msi.gama.precompiler.util.MathUtils;

@SupportedAnnotationTypes({ "msi.gama.precompiler.GamlAnnotations.*" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class GamaProcessor extends AbstractProcessor {

	public final static String GRAMMAR = "std.gaml";
	public final static String SKILLS = "skills.properties";
	public final static String UNARIES = "unaries.properties";
	public final static String BINARIES = "binaries.properties";
	public final static String TYPES = "types.properties";
	public final static String SYMBOLS = "symbols.properties";
	public final static String CHILDREN = "children.properties";
	public final static String FACETS = "facets.properties";
	public final static String KINDS = "kinds.properties";
	public final static String FACTORIES = "factories.properties";
	public final static String SPECIES = "species.properties";
	public final static String VARS = "vars.properties";

	// here util.MathUtils is a copy of msi.gama.util.MathUtils
	private final static Set<String> BUILT_IN_UNIT = MathUtils.UNITS.keySet();
	private final static List<String> BUILT_IN_KEYWORDS = Arrays.asList("entities", "abstract");
	private final static List<String> FORBIDDEN_KEYWORDS = Arrays.asList("model", "set");
	private final static List<String> FORBIDDEN_OPERATORS = Arrays.asList("<->", "@", "div", ">=",
		"^", "**", "!", "or", "*", "+", ".", "/", "and", "-", "not", "//", "<>", "<=", "!=", ":",
		"::", "|", "?", ">", "=", "<", "3d");
	private static final String[] FILES = new String[] { SKILLS, UNARIES, BINARIES, TYPES, SYMBOLS,
		CHILDREN, FACETS, KINDS, FACTORIES, SPECIES, VARS };
	private final List<String> lreserved = new ArrayList<String>();

	private final Map<String, MultiProperties> store = new HashMap<String, MultiProperties>();

	public GamaProcessor() {}

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		for ( String f : FILES ) {
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
		store.get(VARS).put("reserved", svars);
	}

	private void processArgs(final Set<? extends Element> set) {
		Set<String> svars = new HashSet<String>();
		for ( Element e : set ) {
			for ( String a : e.getAnnotation(args.class).value() ) {
				svars.add(a);
			}
		}
		store.get(VARS).put("actions_args", svars);
	}

	private void processAction(final Set<? extends Element> set) {
		Set<String> sactions = new HashSet<String>();
		for ( Element e : set ) {
			sactions.add(e.getAnnotation(action.class).value());
		}
		store.get(VARS).put("actions", sactions);
	}

	private void processVar(final Set<? extends Element> set) {
		Set<String> svars = new HashSet<String>();
		for ( Element e : set ) {
			for ( var v : e.getAnnotation(vars.class).value() ) {
				svars.add(v.name());
			}
		}
		store.get(VARS).put("vars", svars);
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
					store.get(FACTORIES).put(String.valueOf(kind), className);
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
					store.get(SKILLS).put(className, skill_name);
				}
			} /* else */

			species species_annot = e.getAnnotation(species.class);
			if ( species_annot != null ) {
				store.get(SKILLS).put(className, species_annot.value());
				store.get(SPECIES).put(className, species_annot.value());
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
					store.get(TYPES).put(className, typeName);
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
		if ( args.size() == 0 && !isStatic ) { return UNARIES; }
		boolean contextual = args.get(0).asType().toString().contains("IScope");
		if ( args.size() == 1 && !isStatic && contextual ) { return UNARIES; }
		if ( args.size() == 1 && isStatic ) { return UNARIES; }
		if ( args.size() == 2 && isStatic && contextual ) { return UNARIES; }

		// Etendre cette m�thode en faisant un check plus approfondi et en
		// �mettant une erreur si la m�thode est mal form�e.
		// L'ensemble des annotations pourrait etre check� de cette facon.

		return BINARIES;
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
				store.get(FACETS).put(k, facet_names);
				store.get(SYMBOLS).put(className, k);
				store.get(KINDS).put(String.valueOf(mirror.kind()), className);
			}
			store.get(VARS).put("facets_values", facet_values);
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
						store.get(CHILDREN).put(p, k);
					}
					int[] parent_kinds = parent_decl.kinds();
					for ( int i : parent_kinds ) {
						Set<String> classes_in_kind = store.get(KINDS).get(String.valueOf(i));
						if ( classes_in_kind != null ) {
							for ( String class_in_kind : classes_in_kind ) {
								Set<String> keywords_in_kind =
									store.get(SYMBOLS).get(class_in_kind);
								for ( String keyword_in_kind : keywords_in_kind ) {
									store.get(CHILDREN).put(keyword_in_kind, k);
								}
							}
						}
					}
				}
			}
		}
	}

	private void dumpFiles() {
		for ( String file : FILES ) {
			store.get(file).store(createWriter(file));
		}

		final PrintWriter grammarWriter = new PrintWriter(createWriter(GRAMMAR));
		grammarWriter.append("model std").append('\n').println("_gaml {");
		printProps(grammarWriter, "Facets keywords", "_facet", store.get(FACETS));
		grammarWriter.println("// Symbol keywords");
		for ( String s : store.get(SYMBOLS).values() ) {
			if ( !FORBIDDEN_KEYWORDS.contains(s) ) {
				grammarWriter.append("\t_keyword ").append(s).println(" {");
				printGamlList(grammarWriter, "\t\t_facets", store.get(FACETS).get(s));
				printGamlList(grammarWriter, "\t\t_children", store.get(CHILDREN).get(s));
				grammarWriter.println("\t}\n");
			}
		}
		for ( String s : BUILT_IN_KEYWORDS ) {
			grammarWriter.append("\t_keyword ").append(s).println(" {");
			printGamlList(grammarWriter, "\t\t_children", store.get(CHILDREN).get(s));
			grammarWriter.println("\t}\n");
		}

		for ( String s : BUILT_IN_UNIT ) {
			grammarWriter.append("\t_unit ").append(s).println(";");
		}

		printProps(grammarWriter, "Binary keywords", "_binary", store.get(BINARIES));
		// shouldnt have doublons here:
		lreserved.addAll(FORBIDDEN_OPERATORS);
		printPropsNoDoublons(grammarWriter, "Reserved keywords (unaries)", "_reserved",
			store.get(UNARIES));
		printPropsNoDoublons(grammarWriter, "Reserved keywords (skills)", "_reserved",
			store.get(SKILLS));
		printPropsNoDoublons(grammarWriter, "Reserved keywords (types)", "_reserved",
			store.get(TYPES));
		printPropsNoDoublons(grammarWriter, "Reserved keywords (vars)", "_reserved",
			store.get(VARS));
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
	// Ajouter variables globales pr�d�finies

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
