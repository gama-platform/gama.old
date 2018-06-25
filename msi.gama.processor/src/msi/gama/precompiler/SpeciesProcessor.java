package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.species;

public class SpeciesProcessor extends ElementProcessor<species> {

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final species spec) {
		final String clazz = rawNameOf(context, e.asType());
		verifyDoc(context, e, spec);
		sb.append(in).append("_species(").append(toJavaString(spec.name())).append(",").append(toClassObject(clazz))
				.append(", new IAgentConstructor(){" + OVERRIDE + "public ").append(IAGENT).append(" createOneAgent(")
				.append(IPOPULATION).append(" p) {return new ").append(clazz).append("(p);}},");
		toArrayOfStrings(spec.skills(), sb).append(");");
	}

	private void verifyDoc(final ProcessorContext context, final Element e, final species spec) {
		final doc[] docs = spec.doc();
		final doc d = docs.length == 0 ? e.getAnnotation(doc.class) : docs[0];
		if (d == null && !spec.internal()) {
			context.emitWarning("GAML: species '" + spec.name() + "' is not documented", e);
		}
	}

	@Override
	protected Class<species> getAnnotationClass() {
		return species.class;
	}

}
