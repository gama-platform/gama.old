/*********************************************************************************************
 *
 *
 * 'GraphstreamOutput.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.networks.ui;

import msi.gama.common.interfaces.*;
import msi.gama.outputs.AbstractDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.IGraph;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@SuppressWarnings("unchecked")
@symbol(name = IKeyword.DISPLAY_GRAPH, kind = ISymbolKind.OUTPUT, with_sequence = true, internal = true)
@facets(value = { @facet(name = IKeyword.NAME, type = IType.STRING, optional = true),
	@facet(name = "graph", type = IType.GRAPH, optional = false),
	@facet(name = "lowquality", type = IType.BOOL, optional = true), }, omissible = IKeyword.NAME)
@inside(symbols = IKeyword.OUTPUT)
@doc(value = "", deprecated = "do not use")
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
		return IGui.GRAPHSTREAM_VIEW_ID;
	}

	@Override
	public boolean step(final IScope scope) throws GamaRuntimeException {
		// retrieve the graph to be displayed
		Object tmp = Cast.asGraph(getScope(), graphExpr);
		if ( tmp != null ) {
			this.graph = (IGraph) tmp;
		}
		return true;
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		boolean result = super.init(scope);
		if ( !result ) { return false; }
		// retrieve the graph to be displayed
		Object tmp = Cast.asGraph(getScope(), graphExpr);
		if ( tmp != null ) {
			this.graph = (IGraph) tmp;
		}

		lowquality = Cast.asBool(getScope(), getFacet("lowquality"));
		return true;

	}

	public IGraph getGraph() {

		Object tmp = Cast.asGraph(getScope(), graphExpr);
		if ( tmp != null ) {
			this.graph = (IGraph) tmp;
		}

		return graph;
	}

	public boolean getLowQuality() {
		return lowquality;
	}

	@Override
	public void open() {
		super.open();
		update();
	}

}
