/*******************************************************************************************************
 *
 * ListenerProcessor.java, in msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import msi.gama.precompiler.GamlAnnotations.listener;

/**
 * The Class ListenerProcessor.
 */
public class ListenerProcessor extends ElementProcessor<listener> {

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final listener node) {

		final String clazz = rawNameOf(context, e.getEnclosingElement().asType());
		final String clazzObject = toClassObject(clazz);
		ExecutableElement ex = (ExecutableElement) e;
		final List<? extends VariableElement> argParams = ex.getParameters();
		final int n = argParams.size();
		if (n == 0) {
			context.emitError(
					"listeners must declare at least one argument corresponding to the new value of the variable (or 2 if the scope is passed)",
					ex);
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

		String listenerHelper = concat("(s,a,t,v)->{if (t != null) ((", clazz, ") t).", method, "(", scope ? "s," : "",
				isDynamic ? "a, " : "", "(" + param_class + ") v); return null; }");

		sb.append("_listener(").append(toJavaString(node.value())).append(',').append(clazzObject).append(',')
				.append(listenerHelper).append(");");

	}

	@Override
	protected Class<listener> getAnnotationClass() {
		return listener.class;
	}

}
