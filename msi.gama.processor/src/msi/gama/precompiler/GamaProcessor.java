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
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;

import msi.gama.precompiler.GamlAnnotations.tests;
import msi.gama.precompiler.tests.TestProcessor;

@SuppressWarnings ({ "unchecked", "rawtypes" })
@SupportedAnnotationTypes ({ "*" })
@SupportedSourceVersion (SourceVersion.RELEASE_8)
public class GamaProcessor extends AbstractProcessor implements Constants {

	private ProcessorContext context;
	int count;

	@Override
	public synchronized void init(final ProcessingEnvironment pe) {
		super.init(pe);

		context = new ProcessorContext(pe);
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
				writeJavaHeader(sourceBuilder);
				writeJavaBody(sourceBuilder);
				source.append(sourceBuilder);
			}
		} catch (final Exception e) {
			context.emitWarning("An exception occured in the generation of Java files: " + e.getMessage(), null);
		}
	}

	protected void writeJavaHeader(final StringBuilder sb) {
		sb.append("package ").append(PACKAGE_NAME).append(';');
		for (final String element : IMPORTS) {
			sb.append(ln).append("import ").append(element).append(".*;");
		}
		for (final String element : EXPLICIT_IMPORTS) {
			sb.append(ln).append("import ").append(element).append(";");
		}
		sb.append(ln).append("import static msi.gaml.operators.Cast.*;");
		sb.append(ln).append("import static msi.gaml.operators.Spatial.*;");
		sb.append(ln).append("import static msi.gama.common.interfaces.IKeyword.*;");
		sb.append(ln).append("	@SuppressWarnings({ \"rawtypes\", \"unchecked\" })");
		sb.append(ln).append(ln).append("public class GamlAdditions extends AbstractGamlAdditions").append(" {");
		sb.append(ln).append(tab);
		sb.append("public void initialize() throws SecurityException, NoSuchMethodException {");
		processors.values().forEach(p -> {
			final String method = p.getInitializationMethodName();
			if (method != null)
				sb.append(ln).append(tab).append(method).append("();");
		});

		sb.append(ln).append('}');
	}

	public String writeJavaBody(final StringBuilder sb) {

		processors.values().forEach(p -> {
			final String method = p.getInitializationMethodName();
			if (method != null) {
				sb.append("public void " + method + "() " + p.getExceptions() + " {");
				p.writeTo(context, sb);
				sb.append(ln);
				sb.append("}");
			}
		});

		sb.append(ln).append('}');
		return sb.toString();
	}

}
