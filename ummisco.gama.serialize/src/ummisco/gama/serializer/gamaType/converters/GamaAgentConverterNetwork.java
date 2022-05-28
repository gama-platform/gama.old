/*******************************************************************************************************
 *
 * GamaAgentConverterNetwork.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MutableSavedAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.runtime.IScope;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaAgentConverterNetwork.
 */
public class GamaAgentConverterNetwork extends AbstractGamaConverter<IAgent, SavedAgent> {

	/**
	 * Instantiates a new gama agent converter network.
	 *
	 * @param target
	 *            the target
	 */
	public GamaAgentConverterNetwork(final Class<IAgent> target) {
		super(target);
	}

	@Override
	public void write(IScope scope, final IAgent agt, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		// writer.startNode("save_agent network");
		context.convertAnother(new SavedAgent(getScope(), agt));
		DEBUG.OUT("===========END ConvertAnother : GamaAgent Network");
		// System.out.println("===========END ConvertAnother : GamaAgent Network");
		// writer.endNode();

	}

	@Override
	public SavedAgent read(IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {

		MutableSavedAgent msa = new MutableSavedAgent();
		// var converter = new SavedAgentConverter(convertScope);
		// arg1.convertAnother(msa, getClass())
		// return converter.unmarshal(reader, arg1, msa);
		//
		SavedAgentProvider.push(msa);
		var tmp = arg1.convertAnother(msa, SavedAgent.class);
		SavedAgentProvider.pop();
		return msa;
		// var context = new AgentUnmarshallingContext(arg1, msa);
		// context.convertAnother(msa, SavedAgent.class, null);
		// msa = (MutableSavedAgent) arg1.convertAnother(msa, SavedAgent.class);
		// reader.moveUp();
		// System.out.println("lecture d'un save agent " + rmt.getName()+" " +rmt.values());

		// return msa;

	}

}
