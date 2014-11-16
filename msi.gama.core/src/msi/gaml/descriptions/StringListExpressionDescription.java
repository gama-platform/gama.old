/*********************************************************************************************
 * 
 * 
 * 'StringListExpressionDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.descriptions;

import gnu.trove.set.hash.THashSet;
import java.util.*;
import org.eclipse.emf.ecore.EObject;

/**
 * The class EcoreBasedExpressionDescription.
 * 
 * @author drogoul
 * @since 31 mars 2012
 * 
 */
public class StringListExpressionDescription extends BasicExpressionDescription {

	final Collection<String> strings;

	public StringListExpressionDescription(final Collection<String> exp) {
		super((EObject) null);
		strings = exp;
	}

	@Override
	public IExpressionDescription cleanCopy() {
		IExpressionDescription copy = new StringListExpressionDescription(strings);
		copy.setTarget(target);
		return copy;
	}

	public StringListExpressionDescription(final String ... exp) {
		super((EObject) null);
		strings = Arrays.asList(exp);
	}

	// @Override
	// public String toString() {
	// return strings.toString();
	// }

	@Override
	public String toOwnString() {
		return strings.toString();
	}

	@Override
	public Set<String> getStrings(final IDescription context, final boolean skills) {
		return new THashSet(strings);
	}

}
