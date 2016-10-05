package msi.gama.outputs.layers.charts;

import java.util.HashMap;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.util.GAML;
import msi.gama.util.GamaColor;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Random;
import msi.gaml.types.Types;

public class ChartDataSourceUnique extends ChartDataSource {

	String myname;

	@Override
	public boolean cloneMe(final IScope scope, final int chartCycle, final ChartDataSource source) {

		final boolean res = super.cloneMe(scope, chartCycle, source);
		final GamaColor col = new GamaColor(Random.opRnd(scope, 255), Random.opRnd(scope, 255),
				Random.opRnd(scope, 255), 255);
		final IExpression ncol = GAML.getExpressionFactory().createConst(col, Types.COLOR);
		this.colorexp = ncol;
		myname = ((ChartDataSourceUnique) source).myname + "*";
		return res;
	}

	@Override
	public ChartDataSource getClone(final IScope scope, final int chartCycle) {
		final ChartDataSourceUnique res = new ChartDataSourceUnique();
		res.cloneMe(scope, chartCycle, this);
		return res;
	}

	public ChartDataSeries getMyserie() {
		return mySeries.get(getName());
	}

	public String getName() {
		return myname;
	}

	public void setName(final String name) {
		this.myname = name;
	}

	public ChartDataSourceUnique() {
		// TODO Auto-generated constructor stub
	}

	public void setLegend(final IScope scope, final String stval) {
		myname = stval;
	}

	@Override
	public void updatevalues(final IScope scope, final int chartCycle) {
		super.updatevalues(scope, chartCycle);

		Object o = null;
		final HashMap<String, Object> barvalues = new HashMap<String, Object>();
		if (this.isUseYErrValues())
			barvalues.put(ChartDataStatement.YERR_VALUES, this.getValueyerr().value(scope));
		if (this.isUseXErrValues())
			barvalues.put(ChartDataStatement.XERR_VALUES, this.getValueyerr().value(scope));
		if (this.isUseYMinMaxValues())
			barvalues.put(ChartDataStatement.XERR_VALUES, this.getValuexerr().value(scope));
		if (this.isUseSizeExp())
			barvalues.put(ChartDataStatement.MARKERSIZE, this.getSizeexp().value(scope));
		if (this.isUseColorExp())
			barvalues.put(IKeyword.COLOR, this.getColorexp().value(scope));

		if (getValue() != null) {
			o = getValue().value(scope);
		}

		if (o == null) {
			// lastvalue??
		} else {

			updateseriewithvalue(scope, getMyserie(), o, chartCycle, barvalues, -1);

		}

	}

	public void inferDatasetProperties(final IScope scope, final ChartDataSeries myserie) {
		Object o = null;
		if (this.getValue() != null) {
			o = this.getValue().value(scope);
		}

		final int type_val = get_data_type(scope, o);
		// by default

		getDataset().getOutput().setDefaultPropertiesFromType(scope, this, o, type_val);

	}

	@Override
	public void createInitialSeries(final IScope scope) {
		final ChartDataSeries myserie = new ChartDataSeries();

		myserie.setMysource(this);

		myserie.setDataset(getDataset());

		inferDatasetProperties(scope, myserie);

		final String myname = getName();

		myserie.setName(myname);

		mySeries.put(myname, myserie);
	}

}
