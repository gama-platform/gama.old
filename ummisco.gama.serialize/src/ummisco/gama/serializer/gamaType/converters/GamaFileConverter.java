/*********************************************************************************************
 * 
 * 
 * 'GamaAgentConverter.java', in plugin 'ummisco.gama.communicator', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaShapeFile;
import msi.gama.util.file.IGamaFile;
import msi.gama.util.matrix.GamaMatrix;
import msi.gama.util.matrix.IMatrix;
import ummisco.gama.serializer.gamaType.reduced.GamaFileReducer;
import ummisco.gama.serializer.gamaType.reduced.GamaMatrixReducer;

import java.util.List;

import org.apache.commons.lang.ClassUtils;

import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.*;

public class GamaFileConverter implements Converter {
	private final static String TAG="GamaFile";
	private final static String TAG_PATH="path";
	private final static String TAG_ATTRIBUTES="attributes";
	
	private ConverterScope convertScope;
	
	public GamaFileConverter(ConverterScope s){
		convertScope = s;
	}
	
	@Override
	public boolean canConvert(final Class arg0) {
		List allInterfaceApa = ClassUtils.getAllInterfaces(arg0);
		
		for(Object i : allInterfaceApa) {
			if(i.equals(IGamaFile.class))
				return true;
		}
		return false;		
		
		
	//	return (arg0.equals(GamaShapeFile.class));
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {		
		System.out.println("ConvertAnother : GamaFileConverter " + arg0.getClass());		
	     
	    IGamaFile gamaFile = (IGamaFile) arg0;
		context.convertAnother(new GamaFileReducer(convertScope.getScope(), gamaFile));		
		System.out.println("===========END ConvertAnother : GamaFile");	
		
	}

	
	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		GamaFileReducer rmt = (GamaFileReducer) context.convertAnother(null, GamaFileReducer.class);
		return rmt.constructObject(convertScope.getScope());
	}

}
