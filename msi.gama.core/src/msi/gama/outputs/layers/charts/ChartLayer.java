/*********************************************************************************************
 *
 * 'ChartLayer.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.AbstractLayer;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.runtime.IScope;
import msi.gaml.statements.draw.FileDrawingAttributes;

/**
 * Written by drogoul Modified on 1 avr. 2010
 * 
 * @todo Description
 * 
 */
public class ChartLayer extends AbstractLayer {

	// final ChartRenderingInfo info;
	public ChartLayer(final ILayerStatement model) {
		super(model);
		// info = new ChartRenderingInfo();
	}

	@Override
	public Rectangle2D focusOn(final IShape geometry, final IDisplaySurface s) {
		// Cannot focus
		return null;
	}

	// private JFreeChart getChart() {
	// return ((ChartLayerStatement) definition).getChart();
	// }

	private ChartOutput getChart() {
		return ((ChartLayerStatement) definition).getOutput();
	}

	@Override
	public String getType() {
		return "Chart layer";
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics dg) {
		try {
			int x = getSizeInPixels().x;
			int y = getSizeInPixels().y;
			if (!dg.is2D()) {
				x = (int) (Math.min(x, y) * 0.80);
				y = x;
//				x = (int) (x* 0.80);
//				y = (int) (y* 0.80);
			}
			final BufferedImage im = getChart().getImage(scope, x, y, dg.getSurface().getData().isAntialias());
			final FileDrawingAttributes attributes = new FileDrawingAttributes(null, true);
			dg.drawImage(im, attributes);
		} catch (IndexOutOfBoundsException | IllegalArgumentException e) {
			// Do nothing. See Issue #1605
		}
	}

	@Override
	public boolean stayProportional() {
		return false;
	}

	@Override
	public boolean isProvidingWorldCoordinates() {
		return false;
	}

	@Override
	public void getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g,
			final StringBuilder sb) {
		getChart().getModelCoordinatesInfo(xOnScreen, yOnScreen, g, positionInPixels, sb);
	}

}
