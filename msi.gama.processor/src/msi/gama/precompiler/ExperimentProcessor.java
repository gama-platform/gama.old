package msi.gama.precompiler;

import static msi.gama.precompiler.java.JavaWriter.EXPERIMENT_PREFIX;
import static msi.gama.precompiler.java.JavaWriter.SEP;

import java.util.List;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.experiment;

public class ExperimentProcessor implements IProcessor<experiment> {

	@Override
	public void process(final ProcessorContext environment) {
		final List<? extends Element> experiments = environment.sortElements(experiment.class);
		for (final Element e : experiments) {
			final experiment spec = e.getAnnotation(experiment.class);
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(EXPERIMENT_PREFIX);
			// name
			sb.append(spec.value()).append(SEP);
			// class
			sb.append(environment.rawNameOf(e)).append(SEP);
			// skills

			environment.getProperties().put(sb.toString(), "");
		}
	}

}
