/*******************************************************************************************************
 *
 * GamaFileConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
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
import msi.gama.util.file.IGamaFile;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.gamaType.reduced.GamaFileReducer;

/**
 * The Class GamaFileConverter.
 */
public class GamaFileConverter extends AbstractGamaConverter<IGamaFile, IGamaFile> {

	/**
	 * Instantiates a new gama file converter.
	 *
	 * @param target
	 *            the target
	 */
	public GamaFileConverter(final Class<IGamaFile> target) {
		super(target);
	}

	@Override
	public void write(IScope scope, final IGamaFile gamaFile,
			final HierarchicalStreamWriter writer, final MarshallingContext context) {
		DEBUG.OUT("ConvertAnother : GamaFileConverter " + gamaFile.getClass());
		context.convertAnother(new GamaFileReducer(getScope(), gamaFile));
		DEBUG.OUT("===========END ConvertAnother : GamaFile");
	}

	@Override
	public IGamaFile read(IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		final GamaFileReducer rmt = (GamaFileReducer) context.convertAnother(null, GamaFileReducer.class);
		return rmt.constructObject(getScope());
	}

}
