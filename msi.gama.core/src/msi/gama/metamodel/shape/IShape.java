/*********************************************************************************************
 *
 * 'IShape.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform. (c)
 * 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.metamodel.shape;

import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IAttributed;
import msi.gama.common.interfaces.ILocated;
import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gaml.types.IType;

/**
 * Interface for objects that can be provided with a geometry (or which can be translated to a GamaGeometry)
 *
 * @author Alexis Drogoul
 * @since 16 avr. 2011
 * @modified November 2011 to include isPoint(), getInnerGeometry() and getEnvelope()
 *
 */
@vars ({ @var (
		name = "area",
		type = IType.FLOAT,
		doc = { @doc ("Returns the total area of this geometry") }),
		@var (
				name = "volume",
				type = IType.FLOAT,
				doc = { @doc ("Returns the total volume of this geometry") }),
		@var (
				name = "centroid",
				type = IType.POINT,
				doc = { @doc ("Returns the centroid of this geometry") }),
		@var (
				name = "width",
				type = IType.FLOAT,
				doc = { @doc ("Returns the width (length on the x-axis) of the rectangular envelope of this  geometry") }),
		@var (
				name = "attributes",
				type = IType.MAP,
				doc = { @doc ("Returns the attributes kept by this geometry (the ones shared with the agent)") }),
		@var (
				name = "depth",
				type = IType.FLOAT,
				doc = { @doc ("Returns the depth (length on the z-axis) of the rectangular envelope of this geometry") }),
		@var (
				name = "height",
				type = IType.FLOAT,
				doc = { @doc ("Returns the height (length on the y-axis) of the rectangular envelope of this geometry") }),
		@var (
				name = "points",
				type = IType.LIST,
				of = IType.POINT,
				doc = { @doc ("Returns the list of points that delimit this geometry. A point will return a list with itself") }),
		@var (
				name = "envelope",
				type = IType.GEOMETRY,
				doc = { @doc ("Returns the envelope of this geometry (the smallest rectangle that contains the geometry)") }),
		@var (
				name = "geometries",
				type = IType.LIST,
				of = IType.GEOMETRY,
				doc = { @doc ("Returns the list of geometries that compose this geometry, or a list containing the geometry itself if it is simple") }),
		@var (
				name = "multiple",
				type = IType.BOOL,
				doc = { @doc ("Returns whether this geometry is composed of multiple geometries or not") }),
		@var (
				name = "perimeter",
				type = IType.FLOAT,
				doc = { @doc ("Returns the length of the contour of this geometry") }),
		@var (
				name = "holes",
				type = IType.LIST,
				of = IType.GEOMETRY,
				doc = { @doc ("Returns the list of holes inside this geometry as a list of geometries, and an emptly list if this geometry is solid") }),
		@var (
				name = "contour",
				type = IType.GEOMETRY,
				doc = { @doc ("Returns the polyline representing the contour of this geometry") }) })
public interface IShape extends ILocated, IValue, IAttributed {

	static enum Type {
		BOX,
		CIRCLE,
		CONE,
		CUBE,
		CYLINDER,
		ENVIRONMENT,
		GRIDLINE,
		LINEARRING("LinearRing"),
		LINESTRING("LineString"),
		MULTILINESTRING("MultiLineString"),
		MULTIPOINT("MultiPoint"),
		MULTIPOLYGON("MultiPolygon"),
		NULL,
		PLAN,
		POINT("Point"),
		POLYGON("Polygon"),
		POLYHEDRON,
		POLYPLAN,
		PYRAMID,
		SPHERE,
		TEAPOT,
		LINECYLINDER,
		POLYLINECYLINDER;

		Type() {}

		Type(final String name) {
			JTS_TYPES.put(name, this);
		}
	}

	public static final WKTWriter SHAPE_WRITER = new WKTWriter();

	public static final String DEPTH_ATTRIBUTE = "_shape_internal_depth";
	public static final Map<String, Type> JTS_TYPES = new THashMap<>();
	// public static final String TEXTURE_ATTRIBUTE = "_shape_internal_texture";
	public static final String TYPE_ATTRIBUTE = "_shape_internal_type";
	// public static final String RATIO_ATTRIBUTE = "_shape_internal_ratio";
	// public static final String COLOR_LIST_ATTRIBUTE =
	// "_shape_internal_color_list";
	// public static final String ROTATE_ATTRIBUTE = "_shape_internal_rotate";

	@Override
	public IShape copy(IScope scope);

	public abstract boolean covers(IShape g);

	public abstract boolean crosses(IShape g);

	public abstract void dispose();

	public abstract double euclidianDistanceTo(ILocation g);

	public abstract double euclidianDistanceTo(IShape g);

	public abstract IAgent getAgent();

	public abstract Envelope3D getEnvelope();

	/**
	 * Returns the geometrical type of this shape. May be computed dynamically (from the JTS inner geometry) or stored
	 * somewhere (in the attributes of the shape, using TYPE_ATTRIBUTE)
	 * 
	 * @param g
	 * @return
	 */
	public IShape.Type getGeometricalType();

	public abstract IShape getGeometry();

	public abstract Geometry getInnerGeometry();

	public abstract boolean intersects(IShape g);

	public abstract boolean isLine();

	public abstract boolean isPoint();

	public abstract void setAgent(IAgent agent);

	public abstract void setGeometry(IShape g);

	public abstract void setInnerGeometry(Geometry intersection);

	public void setDepth(double depth);

	@Override
	@getter ("attributes")
	public GamaMap<String, Object> getOrCreateAttributes();

	@getter ("multiple")
	public boolean isMultiple();

	@getter ("area")
	public Double getArea();

	@getter ("volume")
	public Double getVolume();

	@getter ("perimeter")
	public double getPerimeter();

	@getter ("holes")
	public IList<GamaShape> getHoles();

	@getter ("centroid")
	public GamaPoint getCentroid();

	@getter ("contour")
	public GamaShape getExteriorRing(IScope scope);

	@getter ("width")
	public Double getWidth();

	@getter ("height")
	public Double getHeight();

	@getter ("depth")
	public Double getDepth();

	@getter ("envelope")
	public GamaShape getGeometricEnvelope();

	@getter ("points")
	public IList<? extends ILocation> getPoints();

	@getter ("geometries")
	public IList<? extends IShape> getGeometries();

	/**
	 * Copy only the attributes that support defining the shape
	 * 
	 * @param other
	 */
	public default void copyShapeAttributesFrom(final IShape other) {
		if (other.hasAttribute(DEPTH_ATTRIBUTE))
			this.setAttribute(DEPTH_ATTRIBUTE, other.getAttribute(DEPTH_ATTRIBUTE));
		if (other.hasAttribute(TYPE_ATTRIBUTE))
			this.setAttribute(TYPE_ATTRIBUTE, other.getAttribute(TYPE_ATTRIBUTE));
	}

}
