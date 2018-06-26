package msi.gama.precompiler;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;

public class VarsProcessor extends ElementProcessor<vars> {

	static final StringBuilder CONCAT = new StringBuilder();
	Map<Element, Map<String, ExecutableElement>> setters = new HashMap<>();
	Map<Element, Map<String, ExecutableElement>> getters = new HashMap<>();

	protected final static String concat(final String... array) {
		for (final String element : array) {
			CONCAT.append(element);
		}
		final String result = CONCAT.toString();
		CONCAT.setLength(0);
		return result;
	}

	@Override
	public void process(final ProcessorContext context) {
		buildSettersAndGetters(context);
		super.process(context);
	}

	private void buildSettersAndGetters(final ProcessorContext context) {
		setters =
				context.getElementsAnnotatedWith(setter.class).stream().collect(groupingBy(Element::getEnclosingElement,
						toMap(f -> f.getAnnotation(setter.class).value(), f -> (ExecutableElement) f)));
		getters =
				context.getElementsAnnotatedWith(getter.class).stream().collect(groupingBy(Element::getEnclosingElement,
						toMap(f -> f.getAnnotation(getter.class).value(), f -> (ExecutableElement) f)));
	}

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final vars vars) {
		final TypeMirror typeClass = e.asType();
		final boolean isField = !context.getTypeUtils().isAssignable(typeClass, context.getISkill())
				&& !context.getTypeUtils().isAssignable(typeClass, context.getIAgent());

		for (final var node : vars.value()) {
			final doc[] docs = node.doc();
			String d = "";
			if (docs.length == 0) {
				if (!node.internal()) {
					UNDOCUMENTED.add(node.name());
				}
			} else if (!isField) { // documentation of fields is not used
				d = docs[0].value();
			}
			final String clazz = rawNameOf(context, e.asType());
			final String clazzObject = toClassObject(clazz);

			sb.append(in).append(isField ? "_field(" : "_var(").append(clazzObject);
			if (!isField) {
				sb.append(",").append(toJavaString(escapeDoubleQuotes(d)));
			}
			sb.append(",");
			if (isField) {
				sb.append("new OperatorProto(").append(toJavaString(node.name())).append(",null,");
				writeHelpers(sb, context, node, clazz, e, isField, true);
				sb.append(",F,T,").append(node.type()).append(',').append(clazzObject).append(",F,").append(node.type())
						.append(",").append(node.of()).append(',').append(node.index()).append(',').append("AI)");
			} else {
				sb.append("desc(").append(node.type()).append(',');
				writeFacets(sb, node);
				sb.append("),");
				writeHelpers(sb, context, node, clazz, e, isField, false);
			}
			sb.append(");");
		}
		if (!UNDOCUMENTED.isEmpty()) {
			context.emitWarning("GAML: vars '" + UNDOCUMENTED + "' are not documented", e);
		}
		UNDOCUMENTED.clear();
	}

	private void writeHelpers(final StringBuilder sb, final ProcessorContext context, final var var, final String clazz,
			final Element e, final boolean isField, final boolean onlyGetter) {
		String getterHelper = null;
		String initerHelper = null;
		String setterHelper = null;
		final String name = var.name();
		if (!onlyGetter) {
			final Map<String, ExecutableElement> elements = setters.get(e);
			if (elements != null) {
				final ExecutableElement ex = elements.get(name);
				if (ex != null) {
					final List<? extends VariableElement> argParams = ex.getParameters();
					final int n = argParams.size();
					if (n == 0) {
						context.emitError(
								"GAML: Setters must declare at least one argument (or 2 if the scope is passed", ex);
						return;
					}
					final String[] args = new String[n];
					for (int i = 0; i < args.length; i++) {
						args[i] = rawNameOf(context, argParams.get(i).asType());
					}

					final boolean scope = n > 0 && args[0].contains("IScope");
					final String method = ex.getSimpleName().toString();
					final boolean isDynamic = !scope && n == 2 || scope && n == 3;
					final String param_class = checkPrim(isDynamic ? args[!scope ? 1 : 2] : args[!scope ? 0 : 1]);

					setterHelper = concat("new GamaHelper(", toClassObject(clazz), ",(s,a,t,v)->{if (t != null) ((",
							clazz, ") t).", method, "(", scope ? "s," : "", isDynamic ? "a, " : "",
							"(" + param_class + ") v[0]); return null; })");
				}
			}
		}

		final Map<String, ExecutableElement> elements = getters.get(e);
		if (elements != null) {
			final ExecutableElement ex = elements.get(name);
			if (ex != null) {
				final List<? extends VariableElement> argParams = ex.getParameters();
				final String[] args = new String[argParams.size()];
				for (int i = 0; i < args.length; i++) {
					args[i] = rawNameOf(context, argParams.get(i).asType());
				}
				final int n = args.length;
				final boolean scope = n > 0 && args[0].contains("IScope");
				final String method = ex.getSimpleName().toString();
				final String returns = rawNameOf(context, ex.getReturnType());
				final boolean dynamic = !scope && n > 0 || scope && n > 1;

				if (isField) {
					getterHelper = concat("(s, v)->(v==null||v.length==0)?", returnWhenNull(checkPrim(returns)), ":((",
							clazz, ")v[0]).", method, scope ? "(s)" : "()");
				} else {
					getterHelper = concat("new GamaHelper(", toClassObject(clazz), ",(s,a,t,v)->t==null?",
							returnWhenNull(checkPrim(returns)), ":((", clazz, ")t).", method, "(", scope ? "s" : "",
							dynamic ? (scope ? "," : "") + "a))" : "))");
				}
				if (ex.getAnnotation(getter.class).initializer()) {
					initerHelper = getterHelper;
				}
			}
		}

		if (onlyGetter) {
			sb.append(getterHelper);
		} else {
			sb.append(getterHelper).append(',').append(initerHelper).append(',').append(setterHelper);
		}
	}

	private void writeFacets(final StringBuilder sb, final var s) {
		sb.append("S(\"type\"").append(',').append(toJavaString(String.valueOf(s.type()))).append(',')
				.append("\"name\"").append(',').append(toJavaString(String.valueOf(s.name()))).append(',')
				.append("\"const\"").append(',').append(toJavaString(String.valueOf(s.constant())));

		final String[] dependencies = s.depends_on();
		if (dependencies.length > 0) {
			String depends = "[";
			for (int i = 0; i < dependencies.length; i++) {
				final String string = dependencies[i];
				depends += string;
				if (i < dependencies.length - 1) {
					depends += ",";
				}
			}
			depends += "]";
			sb.append(',').append("\"depends_on\"").append(',').append(toJavaString(depends));
		}
		if (s.of() != 0) {
			sb.append(',').append("\"of\"").append(',').append(toJavaString(String.valueOf(s.of())));
		}
		final String init = s.init();
		if (!"".equals(init)) {
			sb.append(',').append("\"init\"").append(',').append(toJavaString(init));
		}
		sb.append(')');
	}

	@Override
	protected Class<vars> getAnnotationClass() {
		return vars.class;
	}

}
