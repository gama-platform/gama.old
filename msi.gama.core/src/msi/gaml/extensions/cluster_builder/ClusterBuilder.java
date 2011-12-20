/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.extensions.cluster_builder;

import java.util.List;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import weka.clusterers.*;
import weka.clusterers.forOPTICSAndDBScan.DataObjects.ManhattanDataObject;
import weka.core.*;

@species("cluster_builder")
public class ClusterBuilder extends GamlAgent {

	public ClusterBuilder(final ISimulation sim, final IPopulation s) throws GamaRuntimeException {
		super(sim, s);
	}

	private Instances convertToInstances(final IList<String> attributes, final IList<IAgent> agents)
		throws GamaRuntimeException {
		FastVector attribs = new FastVector();
		for ( String att : attributes ) {
			attribs.addElement(new Attribute(att));
		}
		Instances dataset = new Instances(getName(), attribs, agents.size());
		for ( IAgent ag : agents ) {

			int nb = attributes.size();
			double vals[] = new double[nb];
			for ( int i = 0; i < nb; i++ ) {
				String attrib = attributes.get(i);
				Double var =
					Cast.asFloat(simulation.getGlobalScope(), ag.getDirectVarValue(attrib));
				vals[i] = var;
			}
			Instance instance = new Instance(1, vals);
			dataset.add(instance);
		}
		return dataset;
	}

	private List<List<IAgent>> clusteringUsingWeka(final Clusterer clusterer,
		final IList<String> attributes, final IList<IAgent> agents) throws GamaRuntimeException {
		Instances dataset = convertToInstances(attributes, agents);
		try {
			clusterer.buildClusterer(dataset);

			List<List<IAgent>> groupes = new GamaList<List<IAgent>>();

			for ( int i = 0; i < clusterer.numberOfClusters(); i++ ) {
				groupes.add(new GamaList<IAgent>());
			}
			for ( int i = 0; i < dataset.numInstances(); i++ ) {
				Instance inst = dataset.instance(i);
				int clusterIndex = -1;
				clusterIndex = clusterer.clusterInstance(inst);
				List<IAgent> groupe = groupes.get(clusterIndex);
				groupe.add(agents.get(i));
			}
			return groupes;
		} catch (Exception e) {
			return null;
		}

	}

	/*
	 * bin_value -- Set the value that represents true in the new attributes. cut_off_factor -- the
	 * cut-off factor to use distance_f -- The distance function to use : String : 4 possible
	 * distance functions: (by default) euclidean; otherwise 'chebyshev', 'manhattan' and
	 * 'levenshtein' max_iterations -- the maximum number of iterations to perform max_kmeans -- the
	 * maximum number of iterations to perform in KMeans max_kmeans_for_children -- the maximum
	 * number of iterations KMeans that is performed on the child centers max_num_clusters -- set
	 * maximum number of clusters min_num_clusters -- set minimum number of clusters seed -- The
	 * random number seed to be used.
	 */
	@action("clustering_xmeans")
	@args({ "agents", "attributes", "bin_value", "cut_off_factor", "distance_f", "max_iterations",
		"max_kmeans", "max_kmeans_for_children", "max_kmeans_for_children", "max_num_clusters",
		"min_num_clusters", "seed" })
	public List<List<IAgent>> primClusteringXMeans(final IScope scope) throws GamaRuntimeException {
		final IList<IAgent> agents = scope.getListArg("agents");
		IList<String> attributes = scope.getListArg("attributes");
		XMeans xmeans = new XMeans();
		if ( scope.hasArg("bin_value") ) {
			xmeans.setBinValue(scope.getFloatArg("bin_value"));
		}
		if ( scope.hasArg("cut_off_factor") ) {
			xmeans.setCutOffFactor(scope.getFloatArg("cut_off_factor"));
		}
		if ( scope.hasArg("distance_f") ) {
			String distanceFct = scope.getStringArg("distance_f");
			if ( distanceFct.equals("chebyshev") ) {
				xmeans.setDistanceF(new ChebyshevDistance());
			} else if ( distanceFct.equals("manhattan") ) {
				xmeans.setDistanceF(new ManhattanDistance());
			} else if ( distanceFct.equals("levenshtein") ) {
				xmeans.setDistanceF(new EditDistance());
			}
		}
		if ( scope.hasArg("max_iterations") ) {
			try {
				xmeans.setMaxIterations((Integer) scope.getArg("max_iterations", IType.INT));
			} catch (Exception e) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
		}
		if ( scope.hasArg("max_kmeans") ) {
			xmeans.setMaxKMeans(scope.getIntArg("max_kmeans"));
		}
		if ( scope.hasArg("max_kmeans_for_children") ) {
			xmeans.setMaxKMeansForChildren(scope.getIntArg("max_kmeans_for_children"));
		}
		if ( scope.hasArg("max_num_clusters") ) {
			xmeans.setMaxNumClusters(scope.getIntArg("max_num_clusters"));
		}
		if ( scope.hasArg("min_num_clusters") ) {
			xmeans.setMinNumClusters(scope.getIntArg("min_num_clusters"));
		}
		if ( scope.hasArg("seed") ) {
			xmeans.setSeed(scope.getIntArg("seed"));
		}

		List<List<IAgent>> groupes = clusteringUsingWeka(xmeans, attributes, agents);
		{
			scope.setStatus(groupes == null ? ExecutionStatus.failure : ExecutionStatus.success);
		}
		return groupes;
	}

