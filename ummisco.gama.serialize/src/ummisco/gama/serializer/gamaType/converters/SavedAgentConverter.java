/*********************************************************************************************
 * 
 * 
 * 'GamaAgentConverter.java', in plugin 'ummisco.gama.communicator', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.variables.IVariable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.*;

import gnu.trove.map.hash.THashMap;

public class SavedAgentConverter implements Converter {
	
	private final static String TAG="IMacroAgent";
	ConverterScope convertScope;
	
	public SavedAgentConverter(ConverterScope s){
		convertScope = s;
	}
	
	@Override
	public boolean canConvert(final Class arg0) {
		return (arg0.equals(SavedAgent.class));
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		System.out.println("ConvertAnother : GamaSavedAgentConverter " + arg0.getClass());		
		SavedAgent savedAgt = (SavedAgent) arg0;
				
		writer.startNode("variables");
		context.convertAnother(savedAgt.getVariables());
		writer.endNode();
		
		writer.startNode("innerPopulations");
		Map<String, List<SavedAgent>> inPop = savedAgt.getInnerPopulations();
		if(inPop == null) {
			context.convertAnother(new THashMap<String, Object>(11, 0.9f));
		} else {
			context.convertAnother(inPop);	
		}
		writer.endNode();
		
		writer.startNode(TAG);
		context.convertAnother(new Boolean(inPop==null?false:true));
	    writer.endNode();		
		
		System.out.println("===========END ConvertAnother : GamaSavedAgentConverter");	
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		reader.moveDown();
		Map<String, Object> v = (Map<String, Object>) arg1.convertAnother(null, THashMap.class);
		reader.moveUp();
		
		reader.moveDown();
		Map<String, List<SavedAgent>> inPop = (Map<String, List<SavedAgent>>) arg1.convertAnother(null, THashMap.class);
		reader.moveUp();
		
		reader.moveDown();
		Boolean isIMacroAgent = (Boolean) arg1.convertAnother(null, Boolean.class);
		reader.moveUp();

		SavedAgent agtToReturn = new SavedAgent(v, (isIMacroAgent.booleanValue())?inPop:null);

		SimulationAgent simAgent = convertScope.getSimulationAgent();
		
		// The unserialization requieres 2 steps. 
		// After the first step: the SavedAgent is restored. simAgt is null during the first step.
		// After the second step: the simAgt is not null. Its agents variables are only updated.
		if(simAgent != null){		
			// get the existing agent with the same name in the simulationAgent
			// update/replace its variables

			String savedAgtName = (String) agtToReturn.getAttributeValue("name");
			
			List<IAgent> lagt = simAgent.getAgents(convertScope.getScope());
			boolean found = false;
			int i = 0;
			IAgent agt = null;
			while(!found && (i < lagt.size())) {	
				if(lagt.get(i).getName().equals(savedAgtName)) {
					found = true;
					agt = lagt.get(i);
				}
				i++;
			}
			// If the agent is not found in the simAgent agents, it should be the simulationAgent itself
			if(agt == null) {
				if (savedAgtName.equals(simAgent.getName())) {
					agt = simAgent;
				}
			}
			
			if(agt != null) {
				// We have in agt the chosen agent we need to update variables
				final List<Map> agentAttrs = new ArrayList<Map>();
				agentAttrs.add(agtToReturn.getVariables());
				ArrayList<IAgent> agentsList = new ArrayList<>();
				agentsList.add(agt);			
				GamaPopulation pop = (GamaPopulation) agt.getPopulation();
				
				pop.createAndUpdateVariablesFor(convertScope.getScope(), agentsList, agentAttrs, true);
				
			} 
		} 
		return agtToReturn; 
	}

}
