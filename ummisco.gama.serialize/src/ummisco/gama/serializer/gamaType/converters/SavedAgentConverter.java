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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import gnu.trove.map.hash.THashMap;
import msi.gama.metamodel.agent.SavedAgent;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SavedAgentConverter implements Converter {

	private final static String TAG = "IMacroAgent";
	ConverterScope convertScope;

	public SavedAgentConverter(final ConverterScope s) {
		convertScope = s;
	}

	@Override
	public boolean canConvert(final Class arg0) {
		return arg0.equals(SavedAgent.class);
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		final SavedAgent savedAgt = (SavedAgent) arg0;
		writer.startNode("index");
		writer.setValue("" + savedAgt.getIndex());
		writer.endNode();

		final ArrayList<String> keys = new ArrayList<String>();
		final ArrayList<Object> datas = new ArrayList<Object>();

		for (final String ky : savedAgt.getKeys()) {
			keys.add(ky);
			datas.add(savedAgt.get(ky));
		}

		writer.startNode("variables");
		writer.startNode("keys");
		context.convertAnother(keys);
		writer.endNode();
		writer.startNode("data");
		context.convertAnother(datas);
		writer.endNode();
		writer.endNode();
		final Map<String, List<SavedAgent>> inPop = savedAgt.getInnerPopulations();
		if ( (inPop != null) && (inPop.size() > 0)) {
			writer.startNode("innerPopulations");
			context.convertAnother(inPop);
			writer.endNode();
		}

		writer.startNode(TAG);
		context.convertAnother(true);
		writer.endNode();
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		
		System.out.println("read saved agent");
		reader.moveDown();
		final String indexStr = reader.getValue();
		final Integer index = Integer.parseInt(indexStr);
		reader.moveUp();
		reader.moveDown();
		reader.moveDown();
		final ArrayList<String> keys = (ArrayList<String>) arg1.convertAnother(null, ArrayList.class);
		reader.moveUp();
		reader.moveDown();
		final ArrayList<Object> datas = (ArrayList<Object>) arg1.convertAnother(null, ArrayList.class);
		reader.moveUp();
		reader.moveUp();
		final Map<String, Object> localData = new HashMap<String, Object>();
		for (int ii = 0; ii < keys.size(); ii++) {
			localData.put(keys.get(ii), datas.get(ii));
		}
		
		reader.moveDown();
		Map<String, List<SavedAgent>> inPop = null;
		if (reader.getNodeName().equals("innerPopulations")) {
			inPop = (Map<String, List<SavedAgent>>) arg1.convertAnother(null, THashMap.class);
			reader.moveUp();
			reader.moveDown();
		}
		final Boolean isIMacroAgent = (Boolean) arg1.convertAnother(null, Boolean.class);
		reader.moveUp();

		final SavedAgent agtToReturn = new SavedAgent(index, localData, inPop);

		return agtToReturn;
	}

}
