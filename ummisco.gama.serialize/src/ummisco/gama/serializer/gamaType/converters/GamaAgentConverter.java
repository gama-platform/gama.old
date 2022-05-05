/*******************************************************************************************************
 *
 * GamaAgentConverter.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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

/**
 * The Class GamaAgentConverter.
 */
@SuppressWarnings ({ "rawtypes" })
public class GamaAgentConverter implements Converter {

	/** The convert scope. */
	ConverterScope convertScope;

	/**
	 * Instantiates a new gama list converter.
	 *
	 * @param s the s
	 */
	public GamaAgentConverter(final ConverterScope s) {
		convertScope = s;
	}

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
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		// MinimalAgent agt = (MinimalAgent) arg0;
		final AbstractAgent agt = (AbstractAgent) arg0;

		writer.startNode("agentReference");
		DEBUG.OUT("ConvertAnother : AgentConverter " + agt.getClass());
		// System.out.println("ConvertAnother : AgentConverter " + agt.getClass());

		final ReferenceAgent refAft = new ReferenceAgent(null, null, agt);
		context.convertAnother(refAft);

		DEBUG.OUT("===========END ConvertAnother : GamaAgent");
		// System.out.println("===========END ConvertAnother : GamaAgent");

		writer.endNode();
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		// TODO manage MinimalAgent and MinimalGridAgent
		reader.moveDown();
		final ReferenceAgent agt = (ReferenceAgent) arg1.convertAnother(null, ReferenceAgent.class);
		reader.moveUp();
		
//		return agt.getReferencedAgent(convertScope.getScope().getSimulation());
		return  agt;
	}

}
