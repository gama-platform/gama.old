/*******************************************************************************************************
 *
 * GamaAgentType.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.species.ISpecies;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The type used to represent an agent of a species. Should be used by the species for all the operations relative to
 * casting, etc.
 *
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ("unchecked")
public class GamaAgentType extends GamaType<IAgent> {

	static {
		DEBUG.ON();
	}

	/** The species. */
	// SpeciesDescription species;

	final String alias;

	/** The macro species. */
	final String macroSpecies;

	/**
	 * Instantiates a new gama agent type.
	 *
	 * @param species
	 *            the species
	 * @param name
	 *            the name
	 * @param speciesId
	 *            the species id
	 * @param base
	 *            the base
	 */
	public GamaAgentType(final SpeciesDescription species, final String name, final int speciesId,
			final Class<IAgent> base) {
		// this.species = species;
		DEBUG.OUT("Creating type " + name + " for SpeciesDescription " + species + " in model "
				+ (species == null ? "null" : species.getModelDescription()));
		ModelDescription md = species == null ? null : species.getModelDescription();
		alias = md == null ? null : md.getAlias();
		macroSpecies = species == null || species.getEnclosingDescription() == null ? null
				: species.getEnclosingDescription().getName();
		this.name = name;
		id = speciesId;
		support = base;
		// supports = new Class[] { base };
		if (species != null) { setDefiningPlugin(species.getDefiningPlugin()); }
	}

	@Override
	public boolean isAssignableFrom(final IType<?> t) {
		final boolean assignable = super.isAssignableFrom(t);
		// Hack to circumvent issue #1999. Should be better handled by
		// letting type managers of comodels inherit from the type managers
		// of imported models.
		if (!assignable && t.isAgentType() && t.getSpeciesName() == getSpeciesName()) return true;
		return assignable;
	}

	@Override
	public IAgent cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		if (obj == null || scope == null || scope.getModel() == null) return null;
		ISpecies species = (ISpecies) param;
		if (species == null) { species = scope.getModel().getSpecies(getSpeciesName()); }
		if (species == null) return (IAgent) Types.AGENT.cast(scope, obj, param, copy);
		if (obj instanceof IAgent) return ((IAgent) obj).isInstanceOf(species, false) ? (IAgent) obj : null;
		if (obj instanceof Integer) return scope.getAgent().getPopulationFor(species).getAgent((Integer) obj);
		if (obj instanceof GamaPoint)
			return scope.getAgent().getPopulationFor(species).getAgent(scope, (GamaPoint) obj);
		return null;
	}

	@Override
	public IAgent getDefault() { return null; }

	@Override
	public boolean isAgentType() { return true; }

	@Override
	public String getSpeciesName() { return name; }

	@Override
	public SpeciesDescription getSpecies(final IDescription context) {
		if (alias == null || alias.isBlank()) return context.getSpeciesDescription(getSpeciesName());
		return context.getModelDescription().getMicroModel(alias).getSpeciesDescription(getSpeciesName());
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}

	@Override
	public boolean canBeTypeOf(final IScope scope, final Object obj) {
		final boolean b = super.canBeTypeOf(scope, obj);
		if (b) return true;
		if (obj instanceof IAgent) {
			final ISpecies s = scope.getModel().getSpecies(getSpeciesName());
			return ((IAgent) obj).isInstanceOf(s, false);
		}
		return false;
	}

	@Override
	public String getSupportName() { return ", type of agents instances of species " + name; }

	@Override
	public IType<String> getKeyType() { return Types.STRING; }

	@Override
	public boolean isFixedLength() { return false; }

	@Override
	public boolean isDrawable() { return true; }

	/**
	 * Gets the alias of micro model.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the alias of micro model
	 * @date 20 janv. 2024
	 */
	public String getAliasOfMicroModel() { return alias; }

	/**
	 * Gets the name of macro species.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the name of macro species
	 * @date 20 janv. 2024
	 */
	public String getNameOfMacroSpecies() { return macroSpecies; }
}
