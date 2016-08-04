package idees.gama.operators;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import weka.clusterers.*;
import weka.clusterers.forOPTICSAndDBScan.DataObjects.ManhattanDataObject;
import weka.core.*;
import weka.core.Instance;

public class Clustering {

	private static Instances convertToInstances(final IScope scope, final IList<String> attributes,
		final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents) throws GamaRuntimeException {
		FastVector attribs = new FastVector();
		for ( String att : attributes ) {
			attribs.addElement(new Attribute(att));
		}
		Instances dataset = new Instances(scope.getAgent().getName(), attribs, agents.length(scope));
		for ( IAgent ag : agents.iterable(scope) ) {

			int nb = attributes.size();
			double vals[] = new double[nb];
			for ( int i = 0; i < nb; i++ ) {
				String attrib = attributes.get(i);
				Double var = Cast.asFloat(scope, ag.getDirectVarValue(scope, attrib));
				vals[i] = var;
			}
			Instance instance = new Instance(1, vals);
			dataset.add(instance);
		}
		return dataset;
	}

	private static IList<IList<IAgent>> clusteringUsingWeka(final IScope scope, final Clusterer clusterer,
		final IList<String> attributes, final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents)
		throws GamaRuntimeException {
		Instances dataset = convertToInstances(scope, attributes, agents);
		try {
			clusterer.buildClusterer(dataset);

			IList<IList<IAgent>> groupes = GamaListFactory.create(Types.LIST.of(Types.AGENT));

			for ( int i = 0; i < clusterer.numberOfClusters(); i++ ) {
				groupes.add(GamaListFactory.<IAgent> create(Types.AGENT));
			}
			for ( int i = 0; i < dataset.numInstances(); i++ ) {
				Instance inst = dataset.instance(i);
				int clusterIndex = -1;
				clusterIndex = clusterer.clusterInstance(inst);
				IList<IAgent> groupe = groupes.get(clusterIndex);
				groupe.add(agents.get(scope, i));
			}
			return groupes;
		} catch (Exception e) {
			return null;
		}

	}

