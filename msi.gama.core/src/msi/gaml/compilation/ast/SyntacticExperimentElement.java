/*********************************************************************************************
 *
 * 'SyntacticExperimentElement.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.compilation.ast;

import org.eclipse.emf.ecore.EObject;

import msi.gaml.statements.Facets;

/**
 * Class GlobalSyntacticElement.
 * 
 * @author drogoul
 * @since 9 sept. 2013
 * 
 */
public class SyntacticExperimentElement extends SyntacticStructuralElement {

	/**
	 * @param keyword
	 * @param facets
	 * @param statement
	 */
	SyntacticExperimentElement(final String keyword, final Facets facets, final EObject statement) {
		super(keyword, facets, statement);
	}

	@Override
	public boolean isSpecies() {
		return false;
	}

	@Override
	public boolean isExperiment() {
		return true;
	}
}
