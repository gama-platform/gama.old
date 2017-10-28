package msi.gama.precompiler;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.regex.Matcher;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.java.Constants;

public abstract class ElementProcessor<T extends Annotation> implements IProcessor<T>, Constants {
	private static final String XML_VERSION = "1.0";
	private static final String XML_ENCODING = "ISO-8859-1";
	public static DocumentBuilder BUILDER = null;
	public static Transformer TRANSFORMER = null;
	public Document document = getBuilder().newDocument();

	static DocumentBuilder getBuilder() {
		if (BUILDER == null)
			try {
				BUILDER = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (final ParserConfigurationException e) {}
		return BUILDER;
	}

	@Override
	public void processXML(final ProcessorContext context) {
		final Class<T> a = getAnnotationClass();
		if (a == null)
			return;
		final Document doc = getDocument(context);
		final List<? extends Element> elements = context.sortElements(a);
		if (elements.isEmpty())
			return;
		for (final Element e : elements) {
			final org.w3c.dom.Element node = doc.createElement(getElementName());
			populateElement(context, e, doc, e.getAnnotation(a), node);
			appendChild(getRootNode(doc), node);
		}
		// XML Files are not saved for the moment as they seem to be correctly kept in memory
		// saveDocument(context, doc);
	}

	protected abstract void populateElement(final ProcessorContext context, final Element e, final Document doc,
			final T action, final org.w3c.dom.Element node);

	protected abstract Class<T> getAnnotationClass();

	Document getDocument(final ProcessorContext environment) {
		return document;
		// if (d)

		// try (InputStream is = environment.getInputStream(getFilename())) {
		// return getBuilder().parse(is);
		// } catch (final SAXException e) {
		// environment.emitWarning("File " + getFilename() + " is corrupted. Creating a new document", null);
		// } catch (final IOException e1) {
		// // environment.emitWarning("File " + getFilename() + " cannot be found. Creating a new document", null);
		// }
		// return getBuilder().newDocument();
	}

	// void saveDocument(final ProcessorContext context, final Document doc) {
	// try (final Writer xmlWriter = context.createWriter(getFilename())) {
	// final OutputFormat outFormat = new OutputFormat(Method.XML, XML_ENCODING, true);
	// final XMLSerializer serializer = new XMLSerializer(xmlWriter, outFormat);
	// serializer.serialize(doc);
	// } catch (final IOException ioEx) {
	// System.out.println("Error " + ioEx);
	// }
	//
	// }

	// void saveDocument(final ProcessorContext context, final Document doc) {
	// try (final Writer xmlWriter = context.createWriter(getFilename())) {
	// final DOMSource domSource = new DOMSource(doc);
	// final Transformer transformer = TransformerFactory.newInstance().newTransformer();
	// transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	// transformer.setOutputProperty(OutputKeys.ENCODING, ProcessorContext.CHARSET.displayName());
	// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	// final StreamResult sr = new StreamResult(xmlWriter);
	// transformer.transform(domSource, sr);
	// } catch (final IOException | TransformerException ioEx) {
	// context.emitWarning("Impossible to create " + getFilename(), null);
	// }
	//
	// }

	// protected String getFilename() {
	// return getRootName() + ".xml";
	// }

	protected final String getRootName() {
		final String element = getElementName();
		if (element == null)
			return null;
		return element + "s";
	}

	protected final String getElementName() {
		final Class<T> c = getAnnotationClass();
		if (c == null)
			return null;
		return c.getSimpleName();
	}

	@Override
	public final String getInitializationMethodName() {
		return "initialize" + Constants.capitalizeFirstLetter(getElementName());
	}

	@Override
	public void writeTo(final ProcessorContext context, final StringBuilder sb) {
		final Document doc = getDocument(context);
		final org.w3c.dom.Element node = doc.getDocumentElement();
		if (node == null)
			return;
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

	org.w3c.dom.Element getRootNode(final Document doc) {
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

	protected org.w3c.dom.Element findFirstChildNamed(final org.w3c.dom.Element node, final String name) {
		if (node == null)
			return null;
		if (name == null)
			return null;
		final NodeList list = node.getElementsByTagName(name);
		if (list.getLength() == 0)
			return null;
		return (org.w3c.dom.Element) list.item(0);
	}

	protected void appendChild(final org.w3c.dom.Element node, final org.w3c.dom.Element child) {
		final NodeList list = node.getElementsByTagName(child.getTagName());
		for (int i = 0; i < list.getLength(); i++) {
			final org.w3c.dom.Element candidate = (org.w3c.dom.Element) list.item(i);
			if (candidate.isEqualNode(child))
				return;
		}
		node.appendChild(child);
	}

	protected String extractMethod(final String s, final boolean stat) {
		if (!stat) { return s; }
		return s.substring(s.lastIndexOf('.') + 1);
	}

	protected String extractClass(final String name, final String string, final boolean stat) {
		if (stat) { return name.substring(0, name.lastIndexOf('.')); }
		return string;
	}

	protected String toJavaString(final String s) {
		if (s == null || s.isEmpty()) { return "(String)null"; }
		final int i = ss1.indexOf(s);
		return i == -1 ? "\"" + s + "\"" : ss2.get(i);
	}

	protected String toBoolean(final String s) {
		return s.equals("true") ? "true" : "false";
	}

	protected String toType(final String s) {
		return s.isEmpty() ? "0" : s;
	}

	protected final String toClassObject(final String s) {
		final String result = CLASS_NAMES.get(s);
		return result == null ? s + ".class" : result;
	}

	protected final String[] splitInClassObjects(final String array) {
		if (array == null || array.equals("")) { return new String[0]; }
		// FIX AD 3/4/13: split(regex) would not include empty trailing strings
		final String[] segments = array.split("\\,", -1);
		for (int i = 0; i < segments.length; i++) {
			segments[i] = segments[i];
		}
		return segments;
	}

	protected final String toArrayOfStrings(final String array) {
		return toArrayOfStrings(array, "\\,");
	}

	protected final String toArrayOfInts(final String array) {
		if (array == null || array.length() == 0) { return "AI"; }
		return "I(" + array + ")";
	}

	protected final String toArrayOfStrings(final String array, final String regex) {
		if (array == null || array.equals("")) { return "AS"; }
		// FIX AD 3/4/13: split(regex) would not include empty trailing strings
		final String[] segments = array.split(regex, -1);
		String result = "S(";
		for (int i = 0; i < segments.length; i++) {
			if (i > 0) {
				result += ",";
			}
			result += toJavaString(segments[i]);
		}
		result += ")";
		return result;
	}

	protected final String concat(final String... tab) {
		final StringBuilder concat = new StringBuilder();
		for (final String element : tab) {
			concat.append(element);
		}
		return concat.toString();
	}

	protected String checkPrim(final String c) {
		final String result = CHECK_PRIM.get(c);
		return result == null ? c : result;
	}

	protected String returnWhenNull(final String returnClass) {
		final String result = RETURN_WHEN_NULL.get(returnClass);
		return result == null ? " null" : result;
	}

	protected String param(final String c, final String par) {
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

	protected String escapeDoubleQuotes(final String input) {
		return input.replaceAll("\"", Matcher.quoteReplacement("\\\""));
	}

	public String arrayToString(final int[] array) {
		if (array.length == 0) { return ""; }
		final StringBuilder sb = new StringBuilder();
		for (final int i : array) {
			sb.append(i).append(",");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	String arrayToString(final String[] array) {
		if (array.length == 0) { return ""; }
		final StringBuilder sb = new StringBuilder();
		for (final String i : array) {
			sb.append(replaceCommas(i)).append(",");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	String replaceCommas(final String s) {
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
	String docToString(final doc[] docs) {
		if (docs == null || docs.length == 0) { return ""; }
		return docToString(docs[0]);
	}

	String docToString(final doc doc) {
		if (doc == null) { return ""; }
		final StringBuilder sb = new StringBuilder();
		sb.append(doc.value()).append(DOC_SEP);
		sb.append(doc.deprecated());
		return sb.toString();
	}

	String rawNameOf(final ProcessorContext context, final Element e) {
		return rawNameOf(context, e.asType());
	}

	String rawNameOf(final ProcessorContext context, final TypeMirror t) {
		if (t.getKind().equals(TypeKind.VOID))
			return "void";
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

}
