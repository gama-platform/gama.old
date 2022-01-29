/*******************************************************************************************************
 *
 * ChartJFreeChartOutput.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.data.general.Dataset;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.ImageUtils;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

/**
 * The Class ChartJFreeChartOutput.
 */
public class ChartJFreeChartOutput extends ChartOutput implements ChartProgressListener {

	/**
	 * Creates the compatible image.
	 *
	 * @param sizeX the size X
	 * @param sizeY the size Y
	 * @return the buffered image
	 */
	private BufferedImage createCompatibleImage(final int sizeX, final int sizeY) {
		if ((int) r.getWidth() != sizeX || (int) r.getHeight() != sizeY) {
			r.setRect(0, 0, sizeX, sizeY);
			if (cache != null) {
				cache.flush();
			}
			cache = ImageUtils.createCompatibleImage(sizeX, sizeY, false);
		}
		return cache;
	}

	/** The Constant defaultmarkers. */
	public static final Shape[] defaultmarkers =
			org.jfree.chart.plot.DefaultDrawingSupplier.createStandardSeriesShapes();
	
	/** The old anti alias. */
	boolean oldAntiAlias;

	/** The info. */
	public ChartRenderingInfo info;
	
	/** The jfreedataset. */
	ArrayList<Dataset> jfreedataset = new ArrayList<>();
	
	/** The chart. */
	JFreeChart chart = null;
	
	/** The r. */
	Rectangle2D r = new Rectangle2D.Double();
	
	/** The cache. */
	BufferedImage cache;
	
	/** The defaultrenderer. */
	AbstractRenderer defaultrenderer;
	
	/** The Id position. */
	HashMap<String, Integer> IdPosition = new HashMap<>(); // serie
															// id-nb
															// for
															// arraylists/table
															/** The Renderer set. */
															// requirements
	HashMap<String, AbstractRenderer> RendererSet = new HashMap<>(); // one
																		// renderer
																		// for
																		// each
																		/** The nbseries. */
																		// serie
	int nbseries = 0; // because there is always one dataset, so it is difficult
	// A filed aiming at controlling the rate at which charts can be produced. Directly controlled by the
	/** The ready. */
	// ChartProgressListener
	private volatile boolean ready = true;
	// to count...

	/**
	 * Instantiates a new chart J free chart output.
	 *
	 * @param scope the scope
	 * @param name the name
	 * @param typeexp the typeexp
	 */
	public ChartJFreeChartOutput(final IScope scope, final String name, final IExpression typeexp) {
		super(scope, name, typeexp);
		info = new ChartRenderingInfo();
	}

	/**
	 * Creates the chart output.
	 *
	 * @param scope the scope
	 * @param name the name
	 * @param typeexp the typeexp
	 * @return the chart J free chart output
	 */
	public static ChartJFreeChartOutput createChartOutput(final IScope scope, final String name,
			final IExpression typeexp) {
		ChartJFreeChartOutput newChart;
		int type = SERIES_CHART;

		final IExpression string1 = typeexp;
		if (string1 != null) {
			final String t = Cast.asString(scope, string1.value(scope));
			type = IKeyword.SERIES.equals(t) ? SERIES_CHART
					: IKeyword.HISTOGRAM.equals(t) ? HISTOGRAM_CHART : IKeyword.PIE.equals(t) ? PIE_CHART
							: IKeyword.RADAR.equals(t) ? RADAR_CHART
									: IKeyword.HEATMAP.equals(t) ? HEATMAP_CHART : IKeyword.BOX_WHISKER.equals(t)
											? BOX_WHISKER_CHART : IKeyword.SCATTER.equals(t) ? SCATTER_CHART : XY_CHART;

		}

		switch (type) {

			case PIE_CHART: {
				newChart = new ChartJFreeChartOutputPie(scope, name, typeexp);
				break;
			}
			case HISTOGRAM_CHART:
			case BOX_WHISKER_CHART: {
				newChart = new ChartJFreeChartOutputHistogram(scope, name, typeexp);
				break;
			}
			case RADAR_CHART: {
				newChart = new ChartJFreeChartOutputRadar(scope, name, typeexp);
				break;
			}
			case HEATMAP_CHART: {
				newChart = new ChartJFreeChartOutputHeatmap(scope, name, typeexp);
				break;
			}
			default: {
				newChart = new ChartJFreeChartOutputScatter(scope, name, typeexp);
			}
		}
		return newChart;
	}

	/**
	 * Gets the graphics.
	 *
	 * @param sizeX the size X
	 * @param sizeY the size Y
	 * @return the graphics
	 */
	private Graphics2D getGraphics(final int sizeX, final int sizeY) {
		return createCompatibleImage(sizeX, sizeY).createGraphics();
	}

