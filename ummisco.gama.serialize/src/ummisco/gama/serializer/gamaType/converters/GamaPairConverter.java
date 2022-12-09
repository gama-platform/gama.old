/*******************************************************************************************************
 *
 * GamaPairConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
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
import msi.gama.util.GamaPair;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.gamaType.reduced.GamaPairReducer;

/**
 * The Class GamaPairConverter.
 */
@SuppressWarnings ({ "rawtypes" })
public class GamaPairConverter extends AbstractGamaConverter<GamaPair, GamaPair> {

	/**
	 * Instantiates a new gama pair converter.
	 *
	 * @param target
	 *            the target
	 */
	public GamaPairConverter(final Class<GamaPair> target) {
		super(target);
	}

	@Override
	public void write(IScope scope, final GamaPair mp, final HierarchicalStreamWriter arg1, final MarshallingContext arg2) {
		DEBUG.OUT("ConvertAnother : GamaPair " + mp.getClass());
		arg2.convertAnother(new GamaPairReducer(mp));
		DEBUG.OUT("END -- ConvertAnother : GamaPair " + mp.getClass());
	}

	@Override
	public GamaPair read(IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		final GamaPairReducer rmt = (GamaPairReducer) context.convertAnother(null, GamaPairReducer.class);
		return rmt.constructObject();
	}

}
