/*********************************************************************************************
 *
 * 'JavaWriter.java, in plugin msi.gama.processor, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.precompiler.java;

import msi.gama.precompiler.ProcessorContext;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class JavaWriter implements Constants {

	protected void writeHeader(final StringBuilder sb) {
		sb.append("package ").append(PACKAGE_NAME).append(';');
		for (final String element : IMPORTS) {
			sb.append(ln).append("import ").append(element).append(".*;");
		}
		for (final String element : EXPLICIT_IMPORTS) {
			sb.append(ln).append("import ").append(element).append(";");
		}
		sb.append(ln).append("import static msi.gaml.operators.Cast.*;");
		sb.append(ln).append("import static msi.gaml.operators.Spatial.*;");
		sb.append(ln).append("import static msi.gama.common.interfaces.IKeyword.*;");
		sb.append(ln).append("	@SuppressWarnings({ \"rawtypes\", \"unchecked\" })");
		sb.append(ln).append(ln).append("public class GamlAdditions extends AbstractGamlAdditions").append(" {");
		sb.append(ln).append(tab);
		sb.append("public void initialize() throws SecurityException, NoSuchMethodException {");
		processors.values().forEach(p -> {
			final String method = p.getInitializationMethodName();
			if (method != null)
				sb.append(ln).append(tab).append(method).append("();");
		});

		sb.append(ln).append('}');
	}

	public String write(final ProcessorContext context, final StringBuilder sb) {
		writeHeader(sb);

		processors.values().forEach(p -> {
			final String method = p.getInitializationMethodName();
			if (method != null) {
				sb.append("public void " + method + "() " + p.getExceptions() + " {");
				p.writeTo(context, sb);
				sb.append(ln);
				sb.append("}");
			}
		});

		writeFooter(sb);
		return sb.toString();
	}

	protected void writeFooter(final StringBuilder sb) {
		sb.append(ln).append('}');
	}

}
