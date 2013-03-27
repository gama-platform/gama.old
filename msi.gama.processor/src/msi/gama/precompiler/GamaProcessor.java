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

import static msi.gama.precompiler.GamlProperties.GAML;
import static msi.gama.precompiler.JavaWriter.*;
import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.Diagnostic.Kind;
import javax.tools.*;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class GamaProcessor extends AbstractProcessor {

	class Pair {

		String key, value;

		Pair(final String s1, final String s2) {
			key = s1;
			value = s2;
		}
	}

	private GamlProperties gp;
	// JavaAgentBaseWriter jabw;

	GamlDocProcessor docProc;

	private static StandardLocation OUT = StandardLocation.SOURCE_OUTPUT;

	static final Class[] classes = new Class[] { symbol.class, factory.class, species.class,
		skill.class, getter.class, setter.class, action.class, type.class, operator.class,
		vars.class, display.class };

	final static Set<String> annotNames = new HashSet();

	static {
		for ( Class c : classes ) {
			annotNames.add(c.getCanonicalName());
		}
	}

	@Override
	public synchronized void init(final ProcessingEnvironment pe) {
		super.init(pe);
		try {
			gp = new GamlProperties(pe.getFiler().getResource(OUT, "", GAML).openReader(true));
		} catch (Exception e) {
			gp = new GamlProperties();
		}
		// jabw = new JavaAgentBaseWriter(processingEnv);
		docProc = new GamlDocProcessor(processingEnv);
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return annotNames;
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
		if ( !env.processingOver() ) {
			write(env, factory.class, GamlProperties.FACTORIES);
			processFactories(env);
			processTypes(env);
			processSpecies(env);
			processSkills(env);
			processOperators(env);
			processActions(env);
			processSymbols(env);
			processVars(env);
			processDisplays(env);

			gp.store(createWriter(GAML));
			Writer w = createSourceWriter();
			if ( w != null ) {
				// try {
				try {
					w.append(new JavaWriter().write("gaml.additions", gp));
				} catch (IOException e) {
					e.printStackTrace();
				}
				// w.flush();
				try {
					w.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// } catch (Exception e) {
				// processingEnv.getMessager().printMessage(Kind.ERROR,
				// "Exception while generating GamlAdditions file ");
				// }
			}
			if ( "true".equals(processingEnv.getOptions().get("doc")) ) {
				// new XMLWriter(processingEnv).write(createWriter("doc.xml"), gp);
				if ( docProc.firstParsing ) {
					docProc.processDocXML(env, createWriter("docGAMA.xml"));
					docProc.firstParsing = false;
				} else {
					processingEnv.getMessager().printMessage(Kind.NOTE,
						"Documentation file has already been produced");
				}
			}

			// Writer w2 = createAgentBaseSourceWriter();
			// if ( w2 != null ) {
			// try {
			// w2.append(jabw.write("gaml.additions", gp));
			// w2.flush();
			// w2.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// }
		}
		return true;
	}

	TypeMirror iSkill;

	TypeMirror getISkill() {
		if ( iSkill == null ) {
			iSkill =
				processingEnv.getElementUtils().getTypeElement("msi.gaml.skills.ISkill").asType();
		}
		return iSkill;
	}

	String rawNameOf(final Element e) {
		return rawNameOf(e.asType());
	}

	String rawNameOf(final TypeMirror t) {
		String string = processingEnv.getTypeUtils().erasure(t).toString();
		int i = string.indexOf('<');
		string = i > -1 ? string.substring(0, i) : string;
		return check(string.replace('$', '.'));
	}

	protected String check(final String clazz) {
		for ( int i = 0; i < IMPORTS.length; i++ ) {
			if ( clazz.startsWith(IMPORTS[i]) ) { return clazz
				.substring(clazz.lastIndexOf('.') + 1); }
		}

		return clazz;
	}

	/**
	 * Format : 0.type 1.type 2. contentType 3.varName 4.class 5.[facetName, facetValue]+ 6.getter
	 * 7.initializer(true/false)? 8.setter
	 * @param env
	 */
	private void processVars(final RoundEnvironment env) {
		Set<? extends Element> elements = env.getElementsAnnotatedWith(vars.class);
		for ( Element e : elements ) {
			boolean isISkill;
			TypeMirror clazz = e.asType();
			isISkill = processingEnv.getTypeUtils().isAssignable(clazz, getISkill());

			vars vars = e.getAnnotation(vars.class);
			for ( final var s : vars.value() ) {
				StringBuilder sb = new StringBuilder();
				String type = s.type();
				String contentType = s.of();
				// 0.type
				sb.append(VAR_PREFIX).append(type).append(SEP);
				// 1.contentType
				sb.append(contentType).append(SEP);
				// 2. keyType
				sb.append(s.index()).append(SEP);
				String varName = s.name();
				// 3.var name
				sb.append(varName).append(SEP);
				// 4. class of declaration
				sb.append(rawNameOf(e)).append(SEP);
				// 5. facets
				sb.append("type").append(',').append(type).append(',');
				sb.append("name").append(',').append(varName).append(',');
				sb.append("const").append(',').append(s.constant() ? "true" : "false");

				String[] dependencies = s.depends_on();
				if ( dependencies.length > 0 ) {
					String depends = "[";
					for ( int i = 0; i < dependencies.length; i++ ) {
						String string = dependencies[i];
						depends += string;
						if ( i < dependencies.length - 1 ) {
							depends += "COMMA";
						}
					}
					depends += "]";
					sb.append(',').append("depends_on").append(',').append(depends);
				}
				if ( !"".equals(contentType) ) {
					sb.append(',').append("of").append(',').append(contentType);
				}
				String init = s.init();
				if ( !"".equals(init) ) {
					sb.append(',').append("init").append(',').append(replaceCommas(init));
				}
				boolean found = false;
				sb.append(SEP);
				for ( final Element m : e.getEnclosedElements() ) {
					getter getter = m.getAnnotation(getter.class);
					if ( getter != null && getter.value().equals(varName) ) {
						ExecutableElement ex = (ExecutableElement) m;
						List<? extends VariableElement> argParams = ex.getParameters();
						String[] args = new String[argParams.size()];
						for ( int i = 0; i < args.length; i++ ) {
							args[i] = rawNameOf(argParams.get(i));
						}
						int n = args.length;
						boolean scope = n > 0 && args[0].contains("IScope");

						// method
						sb.append(ex.getSimpleName()).append(SEP);
						// retClass
						sb.append(rawNameOf(ex.getReturnType())).append(SEP);
						// dynamic ?
						sb.append(!scope && n > 0 || scope && n > 1).append(SEP);
						// field ?
						sb.append(!isISkill).append(SEP);
						// scope ?
						sb.append(scope);
						sb.append(SEP).append(getter.initializer());
						found = true;
						break;
					}
				}
				if ( !found ) {
					sb.append("null");
				}
				found = false;
				sb.append(SEP);
				for ( final Element m : e.getEnclosedElements() ) {
					setter setter = m.getAnnotation(setter.class);
					if ( setter != null && setter.value().equals(varName) ) {
						ExecutableElement ex = (ExecutableElement) m;
						List<? extends VariableElement> argParams = ex.getParameters();
						String[] args = new String[argParams.size()];
						for ( int i = 0; i < args.length; i++ ) {
							args[i] = rawNameOf(argParams.get(i));
						}
						int n = args.length;
						boolean scope = n > 0 && args[0].contains("IScope");
						// method
						sb.append(ex.getSimpleName()).append(SEP);
						// paramClass
						boolean isDynamic = !scope && n == 2 || scope && n == 3;
						sb.append(isDynamic ? args[!scope ? 1 : 2] : args[!scope ? 0 : 1]).append(
							SEP);
						// isDynamic
						sb.append(isDynamic).append(SEP);
						// scope ?
						sb.append(scope);
						found = true;
						break;
					}
				}
				if ( !found ) {
					sb.append("null");
				}
				sb.append(SEP);
				gp.put(sb.toString(), "");
			}
		}

	}

	private String replaceCommas(final String s) {
		return s.replace(",", "COMMA");
	}

	/**
	 * Computes the representation of symbols.
	 * Format: prefix 0.kind 1.class 2.remote 3.with_args 4.with_scope 5.with_sequence
	 * 6.symbols_inside 7.kinds_inside 8.nbFacets 9.[facet]* 10.omissible 11.[name$]*
	 * @param env
	 */
	private void processSymbols(final RoundEnvironment env) {
		Set<? extends Element> symbols = env.getElementsAnnotatedWith(symbol.class);
		for ( Element e : symbols ) {
			StringBuilder sb = new StringBuilder();
			symbol symbol = e.getAnnotation(symbol.class);
			// prefix
			sb.append(SYMBOL_PREFIX);
			// kind
			sb.append(symbol.kind()).append(SEP);
			// class
			sb.append(rawNameOf(e)).append(SEP);
			// remote
			sb.append(symbol.remote_context()).append(SEP);
			// with_args
			sb.append(symbol.with_args()).append(SEP);
			// with_scope
			sb.append(symbol.with_scope()).append(SEP);
			// with_sequence
			sb.append(symbol.with_sequence()).append(SEP);
			// unique_in_context
			sb.append(symbol.unique_in_context()).append(SEP);
			// name_unique
			sb.append(symbol.unique_name()).append(SEP);
			inside inside = e.getAnnotation(inside.class);
			// symbols_inside && kinds_inside
			if ( inside != null ) {
				String[] parentSymbols = inside.symbols();
				for ( int i = 0; i < parentSymbols.length; i++ ) {
					if ( i > 0 ) {
						sb.append(',');
					}
					sb.append(parentSymbols[i]);
				}
				sb.append(SEP);
				int[] parentKinds = inside.kinds();
				for ( int i = 0; i < parentKinds.length; i++ ) {
					if ( i > 0 ) {
						sb.append(',');
					}
					sb.append(parentKinds[i]);
				}
				sb.append(SEP);

			} else {
				sb.append(SEP).append(SEP);
			}
			facets facets = e.getAnnotation(facets.class);
			// facets
			if ( facets == null ) {
				sb.append('0').append(SEP).append(SEP).append(SEP).append(SEP).append(SEP);
			} else {
				sb.append(facets.value().length).append(SEP);
				sb.append(facetsToString(facets)).append(SEP);
				sb.append(facets.combinations().length).append(SEP);
				sb.append(combinationsFacetsToString(facets)).append(SEP);
				sb.append(facets.omissible()).append(SEP);
			}
			// combinations missing
			// if(facets.combinations() == null){sb.append('0').append(SEP).append(SEP);}
			// else
			// {
			// sb.append(facets.combinations().length).append(SEP);
			// sb.append(combinationsFacetsToString(facets)).append(SEP);
			// }
			// names
			for ( String s : symbol.name() ) {
				sb.append(s).append(SEP);
			}
			sb.setLength(sb.length() - 1);
			gp.put(sb.toString(), docToString(symbol.doc())); /* doc */
		}
	}

	/**
	 * Format 0.value 1.deprecated 2.returns 3.comment 4.nb_cases 5.[specialCases$]* 6.nb_examples
	 * 7.[examples$]*
	 * Uses its own separator (DOC_SEP)
	 * 
	 * @param docs an Array of @doc annotations (only the 1st is significant)
	 * @return aString containing the documentation formatted using the format above
	 */
	private String docToString(final doc[] docs) {
		if ( docs == null || docs.length == 0 ) { return ""; }
		doc doc = docs[0];
		StringBuilder sb = new StringBuilder();
		sb.append(doc.value()).append(DOC_SEP);
		sb.append(doc.deprecated()).append(DOC_SEP);
		sb.append(doc.returns()).append(DOC_SEP);
		sb.append(doc.comment()).append(DOC_SEP);
		String[] cases = doc.special_cases();
		sb.append(cases.length).append(DOC_SEP);
		for ( int i = 0; i < cases.length; i++ ) {
			sb.append(cases[i]).append(DOC_SEP);
		}
		String[] examples = doc.examples();
		sb.append(examples.length).append(DOC_SEP);
		for ( int i = 0; i < examples.length; i++ ) {
			sb.append(examples[i]).append(DOC_SEP);
		}
		String[] see = doc.see();
		sb.append(see.length).append(DOC_SEP);
		for ( int i = 0; i < see.length; i++ ) {
			sb.append(see[i]).append(DOC_SEP);
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	private String facetsToString(final facets facets) {
		StringBuilder sb = new StringBuilder();
		if ( facets.value() != null ) {
			for ( facet f : facets.value() ) {
				sb.append(facetToString(f)).append(SEP);
			}
			if ( facets.value().length > 0 ) {
				sb.setLength(sb.length() - 1);
			}
		}
		return sb.toString();
	}

	// Format: 1.name 2.[type,]+ 3.[value,]* 4.optional 5.doc
	private String facetToString(final facet facet) {
		StringBuilder sb = new StringBuilder();
		sb.append(facet.name()).append(SEP);
		sb.append(arrayToString(facet.type())).append(SEP);
		sb.append(arrayToString(facet.values())).append(SEP);
		sb.append(facet.optional()).append(SEP);
		sb.append(docToString(facet.doc()));
		return sb.toString();
	}

	private String combinationsFacetsToString(final facets facets) {
		StringBuilder sb = new StringBuilder();
		if ( facets.combinations() != null ) {
			for ( combination cf : facets.combinations() ) {
				sb.append(arrayToString(cf.value())).append(SEP);
			}
			// if ( facets.combinations().length > 0 ) {
			// sb.setLength(sb.length() - 1);
			// }
		}
		return sb.toString();
	}

	private String arrayToString(final String[] array) {
		if ( array.length == 0 ) { return ""; }
		StringBuilder sb = new StringBuilder();
		for ( String i : array ) {
			sb.append(replaceCommas(i)).append(",");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * Format : prefix 0.class 1.[handles,]* 2.[uses,]*
	 * Format : ]class$handles*$uses*
	 * @param env
	 */
	private void processFactories(final RoundEnvironment env) {
		Set<? extends Element> factories = env.getElementsAnnotatedWith(factory.class);
		for ( Element e : factories ) {
			factory factory = e.getAnnotation(factory.class);
			int[] hKinds = factory.handles();
			// int[] uKinds = factory.uses();
			StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(FACTORY_PREFIX);
			// class
			sb.append(rawNameOf(e)).append(SEP);
			// handles
			sb.append(String.valueOf(hKinds[0]));
			for ( int i = 1; i < hKinds.length; i++ ) {
				sb.append(',').append(String.valueOf(hKinds[i]));
			}
			// uses
			// if ( uKinds.length > 0 ) {
			// sb.append(SEP).append(String.valueOf(uKinds[0]));
			// for ( int i = 1; i < uKinds.length; i++ ) {
			// sb.append(',').append(String.valueOf(uKinds[i]));
			// }
			// }
			gp.put(sb.toString(), ""); /* doc ? */
		}
	}

	/**
	 * Format : prefix 0.name 1.class 2.[skill$]*
	 * @param env
	 */
	private void processSpecies(final RoundEnvironment env) {
		Set<? extends Element> species = env.getElementsAnnotatedWith(species.class);
		for ( Element e : species ) {
			species spec = e.getAnnotation(species.class);
			StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(SPECIES_PREFIX);
			// name
			sb.append(spec.name()).append(SEP);
			// class
			sb.append(rawNameOf(e));
			// skills
			for ( String s : spec.skills() ) {
				sb.append(SEP).append(s);
			}
			gp.put(sb.toString(), docToString(spec.doc())); /* doc */
		}
	}

	private void processDisplays(final RoundEnvironment env) {
		Set<? extends Element> displays = env.getElementsAnnotatedWith(display.class);
		for ( Element e : displays ) {
			display spec = e.getAnnotation(display.class);
			StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(DISPLAY_PREFIX);
			// name
			sb.append(spec.value()).append(SEP);
			// class
			sb.append(rawNameOf(e)).append(SEP);
			// skills

			gp.put(sb.toString(), ""); /* doc */
		}
	}

	/**
	 * Format : prefix 0.name 1.class 2.[species$]*
	 * @param env
	 */
	private void processSkills(final RoundEnvironment env) {
		Set<? extends Element> skills = env.getElementsAnnotatedWith(skill.class);
		for ( Element e : skills ) {
			skill skill = e.getAnnotation(skill.class);
			StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(SKILL_PREFIX);
			// name
			sb.append(skill.name()).append(SEP);
			// class
			sb.append(rawNameOf(e));
			// species
			for ( String s : skill.attach_to() ) {
				sb.append(SEP).append(s);
			}
			gp.put(sb.toString(), docToString(skill.doc())); /* doc */
		}
	}

	/**
	 * Format : prefix 0.name 1.id 2.varKind 3.class 4.[wrapped_class$]+
	 * @param env
	 */
	private void processTypes(final RoundEnvironment env) {
		Set<? extends Element> types = env.getElementsAnnotatedWith(type.class);
		for ( Element e : types ) {
			type t = e.getAnnotation(type.class);

			StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(TYPE_PREFIX);
			// name
			sb.append(t.name()).append(SEP);
			// id
			sb.append(t.id()).append(SEP);
			// kind
			sb.append(t.kind()).append(SEP);
			// class
			sb.append(rawNameOf(e));
			// wraps
			List<? extends TypeMirror> wraps = Collections.EMPTY_LIST;
			// Trick to obtain the names of the classes...
			try {
				t.wraps();
			} catch (MirroredTypesException ex) {
				wraps = ex.getTypeMirrors();
			} catch (MirroredTypeException ex2) {
				wraps = Arrays.asList(ex2.getTypeMirror());
			}
			for ( TypeMirror tm : wraps ) {
				sb.append(SEP).append(rawNameOf(tm));
			}
			gp.put(sb.toString(), docToString(t.doc()));
		}
	}

	/**
	 * Format : prefix 0.leftClass 1.rightClass 2.const 3.type 4.contentType 5.iterator 6.priority
	 * 7.returnClass 8.methodName 9.static 10.contextual 11.[name$]+
	 * @param env
	 */

	public void processOperators(final RoundEnvironment env) {
		for ( Element e : env.getElementsAnnotatedWith(operator.class) ) {
			ExecutableElement ex = (ExecutableElement) e;
			operator op = ex.getAnnotation(operator.class);
			boolean stat = ex.getModifiers().contains(Modifier.STATIC);
			String declClass = rawNameOf(ex.getEnclosingElement());
			List<? extends VariableElement> argParams = ex.getParameters();
			String[] args = new String[argParams.size()];
			for ( int i = 0; i < args.length; i++ ) {
				args[i] = rawNameOf(argParams.get(i));
			}
			int n = args.length;
			boolean scope = n > 0 && args[0].contains("IScope");
			int actual_args_number = n + (scope ? -1 : 0) + (!stat ? 1 : 0);
			String methodName = ex.getSimpleName().toString();
			String[] classes = new String[actual_args_number];
			int begin = 0;
			if ( !stat ) {
				classes[0] = declClass;
				begin = 1;
			}
			int shift = scope ? 1 : 0;
			try {
				for ( int i = 0; i < actual_args_number - begin; i++ ) {
					classes[begin + i] = args[i + shift];
				}
			} catch (Exception e1) {
				processingEnv.getMessager().printMessage(
					Kind.ERROR,
					"Error in processing operator " + declClass + " " + methodName + " " +
						Arrays.toString(args) + "; number of Java parameters: " + n +
						"; number of Gaml parameters:" + actual_args_number + "; begin: " + begin +
						"; shift: " + shift);
			}

			String ret = rawNameOf(ex.getReturnType());
			methodName = stat ? declClass + "." + methodName : methodName;
			StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(OPERATOR_PREFIX);
			// 0.number of arguments
			sb.append(actual_args_number).append(SEP);
			// 1+.arguments classes in the right order
			for ( String s : classes ) {
				sb.append(s).append(SEP);
			}
			// 2.canBeConst
			sb.append(op.can_be_const()).append(SEP);
			// 3.type
			sb.append(op.type()).append(SEP);
			// 4.contentType
			sb.append(op.content_type()).append(SEP);
			// 5.iterator
			sb.append(op.iterator()).append(SEP);
			// 6.priority
			// sb.append(op.priority()).append(SEP);
			// 7.return class
			sb.append(ret).append(SEP);
			// 8.methodName
			sb.append(methodName).append(SEP);
			// 9.static
			sb.append(stat).append(SEP);
			// 10.contextual
			sb.append(scope);
			// 11+. names
			String[] names = op.value();
			for ( int i = 0; i < names.length; i++ ) {
				sb.append(SEP).append(names[i]);
			}
			gp.put(sb.toString(), docToString(op.doc()));
		}
	}

	// Format: prefix 0.method 1.declClass 2.retClass 3.name 4.nbArgs 5.[arg]*
	void processActions(final RoundEnvironment env) {
		for ( Element e : env.getElementsAnnotatedWith(action.class) ) {
			action action = e.getAnnotation(action.class);
			ExecutableElement ex = (ExecutableElement) e;
			StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(ACTION_PREFIX);
			// method
			sb.append(ex.getSimpleName()).append(SEP);
			// declClass
			sb.append(rawNameOf(ex.getEnclosingElement())).append(SEP);
			// retClass
			TypeMirror tm = ex.getReturnType();
			if ( tm.getKind().equals(TypeKind.VOID) ) {
				sb.append("void").append(SEP);
			} else {
				sb.append(rawNameOf(tm)).append(SEP);
			}
			// virtual
			sb.append(action.virtual()).append(SEP);
			// name
			sb.append(action.name()).append(SEP);
			// argNumber
			arg[] args = action.args();
			args deprecatedArgs = e.getAnnotation(args.class);
			// gathering names (in case of doublons)
			Set<String> strings = new HashSet();
			for ( int i = 0; i < args.length; i++ ) {
				strings.add(args[i].name());
			}
			if ( deprecatedArgs != null ) {
				for ( int i = 0; i < deprecatedArgs.names().length; i++ ) {
					strings.add(deprecatedArgs.names()[i]);
				}
			}
			int nb = strings.size();
			sb.append(nb).append(SEP);
			// args format 1.name 2.[type,]+ 3.optional
			strings.clear();
			if ( args.length > 0 ) {
				for ( int i = 0; i < args.length; i++ ) {
					arg arg = args[i];
					sb.append(arg.name()).append(SEP);
					sb.append(arrayToString(arg.type())).append(SEP);
					sb.append(arg.optional()).append(SEP);
					sb.append(docToString(arg.doc())).append(SEP);
					strings.add(args[i].name());
				}
			}
			if ( deprecatedArgs != null && deprecatedArgs.names().length > 0 ) {
				for ( int i = 0; i < deprecatedArgs.names().length; i++ ) {
					String s = deprecatedArgs.names()[i];
					if ( !strings.contains(s) ) {
						sb.append(s).append(SEP);
						sb.append("unknown").append(SEP);
						sb.append("true").append(SEP);
						sb.append("").append(SEP);
					}
				}
			}
			gp.put(sb.toString(), docToString(action.doc())); /* doc */
		}
	}

	void write(final RoundEnvironment r, final Class<? extends Annotation> c, final String s) {
		for ( Element e : r.getElementsAnnotatedWith(c) ) {
			gp.put(s, name((TypeElement) (e instanceof TypeElement ? e : e.getEnclosingElement())));
		}
	}

	private String name(final TypeElement e) {
		if ( e.getNestingKind() == NestingKind.TOP_LEVEL ) { return e.getQualifiedName().toString(); }
		return name((TypeElement) e.getEnclosingElement()) + "." + e.getSimpleName().toString();
	}

	private Writer createWriter(final String s) {
		try {
			return processingEnv.getFiler().createResource(OUT, "", s, (Element[]) null)
				.openWriter();
		} catch (Exception e) {
			processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage());
		}
		return null;
	}

	private Writer createSourceWriter() {
		try {
			return processingEnv.getFiler()
				.createSourceFile("gaml.additions.GamlAdditions", (Element[]) null).openWriter();
		} catch (Exception e) {
			processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage());
		}
		return null;
	}
	//
	// private Writer createAgentBaseSourceWriter() {
	// try {
	// return processingEnv.getFiler()
	// .createSourceFile("gaml.additions.JavaBasedAgent", (Element[]) null).openWriter();
	// } catch (Exception e) {
	// processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage());
	// }
	// return null;
	// }
}
