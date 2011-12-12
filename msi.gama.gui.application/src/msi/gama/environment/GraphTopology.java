/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.environment;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.*;

/**
 * The class GraphTopology.
 * 
 * @author drogoul
 * @since 27 nov. 2011
 * 
 */
public class GraphTopology extends AbstractTopology {

	/**
	 * @param scope
	 * @param env
	 * @param torus
	 */
	public GraphTopology(final IScope scope, final IGeometry env, final GamaSpatialGraph graph) {
		super(scope, env);
		places = graph;
	}

	@Override
	protected boolean createAgents() {
		return true;
	}

	/**
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public GamaPath pathBetween(final IGeometry source, final IGeometry target) {
		Object s1 = null;
		Object t1 = null;
		Object s2 = null;
		Object t2 = null;

		IGeometry edgeS = null, edgeT = null;
		double dist1 = Double.MAX_VALUE;
		double dist2 = Double.MAX_VALUE;

		for ( Object o : getPlaces().getEdges() ) {
			IGeometry eg;
			try {
				eg = Cast.asGeometry(o);
				double d1 = eg.euclidianDistanceTo(source);
				if ( d1 < dist1 ) {
					edgeS = eg;
					s1 = getPlaces().getEdgeSource(o);
					s2 = getPlaces().getEdgeTarget(o);
					dist1 = d1;
				}
				double d2 = eg.euclidianDistanceTo(target);
				if ( d2 < dist2 ) {
					edgeT = eg;
					t1 = getPlaces().getEdgeSource(o);
					t2 = getPlaces().getEdgeTarget(o);
					dist2 = d2;
				}
			} catch (GamaRuntimeException e) {
				return null;
			}
		}
		GamaPath pathComplete = null;
		if ( edgeS == edgeT ) {
			pathComplete = new GamaPath(this, source, target, GamaList.with(edgeS));
		} else {
			Object nodeT = t1;
			try {
				IGeometry g1 = Cast.asGeometry(t1);
				IGeometry g2 = Cast.asGeometry(t2);
				if ( g1.euclidianDistanceTo(target) > g2.euclidianDistanceTo(target) ) {
					nodeT = t2;
				}
			} catch (GamaRuntimeException e) {
				return null;
			}
			Object nodeS = s1;
			try {
				IGeometry g1 = Cast.asGeometry(s1);
				IGeometry g2 = Cast.asGeometry(s2);
				if ( s1 == nodeT || s2 != nodeT &&
					g1.euclidianDistanceTo(source) > g2.euclidianDistanceTo(source) ) {
					nodeS = s2;
				}
			} catch (GamaRuntimeException e) {
				return null;
			}

			GamaPath path = (GamaPath) getPlaces().computeShortestPathBetween(this, nodeS, nodeT);
			List edges = path.getEdgeList().copy();
			if ( edges == null || edges.isEmpty() ) { return null; }
			if ( edges.get(0) != edgeS ) {
				edges.add(0, edgeS);
			}
			if ( edges.get(edges.size() - 1) != edgeT ) {
				edges.add(edgeT);
			}
			pathComplete = new GamaPath(this, source, target, edges);
		}
		return pathComplete;
	}

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue() throws GamaRuntimeException {
		return "GraphTopology";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_toGaml()
	 */
	@Override
	protected String _toGaml() {
		return "GraphTopology";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_copy()
	 */
	@Override
	protected ITopology _copy() {
		return new GraphTopology(scope, environment, (GamaSpatialGraph) places);
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#getRandomPlace()
	 */

	@Override
	public IGraph<IGeometry, IGeometry> getPlaces() {
		return (GamaSpatialGraph) super.getPlaces();
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final GamaPoint p) {
		return isValidGeometry(p.getGeometry());
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IGeometry g) {
		// Geometry g2 = g.getInnerGeometry();
		for ( IGeometry g1 : places ) {
			if ( g1.intersects(g) ) { return true; }
			// TODO covers or intersects ?
			// TODO inverser le calcul (ou faire coveredBy) pour optimiser
		}
		return false;
	}

	/**
	 * @see msi.gama.environment.ITopology#distanceBetween(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry, java.lang.Double)
	 */
	@Override
	public Double distanceBetween(final IGeometry source, final IGeometry target) {
		GamaPath path = this.pathBetween(source, target);
		if ( path == null ) { return Double.MAX_VALUE; }
		return path.getDistance();
		// return d <= max_distance ? d : null;
	}

	/**
	 * @see msi.gama.environment.ITopology#directionInDegreesTo(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Integer directionInDegreesTo(final IGeometry source, final IGeometry target) {
		GamaPath path = this.pathBetween(source, target);
		if ( path == null ) { return null; }
		// LineString ls = (LineString) path.getEdgeList().first().getInnerGeometry();
		// TODO Check this
		final double dx = target.getLocation().x - source.getLocation().x;
		final double dy = target.getLocation().y - source.getLocation().y;
		final double result = MathUtils.aTan2(dy, dx) * MathUtils.toDeg;
		return MathUtils.checkHeading((int) result);
	}

	/**
	 * @see msi.gama.environment.ITopology#getAgentsIn(msi.gama.interfaces.IGeometry,
	 *      msi.gama.environment.IAgentFilter, boolean)
	 */
	@Override
	public GamaList<IAgent> getAgentsIn(final IGeometry source, final IAgentFilter f,
		final boolean covered) {
		GamaList<IAgent> agents = super.getAgentsIn(source, f, covered);
		GamaList<IAgent> result = new GamaList();
		for ( IAgent ag : agents ) {
			if ( isValidGeometry(ag) ) {
				result.add(ag);
			}
		}
		return result;
	}

}
