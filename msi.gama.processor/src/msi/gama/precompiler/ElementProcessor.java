package msi.gama.precompiler;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.tests;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.variable;

public abstract class ElementProcessor<T extends Annotation> implements IProcessor<T>, Constants {

	protected static final Map<String, String> NAME_CACHE = new HashMap<>();
	static final StringBuilder CONCAT = new StringBuilder();

	protected final SortedMap<String, StringBuilder> serializedElements = new TreeMap<>();
	static final Pattern CLASS_PARAM = Pattern.compile("<.*?>");
	static final Pattern SINGLE_QUOTE = Pattern.compile("\"");
	static final String QUOTE_MATCHER = Matcher.quoteReplacement("\\\"");
	protected String initializationMethodName;

	protected final static String concat(final String... array) {
		for (final String element : array) {
			CONCAT.append(element);
		}
		final String result = CONCAT.toString();
		CONCAT.setLength(0);
		return result;
	}

	public ElementProcessor() {}

	protected void clean(final ProcessorContext context, final Map<String, StringBuilder> map) {
		for (final String k : context.getRoots()) {
			map.remove(k);
		}
	}

	@Override
	public boolean hasElements() {
		return serializedElements.size() > 0;
	}

	@Override
	public void process(final ProcessorContext context) {
		final Class<T> a = getAnnotationClass();
		clean(context, serializedElements);
		for (final Map.Entry<String, List<Element>> entry : context.groupElements(a).entrySet()) {
			final List<Element> elements = entry.getValue();
			if (elements.size() == 0) { continue; }
			final StringBuilder sb = new StringBuilder();
			for (final Element e : elements) {
				try {
					if (validateElement(context, e)) { createElement(sb, context, e, e.getAnnotation(a)); }
				} catch (final Exception exception) {

					context.emitError("Exception in processor: " + exception.getMessage(), exception, e);

				}

			}
			if (sb.length() > 0) { serializedElements.put(entry.getKey(), sb); }
		}
	}

	static final doc[] NULL_DOCS = new doc[0];

	protected boolean isInternal(final Element main, final Annotation a) {
		boolean internal = false;
		if (a instanceof species) {
			internal = ((species) a).internal();
		} else if (a instanceof symbol) {
			internal = ((symbol) a).internal();
		} else if (a instanceof operator) {
			internal = ((operator) a).internal();
		} else if (a instanceof skill) {
			internal = ((skill) a).internal();
		} else if (a instanceof facet) {
			internal = ((facet) a).internal();
		} else if (a instanceof type) {
			internal = ((type) a).internal();
		} else if (a instanceof variable) { internal = ((variable) a).internal(); }
		return internal;
	}

	protected doc getDocAnnotation(final Element main, final Annotation a) {
		doc[] docs = NULL_DOCS;
		if (a instanceof species) {
			docs = ((species) a).doc();
		} else if (a instanceof symbol) {
			docs = ((symbol) a).doc();
		} else if (a instanceof arg) {
			docs = ((arg) a).doc();
		} else if (a instanceof display) {
			// nothing
		} else if (a instanceof experiment) {
			// nothing
		} else if (a instanceof constant) {
			docs = ((constant) a).doc();
		} else if (a instanceof operator) {
			docs = ((operator) a).doc();
		} else if (a instanceof skill) {
			docs = ((skill) a).doc();
		} else if (a instanceof facet) {
			docs = ((facet) a).doc();
		} else if (a instanceof type) {
			docs = ((type) a).doc();
		} else if (a instanceof file) {
			docs = ((file) a).doc();
		} else if (a instanceof variable) { docs = ((variable) a).doc(); }
		doc d = null;
		if (docs.length == 0) {
			d = main.getAnnotation(doc.class);
		} else {
			d = docs[0];
		}
		return d;
	}

	protected boolean isDeprecated(final Element e, final Annotation a) {
		final doc d = getDocAnnotation(e, a);
		if (d == null) return false;
		return d.deprecated().length() > 0;
	}

	public boolean hasTests(final example[] examples) {
		for (final example ex : examples) {
			if (ex.isTestOnly() || ex.isExecutable() && ex.test()) return true;
		}
		return false;
	}

	public boolean hasTests(final Element e, final Annotation a) {
		// if the artifact is internal, skip the verification
		if (isInternal(e, a)) return true;
		final no_test no = e.getAnnotation(no_test.class);
		// if no tests are necessary, skip the verification
		if (no != null) return true;
		final tests tests = e.getAnnotation(tests.class);
		if (tests != null) return true;
		final test test = e.getAnnotation(test.class);
		if (test != null) return true;
		final doc doc = getDocAnnotation(e, a);
		if (doc == null) return false;
		if (hasTests(doc.examples())) return true;
		for (final usage us : doc.usages()) {
			if (hasTests(us.examples())) return true;
		}
		return doc.deprecated().length() > 0;
	}

