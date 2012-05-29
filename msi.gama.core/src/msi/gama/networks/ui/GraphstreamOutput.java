package msi.gama.networks.ui;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.outputs.AbstractDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.IGraph;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@SuppressWarnings("unchecked")
@symbol(name = IKeyword.DISPLAY_GRAPH, kind = ISymbolKind.OUTPUT, with_sequence = true)
@facets(value = { @facet(name = IKeyword.NAME, type = IType.STRING_STR, optional = true),
	@facet(name = "graph", type = IType.GRAPH_STR, optional = false),
	@facet(name = "lowquality", type = IType.BOOL_STR, optional = true), }, omissible = IKeyword.NAME)
@inside(symbols = IKeyword.OUTPUT)
public class GraphstreamOutput extends AbstractDisplayOutput {

	// parameters retrieved from GAML
	protected IGraph graph;
	protected boolean lowquality = false;

	private final IExpression graphExpr;

	public GraphstreamOutput(final IDescription desc) {
		super(desc);

		// retrieve and check parameters

		graphExpr = getFacet("graph");

	}

	@Override
	public String getViewId() {
		return GuiUtils.GRAPHSTREAM_VIEW_ID;
	}

	@Override
	public void compute(final IScope scope, final int cycle) throws GamaRuntimeException {
		// retrieve the graph to be displayed
		Object tmp = Cast.asGraph(scope, graphExpr);
		if ( tmp != null ) {
			this.graph = (IGraph) tmp;
		}

	}

	@Override
	public void prepare(final ISimulation sim) throws GamaRuntimeException {
		super.prepare(sim);

		// retrieve the graph to be displayed
		Object tmp = Cast.asGraph(sim.getExecutionScope(), graphExpr);
		if ( tmp != null ) {
			this.graph = (IGraph) tmp;
		}

		lowquality = Cast.asBool(sim.getExecutionScope(), getFacet("lowquality"));

	}

	public IGraph getGraph() {

		Object tmp = Cast.asGraph(getOwnScope(), graphExpr);
		if ( tmp != null ) {
			this.graph = (IGraph) tmp;
		}

		return graph;
	}

	public boolean getLowQuality() {
		return lowquality;
	}

}
