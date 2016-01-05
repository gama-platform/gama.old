/*********************************************************************************************
 *
 *
 * 'ExperimentFactory.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.factories;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.descriptions.*;
import msi.gaml.statements.Facets;

/**
 * The Class EnvironmentFactory.
 *
 * @author drogoul
 */
@factory(handles = { ISymbolKind.EXPERIMENT })
public class ExperimentFactory extends SpeciesFactory {

	public ExperimentFactory(final List<Integer> handles) {
		super(handles);
	}

	@Override
	protected ExperimentDescription buildDescription(final String keyword, final Facets facets, final EObject element,
		final ChildrenProvider children, final IDescription sd, final SymbolProto proto, final String plugin) {
		return new ExperimentDescription(keyword, sd, children, element, facets);
	}

}
