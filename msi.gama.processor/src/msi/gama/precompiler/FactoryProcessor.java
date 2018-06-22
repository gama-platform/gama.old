package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.FactoryProcessor.Fac;
import msi.gama.precompiler.GamlAnnotations.factory;

public class FactoryProcessor extends ElementProcessor<factory, Fac> {

	public static class Fac {
		String clazz;
		int[] kinds;
	}

	@Override
	protected Class<factory> getAnnotationClass() {
		return factory.class;
	}

	@Override
	public void createJava(final ProcessorContext context, final StringBuilder sb, final Fac op) {
		sb.append(in);
		sb.append("_factories(");
		sb.append("new ").append(op.clazz);
		sb.append("(").append("Arrays.asList(");
		for (final int i : op.kinds) {
			sb.append(i).append(",");
		}
		sb.setLength(sb.length() - 1);
		sb.append(")));");

	}

	@Override
	public Fac createElement(final ProcessorContext context, final Element e, final factory factory) {
		final Fac fac = new Fac();
		fac.clazz = rawNameOf(context, e.asType());
		fac.kinds = factory.handles();
		return fac;
	}

}
