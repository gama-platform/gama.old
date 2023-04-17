/*******************************************************************************************************
 *
 * GamaShapeFileConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
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
import msi.gama.util.file.GamaShapeFile;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaShapeFileConverter.
 */
public class GamaShapeFileConverter extends AbstractGamaConverter<GamaShapeFile, GamaShapeFile> {

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
	public void write(final IScope scope, final GamaShapeFile shpFile, final HierarchicalStreamWriter writer,
			final MarshallingContext context) {
		DEBUG.OUT("ConvertAnother : GamaShapeFileConverter " + shpFile.getClass());
		writer.startNode("GamaShapeFile");
		writer.setValue(shpFile.getFile(scope).getAbsolutePath());
		writer.endNode();
		DEBUG.OUT("===========END ConvertAnother : GamaShapeFile");
	}

	@Override
	public GamaShapeFile read(final IScope scope, final HierarchicalStreamReader reader,
			final UnmarshallingContext arg1) {
		reader.moveDown();
		try {
			return new GamaShapeFile(scope, reader.getValue());
		} finally {
			reader.moveUp();
		}
	}

}
