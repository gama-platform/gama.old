package msi.gama.precompiler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;

public class VarsProcessor extends ElementProcessor<vars> {

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final Document doc, final vars vars,
			final org.w3c.dom.Element node) {

		boolean isField;
		final TypeMirror clazz = e.asType();
		isField = !context.getTypeUtils().isAssignable(clazz, context.getISkill())
				&& !context.getTypeUtils().isAssignable(clazz, context.getIAgent());

		final Set<String> undocumented = new HashSet<>();
		for (final var s : vars.value()) {
			final org.w3c.dom.Element child = doc.createElement("var");
			final doc[] docs = s.doc();
			if (docs.length == 0 && !s.internal()) {
				undocumented.add(s.name());
			}
			child.setAttribute("type", String.valueOf(s.type()));
			final int type = s.type();
			child.setAttribute("contents", String.valueOf(s.of()));
			final int contentType = s.of();
			child.setAttribute("index", String.valueOf(s.index()));
			child.setAttribute("name", s.name());
			final String varName = s.name();
			child.setAttribute("class", rawNameOf(context, e));
			if (s.constant())
				child.setAttribute("const", "true");
			if (s.depends_on().length > 0)
				child.setAttribute("depends_on", arrayToString(s.depends_on()));
			if (s.init().length() > 0)
				child.setAttribute("init", s.init());

			/**
			 * 
			 */

			final StringBuilder sb = new StringBuilder();
			sb.append("type").append(',').append(type).append(',');
			sb.append("name").append(',').append(varName).append(',');
			sb.append("const").append(',').append(s.constant() ? "true" : "false");

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
			if (contentType != 0) {
				sb.append(',').append("of").append(',').append(contentType);
			}
			final String init = s.init();
			if (!"".equals(init)) {
				sb.append(',').append("init").append(',').append(replaceCommas(init));
			}
			child.setAttribute("facets", sb.toString());

			addGetter(context, e, isField, s.name(), child, doc);
			addSetter(context, e, isField, s.name(), child, doc);

			String d;
			if (docs.length == 0) {
				d = "";
			} else {
				d = docs[0].value();
				child.setAttribute("doc", d);
			}
			appendChild(node, child);
		}
		if (!undocumented.isEmpty()) {
			context.emitWarning("GAML: vars '" + undocumented + "' are not documented", e);
		}

	}

	private void addSetter(final ProcessorContext context, final Element e, final boolean isField, final String name,
			final org.w3c.dom.Element node, final Document doc) {
		for (final Element m : e.getEnclosedElements()) {
			final setter setter = m.getAnnotation(setter.class);
			if (setter != null && setter.value().equals(name)) {
				final ExecutableElement ex = (ExecutableElement) m;
				final List<? extends VariableElement> argParams = ex.getParameters();
				final String[] args = new String[argParams.size()];
				for (int i = 0; i < args.length; i++) {
					args[i] = rawNameOf(context, argParams.get(i));
				}
				final int n = args.length;
				final boolean scope = n > 0 && args[0].contains("IScope");
				final org.w3c.dom.Element child = doc.createElement("setter");
				child.setAttribute("method", ex.getSimpleName().toString());
				final boolean isDynamic = !scope && n == 2 || scope && n == 3;
				child.setAttribute("param_class", isDynamic ? args[!scope ? 1 : 2] : args[!scope ? 0 : 1]);
				if (isDynamic)
					child.setAttribute("dynamic", String.valueOf(isDynamic));
				if (scope)
					child.setAttribute("scope", "true");
				appendChild(node, child); // method
				break;
			}
		}
	}

	public void addGetter(final ProcessorContext context, final Element e, final boolean isField, final String varName,
			final org.w3c.dom.Element node, final Document doc) {
		for (final Element m : e.getEnclosedElements()) {
			final getter getter = m.getAnnotation(getter.class);
			if (getter != null && getter.value().equals(varName)) {
				final ExecutableElement ex = (ExecutableElement) m;
				final List<? extends VariableElement> argParams = ex.getParameters();
				final String[] args = new String[argParams.size()];
				for (int i = 0; i < args.length; i++) {
					args[i] = rawNameOf(context, argParams.get(i));
				}
				final int n = args.length;
				final boolean scope = n > 0 && args[0].contains("IScope");

				final org.w3c.dom.Element child = doc.createElement("getter");

				child.setAttribute("method", ex.getSimpleName().toString());
				child.setAttribute("returns", rawNameOf(context, ex.getReturnType()));
				child.setAttribute("dynamic", String.valueOf(!scope && n > 0 || scope && n > 1));
				if (isField)
					child.setAttribute("field", "true");
				if (scope)
					child.setAttribute("scope", "true");
				if (getter.initializer())
					child.setAttribute("initializer", "true");
				appendChild(node, child); // method
				break;
			}
		}
	}

	@Override
	protected Class<vars> getAnnotationClass() {
		return vars.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element vars) {

		final NodeList list = vars.getElementsByTagName("var");
		for (int i = 0; i < list.getLength(); i++) {
			final org.w3c.dom.Element node = (org.w3c.dom.Element) list.item(i);
			final String type = toType(node.getAttribute("type"));
			final String contentType = toType(node.getAttribute("contents"));
			final String keyType = toType(node.getAttribute("index"));
			final String name = toJavaString(node.getAttribute("name"));
			final String clazz = node.getAttribute("class");
			// final String facets = segments[5];
			String getterHelper = null;
			String initerHelper = null;
			String setterHelper = null;
			boolean isField = false;
			// getter
			final org.w3c.dom.Element getter = findFirstChildNamed(node, "getter");
			if (getter != null) {
				final String getterName = getter.getAttribute("method");

				final String ret = checkPrim(getter.getAttribute("returns"));
				final boolean dynamic = getter.getAttribute("dynamic").equals("true");
				isField = getter.getAttribute("field").equals("true");
				final boolean scope = getter.getAttribute("scope").equals("true");

				if (isField) {
					getterHelper = concat("new GamaHelper(){", OVERRIDE, "public ", ret, " run(", ISCOPE, " scope, ",
							OBJECT, "... v){return (v==null||v.length==0)?", returnWhenNull(ret), ":((", clazz,
							") v[0]).", getterName, scope ? "(scope);}}" : "();}}");
				} else {
					getterHelper = concat("new GamaHelper(", toClassObject(clazz), "){", OVERRIDE, "public ", ret,
							" run(", ISCOPE, " scope, ", IAGENT, " a, ", ISUPPORT,
							" t, Object... v) {return t == null?", returnWhenNull(ret), ":((", clazz, ")t).",
							getterName, "(", scope ? "scope" : "", dynamic ? (scope ? "," : "") + "a);}}" : ");}}");
				}

				// initer
				final boolean init = getter.getAttribute("initializer").equals("true");
				if (init) {
					initerHelper = getterHelper;
				}

			}

			// setter
			final org.w3c.dom.Element setter = findFirstChildNamed(node, "setter");
			if (setter != null) {
				final String setterName = setter.getAttribute("method");

				final String param = checkPrim(setter.getAttribute("param_class"));
				final boolean dyn = setter.getAttribute("dynamic").equals("true");
				final boolean scope = setter.getAttribute("scope").equals("true");
				setterHelper = concat("new GamaHelper(", toClassObject(clazz), ")", "{", OVERRIDE, "public Object ",
						" run(", ISCOPE, " scope, ", IAGENT, " a, ", ISUPPORT, " t, Object... arg)",
						" {if (t != null) ((", clazz, ") t).", setterName, "(", scope ? "scope," : "", dyn ? "a, " : "",
						"(" + param + ") arg[0]); return null; }}");

			}
			sb.append(in).append(isField ? "_field(" : "_var(").append(toClassObject(clazz)).append(",")
					.append(toJavaString(escapeDoubleQuotes(node.getAttribute("doc")))).append(",");
			if (isField) {
				sb.append("new OperatorProto(").append(name).append(", null, ").append(getterHelper)
						.append(", false, true, ").append(type).append(",").append(toClassObject(clazz))
						.append(", false, ").append(type).append(",").append(contentType).append(",").append(keyType)
						.append(",").append("AI").append(")");
			} else {
				sb.append("desc(").append(type).append(",").append(toOwnArrayOfStrings(node.getAttribute("facets")))
						.append("),").append(getterHelper).append(',').append(initerHelper).append(',')
						.append(setterHelper);
			}
			sb.append(");");
		}

	}

	protected String toOwnArrayOfStrings(final String array) {
		if (array == null || array.equals("")) { return "AS"; }
		// FIX AD 3/4/13: split(regex) would not include empty trailing strings
		final String[] segments = array.split("\\,", -1);
		String result = "S(";
		for (int i = 0; i < segments.length; i++) {
			if (i > 0) {
				result += ",";
			}
			result += toOwnJavaString(segments[i]);
		}
		result += ")";
		return result;
	}

	protected String toOwnJavaString(final String s) {
		if (s == null || s.isEmpty()) { return "(String)null"; }
		final int i = ss1.indexOf(s);
		return i == -1 ? "\"" + ownReplaceCommas(s) + "\"" : ss2.get(i);
	}

	private String ownReplaceCommas(final String s) {
		String result = s.replace("COMMA", ",");
		result = result.replace("\"", "\\\"");
		return result;
	}

}
