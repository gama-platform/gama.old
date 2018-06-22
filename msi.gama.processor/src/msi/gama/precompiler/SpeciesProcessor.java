package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.SpeciesProcessor.Sp;

public class SpeciesProcessor extends ElementProcessor<species, Sp> {

	public static class Sp {
		String name, clazz;
		String[] skills;
	}

	@Override
	public Sp createElement(final ProcessorContext context, final Element e, final species spec) {
		final Sp sp = new Sp();
		sp.name = spec.name();
		sp.clazz = rawNameOf(context, e.asType());
		sp.skills = spec.skills();
		verifyDoc(context, e, spec);
		return sp;
	}

	private void verifyDoc(final ProcessorContext context, final Element e, final species spec) {
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
	public void createJava(final ProcessorContext context, final StringBuilder sb, final Sp sp) {
		final String name = sp.name;
		final String clazz = sp.clazz;
		sb.append(in).append("_species(").append(toJavaString(name)).append(",").append(toClassObject(clazz))
				.append(", new IAgentConstructor(){" + OVERRIDE + "public ").append(IAGENT).append(" createOneAgent(")
				.append(IPOPULATION).append(" p) {return new ").append(clazz).append("(p);}}");
		sb.append(",");
		toArrayOfStrings(sp.skills, sb).append(");");

	}

}
