/*********************************************************************************************
 * 
 *
 * 'SyntacticComposedElement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import java.util.Arrays;
import msi.gaml.statements.Facets;
import org.eclipse.emf.ecore.EObject;

/**
 * The class SyntacticElement.
 * 
 * @author drogoul
 * @since 5 f�vr. 2012
 * 
 */
public class SyntacticComposedElement extends SyntacticSingleElement {

	ISyntacticElement[] children;

	SyntacticComposedElement(final String keyword, final Facets facets, final EObject statement) {
		super(keyword, facets, statement);
	}

	@Override
	public ISyntacticElement[] getChildren() {
		return children == null ? EMPTY_ARRAY : children;
	}

	@Override
	public void addChild(final ISyntacticElement e) {
		if ( e == null ) { return; }
		if ( children == null ) {
			children = new ISyntacticElement[1];
		} else {
			children = Arrays.copyOf(children, children.length + 1);
		}
		children[children.length - 1] = e;
	}

}
