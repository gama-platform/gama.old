package msi.gama.precompiler;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

public abstract class ElementProcessor<T extends Annotation> implements IProcessor<T>, Constants {

	protected static final Map<String, String> NAME_CACHE = new HashMap<>();

	protected final Map<String, StringBuilder> opIndex = new HashMap<>();
	static final Pattern CLASS_PARAM = Pattern.compile("<.*>");
	static final Pattern SINGLE_QUOTE = Pattern.compile("\"");
	static final String QUOTE_MATCHER = Matcher.quoteReplacement("\\\"");
	// final static StringBuilder DOC_BUILDER = new StringBuilder();
	protected String initializationMethodName;
	final static Set<String> UNDOCUMENTED = new HashSet<>();

	public ElementProcessor() {}

	protected void clean(final ProcessorContext context, final Map<String, StringBuilder> map) {
		for (final String k : context.getRoots()) {
			map.remove(k);
		}
	}

	@Override
	public boolean hasElements() {
		return opIndex.size() > 0;
	}

	@Override
	public void process(final ProcessorContext context) {
		final Class<T> a = getAnnotationClass();
		clean(context, opIndex);
		for (final Map.Entry<String, List<Element>> entry : context.groupElements(a).entrySet()) {
			final List<Element> elements = entry.getValue();
			if (elements.size() == 0) {
				continue;
			}
			final StringBuilder sb = new StringBuilder();
			for (final Element e : elements) {
				try {
					createElement(sb, context, e, e.getAnnotation(a));
				} catch (final Exception exception) {
					context.emitError("Exception in processor: " + exception.getMessage(), e);
				}

			}
			if (sb.length() > 0) {
				opIndex.put(entry.getKey(), sb);
			}
		}
	}

	@Override
	public void serialize(final ProcessorContext context, final StringBuilder sb) {
		opIndex.forEach((s, builder) -> {
			if (builder != null) {
				sb.append(builder);
			}
		});
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
		if (s == null || s.isEmpty()) { return "(String)null"; }
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
			if (i > 0) {
				sb.append(',');
			}
			sb.append(toJavaString(segments[i]));
		}
		sb.append(')');
		return sb;
	}

	protected static String checkPrim(final String c) {
		final String result = CHECK_PRIM.get(c);
		return result == null ? c : result;
	}

	protected static String returnWhenNull(final String returnClass) {
		final String result = RETURN_WHEN_NULL.get(returnClass);
		return result == null ? " null" : result;
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
		if (input == null) { return ""; }
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

	/**
	 * Format 0.value 1.deprecated 2.returns 3.comment 4.nb_cases 5.[specialCases$]* 6.nb_examples 7.[examples$]* Uses
	 * its own separator (DOC_SEP)
	 *
	 * @param docs
	 *            an Array of @doc annotations (only the 1st is significant)
	 * @return aString containing the documentation formatted using the format above
	 */
	// static String docToString(final doc[] docs) {
	// if (docs == null || docs.length == 0) { return ""; }
	// final doc doc1 = docs[0];
	// if (doc1 == null) { return ""; }
	// DOC_BUILDER.append(doc1.value()).append(DOC_SEP).append(doc1.deprecated());
	// final String result = DOC_BUILDER.toString();
	// DOC_BUILDER.setLength(0);
	// return result;
	// }

	static String rawNameOf(final ProcessorContext context, final TypeMirror t) {
		if (t.getKind().equals(TypeKind.VOID)) { return "void"; }
		final String key = t.toString();
		if (NAME_CACHE.containsKey(key)) { return NAME_CACHE.get(key); }

		String type = context.getTypeUtils().erasure(t).toString();

		// String type2 = CLASS_PARAM.matcher(type).replaceAll("");

		for (final String element : GamaProcessor.IMPORTS) {
			if (type.startsWith(element)) {
				type = type.replace(element + ".", "");
				break;
			}
		}
		context.emit(Kind.NOTE, "Type to convert : " + key + " | Reduction: " + type, null);
		NAME_CACHE.put(key, type);
		return type;
	}

	protected String toBoolean(final boolean b) {
		return b ? "T" : "F";
	}

}
