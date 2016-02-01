/*********************************************************************************************
 *
 *
 * 'ChartLayer.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.image.BufferedImage;
import org.jfree.chart.JFreeChart;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.runtime.IScope;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;

/**
 * Written by drogoul Modified on 1 avr. 2010
 *
 * @todo Description
 *
 */
public class ChartLayer extends AbstractLayer {

	public ChartLayer(final ILayerStatement model) {
		super(model);
	}

	private JFreeChart getChart() {
		return ((ChartLayerStatement) definition).getChart();
	}

	@Override
	public String getType() {
		return "Chart layer";
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics dg) {
		BufferedImage im = getChart().createBufferedImage(getSizeInPixels().x, getSizeInPixels().y);
		DrawingAttributes attributes = new DrawingAttributes(null, null, null);
		attributes.setDynamic(true);
		dg.drawImage(im, attributes);
	}

	@Override
	public boolean stayProportional() {
		return false;
	}

}
