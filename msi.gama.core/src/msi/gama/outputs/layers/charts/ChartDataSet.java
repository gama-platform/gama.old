package msi.gama.outputs.layers.charts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;


public class ChartDataSet {

	ArrayList<ChartDataSource> sources=new ArrayList<ChartDataSource>();
	LinkedHashMap<String,ChartDataSeries> series=new LinkedHashMap<String,ChartDataSeries>();
	ArrayList<String> categories=new ArrayList<String>(); //for categories datasets
	ArrayList<Double> XSeriesValues=new ArrayList<Double>(); //for series
	LinkedHashMap<String,Integer> serieCreationDate=new LinkedHashMap<String,Integer>();

	LinkedHashMap<String,Integer> serieRemovalDate=new LinkedHashMap<String,Integer>();
	LinkedHashMap<String,Integer> serieToUpdateBefore=new LinkedHashMap<String,Integer>();
	ChartOutput mainoutput;
	int resetAllBefore=0;
	
	boolean commonXSeries=false;
	boolean byCategory=false;
	
	public ArrayList<String> getCategories() {
		return categories;
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
	}

	public void addNewSerie(String id,ChartDataSeries serie,int date)
	{
		if (series.keySet().contains(id))
		{
			System.out.println("Series name already present, should do something....");
		}
		series.put(id,serie);
		serieCreationDate.put(id, date);
		serieToUpdateBefore.put(id, date);
		serieRemovalDate.put(id, -1);
		
	}
	
	public void addDataSource(ChartDataSource source)
	{
		sources.add(source);
		LinkedHashMap<String,ChartDataSeries> newseries=source.getSeries();
		for (Entry<String, ChartDataSeries> entry : newseries.entrySet())
		{
				//should do something... raise an exception?
			addNewSerie(entry.getKey(), entry.getValue(),0);
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
		addCommonXValue(scope,chartCycle);
		for (ChartDataSource source : sources)
		{
			source.updatevalues(scope,chartCycle);
		}
		
	}

	private void addCommonXValue(IScope scope, int chartCycle) {
		// TODO Auto-generated method stub
		XSeriesValues.add(new Double(chartCycle));
		categories.add(""+chartCycle);
		
	}


	
}
