package msi.gama.outputs.layers.charts;

import java.util.HashMap;

import org.jfree.chart.ChartFactory;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;

public class ChartJFreeChartOutputPie extends ChartJFreeChartOutput {

	public ChartJFreeChartOutputPie(IScope scope, String name,
			IExpression typeexp) {
		super(scope, name, typeexp);
		// TODO Auto-generated constructor stubs
		

				if ( style.equals(IKeyword.THREE_D) ) {
					chart = ChartFactory.createPieChart3D(getName(), null, false, true, false);
				} else if ( style.equals(IKeyword.RING) ) {
					chart = ChartFactory.createRingChart(getName(), null, false, true, false);
				} else if ( style.equals(IKeyword.EXPLODED) ) {
					chart = ChartFactory.createPieChart(getName(), null, false, true, false);
					exploded = true;
				} else {
					chart = ChartFactory.createPieChart(getName(), null, false, true, false);
				}

	}
	public void initdataset()
	{
		super.initdataset();
		if (getType()==ChartOutput.PIE_CHART)
		{
			chartdataset.setCommonXSeries(true);
			chartdataset.setByCategory(true);
		}
	}
	

}
