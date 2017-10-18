package msi.gama.precompiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.serializer;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;

public class SymbolProcessor extends ElementProcessor<symbol> {

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final Document doc,
			final symbol symbol, final org.w3c.dom.Element node) {
		addValidator(context, e, node);
		addSerializer(context, e, node);
		node.setAttribute("names", arrayToString(symbol.name()));
		node.setAttribute("kind", String.valueOf(symbol.kind()));
		node.setAttribute("class", rawNameOf(context, e));
		if (symbol.remote_context())
			node.setAttribute("remote", "true");
		if (symbol.with_args())
			node.setAttribute("args", "true");
		if (symbol.with_scope())
			node.setAttribute("scope", "true");
		if (symbol.with_sequence())
			node.setAttribute("sequence", "true");
		if (symbol.unique_in_context())
			node.setAttribute("unique", "true");
		if (symbol.unique_name())
			node.setAttribute("unique_name", "true");
		final inside inside = e.getAnnotation(inside.class);
		if (inside != null) {
			final org.w3c.dom.Element child = doc.createElement("inside");
			child.setAttribute("symbols", arrayToString(inside.symbols()));
			child.setAttribute("kinds", arrayToString(inside.kinds()));
			appendChild(node, child);
		}
		final facets facets = e.getAnnotation(facets.class);
		final Set<String> undocumented = new HashSet<>();
		if (facets != null) {
			for (final facet facet : facets.value()) {
				final org.w3c.dom.Element child = doc.createElement("facet");
				child.setAttribute("name", facet.name());
				child.setAttribute("types", arrayToString(facet.type()));
				if (facet.of() != 0)
					child.setAttribute("contents", String.valueOf(facet.of()));
				if (facet.index() != 0)
					child.setAttribute("index", String.valueOf(facet.index()));
				if (facet.values().length > 0)
					child.setAttribute("values", arrayToString(facet.values()));
				if (facet.optional())
					child.setAttribute("optional", "true");
				if (facet.internal())
					child.setAttribute("internal", "true");
				final doc[] d = facet.doc();
				if (d == null || d.length == 0) {
					if (!facet.internal())
						undocumented.add(facet.name());
				} else
					child.setAttribute("doc", docToString(facet.doc()));
				appendChild(node, child);
				if (!undocumented.isEmpty())
					context.emitWarning("GAML: facets '" + undocumented + "' are not documented", e);
			}
			node.setAttribute("omissible", facets.omissible());
		}

		final doc d = e.getAnnotation(doc.class);

		if (d == null && !symbol.internal()) {
			context.emitWarning("GAML: symbol '" + symbol.name()[0] + "' is not documented", e);
		}

	}

	public void addValidator(final ProcessorContext context, final Element e, final org.w3c.dom.Element node) {
		validator validator = e.getAnnotation(validator.class);
		TypeMirror sup = ((TypeElement) e).getSuperclass();
		// Workaround for bug
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=419944
		// Effectively inherits from a given validator
		while (validator == null && sup != null) {
			if (sup.getKind().equals(TypeKind.NONE)) {
				sup = null;
				continue;
			}
			final TypeElement te = (TypeElement) context.getTypeUtils().asElement(sup);
			validator = te.getAnnotation(validator.class);
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
		if (type_validator != null)
			node.setAttribute("validator", rawNameOf(context, type_validator));
	}

	public void addSerializer(final ProcessorContext context, final Element e, final org.w3c.dom.Element node) {
		TypeMirror sup;
		sup = ((TypeElement) e).getSuperclass();
		serializer serializer = e.getAnnotation(serializer.class);
		while (serializer == null && sup != null) {
			if (sup.getKind().equals(TypeKind.NONE)) {
				sup = null;
				continue;
			}
			final TypeElement te = (TypeElement) context.getTypeUtils().asElement(sup);
			serializer = te.getAnnotation(serializer.class);
			sup = te.getSuperclass();
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
		if (type_serializer != null)
			node.setAttribute("serializer", rawNameOf(context, type_serializer));
	}

	@Override
	protected Class<symbol> getAnnotationClass() {
		return symbol.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {

		String validator = node.getAttribute("validator");
		if (validator.isEmpty()) {
			validator = "null";
		} else {
			validator = "new " + validator + "()";
		}
		String serializer = node.getAttribute("serializer");
		if (serializer.isEmpty()) {
			serializer = "null";
		} else {
			serializer = "new " + serializer + "()";
		}
		final String names = toArrayOfStrings(node.getAttribute("names"));
		final String kind = node.getAttribute("kind");
		final String clazz = node.getAttribute("class");
		final String remote = toBoolean(node.getAttribute("remote"));
		final String args = toBoolean(node.getAttribute("args"));
		final String scope = toBoolean(node.getAttribute("scope"));
		final String sequence = toBoolean(node.getAttribute("sequence"));
		final String unique = toBoolean(node.getAttribute("unique"));
		final String name_unique = toBoolean(node.getAttribute("unique_name"));
		String parentSymbols = "AI", parentKinds = "AS";
		final org.w3c.dom.Element inside = findFirstChildNamed(node, "inside");
		if (inside != null) {
			parentSymbols = toArrayOfStrings(inside.getAttribute("symbols"));
			parentKinds = toArrayOfInts(inside.getAttribute("kinds"));
		}
		String facets;
		String constants = "";

		final List<org.w3c.dom.Element> nodes = findChildrenNamed(node, "facet");
		if (nodes.isEmpty()) {
			facets = "null";
		} else {
			facets = "P(";
			for (int i = 0; i < nodes.size(); i++) {
				final org.w3c.dom.Element child = nodes.get(i);
				if (i > 0) {
					facets += ",";
				}
				facets += "new FacetProto(";
				facets += toJavaString(child.getAttribute("name")) + ',';
				facets += toArrayOfInts(child.getAttribute("types")) + ",";
				facets += toType(child.getAttribute("contents")) + ",";
				facets += toType(child.getAttribute("index")) + ",";
				final String values = child.getAttribute("values");
				if (!values.isEmpty()) {
					constants += toArrayOfStrings(values) + ",";
				}
				facets += toArrayOfStrings(values) + ",";
				facets += toBoolean(child.getAttribute("optional")) + ',';
				facets += toBoolean(child.getAttribute("internal")) + ',';
				facets += toJavaString(escapeDoubleQuotes(child.getAttribute("doc")));
				facets += ")";
			}
			facets += ")";
		}

		final String omissible = node.getAttribute("omissible");

		final String sc = concat("new ISymbolConstructor() {", OVERRIDE,
				"public ISymbol create(" + IDESC + " d) {return new ", clazz, "(d);}}");
		sb.append(in).append("_symbol(").append(names).append(',').append(toClassObject(clazz)).append(",");
		sb.append(validator).append(',').append(serializer);
		sb.append(",").append(kind).append(',').append(remote).append(',').append(args).append(',').append(scope)
				.append(',');
		sb.append(sequence).append(',').append(unique).append(',').append(name_unique).append(',').append(parentSymbols)
				.append(",");
		sb.append(parentKinds).append(',').append(facets).append(',').append(toJavaString(omissible)).append(',')
				.append(sc);
		// ???? if (segments.length > pointer) {
		// for (int i = pointer; i < segments.length; i++) {
		// sb.append(',').append(toJavaString(segments[i]));
		// }
		// }
		sb.append(");");
		if (!constants.isEmpty()) {
			constants = constants.substring(0, constants.length() - 1);
			sb.append(ln).append("_constants(").append(constants).append(");");
		}

	}

	protected List<org.w3c.dom.Element> findChildrenNamed(final org.w3c.dom.Element node, final String name) {
		if (node == null)
			return Collections.EMPTY_LIST;
		if (name == null)
			return Collections.EMPTY_LIST;
		final NodeList list = node.getElementsByTagName(name);
		if (list.getLength() == 0)
			return Collections.EMPTY_LIST;
		final List<org.w3c.dom.Element> result = new ArrayList<>();
		for (int i = 0; i < list.getLength(); i++) {
			final org.w3c.dom.Element child = (org.w3c.dom.Element) list.item(i);
			result.add(child);
		}
		return result;
	}

}
