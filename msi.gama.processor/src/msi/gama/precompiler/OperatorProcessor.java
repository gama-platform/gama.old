package msi.gama.precompiler;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.OperatorProcessor.Op;

public class OperatorProcessor extends ElementProcessor<operator, Op> {

	@Override
	public Op createElement(final ProcessorContext context, final Element method, final operator op) {
		// NAMES
		final String names[] = op.value();
		if (names == null) {
			context.emitError("GAML operators need to have at least one name", method);
			return null;
		}
		// DOC
		verifyDoc(context, method, op);
		// MODIFIERS
		final Set<Modifier> modifiers = method.getModifiers();

		if (!modifiers.contains(Modifier.PUBLIC)) {
			context.emitError("GAML: operators can only be implemented by public (or public static) methods", method);
			return null;
		}

		final String declClass = rawNameOf(context, method.getEnclosingElement().asType());
		final List<? extends VariableElement> argParams = ((ExecutableElement) method).getParameters();
		final String[] args = new String[argParams.size()];
		for (int i = 0; i < args.length; i++) {
			final VariableElement ve = argParams.get(i);
			switch (ve.asType().getKind()) {
				case ARRAY:
					context.emitError("GAML: arrays should be wrapped in a GAML container (IList or IMatrix) ", ve);
					return null;
				case CHAR:
				case BYTE:
				case SHORT:
					context.emitWarning("GAML: This argument will be casted to int", ve);
					break;
				default:
			}
			args[i] = rawNameOf(context, argParams.get(i).asType());
			context.verifyClassTypeCompatibility(args[i], ve);

		}
		final int n = args.length;
		final boolean scope = n > 0 && args[0].contains("IScope");
		final boolean isStatic = modifiers.contains(Modifier.STATIC);
		if (n == 0 && !isStatic || isStatic && scope && n == 1) {
			context.emitError("GAML: an operator needs to have at least one operand", method);
			return null;
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
			return null;
		}

		final String ret = rawNameOf(context, ((ExecutableElement) method).getReturnType());
		context.verifyClassTypeCompatibility(ret, method);

		switch (((ExecutableElement) method).getReturnType().getKind()) {
			case ARRAY:
				context.emitError("GAML: Wrap the returned array in a GAML container (IList or IMatrix) ", method);
				return null;
			case VOID: // does not seem to be recognized
			case NULL:
			case NONE:
			case ERROR:
				context.emitError("GAML operators need to return a value.", method);
				return null;
			case CHAR:
			case BYTE:
			case SHORT:
				context.emitWarning("GAML: the return type will be casted to integer", method);
				break;
			case EXECUTABLE:
				context.emitError("GAML: operators cannot return Java executables", method);
				return null;
			default:
		}
		if (ret.equals("void")) {
			context.emitError("GAML: operators need to return a value", method);
			return null;
		}
		final Op node = new Op();
		node.method = isStatic ? declClass + "." + method.getSimpleName() : method.getSimpleName().toString();
		node.args = classes;
		node.isConst = op.can_be_const();
		node.type = op.type();
		node.contentsType = op.content_type();
		node.contents_contentsType = op.content_type_content_type();
		node.indexType = op.index_type();
		node.iterator = op.iterator();
		node.expectedContentsType = op.expected_content_type();
		node.returns = ret;
		node.contextual = scope;
		node.isStatic = isStatic;
		node.names = names;

		return node;
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

	static class Op {
		String[] args, names;
		boolean isConst, iterator, isStatic, contextual;
		int type, contentsType, contents_contentsType, indexType;
		int[] expectedContentsType;
		String returns;
		String method;
	}

	@Override
	protected Class<operator> getAnnotationClass() {
		return operator.class;
	}

	@Override
	public void createJava(final ProcessorContext context, final StringBuilder sb, final Op node) {
		sb.append(in).append(node.iterator ? "_iterator(" : "_operator(");
		toArrayOfStrings(node.names, sb).append(',');
		buildMethodCall(sb, node.args, node.method, node.isStatic, node.contextual);
		sb.append(',');
		toArrayOfClasses(sb, node.args).append(',');
		toArrayOfInts(node.expectedContentsType, sb).append(',').append(toClassObject(node.returns)).append(',')
				.append(node.isConst).append(',').append(node.type).append(',').append(node.contentsType).append(',')
				.append(node.indexType).append(',').append(node.contents_contentsType).append(',');
		sb.append("new GamaHelper(){").append(OVERRIDE).append("public ").append(checkPrim(node.returns))
				.append(" run(").append(ISCOPE).append(" s,Object... o)");
		buildNAry(sb, node.args, node.method, node.returns, node.isStatic, node.contextual);
		sb.append("});");
	}

	protected static void buildNAry(final StringBuilder sb, final String[] classes, final String name,
			final String retClass, final boolean stat, final boolean scope) {
		final String ret = checkPrim(retClass);
		final int start = stat ? 0 : 1;
		final String firstArg = scope ? "s" : "";
		if (stat) {
			sb.append("{return ").append(name).append("(").append(firstArg);
		} else {
			sb.append("{return o[0] == null?").append(returnWhenNull(ret)).append(":((").append(classes[0])
					.append(")o[0]).").append(name).append('(').append(firstArg);
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
		sb.append(");}");
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

	protected static void buildMethodCall(final StringBuilder sb, final String[] classes, final String name,
			final boolean stat, final boolean scope) {
		final int start = stat ? 0 : 1;
		sb.append(toClassObject(extractClass(name, classes[0], stat)));
		sb.append(".getMethod(").append(toJavaString(extractMethod(name, stat))).append(", ");
		if (scope) {
			sb.append(toClassObject(ISCOPE)).append(',');
		}
		for (int i = start; i < classes.length; i++) {
			sb.append(toClassObject(classes[i]));
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(')');
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
