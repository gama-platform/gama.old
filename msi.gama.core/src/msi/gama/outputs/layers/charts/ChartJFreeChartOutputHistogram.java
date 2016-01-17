package msi.gama.outputs.layers.charts;

import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;

public class ChartJFreeChartOutputHistogram extends ChartJFreeChartOutput {

	public ChartJFreeChartOutputHistogram(IScope scope, String name,
			IExpression typeexp) {
		super(scope, name, typeexp);
		// TODO Auto-generated constructor stub
		
		if ( style.equals(IKeyword.THREE_D) ) {
			chart =
				ChartFactory.createBarChart3D(getName(), null, null, null, PlotOrientation.VERTICAL, true,
					true, false);
		} else if ( style.equals(IKeyword.STACK) ) {
			chart =
				ChartFactory.createStackedBarChart(getName(), null, null, null, PlotOrientation.VERTICAL, true,
					true, false);
		} else {
			chart =
				ChartFactory.createBarChart(getName(), null, null, null, PlotOrientation.VERTICAL, true, true,
					false);
		}		
	}
	public void initdataset()
	{
		super.initdataset();
		if (getType()==ChartOutput.HISTOGRAM_CHART)
		{
			chartdataset.setCommonXSeries(true);
			chartdataset.setByCategory(true);
		}
	}
	

}
