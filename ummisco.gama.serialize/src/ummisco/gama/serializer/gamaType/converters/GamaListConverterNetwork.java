/*******************************************************************************************************
 *
 * GamaListConverterNetwork.java, in ummisco.gama.serialize, is part of the source code of the
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

import msi.gama.util.IList;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.gamaType.reduced.GamaListReducerNetwork;

/**
 * The Class GamaListConverterNetwork.
 */
@SuppressWarnings ({ "rawtypes" })
public class GamaListConverterNetwork implements Converter {

	/** The convert scope. */
	ConverterScope convertScope;

	/**
	 * Instantiates a new gama list converter network.
	 *
	 * @param s the s
	 */
	public GamaListConverterNetwork(final ConverterScope s) {
		convertScope = s;
	}

	@Override
	public boolean canConvert(final Class arg0) {
		return IList.class.isAssignableFrom(arg0); // ??
		// if (GamaList.class.equals(arg0)) {
		// return true;
		// }
		//
		// final Class<?>[] allInterface = arg0.getInterfaces();
		// for (final Class<?> c : allInterface) {
		// if (c.equals(GamaList.class))
		// return true;
		// }
		// return false;
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext arg2) {
		final IList list = (IList) arg0;

		// System.out.println("ConvertAnother : GamaList " + list.getClass()+ " "+list.getGamlType().getContentType());
		DEBUG.OUT("ConvertAnother : GamaList " + list.getClass() + " " + list.getGamlType().getContentType());
		arg2.convertAnother(new GamaListReducerNetwork(list));
		// System.out.println("END --- ConvertAnother : GamaList ");
		DEBUG.OUT("END --- ConvertAnother : GamaList ");
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		// reader.moveDown();
		final GamaListReducerNetwork rmt =
				(GamaListReducerNetwork) arg1.convertAnother(null, GamaListReducerNetwork.class);
		// reader.moveUp();
		return rmt.constructObject(convertScope.getScope());
	}

}
