/*********************************************************************************************
 *
 * 'ObjectDrawer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import ummisco.gama.opengl.JOGLRenderer;

public abstract class ObjectDrawer<T extends AbstractObject> {

	final JOGLRenderer renderer;
	boolean textured = false;
	boolean filled = false;
	final TextureHelper textureHelper = new TextureHelper();

	protected final GamaPoint tempNormal = new GamaPoint();

	public ObjectDrawer(final JOGLRenderer r) {
		renderer = r;
	}

	void draw(final GL2 gl, final T object) {
		try {
			gl.glPushMatrix();
			applyPolygonOffset(object);
			applyLineWidth(object);
			applyTextures(object);
			applyColor(object);
			_draw(gl, object);
		} finally {
			gl.glPopMatrix();
		}
	}

	protected void applyColor(final T object) {
		filled = object.isFilled() && !renderer.data.isTriangulation();
		// Applying the fill color
		renderer.setCurrentColor(object.getColor());
	}

	protected void applyTextures(final T object) {
		textured = object.isTextured() && !renderer.data.isTriangulation();
		// Applying the textures
		renderer.setCurrentTexture(textured ? object.getPrimaryTexture(renderer.getGL(), renderer) : null);
	}

	protected void applyLineWidth(final T object) {
		// Applying the line width
		final double lineWidth = object.getLineWidth();
		renderer.getGL().glLineWidth((float) lineWidth);
	}

	protected void applyPolygonOffset(final T object) {
		renderer.getGL().glTranslated(0, 0,
				object.getZFightingOffset() * renderer.getCurrentScene().getVisualZIncrement());
	}

	protected abstract void _draw(GL2 gl, T object);

	protected GamaPoint _normal(final ICoordinates vertices, final boolean clockwise) {
		vertices.getNormal(clockwise, 1, tempNormal);
		renderer.getGL().glNormal3d(tempNormal.x, tempNormal.y, tempNormal.z);
		if (textured) {
			textureHelper.computeTextureCoordinates(vertices, tempNormal, clockwise);
		}
		if (JOGLRenderer.DRAW_NORM) {
			final GamaPoint center = vertices.getCenter();
			tempNormal.multiplyBy(renderer.getMaxEnvDim() / 20);
			tempNormal.add(center);
			final ICoordinates normalVertices =
					GeometryUtils.GEOMETRY_FACTORY.getCoordinateSequenceFactory().create(2, 3);
			normalVertices.replaceWith(center, tempNormal);
			_contour(normalVertices, Color.red);
		}
		return tempNormal;
	}

	/**
	 * Draws an arbitrary shape using a set of vertices as input, computing the normal if necessary and drawing the
	 * contour if a border is present
	 * 
	 * @param gl
	 *            the OpenGL context
	 * @param vertices
	 *            the set of vertices to draw
	 * @param number
	 *            the number of vertices to draw. Either 3 (a triangle), 4 (a quad) or -1 (a polygon)
	 * @param solid
	 *            whether to draw the shape as a solid shape
	 * @param clockwise
	 *            whether to draw the shape in the clockwise direction (the vertices are always oriented clockwise)
	 * @param computeNormal
	 *            whether to compute the normal for this shape
	 * @param border
	 *            if not null, will be used to draw the contour
	 */
	protected void _shape(final ICoordinates vertices, final int number, final boolean solid, final boolean clockwise,
			final boolean computeNormal, final Color border) {
		final GL2 gl = renderer.getGL();
		if (solid) {
			if (computeNormal)
				_normal(vertices, clockwise);
			final int style = number == 4 ? GL2.GL_QUADS : number == -1 ? GL2.GL_POLYGON : GL2.GL_TRIANGLES;
			gl.glBegin(style);
			vertices.visit(this::_point, number, clockwise);
			gl.glEnd();
		}
		_contour(vertices, border);
	}

	protected void _contour(final ICoordinates vertices, final Color border) {
		if (border == null)
			return;
		final boolean old = this.textured;
		this.textured = false;
		final Color previous = renderer.getCurrentColor();
		final double alpha = previous.getAlpha() / 255d;
		renderer.setCurrentColor(border, alpha);
		final GL2 gl = renderer.getGL();
		gl.glBegin(GL.GL_LINE_LOOP);
		vertices.visit(this::_point, -1, true);
		gl.glEnd();
		renderer.setCurrentColor(previous, 1);
		this.textured = old;
	}

	protected void _line(final ICoordinates vertices, final int max, final boolean closed) {
		final GL2 gl = renderer.getGL();
		gl.glBegin(closed ? GL.GL_LINE_LOOP : GL.GL_LINE_STRIP);
		vertices.visit(this::_point, max, true);
		gl.glEnd();
	}

	protected void _point(final double x, final double y, final double z, final int i) {
		if (textured)
			textureHelper.process(renderer.getGL(), x, y, z);
		renderer.getGL().glVertex3d(x, y, z);

	}

}
