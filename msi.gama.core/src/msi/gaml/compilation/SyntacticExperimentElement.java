/**
 * Created by drogoul, 9 sept. 2013
 * 
 */
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
public class SyntacticExperimentElement extends SyntacticComposedElement {

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
	public boolean isGlobal() {
		return false;
	}

	@Override
	public boolean isExperiment() {
		return true;
	}
}
