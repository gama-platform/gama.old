/*********************************************************************************************
 *
 *
 * 'thisObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import com.jogamp.opengl.GL2;
import com.vividsolutions.jts.geom.Envelope;
import msi.gama.metamodel.shape.*;
import msi.gama.util.GamaPair;
import msi.gama.util.file.*;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;
import ummisco.gama.opengl.JOGLRenderer;

public class ResourceObject extends AbstractObject {

	public final GamaGeometryFile file;

	public ResourceObject(final GamaGeometryFile file, final DrawingAttributes attributes, final LayerObject layer) {
		super(attributes, layer);
		this.file = file;
	}

	@Override
	public void draw(final GL2 gl, final ObjectDrawer drawer, final boolean picking) {

		JOGLRenderer renderer = drawer.renderer;
		// We first push the matrix so that all translations, etc. are done locally

		gl.glPushMatrix();

		// If a location is provided we use it otherwise we use that of the agent if it exists
		if ( attributes.location != null ) {
			gl.glTranslated(attributes.location.x, renderer.yFlag * attributes.location.y, attributes.location.z);
		} else {
			if ( attributes.agent != null ) {
				ILocation loc = attributes.agent.getLocation();
				gl.glTranslated(loc.getX(), renderer.yFlag * loc.getY(), loc.getZ());
			}
		}

		// If there is a rotation we apply it
		if ( attributes.rotation != null ) {
			// AD Change to a negative rotation to fix Issue #1514
			Double rot = -attributes.rotation.key;
			GamaPoint axis = attributes.rotation.value;
			gl.glRotated(rot, axis.x, axis.y, axis.z);
		}

		GamaPair<Double, GamaPoint> initRotation = file.getInitRotation();
		// we also apply the initial rotation if there is any
		if ( initRotation != null ) {
			// AD Change to a negative rotation to fix Issue #1514
			Double rot = -initRotation.key;
			GamaPoint axis = initRotation.value;
			gl.glRotated(rot, axis.x, axis.y, axis.z);
		}
		Envelope env = renderer.getEnvelopeFor(file.getPath());
		GamaPoint size = getDimensions();

		// We then compute the scaling factor to apply
		double factor = 0.0;
		if ( size != null && env != null ) {
			if ( file instanceof GamaSVGFile ) {
				factor = Math.min(size.x / env.getWidth(), size.y / env.getHeight());
			} else {
				factor = Math.min(Math.min(size.x / env.getWidth(), size.y / env.getHeight()),
					size.z / ((Envelope3D) env).getDepth());
			}
			gl.glScaled(factor, factor, factor);
		}
		// And apply a color
		if ( getColor() != null ) { // does not work for obj files
			gl.glColor4d(getColor().getRed() / 255.0, getColor().getGreen() / 255.0, getColor().getBlue() / 255.0,
				getAlpha() * getColor().getAlpha() / 255.0);
		}

		// Then we draw the geometry itself
		if ( picking ) {
			gl.glPushMatrix();
			gl.glLoadName(pickingIndex);
			if ( renderer.pickedObjectIndex == pickingIndex ) {
				if ( attributes.agent != null /* && !picked */ ) {
					renderer.setPicking(false);
					pick();
					renderer.currentPickedObject = this;
					renderer.displaySurface.selectAgent(attributes.agent);
				}
			}
			gl.glColor4d(1.0, 0.0, 0.0, 1.0);

			super.draw(gl, drawer, picking);
			gl.glPopMatrix();
		} else {
			super.draw(gl, drawer, picking);
		}

		// and we pop the matrix
		gl.glPopMatrix();
	}

	@Override
	public void preload(final GL2 gl, final JOGLRenderer renderer) {
		renderer.getGeometryCache().get(gl, file);
	}
}
