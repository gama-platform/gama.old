package yifanhu;

import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.multilevel.MaximalMatchingCoarsening;
import org.gephi.layout.plugin.multilevel.MultiLevelLayout;
import org.gephi.layout.plugin.multilevel.YifanHuMultiLevel;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.IGraph;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.types.IType;

@symbol(name = "layout_yifanhu", kind = ISymbolKind.SINGLE_STATEMENT, concept = {
		IConcept.GRAPH }, with_sequence = false, doc = @doc("Apply YifanHu Layout."))
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = {
		@facet(name = IKeyword.GRAPH, type = IType.GRAPH, optional = false, doc = @doc("the graph to apply the layout.")),
		@facet(name = GraphYiFanHuLayoutStatement.OPTIMAL_DIST, type = IType.FLOAT, optional = true, doc = @doc("the natural length of the springs. Bigger values mean nodes will be farther apart (default: 100).")),
		@facet(name = GraphYiFanHuLayoutStatement.QUAD_MAX, type = IType.INT, optional = true, doc = @doc("The maximum level to be used in the quadtree representation. Greater values mean more accuracy (default: 10).")),
		@facet(name = GraphYiFanHuLayoutStatement.BARNES_THETA, type = IType.FLOAT, optional = true, doc = @doc("The theta parameter for Barnes-Hut opening criteria. Smaller values mean more accuracy (default: 1.2).")),
		@facet(name = GraphYiFanHuLayoutStatement.RELATIVE_STRENGTH, type = IType.FLOAT, optional = true, doc = @doc("The relative strength between electrical force (repulsion) and spring force (attriaction). default: 0.2")),
		@facet(name = GraphYiFanHuLayoutStatement.STEP_SIZE, type = IType.FLOAT, optional = true, doc = @doc("The step size used in the algorithm. It has to be a meaningful size compared to the optimal distance (e.g. 10%). default: 10")),
		@facet(name = GraphYiFanHuLayoutStatement.NB_STEPS, type = IType.INT, optional = true, doc = @doc("The number of steps of the algorithm to perform (default 1).")),
		@facet(name = GraphYiFanHuLayoutStatement.BOUNDED_P1, type = IType.POINT, optional = true, doc = @doc("The new nodes positions are bounded within the two bound points if both are not null. default: null")),
		@facet(name = GraphYiFanHuLayoutStatement.BOUNDED_P2, type = IType.POINT, optional = true, doc = @doc("The new nodes positions are bounded within the two bound points if both are not null. default: null")) }, omissible = IKeyword.GRAPH)
@SuppressWarnings({ "rawtypes" })
public class GraphYiFanHuLayoutStatement extends AbstractGraphLayoutStatement {

	public GraphYiFanHuLayoutStatement(final IDescription desc) {
		super(desc);
		// TODO Auto-generated constructor stub
	}

	public static final String OPTIMAL_DIST = "optimal_distance";
	public static final String QUAD_MAX = "quadtree_max_level";
	public static final String BARNES_THETA = "theta";
	public static final String RELATIVE_STRENGTH = "relative_strength";
	public static final String STEP_SIZE = "step_size";
	public static final String NB_STEPS = "nb_steps";
	public static final String BOUNDED_P1 = "bounded_point1";
	public static final String BOUNDED_P2 = "bounded_point2";

	public static double stepvalue = -1;

	/*
	 * private static ProjectController pc; private static Workspace workspace;
	 * private static GraphModel graph_model; private static UndirectedGraph
	 * the_graph; private static boolean initialized = false; private static int
	 * node_index_diff = 0;
	 */

	private static final double OPTIMAL_DIST_DEFAULT = 100;
	private static final double STEP_RATIO_DEFAULT = 0.95;
	private static final double INITIAL_STEP_DEFAULT = 10.0;
	private static final double CONVERGENCE_THRESHOLD_DEFAULT = 0.95;
	private static final double BARNES_THETA_DEFAULT = 1.2;
	private static final int QUAD_MAX_DEFAULT = 10;
	private static final double RATIO_DEFAULT = 0.2;
	private static final int NB_STEPS_DEFAULT = 1;

