package msi.gama.precompiler;

import static msi.gama.precompiler.java.JavaWriter.SEP;
import static msi.gama.precompiler.java.JavaWriter.VAR_PREFIX;

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

public class VarsProcessor implements IProcessor<vars> {

	@Override
	public void process(final ProcessorContext environment) {

		final List<? extends Element> elements = environment.sortElements(vars.class);
		for (final Element e : elements) {
			boolean isField;
			final TypeMirror clazz = e.asType();
			isField = !environment.getTypeUtils().isAssignable(clazz, environment.getISkill())
					&& !environment.getTypeUtils().isAssignable(clazz, environment.getIAgent());

			final vars vars = e.getAnnotation(vars.class);
			final Set<String> undocumented = new HashSet<>();
			for (final var s : vars.value()) {
				final doc[] docs = s.doc();
				if (docs.length == 0 && !s.internal()) {
					undocumented.add(s.name());
				}
				final StringBuilder sb = new StringBuilder();
				final int type = s.type();
				final int contentType = s.of();
				// 0.type
				sb.append(VAR_PREFIX).append(type).append(SEP);
				// 1.contentType
				sb.append(contentType).append(SEP);
				// 2. keyType
				sb.append(s.index()).append(SEP);
				final String varName = s.name();
				// 3.var name
				sb.append(varName).append(SEP);
				// 4. class of declaration
				sb.append(environment.rawNameOf(e)).append(SEP);
				// 5. facets
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
					sb.append(',').append("init").append(',').append(environment.replaceCommas(init));
				}
				boolean found = false;
				sb.append(SEP);
				for (final Element m : e.getEnclosedElements()) {
					final getter getter = m.getAnnotation(getter.class);
					if (getter != null && getter.value().equals(varName)) {
						final ExecutableElement ex = (ExecutableElement) m;
						final List<? extends VariableElement> argParams = ex.getParameters();
						final String[] args = new String[argParams.size()];
						for (int i = 0; i < args.length; i++) {
							args[i] = environment.rawNameOf(argParams.get(i));
						}
						final int n = args.length;
						final boolean scope = n > 0 && args[0].contains("IScope");

						// method
						sb.append(ex.getSimpleName()).append(SEP);
						// retClass
						sb.append(environment.rawNameOf(ex.getReturnType())).append(SEP);
						// dynamic ?
						sb.append(!scope && n > 0 || scope && n > 1).append(SEP);
						// field ?
						sb.append(isField).append(SEP);
						// scope ?
						sb.append(scope);
						sb.append(SEP).append(getter.initializer());
						found = true;
						break;
					}
				}
				if (!found) {
					sb.append("null");
				}
				found = false;
				sb.append(SEP);
				for (final Element m : e.getEnclosedElements()) {
					final setter setter = m.getAnnotation(setter.class);
					if (setter != null && setter.value().equals(varName)) {
						final ExecutableElement ex = (ExecutableElement) m;
						final List<? extends VariableElement> argParams = ex.getParameters();
						final String[] args = new String[argParams.size()];
						for (int i = 0; i < args.length; i++) {
							args[i] = environment.rawNameOf(argParams.get(i));
						}
						final int n = args.length;
						final boolean scope = n > 0 && args[0].contains("IScope");
						// method
						sb.append(ex.getSimpleName()).append(SEP);
						// paramClass
						final boolean isDynamic = !scope && n == 2 || scope && n == 3;
						sb.append(isDynamic ? args[!scope ? 1 : 2] : args[!scope ? 0 : 1]).append(SEP);
						// isDynamic
						sb.append(isDynamic).append(SEP);
						// scope ?
						sb.append(scope);
						found = true;
						break;
					}
				}
				if (!found) {
					sb.append("null");
				}
				sb.append(SEP);
				String d;
				if (docs.length == 0) {
					d = "";
				} else {
					d = docs[0].value();
				}
				environment.getProperties().put(sb.toString(), d);
			}
			if (!undocumented.isEmpty()) {
				environment.emitWarning("GAML: vars '" + undocumented + "' are not documented", e);
			}
		}

	}

}