	protected void verifyDoc(final ProcessorContext context, final Element e, final String displayedName,
			final Annotation a) {
		if (isInternal(e, a)) return;
		final doc d = getDocAnnotation(e, a);
		boolean docMissing = d == null;
		if (d != null) {
			if (d.value().length() == 0 && d.deprecated().length() == 0 && d.usages().length == 0
					&& d.special_cases().length == 0 && d.examples().length == 0) {
				docMissing = true;
			}
		}
		if (docMissing) { context.emitWarning("documentation missing for " + displayedName, e); }

	}

	@Override
	public void serialize(final ProcessorContext context, final Collection<StringBuilder> elements,
			final StringBuilder sb) {
		elements.forEach((builder) -> {
			if (builder != null) { sb.append(builder); }
		});
	}

	private void writeMethod(final String method, final String followingMethod,
			final Collection<StringBuilder> elements, final StringBuilder sb, final ProcessorContext context) {
		sb.append("public void ").append(method).append("() ").append(getExceptions()).append(" {");
		serialize(context, elements, sb);
		if (followingMethod != null) { sb.append(ln).append(followingMethod).append("(); "); }
		sb.append(ln).append("}");
	}

	@Override
	public void writeJavaBody(final StringBuilder sb, final ProcessorContext context) {
		final String method = getInitializationMethodName();
		if (method == null) return;
		final int size = sizeOf(serializedElements);
		if (size > 20000) {
			writeMethod(method, method + "2", halfOf(serializedElements, true), sb, context);
			writeMethod(method + "2", null, halfOf(serializedElements, false), sb, context);
		} else {
			writeMethod(method, null, serializedElements.values(), sb, context);
		}
	}

	private int sizeOf(final Map<String, StringBuilder> elements) {
		return elements.values().stream().filter(e -> e != null).mapToInt(e -> e.length()).sum();
	}

	private List<StringBuilder> halfOf(final Map<String, StringBuilder> elements, final boolean firstHalf) {
		final int size = elements.size();
		final List<StringBuilder> result = new ArrayList<>(elements.values());
		return firstHalf ? result.subList(0, size / 2) : result.subList(size / 2, size);

	}

	public abstract void createElement(StringBuilder sb, ProcessorContext context, Element e, T annotation);

	protected abstract Class<T> getAnnotationClass();

	@Override
	public final String getInitializationMethodName() {
		if (initializationMethodName == null) {
			initializationMethodName =
					"initialize" + Constants.capitalizeFirstLetter(getAnnotationClass().getSimpleName());
		}
		return initializationMethodName;
	}

	protected static String toJavaString(final String s) {
		if (s == null || s.isEmpty()) return "(String)null";
		final int i = ss1.indexOf(s);
		return i == -1 ? "\"" + s + "\"" : ss2.get(i);
	}

	protected final static String toClassObject(final String s) {
		final String result = CLASS_NAMES.get(s);
		return result == null ? s + ".class" : result;
	}

	protected final static StringBuilder toArrayOfStrings(final String[] segments, final StringBuilder sb) {
		if (segments == null || segments.length == 0) {
			sb.append("AS");
			return sb;
		}
		sb.append("S(");
		for (int i = 0; i < segments.length; i++) {
			if (i > 0) { sb.append(','); }
			sb.append(toJavaString(segments[i]));
		}
		sb.append(')');
		return sb;
	}

	protected static String checkPrim(final String c) {
		return CHECK_PRIM.getOrDefault(c, c);
	}

	protected static String returnWhenNull(final String returnClass) {
		return RETURN_WHEN_NULL.getOrDefault(returnClass, " null");
	}

	protected static void param(final StringBuilder sb, final String c, final String par) {
		final String jc = checkPrim(c);
		switch (jc) {
			case DOUBLE:
				sb.append("asFloat(s,").append(par).append(')');
				break;
			case INTEGER:
				sb.append("asInt(s,").append(par).append(')');
				break;
			case BOOLEAN:
				sb.append("asBool(s,").append(par).append(')');
				break;
			case OBJECT:
				sb.append(par);
				break;
			default:
				sb.append("((").append(jc).append(")").append(par).append(')');

		}
	}

	protected static String escapeDoubleQuotes(final String input) {
		if (input == null) return "";
		return SINGLE_QUOTE.matcher(input).replaceAll(QUOTE_MATCHER);
	}

	public static StringBuilder toArrayOfInts(final int[] array, final StringBuilder sb) {
		if (array == null || array.length == 0) {
			sb.append("AI");
			return sb;
		}
		sb.append("I(");
		for (final int i : array) {
			sb.append(i).append(",");
		}
		sb.setLength(sb.length() - 1);
		sb.append(")");
		return sb;
	}

