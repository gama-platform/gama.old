/*********************************************************************************************
 *
 *
 * 'GamaProcessor.java', in plugin 'msi.gama.processor', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.precompiler;

import static msi.gama.precompiler.GamlProperties.GAML;
import static msi.gama.precompiler.JavaWriter.ACTION_PREFIX;
import static msi.gama.precompiler.JavaWriter.CONSTANT_PREFIX;
import static msi.gama.precompiler.JavaWriter.DISPLAY_PREFIX;
import static msi.gama.precompiler.JavaWriter.DOC_SEP;
import static msi.gama.precompiler.JavaWriter.EXPERIMENT_PREFIX;
import static msi.gama.precompiler.JavaWriter.FACTORY_PREFIX;
import static msi.gama.precompiler.JavaWriter.FILE_PREFIX;
import static msi.gama.precompiler.JavaWriter.IMPORTS;
import static msi.gama.precompiler.JavaWriter.OPERATOR_PREFIX;
import static msi.gama.precompiler.JavaWriter.POPULATIONS_LINKER_PREFIX;
import static msi.gama.precompiler.JavaWriter.SEP;
import static msi.gama.precompiler.JavaWriter.SKILL_PREFIX;
import static msi.gama.precompiler.JavaWriter.SPECIES_PREFIX;
import static msi.gama.precompiler.JavaWriter.SYMBOL_PREFIX;
import static msi.gama.precompiler.JavaWriter.TYPE_PREFIX;
import static msi.gama.precompiler.JavaWriter.VAR_PREFIX;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.populations_linker;
import msi.gama.precompiler.GamlAnnotations.serializer;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class GamaProcessor extends AbstractProcessor {

	private GamlProperties gp;
	private final static boolean PRODUCES_DOC = true;
	private final static boolean PRODUCES_WARNING = false;
	private GamlDocProcessor docProc;
	private TypeMirror iSkill, iAgent;
	private static final Charset CHARSET = Charset.forName("UTF-8");
	private static final String ADDITIONS = "gaml.additions.GamlAdditions";
	private static final StandardLocation OUT = StandardLocation.SOURCE_OUTPUT;
	final static Set<String> ANNOTATIONS = new HashSet() {
		{
			for (final Class c : Arrays.asList(symbol.class, factory.class, species.class, skill.class, getter.class,
					constant.class, setter.class, action.class, type.class, operator.class, vars.class,
					display.class)) {
				add(c.getCanonicalName());
			}
		}
	};

	@Override
	public synchronized void init(final ProcessingEnvironment pe) {
		super.init(pe);
		try {
			gp = new GamlProperties(pe.getFiler().getResource(OUT, "", GAML).openReader(true));
		} catch (final Exception e) {
			gp = new GamlProperties();
		}
		if ("true".equals(pe.getOptions().get("doc")) || PRODUCES_DOC) {
			docProc = new GamlDocProcessor(processingEnv);
		}
	}

	private void emitWarning(final String s, final Element e) {
		if (!PRODUCES_WARNING)
			return;
		if (e == null)
			processingEnv.getMessager().printMessage(Kind.WARNING, s);
		else
			processingEnv.getMessager().printMessage(Kind.WARNING, s, e);
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return ANNOTATIONS;
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
		if (!env.processingOver()) {
			try {
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
				processExperiments(env);
				processFiles(env);
				processConstants(env);
				processPopulationsLinkers(env);
			} catch (final Exception e) {
				emitWarning("An exception occured in the parsing of GAML annotations: " + e.getMessage(), null);
				throw e;
			}

			final Writer gamlWriter = createWriter(GAML);
			if (gamlWriter != null)
				gp.store(gamlWriter);

			final Writer source = createSourceWriter();
			if (source != null) {
				try {
					final StringBuilder sourceBuilder = new StringBuilder();
					new JavaWriter().write("gaml.additions", gp, sourceBuilder/* , docBuilder */);
					source.append(sourceBuilder);
				} catch (final Exception e) {
					emitWarning("An exception occured in the generation of Java files: " + e.getMessage(), null);
				}
				try {
					source.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			if (docProc != null) {

				if (docProc.firstParsing) {
					docProc.processDocXML(env, createWriter("docGAMA.xml"));
					docProc.firstParsing = false;
				}
			}

		}
		return true;
	}

	TypeMirror getISkill() {
		if (iSkill == null) {
			iSkill = processingEnv.getElementUtils().getTypeElement("msi.gaml.skills.ISkill").asType();
		}
		return iSkill;
	}

	TypeMirror getIAgent() {
		if (iAgent == null) {
			iAgent = processingEnv.getElementUtils().getTypeElement("msi.gama.metamodel.agent.IAgent").asType();
		}
		return iAgent;
	}

	String rawNameOf(final Element e) {
		return rawNameOf(e.asType());
	}

	String rawNameOf(final TypeMirror t) {
		final String init = processingEnv.getTypeUtils().erasure(t).toString();
		final String[] segments = init.split("\\.");
		final StringBuilder sb = new StringBuilder();
		int index = 0;
		for (final String segment : segments) {
			final int i = segment.indexOf('<');
			final int j = segment.lastIndexOf('>');
			final String string = i > -1 ? segment.substring(0, i) + segment.substring(j + 1) : segment;
			if (index++ > 0) {
				sb.append(".");
			}
			sb.append(string);
		}
		String clazz = sb.toString();
		for (int i = 0; i < IMPORTS.length; i++) {
			if (clazz.startsWith(IMPORTS[i])) {
				// AD: false
				final String temp = clazz.replace(IMPORTS[i] + ".", "");
				if (!temp.contains(".")) {
					clazz = temp;
				}
			}
		}
		return clazz;
	}

	/**
	 * Format : 0.type 1.type 2. contentType 3.varName 4.class 5.[facetName,
	 * facetValue]+ 6.getter 7.initializer(true/false)? 8.setter
	 * 
	 * @param env
	 */
	private void processVars(final RoundEnvironment env) {
		final List<? extends Element> elements = sortElements(env, vars.class);
		for (final Element e : elements) {
			boolean isField;
			final TypeMirror clazz = e.asType();
			isField = !processingEnv.getTypeUtils().isAssignable(clazz, getISkill())
					&& !processingEnv.getTypeUtils().isAssignable(clazz, getIAgent());

			final vars vars = e.getAnnotation(vars.class);
			for (final var s : vars.value()) {
				final doc[] docs = s.doc();
				if (docs.length == 0) {
					emitWarning("GAML: var '" + s.name() + "' is not documented", e);
				}
				final StringBuilder sb = new StringBuilder();
				final int type = s.type();
				final int contentType = s.of();
				// 0.type
				sb.append(VAR_PREFIX).append(type).append(SEP);
				// 1.contentType
				sb.append(contentType).append(SEP);
				// 2. keyType
				sb.append(s.index()).append(SEP);
				final String varName = s.name();
				// 3.var name
				sb.append(varName).append(SEP);
				// 4. class of declaration
				sb.append(rawNameOf(e)).append(SEP);
				// 5. facets
				sb.append("type").append(',').append(type).append(',');
				sb.append("name").append(',').append(varName).append(',');
				sb.append("const").append(',').append(s.constant() ? "true" : "false");

				final String[] dependencies = s.depends_on();
				if (dependencies.length > 0) {
					String depends = "[";
					for (int i = 0; i < dependencies.length; i++) {
						final String string = dependencies[i];
						depends += string;
						if (i < dependencies.length - 1) {
							depends += "COMMA";
						}
					}
					depends += "]";
					sb.append(',').append("depends_on").append(',').append(depends);
				}
				if (contentType != 0) {
					sb.append(',').append("of").append(',').append(contentType);
				}
				final String init = s.init();
				if (!"".equals(init)) {
					sb.append(',').append("init").append(',').append(replaceCommas(init));
				}
				boolean found = false;
				sb.append(SEP);
				for (final Element m : e.getEnclosedElements()) {
					final getter getter = m.getAnnotation(getter.class);
					if (getter != null && getter.value().equals(varName)) {
						final ExecutableElement ex = (ExecutableElement) m;
						final List<? extends VariableElement> argParams = ex.getParameters();
						final String[] args = new String[argParams.size()];
						for (int i = 0; i < args.length; i++) {
							args[i] = rawNameOf(argParams.get(i));
						}
						final int n = args.length;
						final boolean scope = n > 0 && args[0].contains("IScope");

						// method
						sb.append(ex.getSimpleName()).append(SEP);
						// retClass
						sb.append(rawNameOf(ex.getReturnType())).append(SEP);
						// dynamic ?
						sb.append(!scope && n > 0 || scope && n > 1).append(SEP);
						// field ?
						sb.append(isField).append(SEP);
						// scope ?
						sb.append(scope);
						sb.append(SEP).append(getter.initializer());
						found = true;
						break;
					}
				}
				if (!found) {
					sb.append("null");
				}
				found = false;
				sb.append(SEP);
				for (final Element m : e.getEnclosedElements()) {
					final setter setter = m.getAnnotation(setter.class);
					if (setter != null && setter.value().equals(varName)) {
						final ExecutableElement ex = (ExecutableElement) m;
						final List<? extends VariableElement> argParams = ex.getParameters();
						final String[] args = new String[argParams.size()];
						for (int i = 0; i < args.length; i++) {
							args[i] = rawNameOf(argParams.get(i));
						}
						final int n = args.length;
						final boolean scope = n > 0 && args[0].contains("IScope");
						// method
						sb.append(ex.getSimpleName()).append(SEP);
						// paramClass
						final boolean isDynamic = !scope && n == 2 || scope && n == 3;
						sb.append(isDynamic ? args[!scope ? 1 : 2] : args[!scope ? 0 : 1]).append(SEP);
						// isDynamic
						sb.append(isDynamic).append(SEP);
						// scope ?
						sb.append(scope);
						found = true;
						break;
					}
				}
				if (!found) {
					sb.append("null");
				}
				sb.append(SEP);
				gp.put(sb.toString(), "");
			}
		}
	}

	public void processFiles(final RoundEnvironment env) {
		for (final Element e : sortElements(env, file.class)) {
			final file f = e.getAnnotation(file.class);
			final doc[] docs = f.doc();
			doc doc;
			if (docs.length == 0) {
				doc = e.getAnnotation(doc.class);
			} else {
				doc = docs[0];
			}
			if (doc == null) {
				emitWarning("GAML: file declaration '" + f.name() + "' is not documented", e);
			}

			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(FILE_PREFIX);
			// name
			sb.append(f.name()).append(SEP);
			// class
			sb.append(rawNameOf(e)).append(SEP);
			// buffer type
			sb.append(f.buffer_type()).append(SEP);
			// buffer contents type
			sb.append(f.buffer_content()).append(SEP);
			// buffer key type
			sb.append(f.buffer_index()).append(SEP);
			// suffixes
			final String[] names = f.extensions();
			sb.append(arrayToString(names)).append(SEP);
			// constructors: only the arguments in addition to the scope are
			// provided
			for (final Element m : e.getEnclosedElements()) {
				if (m.getKind() == ElementKind.CONSTRUCTOR) {
					final ExecutableElement ex = (ExecutableElement) m;
					final List<? extends VariableElement> argParams = ex.getParameters();
					// The first parameter must be IScope
					final int n = argParams.size();
					if (n <= 1) {
						continue;
					}
					final String[] args = new String[n - 1];
					for (int i = 1; i < n; i++) {
						args[i - 1] = rawNameOf(argParams.get(i));
					}
					sb.append(arrayToString(args)).append(SEP);
				}
			}
			gp.put(sb.toString(), "" /* docToString(f.doc()) */);
		}
	}

	private String replaceCommas(final String s) {
		return s.replace(",", "COMMA");
	}

	/**
	 * Computes the representation of symbols. Format: prefix 0.kind 1.class
	 * 2.remote 3.with_args 4.with_scope 5.with_sequence 6.symbols_inside
	 * 7.kinds_inside 8.nbFacets 9.[facet]* 10.omissible 11.[name$]*
	 * 
	 * @param env
	 */
	private void processSymbols(final RoundEnvironment env) {
		final List<? extends Element> symbols = sortElements(env, symbol.class);
		for (final Element e : symbols) {
			final StringBuilder sb = new StringBuilder();
			final symbol symbol = e.getAnnotation(symbol.class);
			validator validator = e.getAnnotation(validator.class);
			serializer serializer = e.getAnnotation(serializer.class);
			TypeMirror sup = ((TypeElement) e).getSuperclass();
			// Workaround for bug
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=419944
			// Effectively inherits from a given validator
			while (validator == null && sup != null) {

				if (sup.getKind().equals(TypeKind.NONE)) {
					sup = null;
					continue;
				}
				final TypeElement te = (TypeElement) processingEnv.getTypeUtils().asElement(sup);
				validator = te.getAnnotation(validator.class);
				sup = te.getSuperclass();
			}
			sup = ((TypeElement) e).getSuperclass();
			while (serializer == null && sup != null) {

				if (sup.getKind().equals(TypeKind.NONE)) {
					sup = null;
					continue;
				}
				final TypeElement te = (TypeElement) processingEnv.getTypeUtils().asElement(sup);
				serializer = te.getAnnotation(serializer.class);
				sup = te.getSuperclass();
			}
			TypeMirror type_validator = null;
			// getting the class present in validator
			try {
				if (validator != null) {
					validator.value();
				}
			} catch (final MirroredTypeException e1) {
				type_validator = e1.getTypeMirror();
			} catch (final MirroredTypesException e1) {
				type_validator = e1.getTypeMirrors().get(0);
			}
			TypeMirror type_serializer = null;
			// getting the class present in serializer
			try {
				if (serializer != null) {
					serializer.value();
				}
			} catch (final MirroredTypeException e1) {
				type_serializer = e1.getTypeMirror();
			} catch (final MirroredTypesException e1) {
				type_serializer = e1.getTypeMirrors().get(0);
			}

			// prefix

			sb.append(SYMBOL_PREFIX);
			// validator
			sb.append(type_validator == null ? "" : rawNameOf(type_validator)).append(SEP);
			// serializer
			sb.append(type_serializer == null ? "" : rawNameOf(type_serializer)).append(SEP);
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
			final inside inside = e.getAnnotation(inside.class);
			// symbols_inside && kinds_inside
			if (inside != null) {
				final String[] parentSymbols = inside.symbols();
				for (int i = 0; i < parentSymbols.length; i++) {
					if (i > 0) {
						sb.append(',');
					}
					sb.append(parentSymbols[i]);
				}
				sb.append(SEP);
				final int[] parentKinds = inside.kinds();
				for (int i = 0; i < parentKinds.length; i++) {
					if (i > 0) {
						sb.append(',');
					}
					sb.append(parentKinds[i]);
				}
				sb.append(SEP);

			} else {
				sb.append(SEP).append(SEP);
			}
			final facets facets = e.getAnnotation(facets.class);
			// facets
			if (facets == null) {
				sb.append('0').append(SEP).append(SEP).append(SEP);
			} else {
				sb.append(facets.value().length).append(SEP);
				sb.append(facetsToString(facets, e)).append(SEP);
				sb.append(facets.omissible()).append(SEP);
			}
			// names
			for (final String s : symbol.name()) {
				sb.append(s).append(SEP);
			}
			sb.setLength(sb.length() - 1);
			final doc doc = e.getAnnotation(doc.class);

			if (doc == null) {
				emitWarning("GAML: symbol '" + symbol.name()[0] + "' is not documented", e);
			}
			gp.put(sb.toString(), "" /* docToString(doc) */); /* doc */
		}
	}

	/**
	 * Format 0.value 1.deprecated 2.returns 3.comment 4.nb_cases
	 * 5.[specialCases$]* 6.nb_examples 7.[examples$]* Uses its own separator
	 * (DOC_SEP)
	 *
	 * @param docs
	 *            an Array of @doc annotations (only the 1st is significant)
	 * @return aString containing the documentation formatted using the format
	 *         above
	 */
	private String docToString(final doc[] docs) {
		if (docs == null || docs.length == 0) {
			return "";
		}
		return docToString(docs[0]);
	}

	private String docToString(final doc doc) {
		if (doc == null) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		sb.append(doc.value()).append(DOC_SEP);
		sb.append(doc.deprecated());
		return sb.toString();
	}

	private String facetsToString(final facets facets, final Element e) {
		final StringBuilder sb = new StringBuilder();
		if (facets.value() != null) {
			for (final facet f : facets.value()) {
				final doc[] docs = f.doc();
				if (docs.length == 0) {
					emitWarning("GAML: facet '" + f.name() + "' is not documented", e);
				}
				sb.append(facetToString(f)).append(SEP);
			}
			if (facets.value().length > 0) {
				sb.setLength(sb.length() - 1);
			}
		}
		return sb.toString();
	}

	// Format: 1.name 2.[type,]+ 3.[value,]* 4.optional 5. internal 6.doc
	private String facetToString(final facet facet) {
		final StringBuilder sb = new StringBuilder();
		sb.append(facet.name()).append(SEP);
		sb.append(arrayToString(facet.type())).append(SEP);
		sb.append(facet.of()).append(SEP);
		sb.append(facet.index()).append(SEP);
		sb.append(arrayToString(facet.values())).append(SEP);
		sb.append(facet.optional()).append(SEP);
		sb.append(facet.internal()).append(SEP);
		sb.append(docToString(facet.doc()));
		return sb.toString();
	}

	private String arrayToString(final int[] array) {
		if (array.length == 0) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		for (final int i : array) {
			sb.append(i).append(",");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	private String arrayToString(final String[] array) {
		if (array.length == 0) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		for (final String i : array) {
			sb.append(replaceCommas(i)).append(",");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * Introduced to handle issue #1671
	 * 
	 * @param env
	 * @param annotationClass
	 * @return
	 */
	private List<? extends Element> sortElements(final RoundEnvironment env,
			final Class<? extends Annotation> annotationClass) {
		final Set<? extends Element> elements = env.getElementsAnnotatedWith(annotationClass);
		final List<? extends Element> result = new ArrayList(elements);
		Collections.sort(result, new Comparator<Element>() {

			@Override
			public int compare(final Element o1, final Element o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		return result;
	}

	/**
	 * Format : prefix 0.class 1.[handles,]* 2.[uses,]* Format :
	 * ]class$handles*$uses*
	 * 
	 * @param env
	 */
	private void processFactories(final RoundEnvironment env) {
		final List<? extends Element> factories = sortElements(env, factory.class);

		for (final Element e : factories) {

			final factory factory = e.getAnnotation(factory.class);
			final int[] hKinds = factory.handles();
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(FACTORY_PREFIX);
			// class
			sb.append(rawNameOf(e)).append(SEP);
			// handles
			sb.append(String.valueOf(hKinds[0]));
			for (int i = 1; i < hKinds.length; i++) {
				sb.append(',').append(String.valueOf(hKinds[i]));
			}
			gp.put(sb.toString(), ""); /* doc ? */
		}
	}

	/**
	 * Format : prefix 0.name 1.class 2.[skill$]*
	 * 
	 * @param env
	 */
	private void processSpecies(final RoundEnvironment env) {
		final List<? extends Element> species = sortElements(env, species.class);
		for (final Element e : species) {
			final species spec = e.getAnnotation(species.class);
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(SPECIES_PREFIX);
			// name
			sb.append(spec.name()).append(SEP);
			// class
			sb.append(rawNameOf(e));
			// skills
			for (final String s : spec.skills()) {
				sb.append(SEP).append(s);
			}

			final doc[] docs = spec.doc();
			doc doc;
			if (docs.length == 0) {
				doc = e.getAnnotation(doc.class);
			} else {
				doc = docs[0];
			}
			if (doc == null) {
				emitWarning("GAML: species '" + spec.name() + "' is not documented", e);
			}

			gp.put(sb.toString(), "" /* docToString(spec.doc()) */); /* doc */
		}
	}

	private void processDisplays(final RoundEnvironment env) {
		final List<? extends Element> displays = sortElements(env, display.class);
		for (final Element e : displays) {
			final display spec = e.getAnnotation(display.class);
			final StringBuilder sb = new StringBuilder();
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

	private void processExperiments(final RoundEnvironment env) {
		final List<? extends Element> experiments = sortElements(env, experiment.class);
		for (final Element e : experiments) {
			final experiment spec = e.getAnnotation(experiment.class);
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(EXPERIMENT_PREFIX);
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
	 * 
	 * @param env
	 */
	private void processSkills(final RoundEnvironment env) {
		final List<? extends Element> skills = sortElements(env, skill.class);
		for (final Element e : skills) {
			final skill skill = e.getAnnotation(skill.class);
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(SKILL_PREFIX);
			// name
			sb.append(skill.name()).append(SEP);
			// class
			sb.append(rawNameOf(e));
			// species
			for (final String s : skill.attach_to()) {
				sb.append(SEP).append(s);
			}
			final doc[] docs = skill.doc();
			doc doc;
			if (docs.length == 0) {
				doc = e.getAnnotation(doc.class);
			} else {
				doc = docs[0];
			}
			if (doc == null) {
				emitWarning("GAML: skill '" + skill.name() + "' is not documented", e);
			}

			gp.put(sb.toString(), "" /* docToString(skill.doc()) */); /* doc */
		}
	}

	/**
	 * Format : prefix 0.name 1.id 2.varKind 3.class 4.[wrapped_class$]+
	 * 
	 * @param env
	 */
	private void processTypes(final RoundEnvironment env) {
		final List<? extends Element> types = sortElements(env, type.class);
		for (final Element e : types) {
			final type t = e.getAnnotation(type.class);

			final StringBuilder sb = new StringBuilder();
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
			} catch (final MirroredTypesException ex) {
				try {
					wraps = ex.getTypeMirrors();
				} catch (final MirroredTypeException ex2) {
					wraps = Arrays.asList(ex2.getTypeMirror());
				}
			}
			for (final TypeMirror tm : wraps) {
				sb.append(SEP).append(rawNameOf(tm));
			}
			final doc[] docs = t.doc();
			doc doc;
			if (docs.length == 0) {
				doc = e.getAnnotation(doc.class);
			} else {
				doc = docs[0];
			}
			if (doc == null) {
				emitWarning("GAML: type '" + t.name() + "' is not documented", e);
			}

			gp.put(sb.toString(), ""/* docToString(t.doc()) */);
		}
	}

	/**
	 * Format : prefix 0.leftClass 1.rightClass 2.const 3.type 4.contentType
	 * 5.iterator 6.priority 7.returnClass 8.methodName 9.static 10.contextual
	 * 11.[name$]+
	 * 
	 * @param env
	 */

	public void processOperators(final RoundEnvironment env) {
		for (final Element e : sortElements(env, operator.class)) {
			final ExecutableElement ex = (ExecutableElement) e;
			final operator op = ex.getAnnotation(operator.class);
			doc documentation = ex.getAnnotation(doc.class);
			if (documentation == null) {
				final doc[] docs = op.doc();
				if (docs.length > 0)
					documentation = op.doc()[0];
			}

			if (documentation == null) {
				emitWarning("GAML: operator '" + op.value()[0] + "' is not documented", e);
			}

			final boolean stat = ex.getModifiers().contains(Modifier.STATIC);
			final String declClass = rawNameOf(ex.getEnclosingElement());
			final List<? extends VariableElement> argParams = ex.getParameters();
			final String[] args = new String[argParams.size()];
			for (int i = 0; i < args.length; i++) {
				args[i] = rawNameOf(argParams.get(i));
			}
			final int n = args.length;
			final boolean scope = n > 0 && args[0].contains("IScope");
			final int actual_args_number = n + (scope ? -1 : 0) + (!stat ? 1 : 0);
			String methodName = ex.getSimpleName().toString();
			final String[] classes = new String[actual_args_number];
			int begin = 0;
			if (!stat) {
				classes[0] = declClass;
				begin = 1;
			}
			final int shift = scope ? 1 : 0;
			try {
				for (int i = 0; i < actual_args_number - begin; i++) {
					classes[begin + i] = args[i + shift];
				}
			} catch (final Exception e1) {
			}

			final String ret = rawNameOf(ex.getReturnType());
			methodName = stat ? declClass + "." + methodName : methodName;
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(OPERATOR_PREFIX);
			// 0.number of arguments
			sb.append(actual_args_number).append(SEP);
			// 1+.arguments classes in the right order
			for (final String s : classes) {
				sb.append(s).append(SEP);
			}
			// 2.canBeConst
			sb.append(op.can_be_const()).append(SEP);
			// 3.type
			sb.append(op.type()).append(SEP);
			// 4.contentType
			sb.append(op.content_type()).append(SEP);
			// 4+. index_type
			sb.append(op.index_type()).append(SEP);
			// 5.iterator
			sb.append(op.iterator()).append(SEP);
			// 6.expected types number
			sb.append(op.expected_content_type().length).append(SEP);
			// 6+ expected types
			for (int i = 0; i < op.expected_content_type().length; i++) {
				sb.append(op.expected_content_type()[i]).append(SEP);
			}
			// 7.return class
			sb.append(ret).append(SEP);
			// 8.methodName
			sb.append(methodName).append(SEP);
			// 9.static
			sb.append(stat).append(SEP);
			// 10.contextual
			sb.append(scope);
			// 11+. names
			final String[] names = op.value();
			for (int i = 0; i < names.length; i++) {
				sb.append(SEP).append(names[i]);
			}

			gp.put(sb.toString(), "" /* docToString(documentation) */);
		}
	}

	// Format: prefix 0.method 1.declClass 2.retClass 3.name 4.nbArgs 5.[arg]*
	void processActions(final RoundEnvironment env) {
		for (final Element e : sortElements(env, action.class)) {
			final action action = e.getAnnotation(action.class);
			final ExecutableElement ex = (ExecutableElement) e;
			// note("Action processed: " + ex.getSimpleName());
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(ACTION_PREFIX);
			// method
			sb.append(ex.getSimpleName()).append(SEP);
			// declClass
			sb.append(rawNameOf(ex.getEnclosingElement())).append(SEP);
			// note("On class: " + ex.getSimpleName());
			// retClass
			final TypeMirror tm = ex.getReturnType();
			if (tm.getKind().equals(TypeKind.VOID)) {
				sb.append("void").append(SEP);
			} else {
				sb.append(rawNameOf(tm)).append(SEP);
			}
			// virtual
			sb.append(action.virtual()).append(SEP);
			// name
			sb.append(action.name()).append(SEP);
			// argNumber
			final arg[] args = action.args();
			final args deprecatedArgs = e.getAnnotation(args.class);
			// gathering names (in case of doublons)
			final Set<String> strings = new HashSet();
			for (int i = 0; i < args.length; i++) {
				strings.add(args[i].name());
			}
			if (deprecatedArgs != null) {
				for (int i = 0; i < deprecatedArgs.names().length; i++) {
					strings.add(deprecatedArgs.names()[i]);
				}
			}
			final int nb = strings.size();
			sb.append(nb).append(SEP);
			// args format 1.name 2.[type,]+ 3.optional
			strings.clear();
			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					final arg arg = args[i];
					sb.append(arg.name()).append(SEP);
					sb.append(arrayToString(arg.type())).append(SEP);
					sb.append(arg.optional()).append(SEP);
					final doc[] docs = arg.doc();
					if (docs.length == 0) {
						emitWarning("GAML: argument '" + arg.name() + "' is not documented", e);
					}
					sb.append(docToString(arg.doc())).append(SEP);
					strings.add(args[i].name());
				}
			}
			if (deprecatedArgs != null && deprecatedArgs.names().length > 0) {
				for (int i = 0; i < deprecatedArgs.names().length; i++) {
					final String s = deprecatedArgs.names()[i];
					if (!strings.contains(s)) {
						sb.append(s).append(SEP);
						sb.append("unknown").append(SEP);
						sb.append("true").append(SEP);
						sb.append("").append(SEP);
					}
				}
			}
			gp.put(sb.toString(), ""/* docToString(action.doc()) */); /* doc */
		}
	}

	public void processConstants(final RoundEnvironment env) {
		for (final Element e : sortElements(env, constant.class)) {
			final VariableElement ve = (VariableElement) e;
			// final TypeMirror tm = ve.asType();
			// boolean ok = tm instanceof PrimitiveType || tm instanceof
			// ArrayType;
			// ok |= this.rawNameOf(tm).startsWith("String");
			final constant constant = ve.getAnnotation(constant.class);
			// //if (!ok) {
			//
			// processingEnv.getMessager()
			// .printMessage(Kind.WARNING, "GAML: constant '" + constant.value()
			// +
			// "' cannot be instance of "
			// + tm.toString() + ". The type of constants must be either a
			// primitive type or String",
			// e);
			//
			// }

			final doc documentation = constant.doc().length == 0 ? null : constant.doc()[0];

			if (documentation == null) {
				emitWarning("GAML: constant '" + constant.value() + "' is not documented", e);
			}

			final String ret = rawNameOf(ve.asType());
			final String constantName = constant.value();
			final Object valueConstant = ve.getConstantValue();

			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(CONSTANT_PREFIX);
			// 0.return class
			sb.append(ret).append(SEP);
			// 1.constant name
			sb.append(constantName).append(SEP);
			// 2+.alternative names
			for (final String s : constant.altNames()) {
				sb.append(s).append(SEP);
			}
			// 3.value
			sb.append(valueConstant);
			// 4.doc
			gp.put(sb.toString(), "" /* docToString(documentation) */);
		}
	}

	/**
	 * Format : prefix 0.name 1. class
	 *
	 * @param env
	 */
	private void processPopulationsLinkers(final RoundEnvironment env) {
		final List<? extends Element> populationsLinkers = sortElements(env, populations_linker.class);
		for (final Element e : populationsLinkers) {
			final populations_linker pLinker = e.getAnnotation(populations_linker.class);
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(POPULATIONS_LINKER_PREFIX);
			// name
			sb.append(pLinker.name()).append(SEP);
			// class
			sb.append(rawNameOf(e));

			gp.put(sb.toString(), docToString(pLinker.doc())); /* doc */
		}
	}

	void write(final RoundEnvironment r, final Class<? extends Annotation> c, final String s) {
		for (final Element e : sortElements(r, c)) {
			gp.put(s, name((TypeElement) (e instanceof TypeElement ? e : e.getEnclosingElement())));
		}
	}

	private String name(final TypeElement e) {
		if (e.getNestingKind() == NestingKind.TOP_LEVEL) {
			return e.getQualifiedName().toString();
		}
		return name((TypeElement) e.getEnclosingElement()) + "." + e.getSimpleName().toString();
	}

	private Writer createWriter(final String s) {
		try {
			final OutputStream output = processingEnv.getFiler().createResource(OUT, "", s, (Element[]) null)
					.openOutputStream();
			final Writer writer = new OutputStreamWriter(output, Charset.forName("UTF-8"));
			return writer;
		} catch (final Exception e) {
			emitWarning(e.getMessage(), null);
		}
		return null;
	}

	private Writer createSourceWriter() {
		try {
			final OutputStream output = processingEnv.getFiler().createSourceFile(ADDITIONS, (Element[]) null)
					.openOutputStream();
			final Writer writer = new OutputStreamWriter(output, CHARSET);
			return writer;
		} catch (final Exception e) {
			emitWarning(e.getMessage(), null);
		}
		return null;
	}

}
