package msi.gama.outputs.layers.charts;

import java.util.ArrayList;
import java.util.HashMap;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

public class ChartDataSourceList extends ChartDataSource {

	ArrayList<String> currentseries;
	IExpression nameExp;

	public ChartDataSourceList() {
		// TODO Auto-generated constructor stub
		super();
	}



	public IExpression getNameExp() {
		return nameExp;
	}


	public void setNameExp(final IScope scope, IExpression expval)
	{
		nameExp=expval;
	}
	

	public void setNames(final IScope scope, IList lval)
	{

	}

	public void updatevalues(IScope scope, int chartCycle) {
		super.updatevalues(scope, chartCycle);
		Object o=null;
		Object oname=this.getNameExp();
		HashMap<String,Object> barvalues=new HashMap<String,Object>();
		if (this.isUseYErrValues()) barvalues.put(ChartDataStatement.YERR_VALUES,this.getValueyerr().value(scope));
		if (this.isUseXErrValues()) barvalues.put(ChartDataStatement.XERR_VALUES,this.getValuexerr().value(scope));
		if (this.isUseYMinMaxValues()) barvalues.put(ChartDataStatement.XERR_VALUES,this.getValuexerr().value(scope));
		if (this.isUseSizeExp()) barvalues.put(ChartDataStatement.MARKERSIZE,this.getSizeexp().value(scope));
		if (this.isUseColorExp()) barvalues.put(IKeyword.COLOR,this.getColorexp().value(scope));
		
		//TODO check same length and list
		
		updateserielist(scope,chartCycle);
		
		int type_val=this.DATA_TYPE_NULL;
		if ( getValue() != null ) {
			o = getValue().value(scope);
		} 
		type_val=get_data_type(scope,o);
		
		
		if (o==null)
		{
// lastvalue??			
		}
		else		
		{
			// TODO Matrix case
			if  ( o instanceof GamaList )
			{
				IList lval=Cast.asList(scope, o); 
				
				if (lval.size()>0)
				{
					for (int i=0; i<lval.size(); i++)
					{
						Object no=lval.get(i);
						if (no!=null)
						{
							updateseriewithvalue(scope,mySeries.get(currentseries.get(i)),no,chartCycle,barvalues,i);
						}
					}
				}
			}
			

		}
		
		
	}
	



	private void updateserielist(IScope scope, int chartCycle) {
		Object oname = getNameExp().value(scope);
		Object o = getValue().value(scope);

		ArrayList<String> oldseries=currentseries;
		boolean somethingchanged=false;


		if (oname==null)
		{
			// lastvalue??			
		}
		else		
		{

			if  ( oname instanceof GamaList )
			{
				IList lvaln=Cast.asList(scope, oname); 
				currentseries=new ArrayList<String>();

				if (lvaln.size()>0)
				{

// value list case					
					IList lval=Cast.asList(scope, o); 
					
					for (int i=0; i<Math.min(lvaln.size(),lval.size()); i++)
					{
						Object no=lvaln.get(i);
						if (no!=null)
						{
							String myname=Cast.asString(scope, no);
							currentseries.add(i, myname);

							
							
							if (i>=oldseries.size() || (!oldseries.get(i).equals(myname)))
							{
								somethingchanged=true;
								if (oldseries.contains(myname))
								{
									//serie i was serie k before
								}
								else
								{
									//new serie
									newserie(scope,myname);
								}
							}
						}
					}
				}
				if (currentseries.size()!=oldseries.size())
				{
					somethingchanged=true;
				}
				if (somethingchanged)
				{
					for (int i=0; i<oldseries.size(); i++)
					{
						if (!currentseries.contains(oldseries.get(i)))
						{
							//series i deleted
							removeserie(scope,oldseries.get(i));
						}
						
					}
					ChartDataSeries s;
					
					for (int i=0; i<currentseries.size(); i++)
					{
						s=this.getDataset().getDataSeries(scope, currentseries.get(i));
						this.getDataset().series.remove(currentseries.get(i));
						this.getDataset().series.put(currentseries.get(i),s);
					}

				}
					
			}
		}
	}



	private void removeserie(IScope scope, String string) {
		// TODO Auto-generated method stub
		this.getDataset().removeserie(scope,string);
		
	}



	private void newserie(IScope scope, String myname) {
		// TODO Auto-generated method stub
		if (this.getDataset().getDataSeriesIds(scope).contains(myname))
		{
	//TODO
	//DO SOMETHING? create id and store correspondance
	//		System.out.println("Serie "+myname+"s already exists... Will replace old one!!");
		}
		ChartDataSeries myserie=myDataset.createOrGetSerie(scope,myname,this);
		mySeries.put(myname,myserie);

		
	}

	public void createInitialSeries(final IScope scope)
	{

		Object on=getNameExp().value(scope);

		if  ( on instanceof GamaList )
		{
			IList lval=Cast.asList(scope, on); 
			currentseries=new ArrayList<String>();
			
			if (lval.size()>0)
			{
				for (int i=0; i<lval.size(); i++)
				{
					Object no=lval.get(i);
					if (no!=null)
					{
						String myname=Cast.asString(scope, no);
						newserie(scope,myname);
						currentseries.add(i, myname);
					}
				}
			}
		}
		inferDatasetProperties(scope);		
	}
	
	public void inferDatasetProperties(final IScope scope)
	{
		Object o=null;
		int type_val=ChartDataSource.DATA_TYPE_NULL;
		if ( this.getValue() != null ) {
			o=this.getValue().value(scope);
			if  ( o instanceof GamaList && Cast.asList(scope, o).size()>0)
			{
				Object o2=Cast.asList(scope, o).get(0);
				type_val=get_data_type(scope,o2);
			}
				
		}

		getDataset().getOutput().setDefaultPropertiesFromType(scope,this,o,type_val);
		
		
	}










	
}
