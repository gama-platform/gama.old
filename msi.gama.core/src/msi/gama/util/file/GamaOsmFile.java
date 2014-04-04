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
package msi.gama.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gaml.types.GamaGeometryType;

import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Bound;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Written by drogoul
 * Modified on 13 nov. 2011
 * 
 * @todo Description
 * 
 */
@file(name = "osm", extensions = { "osm", "pbf", "bz2", "gz" })
public class GamaOsmFile extends GamaGisFile {

	GamaMap<String, GamaList> filteringOptions;
	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	public GamaOsmFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null);
	}

	public GamaOsmFile(final IScope scope, final String pathName, final Integer code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}
	
	public GamaOsmFile(final IScope scope, final String pathName, final String code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	public GamaOsmFile(IScope scope, String pathName,
			GamaMap<String, GamaList> filteringOptions) {
		super(scope, pathName,  (Integer) null);
		this.filteringOptions = filteringOptions;
	}

	public GamaOsmFile(IScope scope, String pathName,
			GamaMap<String, GamaList> filteringOption, Integer code) {
		super(scope, pathName, code);
		this.filteringOptions = filteringOptions;
	}

	public void getFeatureIterator(final IScope scope, final boolean returnIt) {
		final Map<Long, GamaShape> nodesPt = new GamaMap<Long, GamaShape>();
		final List<Node> nodes = new GamaList<Node>();
		final List<Way> ways = new GamaList<Way>();
		final Set<Long> intersectionNodes = new HashSet<Long>();
		final Set<Long> usedNodes = new HashSet<Long>();

		Sink sinkImplementation = new Sink() {

			@Override
			public void process(final EntityContainer entityContainer) {
				Entity entity = entityContainer.getEntity();
				boolean toFilter = filteringOptions != null && ! filteringOptions.isEmpty();  
				if ( entity instanceof Bound ) {
					Bound bound = (Bound) entity;
					Envelope env = new Envelope(bound.getLeft(), bound.getRight(), bound.getBottom(), bound.getTop());
					computeProjection(scope, env);
				} else if ( returnIt ) {
					if ( entity instanceof Node ) {
						Node node = (Node) entity;
						nodes.add(node);
						Geometry g =
							gis.transform(new GamaPoint(node.getLongitude(), node.getLatitude()).getInnerGeometry());
						nodesPt.put(node.getId(), new GamaShape(g));
					} else if ( entity instanceof Way ) {
						if (toFilter) {
							boolean keepObject = false;
							for (String keyN: filteringOptions.getKeys()) {
								GamaList valsPoss = filteringOptions.get(keyN);
								for (Tag tagN : ((Way) entity).getTags()) {

									if (keyN.equals(tagN.getKey())) {
										if (valsPoss.contains(tagN.getValue())) {
											keepObject = true;
											break;
										}
									} 
								}
							}
							if (! keepObject) return;
						}
						registerHighway((Way) entity, usedNodes, intersectionNodes);
						ways.add((Way) entity);
					}
				}

			}

			@Override
			public void release() {}

			@Override
			public void complete() {}

			@Override
			public void initialize(final Map<String, Object> arg0) {}
		};
		readFile(sinkImplementation, getFile());
		if ( returnIt ) {
			setBuffer(buildGeometries(nodes, ways, intersectionNodes, nodesPt));
		}
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( getBuffer() != null ) { return; }
		setBuffer(new GamaList());
		getFeatureIterator(scope, true);
	}

	public GamaList<IShape> buildGeometries(final List<Node> nodes, final List<Way> ways,
		final Set<Long> intersectionNodes, final Map<Long, GamaShape> nodesPt) {
		GamaList<IShape> geometries = new GamaList();
		for ( Node node : nodes ) {
			GamaShape pt = nodesPt.get(node.getId());
			boolean hasAttributes = !node.getTags().isEmpty();
			if ( pt != null ) {
				for ( Tag tg : node.getTags() ) {
					String key = tg.getKey();
					pt.setAttribute(key, tg.getValue());
					if ( key.equals("highway") ) {
						intersectionNodes.add(node.getId());
					}
				}
				if ( hasAttributes ) {
					geometries.add(pt);
				}
			}

		}
		for ( Way way : ways ) {
			Map<String, Object> values = new GamaMap<String, Object>();
			for ( Tag tg : way.getTags() ) {
				String key = tg.getKey();
				values.put(key, tg.getValue());
			}
			boolean isPolyline =
				values.containsKey("highway") ||
					way.getWayNodes().get(0).equals(way.getWayNodes().get(way.getWayNodes().size() - 1));
			if ( isPolyline ) {
				((List) geometries).addAll(createSplitRoad(way, values, intersectionNodes, nodesPt));

			} else {
				List<IShape> points = new GamaList<IShape>();
				for ( WayNode node : way.getWayNodes() ) {
					points.add(nodesPt.get(node.getNodeId()));
				}
				if ( points.size() < 3 ) {
					continue;
				}
				IShape geom = GamaGeometryType.buildPolygon(points);
				if ( geom != null && geom.getInnerGeometry() != null && !geom.getInnerGeometry().isEmpty() && /* geom.getInnerGeometry().isValid() && */
				geom.getInnerGeometry().getArea() > 0 ) {
					for ( String key : values.keySet() ) {
						geom.setAttribute(key, values.get(key));
					}
					geometries.add(geom);
				}
			}

		}

		return geometries;
	}

	public List<IShape> createSplitRoad(final Way way, final Map<String, Object> values,
		final Set<Long> intersectionNodes, final Map<Long, GamaShape> nodesPt) {
		List<List<IShape>> pointsList = new GamaList<List<IShape>>();
		List<IShape> points = new GamaList<IShape>();
		GamaList<IShape> geometries = new GamaList();
		WayNode endNode = way.getWayNodes().get(way.getWayNodes().size() - 1);
		for ( WayNode node : way.getWayNodes() ) {
			Long id = node.getNodeId();
			GamaShape pt = nodesPt.get(id);
			points.add(pt);
			if ( intersectionNodes.contains(id) || node == endNode ) {
				if ( points.size() > 1 ) {
					pointsList.add(points);
				}
				points = new GamaList<IShape>();
				points.add(pt);

			}
		}
		for ( List<IShape> pts : pointsList ) {
			IShape g = createRoad(pts, values);
			if ( g != null ) {
				geometries.add(g);
			}
		}
		return geometries;

	}

	private IShape createRoad(final List<IShape> points, final Map<String, Object> values) {
		if ( points.size() < 2 ) { return null; }
		IShape geom = GamaGeometryType.buildPolyline(points);
		if ( geom != null && !geom.getInnerGeometry().isEmpty() && geom.getInnerGeometry().isSimple() &&
			geom.getPerimeter() > 0 ) {
			for ( String key : values.keySet() ) {
				geom.setAttribute(key, values.get(key));
			}
			return geom;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO not sure that is is really interesting to save geographic as OSM file...
	}

	private void registerHighway(final Way way, final Set<Long> usedNodes, final Set<Long> intersectionNodes) {
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
			}
		}
	}

	private void readFile(final Sink sinkImplementation, final File osmFile) {
		boolean pbf = false;
		CompressionMethod compression = CompressionMethod.None;
		if ( getName().endsWith(".pbf") ) {
			pbf = true;
		} else if ( getName().endsWith(".gz") ) {
			compression = CompressionMethod.GZip;
		} else if ( getName().endsWith(".bz2") ) {
			compression = CompressionMethod.BZip2;
		}

		RunnableSource reader;

		reader = new XmlReader(osmFile, false, compression);

		if ( pbf ) {
			try {
				reader = new crosby.binary.osmosis.OsmosisReader(new FileInputStream(osmFile));
			} catch (FileNotFoundException e) {
				return;
			}
		} else {
			reader = new XmlReader(osmFile, false, compression);
		}

		reader.setSink(sinkImplementation);

		Thread readerThread = new Thread(reader);
		readerThread.start();

		while (readerThread.isAlive()) {
			try {
				readerThread.join();
			} catch (InterruptedException e) {

			}
		}
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		if ( gis == null ) {
			getFeatureIterator(scope, false);
		}
		return gis.getProjectedEnvelope();

	}

	/**
	 * Method getExistingCRS()
	 * @see msi.gama.util.file.GamaGisFile#getExistingCRS()
	 */
	@Override
	protected CoordinateReferenceSystem getOwnCRS() {
		// Is it always true ?
		return DefaultGeographicCRS.WGS84;
	}

}
