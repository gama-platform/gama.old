/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.StandardXYItemRenderer.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package msi.gama.outputs.layers.charts;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.UnitType;

/**
 * Standard item renderer for an {@link XYPlot}. This class can draw (a) shapes at each point, or (b) lines between
 * points, or (c) both shapes and lines.
 * <P>
 * This renderer has been retained for historical reasons and, in general, you should use the
 * {@link XYLineAndShapeRenderer} class instead.
 */
public class StandardXYItemRenderer extends AbstractXYItemRenderer
		implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {

	/** For serialization. */
	private static final long serialVersionUID = -3271351259436865995L;

	/** Constant for the type of rendering (shapes only). */
	public static final int SHAPES = 1;

	/** Constant for the type of rendering (lines only). */
	public static final int LINES = 2;

	/** Constant for the type of rendering (shapes and lines). */
	public static final int SHAPES_AND_LINES = SHAPES | LINES;

	/** Constant for the type of rendering (images only). */
	public static final int IMAGES = 4;

	/** Constant for the type of rendering (discontinuous lines). */
	public static final int DISCONTINUOUS = 8;

	/** Constant for the type of rendering (discontinuous lines). */
	public static final int DISCONTINUOUS_LINES = LINES | DISCONTINUOUS;

	/** A flag indicating whether or not shapes are drawn at each XY point. */
	private boolean baseShapesVisible;

	/** A flag indicating whether or not lines are drawn between XY points. */
	private boolean plotLines;

	/** A flag indicating whether or not images are drawn between XY points. */
	private boolean plotImages;

	/** A flag controlling whether or not discontinuous lines are used. */
	private boolean plotDiscontinuous;

	/** Specifies how the gap threshold value is interpreted. */
	protected UnitType gapThresholdType = UnitType.RELATIVE;

	/** Threshold for deciding when to discontinue a line. */
	protected double gapThreshold = 1.0;

	/**
	 * A flag that controls whether or not shapes are filled for ALL series.
	 *
	 * @deprecated As of 1.0.8, this override should not be used.
	 */
	@Deprecated private Boolean shapesFilled;

	/**
	 * A table of flags that control (per series) whether or not shapes are filled.
	 */
	private BooleanList seriesShapesFilled;

	/** The default value returned by the getShapeFilled() method. */
	private boolean baseShapesFilled;

	/**
	 * A flag that controls whether or not each series is drawn as a single path.
	 */
	protected boolean drawSeriesLineAsPath;

	/**
	 * The shape that is used to represent a line in the legend. This should never be set to <code>null</code>.
	 */
	private transient Shape legendLine;

	/**
	 * Constructs a new renderer.
	 */
	public StandardXYItemRenderer() {
		this(LINES, null);
	}

	/**
	 * Constructs a new renderer. To specify the type of renderer, use one of the constants: {@link #SHAPES},
	 * {@link #LINES} or {@link #SHAPES_AND_LINES}.
	 *
	 * @param type
	 *            the type.
	 */
	public StandardXYItemRenderer(final int type) {
		this(type, null);
	}

	/**
	 * Constructs a new renderer. To specify the type of renderer, use one of the constants: {@link #SHAPES},
	 * {@link #LINES} or {@link #SHAPES_AND_LINES}.
	 *
	 * @param type
	 *            the type of renderer.
	 * @param toolTipGenerator
	 *            the item label generator (<code>null</code> permitted).
	 */
	public StandardXYItemRenderer(final int type, final XYToolTipGenerator toolTipGenerator) {
		this(type, toolTipGenerator, null);
	}

	/**
	 * Constructs a new renderer. To specify the type of renderer, use one of the constants: {@link #SHAPES},
	 * {@link #LINES} or {@link #SHAPES_AND_LINES}.
	 *
	 * @param type
	 *            the type of renderer.
	 * @param toolTipGenerator
	 *            the item label generator (<code>null</code> permitted).
	 * @param urlGenerator
	 *            the URL generator.
	 */
	public StandardXYItemRenderer(final int type, final XYToolTipGenerator toolTipGenerator,
			final XYURLGenerator urlGenerator) {

		super();
		setBaseToolTipGenerator(toolTipGenerator);
		setURLGenerator(urlGenerator);
		if ((type & SHAPES) != 0) {
			this.baseShapesVisible = true;
		}
		if ((type & LINES) != 0) {
			this.plotLines = true;
		}
		if ((type & IMAGES) != 0) {
			this.plotImages = true;
		}
		if ((type & DISCONTINUOUS) != 0) {
			this.plotDiscontinuous = true;
		}

		this.shapesFilled = null;
		this.seriesShapesFilled = new BooleanList();
		this.baseShapesFilled = true;
		this.legendLine = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
		this.drawSeriesLineAsPath = false;
	}

	/**
	 * Returns true if shapes are being plotted by the renderer.
	 *
	 * @return <code>true</code> if shapes are being plotted by the renderer.
	 *
	 * @see #setBaseShapesVisible
	 */
	public boolean getBaseShapesVisible() {
		return this.baseShapesVisible;
	}

	/**
	 * Sets the flag that controls whether or not a shape is plotted at each data point.
	 *
	 * @param flag
	 *            the flag.
	 *
	 * @see #getBaseShapesVisible
	 */
	public void setBaseShapesVisible(final boolean flag) {
		if (this.baseShapesVisible != flag) {
			this.baseShapesVisible = flag;
			fireChangeEvent();
		}
	}

	// SHAPES FILLED

	/**
	 * Returns the flag used to control whether or not the shape for an item is filled.
	 * <p>
	 * The default implementation passes control to the <code>getSeriesShapesFilled</code> method. You can override this
	 * method if you require different behaviour.
	 *
	 * @param series
	 *            the series index (zero-based).
	 * @param item
	 *            the item index (zero-based).
	 *
	 * @return A boolean.
	 *
	 * @see #getSeriesShapesFilled(int)
	 */
	public boolean getItemShapeFilled(final int series, final int item) {
		// return the overall setting, if there is one...
		if (this.shapesFilled != null) { return this.shapesFilled.booleanValue(); }

		// otherwise look up the paint table
		final Boolean flag = this.seriesShapesFilled.getBoolean(series);
		if (flag != null) {
			return flag.booleanValue();
		} else {
			return this.baseShapesFilled;
		}
	}

	/**
	 * Returns the override flag that controls whether or not shapes are filled for ALL series.
	 *
	 * @return The flag (possibly <code>null</code>).
	 *
	 * @since 1.0.5
	 *
	 * @deprecated As of 1.0.8, you should avoid using this method and rely on just the per-series (
	 *             {@link #getSeriesShapesFilled(int)}) and base-level ({@link #getBaseShapesFilled()}) settings.
	 */
	@Deprecated
	public Boolean getShapesFilled() {
		return this.shapesFilled;
	}

	/**
	 * Sets the override flag that controls whether or not shapes are filled for ALL series and sends a to all
	 * registered listeners.
	 *
	 * @param filled
	 *            the flag.
	 *
	 * @see #setShapesFilled(Boolean)
	 *
	 * @deprecated As of 1.0.8, you should avoid using this method and rely on just the per-series (
	 *             {@link #setSeriesShapesFilled(int, Boolean)}) and base-level ({@link #setBaseShapesVisible( boolean)}
	 *             ) settings.
	 */
	@Deprecated
	public void setShapesFilled(final boolean filled) {
		// here we use BooleanUtilities to remain compatible with JDKs < 1.4
		setShapesFilled(BooleanUtilities.valueOf(filled));
	}

	/**
	 * Sets the override flag that controls whether or not shapes are filled for ALL series and sends a to all
	 * registered listeners.
	 *
	 * @param filled
	 *            the flag (<code>null</code> permitted).
	 *
	 * @see #setShapesFilled(boolean)
	 *
	 * @deprecated As of 1.0.8, you should avoid using this method and rely on just the per-series (
	 *             {@link #setSeriesShapesFilled(int, Boolean)}) and base-level ({@link #setBaseShapesVisible( boolean)}
	 *             ) settings.
	 */
	@Deprecated
	public void setShapesFilled(final Boolean filled) {
		this.shapesFilled = filled;
		fireChangeEvent();
	}

	/**
	 * Returns the flag used to control whether or not the shapes for a series are filled.
	 *
	 * @param series
	 *            the series index (zero-based).
	 *
	 * @return A boolean.
	 */
	public Boolean getSeriesShapesFilled(final int series) {
		return this.seriesShapesFilled.getBoolean(series);
	}

	/**
	 * Sets the 'shapes filled' flag for a series and sends a to all registered listeners.
	 *
	 * @param series
	 *            the series index (zero-based).
	 * @param flag
	 *            the flag.
	 *
	 * @see #getSeriesShapesFilled(int)
	 */
	public void setSeriesShapesFilled(final int series, final Boolean flag) {
		this.seriesShapesFilled.setBoolean(series, flag);
		fireChangeEvent();
	}

	/**
	 * Returns the base 'shape filled' attribute.
	 *
	 * @return The base flag.
	 *
	 * @see #setBaseShapesFilled(boolean)
	 */
	public boolean getBaseShapesFilled() {
		return this.baseShapesFilled;
	}

	/**
	 * Sets the base 'shapes filled' flag and sends a to all registered listeners.
	 *
	 * @param flag
	 *            the flag.
	 *
	 * @see #getBaseShapesFilled()
	 */
	public void setBaseShapesFilled(final boolean flag) {
		this.baseShapesFilled = flag;
	}

	/**
	 * Returns true if lines are being plotted by the renderer.
	 *
	 * @return <code>true</code> if lines are being plotted by the renderer.
	 *
	 * @see #setPlotLines(boolean)
	 */
	public boolean getPlotLines() {
		return this.plotLines;
	}

	/**
	 * Sets the flag that controls whether or not a line is plotted between each data point and sends a to all
	 * registered listeners.
	 *
	 * @param flag
	 *            the flag.
	 *
	 * @see #getPlotLines()
	 */
	public void setPlotLines(final boolean flag) {
		if (this.plotLines != flag) {
			this.plotLines = flag;
			fireChangeEvent();
		}
	}

	/**
	 * Returns the gap threshold type (relative or absolute).
	 *
	 * @return The type.
	 *
	 * @see #setGapThresholdType(UnitType)
	 */
	public UnitType getGapThresholdType() {
		return this.gapThresholdType;
	}

	/**
	 * Sets the gap threshold type and sends a to all registered listeners.
	 *
	 * @param thresholdType
	 *            the type (<code>null</code> not permitted).
	 *
	 * @see #getGapThresholdType()
	 */
	public void setGapThresholdType(final UnitType thresholdType) {
		this.gapThresholdType = thresholdType;
		fireChangeEvent();
	}

	/**
	 * Returns the gap threshold for discontinuous lines.
	 *
	 * @return The gap threshold.
	 *
	 * @see #setGapThreshold(double)
	 */
	public double getGapThreshold() {
		return this.gapThreshold;
	}

	/**
	 * Sets the gap threshold for discontinuous lines and sends to all registered listeners.
	 *
	 * @param t
	 *            the threshold.
	 *
	 * @see #getGapThreshold()
	 */
	public void setGapThreshold(final double t) {
		this.gapThreshold = t;
		fireChangeEvent();
	}

	/**
	 * Returns true if images are being plotted by the renderer.
	 *
	 * @return <code>true</code> if images are being plotted by the renderer.
	 *
	 * @see #setPlotImages(boolean)
	 */
	public boolean getPlotImages() {
		return this.plotImages;
	}

	/**
	 * Sets the flag that controls whether or not an image is drawn at each data point and sends a to all registered
	 * listeners.
	 *
	 * @param flag
	 *            the flag.
	 *
	 * @see #getPlotImages()
	 */
	public void setPlotImages(final boolean flag) {
		if (this.plotImages != flag) {
			this.plotImages = flag;
			fireChangeEvent();
		}
	}

	/**
	 * Returns a flag that controls whether or not the renderer shows discontinuous lines.
	 *
	 * @return <code>true</code> if lines should be discontinuous.
	 */
	public boolean getPlotDiscontinuous() {
		return this.plotDiscontinuous;
	}

	/**
	 * Sets the flag that controls whether or not the renderer shows discontinuous lines, and sends a to all registered
	 * listeners.
	 *
	 * @param flag
	 *            the new flag value.
	 *
	 * @since 1.0.5
	 */
	public void setPlotDiscontinuous(final boolean flag) {
		if (this.plotDiscontinuous != flag) {
			this.plotDiscontinuous = flag;
			fireChangeEvent();
		}
	}

	/**
	 * Returns a flag that controls whether or not each series is drawn as a single path.
	 *
	 * @return A boolean.
	 *
	 * @see #setDrawSeriesLineAsPath(boolean)
	 */
	public boolean getDrawSeriesLineAsPath() {
		return this.drawSeriesLineAsPath;
	}

	/**
	 * Sets the flag that controls whether or not each series is drawn as a single path.
	 *
	 * @param flag
	 *            the flag.
	 *
	 * @see #getDrawSeriesLineAsPath()
	 */
	public void setDrawSeriesLineAsPath(final boolean flag) {
		this.drawSeriesLineAsPath = flag;
	}

	/**
	 * Returns the shape used to represent a line in the legend.
	 *
	 * @return The legend line (never <code>null</code>).
	 *
	 * @see #setLegendLine(Shape)
	 */
	public Shape getLegendLine() {
		return this.legendLine;
	}

	/**
	 * Sets the shape used as a line in each legend item and sends a to all registered listeners.
	 *
	 * @param line
	 *            the line (<code>null</code> not permitted).
	 *
	 * @see #getLegendLine()
	 */
	public void setLegendLine(final Shape line) {
		this.legendLine = line;
		fireChangeEvent();
	}

	/**
	 * Returns a legend item for a series.
	 *
	 * @param datasetIndex
	 *            the dataset index (zero-based).
	 * @param series
	 *            the series index (zero-based).
	 *
	 * @return A legend item for the series.
	 */
	@Override
	public LegendItem getLegendItem(final int datasetIndex, final int series) {
		final XYPlot plot = getPlot();
		if (plot == null) { return null; }
		LegendItem result = null;
		final XYDataset dataset = plot.getDataset(datasetIndex);
		if (dataset != null) {
			if (getItemVisible(series, 0)) {
				final String label = getLegendItemLabelGenerator().generateLabel(dataset, series);
				final String description = label;
				String toolTipText = null;
				if (getLegendItemToolTipGenerator() != null) {
					toolTipText = getLegendItemToolTipGenerator().generateLabel(dataset, series);
				}
				String urlText = null;
				if (getLegendItemURLGenerator() != null) {
					urlText = getLegendItemURLGenerator().generateLabel(dataset, series);
				}
				final Shape shape = lookupLegendShape(series);
				final boolean shapeFilled = getItemShapeFilled(series, 0);
				final Paint paint = lookupSeriesPaint(series);
				final Paint linePaint = paint;
				final Stroke lineStroke = lookupSeriesStroke(series);
				result = new LegendItem(label, description, toolTipText, urlText, this.baseShapesVisible, shape,
						shapeFilled, paint, !shapeFilled, paint, lineStroke, this.plotLines, this.legendLine,
						lineStroke, linePaint);
				result.setLabelFont(lookupLegendTextFont(series));
				final Paint labelPaint = lookupLegendTextPaint(series);
				if (labelPaint != null) {
					result.setLabelPaint(labelPaint);
				}
				result.setDataset(dataset);
				result.setDatasetIndex(datasetIndex);
				result.setSeriesKey(dataset.getSeriesKey(series));
				result.setSeriesIndex(series);
			}
		}
		return result;
	}

	/**
	 * Records the state for the renderer. This is used to preserve state information between calls to the drawItem()
	 * method for a single chart drawing.
	 */
	public static class State extends XYItemRendererState {

		/** The path for the current series. */
		public GeneralPath seriesPath;

		/** The series index. */
		int seriesIndex;

		/**
		 * A flag that indicates if the last (x, y) point was 'good' (non-null).
		 */
		boolean lastPointGood;

		/**
		 * Creates a new state instance.
		 *
		 * @param info
		 *            the plot rendering info.
		 */
		public State(final PlotRenderingInfo info) {
			super(info);
		}

		/**
		 * Returns a flag that indicates if the last point drawn (in the current series) was 'good' (non-null).
		 *
		 * @return A boolean.
		 */
		public boolean isLastPointGood() {
			return this.lastPointGood;
		}

		/**
		 * Sets a flag that indicates if the last point drawn (in the current series) was 'good' (non-null).
		 *
		 * @param good
		 *            the flag.
		 */
		public void setLastPointGood(final boolean good) {
			this.lastPointGood = good;
		}

		/**
		 * Returns the series index for the current path.
		 *
		 * @return The series index for the current path.
		 */
		public int getSeriesIndex() {
			return this.seriesIndex;
		}

		/**
		 * Sets the series index for the current path.
		 *
		 * @param index
		 *            the index.
		 */
		public void setSeriesIndex(final int index) {
			this.seriesIndex = index;
		}
	}

	/**
	 * Initialises the renderer.
	 * <P>
	 * This method will be called before the first item is rendered, giving the renderer an opportunity to initialise
	 * any state information it wants to maintain. The renderer can do nothing if it chooses.
	 *
	 * @param g2
	 *            the graphics device.
	 * @param dataArea
	 *            the area inside the axes.
	 * @param plot
	 *            the plot.
	 * @param data
	 *            the data.
	 * @param info
	 *            an optional info collection object to return data back to the caller.
	 *
	 * @return The renderer state.
	 */
	@Override
	public XYItemRendererState initialise(final Graphics2D g2, final Rectangle2D dataArea, final XYPlot plot,
			final XYDataset data, final PlotRenderingInfo info) {

		final State state = new State(info);
		state.seriesPath = new GeneralPath();
		state.seriesIndex = -1;
		return state;

	}

	/**
	 * Draws the visual representation of a single data item.
	 *
	 * @param g2
	 *            the graphics device.
	 * @param state
	 *            the renderer state.
	 * @param dataArea
	 *            the area within which the data is being drawn.
	 * @param info
	 *            collects information about the drawing.
	 * @param plot
	 *            the plot (can be used to obtain standard color information etc).
	 * @param domainAxis
	 *            the domain axis.
	 * @param rangeAxis
	 *            the range axis.
	 * @param dataset
	 *            the dataset.
	 * @param series
	 *            the series index (zero-based).
	 * @param item
	 *            the item index (zero-based).
	 * @param crosshairState
	 *            crosshair information for the plot (<code>null</code> permitted).
	 * @param pass
	 *            the pass index.
	 */
	@Override
	public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea,
			final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis,
			final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState,
			final int pass) {

		boolean itemVisible = getItemVisible(series, item);

		// setup for collecting optional entity info...
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
		if (Double.isNaN(x1) || Double.isNaN(y1)) {
			itemVisible = false;
		}

		final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
		final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
		final double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
		final double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

		if (getPlotLines()) {
			if (this.drawSeriesLineAsPath) {
				final State s = (State) state;
				if (s.getSeriesIndex() != series) {
					// we are starting a new series path
					s.seriesPath.reset();
					s.lastPointGood = false;
					s.setSeriesIndex(series);
				}

				// update path to reflect latest point
				if (itemVisible && !Double.isNaN(transX1) && !Double.isNaN(transY1)) {
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
					if (s.seriesIndex == series) {
						// draw path
						g2.setStroke(lookupSeriesStroke(series));
						g2.setPaint(lookupSeriesPaint(series));
						g2.draw(s.seriesPath);
					}
				}
			}

			else if (item != 0 && itemVisible) {
				// get the previous data point...
				final double x0 = dataset.getXValue(series, item - 1);
				final double y0 = dataset.getYValue(series, item - 1);
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

						if (orientation == PlotOrientation.HORIZONTAL) {
							state.workingLine.setLine(transY0, transX0, transY1, transX1);
						} else if (orientation == PlotOrientation.VERTICAL) {
							state.workingLine.setLine(transX0, transY0, transX1, transY1);
						}

						if (state.workingLine.intersects(dataArea)) {
							g2.draw(state.workingLine);
						}
					}
				}
			}
		}

		// we needed to get this far even for invisible items, to ensure that
		// seriesPath updates happened, but now there is nothing more we need
		// to do for non-visible items...
		if (!itemVisible) { return; }

		if (getBaseShapesVisible()) {

			Shape shape = getItemShape(series, item);
			if (orientation == PlotOrientation.HORIZONTAL) {
				shape = ShapeUtilities.createTranslatedShape(shape, transY1, transX1);
			} else if (orientation == PlotOrientation.VERTICAL) {
				shape = ShapeUtilities.createTranslatedShape(shape, transX1, transY1);
			}
			if (shape.intersects(dataArea)) {
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

		double xx = transX1;
		double yy = transY1;
		if (orientation == PlotOrientation.HORIZONTAL) {
			xx = transY1;
			yy = transX1;
		}

		// draw the item label if there is one...
		if (isItemLabelVisible(series, item)) {
			drawItemLabel(g2, orientation, dataset, series, item, xx, yy, y1 < 0.0);
		}

		final int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
		final int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
		updateCrosshairValues(crosshairState, x1, y1, domainAxisIndex, rangeAxisIndex, transX1, transY1, orientation);

		// add an entity for the item...
		if (entities != null && isPointInRect(dataArea, xx, yy)) {
			addEntity(entities, entityArea, dataset, series, item, xx, yy);
		}

	}

	/**
	 * Tests this renderer for equality with another object.
	 *
	 * @param obj
	 *            the object (<code>null</code> permitted).
	 *
	 * @return A boolean.
	 */
	@Override
	public boolean equals(final Object obj) {

		if (obj == this) { return true; }
		if (!(obj instanceof StandardXYItemRenderer)) { return false; }
		final StandardXYItemRenderer that = (StandardXYItemRenderer) obj;
		if (this.baseShapesVisible != that.baseShapesVisible) { return false; }
		if (this.plotLines != that.plotLines) { return false; }
		if (this.plotImages != that.plotImages) { return false; }
		if (this.plotDiscontinuous != that.plotDiscontinuous) { return false; }
		if (this.gapThresholdType != that.gapThresholdType) { return false; }
		if (this.gapThreshold != that.gapThreshold) { return false; }
		if (!ObjectUtilities.equal(this.shapesFilled, that.shapesFilled)) { return false; }
		if (!this.seriesShapesFilled.equals(that.seriesShapesFilled)) { return false; }
		if (this.baseShapesFilled != that.baseShapesFilled) { return false; }
		if (this.drawSeriesLineAsPath != that.drawSeriesLineAsPath) { return false; }
		if (!ShapeUtilities.equal(this.legendLine, that.legendLine)) { return false; }
		return super.equals(obj);

	}

	/**
	 * Returns a clone of the renderer.
	 *
	 * @return A clone.
	 *
	 * @throws CloneNotSupportedException
	 *             if the renderer cannot be cloned.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		final StandardXYItemRenderer clone = (StandardXYItemRenderer) super.clone();
		clone.seriesShapesFilled = (BooleanList) this.seriesShapesFilled.clone();
		clone.legendLine = ShapeUtilities.clone(this.legendLine);
		return clone;
	}

	////////////////////////////////////////////////////////////////////////////
	// PROTECTED METHODS
	// These provide the opportunity to subclass the standard renderer and
	// create custom effects.
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the image used to draw a single data item.
	 *
	 * @param plot
	 *            the plot (can be used to obtain standard color information etc).
	 * @param series
	 *            the series index.
	 * @param item
	 *            the item index.
	 * @param x
	 *            the x value of the item.
	 * @param y
	 *            the y value of the item.
	 *
	 * @return The image.
	 *
	 * @see #getPlotImages()
	 */
	protected Image getImage(final Plot plot, final int series, final int item, final double x, final double y) {
		// this method must be overridden if you want to display images
		return null;
	}

	/**
	 * Returns the hotspot of the image used to draw a single data item. The hotspot is the point relative to the top
	 * left of the image that should indicate the data item. The default is the center of the image.
	 *
	 * @param plot
	 *            the plot (can be used to obtain standard color information etc).
	 * @param image
	 *            the image (can be used to get size information about the image)
	 * @param series
	 *            the series index
	 * @param item
	 *            the item index
	 * @param x
	 *            the x value of the item
	 * @param y
	 *            the y value of the item
	 *
	 * @return The hotspot used to draw the data item.
	 */
	protected Point getImageHotspot(final Plot plot, final int series, final int item, final double x, final double y,
			final Image image) {

		final int height = image.getHeight(null);
		final int width = image.getWidth(null);
		return new Point(width / 2, height / 2);

	}

	/**
	 * Provides serialization support.
	 *
	 * @param stream
	 *            the input stream.
	 *
	 * @throws IOException
	 *             if there is an I/O error.
	 * @throws ClassNotFoundException
	 *             if there is a classpath problem.
	 */
	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		this.legendLine = SerialUtilities.readShape(stream);
	}

	/**
	 * Provides serialization support.
	 *
	 * @param stream
	 *            the output stream.
	 *
	 * @throws IOException
	 *             if there is an I/O error.
	 */
	private void writeObject(final ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
		SerialUtilities.writeShape(this.legendLine, stream);
	}

}
