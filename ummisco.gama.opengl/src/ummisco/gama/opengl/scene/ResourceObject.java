/*********************************************************************************************
 *
 * 'ResourceObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import com.jogamp.opengl.GL2;
import com.vividsolutions.jts.geom.Envelope;

import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaPair;
import msi.gama.util.file.Gama3DGeometryFile;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.statements.draw.DrawingAttributes;
import ummisco.gama.opengl.JOGLRenderer;

public class ResourceObject extends AbstractObject {

	public final GamaGeometryFile file;

	public ResourceObject(final GamaGeometryFile file, final DrawingAttributes attributes) {
		super(attributes);
		this.file = file;
		attributes.setEmpty(false);
	}

	public GamaPair<Double, GamaPoint> getInitialRotation() {
		return file.getInitRotation();
	}

	@Override
	public void draw(final GL2 gl, final ObjectDrawer<AbstractObject> drawer, final boolean isPicking) {

		final JOGLRenderer renderer = drawer.renderer;
		// We first push the matrix so that all translations, etc. are done
		// locally

		gl.glPushMatrix();
		// If a location is provided we use it
		final GamaPoint loc = attributes.getLocation();
		if (loc != null) {
			gl.glTranslated(loc.x, -loc.y, loc.z);
		}

		final GamaPoint size = getDimensions();

		// If there is a rotation we apply it
		Double rot = getRotationAngle();
		if (rot != null) {
			final GamaPoint axis = attributes.getAxis();
			gl.glRotated(rot, axis.x, axis.y, axis.z);
		}

		final GamaPair<Double, GamaPoint> initRotation = file.getInitRotation();
		// we also apply the initial rotation if there is any
		if (initRotation != null) {
			// AD Change to a negative rotation to fix Issue #1514
			rot = -initRotation.key;
			final GamaPoint axis = initRotation.value;
			gl.glRotated(rot, axis.x, axis.y, axis.z);
		}

		// We translate it to its center
		// FIXME Necessary for all file types ?
		//
		final Envelope env = JOGLRenderer.getEnvelopeFor(file.getPath(renderer.getSurface().getScope()));
		if (env != null)
			if (size == null) {
				gl.glTranslated(-env.getWidth() / 2, env.getHeight() / 2, 0);
			} else {
				double factor = 0.0;
				if (!(file instanceof Gama3DGeometryFile) || size.z == 0d) {
					factor = Math.min(size.x / env.getWidth(), size.y / env.getHeight());
				} else {
					final double min_xy = Math.min(size.x / env.getWidth(), size.y / env.getHeight());
					factor = Math.min(min_xy, size.z / ((Envelope3D) env).getDepth());
				}
				gl.glScaled(factor, factor, factor);
			}

		// Then we draw the geometry itself
		super.draw(gl, drawer, isPicking);

		// and we pop the matrix
		gl.glPopMatrix();
	}

	@Override
	public DrawerType getDrawerType() {
		return DrawerType.RESOURCE;
	}

	public GamaGeometryFile getFile() {
		return file;
	}

}
