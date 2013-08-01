package idees.gama.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

	  Map<Long, GamaPoint> nodes;
	  List<Way> ways;
	  List<Relation> relations;
	  Set<Long> intersectionNodes;
	  
	  public OsmReader() {
		  ways = new GamaList<Way>();
		  nodes = new GamaMap<Long, GamaPoint>();
		  intersectionNodes = new HashSet<Long>();
		 // relations = new GamaList<Relation>(); Not used for the moment...
	  } 
		
	 public void loadFile(File file, final boolean splitLines) throws FileNotFoundException {
		 Sink sinkImplementation = new Sink() {
			 Set<Long> usedNodes = new HashSet<Long>();
			 
			 public void process(EntityContainer entityContainer) {
			        Entity entity = entityContainer.getEntity();
			    	
			        if (entity instanceof Node) {
			        	Node node = (Node) entity;
			        	nodes.put(node.getId(), new GamaPoint(node.getLongitude(), node.getLatitude())) ;
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
					for (WayNode node : way.getWayNodes()) {
						 long id = node.getNodeId();
						 if (usedNodes.contains(id)) 
							 intersectionNodes.add(id);
						 else
							 usedNodes.add(id);						
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
			 points.add(nodes.get(node.getNodeId()));
		}
		IShape geom = GamaGeometryType.buildPolygon(points);
		if (geom != null) {
			values.put("shape", new GamaShape(gisUtils.transform(geom.getInnerGeometry())));
				 initialValues.add(values);
				 return buildingPop.createAgents(scope, 1, initialValues, false);
			}
		return new GamaList();
	 }
	 
	 public List createRoad(IScope scope, IPopulation roadPop,GisUtils gisUtils, Way way,Map values) {
		List<IShape> points = new GamaList<IShape>();
		List<Map> initialValues = new GamaList<Map>();
				
		for (WayNode node : way.getWayNodes()) {
			 points.add(nodes.get(node.getNodeId()));
		}
		IShape geom = GamaGeometryType.buildPolyline(points);
		if (geom != null) {
			values.put("shape", new GamaShape(gisUtils.transform(geom.getInnerGeometry())));
				 initialValues.add(values);
				 return roadPop.createAgents(scope, 1, initialValues, false);
			}
		return new GamaList();
	 }
	 
	 public List createSplitRoad(IScope scope, IPopulation roadPop,GisUtils gisUtils, Way way,Map values,Set<Long> intersectionNodes) {
		List<List<IShape>> pointsList = new GamaList<List<IShape>>();
		List<Map> initialValues = new GamaList<Map>();
		List<IShape> points = new GamaList<IShape>();
		for (WayNode node : way.getWayNodes()) {
			Long id = node.getNodeId();
			GamaPoint pt = nodes.get(id);
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
			IShape geom = GamaGeometryType.buildPolyline(pts);
			if (geom != null) {
				if (one) {
					values.put("shape", new GamaShape(gisUtils.transform(geom.getInnerGeometry())));
					initialValues.add(values);
				} else {
					Map valuesAg = new GamaMap();
					for (Object val : values.keySet()) {
						valuesAg.put(val, values.get(val));
					}
					valuesAg.put("name", valuesAg.get("name") + "-" + cpt);
					valuesAg.put("shape", new GamaShape(gisUtils.transform(geom.getInnerGeometry())));
					initialValues.add(valuesAg);
					cpt++;
				}
			}
		}
		return roadPop.createAgents(scope, initialValues.size(), initialValues, false);
		
	}
		 
	 public List<IAgent> buildAgents(IScope scope, IPopulation roadPop, IPopulation buildingPop, boolean splitLines){
		 List<IAgent> createdAgents = new GamaList<IAgent>();
		 GisUtils gisUtils = scope.getTopology().getGisUtils();
		 List<String> boolAtt =  new GamaList<String>();
		 boolAtt.add("oneway");
		 boolAtt.add("motorroad");
		 boolAtt.add("wall");
		 boolAtt.add("bridge");
		 
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
			
			if (isBuilding) {
				 createdAgents.addAll(createBuilding(scope, buildingPop,gisUtils, way,values));
			} else if (isRoad) {
				if (!splitLines) {
					 createdAgents.addAll(createRoad(scope, roadPop,gisUtils, way,values));
				} else {
					 createdAgents.addAll(createSplitRoad(scope, roadPop,gisUtils, way,values,intersectionNodes));
				}
			}
			
		 }
		 
		 return createdAgents;
	 }

}
