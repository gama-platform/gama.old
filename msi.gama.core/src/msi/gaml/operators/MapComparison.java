/*********************************************************************************************
 *
 * 'MapComparison.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.metamodel.topology.filter.In;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IAddressableContainer;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.TOrderedHashMap;
import msi.gama.util.matrix.GamaMatrix;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

//
//
// WARNING TODO AD: Utiliser les collections Trove pour optimiser tout cela !
//
//
public class MapComparison {

	@operator (
			value = { "kappa" },
			content_type = IType.FLOAT,
			category = { IOperatorCategory.MAP_COMPARAISON },
			concept = { IConcept.MAP })
	@doc (
			value = "kappa indicator for 2 map comparisons: kappa(list_vals1,list_vals2,categories). Reference: Cohen, J. A coefficient of agreement for nominal scales. Educ. Psychol. Meas. 1960, 20.",
			examples = { @example (
					value = "kappa([cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2],[cat1,cat2,cat3])",
					isExecutable = false),
					@example (
							value = "kappa([1,3,5,1,5],[1,1,1,1,5],[1,3,5])",
							equals = "the similarity between 0 and 1",
							test = false),
					@example (
							value = "kappa([1,1,1,1,5],[1,1,1,1,5],[1,3,5])",
							equals = "1.0") })
	public static double kappa(final IScope scope, final IList<Object> vals1, final IList<Object> vals2,
			final IList<Object> categories) {
		return kappa(scope, vals1, vals2, categories, null);
	}

	@operator (
			value = { "kappa" },
			content_type = IType.FLOAT,
			category = { IOperatorCategory.MAP_COMPARAISON },
			concept = {})
	@doc (
			value = "kappa indicator for 2 map comparisons: kappa(list_vals1,list_vals2,categories, weights). Reference: Cohen, J. A coefficient of agreement for nominal scales. Educ. Psychol. Meas. 1960, 20. ",
			examples = { @example (
					value = "kappa([cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2],[cat1,cat2,cat3], [1.0, 2.0, 3.0, 1.0, 5.0])",
					isExecutable = false) })
	public static double kappa(final IScope scope, final IList<Object> vals1, final IList<Object> vals2,
			final IList<Object> categories, final IList<Object> weights) {
		if (vals1 == null || vals2 == null) { return 1; }
		final int nb = vals1.size();
		if (nb != vals2.size()) { return 0; }
		final int nbCat = categories.size();
		final double[] X = new double[nbCat];
		final double[] Y = new double[nbCat];
		final double[][] contigency = new double[nbCat][nbCat];
		for (int j = 0; j < nbCat; j++) {
			X[j] = 0;
			Y[j] = 0;
			for (int k = 0; k < nbCat; k++) {
				contigency[j][k] = 0;
			}
		}

		final Map<Object, Integer> categoriesId = new TOrderedHashMap<Object, Integer>();
		for (int i = 0; i < nbCat; i++) {
			categoriesId.put(categories.get(i), i);
		}
		double total = 0;
		for (int i = 0; i < nb; i++) {
			final double weight = weights == null ? 1.0 : Cast.asFloat(scope, weights.get(i));
			total += weight;
			final Object val1 = vals1.get(i);
			final Object val2 = vals2.get(i);
			final int indexVal1 = categoriesId.get(val1);
			final int indexVal2 = categoriesId.get(val2);
			X[indexVal1] += weight;
			Y[indexVal2] += weight;
			contigency[indexVal1][indexVal2] += weight;
		}
		for (int j = 0; j < nbCat; j++) {
			X[j] /= total;
			Y[j] /= total;
			for (int k = 0; k < nbCat; k++) {
				contigency[j][k] /= total;
			}
		}
		double po = 0;
		double pe = 0;
		for (int i = 0; i < nbCat; i++) {
			po += contigency[i][i];
			pe += X[i] * Y[i];
		}
		if (pe == 1) { return 1; }
		return (po - pe) / (1 - pe);
	}

	@operator (
			value = { "kappa_sim" },
			content_type = IType.FLOAT,
			category = { IOperatorCategory.MAP_COMPARAISON },
			concept = { IConcept.MAP })
	@doc (
			value = "kappa simulation indicator for 2 map comparisons: kappa(list_valsInits,list_valsObs,list_valsSim, categories). Reference: van Vliet, J., Bregt, A.K. & Hagen-Zanker, A. (2011). Revisiting Kappa to account for change in the accuracy assessment of land-use change models, Ecological Modelling 222(8).",
			examples = { @example (
					value = "kappa([cat1,cat1,cat2,cat2,cat2],[cat2,cat1,cat2,cat1,cat3],[cat2,cat1,cat2,cat3,cat3], [cat1,cat2,cat3])",
					isExecutable = false) })
	public static double kappaSimulation(final IScope scope, final IList<Object> valsInit, final IList<Object> valsObs,
			final IList<Object> valsSim, final IList<Object> categories) {
		return kappaSimulation(scope, valsInit, valsObs, valsSim, categories, null);
	}

	@operator (
			value = { "kappa_sim" },
			content_type = IType.FLOAT,
			category = { IOperatorCategory.MAP_COMPARAISON },
			concept = {})
	@doc (
			value = "kappa simulation indicator for 2 map comparisons: kappa(list_valsInits,list_valsObs,list_valsSim, categories, weights). Reference: van Vliet, J., Bregt, A.K. & Hagen-Zanker, A. (2011). Revisiting Kappa to account for change in the accuracy assessment of land-use change models, Ecological Modelling 222(8)",
			examples = { @example (
					value = "kappa([cat1,cat1,cat2,cat2,cat2],[cat2,cat1,cat2,cat1,cat3],[cat2,cat1,cat2,cat3,cat3], [cat1,cat2,cat3],[1.0, 2.0, 3.0, 1.0, 5.0])",
					isExecutable = false) })
	public static double kappaSimulation(final IScope scope, final IList<Object> valsInit, final IList<Object> valsObs,
			final IList<Object> valsSim, final IList<Object> categories, final IList<Object> weights) {
		if (valsInit == null || valsObs == null || valsSim == null) { return 1; }
		final int nb = valsInit.size();
		if (nb != valsObs.size() || nb != valsSim.size()) { return 0; }
		final int nbCat = categories.size();
		final double[] O = new double[nbCat];
		final double[][] contigency = new double[nbCat][nbCat];
		final double[][] contigencyOA = new double[nbCat][nbCat];
		final double[][] contigencyOS = new double[nbCat][nbCat];
		for (int j = 0; j < nbCat; j++) {
			O[j] = 0;
			for (int k = 0; k < nbCat; k++) {
				contigency[j][k] = 0;
				contigencyOA[j][k] = 0;
				contigencyOS[j][k] = 0;
			}
		}
		final Map<Object, Integer> categoriesId = new TOrderedHashMap<Object, Integer>();
		for (int i = 0; i < nbCat; i++) {
			categoriesId.put(categories.get(i), i);
		}
		double total = 0;
		for (int i = 0; i < nb; i++) {
			final double weight = weights == null ? 1.0 : Cast.asFloat(scope, weights.get(i));
			total += weight;
			final Object val1 = valsObs.get(i);
			final Object val2 = valsSim.get(i);
			final Object valO = valsInit.get(i);
			final int indexVal1 = categoriesId.get(val1);
			final int indexVal2 = categoriesId.get(val2);
			final int indexValO = categoriesId.get(valO);
			O[indexValO] += weight;
			contigency[indexVal1][indexVal2] += weight;
			contigencyOA[indexValO][indexVal1] += weight;
			contigencyOS[indexValO][indexVal2] += weight;
		}
		for (int j = 0; j < nbCat; j++) {
			for (int k = 0; k < nbCat; k++) {
				contigency[j][k] /= total;
				if (O[j] > 0) {
					contigencyOA[j][k] /= O[j];
					contigencyOS[j][k] /= O[j];
				}
			}
			O[j] /= total;
		}
		double po = 0;
		double pe = 0;
		for (int j = 0; j < nbCat; j++) {
			po += contigency[j][j];
			double sum = 0;
			for (int i = 0; i < nbCat; i++) {
				sum += contigencyOA[j][i] * contigencyOS[j][i];
			}
			pe += O[j] * sum;
		}
		if (pe == 1) { return 1; }
		return (po - pe) / (1 - pe);
	}

	@operator (
			value = { "fuzzy_kappa" },
			content_type = IType.FLOAT,
			category = { IOperatorCategory.MAP_COMPARAISON },
			concept = { IConcept.MAP })
	@doc (
			value = "fuzzy kappa indicator for 2 map comparisons: fuzzy_kappa(agents_list,list_vals1,list_vals2, output_similarity_per_agents,categories,fuzzy_categories_matrix, fuzzy_distance). Reference: Visser, H., and T. de Nijs, 2006. The map comparison kit, Environmental Modelling & Software, 21",
			examples = { @example (
					value = "fuzzy_kappa([ag1, ag2, ag3, ag4, ag5],[cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2], similarity_per_agents,[cat1,cat2,cat3],[[1,0,0],[0,1,0],[0,0,1]], 2)",
					isExecutable = false) })
	public static double fuzzyKappa(final IScope scope,
			final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents, final IList<Object> vals1,
			final IList<Object> vals2, final IList<Double> similarities, final IList<Object> categories,
			final GamaMatrix<Double> fuzzycategories, final Double distance) {
		return fuzzyKappa(scope, agents, vals1, vals2, similarities, categories, fuzzycategories, distance, null);
	}

	@operator (
			value = { "fuzzy_kappa" },
			content_type = IType.FLOAT,
			category = { IOperatorCategory.MAP_COMPARAISON },
			concept = {})
	@doc (
			value = "fuzzy kappa indicator for 2 map comparisons: fuzzy_kappa(agents_list,list_vals1,list_vals2, output_similarity_per_agents,categories,fuzzy_categories_matrix, fuzzy_distance, weights). Reference: Visser, H., and T. de Nijs, 2006. The map comparison kit, Environmental Modelling & Software, 21",
			examples = { @example (
					value = "fuzzy_kappa([ag1, ag2, ag3, ag4, ag5],[cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2], similarity_per_agents,[cat1,cat2,cat3],[[1,0,0],[0,1,0],[0,0,1]], 2, [1.0,3.0,2.0,2.0,4.0])",
					isExecutable = false) })
	public static double fuzzyKappa(final IScope scope,
			final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents, final IList<Object> vals1,
			final IList<Object> vals2, final IList<Double> similarities, final IList<Object> categories,
			final GamaMatrix<Double> fuzzycategories, final Double distance, final IList<Object> weights) {
		if (agents == null) { return 1; }
		final int nb = agents.length(scope);
		if (nb < 1) { return 1; }
		final int nbCat = categories.size();
		similarities.clear();
		final boolean[] sim = new boolean[nb];
		final double[][] crispVector1 = new double[nb][nbCat];
		final double[][] crispVector2 = new double[nb][nbCat];
		final double[][] fuzzyVector1 = new double[nb][nbCat];
		final double[][] fuzzyVector2 = new double[nb][nbCat];
		final double[] X = new double[nbCat];
		final double[] Y = new double[nbCat];
		final Map<Object, Integer> categoriesId = new TOrderedHashMap<Object, Integer>();
		for (int i = 0; i < nbCat; i++) {
			categoriesId.put(categories.get(i), i);
		}
		final IAgentFilter filter = In.list(scope, agents);

		computeXYCrispVector(scope, categoriesId, categories, vals1, vals2, fuzzycategories, nbCat, nb, crispVector1,
				crispVector2, X, Y, sim, weights);
		final double meanSimilarity = computeSimilarity(scope, filter, distance, vals1, vals2, agents, nbCat, nb,
				crispVector1, crispVector2, sim, fuzzyVector1, fuzzyVector2, similarities, weights);

		final List<Double> rings = new ArrayList<Double>();
		final Map<Double, Integer> ringsPn = new TOrderedHashMap<Double, Integer>();
		final int nbRings = buildRings(scope, filter, distance, rings, ringsPn, agents);
		final double similarityExpected = computeExpectedSim(nbCat, X, Y, nbRings, rings, ringsPn);
		if (similarityExpected == 1) { return 1; }
		return (meanSimilarity - similarityExpected) / (1 - similarityExpected);
	}

	@operator (
			value = { "fuzzy_kappa_sim" },
			content_type = IType.FLOAT,
			category = { IOperatorCategory.MAP_COMPARAISON },
			concept = { IConcept.MAP })
	@doc (
			value = "fuzzy kappa simulation indicator for 2 map comparisons: fuzzy_kappa_sim(agents_list,list_vals1,list_vals2, output_similarity_per_agents,fuzzy_transitions_matrix, fuzzy_distance). Reference: Jasper van Vliet, Alex Hagen-Zanker, Jelle Hurkens, Hedwig van Delden, A fuzzy set approach to assess the predictive accuracy of land use simulations, Ecological Modelling, 24 July 2013, Pages 32-42, ISSN 0304-3800, ",
			examples = { @example (
					value = "fuzzy_kappa_sim([ag1, ag2, ag3, ag4, ag5], [cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2], similarity_per_agents,[cat1,cat2,cat3],[[1,0,0,0,0,0,0,0,0],[0,1,0,0,0,0,0,0,0],[0,0,1,0,0,0,0,0,0],[0,0,0,1,0,0,0,0,0],[0,0,0,0,1,0,0,0,0],[0,0,0,0,0,1,0,0,0],[0,0,0,0,0,0,1,0,0],[0,0,0,0,0,0,0,1,0],[0,0,0,0,0,0,0,0,1]], 2)",
					isExecutable = false) })
	public static double fuzzyKappaSimulation(final IScope scope,
			final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents, final IList<Object> valsInit,
			final IList<Object> valsObs, final IList<Object> valsSim, final IList<Double> similarities,
			final IList<Object> categories, final GamaMatrix<Double> fuzzytransitions, final Double distance) {
		return fuzzyKappaSimulation(scope, agents, valsInit, valsObs, valsSim, similarities, categories,
				fuzzytransitions, distance, null);

	}

	@operator (
			value = { "fuzzy_kappa_sim" },
			content_type = IType.FLOAT,
			category = { IOperatorCategory.MAP_COMPARAISON },
			concept = { IConcept.MAP })
	@doc (
			value = "fuzzy kappa simulation indicator for 2 map comparisons: fuzzy_kappa_sim(agents_list,list_vals1,list_vals2, output_similarity_per_agents,fuzzy_transitions_matrix, fuzzy_distance, weights). Reference: Jasper van Vliet, Alex Hagen-Zanker, Jelle Hurkens, Hedwig van Delden, A fuzzy set approach to assess the predictive accuracy of land use simulations, Ecological Modelling, 24 July 2013, Pages 32-42, ISSN 0304-3800, ",
			examples = { @example (
					value = "fuzzy_kappa_sim([ag1, ag2, ag3, ag4, ag5], [cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2], similarity_per_agents,[cat1,cat2,cat3],[[1,0,0,0,0,0,0,0,0],[0,1,0,0,0,0,0,0,0],[0,0,1,0,0,0,0,0,0],[0,0,0,1,0,0,0,0,0],[0,0,0,0,1,0,0,0,0],[0,0,0,0,0,1,0,0,0],[0,0,0,0,0,0,1,0,0],[0,0,0,0,0,0,0,1,0],[0,0,0,0,0,0,0,0,1]], 2,[1.0,3.0,2.0,2.0,4.0])",
					isExecutable = false) })
	public static double fuzzyKappaSimulation(final IScope scope,
			final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents, final IList<Object> valsInit,
			final IList<Object> valsObs, final IList<Object> valsSim, final IList<Double> similarities,
			final IList<Object> categories, final GamaMatrix<Double> fuzzytransitions, final Double distance,
			final IList<Object> weights) {
		if (agents == null) { return 1; }
		final int nb = agents.length(scope);
		if (nb < 1) { return 1; }
		similarities.clear();
		final int nbCat = categories.size();
		final double[] nbObs = new double[nbCat];
		final double[] nbSim = new double[nbCat];
		final double[] nbInit = new double[nbCat];
		final double[][] nbInitObs = new double[nbCat][nbCat];
		final double[][] nbInitSim = new double[nbCat][nbCat];
		final Map<Object, Integer> categoriesId = new TOrderedHashMap<Object, Integer>();

		final Map<List<Integer>, Map<Double, Double>> XaPerTransition =
				new TOrderedHashMap<List<Integer>, Map<Double, Double>>();
		final Map<List<Integer>, Map<Double, Double>> XsPerTransition =
				new TOrderedHashMap<List<Integer>, Map<Double, Double>>();
		final Set<Double> Xvals = new HashSet<Double>();
		for (int i = 0; i < nbCat; i++) {
			categoriesId.put(categories.get(i), i);
		}

		for (int i = 0; i < nbCat; i++) {
			nbInit[i] = 0;
			nbObs[i] = 0;
			nbSim[i] = 0;
			for (int j = 0; j < nbCat; j++) {
				nbInitObs[i][j] = 0;
				nbInitSim[i][j] = 0;
			}
		}
		final IAgentFilter filter = In.list(scope, agents);
		double total = 0;
		for (int i = 0; i < nb; i++) {
			final double weight = weights == null ? 1.0 : Cast.asFloat(scope, weights.get(i));
			total += weight;

			final int idCatInit = categoriesId.get(valsInit.get(i));
			final int idCatObs = categoriesId.get(valsObs.get(i));
			final int idCatSim = categoriesId.get(valsSim.get(i));
			nbInit[idCatInit] += weight;
			nbSim[idCatSim] += weight;
			nbObs[idCatObs] += weight;
			nbInitObs[idCatInit][idCatObs] += weight;
			nbInitSim[idCatInit][idCatSim] += weight;
		}
		final double po = computePo(scope, filter, categoriesId, fuzzytransitions, distance, valsInit, valsObs, valsSim,
				agents, nbCat, nb, similarities, weights);
		double pe = 0;
		computeXaXsTransitions(scope, filter, fuzzytransitions, distance, agents, nbCat, XaPerTransition,
				XsPerTransition, Xvals);
		for (int i = 0; i < nbCat; i++) {
			for (int j = 0; j < nbCat; j++) {
				for (int k = 0; k < nbCat; k++) {
					final List<Integer> ca = new ArrayList<Integer>();
					ca.add(i);
					ca.add(j);
					ca.add(k);
					final Map<Double, Double> pmuXa = XaPerTransition.get(ca);
					final Map<Double, Double> pmuXs = XsPerTransition.get(ca);
					double emu = 0;
					for (final Double xval : Xvals) {
						final double XaVal = pmuXa == null || !pmuXa.containsKey(xval) ? 0 : pmuXa.get(xval);
						final double XsVal = pmuXs == null || !pmuXs.containsKey(xval) ? 0 : pmuXs.get(xval);
						final double proba = xval * XaVal * XsVal;
						emu += proba;
					}

					final double poas = nbInit[i] == 0 ? 0 : nbInitObs[i][j] / nbInit[i] * nbInitSim[i][k] / total;
					pe += emu * poas;
				}
			}
		}
		if (pe == 1) { return 1; }
		return (po - pe) / (1 - pe);
	}

	private static double computePo(final IScope scope, final IAgentFilter filter,
			final Map<Object, Integer> categoriesId, final GamaMatrix<Double> fuzzytransitions, final Double distance,
			final IList<Object> valsInit, final IList<Object> valsObs, final IList<Object> valsSim,
			final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents, final int nbCat, final int nb,
			final IList<Double> similarities, final IList<Object> weights) {
		final Map<IAgent, Integer> agsId = new TOrderedHashMap<IAgent, Integer>();
		for (int i = 0; i < agents.length(scope); i++) {
			agsId.put(agents.get(scope, i), i);
		}

		for (int i = 0; i < nb; i++) {
			final Object valObs = valsObs.get(i);
			final Object valSim = valsSim.get(i);
			final Object valInit = valsInit.get(i);
			final int valObsId = categoriesId.get(valObs);
			final int valSimId = categoriesId.get(valSim);
			final int valInitId = categoriesId.get(valInit);
			final IAgent agent = agents.get(scope, i);
			final double[] XaXs = computeXaXs(scope, filter, categoriesId, agsId, valObsId, valSimId, valInitId,
					fuzzytransitions, distance, agent, valsInit, valsObs, valsSim, agents, nbCat);
			similarities.add(FastMath.min(XaXs[0], XaXs[1]));
		}
		double meanSimilarity = 0;
		double total = 0;
		for (int i = 0; i < nb; i++) {
			final double weight = weights == null ? 1.0 : Cast.asFloat(scope, weights.get(i));
			final double val = weight * similarities.get(i);
			total += weight;
			meanSimilarity += val;
		}
		meanSimilarity /= total;
		return meanSimilarity;
	}

	private static double[] computeXaXs(final IScope scope, final IAgentFilter filter,
			final Map<Object, Integer> categoriesId, final Map<IAgent, Integer> agsId, final int valObsId,
			final int valSimId, final int valInitId, final GamaMatrix<Double> fuzzytransitions, final Double distance,
			final IAgent agent, final IList<Object> valsInit, final IList<Object> valsObs, final IList<Object> valsSim,
			final IContainer.Addressable<Integer, IAgent> agents, final int nbCat) {
		double xa = 0.0;
		double xs = 0.0;
		final double[] XaXs = new double[2];
		final double sizeNorm = FastMath.sqrt(agent.getEnvelope().getArea());
		final List<IAgent> neighbors = distance == 0 || filter == null ? new ArrayList<IAgent>()
				: new ArrayList<IAgent>(scope.getTopology().getNeighborsOf(scope, agent, distance, filter));

		final Map<IAgent, Double> distancesCoeff = new TOrderedHashMap<IAgent, Double>();
		distancesCoeff.put(agent, 1.0);
		for (final IAgent ag : neighbors) {
			final double euclidDist = agent.getLocation().euclidianDistanceTo(ag.getLocation());
			distancesCoeff.put(ag, 1 / (1.0 + euclidDist / sizeNorm));
		}
		for (final IAgent ag : distancesCoeff.keySet()) {
			final int id = agsId.get(ag);
			final Object valI = valsInit.get(id);
			final Object valO = valsObs.get(id);
			final Object valS = valsSim.get(id);
			final int valOId = categoriesId.get(valO);
			final int valSId = categoriesId.get(valS);
			final int valIId = categoriesId.get(valI);
			final double dist = distancesCoeff.get(ag);
			final double valxatmp =
					fuzzyTransition(scope, fuzzytransitions, nbCat, valInitId, valSimId, valIId, valOId) * dist;
			final double valxstmp =
					fuzzyTransition(scope, fuzzytransitions, nbCat, valInitId, valObsId, valIId, valSId) * dist;

			if (valxatmp > xa) {
				xa = valxatmp;
			}
			if (valxstmp > xs) {
				xs = valxstmp;
			}
		}

		XaXs[0] = xa;
		XaXs[1] = xs;
		return XaXs;
	}

	private static double fuzzyTransition(final IScope scope, final GamaMatrix<Double> fuzzytransitions,
			final int nbCat, final int from1, final int to1, final int from2, final int to2) {
		return fuzzytransitions.get(scope, from1 + nbCat * to1, from2 + nbCat * to2);
	}

	private static void computeXaXsTransitions(final IScope scope, final IAgentFilter filter,
			final GamaMatrix<Double> fuzzytransitions, final Double distance, final IContainer<Integer, IAgent> agents,
			final int nbCat, final Map<List<Integer>, Map<Double, Double>> XaPerTransition,
			final Map<List<Integer>, Map<Double, Double>> XsPerTransition, final Set<Double> Xvals) {

		final IList<ILocation> locs = GamaListFactory.create(Types.POINT);
		for (final IAgent ag : agents.iterable(scope)) {
			locs.add(ag.getLocation());
		}
		final ILocation centralLoc = (ILocation) Containers.mean(scope, locs);
		if (filter != null) {
			final IAgent centralAg = scope.getTopology().getAgentClosestTo(scope, centralLoc, filter);
			final List<IAgent> neighbors = distance == 0 ? new ArrayList<IAgent>()
					: new ArrayList<IAgent>(scope.getTopology().getNeighborsOf(scope, centralAg, distance, filter));
			final double sizeNorm = FastMath.sqrt(centralAg.getEnvelope().getArea());

			final Map<IAgent, Double> distancesCoeff = new TOrderedHashMap<IAgent, Double>();
			distancesCoeff.put(centralAg, 1.0);
			for (final IAgent ag : neighbors) {
				final double euclidDist = centralAg.getLocation().euclidianDistanceTo(ag.getLocation());
				final double dist = 1 / (1.0 + euclidDist / sizeNorm);
				distancesCoeff.put(ag, dist);

			}

			for (int i = 0; i < nbCat; i++) {
				for (int j = 0; j < nbCat; j++) {
					for (int k = 0; k < nbCat; k++) {
						final List<Integer> ca = new ArrayList<>();
						ca.add(i);
						ca.add(j);
						ca.add(k);
						double xa = 0;
						double xs = 0;
						for (final IAgent ag : distancesCoeff.keySet()) {
							final double dist = distancesCoeff.get(ag);
							final double xatmp = fuzzyTransition(scope, fuzzytransitions, nbCat, i, k, i, j) * dist;
							final double xstmp = fuzzyTransition(scope, fuzzytransitions, nbCat, i, j, i, k) * dist;
							if (xatmp > xa) {
								xa = xatmp;
							}
							if (xstmp > xs) {
								xs = xstmp;
							}
						}
						if (xa > 0) {

							Map<Double, Double> mapxa = XaPerTransition.get(ca);
							if (mapxa == null) {
								mapxa = new TOrderedHashMap<Double, Double>();
								mapxa.put(xa, 1.0);
								XaPerTransition.put(ca, mapxa);
							} else {
								if (mapxa.containsKey(xa)) {
									mapxa.put(xa, mapxa.get(xa) + 1.0);
								} else {
									mapxa.put(xa, 1.0);
								}
							}
							Xvals.add(xa);
						}
						if (xs > 0) {
							Map<Double, Double> mapxs = XsPerTransition.get(ca);
							if (mapxs == null) {
								mapxs = new TOrderedHashMap<Double, Double>();
								mapxs.put(xs, 1.0);
								XsPerTransition.put(ca, mapxs);
							} else {
								if (mapxs.containsKey(xa)) {
									mapxs.put(xs, mapxs.get(xs) + 1.0);
								} else {
									mapxs.put(xs, 1.0);
								}
							}
							Xvals.add(xs);
						}
					}
				}
			}
		}
	}

	private static double p(final double dist, final int a, final int b, final double[] X, final double[] Y,
			final Map<Double, Integer> ringsPn) {
		int n = 0;
		if (dist > 0.0) {
			n = ringsPn.get(dist);
		}
		return (1 - FastMath.pow(1 - X[a], n)) * (1 - FastMath.pow(1 - Y[b], n));
	}

	private static double computeExpectedSim(final int nbCat, final double[] X, final double[] Y, final int nbRings,
			final List<Double> rings, final Map<Double, Integer> ringsPn) {
		double similarityExpected = 0;
		for (int j = 0; j < nbCat; j++) {
			similarityExpected += X[j] * Y[j];
		}

		double dist = 0;
		for (int p = 0; p < nbRings; p++) {
			final double dist1 = dist;
			dist = rings.get(p);
			final double Mdi = FastMath.pow(2, dist / -2);
			double Ei = 0;
			for (int a = 0; a < nbCat; a++) {
				final double Ya = Y[a];
				for (int b = 0; b < nbCat; b++) {
					final double Xb = X[b];
					final int kro_delta = a == b ? 1 : 0;
					Ei += (1 - kro_delta) * Ya * Xb * (p(dist, a, b, X, Y, ringsPn) - p(dist1, a, b, X, Y, ringsPn));
				}
			}
			similarityExpected += Mdi * Ei;
		}
		return similarityExpected;
	}

	private static double computeSimilarity(final IScope scope, final IAgentFilter filter, final Double distance,
			final IList<Object> vals1, final IList<Object> vals2,
			final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents, final int nbCat, final int nb,
			final double[][] crispVector1, final double[][] crispVector2, final boolean[] sim,
			final double[][] fuzzyVector1, final double[][] fuzzyVector2, final IList<Double> similarities,
			final IList<Object> weights) {
		final Map<IAgent, Integer> agsId = new TOrderedHashMap<IAgent, Integer>();
		for (int i = 0; i < agents.length(scope); i++) {
			agsId.put(agents.get(scope, i), i);
		}

		for (int i = 0; i < nb; i++) {
			if (sim[i]) {
				similarities.add(1.0);
			} else {
				final IAgent agent = agents.get(scope, i);
				// double sizeNorm = agent.getPerimeter() / 4.0;
				final double sizeNorm = FastMath.sqrt(agent.getEnvelope().getArea());
				final List<IAgent> neighbors = distance == 0 || filter == null ? new ArrayList<IAgent>()
						: new ArrayList<IAgent>(scope.getTopology().getNeighborsOf(scope, agent, distance, filter));

				final Map<IAgent, Double> distancesCoeff = new TOrderedHashMap<IAgent, Double>();
				distancesCoeff.put(agent, 1.0);
				for (final IAgent ag : neighbors) {
					final double euclidDist = agent.getLocation().euclidianDistanceTo(ag.getLocation());
					distancesCoeff.put(ag, 1 / (1.0 + euclidDist / sizeNorm));
				}
				for (int j = 0; j < nbCat; j++) {
					double max1 = 0.0;
					double max2 = 0.0;
					for (final IAgent ag : neighbors) {
						final int id = agsId.get(ag);
						final double val1 = crispVector1[id][j] * distancesCoeff.get(ag);
						final double val2 = crispVector2[id][j] * distancesCoeff.get(ag);

						if (val1 > max1) {
							max1 = val1;
						}
						if (val2 > max2) {
							max2 = val2;
						}
					}
					fuzzyVector1[i][j] = max1;
					fuzzyVector2[i][j] = max2;
				}
				double s1Max = -1 * Double.MAX_VALUE;
				double s2Max = -1 * Double.MAX_VALUE;

				for (int j = 0; j < nbCat; j++) {
					final double s1 = FastMath.min(fuzzyVector1[i][j], crispVector2[i][j]);
					final double s2 = FastMath.min(fuzzyVector2[i][j], crispVector1[i][j]);
					if (s1 > s1Max) {
						s1Max = s1;
					}
					if (s2 > s2Max) {
						s2Max = s2;
					}
				}
				similarities.add(FastMath.min(s1Max, s2Max));
			}
		}
		double meanSimilarity = 0;
		double total = 0;
		for (int i = 0; i < nb; i++) {
			final double weight = weights == null ? 1.0 : Cast.asFloat(scope, weights.get(i));
			final double val = weight * similarities.get(i);
			total += weight;
			meanSimilarity += val;
		}
		meanSimilarity /= total;
		return meanSimilarity;
	}

	private static void computeXYCrispVector(final IScope scope, final Map<Object, Integer> categoriesId,
			final List<Object> categories, final IList<Object> vals1, final IList<Object> vals2,
			final GamaMatrix<Double> fuzzycategories, final int nbCat, final int nb, final double[][] crispVector1,
			final double[][] crispVector2, final double[] X, final double[] Y, final boolean[] sim,
			final IList<Object> weights) {
		for (int j = 0; j < nbCat; j++) {
			X[j] = 0;
			Y[j] = 0;
		}
		double total = 0;
		for (int i = 0; i < nb; i++) {
			final double weight = weights == null ? 1.0 : Cast.asFloat(scope, weights.get(i));
			total += weight;
			final Object val1 = vals1.get(i);
			final Object val2 = vals2.get(i);
			final int indexVal1 = categoriesId.get(val1);
			final int indexVal2 = categoriesId.get(val2);
			X[indexVal1] += weight;
			Y[indexVal2] += weight;
			for (int j = 0; j < nbCat; j++) {
				crispVector1[i][j] = Cast.asFloat(scope, fuzzycategories.get(scope, indexVal1, j));
				crispVector2[i][j] = Cast.asFloat(scope, fuzzycategories.get(scope, indexVal2, j));
			}
			if (val1.equals(val2)) {
				sim[i] = true;
			} else {
				sim[i] = false;
			}
		}
		for (int j = 0; j < nbCat; j++) {
			X[j] /= total;
			Y[j] /= total;
		}

	}

	private static int buildRings(final IScope scope, final IAgentFilter filter, final Double distance,
			final List<Double> rings, final Map<Double, Integer> ringsPn,
			final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents) {

		final IList<ILocation> locs = GamaListFactory.create(Types.POINT);
		for (final IAgent ag : agents.iterable(scope)) {
			locs.add(ag.getLocation());
		}
		final ILocation centralLoc = (ILocation) Containers.mean(scope, locs);
		final IAgent centralAg = scope.getTopology().getAgentClosestTo(scope, centralLoc, filter);
		final List<IAgent> neighbors = distance == 0 || filter == null ? new ArrayList<IAgent>()
				: new ArrayList<IAgent>(scope.getTopology().getNeighborsOf(scope, centralAg, distance, filter));

		for (final IAgent ag : neighbors) {
			final double dist = centralLoc.euclidianDistanceTo(ag.getLocation());
			if (dist == 0) {
				continue;
			}
			if (!rings.contains(dist)) {
				rings.add(dist);
				ringsPn.put(dist, 1);
			} else {
				ringsPn.put(dist, 1 + ringsPn.get(dist));
			}
		}
		Collections.sort(rings);

		for (int i = 1; i < rings.size(); i++) {
			final double dist = rings.get(i);
			final double dist1 = rings.get(i - 1);
			ringsPn.put(dist, ringsPn.get(dist) + ringsPn.get(dist1));
		}

		return rings.size();

	}

	@operator (
			value = { "percent_absolute_deviation" },
			content_type = IType.FLOAT,
			category = { IOperatorCategory.MAP_COMPARAISON },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "percent absolute deviation indicator for 2 series of values: percent_absolute_deviation(list_vals_observe,list_vals_sim)",
			examples = { @example (
					value = "percent_absolute_deviation([200,300,150,150,200],[250,250,100,200,200])",
					isExecutable = false) })
	public static double percentAbsoluteDeviation(final IScope scope, final IList<Double> vals1,
			final IList<Double> vals2) {
		if (vals1 == null || vals2 == null) { return 1; }
		final int nb = vals1.size();
		if (nb != vals2.size()) { return 0; }
		double sum = 0;
		double coeff = 0;
		for (int i = 0; i < nb; i++) {
			final double val1 = Cast.asFloat(scope, vals1.get(i));
			final double val2 = Cast.asFloat(scope, vals2.get(i));
			coeff += val1;
			sum += FastMath.abs(val1 - val2) * 100.0;
		}
		if (coeff == 0) { return 0; }
		return sum / coeff;

	}

}
