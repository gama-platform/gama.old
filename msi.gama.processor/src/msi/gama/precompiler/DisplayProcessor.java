package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.DisplayProcessor.Dis;
import msi.gama.precompiler.GamlAnnotations.display;

public class DisplayProcessor extends ElementProcessor<display, Dis> {

	public static class Dis {
		String clazz;
		String name;
	}

	@Override
	protected Class<display> getAnnotationClass() {
		return display.class;
	}

	@Override
	public void createJava(final ProcessorContext context, final StringBuilder sb, final Dis node) {
		sb.append(in).append("_display(").append(toJavaString(node.name)).append(',').append(toClassObject(node.clazz))
				.append(", new IDisplayCreator(){").append(OVERRIDE)
				.append("public IDisplaySurface create(Object...args){return new ").append(node.clazz)
				.append("(args);}});");
	}

	@Override
	public Dis createElement(final ProcessorContext context, final Element e, final display d) {
		final Dis dis = new Dis();
		dis.name = d.value();
		dis.clazz = rawNameOf(context, e.asType());
		return dis;
	}

}
