package msi.gama.metamodel.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.species.ISpecies;

/**
 * A helper class to save agent and restore/recreate agent as a member of a population.
 */
public class SavedAgent {


	/** Variables which are not saved during the capture and release process. */
	static final List<String> UNSAVABLE_VARIABLES = Arrays.asList(IKeyword.PEERS, IKeyword.AGENTS,
		IKeyword.HOST, IKeyword.TOPOLOGY, IKeyword.MEMBERS, "populations");
		
		
	Map<String, Object> variables;
	Map<String, List<SavedAgent>> innerPopulations;

	public SavedAgent(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		saveAttributes(scope, agent);
		if ( agent instanceof IMacroAgent ) {
			saveMicroAgents(scope, (IMacroAgent) agent);
		}
	}

	/**
	 * Saves agent's attributes to a map.
	 *
	 * @param agent
	 * @throws GamaRuntimeException
	 */
	private void saveAttributes(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		variables = new THashMap<String, Object>(11, 0.9f);
		final ISpecies species = agent.getSpecies();
		for ( final String specVar : species.getVarNames() ) {
			if ( UNSAVABLE_VARIABLES.contains(specVar) ) {
				continue;
			}

			if ( species.getVar(specVar).value(scope, agent) instanceof IPopulation ) {
				continue;
			}

			if ( specVar.equals(IKeyword.SHAPE) ) {
				// variables.put(specVar, geometry.copy());
				// Changed 3/2/12: is it necessary to make the things below ?
				variables.put(specVar,
					new GamaShape(((GamaShape) species.getVar(specVar).value(scope, agent)).getInnerGeometry()));
				continue;
			}
			variables.put(specVar, species.getVar(specVar).value(scope, agent));
		}
	}

	/**
	 * Recursively save micro-agents of an agent.
	 *
	 * @param agent The agent having micro-agents to be saved.
	 * @throws GamaRuntimeException
	 */
	private void saveMicroAgents(final IScope scope, final IMacroAgent agent) throws GamaRuntimeException {
		innerPopulations = new THashMap<String, List<SavedAgent>>();

		for ( final IPopulation microPop : agent.getMicroPopulations() ) {
			final List<SavedAgent> savedAgents = new ArrayList<SavedAgent>();
			final Iterator<IAgent> it = microPop.iterator();
			while (it.hasNext()) {
				savedAgents.add(new SavedAgent(scope, it.next()));
			}

			innerPopulations.put(microPop.getSpecies().getName(), savedAgents);
		}
	}

	/**
	 * @param scope
	 * Restores the saved agent as a member of the target population.
	 *
	 * @param targetPopulation The population that the saved agent will be restored to.
	 * @return
	 * @throws GamaRuntimeException
	 */
	IAgent restoreTo(final IScope scope, final IPopulation targetPopulation) throws GamaRuntimeException {
		final List<Map> agentAttrs = new ArrayList<Map>();
		agentAttrs.add(variables);
		final List<? extends IAgent> restoredAgents = targetPopulation.createAgents(scope, 1, agentAttrs, true);
		restoreMicroAgents(scope, restoredAgents.get(0));

		return restoredAgents.get(0);
	}

	/**
	 *
	 *
	 * @param host
	 * @throws GamaRuntimeException
	 */
	void restoreMicroAgents(final IScope scope, final IAgent host) throws GamaRuntimeException {

		for ( final String microPopName : innerPopulations.keySet() ) {
			final IPopulation microPop = ((IMacroAgent) host).getMicroPopulation(microPopName);

			if ( microPop != null ) {
				final List<SavedAgent> savedMicros = innerPopulations.get(microPopName);
				final List<Map> microAttrs = new ArrayList<Map>();
				for ( final SavedAgent sa : savedMicros ) {
					microAttrs.add(sa.variables);
				}

				final List<? extends IAgent> microAgents =
					microPop.createAgents(scope, savedMicros.size(), microAttrs, true);

				for ( int i = 0; i < microAgents.size(); i++ ) {
					savedMicros.get(i).restoreMicroAgents(scope, microAgents.get(i));
				}
			}
		}
	}
}