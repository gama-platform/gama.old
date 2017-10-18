package msi.gama.precompiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import org.w3c.dom.Document;

import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;

public class ConstantProcessor extends ElementProcessor<constant> {

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final Document doc,
			final constant constant, final org.w3c.dom.Element node) {
		final doc documentation = constant.doc().length == 0 ? null : constant.doc()[0];
		if (documentation == null) {
			context.emitWarning("GAML: constant '" + constant.value() + "' is not documented", e);
		}
		final String ret = rawNameOf(context, e.asType());
		final String constantName = constant.value();
		final Object valueConstant = ((VariableElement) e).getConstantValue();
		node.setAttribute("returns", ret);
		node.setAttribute("name", constantName);
		node.setAttribute("value", String.valueOf(valueConstant));
		for (final String s : constant.altNames()) {
			final org.w3c.dom.Element alt = doc.createElement("alt");
			alt.setAttribute("name", s);
			appendChild(node, alt);
		}
	}

	@Override
	protected Class<constant> getAnnotationClass() {
		return constant.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {
		// Seems nothing is done.
	}

}
