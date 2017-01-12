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
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUtessellatorCallbackAdapter;
import com.jogamp.opengl.util.texture.Texture;

import jogamp.opengl.glu.tessellator.GLUtessellatorImpl;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GamaCoordinateSequence.IndexedVisitor;
import msi.gama.common.util.GeometryUtils;
import msi.gama.common.util.ICoordinates;
import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import ummisco.gama.opengl.JOGLRenderer;

public abstract class ObjectDrawer<T extends AbstractObject> extends GLUtessellatorCallbackAdapter {
	protected static final float[] RECT_TEX_COORDS = { 0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f };
	protected static final float[] REVERSE_RECT_TEX_COORDS = { 1f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, };
	protected static final float[] TRIANGLE_TEX_COORDS = { 0.5f, 0f, 1f, 1f, 0f, 1f, };
	protected static final float[] REVERSE_TRIANGLE_TEX_COORDS = { 0.5f, 0f, 0f, 1f, 1f, 1f, };

	protected final GLUtessellatorImpl tobj = (GLUtessellatorImpl) GLU.gluNewTess();

	protected final IndexedVisitor triangleDrawer, rectangleDrawer, rectangleReverseDrawer, triangleReverseDrawer,
			pointDrawer, tessDrawer;

	final JOGLRenderer renderer;

	final ICoordinates normalVertices = GeometryUtils.GEOMETRY_FACTORY.getCoordinateSequenceFactory().create(2, 3);
	final GamaPoint normal = new GamaPoint();

