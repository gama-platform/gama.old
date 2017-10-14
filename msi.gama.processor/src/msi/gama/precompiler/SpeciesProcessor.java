package msi.gama.precompiler;

import static msi.gama.precompiler.java.JavaWriter.SEP;
import static msi.gama.precompiler.java.JavaWriter.SPECIES_PREFIX;

import java.util.List;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.species;

public class SpeciesProcessor implements IProcessor<species> {

	/**
	 * Format : prefix 0.name 1.class 2.[skill$]*
	 * 
	 * @param env
	 */
	@Override
	public void process(final ProcessorContext environment) {
		final List<? extends Element> species = environment.sortElements(species.class);
		for (final Element e : species) {
			final species spec = e.getAnnotation(species.class);
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(SPECIES_PREFIX);
			// name
			sb.append(spec.name()).append(SEP);
			// class
			sb.append(environment.rawNameOf(e));
			// skills
			for (final String s : spec.skills()) {
				sb.append(SEP).append(s);
			}

			final doc[] docs = spec.doc();
			doc doc;
			if (docs.length == 0) {
				doc = e.getAnnotation(doc.class);
			} else {
				doc = docs[0];
			}
			if (doc == null && !spec.internal()) {
				environment.emitWarning("GAML: species '" + spec.name() + "' is not documented", e);
			}

			environment.getProperties().put(sb.toString(), "");
		}
	}

}
