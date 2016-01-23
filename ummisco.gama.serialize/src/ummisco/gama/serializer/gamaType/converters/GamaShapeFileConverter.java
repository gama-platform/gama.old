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

import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.*;

public class GamaShapeFileConverter implements Converter {
	private final static String TAG="GamaShapeFile";
	private IScope scope;
	
	public GamaShapeFileConverter(IScope s){
		scope = s;
	}
	
	@Override
	public boolean canConvert(final Class arg0) {
		return (arg0.equals(GamaShapeFile.class));
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {		
		System.out.println("ConvertAnother : GamaShapeFileConverter " + arg0.getClass());		
	    
	    GamaShapeFile shpFile = (GamaShapeFile) arg0;
		writer.startNode(TAG);
		writer.setValue(shpFile.getFile().getAbsolutePath());	
		writer.endNode();
		System.out.println("===========END ConvertAnother : GamaShapeFile");	
	}

	
	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {

		reader.moveDown();
		GamaShapeFile shp = new GamaShapeFile(scope, reader.getValue());		
		reader.moveUp();
		return shp; 
	}

}
