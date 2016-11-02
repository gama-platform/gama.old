/*********************************************************************************************
 *
 * 'GamaPointConverter.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.metamodel.shape.GamaPoint;

public class GamaPointConverter implements Converter {

	private final static String TAG="GamaPoint";
	private final static String SEPARATOR=":";
	@Override
	public boolean canConvert(Class arg0) {
		if(GamaPoint.class.equals(arg0)){return true;}
		
		Class<?>[] allInterface=arg0.getInterfaces();
		for( Class<?> c:allInterface)
		{
			if(c.equals(GamaPoint.class))
				return true;
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
		y= Double.valueOf(lines[1]).doubleValue();
		z= Double.valueOf(lines[2]).doubleValue();
		return new GamaPoint(x,y,z);
	}

}
