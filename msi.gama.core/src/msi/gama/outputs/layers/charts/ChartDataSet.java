/*******************************************************************************************************
 *
 * ChartDataSet.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.FileUtils;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Files;
import msi.gaml.operators.Strings;

/**
 * The Class ChartDataSet.
 */
public class ChartDataSet {

	/** The chart folder. */
	private static String chartFolder = "charts";

	/** The sources. */
	final ArrayList<ChartDataSource> sources = new ArrayList<>();

	/** The series. */
	final LinkedHashMap<String, ChartDataSeries> series = new LinkedHashMap<>();

	/** The deletedseries. */
	final LinkedHashMap<String, ChartDataSeries> deletedseries = new LinkedHashMap<>();

	/** The Xcategories. */
	final ArrayList<String> Xcategories = new ArrayList<>(); // for categories

	/** The X series values. */
	// datasets
	final ArrayList<Double> XSeriesValues = new ArrayList<>(); // for series

	/** The Ycategories. */
	final ArrayList<String> Ycategories = new ArrayList<>(); // for Y categories

	/** The Y series values. */
	// datasets
	final ArrayList<Double> YSeriesValues = new ArrayList<>(); // for 3d series

	/** The serie creation date. */
	final LinkedHashMap<String, Integer> serieCreationDate = new LinkedHashMap<>();

	/** The common xindex. */
	int commonXindex = -1; // current index on X value (usually last of list,

	/** The common yindex. */
	// can be less when going back in time...)
	int commonYindex = -1; // current index on X value (usually last of list,
							// can be less when going back in time...)

	/** The xsource. */
	IExpression xsource; // to replace default common X Source

	/** The ysource. */
	IExpression ysource; // to replace default common X Labels

	/** The xlabels. */
	IExpression xlabels; // to replace default common Y Source

	/** The ylabels. */
	IExpression ylabels; // to replace default common Y Labels

	/** The serie removal date. */
	final LinkedHashMap<String, Integer> serieRemovalDate = new LinkedHashMap<>();

	/** The serie to update before. */
	final LinkedHashMap<String, Integer> serieToUpdateBefore = new LinkedHashMap<>();

	/** The mainoutput. */
	ChartOutput mainoutput;

	/** The reset all before. */
	int resetAllBefore = 0;

	/** The force reset all. */
	boolean forceResetAll = false;

	/** The defaultstyle. */
	String defaultstyle = IKeyword.DEFAULT;

	/** The lastchartcycle. */
	int lastchartcycle = -1;

	/** The force no X accumulate. */
	boolean forceNoXAccumulate = false;

	/** The force no Y accumulate. */
	boolean forceNoYAccumulate = false;

	/** The use X source. */
	boolean useXSource = false;

	/** The use X labels. */
	boolean useXLabels = false;

	/** The use Y source. */
	boolean useYSource = false;

	/** The use Y labels. */
	boolean useYLabels = false;

	/** The common X series. */
	boolean commonXSeries = false; // series

	/** The common Y series. */
	boolean commonYSeries = false; // heatmap & 3d

	/** The by category. */
	boolean byCategory = false; // histogram/pie

	/** The keep history. */
	final boolean keepHistory;

	/** The history. */
	final ChartHistory history;

	/**
	 * Gets the common X index.
	 *
	 * @return the common X index
	 */
	public int getCommonXIndex() { return commonXindex; }

	/**
	 * Gets the common Y index.
	 *
	 * @return the common Y index
	 */
	public int getCommonYIndex() { return commonYindex; }

	/**
	 * Gets the reset all before.
	 *
	 * @return the reset all before
	 */
	public int getResetAllBefore() { return resetAllBefore; }

	/**
	 * Sets the reset all before.
	 *
	 * @param resetAllBefore
	 *            the new reset all before
	 */
	public void setResetAllBefore(final int resetAllBefore) {
		this.resetAllBefore = resetAllBefore;
		forceResetAll = true;
	}

	/**
	 * Gets the categories.
	 *
	 * @param scope
	 *            the scope
	 * @param i
	 *            the i
	 * @return the categories
	 */
	public String getCategories(final IScope scope, final int i) {
		if (Xcategories.size() > i) return Xcategories.get(i);
		for (int c = Xcategories.size(); c <= i; c++) { this.Xcategories.add("c" + c); }
		return Xcategories.get(i);
	}

