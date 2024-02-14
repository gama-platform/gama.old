/*******************************************************************************************************
 *
 * GamlParser.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.parsing;

/*******************************************************************************************************
 *
 * DemoParser.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

import org.antlr.runtime.CharStream;
import org.antlr.runtime.TokenSource;

import msi.gama.lang.gaml.resource.GamlResource;

/**
 * Overview of this project here: https://github.com/martinbaker/xtextadd/tree/master/macro
 */
public class GamlParser extends msi.gama.lang.gaml.parser.antlr.GamlParser {

	/** The resource. */
	private GamlResource resource;

	/**
	 * Creates the lexer.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param stream
	 *            the stream
	 * @return the token source
	 * @date 7 févr. 2024
	 */
	@Override
	protected TokenSource createLexer(final CharStream stream) {
		PreProcessorTokenSource tokenSource = new PreProcessorTokenSource();
		tokenSource.setDelegate(super.createLexer(stream));
		tokenSource.setResource(resource);
		// tokenSource.initializeTokenDefsFrom(getTokenDefProvider());
		return tokenSource;
	}

	/**
	 * Sets the resource.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param gamlResource
	 *            the new resource
	 * @date 13 févr. 2024
	 */
	public void setResource(final GamlResource gamlResource) { this.resource = gamlResource; }

}