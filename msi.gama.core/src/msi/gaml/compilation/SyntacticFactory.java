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

	public static ISyntacticElement create(final String keyword, final EObject statement) {
		return create(keyword, null, statement);
	}

	public static ISyntacticElement create(final String keyword, final Facets facets) {
		return create(keyword, facets, null);
	}

	public static ISyntacticElement create(final String keyword, final Facets facets, final EObject statement) {
		if ( keyword.equals(GLOBAL) ) {
			return new GlobalSyntacticElement(keyword, facets, statement);
		} else if ( keyword.equals(SPECIES) || keyword.equals(GRID) ) {
			return new SpeciesSyntacticElement(keyword, facets, statement);
		} else if ( keyword.equals(EXPERIMENT) ) { return new ExperimentSyntacticElement(keyword, facets, statement); }
		return new SyntacticElement(keyword, facets, statement);
	}
}

// TODO for content assist
// Build a scope accessible by EObjects that contain variables and actions names in the syntactic structure
// A global scope can also be built for built-in elements (and attached to the local scopes if we can detect things like
// skills, etc.)
// The scope could be attached to resources (like the syntactic elements) and become accessible from content assist to
// return possible candidates