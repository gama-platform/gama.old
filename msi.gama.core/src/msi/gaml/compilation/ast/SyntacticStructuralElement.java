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
