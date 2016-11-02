/*********************************************************************************************
 *
 * 'GamaPopulationConverter.java, in plugin ummisco.gama.serialize, is part of the source code of the
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

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.util.GamaList;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GamaPopulationConverter implements Converter {

	ConverterScope convertScope;

	public GamaPopulationConverter(final ConverterScope s) {
		convertScope = s;
	}

	@Override
	public boolean canConvert(final Class arg0) {
		// TODO management of other GamaPopulation (grid)

		final Class sc = arg0.getSuperclass();
		final Class<?>[] allInterface = arg0.getInterfaces();
		for (final Class<?> c : allInterface) {
			final Class scs = c.getSuperclass();
		}

		// if(GamlAgent.class.equals(arg0) || MinimalAgent.class.equals(arg0) ||
		// GamlAgent.class.equals(arg0.getSuperclass())){
		// return true;
		// }
		//
		// Class<?>[] allInterface=arg0.getInterfaces();
		// for( Class<?> c:allInterface)
		// {
		// if(c.equals(GamlAgent.class))
		// return true;
		// }

		return arg0.equals(GamaPopulation.class);
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		System.out.println("ConvertAnother : GamaPopulationConverter " + arg0.getClass());
		final GamaPopulation pop = (GamaPopulation) arg0;

		writer.startNode("agentSetFromPopulation");
		context.convertAnother(pop.getAgents(convertScope.getScope()));
		writer.endNode();

		System.out.println("===========END ConvertAnother : GamaSavedAgentConverter");
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {

		reader.moveDown();
		final GamaList<IAgent> listAgetFromPopulation = (GamaList<IAgent>) context.convertAnother(null, GamaList.class);
		reader.moveUp();

		return listAgetFromPopulation;
	}

}
