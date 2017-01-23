/*********************************************************************************************
 *
 * 'AbstractLayer.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.interfaces.ItemList;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.charts.ChartLayer;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.fastmaths.FastMath;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
public abstract class AbstractLayer implements ILayer {

	@Override
	public ILayerStatement getDefinition() {
		return definition;
	}

	@Override
	public Rectangle2D focusOn(final IShape geometry, final IDisplaySurface s) {
		final Envelope3D envelope = geometry.getEnvelope();
		final Point min = this.getScreenCoordinatesFrom(envelope.getMinX(), envelope.getMinY(), s);
		final Point max = this.getScreenCoordinatesFrom(envelope.getMaxX(), envelope.getMaxY(), s);
		final Rectangle2D r = new Rectangle2D.Double(min.x, min.y, max.x - min.x, max.y - min.y);
		return r;
	}

	protected ILayerStatement definition;
	private String name;
	protected final Point positionInPixels;
	protected final Point sizeInPixels;
	private Envelope visibleModelRegion;

	protected AbstractLayer(final ILayerStatement layer) {
		definition = layer;
		if (definition != null) {
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
		definition.setOrder(o);
	}

	@Override
	public Integer getOrder() {
		return definition.getOrder();
	}

	@Override
	public int compareTo(final ILayer o) {
		return definition.compareTo(o.getDefinition());
	}

	@Override
	public void dispose() {}

	@Override
	public void drawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		if (definition != null) {
			definition.getBox().compute(scope);
			setPositionAndSize(definition.getBox(), g);
			g.setOpacity(definition.getTransparency());
		}
		g.beginDrawingLayer(this);
		privateDrawDisplay(scope, g);
		g.endDrawingLayer(this);
	}

	@Override
	public Collection<IAgent> getAgentsForMenu(final IScope scope) {
		return Collections.EMPTY_LIST;
	}

	@Override
	public void setTransparency(final Double transparency) {
		definition.setTransparency(transparency);
	}

	@Override
	public void setPosition(final ILocation p) {
		definition.getBox().setPosition(p);
	}

	@Override
	public ILocation getPosition() {
		return definition.getBox().getPosition();
	}

	@Override
	public void setExtent(final ILocation p) {
		definition.getBox().setSize(p);
	}

	@Override
	public ILocation getExtent() {
		return definition.getBox().getSize();
	}

	@Override
	public void setElevation(final Double elevation) {
		final ILocation original = definition.getBox().getPosition();
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
		final int pixelWidth = g.getDisplayWidth();
		final int pixelHeight = g.getDisplayHeight();

		ILocation point = box.getPosition();
		// Computation of x
		final double x = point.getX();
		final double relative_x = FastMath.abs(x) <= 1 ? pixelWidth * x : g.getxRatioBetweenPixelsAndModelUnits() * x;
		final double absolute_x = FastMath.signum(x) < 0 ? pixelWidth + relative_x : relative_x;
		// Computation of y
		final double y = point.getY();
		final double relative_y = FastMath.abs(y) <= 1 ? pixelHeight * y : g.getyRatioBetweenPixelsAndModelUnits() * y;
		final double absolute_y = FastMath.signum(y) < 0 ? pixelHeight + relative_y : relative_y;

		point = box.getSize();
		// Computation of width
		final double w = point.getX();
		final double absolute_width =
				FastMath.abs(w) <= 1 ? pixelWidth * w : g.getxRatioBetweenPixelsAndModelUnits() * w;
		// Computation of height
		final double h = point.getY();
		final double absolute_height =
				FastMath.abs(h) <= 1 ? pixelHeight * h : g.getyRatioBetweenPixelsAndModelUnits() * h;
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
	public Boolean isSelectable() {
		return definition.getBox().isSelectable();
	}

	@Override
	public boolean containsScreenPoint(final int x, final int y) {
		return x >= positionInPixels.x && y >= positionInPixels.y && x <= positionInPixels.x + sizeInPixels.x
				&& y <= positionInPixels.y + sizeInPixels.y;
	}

	@Override
	public ILocation getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final IDisplaySurface g) {
		return g.getModelCoordinatesFrom(xOnScreen, yOnScreen, sizeInPixels, positionInPixels);
	}

	@Override
	public String getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g) {
		// By default, returns the coordinates in the world. Redefined for
		// charts
		final ILocation point = getModelCoordinatesFrom(xOnScreen, yOnScreen, g);
		final String x = point == null ? "N/A" : String.format("%8.2f", point.getX());
		final String y = point == null ? "N/A" : String.format("%8.2f", point.getY());
		final Object[] objects = new Object[] { x, y };
		return String.format("X%10s | Y%10s", objects);
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
			// case ILayerStatement.TEXT: {
			// return new TextLayer(layer);
			// }
			case ILayerStatement.IMAGE: {
				return new ImageLayer(scope, layer);
			}
			case ILayerStatement.GIS: {
				return new GisLayer(layer);
			}
			case ILayerStatement.CHART: {
				return new ChartLayer(layer);
			}
			// case ILayerStatement.QUADTREE: {
			// return new QuadTreeLayer(layer);
			// }
			case ILayerStatement.EVENT: {
				return new EventLayer(layer);
			}
			case ILayerStatement.GRAPHICS: {
				return new GraphicLayer(layer);
			}
			case ILayerStatement.OVERLAY: {
				return new OverlayLayer(layer);
			}
			default:
				return null;
		}
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return definition.serialize(includingBuiltIn);
	}

	@Override
	public void setVisibleRegion(final Envelope e) {
		visibleModelRegion = e;
	}

	@Override
	public Envelope getVisibleRegion() {
		return visibleModelRegion;
	}

	@Override
	public boolean isProvidingCoordinates() {
		return true; // by default
	}

	@Override
	public boolean isProvidingWorldCoordinates() {
		return true; // by default
	}

}
