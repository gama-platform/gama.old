package msi.gama.precompiler;

import static msi.gama.precompiler.java.JavaWriter.DISPLAY_PREFIX;
import static msi.gama.precompiler.java.JavaWriter.SEP;

import java.util.List;

import javax.lang.model.element.Element;

import msi.gama.precompiler.GamlAnnotations.display;

public class DisplayProcessor implements IProcessor<display> {

	@Override
	public void process(final ProcessorContext environment) {
		final List<? extends Element> displays = environment.sortElements(display.class);
		for (final Element e : displays) {
			final display spec = e.getAnnotation(display.class);
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(DISPLAY_PREFIX);
			// name
			sb.append(spec.value()).append(SEP);
			// class
			sb.append(environment.rawNameOf(e)).append(SEP);
			// skills

			environment.getProperties().put(sb.toString(), "");
		}
	}

}
