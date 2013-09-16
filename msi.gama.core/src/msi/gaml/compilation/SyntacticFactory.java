/**
 * Created by drogoul, 9 sept. 2013
 * 
 */
package msi.gaml.compilation;

import static msi.gama.common.interfaces.IKeyword.*;
import msi.gaml.statements.Facets;
import org.eclipse.emf.ecore.EObject;

/**
 * Class SyntacticFactory.
 * 
 * @author drogoul
 * @since 9 sept. 2013
 * 
 */
public class SyntacticFactory {

	public static ISyntacticElement create(final String keyword, final EObject statement, final boolean withChildren) {
		return create(keyword, null, statement, withChildren);
	}

	public static ISyntacticElement create(final String keyword, final Facets facets, final boolean withChildren) {
		return create(keyword, facets, null, withChildren);
	}

	public static ISyntacticElement create(final String keyword, final Facets facets, final EObject statement,
		final boolean withChildren) {
		if ( keyword.equals(GLOBAL) ) {
			return new SyntacticGlobalElement(keyword, facets, statement);
		} else if ( keyword.equals(SPECIES) || keyword.equals(GRID) ) {
			return new SyntacticSpeciesElement(keyword, facets, statement);
		} else if ( keyword.equals(EXPERIMENT) ) { return new SyntacticExperimentElement(keyword, facets, statement); }
		if ( !withChildren ) { return new SyntacticSingleElement(keyword, facets, statement); }
		return new SyntacticComposedElement(keyword, facets, statement);
	}
}

// TODO for content assist
// Build a scope accessible by EObjects that contain variables and actions names in the syntactic structure
// A global scope can also be built for built-in elements (and attached to the local scopes if we can detect things like
// skills, etc.)
// The scope could be attached to resources (like the syntactic elements) and become accessible from content assist to
// return possible candidates