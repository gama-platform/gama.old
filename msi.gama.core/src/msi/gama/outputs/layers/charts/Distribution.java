package msi.gama.outputs.layers.charts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation.GamlGridAgent;
import msi.gama.metamodel.topology.grid.GridTopology;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.GamaMatrix;
import msi.gama.util.path.IPath;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.GamlSpecies;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import msi.gama.util.*;

public class Distribution
{

	public static GamaMap computeDistrib(final IScope scope, final IList lvalue, final int nbBarres)
	{
		int len = lvalue.length(scope);
		double[] doublelist = new double[lvalue.length(scope)];
		int[] distribInts = new int[nbBarres];
		int[] distribParams = new int[2];
		String[] distribLegend = new String[nbBarres];

		for (int i = 0; i < lvalue.length(scope); i++) {
			doublelist[i] = Cast.asFloat(scope, lvalue.get(i));
		}
		Arrays.sort(doublelist);
		double min = doublelist[0];
		double max = doublelist[len - 1];
		int twoExponent = 0;
		int startMultiplier = 0;

		double newminInt = 0;
		double deuxpuissancek=0;

		if (min == max) {
			twoExponent=0;
			startMultiplier=(int)min;
			deuxpuissancek = (float) Math.pow(2, twoExponent);
			newminInt = (int)min;
			
		}

		else {


			double intermin = min;
/*
			if (min < 0) {
				intermin = intermin - 1;
			}*/
//			double intermax =  max + 1;
			double intermax =  max;

			float minInt = (float) intermin;
			float maxInt = (float) intermax;
				double N = Math.log10((maxInt - minInt) / ((double)(nbBarres - 1))) / Math.log10(2);
				System.out.println("Ncalc: maxmin: "+maxInt+"/"+minInt+" N "+N);
				twoExponent = (int) N ;
				deuxpuissancek = (float) Math.pow(2, twoExponent);
				newminInt = deuxpuissancek * (int) ((minInt / deuxpuissancek));
				startMultiplier = (int) ((minInt / deuxpuissancek));
//				System.out.println("Min "+min+" newmin "+newminInt+" startmult "+startMultiplier);
				if (newminInt>min)
				{
					newminInt = deuxpuissancek * (int) ((minInt / deuxpuissancek)-1);
					startMultiplier = (int) ((minInt / deuxpuissancek) - 1);
				}
				if ((newminInt+nbBarres*deuxpuissancek)<=max)
				{
					N=N+1;
					twoExponent = (int) N ;
					deuxpuissancek = (float) Math.pow(2, twoExponent);
					newminInt = deuxpuissancek * (int) ((minInt / deuxpuissancek));
					startMultiplier = (int) ((minInt / deuxpuissancek));
					if (newminInt>min)
					{
						newminInt = deuxpuissancek * (int) ((minInt / deuxpuissancek)-1);
						startMultiplier = (int) ((minInt / deuxpuissancek) - 1);
					}
				}
//				System.out.println(" "+maxInt+"/"+minInt+" N "+N+ " twoexp "+twoExponent+" maxv "+(newminInt+nbBarres*deuxpuissancek));


		}

		
		  
		  double preval = newminInt;
		  double postval=0;
		  int nba=0;
		  int nbaprec=0;
				for(int i=0;i<nbBarres;i++) {
					if (i!=0) {
						preval = (double) (preval + deuxpuissancek);
					}
					postval = (double) (preval + deuxpuissancek);	
					while (nba<len && doublelist[nba]<postval)
					{
						nba++;
					}
					
					distribInts[i]=nba-nbaprec;
					nbaprec=nba;
					distribLegend[i]="["+preval+":"+postval+"]";
				}


		 
		distribParams[0] = twoExponent;
		distribParams[1] = startMultiplier;

		final GamaMap<String, Object> result = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
		final IList vallist = GamaListFactory.create(scope, Types.INT, distribInts);
		final IList parlist = GamaListFactory.create(scope, Types.INT, distribParams);
		final IList leglist = GamaListFactory.create(scope, Types.STRING, distribLegend);
		result.addValueAtIndex(scope, "values", vallist);
		result.addValueAtIndex(scope, "legend", leglist);
		result.addValueAtIndex(scope, "parlist", parlist);

		return result;
		
	}
	
	@operator(value = {"distribution_of" }, 
			can_be_const = false, iterator = true, 
//			index_type = ITypeProvider.SECOND_CONTENT_TYPE, 
					index_type = IType.STRING, 
			content_type = IType.LIST, category = {IOperatorCategory.STATISTICAL }, 
			concept = { IConcept.STATISTIC, IConcept.CHART })
	@doc(value = "Discretize a list of values into n bins (computes the bins from a numerical variable into n (default 10) bins. Returns a distribution map with the values (values key), the interval legends (legend key), the distribution parameters (params keys, for cumulative charts).", comment = "", examples = {
			@example(value = "distribution_of([1,1,2,12.5],10)", equals = "map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])", isExecutable = false) }, see = "as_map")

	public static GamaMap DistributionOf(final IScope scope, final IContainer values, final Integer nbbars) throws GamaRuntimeException {

		if (values == null) {
			return GamaMapFactory.create(Types.STRING, Types.LIST);
		}
		IList lvalue = Cast.asList(scope, values);
		if (lvalue.length(scope) < 1)
			return GamaMapFactory.create(Types.STRING, Types.LIST);

		int nbBarres = 10;
			nbBarres=nbbars.intValue();

		return computeDistrib(scope,lvalue,nbBarres);

	}

	@operator(value = {"distribution_of" }, 
			can_be_const = false, iterator = true, 
//			index_type = ITypeProvider.SECOND_CONTENT_TYPE, 
					index_type = IType.STRING, 
			content_type = IType.LIST, category = {IOperatorCategory.STATISTICAL }, 
			concept = { IConcept.STATISTIC, IConcept.CHART })
	@doc(value = "Discretize a list of values into n bins (computes the bins from a numerical variable into n (default 10) bins. Returns a distribution map with the values (values key), the interval legends (legend key), the distribution parameters (params keys, for cumulative charts).", 
			masterDoc = true,
			comment = "", examples = {
			@example(value = "distribution_of([1,1,2,12.5])", equals = "map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])", isExecutable = false) }, see = "as_map")

	public static GamaMap DistributionOf(final IScope scope, final IContainer values) throws GamaRuntimeException {

		if (values == null) {
			return GamaMapFactory.create(Types.STRING, Types.LIST);
		}
		IList lvalue = Cast.asList(scope, values);
		if (lvalue.length(scope) < 1)
			return GamaMapFactory.create(Types.STRING, Types.LIST);

		int nbBarres = 10;

		return computeDistrib(scope,lvalue,nbBarres);

	}
	
}
