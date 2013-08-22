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
	public void outputChanged() {}

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
		EditorFactory.create(compo, "Opacity:", definition.getTransparency(), 0.0, 1.0, 0.1, false,
			new EditorListener<Double>() {

				@Override
				public void valueModified(final Double newValue) {
					setOpacity(1d - newValue);
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
		EditorFactory.create(compo, "Extent:", definition.getBox().getExtent(), new EditorListener<GamaPoint>() {

			@Override
			public void valueModified(final GamaPoint newValue) {
				setExtent(newValue);
				if ( isPaused(container) ) {
					container.forceUpdateDisplay();
				}
			}

		});
		EditorFactory.create(compo, "Elevation:", definition.getElevation(), 0.0, 1.0, 0.1, false,
			new EditorListener<Double>() {

				@Override
				public void valueModified(final Double newValue) {
					setElevation(newValue);
					if ( isPaused(container) ) {
						container.forceUpdateDisplay();
					}
				}

			});
	}

	@Override
	public void dispose() {}

	@Override
	public final void drawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		// if ( scope.interrupted() ) { return; }
		if ( definition != null ) {
			definition.getBox().compute(scope);
			// definition.step(scope);
			g.setOpacity(definition.getTransparency());
			setPositionAndSize(definition.getBox(), g);
		}
		g.beginDrawingLayer(this);
		privateDrawDisplay(scope, g);
		g.endDrawingLayer(this);
	}

	@Override
	public void setOpacity(final Double opacity) {
		definition.setOpacity(opacity);
	}

	public void setPosition(final GamaPoint p) {
		definition.getBox().setPosition(p);
	}

	public ILocation getPosition() {
		return definition.getBox().getPosition();
	}

	public void setExtent(final GamaPoint p) {
		definition.getBox().setExtent(p);
	}

	@Override
	public void setElevation(final Double elevation) {
		definition.setElevation(elevation);
	}

	@Override
	public double getZPosition() {
		return definition.getElevation();
	}

	@Override
	public Boolean isDynamic() {
		return definition.getRefresh();
	}

	/**
	 * @param boundingBox
	 * @param g
	 */
	private void setPositionAndSize(final IDisplayLayerBox box, final IGraphics g) {
		// Voir comment conserver cette information
		final int displayPixelWidth = g.getDisplayWidthInPixels();
		final int displayPixelHeight = g.getDisplayHeightInPixels();

		// Computation of x
		final double x = box.getPosition().getX();
		double relative_x = 0;
		if ( box.isAbsoluteX() ) {
			relative_x = x * g.getxRatioBetweenPixelsAndModelUnits();
		} else {
			relative_x = Math.abs(x) <= 1 ? displayPixelWidth * x : g.getxRatioBetweenPixelsAndModelUnits() * x;
		}
		final double absolute_x = Math.signum(x) < 0 ? displayPixelWidth + relative_x : relative_x;

		// Computation of y
		final double y = box.getPosition().getY();
		double relative_y = 0;
		if ( box.isAbsoluteY() ) {
			relative_y = y * g.getyRatioBetweenPixelsAndModelUnits();
		} else {
			relative_y = Math.abs(y) <= 1 ? displayPixelHeight * y : g.getyRatioBetweenPixelsAndModelUnits() * y;
		}
		final double absolute_y = Math.signum(y) < 0 ? displayPixelHeight + relative_y : relative_y;

		// Computation of width
		double absolute_width = 0;
		final double width = box.getExtent().getX();
		if ( box.isAbsoluteWidth() ) {
			absolute_width = width * g.getxRatioBetweenPixelsAndModelUnits();
		} else {
			absolute_width =
				Math.abs(width) <= 1 ? displayPixelWidth * width : g.getxRatioBetweenPixelsAndModelUnits() * width;
		}
		// Computation of height
		double absolute_height = 0;
		final double height = box.getExtent().getY();
		if ( box.isAbsoluteHeight() ) {
			absolute_height = height * g.getyRatioBetweenPixelsAndModelUnits();
		} else {
			absolute_height =
				Math.abs(height) <= 1 ? displayPixelHeight * height : g.getyRatioBetweenPixelsAndModelUnits() * height;
		}
		sizeInPixels.setLocation(absolute_width, absolute_height);
		// GuiUtils.debug("AbstractLayer.setSize : " + sizeInPixels);
		positionInPixels.setLocation(absolute_x, absolute_y);
		// GuiUtils.debug("AbstractLayer.setPosition : " + positionInPixels);
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
	public boolean containsScreenPoint(final int x, final int y) {
		return x >= positionInPixels.x && y >= positionInPixels.y && x <= positionInPixels.x + sizeInPixels.x &&
			y <= positionInPixels.y + sizeInPixels.y;
	}

	@Override
	public GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final IDisplaySurface g) {
		final double xScale = sizeInPixels.x / g.getEnvWidth();
		final double yScale = sizeInPixels.y / g.getEnvHeight();
		final int xInDisplay = xOnScreen - positionInPixels.x;
		final int yInDisplay = yOnScreen - positionInPixels.y;
		final double xInModel = xInDisplay / xScale;
		final double yInModel = yInDisplay / yScale;
		return new GamaPoint(xInModel, yInModel);
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
