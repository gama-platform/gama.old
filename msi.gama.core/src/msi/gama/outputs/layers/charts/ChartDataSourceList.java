package msi.gama.outputs.layers.charts;

import java.util.ArrayList;

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
							updateseriewithvalue(scope,mySeries.get(currentseries.get(i)),no,chartCycle);
						}
					}
				}
			}
			

		}
		
		
	}
	
	
	private void updateseriewithvalue(IScope scope, ChartDataSeries myserie, Object o, int chartCycle) {

		
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
					myserie.addxyvalue(getDataset().getXSeriesValues().get(getDataset().getXSeriesValues().size()-1),
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
				myserie.addxyvalue(getDataset().getXSeriesValues().get(getDataset().getXSeriesValues().size()-1),
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
					myserie.addxyvalue(0.0,Cast.asFloat(scope,  lvalue.get(0)),chartCycle);

				}
				if (lvalue.length(scope)==2)
				{
					myserie.addxyvalue(Cast.asFloat(scope,  lvalue.get(0)),
							Cast.asFloat(scope,  lvalue.get(1)),chartCycle);
				}
				if (lvalue.length(scope)>2)
				{
					myserie.addxysvalue(Cast.asFloat(scope,  lvalue.get(0)),
							Cast.asFloat(scope,  lvalue.get(1)),
							Cast.asFloat(scope,  lvalue.get(2)),chartCycle);
				}
				}
			else
			if ( o instanceof GamaPoint ) { 
				ILocation pvalue=Cast.asPoint(scope, o); 
				myserie.addxyvalue(
						pvalue.getX(),
						pvalue.getY(),
						chartCycle);
				}
			else
			{
				Double dvalue=Cast.asFloat(scope, o);
				myserie.addxyvalue(0.0,dvalue,chartCycle);
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
					myserie.addxyvalue(0.0,Cast.asFloat(scope,  l1value.get(0)),chartCycle);

				}
				if (l1value.length(scope)>1)
				{
					myserie.addxyvalue(Cast.asFloat(scope,  l1value.get(0)),
							Cast.asFloat(scope,  l1value.get(1)),chartCycle);
				}
				}
			else
			if ( o instanceof GamaPoint ) { 
				ILocation pvalue=Cast.asPoint(scope, o); 
				myserie.addxyvalue(
						pvalue.getX(),
						pvalue.getY(),
						chartCycle);
				}
			else
			{
				Double dvalue=Cast.asFloat(scope, o);
				myserie.addxyvalue(0.0,dvalue,chartCycle);
			}
			
		}
				
	}



	private void updateserielist(IScope scope, int chartCycle) {
		Object oname = getNameExp().value(scope);

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
				IList lval=Cast.asList(scope, oname); 
				currentseries=new ArrayList<String>();

				if (lval.size()>0)
				{
					for (int i=0; i<lval.size(); i++)
					{
						Object no=lval.get(i);
						if (no!=null)
						{
							String myname=Cast.asString(scope, no);
							currentseries.add(i, myname);

							
							
							if (!oldseries.get(i).equals(myname))
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
						}
						
					}

				}
					
			}
		}
	}



	private void newserie(IScope scope, String myname) {
		// TODO Auto-generated method stub
		if (this.getDataset().getDataSeriesIds(scope).contains(myname))
		{
	//TODO
	//DO SOMETHING! create id and store correspondance
	System.out.println("Serie "+myname+"s already exists... Will replace old one!!");
		}
		ChartDataSeries myserie=new ChartDataSeries();
		myserie.setMysource(this);
		myserie.setDataset(getDataset());
		myserie.setName(myname);

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
			if  ( o instanceof GamaList )
			{
				Object o2=Cast.asList(scope, o).get(0);
				type_val=get_data_type(scope,o2);
			}
				
		}

		getDataset().getOutput().setDefaultPropertiesFromType(scope,this,o,type_val);
		
		
	}
	
}
