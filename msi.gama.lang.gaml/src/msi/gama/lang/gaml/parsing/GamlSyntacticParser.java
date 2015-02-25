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

import gnu.trove.set.hash.TLinkedHashSet;
import java.util.*;
import msi.gama.lang.gaml.parser.antlr.GamlParser;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gaml.compilation.SyntacticModelElement;
import org.antlr.runtime.CharStream;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.util.ReplaceRegion;

public class GamlSyntacticParser extends GamlParser {

	// A "wrapped" parse result which contains both the result of the parsing and the syntactic elements needed by GAML
	public static class GamlParseResult extends ParseResult {

		final SyntacticModelElement element;
		final Set<Diagnostic> errors = new HashSet();

		public GamlParseResult(final IParseResult result) {
			super(result.getRootASTElement(), result.getRootNode(), result.hasSyntaxErrors());
			if ( !result.hasSyntaxErrors() ) {
				element = GamlCompatibilityConverter.buildSyntacticContents(getRootASTElement(), errors);
			} else {
				element = null;
			}
		}

		public SyntacticModelElement getSyntacticContents() {
			return element;
		}

		public Collection<? extends Diagnostic> getWarnings() {
			return errors;
		}

		/**
		 * @param uri
		 */
		public void fixURIsWith(final GamlResource r) {
			if ( element == null ) { return; }
			if ( element.areURIFixed() ) { return; }
			Set<URI> set = element.getImports();
			if ( set.isEmpty() ) { return; }
			Set<URI> newSet = new TLinkedHashSet();
			for ( URI u : set ) {
				URI newUri = u.resolve(r.getURI());
				if ( EcoreUtil2.isValidUri(r, newUri) ) {
					newSet.add(newUri);
				}
			}
			element.setImports(newSet);
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
