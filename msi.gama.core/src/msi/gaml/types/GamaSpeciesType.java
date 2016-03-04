/*********************************************************************************************
 *
 *
 * 'GamaSpeciesType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulationSet;
import msi.gama.precompiler.*;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;

/**
 * The type used for representing species objects (since they can be manipulated in a model)
 *
 * Written by drogoul Modified on 1 aout 2010
 *
 * @todo Description
 *
 */
@type(name = IKeyword.SPECIES, id = IType.SPECIES, wraps = { ISpecies.class }, kind = ISymbolKind.Variable.REGULAR,
concept = { IConcept.TYPE, IConcept.SPECIES })
public class GamaSpeciesType extends GamaContainerType<ISpecies> {

	@Override
	public ISpecies cast(final IScope scope, final Object obj, final Object param, final boolean copy)
		throws GamaRuntimeException {
		// TODO Add a more general cast with list of agents to find a common species.
		ISpecies species =
			obj == null ? getDefault() : obj instanceof ISpecies ? (ISpecies) obj : obj instanceof IAgent
				? ((IAgent) obj).getSpecies() : obj instanceof String ? scope.getModel().getSpecies((String) obj)
					: getDefault();
				if ( obj instanceof IPopulationSet ) {
					species = ((IPopulationSet) obj).getSpecies();
				}
				return species;
	}

	@Override
	public ISpecies cast(final IScope scope, final Object obj, final Object param, final IType keyType,
		final IType contentType, final boolean copy) {

		ISpecies result = cast(scope, obj, param, copy);
		if ( result == null ) {
			if ( contentType.isAgentType() ) { return scope.getModel().getSpecies(contentType.getName()); }
		}
		return result;
	}

	// TODO Verify that we dont need to declare the other cast method

	@Override
	public ISpecies getDefault() {
		return null;
	}

	@Override
	public IType getContentType() {
		return Types.get(AGENT);
	}

	@Override
	public IType getKeyType() {
		return Types.INT;
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

	@Override
	public IType contentsTypeIfCasting(final IExpression exp) {
		IType itemType = exp.getType();
		if ( itemType.isAgentType() ) { return itemType; }
		switch (exp.getType().id()) {
			case SPECIES:
				return itemType.getContentType();
		}
		return exp.getType();
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}

}
