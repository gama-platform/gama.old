package msi.gama.precompiler;

import javax.lang.model.element.Element;

import org.w3c.dom.Document;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;

public class SkillProcessor extends ElementProcessor<skill> {

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final Document doc,
			final skill skill, final org.w3c.dom.Element node) {
		node.setAttribute("name", skill.name());
		node.setAttribute("class", rawNameOf(context, e));
		node.setAttribute("attach", arrayToString(skill.attach_to()));
		final doc[] docs = skill.doc();
		doc d;
		if (docs.length == 0) {
			d = e.getAnnotation(doc.class);
		} else {
			d = docs[0];
		}
		if (d == null && !skill.internal()) {
			context.emitWarning("GAML: skill '" + skill.name() + "' is not documented", e);
		}

	}

	@Override
	protected Class<skill> getAnnotationClass() {
		return skill.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {
		final String name = node.getAttribute("name");
		final String clazz = node.getAttribute("class");
		sb.append(concat(in, "_skill(", toJavaString(name), ",", toClassObject(clazz)));
		sb.append(",").append(toArrayOfStrings(node.getAttribute("attach")));
		sb.append(");");

	}

}
