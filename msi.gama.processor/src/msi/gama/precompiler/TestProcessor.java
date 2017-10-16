package msi.gama.precompiler;

import java.util.List;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.test;

public class TestProcessor implements IProcessor<test> {

	@Override
	public void process(final ProcessorContext environment) {
		final List<? extends Element> elements = environment.sortElements(test.class);
		for (final Element e : elements) {

		}

	}
}
