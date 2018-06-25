package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.experiment;

public class ExperimentProcessor extends ElementProcessor<experiment> {

	@Override
	protected Class<experiment> getAnnotationClass() {
		return experiment.class;
	}

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final experiment exp) {
		final String clazz = rawNameOf(context, e.asType());
		sb.append(in).append("_experiment(").append(toJavaString(exp.value())).append(',').append(toClassObject(clazz))
				.append(", new IExperimentAgentCreator(){").append(OVERRIDE)
				.append("public IExperimentAgent create(IPopulation pop){return new ").append(clazz).append("(pop);}}")
				.append(");");
	}

}
