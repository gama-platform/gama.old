package msi.gama.precompiler;

import static msi.gama.precompiler.java.JavaWriter.FACTORY_PREFIX;
import static msi.gama.precompiler.java.JavaWriter.SEP;

import java.util.List;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.factory;

public class FactoryProcessor implements IProcessor<factory> {

	/**
	 * Format : prefix 0.class 1.[handles,]* 2.[uses,]* Format : ]class$handles*$uses*
	 * 
	 * @param env
	 */
	@Override
	public void process(final ProcessorContext environment) {

		environment.write(factory.class, GamlProperties.FACTORIES);
		final List<? extends Element> factories = environment.sortElements(factory.class);

		for (final Element e : factories) {

			final factory factory = e.getAnnotation(factory.class);
			final int[] hKinds = factory.handles();
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(FACTORY_PREFIX);
			// class
			sb.append(environment.rawNameOf(e)).append(SEP);
			// handles
			sb.append(String.valueOf(hKinds[0]));
			for (int i = 1; i < hKinds.length; i++) {
				sb.append(',').append(String.valueOf(hKinds[i]));
			}
			environment.getProperties().put(sb.toString(), "");
		}

	}

}
