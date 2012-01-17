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

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import msi.gama.common.interfaces.*;
import msi.gama.gui.parameters.*;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.IDisplayLayer;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.eclipse.swt.widgets.Composite;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
public abstract class AbstractDisplay implements IDisplay {

	protected Integer order = 0;
	protected boolean disposed = false;
	protected IDisplayLayer model;
	private String name;
	private final java.awt.Point position; // The position in pixels
	protected java.awt.Point size; // The extension (from the position) in pixels
	protected java.util.List<java.awt.MenuItem> menuItems = new ArrayList();
	protected double env_width, env_height;

	protected AbstractDisplay(final double env_width, final double env_height,
		final IDisplayLayer layer, final IGraphics dg) {
		model = layer;
		if ( model != null ) {
			model.setPhysicalLayer(this);
			setName(model.getName());
		}
		size = new Point(0, 0);
		position = new Point(0, 0);
		this.env_width = env_width;
		this.env_height = env_height;
	}

	@Override
	public void updateEnvDimensions(final double env_width, final double env_height) {
		this.env_width = env_width;
		this.env_height = env_height;
	}

	@Override
	public void initMenuItems(final IDisplaySurface surface) {}

	@Override
	public void setOrder(final Integer o) {
		order = o;
	}

	@Override
	public Integer getOrder() {
		return order;
	}

	@Override
	public int compareTo(final IDisplay o) {
		return order.compareTo(o.getOrder());
	}

	public void fillComposite(final Composite compo, final IDisplaySurface container) {

		EditorFactory.create(compo, "Visible:", container.getManager().isEnabled(this),
			new EditorListener<Boolean>() {

				@Override
				public void valueModified(final Boolean newValue) {

					container.getManager().enableDisplay(AbstractDisplay.this, newValue);
					if ( container.isPaused() ) {
						container.updateDisplay();
					}
				}
			});
		EditorFactory.create(compo, "Opacity:", model.getTransparency(), 0.0, 1.0, 0.1, false,
			new EditorListener<Double>() {

				@Override
				public void valueModified(final Double newValue) {
					setOpacity(1d - newValue);
					if ( container.isPaused() ) {
						container.updateDisplay();
					}
				}

			});
		EditorFactory.create(compo, "Position:", model.getBox().getPosition(),
			new EditorListener<GamaPoint>() {

				@Override
				public void valueModified(final GamaPoint newValue) {
					setPosition(newValue);
					if ( container.isPaused() ) {
						container.updateDisplay();
					}
				}

			});
		EditorFactory.create(compo, "Extent:", model.getBox().getExtent(),
			new EditorListener<GamaPoint>() {

				@Override
				public void valueModified(final GamaPoint newValue) {
					setExtent(newValue);
					if ( container.isPaused() ) {
						container.updateDisplay();
					}
				}

			});
	}

	@Override
	public void putMenuItemsIn(final java.awt.Menu inMenu, final int x, final int y) {
		for ( java.awt.MenuItem mi : menuItems ) {
			inMenu.add(mi);
		}
	}

	@Override
	public void dispose() {
		// disposed = true;
	}

	@Override
	public final void drawDisplay(final IGraphics g) throws GamaRuntimeException {
		if ( disposed ) { return; }
		if ( model != null ) {
			g.setOpacity(model.getTransparency());
			setPositionAndSize(model.getBoundingBox(), g);
		}
		privateDrawDisplay(g);
	}

	@Override
	public void setOpacity(final Double opacity) {
		model.setOpacity(opacity);
	}

	public void setPosition(final GamaPoint p) {
		model.getBox().setPosition(p);
	}

	public void setExtent(final GamaPoint p) {
		model.getBox().setExtent(p);
	}

	/**
	 * @param boundingBox
	 * @param g
	 */
	private void setPositionAndSize(final Rectangle2D.Double b, final IGraphics g) {
		// Voir comment conserver cette information
		int w = g.getDisplayWidth();
		int h = g.getDisplayHeight();
		double x = (Math.signum(b.x) < 0 ? w : 0) + (Math.abs(b.x) <= 1 ? w * b.x : b.x);
		double y = (Math.signum(b.y) < 0 ? h : 0) + (Math.abs(b.y) <= 1 ? h * b.y : b.y);
		double width = b.width <= 1 ? w * b.width : b.width;
		double height = b.height <= 1 ? h * b.height : b.height;
		selectionWidthInModel = IDisplaySurface.SELECTION_SIZE / 2d / size.x * env_width;
		size.setLocation(width, height);
		position.setLocation(x, y);
		g.setXScale(size.x / env_width);
		g.setYScale(size.y / env_height);
		g.setDrawingOffset(position.x, position.y);
		g.setDrawingCoordinates(0, 0);
		g.setDrawingDimensions(size.x, size.y);
	}

	public int getDisplayWidth() {
		return size.x;
	}

	@Override
	public double getXScale() {
		return size.x / env_width;
	}

	@Override
	public double getYScale() {
		return size.y / env_height;
	}

	public int getDisplayHeight() {
		return size.y;
	}

	@Override
	public Point getSize() {
		return size;
	}

	@Override
	public Point getPosition() {
		return position;
	}

	public Point getEnvironmentSize() {
		return new Point((int) env_width, (int) env_height);
	}

	@Override
	public boolean containsScreenPoint(final int x, final int y) {
		return x >= position.x && y >= position.y && x <= position.x + size.x &&
			y <= position.y + size.y;
	}

	protected GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen) {
		// MetaEnvironment e = simulation.getEnvironment();
		double xScale = size.x / env_width;
		double yScale = size.y / env_height;
		int xInDisplay = xOnScreen - position.x;
		int yInDisplay = yOnScreen - position.y;
		double xInModel = xInDisplay / xScale;
		double yInModel = yInDisplay / yScale;
		return new GamaPoint(xInModel, yInModel);
	}

	double selectionWidthInModel = 0;

	protected abstract void privateDrawDisplay(final IGraphics g) throws GamaRuntimeException;

	@Override
	public void collectAgentsAt(final int x, final int y) {
		// Nothing to do by default
	}

	@Override
	public String getMenuName() {
		return getType() + ItemList.SEPARATION_CODE + getName();
	}

	protected abstract String getType();

	// @Override
	// public Image getMenuImage() {
	// return images.get(this.getClass());
	// }

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final void setName(final String name) {
		this.name = name;
	}

}
