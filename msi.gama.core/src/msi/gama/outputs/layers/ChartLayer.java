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
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.*;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;
import msi.gama.common.interfaces.*;
import msi.gama.runtime.IScope;
import msi.gaml.statements.draw.FileDrawingAttributes;

/**
 * Written by drogoul Modified on 1 avr. 2010
 *
 * @todo Description
 *
 */
public class ChartLayer extends AbstractLayer {

	final ChartRenderingInfo info;

	public ChartLayer(final ILayerStatement model) {
		super(model);
		info = new ChartRenderingInfo();
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
		try {
			JFreeChart chart = getChart();
			getChart().setAntiAlias(true);
			getChart().setTextAntiAlias(true);
			BufferedImage im = chart.createBufferedImage(getSizeInPixels().x, getSizeInPixels().y, info);
			FileDrawingAttributes attributes = new FileDrawingAttributes(null);
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
	public String getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g) {
		int x = xOnScreen - positionInPixels.x;
		int y = yOnScreen - positionInPixels.y;
		ChartEntity entity = info.getEntityCollection().getEntity(x, y);
		// getChart().handleClick(x, y, info);
		if ( entity instanceof XYItemEntity ) {
			XYDataset data = ((XYItemEntity) entity).getDataset();
			int index = ((XYItemEntity) entity).getItem();
			int series = ((XYItemEntity) entity).getSeriesIndex();
			double xx = data.getXValue(series, index);
			double yy = data.getYValue(series, index);
			XYPlot plot = (XYPlot) getChart().getPlot();
			ValueAxis xAxis = plot.getDomainAxis(series);
			ValueAxis yAxis = plot.getRangeAxis(series);
			boolean xInt = xx % 1 == 0;
			boolean yInt = yy % 1 == 0;
			String xTitle = xAxis.getLabel();
			if ( StringUtils.isBlank(xTitle) ) {
				xTitle = "X";
			}
			String yTitle = yAxis.getLabel();
			if ( StringUtils.isBlank(yTitle) ) {
				yTitle = "Y";
			}
			StringBuilder sb = new StringBuilder();
			sb.append(xTitle).append(" ").append(xInt ? (int) xx : String.format("%.2f", xx));
			sb.append(" | ").append(yTitle).append(" ").append(yInt ? (int) yy : String.format("%.2f", yy));
			return sb.toString();
		} else if ( entity instanceof PieSectionEntity ) {
			String title = ((PieSectionEntity) entity).getSectionKey().toString();
			PieDataset data = ((PieSectionEntity) entity).getDataset();
			int index = ((PieSectionEntity) entity).getSectionIndex();
			double xx = data.getValue(index).doubleValue();
			StringBuilder sb = new StringBuilder();
			boolean xInt = xx % 1 == 0;
			sb.append(title).append(" ").append(xInt ? (int) xx : String.format("%.2f", xx));
			return sb.toString();
		} else if ( entity instanceof CategoryItemEntity ) {
			Comparable columnKey = ((CategoryItemEntity) entity).getColumnKey();
			String title = columnKey.toString();
			CategoryDataset data = ((CategoryItemEntity) entity).getDataset();
			Comparable rowKey = ((CategoryItemEntity) entity).getRowKey();
			double xx = data.getValue(rowKey, columnKey).doubleValue();
			StringBuilder sb = new StringBuilder();
			boolean xInt = xx % 1 == 0;
			sb.append(title).append(" ").append(xInt ? (int) xx : String.format("%.2f", xx));
			return sb.toString();
		}
		return "";
	}

}
