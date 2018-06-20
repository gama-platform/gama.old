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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import msi.gama.precompiler.GamlAnnotations.doc;

public abstract class ElementProcessor<T extends Annotation> implements IProcessor<T>, Constants {
	public Document document;
	final Map<String, List<org.w3c.dom.Element>> index = new HashMap<>();

	public ElementProcessor() {
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (final ParserConfigurationException e1) {}
		document = builder == null ? null : builder.newDocument();
	}

	@Override
	public void processXML(final ProcessorContext context) {
		if (document == null) { return; }
		final Class<T> a = getAnnotationClass();
		if (a == null) { return; }
		cleanIndex(context, index);
		for (final Map.Entry<String, List<Element>> entry : context.groupElements(a).entrySet()) {
			final List<org.w3c.dom.Element> list = clearAndGetFrom(entry.getKey(), index);
			for (final Element e : entry.getValue()) {
				try {
					final org.w3c.dom.Element node = document.createElement(getElementName());
					populateElement(context, e, e.getAnnotation(a), node);
					list.add(node);
					getRootNode(document).appendChild(node);
				} catch (final Exception exception) {
					context.emitError("Exception in processor: " + exception.getMessage(), e);
				}

			}
		}
	}

	protected void cleanIndex(final ProcessorContext context, final Map<String, List<org.w3c.dom.Element>> index) {

		for (final String k : context.getRoots()) {
			final List<org.w3c.dom.Element> nodes = index.get(k);
			if (nodes != null) {
				nodes.forEach(n -> n.getParentNode().removeChild(n));
				index.remove(k);
			}

		}
	}

	protected List<org.w3c.dom.Element> clearAndGetFrom(final String key,
			final Map<String, List<org.w3c.dom.Element>> currentIndex) {
		List<org.w3c.dom.Element> list = index.get(key);
		if (list == null) {
			list = new ArrayList<>();
			currentIndex.put(key, list);
		} else {
			list.forEach(n -> n.getParentNode().removeChild(n));
			list.clear();
		}
		return list;
	}

	protected abstract void populateElement(final ProcessorContext context, final Element e, final T action,
			final org.w3c.dom.Element node);

	protected abstract Class<T> getAnnotationClass();

	protected String getRootName() {
		final String element = getElementName();
		if (element == null) { return null; }
		return element + "s";
	}

	protected String getElementName() {
		final Class<T> c = getAnnotationClass();
		if (c == null) { return null; }
		return c.getSimpleName();
	}

	@Override
	public final String getInitializationMethodName() {
		return "initialize" + Constants.capitalizeFirstLetter(getElementName());
	}

	@Override
	public void writeTo(final ProcessorContext context, final StringBuilder sb) {
		final org.w3c.dom.Element node = document.getDocumentElement();
		if (node == null) { return; }
		final NodeList nl = node.getElementsByTagName(getElementName());
		for (int i = 0; i < nl.getLength(); i++) {
			populateJava(context, sb, (org.w3c.dom.Element) nl.item(i));
		}
	}

	protected abstract void populateJava(ProcessorContext context, StringBuilder sb, org.w3c.dom.Element node);

	/**
	 * 
	 * Utilities used by subclasses
	 */

	protected org.w3c.dom.Element getRootNode(final Document doc) {
		org.w3c.dom.Element root = null;
		if (doc.hasChildNodes()) {
			root = (org.w3c.dom.Element) doc.getElementsByTagName(getRootName()).item(0);
		}
		if (root == null) {
			root = doc.createElement(getRootName());
			doc.appendChild(root);
		}
		return root;
	}

	protected static org.w3c.dom.Element findFirstChildNamed(final org.w3c.dom.Element node, final String name) {
		if (node == null) { return null; }
		if (name == null) { return null; }
		final NodeList list = node.getElementsByTagName(name);
		if (list.getLength() == 0) { return null; }
		return (org.w3c.dom.Element) list.item(0);
	}

	protected void appendChild(final org.w3c.dom.Element node, final org.w3c.dom.Element child) {
		// final NodeList list = node.getElementsByTagName(child.getTagName());
		// for (int i = 0; i < list.getLength(); i++) {
		// final org.w3c.dom.Element candidate = (org.w3c.dom.Element) list.item(i);
		// if (isEqual(candidate, (child))) { return; }
		// }
		node.appendChild(child);
	}

