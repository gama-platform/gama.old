/*********************************************************************************************
 * 
 * 
 * 'SpeciesFactory.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.factories;

import static msi.gama.precompiler.ISymbolKind.SPECIES;
import java.util.*;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gaml.compilation.IAgentConstructor;
import msi.gaml.descriptions.*;
import msi.gaml.statements.Facets;
import org.eclipse.emf.ecore.EObject;

/**
 * SpeciesFactory.
 * 
 * @author drogoul 25 oct. 07
 */

@factory(handles = { SPECIES })
public class SpeciesFactory extends SymbolFactory {

	public SpeciesFactory(final List<Integer> handles) {
		super(handles);
	}

	@Override
	protected TypeDescription buildDescription(final String keyword, final Facets facets, final EObject element,
		final ChildrenProvider children, final IDescription sd, final SymbolProto proto) {
		// String name = facets.getLabel(IKeyword.NAME);
		// DescriptionFactory.addSpeciesNameAsType(name);
		return new SpeciesDescription(keyword, sd, children, element, facets);
	}

	public SpeciesDescription createBuiltInSpeciesDescription(final String name, final Class clazz,
		final IDescription superDesc, final SpeciesDescription parent, final IAgentConstructor helper,
		final Set<String> skills, final Facets userSkills) {
		DescriptionFactory.addSpeciesNameAsType(name);
		return new SpeciesDescription(name, clazz, superDesc, parent, helper, skills, userSkills);
	}

}
