/*********************************************************************************************
 * 
 * 
 * 'GamlSyntacticParser.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.parsing;

import org.antlr.runtime.CharStream;
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.ParseResult;
import org.eclipse.xtext.util.ReplaceRegion;

import msi.gama.lang.gaml.parser.antlr.GamlParser;
import msi.gaml.compilation.SyntacticModelElement;

public class GamlSyntacticParser extends GamlParser {

	// A "wrapped" parse result which contains both the result of the parsing
	// and the syntactic elements needed by GAML
	public static class GamlParseResult extends ParseResult {

		SyntacticModelElement element;

		public GamlParseResult(final IParseResult result) {
			super(result.getRootASTElement(), result.getRootNode(), result.hasSyntaxErrors());
		}

		public SyntacticModelElement getSyntacticContents() {
			if (element != null)
				return element;
			if (!hasSyntaxErrors()) {
				element = GamlCompatibilityConverter.buildSyntacticContents(getRootASTElement(), null);
			}
			return element;
		}

	}

	@Override
	protected IParseResult doParse(final String ruleName, final CharStream in, final NodeModelBuilder nodeModelBuilder,
			final int initialLookAhead) {
		final IParseResult result = super.doParse(ruleName, in, nodeModelBuilder, initialLookAhead);
		return new GamlParseResult(result);
	}

	@Override
	protected IParseResult doReparse(final IParseResult previousParseResult, final ReplaceRegion replaceRegion) {
		final IParseResult result = super.doReparse(previousParseResult, replaceRegion);
		final GamlParseResult r = new GamlParseResult(result);
		return r;
	}

}
