/*********************************************************************************************
 *
 * 'GLUtilNormal.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.utils;

import java.awt.Color;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLContext;

import msi.gaml.operators.fastmaths.FastMath;
import ummisco.gama.opengl.JOGLRenderer;

public class GLUtilNormal {

	// Calculate the normal, from three points on a surface
	public static double[] CalculateNormal(final Vertex pointA, final Vertex pointB, final Vertex pointC,
			final int multiplier) {
		// Step 1
		// build two vectors, one pointing from A to B, the other pointing from
		// A to C
		final double[] vector1 = new double[3];
		final double[] vector2 = new double[3];

		vector1[0] = pointB.x - pointA.x;
		vector2[0] = pointC.x - pointA.x;

		vector1[1] = pointB.y - pointA.y;
		vector2[1] = pointC.y - pointA.y;

		vector1[2] = pointB.z - pointA.z;
		vector2[2] = pointC.z - pointA.z;

		// Step 2
		// do the cross product of these two vectors to find the normal
		// of the surface

		final double[] normal = new double[3];
		// normal[0] = vector1[1] * vector2[2] - vector1[2] * vector2[1];
		// normal[1] = vector1[2] * vector2[0] - vector1[0] * vector2[2];
		// normal[2] = vector1[0] * vector2[1] - vector1[1] * vector2[0];
		normal[0] = vector1[1] * vector2[2] - vector1[2] * vector2[1];
		normal[1] = vector1[2] * vector2[0] - vector1[0] * vector2[2];
		normal[2] = vector1[0] * vector2[1] - vector1[1] * vector2[0];

		// Step 3
		// "normalise" the normal (make sure it has length of one)

		double total = 0.0d;
		for (int i = 0; i < 3; i++) {
			total += normal[i] * normal[i];
		}
		final double length = FastMath.sqrt(total);

		for (int i = 0; i < 3; i++) {
			normal[i] /= length;
			normal[i] *= multiplier;
		}

		// done
		return normal;
	}

	public static Vertex GetCenter(final Vertex pointA, final Vertex pointB, final Vertex pointC) {
		final Vertex center = new Vertex();
		center.x = (pointA.x + pointB.x + pointC.x) / 3;
		center.y = (pointA.y + pointB.y + pointC.y) / 3;
		center.z = (pointA.z + pointB.z + pointC.z) / 3;
		return center;
	}

	public static Vertex GetCenter(final Vertex[] vertices) {
		final Vertex center = new Vertex();
		for (int i = 0; i < vertices.length; i++) {
			center.x = center.x + vertices[i].x;
			center.y = center.y + vertices[i].y;
			center.z = center.z + vertices[i].z;
		}

		center.x = center.x / vertices.length;
		center.y = center.y / vertices.length;
		center.z = center.z / vertices.length;
		return center;
	}

	public static void HandleNormal(final Vertex[] vertices, final int norm_dir1, final JOGLRenderer renderer) {
		final GL2 gl = GLContext.getCurrentGL().getGL2();
		final double[] normal = CalculateNormal(vertices[0], vertices[0 + 1], vertices[0 + 2], 1);

		gl.glNormal3dv(normal, 0);

		normal[0] = renderer.getMaxEnvDim() / 20 * normal[0];
		normal[1] = renderer.getMaxEnvDim() / 20 * normal[1];
		normal[2] = renderer.getMaxEnvDim() / 20 * normal[2];

		if (renderer.data.isDraw_norm()) {

			drawNormal(vertices, renderer, gl, normal);

		}

	}

	public static void drawNormal(final Vertex[] vertices, final JOGLRenderer renderer, final GL2 gl,
			final double[] normal) {
		// memorize the current color to the buffer
		final Color previousColor = renderer.getCurrentColor();

		final Vertex center = GetCenter(vertices);
		gl.glBegin(GL.GL_LINES);
		// set the color of the normal to red
		renderer.setCurrentColor(gl, Color.red);
		// GLUtilGLContext.SetCurrentColor(gl, 1.0f, 0.0f, 0.0f);
		gl.glVertex3d(center.x, center.y, center.z);
		gl.glVertex3d(center.x + normal[0], center.y + normal[1], center.z + normal[2]);
		gl.glEnd();

		gl.glPointSize(2.0f);
		gl.glBegin(GL.GL_POINTS);
		gl.glVertex3d(center.x + normal[0], center.y + normal[1], center.z + normal[2]);
		gl.glEnd();

		// reset the previous color of the opengl context.
		renderer.setCurrentColor(gl, previousColor);
	}

}
