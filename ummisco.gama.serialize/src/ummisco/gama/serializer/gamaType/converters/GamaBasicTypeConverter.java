/*********************************************************************************************
 *
 * 'GamaBasicTypeConverter.java, in plugin ummisco.gama.serialize, is part of the source code of the
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

import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

public class GamaBasicTypeConverter implements Converter {

	private final static String TAG = "GamaType";
	ConverterScope convertScope;

	public GamaBasicTypeConverter(final ConverterScope s) {
		convertScope = s;
	}

	@Override
	public boolean canConvert(final Class arg0) {
		final List<Class<?>> allClassesApa = ClassUtils.getAllSuperclasses(arg0);
		for (final Object c : allClassesApa) {
			if (c.equals(GamaType.class))
				return true;
		}
		// if((arg0.equals(GamaType.class)) ||
		// (arg0.getSuperclass().equals(GamaType.class)))
		// {return true;}

		// List allInterfaceApa = ClassUtils.getAllInterfaces(arg0);
		//
		// for(Object i : allInterfaceApa) {
		// if(i.equals(IType.class))
		// return true;
		// }
		return false;
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext arg2) {
		final GamaType<?> type = (GamaType<?>) arg0;
		System.out.println("==GamaType  " + arg0);
		writer.startNode(TAG);
		writer.setValue("" + type.getName());
		// writer.setValue(""+arg0.getClass());
		writer.endNode();
	}

	// TODO
	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		reader.moveDown();
		final IType<?> t = convertScope.getScope().getType(reader.getValue());
		// ModelDescription modelDesc = ((ModelDescription)
		// convertScope.getScope().getModelContext());
		// IType t = ((ModelDescription)
		// convertScope.getScope().getModelContext()).getTypesManager().get(type)
		// String val = reader.getValue();
		reader.moveUp();

		return t;
	}

}
