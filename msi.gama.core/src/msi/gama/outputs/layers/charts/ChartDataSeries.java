package msi.gama.outputs.layers.charts;

import java.util.ArrayList;
import java.util.HashMap;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

public class ChartDataSeries {
	
	ArrayList<Double> xvalues=new ArrayList<Double>(); //for xy charts
	ArrayList<Double> yvalues=new ArrayList<Double>();
	ArrayList<Double> svalues=new ArrayList<Double>(); //for marker sizes or 3d charts
	ArrayList<Double> xerrvalues=new ArrayList<Double>();
	ArrayList<Double> yerrvalues=new ArrayList<Double>();	

//	HashMap<String,Object> serieParameters=new HashMap<String,Object>();	
	ChartDataSource mysource;
	ChartDataSet mydataset;
	boolean useDefaultSValues=true;
	
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

	public void addxyvalue(double dx, double dy, int date) {
		// TODO Auto-generated method stub
		xvalues.add(dx);
		yvalues.add(dy);
		this.getDataset().serieToUpdateBefore.put(this.getName(), date);
		
	}

	public void addxysvalue(double dx, double dy, double dz, int date) {
		// TODO Auto-generated method stub
		xvalues.add(dx);
		yvalues.add(dy);
		svalues.add(dz);
		this.getDataset().serieToUpdateBefore.put(this.getName(), date);
		
	}

}
