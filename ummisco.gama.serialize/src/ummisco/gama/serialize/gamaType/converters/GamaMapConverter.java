package ummisco.gama.serialize.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import ummisco.gama.serialize.gamaType.reduced.GamaMapReducer;

public class GamaMapConverter implements Converter {

	@Override
	public boolean canConvert(Class arg0) {
		Class<?>[] allInterface=arg0.getInterfaces();
		for( Class<?> c:allInterface)
		{
			if(c.equals(GamaMap.class))
				return true;
		}
		return false;
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter arg1, MarshallingContext arg2) {
		GamaMap mp = (GamaMap) arg0;
		arg2.convertAnother(new GamaMapReducer(mp));
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext arg1) {
		reader.moveDown();
		GamaMapReducer rmt = (GamaMapReducer) arg1.convertAnother(null, GamaMapReducer.class);
		reader.moveUp();
		return rmt.constructObject();
	}

}
