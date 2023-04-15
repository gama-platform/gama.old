/*******************************************************************************************************
 *
 * GamaPathConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
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
import msi.gama.util.path.GamaPath;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.gamaType.reduced.GamaPathReducer;

/**
 * The Class GamaPathConverter.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class GamaPathConverter extends AbstractGamaConverter<GamaPath, GamaPath> {

	/**
	 * Instantiates a new gama path converter.
	 *
	 * @param target
	 *            the target
	 */
	public GamaPathConverter(final Class<GamaPath> target) {
		super(target);
	}

	@Override
	public void write(final IScope scope, final GamaPath path, final HierarchicalStreamWriter writer,
			final MarshallingContext context) {
		DEBUG.OUT("ConvertAnother : GamaPath " + path.getClass());
		context.convertAnother(new GamaPathReducer(path));
		DEBUG.OUT("END -- ConvertAnother : GamaPath " + path.getClass());
	}

	@Override
	public GamaPath read(final IScope scope, final HierarchicalStreamReader reader,
			final UnmarshallingContext context) {
		final GamaPathReducer rmt = (GamaPathReducer) context.convertAnother(null, GamaPathReducer.class);
		return rmt.constructObject(scope);
	}

}
