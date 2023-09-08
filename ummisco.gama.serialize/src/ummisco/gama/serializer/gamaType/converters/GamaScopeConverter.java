/*******************************************************************************************************
 *
 * GamaScopeConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaScopeConverter.
 */
public class GamaScopeConverter extends AbstractGamaConverter<IScope, String> {

	/**
	 * Instantiates a new gama scope converter.
	 *
	 * @param target
	 *            the target
	 */
	public GamaScopeConverter(final Class<IScope> target) {
		super(target);
	}

	@Override
	public void write(final IScope scope, final IScope scopeToSave, final HierarchicalStreamWriter writer,
			final MarshallingContext context) {
		writer.startNode("IScope");
		writer.setValue(scopeToSave.getName().toString());
		writer.endNode();
		// The experiment ???
		writer.startNode("Simulations");
		final ExperimentAgent expAgt = (ExperimentAgent) scopeToSave.getExperiment();
		for (final IAgent agt : expAgt.getSimulationPopulation()) {
			// Each simulation
			DEBUG.OUT("ConvertAnother : ScopeConverter " + agt.getClass());
			context.convertAnother(agt);
		}
		writer.endNode();
	}

	@Override
	public String read(final IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		reader.moveDown();
		try {
			return reader.getValue();
		} finally {
			reader.moveUp();
		}
	}

}
