/**
 * Created by drogoul, 9 déc. 2013
 * 
 */
package msi.gama.metamodel.population;

import java.util.Collection;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;

/**
 * Class IPopulationSet. An interface common to ISpecies, IPopulation and MetaPopulation
 * 
 * @author drogoul
 * @since 9 déc. 2013
 * 
 */
public interface IPopulationSet extends IContainer<Integer, IAgent>, IAgentFilter {

	Collection<? extends IPopulation> getPopulations(IScope scope);

}
