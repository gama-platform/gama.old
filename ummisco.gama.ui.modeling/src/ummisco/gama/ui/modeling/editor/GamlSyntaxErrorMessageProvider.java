/*********************************************************************************************
 * 
 *
 * 'GamlSyntaxErrorMessageProvider.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.ui.modeling.editor;

import org.antlr.runtime.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.parser.antlr.SyntaxErrorMessageProvider;

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
		EObject contextobj = context.getCurrentContext();
		RecognitionException ex = context.getRecognitionException();
		String msg = context.getDefaultMessage();
		if ( ex == null ) {
			if ( msg.endsWith("'_'") ) {
				msg = "Illegal identifier";
			} else if ( msg.startsWith("mismatched ch") ) {
				msg = "Identifier cannot end with '_'";
			}
		}
		if ( ex instanceof MissingTokenException ) {
			msg = msg.replaceFirst("RULE_ID at", "identifier before");
		}
		if ( ex instanceof MismatchedTokenException ) {
			// mismatched input 'xxx' expecting 'end'
			msg = msg.replaceFirst("mismatched input", "Not allowed: ");
		}
		if ( ex instanceof NoViableAltException ) {
			// no viable alternative at input 'xxx'
			Token t = ((NoViableAltException) ex).token;
			String s = t == null ? " this symbol " : t.getText();
			msg =
				msg.replaceFirst("no viable alternative at input", "Error at: ") +
					". Previous keyword may be out of place or the block introduced by '" + s +
					"' may not be correctly terminated.";
		}
		if ( contextobj == null ) {
			msg = "Incomplete model or species";
		}
		return new SyntaxErrorMessage(msg, Diagnostic.SYNTAX_DIAGNOSTIC);
	}

}