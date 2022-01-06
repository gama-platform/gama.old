/*******************************************************************************************************
 *
 * LayerManager.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.display;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.interfaces.ILayerManager;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.AbstractLayerStatement;
import msi.gama.outputs.layers.AgentLayer;
import msi.gama.outputs.layers.EventLayer;
import msi.gama.outputs.layers.GisLayer;
import msi.gama.outputs.layers.GraphicLayer;
import msi.gama.outputs.layers.GridAgentLayer;
import msi.gama.outputs.layers.GridLayer;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.outputs.layers.ImageLayer;
import msi.gama.outputs.layers.MeshLayer;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.outputs.layers.SpeciesLayer;
import msi.gama.outputs.layers.charts.ChartLayer;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 23 janv. 2011
 *
 * @todo Description
 *
 */
public class LayerManager implements ILayerManager {

	/**
	 * Creates the layer.
	 *
	 * @param output
	 *            the output
	 * @param layer
	 *            the layer
	 * @return the i layer
	 */
	public static ILayer createLayer(final LayeredDisplayOutput output, final ILayerStatement layer) {
		switch (layer.getType(output)) {
			case GRID:
				return new GridLayer(layer);
			case AGENTS:
				return new AgentLayer(layer);
			case GRID_AGENTS:
				return new GridAgentLayer(layer);
			case SPECIES:
				return new SpeciesLayer(layer);
			case IMAGE:
				return new ImageLayer(output.getScope(), layer);
			case GIS:
				return new GisLayer(layer);
			case CHART:
				return new ChartLayer(layer);
			case EVENT:
				return new EventLayer(layer);
			case GRAPHICS:
				return new GraphicLayer(layer);
			case OVERLAY:
				return new OverlayLayer(layer);
			case MESH:
				return new MeshLayer(layer);
			default:
				return null;
		}
	}

	/** The enabled layers. */
	private final ILayer[] layers;

	/** The surface. */
	final IDisplaySurface surface;

	/** The count. */
	private int count = 0;

	/**
	 * Instantiates a new layer manager.
	 *
	 * @param surface
	 *            the surface
	 * @param output
	 *            the output
	 */
	public LayerManager(final IDisplaySurface surface, final LayeredDisplayOutput output) {
		this.surface = surface;
		OverlayLayer overlay = null;
		final List<ILayer> layers = new ArrayList<>();
		for (final AbstractLayerStatement layer : output.getLayers()) {
			if (layer.isToCreate()) {
				final ILayer result = createLayer(output, layer);
				if (result instanceof OverlayLayer) {
					overlay = (OverlayLayer) result;
				} else if (result != null) {
					layers.add(result);
					addItem(result);
				}
			}
		}
		if (overlay != null) { layers.add(overlay); }
		this.layers = layers.toArray(new ILayer[layers.size()]);
	}

	@Override
	public void dispose() {
		for (final ILayer d : layers) { d.dispose(); }
	}

	@Override
	public List<ILayer> getLayersIntersecting(final int x, final int y) {
		final List<ILayer> result = new ArrayList<>();
		for (final ILayer layer : layers) { if (layer.containsScreenPoint(x, y)) { result.add(layer); } }
		return result;
	}

	/**
	 * Method focusOn()
	 *
	 * @see msi.gama.common.interfaces.ILayerManager#focusOn(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public Rectangle2D focusOn(final IShape geometry, final IDisplaySurface s) {
		if (geometry == null) return null;
		Rectangle2D result = null;
		for (final ILayer display : layers) {
			final Rectangle2D r = display.focusOn(geometry, s);
			if (r != null) {
				if (result == null) {
					result = new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), r.getHeight());
				} else {
					result.add(r);
				}
			}
		}
		return result;
	}

	@Override
	public void drawLayersOn(final IGraphics g) {
		if (g == null || g.cannotDraw()) return;
		final IGraphicsScope scope = surface.getScope();
		// If the experiment is already closed
		if (scope == null || scope.interrupted()) return;
		scope.setGraphics(g);
		boolean changed = false;
		// First we compute all the data and verify if anything is changed
		for (final ILayer dis : layers) {
			if (scope.interrupted()) return;
			changed |= dis.getData().compute(scope, g);
		}
		if (changed) { forceRedrawingLayers(); }

		if (g.beginDrawingLayers()) {
			try {
				// We separate in two phases: updating of the data and then drawing
				for (final ILayer dis : layers) {
					if (scope.interrupted()) return;
					dis.draw(scope, g);
				}
			} catch (final Exception e) {
				GAMA.reportAndThrowIfNeeded(scope, GamaRuntimeException.create(e, scope), false);
			} finally {
				g.endDrawingLayers();
			}
		}
	}

	@Override
	public List<ILayer> getItems() { return Arrays.asList(layers); }

	/**
	 * Gets the item display name.
	 *
	 * @param obj
	 *            the obj
	 * @param previousName
	 *            the previous name
	 * @return the item display name
	 */
	@Override
	public String getItemDisplayName(final ILayer obj, final String previousName) {
		return obj.getMenuName();
	}

	/**
	 * Adds the item.
	 *
	 * @param obj
	 *            the obj
	 * @return true, if successful
	 */
	@Override
	public boolean addItem(final ILayer obj) {
		obj.getDefinition().setOrder(count++);
		obj.firstLaunchOn(surface);
		return true;
	}

	/**
	 * Allows the layers to do some cleansing when the output of the display changes
	 *
	 * @see msi.gama.common.interfaces.ILayerManager#outputChanged()
	 */
	@Override
	public void outputChanged() {
		for (final ILayer i : layers) { i.reloadOn(surface); }
	}

	@Override
	public boolean stayProportional() {
		for (final ILayer i : layers) { if (i.stayProportional()) return true; }
		return false;
	}

	/**
	 * Method makeItemSelectable()
	 *
	 * @see msi.gama.common.interfaces.ItemList#makeItemSelectable(java.lang.Object, boolean)
	 */
	@Override
	public void makeItemSelectable(final ILayer layer, final boolean b) {
		layer.getData().setSelectable(b);
	}

	/**
	 * Method makeItemVisible()
	 *
	 * @see msi.gama.common.interfaces.ItemList#makeItemVisible(java.lang.Object, boolean)
	 */
	@Override
	public void makeItemVisible(final ILayer obj, final boolean b) {
		surface.runAndUpdate(() -> {
			if (b) {
				obj.enableOn(surface);
			} else {
				obj.disableOn(surface);
			}
			forceRedrawingLayers();
		});

	}

	@Override
	public void forceRedrawingLayers() {
		for (final ILayer l : layers) { l.forceRedrawingOnce(); }
		surface.layersChanged();
	}

	@Override
	public boolean isProvidingCoordinates() {
		for (final ILayer i : layers) { if (i.getData().isVisible() && i.isProvidingCoordinates()) return true; }
		return false;
	}

	@Override
	public boolean isProvidingWorldCoordinates() {
		for (final ILayer i : layers) { if (i.getData().isVisible() && i.isProvidingWorldCoordinates()) return true; }
		return false;
	}

	@Override
	public boolean hasMouseMenuEventLayer() {
		for (final ILayer i : layers) {
			if (i instanceof EventLayer && IKeyword.MOUSE_MENU.equals(((EventLayer) i).getEvent())) return true;
		}
		return false;
	}

	@Override
	public boolean isItemVisible(final ILayer obj) {
		return obj.getData().isVisible();
	}

}