	/*
	 * distance_f -- The distance function to use for instances comparison (default:
	 * weka.core.EuclideanDistance). dont_replace_missing_values -- Replace missing values globally
	 * with mean/mode. max_iterations -- set maximum number of iterations num_clusters -- set number
	 * of clusters preserve_instances_order -- Preserve order of instances. seed -- The random
	 * number seed to be used.
	 */
	@action("clustering_simple_kmeans")
	@args({ "agents", "attributes", "distance_f", "dont_replace_missing_values", "max_iterations",
		"num_clusters", "preserve_instances_order", "seed" })
	public List<List<IAgent>> primClusteringSimpleKMeans(final IScope scope)
		throws GamaRuntimeException {
		final IList<IAgent> agents = scope.getListArg("agents");
		IList<String> attributes = scope.getListArg("attributes");
		SimpleKMeans kmeans = new SimpleKMeans();
		try {
			if ( scope.hasArg("distance_f") ) {
				String distanceFct = scope.getStringArg("distance_f");
				if ( distanceFct.equals("chebyshev") ) {
					kmeans.setDistanceFunction(new ChebyshevDistance());
				} else if ( distanceFct.equals("manhattan") ) {
					kmeans.setDistanceFunction(new ManhattanDistance());
				} else if ( distanceFct.equals("levenshtein") ) {
					kmeans.setDistanceFunction(new EditDistance());
				}
			}
			if ( scope.hasArg("dont_replace_missing_values") ) {
				kmeans.setDontReplaceMissingValues(scope.getBoolArg("dont_replace_missing_values"));
			}
			if ( scope.hasArg("max_iterations") ) {
				kmeans.setMaxIterations(scope.getIntArg("max_iterations"));
			}
			if ( scope.hasArg("num_clusters") ) {
				kmeans.setNumClusters(scope.getIntArg("num_clusters"));
			}
			if ( scope.hasArg("preserve_instances_order") ) {
				kmeans.setPreserveInstancesOrder(scope.getBoolArg("preserve_instances_order"));
			}
			if ( scope.hasArg("seed") ) {
				kmeans.setSeed(scope.getIntArg("seed"));
			}
		} catch (Exception e) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		List<List<IAgent>> groupes = clusteringUsingWeka(kmeans, attributes, agents);
		{
			scope.setStatus(groupes == null ? ExecutionStatus.failure : ExecutionStatus.success);
		}
		return groupes;
	}

	/*
	 * max_iterations -- set maximum number of iterations num_clusters -- set number of clusters
	 * minStdDev -- set minimum allowable standard deviation seed -- The random number seed to be
	 * used.
	 */
	@action("clustering_em")
	@args({ "agents", "attributes", "max_iterations", "num_clusters", "min_std_dev", "seed" })
	public List<List<IAgent>> primClusteringEM(final IScope scope) throws GamaRuntimeException {
		final IList<IAgent> agents = scope.getListArg("agents");
		IList<String> attributes = scope.getListArg("attributes");
		EM em = new EM();
		try {

			if ( scope.hasArg("max_iterations") ) {
				em.setMaxIterations(scope.getIntArg("max_iterations"));
			}
			if ( scope.hasArg("num_clusters") ) {
				em.setNumClusters(scope.getIntArg("num_clusters"));
			}
			if ( scope.hasArg("min-std") ) {
				em.setMinStdDev(scope.getFloatArg("min_std_dev"));
			}
			if ( scope.hasArg("seed") ) {
				em.setSeed(scope.getIntArg("seed"));
			}
		} catch (Exception e) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		List<List<IAgent>> groupes = clusteringUsingWeka(em, attributes, agents);
		{
			scope.setStatus(groupes == null ? ExecutionStatus.failure : ExecutionStatus.success);
		}
		return groupes;
	}

