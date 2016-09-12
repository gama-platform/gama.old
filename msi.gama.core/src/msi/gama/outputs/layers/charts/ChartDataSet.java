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
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Files;
import msi.gaml.operators.Strings;

public class ChartDataSet {

	private static String chartFolder = "charts";

	ArrayList<ChartDataSource> sources = new ArrayList<ChartDataSource>();
	LinkedHashMap<String, ChartDataSeries> series = new LinkedHashMap<String, ChartDataSeries>();
	LinkedHashMap<String, ChartDataSeries> deletedseries = new LinkedHashMap<String, ChartDataSeries>();
	ArrayList<String> Xcategories = new ArrayList<String>(); // for categories
																// datasets
	ArrayList<Double> XSeriesValues = new ArrayList<Double>(); // for series
	ArrayList<String> Ycategories = new ArrayList<String>(); // for Y categories
																// datasets
	ArrayList<Double> YSeriesValues = new ArrayList<Double>(); // for 3d series
	LinkedHashMap<String, Integer> serieCreationDate = new LinkedHashMap<String, Integer>();

	int commonXindex = -1; // current index on X value (usually last of list,
							// can be less when going back in time...)
	int commonYindex = -1; // current index on X value (usually last of list,
							// can be less when going back in time...)

	IExpression xsource; // to replace default common X Source
	IExpression ysource; // to replace default common X Labels
	IExpression xlabels; // to replace default common Y Source
	IExpression ylabels; // to replace default common Y Labels

	LinkedHashMap<String, Integer> serieRemovalDate = new LinkedHashMap<String, Integer>();
	LinkedHashMap<String, Integer> serieToUpdateBefore = new LinkedHashMap<String, Integer>();
	ChartOutput mainoutput;
	int resetAllBefore = 0;
	boolean forceResetAll = false;

	String defaultstyle = IKeyword.DEFAULT;

	int lastchartcycle = -1;
	boolean forceNoXAccumulate = false;
	boolean forceNoYAccumulate = false;
	boolean useXSource = false;
	boolean useXLabels = false;
	boolean useYSource = false;
	boolean useYLabels = false;
	boolean commonXSeries = false; // series
	boolean commonYSeries = false; // heatmap & 3d
	boolean byCategory = false; // histogram/pie
	boolean keepOldSeries = true; // keep old series or move to deleted (to keep
									// history)

	StringBuilder history;

	public int getCommonXIndex() {
		return commonXindex;
	}

	public int getCommonYIndex() {
		return commonYindex;
	}

	public int getResetAllBefore() {
		return resetAllBefore;
	}

	public void setResetAllBefore(final int resetAllBefore) {
		this.resetAllBefore = resetAllBefore;
		forceResetAll = true;
	}

	public boolean isKeepOldSeries() {
		return keepOldSeries;
	}

	public void setKeepOldSeries(final boolean keepOldSeries) {
		this.keepOldSeries = keepOldSeries;
	}

	private ArrayList<String> getCategories() {
		return Xcategories;
	}

	public String getCategories(final IScope scope, final int i) {
		if (Xcategories.size() > i) {
			return Xcategories.get(i);

		} else {
			for (int c = Xcategories.size(); c <= i; c++) {
				this.Xcategories.add("c" + c);
			}
			return Xcategories.get(i);
		}
	}

	public String getLastCategories(final IScope scope) {
		if (Xcategories.size() > 0) {
			return Xcategories.get(Xcategories.size() - 1);

		} else {
			this.Xcategories.add("c" + 0);
			return Xcategories.get(Xcategories.size() - 1);
		}
	}

	public void setCategories(final ArrayList<String> categories) {
		this.Xcategories = categories;
	}

	public ArrayList<Double> getXSeriesValues() {
		return XSeriesValues;
	}

	public ArrayList<Double> getYSeriesValues() {
		return YSeriesValues;
	}

	public void setXSeriesValues(final ArrayList<Double> xSeriesValues) {
		XSeriesValues = xSeriesValues;
	}

	public boolean isByCategory() {
		return byCategory;
	}

	public void setByCategory(final boolean byCategory) {
		this.byCategory = byCategory;
	}

	public boolean isCommonXSeries() {
		return commonXSeries;
	}

	public void setCommonXSeries(final boolean temporalSeries) {
		this.commonXSeries = temporalSeries;
	}

	public boolean isCommonYSeries() {
		return commonYSeries;
	}

	public void setCommonYSeries(final boolean temporalSeries) {
		this.commonYSeries = temporalSeries;
	}

	public LinkedHashMap<String, Integer> getSerieCreationDate() {
		return serieCreationDate;
	}

	public LinkedHashMap<String, Integer> getSerieRemovalDate() {
		return serieRemovalDate;
	}

