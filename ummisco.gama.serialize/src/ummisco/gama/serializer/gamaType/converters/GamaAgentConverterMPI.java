 /* GamaAgentConverterNetwork.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.agent.MutableSavedAgent;
import msi.gama.metamodel.agent.SavedAgent;
import ummisco.gama.dev.utils.DEBUG;
import msi.gaml.descriptions.ModelDescription;

/**
 * The Class GamaAgentConverterNetwork.
 */
public class GamaAgentConverterMPI implements Converter {

	/** The convert scope. */
	ConverterScope convertScope;

	/**
	 * Instantiates a new gama agent converter network.
	 *
	 * @param s the s
	 */
	public GamaAgentConverterMPI(final ConverterScope s) {
		convertScope = s;
	}

	@Override
	public boolean canConvert(final Class arg0) {
		// return (arg0.equals(GamlAgent.class) ||
		// arg0.equals(MinimalAgent.class));
		if (GamlAgent.class.equals(arg0) || MinimalAgent.class.equals(arg0)
				|| GamlAgent.class.equals(arg0.getSuperclass())) {
			return true;
		}

		return false;
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		System.out.println("marshal ---------------------------------------------------");
		final IAgent agt = (IAgent) arg0;
		System.out.println(" MARSHAL UNIQUE ID = " + agt.getUniqueID());
		System.out.println("Alias = "+ ((ModelDescription) agt.getScope().getSimulation().getSpecies().getDescription()).getAlias());
		context.convertAnother(new SavedAgent(convertScope.getScope(), agt));
		DEBUG.OUT("===========END ConvertAnother : GamaAgent Network");
		
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {

		System.out.println("unmarshal MPI ");
		
		MutableSavedAgent msa = new MutableSavedAgent();
		SavedAgentProvider.push(msa);
		
		arg1.convertAnother(msa, SavedAgent.class);
		SavedAgentProvider.pop();
		System.out.println("msa.getAlias()+\".\"+msa.getSource() = "+msa.getAlias()+"."+msa.getSource());
		System.out.println("msa.getUniqueID() = "+msa.getUniqueID());
		System.out.println("convertScope.scope.getSimulation().getExternMicroPopulationFor(\"pp.movingExp\") = "+convertScope.scope.getSimulation().getExternMicroPopulationFor(msa.getAlias()+"."+msa.getSource()));
		// todo change pp.movingExp by the true experiment
		return msa.restoreToMPI(convertScope.getScope(), ((ExperimentAgent)convertScope.scope.getSimulation().getExternMicroPopulationFor(msa.getAlias()+"."+msa.getSource()).getAgent(0)).getSimulation().getMicroPopulation(msa.getSpecies()), msa.getUniqueID());
	}

}
