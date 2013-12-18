package idees.gama.io;

import idees.gama.agents.*;
import java.io.*;
import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gaml.types.GamaGeometryType;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.*;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;

public class OsmReader {

	Map<Long, GamaPoint> nodesPt;
	List<Node> nodes;
	List<Way> ways;
	List<Relation> relations;
	Set<Long> intersectionNodes;
	Map<GamaPoint, Map> signals;
	Map<GamaPoint, OsmNodeAgent> nodes_created;
	IProjection gis;

	public OsmReader(final IProjection gis) {
		ways = new GamaList<Way>();
		nodesPt = new GamaMap<Long, GamaPoint>();
		nodes = new GamaList<Node>();
		intersectionNodes = new HashSet<Long>();
		nodes_created = new GamaMap<GamaPoint, OsmNodeAgent>();
		signals = new GamaMap<GamaPoint, Map>();
		this.gis = gis;
		// relations = new GamaList<Relation>(); Not used for the moment...
	}

	public void loadFile(final File file, final boolean splitLines) throws FileNotFoundException {
		Sink sinkImplementation = new Sink() {

			Set<Long> usedNodes = new HashSet<Long>();

			@Override
			public void process(final EntityContainer entityContainer) {
				Entity entity = entityContainer.getEntity();

				if ( entity instanceof Node ) {
					Node node = (Node) entity;
					nodes.add(node);
					nodesPt.put(node.getId(), new GamaPoint(node.getLongitude(), node.getLatitude()));
				} else if ( entity instanceof Way ) {
					if ( splitLines ) {
						if ( toConsider((Way) entity, usedNodes, intersectionNodes) ) {
							ways.add((Way) entity);
						}
					} else {
						if ( toConsider(entity) ) {
							ways.add((Way) entity);
						}
					}

				}/*
				 * else if (entity instanceof Relation) { Not used for the moment...
				 * relations.add((Relation)entity);
				 * }
				 */
			}

			@Override
			public void release() {}

			@Override
			public void complete() {}

			@Override
			public void initialize(final Map<String, Object> arg0) {}
		};
		boolean pbf = false;
		CompressionMethod compression = CompressionMethod.None;

		if ( file.getName().endsWith(".pbf") ) {
			pbf = true;
		} else if ( file.getName().endsWith(".gz") ) {
			compression = CompressionMethod.GZip;
		} else if ( file.getName().endsWith(".bz2") ) {
			compression = CompressionMethod.BZip2;
		}

		RunnableSource reader;

		if ( pbf ) {
			reader = new crosby.binary.osmosis.OsmosisReader(new FileInputStream(file));
		} else {
			reader = new XmlReader(file, false, compression);
		}

		reader.setSink(sinkImplementation);

		Thread readerThread = new Thread(reader);
		readerThread.start();

		while (readerThread.isAlive()) {
			try {
				readerThread.join();
			} catch (InterruptedException e) {
				/* do nothing */
			}
		}
	}

	public boolean toConsider(final Way way, final Set<Long> usedNodes, final Set<Long> intersectionNodes) {
		for ( Tag tg : way.getTags() ) {
			String key = tg.getKey();
			if ( key.equals("highway") ) {
				List<WayNode> nodes = way.getWayNodes();
				for ( WayNode node : nodes ) {
					long id = node.getNodeId();
					if ( usedNodes.contains(id) ) {
						intersectionNodes.add(id);
					} else {
						usedNodes.add(id);
					}
				}
				if ( nodes.size() > 2 && nodes.get(0) == nodes.get(nodes.size() - 1) ) {
					intersectionNodes.add(nodes.get(nodes.size() / 2).getNodeId());
				}
				return true;
			} else if ( key.equals("building") ) { return true; }
		}
		return false;
	}

	public boolean toConsider(final Entity entity) {
		for ( Tag tg : entity.getTags() ) {
			String key = tg.getKey();
			if ( key.equals("highway") || key.equals("building") ) { return true; }
		}
		return false;
	}

	public List createBuilding(final IScope scope, final IPopulation buildingPop, final Way way, final Map values) {
		List<IShape> points = new GamaList<IShape>();
		List<Map> initialValues = new GamaList<Map>();

		for ( WayNode node : way.getWayNodes() ) {
			points.add(nodesPt.get(node.getNodeId()));
		}
		if ( points.size() < 3 ) { return new GamaList(); }
		IShape geom = GamaGeometryType.buildPolygon(points);
		if ( geom != null && geom.getInnerGeometry().isValid() && geom.getInnerGeometry().getCoordinates().length > 2 &&
			geom.getInnerGeometry().getArea() > 0 ) {
			values.put("shape", new GamaShape(gis.transform(geom.getInnerGeometry())));
			initialValues.add(values);
			return buildingPop.createAgents(scope, 1, initialValues, false);
		}
		return new GamaList();
	}

