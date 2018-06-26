package msi.gama.precompiler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;

public class ActionProcessor extends ElementProcessor<action> {

	private static Set<String> RESERVED_FACETS = new HashSet<>(Arrays.asList("name", "keyword", "returns"));

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final action action) {
		final String method = e.getSimpleName().toString();
		final String clazz = rawNameOf(context, e.getEnclosingElement().asType());
		final String ret = checkPrim(getReturnType(context, (ExecutableElement) e));
		sb.append(in).append("_action(").append("(s,a,t,v)->").append(!ret.equals("void") ? "" : "{").append("((")
				.append(clazz).append(") t).").append(method).append("(s)")
				.append(ret.equals("void") ? ";return null;}," : ",").append("desc(PRIM,");
		buildArgs(context, e, action.args(), sb).append(",NAME,").append(toJavaString(action.name()))
				.append(",TYPE,Ti(").append(toClassObject(ret)).append("),VIRTUAL,")
				.append(toJavaString(String.valueOf(action.virtual()))).append(')').append(',')
				.append(toClassObject(clazz)).append(".getMethod(").append(toJavaString(method)).append(',')
				.append(toClassObject(ISCOPE)).append("));");
	}

	@Override
	protected Class<action> getAnnotationClass() {
		return action.class;
	}

	private final StringBuilder buildArgs(final ProcessorContext context, final Element e, final arg[] args,
			final StringBuilder sb) {
		sb.append("new Children(");
		// TODO Argument types not taken into account when declaring them
		for (int i = 0; i < args.length; i++) {
			final arg arg = args[i];
			if (i > 0) {
				sb.append(',');
			}
			final String argName = arg.name();
			if (RESERVED_FACETS.contains(argName)) {
				context.emitWarning("Argument '" + argName
						+ "' prevents this action to be called using facets (e.g. 'do action arg1: val1 arg2: val2;'). Consider renaming it to a non-reserved facet keyword",
						e);
			}
			final doc[] docs = arg.doc();
			if (docs.length == 0) {
				context.emitWarning("GAML: argument '" + arg.name() + "' is not documented", e);
			}
			sb.append("desc(ARG,NAME,").append(toJavaString(argName)).append(",TYPE,")
					.append(toJavaString(String.valueOf(arg.type()))).append(",\"optional\",")
					.append(toJavaString(String.valueOf(arg.optional()))).append(')');
		}
		sb.append(")");
		return sb;

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
