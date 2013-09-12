package msi.gama.lang.gaml.parsing;

import java.util.*;
import msi.gama.lang.gaml.parser.antlr.GamlParser;
import msi.gaml.compilation.ISyntacticElement;
import org.antlr.runtime.CharStream;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.util.ReplaceRegion;

public class GamlSyntacticParser extends GamlParser {

	// A "wrapped" parse result which contains both the result of the parsing and the syntactic elements needed by GAML
	public static class GamlParseResult extends ParseResult {

		final ISyntacticElement element;
		final Set<Diagnostic> errors = new HashSet();

		public GamlParseResult(final IParseResult result) {
			super(result.getRootASTElement(), result.getRootNode(), result.hasSyntaxErrors());
			if ( !result.hasSyntaxErrors() ) {
				element = GamlCompatibilityConverter.buildSyntacticContents(getRootASTElement(), errors);
			} else {
				element = null;
			}
		}

		public ISyntacticElement getSyntacticContents() {
			return element;
		}

		public Collection<? extends Diagnostic> getWarnings() {
			return errors;
		}

	}

	@Override
	protected IParseResult doParse(final String ruleName, final CharStream in, final NodeModelBuilder nodeModelBuilder,
		final int initialLookAhead) {
		IParseResult result = super.doParse(ruleName, in, nodeModelBuilder, initialLookAhead);
		return new GamlParseResult(result);
	}

	@Override
	protected IParseResult doReparse(final IParseResult previousParseResult, final ReplaceRegion replaceRegion) {
		IParseResult result = super.doReparse(previousParseResult, replaceRegion);
		return new GamlParseResult(result);
	}

}