	@Override
	protected Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		// IGraph data = createData(scope);
		final IGraph g = Cast.asGraph(scope, getFacetValue(scope, IKeyword.GRAPH));
		final double optimal_distance = Cast.asFloat(scope,
				getFacetValue(scope, GraphYiFanHuLayoutStatement.OPTIMAL_DIST, OPTIMAL_DIST_DEFAULT));
		final double initial_step = Cast.asFloat(scope,
				getFacetValue(scope, GraphYiFanHuLayoutStatement.STEP_SIZE, INITIAL_STEP_DEFAULT));
		final int nb_steps = Cast.asInt(scope,
				getFacetValue(scope, GraphYiFanHuLayoutStatement.NB_STEPS, NB_STEPS_DEFAULT));
		final double barnes_hut_theta = Cast.asFloat(scope,
				getFacetValue(scope, GraphYiFanHuLayoutStatement.BARNES_THETA, BARNES_THETA_DEFAULT));
		final int max_level = Cast.asInt(scope,
				getFacetValue(scope, GraphYiFanHuLayoutStatement.QUAD_MAX, QUAD_MAX_DEFAULT));
		final double relative_strength = Cast.asFloat(scope,
				getFacetValue(scope, GraphYiFanHuLayoutStatement.RELATIVE_STRENGTH, RATIO_DEFAULT));
		final Object bp1 = getFacetValue(scope, GraphYiFanHuLayoutStatement.BOUNDED_P1, null);
		final Object bp2 = getFacetValue(scope, GraphYiFanHuLayoutStatement.BOUNDED_P2, null);
		// public static IGraph YifanHuLayout_Step_Unlimited(final IGraph g,
		// double optimal_distance, double initial_step, double step_ratio,
		// double convergence_threshold, double barnes_hut_theta) {
		initializing();
		// DEBUG.LOG("Yifan Hu algorithm (by step), starting.");
		// DEBUG.LOG("converting ...");
		IGraph_to_GraphModel(g);
		// DEBUG.LOG("initialising ...");
		initializing_GraphModel(g);
		final YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
		InitializingYifanHuLayout(layout, optimal_distance, initial_step, STEP_RATIO_DEFAULT,
				CONVERGENCE_THRESHOLD_DEFAULT, barnes_hut_theta, max_level, relative_strength);
		// layout.goAlgo();
		// for ( int i = 0; i < nb_steps && layout.canAlgo(); i++ ) {
		for (int i = 0; i < nb_steps; i++) {
			layout.goAlgo();
			// DEBUG.LOG("..." + i + "/" + nb_steps + "/" +
			// layout.canAlgo());
		}
		if (bp1 != null && bp2 != null) {
			final ILocation p1 = Cast.asPoint(scope, bp1);
			final ILocation p2 = Cast.asPoint(scope, bp2);
			Update_locations(g, FastMath.min(p1.getX(), p2.getX()), FastMath.min(p1.getY(), p2.getY()),
					Math.max(p1.getX(), p2.getX()), FastMath.max(p1.getY(), p2.getY()));
		} else {
			Update_locations(g);
		}
		// DEBUG.LOG("ended.");
		// return g;
		// }