	public ChartDataSet() {
		history = new StringBuilder();
	}

	public StringBuilder getHistory() {
		return history;
	}

	public ChartOutput getOutput() {
		return mainoutput;
	}

	public void setOutput(final ChartOutput output) {
		mainoutput = output;
		this.defaultstyle = output.getStyle();
	}

	public void addNewSerie(final String id, final ChartDataSeries serie, final int date) {
		if (series.keySet().contains(id)) {
			// Series name already present, should do something.... Don't change
			// creation date?
			series.put(id, serie);
			// serieCreationDate.put(id, date);
			serieToUpdateBefore.put(id, date);
			serieRemovalDate.put(id, -1);
		} else {
			series.put(id, serie);
			serieCreationDate.put(id, date);
			serieToUpdateBefore.put(id, date);
			serieRemovalDate.put(id, -1);

		}

	}

	public ArrayList<ChartDataSource> getSources() {
		return sources;
	}

	public void addDataSource(final ChartDataSource source) {
		sources.add(source);
		final LinkedHashMap<String, ChartDataSeries> newseries = source.getSeries();
		for (final Entry<String, ChartDataSeries> entry : newseries.entrySet()) {
			// should do something... raise an exception?
			addNewSerie(entry.getKey(), entry.getValue(), -1);
		}
		// series.putAll(source.getSeries());
	}

	public boolean doResetAll(final IScope scope, final int lastUpdateCycle) {
		// TODO Auto-generated method stub
		if (resetAllBefore > lastUpdateCycle || forceResetAll) {
			forceResetAll = false;
			return true;

		}
		return false;
	}

	public Set<String> getDataSeriesIds(final IScope scope) {
		// TODO Auto-generated method stub
		return series.keySet();
	}

	public ChartDataSeries getDataSeries(final IScope scope, final String serieid) {
		// TODO Auto-generated method stub
		return series.get(serieid);
	}

	public boolean didReload(final IScope scope, final int chartCycle) {

		boolean didr = false;
		final int mychartcycle = chartCycle;
		// int mychartcycle=scope.getSimulationScope().getCycle(scope)+1;
		// System.out.println("cycle "+mychartcycle+" last: "+lastchartcycle);
		if (lastchartcycle >= mychartcycle) {
			lastchartcycle = mychartcycle - 1;
			didr = true;
		} else {
			lastchartcycle = mychartcycle;

		}
		return didr;

	}

	public void BackwardSim(final IScope scope, final int chartCycle) {
		this.setResetAllBefore(chartCycle);
		final ArrayList<ChartDataSource> sourcestoremove = new ArrayList<ChartDataSource>();
		final ArrayList<ChartDataSource> sourcestoadd = new ArrayList<ChartDataSource>();
		for (final ChartDataSource source : sources) {
			if (source.isCumulative || source.isCumulativeY) {

				final ChartDataSource newsource = source.getClone(scope, chartCycle);
				newsource.createInitialSeries(scope);
				sourcestoremove.add(source);
				sourcestoadd.add(newsource);
			}
		}
		for (final ChartDataSource source : sourcestoremove) {
			sources.remove(source);
		}
		for (final ChartDataSource source : sourcestoadd) {
			this.addDataSource(source);
		}
		if (this.getXSeriesValues().size() > 0) {
			final ArrayList<Double> ser = this.getXSeriesValues();
			for (int i = 0; i < this.getXSeriesValues().size(); i++) {
				if (ser.get(i) == chartCycle - 1) {
					this.commonXindex = i;
				}
			}

		}
		if (this.getYSeriesValues().size() > 0) {
			final ArrayList<Double> sery = this.getYSeriesValues();
			for (int i = 0; i < this.getYSeriesValues().size(); i++) {
				if (sery.get(i) == chartCycle - 1) {
					this.commonYindex = i;
				}
			}

		}

	}

	public void updatedataset(final IScope scope, final int chartCycle) {
		// TODO Auto-generated method stub

		commonXindex++;
		commonYindex++;
		if (didReload(scope, chartCycle)) {
			BackwardSim(scope, chartCycle);
		}
		updateXValues(scope, chartCycle);
		updateYValues(scope, chartCycle);

		if (commonXindex >= this.getXSeriesValues().size())
			commonXindex = this.getXSeriesValues().size() - 1;
		if (commonYindex >= this.getYSeriesValues().size())
			commonYindex = this.getYSeriesValues().size() - 1;

		for (final ChartDataSource source : sources) {
			source.updatevalues(scope, chartCycle);
			source.savehistory(scope, history);
		}
		history.append(Strings.LN);
	}

