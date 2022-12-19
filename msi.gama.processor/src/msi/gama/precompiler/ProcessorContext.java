/*******************************************************************************************************
 *
 * ProcessorContext.java, in msi.gama.processor, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.precompiler;

import static java.util.Collections.sort;

// import static gama.processor.annotations.GamlProperties.GAML;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * The Class ProcessorContext.
 */
public class ProcessorContext implements ProcessingEnvironment, RoundEnvironment, Constants {

	/** The Constant PRODUCES_DOC. */
	private final static boolean PRODUCES_DOC = true;

	/** The Constant CHARSET. */
	public static final Charset CHARSET = Charset.forName("UTF-8");

	/** The Constant ADDITIONS_PACKAGE_BASE. */
	public static final String ADDITIONS_PACKAGE_BASE = "gaml.additions";

	/** The Constant ADDITIONS_CLASS_NAME. */
	public static final String ADDITIONS_CLASS_NAME = "GamlAdditions";

	/** The Constant PRODUCES_WARNING. */
	private final static boolean PRODUCES_WARNING = true;

	/** The Constant OUT. */
	public static final StandardLocation OUT = StandardLocation.SOURCE_OUTPUT;

	/** The delegate. */
	private final ProcessingEnvironment delegate;

	/** The round. */
	private RoundEnvironment round;

	/** The current plugin. */
	public volatile String currentPlugin;

	/** The shortcut. */
	public volatile String shortcut;

	/** The roots. */
	public List<String> roots;

	/** The Constant xmlBuilder. */
	public static final DocumentBuilder xmlBuilder;

	/** The imports. */
	public final Set<String> imports = Stream
			.of("msi.gaml.extensions.multi_criteria", "msi.gama.outputs.layers.charts", "msi.gama.outputs.layers",
					"msi.gama.outputs", "msi.gama.kernel.batch", "msi.gama.kernel.root",
					"msi.gaml.architecture.weighted_tasks", "msi.gaml.architecture.user",
					"msi.gaml.architecture.reflex", "msi.gaml.architecture.finite_state_machine", "msi.gaml.species",
					"msi.gama.metamodel.shape", "msi.gaml.expressions", "msi.gama.metamodel.topology",
					"msi.gaml.statements.test", "msi.gama.metamodel.population", "msi.gama.kernel.simulation",
					"msi.gama.kernel.model", "java.util", "msi.gaml.statements.draw", " msi.gama.metamodel.shape",
					"msi.gama.common.interfaces", "msi.gama.runtime", "java.lang", "msi.gama.metamodel.agent",
					"msi.gaml.types", "msi.gaml.compilation", "msi.gaml.factories", "msi.gaml.descriptions",
					"msi.gama.util.tree", "msi.gama.util.file", "msi.gama.util.matrix", "msi.gama.util.graph",
					"msi.gama.util.path", "msi.gama.util", "msi.gama.runtime.exceptions", "msi.gaml.statements",
					"msi.gaml.skills", "msi.gaml.variables", "msi.gama.kernel.experiment", "msi.gaml.operators",
					"msi.gama.common.interfaces", "msi.gama.extensions.messaging", "msi.gama.metamodel.population")
			.map(s -> s + ".").collect(Collectors.toSet());

