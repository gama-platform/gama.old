package msi.gama.precompiler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;

public class ActionProcessor extends ElementProcessor<action> {
	private static Set<String> RESERVED_FACETS = new HashSet<>(Arrays.asList("name", "keyword", "returns"));

	@Override
	protected Class<action> getAnnotationClass() {
		return action.class;
	}

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final Document doc,
			final action action, final org.w3c.dom.Element node) {
		final ExecutableElement ex = (ExecutableElement) e;
		node.setAttribute("name", action.name());
		node.setAttribute("method", ex.getSimpleName().toString());
		node.setAttribute("class", rawNameOf(context, ex.getEnclosingElement()));
		node.setAttribute("returns", getReturnType(context, ex));
		if (action.virtual())
			node.setAttribute("virtual", "true");
		node.setAttribute("name", action.name());
		final arg[] args = action.args();
		for (int i = 0; i < args.length; i++) {
			appendChild(node, buildArg(context, e, doc, args[i]));
		}

	}

	private final org.w3c.dom.Element buildArg(final ProcessorContext context, final Element e, final Document doc,
			final arg arg) {
		final String argName = arg.name();
		if (RESERVED_FACETS.contains(argName)) {
			context.emitWarning(
					"Argument '" + argName
							+ "' prevents this action to be called using facets (e.g. 'do action arg1: val1 arg2: val2;'). Consider renaming it to a non-reserved facet keyword",
					e);
		}
		final org.w3c.dom.Element child = doc.createElement("arg");
		child.setAttribute("name", argName);
		child.setAttribute("type", String.valueOf(arg.type()));
		if (arg.optional())
			child.setAttribute("optional", "true");
		final doc[] docs = arg.doc();
		if (docs.length == 0) {
			context.emitWarning("GAML: argument '" + arg.name() + "' is not documented", e);
		} else
			child.setAttribute("doc", docToString(arg.doc()));
		return child;
	}

	private String getReturnType(final ProcessorContext context, final ExecutableElement ex) {
		final TypeMirror tm = ex.getReturnType();
		if (tm.getKind().equals(TypeKind.VOID)) {
			return "void";
		} else {
			return rawNameOf(context, tm);
		}
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {

		final String method = node.getAttribute("method");
		final String clazz = node.getAttribute("class");
		final String virtual = toJavaString(toBoolean(node.getAttribute("virtual")));
		final String name = node.getAttribute("name");
		final String ret = checkPrim(node.getAttribute("returns"));
		String args = "new ChildrenProvider(Arrays.asList(";
		// TODO Argument types not taken into account when declaring them
		final NodeList list = node.getElementsByTagName("*");
		for (int i = 0; i < list.getLength(); i++) {
			final org.w3c.dom.Element child = (org.w3c.dom.Element) list.item(i);
			if (i > 0) {
				args += ",";
			}
			args += "desc(ARG,NAME," + toJavaString(child.getAttribute("name")) + ", TYPE, "
					+ toJavaString(child.getAttribute("type"));
			args += ", \"optional\", " + toJavaString(toBoolean(child.getAttribute("optional"))) + ")";
		}
		args += "))";
		final String desc = "desc(PRIMITIVE, null, " + args + ", NAME, " + toJavaString(name) + ",TYPE, " + "Ti("
				+ toClassObject(ret) + "), VIRTUAL," + virtual + ")";
		sb.append(concat(in, "_action(", toJavaString(method), ",", toClassObject(clazz), ",new GamaHelper(T(",
				toClassObject(ret), "), ", toClassObject(clazz), "){", OVERRIDE, "public ",
				ret.equals("void") ? "Object" : ret, " run(", ISCOPE, " s, ", IAGENT, " a, ", ISUPPORT,
				" t, Object... v){ ", !ret.equals("void") ? "return" : "", " ((", clazz, ") t).", method, "(s); ",
				ret.equals("void") ? "return null;" : "", "} },", desc, ",",
				buildMethodCallForAction(clazz, method, false), ");"));

	}

	@Override
	public String getExceptions() {
		return "throws SecurityException, NoSuchMethodException";
	}

	protected String buildMethodCallForAction(final String clazz, final String name, final boolean stat) {
		final String methodName = extractMethod(name, stat);
		final String className = toClassObject(extractClass(name, clazz, stat));
		String result = className + ".getMethod(" + toJavaString(methodName) + ", ";
		result += toClassObject(ISCOPE) + ")";
		return result;
	}

}
