/*******************************************************************************************************
 *
 * GamlParser.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.parser.antlr;

import com.google.inject.Inject;
import msi.gama.lang.gaml.parser.antlr.internal.InternalGamlParser;
import msi.gama.lang.gaml.services.GamlGrammarAccess;
import org.eclipse.xtext.parser.antlr.AbstractAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;

/**
 * The Class GamlParser.
 */
public class GamlParser extends AbstractAntlrParser {

	/** The grammar access. */
	@Inject
	private GamlGrammarAccess grammarAccess;

	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	

	@Override
	protected InternalGamlParser createParser(XtextTokenStream stream) {
		return new InternalGamlParser(stream, getGrammarAccess());
	}

	@Override 
	protected String getDefaultRuleName() {
		return "Entry";
	}

	/**
	 * Gets the grammar access.
	 *
	 * @return the grammar access
	 */
	public GamlGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	/**
	 * Sets the grammar access.
	 *
	 * @param grammarAccess the new grammar access
	 */
	public void setGrammarAccess(GamlGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
