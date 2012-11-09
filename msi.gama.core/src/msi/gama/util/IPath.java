/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.IGraph;
import msi.gaml.types.IType;

/**
 * The class IPath.
 * 
 * @author drogoul
 * @since 14 déc. 2011
 * 
 */
@vars({ @var(name = IKeyword.TARGET, type = IType.POINT_STR),
	@var(name = IKeyword.SOURCE, type = IType.POINT_STR),
	@var(name = IKeyword.GRAPH, type = IType.GRAPH_STR),
	@var(name = IKeyword.SEGMENTS, type = IType.LIST_STR, of = IType.GEOM_STR),
	@var(name = IKeyword.AGENTS, type = IType.LIST_STR, of = IType.AGENT_STR)
// Could be replaced by "geometries"
/*
 * Normally not necessary as it is inherited from GamaGeometry @var(name = GamaPath.POINTS, type =
 * IType.LIST_STR, of = IType.POINT_STR)
 */
})
public interface IPath extends IShape {

	@getter( IKeyword.SOURCE)
	public abstract ILocation getStartVertex();

	@getter( IKeyword.TARGET)
	public abstract ILocation getEndVertex();

	@getter( IKeyword.SEGMENTS)
	public abstract IList<IShape> getEdgeList();

	@getter( IKeyword.AGENTS)
	public abstract List<IShape> getAgentList();

	@getter( IKeyword.GRAPH)
	public IGraph getGraph();

	public abstract IList<ILocation> getVertexList();

	public abstract double getWeight();

	public abstract double getWeight(final IShape line) throws GamaRuntimeException;

	public abstract void acceptVisitor(final IAgent agent);

	public abstract void forgetVisitor(final IAgent agent);

	public abstract int indexOf(final IAgent a);

	public abstract int indexSegmentOf(final IAgent a);

	public abstract boolean isVisitor(final IAgent a);

	public abstract void setIndexOf(final IAgent a, final int index);

	public abstract void setIndexSegementOf(final IAgent a, final int indexSegement);

	public abstract int getLength();

	public abstract double getDistance();

	public abstract ITopology getTopology();

	public abstract void setRealObjects(final Map realObjects);

	public abstract IShape getRealObject(final Object obj);
	
	public void setSource(IShape source);

	public void setTarget(IShape target);
	
	public int getGraphVersion();

}