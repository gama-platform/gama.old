package msi.gama.outputs.layers.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
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


	IExpression valueyerr;
	IExpression valuexerr;
	IExpression valueyminmax;
	IExpression colorexp;
	IExpression sizeexp;
	IExpression markershapeexp;
	
	String uniqueMarkerName;
	String style=IKeyword.DEFAULT;

	Object lastvalue;	
	LinkedHashMap<String,ChartDataSeries> mySeries=new LinkedHashMap<String,ChartDataSeries>();
	ChartDataSet myDataset;
	boolean isCumulative=false;
	boolean forceCumulative=false;
	boolean useMarker=true;
	boolean fillMarker=true;
	boolean showLine=true;

	boolean useSize=false;
	

	boolean useYErrValues=false;
	boolean useXErrValues=false;
	boolean useYMinMaxValues=false;
	boolean useColorExp=false;
	boolean useMarkerShapeExp=false;

	public IExpression getValueyerr() {
		return valueyerr;
	}

	public IExpression getValuexerr() {
		return valuexerr;
	}

	public IExpression getValueyminmax() {
		return valueyminmax;
	}

	public String getUniqueMarkerName() {
		return uniqueMarkerName;
	}

	public boolean isUseSize() {
		return useSize;
	}

	public void setUseSize(boolean useSize) {
		this.useSize = useSize;
	}

	public IExpression getColorexp() {
		return colorexp;
	}	
	
	public boolean isUseYErrValues() {
		return useYErrValues;
	}

	public void setUseYErrValues(boolean useYErrValues) {
		this.useYErrValues = useYErrValues;
	}

	public boolean isUseXErrValues() {
		return useXErrValues;
	}

	public void setUseXErrValues(boolean useXErrValues) {
		this.useXErrValues = useXErrValues;
	}

	public boolean isUseYMinMaxValues() {
		return useYMinMaxValues;
	}

	public void setUseYMinMaxValues(boolean useYMinMaxValues) {
		this.useYMinMaxValues = useYMinMaxValues;
	}

	
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
		if (!forceCumulative)
		this.isCumulative = isCumulative;
	}

	public void setForceCumulative(IScope scope, boolean b) {
		this.forceCumulative = b;
		
	}
	
	public ChartDataSet getDataset() {
		return myDataset;
	}

	public void setDataset(IScope scope, ChartDataSet myDataset) {
		this.myDataset = myDataset;
		if (myDataset.getStyle(scope)!=null)
			this.setStyle(scope, myDataset.getStyle(scope));
	}

	public void setStyle(IScope scope, String stval) {
		style=stval;
	}
	public String getStyle(IScope scope) {
		if (style==IKeyword.DEFAULT) return this.getDataset().getStyle(scope);
		return style;
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
	

	 void updateseriewithvalue(IScope scope, ChartDataSeries myserie, Object o, int chartCycle, HashMap barvalues, int listvalue) {
		int type_val=this.get_data_type(scope, o);

		
		//could move into outputs object... would be (a little) less complex. But less factorisation...
				
		if (!this.isCumulative()) 
		{
			myserie.clearValues(scope);
			myserie.startupdate(scope);
			
		}
		
		//series charts (series/bw/...)
		if (this.isCommonXSeries() & !this.isByCategory()) 
		{
			if (this.isCumulative()) 
			{
				// new cumulative Y value

				switch (type_val)
				{
				case ChartDataSource.DATA_TYPE_POINT:
				{
					ILocation pvalue=Cast.asPoint(scope, o); 
					myserie.addxysvalue(scope,
							getDataset().getXSeriesValues().get(getDataset().getXSeriesValues().size()-1),
							pvalue.getX(),
							pvalue.getY(),
							chartCycle,barvalues,listvalue);
					break;
				}
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N:
				{
					IList lvalue=Cast.asList(scope, o); 
					if (lvalue.length(scope)==0)
					{
						
					}
					if (lvalue.length(scope)==1)
					{
						myserie.addxyvalue(scope,
								getDataset().getXSeriesValues().get(getDataset().getXSeriesValues().size()-1),
								Cast.asFloat(scope,  lvalue.get(0)),
								chartCycle,barvalues,listvalue);
					}
					if (lvalue.length(scope)>1)
					{
						myserie.addxysvalue(scope,
								getDataset().getXSeriesValues().get(getDataset().getXSeriesValues().size()-1),
								Cast.asFloat(scope,  lvalue.get(0)),Cast.asFloat(scope,  lvalue.get(1)),
								chartCycle,barvalues,listvalue);
					}
					break;
					
				}
				case ChartDataSource.DATA_TYPE_NULL:
				{
					//last value?
					break;
				}
				case ChartDataSource.DATA_TYPE_DOUBLE:
				default:
				{
					Double dvalue=Cast.asFloat(scope, o);
					myserie.addxyvalue(scope,getDataset().getXSeriesValues().get(getDataset().getXSeriesValues().size()-1),
							dvalue,chartCycle,barvalues,listvalue);
					
					break;
				}
				
				}
				
				
			}
			if (!this.isCumulative()) 
			{
				// new non cumulative y value
				// serie in the order of the dataset
				switch (type_val)
				{
				case ChartDataSource.DATA_TYPE_POINT:
				{
					ILocation pvalue=Cast.asPoint(scope, o); 
					myserie.addxysvalue(scope,
							getDataset().getXSeriesValues().get(0),
							pvalue.getX(),
							pvalue.getY(),
							chartCycle,barvalues,listvalue);
					
					break;
				}
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N:
				{
					IList l1value=Cast.asList(scope, o); 
					for (int n1=0; n1<l1value.size(); n1++)
					{
						Object o2=l1value.get(n1);
						while (n1>=getDataset().getXSeriesValues().size())
							getDataset().updateXValues(scope, chartCycle,l1value.size());
						myserie.addxyvalue(scope,
								getDataset().getXSeriesValues().get(n1),
								Cast.asFloat(scope,  o2),
								chartCycle,barvalues,listvalue);
					}
					break;
					
				}
				case ChartDataSource.DATA_TYPE_LIST_LIST_POINT:
				case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
				case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3:
				case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N:
				{
					IList l1value=Cast.asList(scope, o); 
					for (int n1=0; n1<l1value.size(); n1++)
					{
						Object o2=l1value.get(n1);
						IList lvalue=Cast.asList(scope, o2); 
						if (lvalue.length(scope)==1)
						{
							myserie.addxyvalue(scope,
									getDataset().getXSeriesValues().get(n1),
									Cast.asFloat(scope,  lvalue.get(0)),
									chartCycle,barvalues,listvalue);
							
						}
						if (lvalue.length(scope)>1)
						{
							myserie.addxysvalue(scope,
									getDataset().getXSeriesValues().get(n1),
									Cast.asFloat(scope,  lvalue.get(0)),
									Cast.asFloat(scope,  lvalue.get(1)),
									chartCycle,barvalues,listvalue);
						}
						
					}
					break;
					
				}
				case ChartDataSource.DATA_TYPE_NULL:
				{
					//last value?
					break;
				}
				case ChartDataSource.DATA_TYPE_DOUBLE:
				default:
				{
					Double dvalue=Cast.asFloat(scope, o);
					myserie.addxyvalue(scope,
							getDataset().getXSeriesValues().get(0),
							dvalue,
							chartCycle,barvalues,listvalue);
					break;
					
				}
				
				}
				
				
			}
			
		}

		//xy charts
		if (!this.isByCategory() &&  !this.isCommonXSeries()) 
		{

			if (this.isCumulative()) 
			{
				// new cumulative XY value			

				switch (type_val)
				{
				case ChartDataSource.DATA_TYPE_POINT:
				{
					ILocation pvalue=Cast.asPoint(scope, o); 
					myserie.addxysvalue(scope,
							pvalue.getX(),
							pvalue.getY(),
							pvalue.getZ(),
							chartCycle,barvalues,listvalue);
					
					break;
				}
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N:
				{
					IList lvalue=Cast.asList(scope, o); 
					if (lvalue.length(scope)<2)
					{
						
					}
					if (lvalue.length(scope)==2)
					{
						myserie.addxyvalue(scope,
								Cast.asFloat(scope,  lvalue.get(0)),
								Cast.asFloat(scope,  lvalue.get(1)),
								chartCycle,barvalues,listvalue);
					}
					if (lvalue.length(scope)>2)
					{
						myserie.addxysvalue(scope,
								Cast.asFloat(scope,  lvalue.get(0)),
								Cast.asFloat(scope,  lvalue.get(1)),
								Cast.asFloat(scope,  lvalue.get(2)),
								chartCycle,barvalues,listvalue);
					}
					break;
					
				}
				case ChartDataSource.DATA_TYPE_NULL:
				{
					//last value?
					break;
				}
				case ChartDataSource.DATA_TYPE_DOUBLE:
				default:
				{
					Double dvalue=Cast.asFloat(scope, o);
					myserie.addxyvalue(scope,
							getDataset().getXSeriesValues().get(getDataset().getXSeriesValues().size()-1),
							dvalue,chartCycle,barvalues,listvalue);
					
					break;
				}
				
				}
				
				
			}
			
			if (!this.isCumulative()) 
			{
				// new XY values			
				switch (type_val)
				{
				case ChartDataSource.DATA_TYPE_POINT:
				{
					ILocation pvalue=Cast.asPoint(scope, o); 
					myserie.addxysvalue(scope,
							pvalue.getX(),
							pvalue.getY(),
							pvalue.getZ(),
							chartCycle,barvalues,listvalue);
					
					break;
				}
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N:
				{
					IList lvalue=Cast.asList(scope, o); 
					if (lvalue.length(scope)<2)
					{
						
					}
					if (lvalue.length(scope)==2)
					{
						myserie.addxyvalue(scope,
								Cast.asFloat(scope,  lvalue.get(0)),
								Cast.asFloat(scope,  lvalue.get(1)),
								chartCycle,barvalues,listvalue);
					}
					if (lvalue.length(scope)>2)
					{
						myserie.addxysvalue(scope,
								Cast.asFloat(scope,  lvalue.get(0)),
								Cast.asFloat(scope,  lvalue.get(1)),
								Cast.asFloat(scope,  lvalue.get(2)),
								chartCycle,barvalues,listvalue);
					}
					break;
					
				}
				case ChartDataSource.DATA_TYPE_LIST_POINT:
				case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
				case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3:
				case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N:
				{
					IList l1value=Cast.asList(scope, o); 
					for (int n1=0; n1<l1value.size(); n1++)
					{
						Object o2=l1value.get(n1);
						IList lvalue=Cast.asList(scope, o2); 
						if (lvalue.length(scope)<2)
						{
							
						}
						if (lvalue.length(scope)==2)
						{
							myserie.addxyvalue(scope,
									Cast.asFloat(scope,  lvalue.get(0)),
									Cast.asFloat(scope,  lvalue.get(1)),
									chartCycle,barvalues,listvalue);
						}
						if (lvalue.length(scope)>2)
						{
							myserie.addxysvalue(scope,
									Cast.asFloat(scope,  lvalue.get(0)),
									Cast.asFloat(scope,  lvalue.get(1)),
									Cast.asFloat(scope,  lvalue.get(2)),
									chartCycle,barvalues,listvalue);
						}
						
					}
					break;
					
				}
				case ChartDataSource.DATA_TYPE_NULL:
				{
					//last value?
					break;
				}
				case ChartDataSource.DATA_TYPE_DOUBLE:
				default:
				{
					Double dvalue=Cast.asFloat(scope, o);
					myserie.addxyvalue(scope,getDataset().getXSeriesValues().get(getDataset().getXSeriesValues().size()-1),
							dvalue,chartCycle,barvalues,listvalue);
					break;
					
				}
				
				}
				
				
			}
			
		}
		
		//category charts
		if (this.isByCategory()) 
		{

			if (this.isCumulative()) 
			{
				// new cumulative category value
				// category is the last of the dataset

				switch (type_val)
				{
				case ChartDataSource.DATA_TYPE_POINT:
				{
					ILocation pvalue=Cast.asPoint(scope, o); 
					myserie.addcysvalue(scope,
							getDataset().getLastCategories(scope),
							pvalue.getX(),
							pvalue.getY(),
							chartCycle,barvalues,listvalue);
					break;
				}
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N:
				{
					IList lvalue=Cast.asList(scope, o); 
					if (lvalue.length(scope)==0)
					{
						
					}
					if (lvalue.length(scope)==1)
					{
						myserie.addcyvalue(scope,
								getDataset().getLastCategories(scope),
								Cast.asFloat(scope,  lvalue.get(0)),
								chartCycle,barvalues,listvalue);
					}
					if (lvalue.length(scope)>1)
					{
						myserie.addcysvalue(scope,
								getDataset().getLastCategories(scope),
								Cast.asFloat(scope,  lvalue.get(0)),Cast.asFloat(scope,  lvalue.get(1)),
								chartCycle,barvalues,listvalue);
					}
					break;
					
				}
				case ChartDataSource.DATA_TYPE_NULL:
				{
					//last value?
					break;
				}
				case ChartDataSource.DATA_TYPE_DOUBLE:
				default:
				{
					Double dvalue=Cast.asFloat(scope, o);
					myserie.addcyvalue(scope,
							getDataset().getLastCategories(scope),
							dvalue,chartCycle,barvalues,listvalue);
					
					break;
				}
				
				}
				
				
			}

					
			if (!this.isCumulative()) 
			{
				// new non cumulative category value
				// category in the order of the dataset
				switch (type_val)
				{
				case ChartDataSource.DATA_TYPE_POINT:
				{
					ILocation pvalue=Cast.asPoint(scope, o); 
					myserie.addcysvalue(scope,
							getDataset().getCategories(scope,0),
							pvalue.getX(),
							pvalue.getY(),
							chartCycle,barvalues,listvalue);
					
					break;
				}
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
				case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N:
				{
					IList l1value=Cast.asList(scope, o); 
					for (int n1=0; n1<l1value.size(); n1++)
					{
						Object o2=l1value.get(n1);
						myserie.addcyvalue(scope,
								getDataset().getCategories(scope,n1),
								Cast.asFloat(scope,  o2),
								chartCycle,barvalues,listvalue);
					}
					break;
					
				}
				case ChartDataSource.DATA_TYPE_LIST_LIST_POINT:
				case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
				case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3:
				case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N:
				{
					IList l1value=Cast.asList(scope, o); 
					for (int n1=0; n1<l1value.size(); n1++)
					{
						Object o2=l1value.get(n1);
						IList lvalue=Cast.asList(scope, o2); 
						if (lvalue.length(scope)==1)
						{
							myserie.addcyvalue(scope,
									getDataset().getCategories(scope,n1),
									Cast.asFloat(scope,  lvalue.get(0)),
									chartCycle,barvalues,listvalue);
							
						}
						if (lvalue.length(scope)>1)
						{
							myserie.addcysvalue(scope,
									getDataset().getCategories(scope,n1),
									Cast.asFloat(scope,  lvalue.get(0)),
									Cast.asFloat(scope,  lvalue.get(1)),
									chartCycle,barvalues,listvalue);
						}
						
					}
					break;
					
				}
				case ChartDataSource.DATA_TYPE_NULL:
				{
					//last value?
					break;
				}
				case ChartDataSource.DATA_TYPE_DOUBLE:
				default:
				{
					Double dvalue=Cast.asFloat(scope, o);
					myserie.addcyvalue(scope,
							getDataset().getCategories(scope,0),
							dvalue,
							chartCycle,barvalues,listvalue);
					break;
					
				}
				
				}
				
				
			}
			
		}		
			
		if (!this.isCumulative()) 
		{
			myserie.endupdate(scope);
			
		}


		
	}

	
	
	public LinkedHashMap<String,ChartDataSeries> getSeries()
	{
		return mySeries;
	}
	
	public void setValue(final IExpression value) {
		this.value = value;
	}



	public void setYErrValueExp(IScope scope, IExpression expval) {
		this.setUseYErrValues(true);
		this.valueyerr=expval;
		
	}
	
	public void setXErrValueExp(IScope scope, IExpression expval) {
		this.setUseXErrValues(true);
		this.valuexerr=expval;
		
	}

	public void setYMinMaxValueExp(IScope scope, IExpression expval) {
		this.setUseYMinMaxValues(true);
		this.valueyminmax=expval;
		

	}
	public void setMarkerShape(IScope scope, String stval) {
		//markerName is useless, for now creates/modifies the output
		uniqueMarkerName=stval;
		if (uniqueMarkerName==ChartDataStatement.MARKER_EMPTY)
		{
			this.setMarkerBool(scope, false);
		}
	}

	public void setMarkerSize(IScope scope, IExpression expval) {
		this.setUseSize(scope, true);
		this.sizeexp=expval;
		
	}

	public IExpression getSizeexp() {
		return sizeexp;
	}

	public void setColorExp(IScope scope, IExpression expval) {
		this.setUseColorExp(scope, true);
		this.colorexp=expval;
		
	}
	public boolean isUseSizeExp() {
		if (this.sizeexp==null) return false;
		return true;
	}

	
	public void setUseColorExp(IScope scope, boolean b) {
		this.useColorExp=b;
		
	}

	public boolean isUseColorExp() {
		return useColorExp;
	}

	public void setMarkerBool(IScope scope, boolean boolval) {
		useMarker=boolval;
	}

	public void setFillMarker(IScope scope, boolean boolval) {
		fillMarker=boolval;
	}

	public void setShowLine(IScope scope, boolean boolval) {
		showLine=boolval;
	}


	public void updatevalues(IScope scope, int lastUpdateCycle) {
		
		
	}

	public void setUseSize(IScope scope, boolean b) {
		this.setUseSize(b);
	}



}
