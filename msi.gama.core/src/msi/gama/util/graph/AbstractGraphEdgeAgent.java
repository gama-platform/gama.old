/*********************************************************************************************
 *
 * 'AbstractGraphEdgeAgent.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;

// FIXME: Add all the necessary variables and actions ?
// FIXME:
@species (
		name = "graph_edge",
		doc = @doc ("A species that represents an edge of a graph made of agents. The source and the target of the edge should be agents"))
@vars ({ @var (
		name = IKeyword.SOURCE,
		type = IType.AGENT,
		doc = @doc ("The source agent of this edge")),
		@var (
				name = IKeyword.TARGET,
				type = IType.AGENT,
				doc = @doc ("The target agent of this edge")) })
public class AbstractGraphEdgeAgent extends GamlAgent {

	public AbstractGraphEdgeAgent(final IPopulation<? extends IAgent> s, final int index) throws GamaRuntimeException {
		super(s, index);
	}

	@Override
	public Object _step_(final IScope scope) {
		// if ( scope.interrupted() || dead() ) { return null; }
		final IAgent s = (IAgent) getAttribute(IKeyword.SOURCE);
		final IAgent t = (IAgent) getAttribute(IKeyword.TARGET);
		if (s == null || t == null) { return null; }
		setGeometry(GamaGeometryType.buildLine(s.getLocation(), t.getLocation()));
		return super._step_(scope);
	}

}
