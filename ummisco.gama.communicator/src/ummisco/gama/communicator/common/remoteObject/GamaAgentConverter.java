package ummisco.gama.communicator.common.remoteObject;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class GamaAgentConverter implements Converter {

	public GamaAgentConverter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canConvert(Class arg0) {
		return arg0.equals(GamlAgent.class);
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer,
			MarshallingContext arg2) {
			GamlAgent agt = (GamlAgent)arg0;
			arg2.convertAnother(new RemoteAgent(agt));
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext arg1) {
		
			reader.moveDown();
			RemoteAgent rmt =(RemoteAgent )arg1.convertAnother(null, RemoteAgent.class);
			reader.moveUp();
		return rmt; // ragt;
	}

}
