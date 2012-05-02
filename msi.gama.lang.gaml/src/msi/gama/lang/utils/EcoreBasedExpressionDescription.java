/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gama.lang.utils;

import msi.gaml.descriptions.BasicExpressionDescription;
import org.eclipse.emf.ecore.EObject;

/**
 * The class EcoreBasedExpressionDescription.
 * 
 * @author drogoul
 * @since 31 mars 2012
 * 
 */
public class EcoreBasedExpressionDescription extends BasicExpressionDescription {

	public EcoreBasedExpressionDescription(final EObject exp) {
		super(exp);
	}

	@Override
	public String toString() {
		return expression == null ? EGaml.toString(target) : super.toString();
	}

}
