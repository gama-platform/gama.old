/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.graphics;

import java.util.*;
import msi.gama.gui.application.views.ItemList;
import msi.gama.gui.displays.*;
import msi.gama.gui.graphics.DisplayManager.DisplayItem;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.outputs.layers.AbstractDisplayLayer;
import msi.gama.util.GamaList;

/**
 * Written by drogoul Modified on 23 janv. 2011
 * 
 * @todo Description
 * 
 */
public class DisplayManager implements ItemList<DisplayItem> {

	public final GamaList<DisplayItem> enabledDisplays = new GamaList();
	final GamaList<DisplayItem> disabledDisplays = new GamaList();
	final IDisplaySurface surface;

	public DisplayManager(final IDisplaySurface surface) {
		this.surface = surface;
	}

	public void dispose() {
		for ( DisplayItem i : enabledDisplays ) {
			i.display.dispose();
		}
		for ( DisplayItem i : disabledDisplays ) {
			i.display.dispose();
		}
		enabledDisplays.clear();
		disabledDisplays.clear();
	}

	public IDisplay addDisplay(final IDisplay d, final String name) {
		DisplayItem item = new DisplayItem(d, enabledDisplays.size());
		if ( addItem(item) ) { return d; }
		return null;
	}

	public DisplayItem removeDisplay(final DisplayItem found) {
		if ( found != null ) {
			enabledDisplays.remove(found);
		}
		Collections.sort(enabledDisplays);
		return found;
	}

	public List<IDisplay> getDisplays(final int x, final int y) {
		List<IDisplay> result = new ArrayList();
		for ( DisplayItem item : enabledDisplays ) {
			IDisplay display = item.display;
			if ( display.containsScreenPoint(x, y) ) {
				result.add(display);
			}
		}
		return result;
	}

	private void enable(final DisplayItem found) {
		enabledDisplays.add(found);
		disabledDisplays.remove(found);
		Collections.sort(enabledDisplays);
	}

	public boolean isEnabled(final DisplayItem item) {
		return enabledDisplays.contains(item);
	}

	private void disable(DisplayItem found) {
		found = removeDisplay(found);
		if ( found != null ) {
			disabledDisplays.add(found);
		}
	}

	public void enableDisplay(final Object data, final boolean enable) {
		DisplayItem display = (DisplayItem) data;
		while (!surface.canBeUpdated()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		boolean before = surface.canBeUpdated();
		surface.canBeUpdated(false);
		if ( enable ) {
			enable(display);
		} else {
			disable(display);
		}
		surface.canBeUpdated(before);

	}

	public void drawDisplaysOn(final IGraphics g) throws GamaRuntimeException {
		for ( int i = 0, n = enabledDisplays.size(); i < n; i++ ) {
			final IDisplay dis = enabledDisplays.get(i).display;
			try {
				dis.drawDisplay(g);
			} catch (GamaRuntimeException e) {
				// e.addContext("in drawing layer " + dis.getMenuName());
				// throw e;
				// Temporarily disabled so that it does not appear on the interface.
				e.printStackTrace();
			}
		}
	}

	public static class DisplayItem implements Comparable {

		public final IDisplay display;
		private final Integer order;

		DisplayItem(final IDisplay d, final Integer o) {
			display = d;
			order = o;
		}

		@Override
		public int compareTo(final Object o) {
			return order.compareTo(((DisplayItem) o).order);
		}

		@Override
		public boolean equals(final Object o) {
			if ( o == null ) { return false; }
			if ( o instanceof DisplayItem ) { return order.equals(((DisplayItem) o).order); }
			return false;
		}

		@Override
		public int hashCode() {
			return order;
		}
	}

	@Override
	public List<DisplayItem> getItems() {
		List<DisplayItem> items = new ArrayList();
		items.addAll(enabledDisplays);
		items.addAll(disabledDisplays);
		Collections.sort(items);
		return items;
	}

	@Override
	public void removeItem(final DisplayItem found) {
		if ( found != null ) {
			enabledDisplays.remove(found);
		}
		Collections.sort(enabledDisplays);
	}

	@Override
	public void pauseItem(final DisplayItem obj) {}

	@Override
	public void resumeItem(final DisplayItem obj) {}

	@Override
	public String getItemDisplayName(final DisplayItem obj, final String previousName) {
		return obj.display.getMenuName();
	}

	@Override
	public void focusItem(final DisplayItem obj) {}

	@Override
	public boolean addItem(final DisplayItem obj) {
		enabledDisplays.add(obj);
		Collections.sort(enabledDisplays);
		return true;
	}

	@Override
	public void updateItemValues() {}

	public static IDisplay createDisplay(final AbstractDisplayLayer layer, final double env_width,
		final double env_height, final IGraphics dg) {
		switch (layer.getType()) {
			case IDisplay.GRID: {
				return new GridDisplay(env_width, env_height, layer, dg);
			}
			case IDisplay.AGENTS: {
				return new AgentDisplay(env_width, env_height, layer, dg);
			}
			case IDisplay.SPECIES: {
				return new SpeciesDisplay(env_width, env_height, layer, dg);
			}
			case IDisplay.TEXT: {
				return new TextDisplay(env_width, env_height, layer, dg);
			}
			case IDisplay.IMAGE: {
				return new ImageDisplay(env_width, env_height, layer, dg);
			}
			case IDisplay.GIS: {
				return new GisDisplay(env_width, env_height, layer, dg);
			}
			case IDisplay.CHART: {
				return new ChartDisplay(env_width, env_height, layer, dg);
			}
			case IDisplay.QUADTREE: {
				return new QuadTreeDisplay(env_width, env_height, layer, dg);
			}
			default:
				return null;
		}
	}

	public void updateEnvDimensions(final double env_width, final double env_height) {
		for ( DisplayItem i : enabledDisplays ) {
			i.display.updateEnvDimensions(env_width, env_height);
		}
		for ( DisplayItem i : disabledDisplays ) {
			i.display.updateEnvDimensions(env_width, env_height);
		}
	}
}
