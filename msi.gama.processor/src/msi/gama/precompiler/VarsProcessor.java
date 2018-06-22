package msi.gama.precompiler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.VarsProcessor.Vars;
import msi.gama.precompiler.VarsProcessor.Vars.Getter;
import msi.gama.precompiler.VarsProcessor.Vars.Setter;
import msi.gama.precompiler.VarsProcessor.Vars.Var;

public class VarsProcessor extends ElementProcessor<vars, Vars> {

	public static class Vars {
		List<Var> vars = new ArrayList<>();

		public static class Var {
			String name, clazz, init;
			int type, contents, index;
			boolean isConst;
			String[] dependsOn;
			public String facets;
			public String doc;
			public Setter setter;
			public Getter getter;
		}

		public static class Getter {

			public String method;
			public String returns;
			public boolean dynamic;
			public boolean isField;
			public boolean scope;
			public boolean initializer;

		}

		public static class Setter {

			public String method;
			public String param_class;
			public boolean dynamic;
			public boolean scope;

		}

	}

	static final StringBuilder CONCAT = new StringBuilder();

	protected final static String concat(final String... array) {
		for (final String element : array) {
			CONCAT.append(element);
		}
		final String result = CONCAT.toString();
		CONCAT.setLength(0);
		return result;
	}

	@Override
	public void createJava(final ProcessorContext context, final StringBuilder sb, final Vars vars) {
		for (final Var node : vars.vars) {
			String getterHelper = null;
			String initerHelper = null;
			String setterHelper = null;
			// getter
			final Getter getter = node.getter;
			if (getter != null) {

				if (getter.isField) {
					getterHelper = concat("new GamaHelper(){", OVERRIDE, "public ", checkPrim(getter.returns), " run(",
							ISCOPE, " scope, ", OBJECT, "... v){return (v==null||v.length==0)?",
							returnWhenNull(checkPrim(getter.returns)), ":((", node.clazz, ") v[0]).", getter.method,
							getter.scope ? "(scope);}}" : "();}}");
				} else {
					getterHelper = concat("new GamaHelper(", toClassObject(node.clazz), "){", OVERRIDE, "public ",
							checkPrim(getter.returns), " run(", ISCOPE, " scope, ", IAGENT, " a, ", ISUPPORT,
							" t, Object... v) {return t == null?", returnWhenNull(checkPrim(getter.returns)), ":((",
							node.clazz, ")t).", getter.method, "(", getter.scope ? "scope" : "",
							getter.dynamic ? (getter.scope ? "," : "") + "a);}}" : ");}}");
				}

				// initer
				if (getter.initializer) {
					initerHelper = getterHelper;
				}

			}

			// setter
			final Setter setter = node.setter;
			if (setter != null) {
				final String param = checkPrim(setter.param_class);
				setterHelper = concat("new GamaHelper(", toClassObject(node.clazz), ")", "{", OVERRIDE,
						"public Object ", " run(", ISCOPE, " scope, ", IAGENT, " a, ", ISUPPORT, " t, Object... arg)",
						" {if (t != null) ((", node.clazz, ") t).", setter.method, "(", setter.scope ? "scope," : "",
						setter.dynamic ? "a, " : "", "(" + param + ") arg[0]); return null; }}");
			}
			final boolean isField = getter != null && getter.isField;
			sb.append(in).append(isField ? "_field(" : "_var(").append(toClassObject(node.clazz)).append(",")
					.append(toJavaString(escapeDoubleQuotes(node.doc))).append(",");
			if (isField) {
				sb.append("new OperatorProto(").append(toJavaString(node.name)).append(", null, ").append(getterHelper)
						.append(", false, true, ").append(node.type).append(",").append(toClassObject(node.clazz))
						.append(", false, ").append(node.type).append(",").append(node.contents).append(",")
						.append(node.index).append(",").append("AI").append(")");
			} else {
				sb.append("desc(").append(node.type).append(",");
				toOwnArrayOfStrings(node.facets, sb).append("),").append(getterHelper).append(',').append(initerHelper)
						.append(',').append(setterHelper);
			}
			sb.append(");");
		}

	}