	@operator(value = { "clustering_xmeans" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "A list of agent groups clustered by X-Means Algorithm based on the given attributes. Some paremeters can be defined: bin_value: value given for true value of boolean attributes; cut_off_factor: the cut-off factor to use;"
		+ "distance_f: The distance function to use. 4 possible distance functions: euclidean (by default) ; 'chebyshev', 'manhattan' or 'levenshtein'; "
		+ "max_iterations: the maximum number of iterations to perform; max_kmeans: the maximum number of iterations to perform in KMeans; max_kmeans_for_children: the maximum number of iterations KMeans that is performed on the child centers;"
		+ "max_num_clusters: the maximum number of clusters; min_num_clusters: the minimal number of clusters",
		examples = { @example(value = "clustering_xmeans([ag1, ag2, ag3, ag4, ag5],[\"size\",\"age\", \"weight\", \"is_male\"],[\"bin_value\"::1.0, \"distance_f\"::\"manhattan\", \"max_num_clusters\"::10, \"min_num_clusters\"::2])",
			equals = "for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]",
			isExecutable = false) },
		see = { "clustering_simple_kmeans", "clustering_em", "clustering_farthestFirst", "clustering_DBScan",
			"clustering_cobweb" })
	public static
		IList<IList<IAgent>> primClusteringXMeans(final IScope scope,
			final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents, final IList<String> attributes,
			final GamaMap<String, Object> parameters) throws GamaRuntimeException {
		XMeans xmeans = new XMeans();
		xmeans.setSeed(Cast.asInt(scope, scope.getRandom().getSeed()));

		if ( parameters != null ) {
			if ( parameters.containsKey("bin_value") ) {
				xmeans.setBinValue(Cast.asFloat(scope, parameters.get("bin_value")));
			}
			if ( parameters.containsKey("cut_off_factor") ) {
				xmeans.setCutOffFactor(Cast.asFloat(scope, parameters.get("cut_off_factor")));
			}

			if ( parameters.containsKey("distance_f") ) {
				String distanceFct = Cast.asString(scope, parameters.get("distance_f"));
				if ( distanceFct.equals("chebyshev") ) {
					xmeans.setDistanceF(new ChebyshevDistance());
				} else if ( distanceFct.equals("manhattan") ) {
					xmeans.setDistanceF(new ManhattanDistance());
				} else if ( distanceFct.equals("levenshtein") ) {
					xmeans.setDistanceF(new EditDistance());
				}
			}
			if ( parameters.containsKey("max_iterations") ) {
				try {
					xmeans.setMaxIterations(Cast.asInt(scope, parameters.get("max_iterations")));
				} catch (Exception e) {}
			}
			if ( parameters.containsKey("max_kmeans") ) {
				xmeans.setMaxKMeans(Cast.asInt(scope, parameters.get("max_kmeans")));
			}
			if ( parameters.containsKey("max_kmeans_for_children") ) {
				xmeans.setMaxKMeansForChildren(Cast.asInt(scope, parameters.get("max_kmeans_for_children")));
			}
			if ( parameters.containsKey("max_num_clusters") ) {
				xmeans.setMaxNumClusters(Cast.asInt(scope, parameters.get("max_num_clusters")));
			}
			if ( parameters.containsKey("min_num_clusters") ) {
				xmeans.setMinNumClusters(Cast.asInt(scope, parameters.get("min_num_clusters")));
			}
		}

		IList<IList<IAgent>> groupes = clusteringUsingWeka(scope, xmeans, attributes, agents);

		return groupes;
	}

	@operator(value = { "clustering_simple_kmeans" },
		content_type = IType.LIST,
		category = { IOperatorCategory.STATISTICAL },
		concept = { IConcept.STATISTIC })
	@doc(value = "A list of agent groups clustered by K-Means Algorithm based on the given attributes. Some paremeters can be defined: "
		+ "distance_f: The distance function to use. 4 possible distance functions: euclidean (by default) ; 'chebyshev', 'manhattan' or 'levenshtein'; "
		+ "dont_replace_missing_values: if false, replace missing values globally with mean/mode; max_iterations: the maximum number of iterations to perform;"
		+ "num_clusters: the number of clusters",
		examples = { @example(value = "clustering_simple_kmeans([ag1, ag2, ag3, ag4, ag5],[\"size\",\"age\", \"weight\"],[\"distance_f\"::\"manhattan\", \"num_clusters\"::3])",
			equals = "for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]",
			isExecutable = false) },
		see = { "clustering_xmeans", "clustering_em", "clustering_farthestFirst", "clustering_DBScan",
			"clustering_cobweb" })
	public static
		IList<IList<IAgent>> primClusteringSimpleKMeans(final IScope scope,
			final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents, final IList<String> attributes,
			final GamaMap<String, Object> parameters) {
		SimpleKMeans kmeans = new SimpleKMeans();
		kmeans.setSeed(Cast.asInt(scope, scope.getRandom().getSeed()));

		if ( parameters != null ) {
			try {
				if ( parameters.containsKey("distance_f") ) {
					String distanceFct = Cast.asString(scope, parameters.get("distance_f"));
					if ( distanceFct.equals("chebyshev") ) {
						kmeans.setDistanceFunction(new ChebyshevDistance());
					} else if ( distanceFct.equals("manhattan") ) {
						kmeans.setDistanceFunction(new ManhattanDistance());
					} else if ( distanceFct.equals("levenshtein") ) {
						kmeans.setDistanceFunction(new EditDistance());
					}
				}
				if ( parameters.containsKey("dont_replace_missing_values") ) {
					kmeans
						.setDontReplaceMissingValues(Cast.asBool(scope, parameters.get("dont_replace_missing_values")));
				}
				if ( parameters.containsKey("max_iterations") ) {
					kmeans.setMaxIterations(Cast.asInt(scope, parameters.get("max_iterations")));
				}
				if ( parameters.containsKey("num_clusters") ) {
					kmeans.setNumClusters(Cast.asInt(scope, parameters.get("num_clusters")));
				}
			} catch (Exception e) {}
		}

		IList<IList<IAgent>> groupes = clusteringUsingWeka(scope, kmeans, attributes, agents);

		return groupes;
	}

	@operator(value = { "clustering_em" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "A list of agent groups clustered by EM Algorithm based on the given attributes. Some paremeters can be defined: "
		+ "max_iterations: the maximum number of iterations to perform;"
		+ "num_clusters: the number of clusters; minStdDev: minimum allowable standard deviation",
		examples = { @example(value = "clustering_em([ag1, ag2, ag3, ag4, ag5],[\"size\",\"age\", \"weight\"],[\"max_iterations\"::10, \"num_clusters\"::3])",
			equals = "for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]",
			isExecutable = false) },
		see = { "clustering_xmeans", "clustering_simple_kmeans", "clustering_farthestFirst", "clustering_DBScan",
			"clustering_cobweb" })
	public static
		IList<IList<IAgent>> primClusteringEM(final IScope scope,
			final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents, final IList<String> attributes,
			final GamaMap<String, Object> parameters) {
		EM em = new EM();
		em.setSeed(Cast.asInt(scope, scope.getRandom().getSeed()));

		if ( parameters != null ) {
			try {

				if ( parameters.containsKey("max_iterations") ) {
					em.setMaxIterations(Cast.asInt(scope, parameters.get("max_iterations")));
				}
				if ( parameters.containsKey("num_clusters") ) {
					em.setNumClusters(Cast.asInt(scope, parameters.get("num_clusters")));
				}
				if ( parameters.containsKey("minStdDev") ) {
					em.setMinStdDev(Cast.asFloat(scope, parameters.get("minStdDev")));
				}
			} catch (Exception e) {

			}
		}
		IList<IList<IAgent>> groupes = clusteringUsingWeka(scope, em, attributes, agents);
		return groupes;
	}

	@operator(value = { "clustering_farthestFirst" },
		content_type = IType.LIST,
		category = { IOperatorCategory.STATISTICAL },
		concept = { IConcept.STATISTIC })
	@doc(value = "A list of agent groups clustered by Farthest First Algorithm based on the given attributes. Some paremeters can be defined: "
		+ "num_clusters: the number of clusters",
		examples = { @example(value = "clustering_farthestFirst([ag1, ag2, ag3, ag4, ag5],[\"size\",\"age\", \"weight\"],[\"num_clusters\"::3])",
			equals = "for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]",
			isExecutable = false) },
		see = { "clustering_xmeans", "clustering_simple_kmeans", "clustering_em", "clustering_DBScan",
			"clustering_cobweb" })
	public static
		IList<IList<IAgent>> primClusteringFarthestFirst(final IScope scope,
			final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents, final IList<String> attributes,
			final GamaMap<String, Object> parameters) {
		FarthestFirst ff = new FarthestFirst();
		ff.setSeed(Cast.asInt(scope, scope.getRandom().getSeed()));

		if ( parameters != null ) {
			try {
				if ( parameters.containsKey("num_clusters") ) {
					ff.setNumClusters(Cast.asInt(scope, parameters.get("num_clusters")));
				}
			} catch (Exception e) {

			}
		}
		IList<IList<IAgent>> groupes = clusteringUsingWeka(scope, ff, attributes, agents);
		return groupes;
	}

	@operator(value = { "clustering_DBScan" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "A list of agent groups clustered by DBScan Algorithm based on the given attributes. Some paremeters can be defined: "
		+ "distance_f: The distance function to use for instances comparison (euclidean or manhattan); "
		+ "min_points: minimun number of DataObjects required in an epsilon-range-query"
		+ "epsilon: epsilon -- radius of the epsilon-range-queries",
		examples = { @example(value = "clustering_DBScan([ag1, ag2, ag3, ag4, ag5],[\"size\",\"age\", \"weight\"],[\"distance_f\"::\"manhattan\"])",
			equals = "for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]",
			isExecutable = false) },
		see = { "clustering_xmeans", "clustering_em", "clustering_farthestFirst", "clustering_simple_kmeans",
			"clustering_cobweb" })
	public static
		IList<IList<IAgent>> primClusteringDBScan(final IScope scope,
			final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents, final IList<String> attributes,
			final GamaMap<String, Object> parameters) {
		DBSCAN dbScan = new DBSCAN();

		if ( parameters != null ) {
			try {
				if ( parameters.containsKey("distance_f") ) {
					String distanceFct = Cast.asString(scope, parameters.get("distance_f"));
					if ( distanceFct.equals("manhattan") ) {
						dbScan.setDatabase_distanceType(ManhattanDataObject.class.getName());
					} else {
						dbScan.setDatabase_distanceType(EuclideanDistance.class.getName());
					}
				}

				if ( parameters.containsKey("min_points") ) {
					dbScan.setMinPoints(Cast.asInt(scope, parameters.get("min_points")));
				}
				if ( parameters.containsKey("epsilon") ) {
					dbScan.setEpsilon(Cast.asInt(scope, parameters.get("epsilon")));
				}
			} catch (Exception e) {}
		}

		IList<IList<IAgent>> groupes = clusteringUsingWeka(scope, dbScan, attributes, agents);

		return groupes;
	}

	@operator(value = { "clustering_cobweb" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "A list of agent groups clusteredby CobWeb Algorithm based on the given attributes. Some paremeters can be defined: "
		+ "acuity: minimum standard deviation for numeric attributes; "
		+ "cutoff: category utility threshold by which to prune nodes seed",
		examples = { @example(value = "clustering_cobweb([ag1, ag2, ag3, ag4, ag5],[\"size\",\"age\", \"weight\"],[\"acuity\"::3.0, \"cutoff\"::0.5)",
			equals = "for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]",
			isExecutable = false) },
		see = { "clustering_xmeans", "clustering_em", "clustering_farthestFirst", "clustering_simple_kmeans",
			"clustering_cobweb" })
	public static
		IList<IList<IAgent>> primClusteringCobweb(final IScope scope,
			final IAddressableContainer<Integer, IAgent, Integer, IAgent> agents, final IList<String> attributes,
			final GamaMap<String, Object> parameters) {
		Cobweb cobweb = new Cobweb();
		cobweb.setSeed(Cast.asInt(scope, scope.getRandom().getSeed()));

		if ( parameters != null ) {
			try {
				if ( parameters.containsKey("acuity") ) {
					cobweb.setAcuity(Cast.asFloat(scope, parameters.get("acuity")));
				}
				if ( parameters.containsKey("cutoff") ) {
					cobweb.setCutoff(Cast.asFloat(scope, parameters.get("cutoff")));
				}
			} catch (Exception e) {}
		}

		IList<IList<IAgent>> groupes = clusteringUsingWeka(scope, cobweb, attributes, agents);

		return groupes;
	}
}
