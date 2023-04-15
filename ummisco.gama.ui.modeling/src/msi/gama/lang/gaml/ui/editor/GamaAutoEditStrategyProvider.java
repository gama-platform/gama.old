/*******************************************************************************************************
 *
 * GamaAutoEditStrategyProvider.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.xtext.ui.editor.autoedit.CompoundMultiLineTerminalsEditStrategy;
import org.eclipse.xtext.ui.editor.autoedit.DefaultAutoEditStrategyProvider;
import org.eclipse.xtext.ui.editor.model.TerminalsTokenTypeToPartitionMapper;

import msi.gama.common.preferences.GamaPreferences;

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
		if (GamaPreferences.Modeling.CORE_CLOSE_CURLY.getValue()) {
			s = compoundMultiLineTerminals.newInstanceFor("{", "}");
		}
		if (GamaPreferences.Modeling.CORE_CLOSE_SQUARE.getValue()) {
			if (s == null) {
				s = compoundMultiLineTerminals.newInstanceFor("[", "]");
			} else {
				s = s.and("[", "]");
			}
		}
		if (GamaPreferences.Modeling.CORE_CLOSE_PARENTHESES.getValue()) {
			if (s == null) {
				s = compoundMultiLineTerminals.newInstanceFor("(", ")");
			} else {
				s = s.and("(", ")");
			}
		}

		if (s != null) { acceptor.accept(s, IDocument.DEFAULT_CONTENT_TYPE); }
	}

	@Override
	protected void configureStringLiteral(final IEditStrategyAcceptor acceptor) {
		if (GamaPreferences.Modeling.CORE_CLOSE_DOUBLE.getValue()) {
			acceptor.accept(partitionInsert.newInstance("\"", "\""), IDocument.DEFAULT_CONTENT_TYPE);
			acceptor.accept(partitionDeletion.newInstance("\"", "\""), IDocument.DEFAULT_CONTENT_TYPE);
		}
		if (GamaPreferences.Modeling.CORE_CLOSE_QUOTE.getValue()) {
			acceptor.accept(partitionInsert.newInstance("'", "'"), IDocument.DEFAULT_CONTENT_TYPE);
			acceptor.accept(partitionDeletion.newInstance("'", "'"), IDocument.DEFAULT_CONTENT_TYPE);
		}
		acceptor.accept(partitionEndSkippingEditStrategy.get(),
				TerminalsTokenTypeToPartitionMapper.STRING_LITERAL_PARTITION);
	}

	@Override
	protected void configureCurlyBracesBlock(final IEditStrategyAcceptor acceptor) {
		if (GamaPreferences.Modeling.CORE_CLOSE_CURLY.getValue()) { super.configureCurlyBracesBlock(acceptor); }
	}

	@Override
	protected void configureSquareBrackets(final IEditStrategyAcceptor acceptor) {
		if (GamaPreferences.Modeling.CORE_CLOSE_SQUARE.getValue()) { super.configureSquareBrackets(acceptor); }
	}

	@Override
	protected void configureParenthesis(final IEditStrategyAcceptor acceptor) {
		if (GamaPreferences.Modeling.CORE_CLOSE_PARENTHESES.getValue()) { super.configureParenthesis(acceptor); }
	}

}
