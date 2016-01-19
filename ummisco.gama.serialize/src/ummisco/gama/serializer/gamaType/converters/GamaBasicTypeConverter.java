package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.types.GamaBoolType;
import msi.gaml.types.GamaType;

public class GamaBasicTypeConverter implements Converter {

	private final static String TAG="GamaPoint";
	private final static String SEPARATOR=":";
	@Override
	public boolean canConvert(Class arg0) {
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
		GamaPoint pt = (GamaPoint) arg0;
		String line=pt.getX()+SEPARATOR+pt.getY()+SEPARATOR+pt.getZ();
		writer.startNode(TAG);
		writer.setValue(line);
	    writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext arg1) {
		// TODO Auto-generated method stub
		double x,y,z;
		reader.moveDown();
		String[] lines = reader.getValue().split(SEPARATOR);
		reader.moveUp();
		x= Double.valueOf(lines[0]).doubleValue();
		y= Double.valueOf(lines[0]).doubleValue();
		z= Double.valueOf(lines[0]).doubleValue();
		return new GamaPoint(x,y,z);
	}

}
