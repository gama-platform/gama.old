/*********************************************************************************************
 *
 *
 * 'IShape.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.metamodel.shape;

import java.util.Map;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;
import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gaml.types.*;

/**
 * Interface for objects that can be provided with a geometry (or which can be translated to
 * a GamaGeometry)
 *
 * @author Alexis Drogoul
 * @since 16 avr. 2011
 * @modified November 2011 to include isPoint(), getInnerGeometry() and getEnvelope()
 *
 */
@vars({ @var(name = "perimeter",
	type = IType.FLOAT,
	doc = { @doc("Returns the length of the contour of this geometry") }) })
public interface IShape extends ILocated, IValue, IAttributed {

	static enum Type {
		BOX, CIRCLE, CONE, CUBE, CYLINDER, ENVIRONMENT, GRIDLINE, LINEARRING("LinearRing"),
		LINESTRING("LineString"), MULTILINESTRING("MultiLineString"), MULTIPOINT("MultiPoint"),
		MULTIPOLYGON("MultiPolygon"), NULL, PLAN, POINT("Point"), POLYGON("Polygon"), POLYHEDRON, POLYPLAN, PYRAMID,
		SPHERE, TEAPOT, LINECYLINDER, POLYLINECYLINDER;

		Type() {}

		Type(final String name) {
			JTS_TYPES.put(name, this);
		}
	}

	public static final WKTWriter SHAPE_WRITER = new WKTWriter();

	public static final String DEPTH_ATTRIBUTE = "_shape_internal_depth";
	public static final Map<String, Type> JTS_TYPES = new THashMap();
	public static final String TEXTURE_ATTRIBUTE = "_shape_internal_texture";
	public static final String ASSET3D_ATTRIBUTE = "_shape_internal_asset3D";
	public static final String TYPE_ATTRIBUTE = "_shape_internal_type";
	public static final String RATIO_ATTRIBUTE = "_shape_internal_ratio";
	public static final String COLOR_LIST_ATTRIBUTE = "_shape_internal_color_list";
	public static final String ROTATE_ATTRIBUTE = "_shape_internal_rotate";

	@Override
	public IShape copy(IScope scope);

	public abstract boolean covers(IShape g);

	public abstract boolean crosses(IShape g);

	public abstract void dispose();

	public abstract double euclidianDistanceTo(ILocation g);

	public abstract double euclidianDistanceTo(IShape g);

	public abstract IAgent getAgent();

	public abstract Envelope3D getEnvelope();

	public IList<? extends ILocation> getPoints();

	/**
	 * Returns the geometrical type of this shape. May be computed dynamically (from the JTS inner geometry) or stored
	 * somewhere (in the attributes of the shape, using TYPE_ATTRIBUTE)
	 * @param g
	 * @return
	 */
	public IShape.Type getGeometricalType();

	public abstract IShape getGeometry();

	public abstract Geometry getInnerGeometry();

	@getter("perimeter")
	public abstract double getPerimeter();

	public abstract boolean intersects(IShape g);

	public abstract boolean isLine();

	public abstract boolean isPoint();

	public abstract void setAgent(IAgent agent);

	public abstract void setGeometry(IShape g);

	public abstract void setInnerGeometry(Geometry intersection);

	public void setDepth(double depth);

	public void setRotate3D(GamaPair rot3D);

}
