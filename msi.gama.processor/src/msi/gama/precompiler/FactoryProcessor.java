package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.factory;

public class FactoryProcessor extends ElementProcessor<factory> {

	@Override
	protected Class<factory> getAnnotationClass() {
		return factory.class;
	}

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final factory factory) {
		sb.append("new ").append(rawNameOf(context, e.asType())).append("(I(");
		for (final int i : factory.handles()) {
			sb.append(i).append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(")),");
	}

	@Override
	public void serialize(final ProcessorContext context, final StringBuilder sb) {
		sb.append("_factories(");
		super.serialize(context, sb);
		sb.setLength(sb.length() - 1);
		sb.append(");");
	}
}
