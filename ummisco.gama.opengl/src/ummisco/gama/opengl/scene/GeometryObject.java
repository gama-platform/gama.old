/*********************************************************************************************
 *
 * 'GeometryObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import com.vividsolutions.jts.geom.Geometry;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.webgl.SimpleGeometryObject;

public class GeometryObject extends AbstractObject {

	protected Geometry geometry;

	public GeometryObject(final Geometry geometry, final DrawingAttributes attributes) {
		super(attributes);
		this.geometry = geometry;
	}

	// Package protected as it is only used by the static layers
	GeometryObject(final IShape geometry, final GamaColor color, final IShape.Type type, final boolean empty) {
		this(geometry, color, type, JOGLRenderer.getLineWidth());
		attributes.setEmpty(empty);
	}

	GeometryObject(final IShape geometry, final GamaColor color, final IShape.Type type, final double lineWidth) {
		this(geometry.getInnerGeometry(), new ShapeDrawingAttributes(geometry, (IAgent) null, color, color, lineWidth));
	}

	public IShape.Type getType() {
		if (!(attributes instanceof ShapeDrawingAttributes)) { return IShape.Type.POLYGON; }
		return ((ShapeDrawingAttributes) attributes).type;
	}

	@Override
	public boolean isFilled() {
		return super.isFilled() && getType() != IShape.Type.GRIDLINE;
	}

	public SimpleGeometryObject toSimpleGeometryObject() {
		return new SimpleGeometryObject(geometry, getColor(), this.getBorder(), attributes.getDepth(),
				attributes.getAngle(), attributes.getAxis(), getLocation(), attributes.getSize(), getType(),
				!isFilled(), attributes.getTextures());
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public GamaColor[] getColors() {
		return attributes.getColors();
	}

	@Override
	public DrawerType getDrawerType() {
		return DrawerType.GEOMETRY;
	}

}
