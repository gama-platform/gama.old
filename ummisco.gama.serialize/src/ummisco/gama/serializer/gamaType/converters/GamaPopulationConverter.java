package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.util.GamaList;

public class GamaPopulationConverter implements Converter {
		
		ConverterScope convertScope;
		
		public GamaPopulationConverter(ConverterScope s){
			convertScope = s;
		}
		
		@Override
		public boolean canConvert(final Class arg0) {
			// TODO management of other GamaPopulation (grid)
			
			Class sc = arg0.getSuperclass();
			Class<?>[] allInterface=arg0.getInterfaces();
			for( Class<?> c:allInterface) {
				Class scs = c.getSuperclass();
			}

			
			
//			if(GamlAgent.class.equals(arg0) || MinimalAgent.class.equals(arg0) || GamlAgent.class.equals(arg0.getSuperclass())){
//				return true;
//			}
//			
//			Class<?>[] allInterface=arg0.getInterfaces();
//			for( Class<?> c:allInterface)
//			{
//				if(c.equals(GamlAgent.class))
//					return true;
//			}			
			
			return (arg0.equals(GamaPopulation.class));
		}

		@Override
		public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext context) {
			System.out.println("ConvertAnother : GamaPopulationConverter " + arg0.getClass());		
			GamaPopulation pop = (GamaPopulation) arg0;

			writer.startNode("agentSetFromPopulation");
			context.convertAnother((GamaList<IAgent>) pop.getAgents(convertScope.getScope()));
			writer.endNode();		
		
			System.out.println("===========END ConvertAnother : GamaSavedAgentConverter");				
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

			reader.moveDown();
			GamaList<IAgent> listAgetFromPopulation = (GamaList<IAgent>) context.convertAnother(null, GamaList.class);
			reader.moveUp();
			
			return listAgetFromPopulation;
		}

}
