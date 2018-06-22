package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;

public class ConstantProcessor extends ElementProcessor<constant, Object> {

	@Override
	protected Class<constant> getAnnotationClass() {
		return constant.class;
	}

	@Override
	public void createJava(final ProcessorContext context, final StringBuilder sb, final Object op) {}

	@Override
	public Object createElement(final ProcessorContext context, final Element e, final constant constant) {
		verifyDoc(context, e, constant);
		return null;
	}

	private void verifyDoc(final ProcessorContext context, final Element e, final constant constant) {
		final doc documentation = constant.doc().length == 0 ? null : constant.doc()[0];
		if (documentation == null) {
			context.emitWarning("GAML: constant '" + constant.value() + "' is not documented", e);
		}
	}

}
