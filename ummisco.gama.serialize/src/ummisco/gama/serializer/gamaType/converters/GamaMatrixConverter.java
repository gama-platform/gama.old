package ummisco.gama.serializer.gamaType.converters;

import java.util.List;

import org.apache.commons.lang.ClassUtils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.util.matrix.GamaMatrix;
import msi.gama.util.matrix.IMatrix;
import ummisco.gama.serializer.gamaType.reduced.GamaMatrixReducer;

public class GamaMatrixConverter implements Converter {

	ConverterScope convertScope;
	
	public GamaMatrixConverter(ConverterScope s){
		convertScope = s;
	}
	
	@Override
	public boolean canConvert(Class arg0) {		
		List allInterfaceApa = ClassUtils.getAllInterfaces(arg0);
		
		for(Object i : allInterfaceApa) {
			if(i.equals(IMatrix.class))
				return true;
		}
		return false;
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext arg2) {
		GamaMatrix mat = (GamaMatrix) arg0;

		System.out.println("ConvertAnother : GamaMatrix " + mat.getClass());			        
		arg2.convertAnother(new GamaMatrixReducer(convertScope.getScope(), mat));        		
		System.out.println("END --- ConvertAnother : GamaMatrix ");			        

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext arg1) {
		GamaMatrixReducer rmt = (GamaMatrixReducer) arg1.convertAnother(null, GamaMatrixReducer.class);
		return rmt.constructObject(convertScope.getScope());
	}

}