	@Override
	public BufferedImage getImage(final int sizeX, final int sizeY, final boolean antiAlias) {
		if (!ready) { return cache; }
		if (antiAlias != oldAntiAlias) {
			oldAntiAlias = antiAlias;
			getJFChart().setAntiAlias(antiAlias);
			getJFChart().setTextAntiAlias(antiAlias);
		}
		final Graphics2D g2D = getGraphics(sizeX, sizeY);
		try {
			chart.draw(g2D, r, info);
		} catch (IndexOutOfBoundsException | IllegalArgumentException | NullPointerException e) {
			// Do nothing. See #1605
			// e.printStackTrace();
		} finally {
			g2D.dispose();
		}
		return cache;

	}

	@Override
	public void chartProgress(final ChartProgressEvent event) {
		ready = event.getType() == ChartProgressEvent.DRAWING_FINISHED;
	}

	@Override
	public void draw(final Graphics2D g2D, final Rectangle2D area, final boolean antiAlias) {
		// if (!ready) { return; }
		if (antiAlias != oldAntiAlias) {
			oldAntiAlias = antiAlias;
			chart.setAntiAlias(antiAlias);
			chart.setTextAntiAlias(antiAlias);
		}
		chart.draw(g2D, area, info);
	}

	@Override
	public void step(final IScope scope) {
		chartdataset.updatedataset(scope, getChartCycle(scope));
		if (!ready) { return; }
		updateOutput(scope);
	}

	/**
	 * Inits the renderer.
	 *
	 * @param scope the scope
	 */
	protected void initRenderer(final IScope scope) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initChart(final IScope scope, final String chartname) {
		super.initChart(scope, chartname);

		initRenderer(scope);
		final Plot plot = chart.getPlot();

		chart.setBorderVisible(false);
		plot.setOutlineVisible(false);
		chart.setTitle(this.getName());
		chart.getTitle().setVisible(true);
		chart.getTitle().setFont(getTitleFont());
		if (!this.getTitleVisible(scope)) {
			chart.getTitle().setVisible(false);
		}
		if (textColor != null) {
			chart.getTitle().setPaint(textColor);
		}

		if (backgroundColor == null) {
			plot.setBackgroundPaint(null);
			chart.setBackgroundPaint(null);
			chart.setBorderPaint(null);
			if (chart.getLegend() != null) {
				chart.getLegend().setBackgroundPaint(null);
			}
		} else {
			final Color bg = backgroundColor;
			chart.setBackgroundPaint(bg);
			plot.setBackgroundPaint(bg);
			chart.setBorderPaint(bg);
			if (chart.getLegend() != null) {
				chart.getLegend().setBackgroundPaint(bg);
			}
		}
		if (chart.getLegend() != null) {
			chart.getLegend().setItemFont(getLegendFont());
			chart.getLegend().setFrame(BlockBorder.NONE);
			if (textColor != null) {
				chart.getLegend().setItemPaint(textColor);
			}
		}

		chart.addProgressListener(this);

	}

	/**
	 * Gets the or create renderer.
	 *
	 * @param scope the scope
	 * @param serieid the serieid
	 * @return the or create renderer
	 */
	AbstractRenderer getOrCreateRenderer(final IScope scope, final String serieid) {
		if (RendererSet.containsKey(serieid)) { return RendererSet.get(serieid); }
		final AbstractRenderer newrenderer = createRenderer(scope, serieid);
		RendererSet.put(serieid, newrenderer);
		return newrenderer;

	}

	/**
	 * Creates the renderer.
	 *
	 * @param scope the scope
	 * @param serieid the serieid
	 * @return the abstract renderer
	 */
	protected AbstractRenderer createRenderer(final IScope scope, final String serieid) {
		// TODO Auto-generated method stub
		return new XYErrorRenderer();

	}

	/**
	 * Gets the label font.
	 *
	 * @return the label font
	 */
	Font getLabelFont() {
		return new Font(labelFontFace, labelFontStyle, labelFontSize);
	}

	/**
	 * Gets the tick font.
	 *
	 * @return the tick font
	 */
	Font getTickFont() {
		return new Font(tickFontFace, tickFontStyle, tickFontSize);
	}

	/**
	 * Gets the legend font.
	 *
	 * @return the legend font
	 */
	Font getLegendFont() {
		return new Font(legendFontFace, legendFontStyle, legendFontSize);
	}

	/**
	 * Gets the title font.
	 *
	 * @return the title font
	 */
	Font getTitleFont() {
		return new Font(titleFontFace, titleFontStyle, titleFontSize);
	}

	@Override
	protected void updateImage(final IScope scope) {
		// TODO Auto-generated method stub
	}

	@Override
	public JFreeChart getJFChart() {
		return chart;
	}

	@Override
	public void dispose(final IScope scope) {
		if (cache != null) {
			cache.flush();
		}
		cache = null;
		clearDataSet(scope);
		jfreedataset.clear();
		chart = null;
	}

}
