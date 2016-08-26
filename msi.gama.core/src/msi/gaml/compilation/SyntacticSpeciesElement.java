/*********************************************************************************************
 * 
 *
 * 'SyntacticSpeciesElement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import java.util.Collections;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterables;

import msi.gaml.statements.Facets;

/**
 * Class GlobalSyntacticElement.
 * 
 * @author drogoul
 * @since 9 sept. 2013
 * 
 */
public class SyntacticSpeciesElement extends SyntacticStructuralElement {

	/**
	 * @param keyword
	 * @param facets
	 * @param statement
	 */
	SyntacticSpeciesElement(final String keyword, final Facets facets, final EObject statement) {
		super(keyword, facets, statement);
	}

	@Override
	public Iterable<ISyntacticElement> getSpecies() {
		if (children == null)
			return Collections.EMPTY_LIST;
		return Iterables.filter(children, SPECIES_FILTER);
	}

	@Override
	public boolean isSpecies() {
		return true;
	}

}
