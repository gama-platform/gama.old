package msi.gama.precompiler;

import static msi.gama.precompiler.GamlProperties.GAML;
import static msi.gama.precompiler.java.JavaWriter.DOC_SEP;
import static msi.gama.precompiler.java.JavaWriter.IMPORTS;
import static msi.gama.precompiler.java.JavaWriter.SEP;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;

public class ProcessorContext implements ProcessingEnvironment, RoundEnvironment {
	private final static boolean PRODUCES_DOC = true;
	public static final Charset CHARSET = Charset.forName("UTF-8");
	public static final String ADDITIONS = "gaml.additions.GamlAdditions";
	private final static boolean PRODUCES_WARNING = true;
	public static final StandardLocation OUT = StandardLocation.SOURCE_OUTPUT;
	private final ProcessingEnvironment delegate;
	private final GamlProperties properties;
	private RoundEnvironment round;
	private TypeMirror iSkill, iAgent;

	ProcessorContext(final ProcessingEnvironment pe) {
		delegate = pe;
		GamlProperties gp;
		try {
			gp = new GamlProperties(pe.getFiler().getResource(ProcessorContext.OUT, "", GAML).openReader(true));
		} catch (final Exception e) {
			gp = new GamlProperties();
		}
		properties = gp;
	}

	GamlProperties getProperties() {
		return properties;
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

	String rawNameOf(final Element e) {
		return rawNameOf(e.asType());
	}

	String rawNameOf(final TypeMirror t) {
		if (t.getKind().equals(TypeKind.VOID))
			return "void";
		final String init = getTypeUtils().erasure(t).toString();
		final String[] segments = init.split("\\.");
		final StringBuilder sb = new StringBuilder();
		int index = 0;
		for (final String segment : segments) {
			final int i = segment.indexOf('<');
			final int j = segment.lastIndexOf('>');
			final String string = i > -1 ? segment.substring(0, i) + segment.substring(j + 1) : segment;
			if (index++ > 0) {
				sb.append(".");
			}
			sb.append(string);
		}
		String clazz = sb.toString();
		for (int i = 0; i < IMPORTS.length; i++) {
			if (clazz.startsWith(IMPORTS[i])) {
				// AD: false
				final String temp = clazz.replace(IMPORTS[i] + ".", "");
				if (!temp.contains(".")) {
					clazz = temp;
				}
			}
		}
		return clazz;
	}

	/**
	 * Format 0.value 1.deprecated 2.returns 3.comment 4.nb_cases 5.[specialCases$]* 6.nb_examples 7.[examples$]* Uses
	 * its own separator (DOC_SEP)
	 *
	 * @param docs
	 *            an Array of @doc annotations (only the 1st is significant)
	 * @return aString containing the documentation formatted using the format above
	 */
	String docToString(final doc[] docs) {
		if (docs == null || docs.length == 0) { return ""; }
		return docToString(docs[0]);
	}

	String docToString(final doc doc) {
		if (doc == null) { return ""; }
		final StringBuilder sb = new StringBuilder();
		sb.append(doc.value()).append(DOC_SEP);
		sb.append(doc.deprecated());
		return sb.toString();
	}

	String facetsToString(final facets facets, final Element e) {
		final StringBuilder sb = new StringBuilder();
		final Set<String> undocumented = new HashSet<>();
		if (facets.value() != null) {
			for (final facet f : facets.value()) {
				final doc[] docs = f.doc();
				if (docs.length == 0 && !f.internal()) {
					undocumented.add(f.name());
				}
				sb.append(facetToString(f)).append(SEP);
			}
			if (facets.value().length > 0) {
				sb.setLength(sb.length() - 1);
			}
			if (!undocumented.isEmpty())
				emitWarning("GAML: facets '" + undocumented + "' are not documented", e);
		}
		return sb.toString();
	}

	// Format: 1.name 2.[type,]+ 3.[value,]* 4.optional 5. internal 6.doc
	String facetToString(final facet facet) {
		final StringBuilder sb = new StringBuilder();
		sb.append(facet.name()).append(SEP);
		sb.append(arrayToString(facet.type())).append(SEP);
		sb.append(facet.of()).append(SEP);
		sb.append(facet.index()).append(SEP);
		sb.append(arrayToString(facet.values())).append(SEP);
		sb.append(facet.optional()).append(SEP);
		sb.append(facet.internal()).append(SEP);
		sb.append(docToString(facet.doc()));
		return sb.toString();
	}

	private String typeArrayToFirstType(final int[] array) {
		if (array.length == 0) { return "unknown"; }
		return Integer.toString(array[0]);
	}

	private String arrayToString(final int[] array) {
		if (array.length == 0) { return ""; }
		final StringBuilder sb = new StringBuilder();
		for (final int i : array) {
			sb.append(i).append(",");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	String arrayToString(final String[] array) {
		if (array.length == 0) { return ""; }
		final StringBuilder sb = new StringBuilder();
		for (final String i : array) {
			sb.append(replaceCommas(i)).append(",");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	String replaceCommas(final String s) {
		return s.replace(",", "COMMA");
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

	void write(final Class<? extends Annotation> c, final String s) {
		for (final Element e : sortElements(c)) {
			getProperties().put(s, nameOf((TypeElement) (e instanceof TypeElement ? e : e.getEnclosingElement())));
		}
	}

	public void storeProperties() {
		final Writer gamlWriter = createWriter(GAML);
		if (gamlWriter != null)
			getProperties().store(gamlWriter);
	}

	Writer createWriter(final String s) {
		try {
			final OutputStream output = getFiler().createResource(OUT, "", s, (Element[]) null).openOutputStream();
			final Writer writer = new OutputStreamWriter(output, Charset.forName("UTF-8"));
			return writer;
		} catch (final Exception e) {
			emitWarning(e.getMessage(), null);
		}
		return null;
	}

	Writer createSourceWriter() {
		try {
			final OutputStream output = getFiler().createSourceFile(ADDITIONS, (Element[]) null).openOutputStream();
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

}
