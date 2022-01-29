/*******************************************************************************************************
 *
 * GamaShapeConverter.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import msi.gama.metamodel.shape.GamaShape;
import ummisco.gama.dev.utils.DEBUG;

import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.*;

/**
 * The Class GamaShapeConverter.
 */
public class GamaShapeConverter implements Converter {

	@Override
	public boolean canConvert(final Class arg0) {
		return (arg0.equals(GamaShape.class));
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		GamaShape agt = (GamaShape) arg0;		
	//	System.out.println("ConvertAnother : AgentConverter " + agt.getClass());		
		DEBUG.OUT("ConvertAnother : AgentConverter " + agt.getClass());		
	// 	context.convertAnother(agt);
	//	System.out.println("===========END ConvertAnother : GamaShape");		
		DEBUG.OUT("===========END ConvertAnother : GamaShape");		
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		GamaShape rmt = (GamaShape) arg1.convertAnother(null, GamaShape.class);
		return rmt; // ragt;
	}

}
