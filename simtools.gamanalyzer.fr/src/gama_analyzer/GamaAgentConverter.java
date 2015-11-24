package gama_analyzer;

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
		return (GamlAgent.class).isAssignableFrom(arg0);
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer,
			MarshallingContext arg2) {
			GamlAgent agt = (GamlAgent)arg0;
			writer.startNode("GamlAgent");
            writer.setValue(agt.getName());
            writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext arg1) {
		 reader.moveDown();
		 String res = reader.getValue();
		 reader.moveUp();
		return res;
	}

}
