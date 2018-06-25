package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.display;

public class DisplayProcessor extends ElementProcessor<display> {

	@Override
	protected Class<display> getAnnotationClass() {
		return display.class;
	}

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final display d) {
		final String clazz = rawNameOf(context, e.asType());
		sb.append(in).append("_display(").append(toJavaString(d.value())).append(',').append(toClassObject(clazz))
				.append(", new IDisplayCreator(){").append(OVERRIDE)
				.append("public IDisplaySurface create(Object...args){return new ").append(clazz).append("(args);}});");
	}
}
