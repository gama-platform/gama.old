/*******************************************************************************************************
 *
 * ChartJFreeChartOutput.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
import java.util.List;

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
import msi.gama.outputs.display.AbstractDisplayGraphics;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

/**
 * The Class ChartJFreeChartOutput.
 */
public class ChartJFreeChartOutput extends ChartOutput implements ChartProgressListener {

	/** The lock. */
	Object lock = new Object();

	/** The Constant defaultmarkers. */
	public static final Shape[] defaultmarkers =
			org.jfree.chart.plot.DefaultDrawingSupplier.createStandardSeriesShapes();

	/** The old anti alias. */
	boolean oldAntiAlias;

	/** The info. */
	final public ChartRenderingInfo info;

	/** The jfreedataset. */
	final List<Dataset> jfreedataset = new ArrayList<>();

	/** The chart. */
	JFreeChart chart, snapshot = null;

	/** The area. */
	final Rectangle2D area = new Rectangle2D.Double();

	/** The cache. */
	BufferedImage frontImage, backImage;

	/** The defaultrenderer. */
	AbstractRenderer defaultrenderer;

	/** The Id position. */
	final HashMap<String, Integer> idPosition = new HashMap<>();

	/** The renderer set. */
	final HashMap<String, AbstractRenderer> rendererSet = new HashMap<>();

	/** The nbseries. */
	int nbseries = 0;
	/** The ready. */
	// ChartProgressListener
	private volatile boolean ready = true;

	/**
	 * Instantiates a new chart J free chart output.
	 *
	 * @param scope
	 *            the scope
	 * @param name
	 *            the name
	 * @param typeexp
	 *            the typeexp
	 */
	public ChartJFreeChartOutput(final IScope scope, final String name, final IExpression typeexp) {
		super(scope, name, typeexp);
		info = new ChartRenderingInfo();
	}

	/**
	 * Creates the chart output.
	 *
	 * @param scope
	 *            the scope
	 * @param name
	 *            the name
	 * @param typeexp
	 *            the typeexp
	 * @return the chart J free chart output
	 */
	public static ChartJFreeChartOutput createChartOutput(final IScope scope, final String name,
			final IExpression typeexp) {

		final IExpression string1 = typeexp;
		if (string1 != null) {
			final String t = Cast.asString(scope, string1.value(scope));
			return switch (t) {
				case IKeyword.HISTOGRAM -> new ChartJFreeChartOutputHistogram(scope, name, typeexp);
				case IKeyword.PIE -> new ChartJFreeChartOutputPie(scope, name, typeexp);
				case IKeyword.RADAR -> new ChartJFreeChartOutputRadar(scope, name, typeexp);
				case IKeyword.HEATMAP -> new ChartJFreeChartOutputHeatmap(scope, name, typeexp);
				case IKeyword.BOX_WHISKER -> new ChartJFreeChartOutputBoxAndWhiskerCategory(scope, name, typeexp);
				default -> new ChartJFreeChartOutputScatter(scope, name, typeexp);
			};
		}
		return new ChartJFreeChartOutputScatter(scope, name, typeexp);
	}

	@Override
	public BufferedImage getImage(final int sizeX, final int sizeY, final boolean antiAlias) {
		// if (GAMA.isSynchronized()) { while (!ready) { DEBUG.WAIT(20); } }
		// if (!ready) return frontImage;
		adjustImage(sizeX, sizeY, antiAlias);

		final Graphics2D g2D = backImage.createGraphics();
		try {
			synchronized (lock) {
				snapshot = (JFreeChart) chart.clone();
				snapshot.addProgressListener(this);
				snapshot.draw(g2D, area, info);
			}
		} catch (IndexOutOfBoundsException | IllegalArgumentException | NullPointerException
				| CloneNotSupportedException e) {
			// Do nothing. See #1605
			// e.printStackTrace();
			// To force redrawing in case of error. See #3442
			ready = true;
		} finally {
			g2D.dispose();
		}
		return frontImage;

	}

