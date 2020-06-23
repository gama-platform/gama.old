/*******************************************************************************************************
 *
 * msi.gaml.compilation.ast.SyntacticModelElement.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation.ast;

import java.io.File;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IKeyword;
import msi.gaml.statements.Facets;

/**
 * Class SyntacticModelElement.
 *
 * @author drogoul
 * @since 12 avr. 2014
 *
 */
public class SyntacticModelElement extends SyntacticTopLevelElement {

	/**
	 * The Class SyntacticExperimentModelElement.
	 */
	public static class SyntacticExperimentModelElement extends SyntacticModelElement {

		/**
		 * Instantiates a new syntactic experiment model element.
		 *
		 * @param keyword
		 *            the keyword
		 * @param root
		 *            the root
		 * @param path
		 *            the path
		 */
		public SyntacticExperimentModelElement(final String keyword, final EObject root, final String path) {
			super(keyword, null, root, path);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see msi.gaml.compilation.ast.SyntacticComposedElement#addChild(msi.gaml.compilation.ast.ISyntacticElement)
		 */
		@Override
		public void addChild(final ISyntacticElement e) {
			super.addChild(e);
			setFacet(IKeyword.NAME, e.getExpressionAt(IKeyword.NAME));
		}

		/**
		 * Gets the experiment.
		 *
		 * @return the experiment
		 */
		public SyntacticExperimentElement getExperiment() {
			return (SyntacticExperimentElement) children[0];
		}
	}

	/**
	 * The path.
	 */
	final private String path;

	/**
	 * Instantiates a new syntactic model element.
	 *
	 * @param keyword
	 *            the keyword
	 * @param facets
	 *            the facets
	 * @param statement
	 *            the statement
	 * @param path
	 *            the path
	 * @param imports
	 *            the imports
	 */
	public SyntacticModelElement(final String keyword, final Facets facets, final EObject statement, final String path,
			final Object... imports) {
		super(keyword, facets, statement);
		if (path != null) {
			final String p = path;
			this.path = p.endsWith(File.pathSeparator) ? p : p + "/";
		} else {
			// Case of ill resources (compilation of blocks)
			this.path = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.compilation.ast.SyntacticSpeciesElement#isSpecies()
	 */
	@Override
	public boolean isSpecies() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * msi.gaml.compilation.ast.AbstractSyntacticElement#visitExperiments(msi.gaml.compilation.ast.ISyntacticElement.
	 * SyntacticVisitor)
	 */
	@Override
	public void visitExperiments(final SyntacticVisitor visitor) {
		visitAllChildren(visitor, EXPERIMENT_FILTER);
	}

	/**
	 * The compacter.
	 */
	static SyntacticVisitor compacter = element -> element.compact();

	/**
	 * Compact model.
	 */
	public void compactModel() {
		this.visitThisAndAllChildrenRecursively(compacter);
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

}
