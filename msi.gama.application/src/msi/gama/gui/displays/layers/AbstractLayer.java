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

import java.awt.Point;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.eclipse.swt.widgets.Composite;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
public abstract class AbstractLayer implements ILayer {

	private Integer order = 0;
	protected ILayerStatement definition;
	private String name;
	private final Point positionInPixels, sizeInPixels;

	protected AbstractLayer(final ILayerStatement layer) {
		definition = layer;
		if ( definition != null ) {
			setName(definition.getName());
		}
		sizeInPixels = new Point(0, 0);
		positionInPixels = new Point(0, 0);
	}

	@Override
	public void reloadOn(final IDisplaySurface surface) {}

	@Override
	public void firstLaunchOn(final IDisplaySurface surface) {}

	@Override
	public void enableOn(final IDisplaySurface surface) {}

	@Override
	public void disableOn(final IDisplaySurface surface) {}

	@Override
	public void setOrder(final Integer o) {
		order = o;
	}

	@Override
	public Integer getOrder() {
		return order;
	}

	@Override
	public int compareTo(final ILayer o) {
		return order.compareTo(o.getOrder());
	}

	protected boolean isPaused(final IDisplaySurface container) {
		return container.isPaused() || GAMA.isPaused();
	}

	public void fillComposite(final Composite compo, final IDisplaySurface container) {

		EditorFactory.create(compo, "Visible:", container.getManager().isEnabled(this), new EditorListener<Boolean>() {

			@Override
			public void valueModified(final Boolean newValue) {

				container.getManager().enableLayer(AbstractLayer.this, newValue);
				if ( isPaused(container) ) {
					container.forceUpdateDisplay();
				}
			}
		});
		EditorFactory.create(compo, "Transparency:", definition.getTransparency(), 0.0, 1.0, 0.1, false,
			new EditorListener<Double>() {

				@Override
				public void valueModified(final Double newValue) {
					setTransparency(newValue);
					if ( isPaused(container) ) {
						container.forceUpdateDisplay();
					}
				}

			});
		EditorFactory.create(compo, "Position:", definition.getBox().getPosition(), new EditorListener<GamaPoint>() {

			@Override
			public void valueModified(final GamaPoint newValue) {
				setPosition(newValue);
				if ( isPaused(container) ) {
					container.forceUpdateDisplay();
				}
			}

		});
		EditorFactory.create(compo, "Size:", definition.getBox().getSize(), new EditorListener<GamaPoint>() {

			@Override
			public void valueModified(final GamaPoint newValue) {
				setExtent(newValue);
				if ( isPaused(container) ) {
					container.forceUpdateDisplay();
				}
			}

		});
		// EditorFactory.create(compo, "Elevation:", definition.getElevation(), 0.0, 1.0, 0.1, false,
		// new EditorListener<Double>() {
		//
		// @Override
		// public void valueModified(final Double newValue) {
		// setElevation(newValue);
		// if ( isPaused(container) ) {
		// container.forceUpdateDisplay();
		// }
		// }
		//
		// });
	}

	@Override
	public void dispose() {}

	@Override
	public void drawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		if ( definition != null ) {
			definition.getBox().compute(scope);
			g.setOpacity(definition.getTransparency());
			setPositionAndSize(definition.getBox(), g);
		}
		g.beginDrawingLayer(this);
		privateDrawDisplay(scope, g);
		g.endDrawingLayer(this);
	}

	@Override
	public void setTransparency(final Double transparency) {
		definition.setTransparency(transparency);
	}

	public void setPosition(final GamaPoint p) {
		definition.getBox().setPosition(p);
	}

	@Override
	public ILocation getPosition() {
		return definition.getBox().getPosition();
	}

	public void setExtent(final GamaPoint p) {
		definition.getBox().setSize(p);
	}

	@Override
	public ILocation getExtent() {
		return definition.getBox().getSize();
	}

	@Override
	public void setElevation(final Double elevation) {
		ILocation original = definition.getBox().getPosition();
		definition.getBox().setPosition(original.getX(), original.getY(), elevation);
	}

	@Override
	public Boolean isDynamic() {
		return definition.getRefresh();
	}

	/**
	 * @param boundingBox
	 * @param g
	 */
	protected void setPositionAndSize(final IDisplayLayerBox box, final IGraphics g) {
		// Voir comment conserver cette information
		final int pixelWidth = g.getDisplayWidthInPixels();
		final int pixelHeight = g.getDisplayHeightInPixels();

		ILocation point = box.getPosition();
		// Computation of x
		final double x = point.getX();
		double relative_x = Math.abs(x) <= 1 ? pixelWidth * x : g.getxRatioBetweenPixelsAndModelUnits() * x;
		final double absolute_x = Math.signum(x) < 0 ? pixelWidth + relative_x : relative_x;
		// Computation of y
		final double y = point.getY();
		double relative_y = Math.abs(y) <= 1 ? pixelHeight * y : g.getyRatioBetweenPixelsAndModelUnits() * y;
		final double absolute_y = Math.signum(y) < 0 ? pixelHeight + relative_y : relative_y;

		point = box.getSize();
		// Computation of width
		final double w = point.getX();
		double absolute_width = Math.abs(w) <= 1 ? pixelWidth * w : g.getxRatioBetweenPixelsAndModelUnits() * w;
		// Computation of height
		final double h = point.getY();
		double absolute_height = Math.abs(h) <= 1 ? pixelHeight * h : g.getyRatioBetweenPixelsAndModelUnits() * h;
		sizeInPixels.setLocation(absolute_width, absolute_height);
		positionInPixels.setLocation(absolute_x, absolute_y);
	}

	@Override
	public Point getSizeInPixels() {
		return sizeInPixels;
	}

	@Override
	public Point getPositionInPixels() {
		return positionInPixels;
	}

	@Override
	public Integer getTrace() {
		return definition.getBox().getTrace();
	}

	@Override
	public Boolean getFading() {
		return definition.getBox().getFading();
	}

	@Override
	public boolean containsScreenPoint(final int x, final int y) {
		return x >= positionInPixels.x && y >= positionInPixels.y && x <= positionInPixels.x + sizeInPixels.x &&
			y <= positionInPixels.y + sizeInPixels.y;
	}

	@Override
	public GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final IDisplaySurface g) {
		return g.getModelCoordinatesFrom(xOnScreen, yOnScreen,sizeInPixels,positionInPixels);	
	}

	@Override
	public Point getScreenCoordinatesFrom(final double x, final double y, final IDisplaySurface g) {
		final double xFactor = x / g.getEnvWidth();
		final double yFactor = y / g.getEnvHeight();
		final int xOnDisplay = (int) (xFactor * sizeInPixels.x);
		final int yOnDisplay = (int) (yFactor * sizeInPixels.y);
		return new Point(xOnDisplay, yOnDisplay);

	}

	protected abstract void privateDrawDisplay(IScope scope, final IGraphics g) throws GamaRuntimeException;

	@Override
	public Set<IAgent> collectAgentsAt(final int x, final int y, final IDisplaySurface g) {
		// Nothing to do by default
		return Collections.EMPTY_SET;
	}

	@Override
	public String getMenuName() {
		return getType() + ItemList.SEPARATION_CODE + getName();
	}

	@Override
	public abstract String getType();

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final void setName(final String name) {
		this.name = name;
	}

	@Override
	public boolean stayProportional() {
		return true;
	}

}
