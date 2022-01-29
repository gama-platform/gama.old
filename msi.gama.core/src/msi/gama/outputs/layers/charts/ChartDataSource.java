/*******************************************************************************************************
 *
 * ChartDataSource.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;

import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

/**
 * The Class ChartDataSource.
 */
@SuppressWarnings ({ "rawtypes" })
public class ChartDataSource {

	/** The Constant DATA_TYPE_NULL. */
	public static final int DATA_TYPE_NULL = 0;
	
	/** The Constant DATA_TYPE_DOUBLE. */
	public static final int DATA_TYPE_DOUBLE = 1;
	
	/** The Constant DATA_TYPE_LIST_DOUBLE_12. */
	public static final int DATA_TYPE_LIST_DOUBLE_12 = 2;
	
	/** The Constant DATA_TYPE_LIST_DOUBLE_3. */
	public static final int DATA_TYPE_LIST_DOUBLE_3 = 3;
	
	/** The Constant DATA_TYPE_LIST_DOUBLE_N. */
	public static final int DATA_TYPE_LIST_DOUBLE_N = 4;
	
	/** The Constant DATA_TYPE_LIST_LIST_DOUBLE_12. */
	public static final int DATA_TYPE_LIST_LIST_DOUBLE_12 = 5;
	
	/** The Constant DATA_TYPE_LIST_LIST_DOUBLE_3. */
	public static final int DATA_TYPE_LIST_LIST_DOUBLE_3 = 6;
	
	/** The Constant DATA_TYPE_LIST_LIST_DOUBLE_N. */
	public static final int DATA_TYPE_LIST_LIST_DOUBLE_N = 7;
	
	/** The Constant DATA_TYPE_LIST_LIST_LIST_DOUBLE. */
	public static final int DATA_TYPE_LIST_LIST_LIST_DOUBLE = 8;
	
	/** The Constant DATA_TYPE_POINT. */
	public static final int DATA_TYPE_POINT = 9;
	
	/** The Constant DATA_TYPE_LIST_POINT. */
	public static final int DATA_TYPE_LIST_POINT = 10;
	
	/** The Constant DATA_TYPE_LIST_LIST_POINT. */
	public static final int DATA_TYPE_LIST_LIST_POINT = 11;
	
	/** The Constant DATA_TYPE_MATRIX_DOUBLE. */
	public static final int DATA_TYPE_MATRIX_DOUBLE = 12;
	
	/** The Constant DATA_TYPE_MATRIX_POINT. */
	public static final int DATA_TYPE_MATRIX_POINT = 13;
	
	/** The Constant DATA_TYPE_MATRIX_LIST_DOUBLE. */
	public static final int DATA_TYPE_MATRIX_LIST_DOUBLE = 14;

	/** The value. */
	IExpression value;

	/** The valueyerr. */
	IExpression valueyerr;
	
	/** The valuexerr. */
	IExpression valuexerr;
	
	/** The valueyminmax. */
	IExpression valueyminmax;
	
	/** The colorexp. */
	IExpression colorexp;
	
	/** The sizeexp. */
	IExpression sizeexp;
	
	/** The markershapeexp. */
	IExpression markershapeexp;

	/** The unique marker name. */
	String uniqueMarkerName;
	
	/** The style. */
	String style = IKeyword.DEFAULT;

	/** The my series. */
	// Object lastvalue;
	LinkedHashMap<String, ChartDataSeries> mySeries = new LinkedHashMap<>();
	
	/** The my dataset. */
	ChartDataSet myDataset;
	
	/** The is cumulative. */
	boolean isCumulative = false;
	
	/** The is cumulative Y. */
	boolean isCumulativeY = false;
	
	/** The force cumulative. */
	boolean forceCumulative = false;
	
	/** The force cumulative Y. */
	boolean forceCumulativeY = false;
	
	/** The use marker. */
	boolean useMarker = true;
	
	/** The fill marker. */
	boolean fillMarker = true;
	
	/** The show line. */
	boolean showLine = true;
	
	/** The use second Y axis. */
	boolean useSecondYAxis = false;

	/** The use size. */
	boolean useSize = false;

	/** The use Y err values. */
	boolean useYErrValues = false;
	
	/** The use X err values. */
	boolean useXErrValues = false;
	
	/** The use Y min max values. */
	boolean useYMinMaxValues = false;
	
	/** The use color exp. */
	boolean useColorExp = false;
	