	static String rawNameOf(final ProcessorContext context, final TypeMirror t) {
		if (t.getKind().equals(TypeKind.VOID)) return "void";
		final String key = t.toString();
		if (NAME_CACHE.containsKey(key)) return NAME_CACHE.get(key);
		String type = context.getTypeUtils().erasure(t).toString();
		// As a workaround for ECJ/javac discrepancies regarding erasure
		type = CLASS_PARAM.matcher(type).replaceAll("");
		// Reduction by considering the imports written in the header
		for (final String element : GamaProcessor.IMPORTS) {
			if (type.startsWith(element)) {
				String tmp=type.replace(element + ".", "");
				type = tmp.contains(".")?type:tmp;// type.replace(element + ".", "");
				break;
			}
		}
		// context.emit(Kind.NOTE, "Type to convert : " + key + " | Reduction: " + type, null);
		NAME_CACHE.put(key, type);
		return type;
	}

	protected String toBoolean(final boolean b) {
		return b ? "T" : "F";
	}

	// -------------- VALIDATION METHODS

	/**
	 * A method that allows to verify that the element on which annotations are processed is valid. Should return true
	 * if it is the case, false otherwise. And errors should be produced in this case
	 *
	 * @param context
	 *            the current processor context (which gives access to various utilities)
	 * @param e
	 *            the current element to be processed
	 */
	protected boolean validateElement(final ProcessorContext context, final Element e) {
		return true;
	}

	protected boolean assertContainsScope(final ProcessorContext context, final boolean throwsError,
			final ExecutableElement e) {
		for (VariableElement v : e.getParameters()) {
			if (v.asType().toString().endsWith("IScope")) return true;
		}
		context.emit(throwsError ? Kind.ERROR : Kind.WARNING, "IScope must be passed as a parameter to this method", e);
		return !throwsError;
	}

	protected boolean assertArgumentsSize(final ProcessorContext context, final boolean throwsError,
			final ExecutableElement e, final int i) {
		List<? extends VariableElement> parameters = e.getParameters();
		if (parameters.size() == 1) return true;
		context.emit(throwsError ? Kind.ERROR : Kind.WARNING, "The size of parameters should be equal to " + i, e);
		return !throwsError;
	}

	protected boolean assertNotVoid(final ProcessorContext context, final boolean throwsError,
			final ExecutableElement e) {
		if (!e.getReturnType().getKind().equals(TypeKind.VOID)) return true;
		context.emit(throwsError ? Kind.ERROR : Kind.WARNING,
				"The method should return a result to be annotated with " + getAnnotationClass().getSimpleName(), e);
		return !throwsError;
	}

	protected boolean assertClassIsAgentOrSkill(final ProcessorContext context, final boolean throwsError,
			final TypeElement e) {
		TypeMirror t = e.asType();
		if (context.getTypeUtils().isAssignable(t, context.getIAgent())
				|| context.getTypeUtils().isAssignable(t, context.getISkill()))
			return true;
		context.emit(throwsError ? Kind.ERROR : Kind.WARNING, getAnnotationClass().getSimpleName()
				+ " annotations do not make sense outside IAgent or ISkill subclasses", e);
		return !throwsError;
	}

	protected boolean assertClassExtends(final ProcessorContext context, final boolean throwsError, final TypeElement e,
			final TypeMirror type) {
		if (context.getTypeUtils().isAssignable(e.asType(), type)) return true;
		context.emit(throwsError ? Kind.ERROR : Kind.WARNING, getAnnotationClass().getSimpleName()
				+ " annotations shoud be placed on classes extending or implementing " + type.toString(), e);
		return !throwsError;
	}

	protected boolean assertOneScopeAndStringConstructor(final ProcessorContext context, final boolean throwsError,
			final TypeElement e) {
		for (Element ee : e.getEnclosedElements()) {
			if (ee.getKind() == ElementKind.CONSTRUCTOR) {
				ExecutableElement constr = (ExecutableElement) ee;
				List<? extends VariableElement> param = constr.getParameters();
				if (param.size() == 2 && param.get(0).asType().equals(context.getIScope())
						&& param.get(1).asType().equals(context.getString()))
					return true;
			}
		}
		context.emit(throwsError ? Kind.ERROR : Kind.WARNING, getAnnotationClass().getSimpleName() + " " + e.toString()
				+ " should declare at least one constructor with the signature (IScope scope, String fileName) to be usable in GAML",
				e);
		return !throwsError;

	}

	protected boolean assertAnnotationPresent(final ProcessorContext context, final boolean throwsError,
			final Element e, final Class<? extends Annotation> anno) {
		if (e.getAnnotation(anno) != null) return true;
		context.emit(throwsError ? Kind.ERROR : Kind.WARNING,
				"A @" + anno.getSimpleName() + " annotation should be present on this element", e);
		return !throwsError;

	}

	protected boolean assertElementIsPublic(final ProcessorContext context, final boolean throwsError,
			final Element e) {
		final Set<Modifier> modifiers = e.getModifiers();
		if (modifiers.contains(Modifier.PUBLIC)) return true;
		context.emit(throwsError ? Kind.ERROR : Kind.WARNING,
				getAnnotationClass().getSimpleName() + "s can only be implemented by public elements", e);
		return !throwsError;
	}

}
