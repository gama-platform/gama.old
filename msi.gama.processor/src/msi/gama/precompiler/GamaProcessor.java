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
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.tests;
import msi.gama.precompiler.doc.DocProcessor;
import msi.gama.precompiler.tests.ExamplesToTests;
import msi.gama.precompiler.tests.TestProcessor;

@SuppressWarnings ({ "unchecked", "rawtypes" })
@SupportedAnnotationTypes ({ "*" })
@SupportedSourceVersion (SourceVersion.RELEASE_8)
public class GamaProcessor extends AbstractProcessor implements Constants {

	private ProcessorContext context;
	public static final String JAVA_HEADER;
	int count;
	long begin = 0;
	long complete = 0;

	static {
		final StringBuilder sb = new StringBuilder();
		writeJavaHeader(sb);
		JAVA_HEADER = sb.toString();
	}

	@Override
	public synchronized void init(final ProcessingEnvironment pe) {
		super.init(pe);
		context = new ProcessorContext(pe);
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
		if (complete == 0) {
			complete = System.currentTimeMillis();
		}
		context.setRoundEnvironment(env);

		if (env.getRootElements().size() > 0) {
			try {
				begin = System.currentTimeMillis();
				processors.forEach((s, p) -> p.process(context));
			} catch (final Exception e) {
				context.emitWarning("An exception occured in the parsing of GAML annotations: ", e);
				throw e;
			} finally {
				// context.emit(Kind.NOTE, "GAML Processor: XML Trees produced", (Element) null);
			}
		}
		if (context.processingOver()) {
			final FileObject file = context.createSource();
			generateJavaSource(file);
			context.emit(Kind.NOTE, "GAML Processor: Java sources produced for " + context.currentPlugin + " in "
					+ (System.currentTimeMillis() - begin) + "ms", (Element) null);
			begin = System.currentTimeMillis();
			generateTests();
			context.emit(Kind.NOTE, "GAML Processor: GAMA tests produced for " + context.currentPlugin + " in "
					+ (System.currentTimeMillis() - begin) + "ms", (Element) null);
			context.emit(Kind.NOTE, "GAML Processor: Complete processing of " + context.currentPlugin + " in "
					+ (System.currentTimeMillis() - complete) + "ms", (Element) null);
			complete = 0;
		}
		return true;
	}

	public void generateTests() {
		final long begin = System.currentTimeMillis();
		final TestProcessor tp = (TestProcessor) processors.get(tests.class);
		if (tp.hasTests()) {
			try (Writer source = context.createTestWriter()) {
				final StringBuilder sourceBuilder = new StringBuilder();
				tp.writeTests(context, sourceBuilder);
				source.append(sourceBuilder.toString());
			} catch (final IOException e) {
				context.emitWarning("An exception occured in the generation of test files: ", e);
			}
		}
		// We pass the current document of the documentation processor to avoir re-reading it
		final DocProcessor dp = (DocProcessor) processors.get(doc.class);
		context.emit(Kind.NOTE,
				"    GAML Tests: lone tests serialization took " + (System.currentTimeMillis() - begin) + "ms",
				(Element) null);
		ExamplesToTests.createTests(context, dp.document);
	}

	public void generateJavaSource(final FileObject file) {
		try (Writer source = context.createSourceWriter(file)) {
			if (source != null) {
				source.append(writeJavaBody());
			}
		} catch (final IOException io) {
			context.emitWarning("An IO exception occured in the generation of Java files: ", io);
		} catch (final Exception e) {
			context.emitWarning("An exception occured in the generation of Java files: ", e);
			throw e;
		}
	}

	protected static void writeJavaHeader(final StringBuilder sb) {
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
			if (method != null) {
				sb.append(ln).append(tab).append(method).append("();");
			}
		});

		sb.append(ln).append('}');
	}

	public StringBuilder writeJavaBody() {
		final StringBuilder sb = new StringBuilder(JAVA_HEADER);
		processors.values().forEach(p -> {
			final String method = p.getInitializationMethodName();
			if (method != null) {
				sb.append("public void ").append(method).append("() ").append(p.getExceptions()).append(" {");
				p.serialize(context, sb);
				sb.append(ln);
				sb.append("}");
			}
		});

		sb.append(ln).append('}');
		return sb;
	}

}
