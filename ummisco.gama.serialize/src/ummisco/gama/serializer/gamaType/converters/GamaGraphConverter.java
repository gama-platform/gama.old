/*******************************************************************************************************
 *
 * GamaGraphConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
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

import msi.gama.runtime.IScope;
import msi.gama.util.graph.IGraph;
import ummisco.gama.serializer.gamaType.reduced.GamaGraphReducer;

/**
 * The Class GamaGraphConverter.
 */
public class GamaGraphConverter extends AbstractGamaConverter<IGraph, IGraph> {

	/**
	 * Instantiates a new gama graph converter.
	 *
	 * @param target
	 *            the target
	 */
	public GamaGraphConverter(final Class<IGraph> target) {
		super(target);
	}

	@Override
	public void write(final IScope scope, final IGraph graph, final HierarchicalStreamWriter writer,
			final MarshallingContext arg2) {
		arg2.convertAnother(new GamaGraphReducer(scope, graph));
	}

	@Override
	public IGraph read(final IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		final GamaGraphReducer rmt = (GamaGraphReducer) context.convertAnother(null, GamaGraphReducer.class);
		return rmt.constructObject(scope);
	}

}
