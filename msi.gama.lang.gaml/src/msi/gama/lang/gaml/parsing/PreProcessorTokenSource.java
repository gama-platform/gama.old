/*******************************************************************************************************
 *
 * PreProcessorTokenSource.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.parsing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.eclipse.xtext.parser.antlr.TokenAcceptor;

import msi.gama.lang.gaml.indexer.GamlResourceIndexer;
import msi.gama.lang.gaml.parser.antlr.internal.InternalGamlParser;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.factories.DescriptionFactory;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class PreProcessorTokenSource. Provides a token source for GAML, allowing to pre-process a number of things based
 * on the knowledge provided by GAML idioms
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 12 févr. 2024
 */
public class PreProcessorTokenSource implements TokenSource {

	/** The Constant TOKENS. */
	public static final Set<String> TOKENS = new HashSet<>();

	/** The Constant TERMINATIONS. */
	public static final Set<String> TERMINATIONS = Set.of(";", "{", "}");

	/** The Constant STATEMENT_TAG. */
	public static final String STATEMENT_TAG = "**statement*";

	/** The Constant BEHAVIOR_TAG. */
	public static final String BEHAVIOR_TAG = "**behavior*";

	/** The Constant DECLARATION_TAG. */
	public static final String DECLARATION_TAG = "**behavior*";

	static {
		DEBUG.ON();
		Collections.addAll(TOKENS, InternalGamlParser.tokenNames);
	}

	/**
	 * state tells us if we are parsing a statement
	 *
	 * @author Martin Baker
	 */
	private enum StateValues {

		/** The beginning. */
		BEGINNING,
		/** The looking for first facet. */
		LOOKING_FOR_FIRST_FACET,
		/** The in statement. */
		LOOKING_FOR_EOL,
	}

	/** The name. */
	public static CommonToken name = new CommonToken(InternalGamlParser.RULE_ID, "");

	/** The state. */
	StateValues state = StateValues.BEGINNING;

	/**
	 * We hold thisToken and lastToken because we need lastToken in doSplitToken. TODO this is a hack, it would be
	 * better if AbstractSplittingTokenSource provided this.
	 */
	CommonToken thisToken, lastToken;

	/** The statement. */
	SymbolProto statement = null;

	/** The current tag. */
	String currentTag = null;

	/** The resource. */
	private Set<String> types;

	/** The delegate. */
	private TokenSource delegate;

	/** The next tokens. */
	private final TokenAcceptor nextTokens;

	/**
	 * Instantiates a new pre processor token source.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 12 févr. 2024
	 */
	public PreProcessorTokenSource() {
		nextTokens = new TokenAcceptor();
	}

	/**
	 * Sets the delegate.
	 *
	 * @param delegate
	 *            the new delegate
	 */
	public void setDelegate(final TokenSource delegate) { this.delegate = delegate; }

	@Override
	public Token nextToken() {
		if (nextTokens.hasNext()) {
			Token result = nextTokens.next();
			return result;
		}
		Token result = delegate.nextToken();
		process(result);
		result = nextTokens.next();
		return result;
	}

	@Override
	public String getSourceName() { return "[PreProcessorTokenSource] " + delegate.getSourceName(); }

