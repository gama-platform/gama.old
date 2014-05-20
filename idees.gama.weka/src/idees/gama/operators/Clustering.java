package idees.gama.operators;

import java.util.List;

import weka.clusterers.Clusterer;
import weka.clusterers.Cobweb;
import weka.clusterers.DBSCAN;
import weka.clusterers.EM;
import weka.clusterers.FarthestFirst;
import weka.clusterers.SimpleKMeans;
import weka.clusterers.XMeans;
import weka.clusterers.forOPTICSAndDBScan.DataObjects.ManhattanDataObject;
import weka.core.Attribute;
import weka.core.ChebyshevDistance;
import weka.core.EditDistance;
import weka.core.EuclideanDistance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

public class Clustering {

	private static Instances convertToInstances(final IScope scope,
			final IList<String> attributes, final IList<IAgent> agents)
			throws GamaRuntimeException {
		FastVector attribs = new FastVector();
		for (String att : attributes) {
			attribs.addElement(new Attribute(att));
		}
		Instances dataset = new Instances(scope.getAgentScope().getName(),
				attribs, agents.size());
		for (IAgent ag : agents) {

			int nb = attributes.size();
			double vals[] = new double[nb];
			for (int i = 0; i < nb; i++) {
				String attrib = attributes.get(i);
				Double var = Cast.asFloat(scope,
						ag.getDirectVarValue(scope, attrib));
				vals[i] = var;
			}
			Instance instance = new Instance(1, vals);
			dataset.add(instance);
		}
		return dataset;
	}

	private static IList<IList<IAgent>> clusteringUsingWeka(final IScope scope,
			final Clusterer clusterer, final IList<String> attributes,
			final IList<IAgent> agents) throws GamaRuntimeException {
		Instances dataset = convertToInstances(scope, attributes, agents);
		try {
			clusterer.buildClusterer(dataset);

			IList<IList<IAgent>> groupes = new GamaList<IList<IAgent>>();

			for (int i = 0; i < clusterer.numberOfClusters(); i++) {
				groupes.add(new GamaList<IAgent>());
			}
			for (int i = 0; i < dataset.numInstances(); i++) {
				Instance inst = dataset.instance(i);
				int clusterIndex = -1;
				clusterIndex = clusterer.clusterInstance(inst);
				IList<IAgent> groupe = groupes.get(clusterIndex);
				groupe.add(agents.get(i));
			}
			return groupes;
		} catch (Exception e) {
			return null;
		}

	}

