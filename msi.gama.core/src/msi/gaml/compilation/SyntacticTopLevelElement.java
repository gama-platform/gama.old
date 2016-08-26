package msi.gaml.compilation;

import java.util.Collections;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterables;

import msi.gaml.statements.Facets;

public class SyntacticTopLevelElement extends SyntacticSpeciesElement {

	SyntacticTopLevelElement(final String keyword, final Facets facets, final EObject statement) {
		super(keyword, facets, statement);
	}

	@Override
	public Iterable<ISyntacticElement> getGrids() {
		if (children == null)
			return Collections.EMPTY_LIST;
		return Iterables.filter(children, GRID_FILTER);
	}

}