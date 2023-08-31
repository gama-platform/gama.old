/*******************************************************************************************************
 *
 * GamaSpeciesConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
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

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gaml.species.AbstractSpecies;
import msi.gaml.species.ISpecies;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaSpeciesConverter.
 */
@SuppressWarnings ({ "unchecked" })
public class GamaSpeciesConverter extends AbstractGamaConverter<ISpecies, IList<IAgent>> {

	/**
	 * Instantiates a new gama species converter.
	 *
	 * @param target
	 *            the target
	 */
	public GamaSpeciesConverter(final Class<ISpecies> target) {
		super(target);
	}

	@Override
	public void write(final IScope scope, final ISpecies arg0, final HierarchicalStreamWriter writer,
			final MarshallingContext context) {
		DEBUG.OUT("ConvertAnother : ConvertGamaSpecies " + arg0.getClass());
		final AbstractSpecies spec = (AbstractSpecies) arg0;
		final IPopulation<? extends IAgent> pop = (IPopulation<? extends IAgent>) spec.getPopulation(scope);
		writer.startNode("agentSetFromPopulation");
		context.convertAnother(pop.getAgents(scope));
		writer.endNode();
		DEBUG.OUT("===========END ConvertAnother : ConvertGamaSpecies");
	}

	@Override
	public IList<IAgent> read(final IScope scope, final HierarchicalStreamReader reader,
			final UnmarshallingContext context) {
		reader.moveDown();
		try {
			return (IList<IAgent>) context.convertAnother(null, IList.class);
		} finally {
			reader.moveUp();
		}

	}

}
