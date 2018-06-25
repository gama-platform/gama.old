package msi.gama.precompiler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;

public class TypeProcessor extends ElementProcessor<type> {

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e, final type t) {
		List<? extends TypeMirror> types = Collections.EMPTY_LIST;
		// Trick to obtain the names of the classes...
		try {
			t.wraps();
		} catch (final MirroredTypesException ex) {
			try {
				types = ex.getTypeMirrors();
			} catch (final MirroredTypeException ex2) {
				types = Arrays.asList(ex2.getTypeMirror());
			}
		}
		verifyDoc(context, e, t);
		sb.append(in).append("_type(").append(toJavaString(t.name())).append(",new ")
				.append(rawNameOf(context, e.asType())).append("(),").append(t.id()).append(',').append(t.kind());
		types.stream().map((ty) -> rawNameOf(context, ty)).forEach(s -> sb.append(',').append(toClassObject(s)));
		sb.append(");");
	}

	private void verifyDoc(final ProcessorContext context, final Element e, final type t) {
		final doc[] docs = t.doc();
		final doc d = docs.length == 0 ? e.getAnnotation(doc.class) : docs[0];
		if (d == null && !t.internal()) {
			context.emitWarning("GAML: type '" + t.name() + "' is not documented", e);
		}
	}

	@Override
	protected Class<type> getAnnotationClass() {
		return type.class;
	}

}
