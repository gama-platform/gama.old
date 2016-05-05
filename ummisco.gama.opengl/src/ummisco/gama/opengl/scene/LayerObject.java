/*********************************************************************************************
 *
 *
 * 'LayerObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.image.BufferedImage;
import java.util.Iterator;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.FieldDrawingAttributes;
import ummisco.gama.opengl.JOGLRenderer;

/**
 * Class LayerObject.
 *
 * @author drogoul
 * @since 3 mars 2014
 *
 */
public class LayerObject implements Iterable<GeometryObject> {

	GamaPoint offset = new GamaPoint();
	GamaPoint scale = new GamaPoint(1, 1, 1);
	Double alpha = 1d;
	final ILayer layer;
	volatile boolean isInvalid;
	volatile boolean overlay;

	protected ISceneObjects<GeometryObject> geometries;
	protected ISceneObjects<ResourceObject> resources;
	protected ISceneObjects<ImageObject> images;
	protected ISceneObjects<FieldObject> dems;
	protected ISceneObjects<StringObject> strings;

	public LayerObject(final JOGLRenderer renderer, final ILayer layer) {
		this.layer = layer;
		geometries = buildSceneObjects(new GeometryDrawer(renderer));
		resources = buildSceneObjects(new ResourceDrawer(renderer));
		// TODO AD True or False for strings ??
		strings = buildSceneObjects(new StringDrawer(renderer));
		images = buildSceneObjects(new ImageDrawer(renderer));
		dems = buildSceneObjects(new FieldDrawer(renderer));
	}

	protected ISceneObjects<GeometryObject> getGeometries() {
		return geometries;
	}

	/**
	 * @return the images
	 */
	protected ISceneObjects<ImageObject> getImages() {
		return images;
	}

	/**
	 * @return the dems
	 */
	protected ISceneObjects<FieldObject> getDems() {
		return dems;
	}

	/**
	 * @return the strings
	 */
	protected ISceneObjects<StringObject> getStrings() {
		return strings;
	}

	/**
	 * @return the resources
	 */
	protected ISceneObjects<ResourceObject> getResources() {
		return resources;
	}

	protected ISceneObjects buildSceneObjects(final ObjectDrawer drawer) {
		return new SceneObjects(drawer);
	}

	private boolean isPickable() {
		return layer == null ? false : layer.isSelectable();
	}

	public void draw(final GL2 gl, final JOGLRenderer renderer) {
		if (isInvalid()) {
			return;
		}
		if (overlay) {
			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0.0, renderer.data.getEnvWidth(), renderer.data.getEnvHeight(), 0.0, -1.0,
					renderer.getMaxEnvDim());
			gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
			gl.glLoadIdentity();
			// gl.glDisable(GL.GL_CULL_FACE);
			// gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		}
		gl.glPushMatrix();
		gl.glTranslated(offset.x, -offset.y, offset.z);
		gl.glScaled(scale.x, scale.y, scale.z);
		// NOTE: In the same layer if geometries and image are drawn images are
		// drawn first and then the geometries.
		// To be sure that the line of a grid is well displayed we decide to
		// draw first the image that corresponds to the grid and then the line
		// as geometries. (otherwise the lines are invisible)
		gl.glEnable(GL.GL_TEXTURE_2D);
		final boolean picking = renderer.getPickingState().isPicking() && isPickable();

		images.draw(gl, picking);
		gl.glDisable(GL.GL_TEXTURE_2D);
		resources.draw(gl, picking);
		geometries.draw(gl, picking);
		//
		strings.draw(gl, picking);
		gl.glPopMatrix();

		gl.glPushMatrix();
		// DEMS are treated apart for the moment, since they have a very special
		// draw that already applies the scale
		// and offset...
		// FIXME this needs to be changed
		dems.draw(gl, picking);

		gl.glPopMatrix();
		if (overlay) {
			// Making sure we can render 3d again
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
			gl.glPopMatrix();
			gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
			// glPopMatrix(); ----and this?
		}
		// gl.glFlush();
	}

	public boolean isStatic() {
		if (layer == null) {
			return true;
		}
		final Boolean isDynamic = layer.isDynamic();
		return isDynamic == null ? false : !isDynamic;
	}

	public void setAlpha(final Double a) {
		alpha = a;
	}

	public GamaPoint getOffset() {
		return offset;
	}

	public void setOffset(final GamaPoint offset) {
		this.offset = offset;
	}

	public GamaPoint getScale() {
		return scale;
	}

	public void setScale(final GamaPoint scale) {
		this.scale = scale;
	}

	public void addString(final String string, final DrawingAttributes attributes) {
		strings.add(new StringObject(string, attributes, this));
	}

	public void addFile(final GamaGeometryFile file, final DrawingAttributes attributes) {
		resources.add(new ResourceObject(file, attributes, this));
	}

	public void addImage(final GamaImageFile img, final DrawingAttributes attributes) {
		images.add(new ImageObject(img, attributes, this));
	}

	public void addImage(final BufferedImage img, final DrawingAttributes attributes) {
		images.add(new ImageObject(img, attributes, this));
	}

	public void addField(final double[] fieldValues, final FieldDrawingAttributes attributes) {
		dems.add(new FieldObject(fieldValues, attributes, this));
	}

	public void addGeometry(final Geometry geometry, final DrawingAttributes attributes) {
		geometries.add(new GeometryObject(geometry, attributes, this));
	}

	public int getOrder() {
		return layer == null ? 0 : layer.getOrder();
	}

	private int getTrace() {
		if (layer == null) {
			return 0;
		}
		final Integer trace = layer.getTrace();
		return trace == null ? 0 : trace;
	}

	private boolean getFading() {
		if (layer == null) {
			return false;
		}
		final Boolean fading = layer.getFading();
		return fading == null ? false : fading;
	}

	public void clear(final GL gl) {
		final int trace = getTrace();
		// int traceSize = trace == 0 ? requestedDisplayTraceSize : trace;
		final boolean fading = getFading();
		geometries.clear(gl, trace, fading);
		resources.clear(gl, trace, fading);
		images.clear(gl, trace, fading);
		dems.clear(gl, trace, fading);
		strings.clear(gl, trace, fading);
	}

	/**
	 * Method iterator()
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<GeometryObject> iterator() {
		return geometries.getObjects().iterator();
	}

	/**
	 * @return
	 */
	public boolean isInvalid() {
		return isInvalid;
	}

	public void invalidate() {
		isInvalid = true;
	}

	/**
	 * @return
	 */
	public boolean hasTrace() {
		return getTrace() > 0;
	}

	/**
	 * @param gl
	 */
	public void preload(final GL2 gl) {
		resources.preload(gl);
	}

	/**
	 * @param b
	 */
	public void setOverlay(final boolean b) {
		overlay = b;
	}

}