	public List createRoad(final IScope scope, final IPopulation roadPop, final IPopulation nodePop, final Way way,
		final Map values) {
		List<IShape> points = new GamaList<IShape>();
		return createRoad(scope, roadPop, nodePop, points, values);
	}

	public List createRoad(final IScope scope, final IPopulation roadPop, final IPopulation nodePop,
		final List<IShape> points, final Map values) {
		List agents = new GamaList();
		Boolean oneWay = (Boolean) values.get("oneway");
		if ( oneWay != null && oneWay ) {
			List<Map> initialValues = new GamaList<Map>();
			IShape geom = GamaGeometryType.buildPolyline(points);
			if ( geom != null && geom.getInnerGeometry().isValid() &&
				geom.getInnerGeometry().getCoordinates().length > 1 && geom.getInnerGeometry().getArea() == 0 ) {
				GamaShape shape = new GamaShape(gis.transform(geom.getInnerGeometry()));
				values.put("shape", shape);
				initialValues.add(values);
				agents.addAll(roadPop.createAgents(scope, 1, initialValues, false));
				if ( nodePop != null ) {
					createNodes(scope, nodePop, shape.getPoints().first(scope), shape.getPoints().last(scope),
						(OsmRoadAgent) agents.get(0));
				}

			}
		} else {
			IShape geom1 = GamaGeometryType.buildPolyline(points);
			GamaList<IShape> points2 = new GamaList<IShape>(points);
			Collections.reverse(points2);
			IShape geom2 = GamaGeometryType.buildPolyline(points2);
			if ( geom1 != null && geom1.getInnerGeometry().isValid() &&
				geom1.getInnerGeometry().getCoordinates().length > 1 && geom1.getInnerGeometry().getArea() == 0 ) {
				List<Map> initialValues = new GamaList<Map>();
				Map valuesAg = new GamaMap();
				for ( Object val : values.keySet() ) {
					valuesAg.put(val, values.get(val));
				}
				valuesAg.put("name", valuesAg.get("name") + "-Dir");
				GamaShape shape = new GamaShape(gis.transform(geom1.getInnerGeometry()));
				valuesAg.put("shape", shape);
				initialValues.add(valuesAg);
				agents.addAll(roadPop.createAgents(scope, 1, initialValues, false));
				if ( nodePop != null ) {
					createNodes(scope, nodePop, shape.getPoints().first(scope), shape.getPoints().last(scope),
						(OsmRoadAgent) agents.get(agents.size() - 1));
				}

			}
			if ( geom2 != null && geom2.getInnerGeometry().isValid() &&
				geom2.getInnerGeometry().getCoordinates().length > 1 && geom2.getInnerGeometry().getArea() == 0 ) {
				List<Map> initialValues = new GamaList<Map>();
				Map valuesAg = new GamaMap();
				for ( Object val : values.keySet() ) {
					valuesAg.put(val, values.get(val));
				}
				valuesAg.put("name", valuesAg.get("name") + "-Rev");
				GamaShape shape = new GamaShape(gis.transform(geom2.getInnerGeometry()));
				valuesAg.put("shape", shape);
				initialValues.add(valuesAg);
				agents.addAll(roadPop.createAgents(scope, 1, initialValues, false));
				if ( nodePop != null ) {
					createNodes(scope, nodePop, shape.getPoints().first(scope), shape.getPoints().last(scope),
						(OsmRoadAgent) agents.get(agents.size() - 1));
				}
			}
		}
		return agents;
	}

	public void createSignal(final IScope scope, final Node node, final Map values) {
		GamaPoint pt = null;
		pt = nodesPt.get(node.getId());
		if ( pt != null ) {
			GamaShape ptT = new GamaShape(gis.transform(pt.getInnerGeometry()));
			signals.put((GamaPoint) ptT.getLocation(), values);
		}
	}

