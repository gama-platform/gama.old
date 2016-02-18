package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gaml.types.GamaBoolType;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

public class GamaBasicTypeConverter implements Converter {

	private final static String TAG="GamaType";
	ConverterScope scope;
	
	public GamaBasicTypeConverter(ConverterScope s){
		scope = s;
	}

	@Override
	public boolean canConvert(Class arg0) {
		if((arg0.equals(GamaType.class)) || (arg0.getSuperclass().equals(GamaType.class)))
		{return true;}
		
		
		Class<?>[] allInterface=arg0.getInterfaces();
		for( Class<?> c:allInterface)
		{
			
		//	if(c.equals(Gam.class))
		//		return true;
		}
		return false;
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext arg2) {
		//GamaPoint pt = (GamaPoint) arg0;
		// String line=pt.getX()+SEPARATOR+pt.getY()+SEPARATOR+pt.getZ();
		System.out.println("==GamaType  " + arg0);
		writer.startNode(TAG);
		writer.setValue(""+arg0.getClass());
	    writer.endNode();
	}

	// TODO
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext arg1) {
		// TODO Auto-generated method stub
		reader.moveDown();
		IType t = scope.getScope().getModelContext().getTypeNamed(reader.getValue());
//		String val = reader.getValue();
		reader.moveUp();
	//	x= Double.valueOf(lines[0]).doubleValue();
	//	y= Double.valueOf(lines[0]).doubleValue();
	//	z= Double.valueOf(lines[0]).doubleValue();
		return t;
	}

}
