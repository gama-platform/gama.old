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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays.layers;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.runtime.*;
import msi.gama.util.*;

/**
 * Written by drogoul Modified on 23 janv. 2011
 * 
 * @todo Description
 * 
 */
public class LayerManager implements ILayerManager {

	// public final ILayer[] layers = new ILayer[20];
	// public final boolean[] enabled = new boolean[20];
	private final IList<ILayer> enabledLayers = new GamaList();
	private final IList<ILayer> disabledLayers = new GamaList();
	private final IDisplaySurface surface;
	// private final PauseLayer pd = new PauseLayer();
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
		// pd.dispose();
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
		// GuiUtils.debug("Enabled layers (ordered):" + enabledLayers);
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

	private void enable(final ILayer found) {
		enabledLayers.add(found);
		disabledLayers.remove(found);
		Collections.sort(enabledLayers);
		// GuiUtils.debug("Enabled layers (ordered):" + enabledLayers);
	}

	@Override
	public boolean isEnabled(final ILayer item) {
		return enabledLayers.contains(item);
	}

	private void disable(final ILayer found) {
		final ILayer ff = removeLayer(found);
		if ( ff != null ) {
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
		final IScope scope = GAMA.obtainNewScope();
		// If the experiment is already closed
		if ( scope == null || scope.interrupted() ) { return; }
		// GuiUtils.debug("LayerManager.drawLayersOn");
		scope.setGraphics(g);
		try {
			g.beginDrawingLayers();
			for ( int i = 0, n = enabledLayers.size(); i < n; i++ ) {
				final ILayer dis = enabledLayers.get(i);
				dis.drawDisplay(scope, g);
			}
			// if ( surface.isPaused() ) {
			// double max_z = 0d;
			// for ( int i = 0, n = enabledLayers.size(); i < n; i++ ) {
			// final ILayer dis = enabledLayers.get(i);
			// final double z = dis.getZPosition();
			// if ( z > max_z ) {
			// max_z = z;
			// }
			// }
			// // pd.setElevation(max_z);
			// // pd.drawDisplay(scope, g);
			// }
		} catch (final Exception e) {
			GuiUtils.debug(e);
		}
		// catch (final Exception e) {
		// // e.addContext("in drawing layer " + dis.getMenuName());
		// // throw e;
		// // Temporarily disabled so that it does not appear on the interface.
		// e.printStackTrace();
		// }
		finally {
			g.endDrawingLayers();
			GAMA.releaseScope(scope);
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
		return true;
	}

	@Override
	public void updateItemValues() {}

	public static ILayer createLayer(final ILayerStatement layer, final double env_width, final double env_height,
		final IGraphics dg) {
		switch (layer.getType()) {

			case ILayerStatement.GRID: {
				return new GridLayer(layer);
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
				return new ImageLayer(layer);
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
			i.outputChanged();
		}
		for ( final ILayer i : disabledLayers ) {
			i.outputChanged();
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
