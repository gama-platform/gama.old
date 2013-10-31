package idees.gama.io;

import idees.gama.agents.OsmNodeAgent;
import idees.gama.agents.OsmRoadAgent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;


import msi.gama.common.util.GisUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gaml.types.GamaGeometryType;


public class OsmReader {

	  Map<Long, GamaPoint> nodesPt;
	  List<Node> nodes;
	  List<Way> ways;
	  List<Relation> relations;
	  Set<Long> intersectionNodes;
	  Map<GamaPoint,Map> signals;
	  Map<GamaPoint,OsmNodeAgent> nodes_created;
	  
	  public OsmReader() {
		  ways = new GamaList<Way>();
		  nodesPt = new GamaMap<Long, GamaPoint>();
		  nodes = new GamaList<Node>();
		  intersectionNodes = new HashSet<Long>();
		  nodes_created = new GamaMap<GamaPoint, OsmNodeAgent>();
		  signals = new GamaMap<GamaPoint, Map>();
		 // relations = new GamaList<Relation>(); Not used for the moment...
	  } 
		
	 public void loadFile(File file, final boolean splitLines) throws FileNotFoundException {
		 Sink sinkImplementation = new Sink() {
			 Set<Long> usedNodes = new HashSet<Long>();
			 
			 public void process(EntityContainer entityContainer) {
				 	Entity entity = entityContainer.getEntity();
			    	
			        if (entity instanceof Node) {
			        	Node node = (Node) entity;
			        	nodes.add(node);
			        	nodesPt.put(node.getId(), new GamaPoint(node.getLongitude(), node.getLatitude())) ;
			        } else if (entity instanceof Way) {
			        	if (splitLines) {
			        		if (toConsider((Way)entity,usedNodes, intersectionNodes))
			        			ways.add((Way) entity);
			        	} else {
			        		if (toConsider(entity))
				        		ways.add((Way) entity);
			        	}
			        	
			        }/* else if (entity instanceof Relation) {  Not used for the moment...
			        	relations.add((Relation)entity);
			        }*/
			    }
			    public void release() { }
			    public void complete() { }
				public void initialize(Map<String, Object> arg0) {}
			};
			boolean pbf = false;
			CompressionMethod compression = CompressionMethod.None;
		
			if (file.getName().endsWith(".pbf")) {
			    pbf = true;
			} else if (file.getName().endsWith(".gz")) {
			    compression = CompressionMethod.GZip;
			} else if (file.getName().endsWith(".bz2")) {
			    compression = CompressionMethod.BZip2;
			}
		
			RunnableSource reader;
		
			if (pbf) {
			    reader = new crosby.binary.osmosis.OsmosisReader(
			            new FileInputStream(file));
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
	 
	 
	 public boolean toConsider(Way way, Set<Long> usedNodes, Set<Long> intersectionNodes) {
		 for (Tag tg : way.getTags()) {
				String key = tg.getKey();
				if (key.equals("highway")) {
					List<WayNode> nodes = way.getWayNodes();
					for (WayNode node : nodes) {
						 long id = node.getNodeId();
						 if (usedNodes.contains(id)) 
							 intersectionNodes.add(id);
						 else
							 usedNodes.add(id);						
					}
					if (nodes.size() > 2 && nodes.get(0) == nodes.get(nodes.size() -1)) {
						intersectionNodes.add( nodes.get((int)(nodes.size() / 2)).getNodeId());
					}
					return true;
				} else if (key.equals("building")) {
					return true;
				}		
		 }
		 return false;
	 }
	 
	 public boolean toConsider(Entity entity) {
		 for (Tag tg : entity.getTags()) {
				String key = tg.getKey();
				if (key.equals("highway") || key.equals("building")) 
					return true;
		 }
		 return false;
	 }
	 
	 public List createBuilding(IScope scope, IPopulation buildingPop,GisUtils gisUtils, Way way,Map values) {
		List<IShape> points = new GamaList<IShape>();
		List<Map> initialValues = new GamaList<Map>();
			
		for (WayNode node : way.getWayNodes()) {
			 points.add(nodesPt.get(node.getNodeId()));
		}
		if (points.size() < 3) return new GamaList();
		IShape geom = GamaGeometryType.buildPolygon(points);
		if (geom != null && geom.getInnerGeometry().isValid() &&  geom.getInnerGeometry().getCoordinates().length >2 && geom.getInnerGeometry().getArea() > 0) {
			values.put("shape", new GamaShape(gisUtils.transform(geom.getInnerGeometry())));
				 initialValues.add(values);
				 return buildingPop.createAgents(scope, 1, initialValues, false);
			}
		return new GamaList();
	 }
	 
	 public List createRoad(IScope scope, IPopulation roadPop,IPopulation nodePop, GisUtils gisUtils, Way way,Map values) {
		List<IShape> points = new GamaList<IShape>();
		return createRoad(scope, roadPop,nodePop, gisUtils, points,values);
	 }
	 
	 public List createRoad(IScope scope, IPopulation roadPop,IPopulation nodePop, GisUtils gisUtils, List<IShape> points,Map values) {
		List agents = new GamaList();
		Boolean oneWay = (Boolean) values.get("oneway");
		if (oneWay != null && oneWay) {
			List<Map> initialValues = new GamaList<Map>();
			IShape geom = GamaGeometryType.buildPolyline(points);
			if (geom != null && geom.getInnerGeometry().isValid() &&  geom.getInnerGeometry().getCoordinates().length > 1 && geom.getInnerGeometry().getArea()==0) {
				GamaShape shape = new GamaShape(gisUtils.transform(geom.getInnerGeometry()));
				values.put("shape", shape);
				initialValues.add(values);
				agents.addAll(roadPop.createAgents(scope, 1, initialValues, false));
				if (nodePop != null)
					createNodes(scope,nodePop,gisUtils, shape.getPoints().first(scope), shape.getPoints().last(scope), (OsmRoadAgent) agents.get(0));
				
			}
		} else {
			IShape geom1 = GamaGeometryType.buildPolyline(points);
			GamaList<IShape> points2 = new GamaList<IShape>(points);
			Collections.reverse(points2);
			IShape geom2 = GamaGeometryType.buildPolyline(points2);
			if (geom1 != null && geom1.getInnerGeometry().isValid() &&  geom1.getInnerGeometry().getCoordinates().length > 1 && geom1.getInnerGeometry().getArea() == 0) {
				List<Map> initialValues = new GamaList<Map>();
				Map valuesAg = new GamaMap();
				for (Object val : values.keySet()) {
					valuesAg.put(val, values.get(val));
				}
				valuesAg.put("name", valuesAg.get("name") + "-Dir");
				GamaShape shape = new GamaShape(gisUtils.transform(geom1.getInnerGeometry()));
				valuesAg.put("shape", shape);
				initialValues.add(valuesAg);
				agents.addAll(roadPop.createAgents(scope, 1, initialValues, false));
				if (nodePop != null)
					createNodes(scope,nodePop,gisUtils, shape.getPoints().first(scope), shape.getPoints().last(scope), (OsmRoadAgent) agents.get(agents.size() -1));
				
			}
			if (geom2 != null&& geom2.getInnerGeometry().isValid() &&  geom2.getInnerGeometry().getCoordinates().length > 1 && geom2.getInnerGeometry().getArea() == 0) {
				List<Map> initialValues = new GamaList<Map>();
				Map valuesAg = new GamaMap();
				for (Object val : values.keySet()) {
					valuesAg.put(val, values.get(val));
				}
				valuesAg.put("name", valuesAg.get("name") + "-Rev");
				GamaShape shape = new GamaShape(gisUtils.transform(geom2.getInnerGeometry()));
				valuesAg.put("shape", shape);
				initialValues.add(valuesAg);
				agents.addAll(roadPop.createAgents(scope, 1, initialValues, false));
				if (nodePop != null)
					createNodes(scope,nodePop,gisUtils, shape.getPoints().first(scope), shape.getPoints().last(scope), (OsmRoadAgent) agents.get(agents.size() -1));
			}
		}
		return agents;
	 }
		 
	 
	 public void createSignal(IScope scope, GisUtils gisUtils, Node node,Map values) {
		GamaPoint pt = null;
		pt = nodesPt.get(node.getId());
		if (pt != null) {
			GamaShape ptT = new GamaShape(gisUtils.transform(pt.getInnerGeometry()));
			signals.put((GamaPoint) ptT.getLocation(), values);
		}
	 }
	 
	 public List createNodes(IScope scope, IPopulation nodePop,GisUtils gisUtils, GamaPoint ptInit, GamaPoint ptFinal, OsmRoadAgent road) {
		List agentsCreated = new GamaList();
		if (nodes_created.containsKey(ptInit)) {
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
			IAgent nodeSource = (IAgent) createNodes(scope, nodePop,gisUtils, ptInit, values).get(0);
			road.setAttribute("source_node", nodeSource);
			agentsCreated.add(nodeSource);
		}
		if (nodes_created.containsKey(ptFinal)) {
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
			IAgent nodeTarget = (IAgent) createNodes(scope, nodePop,gisUtils, ptFinal, values).get(0);
			road.setAttribute("target_node", nodeTarget);
			agentsCreated.add(nodeTarget);
			
		}
		return new GamaList();
	}
	 
	 public List createNodes(IScope scope, IPopulation nodePop,GisUtils gisUtils, GamaPoint pt,Map values) {
		List<Map> initialValues = new GamaList<Map>();	
		if (pt != null) {
			values.put("shape", pt.getGeometry());
			
			if (signals.containsKey(pt)) {
				Map signal = signals.get(pt);
				for (Object si : signal.keySet()) {
					values.put(si, signal.get(si) );
				}
			}
			initialValues.add(values);
			List ags = nodePop.createAgents(scope, 1, initialValues, false);
			nodes_created.put(pt, (OsmNodeAgent) ags.get(0));
			return ags;
		}
		return new GamaList(); 
	}
	 
	 public List createSplitRoad(IScope scope, IPopulation roadPop,IPopulation nodePop,GisUtils gisUtils, Way way,Map values,Set<Long> intersectionNodes) {
		List<List<IShape>> pointsList = new GamaList<List<IShape>>();
		List<Map> initialValues = new GamaList<Map>();
		List<IShape> points = new GamaList<IShape>();
		List agents_created = new GamaList();
		for (WayNode node : way.getWayNodes()) {
			Long id = node.getNodeId();
			GamaPoint pt = nodesPt.get(id);
			points.add(pt);
			if (intersectionNodes.contains(id)) {
				if (points.size() > 1)
					pointsList.add(points);
				points = new GamaList<IShape>();
				points.add(pt);
				
			}
		}
		int cpt = 1;
		boolean one = (pointsList.size() == 1);
		for (List<IShape> pts: pointsList) {
			if (one) {
				agents_created.addAll(createRoad(scope, roadPop,nodePop, gisUtils, pts,values));
			} else {
				Map valuesAg = new GamaMap();
				for (Object val : values.keySet()) {
					valuesAg.put(val, values.get(val));
				}
				valuesAg.put("name", valuesAg.get("name") + "-" + cpt);
				agents_created.addAll(createRoad(scope, roadPop,nodePop, gisUtils, pts,valuesAg));
				cpt++;
			}
		}
		return agents_created; 
		
	}
		 
	 public List<IAgent> buildAgents(IScope scope, IPopulation roadPop, IPopulation buildingPop, IPopulation nodePop,boolean splitLines){
		 List<IAgent> createdAgents = new GamaList<IAgent>();
		 GisUtils gisUtils = scope.getTopology().getGisUtils();
		 List<String> boolAtt =  new GamaList<String>();
		 boolAtt.add("oneway");
		 boolAtt.add("motorroad");
		 boolAtt.add("wall");
		 boolAtt.add("bridge");
		 for (Node node : nodes) {
			Map values = new GamaMap();
			for (Tag tg : node.getTags()) {
				String key = tg.getKey();
				values.put(key, tg.getValue());
			}
			if (values.containsKey("highway")) {
				intersectionNodes.add(node.getId());
			    createSignal(scope, gisUtils, node,values);
			 }
		 }
		for (Way way : ways) {
			 Map values = new GamaMap();
			 
			for (Tag tg : way.getTags()) {
				String key = tg.getKey();
				//System.out.println("TAG : " + tg.getKey() + " -> " + tg.getValue());
				if (boolAtt.contains(key) ) {
					values.put(key, (tg.getValue()).equals("yes"));
				} else {
					values.put(key, tg.getValue());
				}
			}
			boolean isRoad = values.containsKey("highway");
			boolean isBuilding = values.containsKey("building");
			
			if (isBuilding && buildingPop != null) {
				 createdAgents.addAll(createBuilding(scope, buildingPop,gisUtils, way,values));
			} else if (isRoad) {
				if (roadPop != null &&  way.getWayNodes().size() > 1) {
					if (!splitLines) {
						createdAgents.addAll(createRoad(scope, roadPop,nodePop,gisUtils, way,values));
					} else {
						createdAgents.addAll(createSplitRoad(scope, roadPop,nodePop,gisUtils, way,values,intersectionNodes));
					}
				}
			}
			
		 }
		
		 return createdAgents;
	 }

}
