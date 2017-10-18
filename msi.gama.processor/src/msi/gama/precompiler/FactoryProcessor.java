package msi.gama.precompiler;

import javax.lang.model.element.Element;

import org.w3c.dom.Document;

import msi.gama.precompiler.GamlAnnotations.factory;

public class FactoryProcessor extends ElementProcessor<factory> {

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final Document doc,
			final factory factory, final org.w3c.dom.Element node) {
		node.setAttribute("class", rawNameOf(context, e));
		node.setAttribute("kinds", arrayToString(factory.handles()));
	}

	@Override
	protected Class<factory> getAnnotationClass() {
		return factory.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {
		final String clazz = node.getAttribute("class");
		sb.append(in);
		sb.append("_factories(");
		sb.append("new ").append(clazz);
		sb.append("(").append("Arrays.asList(").append(node.getAttribute("kinds")).append(")));");
	}

}
