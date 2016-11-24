/*********************************************************************************************
 *
 * 'GamaFileConverter.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import java.util.List;

import org.apache.commons.lang.ClassUtils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.util.file.IGamaFile;
import ummisco.gama.serializer.gamaType.reduced.GamaFileReducer;

public class GamaFileConverter implements Converter {
	private final static String TAG = "GamaFile";
	private final static String TAG_PATH = "path";
	private final static String TAG_ATTRIBUTES = "attributes";

	private final ConverterScope convertScope;

	public GamaFileConverter(final ConverterScope s) {
		convertScope = s;
	}

	@Override
	public boolean canConvert(final Class arg0) {
		final List<Class<?>> allInterfaceApa = ClassUtils.getAllInterfaces(arg0);

		for (final Object i : allInterfaceApa) {
			if (i.equals(IGamaFile.class))
				return true;
		}
		return false;

		// return (arg0.equals(GamaShapeFile.class));
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		System.out.println("ConvertAnother : GamaFileConverter " + arg0.getClass());

		@SuppressWarnings("rawtypes")
		final IGamaFile gamaFile = (IGamaFile) arg0;
		context.convertAnother(new GamaFileReducer(convertScope.getScope(), gamaFile));
		System.out.println("===========END ConvertAnother : GamaFile");

	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		final GamaFileReducer rmt = (GamaFileReducer) context.convertAnother(null, GamaFileReducer.class);
		return rmt.constructObject(convertScope.getScope());
	}

}
