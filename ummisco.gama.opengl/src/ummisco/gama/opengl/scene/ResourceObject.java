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
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.statements.draw.DrawingAttributes;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.JOGLRenderer;

public class ResourceObject extends AbstractObject {

	public final GamaGeometryFile file;

	public ResourceObject(final GamaGeometryFile file, final DrawingAttributes attributes, final LayerObject layer) {
		super(attributes, layer);
		this.file = file;
	}

	@Override
	public void draw(final GL2 gl, final ObjectDrawer drawer, final boolean isPicking) {

		final JOGLRenderer renderer = drawer.renderer;
		// We first push the matrix so that all translations, etc. are done
		// locally

		gl.glPushMatrix();
		final Envelope env = JOGLRenderer.getEnvelopeFor(file.getPath(renderer.getSurface().getScope()));
		// If a location is provided we use it otherwise we use that of the
		// agent if it exists
		if (attributes.getLocation() != null) {
			gl.glTranslated(attributes.getLocation().x, -attributes.getLocation().y, attributes.getLocation().z);
		}

		final GamaPoint size = getDimensions();

		// If there is a rotation we apply it
		Double rot = attributes.getAngle();
		if (rot != null) {
			// AD Change to a negative rotation to fix Issue #1514
			rot = -rot;
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
		if (size == null && env != null) {
			gl.glTranslated(-env.getWidth() / 2, env.getHeight() / 2, 0);
		}

		// We then compute the scaling factor to apply
		double factor = 0.0;
		if (size != null && env != null) {
			if (!(file instanceof Gama3DGeometryFile) || size.z == 0d) {
				factor = FastMath.min(size.x / env.getWidth(), size.y / env.getHeight());
			} else {
				final double min_xy = FastMath.min(size.x / env.getWidth(), size.y / env.getHeight());
				factor = FastMath.min(min_xy, size.z / ((Envelope3D) env).getDepth());
			}
			gl.glScaled(factor, factor, factor);
		}

		// Then we draw the geometry itself
		super.draw(gl, drawer, isPicking);

		// and we pop the matrix
		gl.glPopMatrix();
	}

	@Override
	public void preload(final GL2 gl, final Abstract3DRenderer renderer) {
		super.preload(gl, renderer);
		renderer.getGeometryListFor(gl, file);
	}
}
