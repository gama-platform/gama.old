package msi.gama.outputs.layers.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

public class ChartDataSource {


	IExpression value;
	Object lastvalue;	
//	HashMap<String,Object> sourceParameters=new HashMap<String,Object>();	
	LinkedHashMap<String,ChartDataSeries> mySeries=new LinkedHashMap<String,ChartDataSeries>();
	ChartDataSet myDataset;
	boolean isCumulative=false;
	boolean useMarker=true;

	public boolean isByCategory() {
		return this.getDataset().isByCategory();
	}

	public boolean isCommonXSeries() {
		return this.getDataset().isCommonXSeries();
	}

	public boolean isCumulative() {
		return isCumulative;
	}

	public void setCumulative(boolean isCumulative) {
		this.isCumulative = isCumulative;
	}

	public ChartDataSet getDataset() {
		return myDataset;
	}

	public void setDataset(ChartDataSet myDataset) {
		this.myDataset = myDataset;
	}

	public IExpression getValue() {
		return value;
	}

	public Object getValue(final IScope scope) throws GamaRuntimeException {
		Object o;
		if ( value != null ) {
			o = value.value(scope);
		} else {
			o = lastvalue;
		}
		if ( o instanceof GamaList ) { return Cast.asList(scope, o); }
		if ( o instanceof GamaPoint ) { return Cast.asPoint(scope, o); }
		return Cast.asFloat(scope, o);
	}	
	
	public LinkedHashMap<String,ChartDataSeries> getSeries()
	{
		return mySeries;
	}
	
	public void setValue(final IExpression value) {
		this.value = value;
	}

	public void setMarkerBool(IScope scope, boolean boolval) {
		// TODO Auto-generated method stub
		useMarker=boolval;
	}

	
	public class ChartUniqueDataSource extends ChartDataSource
	{

		
		
	}

	public class ChartListDataSource extends ChartDataSource
	{
		
	}

	public void updatevalues(IScope scope, int lastUpdateCycle) {
		// TODO Auto-generated method stub
		
		
	}


}