	@Override
	public Vars createElement(final ProcessorContext context, final Element e, final vars vars) {
		final Vars result = new Vars();
		final TypeMirror clazz = e.asType();
		final boolean isField = !context.getTypeUtils().isAssignable(clazz, context.getISkill())
				&& !context.getTypeUtils().isAssignable(clazz, context.getIAgent());
		final Set<String> undocumented = new HashSet<>();
		for (final var s : vars.value()) {
			final Vars.Var child = new Var();

			child.type = s.type();
			child.contents = s.of();
			child.index = s.index();
			child.name = s.name();
			child.clazz = rawNameOf(context, e.asType());
			child.isConst = s.constant();
			child.dependsOn = s.depends_on();
			child.init = s.init();

			final StringBuilder sb = new StringBuilder();
			sb.append("type").append(',').append(child.type).append(',').append("name").append(',').append(child.name)
					.append(',').append("const").append(',').append(s.constant());

			final String[] dependencies = s.depends_on();
			if (dependencies.length > 0) {
				String depends = "[";
				for (int i = 0; i < dependencies.length; i++) {
					final String string = dependencies[i];
					depends += string;
					if (i < dependencies.length - 1) {
						depends += "COMMA";
					}
				}
				depends += "]";
				sb.append(',').append("depends_on").append(',').append(depends);
			}
			if (child.contents != 0) {
				sb.append(',').append("of").append(',').append(child.contents);
			}
			final String init = child.init;
			if (!"".equals(init)) {
				sb.append(',').append("init").append(',').append(init.replace(",", "COMMA"));
			}
			child.facets = sb.toString();

			addGetter(context, e, isField, s.name(), child);
			addSetter(context, e, isField, s.name(), child);

			final doc[] docs = s.doc();
			String d;
			if (docs.length == 0) {
				if (!s.internal()) {
					undocumented.add(s.name());
				}
				d = "";
			} else {
				d = docs[0].value();
				child.doc = d;
			}
			result.vars.add(child);
		}
		if (!undocumented.isEmpty()) {
			context.emitWarning("GAML: vars '" + undocumented + "' are not documented", e);
		}
		return result;
	}

	private void addSetter(final ProcessorContext context, final Element e, final boolean isField, final String name,
			final Var var) {
		for (final Element m : e.getEnclosedElements()) {
			final setter setter = m.getAnnotation(setter.class);
			if (setter != null && setter.value().equals(name)) {
				final ExecutableElement ex = (ExecutableElement) m;
				final List<? extends VariableElement> argParams = ex.getParameters();
				final int n = argParams.size();
				if (n == 0) {
					context.emitError("GAML: Setters must declare at least one argument (or 2 if the scope is passed",
							ex);
					return;
				}
				final String[] args = new String[n];
				for (int i = 0; i < args.length; i++) {
					args[i] = rawNameOf(context, argParams.get(i).asType());
				}

				final boolean scope = n > 0 && args[0].contains("IScope");
				final Setter child = new Setter();
				child.method = ex.getSimpleName().toString();
				final boolean isDynamic = !scope && n == 2 || scope && n == 3;
				child.param_class = isDynamic ? args[!scope ? 1 : 2] : args[!scope ? 0 : 1];
				child.dynamic = isDynamic;
				child.scope = scope;
				var.setter = child;
				break;
			}
		}
	}

	public void addGetter(final ProcessorContext context, final Element e, final boolean isField, final String varName,
			final Var var) {
		for (final Element m : e.getEnclosedElements()) {
			final getter getter = m.getAnnotation(getter.class);
			if (getter != null && getter.value().equals(varName)) {
				final ExecutableElement ex = (ExecutableElement) m;
				final List<? extends VariableElement> argParams = ex.getParameters();
				final String[] args = new String[argParams.size()];
				for (int i = 0; i < args.length; i++) {
					args[i] = rawNameOf(context, argParams.get(i).asType());
				}
				final int n = args.length;
				final boolean scope = n > 0 && args[0].contains("IScope");

				final Getter child = new Getter();

				child.method = ex.getSimpleName().toString();
				child.returns = rawNameOf(context, ex.getReturnType());
				child.dynamic = !scope && n > 0 || scope && n > 1;
				child.isField = isField;
				child.scope = scope;
				child.initializer = getter.initializer();
				var.getter = child;
				break;
			}
		}
	}

	@Override
	protected Class<vars> getAnnotationClass() {
		return vars.class;
	}

	protected StringBuilder toOwnArrayOfStrings(final String array, final StringBuilder sb) {
		if (array == null || array.equals("")) {
			sb.append("AS");
			return sb;
		}
		// FIX AD 3/4/13: split(regex) would not include empty trailing strings
		final String[] segments = array.split("\\,", -1);
		sb.append("S(");
		for (final String segment : segments) {
			if (segment.isEmpty()) {
				sb.append("(String)null");
			} else {
				final int i = ss1.indexOf(segment);
				if (i == -1) {
					sb.append("\"").append(segment.replace("COMMA", ",").replace("\"", "\\\"")).append("\"");
				} else {
					sb.append(ss2.get(i));
				}
			}
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(')');
		return sb;
	}

}
