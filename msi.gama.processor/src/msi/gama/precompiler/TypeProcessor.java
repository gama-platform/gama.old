package msi.gama.precompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.TypeProcessor.Type;

public class TypeProcessor extends ElementProcessor<type, Type> {

	public static class Type {

		public String name;
		public int id;
		public int kind;
		public String clazz;
		List<String> wraps = new ArrayList<>();

	}

	@Override
	public void createJava(final ProcessorContext context, final StringBuilder sb, final Type type) {
		sb.append(in).append("_type(").append(toJavaString(type.name)).append(",new ").append(type.clazz).append("(),")
				.append(type.id).append(',').append(type.kind);
		for (final String wrap : type.wraps) {
			sb.append(',').append(toClassObject(wrap));
		}
		sb.append(");");
	}

	@Override
	public Type createElement(final ProcessorContext context, final Element e, final type t) {
		final Type node = new Type();
		node.name = t.name();
		node.id = t.id();
		node.kind = t.kind();
		node.clazz = rawNameOf(context, e.asType());
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
			node.wraps.add(rawNameOf(context, tm));
		}
		verifyDoc(context, e, t);
		return node;
	}

	private void verifyDoc(final ProcessorContext context, final Element e, final type t) {
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

}
