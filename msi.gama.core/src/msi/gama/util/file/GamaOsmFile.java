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

import static org.apache.commons.lang.StringUtils.join;
import static org.apache.commons.lang.StringUtils.splitByWholeSeparatorPreserveAllTokens;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataUtilities;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
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

import crosby.binary.osmosis.OsmosisReader;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.hash.TLongHashSet;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.operators.Strings;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.statements.Facets;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@file(name = "osm", extensions = { "osm", "pbf", "bz2",
		"gz" }, buffer_type = IType.LIST, buffer_content = IType.GEOMETRY, buffer_index = IType.INT, concept = {
				IConcept.OSM, IConcept.FILE })
public class GamaOsmFile extends GamaGisFile {

	public static class OSMInfo extends GamaFileMetaData {

		final int itemNumber;
		final CoordinateReferenceSystem crs;
		final double width;
		final double height;
		final Map<String, String> attributes = new LinkedHashMap();

		public OSMInfo(final URL url, final long modificationStamp) {
			super(modificationStamp);
			CoordinateReferenceSystem crs = null;
			ReferencedEnvelope env2 = new ReferencedEnvelope();

			int number = 0;
			try {
				final File f = new File(url.toURI());
				final GamaOsmFile osmfile = new GamaOsmFile(null, f.getAbsolutePath());
				attributes.putAll(osmfile.getAttributes());

				final SimpleFeatureType TYPE = DataUtilities.createType("geometries", "geom:LineString");
				final ArrayList<SimpleFeature> list = new ArrayList<SimpleFeature>();
				for (final IShape shape : osmfile.iterable(null)) {
					list.add(SimpleFeatureBuilder.build(TYPE, new Object[] { shape.getInnerGeometry() }, null));
				}
				final SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, list);
				final SimpleFeatureSource featureSource = DataUtilities.source(collection);

				env2 = featureSource.getBounds();
				number = osmfile.nbObjects;
				crs = osmfile.getOwnCRS(null);
			} catch (final Exception e) {
				System.out.println("Error in reading metadata of " + url);

			} finally {

				// approximation of the width and height in meters.
				width = env2 != null ? env2.getWidth() * (FastMath.PI / 180) * 6378137 : 0;
				height = env2 != null ? env2.getHeight() * (FastMath.PI / 180) * 6378137 : 0;
				itemNumber = number;
				this.crs = crs;
			}

		}

		public CoordinateReferenceSystem getCRS() {
			return crs;
		}

		public OSMInfo(final String propertiesString) throws NoSuchAuthorityCodeException, FactoryException {
			super(propertiesString);
			final String[] segments = split(propertiesString);
			itemNumber = Integer.valueOf(segments[1]);
			final String crsString = segments[2];
			if ("null".equals(crsString)) {
				crs = null;
			} else {
				crs = CRS.parseWKT(crsString);
			}
			width = Double.valueOf(segments[3]);
			height = Double.valueOf(segments[4]);
			if (segments.length > 5) {
				final String[] names = splitByWholeSeparatorPreserveAllTokens(segments[5], SUB_DELIMITER);
				final String[] types = splitByWholeSeparatorPreserveAllTokens(segments[6], SUB_DELIMITER);
				for (int i = 0; i < names.length; i++) {
					attributes.put(names[i], types[i]);
				}
			}
		}

		/**
		 * Method getSuffix()
		 * 
		 * @see msi.gama.util.file.GamaFileMetaInformation#getSuffix()
		 */
		@Override
		public String getSuffix() {
			return "" + itemNumber + " objects | " + FastMath.round(width) + "m x " + FastMath.round(height) + "m";
		}

		@Override
		public String getDocumentation() {
			final StringBuilder sb = new StringBuilder();
			sb.append("OSM file").append(Strings.LN);
			sb.append(itemNumber).append(" objects").append(Strings.LN);
			sb.append("Dimensions: ").append(FastMath.round(width) + "m x " + FastMath.round(height) + "m")
					.append(Strings.LN);
			sb.append("Coordinate Reference System: ").append(crs == null ? "No CRS" : crs.getName().getCode())
					.append(Strings.LN);
			if (!attributes.isEmpty()) {
				sb.append("Attributes: ").append(Strings.LN);
				for (final Map.Entry<String, String> entry : attributes.entrySet()) {
					sb.append("<li>").append(entry.getKey()).append(" (" + entry.getValue() + ")").append("</li>");
				}
			}
			return sb.toString();
		}

