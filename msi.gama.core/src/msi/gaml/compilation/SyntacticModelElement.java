/*********************************************************************************************
 * 
 * 
 * 'SyntacticModelElement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import java.io.File;

import org.eclipse.emf.ecore.EObject;

import msi.gaml.statements.Facets;

/**
 * Class SyntacticModelElement.
 * 
 * @author drogoul
 * @since 12 avr. 2014
 * 
 */
public class SyntacticModelElement extends SyntacticTopLevelElement {

	final private String path;

	public SyntacticModelElement(final String keyword, final Facets facets, final EObject statement, final File path,
			final Object... imports) {
		super(keyword, facets, statement);
		if (path != null) {
			final String p = path.getAbsolutePath();
			this.path = p.endsWith(File.pathSeparator) ? p : p + "/";
		} else
			// Case of ill resources (compilation of blocks)
			this.path = null;
	}

	@Override
	public boolean isSpecies() {
		return false;
	}

	// public void printStats() {
	// final Map<String, Integer> stats = new HashMap();
	// computeStats(stats);
	// // System.out.println("Stats for " + getName() + " : " + stats);
	// }

	@Override
	public void visitExperiments(final SyntacticVisitor visitor) {
		visitAllChildren(visitor, EXPERIMENT_FILTER);
	}

	static SyntacticVisitor compacter = new SyntacticVisitor() {

		@Override
		public void visit(final ISyntacticElement element) {
			element.compact();
		}
	};

	public void compactModel() {
		this.visitThisAndAllChildrenRecursively(compacter);
	}

	public String getPath() {
		return path;
	}
}