	public ObjectDrawer(final JOGLRenderer r) {
		renderer = r;
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, this);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, this);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, this);

		triangleDrawer = (x, y, z, i) -> {
			renderer.getGL().glTexCoord2f(TRIANGLE_TEX_COORDS[i * 2], TRIANGLE_TEX_COORDS[i * 2 + 1]);
			renderer.getGL().glVertex3d(x, y, z);
		};

		triangleReverseDrawer = (x, y, z, i) -> {
			renderer.getGL().glTexCoord2f(REVERSE_TRIANGLE_TEX_COORDS[i * 2], REVERSE_TRIANGLE_TEX_COORDS[i * 2 + 1]);
			renderer.getGL().glVertex3d(x, y, z);
		};

		rectangleDrawer = (x, y, z, i) -> {
			renderer.getGL().glTexCoord2f(RECT_TEX_COORDS[i * 2], RECT_TEX_COORDS[i * 2 + 1]);
			renderer.getGL().glVertex3d(x, y, z);
		};

		rectangleReverseDrawer = (x, y, z, i) -> {
			renderer.getGL().glTexCoord2f(REVERSE_RECT_TEX_COORDS[i * 2], REVERSE_RECT_TEX_COORDS[i * 2 + 1]);
			renderer.getGL().glVertex3d(x, y, z);
		};

		tessDrawer = (x, y, z, i) -> {
			final double[] data = new double[] { x, y, z };
			tobj.gluTessVertex(data, 0, data);
		};

		pointDrawer = (x, y, z, i) -> {
			renderer.getGL().glVertex3d(x, y, z);
		};
	}

	void draw(final GL2 gl, final T object) {
		gl.glPushMatrix();
		try {
			applyPolygonOffset(gl, object);
			applyLineWidth(gl, object);
			applyTextures(gl, object);
			applyColor(gl, object);
			_draw(gl, object);
		} finally {
			gl.glPopMatrix();
		}
	}

	protected void applyColor(final GL2 gl, final T object) {
		// Applying the fill color
		final Color color = object.isTextured() ? Color.white : object.getColor();
		renderer.setCurrentColor(gl, color, object.getAlpha());
	}

	protected void applyTextures(final GL2 gl, final T object) {
		// Applying the textures
		if (object.isTextured()) {
			final Texture texture = object.getPrimaryTexture(gl, renderer);
			if (texture != null) {
				texture.enable(gl);
				texture.bind(gl);
			}
		} else {
			gl.glDisable(GL.GL_TEXTURE_2D);
		}
	}

	protected void applyLineWidth(final GL2 gl, final T object) {
		// Applying the line width
		final double lineWidth = object.getLineWidth();
		gl.glLineWidth((float) lineWidth);
	}

	protected void applyPolygonOffset(final GL2 gl, final T object) {
		if (renderer.data.isZ_fighting()) {
			if (!object.isFilled() || renderer.data.isTriangulation()) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
				gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
				gl.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
				gl.glPolygonOffset(0.0f, -((float) object.getZFightingId() + 0.1f));
			} else {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
				gl.glDisable(GL2GL3.GL_POLYGON_OFFSET_LINE);
				gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
				gl.glPolygonOffset(1, (float) -object.getZFightingId());
			}
		}
	}

	protected abstract void _draw(GL2 gl, T object);

	public void dispose() {
		GLU.gluDeleteTess(tobj);
	}

	// Tesselation callback methods

	@Override
	public void begin(final int type) {
		renderer.getGL().glBegin(type);
	}

	@Override
	public void end() {
		renderer.getGL().glEnd();
	}

	@Override
	public void vertex(final Object vertexData) {
		renderer.getGL().glVertex3dv((double[]) vertexData, 0);
	}

	protected GamaPoint handleNormal(final GL2 gl, final ICoordinates vertices, final boolean clockwise) {
		vertices.getNormal(clockwise, 1, normal);
		gl.glNormal3d(normal.x, normal.y, normal.z);
		if (JOGLRenderer.DRAW_NORM) {
			final GamaPoint center = vertices.getCenter();
			normal.multiplyBy(renderer.getMaxEnvDim() / 20);
			normal.add(center);
			normalVertices.replaceWith(center, normal);
			_contour(gl, normalVertices, Color.red);
		}
		return normal;
	}

	protected void _triangle(final GL2 gl, final ICoordinates vertices, final boolean solid, final boolean clockwise,
			final boolean computeNormal, final Color border) {
		if (solid) {
			if (computeNormal)
				handleNormal(gl, vertices, clockwise);
			gl.glBegin(GL.GL_TRIANGLES);
			if (clockwise)
				vertices.visit(triangleDrawer, 3, clockwise);
			else
				vertices.visit(triangleReverseDrawer, 3, clockwise);
			gl.glEnd();
		}
		_contour(gl, vertices, border);
	}

	protected void _rectangle(final GL2 gl, final ICoordinates vertices, final boolean solid, final boolean clockwise,
			final boolean computeNormal, final Color border) {
		if (solid) {
			if (computeNormal)
				handleNormal(gl, vertices, clockwise);
			gl.glBegin(GL2ES3.GL_QUADS);
			if (clockwise)
				vertices.visit(rectangleDrawer, 4, clockwise);
			else
				vertices.visit(rectangleReverseDrawer, 4, clockwise);
			gl.glEnd();
		}
		_contour(gl, vertices, border);
	}

	protected void _contour(final GL2 gl, final ICoordinates vertices, final Color border) {
		if (border == null)
			return;
		final Color previous = renderer.getCurrentColor();
		final double alpha = previous.getAlpha() / 255d;
		renderer.setCurrentColor(gl, border, alpha);
		gl.glBegin(GL.GL_LINE_LOOP);
		vertices.visit(pointDrawer, -1, true);
		gl.glEnd();
		renderer.setCurrentColor(gl, previous, 1);
	}

	public void drawROIHelper(final GL2 gl, final Envelope3D envelope) {
		if (envelope == null)
			return;
		final GamaPoint pos = envelope.centre();
		final double width = envelope.getWidth();
		final double height = envelope.getHeight();
		final double z = Math.max(2, renderer.getMaxEnvDim() / 100);
		// TODO
		if (gl == null) { return; }
		gl.glPushMatrix();
		gl.glTranslated(pos.x, pos.y, pos.z);
		gl.glScaled(width, height, z);
		renderer.setCurrentColor(gl, 0, 0.5, 0, 0.15);
		renderer.getGlut().glutSolidCube(0.99f);
		renderer.setCurrentColor(gl, Color.gray, 1);
		renderer.getGlut().glutWireCube(1f);
		gl.glPopMatrix();
	}

	public void drawRotationHelper(final GL2 gl, final GamaPoint pos, final double distance) {
		// TODO
		if (gl == null) { return; }
		final int slices = GamaPreferences.DISPLAY_SLICE_NUMBER.getValue();
		final int stacks = slices;
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glPushMatrix();
		gl.glTranslated(pos.x, pos.y, pos.z);
		renderer.setCurrentColor(gl, Color.gray, 1);
		renderer.getGlut().glutSolidSphere(5.0 * (distance / 500), slices, stacks);
		renderer.setCurrentColor(gl, Color.gray, 0.1);
		renderer.getGlut().glutSolidSphere(49.0 * (distance / 500), slices, stacks);
		renderer.setCurrentColor(gl, Color.gray, 1);
		renderer.getGlut().glutWireSphere(50.0 * (distance / 500), slices / 2, stacks / 2);
		gl.glPopMatrix();
		gl.glEnable(GL2.GL_LIGHTING);
	}

	protected void _line(final GL2 gl, final ICoordinates vertices, final int max, final boolean closed) {
		gl.glBegin(closed ? GL.GL_LINE_LOOP : GL.GL_LINE_STRIP);
		vertices.visit(pointDrawer, max, true);
		gl.glEnd();
	}

}
