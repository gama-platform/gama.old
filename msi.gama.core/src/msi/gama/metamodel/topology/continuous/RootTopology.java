/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.continuous.RootTopology.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.continuous;

import org.locationtech.jts.geom.Envelope;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.CompoundSpatialIndex;
import msi.gama.metamodel.topology.ISpatialIndex;
import msi.gama.runtime.IScope;

public class RootTopology extends ContinuousTopology {

	public RootTopology(final IScope scope, final IShape geom, final boolean isTorus, final boolean hasParallelism) {
		super(scope, geom);
		final Envelope bounds = geom.getEnvelope();
		spatialIndex = new CompoundSpatialIndex(bounds, hasParallelism);
		this.isTorus = isTorus;
		root = this;
	}

	private final ISpatialIndex.Compound spatialIndex;
	private final boolean isTorus;

	@Override
	public ISpatialIndex getSpatialIndex() {
		return spatialIndex;
	}

	public void updateEnvironment(final IShape newEnv) {
		spatialIndex.updateQuadtree(newEnv.getEnvelope());
	}

	@Override
	public boolean isTorus() {
		return isTorus;
	}

	@Override
	public void setRoot(final IScope scope, final RootTopology root) {}

	public void mergeWith(final RootTopology other) {
		spatialIndex.mergeWith(other.spatialIndex);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (spatialIndex != null) {
			spatialIndex.dispose();
		}
	}

	public void remove(final IPopulation<? extends IAgent> pop) {
		if (spatialIndex != null) {
			spatialIndex.remove(pop);
		}

	}

}