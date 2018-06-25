package msi.gama.precompiler;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;

public class SkillProcessor extends ElementProcessor<skill> {

	@Override
	protected Class<skill> getAnnotationClass() {
		return skill.class;
	}

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final skill skill) {
		verifyDoc(context, e, skill);
		sb.append(in).append("_skill(").append(toJavaString(skill.name())).append(',')
				.append(toClassObject(rawNameOf(context, e.asType()))).append(',');
		toArrayOfStrings(skill.attach_to(), sb).append(");");
	}

	private void verifyDoc(final ProcessorContext context, final Element e, final skill skill) {
		final doc[] docs = skill.doc();
		final doc d = docs.length == 0 ? e.getAnnotation(doc.class) : docs[0];
		if (d == null && !skill.internal()) {
			context.emitWarning("GAML: skill '" + skill.name() + "' is not documented", e);
		}
	}

}
