/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gama.lang.utils;

import java.util.Set;
import msi.gaml.descriptions.BasicExpressionDescription;
import org.eclipse.emf.ecore.EObject;

/**
 * The class EcoreBasedExpressionDescription.
 * 
 * @author drogoul
 * @since 31 mars 2012
 * 
 */
public class StringListExpressionDescription extends BasicExpressionDescription {

	final Set<String> strings;

	public StringListExpressionDescription(final Set<String> exp) {
		super((EObject) null);
		strings = exp;
	}

	@Override
	public String toString() {
		return strings.toString();
	}

	public Set<String> getStrings() {
		return strings;
	}

}