	/**
	 * Gets the last categories.
	 *
	 * @param scope
	 *            the scope
	 * @return the last categories
	 */
	public String getLastCategories(final IScope scope) {
		if (Xcategories.size() > 0) return Xcategories.get(Xcategories.size() - 1);
		this.Xcategories.add("c" + 0);
		return Xcategories.get(Xcategories.size() - 1);
	}

	/**
	 * Sets the categories.
	 *
	 * @param categories
	 *            the new categories
	 */
	public void setCategories(final ArrayList<String> categories) {
		this.Xcategories.clear();
		Xcategories.addAll(categories);
	}

	/**
	 * Gets the x series values.
	 *
	 * @return the x series values
	 */
	public ArrayList<Double> getXSeriesValues() { return XSeriesValues; }

	/**
	 * Gets the y series values.
	 *
	 * @return the y series values
	 */
	public ArrayList<Double> getYSeriesValues() { return YSeriesValues; }

	/**
	 * Sets the x series values.
	 *
	 * @param xSeriesValues
	 *            the new x series values
	 */
	public void setXSeriesValues(final ArrayList<Double> xSeriesValues) {
		XSeriesValues.clear();
		XSeriesValues.addAll(xSeriesValues);
	}

	/**
	 * Checks if is by category.
	 *
	 * @return true, if is by category
	 */
	public boolean isByCategory() { return byCategory; }

	/**
	 * Sets the by category.
	 *
	 * @param byCategory
	 *            the new by category
	 */
	public void setByCategory(final boolean byCategory) { this.byCategory = byCategory; }

	/**
	 * Checks if is common X series.
	 *
	 * @return true, if is common X series
	 */
	public boolean isCommonXSeries() { return commonXSeries; }

	/**
	 * Sets the common X series.
	 *
	 * @param temporalSeries
	 *            the new common X series
	 */
	public void setCommonXSeries(final boolean temporalSeries) { this.commonXSeries = temporalSeries; }

	/**
	 * Checks if is common Y series.
	 *
	 * @return true, if is common Y series
	 */
	public boolean isCommonYSeries() { return commonYSeries; }

	/**
	 * Sets the common Y series.
	 *
	 * @param temporalSeries
	 *            the new common Y series
	 */
	public void setCommonYSeries(final boolean temporalSeries) { this.commonYSeries = temporalSeries; }

	/**
	 * Gets the serie creation date.
	 *
	 * @return the serie creation date
	 */
	public LinkedHashMap<String, Integer> getSerieCreationDate() { return serieCreationDate; }

	/**
	 * Gets the serie removal date.
	 *
	 * @return the serie removal date
	 */
	public LinkedHashMap<String, Integer> getSerieRemovalDate() { return serieRemovalDate; }

	/** The is batch and permanent. */
	final boolean isBatchAndPermanent;

	/**
	 * Instantiates a new chart data set.
	 *
	 * @param keepHistory
	 *            the keep history
	 * @param isBatchAndPermanent
	 *            the is batch and permanent
	 */
	public ChartDataSet(final boolean keepHistory, final boolean isBatchAndPermanent) {
		this.keepHistory = keepHistory;
		this.isBatchAndPermanent = isBatchAndPermanent;
		history = keepHistory ? new ChartHistory() : null;
	}

	/**
	 * Keeps history.
	 *
	 * @return true, if successful
	 */
	public boolean keepsHistory() {
		return keepHistory;
	}

	/**
	 * Gets the history.
	 *
	 * @return the history
	 */
	public ChartHistory getHistory() { return history; }

	/**
	 * Gets the output.
	 *
	 * @return the output
	 */
	public ChartOutput getOutput() { return mainoutput; }

	/**
	 * Sets the output.
	 *
	 * @param output
	 *            the new output
	 */
	public void setOutput(final ChartOutput output) {
		mainoutput = output;
		this.defaultstyle = output.getStyle();
	}

