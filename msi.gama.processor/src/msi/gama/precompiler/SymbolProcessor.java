package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;

public class SymbolProcessor extends ElementProcessor<symbol> {

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final symbol symbol) {
		final String clazz = rawNameOf(context, e.asType());
		verifyDoc(context, e, symbol);
		final StringBuilder constants = new StringBuilder();

		sb.append(in).append("_symbol(");
		toArrayOfStrings(symbol.name(), sb).append(',').append(toClassObject(clazz));
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
				sb.append("_facet(").append(toJavaString(child.name())).append(',');
				toArrayOfInts(child.type(), sb).append(',').append(child.of()).append(',').append(child.index())
						.append(',');
				final String[] values = child.values();
				if (values != null && values.length > 0) {
					toArrayOfStrings(values, constants).append(',');
				}
				toArrayOfStrings(values, sb).append(',').append(toBoolean(child.optional())).append(',')
						.append(toBoolean(child.internal()));
				final doc[] d = child.doc();
				if (d == null || d.length == 0) {
					if (!child.internal()) {
						UNDOCUMENTED.add(child.name());
					}
				} else {}
				sb.append(')');
			}
			sb.append(')');
		}
		sb.append(',').append(toJavaString(omissible)).append(',').append("(x)->new ").append(clazz).append("(x)");
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

	@Override
	protected Class<symbol> getAnnotationClass() {
		return symbol.class;
	}

}
