package msi.gama.precompiler;

import static msi.gama.precompiler.java.JavaWriter.SEP;
import static msi.gama.precompiler.java.JavaWriter.SKILL_PREFIX;

import java.util.List;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;

public class SkillProcessor implements IProcessor<skill> {

	@Override
	public void process(final ProcessorContext environment) {
		/**
		 * Format : prefix 0.name 1.class 2.[species$]*
		 * 
		 * @param env
		 */
		final List<? extends Element> skills = environment.sortElements(skill.class);
		for (final Element e : skills) {
			final skill skill = e.getAnnotation(skill.class);
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(SKILL_PREFIX);
			// name
			sb.append(skill.name()).append(SEP);
			// class
			sb.append(environment.rawNameOf(e));
			// species
			for (final String s : skill.attach_to()) {
				sb.append(SEP).append(s);
			}
			final doc[] docs = skill.doc();
			doc doc;
			if (docs.length == 0) {
				doc = e.getAnnotation(doc.class);
			} else {
				doc = docs[0];
			}
			if (doc == null && !skill.internal()) {
				environment.emitWarning("GAML: skill '" + skill.name() + "' is not documented", e);
			}
			environment.getProperties().put(sb.toString(), "");
		}

	}

}
