/*******************************************************************************************************
 *
 * DisplayProcessor.java, in msi.gama.processor, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.precompiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import msi.gama.precompiler.GamlAnnotations.display;

/**
 * The Class DisplayProcessor.
 */
public class DisplayProcessor extends ElementProcessor<display> {

	@Override
	protected Class<display> getAnnotationClass() { return display.class; }

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final display d) {
		String[] names = d.value();
		if (names == null) return;
		for (String name : names) {
			verifyDoc(context, e, "display " + name, d);
			String clazz = rawNameOf(context, e.asType());
			sb.append(in).append("_display(").append(toJavaString(name)).append(",").append(clazz)
					.append(".class,(a)->new ").append(clazz).append("(a));");
		}

	}

	@Override
	protected boolean validateElement(final ProcessorContext context, final Element e) {
		return assertClassExtends(context, true, (TypeElement) e,
				context.getType("msi.gama.common.interfaces.IDisplaySurface"));
	}
}
