package idees.gama.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

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

import com.vividsolutions.jts.geom.Geometry;

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
	  
	  public OsmReader() {
		  ways = new GamaList<Way>();
		  nodes = new GamaMap<Long, GamaPoint>();
		  relations = new GamaList<Relation>();
	  }
		
	 public void loadFile(File file) throws FileNotFoundException {
		 Sink sinkImplementation = new Sink() {
			    public void process(EntityContainer entityContainer) {
			        Entity entity = entityContainer.getEntity();
			        if (entity instanceof Node) {
			        	Node node = (Node) entity;
			        	nodes.put(node.getId(), new GamaPoint(node.getLongitude(), node.getLatitude())) ;
			        } else if (entity instanceof Way) {
			        	ways.add((Way) entity);
			        } else if (entity instanceof Relation) {
			        	relations.add((Relation)entity);
			        }
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
	 
	 public List<IAgent> buildAgents(IScope scope, IPopulation roadPop, IPopulation buildingPop){
		 List<IAgent> createdAgents = new GamaList<IAgent>();
		 GisUtils gisUtils = scope.getTopology().getGisUtils();
		 for (Way way : ways) {
			 List<Map> initialValues = new GamaList<Map>();
			 Map values = new GamaMap();
			 List<IShape> points = new GamaList<IShape>();
			 for (WayNode node : way.getWayNodes()) {
				 points.add(nodes.get(node.getNodeId()));
			 }
			
			for (Tag tg : way.getTags()) {
				//System.out.println("TAG : " + tg.getKey() + " -> " + tg.getValue());
				values.put(tg.getKey(), tg.getValue());
			}
			/*for (String mt : way.getMetaTags().keySet()) {
				System.out.println("METATAGS : " + mt + " -> " + way.getMetaTags().get(mt));
			}*/
			IShape geom = null;
			IPopulation pop = null;
			if (values.get("highway") != null && points.size() > 1) {
				geom = GamaGeometryType.buildPolyline(points);
				pop = roadPop;
			} else if (values.get("building") != null) {
				geom = GamaGeometryType.buildPolygon(points);
				pop = buildingPop;
			}
			if (geom != null) {
				 values.put("shape", new GamaShape(gisUtils.transform(geom.getInnerGeometry())));
				 initialValues.add(values);
				 createdAgents.addAll(pop.createAgents(scope, 1, initialValues, false));
			}
			
		 }
		 return createdAgents;
	 }

}