	/**
	 * Adds the new serie.
	 *
	 * @param id
	 *            the id
	 * @param serie
	 *            the serie
	 * @param date
	 *            the date
	 */
	public void addNewSerie(final String id, final ChartDataSeries serie, final int date) {
		if (series.containsKey(id)) {
			// Series name already present, should do something.... Don't change
			// creation date?
			series.put(id, serie);
		} else {
			series.put(id, serie);
			serieCreationDate.put(id, date);

		}
		// serieCreationDate.put(id, date);
		serieToUpdateBefore.put(id, date);
		serieRemovalDate.put(id, -1);

	}

	/**
	 * Gets the sources.
	 *
	 * @return the sources
	 */
	public ArrayList<ChartDataSource> getSources() { return sources; }

	/**
	 * Adds the data source.
	 *
	 * @param source
	 *            the source
	 */
	public void addDataSource(final ChartDataSource source) {
		sources.add(source);
		final LinkedHashMap<String, ChartDataSeries> newseries = source.getSeries();
		for (final Entry<String, ChartDataSeries> entry : newseries.entrySet()) {
			// should do something... raise an exception?
			addNewSerie(entry.getKey(), entry.getValue(), -1);
		}
		// series.putAll(source.getSeries());
	}

	/**
	 * Do reset all.
	 *
	 * @param scope
	 *            the scope
	 * @param lastUpdateCycle
	 *            the last update cycle
	 * @return true, if successful
	 */
	public boolean doResetAll(final IScope scope, final int lastUpdateCycle) {

		if (resetAllBefore > lastUpdateCycle || forceResetAll) {
			forceResetAll = false;
			return true;

		}
		return false;
	}

	/**
	 * Gets the data series ids.
	 *
	 * @param scope
	 *            the scope
	 * @return the data series ids
	 */
	public Set<String> getDataSeriesIds(final IScope scope) {

		return series.keySet();
	}

	/**
	 * Gets the data series.
	 *
	 * @param scope
	 *            the scope
	 * @param serieid
	 *            the serieid
	 * @return the data series
	 */
	public ChartDataSeries getDataSeries(final IScope scope, final String serieid) {

		return series.get(serieid);
	}

	/**
	 * Did reload.
	 *
	 * @param scope
	 *            the scope
	 * @param chartCycle
	 *            the chart cycle
	 * @return true, if successful
	 */
	public boolean didReload(final IScope scope, final int chartCycle) {

		boolean didr = false;
		final int mychartcycle = chartCycle;
		// int mychartcycle=scope.getSimulationScope().getCycle(scope)+1;
		// DEBUG.LOG("cycle "+mychartcycle+" last: "+lastchartcycle);
		if (lastchartcycle >= mychartcycle) {
			lastchartcycle = mychartcycle - 1;
			didr = true;
		} else {
			lastchartcycle = mychartcycle;

		}
		return didr;

	}

	/**
	 * Backward sim.
	 *
	 * @param scope
	 *            the scope
	 * @param chartCycle
	 *            the chart cycle
	 */
	public void BackwardSim(final IScope scope, final int chartCycle) {
		this.setResetAllBefore(chartCycle);
		final ArrayList<ChartDataSource> sourcestoremove = new ArrayList<>();
		final ArrayList<ChartDataSource> sourcestoadd = new ArrayList<>();
		for (final ChartDataSource source : sources) {
			if (source.isCumulative || source.isCumulativeY) {

				final ChartDataSource newsource = source.getClone(scope, chartCycle);
				newsource.createInitialSeries(scope);
				sourcestoremove.add(source);
				sourcestoadd.add(newsource);
			}
		}
		for (final ChartDataSource source : sourcestoremove) {
			final LinkedHashMap<String, ChartDataSeries> sourceseries = source.getSeries();

			for (final String sn : sourceseries.keySet()) {
				final ChartDataSeries ser = sourceseries.get(sn);
				if (ser.xvalues.size() < 2) { this.removeserie(scope, sn); }
			}
			sources.remove(source);
		}
		for (final ChartDataSource source : sourcestoadd) { this.addDataSource(source); }
		if (this.getXSeriesValues().size() > 0) {
			final ArrayList<Double> ser = this.getXSeriesValues();
			for (int i = 0; i < this.getXSeriesValues().size(); i++) {
				if (ser.get(i) == chartCycle - 1) { this.commonXindex = i; }
			}

		}
		if (this.getYSeriesValues().size() > 0) {
			final ArrayList<Double> sery = this.getYSeriesValues();
			for (int i = 0; i < this.getYSeriesValues().size(); i++) {
				if (sery.get(i) == chartCycle - 1) { this.commonYindex = i; }
			}

		}

	}

