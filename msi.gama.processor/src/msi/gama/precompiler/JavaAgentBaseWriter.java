/*********************************************************************************************
 * 
 *
 * 'JavaAgentBaseWriter.java', in plugin 'msi.gama.processor', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.precompiler;

import java.util.Map;

public class JavaAgentBaseWriter extends JavaWriter {

	public String write(final String packageName, final GamlProperties props) {
		final StringBuilder sb = new StringBuilder();
		writeHeader(sb, packageName);

		for (final Map.Entry<String, String> entry : props.filterFirst(OPERATOR_PREFIX).entrySet()) {
			writeOperatorAddition(sb, entry.getKey(), entry.getValue());
		}
		// for ( Map.Entry<String, String> entry :
		// props.filterFirst(ACTION_PREFIX).entrySet() ) {
		// writeActionAddition(sb, entry.getKey(), entry.getValue());
		// }

		writeFooter(sb);
		return sb.toString();
	}

	@Override
	protected void writeOperatorAddition(final StringBuilder sb, final String s, final String helper) {
		boolean isUnary = true;
		final String[] segments = s.split("\\$");
		final String leftClass = segments[1];
		final String keyword = segments[0];
		// TEST
		if (keyword.length() < 4) {
			return;
		}
		// TEST
		String rightClass;
		if (segments[2].equals("")) {
			rightClass = "null";
		} else {
			rightClass = segments[2];
			isUnary = false;
		}
		final String returnClass = segments[8];

		if (isUnary) {
			sb.append(ln).append(tab).append(tab);
			sb.append("protected ").append(returnClass).append(" _op_").append(keyword).append("(final ")
					.append(leftClass).append(" target) {");
			sb.append(ln);
			sb.append(tab).append(tab).append(tab);
			sb.append("IScope scope = getScope();");
			sb.append(ln);
			sb.append(tab).append(tab).append(tab);
			sb.append(helper.substring(helper.lastIndexOf('{') + 1, helper.indexOf('}')));
			sb.append(ln);
			sb.append(tab).append(tab).append(tab);
			sb.append("}");
			sb.append(ln);
		} else {
			sb.append(ln).append(tab).append(tab);
			sb.append("protected ").append(returnClass).append(" _op_").append(keyword).append("(final ")
					.append(leftClass).append(" left, ").append(rightClass).append(" right) {");
			sb.append(ln);
			sb.append(tab).append(tab).append(tab);
			sb.append("IScope scope = getScope();");
			sb.append(ln);
			sb.append(tab).append(tab).append(tab);
			sb.append(helper.substring(helper.lastIndexOf('{') + 1, helper.indexOf('}')));
			sb.append(ln);
			sb.append(tab).append(tab).append(tab);
			sb.append("}");
			sb.append(ln);
		}
	}

	@Override
	protected void writeHeader(final StringBuilder sb, final String packageName) {
		super.writeHeader(sb, packageName);
		sb.append("}");
		sb.append(ln);
		sb.append("protected IScope getScope() {return null;}").append(ln);
	}

	@Override
	protected String classDefinition() {
		return "public class JavaBasedAgent extends GamlAgent";
	}

	@Override
	protected void writeFooter(final StringBuilder sb) {
		sb.append(ln);
		sb.append(tab).append('}');
	}

}
