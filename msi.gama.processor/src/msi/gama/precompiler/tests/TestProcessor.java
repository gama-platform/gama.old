package msi.gama.precompiler.tests;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
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

public class TestProcessor extends ElementProcessor<tests> {

	@Override
	public void serialize(final ProcessorContext context, final StringBuilder sb) {}

	@Override
	public void process(final ProcessorContext context) {
		// Processes tests annotations
		super.process(context);
		// Special case for lone test annotations
		final Map<String, List<Element>> elements = context.groupElements(test.class);
		for (final Map.Entry<String, List<Element>> entry : elements.entrySet()) {
			final StringBuilder sb = opIndex.getOrDefault(entry.getKey(), new StringBuilder());
			for (final Element e : entry.getValue()) {
				try {
					createElement(sb, context, e, createFrom(e.getAnnotation(test.class)));
				} catch (final Exception exception) {
					context.emitError("Exception in processor: " + exception.getMessage(), e);
				}

			}
			opIndex.put(entry.getKey(), sb);
		}
	}

	@Override
	public boolean outputToJava() {
		return false;
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
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final tests tests) {
		final String name = getTestName(determineName(context, e, tests));
		sb.append(ln).append(tab).append("test ").append(name).append(" {");
		for (final test test : tests.value()) {
			final String[] lines = determineText(context, test).split(";");
			for (final String line : lines) {
				if (!line.isEmpty()) {
					sb.append(ln).append(tab).append(tab).append(line).append(';');
				}
			}
		}
		// Output the footer
		sb.append(ln).append(tab).append("}").append(ln);
	}

	public void writeTests(final ProcessorContext context, final Writer sb) throws IOException {
		sb.append("experiment ").append(toJavaString("Tests for " + context.currentPlugin)).append(" type: test {");
		for (final StringBuilder tests : opIndex.values()) {
			sb.append(ln);
			sb.append(tests);
		}
		sb.append(ln).append('}');
		namesAlreadyUsed.clear();
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

}
