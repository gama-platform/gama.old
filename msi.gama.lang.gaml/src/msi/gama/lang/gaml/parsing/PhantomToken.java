/*******************************************************************************************************
 *
 * PhantomToken.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.parsing;

import org.antlr.runtime.CommonToken;

/**
 * This is intended to hold a phantom token, that it a token that is read by the parser but is not linked to any text in
 * the editor.
 *
 * PhantomTokens will typically be inserted into the token stream by a custom TokenSource. TokenSource lives between the
 * lexer and the parser. When the parser calls TokenSource.nextToken() then TokenSource will either call
 * Lexer.nextToken() on or insert a PhantomToken.
 *
 * A typical use-case for PhantomTokens is Python-like block delineation. Here the TokenSource will read the whitespace
 * and insert PhantomTokens into the token-stream to mark the beginning and end of blocks.
 *
 * This makes it possible to parse code where it would be difficult or impossible to define a suitable grammar
 * otherwise.
 *
 * It relies on returning an index range with zero length which feels to me like a hack because it could easily be
 * broken by possible changes in Xtext code.
 *
 * Technical Background -------------------- Here are some technical notes which might help in understanding the code.
 *
 * The output of the Xtext parser is two separate tree structures: 1) EMF model (semantic model) - used for validation
 * and eventually code generation - I think this is the equivalent to the AST. 2) Node Model - This is read by the
 * Eclipse JFace text editor component to display and enter the text. Leaves in this tree are tokens which point to a
 * chunk of text stream which must be contiguous and non-overlapping with other tokens.
 *
 * Each token can refer to two separate text values: 1) A start and end index into the text stream. This value will be
 * used for the NodeModel. 2) An explicit text value. This will be used for the EMF model.
 *
 * So each token can have two values. So if we take the macro example, the index for the macro will point to the macro
 * name and the text value will contain the expansion of the macro. PhantomToken
 *
 * There is not an explicit mechanism for tokens which need to be used in the parser and will affect the EMF, but do not
 * exist anywhere in the editor, such as the inserted curly brackets in the Python-like example above.
 *
 * However we can cheat by making the start and stop indexes the same, this means that the token has little effect on
 * the NodeModel. It is still important that the index values are contiguous with the tokens before and after it.
 *
 * The best way to understand the indexes into the text stream is to think of the indexes as representing the spaces
 * between the characters, not the characters, like this: Index: 0 1 2 3 4 5 6 7 text stream: { { { a } } }
 *
 * So the first character has index 0:1 The second 1:2 and so on. This makes it easier to work out the indexes for
 * composite nodes as well as leaf nodes. So, for example, the composite node holding the outer brackets is 0:7. The
 * inner brackets are 2:5.
 *
 * Known Bugs ---------- There is a known bug here: https://github.com/martinbaker/xtextadd/issues/1
 *
 * @author Martin Baker
 */
public class PhantomToken extends CommonToken {

	/**
	 * is Serializable so have static id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a token to which is not linked to any text in the editor but will be parsed. The stop and start indexes
	 * are both set to the stop index of the previous token.
	 *
	 * Use this constructor if there is no text associated with this token. That is when the parser only needs to
	 * inspect the 'type' but not the 'text'.
	 *
	 * A use-case for this constructor is Python-like block handling where BEGIN and END tokens do not need any more
	 * information associated with them.
	 *
	 * @param type
	 *            A token type defined in the Internal Lexer
	 * @param previousToken
	 *            A reference to the token immediately preceding this in the token stream
	 * @author Martin Baker
	 */
	public PhantomToken(final int type, final CommonToken previousToken) {
		super(previousToken.getInputStream(), type, previousToken.getChannel(), previousToken.getStopIndex() + 1, // start
																													// index
																													// set
																													// to
																													// STOP
																													// index
																													// of
																													// previous
				previousToken.getStopIndex()); // stop index also set to stop index of previous
		/*
		 * We must set the text to an empty string rather than leaving it null. This is because, if text value is left
		 * at null, then there would be an attempt to fetch text from character stream which is not what we want.
		 */
		setText("");
	}

	/**
	 * An alternative constructor which takes its type from a template token and puts it after previousToken in
	 * character stream.
	 *
	 * Use this constructor if there is text associated with this token. That is when the parser needs to inspect both
	 * the 'type' and the 'text'. Note: this text is not associated with any text in the editor.
	 *
	 * A use-case for this constructor is macros where we need to insert a substituted macro which might need 'text'
	 * information in addition to token 'type'.
	 *
	 * @param template
	 *            The token which specifies the type
	 * @param previousToken
	 *            A reference to the token immediately preceding this in the token stream
	 * @author Martin Baker
	 */
	public PhantomToken(final CommonToken template, final CommonToken previousToken) {
		super(template.getInputStream(), template.getType(), previousToken.getChannel(),
				previousToken.getStopIndex() + 1, // start index set to STOP index of previous
				previousToken.getStopIndex()); // stop index also set to stop index of previous
		String t = template.getText();
		/*
		 * We must set the text to an empty string rather than leaving it null. This is because, if text value is left
		 * at null, then there would be an attempt to fetch text from character stream which is not what we want.
		 */
		if (t == null) { t = ""; }
		setText(t);
	}

	@Override
	public String toString() {
		String channelStr = "";
		if (channel > 0) { channelStr = ",channel=" + channel; }
		return "[phantom token ix=" + getTokenIndex() + " ch=" + channelStr + "]";
	}
}
