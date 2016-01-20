package msi.gama.outputs.layers.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

public class ChartDataSource {

	
	public static final int DATA_TYPE_NULL = 0;
	public static final int DATA_TYPE_DOUBLE = 1;
	public static final int DATA_TYPE_LIST_DOUBLE_12 = 2;
	public static final int DATA_TYPE_LIST_DOUBLE_3 = 3;
	public static final int DATA_TYPE_LIST_DOUBLE_N = 4;
	public static final int DATA_TYPE_LIST_LIST_DOUBLE_12 = 5;
	public static final int DATA_TYPE_LIST_LIST_DOUBLE_3 = 6;
	public static final int DATA_TYPE_LIST_LIST_DOUBLE_N = 7;
	public static final int DATA_TYPE_LIST_LIST_LIST_DOUBLE = 8;
	public static final int DATA_TYPE_POINT = 9;
	public static final int DATA_TYPE_LIST_POINT = 10;
	public static final int DATA_TYPE_LIST_LIST_POINT = 11;
	public static final int DATA_TYPE_MATRIX_DOUBLE = 12;
	public static final int DATA_TYPE_MATRIX_POINT = 13;
	public static final int DATA_TYPE_MATRIX_LIST_DOUBLE = 14;


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

	public void setCumulative(IScope scope, boolean isCumulative) {
		this.isCumulative = isCumulative;
	}

	public ChartDataSet getDataset() {
		return myDataset;
	}

	public void setDataset(ChartDataSet myDataset) {
		this.myDataset = myDataset;
	}

	public void setValueExp(final IScope scope, IExpression expval)
	{
		value=expval;
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
	
	public int get_data_type(IScope scope, Object o)
	{
		int type=this.DATA_TYPE_NULL;
		if (o==null) return this.DATA_TYPE_NULL;
		if ( o instanceof GamaPoint ) return this.DATA_TYPE_POINT;  
		if ( o instanceof GamaMatrix ) 
		{
			IMatrix l1value=Cast.asMatrix(scope, o); 
			if (l1value.length(scope)==0) return this.DATA_TYPE_MATRIX_DOUBLE;
			Object o2=l1value.get(scope, 0, 0);
			if ( o2 instanceof GamaPoint )  return this.DATA_TYPE_MATRIX_POINT;
			if ( o2 instanceof GamaList ) return this.DATA_TYPE_MATRIX_LIST_DOUBLE;
			return this.DATA_TYPE_MATRIX_DOUBLE;
		}
		if ( o instanceof GamaList ) 
		{
		
			IList l1value=Cast.asList(scope, o); 
			if (l1value.length(scope)==0) return this.DATA_TYPE_LIST_DOUBLE_N;
			Object o2=l1value.get(0);
			if ( o2 instanceof GamaPoint )  return this.DATA_TYPE_LIST_POINT;
			if ( o2 instanceof GamaList ) 
			{
				IList l2value=Cast.asList(scope, o2); 
				if (l2value.length(scope)==0) return this.DATA_TYPE_LIST_LIST_DOUBLE_N;
				Object o3=l2value.get(0);
				if ( o3 instanceof GamaList ) return this.DATA_TYPE_LIST_LIST_LIST_DOUBLE;
				if ( o3 instanceof GamaPoint ) return this.DATA_TYPE_LIST_LIST_POINT;
				if (l2value.length(scope)==1) return this.DATA_TYPE_LIST_LIST_DOUBLE_12;
				if (l2value.length(scope)==2) return this.DATA_TYPE_LIST_LIST_DOUBLE_12;
				if (l2value.length(scope)==3) return this.DATA_TYPE_LIST_LIST_DOUBLE_3;				
				if (l2value.length(scope)>3) return this.DATA_TYPE_LIST_LIST_DOUBLE_N;				
			}

			
			if (l1value.length(scope)==1) return this.DATA_TYPE_LIST_DOUBLE_12;
			if (l1value.length(scope)==2) return this.DATA_TYPE_LIST_DOUBLE_12;
			if (l1value.length(scope)==3) return this.DATA_TYPE_LIST_DOUBLE_3;
			if (l1value.length(scope)>3) return this.DATA_TYPE_LIST_DOUBLE_N;
		}
		return this.DATA_TYPE_DOUBLE;
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

	public void setUseSize(IScope scope, boolean b) {
		// TODO Auto-generated method stub
		
	}



}
