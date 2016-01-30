package msi.gama.outputs.layers.charts;

import java.util.ArrayList;
import java.util.HashMap;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

public class ChartDataSeries {
	
	ArrayList<Double> xvalues=new ArrayList<Double>(); //for xy charts
	ArrayList<Double> yvalues=new ArrayList<Double>();
	ArrayList<Double> svalues=new ArrayList<Double>(); //for marker sizes or 3d charts
	ArrayList<Double> xerrvaluesmax=new ArrayList<Double>();
	ArrayList<Double> yerrvaluesmax=new ArrayList<Double>();	
	ArrayList<Double> xerrvaluesmin=new ArrayList<Double>();
	ArrayList<Double> yerrvaluesmin=new ArrayList<Double>();	
	ArrayList<Double> yvaluemax=new ArrayList<Double>(); //for box and whisker
	ArrayList<Double> yvaluemin=new ArrayList<Double>(); //for box and whisker	

	GamaColor mycolor;



	//	HashMap<String,Object> serieParameters=new HashMap<String,Object>();	
	ChartDataSource mysource;
	ChartDataSet mydataset;

	
	String name;
	
	public ChartDataSet getDataset() {
		return mydataset;
	}

	public void setDataset(ChartDataSet mydataset) {
		this.mydataset = mydataset;
	}

	public String getName() {
		return name;
	}

	public ChartDataSource getMysource() {
		return mysource;
	}

