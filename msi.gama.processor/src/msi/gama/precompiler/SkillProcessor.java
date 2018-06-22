package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.SkillProcessor.Sk;

public class SkillProcessor extends ElementProcessor<skill, Sk> {

	public static class Sk {
		String name, clazz;
		String[] attach;
	}

	@Override
	protected Class<skill> getAnnotationClass() {
		return skill.class;
	}

	@Override
	public void createJava(final ProcessorContext context, final StringBuilder sb, final Sk node) {
		sb.append(in).append("_skill(").append(toJavaString(node.name)).append(',').append(toClassObject(node.clazz))
				.append(',');
		(toArrayOfStrings(node.attach, sb)).append(");");
	}

	@Override
	public Sk createElement(final ProcessorContext context, final Element e, final skill skill) {
		final Sk sk = new Sk();
		sk.name = skill.name();
		sk.clazz = rawNameOf(context, e.asType());
		sk.attach = skill.attach_to();
		verifyDoc(context, e, skill);
		return sk;
	}

	private void verifyDoc(final ProcessorContext context, final Element e, final skill skill) {
		final doc[] docs = skill.doc();
		final doc d = docs.length == 0 ? e.getAnnotation(doc.class) : docs[0];
		if (d == null && !skill.internal()) {
			context.emitWarning("GAML: skill '" + skill.name() + "' is not documented", e);
		}
	}

}
