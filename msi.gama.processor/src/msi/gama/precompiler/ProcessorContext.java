package msi.gama.precompiler;

import static java.util.Collections.sort;

// import static msi.gama.precompiler.GamlProperties.GAML;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

public class ProcessorContext implements ProcessingEnvironment, RoundEnvironment, Constants {
	private final static boolean PRODUCES_DOC = true;
	public static final Charset CHARSET = Charset.forName("UTF-8");
	public static final String ADDITIONS = "gaml.additions.GamlAdditions";
	private final static boolean PRODUCES_WARNING = true;
	public static final StandardLocation OUT = StandardLocation.SOURCE_OUTPUT;
	private final ProcessingEnvironment delegate;
	private RoundEnvironment round;
	private TypeMirror iSkill, iAgent;
	public volatile String currentPlugin;
	public List<String> roots;

	ProcessorContext(final ProcessingEnvironment pe) {
		delegate = pe;
	}

	public String nameOf(final TypeElement e) {
		if (e.getNestingKind() == NestingKind.TOP_LEVEL) { return e.getQualifiedName().toString(); }
		return nameOf((TypeElement) e.getEnclosingElement()) + "." + e.getSimpleName().toString();
	}

	/**
	 * Introduced to handle issue #1671
	 * 
	 * @param env
	 * @param annotationClass
	 * @return
	 */
	public List<? extends Element> sortElements(final Class<? extends Annotation> annotationClass) {
		final Set<? extends Element> elements = getElementsAnnotatedWith(annotationClass);
		final List<? extends Element> result = new ArrayList<>(elements);
		sort(result, (o1, o2) -> o1.toString().compareTo(o2.toString()));
		return result;
	}

	public final Map<String, List<Element>> groupElements(final Class<? extends Annotation> annotationClass) {
		final Map<String, List<Element>> result = getElementsAnnotatedWith(annotationClass).stream()
				.collect(Collectors.groupingBy((k) -> getRootClassOf(k)));
		// result.forEach((s, l) -> sort(l, (o1, o2) -> o1.toString().compareTo(o2.toString())));
		return result;
	}

	private String getRootClassOf(final Element e) {
		final ElementKind kind = e.getKind();
		final Element enclosing = e.getEnclosingElement();
		final ElementKind enclosingKind = enclosing.getKind();
		if ((kind == ElementKind.CLASS || kind == ElementKind.INTERFACE)
				&& !(enclosingKind == ElementKind.CLASS || enclosingKind == ElementKind.INTERFACE)) { return e
						.toString(); }
		return getRootClassOf(enclosing);
	}

	public void verifyClassTypeCompatibility(final String string, final Element ve) {
		String warning = null;
		switch (string) {
			case "Map":
				warning = "it is safer to use the GamaMap type";
				break;
			case "ArrayList":
			case "List":
				warning = "it is safer to use the IList type";
				break;
			case "short":
			case "long":
			case "Long":
			case "Short":
				warning = "it is safer to use the Integer type";
				break;
			case "Color":
				warning = "it is safer to use the GamaColor type";
				break;
		}
		if (warning != null) {
			emitWarning("GAML: " + warning, ve);
		}

	}

	public TypeMirror getISkill() {
		if (iSkill == null) {
			iSkill = delegate.getElementUtils().getTypeElement("msi.gama.common.interfaces.ISkill").asType();
		}
		return iSkill;
	}

	TypeMirror getIAgent() {
		if (iAgent == null) {
			iAgent = delegate.getElementUtils().getTypeElement("msi.gama.metamodel.agent.IAgent").asType();
		}
		return iAgent;
	}

	@Override
	public Map<String, String> getOptions() {
		return delegate.getOptions();
	}

	@Override
	public Messager getMessager() {
		return delegate.getMessager();
	}

	@Override
	public Filer getFiler() {
		return delegate.getFiler();
	}

	@Override
	public Elements getElementUtils() {
		return delegate.getElementUtils();
	}

	@Override
	public Types getTypeUtils() {
		return delegate.getTypeUtils();
	}

	@Override
	public SourceVersion getSourceVersion() {
		return delegate.getSourceVersion();
	}

	@Override
	public Locale getLocale() {
		return delegate.getLocale();
	}

	public void emitWarning(final String s) {
		emitWarning(s, (Element) null);
	}

	public void emitError(final String s) {
		emitError(s, (Element) null);
	}

	public void emitWarning(final String s, final Element e) {
		emit(Kind.WARNING, s, e);
	}

