package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.util.GamaList;
import msi.gaml.species.AbstractSpecies;
import msi.gaml.species.GamlSpecies;

public class GamaSpeciesConverter implements Converter {
	ConverterScope convertScope;
	
	public GamaSpeciesConverter(ConverterScope s){
		convertScope = s;
	}
	
	@Override
	public boolean canConvert(final Class arg0) {		
		return (GamlSpecies.class.equals(arg0) || AbstractSpecies.class.equals(arg0.getSuperclass()));
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext context) {
		System.out.println("ConvertAnother : ConvertGamaSpecies " + arg0.getClass());		
		AbstractSpecies spec = (AbstractSpecies) arg0;
		GamaPopulation pop = (GamaPopulation) spec.getPopulation(convertScope.getScope());
		
		writer.startNode("agentSetFromPopulation");
		context.convertAnother((GamaList<IAgent>) pop.getAgents(convertScope.getScope()));
		writer.endNode();		
	
		System.out.println("===========END ConvertAnother : ConvertGamaSpecies");				
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

		reader.moveDown();
		GamaList<IAgent> listAgetFromPopulation = (GamaList<IAgent>) context.convertAnother(null, GamaList.class);
		reader.moveUp();
		
		return listAgetFromPopulation;
	}

}
