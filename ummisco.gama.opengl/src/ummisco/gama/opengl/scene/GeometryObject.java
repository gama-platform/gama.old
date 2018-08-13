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

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import ummisco.gama.opengl.OpenGL;

public class GeometryObject extends AbstractObject {

	public static class GeometryObjectWithAnimation extends GeometryObject {

		public GeometryObjectWithAnimation(final Geometry geometry, final DrawingAttributes attributes) {
			super(geometry, attributes);
		}

		@Override
		public boolean isAnimated() {
			return true;
		}

	}

	protected Geometry geometry;

	public GeometryObject(final Geometry geometry, final DrawingAttributes attributes) {
		super(attributes);
		this.geometry = geometry;
	}

	public GeometryObject(final IShape geometry, final GamaColor color, final IShape.Type type, final boolean empty) {
		this(geometry, color, type, GamaPreferences.Displays.CORE_LINE_WIDTH.getValue().floatValue());
		attributes.setEmpty(empty);
		attributes.setHeight(geometry.getDepth());
		attributes.withLighting(false);
	}

	GeometryObject(final IShape geometry, final GamaColor color, final IShape.Type type, final double lineWidth) {
		this(geometry.getInnerGeometry(), new ShapeDrawingAttributes(geometry, (IAgent) null, color, color, lineWidth));
	}

	public IShape.Type getType() {
		return attributes.getType();
	}

	@Override
	public boolean isFilled() {
		return super.isFilled() && getType() != IShape.Type.GRIDLINE;
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

	@Override
	public Envelope3D getEnvelope(final OpenGL gl) {
		return Envelope3D.of(geometry);
	}

	@Override
	public AxisAngle getRotation() {
		return attributes.getRotation();
	}

}