	/** The use marker shape exp. */
	boolean useMarkerShapeExp = false;

	/** The line thickness. */
	double lineThickness = 1.0;

	/**
	 * Clone me.
	 *
	 * @param scope the scope
	 * @param chartCycle the chart cycle
	 * @param source the source
	 * @return true, if successful
	 */
	public boolean cloneMe(final IScope scope, final int chartCycle, final ChartDataSource source) {

		value = source.value;

		valueyerr = source.valueyerr;
		valuexerr = source.valuexerr;
		valueyminmax = source.valueyminmax;
		colorexp = source.colorexp;
		sizeexp = source.sizeexp;
		markershapeexp = source.markershapeexp;

		uniqueMarkerName = source.uniqueMarkerName;
		style = source.style;

		myDataset = source.myDataset;
		isCumulative = source.isCumulative;
		isCumulativeY = source.isCumulativeY;
		forceCumulative = source.forceCumulative;
		forceCumulativeY = source.forceCumulativeY;
		useMarker = source.useMarker;
		fillMarker = source.fillMarker;
		showLine = source.showLine;

		useSize = source.useSize;

		useYErrValues = source.useYErrValues;
		useXErrValues = source.useXErrValues;
		useYMinMaxValues = source.useYMinMaxValues;
		useColorExp = source.useColorExp;
		useMarkerShapeExp = source.useMarkerShapeExp;
		lineThickness = source.lineThickness;

		return true;
	}

	/**
	 * Gets the clone.
	 *
	 * @param scope the scope
	 * @param chartCycle the chart cycle
	 * @return the clone
	 */
	public ChartDataSource getClone(final IScope scope, final int chartCycle) {
		final ChartDataSource res = new ChartDataSource();
		res.cloneMe(scope, chartCycle, this);
		return res;
	}

	/**
	 * Gets the valueyerr.
	 *
	 * @return the valueyerr
	 */
	public IExpression getValueyerr() {
		return valueyerr;
	}

	/**
	 * Gets the valuexerr.
	 *
	 * @return the valuexerr
	 */
	public IExpression getValuexerr() {
		return valuexerr;
	}

	/**
	 * Gets the valueyminmax.
	 *
	 * @return the valueyminmax
	 */
	public IExpression getValueyminmax() {
		return valueyminmax;
	}

	/**
	 * Gets the unique marker name.
	 *
	 * @return the unique marker name
	 */
	public String getUniqueMarkerName() {
		return uniqueMarkerName;
	}

	/**
	 * Checks if is use size.
	 *
	 * @return true, if is use size
	 */
	public boolean isUseSize() {
		return useSize;
	}

	/**
	 * Sets the use size.
	 *
	 * @param useSize the new use size
	 */
	public void setUseSize(final boolean useSize) {
		this.useSize = useSize;
	}

	/**
	 * Sets the line thickness.
	 *
	 * @param thickness the new line thickness
	 */
	public void setLineThickness(final double thickness) {
		lineThickness = thickness;
	}

	/**
	 * Gets the line thickness.
	 *
	 * @return the line thickness
	 */
	public double getLineThickness() {
		return lineThickness;
	}

	/**
	 * Gets the colorexp.
	 *
	 * @return the colorexp
	 */
	public IExpression getColorexp() {
		return colorexp;
	}

	/**
	 * Checks if is use Y err values.
	 *
	 * @return true, if is use Y err values
	 */
	public boolean isUseYErrValues() {
		return useYErrValues;
	}

	/**
	 * Sets the use Y err values.
	 *
	 * @param useYErrValues the new use Y err values
	 */
	public void setUseYErrValues(final boolean useYErrValues) {
		this.useYErrValues = useYErrValues;
	}

	/**
	 * Checks if is use X err values.
	 *
	 * @return true, if is use X err values
	 */
	public boolean isUseXErrValues() {
		return useXErrValues;
	}

	/**
	 * Sets the use X err values.
	 *
	 * @param useXErrValues the new use X err values
	 */
	public void setUseXErrValues(final boolean useXErrValues) {
		this.useXErrValues = useXErrValues;
	}

	/**
	 * Checks if is use Y min max values.
	 *
	 * @return true, if is use Y min max values
	 */
	public boolean isUseYMinMaxValues() {
		return useYMinMaxValues;
	}

	/**
	 * Sets the use Y min max values.
	 *
	 * @param useYMinMaxValues the new use Y min max values
	 */
	public void setUseYMinMaxValues(final boolean useYMinMaxValues) {
		this.useYMinMaxValues = useYMinMaxValues;
	}

