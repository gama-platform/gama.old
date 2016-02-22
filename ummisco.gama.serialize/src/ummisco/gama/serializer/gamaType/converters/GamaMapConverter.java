package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaPair;
import ummisco.gama.serializer.gamaType.reduced.GamaMapReducer;

public class GamaMapConverter implements Converter {

	ConverterScope convertScope;
	
	public GamaMapConverter(ConverterScope s){
		convertScope = s;
	}
	
	@Override
	public boolean canConvert(Class arg0) {
		if(GamaMap.class.equals(arg0)){return true;}
		return false;
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext arg2) {
		GamaMap mp = (GamaMap) arg0;
//		GamaMapReducer m = new GamaMapReducer(mp);
//		writer.startNode("GamaMap");
//		
//		writer.startNode("KeysType");
//		arg2.convertAnother(m.getKeysType());        
//		writer.endNode();
//        
//		writer.startNode("ValueType");
//		arg2.convertAnother(m.getDataType());        		
//        writer.endNode();
//		
//        for(GamaPair gm : m.getValues()) {
//        	arg2.convertAnother(gm);        	
//        }
//        
//        writer.endNode();		
        
		System.out.println("ConvertAnother : GamaMap " + mp.getClass());			        
		arg2.convertAnother(new GamaMapReducer(mp));        		
		System.out.println("END --- ConvertAnother : GamaMap " + mp.getClass());			        

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext arg1) {
		//reader.moveDown();
		GamaMapReducer rmt = (GamaMapReducer) arg1.convertAnother(null, GamaMapReducer.class);
		//reader.moveUp();
		return rmt.constructObject(convertScope.getScope());
	}

}
