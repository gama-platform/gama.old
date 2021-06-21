package msi.gama.precompiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import msi.gama.precompiler.GamlAnnotations.display;

public class DisplayProcessor extends ElementProcessor<display> {

	@Override
	protected Class<display> getAnnotationClass() {
		return display.class;
	}

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final display d) {
		verifyDoc(context, e, "display " + d.value(), d);
		sb.append(in).append("_display(").append(toJavaString(d.value())).append(",(a)->new ")
				.append(rawNameOf(context, e.asType())).append("(a));");
	}

	@Override
	protected boolean validateElement(final ProcessorContext context, final Element e) {
		boolean result = assertClassExtends(context, true, (TypeElement) e,
				context.getType("msi.gama.common.interfaces.IDisplaySurface"));
		return result;
	}
}
