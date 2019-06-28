/*********************************************************************************************
 *
 * 'GamaAgentConverter.java, in plugin ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import java.util.List;

import org.apache.commons.lang.ClassUtils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.metamodel.agent.AbstractAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation.MinimalGridAgent;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.gamaType.reference.ReferenceAgent;

@SuppressWarnings ({ "rawtypes" })
public class GamaAgentConverter implements Converter {

	public GamaAgentConverter(final ConverterScope s) {}

	@Override
	public boolean canConvert(final Class arg0) {
		if (ReferenceAgent.class.equals(arg0)) { return false; }

		if (GamlAgent.class.equals(arg0) || MinimalAgent.class.equals(arg0)
				|| GamlAgent.class.equals(arg0.getSuperclass())) {
			return true;
		}

		if (MinimalGridAgent.class.equals(arg0)) { return true; }
		final List<Class<?>> allClassesApa = ClassUtils.getAllSuperclasses(arg0);
		for (final Object c : allClassesApa) {
			if (c.equals(GamlAgent.class)) { return true; }
		}

		final Class<?>[] allInterface = arg0.getInterfaces();
		for (final Class<?> c : allInterface) {
			if (c.equals(GamlAgent.class)) { return true; }
		}

		return false;
		// return (arg0.equals(GamlAgent.class) || arg0.equals(MinimalAgent.class));
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		// MinimalAgent agt = (MinimalAgent) arg0;
		final AbstractAgent agt = (AbstractAgent) arg0;

		writer.startNode("agentReference");
		DEBUG.OUT("ConvertAnother : AgentConverter " + agt.getClass());
		// System.out.println("ConvertAnother : AgentConverter " + agt.getClass());

		// ReferenceSavedAgent refAft = new ReferenceSavedAgent(agt, null, (ReferenceToAgent) null);
		final ReferenceAgent refAft = new ReferenceAgent(null, null, agt);
		context.convertAnother(refAft);

		// writer.setValue(agt.getName());
		DEBUG.OUT("===========END ConvertAnother : GamaAgent");
		// System.out.println("===========END ConvertAnother : GamaAgent");

		writer.endNode();
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		// TODO manage MinimalAgent and MinimalGridAgent
		reader.moveDown();
		// SimulationAgent simAgt = convertScope.getSimulationAgent();
		// List<IAgent> lagt;
		// if(simAgt == null) {
		// lagt = (convertScope.getScope()).getSimulation().getAgents(convertScope.getScope());
		// } else {
		// lagt = simAgt.getAgents(convertScope.getScope());
		// }
		final ReferenceAgent agt = (ReferenceAgent) arg1.convertAnother(null, ReferenceAgent.class);

		// boolean found = false;
		// int i = 0;
		// IAgent agt = null;
		// while(!found && (i < lagt.size())) {
		// if(lagt.get(i).getName().equals(reader.getValue())) {
		// found = true;
		// agt = lagt.get(i);
		// }
		// i++;
		// }
		reader.moveUp();
		return agt;
	}

}
