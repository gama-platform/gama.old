package msi.gaml.compilation;

import org.eclipse.emf.ecore.EObject;

import msi.gaml.statements.Facets;

public class SyntacticTopLevelElement extends SyntacticSpeciesElement {

	SyntacticTopLevelElement(final String keyword, final Facets facets, final EObject statement) {
		super(keyword, facets, statement);
	}

	@Override
	public void visitGrids(final SyntacticVisitor visitor) {
		visitAllChildren(visitor, GRID_FILTER);
	}

}