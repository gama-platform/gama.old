/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.ObjectDrawer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.metamodel.shape.GamaPoint;
import ummisco.gama.opengl.OpenGL;

public abstract class ObjectDrawer<T extends AbstractObject<?, ?>> {

	final OpenGL gl;

	public ObjectDrawer(final OpenGL gl) {
		this.gl = gl;
	}

	void draw(final T object) {
		gl.beginObject(object);
		_draw(object);
		gl.endObject(object);
	}

	/**
	 * Applies a scaling to the gl context if a size is defined. The scaling is done with respect of the envelope of the
	 * geometrical object
	 * 
	 * @param object
	 *            the object defining the size and the original envelope of the geometry
	 * @param returns
	 *            true if a scaling occured, false otherwise
	 */
	protected boolean applyScaling(final T object) {

		final Scaling3D size = object.getAttributes().getSize();
		if (size != null) {
			final Envelope3D env = object.getEnvelope(gl);
			if (env != null) {
				final boolean in2D = isDrawing2D(size, env, object);
				double factor = 0.0;
				if (in2D) {
					factor = Math.min(size.getX() / env.getWidth(), size.getY() / env.getHeight());
				} else {
					final double min_xy = Math.min(size.getX() / env.getWidth(), size.getY() / env.getHeight());
					factor = Math.min(min_xy, size.getZ() / env.getDepth());
				}
				if (factor != 1d) {
					gl.scaleBy(factor, factor, factor);
				}
				return true;
			}
		}
		return false;

	}

	protected boolean isDrawing2D(final Scaling3D size, final Envelope3D env, final T object) {
		return env.isFlat() || size.getZ() == 0d;
	}

	/**
	 * Applies either the rotation defined by the modeler in the draw statement and/or the initial rotation imposed to
	 * geometries read from 3D files to the gl context
	 * 
	 * @param object
	 *            the object specifying the rotations
	 * @return true if one of the 2 rotations is applied, false otherwise
	 */
	protected boolean applyRotation(final T object) {
		final AxisAngle rotation = object.getAttributes().getRotation();
		if (rotation == null) { return false; }
		final GamaPoint loc = object.getAttributes().getLocation();
		try {
			gl.translateBy(loc.x, -loc.y, loc.z);
			final GamaPoint axis = rotation.getAxis();
			// AD Change to a negative rotation to fix Issue #1514
			gl.rotateBy(-rotation.getAngle(), axis.x, axis.y, axis.z);

		} finally {
			gl.translateBy(-loc.x, loc.y, -loc.z);
		}
		return true;
	}

	protected abstract void _draw(T object);

}
