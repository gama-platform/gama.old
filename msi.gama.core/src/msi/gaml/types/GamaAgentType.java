/*********************************************************************************************
 *
 *
 * 'GamaAgentType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.types;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.species.ISpecies;

/**
 * The type used to represent an agent of a species. Should be used by the species for all the
 * operations relative to casting, etc.
 *
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
public class GamaAgentType extends GamaType<IAgent> {

	SpeciesDescription species;

	public GamaAgentType(final SpeciesDescription species, final String name, final int speciesId, final Class base) {
		this.species = species;
		this.name = name;
		id = speciesId;
		supports = new Class[] { base };
		if ( species != null ) {
			setDefiningPlugin(species.getDefiningPlugin());
		}
	}

	@Override
	public String getDefiningPlugin() {
		return species.getDefiningPlugin();
	}

	@Override
	public IAgent cast(final IScope scope, final Object obj, final Object param, final boolean copy)
		throws GamaRuntimeException {
		if ( obj == null ) { return null; }
		ISpecies species = (ISpecies) param;
		if ( species == null ) {
			species = scope.getModel().getSpecies(this.species.getName());
		}
		if ( species == null ) { return (IAgent) Types.AGENT.cast(scope, obj, param, copy); }
		if ( obj instanceof IAgent ) { return ((IAgent) obj).isInstanceOf(species, false) ? (IAgent) obj : null; }
		if ( obj instanceof Integer ) { return scope.getAgent().getPopulationFor(species)
			.getAgent((Integer) obj); }
		if ( obj instanceof ILocation ) {
			final IAgent result = scope.getAgent().getPopulationFor(species).getAgent(scope, (ILocation) obj);
			return result;
		}
		return null;
	}

	@Override
	public IAgent getDefault() {
		return null;
	}

	@Override
	public boolean isAgentType() {
		return true;
	}


	@Override
	public String getSpeciesName() {
		return name;
	}

	@Override
	public SpeciesDescription getSpecies() {
		return species;
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}


	@Override
	public boolean canBeTypeOf(final IScope scope, final Object obj) {
		final boolean b = super.canBeTypeOf(scope, obj);
		if ( b ) { return true; }
		if ( obj instanceof IAgent ) {
			final ISpecies s = scope.getSimulation().getModel().getSpecies(getSpeciesName());
			return ((IAgent) obj).isInstanceOf(s, false);
		}
		return false;
	}

	@Override
	public IType getKeyType() {
		return Types.STRING;
	}


	@Override
	public boolean isFixedLength() {
		return false;
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

}
