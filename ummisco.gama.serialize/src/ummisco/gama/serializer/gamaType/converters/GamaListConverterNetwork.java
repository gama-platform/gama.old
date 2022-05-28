/*******************************************************************************************************
 *
 * GamaListConverterNetwork.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
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

import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.gamaType.reduced.GamaListReducerNetwork;

/**
 * The Class GamaListConverterNetwork.
 */
public class GamaListConverterNetwork extends AbstractGamaConverter<IList, IList> {

	/**
	 * Instantiates a new gama list converter network.
	 *
	 * @param target
	 *            the target
	 */
	public GamaListConverterNetwork(final Class<IList> target) {
		super(target);
	}

	@Override
	public void write(IScope scope, final IList list, final HierarchicalStreamWriter writer, final MarshallingContext arg2) {
		DEBUG.OUT("ConvertAnother : GamaList " + list.getClass() + " " + list.getGamlType().getContentType());
		arg2.convertAnother(new GamaListReducerNetwork(list));
		DEBUG.OUT("END --- ConvertAnother : GamaList ");
	}

	@Override
	public IList read(IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		final GamaListReducerNetwork rmt =
				(GamaListReducerNetwork) arg1.convertAnother(null, GamaListReducerNetwork.class);
		return rmt.constructObject(getScope());
	}

}
