/*******************************************************************************************************
 *
 * OperatorProcessor.java, in msi.gama.processor, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.precompiler;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import msi.gama.precompiler.GamlAnnotations.operator;

/**
 * The Class OperatorProcessor.
 */
public class OperatorProcessor extends ElementProcessor<operator> {

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element method,
			final operator op) {
		final String names[] = op.value();
		if (names == null) {
			context.emitError("GAML operators need to have at least one name", method);
			return;
		}
		final String name = op.value().length == 0 ? method.getSimpleName().toString() : op.value()[0];
		verifyDoc(context, method, "operator " + name, op);
		verifyTests(context, method, op);

		final String declClass = rawNameOf(context, method.getEnclosingElement().asType());
		final List<? extends VariableElement> argParams = ((ExecutableElement) method).getParameters();
		final String[] args = new String[argParams.size()];
		for (int i = 0; i < args.length; i++) {
			final VariableElement ve = argParams.get(i);
			switch (ve.asType().getKind()) {
				case ARRAY:
					context.emitError("arrays should be wrapped in a GAML container (IList or IMatrix) ", ve);
					return;
				case CHAR:
				case BYTE:
				case SHORT:
					context.emitWarning("this argument will be casted to int", ve);
					break;
				default:
			}
			args[i] = rawNameOf(context, argParams.get(i).asType());
			verifyClassTypeCompatibility(context, args[i], ve);

		}
		final int n = args.length;
		final boolean hasScope = n > 0 && args[0].contains("IScope");
		final Set<Modifier> modifiers = method.getModifiers();
		final boolean isStatic = modifiers.contains(Modifier.STATIC);
		if (isStatic && (n == 0 || hasScope && n == 1)) {
			context.emitError("an operator needs to have at least one operand", method);
			return;
		}
		final int actualArgsNumber = n + (hasScope ? -1 : 0) + (!isStatic ? 1 : 0);

		final String[] classes = new String[actualArgsNumber];
		int begin = 0;
		if (!isStatic) {
			classes[0] = declClass;
			begin = 1;
		}
		final int shift = hasScope ? 1 : 0;
		try {
			for (int i = 0; i < actualArgsNumber - begin; i++) { classes[begin + i] = args[i + shift]; }
		} catch (final Exception e1) {
			context.emitError("an exception occured in the processing of operators: ", e1, method);
			return;
		}

		final String ret = rawNameOf(context, ((ExecutableElement) method).getReturnType());
		verifyClassTypeCompatibility(context, ret, method);

		switch (((ExecutableElement) method).getReturnType().getKind()) {
			case ARRAY:
				context.emitError("wrap the returned array in a GAML container (IList or IMatrix) ", method);
				return;
			case VOID: // does not seem to be recognized
			case NULL:
			case NONE:
			case ERROR:
				context.emitError("operators need to return a value.", method);
				return;
			case CHAR:
			case BYTE:
			case SHORT:
				context.emitWarning("the return type will be casted to integer", method);
				break;
			case EXECUTABLE:
				context.emitError("operators cannot return Java executables", method);
				return;
			default:
		}
		if ("void".equals(ret)) {
			context.emitError("operators need to return a value", method);
			return;
		}
		final String met = isStatic ? declClass + "." + method.getSimpleName() : method.getSimpleName().toString();
		sb.append(in).append("_operator(");
		toArrayOfStrings(names, sb).append(',');
		buildMethodCall(sb, classes, met, isStatic, hasScope).append(",null,"); /* doc */
		toArrayOfInts(op.expected_content_type(), sb).append(',').append(toClassObject(ret)).append(',')
				.append(toBoolean(op.can_be_const())).append(',').append(op.type()).append(',')
				.append(op.content_type()).append(',').append(op.index_type()).append(',')
				.append(op.content_type_content_type());

		buildHelperCall(sb, hasScope, isStatic, classes, met);
		sb.append(',').append(toBoolean(op.iterator()));
		sb.append(");");
	}

	/**
	 * Builds the helper call.
	 *
	 * @param sb
	 *            the sb
	 * @param hasScope
	 *            the has scope
	 * @param isStatic
	 *            the is static
	 * @param isUnary
	 *            the is unary
	 * @param classes
	 *            the classes
	 * @param met
	 *            the met
	 */
	private void buildHelperCall(final StringBuilder sb, final boolean hasScope, final boolean isStatic,
			final String[] classes, final String met) {
		sb.append(',').append("(s,o)->");
		final int start = isStatic ? 0 : 1;
		final String firstArg = hasScope ? "s" : "";
		if (isStatic) {
			sb.append(met).append('(').append(firstArg);
		} else {
			sb.append("((").append(classes[0]).append(")o[0]).").append(met).append('(').append(firstArg);
		}
		if (start < classes.length) {
			if (hasScope) { sb.append(','); }
			for (int i = start; i < classes.length; i++) {
				param(sb, classes[i], "o[" + i + "]");
				sb.append(i != classes.length - 1 ? "," : "");
			}
		}
		sb.append(")");
	}

	/**
	 * Verify class type compatibility.
	 *
	 * @param context
	 *            the context
	 * @param string
	 *            the string
	 * @param ve
	 *            the ve
	 */
	public void verifyClassTypeCompatibility(final ProcessorContext context, final String string, final Element ve) {
		String warning = null;
		switch (string) {
			case "Map":
				warning = "it is safer to use the IMap type";
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
			case "float":
			case "Float":
				warning = "it is safer to use the Double type";
				break;
			case "Color":
				warning = "it is safer to use the GamaColor type";
				break;
		}
		if (warning != null) { context.emitWarning(warning, ve); }

	}

	/**
	 * Verify tests.
	 *
	 * @param context
	 *            the context
	 * @param method
	 *            the method
	 * @param op
	 *            the op
	 */
	private void verifyTests(final ProcessorContext context, final Element method, final operator op) {
		if (!hasTests(method, op)) { context.emitWarning("operator '" + op.value()[0] + "' is not tested", method); }
	}

	@Override
	protected Class<operator> getAnnotationClass() { return operator.class; }

	@Override
	public String getExceptions() { return "throws SecurityException, NoSuchMethodException"; }

	/**
	 * Extract method.
	 *
	 * @param s
	 *            the s
	 * @param stat
	 *            the stat
	 * @return the string
	 */
	protected static String extractMethod(final String s, final boolean stat) {
		if (!stat) return s;
		return s.substring(s.lastIndexOf('.') + 1);
	}

	/**
	 * Extract class.
	 *
	 * @param name
	 *            the name
	 * @param string
	 *            the string
	 * @param stat
	 *            the stat
	 * @return the string
	 */
	protected static String extractClass(final String name, final String string, final boolean stat) {
		if (stat) return name.substring(0, name.lastIndexOf('.'));
		return string;
	}

	/**
	 * Builds the method call.
	 *
	 * @param sb
	 *            the sb
	 * @param classes
	 *            the classes
	 * @param name
	 *            the name
	 * @param stat
	 *            the stat
	 * @param scope
	 *            the scope
	 * @return the string builder
	 */
	protected static StringBuilder buildMethodCall(final StringBuilder sb, final String[] classes, final String name,
			final boolean stat, final boolean scope) {
		final int start = stat ? 0 : 1;
		sb.append(toClassObject(extractClass(name, classes[0], stat)));
		sb.append(".getMethod(").append(toJavaString(extractMethod(name, stat))).append(',');
		if (scope) { sb.append(toClassObject(ISCOPE)).append(','); }
		for (int i = start; i < classes.length; i++) {
			sb.append(toClassObject(classes[i]));
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(')');
		return sb;
	}

	/**
	 * To array of classes.
	 *
	 * @param sb
	 *            the sb
	 * @param segments
	 *            the segments
	 * @return the string builder
	 */
	protected final static StringBuilder toArrayOfClasses(final StringBuilder sb, final String[] segments) {
		if (segments == null || segments.length == 0) {
			sb.append("{}");
			return sb;
		}
		sb.append("C(");
		for (int i = 0; i < segments.length; i++) {
			if (i > 0) { sb.append(','); }
			sb.append(toClassObject(segments[i]));
		}
		sb.append(")");
		return sb;
	}

	@Override
	protected boolean validateElement(final ProcessorContext context, final Element e) {

		// TODO: move all other warnings and errors here
		return assertElementIsPublic(context, true, e);
	}

}