	/**
	 * Checks if is by category.
	 *
	 * @return true, if is by category
	 */
	public boolean isByCategory() {
		return this.getDataset().isByCategory();
	}

	/**
	 * Checks if is common X series.
	 *
	 * @return true, if is common X series
	 */
	public boolean isCommonXSeries() {
		return this.getDataset().isCommonXSeries();
	}

	/**
	 * Checks if is common Y series.
	 *
	 * @return true, if is common Y series
	 */
	public boolean isCommonYSeries() {
		return this.getDataset().isCommonYSeries();
	}

	/**
	 * Checks if is cumulative.
	 *
	 * @return true, if is cumulative
	 */
	public boolean isCumulative() {
		return isCumulative;
	}

	/**
	 * Sets the cumulative.
	 *
	 * @param scope the scope
	 * @param isCumulative the is cumulative
	 */
	public void setCumulative(final IScope scope, final boolean isCumulative) {
		if (!forceCumulative) {
			this.isCumulative = isCumulative;
		}
	}

	/**
	 * Checks if is cumulative Y.
	 *
	 * @return true, if is cumulative Y
	 */
	public boolean isCumulativeY() {
		return isCumulativeY;
	}

	/**
	 * Sets the cumulative Y.
	 *
	 * @param scope the scope
	 * @param isCumulative the is cumulative
	 */
	public void setCumulativeY(final IScope scope, final boolean isCumulative) {
		if (!forceCumulativeY) {
			this.isCumulativeY = isCumulative;
		}
		if (this.isCumulativeY) {
			this.getDataset().setForceNoYAccumulate(false);
		}
	}

	/**
	 * Sets the force cumulative.
	 *
	 * @param scope the scope
	 * @param b the b
	 */
	public void setForceCumulative(final IScope scope, final boolean b) {
		this.forceCumulative = b;

	}

	/**
	 * Gets the dataset.
	 *
	 * @return the dataset
	 */
	public ChartDataSet getDataset() {
		return myDataset;
	}

	/**
	 * Sets the dataset.
	 *
	 * @param scope the scope
	 * @param myDataset the my dataset
	 */
	public void setDataset(final IScope scope, final ChartDataSet myDataset) {
		this.myDataset = myDataset;
		if (myDataset.getStyle(scope) != null) {
			this.setStyle(scope, myDataset.getStyle(scope));
		}
	}

	/**
	 * Sets the style.
	 *
	 * @param scope the scope
	 * @param stval the stval
	 */
	public void setStyle(final IScope scope, final String stval) {
		style = stval;
	}

	/**
	 * Gets the style.
	 *
	 * @param scope the scope
	 * @return the style
	 */
	public String getStyle(final IScope scope) {
		if (IKeyword.DEFAULT.equals(style)) { return this.getDataset().getStyle(scope); }
		return style;
	}

