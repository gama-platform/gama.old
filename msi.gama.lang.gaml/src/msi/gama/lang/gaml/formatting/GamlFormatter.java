/*******************************************************************************************************
 *
 * GamlFormatter.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.formatting;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;
import org.eclipse.xtext.parsetree.reconstr.ITokenStream;

import msi.gama.lang.gaml.services.GamlGrammarAccess;
import msi.gama.lang.gaml.services.GamlGrammarAccess.BlockElements;

/**
 * This class contains custom formatting description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#formatting on how and when to use it
 * 
 * Also see {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an example
 */
public class GamlFormatter extends AbstractDeclarativeFormatter {

	@Override
	public ITokenStream createFormatterStream(final EObject context, final String indent, final ITokenStream out,
			final boolean preserveWhitespaces) {
		if (context == null || !context.eResource().getErrors().isEmpty()) {
			// Fixes #2018
			return out;
		}
		return super.createFormatterStream(context, indent, out, preserveWhitespaces);
	}

	/** The keywords 1 space around. */
	static String[] keywords1SpaceAround =
			new String[] { ">", "<", "=", "<<", ">>", "<-", "->", ">=", "<=", "+", "-", "/", "*" };
	
	/** The keyword no space after. */
	static String[] keywordNoSpaceAfter = new String[] { ".", "[", "(", "::", "°", "#", "!", "{" };
	
	/** The keyword no space before. */
	static String[] keywordNoSpaceBefore = new String[] { "]", ".", ")", ",", ":", "::", "}" };
	
	/** The keyword 1 space after. */
	static String[] keyword1SpaceAfter = new String[] { ",", ":" };

	/**
	 * @see org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter#configureFormatting(org.eclipse.xtext.formatting.impl.FormattingConfig)
	 */

	@Override
	protected void configureFormatting(final FormattingConfig c) {
		final GamlGrammarAccess g = getGrammarAccess();

		c.setLinewrap(2).after(g.getModelAccess().getNameAssignment_2());
		c.setLinewrap(1).after(g.getImportRule());

		final Set<Keyword> handled = new HashSet<>();
		handled.add(g.getTypeInfoAccess().getLessThanSignKeyword_0());
		handled.add(g.getTypeInfoAccess().getGreaterThanSignKeyword_3());
		handled.add(g.getUnaryAccess().getOpHyphenMinusKeyword_1_1_1_0_0_0());

		// Operators are surrounded by a space
		for (final Keyword k : g.findKeywords(keywords1SpaceAround)) {
			if (!handled.contains(k)) {
				c.setSpace(" ").around(k);
			}
		}
		handled.add(g.getBlockAccess().getLeftCurlyBracketKeyword_1());
		handled.add(g.getDisplayBlockAccess().getLeftCurlyBracketKeyword_1());
		handled.add(g.getBlockAccess().getRightCurlyBracketKeyword_2_1());
		handled.add(g.getDisplayBlockAccess().getRightCurlyBracketKeyword_3());
		handled.add(g.getS_EquationsAccess().getLeftCurlyBracketKeyword_3_0_0());
		handled.add(g.getS_EquationsAccess().getRightCurlyBracketKeyword_3_0_2());
		// No space after these elements
		for (final Keyword k : g.findKeywords(keywordNoSpaceAfter)) {
			if (!handled.contains(k)) {
				c.setNoSpace().after(k);
			}
		}
		// No space before these ones
		for (final Keyword k : g.findKeywords(keywordNoSpaceBefore)) {
			if (!handled.contains(k)) {
				c.setNoSpace().before(k);
			}
		}
		// One space after these ones
		for (final Keyword k : g.findKeywords(keyword1SpaceAfter)) {
			c.setSpace(" ").after(k);
		}

		// Parameters of operators/actions and access should be handled with no
		// space before
		c.setNoSpace().before(g.getFunctionAccess().getLeftParenthesisKeyword_3());
		c.setNoSpace().before(g.getAccessAccess().getOpLeftSquareBracketKeyword_1_1_0_0_0());

		// The unary minus should be treated differently (no space after)
		c.setNoSpace().after(g.getUnaryAccess().getOpHyphenMinusKeyword_1_1_1_0_0_0());

		// The "<" and ">" part of the type references must alse be handled
		// differently (no space)
		c.setNoSpace().after(g.getTypeInfoAccess().getLessThanSignKeyword_0());
		c.setNoSpace().before(g.getTypeInfoAccess().getLessThanSignKeyword_0());
		c.setNoSpace().before(g.getTypeInfoAccess().getGreaterThanSignKeyword_3());

		// And the ":" in ternary if should be surrounded by spaces
		c.setSpace(" ").before(g.getIfAccess().getColonKeyword_1_3_0());

		// Semicolons induce line separations
		for (final Keyword k : g.findKeywords(";")) {
			c.setNoSpace().before(k);
			c.setLinewrap(1).after(k);
		}

		// Regular blocks
		final BlockElements elem = g.getBlockAccess();
		handleBlock(c, elem.getLeftCurlyBracketKeyword_1(), elem.getRightCurlyBracketKeyword_2_1(), 2);
		handleBlock(c, g.getDisplayBlockAccess().getLeftCurlyBracketKeyword_1(),
				g.getDisplayBlockAccess().getRightCurlyBracketKeyword_3(), 2);
		handleBlock(c, g.getS_EquationsAccess().getLeftCurlyBracketKeyword_3_0_0(),
				g.getS_EquationsAccess().getRightCurlyBracketKeyword_3_0_2(), 2);

		// Else blocks should not be separated from their if
		c.setNoLinewrap().before(g.getS_IfAccess().getElseKeyword_4_0());
		// Adding more space to init (supposing it is the first declared)
		c.setLinewrap(2).before(g.getS_ReflexRule());
		c.setLinewrap(2).before(g.getS_ActionRule());
		c.setLinewrap(2).before(g.getS_EquationsRule());
		c.setLinewrap(2).before(g.getS_ActionAccess().getKeyAssignment_1());
		c.setNoLinewrap().between(elem.getRightCurlyBracketKeyword_2_1(), elem.getRightCurlyBracketKeyword_2_1());
		c.setLinewrap(2).after(g.getS_SpeciesRule());
		c.setLinewrap(2).after(g.getS_ExperimentRule());
		c.setLinewrap(2).before(g.getS_GlobalRule());

		c.setAutoLinewrap(180);

		handleComments(c, g);

	}

