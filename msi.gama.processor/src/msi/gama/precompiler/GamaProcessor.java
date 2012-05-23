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

import static msi.gama.precompiler.GamlProperties.*;
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
import msi.gama.precompiler.JavaWriter.Pair;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class GamaProcessor extends AbstractProcessor {

	private GamlProperties gp;
	JavaWriter jw;
	
	docProcessor docProc;

	private static StandardLocation OUT = StandardLocation.SOURCE_OUTPUT;

	static final Class[] classes = new Class[] { operator.class, symbol.class, handles.class,
		species.class, skill.class, getter.class, setter.class, action.class, type.class };
	static final String[] cats = new String[] { OPERATORS, SYMBOLS, FACTORIES };
	static final Set<String> catSet = new HashSet(Arrays.asList(cats));
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
			for ( int i = 0; i < cats.length; i++ ) {
				write(env, classes[i], cats[i]);
			}
			processTypes(env);
			processSpecies(env);
			processSkills(env);
			processOperators(env);
			processActions(env);
			processGetters(env);
			processSetters(env);
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
		List<String> basicTypes = new ArrayList();
		List<String> otherTypes = new ArrayList();
		for ( Element e : types ) {
			type t = e.getAnnotation(type.class);
			TypeMirror firstClass;
			// TRICK
			try {
				t.wraps();
			} catch (MirroredTypesException ex) {
				firstClass = ex.getTypeMirrors().get(0);
				if ( jw.rawNameOf(firstClass).startsWith("java") ) {
					basicTypes.add(0, jw.rawNameOf(e));
				} else {
					otherTypes.add(jw.rawNameOf(e));
				}
			} catch (MirroredTypeException ex) {
				firstClass = ex.getTypeMirror();
				if ( jw.rawNameOf(firstClass).startsWith("java") ) {
					basicTypes.add(0, jw.rawNameOf(e));
				} else {
					otherTypes.add(jw.rawNameOf(e));
				}
			}

		}
		for ( String s : basicTypes ) {
			gp.put(JAVA_TYPES, s);
		}
		for ( String s : otherTypes ) {
			gp.put(GAMA_TYPES, s);
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