	/*
	 * max_iterations -- set maximum number of iterations num_clusters -- set number of clusters
	 * minStdDev -- set minimum allowable standard deviation seed -- The random number seed to be
	 * used.
	 */
	@action("clustering_farthestFirst")
	@args({ "agents", "attributes", "num_clusters", "seed" })
	public List<List<IAgent>> primClusteringFarthestFirst(final IScope scope)
		throws GamaRuntimeException {
		final IList<IAgent> agents = scope.getListArg("agents");
		IList<String> attributes = scope.getListArg("attributes");
		FarthestFirst ff = new FarthestFirst();
		try {

			if ( scope.hasArg("num_clusters") ) {
				ff.setNumClusters(scope.getIntArg("num_clusters"));
			}
			if ( scope.hasArg("seed") ) {
				ff.setSeed(scope.getIntArg("seed"));
			}
		} catch (Exception e) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		List<List<IAgent>> groupes = clusteringUsingWeka(ff, attributes, agents);
		{
			scope.setStatus(groupes == null ? ExecutionStatus.failure : ExecutionStatus.success);
		}
		return groupes;
	}

	/*
	 * distance_f -- The distance function to use for instances comparison (Euclidean or Manhattan),
	 * Default: Euclidean (default: weka.core.EuclideanDistance). epsilon -- radius of the
	 * epsilon-range-queries minPoints -- minimun number of DataObjects required in an
	 * epsilon-range-query
	 */
	@action("clustering_DBScan")
	@args({ "agents", "attributes", "distance_f", "epsilon", "min_points" })
	public List<List<IAgent>> primClusteringDBScan(final IScope scope) throws GamaRuntimeException {
		final IList<IAgent> agents = scope.getListArg("agents");
		IList<String> attributes = scope.getListArg("attributes");
		DBScan dbScan = new DBScan();
		try {
			if ( scope.hasArg("distance_f") ) {
				String distanceFct = scope.getStringArg("distance_f");
				if ( distanceFct.equals("manhattan") ) {
					dbScan.setDatabase_distanceType(ManhattanDataObject.class.getName());
				} else {
					dbScan.setDatabase_distanceType(EuclideanDistance.class.getName());
				}
			}
			if ( scope.hasArg("epsilon") ) {
				dbScan.setEpsilon(scope.getFloatArg("epsilon"));
			}
			if ( scope.hasArg("min_points") ) {
				dbScan.setMinPoints(scope.getIntArg("min_points"));
			}
		} catch (Exception e) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		List<List<IAgent>> groupes = clusteringUsingWeka(dbScan, attributes, agents);
		{
			scope.setStatus(groupes == null ? ExecutionStatus.failure : ExecutionStatus.success);
		}
		return groupes;
	}

	/*
	 * acuity -- minimum standard deviation for numeric attributes cutoff -- category utility
	 * threshold by which to prune nodes seed -- random number seed to be used.
	 */
	@action("clustering_cobweb")
	@args({ "agents", "attributes", "acuity", "cutoff", "seed" })
	public List<List<IAgent>> primClusteringCobweb(final IScope scope) throws GamaRuntimeException {
		final IList<IAgent> agents = scope.getListArg("agents");
		IList<String> attributes = scope.getListArg("attributes");
		Cobweb cutOff = new Cobweb();
		try {

			if ( scope.hasArg("acuity") ) {
				cutOff.setAcuity(scope.getFloatArg("acuity"));
			}
			if ( scope.hasArg("cutoff") ) {
				cutOff.setCutoff(scope.getFloatArg("cutoff"));
			}
			if ( scope.hasArg("seed") ) {
				cutOff.setSeed(scope.getIntArg("seed"));
			}
		} catch (Exception e) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		List<List<IAgent>> groupes = clusteringUsingWeka(cutOff, attributes, agents);
		{
			scope.setStatus(groupes == null ? ExecutionStatus.failure : ExecutionStatus.success);
		}
		return groupes;

	}

}