	static {
		DocumentBuilder temp = null;
		try {
			temp = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (final ParserConfigurationException e) {}
		xmlBuilder = temp;
	}

	/**
	 * Instantiates a new processor context.
	 *
	 * @param pe
	 *            the pe
	 */
	public ProcessorContext(final ProcessingEnvironment pe) {
		delegate = pe;
	}

	/**
	 * Gets the builder.
	 *
	 * @return the builder
	 */
	public DocumentBuilder getBuilder() { return xmlBuilder; }

	/**
	 * Name of.
	 *
	 * @param e
	 *            the e
	 * @return the string
	 */
	public String nameOf(final TypeElement e) {
		if (e.getNestingKind() == NestingKind.TOP_LEVEL) return e.getQualifiedName().toString();
		return nameOf((TypeElement) e.getEnclosingElement()) + "." + e.getSimpleName().toString();
	}

	/**
	 * Introduced to handle issue #1671
	 *
	 * @param env
	 * @param annotationClass
	 * @return
	 */
	public List<Element> sortElements(final Class<? extends Annotation> annotationClass) {
		final Set<? extends Element> elements = getElementsAnnotatedWith(annotationClass);
		final List<Element> result = new ArrayList<>(elements);
		sort(result, Comparator.comparing(Element::toString));
		return result;
	}

	/**
	 * Group elements.
	 *
	 * @param annotationClass
	 *            the annotation class
	 * @return the map
	 */
	public final Map<String, List<Element>> groupElements(final Class<? extends Annotation> annotationClass) {

		// result.forEach((s, l) -> sort(l, (o1, o2) -> o1.toString().compareTo(o2.toString())));
		return getElementsAnnotatedWith(annotationClass).stream().collect(Collectors.groupingBy(this::getRootClassOf));
	}

	/**
	 * Gets the root class of.
	 *
	 * @param e
	 *            the e
	 * @return the root class of
	 */
	private String getRootClassOf(final Element e) {
		final ElementKind kind = e.getKind();
		final Element enclosing = e.getEnclosingElement();
		final ElementKind enclosingKind = enclosing.getKind();
		if ((kind == ElementKind.CLASS || kind == ElementKind.INTERFACE) && enclosingKind != ElementKind.CLASS
				&& enclosingKind != ElementKind.INTERFACE)
			return e.toString();
		return getRootClassOf(enclosing);
	}

	/**
	 * Gets the i skill.
	 *
	 * @return the i skill
	 */
	public TypeMirror getISkill() {
		return getType("msi.gama.common.interfaces.ISkill");
		// if (iSkill == null) { iSkill = getType("msi.gama.common.interfaces.ISkill"); }
		// return iSkill;
	}

	/**
	 * Gets the i scope.
	 *
	 * @return the i scope
	 */
	public TypeMirror getIScope() {
		return getType("msi.gama.runtime.IScope");
		// if (iScope == null) { iScope = getType("msi.gama.runtime.IScope"); }
		// return iScope;
	}

	/**
	 * Gets the string.
	 *
	 * @return the string
	 */
	public TypeMirror getString() {
		return getType("java.lang.String");
		// if (string == null) { string = getType("java.lang.String"); }
		// return string;
	}

	/**
	 * Gets the i expression.
	 *
	 * @return the i expression
	 */
	public TypeMirror getIExpression() {
		return getType("msi.gaml.expressions.IExpression");
		// if (iExpression == null) { iExpression = getType("msi.gaml.expressions.IExpression"); }
		// return iExpression;
	}

	/**
	 * Gets the string.
	 *
	 * @return the string
	 */
	public TypeMirror getIType() {
		return getType("msi.gaml.types.IType");
		// if (iType == null) { iType = getType("msi.gaml.types.IType"); }
		// return iType;
	}

	/**
	 * Gets the type.
	 *
	 * @param qualifiedName
	 *            the qualified name
	 * @return the type
	 */
	public TypeMirror getType(final String qualifiedName) {
		TypeElement e = delegate.getElementUtils().getTypeElement(qualifiedName);
		if (e == null) return null;
		return e.asType();
	}

	/**
	 * Gets the i var and action support.
	 *
	 * @return the i var and action support
	 */
	public TypeMirror getIVarAndActionSupport() {
		return getType("msi.gama.common.interfaces.IVarAndActionSupport");
		// if (iVarAndActionSupport == null) {
		// iVarAndActionSupport = getType("msi.gama.common.interfaces.IVarAndActionSupport");
		// }
		// return iVarAndActionSupport;
	}

	/**
	 * Gets the i agent.
	 *
	 * @return the i agent
	 */
	TypeMirror getIAgent() {
		return getType("msi.gama.metamodel.agent.IAgent");
		// if (iAgent == null) { iAgent = getType("msi.gama.metamodel.agent.IAgent"); }
		// return iAgent;
	}

	@Override
	public Map<String, String> getOptions() { return delegate.getOptions(); }

	@Override
	public Messager getMessager() { return delegate.getMessager(); }

	@Override
	public Filer getFiler() { return delegate.getFiler(); }

	@Override
	public Elements getElementUtils() { return delegate.getElementUtils(); }

	@Override
	public Types getTypeUtils() { return delegate.getTypeUtils(); }

	@Override
	public SourceVersion getSourceVersion() { return delegate.getSourceVersion(); }

	@Override
	public Locale getLocale() { return delegate.getLocale(); }

	/**
	 * Emit warning.
	 *
	 * @param s
	 *            the s
	 */
	public void emitWarning(final String s) {
		emitWarning(s, (Element) null);
	}

	/**
	 * Emit error.
	 *
	 * @param s
	 *            the s
	 */
	public void emitError(final String s) {
		emitError(s, (Element) null);
	}

	/**
	 * Emit warning.
	 *
	 * @param s
	 *            the s
	 * @param e
	 *            the e
	 */
	public void emitWarning(final String s, final Element e) {
		emit(Kind.WARNING, s, e);
	}

	/**
	 * Emit error.
	 *
	 * @param s
	 *            the s
	 * @param e
	 *            the e
	 */
	public void emitError(final String s, final Element e) {
		emit(Kind.ERROR, s, e);
	}

	/**
	 * Emit.
	 *
	 * @param kind
	 *            the kind
	 * @param s
	 *            the s
	 * @param e
	 *            the e
	 */
	public void emit(final Kind kind, final String s, final Element e) {
		if (!PRODUCES_WARNING) return;
		if (e == null) {
			getMessager().printMessage(kind, "GAML: " + s);
		} else {
			getMessager().printMessage(kind, "GAML: " + s, e);
		}
	}

	/**
	 * Emit error.
	 *
	 * @param s
	 *            the s
	 * @param e1
	 *            the e 1
	 */
	public void emitError(final String s, final Exception e1) {
		emit(Kind.ERROR, s, e1, null);
	}

	/**
	 * Emit warning.
	 *
	 * @param s
	 *            the s
	 * @param e1
	 *            the e 1
	 */
	public void emitWarning(final String s, final Exception e1) {
		emit(Kind.WARNING, s, e1, null);
	}

	/**
	 * Emit error.
	 *
	 * @param s
	 *            the s
	 * @param e1
	 *            the e 1
	 * @param element
	 *            the element
	 */
	public void emitError(final String s, final Exception e1, final Element element) {
		emit(Kind.ERROR, s, e1, element);
	}

	/**
	 * Emit warning.
	 *
	 * @param s
	 *            the s
	 * @param e1
	 *            the e 1
	 * @param element
	 *            the element
	 */
	public void emitWarning(final String s, final Exception e1, final Element element) {
		emit(Kind.WARNING, s, e1, element);
	}

	/**
	 * Emit.
	 *
	 * @param kind
	 *            the kind
	 * @param s
	 *            the s
	 * @param e1
	 *            the e 1
	 * @param element
	 *            the element
	 */
	public void emit(final Kind kind, final String s, final Exception e1, final Element element) {
		final StringBuilder sb = new StringBuilder();
		sb.append(s);
		sb.append(e1.getMessage());
		for (final StackTraceElement t : e1.getStackTrace()) {
			sb.append("\n");
			sb.append(t.toString());
		}
		emit(kind, sb.toString(), element);
	}

	/**
	 * Sets the round environment.
	 *
	 * @param env
	 *            the new round environment
	 */
	public void setRoundEnvironment(final RoundEnvironment env) {
		round = env;
		roots = round.getRootElements().stream().map(Element::toString).toList();
	}

	@Override
	public boolean processingOver() {
		return round.processingOver();
	}

	@Override
	public boolean errorRaised() {
		return round.errorRaised();
	}

	@Override
	public Set<? extends Element> getRootElements() { return round.getRootElements(); }

	@Override
	public Set<? extends Element> getElementsAnnotatedWith(final TypeElement a) {
		return round.getElementsAnnotatedWith(a);
	}

	@Override
	public Set<? extends Element> getElementsAnnotatedWith(final Class<? extends Annotation> a) {
		return round.getElementsAnnotatedWith(a);
	}

	/**
	 * Creates the writer.
	 *
	 * @param s
	 *            the s
	 * @return the writer
	 */
	public Writer createWriter(final String s) {
		try {
			final OutputStream output = getFiler().createResource(OUT, "", s, (Element[]) null).openOutputStream();
			return new OutputStreamWriter(output, CHARSET);
		} catch (final Exception e) {
			emitWarning("", e);
		}
		return null;
	}

	/**
	 * Inits the current plugin.
	 */
	void initCurrentPlugin() {
		try {
			final FileObject temp = getFiler().createSourceFile("gaml.additions.package-info", (Element[]) null);
			emit(Kind.NOTE, "GAML Processor: creating " + temp.toUri(), (Element) null);
			final String plugin2 = temp.toUri().toASCIIString().replace("/target/gaml/additions/package-info.java", "")
					.replace("/gaml/gaml/additions/package-info.java", "");
			currentPlugin = plugin2.substring(plugin2.lastIndexOf('/') + 1);
			shortcut = currentPlugin.substring(currentPlugin.lastIndexOf('.') + 1);
		} catch (IOException e) {
			emitWarning("Exception raised while reading the current plugin name " + e.getMessage(), e);
		}
	}

	/**
	 * Creates the source.
	 *
	 * @return the file object
	 */
	public FileObject createSource() {
		initCurrentPlugin();
		try {

			return getFiler().createSourceFile(ADDITIONS_PACKAGE_BASE + "." + shortcut + "." + ADDITIONS_CLASS_NAME,
					(Element[]) null);
		} catch (final Exception e) {
			emitWarning("Exception raised while creating the source file: " + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Creates the test writer.
	 *
	 * @return the writer
	 */
	public Writer createTestWriter() {
		return createTestWriter(getTestFileName());
	}

	/**
	 * Creates the test writer.
	 *
	 * @param fileName
	 *            the file name
	 * @return the writer
	 */
	public Writer createTestWriter(final String fileName) {
		createTestsFolder();
		try {
			final OutputStream output =
					getFiler().createResource(OUT, getTestFolderName() + ".models", fileName, (Element[]) null)
							.openOutputStream();
			return new OutputStreamWriter(output, CHARSET);
		} catch (final Exception e) {
			e.printStackTrace();
			emitWarning("Impossible to create test file " + fileName + ": ", e);
		}
		return null;
	}

	/**
	 * Gets the test file name.
	 *
	 * @return the test file name
	 */
	private String getTestFileName() {
		final String title = currentPlugin.substring(currentPlugin.lastIndexOf('.') + 1);
		return Constants.capitalizeFirstLetter(title) + " Tests.experiment";
	}

	/**
	 * Gets the test folder name.
	 *
	 * @return the test folder name
	 */
	private String getTestFolderName() {
		final String title = currentPlugin.substring(currentPlugin.lastIndexOf('.') + 1);
		return "tests.Generated From " + Constants.capitalizeFirstLetter(title);
	}

	/**
	 * Creates the tests folder.
	 */
	public void createTestsFolder() {
		FileObject obj = null;
		try {
			obj = getFiler().createResource(OUT, getTestFolderName(), ".project", (Element[]) null);
		} catch (final FilerException e) {
			// Already exists. Simply return
			return;
		} catch (final IOException e) {
			// More serious problem
			emitWarning("Cannot create tests folder: ", e);
			return;
		}
		try (final OutputStream output = obj.openOutputStream();
				final Writer writer = new OutputStreamWriter(output, CHARSET);) {
			writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<projectDescription>\n"
					+ "	<name>Generated tests in " + currentPlugin + "</name>\n" + "	<comment>" + currentPlugin
					+ "</comment>\n" + "	<projects>\n" + "	</projects>\n" + "	<buildSpec>\n"
					+ "		<buildCommand>\n" + "			<name>org.eclipse.xtext.ui.shared.xtextBuilder</name>\n"
					+ "			<arguments>\n" + "			</arguments>\n" + "		</buildCommand>\n"
					+ "	</buildSpec>\n" + "	<natures>\n"
					+ "		<nature>org.eclipse.xtext.ui.shared.xtextNature</nature>\n"
					+ "		<nature>msi.gama.application.gamaNature</nature>\n"
					+ "		<nature>msi.gama.application.testNature</nature>\n" + "	</natures>\n"
					+ "</projectDescription>\n" + "");
		} catch (final IOException t) {
			emitWarning("", t);
		}
	}

	/**
	 * Creates the source writer.
	 *
	 * @param file
	 *            the file
	 * @return the writer
	 */
	public Writer createSourceWriter(final FileObject file) {
		try {
			final OutputStream output = file.openOutputStream();
			return new OutputStreamWriter(output, CHARSET);
		} catch (final Exception e) {
			emitWarning("Error in creating source writer", e);
		}
		return null;
	}

	/**
	 * Should produce doc.
	 *
	 * @return true, if successful
	 */
	public boolean shouldProduceDoc() {
		return "true".equals(getOptions().get("doc")) || PRODUCES_DOC;
	}

	/**
	 * Gets the input stream.
	 *
	 * @param string
	 *            the string
	 * @return the input stream
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public InputStream getInputStream(final String string) throws IOException {
		return getFiler().getResource(ProcessorContext.OUT, "", string).openInputStream();
	}

	/**
	 * Gets the useful annotations on.
	 *
	 * @param e
	 *            the e
	 * @return the useful annotations on
	 */
	public List<Annotation> getUsefulAnnotationsOn(final Element e) {
		final List<Annotation> result = new ArrayList<>();
		for (final Class<? extends Annotation> clazz : processors.keySet()) {
			final Annotation a = e.getAnnotation(clazz);
			if (a != null) { result.add(a); }
		}
		return result;
	}

	/**
	 * Gets the roots.
	 *
	 * @return the roots
	 */
	public List<String> getRoots() { return roots; }

	/**
	 * Contains import.
	 *
	 * @param path
	 *            the path
	 * @return true, if successful
	 */
	public boolean containsImport(final String path) {
		return imports.contains(path);
	}

	/**
	 * Checks if is i type.
	 *
	 * @param type
	 *            the type
	 * @return true, if is i type
	 */
	public boolean isIType(final TypeMirror type) {
		return delegate.getTypeUtils().isSubtype(type, getIType());
	}

}