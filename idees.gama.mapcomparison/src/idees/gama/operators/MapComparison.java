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

	@operator(value = { "kappa"}, content_type = IType.FLOAT)
	@doc(value = "kappa indicator for 2 map comparisons: kappa(list_vals1,list_vals2,categories). Reference: Cohen, J. A coefficient of agreement for nominal scales. Educ. Psychol. Meas. 1960, 20, 37–46. ", examples = { "kappa([cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2],[cat1,cat2,cat3])" })
	public static double kappa(final IScope scope,final IList<Object> vals1, final IList<Object> vals2, final List<Object> categories) {
		if (vals1 == null ||vals2 == null)
			return 1;
		int nb = vals1.size();
		if (nb  != vals2.size())
			return 0;
		int nbCat = categories.size();
		double[] X = new double[nbCat];
		double[] Y = new double[nbCat];
		double[][] contigency = new double[nbCat][nbCat];
		for (int j = 0; j < nbCat; j++) {
			X[j] = 0;
			Y[j] = 0;
			for (int k = 0; k < nbCat; k++) {
				contigency[j][k] = 0;
			}
		}
		
		GamaMap<Object, Integer> categoriesId = new GamaMap<Object, Integer>();
		for (int i = 0; i < nbCat; i++) {
			categoriesId.put(categories.get(i), i);
		}
		for (int i = 0; i < nb; i++) {
			Object val1 = vals1.get(i);
			Object val2 = vals2.get(i);
			int indexVal1 = categoriesId.get(val1);
			int indexVal2 = categoriesId.get(val2);
			X[indexVal1] += 1;
			Y[indexVal2] += 1;
			contigency[indexVal1][indexVal2] += 1;
		}
		for (int j = 0; j < nbCat; j++) {
			X[j] /= nb;
			Y[j] /= nb;
			for (int k = 0; k < nbCat; k++) {
				contigency[j][k] /= nb;
			}
		}
		double po = 0;
		double pe = 0;
		for (int i = 0; i < nbCat; i++) {
			po += contigency[i][i];
			pe += X[i] * Y[i];
		}
		return (po - pe) / (1 - pe);
	}
	
	@operator(value = { "kappa_sim"}, content_type = IType.FLOAT)
	@doc(value = "kappa simulation indicator for 2 map comparisons: kappa(list_valsInits,list_valsObs,list_valsSim, categories). Reference: van Vliet, J., Bregt, A.K. & Hagen-Zanker, A. (2011). Revisiting Kappa to account for change in the accuracy assessment of land-use change models, Ecological Modelling 222(8):1367–1375.", examples = { "kappa([cat1,cat1,cat2,cat2,cat2],[cat2,cat1,cat2,cat1,cat3],[cat2,cat1,cat2,cat3,cat3], [cat1,cat2,cat3])" })
	public static double kappaSimulation(final IScope scope,final IList<Object> valsInit, final IList<Object> valsObs, final IList<Object> valsSim, final List<Object> categories) {
		if (valsInit == null ||valsObs == null||valsSim == null)
			return 1;
		int nb = valsInit.size();
		if (nb  != valsObs.size() || nb  != valsSim.size())
			return 0;
		int nbCat = categories.size();
		double[] O = new double[nbCat];
		double[][] contigency = new double[nbCat][nbCat];
		double[][] contigencyOA = new double[nbCat][nbCat];
		double[][] contigencyOS = new double[nbCat][nbCat];
		for (int j = 0; j < nbCat; j++) {
			O[j] = 0;
			for (int k = 0; k < nbCat; k++) {
				contigency[j][k] = 0;
				contigencyOA[j][k] = 0;
				contigencyOS[j][k] = 0;
			}
		}
		GamaMap<Object, Integer> categoriesId = new GamaMap<Object, Integer>();
		for (int i = 0; i < nbCat; i++) {
			categoriesId.put(categories.get(i), i);
		}
		for (int i = 0; i < nb; i++) {
			Object val1 = valsObs.get(i);
			Object val2 = valsSim.get(i);
			Object valO = valsInit.get(i);
			int indexVal1 = categoriesId.get(val1);
			int indexVal2 = categoriesId.get(val2);
			int indexValO = categoriesId.get(valO);
			O[indexValO] += 1;
			contigency[indexVal1][indexVal2] += 1;
			contigencyOA[indexValO][indexVal1] += 1;
			contigencyOS[indexValO][indexVal2] += 1;
		}
		for (int j = 0; j < nbCat; j++) {
			for (int k = 0; k < nbCat; k++) {
				contigency[j][k] /= nb;
				contigencyOA[j][k] /= O[j];
				contigencyOS[j][k] /= O[j];
			}
			O[j] /= nb;
			
		}
		double po = 0;
		double pe = 0;
		for (int j = 0; j < nbCat; j++) {
			po += contigency[j][j];
			double sum = 0;
			for (int i = 0; i < nbCat; i++) {
				sum += (contigencyOA[j][i] * contigencyOS[j][i]);
			}
			pe += (O[j] * sum);
		}
		return (po - pe) / (1 - pe);
	}
	
	@operator(value = { "fuzzy_kappa"}, content_type = IType.FLOAT)
	@doc(value = "fuzzy kappa indicator for 2 map comparisons: fuzzy_kappa(agents_list,list_vals1,list_vals2, output_similarity_per_agents,categories,fuzzy_categories_matrix, fuzzy_distance). Reference: Visser, H., and T. de Nijs, 2006. The map comparison kit, Environmental Modelling & Software, 21:346–358.", examples = { "fuzzy_kappa([ag1, ag2, ag3, ag4, ag5],[cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2], similarity_per_agents,[cat1,cat2,cat3],[[1,0,0],[0,1,0],[0,0,1]], 2)" })
	public static double fuzzyKappa(final IScope scope,
		final IContainer<Integer, IAgent> agents, final IList<Object> vals1, final IList<Object> vals2, final IList<Double> similarities,final List<Object> categories, final GamaMatrix<Double> fuzzycategories, final Double distance) {
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
		GamaMap<Object, Integer> categoriesId = new GamaMap<Object, Integer>();
		for (int i = 0; i < nbCat; i++) {
			categoriesId.put(categories.get(i), i);
		}
		In filter = null;
		if (agents instanceof ISpecies) {
			final IPopulation pop = agents.first(scope).getPopulationFor((ISpecies) agents);
			filter = In.population(pop);
		}
		else 
			filter = In.list(scope, agents);
	
		
		computeXYCrispVector(scope, categoriesId,categories, vals1, vals2, fuzzycategories, nbCat, nb, crispVector1 ,crispVector2, X, Y, sim);
		double meanSimilarity = computeSimilarity(scope, filter,distance, vals1, vals2, agents, nbCat, nb, crispVector1 ,crispVector2,sim,fuzzyVector1, fuzzyVector2,similarities);
			
		GamaList<Double> rings = new GamaList<Double>();
		Map<Double,Integer> ringsPn = new GamaMap<Double, Integer>();
		int nbRings = buildRings(scope,filter,distance,rings, ringsPn, agents);
		double similarityExpected = computeExpectedSim(nbCat, X, Y, nbRings, rings,ringsPn);
		return (meanSimilarity - similarityExpected) / (1 - similarityExpected);
	}
	
	/*@operator(value = { "fuzzy_kappa_sim"}, content_type = IType.FLOAT)
	@doc(value = "fuzzy kappa simulation indicator for 2 map comparisons: fuzzy_kappa_sim(agents_list,list_vals1,list_vals2, output_similarity_per_agents,fuzzy_categories_matrix, fuzzy_distance)", examples = { "fuzzy_kappa([ag1, ag2, ag3, ag4, ag5], [1,3,2,8,2],[10,2,3,8,2], similarity_per_agents,[cat1,cat2,cat3],[[1,0,0],[0,1,0],[0,0,1]], 2)" })
	public static double fuzzyKappaSimulation(final IScope scope,
		final IContainer<Integer, IAgent> agents, final IList<Object> valsInit, final IList<Object> valsObs, final IList<Object> valsSim, final IList<Double> observeAgreements, final IList<Double> expectedAgreements, final List<Object> categories, final GamaMatrix<Double> fuzzycategories, final Double distance) {
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
		double[] nbObs = new double[nbCat];
		double[] nbSim = new double[nbCat];
		double[] nbInit = new double[nbCat];
		double[][] nbInitObs = new double[nbCat][nbCat];
		double[][] nbInitSim = new double[nbCat][nbCat];
		GamaMap<Object, Integer> categoriesId = new GamaMap<Object, Integer>();
		for (int i = 0; i < nbCat; i++) {
			categoriesId.put(categories.get(i), i);
		}
		
		computeXYCrispVector(scope, categoriesId,categories, valsObs, valsSim, fuzzycategories, nbCat, nb, crispVector1 ,crispVector2, nbObs, nbSim, sim);
		for (int i = 0 ; i < nbCat; i++ ) {
			nbInit[i] = 0;
			for (int j = 0 ; j < nbCat; j++ ) {
				nbInitObs[i][j] = 0;
				nbInitSim[i][j] = 0;
			}
		}
		In filter = null;
		if (agents instanceof ISpecies) {
			final IPopulation pop = agents.first(scope).getPopulationFor((ISpecies) agents);
			filter = In.population(pop);
		}
		else 
			filter = In.list(scope, agents);
	
		for (int i = 0; i < nb; i++) {
			int idCatInit = categoriesId.get(valsInit.get(i));
			nbInit[idCatInit] += 1;
			int idCatObs = categoriesId.get(valsObs.get(i));
			int idCatSim = categoriesId.get(valsSim.get(i));
			nbInitObs[idCatInit][idCatObs] += 1;
			nbInitSim[idCatInit][idCatSim] += 1;
		}
		double po = computeSimilarity(scope, filter,distance, valsObs, valsSim, agents, nbCat, nb, crispVector1 ,crispVector2,sim,fuzzyVector1, fuzzyVector2,observeAgreements);
		
		Map<Set<Object>, Double> listE = new GamaMap<Set<Object>, Double>();
		Map<Set<Object>, Double> listP = new GamaMap<Set<Object>, Double>();
		for (int i = 0 ; i < nbCat; i++ ) {
			Object o = categories.get(i);
			for (int j = 0 ; j < nbCat; j++ ) {
				Object a = categories.get(j);
				for (int k = 0 ; k < nbCat; k++ ) {
					Object s = categories.get(k);
					Set<Object> set = new HashSet<Object>();
					set.add(o); set.add(a); set.add(s);
					double poas = nbInitObs[i][j]/nbInit[i] * nbInitSim[i][k]/nbInit[i] * nbInit[i] / nb;
					listP.put(set, poas);
				}
			}
		}
		double pe = 0;
		for (Set<Object> val : listE.keySet()) {
			pe += (listE.get(val) * listP.get(val));
		}
	
		return (po - pe) / (1 - pe);
	}*/
	
	private static double p(double dist, int a, int b,double[] X,double[] Y,Map<Double,Integer> ringsPn) {
		int n = 0;
		if (dist > 0.0) {
			n = ringsPn.get(dist);
		}
		return (1 - (Math.pow(1-X[a],n))) * (1 - (Math.pow(1 - Y[b],n)));
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
					Ei += ((1 - kro_delta) * Ya * Xb * (p(dist, a,b,X,Y,ringsPn) - p(dist1, a,b,X,Y,ringsPn)));
				}
			} 
			similarityExpected += (Mdi * Ei);
		}
		return similarityExpected;
	}
	
	private static double computeSimilarity(final IScope scope, In filter, final Double distance, final IList<Object> vals1, final IList<Object> vals2, final IContainer<Integer, IAgent> agents, int nbCat, int nb, double[][] crispVector1 ,double[][] crispVector2, boolean[] sim,double[][] fuzzyVector1, double[][] fuzzyVector2,final IList<Double> similarities){
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
				
				GamaList<IAgent> neighbours = distance == 0 ?  new GamaList<IAgent>() : new GamaList<IAgent>(scope.getTopology().getNeighboursOf(scope, agent, distance, filter));
				
				
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
		double meanSimilarity = 0;
		for (Double val : similarities) {
			meanSimilarity += val;
		}
		meanSimilarity /= similarities.size();
		return meanSimilarity;
	}
		
	
	private static void computeXYCrispVector(final IScope scope, GamaMap<Object, Integer> categoriesId,final List<Object> categories,final IList<Object> vals1, final IList<Object> vals2, final GamaMatrix<Double> fuzzycategories, int nbCat, int nb, double[][] crispVector1 ,double[][] crispVector2, double[] X, double[] Y, boolean[] sim){
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
	
	private static int buildRings(IScope scope, In filter, final Double distance, GamaList<Double> rings, Map<Double,Integer> ringsPn, IContainer<Integer, IAgent> agents) {
		
		GamaList<ILocation> locs = new GamaList<ILocation>();
		for (IAgent ag : agents) {
			locs.add(ag.getLocation());
		}
		ILocation centralLoc = (ILocation) Stats.getMean(scope,locs);
		IAgent centralAg =  scope.getTopology().getAgentClosestTo(scope, centralLoc, filter);
		GamaList<IAgent> neighbours = distance == 0 ?  new GamaList<IAgent>() : new GamaList<IAgent>(scope.getTopology().getNeighboursOf(scope, centralAg, distance, filter));
		
		for (IAgent ag : neighbours) {
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
	
	
	@operator(value = { "percent_absolute_deviation"}, content_type = IType.FLOAT)
	@doc(value = "percent absolute deviation indicator for 2 series of values: percent_absolute_deviation(list_vals_observe,list_vals_sim)", examples = { "percent_absolute_deviation([200,300,150,150,200],[250,250,100,200,200])" })
	public static double percentAbsoluteDeviation(final IScope scope,final IList<Double> vals1, final IList<Double> vals2) {
		if (vals1 == null ||vals2 == null)
			return 1;
		int nb = vals1.size();
		if (nb  != vals2.size())
			return 0;
		double sum = 0;
		double coeff = 0;
		for (int i = 0; i < nb ; i++) {
			double val1 = Cast.asFloat(scope, vals1.get(i));
			double val2 = Cast.asFloat(scope, vals2.get(i));
			coeff += val1;
			sum += Math.abs(val1 - val2) * 100.0;
		}
		if (coeff == 0) return 0;
		return sum / coeff;
	
	}

	

}
