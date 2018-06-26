package msi.gama.precompiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.serializer;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;

public class SymbolProcessor extends ElementProcessor<symbol> {

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final symbol symbol) {
		final String clazz = rawNameOf(context, e.asType());
		verifyDoc(context, e, symbol);
		final StringBuilder constants = new StringBuilder();

		sb.append(in).append("_symbol(");
		toArrayOfStrings(symbol.name(), sb).append(',').append(toClassObject(clazz)).append(",");
		sb.append(getValidator(context, e)).append(',').append(getSerializer(context, e));
		sb.append(",").append(symbol.kind()).append(',').append(toBoolean(symbol.remote_context())).append(',')
				.append(toBoolean(symbol.with_args())).append(',').append(toBoolean(symbol.with_scope())).append(',');
		sb.append(toBoolean(symbol.with_sequence())).append(',').append(toBoolean(symbol.unique_in_context()))
				.append(',').append(toBoolean(symbol.unique_name())).append(',');
		final inside inside = e.getAnnotation(inside.class);
		if (inside != null) {
			toArrayOfStrings(inside.symbols(), sb).append(',');
			toArrayOfInts(inside.kinds(), sb).append(',');
		}
		final facets facets = e.getAnnotation(facets.class);
		String omissible = "";
		if (facets == null) {
			sb.append("null");
		} else {
			omissible = facets.omissible();
			sb.append("P(");
			for (int i = 0; i < facets.value().length; i++) {
				final facet child = facets.value()[i];
				if (i > 0) {
					sb.append(',');
				}
				sb.append("new FacetProto(").append(toJavaString(child.name())).append(',');
				toArrayOfInts(child.type(), sb).append(',').append(child.of()).append(',').append(child.index())
						.append(',');
				final String[] values = child.values();
				if (values != null && values.length > 0) {
					toArrayOfStrings(values, constants).append(',');
				}
				toArrayOfStrings(values, sb).append(',').append(toBoolean(child.optional())).append(',')
						.append(toBoolean(child.internal()));
				// sb.append(',');
				final doc[] d = child.doc();
				// String doc = "";
				if (d == null || d.length == 0) {
					if (!child.internal()) {
						UNDOCUMENTED.add(child.name());
					}
				} else {
					// doc = docToString(child.doc());
				}
				// sb.append(toJavaString(escapeDoubleQuotes(doc)));
				sb.append(')');
			}
			sb.append(')');
		}
		sb.append(',').append(toJavaString(omissible)).append(',').append("(d)->new ").append(clazz).append("(d)");
		// sb.append("new ISymbolConstructor() {").append("public ISymbol create(").append(IDESC)
		// .append(" d) {return new ").append(clazz).append("(d);}}");
		sb.append(");");
		if (constants.length() > 0) {
			constants.setLength(constants.length() - 1);
			sb.append(ln).append("_constants(").append(constants).append(");");
		}

	}

	private void verifyDoc(final ProcessorContext context, final Element e, final symbol symbol) {
		final doc d = e.getAnnotation(doc.class);
		if (d == null && !symbol.internal()) {
			context.emitWarning("GAML: symbol '" + symbol.name()[0] + "' is not documented", e);
		}
	}

	public String getValidator(final ProcessorContext context, final Element e) {
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
		if (type_validator != null) { return "new " + rawNameOf(context, type_validator) + "()"; }
		return "null";
	}

	public String getSerializer(final ProcessorContext context, final Element e) {
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
		if (type_serializer != null) { return "new " + rawNameOf(context, type_serializer) + "()"; }
		return "null";
	}

	@Override
	protected Class<symbol> getAnnotationClass() {
		return symbol.class;
	}

}
