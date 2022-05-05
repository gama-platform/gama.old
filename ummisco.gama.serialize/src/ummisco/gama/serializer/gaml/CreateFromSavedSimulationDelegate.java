/*******************************************************************************************************
 *
 * CreateFromSavedSimulationDelegate.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package ummisco.gama.serializer.gaml;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.CreateStatement;
import msi.gaml.types.IType;

import msi.gama.common.interfaces.ICreateDelegate;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gaml.types.Types;
import ummisco.gama.serializer.factory.StreamConverter;
import ummisco.gama.serializer.gamaType.converters.ConverterScope;

/**
 * Class CreateFromSavecSimulationDelegate.
 *
 * @author bgaudou
 * @since 18 July 2018
 *
 */

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CreateFromSavedSimulationDelegate implements ICreateDelegate {

	/**
	 * Method acceptSource()
	 * 
	 * @see msi.gama.common.interfaces.ICreateDelegate#acceptSource(IScope, java.lang.Object)
	 */
	@Override
	public boolean acceptSource(IScope scope, final Object source) {
		return source instanceof GamaSavedSimulationFile;
	}

	/**
	 * @see msi.gama.common.interfaces.ICreateDelegate#createFrom(msi.gama.runtime.IScope,
	 *      java.util.List, int, java.lang.Object)
	 */
	@Override
	public boolean createFrom(final IScope scope, final List<Map<String, Object>> inits, final Integer max,
			final Object source, final Arguments init, final CreateStatement statement) {
		final GamaSavedSimulationFile file = (GamaSavedSimulationFile) source;

		SimulationPopulation pop = scope.getExperiment().getSimulationPopulation();
		
		if (pop == null) pop = (SimulationPopulation) GamaListFactory.EMPTY_LIST;
		// final boolean hasSequence = sequence != null && !sequence.isEmpty();
		boolean shouldBeScheduled = false;

		// Create an empty new simulation, that is necesssary to load the SavedAgents.
		List<Map<String, Object>> mock_inits = GamaListFactory.create(Types.MAP, 1);
		mock_inits.add(Collections.EMPTY_MAP);
		pop.createAgents(scope, 1, mock_inits, false, shouldBeScheduled, null);
		
		
		final ConverterScope cScope = new ConverterScope(scope);
		final XStream xstream = StreamConverter.loadAndBuild(cScope,cScope.getClass());

		String stringFile = file.getBuffer().get(0);
		final SavedAgent saveAgt = (SavedAgent) xstream.fromXML(stringFile);
		
		HashMap mapSavedAgt = new HashMap<String, Object>();
		mapSavedAgt.put("SavedAgent", saveAgt);

		// Dispose the empty simulation, created only to read/load the saved agent
		scope.getSimulation().dispose();
		
		inits.add(mapSavedAgt);
		
//		scope.getSimulation().dispose();
		
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
