/*******************************************************************************************************
 *
 * ExperimentProcessor.java, in msi.gama.processor, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.precompiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import msi.gama.precompiler.GamlAnnotations.experiment;

/**
 * The Class ExperimentProcessor.
 */
public class ExperimentProcessor extends ElementProcessor<experiment> {

	@Override
	protected Class<experiment> getAnnotationClass() { return experiment.class; }

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final experiment exp) {
		verifyDoc(context, e, "experiment " + exp.value(), exp);
		String clazz = e.asType().toString();
		sb.append(in).append("_experiment(").append(toJavaString(exp.value())).append(",(p, i)->new ")
				.append(rawNameOf(context, e.asType())).append("(p, i),").append(clazz).append(".class);");
	}

	@Override
	protected boolean validateElement(final ProcessorContext context, final Element e) {
		boolean result = assertClassExtends(context, true, (TypeElement) e,
				context.getType("msi.gama.kernel.experiment.IExperimentAgent"));
		return result;
	}
}
