/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays.layers;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.*;
import org.eclipse.swt.widgets.Composite;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul Modified on 23 ao√ªt 2008
 */

public class SpeciesLayer extends AgentLayer {

	public SpeciesLayer(final double env_width, final double env_height,
		final ILayerStatement layer, final IGraphics dg) {
		super(env_width, env_height, layer, dg);
	}

	@Override
	public void fillComposite(final Composite compo, final IDisplaySurface container) {
		super.fillComposite(compo, container);
		EditorFactory.choose(compo, "Aspect:", ((SpeciesLayerStatement) definition).getAspectName(), true,
			((SpeciesLayerStatement) definition).getAspects(), new EditorListener<String>() {

				@Override
				public void valueModified(final String newValue) {
					changeAspect(newValue);
					container.updateDisplay();
				}
			});
	}

	@Override
	public Set<IAgent> getAgentsForMenu() {
		return new HashSet(GAMA.getDefaultScope().getWorldScope()
			.getMicroPopulation(((SpeciesLayerStatement) definition).getSpecies()).getAgentsList());
	}

	private void changeAspect(final String s) {
		((SpeciesLayerStatement) definition).setAspect(s);
	}

	@Override
	public String getType() {
		return "Species layer";
	}

	@Override
	public void privateDrawDisplay(final IGraphics g) throws GamaRuntimeException {

		shapes.clear();
		IScope scope = GAMA.obtainNewScope();
		if ( scope != null ) {

			// TODO search for hostPopulations then recursively draw micro-grid-populations and
			// micro-populations

			// start drawing agents of "level 1" species ...
			ISpecies species = ((SpeciesLayerStatement) definition).getSpecies();
			if ( species.getLevel() == 1 ) {
				IAgent world = scope.getWorldScope();
				if ( !world.dead() ) {
					IPopulation microPop = world.getMicroPopulation(species);
					if ( microPop != null ) {
						scope.setContext(g);
						drawPopulation(world, (SpeciesLayerStatement) definition, microPop, scope, g);
					}
				}
			}

			GAMA.releaseScope(scope);
		}
	}

	private void drawPopulation(final IAgent host, final SpeciesLayerStatement layer,
		final IPopulation population, final IScope scope, final IGraphics g)
		throws GamaRuntimeException {
		IAspect aspect = population.getAspect(layer.getAspectName());
		if ( aspect == null ) {
			aspect = AspectStatement.DEFAULT_ASPECT;
		}
		g.setOpacity(layer.getTransparency());

		List<IAgent> _agents = population.getAgentsList();
		if ( !_agents.isEmpty() ) {

			// draw the population
			for ( IAgent a : _agents ) {
				if ( a != null && !a.dead() ) {
					Rectangle2D r = aspect.draw(scope, a);
					if ( a == GuiUtils.getHighlightedAgent() ) {
						g.highlight(r);
					}
					shapes.put(a, r);
				}
			}

			IPopulation microPop;

			// draw grids first...
			List<GridLayerStatement> gridLayers = layer.getGridLayers();
			for ( GridLayerStatement gl : gridLayers ) {
				for ( IAgent a : _agents ) {
					if ( a.acquireLock() ) {
						try {
							microPop = a.getMicroPopulation(gl.getName());

							if ( microPop != null && microPop.size() > 0 ) {
								drawGridPopulation(a, gl, microPop, scope, g);
							}
						} finally {
							a.releaseLock();
						}
					}
				}
			}

			// then recursively draw the micro-populations
			List<SpeciesLayerStatement> microLayers = layer.getMicroSpeciesLayers();
			for ( SpeciesLayerStatement ml : microLayers ) {
				for ( IAgent a : _agents ) {
					if ( a.acquireLock() ) {
						try {
							microPop = a.getMicroPopulation(ml.getSpecies());

							if ( microPop != null && microPop.size() > 0 ) {
								drawPopulation(a, ml, microPop, scope, g);
							}
						} finally {
							a.releaseLock();
						}
					}
				}
			}
		}
	}

	private void drawGridPopulation(final IAgent host, final GridLayerStatement layer,
		final IPopulation population, final IScope scope, final IGraphics g)
		throws GamaRuntimeException {
		GamaSpatialMatrix gridAgentStorage =
			(GamaSpatialMatrix) population.getTopology().getPlaces();
		gridAgentStorage.refreshDisplayData();

		// MUST cache this image as GridDisplayLayer does to increase performance
		BufferedImage supportImage =
			ImageUtils.createCompatibleImage(gridAgentStorage.numCols, gridAgentStorage.numRows);
		supportImage.setRGB(0, 0, gridAgentStorage.numCols, gridAgentStorage.numRows,
			gridAgentStorage.getDisplayData(), 0, gridAgentStorage.numCols);

		IShape hostShape = host.getGeometry();
		// GamaPoint hostLocation = hostShape.getLocation();
		Envelope hostEnv = hostShape.getEnvelope();

		// AffineTransform saved = g.getTransform();

		g.setDrawingCoordinates(hostEnv.getMinX() * g.getXScale(),
			hostEnv.getMinY() * g.getYScale());
		g.setDrawingDimensions((int) (gridAgentStorage.numCols * g.getXScale() * 2),
			(int) (gridAgentStorage.numCols * g.getYScale()));
		g.setOpacity(layer.getTransparency());
		g.drawImage(supportImage, null, false, "SpeciesDisplay");

		// System.out.println("xScale = " + g.getXScale() + "; yScale = " + g.getYScale());

		// TODO draw sub grids and sub micro-populations
		//
		// for ( IAgent a : population.getAgentsList() ) {

		// }

	}

	// private void drawSubPopulations() {
	//
	// }
}
