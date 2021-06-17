/*******************************************************************************************************
 *
 * msi.gama.outputs.display.LayerManager.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.display;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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
import msi.gama.outputs.layers.MeshLayer;
import msi.gama.outputs.layers.GisLayer;
import msi.gama.outputs.layers.GraphicLayer;
import msi.gama.outputs.layers.GridAgentLayer;
import msi.gama.outputs.layers.GridLayer;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.outputs.layers.ImageLayer;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.outputs.layers.SpeciesLayer;
import msi.gama.outputs.layers.charts.ChartLayer;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;

/**
 * Written by drogoul Modified on 23 janv. 2011
 *
 * @todo Description
 *
 */
public class LayerManager implements ILayerManager {

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

	// Feb 2020: added to avoid concurrentModifications
	private final List<ILayer> enabledLayers = new CopyOnWriteArrayList<>();
	private final List<ILayer> disabledLayers = new ArrayList<>();
	private OverlayLayer overlay = null;
	final IDisplaySurface surface;
	private int count = 0;

	public LayerManager(final IDisplaySurface surface, final LayeredDisplayOutput output) {
		this.surface = surface;
		final List<AbstractLayerStatement> layers = output.getLayers();
		for (final AbstractLayerStatement layer : layers) {
			if (layer.isToCreate()) {
				final ILayer result = createLayer(output, layer);
				if (result instanceof OverlayLayer) {
					overlay = (OverlayLayer) result;
				} else {
					addLayer(result);
				}
			}
		}
	}

	@Override
	public void dispose() {
		for (final ILayer d : enabledLayers) {
			d.dispose();
		}
		for (final ILayer d : disabledLayers) {
			d.dispose();
		}
		enabledLayers.clear();
		disabledLayers.clear();
	}

	protected ILayer addLayer(final ILayer d) {
		if (addItem(d)) return d;
		return null;
	}

	public void removeLayer(final ILayer found) {
		if (found != null) { enabledLayers.remove(found); }
		Collections.sort(enabledLayers);
	}

	@Override
	public List<ILayer> getLayersIntersecting(final int x, final int y) {
		final List<ILayer> result = new ArrayList<>();
		for (final ILayer display : enabledLayers) {
			if (display.containsScreenPoint(x, y)) { result.add(display); }
		}
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
		for (final ILayer display : enabledLayers) {
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

	void enable(final ILayer found) {
		found.enableOn(surface);
		enabledLayers.add(found);
		disabledLayers.remove(found);
		Collections.sort(enabledLayers);
	}

	void disable(final ILayer found) {
		if (found != null) {
			found.disableOn(surface);
			removeLayer(found);
			disabledLayers.add(found);
		}
	}

	@Override
	public void drawLayersOn(final IGraphics g) {
		if (g == null || g.cannotDraw()) return;
		final IScope scope = surface.getScope();
		// If the experiment is already closed
		if (scope == null || scope.interrupted()) return;
		scope.setGraphics(g);
		try {
			if (g.beginDrawingLayers()) {
				for (final ILayer dis : enabledLayers) {
					if (scope.interrupted()) return;
					dis.draw(scope, g);
				}
				if (overlay != null) { overlay.draw(scope, g); }
			}
		} catch (final Exception e) {
			GAMA.reportAndThrowIfNeeded(scope, GamaRuntimeException.create(e, scope), false);
		} finally {
			g.endDrawingLayers();
		}
	}

	@Override
	public List<ILayer> getItems() {
		final List<ILayer> items = new ArrayList<>();
		items.addAll(enabledLayers);
		items.addAll(disabledLayers);
		Collections.sort(items);
		return items;
	}

	@Override
	public void removeItem(final ILayer found) {
		if (found != null) { enabledLayers.remove(found); }
		Collections.sort(enabledLayers);
	}

	@Override
	public void pauseItem(final ILayer obj) {}

	@Override
	public void resumeItem(final ILayer obj) {}

	@Override
	public String getItemDisplayName(final ILayer obj, final String previousName) {
		return obj.getMenuName();
	}

	@Override
	public GamaColor getItemDisplayColor(final ILayer o) {
		return null;
	}

	@Override
	public void focusItem(final ILayer obj) {}

	@Override
	public boolean addItem(final ILayer obj) {
		obj.getDefinition().setOrder(count++);
		enabledLayers.add(obj);
		Collections.sort(enabledLayers);
		obj.firstLaunchOn(surface);
		return true;
	}

	@Override
	public void updateItemValues() {}

	/**
	 * Allows the layers to do some cleansing when the output of the display changes
	 *
	 * @see msi.gama.common.interfaces.ILayerManager#outputChanged()
	 */
	@Override
	public void outputChanged() {
		for (final ILayer i : enabledLayers) {
			i.reloadOn(surface);
		}
		for (final ILayer i : disabledLayers) {
			i.reloadOn(surface);
		}
	}

	@Override
	public boolean stayProportional() {
		for (final ILayer i : enabledLayers) {
			if (i.stayProportional()) return true;
		}
		return false;
	}

	/**
	 * Method makeItemSelectable()
	 *
	 * @see msi.gama.common.interfaces.ItemList#makeItemSelectable(java.lang.Object, boolean)
	 */
	@Override
	public void makeItemSelectable(final ILayer data, final boolean b) {
		data.getData().setSelectable(b);
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
				enable(obj);
			} else {
				disable(obj);
			}
			for (final ILayer l : enabledLayers) {
				l.forceRedrawingOnce();
			}
			surface.layersChanged();

		});
	}

	/**
	 * Method handleMenu()
	 *
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object, int, int)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final ILayer data, final int x, final int y) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.common.interfaces.ILayerManager#isProvidingCoordinates()
	 */
	@Override
	public boolean isProvidingCoordinates() {
		for (final ILayer i : enabledLayers) {
			if (i.isProvidingCoordinates()) return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.common.interfaces.ILayerManager#isProvidingWorldCoordinates()
	 */
	@Override
	public boolean isProvidingWorldCoordinates() {
		for (final ILayer i : enabledLayers) {
			if (i.isProvidingWorldCoordinates()) return true;
		}
		return false;
	}

	@Override
	public boolean hasMouseMenuEventLayer() {
		for (final ILayer i : enabledLayers) {
			if (i instanceof EventLayer) { if (((EventLayer) i).getEvent().equals(IKeyword.MOUSE_MENU)) return true; }
		}
		return false;
	}

}
