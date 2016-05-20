package msi.gama.outputs.layers.charts;

import java.util.HashMap;

import org.jfree.chart.renderer.AbstractRenderer;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.util.GAML;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gama.util.random.GamaRNG;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Random;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public class ChartDataSourceUnique extends ChartDataSource {

	String myname;

	public boolean cloneMe(IScope scope, int chartCycle,ChartDataSource source) {

		boolean res=super.cloneMe(scope, chartCycle, source);
		GamaColor col=new GamaColor(Random.opRnd(scope, 255),Random.opRnd(scope, 255),Random.opRnd(scope, 255),255);
		IExpression ncol=GAML.getExpressionFactory().createConst(col, Types.COLOR);
		this.colorexp=ncol;
		myname=((ChartDataSourceUnique)source).myname+"*";
		return res;
	}	
	
	public ChartDataSource getClone(IScope scope, int chartCycle) {
		ChartDataSourceUnique res=new ChartDataSourceUnique();
		res.cloneMe(scope, chartCycle, this);
		return res;
	}

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


	public void setLegend(final IScope scope, String stval)
	{
		myname=stval;
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
