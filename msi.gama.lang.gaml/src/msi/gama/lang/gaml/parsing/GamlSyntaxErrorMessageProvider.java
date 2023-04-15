/*******************************************************************************************************
 *
 * GamlSyntaxErrorMessageProvider.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.parsing;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parser.antlr.SyntaxErrorMessageProvider;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.EGaml;

/**
 * The class GamlSyntaxErrorMessageProvider.
 *
 * @author drogoul
 * @since 15 sept. 2013
 *
 */
public class GamlSyntaxErrorMessageProvider extends SyntaxErrorMessageProvider {

	@Override
	public SyntaxErrorMessage getSyntaxErrorMessage(final IParserErrorContext context) {
		final EObject contextobj = context.getCurrentContext();
		final RecognitionException ex = context.getRecognitionException();

		StringBuilder msg = new StringBuilder();
		final INode node = context.getCurrentNode();
		if (ex == null) {
			msg.append(translateMessage(contextobj, context.getDefaultMessage(), node));
		} else {
			final Token t = ex.token;
			if (t != null) {
				final String token = t.getText();
				if (token != null) {
					if (token.length() == 1) {
						process1CharToken(msg, token);
					} else {
						String text;
						if (contextobj != null) {
							text = EGaml.getInstance().getKeyOf(contextobj);
							if (text == null) { text = token; }
							msg.append("Symbol '").append(text).append("' seems to be incomplete or misplaced");
						} else {
							switch (token) {
								case IKeyword.ENVIRONMENT:
									msg.append(
											"'environment' cannot be declared anymore.Its bounds should be declared in the global section as the value of the 'shape' attribute (since GAMA 1.7)");
									break;
								case IKeyword.ENTITIES:
									msg.append(
											"'entities' cannot be declared anymore. The species it contains should be declared in the model or in the global section (since GAMA 1.6)");
									break;
								default:
									msg.append("Unexpected symbol '").append(token).append("'");
							}

						}
					}
				} else {
					// cf. Issue #3003. Some errors do not have token text.
					msg.append(translateMessage(contextobj, context.getDefaultMessage(), node));
				}
			}
		}
		if (msg.isEmpty()) { msg.append(context.getDefaultMessage()); }
		return new SyntaxErrorMessage(msg.toString(), Diagnostic.SYNTAX_DIAGNOSTIC);
	}

	/**
	 * Process 1 char token.
	 *
	 * @param msg
	 *            the msg
	 * @param token
	 *            the token
	 */
	private void process1CharToken(final StringBuilder msg, final String token) {
		final char c = token.charAt(0);
		switch (c) {
			case ';':
				msg.append("Unexpected line termination character ' ").append(token).append(" '");
				break;
			case '{':
			case '}':
				msg.append("Block definition does not begin or end correctly");
				break;
			case '[':
			case ']':
				msg.append("List definition does not begin or end correctly");
				break;
			case '(':
			case ')':
				msg.append("Parenthesized expression do not begin or end correctly");
				break;
			default:
				msg.append("Unwanted or misplaced character ' ").append(token).append(" '");
		}
	}

	/**
	 * Translate message.
	 *
	 * @param contextobj
	 *            the contextobj
	 * @param message
	 *            the message
	 * @param node
	 *            the node
	 * @return the string
	 */
	private String translateMessage(final EObject contextobj, final String message, final INode node) {
		String msg = message;
		if (msg.startsWith("mismatched ch")) {
			final String ch = msg.substring(msg.lastIndexOf(' '));
			msg = "Character expected " + ch;
		} else if (msg.startsWith("mismatched in")) {
			msg = msg.replace("mismatched input", "Found");
		} else if (contextobj != null) {
			msg += "Error in expression '" + NodeModelUtils.getTokenText(NodeModelUtils.getNode(contextobj)) + "'";
		} else if (node != null) { msg += "Error in expression '" + NodeModelUtils.getTokenText(node) + "'"; }
		return msg;
	}

}