package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.display;

public class DisplayProcessor extends ElementProcessor<display> {

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final display action,
			final org.w3c.dom.Element node) {
		node.setAttribute("type", action.value());
		node.setAttribute("class", rawNameOf(context, e));
	}

	@Override
	protected Class<display> getAnnotationClass() {
		return display.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {
		final String name = node.getAttribute("type");
		final String clazz = node.getAttribute("class");
		sb.append(concat(in, "_display(", toJavaString(name), ",", toClassObject(clazz), ", new IDisplayCreator(){",
				OVERRIDE, "public IDisplaySurface create(Object...args){return new ", clazz, "(args);}}"));
		sb.append(");");

	}

}
