/*********************************************************************************************
 *
 * 'SyntacticStructuralElement.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.compilation.ast;

import org.eclipse.emf.ecore.EObject;

import msi.gaml.statements.Facets;

public class SyntacticStructuralElement extends SyntacticComposedElement {

	String name;

	public SyntacticStructuralElement(final String keyword, final Facets facets, final EObject statement) {
		super(keyword, facets, statement);
		name = super.getName();
	}

	@Override
	public String getName() {
		if (name == null) {
			name = super.getName();
		}
		return name;
	}

}
