/*******************************************************************************************************
 *
 * ReferenceAgentConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
import msi.gama.runtime.IScope;
import ummisco.gama.serializer.gamaType.reference.ReferenceAgent;
import ummisco.gama.serializer.gamaType.reference.ReferenceToAgent;

/**
 * The Class ReferenceAgentConverter.
 */
@SuppressWarnings ({ "rawtypes" })
public class ReferenceAgentConverter extends AbstractGamaConverter<ReferenceAgent, ReferenceAgent> {

	/**
	 * Instantiates a new reference agent converter.
	 *
	 * @param target
	 *            the target
	 */
	public ReferenceAgentConverter(final Class<ReferenceAgent> target) {
		super(target);
	}

	@Override
	public void write(final IScope scope, final ReferenceAgent refSavedAgt, final HierarchicalStreamWriter writer,
			final MarshallingContext context) {
		writer.startNode("attributeValue");
		context.convertAnother(refSavedAgt.getAttributeValue());
		writer.endNode();
	}

	@Override
	public ReferenceAgent read(final IScope scope, final HierarchicalStreamReader reader,
			final UnmarshallingContext arg1) {
		reader.moveDown();
		try {
			return new ReferenceAgent(null, null, (ReferenceToAgent) arg1.convertAnother(null, ReferenceToAgent.class));
		} finally {
			reader.moveUp();
		}
	}

}
