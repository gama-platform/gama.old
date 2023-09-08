/*******************************************************************************************************
 *
 * GamaDXFFile.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.ext.kabeja.dxf.DXFArc;
import msi.gama.ext.kabeja.dxf.DXFBlock;
import msi.gama.ext.kabeja.dxf.DXFCircle;
import msi.gama.ext.kabeja.dxf.DXFDocument;
import msi.gama.ext.kabeja.dxf.DXFEntity;
import msi.gama.ext.kabeja.dxf.DXFLayer;
import msi.gama.ext.kabeja.dxf.DXFLine;
import msi.gama.ext.kabeja.dxf.DXFPolyline;
import msi.gama.ext.kabeja.dxf.DXFSolid;
import msi.gama.ext.kabeja.dxf.DXFVertex;
import msi.gama.ext.kabeja.parser.DXFParser;
import msi.gama.ext.kabeja.parser.Parser;
import msi.gama.ext.kabeja.parser.ParserBuilder;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Creation;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 13 nov. 2011
 *
 * @todo Description
 *
 */
@file (
		name = "dxf",
		extensions = { "dxf" },
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY,
		buffer_index = IType.INT,
		concept = { IConcept.DXF, IConcept.FILE },
		doc = @doc ("DXF files are 2D geometrical files. The internal representation is a list of geometries"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaDXFFile extends GamaGeometryFile {

	/** The size. */
	GamaPoint size;

	/** The unit. */
	Double unit;

	/** The x t. */
	double x_t;

	/** The y t. */
	double y_t;

	/** The Constant QUARTER_CIRCLE_ANGLE. */
	protected static final double QUARTER_CIRCLE_ANGLE = Math.tan(0.39269908169872414D);

	/**
	 * Instantiates a new gama DXF file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read a dxf (.dxf) file",
			examples = { @example (
					value = "file f <- dxf_file(\"file.dxf\");",
					isExecutable = false) })
	public GamaDXFFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	/**
	 * Instantiates a new gama DXF file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @param unit
	 *            the unit
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read a dxf (.dxf) file and specify the unit (meter by default)",
			examples = { @example (
					value = "file f <- dxf_file(\"file.dxf\",#m);",
					isExecutable = false) })
	public GamaDXFFile(final IScope scope, final String pathName, final Double unit) throws GamaRuntimeException {
		super(scope, pathName);
		if (unit <= 0) { GamaRuntimeException.error("the unity given has to be higher than 0", scope); }
		this.unit = unit;
	}

	@Override
	protected IShape buildGeometry(final IScope scope) {
		return GamaGeometryType.geometriesToGeometry(scope, getBuffer());
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO are there attributes ?
		return GamaListFactory.EMPTY_LIST;
	}

	/**
	 * Creates the polyline.
	 *
	 * @param scope
	 *            the scope
	 * @param pts
	 *            the pts
	 * @return the i shape
	 */
	public IShape createPolyline(final IScope scope, final IList pts) {
		if (pts.isEmpty()) return null;
		final IShape shape = GamaGeometryType.buildPolyline(pts);
		if (shape != null) {
			if (size != null) return Spatial.Transformations.scaled_to(scope, shape, size);
			return shape;
		}
		return null;
	}

	/**
	 * Creates the polygon.
	 *
	 * @param scope
	 *            the scope
	 * @param pts
	 *            the pts
	 * @return the i shape
	 */
	public IShape createPolygon(final IScope scope, final IList pts) {
		if (pts.isEmpty()) return null;
		final IShape shape = GamaGeometryType.buildPolygon(pts);
		if (shape != null) {
			if (size != null) return Spatial.Transformations.scaled_to(scope, shape, size);
			return shape;
		}
		return null;
	}

	/**
	 * Creates the circle.
	 *
	 * @param scope
	 *            the scope
	 * @param location
	 *            the location
	 * @param radius
	 *            the radius
	 * @return the i shape
	 */
	public IShape createCircle(final IScope scope, final GamaPoint location, final double radius) {
		IShape shape = GamaGeometryType.buildCircle(radius, location).getExteriorRing(scope);
		if (shape != null) {
			if (size != null) return Spatial.Transformations.scaled_to(scope, shape, size);
			return shape;
		}
		return null;
	}

	/**
	 * Manage obj.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @return the i shape
	 */
	public IShape manageObj(final IScope scope, final DXFSolid obj) {
		if (obj == null) return null;
		final IList list = GamaListFactory.create(Types.POINT);
		list.add(new GamaPoint(obj.getPoint1().getX() * (unit == null ? 1 : unit) - x_t,
				obj.getPoint1().getY() * (unit == null ? 1 : unit) - y_t,
				obj.getPoint1().getZ() * (unit == null ? 1 : unit)));
		list.add(new GamaPoint(obj.getPoint2().getX() * (unit == null ? 1 : unit) - x_t,
				obj.getPoint2().getY() * (unit == null ? 1 : unit) - y_t,
				obj.getPoint2().getZ() * (unit == null ? 1 : unit)));
		list.add(new GamaPoint(obj.getPoint3().getX() * (unit == null ? 1 : unit) - x_t,
				obj.getPoint3().getY() * (unit == null ? 1 : unit) - y_t,
				obj.getPoint3().getZ() * (unit == null ? 1 : unit)));
		list.add(new GamaPoint(obj.getPoint4().getX() * (unit == null ? 1 : unit) - x_t,
				obj.getPoint4().getY() * (unit == null ? 1 : unit) - y_t,
				obj.getPoint4().getZ() * (unit == null ? 1 : unit)));

		return createPolygon(scope, list);
	}

	/**
	 * Manage obj.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @return the i shape
	 */
	public IShape manageObj(final IScope scope, final DXFCircle obj) {
		if (obj == null) return null;
		final GamaPoint pt = new GamaPoint(obj.getCenterPoint().getX() * (unit == null ? 1 : unit) - x_t,
				obj.getCenterPoint().getY() * (unit == null ? 1 : unit) - y_t,
				obj.getCenterPoint().getZ() * (unit == null ? 1 : unit));
		return createCircle(scope, pt, obj.getRadius() * (unit == null ? 1 : unit));
	}

	/**
	 * Manage obj.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @return the i shape
	 */
	public IShape manageObj(final IScope scope, final DXFLine obj) {
		if (obj == null) return null;
		final IList list = GamaListFactory.create(Types.POINT);
		list.add(new GamaPoint(obj.getStartPoint().getX() * (unit == null ? 1 : unit) - x_t,
				obj.getStartPoint().getY() * (unit == null ? 1 : unit) - y_t,
				obj.getStartPoint().getZ() * (unit == null ? 1 : unit)));
		list.add(new GamaPoint(obj.getEndPoint().getX() * (unit == null ? 1 : unit) - x_t,
				obj.getEndPoint().getY() * (unit == null ? 1 : unit) - y_t,
				obj.getEndPoint().getZ() * (unit == null ? 1 : unit)));
		return createPolyline(scope, list);
	}

	/**
	 * Manage obj.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @return the i shape
	 */
	public IShape manageObj(final IScope scope, final DXFArc obj) {
		if (obj == null) return null;
		final IList list = GamaListFactory.create(Types.POINT);
		list.add(new GamaPoint(obj.getStartPoint().getX() * (unit == null ? 1 : unit) - x_t,
				obj.getStartPoint().getY() * (unit == null ? 1 : unit) - y_t,
				obj.getStartPoint().getZ() * (unit == null ? 1 : unit)));
		list.add(new GamaPoint(obj.getEndPoint().getX() * (unit == null ? 1 : unit) - x_t,
				obj.getEndPoint().getY() * (unit == null ? 1 : unit) - y_t,
				obj.getEndPoint().getZ() * (unit == null ? 1 : unit)));
		return createPolyline(scope, list);
	}

	/**
	 * To gama point.
	 *
	 * @param v
	 *            the v
	 * @return the gama point
	 */
	public GamaPoint toGamaPoint(final DXFVertex v) {
		return new GamaPoint(v.getPoint().getX(), v.getPoint().getY(), v.getPoint().getZ());
	}

	/**
	 * Adds the to lists.
	 *
	 * @param scope
	 *            the scope
	 * @param pline
	 *            the pline
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param list
	 *            the list
	 */
	protected void addToLists(final IScope scope, final DXFPolyline pline, final DXFVertex start, final DXFVertex end,
			final IList list) {
		IList<GamaPoint> locs = GamaListFactory.create(Types.POINT);
		locs.add(new GamaPoint(start.getPoint().getX(), start.getPoint().getY(), start.getPoint().getZ()));
		// calculte the height
		GamaPoint startPt = toGamaPoint(start);
		GamaPoint endPt = toGamaPoint(end);
		if (start.getBulge() == 0) {
			list.add(startPt);
			list.add(endPt);

		} else {
			double l = startPt.distance(endPt);

			double s = start.getBulge() * l / 2;

			IShape c = Creation.EllipticalArc(scope, startPt, endPt, s, 20);
			list.addAll(c.getPoints());
		}

	}

	/**
	 * Gets the points.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @return the points
	 */
	public IList<GamaPoint> getPoints(final IScope scope, final DXFPolyline obj) {
		IList list = GamaListFactory.create(Types.POINT);

		Iterator i = obj.getVertexIterator();

		if (i.hasNext()) {
			DXFVertex last;
			DXFVertex first;
			DXFVertex v = null;

			last = first = (DXFVertex) i.next();
			list.add(new GamaPoint(last.getPoint().getX(), last.getPoint().getY(), last.getPoint().getZ()));

			while (i.hasNext()) {
				v = (DXFVertex) i.next();
				addToLists(scope, obj, last, v, list);
				last = v;
			}

			if (v != null && v.getBulge() != 0.0) { addToLists(scope, obj, v, first, list); }

		}

		boolean change = true;
		while (change) {
			change = false;
			for (int k = 0; k < list.size() - 1; k++) {
				GamaPoint pt1 = (GamaPoint) list.get(k);
				GamaPoint pt2 = (GamaPoint) list.get(k + 1);
				if (pt1.euclidianDistanceTo(pt2) < 0.000001) {
					list.remove(k + 1);
					change = true;
					break;
				}
			}
		}

		return list;
	}

	/**
	 * Manage obj.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @return the i shape
	 */
	public IShape manageObj(final IScope scope, final DXFPolyline obj) {
		if (obj == null) return null;
		IList<GamaPoint> list_ = getPoints(scope, obj);

		final GamaPoint pt = list_.get(list_.size() - 1);
		if (pt.getX() == 0 && pt.getY() == 0 && pt.getZ() == 0) { list_.remove(pt); }

		IList<GamaPoint> list = GamaListFactory.create(Types.POINT);

		for (GamaPoint p : list_) {
			list.add(new GamaPoint(p.getX() * (unit == null ? 1 : unit) - x_t,
					p.getY() * (unit == null ? 1 : unit) - y_t, p.getZ() * (unit == null ? 1 : unit)));
		}

		if (list.size() < 2) return null;
		if (obj.isClosed() && list.get(0) != list.get(list.size() - 1)) { list.add(list.firstValue(scope)); }
		return createPolyline(scope, list);
	}

	/**
	 * Define geom.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @return the i shape
	 */
	public IShape defineGeom(final IScope scope, final Object obj) {

		if (obj != null) {
			if (obj instanceof DXFArc) return manageObj(scope, (DXFArc) obj);
			if (obj instanceof DXFLine) return manageObj(scope, (DXFLine) obj);
			if (obj instanceof DXFPolyline) return manageObj(scope, (DXFPolyline) obj);
			if (obj instanceof DXFSolid) return manageObj(scope, (DXFSolid) obj);
			if (obj instanceof DXFCircle) return manageObj(scope, (DXFCircle) obj);

		}

		return null;
	}

	/**
	 * Fill buffer.
	 *
	 * @param scope
	 *            the scope
	 * @param doc
	 *            the doc
	 */
	protected void fillBuffer(final IScope scope, final DXFDocument doc) {
		final IList<IShape> geoms = GamaListFactory.create(Types.GEOMETRY);
		final double xmax = (doc.getBounds().getMaximumX() - doc.getBounds().getMinimumX()) * (unit == null ? 1 : unit);
		final double ymax = (doc.getBounds().getMaximumY() - doc.getBounds().getMinimumY()) * (unit == null ? 1 : unit);

		final IShape env = GamaGeometryType.buildPolygon(GamaListFactory.wrap(Types.POINT, new GamaPoint(0, 0),
				new GamaPoint(xmax, 0), new GamaPoint(xmax, ymax), new GamaPoint(0, ymax), new GamaPoint(0, 0)));
		final Iterator it = doc.getDXFLayerIterator();
		final List<IShape> entities = new ArrayList<>();
		while (it.hasNext()) {
			final DXFLayer layer = (DXFLayer) it.next();
			final Iterator ittype = layer.getDXFEntityTypeIterator();
			while (ittype.hasNext()) {
				final String entityType = (String) ittype.next();
				final List<DXFEntity> entity_list = layer.getDXFEntities(entityType);
				for (final DXFEntity obj : entity_list) {
					final IShape g = defineGeom(scope, obj);
					if (g != null && g.intersects(env)) {
						if (entities.contains(g)) { continue; }
						entities.add(g);

						g.setAttribute("layer", obj.getLayerName());
						g.setAttribute("id", obj.getID());
						g.setAttribute("scale_factor", obj.getLinetypeScaleFactor());
						g.setAttribute("thickness", obj.getThickness());
						g.setAttribute("is_visible", obj.isVisibile());
						g.setAttribute("is_omit", obj.isOmitLineType());
						g.setAttribute("color_index", obj.getColor());

						if (obj.getColorRGB() != null) {
							g.setAttribute("color", GamaColor.get(obj.getColorRGB()[0], obj.getColorRGB()[1],
									obj.getColorRGB()[2], 255));
						}
						if (obj.getLineType() != null) { g.setAttribute("line_type", obj.getLineType()); }

						geoms.add(g);
					}
				}

			}
		}

		final Iterator itbl = doc.getDXFBlockIterator();
		while (itbl.hasNext()) {
			final DXFBlock block = (DXFBlock) itbl.next();
			final Iterator itent = block.getDXFEntitiesIterator();
			while (itent.hasNext()) {
				final DXFEntity obj = (DXFEntity) itent.next();
				final IShape g = defineGeom(scope, obj);
				if (g != null && g.intersects(env)) {
					if (entities.contains(g)) { continue; }
					entities.add(g);

					g.setAttribute("layer", obj.getLayerName());
					g.setAttribute("id", obj.getID());
					g.setAttribute("scale_factor", obj.getLinetypeScaleFactor());
					g.setAttribute("thickness", obj.getThickness());
					g.setAttribute("is_visible", obj.isVisibile());
					g.setAttribute("is_omit", obj.isOmitLineType());

					g.setAttribute("color_index", obj.getColor());

					if (obj.getColorRGB() != null) {
						g.setAttribute("color",
								GamaColor.get(obj.getColorRGB()[0], obj.getColorRGB()[1], obj.getColorRGB()[2], 255));
					}
					if (obj.getLineType() != null) { g.setAttribute("line_type", obj.getLineType()); }

					geoms.add(g);
				}
			}
		}

		setBuffer(geoms);
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) return;
		final Parser parser = ParserBuilder.createDefaultParser();
		try (InputStream in = new FileInputStream(getFile(scope))) {
			parser.parse(in, DXFParser.DEFAULT_ENCODING);

			// get the document and the layer
			final DXFDocument doc = parser.getDocument();
			x_t = doc.getBounds().getMinimumX() * (unit == null ? 1 : unit);
			y_t = doc.getBounds().getMinimumY() * (unit == null ? 1 : unit);

			fillBuffer(scope, doc);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		final Parser parser = ParserBuilder.createDefaultParser();
		try (InputStream in = new FileInputStream(getFile(scope))) {

			// parse
			parser.parse(in, DXFParser.DEFAULT_ENCODING);

			// get the documnet and the layer
			final DXFDocument doc = parser.getDocument();
			return Envelope3D.of(new Envelope(0,
					(doc.getBounds().getMaximumX() - doc.getBounds().getMinimumX()) * (unit == null ? 1 : unit), 0,
					(doc.getBounds().getMaximumY() - doc.getBounds().getMinimumY()) * (unit == null ? 1 : unit)));
		} catch (final Exception e) {

			e.printStackTrace();
			return null;
		}
	}
}
