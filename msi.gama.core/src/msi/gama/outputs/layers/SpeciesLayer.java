/*********************************************************************************************
 *
 *
 * 'SpeciesLayer.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.geom.Rectangle2D;
import java.util.*;
import com.google.common.collect.ImmutableSet;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.*;

/**
 * Written by drogoul Modified on 23 août 2008
 */

public class SpeciesLayer extends AgentLayer {

	public SpeciesLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	public Set<IAgent> getAgentsForMenu(final IScope scope) {
		final Set<IAgent> result = ImmutableSet.copyOf(scope.getSimulationScope()
			.getMicroPopulation(((SpeciesLayerStatement) definition).getSpecies()).iterator());
		return result;
	}

	@Override
	public String getType() {
		return "Species layer";
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		shapes.clear();
		final ISpecies species = ((SpeciesLayerStatement) definition).getSpecies();
		final IMacroAgent world = scope.getSimulationScope();
		if ( world != null && !world.dead() ) {
			final IPopulation microPop = world.getMicroPopulation(species);
			if ( microPop != null ) {
				drawPopulation(scope, g, (SpeciesLayerStatement) definition, microPop);
			}
		}
	}

	private void drawPopulation(final IScope scope, final IGraphics g, final SpeciesLayerStatement layer,
		final IPopulation population) throws GamaRuntimeException {
		IExecutable aspect = layer.getAspect();
		// IAspect aspect = population.getAspect(layer.getAspectName());
		if ( aspect == null ) {
			aspect = AspectStatement.DEFAULT_ASPECT;
		}
		// IAgent[] _agents = null;
		// _agents = Iterators.toArray(population.iterator(), IAgent.class);

		// draw the population. A copy of the population is made to avoid concurrent modification exceptions
		for ( final IAgent a : /* population.iterable(scope) */population.toArray() ) {
			if ( a == null || a.dead() ) {
				continue;
			}
			Object[] result = new Object[1];
			if ( a == scope.getGui().getHighlightedAgent() ) {
				IExecutable hAspect = population.getSpecies().getAspect("highlighted");
				if ( hAspect == null ) {
					hAspect = aspect;
					// hAspect = AspectStatement.HIGHLIGHTED_ASPECT;
				}
				scope.execute(hAspect, a, null, result);
			} else {
				scope.execute(aspect, a, null, result);
			}
			final Rectangle2D r = (Rectangle2D) result[0];
			if ( r != null ) {
				shapes.put(a, r);
			}
			if ( !(a instanceof IMacroAgent) ) {
				continue;
			}
			IPopulation microPop;
			// draw grids first...
			final List<GridLayerStatement> gridLayers = layer.getGridLayers();
			for ( final GridLayerStatement gl : gridLayers ) {
				try {
					// a.acquireLock();
					if ( a.dead() /* || scope.interrupted() */ ) {
						continue;
					}
					microPop = ((IMacroAgent) a).getMicroPopulation(gl.getName());
					if ( microPop != null && microPop.size() > 0 ) {
						// FIXME Needs to be entirely redefined using the new interfaces
						// drawGridPopulation(a, gl, microPop, scope, g);
					}
				} finally {
					// a.releaseLock();
				}
			}

			// then recursively draw the micro-populations
			final List<SpeciesLayerStatement> microLayers = layer.getMicroSpeciesLayers();
			for ( final SpeciesLayerStatement ml : microLayers ) {
				try {
					// a.acquireLock();
					if ( a.dead() ) {
						continue;
					}
					microPop = ((IMacroAgent) a).getMicroPopulation(ml.getSpecies());

					if ( microPop != null && microPop.size() > 0 ) {
						drawPopulation(scope, g, ml, microPop);
					}
				} finally {
					// a.releaseLock();
				}
			}
		}

	}

	// private void drawGridPopulation(final IAgent host, final GridLayerStatement layer, final IPopulation population,
	// final IScope scope, final IGraphics g) throws GamaRuntimeException {
	// GamaSpatialMatrix gridAgentStorage = (GamaSpatialMatrix) population.getTopology().getPlaces();
	// gridAgentStorage.refreshDisplayData(scope);
	//
	// // MUST cache this image as GridDisplayLayer does to increase performance
	// BufferedImage supportImage =
	// ImageUtils.createCompatibleImage(gridAgentStorage.numCols, gridAgentStorage.numRows);
	// supportImage.setRGB(0, 0, gridAgentStorage.numCols, gridAgentStorage.numRows,
	// gridAgentStorage.getDisplayData(), 0, gridAgentStorage.numCols);
	//
	// IShape hostShape = host.getGeometry();
	// Envelope hostEnv = hostShape.getEnvelope();
	// g.setDrawingCoordinates(hostEnv.getMinX() * g.getXScale(), hostEnv.getMinY() * g.getYScale());
	// g.setDrawingDimensions((int) (gridAgentStorage.numCols * g.getXScale()),
	// (int) (gridAgentStorage.numCols * g.getYScale()));
	// g.setOpacity(layer.getTransparency());
	// g.drawImage(scope, supportImage, null, 0.0f, true);
	//
	// }

}