		public Map<String, String> getAttributes() {
			return attributes;
		}

		@Override
		public String toPropertyString() {
			final String attributeNames = join(attributes.keySet(), SUB_DELIMITER);
			final String types = join(attributes.values(), SUB_DELIMITER);
			final Object[] toSave = new Object[] { super.toPropertyString(), itemNumber,
					crs == null ? "null" : crs.toWKT(), width, height, attributeNames, types };
			return join(toSave, DELIMITER);
		}
	}

	GamaMap<String, GamaList> filteringOptions;
	Map<String, String> attributes = new Hashtable<String, String>();

	final Map<String, List<IShape>> layers = GamaMapFactory.create(Types.STRING, Types.LIST);
	final static List<String> featureTypes = new ArrayList<String>() {

		{
			add("aerialway");
			add("aeroway");
			add("amenity");
			add("barrier");
			add("boundary");
			add("building");
			add("craft");
			add("emergency");
			add("geological");
			add("highway");
			add("historic");
			add("landuse");
			add("leisure");
			add("man_made");
			add("military");
			add("natural");
			add("office");
			add("place");
			add("power");
			add("public_transport");
			add("railway");
			add("route");
			add("shop");
			add("sport");
			add("tourism");
			add("waterway");
		}
	};
	int nbObjects;

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

		final Sink sinkImplementation = new Sink() {

			@Override
			public void process(final EntityContainer entityContainer) {
				final Entity entity = entityContainer.getEntity();
				final boolean toFilter = filteringOptions != null && !filteringOptions.isEmpty();
				if (entity instanceof Bound) {
					final Bound bound = (Bound) entity;
					final Envelope env = new Envelope(bound.getLeft(), bound.getRight(), bound.getBottom(),
							bound.getTop());
					computeProjection(scope, env);
				} else if (returnIt) {
					if (entity instanceof Node) {
						final Node node = (Node) entity;
						nodes.add(node);
						final Geometry g = gis == null
								? new GamaPoint(node.getLongitude(), node.getLatitude()).getInnerGeometry()
								: gis.transform(
										new GamaPoint(node.getLongitude(), node.getLatitude()).getInnerGeometry());
						nodesPt.put(node.getId(), new GamaShape(g));
					} else if (entity instanceof Way) {
						if (toFilter) {
							boolean keepObject = false;
							for (final String keyN : filteringOptions.getKeys()) {
								final GamaList valsPoss = filteringOptions.get(keyN);
								for (final Tag tagN : ((Way) entity).getTags()) {
									if (keyN.equals(tagN.getKey())) {
										if (valsPoss == null || valsPoss.isEmpty()
												|| valsPoss.contains(tagN.getValue())) {
											keepObject = true;
											break;
										}
									}

								}
							}
							if (!keepObject) {
								return;
							}
						}
						registerHighway((Way) entity, usedNodes, intersectionNodes);
						ways.add((Way) entity);
					}
				}

			}

			@Override
			public void release() {
			}

			@Override
			public void complete() {
			}

			@Override
			public void initialize(final Map<String, Object> arg0) {
			}
		};
		readFile(scope, sinkImplementation, getFile(scope));
		if (returnIt) {
			setBuffer(buildGeometries(nodes, ways, intersectionNodes, nodesPt));
		}
	}

	private void addAttribute(final Map<String, String> atts, final String nameAt, final Object val) {
		final String type = atts.get(nameAt);
		if (type != null && type.equals("string")) {
			return;
		}
		String newType = "int";
		try {
			Integer.parseInt(val.toString());
		} catch (final Exception e) {
			try {
				Double.parseDouble(val.toString());
			} catch (final Exception e2) {
				newType = "string";
			}
		}

		if (type == null || newType.equals("string")) {
			atts.put(nameAt, newType);
		}
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) {
			return;
		}
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		getFeatureIterator(scope, true);
	}

	public IList<IShape> buildGeometries(final List<Node> nodes, final List<Way> ways,
			final TLongHashSet intersectionNodes, final TLongObjectHashMap<GamaShape> nodesPt) {
		final IList<IShape> geometries = GamaListFactory.create(Types.GEOMETRY);
		for (final Node node : nodes) {
			final GamaShape pt = nodesPt.get(node.getId());
			final boolean hasAttributes = !node.getTags().isEmpty();
			final Map<String, String> atts = new Hashtable<String, String>();
			if (pt != null) {
				for (final Tag tg : node.getTags()) {
					final String key = tg.getKey().split(":")[0];
					final Object val = tg.getValue();
					if (val != null) {
						addAttribute(atts, key, val);
					}
					pt.setAttribute(key, val);
					if (key.equals("highway")) {
						intersectionNodes.add(node.getId());
					}
				}
				if (hasAttributes) {
					geometries.add(pt);
					for (final Object att : pt.getAttributes().keySet()) {
						if (featureTypes.contains(att)) {
							final String idType = att + " (point)";
							List objs = layers.get(idType);
							if (objs == null) {
								objs = GamaListFactory.create(Types.GEOMETRY);
								layers.put(idType, objs);
							}
							objs.add(pt);
							for (final String v : atts.keySet()) {
								final String id = idType + ";" + v;
								attributes.put(id, atts.get(v));
							}
							break;
						}
					}
				}
			}

		}
		for (final Way way : ways) {
			final Map<String, Object> values = new TOrderedHashMap<String, Object>();
			final Map<String, String> atts = new Hashtable<String, String>();

			for (final Tag tg : way.getTags()) {

				final String key = tg.getKey().split(":")[0];
				final Object val = tg.getValue();
				if (val != null) {
					addAttribute(atts, key, val);
				}
				values.put(key, tg.getValue());
			}
			// boolean isPolyline = values.containsKey("highway") ||
			// !way.getWayNodes().get(0).equals(way.getWayNodes().get(way.getWayNodes().size()
			// - 1));
			final boolean isPolyline = values.containsKey("highway") || !(way.getWayNodes().get(0).getNodeId() == way
					.getWayNodes().get(way.getWayNodes().size() - 1).getNodeId());
			if (isPolyline) {
				final List<IShape> geoms = createSplitRoad(way, values, intersectionNodes, nodesPt);
				((List) geometries).addAll(geoms);
				if (!geoms.isEmpty()) {
					for (final Object att : values.keySet()) {
						final String idType = att + " (line)";
						if (featureTypes.contains(att)) {
							List objs = layers.get(idType);
							if (objs == null) {
								objs = GamaListFactory.create(Types.GEOMETRY);
								layers.put(idType, objs);
							}
							objs.addAll(geoms);
							for (final String v : atts.keySet()) {
								final String id = idType + ";" + v;
								attributes.put(id, atts.get(v));
							}
							break;
						}
					}
				}

			} else {
				final List<IShape> points = GamaListFactory.create(Types.GEOMETRY);
				for (final WayNode node : way.getWayNodes()) {
					final GamaShape pp = nodesPt.get(node.getNodeId());
					if (pp == null) {
						continue;
					}
					points.add(pp);
				}
				if (points.size() < 3) {
					continue;
				}
				final IShape geom = GamaGeometryType.buildPolygon(points);
				if (geom != null && geom.getInnerGeometry() != null && !geom.getInnerGeometry().isEmpty()
						&& geom.getInnerGeometry().getArea() > 0) {
					for (final String key : values.keySet()) {
						final Object val = values.get(key);
						geom.setAttribute(key, val);
					}
					geometries.add(geom);
					if (geom.getAttributes() != null) {
						for (final Object att : geom.getAttributes().keySet()) {
							final String idType = att + " (polygon)";
							if (featureTypes.contains(att)) {
								List objs = layers.get(idType);
								if (objs == null) {
									objs = GamaListFactory.create(Types.GEOMETRY);
									layers.put(idType, objs);
								}
								objs.add(geom);
								for (final String v : atts.keySet()) {
									final String id = idType + ";" + v;
									attributes.put(id, atts.get(v));
								}
								break;
							}
						}
					}

				}
			}

		}
		nbObjects = geometries == null ? 0 : geometries.size();
		return geometries;
	}

	public List<IShape> createSplitRoad(final Way way, final Map<String, Object> values,
			final TLongHashSet intersectionNodes, final TLongObjectHashMap<GamaShape> nodesPt) {
		final List<List<IShape>> pointsList = GamaListFactory.create(Types.LIST.of(Types.GEOMETRY));
		List<IShape> points = GamaListFactory.create(Types.GEOMETRY);
		final IList<IShape> geometries = GamaListFactory.create(Types.GEOMETRY);
		final WayNode endNode = way.getWayNodes().get(way.getWayNodes().size() - 1);
		for (final WayNode node : way.getWayNodes()) {
			final Long id = node.getNodeId();
			final GamaShape pt = nodesPt.get(id);
			if (pt == null) {
				continue;
			}
			points.add(pt);
			if (intersectionNodes.contains(id) || node == endNode) {
				if (points.size() > 1) {
					pointsList.add(points);
				}
				points = GamaListFactory.create(Types.GEOMETRY);
				points.add(pt);

			}
		}
		for (final List<IShape> pts : pointsList) {
			final IShape g = createRoad(pts, values);
			if (g != null) {
				geometries.add(g);
			}
		}
		return geometries;

	}

	private IShape createRoad(final List<IShape> points, final Map<String, Object> values) {
		if (points.size() < 2) {
			return null;
		}
		final IShape geom = GamaGeometryType.buildPolyline(points);
		if (geom != null && geom.getInnerGeometry() != null && !geom.getInnerGeometry().isEmpty()
				&& geom.getInnerGeometry().isSimple() && geom.getPerimeter() > 0) {
			for (final String key : values.keySet()) {
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
	protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {
		// TODO not sure that is is really interesting to save geographic as OSM
		// file...
	}

	private void registerHighway(final Way way, final TLongHashSet usedNodes, final TLongHashSet intersectionNodes) {
		for (final Tag tg : way.getTags()) {
			final String key = tg.getKey();
			if (key.equals("highway")) {
				final List<WayNode> nodes = way.getWayNodes();
				for (final WayNode node : nodes) {
					final long id = node.getNodeId();
					if (usedNodes.contains(id)) {
						intersectionNodes.add(id);
					} else {
						usedNodes.add(id);
					}
				}
				if (nodes.size() > 2 && nodes.get(0) == nodes.get(nodes.size() - 1)) {
					intersectionNodes.add(nodes.get(nodes.size() / 2).getNodeId());
				}
			}
		}
	}

	private void readFile(final IScope scope, final Sink sinkImplementation, final File osmFile) {
		boolean pbf = false;
		CompressionMethod compression = CompressionMethod.None;
		if (getName(scope).endsWith(".pbf")) {
			pbf = true;
		} else if (getName(scope).endsWith(".gz")) {
			compression = CompressionMethod.GZip;
		} else if (getName(scope).endsWith(".bz2")) {
			compression = CompressionMethod.BZip2;
		}

		RunnableSource reader;

		// reader = new XmlReader(osmFile, false, compression);

		if (pbf) {
			try (FileInputStream stream = new FileInputStream(osmFile)) {
				reader = new OsmosisReader(stream);
			} catch (final IOException e) {
				System.out.println("Ignored exception in GamaOSMFile readFile: " + e.getMessage());
				return;
			}
		} else {
			reader = new XmlReader(osmFile, false, compression);
		}

		reader.setSink(sinkImplementation);

		final Thread readerThread = new Thread(reader);
		readerThread.start();

		while (readerThread.isAlive()) {
			try {
				readerThread.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		if (gis == null) {
			getFeatureIterator(scope, false);
		}
		return gis.getProjectedEnvelope();

	}

	/**
	 * Method getExistingCRS()
	 * 
	 * @see msi.gama.util.file.GamaGisFile#getExistingCRS()
	 */
	@Override
	protected CoordinateReferenceSystem getOwnCRS(final IScope scope) {
		// Is it always true ?
		return DefaultGeographicCRS.WGS84;
	}

	public Map<String, String> getAttributes() {
		if (attributes == null) {
			attributes = new HashMap<String, String>();
			getFeatureIterator(null, true);
		}
		return attributes;
	}

	public Map<String, List<IShape>> getLayers() {
		return layers;
	}

	public List<String> getFeatureTypes() {
		return featureTypes;
	}

}
