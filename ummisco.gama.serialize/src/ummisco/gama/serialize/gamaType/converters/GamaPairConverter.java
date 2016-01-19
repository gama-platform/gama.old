package ummisco.gama.serialize.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaPair;
import ummisco.gama.serialize.gamaType.reduced.GamaPairReducer;

public class GamaPairConverter implements Converter {

	@Override
	public boolean canConvert(Class arg0) {
		if(GamaPair.class.equals(arg0)){return true;}		
		
		Class<?>[] allInterface=arg0.getInterfaces();
		for( Class<?> c:allInterface)
		{
			if(c.equals(GamaPair.class))
				return true;
		}
		return false;
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter arg1, MarshallingContext arg2) {
		GamaPair mp = (GamaPair) arg0;
		arg2.convertAnother(new GamaPairReducer(mp));
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext arg1) {
		reader.moveDown();
		GamaPairReducer rmt = (GamaPairReducer) arg1.convertAnother(null, GamaPairReducer.class);
		reader.moveUp();
		return rmt.constructObject();
	}

}
