/*********************************************************************************************
 * 
 * 
 * 'GamlFormatter.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.formatting;

import java.util.*;
import msi.gama.lang.gaml.services.GamlGrammarAccess;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.impl.*;

/**
 * This class contains custom formatting description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#formatting
 * on how and when to use it
 * 
 * Also see {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an example
 */
public class GamlFormatter extends AbstractDeclarativeFormatter {

	/**
	 * @see org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter#configureFormatting(org.eclipse.xtext.formatting.impl.FormattingConfig)
	 */

	@Override
	protected void configureFormatting(final FormattingConfig c) {
		GamlGrammarAccess g = getGrammarAccess();

		// c.setLinewrap(0, 1, 2).before(g.getSL_COMMENTRule());
		c.setLinewrap(1).around(g.getStatementRule());
		c.setLinewrap(2).after(g.getModelAccess().getNameAssignment_1());
		c.setLinewrap(1).after(g.getImportRule());

		Set<Keyword> handled = new HashSet();
		handled.add(g.getTypeInfoAccess().getLessThanSignKeyword_0());
		handled.add(g.getTypeInfoAccess().getGreaterThanSignKeyword_3());
		handled.add(g.getUnaryAccess().getOpHyphenMinusKeyword_1_1_1_0_0_0());

		// Operators are surrounded by a space
		for ( Keyword k : g.findKeywords(">", "<", "=", "<<", ">>", "<-", "->", ">=", "<=", "+", "-", "/", "*") ) {
			if ( handled.contains(k) ) {
				continue;
			}
			c.setSpace(" ").around(k);
		}

		// No space after these elements
		for ( Keyword k : g.findKeywords(".", "[", "(", "::", "°", "!") ) {
			c.setNoSpace().after(k);
		}
		// No space before these ones
		for ( Keyword k : g.findKeywords("]", ".", ")", ",", ":", "::") ) {
			c.setNoSpace().before(k);
		}
		// One space after these ones
		for ( Keyword k : g.findKeywords(",", ":") ) {
			c.setSpace(" ").after(k);
		}

		// Parameters of operators/actions and access should be handled with no space before
		c.setNoSpace().before(g.getFunctionAccess().getLeftParenthesisKeyword_2());
		c.setNoSpace().before(g.getAccessAccess().getOpLeftSquareBracketKeyword_1_1_0_0_0());

		// The unary minus should be treated differently (no space after)
		c.setNoSpace().after(g.getUnaryAccess().getOpHyphenMinusKeyword_1_1_1_0_0_0());

		// The "<" and ">" part of the type references must alse be handled differently (no space)
		c.setNoSpace().after(g.getTypeInfoAccess().getLessThanSignKeyword_0());
		c.setNoSpace().before(g.getTypeInfoAccess().getLessThanSignKeyword_0());
		c.setNoSpace().before(g.getTypeInfoAccess().getGreaterThanSignKeyword_3());

		// And the ":" in ternary if should be surrounded by spaces
		c.setSpace(" ").before(g.getIfAccess().getColonKeyword_1_3_0());

		// Semicolons induce line separations
		for ( Keyword k : g.findKeywords(";") ) {
			c.setNoSpace().before(k);
			c.setLinewrap().after(k);
		}

		// Regular blocks
		handleBlock(c, g.getBlockAccess().getLeftCurlyBracketKeyword_1(), g.getBlockAccess()
			.getRightCurlyBracketKeyword_2_1_1(), 2);
		handleBlock(c, g.getDisplayBlockAccess().getLeftCurlyBracketKeyword_1(), g.getDisplayBlockAccess()
			.getRightCurlyBracketKeyword_3(), 2);
		handleBlock(c, g.getExperimentBlockAccess().getLeftCurlyBracketKeyword_1(), g.getExperimentBlockAccess()
			.getRightCurlyBracketKeyword_3(), 2);
		handleBlock(c, g.getOutputBlockAccess().getLeftCurlyBracketKeyword_1(), g.getOutputBlockAccess()
			.getRightCurlyBracketKeyword_3(), 2);
		handleBlock(c, g.getS_EquationsAccess().getLeftCurlyBracketKeyword_3_0_0(), g.getS_EquationsAccess()
			.getRightCurlyBracketKeyword_3_0_2(), 2);
		// Functions
		handleBlockTermination(c, g.getBlockAccess().getRightCurlyBracketKeyword_2_0_0_1(), 1);
		// Else blocks should not be separated from their if
		c.setNoLinewrap().before(g.getS_IfAccess().getElseKeyword_4_0());

		c.setAutoLinewrap(180);

		handleComments(c, g);

	}

	/**
	 * @param c
	 * @param opening and closing keywords
	 */
	private void handleBlock(final FormattingConfig c, final Keyword opening, final Keyword closing,
		final int lineWrapAfter) {
		// A same opening curly bracket is shared by blocks and functions, after which a ln is done
		handleBlockOpening(c, opening);
		handleBlockTermination(c, closing, lineWrapAfter);
	}

	private void handleBlockOpening(final FormattingConfig c, final Keyword opening) {
		c.setLinewrap().after(opening);
		c.setIndentationIncrement().after(opening);
	}

	private void handleBlockTermination(final FormattingConfig c, final Keyword closing, final int lineWrapAfter) {
		c.setLinewrap(lineWrapAfter).after(closing);
		c.setLinewrap(1).before(closing);
		c.setIndentationDecrement().before(closing);
	}

	@Override
	public GamlGrammarAccess getGrammarAccess() {
		return (GamlGrammarAccess) super.getGrammarAccess();
	}

	private void handleComments(final FormattingConfig c, final GamlGrammarAccess f) {
		c.setLinewrap(0, 1, 2).before(f.getSL_COMMENTRule());
		c.setLinewrap(0, 1, 2).before(f.getML_COMMENTRule());
		c.setLinewrap(0, 1, 1).after(f.getML_COMMENTRule());
	}

}
