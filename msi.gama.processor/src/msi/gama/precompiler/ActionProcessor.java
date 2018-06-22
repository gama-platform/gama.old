package msi.gama.precompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.ActionProcessor.Ac;
import msi.gama.precompiler.ActionProcessor.Ac.Arg;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;

public class ActionProcessor extends ElementProcessor<action, Ac> {

	public static class Ac {

		public String name;
		public String method;
		public String clazz;
		public String returns;
		public boolean virtual;
		public List<Arg> args = new ArrayList<>();

		public static class Arg {

			public String name;
			public int type;
			public boolean optional;
			public String doc;

		}

	}

	private static Set<String> RESERVED_FACETS = new HashSet<>(Arrays.asList("name", "keyword", "returns"));

	@Override
	public void createJava(final ProcessorContext context, final StringBuilder sb, final Ac node) {
		final String virtual = toJavaString(String.valueOf(node.virtual));
		final String ret = checkPrim(node.returns);
		final StringBuilder args = new StringBuilder("new ChildrenProvider(Arrays.asList(");
		// TODO Argument types not taken into account when declaring them
		for (int i = 0; i < node.args.size(); i++) {
			final Arg child = node.args.get(i);
			if (i > 0) {
				args.append(',');
			}
			args.append("desc(ARG,NAME,").append(toJavaString(child.name)).append(", TYPE, ")
					.append(toJavaString(String.valueOf(child.type))).append(", \"optional\", ")
					.append(toJavaString(String.valueOf(child.optional))).append(')');
		}
		args.append("))");
		sb.append(in).append("_action(").append(toJavaString(node.method)).append(',');
		sb.append(toClassObject(node.clazz)).append(",new GamaHelper(T(").append(toClassObject(ret)).append("),")
				.append(toClassObject(node.clazz)).append("){").append(OVERRIDE).append("public ")
				.append(ret.equals("void") ? "Object" : ret);
		sb.append(" run(").append(ISCOPE).append(" s, ").append(IAGENT);
		sb.append(" a, ").append(ISUPPORT).append(" t, Object... v){ ").append(!ret.equals("void") ? "return" : "");
		sb.append(" ((").append(node.clazz).append(") t).").append(node.method);
		sb.append("(s); ").append(ret.equals("void") ? "return null;" : "").append("} },");
		sb.append("desc(PRIMITIVE, null, ").append(args).append(", NAME, ").append(toJavaString(node.name))
				.append(",TYPE, Ti(").append(toClassObject(ret)).append("), VIRTUAL,").append(virtual).append(')');
		sb.append(',').append(toClassObject(node.clazz)).append(".getMethod(").append(toJavaString(node.method))
				.append(',').append(toClassObject(ISCOPE)).append("));");
	}

	@Override
	public Ac createElement(final ProcessorContext context, final Element e, final action action) {
		final Ac node = new Ac();
		final ExecutableElement ex = (ExecutableElement) e;
		node.name = action.name();
		node.method = ex.getSimpleName().toString();
		node.clazz = rawNameOf(context, ex.getEnclosingElement().asType());
		node.returns = getReturnType(context, ex);
		node.virtual = action.virtual();
		for (final arg arg : action.args()) {
			node.args.add(buildArg(context, e, arg));
		}
		return node;
	}

	@Override
	protected Class<action> getAnnotationClass() {
		return action.class;
	}

	private final Arg buildArg(final ProcessorContext context, final Element e, final arg arg) {
		final String argName = arg.name();
		if (RESERVED_FACETS.contains(argName)) {
			context.emitWarning("Argument '" + argName
					+ "' prevents this action to be called using facets (e.g. 'do action arg1: val1 arg2: val2;'). Consider renaming it to a non-reserved facet keyword",
					e);
		}
		final Arg child = new Arg();
		child.name = argName;
		child.type = arg.type();
		child.optional = arg.optional();
		final doc[] docs = arg.doc();
		if (docs.length == 0) {
			context.emitWarning("GAML: argument '" + arg.name() + "' is not documented", e);
		} else {
			child.doc = docToString(arg.doc());
		}
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
	public String getExceptions() {
		return "throws SecurityException, NoSuchMethodException";
	}

}
