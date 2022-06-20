/*******************************************************************************************************
 *
 * SavedAgentConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.agent.MutableSavedAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.IMap;

/**
 * The Class SavedAgentConverter.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SavedAgentConverter extends AbstractGamaConverter<SavedAgent, SavedAgent> {

	/**
	 * Instantiates a new saved agent converter.
	 *
	 * @param target
	 *            the target
	 */
	public SavedAgentConverter(final Class<SavedAgent> target) {
		super(target);
	}

	/**
	 * Serialize.
	 *
	 * @param writer
	 *            the writer
	 * @param context
	 *            the context
	 * @param arg0
	 *            the arg 0
	 */
	@Override
	public void write(final IScope scope, final SavedAgent savedAgt, final HierarchicalStreamWriter writer,
			final MarshallingContext context) {
		writer.startNode("index");
		writer.setValue("" + savedAgt.getIndex());
		writer.endNode();
		
		writer.startNode("species");
		writer.setValue("" + savedAgt.getSpecies());
		writer.endNode();
		
		writer.startNode("source");
		writer.setValue("" + savedAgt.getSource());
		writer.endNode();
		
		writer.startNode("alias");
		writer.setValue("" + savedAgt.getAlias());
		writer.endNode();
		
		writer.startNode("uniqueID");
		writer.setValue("" + savedAgt.getUniqueID());
		writer.endNode();

		final ArrayList<String> keys = new ArrayList<>();
		final ArrayList<Object> datas = new ArrayList<>();

		for (final String ky : savedAgt.getKeys()) {
			final Object val = savedAgt.get(ky);
			if (!(val instanceof ITopLevelAgent)) {
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
	public SavedAgent read(final IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		reader.moveDown();
		final String indexStr = reader.getValue();	
		
		final MutableSavedAgent agtToReturn = base != null ? base : new MutableSavedAgent();
		
		final Integer index = Integer.parseInt(indexStr);
		agtToReturn.setIndex(index);
		reader.moveUp();
		reader.moveDown();
		final String species = reader.getValue();
		agtToReturn.setSpecies(species);	
		
		reader.moveUp();
		reader.moveDown();
		final String source = reader.getValue();
		agtToReturn.setSource(source);
		
		reader.moveUp();
		reader.moveDown();
		final String alias = reader.getValue();
		agtToReturn.setAlias(alias);
	
		reader.moveUp();
		reader.moveDown();
		final int uniqueID = Integer.parseInt(reader.getValue());
		System.out.println("UNIQUE ID FROM DESERI + "+uniqueID);
		agtToReturn.setUniqueID(uniqueID);

		System.out.println("variables in agent = ");
		for(var auto : agtToReturn.entrySet())
		{
			System.out.println(auto.getKey() + " = " +auto.getValue());
		}
		
		reader.moveUp();
		reader.moveDown();
		reader.moveDown();
		final ArrayList<String> keys = (ArrayList<String>) context.convertAnother(null, ArrayList.class);
		reader.moveUp();
		reader.moveDown();
		final ArrayList<Object> datas = (ArrayList<Object>) context.convertAnother(null, ArrayList.class);
		reader.moveUp();
		reader.moveUp();
		for (int ii = 0; ii < keys.size(); ii++) { agtToReturn.put(keys.get(ii), datas.get(ii)); }
		Map<String, List<SavedAgent>> inPop = null;

		if (reader.hasMoreChildren()) {
			reader.moveDown();
			inPop = (Map<String, List<SavedAgent>>) context.convertAnother(null, IMap.class);
			agtToReturn.setInnerPop(inPop);
			reader.moveUp();
		}
		return agtToReturn;
	}

}