	public List createNodes(final IScope scope, final IPopulation nodePop, final GamaPoint ptInit,
		final GamaPoint ptFinal, final OsmRoadAgent road) {
		List agentsCreated = new GamaList();
		if ( nodes_created.containsKey(ptInit) ) {
			OsmNodeAgent na = nodes_created.get(ptInit);
			road.setAttribute("source_node", na);
			GamaList road_out = (GamaList) na.getAttribute("roads_out");
			road_out.add(road);
			na.setAttribute("roads_out", road_out);
		} else {
			Map values = new GamaMap();
			List<OsmRoadAgent> road_out = new GamaList<OsmRoadAgent>();
			road_out.add(road);
			values.put("roads_out", road_out);
			IAgent nodeSource = (IAgent) createNodes(scope, nodePop, ptInit, values).get(0);
			road.setAttribute("source_node", nodeSource);
			agentsCreated.add(nodeSource);
		}
		if ( nodes_created.containsKey(ptFinal) ) {
			OsmNodeAgent na = nodes_created.get(ptFinal);
			GamaList road_in = (GamaList) na.getAttribute("roads_in");
			road.setAttribute("target_node", na);
			road_in.add(road);
			na.setAttribute("roads_in", road_in);
		} else {
			Map values = new GamaMap();
			List<OsmRoadAgent> road_in = new GamaList<OsmRoadAgent>();
			road_in.add(road);
			values.put("roads_in", road_in);
			IAgent nodeTarget = (IAgent) createNodes(scope, nodePop, ptFinal, values).get(0);
			road.setAttribute("target_node", nodeTarget);
			agentsCreated.add(nodeTarget);

		}
		return new GamaList();
	}

	public List createNodes(final IScope scope, final IPopulation nodePop, final GamaPoint pt, final Map values) {
		List<Map> initialValues = new GamaList<Map>();
		if ( pt != null ) {
			values.put("shape", pt.getGeometry());

			if ( signals.containsKey(pt) ) {
				Map signal = signals.get(pt);
				for ( Object si : signal.keySet() ) {
					values.put(si, signal.get(si));
				}
			}
			initialValues.add(values);
			List ags = nodePop.createAgents(scope, 1, initialValues, false);
			nodes_created.put(pt, (OsmNodeAgent) ags.get(0));
			return ags;
		}
		return new GamaList();
	}

	public List createSplitRoad(final IScope scope, final IPopulation roadPop, final IPopulation nodePop,
		final Way way, final Map values, final Set<Long> intersectionNodes) {
		List<List<IShape>> pointsList = new GamaList<List<IShape>>();
		List<Map> initialValues = new GamaList<Map>();
		List<IShape> points = new GamaList<IShape>();
		List agents_created = new GamaList();
		for ( WayNode node : way.getWayNodes() ) {
			Long id = node.getNodeId();
			GamaPoint pt = nodesPt.get(id);
			points.add(pt);
			if ( intersectionNodes.contains(id) ) {
				if ( points.size() > 1 ) {
					pointsList.add(points);
				}
				points = new GamaList<IShape>();
				points.add(pt);

			}
		}
		int cpt = 1;
		boolean one = pointsList.size() == 1;
		for ( List<IShape> pts : pointsList ) {
			if ( one ) {
				agents_created.addAll(createRoad(scope, roadPop, nodePop, pts, values));
			} else {
				Map valuesAg = new GamaMap();
				for ( Object val : values.keySet() ) {
					valuesAg.put(val, values.get(val));
				}
				valuesAg.put("name", valuesAg.get("name") + "-" + cpt);
				agents_created.addAll(createRoad(scope, roadPop, nodePop, pts, valuesAg));
				cpt++;
			}
		}
		return agents_created;

	}

	public List<IAgent> buildAgents(final IScope scope, final IPopulation roadPop, final IPopulation buildingPop,
		final IPopulation nodePop, final boolean splitLines) {
		List<IAgent> createdAgents = new GamaList<IAgent>();
		List<String> boolAtt = new GamaList<String>();
		boolAtt.add("oneway");
		boolAtt.add("motorroad");
		boolAtt.add("wall");
		boolAtt.add("bridge");
		for ( Node node : nodes ) {
			Map values = new GamaMap();
			for ( Tag tg : node.getTags() ) {
				String key = tg.getKey();
				values.put(key, tg.getValue());
			}
			if ( values.containsKey("highway") ) {
				intersectionNodes.add(node.getId());
				createSignal(scope, node, values);
			}
		}
		for ( Way way : ways ) {
			Map values = new GamaMap();

			for ( Tag tg : way.getTags() ) {
				String key = tg.getKey();
				// System.out.println("TAG : " + tg.getKey() + " -> " + tg.getValue());
				if ( boolAtt.contains(key) ) {
					values.put(key, tg.getValue().equals("yes"));
				} else {
					values.put(key, tg.getValue());
				}
			}
			boolean isRoad = values.containsKey("highway");
			boolean isBuilding = values.containsKey("building");

			if ( isBuilding && buildingPop != null ) {
				createdAgents.addAll(createBuilding(scope, buildingPop, way, values));
			} else if ( isRoad ) {
				if ( roadPop != null && way.getWayNodes().size() > 1 ) {
					if ( !splitLines ) {
						createdAgents.addAll(createRoad(scope, roadPop, nodePop, way, values));
					} else {
						createdAgents.addAll(createSplitRoad(scope, roadPop, nodePop, way, values, intersectionNodes));
					}
				}
			}

		}

		return createdAgents;
	}

}
