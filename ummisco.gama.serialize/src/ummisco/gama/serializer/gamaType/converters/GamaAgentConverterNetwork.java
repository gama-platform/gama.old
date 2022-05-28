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
	public void write(final IScope scope, final IAgent agt, final HierarchicalStreamWriter writer,
			final MarshallingContext context) {
		context.convertAnother(new SavedAgent(scope, agt));
		DEBUG.OUT("===========END ConvertAnother : GamaAgent Network");
	}

	@Override
	public SavedAgent read(final IScope scope, final HierarchicalStreamReader reader,
			final UnmarshallingContext context) {
		MutableSavedAgent msa = new MutableSavedAgent();
		SavedAgentProvider.push(msa);
		context.convertAnother(msa, SavedAgent.class);
		SavedAgentProvider.pop();
		return msa;
	}

}
