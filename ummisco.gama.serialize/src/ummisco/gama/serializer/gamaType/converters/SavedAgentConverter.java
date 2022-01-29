/*******************************************************************************************************
 *
 * SavedAgentConverter.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.util.IMap;

/**
 * The Class SavedAgentConverter.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SavedAgentConverter implements Converter {

	/**
	 * Instantiates a new saved agent converter.
	 *
	 * @param s the s
	 */
	public SavedAgentConverter(final ConverterScope s) {}

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

		final ArrayList<String> keys = new ArrayList<>();
		final ArrayList<Object> datas = new ArrayList<>();

		for (final String ky : savedAgt.getKeys()) {
			final Object val = savedAgt.get(ky);
			if (!(val instanceof ExperimentAgent) && !(val instanceof SimulationAgent)) {
				keys.add(ky);
				datas.add(val);
			}
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
		if (inPop != null) {
			writer.startNode("innerPopulations");
			context.convertAnother(inPop);
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {

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
		final Map<String, Object> localData = new HashMap<>();
		for (int ii = 0; ii < keys.size(); ii++) {
			localData.put(keys.get(ii), datas.get(ii));
		}
		Map<String, List<SavedAgent>> inPop = null;

		if (reader.hasMoreChildren()) {
			reader.moveDown();
			inPop = (Map<String, List<SavedAgent>>) arg1.convertAnother(null, IMap.class);
			reader.moveUp();
		}

		final SavedAgent agtToReturn = new SavedAgent(index, localData, inPop);

		return agtToReturn;
	}

}