	/**
	 * Updatedataset.
	 *
	 * @param scope
	 *            the scope
	 * @param chartCycle
	 *            the chart cycle
	 */
	public void updatedataset(final IScope scope, final int chartCycle) {

		commonXindex++;
		commonYindex++;
		if (scope.getExperiment().canStepBack() && didReload(scope, chartCycle)) { BackwardSim(scope, chartCycle); }
		updateXValues(scope, chartCycle);
		updateYValues(scope, chartCycle);

		if (commonXindex >= this.getXSeriesValues().size()) { commonXindex = this.getXSeriesValues().size() - 1; }
		if (commonYindex >= this.getYSeriesValues().size()) { commonYindex = this.getYSeriesValues().size() - 1; }

		for (final ChartDataSource source : sources) {
			source.updatevalues(scope, chartCycle);
			if (keepHistory) { source.savehistory(scope, history); }
		}
		if (keepHistory) { history.append(Strings.LN); }
	}

	/**
	 * Update Y values.
	 *
	 * @param scope
	 *            the scope
	 * @param chartCycle
	 *            the chart cycle
	 * @param nb
	 *            the nb
	 */
	public void updateYValues(final IScope scope, final int chartCycle, final int nb) {
		int targetNb = nb;
		if (this.useYLabels) {

			Object xlab = ylabels.resolveAgainst(scope).value(scope);
			if (targetNb == -1 && !this.forceNoYAccumulate) { targetNb = YSeriesValues.size() + 1; }
			while (YSeriesValues.size() < targetNb) {
				YSeriesValues.add(getYCycleOrPlusOneForBatch(scope, chartCycle));
				Ycategories.add(Cast.asString(scope, xlab));
			}
		} else {
			if (targetNb == -1 && !this.forceNoYAccumulate && commonYindex >= YSeriesValues.size()) {
				targetNb = YSeriesValues.size() + 1;
			}
			while (YSeriesValues.size() < targetNb) {
				double nvalue = getYCycleOrPlusOneForBatch(scope, chartCycle);
				if (YSeriesValues.size() > 0 && YSeriesValues.get(YSeriesValues.size() - 1) >= nvalue) {
					nvalue = YSeriesValues.get(YSeriesValues.size() - 1) + 1;
				}
				addCommonYValue(scope, nvalue);
			}

		}

	}

	/**
	 * Update Y values.
	 *
	 * @param scope
	 *            the scope
	 * @param chartCycle
	 *            the chart cycle
	 */
	public void updateYValues(final IScope scope, final int chartCycle) {
		updateYValues(scope, chartCycle, -1);

	}

	/**
	 * Gets the y cycle or plus one for batch.
	 *
	 * @param scope
	 *            the scope
	 * @param chartcycle
	 *            the chartcycle
	 * @return the y cycle or plus one for batch
	 */
	public Double getYCycleOrPlusOneForBatch(final IScope scope, final int chartcycle) {
		if (isBatchAndPermanent && YSeriesValues.isEmpty()) return 1d;
		// if (this.YSeriesValues.contains((double) chartcycle))
		// return (int) YSeriesValues.get(YSeriesValues.size() - 1).doubleValue() + 1;
		double value = chartcycle;
		if (YSeriesValues.size() > 0 && YSeriesValues.get(YSeriesValues.size() - 1) >= value) {
			value = YSeriesValues.get(YSeriesValues.size() - 1) + 1;
		}
		return value;
	}

	/**
	 * Adds the common Y value.
	 *
	 * @param scope
	 *            the scope
	 * @param chartCycle
	 *            the chart cycle
	 */
	private void addCommonYValue(final IScope scope, final Double chartCycle) {

		YSeriesValues.add(chartCycle);
		Ycategories.add("" + chartCycle);

	}

