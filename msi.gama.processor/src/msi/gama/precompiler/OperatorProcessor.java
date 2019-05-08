package msi.gama.precompiler;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.tests;

public class OperatorProcessor extends ElementProcessor<operator> {

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element method,
			final operator op) {
		final String names[] = op.value();
		if (names == null) {
			context.emitError("GAML operators need to have at least one name", method);
			return;
		}
		verifyDoc(context, method, op);
		verifyTests(context, method, op);
		final Set<Modifier> modifiers = method.getModifiers();
		if (!modifiers.contains(Modifier.PUBLIC)) {
			context.emitError("GAML: operators can only be implemented by public (or public static) methods", method);
			return;
		}

		final String declClass = rawNameOf(context, method.getEnclosingElement().asType());
		final List<? extends VariableElement> argParams = ((ExecutableElement) method).getParameters();
		final String[] args = new String[argParams.size()];
		for (int i = 0; i < args.length; i++) {
			final VariableElement ve = argParams.get(i);
			switch (ve.asType().getKind()) {
				case ARRAY:
					context.emitError("GAML: arrays should be wrapped in a GAML container (IList or IMatrix) ", ve);
					return;
				case CHAR:
				case BYTE:
				case SHORT:
					context.emitWarning("GAML: This argument will be casted to int", ve);
					break;
				default:
			}
			args[i] = rawNameOf(context, argParams.get(i).asType());
			verifyClassTypeCompatibility(context, args[i], ve);

		}
		final int n = args.length;
		final boolean scope = n > 0 && args[0].contains("IScope");
		final boolean isStatic = modifiers.contains(Modifier.STATIC);
		if (n == 0 && !isStatic || isStatic && scope && n == 1) {
			context.emitError("GAML: an operator needs to have at least one operand", method);
			return;
		}
		final int actual_args_number = n + (scope ? -1 : 0) + (!isStatic ? 1 : 0);

		final String[] classes = new String[actual_args_number];
		int begin = 0;
		if (!isStatic) {
			classes[0] = declClass;
			begin = 1;
		}
		final int shift = scope ? 1 : 0;
		try {
			for (int i = 0; i < actual_args_number - begin; i++) {
				classes[begin + i] = args[i + shift];
			}
		} catch (final Exception e1) {
			context.emitError("An exception occured in the processing of operators: ", e1, method);
			return;
		}

		final String ret = rawNameOf(context, ((ExecutableElement) method).getReturnType());
		verifyClassTypeCompatibility(context, ret, method);

		switch (((ExecutableElement) method).getReturnType().getKind()) {
			case ARRAY:
				context.emitError("GAML: Wrap the returned array in a GAML container (IList or IMatrix) ", method);
				return;
			case VOID: // does not seem to be recognized
			case NULL:
			case NONE:
			case ERROR:
				context.emitError("GAML operators need to return a value.", method);
				return;
			case CHAR:
			case BYTE:
			case SHORT:
				context.emitWarning("GAML: the return type will be casted to integer", method);
				break;
			case EXECUTABLE:
				context.emitError("GAML: operators cannot return Java executables", method);
				return;
			default:
		}
		if (ret.equals("void")) {
			context.emitError("GAML: operators need to return a value", method);
			return;
		}
		final String met = isStatic ? declClass + "." + method.getSimpleName() : method.getSimpleName().toString();
		sb.append(in).append(op.iterator() ? "_iterator(" : "_operator(");
		toArrayOfStrings(names, sb).append(',');
		buildMethodCall(sb, classes, met, isStatic, scope).append(',');
		toArrayOfClasses(sb, classes).append(',');
		toArrayOfInts(op.expected_content_type(), sb).append(',').append(toClassObject(ret)).append(',')
				.append(toBoolean(op.can_be_const())).append(',').append(op.type()).append(',')
				.append(op.content_type()).append(',').append(op.index_type()).append(',')
				.append(op.content_type_content_type()).append(',').append("(s,o)->");
		buildNAry(sb, classes, met, ret, isStatic, scope).append(");");
	}

	public void verifyClassTypeCompatibility(final ProcessorContext context, final String string, final Element ve) {
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
			context.emitWarning("GAML: " + warning, ve);
		}

	}

	private void verifyDoc(final ProcessorContext context, final Element method, final operator op) {
		doc documentation = method.getAnnotation(doc.class);
		if (documentation == null) {
			final doc[] docs = op.doc();
			if (docs.length > 0) {
				documentation = op.doc()[0];
			}
		}

		if (documentation == null && !op.internal()) {
			context.emitWarning("GAML: operator '" + op.value()[0] + "' is not documented", method);
		}
	}

	private void verifyTests(final ProcessorContext context, final Element method, final operator op) {
		no_test no = method.getAnnotation(no_test.class);
		// if no tests are necessary, skip the verification
		if (no != null)
			return;
		tests tests = method.getAnnotation(tests.class);
		if (tests != null)
			return;
		test test = method.getAnnotation(test.class);
		if (test != null)
			return;
		doc doc = method.getAnnotation(doc.class);
		if (doc != null) {
			boolean hasTests = context.docHasTests(doc);
			if (hasTests)
				return;
		}
		context.emitWarning("GAML: operator '" + op.value()[0] + "' is not tested", method);
	}

	@Override
	protected Class<operator> getAnnotationClass() {
		return operator.class;
	}

	protected static StringBuilder buildNAry(final StringBuilder sb, final String[] classes, final String name,
			final String retClass, final boolean stat, final boolean scope) {
		final String ret = checkPrim(retClass);
		final int start = stat ? 0 : 1;
		final String firstArg = scope ? "s" : "";
		if (stat) {
			sb.append(name).append('(').append(firstArg);
		} else {
			sb.append("o[0]==null?").append(returnWhenNull(ret)).append(":((").append(classes[0]).append(")o[0]).")
					.append(name).append('(').append(firstArg);
		}
		if (start < classes.length) {
			if (scope) {
				sb.append(',');
			}
			for (int i = start; i < classes.length; i++) {
				param(sb, classes[i], "o[" + i + "]");
				sb.append(i != classes.length - 1 ? "," : "");
			}
		}
		sb.append(")");
		return sb;
	}

	@Override
	public String getExceptions() {
		return "throws SecurityException, NoSuchMethodException";
	}

	protected static String extractMethod(final String s, final boolean stat) {
		if (!stat) { return s; }
		return s.substring(s.lastIndexOf('.') + 1);
	}

	protected static String extractClass(final String name, final String string, final boolean stat) {
		if (stat) { return name.substring(0, name.lastIndexOf('.')); }
		return string;
	}

	protected static StringBuilder buildMethodCall(final StringBuilder sb, final String[] classes, final String name,
			final boolean stat, final boolean scope) {
		final int start = stat ? 0 : 1;
		sb.append(toClassObject(extractClass(name, classes[0], stat)));
		sb.append(".getMethod(").append(toJavaString(extractMethod(name, stat))).append(',');
		if (scope) {
			sb.append(toClassObject(ISCOPE)).append(',');
		}
		for (int i = start; i < classes.length; i++) {
			sb.append(toClassObject(classes[i]));
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(')');
		return sb;
	}

	protected final static StringBuilder toArrayOfClasses(final StringBuilder sb, final String[] segments) {
		if (segments == null || segments.length == 0) {
			sb.append("{}");
			return sb;
		}
		sb.append("C(");
		for (int i = 0; i < segments.length; i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(toClassObject(segments[i]));
		}
		sb.append(")");
		return sb;
	}

}
