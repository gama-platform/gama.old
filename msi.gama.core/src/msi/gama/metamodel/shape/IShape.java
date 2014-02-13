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
package msi.gama.metamodel.shape;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gaml.types.IType;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Interface for objects that can be provided with a geometry (or which can be translated to
 * a GamaGeometry)
 * 
 * @author Alexis Drogoul
 * @since 16 avr. 2011
 * @modified November 2011 to include isPoint(), getInnerGeometry() and getEnvelope()
 * 
 */
@vars({ @var(name = "perimeter", type = IType.FLOAT) })
public interface IShape extends ILocated, IValue, IAttributed {

	static enum Type {
		BOX, CIRCLE, CONE, CUBE, CYLINDER, ENVIRONMENT, GRIDLINE, LINEARRING("LinearRing"), LINESTRING("LineString"),
		MULTILINESTRING("MultiLineString"), MULTIPOINT("MultiPoint"), MULTIPOLYGON("MultiPolygon"), NULL, PLAN, POINT(
			"Point"), POLYGON("Polygon"), POLYHEDRON, POLYPLAN, PYRAMID, SPHERE, TEAPOT, HEMISPHERE;

		Type() {}

		Type(final String name) {
			JTS_TYPES.put(name, this);
		}
	}

	public static final String DEPTH_ATTRIBUTE = "_shape_internal_depth";
	public static final Map<String, Type> JTS_TYPES = new HashMap();
	public static final GamaList<String> TEXTURE_ATTRIBUTE = new GamaList<String>();

	public static final String TYPE_ATTRIBUTE = "_shape_internal_type";
	
	public static final String RATIO_ATTRIBUTE = "_shape_internal_ratio";

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

}
