/*********************************************************************************************
 * 
 *
 * 'GamaScopeConverter.java', in plugin 'ummisco.gama.communicator', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import msi.gaml.descriptions.IDescription;
import msi.gaml.variables.IVariable;

import java.util.Collection;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class GamaScopeConverter implements Converter {

	@Override
	public boolean canConvert(Class arg0) {
		if((ExperimentAgent.ExperimentAgentScope.class).equals(arg0)){return true;}
		
		Class<?>[] allInterface=arg0.getInterfaces();
		for( Class<?> c:allInterface)
		{
			if(c.equals(IScope.class))
				return true;
		}
		return false;
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext context) {
		IScope scope = (IScope) arg0;
		writer.startNode("IScope");
        writer.setValue(scope.getName().toString());
        writer.endNode();

        // The experiment ???
        
		writer.startNode("Simulations");
        ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
        // The model / global
        //         IModel model = expAgt.getModel();
        // Collection<IVariable> vars = model.getVars();		

        
		SimulationPopulation simPop = expAgt.getSimulationPopulation();
		
		for(IAgent agt : expAgt.getSimulationPopulation()){
			// Each simulation 			
			SimulationAgent simAgt = (SimulationAgent) agt;
			System.out.println("ConvertAnother : ScopeConverter " + agt.getClass());
    		context.convertAnother(agt);
		}
		
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
