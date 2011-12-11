/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.skills;

import java.util.*;
import msi.gama.environment.*;
import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.util.*;
import msi.gama.util.graph.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.prep.*;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;

/**
 * GeometricSkill
 * 
 * @author taillandier
 * @version $Revision: 1.0 $
 */

@skill({ "situated", "geometric" })
public class GeometricSkill extends Skill {

	/**
	 * @throws GamaRuntimeException Method primPercievedArea.
	 * @param scope IScope
	 * @param args Arguments
	 * @return GamaList
	 */
	@action("percieved_area")
	@args({ "agent", "geometry", "range", "precision" })
	public GamaGeometry primPercievedArea(final IScope scope) throws GamaRuntimeException {
		List<List<List<GamaPoint>>> coords = Cast.asList(scope.getArg("geometry"));
		IAgent ag = Cast.asAgent(scope.getArg("agent"));
		Geometry geom = null;
		if ( ag != null ) {
			geom = ag.getInnerGeometry();
		} else if ( !coords.isEmpty() ) {
			geom = GeometricFunctions.buildGeometryJTS(coords);
		} else {
			geom = scope.getWorldScope().getInnerGeometry();
		}
		Double percep_dist = scope.hasArg("range") ? Cast.asFloat(scope.getArg("range")) : null;
		Integer precision =
			scope.hasArg("precision") ? Cast.asInt(scope.getArg("precision")) : null;

		if ( percep_dist == null ) {
			percep_dist = (Double) scope.getAgentVarValue(getCurrentAgent(scope), "range");
		}
		if ( precision == null ) {
			precision = 10;
		}
		List<Geometry> geoms = new GamaList<Geometry>();
		Coordinate coord_loc = getCurrentAgent(scope).getLocation().toCoordinate();
		Coordinate prec = new Coordinate(coord_loc.x + percep_dist, coord_loc.y);
		for ( int k = 1; k <= precision; k++ ) {
			double angle = (double) k / precision * 2 * Math.PI;
			Coordinate next = null;
			if ( k < precision ) {
				next =
					new Coordinate(coord_loc.x + Math.cos(angle) * percep_dist, coord_loc.y +
						Math.sin(angle) * percep_dist);
			} else {
				next = new Coordinate(coord_loc.x + percep_dist, coord_loc.y);
			}
			Coordinate[] coordinates = new Coordinate[4];
			coordinates[0] = coord_loc;
			coordinates[1] = prec;
			coordinates[2] = next;
			coordinates[3] = coord_loc;
			LinearRing closeRing = GamaGeometry.getFactory().createLinearRing(coordinates);
			Geometry percept = GamaGeometry.getFactory().createPolygon(closeRing, null);

			Geometry areaPerc = null;
			Geometry frontier = null;
			// try {
			frontier = geom.intersection(percept);
			/*
			 * } catch (AssertionFailedException e) { frontier =
			 * backgd.intersection(percept.buffer(0.001)); }
			 */
			PreparedGeometry ref =
				PreparedGeometryFactory.prepare(getCurrentAgent(scope).getGeometry()
					.getInnerGeometry().buffer(0.01));
			if ( frontier instanceof GeometryCollection ) {
				GeometryCollection gc = (GeometryCollection) frontier;
				int nb = gc.getNumGeometries();
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
		Geometry geomFinal = CascadedPolygonUnion.union(geoms);
		// geomFinal.buffer(1);
		// /
		// System.out.println(geomFinal);
		return new GamaGeometry(geomFinal);

	}

	/**
	 * @throws GamaRuntimeException Return a geometry resulting from the difference between a
	 *             geometry representing the exterior ring of the agent geometry (ring :
	 *             geometry.buffer(distance) - geometry.buffer(buffer_in)) and the geometries of the
	 *             localized entities of the specified species (application of a buffer on these
	 *             geometries of size buffer_others)
	 * 
	 * @param args : distance -> float, distance considered for the neighborhood species ->
	 *            optional, a list of species; buffers_others -> optional, a float, size of the
	 *            buffer applied to the other localized entity geometries; buffer_in -> optional, a
	 *            float, size of the "interior" buffer applied to the geometry
	 * 
	 * @param scope IScope
	 * @return the prim CommandStatus
	 */
	@action("neighbourhood_exclusive")
	@args({ "distance", "species", "buffer_others", "buffer_in" })
	public GamaGeometry primNeighbourhoodExclu(final IScope scope) throws GamaRuntimeException {
		Double distance = scope.hasArg("distance") ? Cast.asFloat(scope.getArg("distance")) : null;
		if ( distance == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		Geometry geom = getCurrentAgent(scope).getInnerGeometry().buffer(distance.doubleValue());

		Double buffer_in = Cast.asFloat(scope.getArg("buffer_in"));
		if ( buffer_in != null ) {
			Geometry geom2 = getCurrentAgent(scope).getInnerGeometry();
			geom = geom.difference(geom2.buffer(buffer_in.doubleValue()));
		}

		Double buffer =
			scope.hasArg("buffer_others") ? Cast.asFloat(scope.getArg("buffer_others")) : null;
		List<ISpecies> species = Cast.asList(scope.getArg("species"));

		if ( !species.isEmpty() ) {
			for ( ISpecies sp : species ) {
				List<IAgent> ags = scope.getAgentScope().getPopulationFor(sp).getAgentsList();
				for ( IAgent be : ags ) {
					try {
						if ( be != null && be.getGeometry() != null ) {
							if ( buffer != null ) {
								geom =
									geom.difference(be.getInnerGeometry().buffer(
										buffer.doubleValue()));
							} else {
								geom = geom.difference(be.getInnerGeometry());
							}
						}
					} catch (Exception e) {}
				}
			}
		}
		return new GamaGeometry(geom);

	}

	/**
	 * @throws GamaRuntimeException
	 * 
	 * @param scope IScope
	 * @param args Arguments
	 * @return Double
	 */
	@action("distance_graph")
	@args({ "graph", "target" })
	public Double primDistanceGraph(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final Object target = scope.getArg("target");
		if ( target == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return 0d;
		}
		final GamaGraph graph =
			(GamaGraph) Types.coerce(scope, scope.getArg("graph"), Types.get(IType.GRAPH), null);

		final GamaPoint goal =
			target instanceof ILocation ? ((ILocation) target).getLocation() : null;
		GamaPoint source = agent.getLocation();
		GamaPoint targ = goal;
		GamaPath path = GeometricFunctions.pathBetween(scope, source, targ, graph);
		if ( path == null ) { return Double.MAX_VALUE;

		}
		double distance = path.getStartVertex().distance(source);
		distance += path.getEndVertex().distance(targ);
		Collection<IGeometry> edges = path.getEdgeList();
		for ( IGeometry g : edges ) {
			distance += g.getInnerGeometry().getLength();
		}
		return distance;
	}

	@action("towards_graph")
	@args({ "graph", "target" })
	public Integer primDirectionGraph(final IScope scope) throws GamaRuntimeException {
		IAgent agent = getCurrentAgent(scope);
		final Object target = scope.getArg("target");
		if ( target == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return 0;
		}
		final GamaGraph graph = Cast.asGraph(scope.getArg("graph"));
		final GamaPoint goal =
			target instanceof ILocation ? ((ILocation) target).getLocation() : null;
		GamaPoint source = agent.getLocation();
		GamaPoint targ = goal;
		GamaPath path = GeometricFunctions.pathBetween(scope, source, targ, graph);
		if ( path == null ) { return 0; }
		GamaPoint firstPoint = path.getStartVertex();
		return getTopology(agent).directionInDegreesTo(source, firstPoint);
	}

	/**
	 * @throws GamaRuntimeException Updates the weight of a graph
	 * 
	 * 
	 * @param scope IScope
	 * @param args Arguments
	 * @return the prim CommandStatus
	 */
	@action("update_graph")
	@args({ "graph", "agents", "network", "weights", "optimizer_type" })
	public Object primUpdatesGraphEdgeValue(final IScope scope) throws GamaRuntimeException {
		final GamaGraph graph = Cast.asGraph(scope.getArg("graph"));

		if ( graph == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}

		GamaList<IAgent> ags = Cast.asList(scope.getArg("agents"));
		GamaList<IAgent> netw = Cast.asList(scope.getArg("network"));
		String strWgtExp = Cast.asString(scope.getArg("weights"));
		if ( ags.isEmpty() ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		Map weights = new GamaMap();

		if ( strWgtExp != null ) {
			for ( IAgent ag : ags ) {
				Object v = scope.getAgentVarValue(ag, strWgtExp);
				if ( v != null ) {
					weights.put(ag.getGeometry(), Cast.asFloat(scope, v));
				}
			}
		}

		if ( !netw.isEmpty() ) {
			if ( strWgtExp != null ) {
				for ( IAgent ag : netw ) {
					Object v = scope.getAgentVarValue(ag, strWgtExp);
					if ( v != null ) {
						weights.put(ag.getGeometry(), Cast.asFloat(scope, v));
					}
				}
			}
		}

		graph.setWeights(weights);
		// graph.updateEdgesVal();
		String optiType = Cast.asString(scope.getArg("optimizer_type"));
		if ( optiType == null ) {
			optiType = "static";
		}
		graph.setOptimizerType(optiType);

		// graph.computeShortestPath(optiType);
		return null; // ??
	}

	/**
	 * @throws GamaRuntimeException compute and save the graph resulting either from the
	 *             triangulation of the agent geometry or from a list of agent which geometries are
	 *             polylines
	 * 
	 * @param args : geometry -> optional, a list describing a geometry; agent -> optional, a
	 *            localized entity; name -> string, name of the graph; optimizer_type -> optional,
	 *            String, type of optimizer : "dynamic", "progressive", "static", by default,
	 *            "static" network -> optional, a list of localized entity with polylines for
	 *            geometry or a list of polyline agents -> optional, a list of localized entities
	 *            weight -> optional, the name of the attribute used to compute the weight of each
	 *            edge
	 * 
	 * @param scope IScope
	 * @return the prim CommandStatus
	 */
	@action("compute_graph")
	@args({ "agents", "geometry", "agent", "triangles", "network", "split_lines", "name",
		"optimizer_type", "weights", "size_parts" })
	public Object primComputeGraph(final IScope scope) throws GamaRuntimeException {
		IAgent agent = getCurrentAgent(scope);
		// Chrono c = new Chrono();
		// Chrono c1 = new Chrono();
		// c.start();
		GamaList network = Cast.asList(scope.getArg("network"));
		GamaGraph graph = null;
		boolean polygon = true;
		String nameGp = Cast.asString(scope.getArg("name"));
		Double sizeParts = Cast.asFloat(scope.getArg("size_parts"));
		/*
		 * 
		 * 
		 * String saveFileName = null; String loadFileName = null; if (
		 * Cast.asString(scope.getArg("save_file") != null ) { try { saveFileName =
		 * scope.getSimulationScope().getModelFileManager().constructAbsoluteFilePath(
		 * Cast.asString(scope.getArg("save_file"), false); } catch (GamlException e) {
		 * e.printStackTrace(); } }
		 * 
		 * if ( Cast.asString(scope.getArg("load_file") != null ) { try { loadFileName =
		 * scope.getSimulationScope().getModelFileManager().constructAbsoluteFilePath(
		 * Cast.asString(scope.getArg("load_file"), true); } catch (GamlException e) {
		 * e.printStackTrace(); } }
		 */
		/*
		 * if (loadFileName != null) { GraphGeometry graphG = (GraphGeometry)
		 * SerialiseDeserialise.relitFichierXML(loadFileName); if (graphG != null &&
		 * (graphG.getShortestPath() == null || graphG.getShortestPath().isEmpty())) { Boolean shPat
		 * = args.boolValue("compute_shortest_path"); if (shPat != null && shPat.booleanValue())
		 * graphG.computeAllShorstestPathes(); } String nameGp = Cast.asString(scope.getArg("name");
		 * if (nameGp != null && graphG != null)
		 * scope.getSimulationScope().getEnvironmentManager().getComputedGraphs().put(nameGp,
		 * graphG); return CommandStatus.success; }
		 */
		Map<Object, Double> weights = new HashMap<Object, Double>();
		if ( network != null && !network.isEmpty() ) {
			polygon = false;
			GamaList<LineString> lines = new GamaList();
			if ( network.first() instanceof IAgent ) {
				for ( Object ag : network ) {
					Geometry geom = ((IAgent) ag).getInnerGeometry();
					if ( geom instanceof LineString ) {
						lines.add((LineString) geom);
					} else if ( geom instanceof MultiLineString ) {
						int nb = ((MultiLineString) geom).getNumGeometries();
						for ( int i = 0; i < nb; i++ ) {
							lines.add((LineString) ((MultiLineString) geom).getGeometryN(i));
						}
					}
				}
			} else {
				for ( Object coords : network ) {
					Geometry geom =
						GeometricFunctions.buildGeometryJTS((List<List<List<GamaPoint>>>) coords);
					if ( geom instanceof LineString ) {
						lines.add((LineString) geom);
					}
				}
			}
			if ( !lines.isEmpty() ) {
				Boolean splitLines =
					scope.hasArg("split_lines") ? Cast.asBool(scope.getArg("split_lines")) : true;

				graph = GeometricFunctions.buildNetworkGraph(scope, lines, splitLines);
				String strWgtExp = Cast.asString(scope.getArg("weights"));

				if ( strWgtExp != null && graph != null && network.first() instanceof IAgent ) {
					// System.out.println("wgts2 : " + wgts2);
					Map<IAgent, Geometry> geomsBuff = new GamaMap();
					for ( Object obj : network ) {
						IAgent ag = (IAgent) obj;
						geomsBuff.put(ag, ag.getInnerGeometry().buffer(0.5));
					}
					Collection<GamaGeometry> edges = graph.edgeSet();
					for ( GamaGeometry n : edges ) {
						for ( IAgent ag : geomsBuff.keySet() ) {
							if ( geomsBuff.get(ag).covers(n.getInnerGeometry()) ) {
								Double var = Cast.asFloat(scope.getAgentVarValue(ag, strWgtExp));
								weights.put(n, var);
								// n.setAgentIndex(ag.getIndex());
								break;
							}
						}
					}
					graph.setWeights(weights);
					// graph.updateEdgesVal();
				}

			}

		} else {
			GamaList<IAgent> ags = Cast.asList(scope.getArg("agents"));
			GamaList<IAgent> trs = Cast.asList(scope.getArg("triangles"));
			String strWgtExp = Cast.asString(scope.getArg("weights"));
			Map<Polygon, Double> agsGeom = new HashMap<Polygon, Double>();
			// Map<Polygon, Integer> agsIndex = new HashMap<Polygon, Integer>();

			if ( !ags.isEmpty() || !trs.isEmpty() ) {
				GamaList<Polygon> triangles = new GamaList<Polygon>();
				if ( !ags.isEmpty() ) {
					for ( IAgent ag : ags ) {
						List<Polygon> ts = GeometricFunctions.triangulation(ag.getInnerGeometry());
						if ( strWgtExp != null ) {
							Object v = scope.getAgentVarValue(ag, strWgtExp);
							if ( v != null ) {
								for ( Polygon p : ts ) {
									agsGeom.put(p, Cast.asFloat(v));
								}
							}
						}
						triangles.addAll(ts);
					}
				} else if ( !trs.isEmpty() ) {
					for ( IAgent ag : trs ) {
						Geometry g = ag.getInnerGeometry();
						if ( g instanceof MultiPolygon && g.getNumGeometries() == 1 &&
							g.getNumPoints() == 4 ) {
							g = g.getGeometryN(0);
						} else if ( !(g instanceof Polygon) ) {
							continue;
						}
						triangles.add((Polygon) g);
						if ( strWgtExp != null ) {
							Object v = scope.getAgentVarValue(ag, strWgtExp);
							if ( v != null ) {
								agsGeom.put((Polygon) g, Cast.asFloat(v));
							}
						}

					}
				}

				if ( !triangles.isEmpty() ) {
					graph = GeometricFunctions.buildPolygonGraph(scope, triangles);
					// System.out.println("wgts : " + wgts);
					if ( !agsGeom.isEmpty() && graph != null ) {
						// System.out.println("wgts2 : " + wgts2);

						Collection<GamaGeometry> nodes = graph.vertexSet();
						for ( GamaGeometry n : nodes ) {
							Polygon geom = (Polygon) n.getInnerGeometry();
							Double w = agsGeom.get(geom);
							// System.out.println("sp : " + sp);
							if ( w != null ) {
								// System.out.println("w : " + w);
								weights.put(n, w);

							}
							// Integer ind = agsIndex.get(geom);
							// if ( ind != null ) {
							// n.setAgentIndex(ind);
							// }
						}

						// System.out.println("weights LA : " + weights);
						graph.setWeights(weights);
						// graph.updateEdgesVal();
					}

				}

			} else {
				List<List<List<GamaPoint>>> coords = Cast.asList(scope.getArg("geometry"));
				IAgent ag = Cast.asAgent(scope.getArg("agent"));
				Geometry geom = null;
				if ( ag != null ) {
					geom = ag.getInnerGeometry();
				} else if ( !coords.isEmpty() ) {
					geom = GeometricFunctions.buildGeometryJTS(coords);
				} else {
					geom = agent.getInnerGeometry();
				}
				GamaList triangles = new GamaList();
				if ( sizeParts != null && sizeParts.doubleValue() > 0 ) {
					List<Geometry> parts = GeometricFunctions.discretisation(geom, sizeParts, true);
					int i = 0;
					for ( Geometry gg : parts ) {
						// if (i < imax) {
						// } else {
						List ts = GeometricFunctions.triangulation(gg);
						// }
						// List p = new GamaList();
						// p.add(gg);
						// p.add(ts);
						// partsTg.add(p);
						triangles.addAll(ts);
						i++;
					}
				} else {

					// c1.start();
					triangles = GeometricFunctions.triangulation(geom);
					// c1.stop();
				}
				// System.out.println("TEMPS triangulation : " + c1.getMilliSec());

				if ( !triangles.isEmpty() ) {
					// c1.start();
					graph = GeometricFunctions.buildPolygonGraph(scope, triangles);
					// c1.stop();
					// System.out.println("TEMPS build graph : " + c1.getMilliSec());

					if ( !agsGeom.isEmpty() && graph != null ) {

						Collection<GamaGeometry> nodes = graph.vertexSet();
						for ( GamaGeometry n : nodes ) {
							// Polygon gg = (Polygon) n.getGeom();
							Double w = agsGeom.get(geom);

							// AD: geom does not change ??
							if ( w != null ) {
								weights.put(n, w);
							}
						}
						graph.setWeights(weights);
						// graph.updateEdgesVal();
						// graph.setParts(partsTg);
					}
					// msi.gama.extensions.geometry.graph.Graph gN = new
					// msi.gama.extensions.geometry.graph.Graph(scope.getSimulationScope(),Cast.asString(scope.getArg("name"),
					// triangles);
					// gN.computeShortestPath();
				}
			}
		}
		if ( graph == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		// graph.setPolygon(polygon);
		String optiType = Cast.asString(scope.getArg("optimizer_type"));
		if ( optiType == null ) {
			optiType = "static";
		}

		// c1.start();
		// graph.computeShortestPath(optiType);
		// c1.stop();
		// System.out.println("TEMPS sp : " + c1.getMilliSec());

		// getEnvironment(agent).getComputedGraphs().put(nameGp, graph);
		/*
		 * if (saveFileName != null) { System.out.println("saveFileName : " + saveFileName);
		 * SerialiseDeserialise.ecritDansFichierXML(graphG, saveFileName); }
		 */
		// c.stop();
		// System.out.println("TEMPS complet : " + c.getMilliSec());
		return null;

	}

	protected ITopology getTopology(final IAgent agent) {
		return agent.getTopology();
	}

}
