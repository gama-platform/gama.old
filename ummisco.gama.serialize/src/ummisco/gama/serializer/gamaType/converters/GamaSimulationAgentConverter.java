/*********************************************************************************************
 *
 * 'GamaSimulationAgentConverter.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.kernel.simulation.SimulationAgent;
import ummisco.gama.serializer.gamaType.reduced.GamaSimulationAgentReducer;
import ummisco.gama.serializer.gamaType.reduced.RemoteAgent;

public class GamaSimulationAgentConverter implements Converter{

	@Override
	public boolean canConvert(final Class arg0) {
		return (SimulationAgent.class.equals(arg0));
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		SimulationAgent simAgt = (SimulationAgent) arg0;
		writer.startNode("oneSimulation");
		context.convertAnother(new GamaSimulationAgentReducer(simAgt));
		writer.endNode();
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {

		reader.moveDown();
		RemoteAgent rmt = (RemoteAgent) arg1.convertAnother(null, RemoteAgent.class);
		reader.moveUp();
		return rmt; 
	}

}