	public void updateYValues(final IScope scope, final int chartCycle, int targetNb) {
		Object xval, xlab;
		if (this.useYSource || this.useYLabels) {

			if (this.useYSource) {
				xval = ysource.resolveAgainst(scope).value(scope);
			} else {
				xval = ylabels.resolveAgainst(scope).value(scope);
			}
			if (this.useYLabels) {
				xlab = ylabels.resolveAgainst(scope).value(scope);
			} else {
				xlab = ysource.resolveAgainst(scope).value(scope);
			}

			if (xval instanceof GamaList) {
				final IList xv2 = Cast.asList(scope, xval);
				final IList xl2 = Cast.asList(scope, xlab);

				if (this.useYSource && xv2.size() > 0 && xv2.get(0) instanceof Number) {
					YSeriesValues = new ArrayList<Double>();
					Ycategories = new ArrayList<String>();
					for (int i = 0; i < xv2.size(); i++) {
						YSeriesValues.add(new Double(Cast.asFloat(scope, xv2.get(i))));
						Ycategories.add(Cast.asString(scope, xl2.get(i)));

					}

				} else {
					if (xv2.size() > Ycategories.size()) {
						Ycategories = new ArrayList<String>();
						for (int i = 0; i < xv2.size(); i++) {
							if (i >= YSeriesValues.size()) {
								YSeriesValues.add(new Double(getYCycleOrPlusOneForBatch(scope, chartCycle)));
							}
							Ycategories.add(Cast.asString(scope, xl2.get(i)));
						}
					}
				}
				if (xv2.size() < targetNb) {
					throw GamaRuntimeException.error("The x-serie length (" + xv2.size()
							+ ") should NOT be shorter than any series length (" + targetNb + ") !", scope);
				}
			} else {
				if (this.useYSource && xval instanceof Number) {
					final double dvalue = Cast.asFloat(scope, xval);
					final String lvalue = Cast.asString(scope, xlab);
					YSeriesValues.add(new Double(dvalue));
					Ycategories.add(lvalue);
				}
				if (targetNb == -1 && !this.forceNoYAccumulate)
					targetNb = YSeriesValues.size() + 1;
				while (YSeriesValues.size() < targetNb) {
					YSeriesValues.add(new Double(getYCycleOrPlusOneForBatch(scope, chartCycle)));
					Ycategories.add(Cast.asString(scope, xlab));
				}
			}

		}

		if (!this.useYSource && !this.useYLabels) {
			if (targetNb == -1 && !this.forceNoYAccumulate && commonYindex >= YSeriesValues.size())
				targetNb = YSeriesValues.size() + 1;
			while (YSeriesValues.size() < targetNb) {
				addCommonYValue(scope, getYCycleOrPlusOneForBatch(scope, chartCycle));
			}

		}

	}

	public void updateYValues(final IScope scope, final int chartCycle) {
		updateYValues(scope, chartCycle, -1);

	}

	public int getYCycleOrPlusOneForBatch(final IScope scope, final int chartcycle) {
		if (this.YSeriesValues.contains((double) chartcycle))
			return (int) YSeriesValues.get(YSeriesValues.size() - 1).doubleValue() + 1;
		return chartcycle;
	}

	private void addCommonYValue(final IScope scope, final int chartCycle) {
		// TODO Auto-generated method stub
		YSeriesValues.add(new Double(chartCycle));
		Ycategories.add("" + chartCycle);

	}

	public double getCurrentCommonYValue() {
		// TODO Auto-generated method stub
		return this.YSeriesValues.get(this.commonYindex);
	}

	public double getCurrentCommonXValue() {
		// TODO Auto-generated method stub
		return this.XSeriesValues.get(this.commonXindex);
	}

	public void updateXValues(final IScope scope, final int chartCycle, int targetNb) {
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

			if (xval instanceof GamaList) {
				final IList xv2 = Cast.asList(scope, xval);
				final IList xl2 = Cast.asList(scope, xlab);

				if (this.useXSource && xv2.size() > 0 && xv2.get(0) instanceof Number) {
					XSeriesValues = new ArrayList<Double>();
					Xcategories = new ArrayList<String>();
					for (int i = 0; i < xv2.size(); i++) {
						XSeriesValues.add(new Double(Cast.asFloat(scope, xv2.get(i))));
						Xcategories.add(Cast.asString(scope, xl2.get(i)));

					}

				} else {
					if (xv2.size() > Xcategories.size()) {
						Xcategories = new ArrayList<String>();
						for (int i = 0; i < xv2.size(); i++) {
							if (i >= XSeriesValues.size()) {
								XSeriesValues.add(new Double(getXCycleOrPlusOneForBatch(scope, chartCycle)));
							}
							Xcategories.add(Cast.asString(scope, xl2.get(i)));
						}

					}

				}
				if (xv2.size() < targetNb) {
					throw GamaRuntimeException.error("The x-serie length (" + xv2.size()
							+ ") should NOT be shorter than any series length (" + targetNb + ") !", scope);
				}

			} else {
				if (this.useXSource && xval instanceof Number) {
					final double dvalue = Cast.asFloat(scope, xval);
					final String lvalue = Cast.asString(scope, xlab);
					XSeriesValues.add(new Double(dvalue));
					Xcategories.add(lvalue);
				}
				if (targetNb == -1 && !this.forceNoXAccumulate)
					targetNb = XSeriesValues.size() + 1;
				while (XSeriesValues.size() < targetNb) {
					XSeriesValues.add(new Double(getXCycleOrPlusOneForBatch(scope, chartCycle)));
					Xcategories.add(Cast.asString(scope, xlab));
				}
			}

		}

