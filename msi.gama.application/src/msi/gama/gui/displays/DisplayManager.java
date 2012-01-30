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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.outputs.layers.IDisplayLayer;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;

/**
 * Written by drogoul Modified on 23 janv. 2011
 * 
 * @todo Description
 * 
 */
public class DisplayManager implements IDisplayManager {

	public final IList<IDisplay> enabledDisplays = new GamaList();
	final IList<IDisplay> disabledDisplays = new GamaList();
	final IDisplaySurface surface;
	final PauseDisplay pd = new PauseDisplay(0d, 0d, null, null);

	public DisplayManager(final IDisplaySurface surface) {
		this.surface = surface;
	}

	@Override
	public void dispose() {
		for ( IDisplay d : enabledDisplays ) {
			d.dispose();
		}
		for ( IDisplay d : disabledDisplays ) {
			d.dispose();
		}
		pd.dispose();
		enabledDisplays.clear();
		disabledDisplays.clear();
	}

	@Override
	public IDisplay addDisplay(final IDisplay d) {
		if ( addItem(d) ) { return d; }
		return null;
	}

	public IDisplay removeDisplay(final IDisplay found) {
		if ( found != null ) {
			enabledDisplays.remove(found);
		}
		Collections.sort(enabledDisplays);
		return found;
	}

	@Override
	public List<IDisplay> getDisplays(final int x, final int y) {
		List<IDisplay> result = new ArrayList();
		for ( IDisplay display : enabledDisplays ) {
			if ( display.containsScreenPoint(x, y) ) {
				result.add(display);
			}
		}
		return result;
	}

	private void enable(final IDisplay found) {
		enabledDisplays.add(found);
		disabledDisplays.remove(found);
		Collections.sort(enabledDisplays);
	}

	@Override
	public boolean isEnabled(final IDisplay item) {
		return enabledDisplays.contains(item);
	}

	private void disable(final IDisplay found) {
		IDisplay ff = removeDisplay(found);
		if ( ff != null ) {
			disabledDisplays.add(ff);
		}
	}

	@Override
	public void enableDisplay(final IDisplay display, final Boolean enable) {
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

	@Override
	public void drawDisplaysOn(final IGraphics g) {
		try {
			for ( int i = 0, n = enabledDisplays.size(); i < n; i++ ) {
				final IDisplay dis = enabledDisplays.get(i);
				dis.drawDisplay(g);

			}
			if ( surface.isPaused() ) {
				pd.drawDisplay(g);
			}
		} catch (GamaRuntimeException e) {
			// e.addContext("in drawing layer " + dis.getMenuName());
			// throw e;
			// Temporarily disabled so that it does not appear on the interface.
			e.printStackTrace();
		}
	}

	@Override
	public List<IDisplay> getItems() {
		List<IDisplay> items = new ArrayList();
		items.addAll(enabledDisplays);
		items.addAll(disabledDisplays);
		Collections.sort(items);
		return items;
	}

	@Override
	public void removeItem(final IDisplay found) {
		if ( found != null ) {
			enabledDisplays.remove(found);
		}
		Collections.sort(enabledDisplays);
	}

	@Override
	public void pauseItem(final IDisplay obj) {}

	@Override
	public void resumeItem(final IDisplay obj) {}

	@Override
	public String getItemDisplayName(final IDisplay obj, final String previousName) {
		return obj.getMenuName();
	}

	@Override
	public void focusItem(final IDisplay obj) {}

	@Override
	public boolean addItem(final IDisplay obj) {
		enabledDisplays.add(obj);
		Collections.sort(enabledDisplays);
		return true;
	}

	@Override
	public void updateItemValues() {}

	public static IDisplay createDisplay(final IDisplayLayer layer, final double env_width,
		final double env_height, final IGraphics dg) {
		switch (layer.getType()) {
			case IDisplayLayer.GRID: {
				return new GridDisplay(env_width, env_height, layer, dg);
			}
			case IDisplayLayer.AGENTS: {
				return new AgentDisplay(env_width, env_height, layer, dg);
			}
			case IDisplayLayer.SPECIES: {
				return new SpeciesDisplay(env_width, env_height, layer, dg);
			}
			case IDisplayLayer.TEXT: {
				return new TextDisplay(env_width, env_height, layer, dg);
			}
			case IDisplayLayer.IMAGE: {
				return new ImageDisplay(env_width, env_height, layer, dg);
			}
			case IDisplayLayer.GIS: {
				return new GisDisplay(env_width, env_height, layer, dg);
			}
			case IDisplayLayer.CHART: {
				return new ChartDisplay(env_width, env_height, layer, dg);
			}
			case IDisplayLayer.QUADTREE: {
				return new QuadTreeDisplay(env_width, env_height, layer, dg);
			}
			default:
				return null;
		}
	}

	@Override
	public void updateEnvDimensions(final double env_width, final double env_height) {
		for ( IDisplay i : enabledDisplays ) {
			i.updateEnvDimensions(env_width, env_height);
		}
		for ( IDisplay i : disabledDisplays ) {
			i.updateEnvDimensions(env_width, env_height);
		}
	}
}