	/*
	 * bin_value -- Set the value that represents true in the new attributes.
	 * cut_off_factor -- the cut-off factor to use distance_f -- The distance
	 * function to use : String : 4 possible distance functions: (by default)
	 * euclidean; otherwise 'chebyshev', 'manhattan' and 'levenshtein'
	 * max_iterations -- the maximum number of iterations to perform max_kmeans
	 * -- the maximum number of iterations to perform in KMeans
	 * max_kmeans_for_children -- the maximum number of iterations KMeans that
	 * is performed on the child centers max_num_clusters -- set maximum number
	 * of clusters min_num_clusters -- set minimum number of clusters seed --
	 * The random number seed to be used.
	 */
	@operator(value = { "clustering_xmeans"},
			content_type = IType.LIST,
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_STATISTICAL, IOperatorCategory.STATISTICAL })
		@doc(value = "A list of agent groups clusteredby X-Means Algorithm based on the given attributes. Some paremeters can be defined: bin_value: value given for true value of boolean attributes; cut_off_factor: the cut-off factor to use;" +
				"distance_f: The distance function to use. 4 possible distance functions: euclidean (by default) ; 'chebyshev', 'manhattan' or 'levenshtein'; " +
				"max_iterations: the maximum number of iterations to perform; max_kmeans: the maximum number of iterations to perform in KMeans; max_kmeans_for_children: the maximum number of iterations KMeans that is performed on the child centers;" +
				"max_num_clusters: the maximum number of clusters; min_num_clusters: the minimal number of clusters"
				,
			examples = { @example(value = "clustering_xmeans([ag1, ag2, ag3, ag4, ag5],[\"size\",\"age\", \"weight\", \"is_male\"],[\"bin_value\"::1.0, \"distance_f\"::\"manhattan\", \"max_num_clusters\":10, \"min_num_clusters\":2])",
				equals = "for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]",
				isExecutable = false) }, see = { "clustering_simple_kmeans", "clustering_em","clustering_farthestFirst","clustering_DBScan", "clustering_cobweb"})
		public static IList<IList<IAgent>> primClusteringXMeans(final IScope scope,
			final IList<IAgent> agents,IList<String> attributes, GamaMap<String, Object> parameters)
			throws GamaRuntimeException {
		XMeans xmeans = new XMeans();
		xmeans.setSeed(Cast.asInt(scope, scope.getRandom().getSeed()));
		
		if (parameters != null) {
			if (parameters.containsKey("bin_value")) {
				xmeans.setBinValue(Cast.asFloat(scope, parameters.get("bin_value")));
			}
			if (parameters.containsKey("cut_off_factor")) {
				xmeans.setCutOffFactor(Cast.asFloat(scope, parameters.get("cut_off_factor")));
			}
			
			
			if (parameters.containsKey("distance_f")) {
				String distanceFct = Cast.asString(scope, parameters.get("distance_f"));
				if (distanceFct.equals("chebyshev")) {
					xmeans.setDistanceF(new ChebyshevDistance());
				} else if (distanceFct.equals("manhattan")) {
					xmeans.setDistanceF(new ManhattanDistance());
				} else if (distanceFct.equals("levenshtein")) {
					xmeans.setDistanceF(new EditDistance());
				}
			}
			if (parameters.containsKey("max_iterations")) {
				try {
					xmeans.setMaxIterations(Cast.asInt(scope, parameters.get("max_iterations")));
				} catch (Exception e) {
				}
			}
			if (parameters.containsKey("max_kmeans")) {
				xmeans.setMaxKMeans(Cast.asInt(scope, parameters.get("max_kmeans")));
			}
			if (parameters.containsKey("max_kmeans_for_children")) {
				xmeans.setMaxKMeansForChildren(Cast.asInt(scope, parameters.get("max_kmeans_for_children")));
			}
			if (parameters.containsKey("max_num_clusters")) {
				xmeans.setMaxNumClusters(Cast.asInt(scope, parameters.get("max_num_clusters")));
			}
			if (parameters.containsKey("min_num_clusters")) {
				xmeans.setMinNumClusters(Cast.asInt(scope, parameters.get("min_num_clusters")));
			}
		}
		
		IList<IList<IAgent>> groupes = clusteringUsingWeka(scope, xmeans,
				attributes, agents);

		return groupes;
	}

	/*
	 * distance_f -- The distance function to use for instances comparison
	 * (default: weka.core.EuclideanDistance). dont_replace_missing_values --
	 * Replace missing values globally with mean/mode. max_iterations -- set
	 * maximum number of iterations num_clusters -- set number of clusters
	 * preserve_instances_order -- Preserve order of instances. seed -- The
	 * random number seed to be used.
	 */
	@action(name = "clustering_simple_kmeans", args = {
			@arg(name = "agents", type = IType.LIST, optional = false),
			@arg(name = "attributes", type = IType.LIST, optional = false),
			@arg(name = "distance_f", optional = true),
			@arg(name = "dont_replace_missing_values", optional = true),
			@arg(name = "max_iterations", optional = true),
			@arg(name = "num_clusters", optional = true),
			@arg(name = "preserve_instances_order", optional = true),
			@arg(name = "seed", optional = true) })
	// @args(names = { "agents", "attributes", "distance_f",
	// "dont_replace_missing_values",
	// "max_iterations", "num_clusters", "preserve_instances_order", "seed" })
	public List<List<IAgent>> primClusteringSimpleKMeans(final IScope scope)
			throws GamaRuntimeException {
		final IList<IAgent> agents = scope.getListArg("agents");
		IList<String> attributes = scope.getListArg("attributes");
		SimpleKMeans kmeans = new SimpleKMeans();
		try {
			if (scope.hasArg("distance_f")) {
				String distanceFct = scope.getStringArg("distance_f");
				if (distanceFct.equals("chebyshev")) {
					kmeans.setDistanceFunction(new ChebyshevDistance());
				} else if (distanceFct.equals("manhattan")) {
					kmeans.setDistanceFunction(new ManhattanDistance());
				} else if (distanceFct.equals("levenshtein")) {
					kmeans.setDistanceFunction(new EditDistance());
				}
			}
			if (scope.hasArg("dont_replace_missing_values")) {
				kmeans.setDontReplaceMissingValues(scope
						.getBoolArg("dont_replace_missing_values"));
			}
			if (scope.hasArg("max_iterations")) {
				kmeans.setMaxIterations(scope.getIntArg("max_iterations"));
			}
			if (scope.hasArg("num_clusters")) {
				kmeans.setNumClusters(scope.getIntArg("num_clusters"));
			}
			if (scope.hasArg("preserve_instances_order")) {
				kmeans.setPreserveInstancesOrder(scope
						.getBoolArg("preserve_instances_order"));
			}
			if (scope.hasArg("seed")) {
				kmeans.setSeed(scope.getIntArg("seed"));
			}
		} catch (Exception e) {
			return null;
		}
		List<List<IAgent>> groupes = clusteringUsingWeka(scope, kmeans,
				attributes, agents);

		return groupes;
	}

	/*
	 * max_iterations -- set maximum number of iterations num_clusters -- set
	 * number of clusters minStdDev -- set minimum allowable standard deviation
	 * seed -- The random number seed to be used.
	 */
	@action(name = "clustering_em")
	@args(names = { "agents", "attributes", "max_iterations", "num_clusters",
			"min_std_dev", "seed" })
	public List<List<IAgent>> primClusteringEM(final IScope scope)
			throws GamaRuntimeException {
		final IList<IAgent> agents = scope.getListArg("agents");
		IList<String> attributes = scope.getListArg("attributes");
		EM em = new EM();
		try {

			if (scope.hasArg("max_iterations")) {
				em.setMaxIterations(scope.getIntArg("max_iterations"));
			}
			if (scope.hasArg("num_clusters")) {
				em.setNumClusters(scope.getIntArg("num_clusters"));
			}
			if (scope.hasArg("min-std")) {
				em.setMinStdDev(scope.getFloatArg("min_std_dev"));
			}
			if (scope.hasArg("seed")) {
				em.setSeed(scope.getIntArg("seed"));
			}
		} catch (Exception e) {
			return null;
		}
		List<List<IAgent>> groupes = clusteringUsingWeka(scope, em, attributes,
				agents);

		return groupes;
	}

	/*
	 * max_iterations -- set maximum number of iterations num_clusters -- set
	 * number of clusters minStdDev -- set minimum allowable standard deviation
	 * seed -- The random number seed to be used.
	 */
	@action(name = "clustering_farthestFirst")
	@args(names = { "agents", "attributes", "num_clusters", "seed" })
	public List<List<IAgent>> primClusteringFarthestFirst(final IScope scope)
			throws GamaRuntimeException {
		final IList<IAgent> agents = scope.getListArg("agents");
		IList<String> attributes = scope.getListArg("attributes");
		FarthestFirst ff = new FarthestFirst();
		try {

			if (scope.hasArg("num_clusters")) {
				ff.setNumClusters(scope.getIntArg("num_clusters"));
			}
			if (scope.hasArg("seed")) {
				ff.setSeed(scope.getIntArg("seed"));
			}
		} catch (Exception e) {
			return null;
		}
		List<List<IAgent>> groupes = clusteringUsingWeka(scope, ff, attributes,
				agents);

		return groupes;
	}

	/*
	 * distance_f -- The distance function to use for instances comparison
	 * (Euclidean or Manhattan), Default: Euclidean (default:
	 * weka.core.EuclideanDistance). epsilon -- radius of the
	 * epsilon-range-queries minPoints -- minimun number of DataObjects required
	 * in an epsilon-range-query
	 */
	@action(name = "clustering_DBScan")
	@args(names = { "agents", "attributes", "distance_f", "epsilon",
			"min_points" })
	public List<List<IAgent>> primClusteringDBScan(final IScope scope)
			throws GamaRuntimeException {
		final IList<IAgent> agents = scope.getListArg("agents");
		IList<String> attributes = scope.getListArg("attributes");
		DBSCAN dbScan = new DBSCAN();
		try {
			if (scope.hasArg("distance_f")) {
				String distanceFct = scope.getStringArg("distance_f");
				if (distanceFct.equals("manhattan")) {
					dbScan.setDatabase_distanceType(ManhattanDataObject.class
							.getName());
				} else {
					dbScan.setDatabase_distanceType(EuclideanDistance.class
							.getName());
				}
			}
			if (scope.hasArg("epsilon")) {
				dbScan.setEpsilon(scope.getFloatArg("epsilon"));
			}
			if (scope.hasArg("min_points")) {
				dbScan.setMinPoints(scope.getIntArg("min_points"));
			}
		} catch (Exception e) {
			return null;
		}
		List<List<IAgent>> groupes = clusteringUsingWeka(scope, dbScan,
				attributes, agents);

		return groupes;
	}

	/*
	 * acuity -- minimum standard deviation for numeric attributes cutoff --
	 * category utility threshold by which to prune nodes seed -- random number
	 * seed to be used.
	 */
	@action(name = "clustering_cobweb")
	@args(names = { "agents", "attributes", "acuity", "cutoff", "seed" })
	public List<List<IAgent>> primClusteringCobweb(final IScope scope)
			throws GamaRuntimeException {
		final IList<IAgent> agents = scope.getListArg("agents");
		IList<String> attributes = scope.getListArg("attributes");
		Cobweb cutOff = new Cobweb();
		try {

			if (scope.hasArg("acuity")) {
				cutOff.setAcuity(scope.getFloatArg("acuity"));
			}
			if (scope.hasArg("cutoff")) {
				cutOff.setCutoff(scope.getFloatArg("cutoff"));
			}
			if (scope.hasArg("seed")) {
				cutOff.setSeed(scope.getIntArg("seed"));
			}
		} catch (Exception e) {
			return null;
		}
		List<List<IAgent>> groupes = clusteringUsingWeka(scope, cutOff,
				attributes, agents);

		return groupes;

	}
}
