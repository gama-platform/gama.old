/*******************************************************************************************************
 *
 * GamaAgentConverterNetwork.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.agent.MutableSavedAgent;
import msi.gama.metamodel.agent.SavedAgent;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaAgentConverterNetwork.
 */
public class GamaAgentConverterNetwork implements Converter {

	/** The convert scope. */
	ConverterScope convertScope;

	/**
	 * Instantiates a new gama agent converter MPI.
	 *
	 * @param s the s
	 */
	public GamaAgentConverterNetwork(final ConverterScope s) {
		convertScope = s;
	}

	@Override
	public boolean canConvert(final Class arg0) {
		if (GamlAgent.class.equals(arg0) || MinimalAgent.class.equals(arg0)
				|| GamlAgent.class.equals(arg0.getSuperclass())) {
			return true;
		}

		return false;
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		final IAgent agt = (IAgent) arg0;
	//	writer.startNode("save_agent network");
		context.convertAnother(new SavedAgent(convertScope.getScope(), agt));
		DEBUG.OUT("===========END ConvertAnother : GamaAgent Network");
	//	System.out.println("===========END ConvertAnother : GamaAgent Network");
	//	writer.endNode();
		
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		
		
		MutableSavedAgent msa = new MutableSavedAgent();
//		var converter = new SavedAgentConverter(convertScope);
//		arg1.convertAnother(msa, getClass())
//		return converter.unmarshal(reader, arg1, msa);
//		
		SavedAgentProvider.push(msa);
		arg1.convertAnother(msa, SavedAgent.class);
		SavedAgentProvider.pop();
		
		return (SavedAgent)msa;
		//return msa.restoreTo(convertScope.scope, convertScope.scope.getSimulation().getPopulationFor(msa.getSpecies()));
//		var context = new AgentUnmarshallingContext(arg1, msa);
//		context.convertAnother(msa, SavedAgent.class, null);
//		msa = (MutableSavedAgent) arg1.convertAnother(msa, SavedAgent.class);
		//reader.moveUp();
	//	System.out.println("lecture d'un save agent " + rmt.getName()+" " +rmt.values());
		
//		return msa;
		
	}

}
