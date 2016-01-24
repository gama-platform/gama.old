package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaPair;
import ummisco.gama.serializer.gamaType.reduced.GamaListReducer;
import ummisco.gama.serializer.gamaType.reduced.GamaMapReducer;

public class GamaListConverter implements Converter {

	IScope scope;
	
	public GamaListConverter(IScope s){
		scope = s;
	}
	
	@Override
	public boolean canConvert(Class arg0) {
		if(GamaList.class.equals(arg0)){return true;}
		Class<?>[] allInterface=arg0.getInterfaces();
		for( Class<?> c:allInterface)
		{
			if(c.equals(GamaList.class))
				return true;
		}
		return false;
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext arg2) {
		GamaList list = (GamaList) arg0;
        
		System.out.println("ConvertAnother : GamaList " + list.getClass());			        
		arg2.convertAnother(new GamaListReducer(list));        		
		System.out.println("END --- ConvertAnother : GamaList ");			        

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext arg1) {
	//	reader.moveDown();
		GamaListReducer rmt = (GamaListReducer) arg1.convertAnother(null, GamaListReducer.class);
	//	reader.moveUp();
		return rmt.constructObject(scope);
	}

}
