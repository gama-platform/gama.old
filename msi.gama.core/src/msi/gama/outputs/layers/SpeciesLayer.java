/*********************************************************************************************
 *
 * 'SpeciesLayer.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.ExecutionResult;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AspectStatement;
import msi.gaml.statements.IExecutable;

/**
 * Written by drogoul Modified on 23 août 2008
 */

public class SpeciesLayer extends AgentLayer {

	public SpeciesLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	public Set<IAgent> getAgentsForMenu(final IScope scope) {
		final Set<IAgent> result = ImmutableSet.copyOf(
				scope.getSimulation().getMicroPopulation(((SpeciesLayerStatement) definition).getSpecies()).iterator());
		return result;
	}

	@Override
	public String getType() {
		return "Species layer";
	}

	@Override
	public void privateDraw(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		shapes.clear();
		final ISpecies species = ((SpeciesLayerStatement) definition).getSpecies();
		final IMacroAgent world = scope.getSimulation();
		if (world != null && !world.dead()) {
			final IPopulation<? extends IAgent> microPop = world.getMicroPopulation(species);
			if (microPop != null) {
				IExecutable aspect = ((SpeciesLayerStatement) definition).getAspect();
				if (aspect == null) {
					aspect = AspectStatement.DEFAULT_ASPECT;
				}
				drawPopulation(scope, g, aspect, microPop);
			}
		}
	}

	private void drawPopulation(final IScope scope, final IGraphics g, final IExecutable aspect,
			final IPopulation<? extends IAgent> population) throws GamaRuntimeException {

		// draw the population. A copy of the population is made to avoid
		// concurrent modification exceptions
		for (final IAgent a : population.toArray()) {
			if (a == null || a.dead()) {
				continue;
			}
			ExecutionResult result;
			if (a == scope.getGui().getHighlightedAgent()) {
				IExecutable hAspect = population.getSpecies().getAspect("highlighted");
				if (hAspect == null) {
					hAspect = aspect;
				}
				result = scope.execute(hAspect, a, null);
			} else {
				result = scope.execute(aspect, a, null);
			}
			if (result.getValue() instanceof Rectangle2D) {
				final Rectangle2D r = (Rectangle2D) result.getValue();
				if (r != null) {
					shapes.put(a, r);
				}
			}
			if (!(a instanceof IMacroAgent)) {
				continue;
			}
			IPopulation<? extends IAgent> microPop;
			// then recursively draw the micro-populations
			final List<SpeciesLayerStatement> microLayers =
					((SpeciesLayerStatement) definition).getMicroSpeciesLayers();
			if (microLayers != null) {
				for (final SpeciesLayerStatement ml : microLayers) {
					// a.acquireLock();
					if (a.dead()) {
						continue;
					}
					microPop = ((IMacroAgent) a).getMicroPopulation(ml.getSpecies());
					if (microPop != null && microPop.size() > 0) {
						IExecutable microAspect = ml.getAspect();
						if (microAspect == null) {
							microAspect = AspectStatement.DEFAULT_ASPECT;
						}
						drawPopulation(scope, g, microAspect, microPop);
					}
				}
			}
		}

	}

}
