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
import msi.gama.metamodel.agent.GamlAgent;
import ummisco.gama.serializer.gamaType.reduced.RemoteAgent;

import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.*;

public class GamaAgentConverter implements Converter {

	@Override
	public boolean canConvert(final Class arg0) {
		return (arg0.equals(GamlAgent.class)) || (ExperimentAgent.class.equals(arg0));
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		GamlAgent agt = (GamlAgent) arg0;
		writer.startNode("oneAgent");
		
		System.out.println("ConvertAnother : AgentConverter " + agt.getClass());		
		context.convertAnother(new RemoteAgent(agt));
		System.out.println("===========END ConvertAnother : GamaAgent");
		
		writer.endNode();
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {

		reader.moveDown();
		RemoteAgent rmt = (RemoteAgent) arg1.convertAnother(null, RemoteAgent.class);
		reader.moveUp();
		return rmt; // ragt;
	}

}
