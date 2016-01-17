package yifanhu;

import org.gephi.layout.plugin.forceAtlas2.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.ILocation;
<<<<<<< Upstream, based on origin/master
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.IConcept;
=======
import msi.gama.outputs.layers.charts.ChartDataListStatement;
import msi.gama.outputs.layers.charts.ChartDataStatement;
import msi.gama.outputs.layers.charts.ChartDataListStatement.ChartDataList;
import msi.gama.precompiler.IOperatorCategory;
>>>>>>> 35f132d move to chart package
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.IGraph;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.types.IType;

@symbol(name = "layout_forceatlas2",
	kind = ISymbolKind.SINGLE_STATEMENT,
	concept = { IConcept.GRAPH },
	with_sequence = false,
	doc = @doc("Apply Force Atlas 2 Layout."))
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = {
	@facet(name = IKeyword.GRAPH, type = IType.GRAPH, optional = false, doc = @doc("the graph to apply the layout.")),
	@facet(name = GraphForceAtlasLayoutStatement.NB_STEPS,
		type = IType.INT,
		optional = true,
		doc = @doc("The number of steps of the algorithm to perform (default 1).")),
	@facet(name = GraphForceAtlasLayoutStatement.THREAD_NUMBER,
		type = IType.INT,
		optional = true,
		doc = @doc("More threads means more speed (default: 1).")),
	@facet(name = GraphForceAtlasLayoutStatement.DISSUADE_HUBS,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Distributes attraction along outbound edges. Hubs attract less and thus are pushed to the borders (default: false).")),
	@facet(name = GraphForceAtlasLayoutStatement.LINLOG_MODE,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Switch model from lin-lin to lin-log. Makes clusters more tight (default: false).")),
	@facet(name = GraphForceAtlasLayoutStatement.PREVENT_OVERLAP,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Should not be used with approximate_repulsion default: false")),
	@facet(name = GraphForceAtlasLayoutStatement.EDGE_WEIGHT_INFLUENCE,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("How much influence you give to the edges wight. 0 is no influence, 1 is normal. default: 1.0")),
	@facet(name = GraphForceAtlasLayoutStatement.SCALING,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("How much repulsion you want. More makes a more sparse graph. default: 2.0")),
	@facet(name = GraphForceAtlasLayoutStatement.STRONGER_GRAVITY,
		type = IType.BOOL,
		optional = true,
		doc = @doc("A stronger gravity law default: false")),
	@facet(name = GraphForceAtlasLayoutStatement.GRAVITY,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("Attracts nodes to the center. Prevents islands from drifting away. default: 1.0")),
	@facet(name = GraphForceAtlasLayoutStatement.TOLERANCE,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("How much swinging you allow. Above 1 discouraged. Lower gives less speed and more precision. default: 0.1")),
	@facet(name = GraphForceAtlasLayoutStatement.APPROXIMATE_REPULSION,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Barnes Hut optimization: n2 complexity to n.ln(n); allows larger graphs. default: false")),
	@facet(name = GraphForceAtlasLayoutStatement.APPROXIMATION,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("Theta of the Barnes Hut optimization. default: 1.2")),
	@facet(name = GraphForceAtlasLayoutStatement.BOUNDED_P1,
		type = IType.POINT,
		optional = true,
		doc = @doc("The new nodes positions are bounded within the two bound points if both are not null. default: null")),
	@facet(name = GraphForceAtlasLayoutStatement.BOUNDED_P2,
		type = IType.POINT,
		optional = true,
		doc = @doc("The new nodes positions are bounded within the two bound points if both are not null. default: null")) },
	omissible = IKeyword.GRAPH)
public class GraphForceAtlasLayoutStatement extends AbstractGraphLayoutStatement {

	public GraphForceAtlasLayoutStatement(final IDescription desc) {
		super(desc);
		// TODO Auto-generated constructor stub
	}

	public static final String THREAD_NUMBER = "thread_number";
	public static final String DISSUADE_HUBS = "dissuade_hubs";
	public static final String LINLOG_MODE = "linlog_mode";
	public static final String PREVENT_OVERLAP = "prevent_overlap";
	public static final String EDGE_WEIGHT_INFLUENCE = "edge_weight_influence";
	public static final String SCALING = "scaling";
	public static final String STRONGER_GRAVITY = "stronger_gravity";
	public static final String GRAVITY = "gravity";
	public static final String TOLERANCE = "tolerance";
	public static final String APPROXIMATE_REPULSION = "approximate_repulsion";
	public static final String APPROXIMATION = "approximation";
	public static final String BOUNDED_P1 = "bounded_point1";
	public static final String BOUNDED_P2 = "bounded_point2";
	public static final String NB_STEPS = "nb_steps";

	public static double stepvalue = -1;

	/*
	 * private static ProjectController pc;
	 * private static Workspace workspace;
	 * private static GraphModel graph_model;
	 * private static UndirectedGraph the_graph;
	 * private static boolean initialized = false;
	 * private static int node_index_diff = 0;
	 */
	private static final int THREAD_NUMBER_DEFAULT = 1;
	private static final boolean DISSUADE_HUBS_DEFAULT = false;
	private static final boolean LINLOG_MODE_DEFAULT = false;
	private static final boolean PREVENT_OVERLAP_DEFAULT = false;
	private static final double EDGE_WEIGHT_INFLUENCE_DEFAULT = 1.0;
	private static final double SCALING_DEFAULT = 2.0;
	private static final boolean STRONGER_GRAVITY_DEFAULT = false;
	private static final double GRAVITY_DEFAULT = 1.0;
	private static final double TOLERANCE_DEFAULT = 0.1;
	private static final boolean APPROXIMATE_REPULSION_DEFAULT = false;
	private static final double APPROXIMATION_DEFAULT = 1.2;
	private static final int NB_STEPS_DEFAULT = 1;

	@Override
	protected Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		// IGraph data = createData(scope);
		IGraph g = Cast.asGraph(scope, getFacetValue(scope, IKeyword.GRAPH));
		int thread_number = Cast.asInt(scope,
			getFacetValue(scope, GraphForceAtlasLayoutStatement.THREAD_NUMBER, THREAD_NUMBER_DEFAULT));
		boolean dissuade_hubs = Cast.asBool(scope,
			getFacetValue(scope, GraphForceAtlasLayoutStatement.DISSUADE_HUBS, DISSUADE_HUBS_DEFAULT));
		boolean linlog_mode =
			Cast.asBool(scope, getFacetValue(scope, GraphForceAtlasLayoutStatement.LINLOG_MODE, LINLOG_MODE_DEFAULT));
		boolean prevent_overlap = Cast.asBool(scope,
			getFacetValue(scope, GraphForceAtlasLayoutStatement.PREVENT_OVERLAP, PREVENT_OVERLAP_DEFAULT));
		double edge_weight_influence = Cast.asFloat(scope,
			getFacetValue(scope, GraphForceAtlasLayoutStatement.EDGE_WEIGHT_INFLUENCE, EDGE_WEIGHT_INFLUENCE_DEFAULT));
		double scaling =
			Cast.asFloat(scope, getFacetValue(scope, GraphForceAtlasLayoutStatement.SCALING, SCALING_DEFAULT));
		boolean stronger_gravity = Cast.asBool(scope,
			getFacetValue(scope, GraphForceAtlasLayoutStatement.STRONGER_GRAVITY, STRONGER_GRAVITY_DEFAULT));
		double gravity =
			Cast.asFloat(scope, getFacetValue(scope, GraphForceAtlasLayoutStatement.GRAVITY, GRAVITY_DEFAULT));
		double tolerance =
			Cast.asFloat(scope, getFacetValue(scope, GraphForceAtlasLayoutStatement.TOLERANCE, TOLERANCE_DEFAULT));
		boolean approximate_repulsion = Cast.asBool(scope,
			getFacetValue(scope, GraphForceAtlasLayoutStatement.APPROXIMATE_REPULSION, APPROXIMATE_REPULSION_DEFAULT));
		double approximation = Cast.asFloat(scope,
			getFacetValue(scope, GraphForceAtlasLayoutStatement.APPROXIMATION, APPROXIMATION_DEFAULT));
		int nb_steps =
			Cast.asInt(scope, getFacetValue(scope, GraphForceAtlasLayoutStatement.NB_STEPS, NB_STEPS_DEFAULT));
		Object bp1 = getFacetValue(scope, GraphForceAtlasLayoutStatement.BOUNDED_P1, null);
		Object bp2 = getFacetValue(scope, GraphForceAtlasLayoutStatement.BOUNDED_P2, null);
		// public static IGraph YifanHuLayout_Step_Unlimited(final IGraph g, double optimal_distance, double initial_step, double step_ratio, double convergence_threshold, double barnes_hut_theta) {
		initializing();
		// System.out.println("ForceAtlas2 algorithm (by step), starting.");
		// System.out.println("converting ...");
		IGraph_to_GraphModel(g);
		// System.out.println("initializing ...");
		ForceAtlas2 fa2Layout = new ForceAtlas2(new ForceAtlas2Builder());
		initializing_GraphModel(g);
		fa2Layout.resetPropertiesValues();
		fa2Layout.setGraphModel(graph_model);
		fa2Layout.setOutboundAttractionDistribution(dissuade_hubs);
		fa2Layout.setLinLogMode(linlog_mode);
		fa2Layout.setAdjustSizes(prevent_overlap);
		fa2Layout.setEdgeWeightInfluence(edge_weight_influence);
		fa2Layout.setScalingRatio(scaling);
		fa2Layout.setStrongGravityMode(stronger_gravity);
		fa2Layout.setGravity(gravity);
		fa2Layout.setJitterTolerance(tolerance);
		fa2Layout.setBarnesHutOptimize(approximate_repulsion);
		fa2Layout.setBarnesHutTheta(approximation);
		// System.out.println("working ...");
		fa2Layout.initAlgo();
		// System.out.println("working ...");
		// int nbsteps=1;
		// for (int i = 0; i < nbsteps && fa2Layout.canAlgo(); i++){
		for ( int i = 0; i < nb_steps; i++ ) {
			fa2Layout.goAlgo();
		}
		fa2Layout.endAlgo();
		// fa2Layout.goAlgo();
		// fa2Layout.endAlgo();
		if ( bp1 != null && bp2 != null ) {
			ILocation p1 = Cast.asPoint(scope, bp1);
			ILocation p2 = Cast.asPoint(scope, bp2);
			Update_locations(g, FastMath.min(p1.getX(), p2.getX()), FastMath.min(p1.getY(), p2.getY()),
				FastMath.max(p1.getX(), p2.getX()), FastMath.max(p1.getY(), p2.getY()));
		} else {
			Update_locations(g);
		}

		// System.out.println("ended.");

		return g;

	}

}
