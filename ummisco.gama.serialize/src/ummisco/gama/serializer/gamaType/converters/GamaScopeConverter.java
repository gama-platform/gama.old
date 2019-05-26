/*********************************************************************************************
 *
 * 'GamaScopeConverter.java, in plugin ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
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

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import ummisco.gama.dev.utils.DEBUG;

public class GamaScopeConverter implements Converter {

	@Override
	public boolean canConvert(final Class arg0) {
		if (ExperimentAgent.ExperimentAgentScope.class.equals(arg0)) { return true; }

		final Class<?>[] allInterface = arg0.getInterfaces();
		for (final Class<?> c : allInterface) {
			if (c.equals(IScope.class)) { return true; }
		}
		return false;
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		final IScope scope = (IScope) arg0;
		writer.startNode("IScope");
		writer.setValue(scope.getName().toString());
		writer.endNode();

		// The experiment ???

		writer.startNode("Simulations");
		final ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		// The model / global
		// IModel model = expAgt.getModel();
		// Collection<IVariable> vars = model.getVars();

		// SimulationPopulation simPop = expAgt.getSimulationPopulation();

		for (final IAgent agt : expAgt.getSimulationPopulation()) {
			// Each simulation
			// SimulationAgent simAgt = (SimulationAgent) agt;
			// System.out.println("ConvertAnother : ScopeConverter " + agt.getClass());
			DEBUG.OUT("ConvertAnother : ScopeConverter " + agt.getClass());
			context.convertAnother(agt);
		}

		writer.endNode();
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		reader.moveDown();
		final String res = reader.getValue();
		reader.moveUp();

		return res;
	}

}
