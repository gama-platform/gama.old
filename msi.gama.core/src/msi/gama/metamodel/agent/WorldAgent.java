package msi.gama.metamodel.agent;

import java.awt.Graphics2D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GisUtils;
import msi.gama.metamodel.population.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.operators.Spatial.Transformations;
import msi.gaml.species.ISpecies;
import com.vividsolutions.jts.geom.Envelope;

@species(name = IKeyword.WORLD_SPECIES)
public class WorldAgent extends GamlAgent {

	// FIXME Temporary. Should be in the topology of the population
	private ISpatialIndex.Compound spatialIndex;
	// FIXME Temporary. Should be in the topology of the population
	private final GisUtils gis = new GisUtils();
	// FIXME Temporary. Should be in the topology of the population
	boolean isTorus = false;

	public WorldAgent(final IPopulation s) throws GamaRuntimeException {
		super(s);
		index = 0;
	}

	@Override
	public synchronized IShape getGeometry() {
		return geometry;
	}

	public GisUtils getGisUtils() {
		return gis;
	}

	@Override
	public synchronized void setLocation(final ILocation newGlobalLoc) {}

	public void setTorus(boolean t) {
		isTorus = t;
	}

	public boolean isTorus() {
		return isTorus;
	}

	@Override
	public synchronized ILocation getLocation() {
		if ( geometry == null ) { return new GamaPoint(0, 0); }
		return super.getLocation();
	}

	@Override
	public synchronized void setGeometry(final IShape newGlobalGeometry) {
		// FIXME Compute the envelope here ?
		Envelope env = newGlobalGeometry.getEnvelope();
		gis.init(env);
		GamaPoint p = new GamaPoint(-1 * env.getMinX(), -1 * env.getMinY());
		geometry = Transformations.translated_by(getScope(), newGlobalGeometry, p);
		Envelope bounds = geometry.getEnvelope();
		// bounds.translate(GisUtils.XMinComp, GisUtils.YMinComp);

		if ( spatialIndex != null ) {
			spatialIndex.dispose();
		}
		spatialIndex = new CompoundSpatialIndex(bounds);
		getPopulation().setTopology(getScope(), geometry, isTorus);
		for ( IAgent ag : getAgents() ) {
			ag.setGeometry(Transformations.translated_by(getScope(), ag.getGeometry(), p));
		}

	}

	public ISpatialIndex.Compound getSpatialIndex() {
		return spatialIndex;
	}

	@Override
	public WorldPopulation getPopulation() {
		return (WorldPopulation) population;
	}

	public void displaySpatialIndexOn(final Graphics2D g2, final int width, final int height) {
		if ( spatialIndex == null ) { return; }
		spatialIndex.drawOn(g2, width, height);
	}

	@Override
	// Special case for built-in species handled by the world (and not created before)
	public IPopulation getPopulationFor(final String speciesName) throws GamaRuntimeException {
		IPopulation pop = super.getPopulationFor(speciesName);

		if ( pop != null ) { return pop; }

		if ( AbstractGamlAdditions.isBuiltIn(speciesName) ) {
			ISpecies microSpec = this.getSpecies().getMicroSpecies(speciesName);
			pop = new GamlPopulation(this, microSpec);
			microPopulations.put(microSpec, pop);
			pop.initializeFor(getScope());
			return pop;
		}

		return null;
	}

}