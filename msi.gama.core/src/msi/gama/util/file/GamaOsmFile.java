/*********************************************************************************************
 *
 *
 * 'GamaOsmFile.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.util.file;

import java.io.*;
import java.util.*;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.*;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;
import com.vividsolutions.jts.geom.*;
import crosby.binary.osmosis.OsmosisReader;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.hash.TLongHashSet;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.types.*;

@file(name = "osm",
	extensions = { "osm", "pbf", "bz2", "gz" },
	buffer_type = IType.LIST,
	buffer_content = IType.GEOMETRY,
	buffer_index = IType.INT)
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

	public GamaOsmFile(final IScope scope, final String pathName, final GamaMap<String, GamaList> filteringOptions) {
		super(scope, pathName, (Integer) null);
		this.filteringOptions = filteringOptions;
	}

	public GamaOsmFile(final IScope scope, final String pathName, final GamaMap<String, GamaList> filteringOption,
		final Integer code) {
		super(scope, pathName, code);
		this.filteringOptions = filteringOption;
	}

	public void getFeatureIterator(final IScope scope, final boolean returnIt) {
		final TLongObjectHashMap<GamaShape> nodesPt = new TLongObjectHashMap<GamaShape>();
		final List<Node> nodes = new ArrayList<Node>();
		final List<Way> ways = new ArrayList<Way>();
		final TLongHashSet intersectionNodes = new TLongHashSet();
		final TLongHashSet usedNodes = new TLongHashSet();

		Sink sinkImplementation = new Sink() {

			@Override
			public void process(final EntityContainer entityContainer) {
				Entity entity = entityContainer.getEntity();
				boolean toFilter = filteringOptions != null && !filteringOptions.isEmpty();
				if ( entity instanceof Bound ) {
					Bound bound = (Bound) entity;
					Envelope env = new Envelope(bound.getLeft(), bound.getRight(), bound.getBottom(), bound.getTop());
					computeProjection(scope, env);
				} else if ( returnIt ) {
					if ( entity instanceof Node ) {
						Node node = (Node) entity;
						nodes.add(node);
						Geometry g = gis == null ? new GamaPoint(node.getLongitude(), node.getLatitude()).getInnerGeometry() :
							gis.transform(new GamaPoint(node.getLongitude(), node.getLatitude()).getInnerGeometry());
						nodesPt.put(node.getId(), new GamaShape(g));
					} else if ( entity instanceof Way ) {
						if ( toFilter ) {
							boolean keepObject = false;
							for ( String keyN : filteringOptions.getKeys() ) {
								GamaList valsPoss = filteringOptions.get(keyN);
								for ( Tag tagN : ((Way) entity).getTags() ) {
									if ( keyN.equals(tagN.getKey()) ) {
										if ( valsPoss == null || valsPoss.isEmpty() ||
											valsPoss.contains(tagN.getValue()) ) {
											keepObject = true;
											break;
										}
									}

								}
							}
							if ( !keepObject ) { return; }
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
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		getFeatureIterator(scope, true);
	}

	public IList<IShape> buildGeometries(final List<Node> nodes, final List<Way> ways,
		final TLongHashSet intersectionNodes, final TLongObjectHashMap<GamaShape> nodesPt) {
		IList<IShape> geometries = GamaListFactory.create(Types.GEOMETRY);
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
			Map<String, Object> values = new TOrderedHashMap<String, Object>();
			for ( Tag tg : way.getTags() ) {
				String key = tg.getKey();
				values.put(key, tg.getValue());
			}
			boolean isPolyline = values.containsKey("highway") ||
				way.getWayNodes().get(0).equals(way.getWayNodes().get(way.getWayNodes().size() - 1));
			if ( isPolyline ) {
				((List) geometries).addAll(createSplitRoad(way, values, intersectionNodes, nodesPt));

			} else {
				List<IShape> points = GamaListFactory.create(Types.GEOMETRY);
				for ( WayNode node : way.getWayNodes() ) {
					GamaShape pp = nodesPt.get(node.getNodeId());
					if ( pp == null ) {
						continue;
					}
					points.add(pp);
				}
				if ( points.size() < 3 ) {
					continue;
				}
				IShape geom = GamaGeometryType.buildPolygon(points);
				if ( geom != null && geom.getInnerGeometry() != null && !geom.getInnerGeometry().isEmpty() &&
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
		final TLongHashSet intersectionNodes, final TLongObjectHashMap<GamaShape> nodesPt) {
		List<List<IShape>> pointsList = GamaListFactory.create(Types.LIST.of(Types.GEOMETRY));
		List<IShape> points = GamaListFactory.create(Types.GEOMETRY);
		IList<IShape> geometries = GamaListFactory.create(Types.GEOMETRY);
		WayNode endNode = way.getWayNodes().get(way.getWayNodes().size() - 1);
		for ( WayNode node : way.getWayNodes() ) {
			Long id = node.getNodeId();
			GamaShape pt = nodesPt.get(id);
			if ( pt == null ) {
				continue;
			}
			points.add(pt);
			if ( intersectionNodes.contains(id) || node == endNode ) {
				if ( points.size() > 1 ) {
					pointsList.add(points);
				}
				points = GamaListFactory.create(Types.GEOMETRY);
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
		if ( geom != null && geom.getInnerGeometry() != null && !geom.getInnerGeometry().isEmpty() &&
			geom.getInnerGeometry().isSimple() && geom.getPerimeter() > 0 ) {
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

	private void registerHighway(final Way way, final TLongHashSet usedNodes, final TLongHashSet intersectionNodes) {
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

		// reader = new XmlReader(osmFile, false, compression);

		if ( pbf ) {
			try {
				reader = new OsmosisReader(new FileInputStream(osmFile));
			} catch (FileNotFoundException e) {
				System.out.println("Ignored exception in GamaOSMFile readFile: " + e.getMessage());
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
				e.printStackTrace();
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
