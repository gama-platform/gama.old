/*********************************************************************************************
 * 
 *
 * 'LayerManager.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.displays.layers;

import java.awt.geom.Rectangle2D;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;

/**
 * Written by drogoul Modified on 23 janv. 2011
 * 
 * @todo Description
 * 
 */
public class LayerManager implements ILayerManager {

	private final List<ILayer> enabledLayers = new GamaList();
	private final List<ILayer> disabledLayers = new GamaList();
	private final IDisplaySurface surface;
	private int count = 0;

	public LayerManager(final IDisplaySurface surface) {
		this.surface = surface;
	}

	@Override
	public void dispose() {
		for ( final ILayer d : enabledLayers ) {
			d.dispose();
		}
		for ( final ILayer d : disabledLayers ) {
			d.dispose();
		}
		enabledLayers.clear();
		disabledLayers.clear();
	}

	@Override
	public ILayer addLayer(final ILayer d) {
		if ( addItem(d) ) { return d; }
		return null;
	}

	public ILayer removeLayer(final ILayer found) {
		if ( found != null ) {
			enabledLayers.remove(found);
		}
		Collections.sort(enabledLayers);
		return found;
	}

	@Override
	public List<ILayer> getLayersIntersecting(final int x, final int y) {
		final List<ILayer> result = new ArrayList();
		for ( final ILayer display : enabledLayers ) {
			if ( display.containsScreenPoint(x, y) ) {
				result.add(display);
			}
		}
		return result;
	}

	/**
	 * Method focusOn()
	 * @see msi.gama.common.interfaces.ILayerManager#focusOn(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public Rectangle2D focusOn(final IShape geometry, final IDisplaySurface s) {
		for ( final ILayer display : enabledLayers ) {
			Rectangle2D r = display.focusOn(geometry, s);
			if ( r != null ) { return r; }
		}
		return null;
	}

	private void enable(final ILayer found) {
		found.enableOn(surface);
		enabledLayers.add(found);
		disabledLayers.remove(found);
		Collections.sort(enabledLayers);
	}

	@Override
	public boolean isEnabled(final ILayer item) {
		return enabledLayers.contains(item);
	}

	private void disable(final ILayer found) {
		final ILayer ff = removeLayer(found);
		if ( ff != null ) {
			ff.disableOn(surface);
			disabledLayers.add(ff);
		}
	}

	@Override
	public void enableLayer(final ILayer display, final Boolean enable) {
		while (!surface.canBeUpdated()) {
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		final boolean before = surface.canBeUpdated();
		surface.canBeUpdated(false);
		if ( enable ) {
			enable(display);
		} else {
			disable(display);
		}
		surface.canBeUpdated(before);

	}

	@Override
	public void drawLayersOn(final IGraphics g) {
		IScope scope = surface.getDisplayScope();
		// If the experiment is already closed
		if ( scope == null || scope.interrupted() ) { return; }
		scope.setGraphics(g);
		try {
			g.beginDrawingLayers();
			for ( int i = 0, n = enabledLayers.size(); i < n; i++ ) {
				final ILayer dis = enabledLayers.get(i);
				//hqnghi: if layer have its own scope (from other experiment, init layer with it
				if ( dis.getPrivateScope() != null ) {
					GAMA.releaseScope(scope);
					scope = dis.getPrivateScope().copy();
					if ( scope == null || scope.interrupted() ) { return; }
					scope.setGraphics(g);
				}
				//end-hqnghi
				dis.drawDisplay(scope, g);
			}
		} catch (final Exception e) {
			GuiUtils.debug(e);
		} finally {
			g.endDrawingLayers();
		}
	}

	@Override
	public List<ILayer> getItems() {
		final List<ILayer> items = new ArrayList();
		items.addAll(enabledLayers);
		items.addAll(disabledLayers);
		Collections.sort(items);
		return items;
	}

	@Override
	public void removeItem(final ILayer found) {
		if ( found != null ) {
			enabledLayers.remove(found);
		}
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
	public void focusItem(final ILayer obj) {}

	@Override
	public boolean addItem(final ILayer obj) {
		obj.setOrder(count++);
		enabledLayers.add(obj);
		Collections.sort(enabledLayers);
		obj.firstLaunchOn(surface);
		return true;
	}

	@Override
	public void updateItemValues() {}

	public static ILayer createLayer(final IScope scope, final ILayerStatement layer) {
		switch (layer.getType()) {

			case ILayerStatement.GRID: {
				return new GridLayer(scope, layer);
			}
			case ILayerStatement.AGENTS: {
				return new AgentLayer(layer);
			}
			case ILayerStatement.SPECIES: {
				return new SpeciesLayer(layer);
			}
			case ILayerStatement.TEXT: {
				return new TextLayer(layer);
			}
			case ILayerStatement.IMAGE: {
				return new ImageLayer(scope, layer);
			}
			case ILayerStatement.GIS: {
				return new GisLayer(layer);
			}
			case ILayerStatement.CHART: {
				return new ChartLayer(layer);
			}
			case ILayerStatement.QUADTREE: {
				return new QuadTreeLayer(layer);
			}
			case ILayerStatement.EVENT: {
				return new EventLayer(layer);
			}
			case ILayerStatement.GRAPHICS: {
				return new GraphicLayer(layer);
			}
			default:
				return null;
		}
	}

	/**
	 * Allows the layers to do some cleansing when the output of the display changes
	 * @see msi.gama.common.interfaces.ILayerManager#outputChanged()
	 */
	@Override
	public void outputChanged() {
		for ( final ILayer i : enabledLayers ) {
			i.reloadOn(surface);
		}
		for ( final ILayer i : disabledLayers ) {
			i.reloadOn(surface);
		}
	}

	@Override
	public boolean stayProportional() {
		for ( final ILayer i : enabledLayers ) {
			if ( i.stayProportional() ) { return true; }
		}
		return false;
	}

}
