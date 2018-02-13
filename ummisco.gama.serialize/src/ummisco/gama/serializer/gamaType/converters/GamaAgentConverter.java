/*********************************************************************************************
 *
 * 'GamaAgentConverter.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.AbstractAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation.MinimalGridAgent;

import java.util.List;

import org.apache.commons.lang.ClassUtils;

import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.*;

@SuppressWarnings({ "rawtypes" })
public class GamaAgentConverter implements Converter {

	ConverterScope convertScope;
	
	public GamaAgentConverter(ConverterScope s){
		convertScope = s;
	}
	
	@Override
	public boolean canConvert(final Class arg0) {
		if(GamlAgent.class.equals(arg0) || MinimalAgent.class.equals(arg0) || GamlAgent.class.equals(arg0.getSuperclass())){
			return true;
		}
		
		if(MinimalGridAgent.class.equals(arg0)) {
			return true;
		}
		final List<Class<?>> allClassesApa = ClassUtils.getAllSuperclasses(arg0);
		for (final Object c : allClassesApa) {
			if (c.equals(GamlAgent.class))
				return true;
		}
		
		Class<?>[] allInterface=arg0.getInterfaces();
		for( Class<?> c:allInterface)
		{
			if(c.equals(GamlAgent.class))
				return true;
		}
		
		return false;
		// return (arg0.equals(GamlAgent.class) || arg0.equals(MinimalAgent.class));
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		// MinimalAgent agt = (MinimalAgent) arg0;
		AbstractAgent agt = (AbstractAgent) arg0;
		
		writer.startNode("agentReference");
		System.out.println("ConvertAnother : AgentConverter " + agt.getClass());
		writer.setValue(agt.getName());
		System.out.println("===========END ConvertAnother : GamaAgent truc youpi");
		
		writer.endNode();
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		// TODO manage MinimalAgent and MinimalGridAgent 
		reader.moveDown();
		SimulationAgent simAgt = convertScope.getSimulationAgent();
		List<IAgent> lagt;
		if(simAgt == null) {
			lagt = (convertScope.getScope()).getSimulation().getAgents(convertScope.getScope());
		} else {
			lagt = simAgt.getAgents(convertScope.getScope());
		}
		
		boolean found = false;
		int i = 0;
		IAgent agt = null;
		while(!found && (i < lagt.size())) {
			if(lagt.get(i).getName().equals(reader.getValue())) {
				found = true;
				agt = lagt.get(i);
			}
			i++;
		}
		reader.moveUp();
		return agt;
	}

}
