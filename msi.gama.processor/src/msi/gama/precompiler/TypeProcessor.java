package msi.gama.precompiler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;

public class TypeProcessor extends ElementProcessor<type> {

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final Document doc, final type t,
			final org.w3c.dom.Element node) {
		node.setAttribute("name", t.name());
		node.setAttribute("id", String.valueOf(t.id()));
		node.setAttribute("kind", String.valueOf(t.kind()));
		node.setAttribute("class", rawNameOf(context, e));
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
			final String type = rawNameOf(context, tm);
			final org.w3c.dom.Element child = doc.createElement("wraps");
			child.setAttribute("class", type);
			appendChild(node, child);
		}

		final doc[] docs = t.doc();
		doc d;
		if (docs.length == 0) {
			d = e.getAnnotation(doc.class);
		} else {
			d = docs[0];
		}
		if (d == null && !t.internal()) {
			context.emitWarning("GAML: type '" + t.name() + "' is not documented", e);
		}

	}

	@Override
	protected Class<type> getAnnotationClass() {
		return type.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {

		final String keyword = node.getAttribute("name");
		final String id = node.getAttribute("id");
		final String varKind = node.getAttribute("kind");
		final String clazz = node.getAttribute("class");
		sb.append(in).append("_type(").append(toJavaString(keyword)).append(",new ").append(clazz).append("(),")
				.append(id).append(',').append(varKind);
		final NodeList list = node.getElementsByTagName("*");
		for (int i = 0; i < list.getLength(); i++) {
			final org.w3c.dom.Element child = (org.w3c.dom.Element) list.item(i);
			sb.append(',').append(toClassObject(child.getAttribute("class")));
		}
		sb.append(");");

	}

}
