/*******************************************************************************************************
 *
 * SpeciesProcessor.java, in msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import msi.gama.precompiler.GamlAnnotations.species;

/**
 * The Class SpeciesProcessor.
 */
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

	@Override
	protected boolean validateElement(final ProcessorContext context, final Element e) {
		boolean result =
				assertClassExtends(context, true, (TypeElement) e, context.getType("msi.gama.metamodel.agent.IAgent"));
		return result;
	}

}