	/**
	 * Gets the current common Y value.
	 *
	 * @return the current common Y value
	 */
	public double getCurrentCommonYValue() {

		return this.YSeriesValues.get(this.commonYindex);
	}

	/**
	 * Gets the current common X value.
	 *
	 * @return the current common X value
	 */
	public double getCurrentCommonXValue() {

		return this.XSeriesValues.get(this.commonXindex);
	}

	/**
	 * Update X values.
	 *
	 * @param scope
	 *            the scope
	 * @param chartCycle
	 *            the chart cycle
	 * @param nb
	 *            the nb
	 */
	public void updateXValues(final IScope scope, final int chartCycle, final int nb) {
		int targetNb = nb;
		Object xval, xlab;
		if (this.useXSource || this.useXLabels) {

			if (this.useXSource) {
				xval = xsource.resolveAgainst(scope).value(scope);
			} else {
				xval = xlabels.resolveAgainst(scope).value(scope);
			}
			if (this.useXLabels) {
				xlab = xlabels.resolveAgainst(scope).value(scope);
			} else {
				xlab = xsource.resolveAgainst(scope).value(scope);
			}

			if (xval instanceof IList) {
				final IList<?> xv2 = Cast.asList(scope, xval);
				final IList<?> xl2 = Cast.asList(scope, xlab);

				if (this.useXSource && xv2.size() > 0 && xv2.get(0) instanceof Number) {
					XSeriesValues.clear();
					Xcategories.clear();
					for (int i = 0; i < xv2.size(); i++) {
						XSeriesValues.add(Cast.asFloat(scope, xv2.get(i)));
						Xcategories.add(Cast.asString(scope, xl2.get(i)));

					}

				} else if (xv2.size() > Xcategories.size()) {
					Xcategories.clear();
					for (int i = 0; i < xv2.size(); i++) {
						if (i >= XSeriesValues.size()) {
							XSeriesValues.add(getXCycleOrPlusOneForBatch(scope, chartCycle));
						}
						Xcategories.add(Cast.asString(scope, xl2.get(i)));
					}

				}
				if (xv2.size() < targetNb) throw GamaRuntimeException.error("The x-serie length (" + xv2.size()
						+ ") should NOT be shorter than any series length (" + targetNb + ") !", scope);

			} else {
				if (this.useXSource && xval instanceof Number) {
					final double dvalue = Cast.asFloat(scope, xval);
					final String lvalue = Cast.asString(scope, xlab);
					XSeriesValues.add(dvalue);
					Xcategories.add(lvalue);
				}
				if (targetNb == -1 && !this.forceNoXAccumulate) { targetNb = XSeriesValues.size() + 1; }
				while (XSeriesValues.size() < targetNb) {
					XSeriesValues.add(getXCycleOrPlusOneForBatch(scope, chartCycle));
					Xcategories.add(Cast.asString(scope, xlab));
				}
			}

		}

		if (!this.useXSource && !this.useXLabels) {
			if (targetNb == -1 && !this.forceNoXAccumulate && commonXindex >= XSeriesValues.size()) {
				targetNb = XSeriesValues.size() + 1;
			}
			while (XSeriesValues.size() < targetNb) {
				double nvalue = getXCycleOrPlusOneForBatch(scope, chartCycle);
				if (XSeriesValues.size() > 0 && XSeriesValues.get(XSeriesValues.size() - 1) >= nvalue) {
					nvalue = XSeriesValues.get(XSeriesValues.size() - 1) + 1;
				}
				addCommonXValue(scope, nvalue);
			}

		}

	}

	/**
	 * Update X values.
	 *
	 * @param scope
	 *            the scope
	 * @param chartCycle
	 *            the chart cycle
	 */
	public void updateXValues(final IScope scope, final int chartCycle) {
		updateXValues(scope, chartCycle, -1);

	}