		return g;

	}

	// Initializing the layout with the given parameters
	private static void InitializingYifanHuLayout(final YifanHuLayout YHL, final double optimal_distance,
			final double initial_step, final double step_ratio, final double convergence_threshold,
			final double barnes_hut_theta) {
		YHL.resetPropertiesValues();
		YHL.setGraphModel(graph_model);
		YHL.setOptimalDistance((float) optimal_distance);
		YHL.setInitialStep((float) initial_step);
		YHL.setStepRatio((float) step_ratio);
		YHL.setBarnesHutTheta((float) barnes_hut_theta);
		YHL.setConvergenceThreshold((float) convergence_threshold);
	}

	// Initializing the layout with the given parameters including relative
	// strength
	private static void InitializingYifanHuLayout(final YifanHuLayout YHL, final double optimal_distance,
			final double initial_step, final double step_ratio, final double convergence_threshold,
			final double barnes_hut_theta, final double max_level, final double relative_strength) {
		YHL.resetPropertiesValues();
		YHL.setGraphModel(graph_model);
		YHL.setOptimalDistance((float) optimal_distance);
		YHL.setInitialStep((float) initial_step);
		YHL.setStep((float) initial_step);
		YHL.setStepRatio((float) step_ratio);
		YHL.setBarnesHutTheta((float) barnes_hut_theta);
		YHL.setConvergenceThreshold((float) convergence_threshold);
		YHL.setQuadTreeMaxLevel((int) max_level);
		YHL.setRelativeStrength((float) relative_strength);
	}

	// Full execution of the Yifan Hu algorithm with bounds limit
	public static IGraph YifanHuLayout_Full_Limited(final IGraph g, final double optimal_distance,
			final double initial_step, final double step_ratio, final double convergence_threshold,
			final double barnes_hut_theta, final int max_step, final boolean initialise, final double max_x,
			final double max_y) {
		initializing();
		// DEBUG.LOG("Yifan Hu algorithm (bounds limited), starting.");
		// DEBUG.LOG("converting ...");
		IGraph_to_GraphModel(g);
		initializing_GraphModel(g);
		// DEBUG.LOG("graphmodel constructed : " +
		// graph_model.getUndirectedGraph().getNodeCount() + " nodes & "+
		// graph_model.getUndirectedGraph().getEdgeCount() + " edges.");
		final YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
		// DEBUG.LOG("initialising layout ...");
		InitializingYifanHuLayout(layout, optimal_distance, initial_step, step_ratio, convergence_threshold,
				barnes_hut_theta);
		// DEBUG.LOG("initialising algorithm ...");
		if (initialise) {
			layout.initAlgo();
		} else {
			// initializing_GraphModel(g);
		}
		// DEBUG.LOG("working ...");
		for (int i = 0; i < max_step && layout.canAlgo(); i++) {
			layout.goAlgo();
		}
		layout.endAlgo();
		Update_locations(g, max_x, max_y);
		// DEBUG.LOG("ended.");
		return g;
	}

	// Full execution of the Yifan Hu algorithm without bounds limit
	// public static IGraph YifanHuLayout_Full_Unlimited(final IGraph g, double
	// optimal_distance, double initial_step, double step_ratio, double
	// convergence_threshold, double barnes_hut_theta, int
	// max_step) {
	public static IGraph YifanHuLayout_Full_Unlimited(final IGraph g, final double optimal_distance,
			final double initial_step, final double step_ratio, final double convergence_threshold,
			final double barnes_hut_theta, final int max_step, final boolean initialise) {
		initializing();
		// DEBUG.LOG("Yifan Hu algorithm starting.");
		// DEBUG.LOG("converting ...");
		IGraph_to_GraphModel(g);
		initializing_GraphModel(g);
		// DEBUG.LOG("graphmodel constructed : " +
		// graph_model.getUndirectedGraph().getNodeCount() + " nodes & "+
		// graph_model.getUndirectedGraph().getEdgeCount() + " edges.");
		final YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
		// DEBUG.LOG("initialising layout ...");
		InitializingYifanHuLayout(layout, optimal_distance, initial_step, step_ratio, convergence_threshold,
				barnes_hut_theta);
		// DEBUG.LOG("initialising algorithm ...");
		if (initialise) {
			layout.initAlgo();
		} else {
			// initializing_GraphModel(g);
		}
		// DEBUG.LOG("working ...");
		for (int i = 0; i < max_step && layout.canAlgo(); i++) {
			layout.goAlgo();
			// DEBUG.LOG("..." + i + "/" + max_step + "/" +
			// layout.canAlgo());
		}
		// DEBUG.LOG("... " + layout.canAlgo());
		layout.endAlgo();
		Update_locations(g);
		// DEBUG.LOG("ended.");
		return g;
	}

	// Step execution of the Yifan Hu algorithm with bounds limit
	public static IGraph YifanHuLayout_Step_Limited(final IGraph g, final double optimal_distance,
			final double initial_step, final double step_ratio, final double convergence_threshold,
			final double barnes_hut_theta, final double max_x, final double max_y) {
		initializing();
		// DEBUG.LOG("Yifan Hu algorithm (by step and bounds limited),
		// starting.");
		// DEBUG.LOG("converting ...");
		IGraph_to_GraphModel(g);
		// DEBUG.LOG("initialising ...");
		initializing_GraphModel(g);
		final YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
		InitializingYifanHuLayout(layout, optimal_distance, initial_step, step_ratio, convergence_threshold,
				barnes_hut_theta);
		layout.goAlgo();
		Update_locations(g, max_x, max_y);
		// DEBUG.LOG("ended.");
		return g;
	}

	// Step execution of the Yifan Hu algorithm without bounds limit
	public static IGraph YifanHuLayout_Step_Unlimited(final IGraph g, final double optimal_distance,
			final double initial_step, final double step_ratio, final double convergence_threshold,
			final double barnes_hut_theta) {
		initializing();
		// DEBUG.LOG("Yifan Hu algorithm (by step), starting.");
		// DEBUG.LOG("converting ...");
		IGraph_to_GraphModel(g);
		// DEBUG.LOG("initialising ...");
		initializing_GraphModel(g);
		final YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
		InitializingYifanHuLayout(layout, optimal_distance, initial_step, step_ratio, convergence_threshold,
				barnes_hut_theta);
		layout.goAlgo();
		Update_locations(g);
		// DEBUG.LOG("ended.");
		return g;
	}

	// Full execution of the Multilevel Yifan Hu algorithm with bounds limit
	public static IGraph YifanHuMultilevel_Full_Limited(final IGraph g, final double optimal_distance,
			final double min_coarsening_rate, final int min_size, final double step_ratio,
			final double barnes_hut_theta, final int max_step, final double max_x, final double max_y) {
		initializing();
		// DEBUG.LOG("Yifan Hu Multilevel (bounds limited) algorithm,
		// starting.");
		// DEBUG.LOG("converting ...");
		initializing();
		final MultiLevelLayout MlL = new MultiLevelLayout(new YifanHuMultiLevel(), new MaximalMatchingCoarsening());
		MlL.resetPropertiesValues();
		MlL.setGraphModel(graph_model);
		MlL.setOptimalDistance((float) optimal_distance);
		MlL.setMinCoarseningRate(min_coarsening_rate);
		MlL.setMinSize(min_size);
		MlL.setStepRatio((float) step_ratio);
		MlL.setBarnesHutTheta((float) barnes_hut_theta);
		// DEBUG.LOG("initializing ...");
		MlL.initAlgo();
		IGraph_to_GraphModel(g);
		// DEBUG.LOG("working ...");
		for (int i = 0; i < max_step && MlL.canAlgo(); i++) {
			MlL.goAlgo();
		}
		MlL.endAlgo();
		Update_locations(g, max_x, max_y);
		// DEBUG.LOG("ended.");
		return g;
	}

	// Full execution of the Multilevel Yifan Hu algorithm without bounds limit
	public static IGraph YifanHuMultilevel_Full_Unlimited(final IGraph g, final double optimal_distance,
			final double min_coarsening_rate, final int min_size, final double step_ratio,
			final double barnes_hut_theta, final int max_step) {
		initializing();
		// DEBUG.LOG("Yifan Hu Multilevel algorithm, starting.");
		// DEBUG.LOG("converting ...");
		initializing();
		final MultiLevelLayout MlL = new MultiLevelLayout(new YifanHuMultiLevel(), new MaximalMatchingCoarsening());
		MlL.resetPropertiesValues();
		MlL.setGraphModel(graph_model);
		MlL.setOptimalDistance((float) optimal_distance);
		MlL.setMinCoarseningRate(min_coarsening_rate);
		MlL.setMinSize(min_size);
		MlL.setStepRatio((float) step_ratio);
		MlL.setBarnesHutTheta((float) barnes_hut_theta);
		// DEBUG.LOG("initializing ...");
		MlL.initAlgo();
		IGraph_to_GraphModel(g);
		// DEBUG.LOG("working ...");
		for (int i = 0; i < max_step && MlL.canAlgo(); i++) {
			MlL.goAlgo();
		}
		MlL.endAlgo();
		Update_locations(g);
		// DEBUG.LOG("ended.");
		return g;
	}

	// Step execution of the Multilevel Yifan Hu algorithm with bounds limit
	public static IGraph YifanHuMultilevelLayout_Step_Limited(final IGraph g, final double optimal_distance,
			final double min_coarsening_rate, final int min_size, final double step_ratio,
			final double barnes_hut_theta, final double max_x, final double max_y) {
		initializing();
		// DEBUG.LOG("Yifan Hu Multilevel algorithm (by step and bounds
		// limited), starting.");
		// DEBUG.LOG("converting ...");
		final MultiLevelLayout MlL = new MultiLevelLayout(new YifanHuMultiLevel(), new MaximalMatchingCoarsening());
		MlL.resetPropertiesValues();
		MlL.setGraphModel(graph_model);
		MlL.setOptimalDistance((float) optimal_distance);
		MlL.setMinCoarseningRate(min_coarsening_rate);
		MlL.setMinSize(min_size);
		MlL.setStepRatio((float) step_ratio);
		MlL.setBarnesHutTheta((float) barnes_hut_theta);
		// DEBUG.LOG("initializing ...");
		MlL.initAlgo();
		IGraph_to_GraphModel(g);
		initializing_GraphModel(g);
		// DEBUG.LOG("working ...");
		MlL.goAlgo();
		MlL.endAlgo();
		Update_locations(g, max_x, max_y);
		// DEBUG.LOG("ended.");
		return g;
	}

	// Step execution of the Multilevel Yifan Hu algorithm without bounds limit
	public static IGraph YifanHuMultilevelLayout_Step(final IGraph g, final double optimal_distance,
			final double min_coarsening_rate, final int min_size, final double step_ratio,
			final double barnes_hut_theta) {
		initializing();
		// DEBUG.LOG("Yifan Hu Multilevel algorithm (by step),
		// starting.");
		// DEBUG.LOG("converting ...");
		final MultiLevelLayout MlL = new MultiLevelLayout(new YifanHuMultiLevel(), new MaximalMatchingCoarsening());
		MlL.resetPropertiesValues();
		MlL.setGraphModel(graph_model);
		MlL.setOptimalDistance((float) optimal_distance);
		MlL.setMinCoarseningRate(min_coarsening_rate);
		MlL.setMinSize(min_size);
		MlL.setStepRatio((float) step_ratio);
		MlL.setBarnesHutTheta((float) barnes_hut_theta);
		// DEBUG.LOG("initializing ...");
		MlL.initAlgo();
		IGraph_to_GraphModel(g);
		initializing_GraphModel(g);
		// DEBUG.LOG("working ...");
		MlL.goAlgo();
		MlL.endAlgo();
		Update_locations(g);
		// DEBUG.LOG("ended.");
		return g;
	}

}
