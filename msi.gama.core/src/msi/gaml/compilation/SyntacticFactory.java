/*********************************************************************************************
 * 
 * 
 * 'SyntacticFactory.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import static msi.gama.common.interfaces.IKeyword.EXPERIMENT;
import static msi.gama.common.interfaces.IKeyword.GRID;
import static msi.gama.common.interfaces.IKeyword.MODEL;
import static msi.gama.common.interfaces.IKeyword.SPECIES;

import org.eclipse.emf.ecore.EObject;

import msi.gaml.statements.Facets;

/**
 * Class SyntacticFactory.
 * 
 * @author drogoul
 * @since 9 sept. 2013
 * 
 */
public class SyntacticFactory {

	public static final String SPECIES_VAR = "species_var";

	public static ISyntacticElement create(final String keyword, final EObject statement, final boolean withChildren,
			final Object... data) {
		return create(keyword, null, statement, withChildren, data);
	}

	public static ISyntacticElement create(final String keyword, final Facets facets, final boolean withChildren,
			final Object... data) {
		return create(keyword, facets, null, withChildren, data);
	}

	public static ISyntacticElement create(final String keyword, final Facets facets, final EObject statement,
			final boolean withChildren, final Object... data) {
		if (keyword.equals(MODEL)) {
			return new SyntacticModelElement(keyword, facets, statement, data);
		} else if (keyword.equals(SPECIES) || keyword.equals(GRID)) {
			return new SyntacticSpeciesElement(keyword, facets, statement);
		} else if (keyword.equals(EXPERIMENT)) {
			return new SyntacticExperimentElement(keyword, facets, statement);
		}
		if (!withChildren) {
			return new SyntacticSingleElement(keyword, facets, statement);
		}
		return new SyntacticComposedElement(keyword, facets, statement);
	}

	public static ISyntacticElement createVar(final String keyword, final String name, final EObject stm) {
		return new SyntacticAttributeElement(keyword, name, stm);
	}
}

// TODO for content assist
// Build a scope accessible by EObjects that contain variables and actions names
// in the syntactic structure
// A global scope can also be built for built-in elements (and attached to the
// local scopes if we can detect things like
// skills, etc.)
// The scope could be attached to resources (like the syntactic elements) and
// become accessible from content assist to
// return possible candidates