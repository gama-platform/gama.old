/*******************************************************************************************************
 *
 * VarsProcessor.java, in msi.gama.processor, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.precompiler;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;

/**
 * The Class VarsProcessor.
 */
public class VarsProcessor extends ElementProcessor<vars> {

	/** The setters. */
	Map<Element, Map<String, ExecutableElement>> setters = new HashMap<>();

	/** The getters. */
	Map<Element, Map<String, ExecutableElement>> getters = new HashMap<>();

	/** The temp. */
	Set<String> temp = new HashSet<>();

	@Override
	public void process(final ProcessorContext context) {
		buildSettersAndGetters(context);
		super.process(context);
	}

	/**
	 * Builds the setters and getters.
	 *
	 * @param context
	 *            the context
	 */
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
		// If the declaring class has nothing to do with IAgent or ISkill, then the variable is considered as a 'field'
		final boolean isField = !context.getTypeUtils().isAssignable(typeClass, context.getIVarAndActionSupport());

		for (final variable node : vars.value()) {
			String vName = node.name();
			if (temp.contains(vName)) {
				context.emitError("Attribute '" + vName + " is declared twice", e);
			} else {
				temp.add(vName);
			}
			verifyDoc(context, e, "attribute " + node.name(), node);
			final String clazz = rawNameOf(context, e.asType());
			final String clazzObject = toClassObject(clazz);

			sb.append(in).append(isField ? "_field(" : "_var(").append(clazzObject).append(",");
			if (isField) {
				sb.append(toJavaString(node.name())).append(',');
				writeHelpers(sb, context, node, clazz, e, isField, true);
				sb.append(',').append(node.type()).append(',').append(clazzObject).append(',').append(node.type())
						.append(',').append(node.of()).append(',').append(node.index());
			} else {
				sb.append("desc(").append(node.type()).append(',');
				writeFacets(sb, node);
				sb.append("),");
				writeHelpers(sb, context, node, clazz, e, isField, false);
			}

			sb.append(");");
		}
		temp.clear();
	}

	/**
	 * Write helpers.
	 *
	 * @param sb
	 *            the sb
	 * @param context
	 *            the context
	 * @param var
	 *            the var
	 * @param clazz
	 *            the clazz
	 * @param e
	 *            the e
	 * @param isField
	 *            the is field
	 * @param onlyGetter
	 *            the only getter
	 */
	private void writeHelpers(final StringBuilder sb, final ProcessorContext context, final variable var,
			final String clazz, final Element e, final boolean isField, final boolean onlyGetter) {
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
								"setters must declare at least one argument corresponding to the value of the variable (or 2 if the scope is passed)",
								ex);
						return;
					}
					final String[] args = new String[n];
					for (int i = 0; i < args.length; i++) { args[i] = rawNameOf(context, argParams.get(i).asType()); }

					final boolean scope = n > 0 && args[0].contains("IScope");
					final String method = ex.getSimpleName().toString();
					final boolean isDynamic = scope ? n == 3 : n == 2;
					final String param_class = checkPrim(isDynamic ? args[!scope ? 1 : 2] : args[!scope ? 0 : 1]);

					setterHelper = concat("(s,a,t,v)->{if (t != null) ((", clazz, ") t).", method, "(",
							scope ? "s," : "", isDynamic ? "a, " : "", "(" + param_class + ") v); return null; }");
				}
			}
		}

		final Map<String, ExecutableElement> elements = getters.get(e);
		if (elements != null) {
			final ExecutableElement ex = elements.get(name);
			if (ex != null) {
				final List<? extends VariableElement> argParams = ex.getParameters();
				final String[] args = new String[argParams.size()];
				for (int i = 0; i < args.length; i++) { args[i] = rawNameOf(context, argParams.get(i).asType()); }
				final int n = args.length;
				final boolean scope = n > 0 && args[0].contains("IScope");
				final String method = ex.getSimpleName().toString();
				final String returns = rawNameOf(context, ex.getReturnType());
				final boolean dynamic = scope ? n > 1 : n > 0;

				if (isField) {
					// AD: REMOVE THE DEFAULT BEHAVIOR WHEN NULL IS PASSED (which was wrong, see #2713)
					// getterHelper = concat("(s, v)->(v==null||v.length==0)?", returnWhenNull(checkPrim(returns)),
					// ":((", clazz, ")v[0]).", method, scope ? "(s)" : "()");
					getterHelper = concat("(s, o)->((", clazz, ")o[0]).", method, scope ? "(s)" : "()");
				} else {
					getterHelper = concat("(s,a,t,v)->t==null?", returnWhenNull(checkPrim(returns)), ":((", clazz,
							")t).", method, "(", scope ? "s" : "", dynamic ? (scope ? "," : "") + "a)" : ")");
				}
				if (ex.getAnnotation(getter.class).initializer()) { initerHelper = getterHelper; }
			}
		}

		if (onlyGetter) {
			sb.append(getterHelper);
		} else {
			sb.append(getterHelper).append(',').append(initerHelper).append(',').append(setterHelper);
		}
	}

	/**
	 * Write facets.
	 *
	 * @param sb
	 *            the sb
	 * @param s
	 *            the s
	 */
	private void writeFacets(final StringBuilder sb, final variable s) {
		sb.append("S(\"type\"").append(',').append(toJavaString(String.valueOf(s.type()))).append(',')
				.append("\"name\"").append(',').append(toJavaString(s.name()));
		if (s.constant()) {
			sb.append(',').append("\"const\"").append(',').append(toJavaString(String.valueOf(s.constant())));
		}

		final String[] dependencies = s.depends_on();
		if (dependencies.length > 0) {
			StringBuilder depends = new StringBuilder("[");
			for (int i = 0; i < dependencies.length; i++) {
				final String string = dependencies[i];
				depends.append(string);
				if (i < dependencies.length - 1) { depends.append(","); }
			}
			depends.append("]");
			sb.append(',').append("\"depends_on\"").append(',').append(toJavaString(depends.toString()));
		}
		if (s.of() != 0) { sb.append(',').append("\"of\"").append(',').append(toJavaString(String.valueOf(s.of()))); }
		final String init = s.init();
		if (!"".equals(init)) { sb.append(',').append("\"init\"").append(',').append(toJavaString(init)); }
		sb.append(')');
	}

	@Override
	protected Class<vars> getAnnotationClass() { return vars.class; }

}
