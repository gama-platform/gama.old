/*********************************************************************************************
 *
 * 'GamaProcessor.java, in plugin msi.gama.processor, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.precompiler;

import java.io.Writer;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;

import msi.gama.precompiler.java.Constants;
import msi.gama.precompiler.java.JavaWriter;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaProcessor extends AbstractProcessor implements Constants {

	private ProcessorContext context;
	private final JavaWriter javaWriter = new JavaWriter();
	int count;

	@Override
	public synchronized void init(final ProcessingEnvironment pe) {
		super.init(pe);
		context = new ProcessorContext(pe);
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return processors.keySet().stream().map(p -> p.getCanonicalName()).collect(Collectors.toSet());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
		context.setRoundEnvironment(env);

		final FileObject file = context.createSource();
		if (!context.processingOver()) {
			context.emitWarning("Generating additions for plugin " + context.currentPlugin, null);
			final Set<? extends Element> elements = env.getRootElements();
			for (final Element e : elements) {
				context.emitWarning("Processing " + e.getSimpleName().toString() + " in package "
						+ e.getEnclosingElement().getSimpleName().toString(), null);
			}
			try {
				processors.values().forEach(p -> p.processXML(context));
			} catch (final Exception e) {
				context.emitWarning("An exception occured in the parsing of GAML annotations: " + e.getMessage(), null);
				throw e;
			}
		} else
			generateJavaSource(file);

		return true;
	}

	public void generateJavaSource(final FileObject file) {
		try (Writer source = context.createSourceWriter(file)) {
			if (source != null) {
				final StringBuilder sourceBuilder = new StringBuilder();
				javaWriter.write(context, sourceBuilder);
				source.append(sourceBuilder);
			}
			// else
			// context.emitWarning("Cannot create source file", null);
		} catch (final Exception e) {
			context.emitWarning("An exception occured in the generation of Java files: " + e.getMessage(), null);
		}
	}

}
