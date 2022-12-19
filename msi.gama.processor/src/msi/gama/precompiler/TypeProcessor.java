/*******************************************************************************************************
 *
 * TypeProcessor.java, in msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.doc.utils.TypeConverter;

/**
 * The Class TypeProcessor.
 */
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
		verifyDoc(context, e, "type " + t.name(), t);
		for (final Element m : e.getEnclosedElements()) {
			if (m.getKind() == ElementKind.METHOD && m.getSimpleName().contentEquals("cast")) {
				final ExecutableElement ee = (ExecutableElement) m;
				if (ee.getParameters().size() == 4) {
					verifyDoc(context, m, "the casting operator of " + t.name(), null);
				}
			}
		}
		sb.append(in).append("_type(").append(toJavaString(t.name())).append(",new ")
				.append(rawNameOf(context, e.asType())).append("(),").append(t.id()).append(',').append(t.kind());
		types.stream().map((ty) -> rawNameOf(context, ty)).forEach(s -> {
			sb.append(',').append(toClassObject(s));
			TypeConverter.registerType(s, t.name(), t.id());
		});
		sb.append(");");

	}

	@Override
	protected Class<type> getAnnotationClass() {
		return type.class;
	}

	@Override
	protected boolean validateElement(final ProcessorContext context, final Element e) {
		boolean result = assertClassExtends(context, true, (TypeElement) e, context.getType("msi.gaml.types.IType"));
		// TODO Add existence of casting procedure ?
		return result;
	}

}
