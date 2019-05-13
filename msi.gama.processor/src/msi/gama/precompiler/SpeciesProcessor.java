package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.species;

public class SpeciesProcessor extends ElementProcessor<species> {

	@Override
	protected Class<species> getAnnotationClass() {
		return species.class;
	}

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final species spec) {
		final String clazz = rawNameOf(context, e.asType());
		verifyDoc(context, e, "species " + spec.name(), spec);
		sb.append(in).append("_species(").append(toJavaString(spec.name())).append(",").append(toClassObject(clazz))
				.append(",(p, i)->").append("new ").append(clazz).append("(p, i),");
		toArrayOfStrings(spec.skills(), sb).append(");");
	}

}
