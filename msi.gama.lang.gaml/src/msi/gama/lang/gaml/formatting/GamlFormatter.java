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
		for ( Keyword k : g.findKeywords(".", "[", "(", "::", "�", "!") ) {
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

		// A same opening curly bracket is shared by blocks and functions, after which a ln is done
		c.setLinewrap().after(g.getBlockAccess().getLeftCurlyBracketKeyword_1());
		c.setIndentationIncrement().after(g.getBlockAccess().getLeftCurlyBracketKeyword_1());
		// Regular blocks
		c.setLinewrap(2).after(g.getBlockAccess().getRightCurlyBracketKeyword_2_1_1());
		c.setLinewrap(1).before(g.getBlockAccess().getRightCurlyBracketKeyword_2_1_1());
		c.setIndentationDecrement().before(g.getBlockAccess().getRightCurlyBracketKeyword_2_1_1());
		// Functions
		c.setLinewrap(1).after(g.getBlockAccess().getRightCurlyBracketKeyword_2_0_0_1());
		c.setLinewrap(1).before(g.getBlockAccess().getRightCurlyBracketKeyword_2_0_0_1());
		c.setIndentationDecrement().before(g.getBlockAccess().getRightCurlyBracketKeyword_2_0_0_1());
		// Equation blocks are defined differently (of course ! It would be too simple...)
		c.setLinewrap().after(g.getS_EquationsAccess().getLeftCurlyBracketKeyword_3_0_0());
		c.setIndentationIncrement().after(g.getS_EquationsAccess().getLeftCurlyBracketKeyword_3_0_0());
		c.setLinewrap(2).after(g.getS_EquationsAccess().getRightCurlyBracketKeyword_3_0_2());
		c.setLinewrap(1).before(g.getS_EquationsAccess().getRightCurlyBracketKeyword_3_0_2());
		c.setIndentationDecrement().before(g.getS_EquationsAccess().getRightCurlyBracketKeyword_3_0_2());
		// Else blocks should not be separated from their if
		c.setNoLinewrap().before(g.getS_IfAccess().getElseKeyword_4_0());

		c.setAutoLinewrap(180);

		handleComments(c, g);

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
