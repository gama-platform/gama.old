/*********************************************************************************************
 *
 * 'SyntacticModelElement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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

	public static class SyntacticExperimentModelElement extends SyntacticModelElement {
		public SyntacticExperimentModelElement(final String keyword, final EObject root, final String path) {
			super(keyword, null, root, path);
		}

		@Override
		public void addChild(final ISyntacticElement e) {
			super.addChild(e);
			setFacet(IKeyword.NAME, e.getExpressionAt(IKeyword.NAME));
		}

		public SyntacticExperimentElement getExperiment() {
			return (SyntacticExperimentElement) children[0];
		}
	}

	final private String path;

	public SyntacticModelElement(final String keyword, final Facets facets, final EObject statement, final String path,
			final Object... imports) {
		super(keyword, facets, statement);
		if (path != null) {
			final String p = path;
			// try {
			// p = path.getCanonicalPath();
			// } catch (final IOException e) {
			// e.printStackTrace();
			// p = path.getAbsolutePath();
			// }
			this.path = p.endsWith(File.pathSeparator) ? p : p + "/";
		} else {
			// Case of ill resources (compilation of blocks)
			this.path = null;
		}
	}

	@Override
	public boolean isSpecies() {
		return false;
	}

	@Override
	public void visitExperiments(final SyntacticVisitor visitor) {
		visitAllChildren(visitor, EXPERIMENT_FILTER);
	}

	static SyntacticVisitor compacter = element -> element.compact();

	public void compactModel() {
		this.visitThisAndAllChildrenRecursively(compacter);
	}

	public String getPath() {
		return path;
	}

}
