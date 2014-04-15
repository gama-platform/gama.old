/*********************************************************************************************
 * 
 *
 * 'SyntacticSingleElement.java', in plugin 'msi.gama.core', is part of the source code of the 
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
 * The class SyntacticSingleElement.
 * 
 * @author drogoul
 * @since 5 f�vr. 2012
 * @modified 9 sept. 2013
 * 
 */
public class SyntacticSingleElement extends AbstractSyntacticElement {

	final EObject element;

	SyntacticSingleElement(final String keyword, final Facets facets, final EObject statement) {
		super(keyword, facets);
		this.element = statement;
	}

	/**
	 * A method to know wheter an EObject is "contained" in this element or not. It is contained if:
	 * 
	 * 1) The EObject of this element is equal to the parameter, or
	 * 2) One of its facets IExpressionDescription EObject is equal to or is a container of the parameter, or
	 * 3)
	 * @param object
	 * @return
	 */
	public boolean contains(final EObject object) {
		return false;
	}

	@Override
	public EObject getElement() {
		return element;
	}

}
