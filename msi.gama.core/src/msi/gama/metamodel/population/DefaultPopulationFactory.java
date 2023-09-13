/*******************************************************************************************************
 *
 * DefaultPopulationFactory.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.population;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.metamodel.topology.grid.GridPopulation;
import msi.gama.metamodel.topology.grid.IGridAgent;
import msi.gama.runtime.IScope;
import msi.gaml.species.ISpecies;

/**
 * A factory for creating DefaultPopulation objects.
 */
public class DefaultPopulationFactory implements IPopulationFactory {

	@Override
	public <E extends IAgent> IPopulation<E> createRegularPopulation(final IScope scope, final IMacroAgent host,
			final ISpecies species) {
		return new GamaPopulation<>(host, species);
	}

	@SuppressWarnings ("unchecked")
	@Override
	public IPopulation<IGridAgent> createGridPopulation(final IScope scope, final IMacroAgent host,
			final ISpecies species) {
		final ITopology t = GridPopulation.buildGridTopology(scope, species, host);
		final GamaSpatialMatrix m = (GamaSpatialMatrix) t.getPlaces();
		return new GridPopulation(m, t, host, species);
	}

}
