package msi.gama.outputs.layers.charts;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.UnitType;

public class FastXYItemRenderer extends StandardXYItemRenderer {

	public FastXYItemRenderer() {
		super();
	}

	public FastXYItemRenderer(final int type) {
		super(type);
	}

	public FastXYItemRenderer(final int type, final XYToolTipGenerator toolTipGenerator) {
		super(type, toolTipGenerator);
	}

	public FastXYItemRenderer(final int type, final XYToolTipGenerator toolTipGenerator,
			final XYURLGenerator urlGenerator) {
		super(type, toolTipGenerator, urlGenerator);
	}

	/** A counter to prevent unnecessary Graphics2D.draw() events in drawItem() */
	private int previousDrawnItem;

	/** {@inheritDoc} */
	@Override
	public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea,
			final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis,
			final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState,
			final int pass) {

		if (!getItemVisible(series, item)) { return; }
		// setup for collecting optional entity info...
		boolean bAddEntity = false;
		Shape entityArea = null;
		EntityCollection entities = null;
		if (info != null) {
			entities = info.getOwner().getEntityCollection();
		}

		final PlotOrientation orientation = plot.getOrientation();
		final Paint paint = getItemPaint(series, item);
		final Stroke seriesStroke = getItemStroke(series, item);
		g2.setPaint(paint);
		g2.setStroke(seriesStroke);

		// get the data point...
		final double x1 = dataset.getXValue(series, item);
		final double y1 = dataset.getYValue(series, item);
		if (Double.isNaN(x1) || Double.isNaN(y1)) { return; }

		final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
		final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
		final double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
		final double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

		if (getPlotLines()) {
			if (item == 0) {
				if (this.drawSeriesLineAsPath) {
					final State s = (State) state;
					s.seriesPath.reset();
					s.lastPointGood = false;
				}
				previousDrawnItem = 0;
			}

			if (this.drawSeriesLineAsPath) {
				final State s = (State) state;
				// update path to reflect latest point
				if (!Double.isNaN(transX1) && !Double.isNaN(transY1)) {
					float x = (float) transX1;
					float y = (float) transY1;
					if (orientation == PlotOrientation.HORIZONTAL) {
						x = (float) transY1;
						y = (float) transX1;
					}
					if (s.isLastPointGood()) {
						// TODO: check threshold
						s.seriesPath.lineTo(x, y);
					} else {
						s.seriesPath.moveTo(x, y);
					}
					s.setLastPointGood(true);
				} else {
					s.setLastPointGood(false);
				}
				if (item == dataset.getItemCount(series) - 1) {
					// draw path
					g2.setStroke(getSeriesStroke(series));
					g2.setPaint(getSeriesPaint(series));
					g2.draw(s.seriesPath);
				}
			}

			else if (item != 0) {
				// get the previous data point...
				final double x0 = dataset.getXValue(series, item - previousDrawnItem);
				final double y0 = dataset.getYValue(series, item - previousDrawnItem);
				if (!Double.isNaN(x0) && !Double.isNaN(y0)) {
					boolean drawLine = true;
					if (getPlotDiscontinuous()) {
						// only draw a line if the gap between the current and
						// previous data point is within the threshold
						final int numX = dataset.getItemCount(series);
						final double minX = dataset.getXValue(series, 0);
						final double maxX = dataset.getXValue(series, numX - 1);
						if (this.gapThresholdType == UnitType.ABSOLUTE) {
							drawLine = Math.abs(x1 - x0) <= this.gapThreshold;
						} else {
							drawLine = Math.abs(x1 - x0) <= (maxX - minX) / numX * getGapThreshold();
						}
					}
					if (drawLine) {
						final double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
						final double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);

						// only draw if we have good values
						if (Double.isNaN(transX0) || Double.isNaN(transY0) || Double.isNaN(transX1)
								|| Double.isNaN(transY1)) { return; }

						// Only draw line if it is more than a pixel away from the previous one
						if (transX1 - transX0 > 2 || transX1 - transX0 < -2 || transY1 - transY0 > 2
								|| transY1 - transY0 < -2 || 0 == previousDrawnItem) {
							previousDrawnItem = 1;

							if (orientation == PlotOrientation.HORIZONTAL) {
								state.workingLine.setLine(transY0, transX0, transY1, transX1);
							} else if (orientation == PlotOrientation.VERTICAL) {
								state.workingLine.setLine(transX0, transY0, transX1, transY1);
							}

							if (state.workingLine.intersects(dataArea)) {
								g2.draw(state.workingLine);
							}
						} else {
							// Increase counter for the previous drawn item.
							previousDrawnItem++;
							bAddEntity = false;
						}
					}
				}
			}
		}

		if (getBaseShapesVisible()) {

			Shape shape = getItemShape(series, item);
			if (orientation == PlotOrientation.HORIZONTAL) {
				shape = ShapeUtilities.createTranslatedShape(shape, transY1, transX1);
			} else if (orientation == PlotOrientation.VERTICAL) {
				shape = ShapeUtilities.createTranslatedShape(shape, transX1, transY1);
			}
			if (shape.intersects(dataArea)) {
				bAddEntity = true;
				if (getItemShapeFilled(series, item)) {
					g2.fill(shape);
				} else {
					g2.draw(shape);
				}
			}
			entityArea = shape;

		}

		if (getPlotImages()) {
			final Image image = getImage(plot, series, item, transX1, transY1);
			if (image != null) {
				final Point hotspot = getImageHotspot(plot, series, item, transX1, transY1, image);
				g2.drawImage(image, (int) (transX1 - hotspot.getX()), (int) (transY1 - hotspot.getY()), null);
				entityArea = new Rectangle2D.Double(transX1 - hotspot.getX(), transY1 - hotspot.getY(),
						image.getWidth(null), image.getHeight(null));
			}

		}

		// draw the item label if there is one...
		if (isItemLabelVisible(series, item)) {
			double xx = transX1;
			double yy = transY1;
			if (orientation == PlotOrientation.HORIZONTAL) {
				xx = transY1;
				yy = transX1;
			}
			drawItemLabel(g2, orientation, dataset, series, item, xx, yy, y1 < 0.0);
		}

		updateCrosshairValues(crosshairState, x1, y1, transX1, transY1, orientation);

		// add an entity for the item...
		if (entities != null && bAddEntity) {
			addEntity(entities, entityArea, dataset, series, item, transX1, transY1);
		}
	}
}
