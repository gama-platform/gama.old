package msi.gama.precompiler;

import javax.lang.model.element.Element;

import org.w3c.dom.Document;

import msi.gama.precompiler.GamlAnnotations.test;

public class TestProcessor extends ElementProcessor<test> {

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final Document doc,
			final test action, final org.w3c.dom.Element node) {

	}

	@Override
	protected Class<test> getAnnotationClass() {
		return test.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {

	}
}
