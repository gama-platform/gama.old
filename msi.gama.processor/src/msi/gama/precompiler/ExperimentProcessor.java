package msi.gama.precompiler;

import javax.lang.model.element.Element;

import org.w3c.dom.Document;

import msi.gama.precompiler.GamlAnnotations.experiment;

public class ExperimentProcessor extends ElementProcessor<experiment> {

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final Document doc,
			final experiment exp, final org.w3c.dom.Element node) {
		node.setAttribute("name", exp.value());
		node.setAttribute("class", rawNameOf(context, e));
	}

	@Override
	protected Class<experiment> getAnnotationClass() {
		return experiment.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {

		final String name = node.getAttribute("name");
		final String clazz = node.getAttribute("class");
		sb.append(concat(in, "_experiment(", toJavaString(name), ",", toClassObject(clazz),
				", new IExperimentAgentCreator(){", OVERRIDE,
				"public IExperimentAgent create(IPopulation pop){return new ", clazz, "(pop);}}"));
		sb.append(");");

	}

}
