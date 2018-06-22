package msi.gama.precompiler.tests;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

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
import msi.gama.precompiler.tests.TestProcessor.Te;

public class TestProcessor extends ElementProcessor<tests, Te> {

	public static class Te {

		public String name;
		public List<String> tests = new ArrayList<>();

	}

	@Override
	public void process(final ProcessorContext context) {
		// Processes tests annotations
		super.process(context);
		// Special case for lone test annotations
		final Map<String, List<Element>> elements = context.groupElements(test.class);
		for (final Map.Entry<String, List<Element>> entry : elements.entrySet()) {
			final List<Te> list = get(entry.getKey(), opIndex);
			for (final Element e : entry.getValue()) {
				try {
					final Te node = createElement(context, e, createFrom(e.getAnnotation(test.class)));
					list.add(node);
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
	public Te createElement(final ProcessorContext context, final Element e, final tests tests) {
		final Te node = new Te();
		final String name = determineName(context, e, tests);
		node.name = name;
		for (final test test : tests.value()) {
			node.tests.add(determineText(context, test));
		}
		return node;
	}

	public boolean hasTests() {
		return opIndex.size() > 0;
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
	public void createJava(final ProcessorContext context, final StringBuilder sb, final Te node) {}

	public void writeTests(final ProcessorContext context, final StringBuilder sb) {
		sb.append("experiment ").append(toJavaString("Tests for " + context.currentPlugin)).append(" type: test {");
		for (final List<Te> tests : opIndex.values()) {
			for (final Te child : tests) {
				sb.append(ln);
				populateGaml(context, sb, child);
			}
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

	public void populateGaml(final ProcessorContext context, final StringBuilder sb, final Te node) {
		// Output the header of the test statement
		sb.append(ln).append(tab).append("test ").append(getTestName(node.name)).append(" {");
		// Output the text of all individual assertions found
		for (int i = 0; i < node.tests.size(); i++) {
			final String child = node.tests.get(i);
			final String[] lines = child.split(";");
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
