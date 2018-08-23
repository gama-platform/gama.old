/*********************************************************************************************
 *
 * SyntacticExperimentElement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. 
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
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
	 * Instantiates a new syntactic experiment element.
	 *
	 * @param keyword the keyword
	 * @param facets the facets
	 * @param statement the statement
	 */
	SyntacticExperimentElement(final String keyword, final Facets facets, final EObject statement) {
		super(keyword, facets, statement);
	}

	/* (non-Javadoc)
	 * @see msi.gaml.compilation.ast.AbstractSyntacticElement#isSpecies()
	 */
	@Override
	public boolean isSpecies() {
		return false;
	}

	/* (non-Javadoc)
	 * @see msi.gaml.compilation.ast.AbstractSyntacticElement#isExperiment()
	 */
	@Override
	public boolean isExperiment() {
		return true;
	}
}
