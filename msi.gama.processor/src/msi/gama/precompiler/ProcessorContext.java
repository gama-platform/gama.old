package msi.gama.precompiler;

// import static msi.gama.precompiler.GamlProperties.GAML;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import msi.gama.precompiler.java.Constants;

public class ProcessorContext implements ProcessingEnvironment, RoundEnvironment, Constants {
	private final static boolean PRODUCES_DOC = true;
	public static final Charset CHARSET = Charset.forName("UTF-8");
	public static final String ADDITIONS = "gaml.additions.GamlAdditions";
	private final static boolean PRODUCES_WARNING = true;
	public static final StandardLocation OUT = StandardLocation.SOURCE_OUTPUT;
	private final ProcessingEnvironment delegate;
	private RoundEnvironment round;
	private TypeMirror iSkill, iAgent;
	String currentPlugin;

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
		Collections.sort(result, (o1, o2) -> o1.toString().compareTo(o2.toString()));
		return result;
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

	void emitWarning(final String s, final Element e) {
		if (!PRODUCES_WARNING)
			return;
		if (e == null)
			getMessager().printMessage(Kind.WARNING, s);
		else
			getMessager().printMessage(Kind.WARNING, s, e);
	}

	void emitError(final String s, final Element e) {
		if (!PRODUCES_WARNING)
			return;
		if (e == null)
			getMessager().printMessage(Kind.ERROR, s);
		else
			getMessager().printMessage(Kind.ERROR, s, e);
	}

	public void setRoundEnvironment(final RoundEnvironment env) {
		round = env;
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

	Writer createWriter(final String s) {
		try {
			final OutputStream output = getFiler().createResource(OUT, "", s, (Element[]) null).openOutputStream();
			final Writer writer = new OutputStreamWriter(output, CHARSET);
			return writer;
		} catch (final Exception e) {
			emitWarning(e.getMessage(), null);
		}
		return null;
	}

	FileObject createSource() {
		try {
			final FileObject obj = getFiler().createSourceFile(ADDITIONS, (Element[]) null);
			final String pluginName =
					URI.create(obj.toUri().toString().replace("/gaml/gaml/additions/GamlAdditions.java", "")).toURL()
							.getFile();
			currentPlugin = pluginName.substring(pluginName.lastIndexOf('/') + 1);
			return obj;
		} catch (final Exception e) {
			emitWarning(e.getMessage(), null);
		}
		return null;
	}

	Writer createSourceWriter(final FileObject file) {
		try {
			final OutputStream output = file.openOutputStream();
			final Writer writer = new OutputStreamWriter(output, CHARSET);
			return writer;
		} catch (final Exception e) {
			emitWarning(e.getMessage(), null);
		}
		return null;
	}

	public boolean shouldProduceDoc() {
		return "true".equals(getOptions().get("doc")) || PRODUCES_DOC;
	}

	public InputStream getInputStream(final String string) throws IOException {
		return getFiler().getResource(ProcessorContext.OUT, "", string).openInputStream();
	}

}