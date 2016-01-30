package msi.gama.outputs.layers.charts;

import java.util.HashMap;

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
	
	public void setLegend(final IScope scope, String stval)
	{
		myname=stval;
	}

	public void setMarkerShape(IScope scope, String stval) {
		// TODO Auto-generated method stub
		//markerName is useless, for now creates/modifies the output
		uniqueMarkerName=stval;
//		this.getDataset().getOutput().setSerieMarkerShape(scope,this.getName(),stval);
	}
		
	
	public void updatevalues(IScope scope, int chartCycle) {
		super.updatevalues(scope, chartCycle);
		
		Object o=null;
		HashMap<String,Object> barvalues=new HashMap<String,Object>();
		if (this.isUseYErrValues()) barvalues.put(ChartDataStatement.YERR_VALUES,this.getValueyerr().value(scope));
		if (this.isUseXErrValues()) barvalues.put(ChartDataStatement.XERR_VALUES,this.getValueyerr().value(scope));
		if (this.isUseYMinMaxValues()) barvalues.put(ChartDataStatement.XERR_VALUES,this.getValuexerr().value(scope));
		if (this.isUseSizeExp()) barvalues.put(ChartDataStatement.MARKERSIZE,this.getSizeexp().value(scope));
		if (this.isUseColorExp()) barvalues.put(IKeyword.COLOR,this.getColorexp().value(scope));

		
		if ( getValue() != null ) {
			o = getValue().value(scope);
		} 
		
		if (o==null)
		{
// lastvalue??			
		}
		else		
		{
			
			updateseriewithvalue(scope,getMyserie(),o,chartCycle,barvalues,-1);
			

		}
		
			
	}

	public void inferDatasetProperties(final IScope scope,ChartDataSeries myserie)
	{
		Object o=null;
		if ( this.getValue() != null ) {
			o = this.getValue().value(scope);
		}

		int type_val=get_data_type(scope,o);
		//by default
		

		getDataset().getOutput().setDefaultPropertiesFromType(scope,this,o,type_val);
		
		//move to output objects:
/*		
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
	*/	
		
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
