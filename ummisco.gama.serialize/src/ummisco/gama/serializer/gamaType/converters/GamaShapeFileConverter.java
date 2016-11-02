/*********************************************************************************************
 *
 * 'GamaShapeFileConverter.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.util.file.GamaShapeFile;

public class GamaShapeFileConverter implements Converter {
	private final static String TAG = "GamaShapeFile";
	private final ConverterScope scope;

	public GamaShapeFileConverter(final ConverterScope s) {
		scope = s;
	}

	@Override
	public boolean canConvert(final Class arg0) {
		return arg0.equals(GamaShapeFile.class);
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		System.out.println("ConvertAnother : GamaShapeFileConverter " + arg0.getClass());

		final GamaShapeFile shpFile = (GamaShapeFile) arg0;
		writer.startNode(TAG);
		writer.setValue(shpFile.getFile(scope.scope).getAbsolutePath());
		writer.endNode();
		System.out.println("===========END ConvertAnother : GamaShapeFile");
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {

		reader.moveDown();
		final GamaShapeFile shp = new GamaShapeFile(scope.getScope(), reader.getValue());
		reader.moveUp();
		return shp;
	}

}
