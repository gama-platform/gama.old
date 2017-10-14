package msi.gama.precompiler;

import static msi.gama.precompiler.java.JavaWriter.SEP;
import static msi.gama.precompiler.java.JavaWriter.TYPE_PREFIX;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;

public class TypeProcessor implements IProcessor<type> {

	@Override
	public void process(final ProcessorContext environment) {
		/**
		 * Format : prefix 0.name 1.id 2.varKind 3.class 4.[wrapped_class$]+
		 * 
		 * @param env
		 */

		// Map of the Class names -> GAML type names
		// Impossible to use as the processor does not keep its state between two files
		// final static Map<String, String> typeMapping = new HashMap<>();

		final List<? extends Element> types = environment.sortElements(type.class);
		for (final Element e : types) {
			final type t = e.getAnnotation(type.class);
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(TYPE_PREFIX);
			// name
			sb.append(t.name()).append(SEP);
			// id
			sb.append(t.id()).append(SEP);
			// kind
			sb.append(t.kind()).append(SEP);
			// class
			sb.append(environment.rawNameOf(e));
			// wraps
			List<? extends TypeMirror> wraps = Collections.EMPTY_LIST;
			// Trick to obtain the names of the classes...
			try {
				t.wraps();
			} catch (final MirroredTypesException ex) {
				try {
					wraps = ex.getTypeMirrors();
				} catch (final MirroredTypeException ex2) {
					wraps = Arrays.asList(ex2.getTypeMirror());
				}
			}
			for (final TypeMirror tm : wraps) {
				final String type = environment.rawNameOf(tm);
				sb.append(SEP).append(type);
				// typeMapping.put(type, t.name());
			}
			final doc[] docs = t.doc();
			doc doc;
			if (docs.length == 0) {
				doc = e.getAnnotation(doc.class);
			} else {
				doc = docs[0];
			}
			if (doc == null && !t.internal()) {
				environment.emitWarning("GAML: type '" + t.name() + "' is not documented", e);
			}

			environment.getProperties().put(sb.toString(), "");
		}
	}

}
