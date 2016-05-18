package msi.gama.outputs.layers.charts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;


public class ChartDataSet {

	ArrayList<ChartDataSource> sources=new ArrayList<ChartDataSource>();
	LinkedHashMap<String,ChartDataSeries> series=new LinkedHashMap<String,ChartDataSeries>();
	LinkedHashMap<String,ChartDataSeries> deletedseries=new LinkedHashMap<String,ChartDataSeries>();
	ArrayList<String> categories=new ArrayList<String>(); //for categories datasets
	ArrayList<Double> XSeriesValues=new ArrayList<Double>(); //for series
	ArrayList<String> Ycategories=new ArrayList<String>(); //for Y categories datasets
	ArrayList<Double> YSeriesValues=new ArrayList<Double>(); //for 3d series
	LinkedHashMap<String,Integer> serieCreationDate=new LinkedHashMap<String,Integer>();

	IExpression xsource; //to replace default common X Source
	IExpression ysource; //to replace default common X Labels
	IExpression xlabels; //to replace default common Y Source
	IExpression ylabels; //to replace default common Y Labels
	
	LinkedHashMap<String,Integer> serieRemovalDate=new LinkedHashMap<String,Integer>();
	LinkedHashMap<String,Integer> serieToUpdateBefore=new LinkedHashMap<String,Integer>();
	ChartOutput mainoutput;
	int resetAllBefore=0;
	
	String defaultstyle=IKeyword.DEFAULT;

	boolean useXSource=false;
	boolean useXLabels=false;
	boolean useYSource=false;
	boolean useYLabels=false;
	boolean commonXSeries=false; // series
	boolean commonYSeries=false; // heatmap & 3d
	boolean byCategory=false; //histogram/pie
	boolean keepOldSeries=true; // keep old series or move to deleted (to keep history)
	
	public int getResetAllBefore() {
		return resetAllBefore;
	}

	public void setResetAllBefore(int resetAllBefore) {
		this.resetAllBefore = resetAllBefore;
	}	
	public boolean isKeepOldSeries() {
		return keepOldSeries;
	}

	public void setKeepOldSeries(boolean keepOldSeries) {
		this.keepOldSeries = keepOldSeries;
	}

	private ArrayList<String> getCategories() {
		return categories;
	}

	public String getCategories(IScope scope, int i) {
		if (categories.size()>i)
		{
			return categories.get(i);
			
		}
		else
		{
			for (int c=categories.size(); c<=i; c++)
			{
				this.categories.add("c"+c);
			}
			return categories.get(i);
		}
	}