	/**
	 * @param c
	 * @param opening
	 *            and closing keywords
	 */
	private void handleBlock(final FormattingConfig c, final Keyword opening, final Keyword closing,
			final int lineWrapAfter) {
		// A same opening curly bracket is shared by blocks and functions, after
		// which a ln is done
		handleBlockTermination(c, closing, lineWrapAfter);
		handleBlockOpening(c, opening);

	}

	/**
	 * Handle block opening.
	 *
	 * @param c the c
	 * @param opening the opening
	 */
	private void handleBlockOpening(final FormattingConfig c, final Keyword opening) {
		// c.setLinewrap().before(opening);
		c.setLinewrap().after(opening);
		c.setIndentationIncrement().after(opening);
	}

	/**
	 * Handle block termination.
	 *
	 * @param c the c
	 * @param closing the closing
	 * @param lineWrapAfter the line wrap after
	 */
	private void handleBlockTermination(final FormattingConfig c, final Keyword closing, final int lineWrapAfter) {

		// c.setSpace("\n/*closing*/").before(closing);
		c.setLinewrap(0, 0, 1).before(closing);
		c.setIndentationDecrement().before(closing);
		c.setLinewrap(lineWrapAfter).after(closing);
		// c.setSpace(" ").after(closing);
	}

	@Override
	public GamlGrammarAccess getGrammarAccess() {
		return (GamlGrammarAccess) super.getGrammarAccess();
	}

	/**
	 * Handle comments.
	 *
	 * @param c the c
	 * @param f the f
	 */
	private void handleComments(final FormattingConfig c, final GamlGrammarAccess f) {
		c.setLinewrap(0, 1, 2).before(f.getSL_COMMENTRule());
		c.setLinewrap(0, 1, 2).before(f.getML_COMMENTRule());
		c.setLinewrap(0, 1, 1).after(f.getML_COMMENTRule());
	}

}
