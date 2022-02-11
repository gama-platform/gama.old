package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.util.GamaColor;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.gamaType.reduced.GamaColorReducer;

@SuppressWarnings("rawtypes")
public class GamaColorConverter implements Converter {

	@Override
	public boolean canConvert(final Class arg0) {
		
		if (GamaColor.class.equals(arg0)) {
			return true;
		}

		final Class<?>[] allInterface = arg0.getInterfaces();
		for (final Class<?> c : allInterface) {
			if (c.equals(GamaColor.class))
				return true;
		}
		
		final Class superClass = arg0.getSuperclass();
		if (superClass != null) {
			return canConvert(superClass);
		}
		
		return false;
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter arg1, MarshallingContext arg2) {
		final GamaColor mc = (GamaColor) arg0;
		DEBUG.OUT("ConvertAnother : GamaColor " + mc.getClass());
		arg2.convertAnother(new GamaColorReducer(mc));
		DEBUG.OUT("END -- ConvertAnother : GamaColor " + mc.getClass());
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader arg0, UnmarshallingContext arg1) {
		final GamaColorReducer gcr = (GamaColorReducer) arg1.convertAnother(null, GamaColorReducer.class);
		return gcr.constructObject();
	}
	
	
	
	
	
	

}
