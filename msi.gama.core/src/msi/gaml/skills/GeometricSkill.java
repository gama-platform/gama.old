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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.skills;

import java.util.*;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.prep.*;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;

/**
 * GeometricSkill
 * 
 * @author taillandier
 * @version $Revision: 1.0 $
 */

@skill(name = "situated")
public class GeometricSkill extends Skill {

	/**
	 * @throws GamaRuntimeException Method primPercievedArea.
	 * @param scope IScope
	 * @param args Arguments
	 * @return GamaList
	 */
	@action(name = "percieved_area")
	@args(names = { "agent", "geometry", "range", "precision" })
	public GamaShape primPercievedArea(final IScope scope) throws GamaRuntimeException {
		final List<List<List<ILocation>>> coords = scope.getListArg("geometry");
		final IAgent ag = (IAgent) scope.getArg("agent", IType.AGENT);
		Geometry geom = null;
		if ( ag != null ) {
			geom = ag.getInnerGeometry();
		} else if ( !coords.isEmpty() ) {
			geom = GeometryUtils.buildGeometryJTS(coords);
		} else {
			geom = scope.getSimulationScope().getInnerGeometry();
		}
		Double percep_dist = scope.hasArg("range") ? scope.getFloatArg("range") : null;
		Integer precision = scope.hasArg("precision") ? scope.getIntArg("precision") : null;

		if ( percep_dist == null ) {
			percep_dist = (Double) scope.getAgentVarValue(getCurrentAgent(scope), "range");
		}
		if ( precision == null ) {
			precision = 10;
		}
		final List<Geometry> geoms = new GamaList<Geometry>();
		final Coordinate coord_loc = getCurrentAgent(scope).getLocation().toCoordinate();
		Coordinate prec = new Coordinate(coord_loc.x + percep_dist, coord_loc.y);
		for ( int k = 1; k <= precision; k++ ) {
			final double angle = (double) k / precision * 2 * Math.PI;
			Coordinate next = null;
			if ( k < precision ) {
				next =
					new Coordinate(coord_loc.x + Math.cos(angle) * percep_dist, coord_loc.y + Math.sin(angle) *
						percep_dist);
			} else {
				next = new Coordinate(coord_loc.x + percep_dist, coord_loc.y);
			}
			final Coordinate[] coordinates = new Coordinate[4];
			coordinates[0] = coord_loc;
			coordinates[1] = prec;
			coordinates[2] = next;
			coordinates[3] = coord_loc;
			final LinearRing closeRing = GeometryUtils.factory.createLinearRing(coordinates);
			final Geometry percept = GeometryUtils.factory.createPolygon(closeRing, null);

			Geometry areaPerc = null;
			Geometry frontier = null;
			// try {
			frontier = geom.intersection(percept);
			/*
			 * } catch (AssertionFailedException e) { frontier =
			 * backgd.intersection(percept.buffer(0.001)); }
			 */
			final PreparedGeometry ref =
				PreparedGeometryFactory.prepare(getCurrentAgent(scope).getGeometry().getInnerGeometry().buffer(0.01));
			if ( frontier instanceof GeometryCollection ) {
				final GeometryCollection gc = (GeometryCollection) frontier;
				final int nb = gc.getNumGeometries();
				for ( int i = 0; i < nb; i++ ) {
					if ( !ref.disjoint(gc.getGeometryN(i)) ) {
						frontier = gc.getGeometryN(i);
						areaPerc = frontier;
						break;
					}
				}
			} else if ( !ref.disjoint(frontier) ) {
				areaPerc = frontier;
			}
			if ( areaPerc != null && !areaPerc.isEmpty() && areaPerc instanceof Polygon ) {
				geoms.add(areaPerc);
			}

			prec = next;
		}
		/*
		 * Geometry geomFinal = null; for (Geometry g : geoms){ //System.out.println("g : " + g); if
		 * (geomFinal == null) geomFinal = g; else { //try { geomFinal = geomFinal.union(g); //}
		 * catch (AssertionFailedException e) { // geomFinal = geomFinal.buffer(0.01).union(g); //}
		 * }
		 * 
		 * }
		 */
		/*
		 * for (Geometry g : geoms){ g.buffer(0.1); }
		 */
		final Geometry geomFinal = CascadedPolygonUnion.union(geoms);
		// geomFinal.buffer(1);
		// /
		// System.out.println(geomFinal);
		return new GamaShape(geomFinal);

	}

	/**
	 * Return a geometry resulting from the difference between a
	 * geometry representing the exterior ring of the agent geometry (ring :
	 * geometry.buffer(distance) - geometry.buffer(buffer_in)) and the geometries of the
	 * localized entities of the specified species (application of a buffer on these
	 * geometries of size buffer_others)
	 * 
	 * @param args : distance -> float, distance considered for the neighborhood species ->
	 *            optional, a list of species; buffers_others -> optional, a float, size of the
	 *            buffer applied to the other localized entity geometries; buffer_in -> optional, a
	 *            float, size of the "interior" buffer applied to the geometry
	 * 
	 * @param scope IScope
	 */
	@action(name = "neighbourhood_exclusive")
	@args(names = { "distance", "species", "buffer_others", "buffer_in" })
	public IShape primNeighbourhoodExclu(final IScope scope) throws GamaRuntimeException {
		final Double distance = scope.hasArg("distance") ? scope.getFloatArg("distance") : null;
		if ( distance == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		Geometry geom = getCurrentAgent(scope).getInnerGeometry().buffer(distance.doubleValue());

		final Double buffer_in = scope.getFloatArg("buffer_in");
		if ( buffer_in != null ) {
			final Geometry geom2 = getCurrentAgent(scope).getInnerGeometry();
			geom = geom.difference(geom2.buffer(buffer_in.doubleValue()));
		}

		final Double buffer = scope.hasArg("buffer_others") ? scope.getFloatArg("buffer_others") : null;
		final List<ISpecies> species = scope.getListArg("species");

		if ( !species.isEmpty() ) {
			for ( final ISpecies sp : species ) {
				final Iterator<IAgent> it = scope.getAgentScope().getPopulationFor(sp).iterator();

				while (it.hasNext()) {
					final IAgent be = it.next();
					try {
						if ( be != null && be.getGeometry() != null ) {
							if ( buffer != null ) {
								geom = geom.difference(be.getInnerGeometry().buffer(buffer.doubleValue()));
							} else {
								geom = geom.difference(be.getInnerGeometry());
							}
						}
					} catch (final Exception e) {}
				}
			}
		}
		return new GamaShape(geom);

	}

	protected ITopology getTopology(final IAgent agent) {
		return agent.getTopology();
	}

}
