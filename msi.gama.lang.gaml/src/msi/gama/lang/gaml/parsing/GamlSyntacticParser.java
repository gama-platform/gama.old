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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.CharStream;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.ParseResult;
import org.eclipse.xtext.util.ReplaceRegion;

import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.lang.gaml.parser.antlr.GamlParser;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gaml.compilation.SyntacticModelElement;

public class GamlSyntacticParser extends GamlParser {

	// A "wrapped" parse result which contains both the result of the parsing
	// and the syntactic elements needed by GAML
	public static class GamlParseResult extends ParseResult {

		final SyntacticModelElement element;
		final Set<Diagnostic> errors = new HashSet();

		public GamlParseResult(final IParseResult result) {
			super(result.getRootASTElement(), result.getRootNode(), result.hasSyntaxErrors());
			if (!result.hasSyntaxErrors()) {
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
			if (element == null) {
				return;
			}
			if (element.areURIFixed()) {
				return;
			}
			final Set<URI> set = element.getImports();
			element.setImports(set);
			if (set.isEmpty()) {
				return;
			}
			final Set<URI> newSet = new TLinkedHashSet();
			for (final URI u : set) {
				final URI newUri = u.resolve(r.getURI());
				if (EcoreUtil2.isValidUri(r, newUri)) {
					newSet.add(newUri);
				}
			}
			element.setImports(newSet);
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
		final long begin = System.nanoTime();
		final IParseResult result = super.doReparse(previousParseResult, replaceRegion);
		final GamlParseResult r = new GamlParseResult(result);
		System.out.println(" reparsed in " + (System.nanoTime() - begin) / 1000000d + " ms in Thread ["
				+ Thread.currentThread().getName() + "]");
		System.out.println("****************************************************");
		return r;
	}

}
