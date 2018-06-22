package msi.gama.precompiler;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.doc;

public abstract class ElementProcessor<T extends Annotation, Result> implements IProcessor<T>, Constants {

	protected final Map<String, List<Result>> opIndex = new HashMap<>();

	public ElementProcessor() {}

	protected void clean(final ProcessorContext context, final Map<String, List<Result>> map) {
		for (final String k : context.getRoots()) {
			map.remove(k);
		}
	}

	protected <T> List<T> get(final String root, final Map<String, List<T>> index) {
		List<T> list = index.get(root);
		if (list == null) {
			list = new ArrayList<>();
			index.put(root, list);
		} else {
			list.clear();
		}
		return list;

	}

	@Override
	public void process(final ProcessorContext context) {
		final Class<T> a = getAnnotationClass();
		clean(context, opIndex);
		for (final Map.Entry<String, List<Element>> entry : context.groupElements(a).entrySet()) {
			final List<Result> list = get(entry.getKey(), opIndex);
			for (final Element e : entry.getValue()) {
				try {
					final Result node = createElement(context, e, e.getAnnotation(a));
					if (node != null) {
						list.add(node);
					}
				} catch (final Exception exception) {
					context.emitError("Exception in processor: " + exception.getMessage(), e);
				}

			}
		}
	}

	@Override
	public void serialize(final ProcessorContext context, final StringBuilder sb) {
		opIndex.forEach((s, list) -> list.forEach(op -> createJava(context, sb, op)));
	}

	public abstract void createJava(final ProcessorContext context, final StringBuilder sb, final Result op);

	public abstract Result createElement(ProcessorContext context, Element e, T annotation);

	protected abstract Class<T> getAnnotationClass();

	@Override
	public final String getInitializationMethodName() {
		return "initialize" + Constants.capitalizeFirstLetter(getAnnotationClass().getSimpleName());
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
		return input.replaceAll("\"", Matcher.quoteReplacement("\\\""));
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

	final static StringBuilder DOC_BUILDER = new StringBuilder();

	/**
	 * Format 0.value 1.deprecated 2.returns 3.comment 4.nb_cases 5.[specialCases$]* 6.nb_examples 7.[examples$]* Uses
	 * its own separator (DOC_SEP)
	 *
	 * @param docs
	 *            an Array of @doc annotations (only the 1st is significant)
	 * @return aString containing the documentation formatted using the format above
	 */
	static String docToString(final doc[] docs) {
		if (docs == null || docs.length == 0) { return ""; }
		final doc doc1 = docs[0];
		if (doc1 == null) { return ""; }
		DOC_BUILDER.append(doc1.value()).append(DOC_SEP);
		DOC_BUILDER.append(doc1.deprecated());
		final String result = DOC_BUILDER.toString();
		DOC_BUILDER.setLength(0);
		return result;
	}

	static String rawNameOf(final ProcessorContext context, final TypeMirror t) {
		if (t.getKind().equals(TypeKind.VOID)) { return "void"; }
		final String init = context.getTypeUtils().erasure(t).toString();
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
		for (final String element : IMPORTS) {
			if (clazz.startsWith(element)) {
				// AD: false
				final String temp = clazz.replace(element + ".", "");
				if (!temp.contains(".")) {
					clazz = temp;
				}
			}
		}
		return clazz;
	}

}
