/*******************************************************************************************************
 *
 * DefaultPopulationFactory.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package proxyExperiment;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.population.IPopulationFactory;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.runtime.IScope;
import msi.gaml.species.ISpecies;
import proxyPopulation.ProxyPopulation;

/**
 * A factory for creating ProxyPopulation objects.
 */
public class ProxyPopulationFactory implements IPopulationFactory {

	@Override
	public <E extends IAgent> IPopulation<E> createRegularPopulation(final IScope scope, final IMacroAgent host,
			final ISpecies species) {
		System.out.println("ProxyPopulationFactory return new Population " + species.getName());
		return (IPopulation<E>) new ProxyPopulation(host, species);
	}

	@Override
	public <E extends IAgent> IPopulation<E> createGridPopulation(final IScope scope, final IMacroAgent host,
			final ISpecies species) {
		final ITopology t = GamaPopulation.buildGridTopology(scope, species, host);
		final GamaSpatialMatrix m = (GamaSpatialMatrix) t.getPlaces();
		return m.new GridPopulation<>(t, host, species);
	} 

}
