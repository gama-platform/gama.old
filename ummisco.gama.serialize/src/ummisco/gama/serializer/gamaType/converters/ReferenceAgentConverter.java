/*********************************************************************************************
 *
 * 'SavedAgentConverter.java, in plugin ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
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

import msi.gama.metamodel.agent.ReferenceAgent;
import msi.gama.metamodel.agent.ReferenceToAgent;

@SuppressWarnings ({ "rawtypes" })
public class ReferenceAgentConverter implements Converter {

	ConverterScope convertScope;

	public ReferenceAgentConverter(final ConverterScope s) {
		convertScope = s;
	}

	@Override
	public boolean canConvert(final Class arg0) {
		return ReferenceAgent.class.equals(arg0);
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		final ReferenceAgent refSavedAgt = (ReferenceAgent) arg0;

//		writer.startNode("agent");
//		context.convertAnother(refSavedAgt.getAgt());
//		writer.endNode();

//		writer.startNode("attributeName");
//		writer.setValue("" + refSavedAgt.getAttributeName());		
//		writer.endNode();
		
		writer.startNode("attributeValue");
		context.convertAnother(refSavedAgt.getAttributeValue());
		writer.endNode();		
	}
	
	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {

//		reader.moveDown();
//		final IAgent refAgt = (IAgent) arg1.convertAnother(null, IAgent.class);
//		reader.moveUp();		
		
//		reader.moveDown();
//		final String attrName = reader.getValue();
//		reader.moveUp();
		
		reader.moveDown();
		final ReferenceToAgent refAttrValue = (ReferenceToAgent) arg1.convertAnother(null, ReferenceToAgent.class);
		reader.moveUp();		

//		final ReferenceSavedAgent agtToReturn = new ReferenceSavedAgent(refAgt, attrName, refAttrValue);
		final ReferenceAgent agtToReturn = new ReferenceAgent(null, null, refAttrValue);

		return agtToReturn;
	}

}
