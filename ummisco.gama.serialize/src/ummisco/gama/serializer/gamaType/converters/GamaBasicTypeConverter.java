package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

public class GamaBasicTypeConverter implements Converter {

	private final static String TAG="GamaType";
	ConverterScope convertScope;
	
	public GamaBasicTypeConverter(ConverterScope s){
		convertScope = s;
	}

	@Override
	public boolean canConvert(Class arg0) {
		if((arg0.equals(GamaType.class)) || (arg0.getSuperclass().equals(GamaType.class)))
		{return true;}
		
	//	List allInterfaceApa = ClassUtils.getAllInterfaces(arg0);
	//	
	//	for(Object i : allInterfaceApa) {
	//		if(i.equals(IType.class))
	//			return true;
	//	}
		return false;
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext arg2) {
		GamaType type = (GamaType) arg0;
		System.out.println("==GamaType  " + arg0);
		writer.startNode(TAG);
		writer.setValue(""+type.getName());		
//		writer.setValue(""+arg0.getClass());
	    writer.endNode();
	}

	// TODO
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext arg1) {
		reader.moveDown();
		IType t = convertScope.getScope().getModelContext().getTypeNamed(reader.getValue());
	//	ModelDescription modelDesc = ((ModelDescription) convertScope.getScope().getModelContext());
	//	IType t = ((ModelDescription) convertScope.getScope().getModelContext()).getTypesManager().get(type)
//		String val = reader.getValue();
		reader.moveUp();

		return t;
	}

}