	/**
	 * Sets the value exp.
	 *
	 * @param scope the scope
	 * @param expval the expval
	 */
	public void setValueExp(final IScope scope, final IExpression expval) {
		value = expval;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public IExpression getValue() {
		return value;
	}

	/**
	 * Gets the data type.
	 *
	 * @param scope the scope
	 * @param o the o
	 * @return the data type
	 */
	public int get_data_type(final IScope scope, final Object o) {
		// final int type = this.DATA_TYPE_NULL;
		if (o == null) { return this.DATA_TYPE_NULL; }
		if (o instanceof GamaPoint) { return this.DATA_TYPE_POINT; }
		if (o instanceof GamaMatrix) {
			final IMatrix l1value = Cast.asMatrix(scope, o);
			if (l1value.length(scope) == 0) { return this.DATA_TYPE_MATRIX_DOUBLE; }
			final Object o2 = l1value.get(scope, 0, 0);
			if (o2 instanceof GamaPoint) { return this.DATA_TYPE_MATRIX_POINT; }
			if (o2 instanceof IList) { return this.DATA_TYPE_MATRIX_LIST_DOUBLE; }
			return this.DATA_TYPE_MATRIX_DOUBLE;
		}
		if (o instanceof IList) {

			final IList l1value = Cast.asList(scope, o);
			if (l1value.length(scope) == 0) { return this.DATA_TYPE_LIST_DOUBLE_N; }
			final Object o2 = l1value.get(0);
			if (o2 instanceof GamaPoint) { return this.DATA_TYPE_LIST_POINT; }
			if (o2 instanceof IList) {
				final IList l2value = Cast.asList(scope, o2);
				if (l2value.length(scope) == 0) { return this.DATA_TYPE_LIST_LIST_DOUBLE_N; }
				final Object o3 = l2value.get(0);
				if (o3 instanceof IList) { return this.DATA_TYPE_LIST_LIST_LIST_DOUBLE; }
				if (o3 instanceof GamaPoint) { return this.DATA_TYPE_LIST_LIST_POINT; }
				if (l2value.length(scope) == 1) { return this.DATA_TYPE_LIST_LIST_DOUBLE_12; }
				if (l2value.length(scope) == 2) { return this.DATA_TYPE_LIST_LIST_DOUBLE_12; }
				if (l2value.length(scope) == 3) { return this.DATA_TYPE_LIST_LIST_DOUBLE_3; }
				if (l2value.length(scope) > 3) { return this.DATA_TYPE_LIST_LIST_DOUBLE_N; }
			}

			if (l1value.length(scope) == 1) { return this.DATA_TYPE_LIST_DOUBLE_12; }
			if (l1value.length(scope) == 2) { return this.DATA_TYPE_LIST_DOUBLE_12; }
			if (l1value.length(scope) == 3) { return this.DATA_TYPE_LIST_DOUBLE_3; }
			if (l1value.length(scope) > 3) { return this.DATA_TYPE_LIST_DOUBLE_N; }
		}
		return this.DATA_TYPE_DOUBLE;
	}

	// void updateseriewithvalue(final IScope scope, final ChartDataSeries myserie, final IExpression expr,
	// final int chartCycle, final HashMap barvalues, final int listvalue) {
	// final int type_val = this.computeTypeOfData(scope, expr);
	/**
	 * Updateseriewithvalue.
	 *
	 * @param scope the scope
	 * @param myserie the myserie
	 * @param o the o
	 * @param chartCycle the chart cycle
	 * @param barvalues the barvalues
	 * @param listvalue the listvalue
	 */
	// final Object o = expr.value(scope);
	void updateseriewithvalue(final IScope scope, final ChartDataSeries myserie, final Object o, final int chartCycle,
			final HashMap barvalues, final int listvalue) {
		final int type_val = this.get_data_type(scope, o);
		// could move into outputs object... would be (a little) less complex.
		// But less factorisation...

		if (!this.isCumulative() && !this.isCumulativeY()) {
			myserie.clearValues(scope);
			myserie.startupdate(scope);

		}
		if (!this.isCommonYSeries()) {

			// series charts (series/bw/...)
			if (this.isCommonXSeries() && !this.isByCategory()) {
				if (this.isCumulative()) {
					// new cumulative Y value

					switch (type_val) {
						case ChartDataSource.DATA_TYPE_POINT: {
							final GamaPoint pvalue = Cast.asPoint(scope, o);
							myserie.addxysvalue(scope,
									getDataset().getXSeriesValues().get(getDataset().getCommonXIndex()), pvalue.getX(),
									pvalue.getY(), chartCycle, barvalues, listvalue);
							break;
						}
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N: {
							final IList lvalue = Cast.asList(scope, o);
							if (lvalue.length(scope) == 0) {

							}
							if (lvalue.length(scope) == 1) {
								myserie.addxyvalue(scope,
										getDataset().getXSeriesValues().get(getDataset().getCommonXIndex()),
										Cast.asFloat(scope, lvalue.get(0)), chartCycle, barvalues, listvalue);
							}
							if (lvalue.length(scope) > 1) {
								myserie.addxysvalue(scope,
										getDataset().getXSeriesValues().get(getDataset().getCommonXIndex()),
										Cast.asFloat(scope, lvalue.get(0)), Cast.asFloat(scope, lvalue.get(1)),
										chartCycle, barvalues, listvalue);
							}
							break;

						}
						case ChartDataSource.DATA_TYPE_NULL: {
							// last value?
							break;
						}
						case ChartDataSource.DATA_TYPE_DOUBLE:
						default: {
							final Double dvalue = Cast.asFloat(scope, o);
							myserie.addxyvalue(scope,
									getDataset().getXSeriesValues().get(getDataset().getCommonXIndex()), dvalue,
									chartCycle, barvalues, listvalue);

							break;
						}

					}

				}
				if (!this.isCumulative()) {
					// new non cumulative y value
					// serie in the order of the dataset
					switch (type_val) {
						case ChartDataSource.DATA_TYPE_POINT: {
							final GamaPoint pvalue = Cast.asPoint(scope, o);
							myserie.addxysvalue(scope, getDataset().getXSeriesValues().get(0), pvalue.getX(),
									pvalue.getY(), chartCycle, barvalues, listvalue);

							break;
						}
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N: {
							final IList l1value = Cast.asList(scope, o);
							for (int n1 = 0; n1 < l1value.size(); n1++) {
								final Object o2 = l1value.get(n1);
								while (n1 >= getDataset().getXSeriesValues().size()) {
									getDataset().updateXValues(scope, chartCycle, l1value.size());
								}
								myserie.addxyvalue(scope, getDataset().getXSeriesValues().get(n1),
										Cast.asFloat(scope, o2), chartCycle, barvalues, listvalue);
							}
							break;

						}
						case ChartDataSource.DATA_TYPE_LIST_LIST_POINT:
						case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
						case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3:
						case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N: {
							final IList l1value = Cast.asList(scope, o);
							for (int n1 = 0; n1 < l1value.size(); n1++) {
								final Object o2 = l1value.get(n1);
								final IList lvalue = Cast.asList(scope, o2);
								if (lvalue.length(scope) == 1) {
									myserie.addxyvalue(scope, getDataset().getXSeriesValues().get(n1),
											Cast.asFloat(scope, lvalue.get(0)), chartCycle, barvalues, listvalue);

								}
								if (lvalue.length(scope) > 1) {
									myserie.addxysvalue(scope, getDataset().getXSeriesValues().get(n1),
											Cast.asFloat(scope, lvalue.get(0)), Cast.asFloat(scope, lvalue.get(1)),
											chartCycle, barvalues, listvalue);
								}

							}
							break;

						}
						case ChartDataSource.DATA_TYPE_NULL: {
							// last value?
							break;
						}
						case ChartDataSource.DATA_TYPE_DOUBLE:
						default: {
							final Double dvalue = Cast.asFloat(scope, o);
							myserie.addxyvalue(scope, getDataset().getXSeriesValues().get(0), dvalue, chartCycle,
									barvalues, listvalue);
							break;

						}

					}

				}

			}

			// xy charts
			if (!this.isByCategory() && !this.isCommonXSeries()) {

				if (this.isCumulative()) {
					// new cumulative XY value

					switch (type_val) {
						case ChartDataSource.DATA_TYPE_POINT: {
							final GamaPoint pvalue = Cast.asPoint(scope, o);
							myserie.addxysvalue(scope, pvalue.getX(), pvalue.getY(), pvalue.getZ(), chartCycle,
									barvalues, listvalue);

							break;
						}
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N: {
							final IList lvalue = Cast.asList(scope, o);
							if (lvalue.length(scope) < 2) {

							}
							if (lvalue.length(scope) == 2) {
								myserie.addxyvalue(scope, Cast.asFloat(scope, lvalue.get(0)),
										Cast.asFloat(scope, lvalue.get(1)), chartCycle, barvalues, listvalue);
							}
							if (lvalue.length(scope) > 2) {
								myserie.addxysvalue(scope, Cast.asFloat(scope, lvalue.get(0)),
										Cast.asFloat(scope, lvalue.get(1)), Cast.asFloat(scope, lvalue.get(2)),
										chartCycle, barvalues, listvalue);
							}
							break;

						}
						case ChartDataSource.DATA_TYPE_NULL: {
							// last value?
							break;
						}
						case ChartDataSource.DATA_TYPE_DOUBLE:
						default: {
							final Double dvalue = Cast.asFloat(scope, o);
							myserie.addxyvalue(scope,
									getDataset().getXSeriesValues().get(getDataset().getCommonXIndex()), dvalue,
									chartCycle, barvalues, listvalue);

							break;
						}

					}

				}

				if (!this.isCumulative()) {
					// new XY values
					switch (type_val) {
						case ChartDataSource.DATA_TYPE_POINT: {
							final GamaPoint pvalue = Cast.asPoint(scope, o);
							myserie.addxysvalue(scope, pvalue.getX(), pvalue.getY(), pvalue.getZ(), chartCycle,
									barvalues, listvalue);

							break;
						}
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N: {
							final IList lvalue = Cast.asList(scope, o);
							if (lvalue.length(scope) < 2) {

							}
							if (lvalue.length(scope) == 2) {
								myserie.addxyvalue(scope, Cast.asFloat(scope, lvalue.get(0)),
										Cast.asFloat(scope, lvalue.get(1)), chartCycle, barvalues, listvalue);
							}
							if (lvalue.length(scope) > 2) {
								myserie.addxysvalue(scope, Cast.asFloat(scope, lvalue.get(0)),
										Cast.asFloat(scope, lvalue.get(1)), Cast.asFloat(scope, lvalue.get(2)),
										chartCycle, barvalues, listvalue);
							}
							break;

						}
						case ChartDataSource.DATA_TYPE_LIST_POINT:
						case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
						case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3:
						case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N: {
							final IList l1value = Cast.asList(scope, o);
							for (int n1 = 0; n1 < l1value.size(); n1++) {
								final Object o2 = l1value.get(n1);
								final IList lvalue = Cast.asList(scope, o2);
								if (lvalue.length(scope) < 2) {

								}
								if (lvalue.length(scope) == 2) {
									myserie.addxyvalue(scope, Cast.asFloat(scope, lvalue.get(0)),
											Cast.asFloat(scope, lvalue.get(1)), chartCycle, barvalues, listvalue);
								}
								if (lvalue.length(scope) > 2) {
									myserie.addxysvalue(scope, Cast.asFloat(scope, lvalue.get(0)),
											Cast.asFloat(scope, lvalue.get(1)), Cast.asFloat(scope, lvalue.get(2)),
											chartCycle, barvalues, listvalue);
								}

							}
							break;

						}
						case ChartDataSource.DATA_TYPE_NULL: {
							// last value?
							break;
						}
						case ChartDataSource.DATA_TYPE_DOUBLE:
						default: {
							final Double dvalue = Cast.asFloat(scope, o);
							myserie.addxyvalue(scope,
									getDataset().getXSeriesValues().get(getDataset().getCommonXIndex()), dvalue,
									chartCycle, barvalues, listvalue);
							break;

						}

					}

				}

			}

			// category charts
			if (this.isByCategory()) {

				if (this.isCumulative()) {
					// new cumulative category value
					// category is the last of the dataset

					switch (type_val) {
						case ChartDataSource.DATA_TYPE_POINT: {
							final GamaPoint pvalue = Cast.asPoint(scope, o);
							myserie.addcysvalue(scope, getDataset().getLastCategories(scope), pvalue.getX(),
									pvalue.getY(), chartCycle, barvalues, listvalue);
							break;
						}
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N: {
							final IList lvalue = Cast.asList(scope, o);
							if (lvalue.length(scope) == 0) {

							}
							if (lvalue.length(scope) == 1) {
								myserie.addcyvalue(scope, getDataset().getLastCategories(scope),
										Cast.asFloat(scope, lvalue.get(0)), chartCycle, barvalues, listvalue);
							}
							if (lvalue.length(scope) > 1) {
								myserie.addcysvalue(scope, getDataset().getLastCategories(scope),
										Cast.asFloat(scope, lvalue.get(0)), Cast.asFloat(scope, lvalue.get(1)),
										chartCycle, barvalues, listvalue);
							}
							break;

						}
						case ChartDataSource.DATA_TYPE_NULL: {
							// last value?
							break;
						}
						case ChartDataSource.DATA_TYPE_DOUBLE:
						default: {
							final Double dvalue = Cast.asFloat(scope, o);
							myserie.addcyvalue(scope, getDataset().getLastCategories(scope), dvalue, chartCycle,
									barvalues, listvalue);

							break;
						}

					}

				}

				if (!this.isCumulative()) {
					// new non cumulative category value
					// category in the order of the dataset
					switch (type_val) {
						case ChartDataSource.DATA_TYPE_POINT: {
							final GamaPoint pvalue = Cast.asPoint(scope, o);
							myserie.addcysvalue(scope, getDataset().getCategories(scope, 0), pvalue.getX(),
									pvalue.getY(), chartCycle, barvalues, listvalue);

							break;
						}
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
						case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N: {
							final IList l1value = Cast.asList(scope, o);
							for (int n1 = 0; n1 < l1value.size(); n1++) {
								final Object o2 = l1value.get(n1);
								myserie.addcyvalue(scope, getDataset().getCategories(scope, n1),
										Cast.asFloat(scope, o2), chartCycle, barvalues, listvalue);
							}
							break;

						}
						case ChartDataSource.DATA_TYPE_LIST_LIST_POINT:
						case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
						case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3:
						case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N: {
							final IList l1value = Cast.asList(scope, o);
							for (int n1 = 0; n1 < l1value.size(); n1++) {
								final Object o2 = l1value.get(n1);
								final IList lvalue = Cast.asList(scope, o2);
								if (lvalue.length(scope) == 1) {
									myserie.addcyvalue(scope, getDataset().getCategories(scope, n1),
											Cast.asFloat(scope, lvalue.get(0)), chartCycle, barvalues, listvalue);

								}
								if (lvalue.length(scope) > 1) {
									myserie.addcysvalue(scope, getDataset().getCategories(scope, n1),
											Cast.asFloat(scope, lvalue.get(0)), Cast.asFloat(scope, lvalue.get(1)),
											chartCycle, barvalues, listvalue);
								}

							}
							break;

						}
						case ChartDataSource.DATA_TYPE_NULL: {
							// last value?
							break;
						}
						case ChartDataSource.DATA_TYPE_DOUBLE:
						default: {
							final Double dvalue = Cast.asFloat(scope, o);
							myserie.addcyvalue(scope, getDataset().getCategories(scope, 0), dvalue, chartCycle,
									barvalues, listvalue);
							break;

						}

					}

				}

			}

		}
		if (this.isCommonYSeries()) {
			// heatmaps

			if (!this.isCumulative()) {
				// new non cumulative z value
				// serie in the order of the dataset

				switch (type_val) {
					case ChartDataSource.DATA_TYPE_POINT: {
						final GamaPoint pvalue = Cast.asPoint(scope, o);
						myserie.addxysvalue(scope, getDataset().getXSeriesValues().get(0),
								getDataset().getYSeriesValues().get(0), pvalue.getX(), chartCycle, barvalues,
								listvalue);

						break;
					}
					case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
					case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
					case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N: {
						final IList l1value = Cast.asList(scope, o);
						for (int n1 = 0; n1 < l1value.size(); n1++) {
							final Object o2 = l1value.get(n1);
							while (n1 >= getDataset().getXSeriesValues().size()) {
								getDataset().updateXValues(scope, chartCycle, l1value.size());
							}
							myserie.addxysvalue(scope, getDataset().getXSeriesValues().get(n1),
									getDataset().getCurrentCommonYValue(), Cast.asFloat(scope, o2), chartCycle,
									barvalues, listvalue);
						}
						break;

					}
					case ChartDataSource.DATA_TYPE_LIST_LIST_POINT:
					case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
					case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3:
					case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N: {
						final IList l1value = Cast.asList(scope, o);
						for (int n1 = 0; n1 < l1value.size(); n1++) {
							final Object o2 = l1value.get(n1);
							final IList lvalue = Cast.asList(scope, o2);
							while (n1 >= getDataset().getXSeriesValues().size()) {
								getDataset().updateXValues(scope, chartCycle, l1value.size());
							}
							for (int n2 = 0; n2 < lvalue.size(); n2++) {
								while (n2 >= getDataset().getYSeriesValues().size()) {
									getDataset().updateYValues(scope, chartCycle, lvalue.size());
								}
								myserie.addxysvalue(scope, getDataset().getXSeriesValues().get(n1),
										getDataset().getYSeriesValues().get(n2), Cast.asFloat(scope, lvalue.get(n2)),
										chartCycle, barvalues, listvalue);

							}

						}
						break;

					}
					case ChartDataSource.DATA_TYPE_NULL: {
						// last value?
						break;
					}
					case ChartDataSource.DATA_TYPE_DOUBLE:
					default: {
						final Double dvalue = Cast.asFloat(scope, o);
						myserie.addxysvalue(scope, getDataset().getXSeriesValues().get(0),
								getDataset().getYSeriesValues().get(0), dvalue, chartCycle, barvalues, listvalue);
						break;

					}

				}

			}

		}
		if (!this.isCumulative()) {
			myserie.endupdate(scope);

		}

	}

	/**
	 * Gets the series.
	 *
	 * @return the series
	 */
	public LinkedHashMap<String, ChartDataSeries> getSeries() {
		return mySeries;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(final IExpression value) {
		this.value = value;
	}

	/**
	 * Sets the Y err value exp.
	 *
	 * @param scope the scope
	 * @param expval the expval
	 */
	public void setYErrValueExp(final IScope scope, final IExpression expval) {
		this.setUseYErrValues(true);
		this.valueyerr = expval;

	}

	/**
	 * Sets the X err value exp.
	 *
	 * @param scope the scope
	 * @param expval the expval
	 */
	public void setXErrValueExp(final IScope scope, final IExpression expval) {
		this.setUseXErrValues(true);
		this.valuexerr = expval;

	}

	/**
	 * Sets the Y min max value exp.
	 *
	 * @param scope the scope
	 * @param expval the expval
	 */
	public void setYMinMaxValueExp(final IScope scope, final IExpression expval) {
		this.setUseYMinMaxValues(true);
		this.valueyminmax = expval;

	}

	/**
	 * Sets the marker shape.
	 *
	 * @param scope the scope
	 * @param stval the stval
	 */
	public void setMarkerShape(final IScope scope, final String stval) {
		// markerName is useless, for now creates/modifies the output
		uniqueMarkerName = stval;
		if (ChartDataStatement.MARKER_EMPTY.equals(uniqueMarkerName)) {
			this.setMarkerBool(scope, false);
		}
	}

	/**
	 * Sets the marker size.
	 *
	 * @param scope the scope
	 * @param expval the expval
	 */
	public void setMarkerSize(final IScope scope, final IExpression expval) {
		this.setUseSize(scope, true);
		this.sizeexp = expval;

	}

	/**
	 * Gets the sizeexp.
	 *
	 * @return the sizeexp
	 */
	public IExpression getSizeexp() {
		return sizeexp;
	}

	/**
	 * Sets the color exp.
	 *
	 * @param scope the scope
	 * @param expval the expval
	 */
	public void setColorExp(final IScope scope, final IExpression expval) {
		this.setUseColorExp(scope, true);
		this.colorexp = expval;

	}

	/**
	 * Checks if is use size exp.
	 *
	 * @return true, if is use size exp
	 */
	public boolean isUseSizeExp() {
		if (this.sizeexp == null) { return false; }
		return true;
	}

	/**
	 * Sets the use color exp.
	 *
	 * @param scope the scope
	 * @param b the b
	 */
	public void setUseColorExp(final IScope scope, final boolean b) {
		this.useColorExp = b;

	}

	/**
	 * Checks if is use color exp.
	 *
	 * @return true, if is use color exp
	 */
	public boolean isUseColorExp() {
		return useColorExp;
	}

	/**
	 * Sets the marker bool.
	 *
	 * @param scope the scope
	 * @param boolval the boolval
	 */
	public void setMarkerBool(final IScope scope, final boolean boolval) {
		useMarker = boolval;
	}

	/**
	 * Sets the fill marker.
	 *
	 * @param scope the scope
	 * @param boolval the boolval
	 */
	public void setFillMarker(final IScope scope, final boolean boolval) {
		fillMarker = boolval;
	}

	/**
	 * Sets the use second Y axis.
	 *
	 * @param scope the scope
	 * @param boolval the boolval
	 */
	public void setUseSecondYAxis(final IScope scope, final boolean boolval) {
		useSecondYAxis = boolval;
	}

	/**
	 * Gets the use second Y axis.
	 *
	 * @param scope the scope
	 * @return the use second Y axis
	 */
	public boolean getUseSecondYAxis(final IScope scope) {
		return useSecondYAxis;
	}

	/**
	 * Sets the show line.
	 *
	 * @param scope the scope
	 * @param boolval the boolval
	 */
	public void setShowLine(final IScope scope, final boolean boolval) {
		showLine = boolval;
	}

	/**
	 * Updatevalues.
	 *
	 * @param scope the scope
	 * @param lastUpdateCycle the last update cycle
	 */
	public void updatevalues(final IScope scope, final int lastUpdateCycle) {

	}

	/**
	 * Sets the use size.
	 *
	 * @param scope the scope
	 * @param b the b
	 */
	public void setUseSize(final IScope scope, final boolean b) {
		this.setUseSize(b);
	}

	/**
	 * Creates the initial series.
	 *
	 * @param scope the scope
	 */
	public void createInitialSeries(final IScope scope) {

	}

	/**
	 * Savehistory.
	 *
	 * @param scope the scope
	 * @param history the history
	 */
	public void savehistory(final IScope scope, final ChartHistory history) {
		for (final Map.Entry<String, ChartDataSeries> seriepair : this.mySeries.entrySet()) {
			seriepair.getValue().savehistory(scope, history);
		}
	}

}