	/**
	 * Split token.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param splitMe
	 *            the split me
	 * @param acceptor
	 *            the acceptor
	 * @date 12 févr. 2024
	 */
	protected void process(final Token token) {
		if (token != null && token.getType() != Token.EOF && token.getChannel() != Token.HIDDEN_CHANNEL) {
			lastToken = thisToken;
			if (token instanceof CommonToken) { thisToken = (CommonToken) token; }
			String tokenText = token.getText();
			int tokenType = token.getType();

			switch (state) {
				case BEGINNING: {
					if (isStatement(tokenText)) {
						addToken(STATEMENT_TAG);
					} else if (isBehavior(tokenText)) { addToken(BEHAVIOR_TAG); }
					state = StateValues.LOOKING_FOR_EOL;
					break;
				}
				case LOOKING_FOR_EOL: {
					if (TERMINATIONS.contains(tokenText)) { state = StateValues.BEGINNING; }
				}
				// case BEGINNING: {
				// if (DescriptionFactory.isStatementProto(tokenText) && !IKeyword.MODEL.equals(tokenText)) {
				// DEBUG.OUT("Statement found with rule " + this.getRuleName(token) + " and text " + tokenText);
				// statement = DescriptionFactory.getStatementProto(tokenText);
				// if (statement == null) {
				// state = StateValues.LOOKING_FOR_EOL;
				// return false;
				// }
				//
				// currentTag = STATEMENT_TAG;
				// return true;
				//
				// // String defaultFacetName = statement.getDefaultFacetName();
				// // if (IKeyword.GLOBAL.equals(tokenText)
				// // || GamlAnnotations.DO_NOT_INSERT_EXPR.equals(defaultFacetName)) {
				// // DEBUG.OUT("==> Default facet does not exist so look out for the end of the line");
				// // state = StateValues.LOOKING_FOR_EOL;
				// // return false;
				// // }
				// // // FacetProto facet = null;
				// // // if (!GamlAnnotations.NOT_USED.equals(defaultFacetName)) {
				// // // facet = statement.getFacet(defaultFacetName);
				// // // }
				// // // if (facet == null || facet.optional) {
				// // DEBUG.OUT("==> Default facet is optional so look out for an expression");
				// // state = StateValues.LOOKING_FOR_FIRST_FACET;
				// // } else {
				// // state = StateValues.LOOKING_FOR_EOL;
				// // }
				// }
				// return false;
				// }
				// case LOOKING_FOR_FIRST_FACET: {
				// if (tokenType == InternalGamlLexer.RULE_WS) return false;
				// if (";".equals(tokenText) || "{".equals(tokenText)) {
				// DEBUG.OUT("==> Fake expression should be inserted because found termination " + tokenText);
				// state = StateValues.BEGINNING;
				// return true;
				// }
				// if (tokenText.endsWith(":")) { tokenText = tokenText.substring(0, tokenText.length() - 1); }
				// if (statement.getFacet(tokenText) != null) {
				// DEBUG.OUT("==> Fake expression should be inserted because found facet " + tokenText);
				// state = StateValues.LOOKING_FOR_EOL;
				// return true;
				// }
				// state = StateValues.LOOKING_FOR_EOL;
				// return false;
				// }

			}
		}
		nextTokens.accept(token);
	}

	/**
	 * Checks if is statement.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param token
	 *            the token
	 * @return true, if is statement
	 * @date 12 févr. 2024
	 */
	boolean isStatement(final String token) {
		SymbolProto proto = DescriptionFactory.getStatementProto(token);
		if (proto != null) {
			int kind = proto.getKind();
			return kind == ISymbolKind.SEQUENCE_STATEMENT || kind == ISymbolKind.SINGLE_STATEMENT;
		}
		return false;
	}

	/**
	 * Checks if is behavior.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param token
	 *            the token
	 * @return true, if is behavior
	 * @date 12 févr. 2024
	 */
	boolean isBehavior(final String token) {
		SymbolProto proto = DescriptionFactory.getStatementProto(token);
		if (proto != null) {
			int kind = proto.getKind();
			return kind == ISymbolKind.BEHAVIOR;
		}
		return false;
	}

	/**
	 * Adds the token.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param text
	 *            the text
	 * @date 12 févr. 2024
	 */
	protected void addToken(final String text) {
		PhantomToken token = new PhantomToken(name, lastToken);
		token.setText(text);
		nextTokens.accept(token);
	}

	/**
	 * Sets the resource.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param resource
	 *            the new resource
	 * @date 13 févr. 2024
	 */
	public void setResource(final GamlResource resource) {
		types = GamlResourceIndexer.allTypesDeclaredIn(resource.getURI(), resource.getStringContents());
	}

}