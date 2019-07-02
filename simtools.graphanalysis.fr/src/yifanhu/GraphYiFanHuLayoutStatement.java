package yifanhu;

import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
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
import msi.gaml.types.IType;

@symbol (
		name = "layout_yifanhu",
		kind = ISymbolKind.SINGLE_STATEMENT,
		concept = { IConcept.GRAPH },
		with_sequence = false,
		doc = @doc ("Apply YifanHu Layout."))
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets (
		value = { @facet (
				name = IKeyword.GRAPH,
				type = IType.GRAPH,
				optional = false,
				doc = @doc ("the graph to apply the layout.")),
				@facet (
						name = GraphYiFanHuLayoutStatement.OPTIMAL_DIST,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the natural length of the springs. Bigger values mean nodes will be farther apart (default: 100).")),
				@facet (
						name = GraphYiFanHuLayoutStatement.QUAD_MAX,
						type = IType.INT,
						optional = true,
						doc = @doc ("The maximum level to be used in the quadtree representation. Greater values mean more accuracy (default: 10).")),
				@facet (
						name = GraphYiFanHuLayoutStatement.BARNES_THETA,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("The theta parameter for Barnes-Hut opening criteria. Smaller values mean more accuracy (default: 1.2).")),
				@facet (
						name = GraphYiFanHuLayoutStatement.RELATIVE_STRENGTH,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("The relative strength between electrical force (repulsion) and spring force (attriaction). default: 0.2")),
				@facet (
						name = GraphYiFanHuLayoutStatement.STEP_SIZE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("The step size used in the algorithm. It has to be a meaningful size compared to the optimal distance (e.g. 10%). default: 10")),
				@facet (
						name = GraphYiFanHuLayoutStatement.NB_STEPS,
						type = IType.INT,
						optional = true,
						doc = @doc ("The number of steps of the algorithm to perform (default 1).")),
				@facet (
						name = GraphYiFanHuLayoutStatement.BOUNDED_P1,
						type = IType.POINT,
						optional = true,
						doc = @doc ("The new nodes positions are bounded within the two bound points if both are not null. default: null")),
				@facet (
						name = GraphYiFanHuLayoutStatement.BOUNDED_P2,
						type = IType.POINT,
						optional = true,
						doc = @doc ("The new nodes positions are bounded within the two bound points if both are not null. default: null")) },
		omissible = IKeyword.GRAPH)
@SuppressWarnings ({ "rawtypes" })
public class GraphYiFanHuLayoutStatement extends AbstractGraphLayoutStatement {

	public GraphYiFanHuLayoutStatement(final IDescription desc) {
		super(desc);
	}

	public static final String OPTIMAL_DIST = "optimal_distance";
	public static final String QUAD_MAX = "quadtree_max_level";
	public static final String BARNES_THETA = "theta";
	public static final String RELATIVE_STRENGTH = "relative_strength";
	public static final String STEP_SIZE = "step_size";
	public static final String NB_STEPS = "nb_steps";
	public static final String BOUNDED_P1 = "bounded_point1";
	public static final String BOUNDED_P2 = "bounded_point2";

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
		final double initial_step =
				Cast.asFloat(scope, getFacetValue(scope, GraphYiFanHuLayoutStatement.STEP_SIZE, INITIAL_STEP_DEFAULT));
		final int nb_steps =
				Cast.asInt(scope, getFacetValue(scope, GraphYiFanHuLayoutStatement.NB_STEPS, NB_STEPS_DEFAULT));
		final double barnes_hut_theta = Cast.asFloat(scope,
				getFacetValue(scope, GraphYiFanHuLayoutStatement.BARNES_THETA, BARNES_THETA_DEFAULT));
		final int max_level =
				Cast.asInt(scope, getFacetValue(scope, GraphYiFanHuLayoutStatement.QUAD_MAX, QUAD_MAX_DEFAULT));
		final double relative_strength =
				Cast.asFloat(scope, getFacetValue(scope, GraphYiFanHuLayoutStatement.RELATIVE_STRENGTH, RATIO_DEFAULT));
		final Object bp1 = getFacetValue(scope, GraphYiFanHuLayoutStatement.BOUNDED_P1, null);
		final Object bp2 = getFacetValue(scope, GraphYiFanHuLayoutStatement.BOUNDED_P2, null);
		initializing();
		IGraph_to_GraphModel(g);
		initializing_GraphModel(g);
		final YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
		InitializingYifanHuLayout(layout, optimal_distance, initial_step, STEP_RATIO_DEFAULT,
				CONVERGENCE_THRESHOLD_DEFAULT, barnes_hut_theta, max_level, relative_strength);
		// layout.goAlgo();
		// for ( int i = 0; i < nb_steps && layout.canAlgo(); i++ ) {
		for (int i = 0; i < nb_steps; i++) {
			layout.goAlgo();
		}
		if (bp1 != null && bp2 != null) {
			final GamaPoint p1 = Cast.asPoint(scope, bp1);
			final GamaPoint p2 = Cast.asPoint(scope, bp2);
			Update_locations(g, Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()),
					Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()));
		} else {
			Update_locations(g);
		}
		return g;

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

}