	/**
	 * Gets the x cycle or plus one for batch.
	 *
	 * @param scope
	 *            the scope
	 * @param chartcycle
	 *            the chartcycle
	 * @return the x cycle or plus one for batch
	 */
	public Double getXCycleOrPlusOneForBatch(final IScope scope, final int chartcycle) {
		if (isBatchAndPermanent && XSeriesValues.isEmpty()) return 1d;
		// if (this.XSeriesValues.contains(Double.valueOf(chartcycle)))
		// return (int) XSeriesValues.get(XSeriesValues.size() - 1).doubleValue() + 1;
		double value = chartcycle;
		if (XSeriesValues.size() > 0 && XSeriesValues.get(XSeriesValues.size() - 1) >= value) {
			value = XSeriesValues.get(XSeriesValues.size() - 1) + 1;
		}
		return value;
	}

	/**
	 * Adds the common X value.
	 *
	 * @param scope
	 *            the scope
	 * @param chartCycle
	 *            the chart cycle
	 */
	private void addCommonXValue(final IScope scope, final Double chartCycle) {

		XSeriesValues.add(chartCycle);
		Xcategories.add("" + chartCycle);

	}

	/**
	 * Gets the date.
	 *
	 * @param scope
	 *            the scope
	 * @return the date
	 */
	public int getDate(final IScope scope) {
		return scope.getClock().getCycle();
	}

	/**
	 * Sets the X source.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 */
	public void setXSource(final IScope scope, final IExpression data) {

		this.useXSource = true;
		this.xsource = data;
	}

	/**
	 * Sets the X labels.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 */
	public void setXLabels(final IScope scope, final IExpression data) {

		this.useXLabels = true;
		this.xlabels = data;
	}

	/**
	 * Sets the Y labels.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 */
	public void setYLabels(final IScope scope, final IExpression data) {

		this.useYLabels = true;
		this.ylabels = data;
	}

	/**
	 * Creates the or get serie.
	 *
	 * @param scope
	 *            the scope
	 * @param id
	 *            the id
	 * @param source
	 *            the source
	 * @return the chart data series
	 */
	public ChartDataSeries createOrGetSerie(final IScope scope, final String id, final ChartDataSourceList source) {

		if (series.containsKey(id)) return series.get(id);
		if (deletedseries.containsKey(id)) {
			final ChartDataSeries myserie = deletedseries.get(id);
			deletedseries.remove(id);
			this.serieRemovalDate.put(id, -1);
			myserie.setMysource(source);
			myserie.setDataset(this);
			myserie.setName(id);
			addNewSerie(id, myserie, getDate(scope));
			return myserie;
		}
		final ChartDataSeries myserie = new ChartDataSeries();
		myserie.setMysource(source);
		myserie.setDataset(this);
		myserie.setName(id);
		addNewSerie(id, myserie, getDate(scope));
		return myserie;

	}

	/**
	 * Removeserie.
	 *
	 * @param scope
	 *            the scope
	 * @param id
	 *            the id
	 */
	public void removeserie(final IScope scope, final String id) {

		final ChartDataSeries serie = this.getDataSeries(scope, id);
		if (serie != null) {
			this.deletedseries.put(id, serie);
			this.series.remove(id);
			this.serieRemovalDate.put(id, this.getDate(scope));
			serieToUpdateBefore.put(id, this.getDate(scope));
			this.deletedseries.put(id, serie);
			this.setResetAllBefore(this.getDate(scope));

		}
	}

	/**
	 * Gets the style.
	 *
	 * @param scope
	 *            the scope
	 * @return the style
	 */
	public String getStyle(final IScope scope) {

		return defaultstyle;
	}

	/**
	 * Sets the force no Y accumulate.
	 *
	 * @param b
	 *            the new force no Y accumulate
	 */
	public void setForceNoYAccumulate(final boolean b) {

		this.forceNoYAccumulate = b;

	}

	/**
	 * Save history.
	 *
	 * @param scope
	 *            the scope
	 * @param name
	 *            the name
	 */
	public void saveHistory(final IScope scope, final String name) {
		if (scope == null) return;
		if (keepHistory) {
			try {
				Files.newFolder(scope, chartFolder);
				String file = chartFolder + "/" + "chart_" + name + ".csv";
				file = FileUtils.constructAbsoluteFilePath(scope, file, false);
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
					history.writeTo(bw);
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

	}

}
