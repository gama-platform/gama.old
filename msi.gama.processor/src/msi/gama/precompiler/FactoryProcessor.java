/*******************************************************************************************************
 *
 * FactoryProcessor.java, in msi.gama.processor, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.precompiler;

import java.util.Collection;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import msi.gama.precompiler.GamlAnnotations.factory;

/**
 * The Class FactoryProcessor. Bound for removal
 */
@Deprecated
public class FactoryProcessor extends ElementProcessor<factory> {

	@Override
	protected Class<factory> getAnnotationClass() { return factory.class; }

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final factory factory) {
		sb.append("new ").append(rawNameOf(context, e.asType())).append("(I(");
		for (final int i : factory.handles()) { sb.append(i).append(','); }
		sb.setLength(sb.length() - 1);
		sb.append(")),");
	}

	@Override
	public void serialize(final ProcessorContext context, final Collection<StringBuilder> elements,
			final StringBuilder sb) {
		sb.append("_factories(");
		super.serialize(context, elements, sb);
		sb.setLength(sb.length() - 1);
		sb.append(");");
	}

	@Override
	protected boolean validateElement(final ProcessorContext context, final Element e) {
		return assertClassExtends(context, true, (TypeElement) e, context.getType("msi.gaml.factories.SymbolFactory"));
	}
}
