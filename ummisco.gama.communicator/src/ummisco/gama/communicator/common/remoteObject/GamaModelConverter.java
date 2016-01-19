package ummisco.gama.communicator.common.remoteObject;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.kernel.model.GamlModelSpecies;
import msi.gama.metamodel.agent.GamlAgent;

public class GamaModelConverter implements Converter{

	@Override
	public boolean canConvert(final Class arg0) {
		return (GamlModelSpecies.class.equals(arg0));
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		GamlAgent agt = (GamlAgent) arg0;
//		context.convertAnother(new RemoteAgent(agt));
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {

		reader.moveDown();
		RemoteAgent rmt = (RemoteAgent) arg1.convertAnother(null, RemoteAgent.class);
		reader.moveUp();
		return rmt; // ragt;
	}

}