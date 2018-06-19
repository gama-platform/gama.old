package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.species;

public class SpeciesProcessor extends ElementProcessor<species> {

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final species spec,
			final org.w3c.dom.Element node) {
		node.setAttribute("name", spec.name());
		node.setAttribute("class", rawNameOf(context, e));
		node.setAttribute("skills", arrayToString(spec.skills()));
		final doc[] docs = spec.doc();
		doc d;
		if (docs.length == 0) {
			d = e.getAnnotation(doc.class);
		} else {
			d = docs[0];
		}
		if (d == null && !spec.internal()) {
			context.emitWarning("GAML: species '" + spec.name() + "' is not documented", e);
		}
	}

	@Override
	protected Class<species> getAnnotationClass() {
		return species.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {

		final String name = node.getAttribute("name");
		final String clazz = node.getAttribute("class");
		sb.append(in).append("_species(").append(toJavaString(name)).append(",").append(toClassObject(clazz))
				.append(", new IAgentConstructor(){" + OVERRIDE + "public ").append(IAGENT).append(" createOneAgent(")
				.append(IPOPULATION).append(" p) {return new ").append(clazz).append("(p);}}");
		sb.append(",").append(toArrayOfStrings(node.getAttribute("skills")));
		sb.append(");");

	}

}
