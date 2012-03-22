package msi.gama.networks.ui;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.outputs.AbstractDisplayOutput;
import msi.gama.outputs.LayerDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.graph.IGraph;
import msi.gaml.compilation.ISymbolKind;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;


@SuppressWarnings("unchecked")
@symbol(name = IKeyword.DISPLAY_GRAPH, kind = ISymbolKind.OUTPUT)
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.STRING_STR, optional = true),
	@facet(name = "graph", type = IType.GRAPH_STR, optional = false),
	}, omissible = IKeyword.NAME)
@with_sequence
@inside(symbols = IKeyword.OUTPUT)
public class GraphstreamOutput extends AbstractDisplayOutput {

	public IGraph graph;
	
	private IExpression graphExpr ;
	
	public GraphstreamOutput(IDescription desc) {
		super(desc);
		
		// retrieve and check parameters
		verifyFacetType(IKeyword.NAME);
		verifyFacetType("graph");
		if ( hasFacet(IKeyword.NAME) ) 
			verifyFacetType(IKeyword.NAME);
		
		graphExpr = getFacet("graph");
	}

	@Override
	public String getViewId() {
		return GuiUtils.GRAPHSTREAM_VIEW_ID;
	}

	@Override
	public void compute(IScope scope, int cycle) throws GamaRuntimeException {
		
	}
	
	public IGraph getGraph() {

		Object tmp = Cast.asGraph(getOwnScope(), graphExpr);
		if (tmp != null)
			this.graph = (IGraph) tmp;
		
		return graph;
	}

}
