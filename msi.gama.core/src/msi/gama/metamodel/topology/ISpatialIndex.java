/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.ISpatialIndex.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology;

import java.util.Collection;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;

/**
 * Written by drogoul Modified on 23 f�vr. 2011
 *
 * @todo Description
 *
 */
public interface ISpatialIndex {

	void insert(IAgent agent);

	void remove(final Envelope3D previous, final IAgent agent);

	IAgent firstAtDistance(IScope scope, final IShape source, final double dist, final IAgentFilter f);

	Collection<IAgent> firstAtDistance(IScope scope, final IShape source, final double dist, final IAgentFilter f,
			int number, Collection<IAgent> alreadyChosen);

	Collection<IAgent> allInEnvelope(IScope scope, final IShape source, final Envelope envelope, final IAgentFilter f,
			boolean contained);

	Collection<IAgent> allAtDistance(IScope scope, IShape source, double dist, IAgentFilter f);

	void dispose();

	public interface Compound extends ISpatialIndex {

		void add(ISpatialIndex index, IPopulation<? extends IAgent> pop);

		void remove(final IPopulation<? extends IAgent> species);

		void updateQuadtree(Envelope envelope);

		void mergeWith(Compound spatialIndex);

	}

	Collection<IAgent> allAgents();

	boolean isParallel();

}