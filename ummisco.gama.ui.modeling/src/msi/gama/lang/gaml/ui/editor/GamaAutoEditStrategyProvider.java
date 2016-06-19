/**
 * Created by drogoul, 17 mars 2015
 * 
 */
package msi.gama.lang.gaml.ui.editor;

import msi.gama.common.*;
import msi.gama.lang.gaml.ui.XtextGui;
import org.eclipse.jface.text.IDocument;
import org.eclipse.xtext.ui.editor.autoedit.*;

/**
 * The class GamaAutoEditStrategyProvider.
 * 
 * @author drogoul
 * @since 17 mars 2015
 * 
 */

public class GamaAutoEditStrategyProvider extends DefaultAutoEditStrategyProvider {

	@Override
	protected void configureCompoundBracesBlocks(final IEditStrategyAcceptor acceptor) {
		CompoundMultiLineTerminalsEditStrategy s = null;
		if ( XtextGui.CORE_CLOSE_CURLY.getValue() ) {
			s = compoundMultiLineTerminals.newInstanceFor("{", "}");
		}
		if ( XtextGui.CORE_CLOSE_SQUARE.getValue() ) {
			if ( s == null ) {
				s = compoundMultiLineTerminals.newInstanceFor("[", "]");
			} else {
				s = s.and("[", "]");
			}
		}
		if ( XtextGui.CORE_CLOSE_PARENTHESES.getValue() ) {
			if ( s == null ) {
				s = compoundMultiLineTerminals.newInstanceFor("(", ")");
			} else {
				s = s.and("(", ")");
			}
		}

		if ( s != null ) {
			acceptor.accept(s, IDocument.DEFAULT_CONTENT_TYPE);
		}
	}

	@Override
	protected void configureCurlyBracesBlock(final IEditStrategyAcceptor acceptor) {
		if ( XtextGui.CORE_CLOSE_CURLY.getValue() ) {
			super.configureCurlyBracesBlock(acceptor);
		}
	}

	@Override
	protected void configureSquareBrackets(final IEditStrategyAcceptor acceptor) {
		if ( XtextGui.CORE_CLOSE_SQUARE.getValue() ) {
			super.configureSquareBrackets(acceptor);
		}
	}

	@Override
	protected void configureParenthesis(final IEditStrategyAcceptor acceptor) {
		if ( XtextGui.CORE_CLOSE_PARENTHESES.getValue() ) {
			super.configureParenthesis(acceptor);
		}
	}

}
