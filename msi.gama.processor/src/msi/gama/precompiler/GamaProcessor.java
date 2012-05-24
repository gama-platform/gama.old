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

import static msi.gama.precompiler.GamlProperties.GAML;
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
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.GamlAnnotations.uses;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.JavaWriter.Pair;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class GamaProcessor extends AbstractProcessor {

	private GamlProperties gp;
	JavaWriter jw;
	
	docProcessor docProc;

	private static StandardLocation OUT = StandardLocation.SOURCE_OUTPUT;

	static final Class[] classes = new Class[] { symbol.class, handles.class, species.class,
		skill.class, getter.class, setter.class, action.class, type.class, operator.class,
		vars.class };

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
		jw = new JavaWriter(processingEnv);
		docProc = new docProcessor(processingEnv);
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return annotNames;
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
		if ( !env.processingOver() ) {
			write(env, handles.class, GamlProperties.FACTORIES);
			processFactories(env);
			processTypes(env);
			processSpecies(env);
			processSkills(env);
			processOperators(env);
			processActions(env);
			processGetters(env);
			processSetters(env);
			processSymbols(env);
			processVars(env);
			if ( "true".equals(processingEnv.getOptions().get("doc")) ) {
				if(docProc.firstParsing) {
					docProc.processDocXML(env, createWriter("doc.xml"));
					docProc.firstParsing = false;
				} else {
					processingEnv.getMessager().printMessage(Kind.NOTE, "Documentation file has already been produced");
				}
			}
			gp.store(createWriter(GAML));
			Writer w = createSourceWriter();
			if ( w != null ) {
				try {
					w.append(jw.write("gaml.additions", gp));
					w.flush();
					w.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	void processGetters(final RoundEnvironment env) {
		for ( Element e : env.getElementsAnnotatedWith(getter.class) ) {
			TypeMirror iSkill =
				processingEnv.getElementUtils().getTypeElement("msi.gaml.skills.ISkill").asType();
			TypeMirror clazz = e.getEnclosingElement().asType();
			Pair executer;
			if ( processingEnv.getTypeUtils().isAssignable(clazz, iSkill) ) {
				executer = jw.getGetter((ExecutableElement) e);
			} else {
				executer = jw.getFiedGetter((ExecutableElement) e);
			}
			gp.put(executer.key, executer.value);
		}
	}

	private void processVars(final RoundEnvironment env) {
		Set<? extends Element> vars = env.getElementsAnnotatedWith(vars.class);
		for ( Element e : vars ) {
			Map<String, Map<String, String>> maps = buildVarsFacetsMap(e);
			for ( String name : maps.keySet() ) {
				Map<String, String> facets = maps.get(name);
				StringBuilder sb = new StringBuilder();
				sb.append(JavaWriter.VAR_PREFIX).append("\"").append(facets.get("type"))
					.append("\"$");
				for ( String facetName : facets.keySet() ) {
					sb.append("\"").append(facetName).append("\",");
					sb.append("\"").append(facets.get(facetName)).append("\",");
				}
				sb.setLength(sb.length() - 1);
				gp.put(sb.toString(), jw.rawNameOf(e));
			}
		}
	}

	static Map<String, Map<String, String>> buildVarsFacetsMap(final Element c) {
		Map<String, Map<String, String>> varMap = new HashMap();
		vars vars = c.getAnnotation(vars.class);
		if ( vars == null ) { return null; /* no vars */}
		for ( final var s : vars.value() ) {
			String name = s.name();
			Map<String, String> facets = new HashMap();
			varMap.put(name, facets);
			facets.put("type", s.type());
			facets.put("name", s.name());
			facets.put("const", s.constant() ? "true" : "false");
			String depends = "";
			String[] dependencies = s.depends_on();
			if ( dependencies.length > 0 ) {
				for ( String string : dependencies ) {
					depends += string + " ";
				}
				depends = depends.trim();
				facets.put("depends_on", depends);
			}
			String of = s.of();
			if ( !"".equals(of) ) {
				facets.put("of", of);
			}
			String init = s.init();
			if ( !"".equals(init) ) {
				facets.put("init", init);
			}
		}
		for ( final Element m : c.getEnclosedElements() ) {
			getter getter = m.getAnnotation(getter.class);
			if ( getter != null ) {
				final Map<String, String> var = varMap.get(getter.var());
				if ( var != null ) {
					var.put("getter", m.getSimpleName().toString());
					if ( getter.initializer() ) {
						var.put("initer", m.getSimpleName().toString());
					}
					// addSkillMethod(c, m.getName());
					// final TypeMirror r = ((ExecutableElement) m).getReturnType();
					// var.put("type", Types.get(r).toString());
				}
			}
			setter setter = m.getAnnotation(setter.class);
			if ( setter != null ) {
				final Map<String, String> var = varMap.get(setter.value());
				if ( var != null ) {
					var.put("setter", m.getSimpleName().toString());
					// addSkillMethod(c, m.getName());
				}
			}
		}

		return varMap;
	}

	private void processSymbols(final RoundEnvironment env) {
		Set<? extends Element> symbols = env.getElementsAnnotatedWith(symbol.class);
		for ( Element e : symbols ) {
			symbol symbol = e.getAnnotation(symbol.class);
			int kind = symbol.kind();
			String[] names = symbol.name();
			String key = JavaWriter.SYMBOL_PREFIX + String.valueOf(kind);
			for ( String s : names ) {
				key += "$" + s;
			}
			gp.put(key, jw.rawNameOf(e));
		}
	}

	private void processFactories(final RoundEnvironment env) {
		Set<? extends Element> symbols = env.getElementsAnnotatedWith(handles.class);
		for ( Element e : symbols ) {
			handles handles = e.getAnnotation(handles.class);
			int[] hKinds = handles.value();
			String key = JavaWriter.FACTORY_PREFIX + String.valueOf(hKinds[0]);
			for ( int i = 1; i < hKinds.length; i++ ) {
				key += "," + String.valueOf(hKinds[i]);
			}
			uses uses = e.getAnnotation(uses.class);
			if ( uses != null ) {
				key += "$";
				int[] uKinds = uses.value();
				key += String.valueOf(uKinds[0]);
				for ( int i = 1; i < uKinds.length; i++ ) {
					key += "," + String.valueOf(uKinds[i]);
				}
			}
			gp.put(key, jw.rawNameOf(e));
		}
	}

	private void processSpecies(final RoundEnvironment env) {
		Set<? extends Element> species = env.getElementsAnnotatedWith(species.class);
		for ( Element e : species ) {
			species spec = e.getAnnotation(species.class);
			String name = spec.value();
			String[] skillTab = spec.skills();
			String key = JavaWriter.SPECIES_PREFIX + name;
			for ( String s : skillTab ) {
				key += "$" + s;
			}
			gp.put(key, jw.rawNameOf(e));
		}
	}

	private void processSkills(final RoundEnvironment env) {
		Set<? extends Element> skills = env.getElementsAnnotatedWith(skill.class);
		for ( Element e : skills ) {
			skill skill = e.getAnnotation(skill.class);
			String name = skill.value()[0];
			String[] specTab = skill.attach_to();
			String key = JavaWriter.SKILL_PREFIX + name;
			for ( String s : specTab ) {
				key += "$" + s;
			}
			gp.put(key, jw.rawNameOf(e));
		}
	}

	private void processTypes(final RoundEnvironment env) {
		Set<? extends Element> types = env.getElementsAnnotatedWith(type.class);
		for ( Element e : types ) {
			type t = e.getAnnotation(type.class);
			List<? extends TypeMirror> wraps = Collections.EMPTY_LIST;
			// TRICK
			try {
				t.wraps();
			} catch (MirroredTypesException ex) {
				wraps = ex.getTypeMirrors();
			} catch (MirroredTypeException ex) {
				wraps = Arrays.asList(ex.getTypeMirror());
			}

			String keyword = t.value();
			short id = t.id();
			int varKind = t.kind();
			String key =
				JavaWriter.TYPE_PREFIX + keyword + "$" + String.valueOf(id) + "$" +
					String.valueOf(varKind);
			for ( TypeMirror tm : wraps ) {
				key += "$" + jw.rawNameOf(tm);
			}
			gp.put(key, jw.rawNameOf(e));
		}

	}

	void processOperators(final RoundEnvironment env) {
		for ( Element e : env.getElementsAnnotatedWith(operator.class) ) {
			Map<String, String> executers = jw.getOperatorExecutersFor((ExecutableElement) e);
			for ( Map.Entry<String, String> entry : executers.entrySet() ) {
				gp.put(entry.getKey(), entry.getValue());
			}
		}
	}

	void processActions(final RoundEnvironment env) {
		for ( Element e : env.getElementsAnnotatedWith(action.class) ) {
			Pair executer = jw.getActionExecuterFor((ExecutableElement) e);
			gp.put(executer.key, executer.value);
		}
	}

	void processSetters(final RoundEnvironment env) {
		for ( Element e : env.getElementsAnnotatedWith(setter.class) ) {
			Pair executer = jw.getSetter((ExecutableElement) e);
			gp.put(executer.key, executer.value);
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
}
