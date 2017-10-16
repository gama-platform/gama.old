package msi.gama.precompiler;

import static msi.gama.precompiler.java.JavaWriter.CONSTANT_PREFIX;
import static msi.gama.precompiler.java.JavaWriter.SEP;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;

public class ConstantProcessor implements IProcessor<constant> {

	@Override
	public void process(final ProcessorContext environment) {
		for (final Element e : environment.sortElements(constant.class)) {
			final VariableElement ve = (VariableElement) e;
			final constant constant = ve.getAnnotation(constant.class);

			final doc documentation = constant.doc().length == 0 ? null : constant.doc()[0];

			if (documentation == null) {
				environment.emitWarning("GAML: constant '" + constant.value() + "' is not documented", e);
			}

			final String ret = environment.rawNameOf(ve.asType());
			final String constantName = constant.value();
			final Object valueConstant = ve.getConstantValue();

			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(CONSTANT_PREFIX);
			// 0.return class
			sb.append(ret).append(SEP);
			// 1.constant name
			sb.append(constantName).append(SEP);
			// 2+.alternative names
			for (final String s : constant.altNames()) {
				sb.append(s).append(SEP);
			}
			// 3.value
			sb.append(valueConstant);
			// 4.doc
			environment.getProperties().put(sb.toString(), "");
		}
	}

}