	/**
	 * Compares two nodes. By default, node equality is being used, but subclasses may redefine if necessary
	 * 
	 * @param existingNode
	 *            the existing node to compare newNode with
	 * @param newNode
	 *            the node to be inserted
	 * @return true if they are to be considered as equal, false otherwise
	 */
	protected boolean isEqual(final org.w3c.dom.Element existingNode, final org.w3c.dom.Element newNode) {
		return existingNode.isEqualNode(newNode);
	}

	protected static String extractMethod(final String s, final boolean stat) {
		if (!stat) { return s; }
		return s.substring(s.lastIndexOf('.') + 1);
	}

	protected static String extractClass(final String name, final String string, final boolean stat) {
		if (stat) { return name.substring(0, name.lastIndexOf('.')); }
		return string;
	}

	protected static String toJavaString(final String s) {
		if (s == null || s.isEmpty()) { return "(String)null"; }
		final int i = ss1.indexOf(s);
		return i == -1 ? "\"" + s + "\"" : ss2.get(i);
	}

	protected static String toBoolean(final String s) {
		return s.equals("true") ? "true" : "false";
	}

	protected static String toType(final String s) {
		return s.isEmpty() ? "0" : s;
	}

	protected final static String toClassObject(final String s) {
		final String result = CLASS_NAMES.get(s);
		return result == null ? s + ".class" : result;
	}

	final static String[] EMPTY_ARGS = new String[0];

	protected final static String[] splitInClassObjects(final String array) {
		if (array == null || array.equals("")) { return EMPTY_ARGS; }
		// FIX AD 3/4/13: split(regex) would not include empty trailing strings
		final String[] segments = array.split("\\,", -1);
		// for (int i = 0; i < segments.length; i++) {
		// segments[i] = (segments[i]);
		// }
		return segments;
	}

	protected final static String toArrayOfStrings(final String array) {
		if (array == null || array.equals("")) { return "AS"; }
		final StringBuilder sb = new StringBuilder();
		// FIX AD 3/4/13: split(regex) would not include empty trailing strings
		final String[] segments = array.split("\\,", -1);
		sb.append("S(");
		for (int i = 0; i < segments.length; i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(toJavaString(segments[i]));
		}
		sb.append(')');
		return sb.toString();
	}

	protected final static String toArrayOfInts(final String array) {
		if (array == null || array.length() == 0) { return "AI"; }
		return "I(" + array + ")";
	}

	static final StringBuilder CONCAT = new StringBuilder();

	protected final static String concat(final String... array) {
		// final StringBuilder concat = new StringBuilder();
		for (final String element : array) {
			CONCAT.append(element);
		}
		final String result = CONCAT.toString();
		CONCAT.setLength(0);
		return result;
	}

	protected static String checkPrim(final String c) {
		final String result = CHECK_PRIM.get(c);
		return result == null ? c : result;
	}

	protected static String returnWhenNull(final String returnClass) {
		final String result = RETURN_WHEN_NULL.get(returnClass);
		return result == null ? " null" : result;
	}

	protected static String param(final String c, final String par) {
		final String jc = checkPrim(c);
		switch (jc) {
			case DOUBLE:
				return concat("asFloat(s,", par, ")");
			case INTEGER:
				return concat("asInt(s,", par, ")");
			case BOOLEAN:
				return concat("asBool(s,", par, ")");
			case OBJECT:
				return par;
			default:
				return concat("((", jc, ")", par, ")");
		}
	}

	protected static String escapeDoubleQuotes(final String input) {
		return input.replaceAll("\"", Matcher.quoteReplacement("\\\""));
	}

	public static String arrayToString(final int[] array) {
		if (array.length == 0) { return ""; }
		final StringBuilder sb = new StringBuilder();
		for (final int i : array) {
			sb.append(i).append(",");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	static String arrayToString(final String[] array) {
		if (array.length == 0) { return ""; }
		final StringBuilder sb = new StringBuilder();
		for (final String i : array) {
			sb.append(replaceCommas(i)).append(",");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	static String replaceCommas(final String s) {
		return s.replace(",", "COMMA");
	}

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
		return docToString(docs[0]);
	}

	static String docToString(final doc doc) {
		if (doc == null) { return ""; }
		final StringBuilder sb = new StringBuilder();
		sb.append(doc.value()).append(DOC_SEP);
		sb.append(doc.deprecated());
		return sb.toString();
	}

	static String rawNameOf(final ProcessorContext context, final Element e) {
		return rawNameOf(context, e.asType());
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
