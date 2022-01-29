/*******************************************************************************************************
 *
 * GamaProcessor.java, in msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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

import msi.gama.precompiler.GamlAnnotations.tests;
import msi.gama.precompiler.tests.TestProcessor;

/**
 * The Class GamaProcessor.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
@SupportedAnnotationTypes ({ "*" })
@SupportedSourceVersion (SourceVersion.RELEASE_11)
public class GamaProcessor extends AbstractProcessor implements Constants {

	/** The context. */
	private ProcessorContext context;

	/** The Constant JAVA_HEADER. */
	// public static final String JAVA_HEADER;

	/** The count. */
	int count;

	/** The begin. */
	long begin = 0;

	/** The complete. */
	long complete = 0;

	// static {
	// final StringBuilder sb = new StringBuilder();
	// writeImmutableHeader(sb);
	// JAVA_HEADER = sb.toString();
	// }

	@Override
	public synchronized void init(final ProcessingEnvironment pe) {
		super.init(pe);
		context = new ProcessorContext(pe);
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
		if (complete == 0) { complete = System.currentTimeMillis(); }
		context.setRoundEnvironment(env);

		if (context.getRoots().size() > 0) {
			try {
				begin = System.currentTimeMillis();
				processors.forEach((s, p) -> p.process(context));
			} catch (final Exception e) {
				context.emitWarning("An exception occured in the parsing of GAML annotations: ", e);
				throw e;
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

	/**
	 * Generate tests.
	 */
	public void generateTests() {
		final TestProcessor tp = (TestProcessor) processors.get(tests.class);
		if (tp.hasElements()) {
			try (Writer source = context.createTestWriter()) {
				tp.writeTests(context, source);
			} catch (final IOException e) {
				context.emitWarning("An exception occured in the generation of test files: ", e);
			}
		}
		// We pass the current document of the documentation processor to avoir re-reading it
		// final DocProcessor dp = (DocProcessor) processors.get(doc.class);
		// ExamplesToTests.createTests(context, dp.document);
	}

	/**
	 * Generate java source.
	 *
	 * @param file
	 *            the file
	 */
	public void generateJavaSource(final FileObject file) {
		try (Writer source = context.createSourceWriter(file)) {
			if (source != null) { source.append(writeJavaBody()); }
		} catch (final IOException io) {
			context.emitWarning("An IO exception occured in the generation of Java files: ", io);
		} catch (final Exception e) {
			context.emitWarning("An exception occured in the generation of Java files: ", e);
			throw e;
		}
	}

	/**
	 * Write immutable header.
	 *
	 * @param sb
	 *            the sb
	 */
	protected void writeImmutableHeader(final StringBuilder sb) {
		for (final String element : context.imports) { sb.append(ln).append("import ").append(element).append("*;"); }
		for (final String element : EXPLICIT_IMPORTS) { sb.append(ln).append("import ").append(element).append(";"); }
		sb.append(ln).append("import static msi.gaml.operators.Cast.*;");
		sb.append(ln).append("import static msi.gaml.operators.Spatial.*;");
		sb.append(ln).append("import static msi.gama.common.interfaces.IKeyword.*;");
		sb.append(ln).append("@SuppressWarnings({ \"rawtypes\", \"unchecked\", \"unused\" })");
		sb.append(ln).append(ln).append("public class GamlAdditions extends msi.gaml.compilation.AbstractGamlAdditions")
				.append(" {");
		sb.append(ln).append(tab);
		sb.append("public void initialize() throws SecurityException, NoSuchMethodException {");
	}

	/**
	 * Write mutable header.
	 *
	 * @param sb
	 *            the sb
	 */
	protected void writeMutableHeader(final StringBuilder sb) {
		processors.values().forEach(p -> {
			if (p.outputToJava() && p.hasElements()) {
				final String method = p.getInitializationMethodName();
				if (method != null) { sb.append(ln).append(tab).append(method).append("();"); }
			}
		});

		sb.append(ln).append('}');
	}

	/**
	 * Write java body.
	 *
	 * @return the string builder
	 */
	public StringBuilder writeJavaBody() {
		final StringBuilder sb = new StringBuilder();
		sb.append("package ").append(PACKAGE_NAME).append(".").append(context.shortcut).append(';');
		sb.append(ln);
		writeImmutableHeader(sb);
		writeMutableHeader(sb);
		processors.values()
				.forEach(p -> { if (p.outputToJava() && p.hasElements()) { p.writeJavaBody(sb, context); } });

		sb.append(ln).append('}');
		return sb;
	}

}
