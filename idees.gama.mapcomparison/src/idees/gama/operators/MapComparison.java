package idees.gama.operators;



import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.filter.In;
import msi.gama.metamodel.topology.grid.GridTopology;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaMatrix;
import msi.gaml.types.IType;

public class MapComparison {

	@operator(value = { "fuzzy_kappa"}, content_type = IType.FLOAT)
	@doc(value = "fuzzy kappa indicator for 2 map comparisons: fuzzy_kappa(agents_list,list_vals1,list_vals2, similarity_per_agents,fuzzy_categories_matrix, fuzzy_distance)", examples = { "fuzzy_kappa([ag1, ag2, ag3, ag4, ag5], [1,3,2,8,2],[10,2,3,8,2], similarity_per_agents,[cat1,cat2,cat3],[[1,0,0],[0,1,0],[0,0,1]], 2)" })
	public static double fuzzyKappa(final IScope scope,
		final IContainer<Integer, IAgent> agents, final IList<Double> vals1, final IList<Double> vals2, final IList<Double> similarities,final List<Object> categories, final GamaMatrix<Double> fuzzycategories, final Double distance) {
		if (agents == null)
			return 1;
		int nb = agents.length(scope);
		if (nb  < 1)
			return 1;
		int nbCat = categories.size();
		GamaMap<Object, Integer> categoriesId = new GamaMap<Object, Integer>();
		for (int i = 0; i < nbCat; i++) {
			categoriesId.put(categories.get(i), i);
		}
		In filter = In.list(scope, agents);
		boolean[] sim = new boolean[nb]; 
		double[][] crispVector1 = new double[nb][nbCat];
		double[][] crispVector2 = new double[nb][nbCat];
		double[][] fuzzyVector1 = new double[nb][nbCat];
		double[][] fuzzyVector2 = new double[nb][nbCat];
		int[] X = new int[nbCat];
		int[] Y = new int[nbCat];
		for (int j = 0; j < nbCat; j++) {
			X[j] = 0;
			Y[j] = 0;
		}
		for (int i = 0; i < nb; i++) {
			Object val1 = vals1.get(i);
			Object val2 = vals2.get(i);
			
			int indexVal1 = categoriesId.get(val1);
			int indexVal2 = categoriesId.get(val2);
			X[indexVal1] += 1;
			Y[indexVal2] += 1;
			for (int j = 0; j < nbCat; j++) {
				crispVector1[i][j] = fuzzycategories.get(scope,indexVal1, j);
				crispVector2[i][j] = fuzzycategories.get(scope,indexVal2, j);
			}
			if (val1.equals(val2)) {
				sim[i] = true;
			} else {
				sim[i] = false;
			}
		}
		for (int j = 0; j < nbCat; j++) {
			X[j] /= nb;
			Y[j] /= nb;
		}
			
		for (int i = 0; i < nb; i++) {
			if (sim[i]) {
				similarities.add(1.0);
			} else {
				IAgent agent = agents.get(scope, i);
				double sizeNorm = agent.getPerimeter() / 4.0;
				GamaList<IAgent> neighbours = new GamaList<IAgent>(scope.getTopology().getNeighboursOf(scope, agent, distance, filter));
				GamaMap<IAgent, Double> distancesCoeff = new GamaMap<IAgent, Double>();
				distancesCoeff.put(agent, 1.0);
				for (IAgent ag : neighbours) {
					double euclidDist = agent.getLocation().euclidianDistanceTo(ag.getLocation());
					distancesCoeff.put(ag, 1 /(1.0 + (euclidDist)/sizeNorm)); 
				}
				for (int j = 0; j < nbCat; j++) {
					double max1 = 0.0;
					double max2 = 0.0;
					for (IAgent ag : neighbours)  {
						double val1 = crispVector1[i][j] * distancesCoeff.get(ag);
						double val2 = crispVector2[i][j] * distancesCoeff.get(ag);
						
						if (val1 > max1) max1 = val1; 
						if (val2 > max2) max2 = val2; 
					}
					fuzzyVector1[i][j] = max1;
					fuzzyVector1[i][j] = max2;
				}
				double s1Max = -1 * Double.MAX_VALUE;
				double s2Max = -1 * Double.MAX_VALUE;
				
				for(int j = 0; j < nbCat; j++) {
					double s1 = Math.min(fuzzyVector1[i][j], crispVector2[i][j]);
					double s2 = Math.min(fuzzyVector2[i][j], crispVector1[i][j]);
					if (s1 > s1Max) s1Max = s1;
					if (s2 > s2Max) s2Max = s2;
				}
				similarities.add(Math.min(s1Max,s2Max));
			}
		}
		float meanSimilarity = 0;
		
		
		for (Double val : similarities) {
			meanSimilarity += val;
		}
		meanSimilarity /= similarities.size();
		int nbRings = 0;
		GamaMap<double, V>
		float similarityExpected = 0;
		for (int j = 0; j < nbCat; j++) {
			similarityExpected += X[j] * Y[j];
		}
		return (meanSimilarity - similarityExpected) / (1 - similarityExpected);
	}
	
	

}
