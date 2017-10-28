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

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;

import msi.gama.precompiler.GamlAnnotations.tests;
import msi.gama.precompiler.java.Constants;
import msi.gama.precompiler.java.JavaWriter;

@SuppressWarnings ({ "unchecked", "rawtypes" })
@SupportedAnnotationTypes ({ "*" })
public class GamaProcessor extends AbstractProcessor implements Constants {

	private ProcessorContext context;
	private final JavaWriter javaWriter = new JavaWriter();
	int count;

	@Override
	public synchronized void init(final ProcessingEnvironment pe) {
		super.init(pe);

		context = new ProcessorContext(pe);
		// context.emitWarning("Options= " + pe.getOptions(), null);
	}

	// @Override
	// public Set<String> getSupportedAnnotationTypes() {
	// return processors.keySet().stream().map(p -> p.getCanonicalName()).collect(Collectors.toSet());
	// }

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
		context.setRoundEnvironment(env);

		if (!context.processingOver()) {
			// context.emitWarning("Generating additions for plugin " + context.currentPlugin, null);
			// final Set<? extends Element> elements = env.getRootElements();
			// for (final Element e : elements) {
			// context.emitWarning("Processing " + e.getSimpleName().toString() + " in package "
			// + e.getEnclosingElement().getSimpleName().toString(), null);
			// }
			try {
				processors.values().forEach(p -> p.processXML(context));
			} catch (final Exception e) {
				context.emitWarning("An exception occured in the parsing of GAML annotations: " + e.getMessage(), null);
				throw e;
			}
		} else {
			final FileObject file = context.createSource();
			generateJavaSource(file);
			generateTests();
		}
		return true;
	}

	public void generateTests() {
		final TestProcessor tp = (TestProcessor) processors.get(tests.class);
		if (!tp.hasTests(context))
			return;
		context.createTestsFolder();
		try (Writer source = context.createTestWriter()) {
			final StringBuilder sourceBuilder = new StringBuilder();
			tp.writeTests(context, sourceBuilder);
			source.append(sourceBuilder.toString());
		} catch (final IOException e) {
			context.emitWarning("An exception occured in the generation of test files: " + e.getMessage(), null);
		}
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