	public void setMysource(ChartDataSource mysource) {
		this.mysource = mysource;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Comparable getSerieLegend(IScope scope) {
		// TODO Auto-generated method stub
		return name;
	}

	public String getSerieId(IScope scope) {
		// TODO Auto-generated method stub
		return name;
	}
	
	public GamaColor getMycolor() {
		return mycolor;
	}

	public void setMycolor(GamaColor mycolor) {
		this.mycolor = mycolor;
	}
	public boolean isUseYErrValues() {
		return this.getMysource().useYErrValues;
	}

	public void setUseYErrValues(boolean useYErrValues) {
		this.getMysource().useYErrValues = useYErrValues;
	}

	public boolean isUseXErrValues() {
		return this.getMysource().useXErrValues;
	}

	public void setUseXErrValues(boolean useXErrValues) {
		this.getMysource().useXErrValues = useXErrValues;
	}

	public boolean isUseYMinMaxValues() {
		return this.getMysource().useYMinMaxValues;
	}

	public void setUseYMinMaxValues(boolean useYMinMaxValues) {
		this.getMysource().useYMinMaxValues = useYMinMaxValues;
	}

	
	public ArrayList<Double> getXValues(IScope scope) {
		// TODO Auto-generated method stub
		return xvalues;
	}

	public ArrayList<Double> getYValues(IScope scope) {
		// TODO Auto-generated method stub
		return yvalues;
	}

	public ArrayList<Double> getSValues(IScope scope) {
		// TODO Auto-generated method stub
		return svalues;
	}

	public void addxysvalue(IScope scope, double dx, double dy, double ds, int date, HashMap barvalues, int listvalue) {

		svalues.add(ds);
		addxyvalue(scope,dx,dy,date,barvalues,listvalue);
		
	}
/*
	public void addxysvalue(double dx, double dy, double dz, int date) {
		// TODO Auto-generated method stub
		xvalues.add(dx);
		yvalues.add(dy);
		svalues.add(dz);
		this.getDataset().serieToUpdateBefore.put(this.getName(), date);
		
	}
*/
	public void clearValues(IScope scope) {
		// TODO Auto-generated method stub
		xvalues=new ArrayList<Double>(); //for xy charts
		yvalues=new ArrayList<Double>();
		svalues=new ArrayList<Double>(); //for marker sizes or 3d charts
		xerrvaluesmax=new ArrayList<Double>();
		yerrvaluesmax=new ArrayList<Double>();	
		xerrvaluesmin=new ArrayList<Double>();
		yerrvaluesmin=new ArrayList<Double>();	
		yvaluemax=new ArrayList<Double>(); //for box and whisker
		yvaluemin=new ArrayList<Double>(); //for box and whisker	
		
	}
	
	
	private Object getlistvalue(IScope scope, HashMap barvalues, String valuetype, int listvalue) {
		// TODO Auto-generated method stub
		if (!barvalues.containsKey(valuetype)) return null;
		boolean uselist=true;
		if (listvalue<0) uselist=false;
		Object oexp=barvalues.get(valuetype);
		Object o=oexp;
		if ( oexp instanceof IExpression ) o=((IExpression)oexp).value(scope);
		
		if (!uselist) return o;
		if (uselist)
		{
			if ( o instanceof GamaList )
			{
				IList ol=Cast.asList(scope, o);
				if (ol.size()<listvalue) return null;
				return ol.get(listvalue);
				
			}
			else
			{
				return o; 
			}
		}
		return null;
	}
		
	
	public void addxyvalue(IScope scope, double dx, double dy, int date, HashMap barvalues, int listvalue) {
		// TODO Auto-generated method stub
		xvalues.add(dx);
		yvalues.add(dy);
		if (barvalues.containsKey(IKeyword.COLOR))
		{
			Object o=getlistvalue(scope,barvalues,IKeyword.COLOR,listvalue);
			if (o!=null)
			{
				GamaColor col=Cast.asColor(scope, o);
				this.setMycolor(col);
			}
			
		}
		if (barvalues.containsKey(ChartDataStatement.MARKERSIZE))
		{
			Object o=getlistvalue(scope,barvalues,ChartDataStatement.MARKERSIZE,listvalue);
			if (o!=null)
			{
				if (svalues.size()>xvalues.size())
					svalues.remove(svalues.get(svalues.size()-1));
				svalues.add(Cast.asFloat(scope, o));
			}
			
		}
		if (this.isUseYErrValues())
		{
			Object o=getlistvalue(scope,barvalues,ChartDataStatement.YERR_VALUES,listvalue);
			if (o!=null)
			{
				if (o instanceof GamaList)
				{
					IList ol=Cast.asList(scope, o);
					if (ol.size()>1)
					{
						this.yerrvaluesmin.add(Cast.asFloat(scope, ol.get(0)));
						this.yerrvaluesmax.add(Cast.asFloat(scope, ol.get(1)));
						
					}
					else
					{
						this.yerrvaluesmin.add(dy-Cast.asFloat(scope, ol.get(0)));
						this.yerrvaluesmax.add(dy+Cast.asFloat(scope, ol.get(0)));					
					}
				}
				else
				{
					this.yerrvaluesmin.add(dy-Cast.asFloat(scope, o));
					this.yerrvaluesmax.add(dy+Cast.asFloat(scope, o));
					
				}
			}
			
		}
		if (this.isUseXErrValues())
		{
			Object o=getlistvalue(scope,barvalues,ChartDataStatement.XERR_VALUES,listvalue);
			if (o!=null)
			{
				if (o instanceof GamaList)
				{
					IList ol=Cast.asList(scope, o);
					if (ol.size()>1)
					{
						this.xerrvaluesmin.add(Cast.asFloat(scope, ol.get(0)));
						this.xerrvaluesmax.add(Cast.asFloat(scope, ol.get(1)));
						
					}
					else
					{
						this.xerrvaluesmin.add(dx-Cast.asFloat(scope, ol.get(0)));
						this.xerrvaluesmax.add(dx+Cast.asFloat(scope, ol.get(0)));					
					}
				}
				else
				{
					this.xerrvaluesmin.add(dx-Cast.asFloat(scope, o));
					this.xerrvaluesmax.add(dx+Cast.asFloat(scope, o));
					
				}
			}
			
		}

		this.getDataset().serieToUpdateBefore.put(this.getName(), date);
		
	}



}
