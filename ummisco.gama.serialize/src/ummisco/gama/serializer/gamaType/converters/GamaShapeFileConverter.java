/*******************************************************************************************************
 *
 * GamaShapeFileConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
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
import msi.gama.util.file.GamaShapeFile;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaShapeFileConverter.
 */
public class GamaShapeFileConverter extends AbstractGamaConverter<GamaShapeFile, GamaShapeFile> {

	/** The Constant TAG. */
	private final static String TAG = "GamaShapeFile";

	/**
	 * Instantiates a new gama shape file converter.
	 *
	 * @param target
	 *            the target
	 */
	public GamaShapeFileConverter(final Class<GamaShapeFile> target) {
		super(target);
	}

	@Override
	public void write(IScope scope, final GamaShapeFile shpFile,
			final HierarchicalStreamWriter writer, final MarshallingContext context) {
		DEBUG.OUT("ConvertAnother : GamaShapeFileConverter " + shpFile.getClass());
		writer.startNode(TAG);
		writer.setValue(shpFile.getFile(getScope()).getAbsolutePath());
		writer.endNode();
		DEBUG.OUT("===========END ConvertAnother : GamaShapeFile");
	}

	@Override
	public GamaShapeFile read(IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		reader.moveDown();
		final GamaShapeFile shp = new GamaShapeFile(getScope(), reader.getValue());
		reader.moveUp();
		return shp;
	}

}
