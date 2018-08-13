/*********************************************************************************************
 *
 * 'GamlSyntaxErrorMessageProvider.java, in plugin msi.gama.lang.gaml, is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
import ummisco.gama.dev.utils.DEBUG;

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
		if (DEBUG.IS_ON()) {
			DEBUG.OUT("======");
		}
		if (contextobj != null) {
			if (DEBUG.IS_ON()) {
				DEBUG.OUT("Current EObject: " + contextobj.eClass().getName() + " " + EGaml.getKeyOf(contextobj));
			}
		}
		final RecognitionException ex = context.getRecognitionException();

		String msg = context.getDefaultMessage();
		if (DEBUG.IS_ON()) {
			DEBUG.OUT("Default Message: " + msg);
		}
		final INode node = context.getCurrentNode();
		if (node != null) {

			if (DEBUG.IS_ON()) {
				DEBUG.OUT("Grammar Element: " + node.getGrammarElement());
			}
			if (DEBUG.IS_ON()) {
				DEBUG.OUT("Node text: " + NodeModelUtils.getTokenText(node));
			}
		}
		// final String[] tokens = context.getTokenNames();
		if (ex == null) {
			if (msg.startsWith("mismatched ch")) {
				final String ch = msg.substring(msg.length() - 3, msg.length());
				msg = "Character expected " + ch;
			} else {

				if (contextobj != null) {
					if (DEBUG.IS_ON()) {
						DEBUG.OUT("No exception but EObject present");
					}
					msg += "Error in expression '" + NodeModelUtils.getTokenText(NodeModelUtils.getNode(contextobj))
							+ "'";
				}
				if (node != null) {
					if (DEBUG.IS_ON()) {
						DEBUG.OUT("No exception but Node present");
					}
					msg += "Error in expression '" + NodeModelUtils.getTokenText(node) + "'";
				}
			}
		} else {
			final int positionInLine = ex.charPositionInLine;
			if (DEBUG.IS_ON()) {
				DEBUG.OUT("Position in line: " + positionInLine);
			}
			if (DEBUG.IS_ON()) {
				DEBUG.OUT("Exception :" + ex);
			}
			msg = "";
			final Token t = ex.token;
			if (t != null) {
				final String token = t.getText();
				if (DEBUG.IS_ON()) {
					DEBUG.OUT("Token text: " + token);
				}
				if (token != null) {
					if (token.length() == 1) {
						final char c = token.charAt(0);
						switch (c) {
							case ';':
								msg += "Unexpected line termination character ' " + token + " '";
								break;
							case '{':
							case '}':
								msg += "Block definition does not begin or end correctly";
								break;
							case '[':
							case ']':
								msg += "List definition does not begin or end correctly";
								break;
							case '(':
							case ')':
								msg += "Parenthesized expression do not begin or end correctly";
								break;
							default:
								msg += "Unwanted or misplaced character ' " + token + " '";
						}

					} else {
						String text;
						if (contextobj != null) {
							text = EGaml.getKeyOf(contextobj);
							if (text == null) {
								text = token;
							}
							msg += "Symbol '" + text + "' seems to be incomplete";

						} else {
							switch (token) {
								case IKeyword.ENVIRONMENT:
									msg += "'environment' cannot be declared anymore.Its bounds should be declared in the global section as the value of the 'shape' attribute (since GAMA 1.7)";
									break;
								case IKeyword.ENTITIES:
									msg += "'entities' cannot be declared anymore. The species it contains should be declared in the model or in the global section (since GAMA 1.6)";
									break;
								default:
									msg += "Unexpected symbol '" + token + "'";
							}

						}
					}
				}
			}
		}
		if (DEBUG.IS_ON()) {
			DEBUG.OUT("Message : " + msg);
		}

		return new SyntaxErrorMessage(msg, Diagnostic.SYNTAX_DIAGNOSTIC);
	}

}