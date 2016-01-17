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
package msi.gama.outputs.layers.charts;

import java.awt.image.BufferedImage;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.outputs.layers.AbstractLayer;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.runtime.IScope;

import org.jfree.chart.JFreeChart;

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

//	private JFreeChart getChart() {
//		return ((ChartLayerStatement) definition).getChart();
//	}

	private ChartOutput getChart() {
	return ((ChartLayerStatement) definition).getOutput();
	}

	@Override
	public String getType() {
		return "Chart layer";
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics dg) {
		BufferedImage im = getChart().getImage(getSizeInPixels().x, getSizeInPixels().y);
		dg.drawChart(scope, im, 0.0);
	}

	@Override
	public boolean stayProportional() {
		return false;
	}

}
