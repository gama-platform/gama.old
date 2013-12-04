package idees.gama.operators;



import java.util.Collections;
import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.topology.filter.In;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Stats;
import msi.gaml.species.ISpecies;
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
		
		boolean[] sim = new boolean[nb]; 
		double[][] crispVector1 = new double[nb][nbCat];
		double[][] crispVector2 = new double[nb][nbCat];
		double[][] fuzzyVector1 = new double[nb][nbCat];
		double[][] fuzzyVector2 = new double[nb][nbCat];
		double[] X = new double[nbCat];
		double[] Y = new double[nbCat];
		
		computeXYCrispVector(scope, categories, vals1, vals2, fuzzycategories, nbCat, nb, crispVector1 ,crispVector2, X, Y, sim);
		double meanSimilarity = computeSimilarity(scope, distance, vals1, vals2, agents, nbCat, nb, crispVector1 ,crispVector2,sim,fuzzyVector1, fuzzyVector2,similarities);
			
		GamaList<Double> rings = new GamaList<Double>();
		Map<Double,Integer> ringsPn = new GamaMap<Double, Integer>();
		int nbRings = buildRings(scope,rings, ringsPn, agents);
		double similarityExpected = computeExpectedSim(nbCat, X, Y, nbRings, rings,ringsPn);
		return (meanSimilarity - similarityExpected) / (1 - similarityExpected);
	}
	
	private static double p(double dist, int a, int b,double[] X,double[] Y,Map<Double,Integer> ringsPn) {
		int n = 0;
		if (dist > 0.0) {
			n = ringsPn.get(dist);
		}
		return (1 - (1 - Math.pow(X[a],n))) * (1 - (1 - Math.pow(Y[b],n)));
	}
	
	private static double computeExpectedSim(int nbCat, double[] X, double[] Y,int nbRings, GamaList<Double> rings,Map<Double,Integer> ringsPn) {
		double similarityExpected = 0;
		for (int j = 0; j < nbCat; j++) {
			similarityExpected += X[j] * Y[j];
		}
		
		double dist = 0;
		for (int p = 0; p < nbRings; p++) {
			double dist1 = dist;
			dist = rings.get(p);
			double Mdi = Math.pow(2, (dist / (-2)));
			double Ei = 0;
			for(int a = 0; a < nbCat; a++) {
				double Ya = Y[a];
				for(int b = 0; b < nbCat; b++) {
					double Xb = X[b];
					int kro_delta = a == b ? 1 : 0;
					Ei = Ei + ((1 - kro_delta) * Ya * Xb * (p(dist, a,b,X,Y,ringsPn) - p(dist1, a,b,X,Y,ringsPn)));
				}
			} 
			similarityExpected = similarityExpected + (Mdi * Ei);
		}
		return similarityExpected;
	}
	
	private static double computeSimilarity(final IScope scope, final Double distance, final IList<Double> vals1, final IList<Double> vals2, final IContainer<Integer, IAgent> agents, int nbCat, int nb, double[][] crispVector1 ,double[][] crispVector2, boolean[] sim,double[][] fuzzyVector1, double[][] fuzzyVector2,final IList<Double> similarities){
		In filter = null;
		if (agents instanceof ISpecies) {
			final IPopulation pop = agents.first(scope).getPopulationFor((ISpecies) agents);
			filter = In.population(pop);
		}
		else 
			filter = In.list(scope, agents);
		GamaMap<IAgent, Integer> agsId = new GamaMap<IAgent, Integer>();
		for (int i = 0; i < agents.length(scope); i++) {
			agsId.put(agents.get(scope, i), i);
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
						int id = agsId.get(ag);
						double val1 = crispVector1[id][j] * distancesCoeff.get(ag);
						double val2 = crispVector2[id][j] * distancesCoeff.get(ag);
						
						if (val1 > max1) max1 = val1; 
						if (val2 > max2) max2 = val2; 
					}
					fuzzyVector1[i][j] = max1;
					fuzzyVector2[i][j] = max2;
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
		return meanSimilarity;
	}
		
	
	private static void computeXYCrispVector(final IScope scope, final List<Object> categories,final IList<Double> vals1, final IList<Double> vals2, final GamaMatrix<Double> fuzzycategories, int nbCat, int nb, double[][] crispVector1 ,double[][] crispVector2, double[] X, double[] Y, boolean[] sim){
		GamaMap<Object, Integer> categoriesId = new GamaMap<Object, Integer>();
		for (int i = 0; i < nbCat; i++) {
			categoriesId.put(categories.get(i), i);
		}
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
				crispVector1[i][j] = Cast.asFloat(scope, fuzzycategories.get(scope,indexVal1, j));
				crispVector2[i][j] = Cast.asFloat(scope,fuzzycategories.get(scope,indexVal2, j));
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
			
	}
	
	private static int buildRings(IScope scope, GamaList<Double> rings, Map<Double,Integer> ringsPn, IContainer<Integer, IAgent> agents) {
		
		GamaList<ILocation> locs = new GamaList<ILocation>();
		for (IAgent ag : agents) {
			locs.add(ag.getLocation());
		}
		ILocation centralLoc = (ILocation) Stats.getMean(scope,locs);
		
		for (IAgent ag : agents) {
			double dist = centralLoc.euclidianDistanceTo(ag.getLocation());
			if (dist == 0) continue;
			if (! rings.contains(dist)) {
				rings.add(dist);
				ringsPn.put(dist, 1);
			} else {
				ringsPn.put(dist, 1 + ringsPn.get(dist));
			}
		}
		Collections.sort(rings);
		
		for (int i = 1; i < rings.size(); i++) {
			double dist = rings.get(i);
			double dist1 = rings.get(i - 1);
			ringsPn.put(dist,ringsPn.get(dist) + ringsPn.get(dist1));
		}
		
		return rings.size();
	
	}
	
	

}
