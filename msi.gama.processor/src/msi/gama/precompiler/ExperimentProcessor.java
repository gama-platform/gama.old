package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.ExperimentProcessor.Exp;
import msi.gama.precompiler.GamlAnnotations.experiment;

public class ExperimentProcessor extends ElementProcessor<experiment, Exp> {

	public static class Exp {
		String name;
		String clazz;
	}

	@Override
	protected Class<experiment> getAnnotationClass() {
		return experiment.class;
	}

	@Override
	public void createJava(final ProcessorContext context, final StringBuilder sb, final Exp node) {
		sb.append(in).append("_experiment(").append(toJavaString(node.name)).append(',')
				.append(toClassObject(node.clazz)).append(", new IExperimentAgentCreator(){").append(OVERRIDE)
				.append("public IExperimentAgent create(IPopulation pop){return new ").append(node.clazz)
				.append("(pop);}}").append(");");
	}

	@Override
	public Exp createElement(final ProcessorContext context, final Element e, final experiment exp) {
		final Exp result = new Exp();
		result.name = exp.value();
		result.clazz = rawNameOf(context, e.asType());
		return result;
	}

}
