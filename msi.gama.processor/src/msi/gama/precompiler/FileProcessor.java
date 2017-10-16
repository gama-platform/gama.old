package msi.gama.precompiler;

import static msi.gama.precompiler.java.JavaWriter.FILE_PREFIX;
import static msi.gama.precompiler.java.JavaWriter.SEP;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;

public class FileProcessor implements IProcessor<file> {

	@Override
	public void process(final ProcessorContext environment) {
		/**
		 * Format : 0.type 1.type 2. contentType 3.varName 4.class 5.[facetName, facetValue]+ 6.getter
		 * 7.initializer(true/false)? 8.setter
		 * 
		 * @param env
		 */

		for (final Element e : environment.sortElements(file.class)) {
			final file f = e.getAnnotation(file.class);
			final doc[] docs = f.doc();
			doc doc;
			if (docs.length == 0) {
				doc = e.getAnnotation(doc.class);
			} else {
				doc = docs[0];
			}
			if (doc == null) {
				environment.emitWarning("GAML: file declaration '" + f.name() + "' is not documented", e);
			}

			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(FILE_PREFIX);
			// name
			sb.append(f.name()).append(SEP);
			// class
			sb.append(environment.rawNameOf(e)).append(SEP);
			// buffer type
			sb.append(f.buffer_type()).append(SEP);
			// buffer contents type
			sb.append(f.buffer_content()).append(SEP);
			// buffer key type
			sb.append(f.buffer_index()).append(SEP);
			// suffixes
			final String[] names = f.extensions();
			sb.append(environment.arrayToString(names)).append(SEP);
			// constructors: only the arguments in addition to the scope are
			// provided
			for (final Element m : e.getEnclosedElements()) {
				if (m.getKind() == ElementKind.CONSTRUCTOR) {
					final ExecutableElement ex = (ExecutableElement) m;
					final List<? extends VariableElement> argParams = ex.getParameters();
					// The first parameter must be IScope
					final int n = argParams.size();
					if (n <= 1) {
						continue;
					}
					final String[] args = new String[n - 1];
					for (int i = 1; i < n; i++) {
						args[i - 1] = environment.rawNameOf(argParams.get(i));
					}
					sb.append(environment.arrayToString(args)).append(SEP);
				}
			}
			environment.getProperties().put(sb.toString(), "");
		}
	}

}
