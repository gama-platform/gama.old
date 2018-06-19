package msi.gama.precompiler;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;

public class OperatorProcessor extends ElementProcessor<operator> {

	@Override
	protected void populateElement(final ProcessorContext context, final Element method, final operator op,
			final org.w3c.dom.Element node) {

		final String names[] = op.value();
		if (names == null) {
			context.emitError("GAML operators need to have at least one name", method);
			return;
		}
		final String name = op.value()[0];
		doc documentation = method.getAnnotation(doc.class);
		if (documentation == null) {
			final doc[] docs = op.doc();
			if (docs.length > 0) {
				documentation = op.doc()[0];
			}
		}

		if (documentation == null && !op.internal()) {
			context.emitWarning("GAML: operator '" + name + "' is not documented", method);
		}
		final Set<Modifier> modifiers = method.getModifiers();

		final boolean isStatic = modifiers.contains(Modifier.STATIC);

		if (!modifiers.contains(Modifier.PUBLIC)) {
			context.emitError("GAML: operators can only be implemented by public (or public static) methods", method);
			return;
		}
		final String declClass = rawNameOf(context, method.getEnclosingElement());
		final List<? extends VariableElement> argParams = ((ExecutableElement) method).getParameters();
		final String[] args = new String[argParams.size()];
		for (int i = 0; i < args.length; i++) {
			final VariableElement ve = argParams.get(i);
			switch (ve.asType().getKind()) {
				case ARRAY:
					context.emitError(
							"GAML: operators cannot accept Java arrays arguments. Please wrap this argument in a GAML container type (IList or IMatrix) ",
							ve);
					return;
				case CHAR:
				case BYTE:
				case SHORT:
					context.emitWarning("GAML: The type of this argument will be casted to int", ve);
					break;
				default:
			}
			args[i] = rawNameOf(context, argParams.get(i));
			context.verifyClassTypeCompatibility(args[i], ve);

		}
		final int n = args.length;
		final boolean scope = n > 0 && args[0].contains("IScope");
		if (n == 0 && !isStatic || isStatic && scope && n == 1) {
			context.emitError("GAML: an operator needs to have at least one operand", method);
			return;
		}
		final int actual_args_number = n + (scope ? -1 : 0) + (!isStatic ? 1 : 0);
		String methodName = method.getSimpleName().toString();
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
		context.verifyClassTypeCompatibility(ret, method);

		switch (((ExecutableElement) method).getReturnType().getKind()) {
			case ARRAY:
				context.emitError(
						"GAML: operators cannot return Java arrays. Please wrap this result in a GAML container type (IList or IMatrix) ",
						method);
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
				context.emitWarning("GAML: the return type of this operator will be casted to integer", method);
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

		methodName = isStatic ? declClass + "." + methodName : methodName;
		node.setAttribute("args", arrayToString(classes));
		node.setAttribute("const", String.valueOf(op.can_be_const()));
		node.setAttribute("type", String.valueOf(op.type()));
		node.setAttribute("contents", String.valueOf(op.content_type()));
		node.setAttribute("contents_content_type", String.valueOf(op.content_type_content_type()));
		node.setAttribute("index", String.valueOf(op.index_type()));
		node.setAttribute("iterator", String.valueOf(op.iterator()));
		node.setAttribute("expected_contents", arrayToString(op.expected_content_type()));
		node.setAttribute("returns", ret);
		node.setAttribute("method", methodName);
		node.setAttribute("static", String.valueOf(isStatic));
		node.setAttribute("contextual", String.valueOf(scope));
		node.setAttribute("names", arrayToString(names));
	}

	@Override
	protected boolean isEqual(final org.w3c.dom.Element existingNode, final org.w3c.dom.Element newNode) {
		return existingNode.isEqualNode(newNode);
	}

	@Override
	protected Class<operator> getAnnotationClass() {
		return operator.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {

		final String[] classes = splitInClassObjects(node.getAttribute("args"));
		final String classNames = toArrayOfClasses(node.getAttribute("args"));
		final String kw = toArrayOfStrings(node.getAttribute("names"));
		final String content_type_expected = toArrayOfInts(node.getAttribute("expected_contents"));
		final String canBeConst = toBoolean(node.getAttribute("const"));
		final String type = node.getAttribute("type");
		final String contentType = toType(node.getAttribute("contents"));
		final String contentTypeContentType = toType(node.getAttribute("contents_content_type"));
		final String indexType = toType(node.getAttribute("index"));
		final boolean iterator = node.getAttribute("iterator").equals("true");
		final String ret = node.getAttribute("returns");
		final String m = node.getAttribute("method");
		final boolean stat = node.getAttribute("static").equals("true");
		final boolean scope = node.getAttribute("contextual").equals("true");

		final String helper = concat("new GamaHelper(){", OVERRIDE, "public ", checkPrim(ret), " run(", ISCOPE,
				" s,Object... o)", buildNAry(classes, m, ret, stat, scope), "}");

		sb.append(in).append(iterator ? "_iterator(" : "_operator(").append(kw).append(',')
				.append(buildMethodCall(classes, m, stat, scope)).append(',').append(classNames).append(",")
				.append(content_type_expected).append(",").append(toClassObject(ret)).append(',').append(canBeConst)
				.append(',').append(type).append(',').append(contentType).append(',').append(indexType).append(',')
				.append(contentTypeContentType).append(',').append(helper).append(");");

	}

	protected static String buildNAry(final String[] classes, final String name, final String retClass,
			final boolean stat, final boolean scope) {
		final String ret = checkPrim(retClass);
		final int start = stat ? 0 : 1;
		final String firstArg = scope ? "s" : "";
		String body = stat ? concat("{return ", name, "(", firstArg) : concat("{return o[0]", " == null?",
				returnWhenNull(ret), ":((", classes[0], ")o[0]).", name, "(", firstArg);
		if (start < classes.length) {
			if (scope) {
				body += ",";
			}
			for (int i = start; i < classes.length; i++) {
				body += param(classes[i], "o[" + i + "]") + (i != classes.length - 1 ? "," : "");
			}
		}
		body += ");}";
		return body;
	}

	@Override
	public String getExceptions() {
		return "throws SecurityException, NoSuchMethodException";
	}

	protected static String buildMethodCall(final String[] classes, final String name, final boolean stat,
			final boolean scope) {
		final int start = stat ? 0 : 1;
		final String methodName = extractMethod(name, stat);
		final String className = toClassObject(extractClass(name, classes[0], stat));
		String result = className + ".getMethod(" + toJavaString(methodName) + ", ";
		result += scope ? toClassObject(ISCOPE) + "," : "";
		for (int i = start; i < classes.length; i++) {
			result += toClassObject(classes[i]) + ",";
		}
		if (result.endsWith(",")) {
			result = result.substring(0, result.length() - 1);
		}
		result += ")";
		return result;
	}

	protected final static String toArrayOfClasses(final String array) {
		if (array == null || array.equals("")) { return "{}"; }
		// FIX AD 3/4/13: split(regex) would not include empty trailing strings
		final String[] segments = array.split("\\,", -1);
		String result = "C(";
		for (int i = 0; i < segments.length; i++) {
			if (i > 0) {
				result += ",";
			}
			result += toClassObject(segments[i]);
		}
		result += ")";
		return result;
	}

}
