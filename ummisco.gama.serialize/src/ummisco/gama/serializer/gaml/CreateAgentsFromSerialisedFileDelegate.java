/*******************************************************************************************************
 *
 * CreateSimulationFromFileDelegate.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package ummisco.gama.serializer.gaml;

import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.ICreateDelegate;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.CreateStatement;
import msi.gaml.statements.RemoteSequence;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.serializer.implementations.SerialisedAgentReader;

/**
 * Class CreateFromSavecSimulationDelegate.
 *
 * @author bgaudou
 * @since 18 July 2018
 *
 */

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class CreateAgentsFromSerialisedFileDelegate implements ICreateDelegate {

	@Override
	public boolean handlesCreation() {
		return true;
	}

	@Override
	public IList<? extends IAgent> createAgents(final IScope scope, final IPopulation<? extends IAgent> pop,
			final List<Map<String, Object>> inits, final CreateStatement statement, final RemoteSequence sequence) {
		IList<? extends IAgent> agents = pop.createAgents(scope, 1, inits, false, true, null);
		IAgent agent = agents.get(0);
		String path = (String) inits.get(0).get("saved_file");
		SerialisedAgentReader.getInstance().restoreFromFile(agent, path);
		// The sequence is executed only after the restoration
		scope.execute(sequence, agent, null);
		return agents;
	}

	/**
	 * Method acceptSource()
	 *
	 * @see msi.gama.common.interfaces.ICreateDelegate#acceptSource(IScope, java.lang.Object)
	 */
	@Override
	public boolean acceptSource(final IScope scope, final Object source) {
		return source instanceof GamaSavedSimulationFile;
	}

	/**
	 * @see msi.gama.common.interfaces.ICreateDelegate#createFrom(msi.gama.runtime.IScope, java.util.List, int,
	 *      java.lang.Object)
	 */
	@Override
	public boolean createFrom(final IScope scope, final List<Map<String, Object>> inits, final Integer max,
			final Object source, final Arguments init, final CreateStatement statement) {
		inits.add(Map.of("saved_file", ((GamaSavedSimulationFile) source).getPath(scope)));
		return true;
	}

	/**
	 * Method fromFacetType()
	 *
	 * @see msi.gama.common.interfaces.ICreateDelegate#fromFacetType()
	 */
	@Override
	public IType fromFacetType() {
		return Types.FILE;
	}
}
