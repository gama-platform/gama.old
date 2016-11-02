/*********************************************************************************************
 *
 * 'RootTopology.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.metamodel.topology.continuous;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.CompoundSpatialIndex;
import msi.gama.metamodel.topology.ISpatialIndex;
import msi.gama.runtime.IScope;

public class RootTopology extends ContinuousTopology {

	public RootTopology(final IScope scope, final IShape geom, final boolean isTorus) {
		super(scope, geom);
		final Envelope bounds = geom.getEnvelope();
		spatialIndex = new CompoundSpatialIndex(bounds);
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
	protected void setRoot(final IScope scope, final RootTopology root) {
	}

	@Override
	public void dispose() {
		super.dispose();
		if (spatialIndex != null) {
			spatialIndex.dispose();
		}
	}

}