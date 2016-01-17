package msi.gama.outputs.layers.charts;

import org.jfree.chart.renderer.AbstractRenderer;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

public class ChartDataSourceUnique extends ChartDataSource {

	String myname;
	GamaColor color;
	String markerName;
	
	public ChartDataSeries getMyserie() {
		return mySeries.get(getName());
	}

	public String getName() {
		return myname;
	}

	public void setName(String name) {
		this.myname = name;
	}	
	
	public ChartDataSourceUnique() {
		// TODO Auto-generated constructor stub
	}

	public void setStyle(final IScope scope, String stval)
	{
		
	}
	
	public void setValueExp(final IScope scope, IExpression expval)
	{
		value=expval;
	}
	
	public void setLegend(final IScope scope, String stval)
	{
		myname=stval;
	}

	public void setMarkerShape(IScope scope, String stval) {
		// TODO Auto-generated method stub
		markerName=stval;
		this.getDataset().getOutput().setSerieMarkerShape(scope,this.getName(),stval);
	}
	
	
	
	public void updatevalues(IScope scope, int chartCycle) {
		super.updatevalues(scope, chartCycle);
		Object o=null;
		if ( getValue() != null ) {
			o = value.value(scope);
		} 
//		else {
//			o = lastvalue; should keep a last value option??
//		}
		
		// TODO instead (?): function to deduce format and then use deduced format to extract values
				
		if (o==null)
		{
// lastvalue??			
		}
		else		
		{
			// new cumulative Y value
			if (this.isCumulative() && !this.isByCategory() &&  this.isCommonXSeries()) 
			{
				if ( o instanceof GamaList ) { 
					IList lvalue=Cast.asList(scope, o); 
					if (lvalue.length(scope)==0)
					{
						
					}
					if (lvalue.length(scope)>=1)
					{
						this.getMyserie().addxyvalue(getDataset().getXSeriesValues().get(getDataset().getXSeriesValues().size()-1),
								Cast.asFloat(scope,  lvalue.get(0)),chartCycle);
					}
					}
				else
				if ( o instanceof GamaPoint ) { 
					ILocation pvalue=Cast.asPoint(scope, o); 
				}
				else
				{
					Double dvalue=Cast.asFloat(scope, o);
					this.getMyserie().addxyvalue(getDataset().getXSeriesValues().get(getDataset().getXSeriesValues().size()-1),
							dvalue,chartCycle);
			}
				
			}

			// new cumulative XY value			
			if (this.isCumulative() && !this.isByCategory() &&  !this.isCommonXSeries())
			{
				if ( o instanceof GamaList ) { 
					IList lvalue=Cast.asList(scope, o); 
					if (lvalue.length(scope)==0)
					{
						
					}
					if (lvalue.length(scope)==1)
					{
						this.getMyserie().addxyvalue(0.0,Cast.asFloat(scope,  lvalue.get(0)),chartCycle);

					}
					if (lvalue.length(scope)>1)
					{
						this.getMyserie().addxyvalue(Cast.asFloat(scope,  lvalue.get(0)),
								Cast.asFloat(scope,  lvalue.get(1)),chartCycle);
					}
					}
				else
				if ( o instanceof GamaPoint ) { 
					ILocation pvalue=Cast.asPoint(scope, o); 
					this.getMyserie().addxyvalue(
							pvalue.getX(),
							pvalue.getY(),
							chartCycle);
					}
				else
				{
					Double dvalue=Cast.asFloat(scope, o);
					this.getMyserie().addxyvalue(0.0,dvalue,chartCycle);
			}
				
			}

			// new XY value
			if (!this.isCumulative() && !this.isByCategory() &&  !this.isCommonXSeries())
			{
				if ( o instanceof GamaList ) { 
					IList l1value=Cast.asList(scope, o); 
					if (l1value.length(scope)==0)
					{
						
					}
					else
					{
						Object o2=l1value.get(0);
						// list of list
						if ( o2 instanceof GamaList ) { 
							
							
						}

						
						
						
					}
					
					if (l1value.length(scope)==1)
					{
						this.getMyserie().addxyvalue(0.0,Cast.asFloat(scope,  l1value.get(0)),chartCycle);

					}
					if (l1value.length(scope)>1)
					{
						this.getMyserie().addxyvalue(Cast.asFloat(scope,  l1value.get(0)),
								Cast.asFloat(scope,  l1value.get(1)),chartCycle);
					}
					}
				else
				if ( o instanceof GamaPoint ) { 
					ILocation pvalue=Cast.asPoint(scope, o); 
					this.getMyserie().addxyvalue(
							pvalue.getX(),
							pvalue.getY(),
							chartCycle);
					}
				else
				{
					Double dvalue=Cast.asFloat(scope, o);
					this.getMyserie().addxyvalue(0.0,dvalue,chartCycle);
			}
				
			}
			
		}
		
		
	}

	public void inferDatasetProperties(final IScope scope,ChartDataSeries myserie)
	{
		Object o=null;
		if ( this.getValue() != null ) {
			o = this.getValue().value(scope);
		}

		//by default
		
		switch (getDataset().getOutput().getType()) {
		case ChartOutput.SERIES_CHART: {
			this.setCumulative(true);
			break;
		}
		case ChartOutput.PIE_CHART: {
			this.setCumulative(false);
			break;
		}
		case ChartOutput.HISTOGRAM_CHART: {
			this.setCumulative(true);
			break;
		}
		case ChartOutput.XY_CHART: {
			this.setCumulative(true);
			break;
		}
		case ChartOutput.SCATTER_CHART: {
			this.setCumulative(true);
			break;
		}
		case ChartOutput.BOX_WHISKER_CHART: {
			this.setCumulative(true);
			break;
		}
		default:
		{
			this.setCumulative(false);
		}
		}		

		if (this.getValue() != null && o!=null) //infer from initial data structure
		{
			switch (getDataset().getOutput().getType()) {
			case ChartOutput.SERIES_CHART: {
				if ( o instanceof GamaList ) { this.setCumulative(false); }
				break;
			}
			case ChartOutput.XY_CHART: {
				if ( o instanceof GamaList ) 
					if ( Cast.asList(scope, o).size()>2 ) { this.setCumulative(false); }
				break;
			}
			case ChartOutput.SCATTER_CHART: {
				if ( o instanceof GamaList ) 
					if ( Cast.asList(scope, o).size()>2 ) { this.setCumulative(false); }
				break;
			}
			case ChartOutput.PIE_CHART: {
				this.setCumulative(false);
				break;
			}
			case ChartOutput.HISTOGRAM_CHART: {
				if ( o instanceof GamaList ) { this.setCumulative(false); }
				break;
			}
			case ChartOutput.BOX_WHISKER_CHART: {
				if ( o instanceof GamaList ) 
					if ( Cast.asList(scope, o).size()>2 ) { this.setCumulative(false); }
				break;
			}
			}		
			
		}
		
		
	}
	
	public void createInitialSeries(final IScope scope)
	{
		ChartDataSeries myserie=new ChartDataSeries();

		myserie.setMysource(this);

		myserie.setDataset(getDataset());
		
		inferDatasetProperties(scope,myserie);		
		
		String myname=getName();


		myserie.setName(myname);
				
		mySeries.put(myname,myserie);
	}

}
