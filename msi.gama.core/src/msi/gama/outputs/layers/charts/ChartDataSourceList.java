/*******************************************************************************************************
 *
 * ChartDataSourceList.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.util.ArrayList;
import java.util.HashMap;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

/**
 * The Class ChartDataSourceList.
 */
public class ChartDataSourceList extends ChartDataSource {

	/** The currentseries. */
	ArrayList<String> currentseries;

	/** The legend exp. */
	IExpression legendExp;

	@Override
	public boolean cloneMe(final IScope scope, final int chartCycle, final ChartDataSource source) {

		currentseries = ((ChartDataSourceList) source).currentseries;
		legendExp = ((ChartDataSourceList) source).legendExp;
		return super.cloneMe(scope, chartCycle, source);
	}

	@Override
	public ChartDataSource getClone(final IScope scope, final int chartCycle) {
		final ChartDataSourceList res = new ChartDataSourceList();
		res.cloneMe(scope, chartCycle, this);
		return res;
	}

	/**
	 * Sets the name exp.
	 *
	 * @param scope
	 *            the scope
	 * @param expval
	 *            the expval
	 */
	public void setNameExp(final IScope scope, final IExpression expval) {
		legendExp = expval;
	}

	@Override
	public void updatevalues(final IScope scope, final int chartCycle) {
		super.updatevalues(scope, chartCycle);
		Object o = null;
		// final Object oname = this.getNameExp();
		final HashMap<String, Object> barvalues = new HashMap<>();
		if (this.isUseYErrValues()) { barvalues.put(ChartDataStatement.YERR_VALUES, this.getValueyerr().value(scope)); }
		if (this.isUseXErrValues()) { barvalues.put(ChartDataStatement.XERR_VALUES, this.getValuexerr().value(scope)); }
		if (this.isUseYMinMaxValues()) {
			barvalues.put(ChartDataStatement.XERR_VALUES, this.getValuexerr().value(scope));
		}
		if (this.isUseSizeExp()) { barvalues.put(ChartDataStatement.MARKERSIZE, this.getSizeexp().value(scope)); }
		if (this.isUseColorExp()) { barvalues.put(IKeyword.COLOR, this.getColorexp().value(scope)); }

		// TODO check same length and list

		updateserielist(scope, chartCycle);

		// int type_val = this.DATA_TYPE_NULL;
		if (getValue() != null) { o = getValue().value(scope); }
		// type_val = get_data_type(scope, o);

		if (o == null) {
			// lastvalue??
		} else // TODO Matrix case
		if (o instanceof IList) {
			final IList<?> lval = Cast.asList(scope, o);

			if (lval.size() > 0) {
				for (int i = 0; i < lval.size(); i++) {
					final Object no = lval.get(i);
					if (no != null) {
						updateseriewithvalue(scope, mySeries.get(currentseries.get(i)), no, chartCycle, barvalues, i);
					}
				}
			}
		}

	}

	/**
	 * Updateserielist.
	 *
	 * @param scope
	 *            the scope
	 * @param chartCycle
	 *            the chart cycle
	 */
	private void updateserielist(final IScope scope, final int chartCycle) {
		final IList<String> legends = Cast.asList(scope, legendExp.value(scope));
		if (legends == null) return;
		final IList<?> values = Cast.asList(scope, getValue().value(scope));
		final ArrayList<String> previousSeries = currentseries;
		currentseries = new ArrayList<>();
		boolean somethingChanged = false;
		if (legends.size() > 0) {
			// value list case
			for (int i = 0; i < Math.min(values.size(), legends.size()); i++) {
				final String name = legends.get(i);
				if (name != null) {
					currentseries.add(name);
					if (i >= previousSeries.size() || !previousSeries.get(i).equals(name)) {
						somethingChanged = true;
						if (previousSeries.contains(name)) {
							// serie i was serie k before
						} else {
							// new serie
							newSerie(scope, name);
						}
					}
				}
			}
		}
		if (currentseries.size() != previousSeries.size()) { somethingChanged = true; }
		if (somethingChanged) {
			for (int i = 0; i < previousSeries.size(); i++) {
				if (!currentseries.contains(previousSeries.get(i))) {
					// series i deleted
					this.getDataset().removeserie(scope, previousSeries.get(i));
				}

			}
			ChartDataSeries s;

			for (String element : currentseries) {
				s = this.getDataset().getDataSeries(scope, element);
				this.getDataset().series.remove(element);
				this.getDataset().series.put(element, s);
			}

		}

	}

	/**
	 * Newserie.
	 *
	 * @param scope
	 *            the scope
	 * @param myname
	 *            the myname
	 */
	private void newSerie(final IScope scope, final String myname) {
		if (this.getDataset().getDataSeriesIds(scope).contains(myname)) {
			// TODO
			// DO SOMETHING? create id and store correspondance
			// DEBUG.LOG("Serie "+myname+"s already exists... Will
			// replace old one!!");
		}
		final ChartDataSeries myserie = myDataset.createOrGetSerie(scope, myname, this);
		mySeries.put(myname, myserie);

	}

	@Override
	public void createInitialSeries(final IScope scope) {

		final Object on = legendExp.value(scope);

		if (on instanceof IList) {
			final IList<?> lval = Cast.asList(scope, on);
			currentseries = new ArrayList<>();

			if (lval.size() > 0) {
				for (int i = 0; i < lval.size(); i++) {
					final Object no = lval.get(i);
					if (no != null) {
						final String myname = Cast.asString(scope, no);
						newSerie(scope, myname);
						currentseries.add(i, myname);
					}
				}
			}
		}
		inferDatasetProperties(scope);
	}

	// public void inferDatasetProperties(final IScope scope) {
	// int type_val = ChartDataSource.DATA_TYPE_NULL;
	// final IExpression value = getValue();
	// if (value != null) {
	// if (Types.LIST.isAssignableFrom(value.getType()) && value instanceof ListExpression
	// && ((ListExpression) value).getElements().length > 0) {
	// type_val = computeTypeOfData(scope, value);
	// }
	//
	// }
	//
	// getDataset().getOutput().setDefaultPropertiesFromType(scope, this, type_val);
	//
	// }

	/**
	 * Infer dataset properties.
	 *
	 * @param scope
	 *            the scope
	 */
	public void inferDatasetProperties(final IScope scope) {
		Object o = null;
		int type_val = ChartDataSource.DATA_TYPE_NULL;
		if (this.getValue() != null) {
			o = this.getValue().value(scope);
			if (o instanceof IList && Cast.asList(scope, o).size() > 0) {
				final Object o2 = Cast.asList(scope, o).get(0);
				type_val = get_data_type(scope, o2);
			}

		}

		getDataset().getOutput().setDefaultPropertiesFromType(scope, this, type_val);

	}
}
