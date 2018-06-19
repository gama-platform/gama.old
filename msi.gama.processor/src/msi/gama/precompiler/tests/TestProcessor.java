package msi.gama.precompiler.tests;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

import org.w3c.dom.NodeList;

import msi.gama.precompiler.ElementProcessor;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.tests;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.ProcessorContext;

public class TestProcessor extends ElementProcessor<tests> {

	final Map<String, List<org.w3c.dom.Element>> testIndex = new HashMap<>();

	@Override
	public void processXML(final ProcessorContext context) {
		// Processes tests annotations
		super.processXML(context);
		// Special case for lone test annotations
		final Map<String, List<Element>> elements = context.groupElements(test.class);
		final org.w3c.dom.Element root = getRootNode(document);
		for (final Map.Entry<String, List<Element>> entry : elements.entrySet()) {
			final List<org.w3c.dom.Element> list = clearAndGetFrom(entry.getKey(), testIndex);
			for (final Element e : entry.getValue()) {
				try {
					final org.w3c.dom.Element node = document.createElement(getElementName());
					populateElement(context, e, createFrom(e.getAnnotation(test.class)), node);
					list.add(node);
					root.appendChild(node);
				} catch (final Exception exception) {
					context.emitError("Exception in processor: " + exception.getMessage(), e);
				}

			}
		}
	}

	private tests createFrom(final test test) {
		return new tests() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return tests.class;
			}

			@Override
			public test[] value() {
				return new test[] { test };
			}
		};
	}

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final tests tests,
			final org.w3c.dom.Element node) {
		final String name = determineName(context, e, tests);
		node.setAttribute("name", name);
		for (final test test : tests.value()) {
			final org.w3c.dom.Element child = document.createElement("test");
			child.setAttribute("value", determineText(context, test));
			appendChild(node, child);
		}
	}

	public boolean hasTests(final ProcessorContext context) {
		final NodeList list = getRootNode(document).getElementsByTagName("tests");
		return list.getLength() > 0;
	}

	private String determineText(final ProcessorContext context, final test test) {
		String text = test.value().trim();
		final int lastSemiColon = text.lastIndexOf(';');
		String lastAssert = text.substring(lastSemiColon + 1);
		text = text.substring(0, lastSemiColon + 1);
		if (lastAssert.isEmpty()) { return text; }
		if (test.warning()) {
			lastAssert += " warning: true";
		}
		return text + "assert " + lastAssert + ";";
	}

	private String determineName(final ProcessorContext context, final Element e, final tests tests) {
		String testName = null;
		// Looking for named tests and concatenating their individual names
		for (final test test : tests.value()) {
			final String individualName = test.name();
			if (!individualName.isEmpty()) {
				if (testName == null) {
					testName = individualName;
				} else {
					testName += " and " + individualName;
				}
			}
		}
		// No named tests, proceed by inferring the name from the GAML artefact (if present)
		if (testName == null) {
			for (final Annotation a : context.getUsefulAnnotationsOn(e)) {
				switch (a.annotationType().getSimpleName()) {
					case "operator":
						testName = "Operator " + ((operator) a).value()[0];
						break;
					case "constant":
						testName = "Constant " + ((constant) a).value();
						break;
					case "symbol":
						testName = ((symbol) a).name()[0];
						break;
					case "type":
						testName = "Type " + ((type) a).name();
						break;
					case "skill":
						testName = "Skill " + ((skill) a).name();
						break;
					case "species":
						testName = "Species " + ((species) a).name();
						break;
					case "file":
						testName = ((file) a).name() + " File";
						break;
					case "action":
						testName = "Action " + ((action) a).name();
						break;
					case "getter":
						testName = "Getting " + ((getter) a).value();
						break;
					case "setter":
						testName = "Setting " + ((setter) a).value();
				}
				if (testName != null) {
					break;
				}
			}
		}
		// No named tests and no GAML artefact present; grab the name of the Java element as a last call
		if (testName == null) {
			testName = e.getSimpleName().toString();
		}
		return testName;
	}

	@Override
	protected Class<tests> getAnnotationClass() {
		return tests.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {

	}

	public void writeTests(final ProcessorContext context, final StringBuilder sb) {
		final NodeList list = getRootNode(document).getElementsByTagName("tests");
		sb.append("experiment ").append(toJavaString("Tests for " + context.currentPlugin)).append(" type: test {");
		for (int i = 0; i < list.getLength(); i++) {
			sb.append(ln);
			final org.w3c.dom.Element child = (org.w3c.dom.Element) list.item(i);
			populateGaml(context, sb, child);
		}
		sb.append(ln).append('}');
		namesAlreadyUsed.clear();
	}

	final Map<String, Integer> namesAlreadyUsed = new HashMap<>();

	private String getTestName(final String name) {
		String result = name;
		if (namesAlreadyUsed.containsKey(name)) {
			final int number = namesAlreadyUsed.get(name) + 1;
			namesAlreadyUsed.put(name, number);
			result = name + " (" + number + ")";
		} else {
			namesAlreadyUsed.put(name, 0);
		}
		return toJavaString(result);
	}

	public void populateGaml(final ProcessorContext context, final StringBuilder sb, final org.w3c.dom.Element node) {
		// Output the header of the test statement
		sb.append(ln).append(tab).append("test ").append(getTestName(node.getAttribute("name"))).append(" {");
		// Output the text of all individual assertions found
		final NodeList list = node.getElementsByTagName("test");
		for (int i = 0; i < list.getLength(); i++) {
			final org.w3c.dom.Element child = (org.w3c.dom.Element) list.item(i);
			final String[] lines = child.getAttribute("value").split(";");
			for (final String line : lines) {
				if (!line.isEmpty()) {
					sb.append(ln).append(tab).append(tab).append(line).append(';');
				}
			}
		}
		// Output the footer
		sb.append(ln).append(tab).append("}").append(ln);
	}
}
