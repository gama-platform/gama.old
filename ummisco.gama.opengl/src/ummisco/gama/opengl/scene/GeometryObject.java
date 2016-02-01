/*********************************************************************************************
 *
 *
 * 'GeometryObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import com.jogamp.opengl.GL2;
import com.vividsolutions.jts.geom.Geometry;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;
import ummisco.gama.opengl.JOGLRenderer;

public class GeometryObject extends AbstractObject {

	public final Geometry geometry;

	public GeometryObject(final Geometry geometry, final DrawingAttributes attributes, final LayerObject layer) {
		super(attributes, layer);
		this.geometry = geometry;
	}

	// Package protected as it is only used by the static layers
	GeometryObject(final Geometry geometry, final GamaColor color, final IShape.Type type, final LayerObject layer) {
		this(geometry, new DrawingAttributes(null, color, color), layer);
		attributes.type = type;
	}

	@Override
	public void draw(final GL2 gl, final ObjectDrawer drawer, final boolean picking) {
		if ( picking ) {
			JOGLRenderer renderer = drawer.renderer;
			gl.glPushMatrix();
			gl.glLoadName(pickingIndex);
			if ( renderer.pickedObjectIndex == pickingIndex ) {
				if ( getAgent() != null /* && !picked */ ) {
					renderer.setPicking(false);
					pick();
					renderer.currentPickedObject = this;
					renderer.displaySurface.selectAgent(getAgent());
				}
			}
			super.draw(gl, drawer, picking);
			gl.glPopMatrix();
		} else {
			super.draw(gl, drawer, picking);
		}
	}

	@Override
	public double getZ_fighting_id() {
		if ( getType() == IShape.Type.GRIDLINE ) { return super.getZ_fighting_id(); }
		if ( getAgent() != null && getAgent().getLocation().getZ() == 0d &&
			getHeight() == 0d ) { return super.getZ_fighting_id() + 1 / (double) (getAgent().getIndex() + 10); }
		return super.getZ_fighting_id();
	}

	public IShape.Type getType() {
		return attributes.type;
	}

	@Override
	public boolean isFilled() {
		return super.isFilled() && attributes.type != IShape.Type.GRIDLINE;
	}

}
