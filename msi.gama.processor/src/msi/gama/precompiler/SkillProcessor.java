/*******************************************************************************************************
 *
 * SkillProcessor.java, in msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import msi.gama.precompiler.GamlAnnotations.skill;

/**
 * The Class SkillProcessor.
 */
public class SkillProcessor extends ElementProcessor<skill> {

	@Override
	protected Class<skill> getAnnotationClass() {
		return skill.class;
	}

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final skill skill) {
		verifyDoc(context, e, "skill " + skill.name(), skill);
		sb.append(in).append("_skill(").append(toJavaString(skill.name())).append(',')
				.append(toClassObject(rawNameOf(context, e.asType()))).append(',');
		toArrayOfStrings(skill.attach_to(), sb).append(");");
	}

	@Override
	protected boolean validateElement(final ProcessorContext context, final Element e) {
		boolean result = assertClassExtends(context, true, (TypeElement) e, context.getISkill());
		return result;
	}

}
