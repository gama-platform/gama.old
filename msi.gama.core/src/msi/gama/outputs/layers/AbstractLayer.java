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

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.charts.ChartLayer;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
public abstract class AbstractLayer implements ILayer {

	protected ILayerStatement definition;
	private String name;
	protected double addedElevation;
	protected final Point positionInPixels;
	protected final Point sizeInPixels;
	private Envelope visibleModelRegion;
	boolean hasBeenDrawnOnce;

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

	public AbstractLayer() {
		sizeInPixels = new Point(0, 0);
		positionInPixels = new Point(0, 0);
	}

	public AbstractLayer(final ILayerStatement layer) {
		definition = layer;
		if (definition != null) {
			setName(definition.getName());
		}
		sizeInPixels = new Point(0, 0);
		positionInPixels = new Point(0, 0);
	}

	@Override
	public void forceRedrawingOnce() {
		hasBeenDrawnOnce = false;
	}

	@Override
	public void dispose() {}

	@Override
	public void drawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		if (!g.is2D() && !isDynamic() && hasBeenDrawnOnce) { return; }
		if (g.isNotReadyToUpdate() && hasBeenDrawnOnce) { return; }
		if (definition != null) {
			definition.getBox().compute(scope);
			setPositionAndSize(definition.getBox(), g);
			g.setOpacity(definition.getTransparency());
		}
		g.beginDrawingLayer(this);
		privateDrawDisplay(scope, g);
		g.endDrawingLayer(this);
		hasBeenDrawnOnce = true;
	}

	@Override
	public void addElevation(final double elevation) {
		addedElevation = elevation;
	}

	@Override
	public double getAddedElevation() {
		return addedElevation;
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

		double relative_x;
		if (!box.isRelativePosition()) {
			relative_x = g.getxRatioBetweenPixelsAndModelUnits() * x;
		} else {
			relative_x = Math.abs(x) <= 1 ? pixelWidth * x : g.getxRatioBetweenPixelsAndModelUnits() * x;
		}
		final double absolute_x = Math.signum(x) < 0 ? pixelWidth + relative_x : relative_x;
		// Computation of y

		final double y = point.getY();
		double relative_y;
		if (!box.isRelativePosition()) {
			relative_y = g.getyRatioBetweenPixelsAndModelUnits() * y;
		} else {
			relative_y = Math.abs(y) <= 1 ? pixelHeight * y : g.getyRatioBetweenPixelsAndModelUnits() * y;
		}

		relative_y = Math.abs(y) <= 1 ? pixelHeight * y : g.getyRatioBetweenPixelsAndModelUnits() * y;
		final double absolute_y = Math.signum(y) < 0 ? pixelHeight + relative_y : relative_y;

		point = box.getSize();
		// Computation of width
		final double w = point.getX();
		double absolute_width;
		if (!box.isRelativeSize()) {
			absolute_width = g.getxRatioBetweenPixelsAndModelUnits() * w;
		} else {
			absolute_width = Math.abs(w) <= 1 ? pixelWidth * w : g.getxRatioBetweenPixelsAndModelUnits() * w;
		}
		// Computation of height
		final double h = point.getY();
		double absolute_height;
		if (!box.isRelativeSize()) {
			absolute_height = g.getyRatioBetweenPixelsAndModelUnits() * h;
		} else {
			absolute_height = Math.abs(h) <= 1 ? pixelHeight * h : g.getyRatioBetweenPixelsAndModelUnits() * h;
		}

		getSizeInPixels().setLocation(absolute_width, absolute_height);
		getPositionInPixels().setLocation(absolute_x, absolute_y);
	}

	@Override
	public Point getSizeInPixels() {
		return sizeInPixels;
	}

	@Override
	public Point getPositionInPixels() {
		return positionInPixels;
	}

	protected abstract void privateDrawDisplay(IScope scope, final IGraphics g) throws GamaRuntimeException;

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
			case GRID:
				return new GridLayer(scope, layer);
			case AGENTS:
				return new AgentLayer(layer);
			case SPECIES:
				return new SpeciesLayer(layer);
			case IMAGE:
				return new ImageLayer(scope, layer);
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
			default:
				return null;
		}
	}

	@Override
	public void setVisibleRegion(final Envelope e) {
		visibleModelRegion = e;
	}

	@Override
	public Envelope getVisibleRegion() {
		return visibleModelRegion;
	}

}
