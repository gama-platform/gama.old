package gama_analyzer;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class GamaSimulationAgentConverter implements Converter {

	public GamaSimulationAgentConverter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canConvert(Class arg0) {
		return arg0.equals(SimulationAgent.class);
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer,
			MarshallingContext arg2) {
		SimulationAgent agt = (SimulationAgent)arg0;
			writer.startNode("SimulationAgent");
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