		if (!this.useXSource && !this.useXLabels) {
			if (targetNb == -1 && !this.forceNoXAccumulate && commonXindex >= XSeriesValues.size())
				targetNb = XSeriesValues.size() + 1;
			while (XSeriesValues.size() < targetNb) {
				addCommonXValue(scope, getXCycleOrPlusOneForBatch(scope, chartCycle));
			}

		}

	}

	public void updateXValues(final IScope scope, final int chartCycle) {
		updateXValues(scope, chartCycle, -1);

	}

	public int getXCycleOrPlusOneForBatch(final IScope scope, final int chartcycle) {
		if (this.XSeriesValues.contains((double) chartcycle))
			return (int) XSeriesValues.get(XSeriesValues.size() - 1).doubleValue() + 1;
		return chartcycle;
	}

	private void addCommonXValue(final IScope scope, final int chartCycle) {
		// TODO Auto-generated method stub
		XSeriesValues.add(new Double(chartCycle));
		Xcategories.add("" + chartCycle);

	}

	public int getDate(final IScope scope) {
		return scope.getClock().getCycle();
	}

	public void setXSource(final IScope scope, final IExpression data) {
		// TODO Auto-generated method stub
		this.useXSource = true;
		this.xsource = data;
	}

	public void setXLabels(final IScope scope, final IExpression data) {
		// TODO Auto-generated method stub
		this.useXLabels = true;
		this.xlabels = data;
	}

	public void setYSource(final IScope scope, final IExpression data) {
		// TODO Auto-generated method stub
		this.useYSource = true;
		this.ysource = data;
	}

	public void setYLabels(final IScope scope, final IExpression data) {
		// TODO Auto-generated method stub
		this.useYLabels = true;
		this.ylabels = data;
	}

	public ChartDataSeries createOrGetSerie(final IScope scope, final String id, final ChartDataSourceList source) {
		// TODO Auto-generated method stub
		if (series.keySet().contains(id)) {
			return series.get(id);
		} else {
			if (deletedseries.keySet().contains(id)) {
				final ChartDataSeries myserie = deletedseries.get(id);
				deletedseries.remove(id);
				this.serieRemovalDate.put(id, -1);
				myserie.setMysource(source);
				myserie.setDataset(this);
				myserie.setName(id);
				addNewSerie(id, myserie, getDate(scope));
				return myserie;
			} else {
				final ChartDataSeries myserie = new ChartDataSeries();
				myserie.setMysource(source);
				myserie.setDataset(this);
				myserie.setName(id);
				addNewSerie(id, myserie, getDate(scope));
				return myserie;
			}

		}

	}

	public void removeserie(final IScope scope, final String id) {
		// TODO Auto-generated method stub
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

	public void setStyle(final IScope scope, final String stval) {
		// TODO Auto-generated method stub
		defaultstyle = stval;
	}

	public String getStyle(final IScope scope) {
		// TODO Auto-generated method stub
		return defaultstyle;
	}

	public void setForceNoYAccumulate(final boolean b) {
		// TODO Auto-generated method stub
		this.forceNoYAccumulate = b;

	}

	public void saveHistory(final IScope scope, final String name) {
		if (scope == null) {
			return;
		}
		try {
			Files.newFolder(scope, chartFolder);
			String file = chartFolder + "/" + "chart_" + name + ".csv";
			BufferedWriter bw;
			// file = FileUtils.constructAbsoluteFilePath(scope, file, false);
			file = FileUtils.constructAbsoluteFilePath(scope, file, false);
			bw = new BufferedWriter(new FileWriter(file));

			bw.append(history);
			bw.close();
		} catch (final Exception e) {
			e.printStackTrace();
			return;
		} finally {
			// GAMA.releaseScope(scope);
		}

	}

}
