/*******************************************************************************************************
 *
 * SymbolProcessor.java, in msi.gama.processor, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.precompiler;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;

/**
 * The Class SymbolProcessor.
 */
public class SymbolProcessor extends ElementProcessor<symbol> {

	/** The temp. */
	Set<String> temp = new HashSet<>();

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final symbol symbol) {
		final String clazz = rawNameOf(context, e.asType());
		final String name = symbol.name().length == 0 ? e.getSimpleName().toString() : symbol.name()[0];
		verifyDoc(context, e, "symbol " + name, symbol);
		final StringBuilder constants = new StringBuilder();

		sb.append(in).append("_symbol(");
		toArrayOfStrings(symbol.name(), sb).append(',').append(toClassObject(clazz));
		sb.append(",").append(symbol.kind()).append(',').append(toBoolean(symbol.breakable())).append(',')
				.append(toBoolean(symbol.continuable())).append(',').append(toBoolean(symbol.remote_context()))
				.append(',').append(toBoolean(symbol.with_args())).append(',').append(toBoolean(symbol.with_scope()))
				.append(',');
		sb.append(toBoolean(symbol.with_sequence())).append(',').append(toBoolean(symbol.unique_in_context()))
				.append(',').append(toBoolean(symbol.unique_name())).append(',');
		final inside inside = e.getAnnotation(inside.class);
		if (inside != null) {
			toArrayOfStrings(inside.symbols(), sb).append(',');
			toArrayOfInts(inside.kinds(), sb).append(',');
		} else {
			toArrayOfStrings(null, sb).append(',');
			toArrayOfInts(null, sb).append(',');
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
				String fName = child.name();
				if (temp.contains(fName)) {
					context.emitError("Facet '" + fName + " is declared twice", e);
				} else {
					temp.add(fName);
				}
				if (i > 0) { sb.append(','); }
				sb.append("_facet(").append(toJavaString(fName)).append(',');
				toArrayOfInts(child.type(), sb).append(',').append(child.of()).append(',').append(child.index())
						.append(',');
				final String[] values = child.values();
				if (values != null && values.length > 0) { toArrayOfStrings(values, constants).append(','); }
				toArrayOfStrings(values, sb).append(',').append(toBoolean(child.optional())).append(',')
						.append(toBoolean(child.internal())).append(',').append(toBoolean(child.remote_context()));
				verifyDoc(context, e, "facet " + child.name(), child);
				sb.append(')');
			}
			temp.clear();
			sb.append(')');
		}
		sb.append(',').append(toJavaString(omissible)).append(',').append("(x)->new ").append(clazz).append("(x)");
		sb.append(");");
		if (constants.length() > 0) {
			constants.setLength(constants.length() - 1);
			sb.append(ln).append("_constants(").append(constants).append(");");
		}

	}

	@Override
	protected Class<symbol> getAnnotationClass() { return symbol.class; }

	@Override
	protected boolean validateElement(final ProcessorContext context, final Element e) {
		boolean result =
				assertClassExtends(context, true, (TypeElement) e, context.getType("msi.gaml.compilation.ISymbol"));
		result &= assertAnnotationPresent(context, false, e, inside.class);
		return result;
	}

}
