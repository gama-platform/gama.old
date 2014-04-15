/*********************************************************************************************
 * 
 *
 * 'SyntacticGlobalElement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import msi.gaml.statements.Facets;
import org.eclipse.emf.ecore.EObject;

/**
 * Class GlobalSyntacticElement.
 * 
 * @author drogoul
 * @since 9 sept. 2013
 * 
 */
public class SyntacticGlobalElement extends SyntacticComposedElement {

	/**
	 * @param keyword
	 * @param facets
	 * @param statement
	 */
	SyntacticGlobalElement(final String keyword, final Facets facets, final EObject statement) {
		super(keyword, facets, statement);
	}

	@Override
	public boolean isSpecies() {
		return false;
	}

	@Override
	public boolean isGlobal() {
		return true;
	}

	@Override
	public boolean isExperiment() {
		return false;
	}
}