	/**
	 * Adjust image.
	 *
	 * @param sizeX
	 *            the size X
	 * @param sizeY
	 *            the size Y
	 * @param antiAlias
	 *            the anti alias
	 */
	private void adjustImage(final int sizeX, final int sizeY, final boolean antiAlias) {
		if (antiAlias != oldAntiAlias) {
			oldAntiAlias = antiAlias;
			chart.setAntiAlias(antiAlias);
			chart.setTextAntiAlias(antiAlias);
		}
		if ((int) area.getWidth() != sizeX || (int) area.getHeight() != sizeY) {
			area.setRect(0, 0, sizeX, sizeY);
			frontImage = AbstractDisplayGraphics.createCompatibleImage(sizeX, sizeY);
			backImage = AbstractDisplayGraphics.createCompatibleImage(sizeX, sizeY);
		}
	}

	/**
	 * Chart progress.
	 *
	 * @param event
	 *            the event
	 */
	@Override
	public void chartProgress(final ChartProgressEvent event) {
		ready = event.getType() == ChartProgressEvent.DRAWING_FINISHED;
		if (ready) {
			BufferedImage bi = backImage;
			backImage = frontImage;
			frontImage = bi;
		}
	}

	@Override
	public void step(final IScope scope) {
		synchronized (lock) {
			super.step(scope);
		}
	}

	/**
	 * Inits the renderer.
	 *
	 * @param scope
	 *            the scope
	 */
	protected void initRenderer(final IScope scope) {}

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
		if (!this.getTitleVisible(scope)) { chart.getTitle().setVisible(false); }
		if (textColor != null) { chart.getTitle().setPaint(textColor); }

		if (backgroundColor == null) {
			plot.setBackgroundPaint(null);
			chart.setBackgroundPaint(null);
			chart.setBorderPaint(null);
			if (chart.getLegend() != null) { chart.getLegend().setBackgroundPaint(null); }
		} else {
			final Color bg = backgroundColor;
			chart.setBackgroundPaint(bg);
			plot.setBackgroundPaint(bg);
			chart.setBorderPaint(bg);
			if (chart.getLegend() != null) { chart.getLegend().setBackgroundPaint(bg); }
		}
		if (chart.getLegend() != null) {
			chart.getLegend().setItemFont(getLegendFont());
			chart.getLegend().setFrame(BlockBorder.NONE);
			if (textColor != null) { chart.getLegend().setItemPaint(textColor); }
		}

	}

	/**
	 * Gets the or create renderer.
	 *
	 * @param scope
	 *            the scope
	 * @param serieid
	 *            the serieid
	 * @return the or create renderer
	 */
	AbstractRenderer getOrCreateRenderer(final IScope scope, final String serieid) {
		if (rendererSet.containsKey(serieid)) return rendererSet.get(serieid);
		final AbstractRenderer newrenderer = createRenderer(scope, serieid);
		rendererSet.put(serieid, newrenderer);
		return newrenderer;

	}

	/**
	 * Creates the renderer.
	 *
	 * @param scope
	 *            the scope
	 * @param serieid
	 *            the serieid
	 * @return the abstract renderer
	 */
	protected AbstractRenderer createRenderer(final IScope scope, final String serieid) {
		return new XYErrorRenderer();
	}

	/**
	 * Gets the label font.
	 *
	 * @return the label font
	 */
	Font getLabelFont() { return new Font(labelFontFace, labelFontStyle, labelFontSize); }

	/**
	 * Gets the tick font.
	 *
	 * @return the tick font
	 */
	Font getTickFont() { return new Font(tickFontFace, tickFontStyle, tickFontSize); }

	/**
	 * Gets the legend font.
	 *
	 * @return the legend font
	 */
	Font getLegendFont() { return new Font(legendFontFace, legendFontStyle, legendFontSize); }

	/**
	 * Gets the title font.
	 *
	 * @return the title font
	 */
	Font getTitleFont() { return new Font(titleFontFace, titleFontStyle, titleFontSize); }

	@Override
	public JFreeChart getJFChart() { return chart; }

	@Override
	public void dispose(final IScope scope) {
		if (frontImage != null) { frontImage.flush(); }
		if (backImage != null) { backImage.flush(); }
		backImage = null;
		frontImage = null;
		clearDataSet(scope);
		jfreedataset.clear();
		chart = null;
	}

}
