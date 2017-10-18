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
import javax.lang.model.element.TypeElement;

import msi.gama.precompiler.java.Constants;
import msi.gama.precompiler.java.JavaWriter;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaProcessor extends AbstractProcessor implements Constants {

	private ProcessorContext context;
	private final JavaWriter javaWriter = new JavaWriter();

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
		if (!context.processingOver()) {
			try {
				processors.values().forEach(p -> p.processXML(context));
			} catch (final Exception e) {
				context.emitWarning("An exception occured in the parsing of GAML annotations: " + e.getMessage(), null);
				throw e;
			}
			// context.storeProperties();
			generateJavaSource();
		}
		return true;
	}

	public void generateJavaSource() {
		try (Writer source = context.createSourceWriter()) {
			final StringBuilder sourceBuilder = new StringBuilder();
			javaWriter.write(context, sourceBuilder);
			source.append(sourceBuilder);
		} catch (final Exception e) {
			context.emitWarning("An exception occured in the generation of Java files: " + e.getMessage(), null);
		}
	}

}