	public String getLastCategories(IScope scope) {
		if (categories.size()>0)
		{
			return categories.get(categories.size()-1);
			
		}
		else
		{
				this.categories.add("c"+0);
				return categories.get(categories.size()-1);
		}
	}

	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}

	public ArrayList<Double> getXSeriesValues() {
		return XSeriesValues;
	}

	public void setXSeriesValues(ArrayList<Double> xSeriesValues) {
		XSeriesValues = xSeriesValues;
	}

	public boolean isByCategory() {
		return byCategory;
	}

	public void setByCategory(boolean byCategory) {
		this.byCategory = byCategory;
	}

	public boolean isCommonXSeries() {
		return commonXSeries;
	}

	public void setCommonXSeries(boolean temporalSeries) {
		this.commonXSeries = temporalSeries;
	}
	public LinkedHashMap<String, Integer> getSerieCreationDate() {
		return serieCreationDate;
	}

	public LinkedHashMap<String, Integer> getSerieRemovalDate() {
		return serieRemovalDate;
	}

	public ChartDataSet()
	{
		
	}

	public ChartOutput getOutput()
	{
		return mainoutput;
	}
	
	public void setOutput(ChartOutput output)
	{
		mainoutput=output;
		this.defaultstyle=output.getStyle();
	}

	public void addNewSerie(String id,ChartDataSeries serie,int date)
	{
		if (series.keySet().contains(id))
		{
			// Series name already present, should do something.... Don't change creation date?
			series.put(id,serie);
//			serieCreationDate.put(id, date);
			serieToUpdateBefore.put(id, date);
			serieRemovalDate.put(id, -1);
		}
		else
		{
			series.put(id,serie);
			serieCreationDate.put(id, date);
			serieToUpdateBefore.put(id, date);
			serieRemovalDate.put(id, -1);
			
		}
		
	}
	
	public ArrayList<ChartDataSource> getSources() {
		return sources;
	}

	public void addDataSource(ChartDataSource source)
	{
		sources.add(source);
		LinkedHashMap<String,ChartDataSeries> newseries=source.getSeries();
		for (Entry<String, ChartDataSeries> entry : newseries.entrySet())
		{
				//should do something... raise an exception?
			addNewSerie(entry.getKey(), entry.getValue(),-1);
		}
//		series.putAll(source.getSeries());
	}

	public boolean doResetAll(IScope scope, int lastUpdateCycle) {
		// TODO Auto-generated method stub
		if (resetAllBefore>lastUpdateCycle)
			return true;
		return false;
	}

	public Set<String> getDataSeriesIds(IScope scope) {
		// TODO Auto-generated method stub
		return series.keySet();
	}

	public ChartDataSeries getDataSeries(IScope scope, String serieid) {
		// TODO Auto-generated method stub
		return series.get(serieid);
	}


	public void updatedataset(IScope scope, int chartCycle) {
		// TODO Auto-generated method stub
		updateXValues(scope, chartCycle);
		for (ChartDataSource source : sources)
		{
			source.updatevalues(scope,chartCycle);
		}
		
	}

	public void updateXValues(IScope scope, int chartCycle, int targetNb)
	{
		Object xval,xlab;
		if (this.useXSource || this.useXLabels)
		{
			
			if (this.useXSource)
			{
				xval=xsource.resolveAgainst(scope).value(scope);
			}
			else
			{
				xval=xlabels.resolveAgainst(scope).value(scope);
			}
			if (this.useXLabels)
			{
				xlab=xlabels.resolveAgainst(scope).value(scope);
			}
			else
			{
				xlab=xsource.resolveAgainst(scope).value(scope);				
			}
			
			if (xval instanceof GamaList)
			{
				IList xv2=Cast.asList(scope, xval);
				IList xl2=Cast.asList(scope, xlab);

				if (this.useXSource && xv2.size()>0 && xv2.get(0) instanceof Number)
				{
					XSeriesValues=new ArrayList<Double>();
					categories=new ArrayList<String>();
					for (int i=0; i<xv2.size(); i++)
					{
						XSeriesValues.add(new Double(Cast.asFloat(scope, xv2.get(i))));
						categories.add(Cast.asString(scope, xl2.get(i)));
						
					}
				
					
				}
				else
				{
					if (xv2.size()>categories.size())
					{
						categories=new ArrayList<String>();
						for (int i=0; i<xv2.size(); i++)
						{
							if (i>=XSeriesValues.size())
							{
								XSeriesValues.add(new Double(getCycleOrPlusOneForBatch(scope,chartCycle)));								
							}
							categories.add(Cast.asString(scope, xl2.get(i)));
						}
						
					}
					
				}				
				if (xv2.size()<targetNb)
				{
				throw GamaRuntimeException.error(
						"The x-serie length ("+xv2.size()+
						") should NOT be shorter than any series length (" + 
								targetNb+") !"
							, scope);
				}

			}
			else 
				{
				if (this.useXSource && xval instanceof Number )
				{
					double dvalue=Cast.asFloat(scope, xval);
					String lvalue=Cast.asString(scope, xlab);
					XSeriesValues.add(new Double(dvalue));
					categories.add(lvalue);				
				}
			if (targetNb==-1)
					targetNb=XSeriesValues.size()+1;
				while (XSeriesValues.size()<targetNb)
			{
					XSeriesValues.add(new Double(getCycleOrPlusOneForBatch(scope,chartCycle)));
					categories.add(Cast.asString(scope, xlab));
			}
			}
			
		}

		
		if (!this.useXSource && !this.useXLabels)
		{
			if (targetNb==-1)
				targetNb=XSeriesValues.size()+1;
			while (XSeriesValues.size()<targetNb)
		{
			addCommonXValue(scope,getCycleOrPlusOneForBatch(scope,chartCycle));
		}
			
		}

		

		
	}
	
	public void updateXValues(IScope scope, int chartCycle)
	{
		updateXValues(scope, chartCycle, -1);

		
	}
	
	public int getCycleOrPlusOneForBatch(IScope scope,int chartcycle)
	{
		if (this.XSeriesValues.contains((double)chartcycle))
			return (int)(XSeriesValues.get(XSeriesValues.size()-1)).doubleValue()+1;
		return chartcycle;
	}
	
	
	private void addCommonXValue(IScope scope, int chartCycle) {
		// TODO Auto-generated method stub
		XSeriesValues.add(new Double(chartCycle));
		categories.add(""+chartCycle);
		
	}

	public int getDate(IScope scope)
	{
		return scope.getClock().getCycle();
	}

	public void setXSource(IScope scope, IExpression data) {
		// TODO Auto-generated method stub
		this.useXSource=true;
		this.xsource=data;
	}
	
	public void setXLabels(IScope scope, IExpression data) {
		// TODO Auto-generated method stub
		this.useXLabels=true;
		this.xlabels=data;
	}
	
	public void setYSource(IScope scope, IExpression data) {
		// TODO Auto-generated method stub
		this.useYSource=true;
		this.ysource=data;
	}
	
	public void setYLabels(IScope scope, IExpression data) {
		// TODO Auto-generated method stub
		this.useYLabels=true;
		this.ylabels=data;
	}
	
	
	public ChartDataSeries createOrGetSerie(IScope scope, String id, ChartDataSourceList source) {
		// TODO Auto-generated method stub
		if (series.keySet().contains(id))
		{
			return series.get(id);
		}
		else
		{
			if (deletedseries.keySet().contains(id))
			{
				ChartDataSeries myserie=deletedseries.get(id);
				deletedseries.remove(id);
				this.serieRemovalDate.put(id, -1);
				myserie.setMysource(source);
				myserie.setDataset(this);
				myserie.setName(id);
				addNewSerie(id, myserie, getDate(scope));
				return myserie;
			}
			else
			{
			ChartDataSeries myserie=new ChartDataSeries();
			myserie.setMysource(source);
			myserie.setDataset(this);
			myserie.setName(id);
			addNewSerie(id, myserie, getDate(scope));
			return myserie;
			}
			
		}

	}

	public void removeserie(IScope scope, String id) {
		// TODO Auto-generated method stub
		ChartDataSeries serie=this.getDataSeries(scope, id);
		if (serie!=null)
		{
		    this.deletedseries.put(id, serie);
		    this.series.remove(id);
		    this.serieRemovalDate.put(id, this.getDate(scope));
			serieToUpdateBefore.put(id, this.getDate(scope));
		    this.deletedseries.put(id, serie);
		    this.setResetAllBefore(this.getDate(scope));
			
		}
	}

	public void setStyle(IScope scope, String stval) {
		// TODO Auto-generated method stub
		defaultstyle=stval;
	}


	public String getStyle(IScope scope) {
		// TODO Auto-generated method stub
		return defaultstyle;
	}



	
}