	public void emitError(final String s, final Element e) {
		emit(Kind.ERROR, s, e);
	}

	public void emit(final Kind kind, final String s, final Element e) {
		if (!PRODUCES_WARNING) { return; }
		if (e == null) {
			getMessager().printMessage(kind, s);
		} else {
			getMessager().printMessage(kind, s, e);
		}
	}

	public void emitError(final String s, final Exception e1) {
		emit(Kind.ERROR, s, e1, null);
	}

	public void emitWarning(final String s, final Exception e1) {
		emit(Kind.WARNING, s, e1, null);
	}

	public void emitError(final String s, final Exception e1, final Element element) {
		emit(Kind.ERROR, s, e1, element);
	}

	public void emitWarning(final String s, final Exception e1, final Element element) {
		emit(Kind.WARNING, s, e1, element);
	}

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

	public void setRoundEnvironment(final RoundEnvironment env) {
		round = env;
		roots = round.getRootElements().stream().map(e -> e.toString()).collect(Collectors.toList());
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
	public Set<? extends Element> getRootElements() {
		return round.getRootElements();
	}

	@Override
	public Set<? extends Element> getElementsAnnotatedWith(final TypeElement a) {
		return round.getElementsAnnotatedWith(a);
	}

	@Override
	public Set<? extends Element> getElementsAnnotatedWith(final Class<? extends Annotation> a) {
		return round.getElementsAnnotatedWith(a);
	}

	public Writer createWriter(final String s) {
		try {
			final OutputStream output = getFiler().createResource(OUT, "", s, (Element[]) null).openOutputStream();
			final Writer writer = new OutputStreamWriter(output, CHARSET);
			return writer;
		} catch (final Exception e) {
			emitWarning("", e);
		}
		return null;
	}

	FileObject createSource() {
		try {
			final FileObject obj = getFiler().createSourceFile(ADDITIONS, (Element[]) null);
			// To accomodate for different classpaths in Maven and Eclipse
			final String plugin2 = obj.toUri().toASCIIString().replace("/target/gaml/additions/GamlAdditions.java", "")
					.replace("/gaml/gaml/additions/GamlAdditions.java", "");
			currentPlugin = plugin2.substring(plugin2.lastIndexOf('/') + 1);
			System.out.println("CURRENT PLUGIN = " + currentPlugin);
			return obj;
		} catch (final Exception e) {
			// emitWarning(e.getMessage(), null);
		}
		return null;
	}

	public Writer createTestWriter() {
		return createTestWriter(getTestFileName());
	}

	public Writer createTestWriter(final String fileName) {
		createTestsFolder();
		try {
			final OutputStream output =
					getFiler().createResource(OUT, getTestFolderName() + ".models", fileName, (Element[]) null)
							.openOutputStream();
			final Writer writer = new OutputStreamWriter(output, CHARSET);
			return writer;
		} catch (final Exception e) {
			e.printStackTrace();
			emitWarning("Impossible to create test file " + fileName + ": ", e);
		}
		return null;
	}

	private String getTestFileName() {
		final String title = currentPlugin.substring(currentPlugin.lastIndexOf('.') + 1);
		return Constants.capitalizeFirstLetter(title) + " Tests.experiment";
	}

	private String getTestFolderName() {
		final String title = currentPlugin.substring(currentPlugin.lastIndexOf('.') + 1);
		return "tests.Generated From " + Constants.capitalizeFirstLetter(title);
	}

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

	Writer createSourceWriter(final FileObject file) {
		try {
			final OutputStream output = file.openOutputStream();
			final Writer writer = new OutputStreamWriter(output, CHARSET);
			return writer;
		} catch (final Exception e) {
			emitWarning("Error in creating source writer", e);
		}
		return null;
	}

	public boolean shouldProduceDoc() {
		return "true".equals(getOptions().get("doc")) || PRODUCES_DOC;
	}

	public InputStream getInputStream(final String string) throws IOException {
		return getFiler().getResource(ProcessorContext.OUT, "", string).openInputStream();
	}

	public List<Annotation> getUsefulAnnotationsOn(final Element e) {
		final List<Annotation> result = new ArrayList<>();
		for (final Class<? extends Annotation> clazz : processors.keySet()) {
			final Annotation a = e.getAnnotation(clazz);
			if (a != null) {
				result.add(a);
			}
		}
		return result;
	}

	public List<String> getRoots() {
		return roots;
	}

}