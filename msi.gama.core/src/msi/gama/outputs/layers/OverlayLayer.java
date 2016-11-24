/*********************************************************************************************
 *
 * 'OverlayLayer.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.fastmaths.FastMath;

/**
 * Class OverlayLayer.
 *
 * @author drogoul
 * @since 23 févr. 2016
 *
 */
public class OverlayLayer extends GraphicLayer {

	boolean computed = false;

	protected OverlayLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	public Rectangle2D focusOn(final IShape geometry, final IDisplaySurface s) {
		// Cannot focus
		return null;
	}

	@Override
	public String getType() {
		return IKeyword.OVERLAY;
	}

	@Override
	protected void privateDrawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		g.setOpacity(1);
		final IAgent agent = scope.getAgent();
		scope.execute(((OverlayStatement) definition).getAspect(), agent, null);
	}

	@Override
	public void drawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		if (definition != null) {
			definition.getBox().compute(scope);
			setPositionAndSize(definition.getBox(), g);

		}

		g.beginDrawingLayer(this);
		if (definition != null)
			g.setOpacity(definition.getTransparency());
		g.beginOverlay(this);
		privateDrawDisplay(scope, g);
		g.endOverlay();
		g.endDrawingLayer(this);
	}

	@Override
	protected void setPositionAndSize(final IDisplayLayerBox box, final IGraphics g) {
		if (computed) {
			return;
		}
		// Voir comment conserver cette information
		final int pixelWidth = g.getDisplayWidth();
		final int pixelHeight = g.getDisplayHeight();
		final double envWidth = g.getSurface().getData().getEnvWidth();
		final double envHeight = g.getSurface().getData().getEnvHeight();
		final double xRatioBetweenPixelsAndModelUnits = pixelWidth / envWidth;
		final double yRatioBetweenPixelsAndModelUnits = pixelHeight / envHeight;

		ILocation point = box.getPosition();
		// Computation of x
		final double x = point.getX();
		final double relative_x = FastMath.abs(x) <= 1 ? pixelWidth * x : xRatioBetweenPixelsAndModelUnits * x;
		final double absolute_x = FastMath.signum(x) < 0 ? pixelWidth + relative_x : relative_x;
		// Computation of y
		final double y = point.getY();
		final double relative_y = FastMath.abs(y) <= 1 ? pixelHeight * y : yRatioBetweenPixelsAndModelUnits * y;
		final double absolute_y = FastMath.signum(y) < 0 ? pixelHeight + relative_y : relative_y;

		point = box.getSize();
		// Computation of width
		final double w = point.getX();
		final double absolute_width = FastMath.abs(w) <= 1 ? pixelWidth * w : xRatioBetweenPixelsAndModelUnits * w;
		// Computation of height
		final double h = point.getY();
		final double absolute_height = FastMath.abs(h) <= 1 ? pixelHeight * h : yRatioBetweenPixelsAndModelUnits * h;
		sizeInPixels.setLocation(absolute_width, absolute_height);
		positionInPixels.setLocation(absolute_x, absolute_y);
		// System.out.println("Overlay position: " + positionInPixels + " size:
		// " + sizeInPixels);
		definition.getBox().setConstantBoundingBox(true);
		computed = true;
	}

	/**
	 * @return
	 */
	public Color getBackground() {
		return ((OverlayStatement) definition).getBackgroundColor();
	}

	public Color getBorder() {
		return ((OverlayStatement) definition).getBorderColor();
	}

	public boolean isRounded() {
		return ((OverlayStatement) definition).isRounded();
	}

	@Override
	public boolean isProvidingCoordinates() {
		return false; // by default
	}

	@Override
	public boolean isProvidingWorldCoordinates() {
		return false; // by default
	}

}
