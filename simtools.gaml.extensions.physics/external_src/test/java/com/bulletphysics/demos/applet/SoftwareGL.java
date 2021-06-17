/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.demos.applet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import com.bulletphysics.demos.opengl.IGL;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.vecmath.Matrix4f;

/**
 *
 * @author jezek2
 */
public class SoftwareGL implements IGL {
	
	private Graphics3D gl;
	private int matrixMode = GL_MODELVIEW;
	private Matrix4f tmpMat = new Matrix4f();
	private Graphics g;
	
	public SoftwareGL() {
		gl = new Graphics3D();
	}
	
	public void init(BufferedImage img) {
		gl.init(((DataBufferInt)img.getRaster().getDataBuffer()).getData(), img.getWidth(), img.getHeight());
		g = img.getGraphics();
		g.setFont(new Font("Dialog", Font.PLAIN, 10));
		g.setColor(Color.BLACK);

		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	public void glViewport(int x, int y, int width, int height) {
	}

	public void glLight(int light, int pname, float[] params) {
		Light l = gl.getLight(light - GL_LIGHT0);
		switch (pname) {
			case GL_AMBIENT: l.ambient.set(params); break;
			case GL_DIFFUSE: l.diffuse.set(params); break;
			case GL_SPECULAR: l.specular.set(params); break;
			case GL_POSITION:
				l.position.set(params);
				if (l.position.w == 0f) {
					float invlen = 1f / (float)Math.sqrt(l.position.x*l.position.x + l.position.y*l.position.y + l.position.z*l.position.z);
					l.position.x *= invlen;
					l.position.y *= invlen;
					l.position.z *= invlen;
				}
				break;
		}
	}

	public void glEnable(int cap) {
		if (cap >= GL_LIGHT0 && cap <= GL_LIGHT1) {
			gl.getLight(cap - GL_LIGHT0).enabled = true;
			return;
		}
		
		switch (cap) {
			case GL_LIGHTING: gl.setLightingEnabled(true); break;
		}
	}

	public void glDisable(int cap) {
		if (cap >= GL_LIGHT0 && cap <= GL_LIGHT1) {
			gl.getLight(cap - GL_LIGHT0).enabled = false;
			return;
		}
		
		switch (cap) {
			case GL_LIGHTING: gl.setLightingEnabled(false); break;
		}
	}

	public void glShadeModel(int mode) {
	}

	public void glDepthFunc(int func) {
	}

	public void glClearColor(float red, float green, float blue, float alpha) {
		gl.setClearColor(red, red, blue);
	}

	public void glMatrixMode(int mode) {
		matrixMode = mode;
	}
	
	private void setMatrix(Matrix4f mat) {
		switch (matrixMode) {
			case GL_MODELVIEW: gl.setViewMatrix(mat); break;
			case GL_PROJECTION: gl.setProjMatrix(mat); break;
			default: throw new IllegalStateException();
		}
	}
	
	private void mulMatrix(Matrix4f mat) {
		switch (matrixMode) {
			case GL_MODELVIEW: gl.mulViewMatrix(mat); break;
			case GL_PROJECTION: gl.mulProjMatrix(mat); break;
			default: throw new IllegalStateException();
		}
	}

	public void glLoadIdentity() {
		tmpMat.setIdentity();
		setMatrix(tmpMat);
	}

	public void glFrustum(double left, double right, double bottom, double top, double zNear, double zFar) {
		Utils.setFrustum(tmpMat, (float)left, (float)right, (float)bottom, (float)top, (float)zNear, (float)zFar);
		mulMatrix(tmpMat);
	}

	public void gluLookAt(float eyex, float eyey, float eyez, float centerx, float centery, float centerz, float upx, float upy, float upz) {
		Utils.setLookAt(tmpMat, eyex, eyey, eyez, centerx, centery, centerz, upx, upy, upz);
		mulMatrix(tmpMat);
	}

	public void glPushMatrix() {
		if (matrixMode == GL_MODELVIEW) {
			gl.pushViewMatrix();
		}
	}

	public void glPopMatrix() {
		if (matrixMode == GL_MODELVIEW) {
			gl.popViewMatrix();
		}
	}

	public void gluOrtho2D(float left, float right, float bottom, float top) {
	}

	public void glScalef(float x, float y, float z) {
		tmpMat.setIdentity();
		tmpMat.m00 = x;
		tmpMat.m11 = y;
		tmpMat.m22 = z;
		mulMatrix(tmpMat);
	}

	public void glTranslatef(float x, float y, float z) {
		tmpMat.setIdentity();
		tmpMat.m03 = x;
		tmpMat.m13 = y;
		tmpMat.m23 = z;
		mulMatrix(tmpMat);
	}

	public void glColor3f(float red, float green, float blue) {
		gl.setColor(red, green, blue);
	}

	public void glClear(int glMask) {
		int mask = 0;
		if ((glMask & GL_COLOR_BUFFER_BIT) != 0) mask |= Graphics3D.COLOR_BUFFER;
		if ((glMask & GL_DEPTH_BUFFER_BIT) != 0) mask |= Graphics3D.DEPTH_BUFFER;
		gl.clear(mask);
	}

	public void glBegin(int mode) {
		switch (mode) {
			case GL_LINES: gl.begin(Graphics3D.LINES); break;
			case GL_TRIANGLES: gl.begin(Graphics3D.TRIANGLES); break;
			case GL_QUADS: gl.begin(Graphics3D.QUADS); break;
			default: throw new IllegalArgumentException();
		}
	}

	public void glEnd() {
		gl.end();
	}

	public void glVertex3f(float x, float y, float z) {
		gl.addVertex(x, y, z);
	}

	public void glLineWidth(float width) {
	}

	public void glPointSize(float size) {
	}

	public void glNormal3f(float nx, float ny, float nz) {
		gl.setNormal(nx, ny, nz);
	}

	public void glMultMatrix(float[] m) {
		tmpMat.m00 = m[0];
		tmpMat.m10 = m[1];
		tmpMat.m20 = m[2];
		tmpMat.m30 = m[3];
		tmpMat.m01 = m[4];
		tmpMat.m11 = m[5];
		tmpMat.m21 = m[6];
		tmpMat.m31 = m[7];
		tmpMat.m02 = m[8];
		tmpMat.m12 = m[9];
		tmpMat.m22 = m[10];
		tmpMat.m32 = m[11];
		tmpMat.m03 = m[12];
		tmpMat.m13 = m[13];
		tmpMat.m23 = m[14];
		tmpMat.m33 = m[15];
		mulMatrix(tmpMat);
	}

	public void drawCube(float extent) {
		extent = extent * 0.5f;
		
	    glBegin(GL_QUADS);
        glNormal3f( 1f, 0f, 0f); glVertex3f(+extent,-extent,+extent); glVertex3f(+extent,-extent,-extent); glVertex3f(+extent,+extent,-extent); glVertex3f(+extent,+extent,+extent);
        glNormal3f( 0f, 1f, 0f); glVertex3f(+extent,+extent,+extent); glVertex3f(+extent,+extent,-extent); glVertex3f(-extent,+extent,-extent); glVertex3f(-extent,+extent,+extent);
        glNormal3f( 0f, 0f, 1f); glVertex3f(+extent,+extent,+extent); glVertex3f(-extent,+extent,+extent); glVertex3f(-extent,-extent,+extent); glVertex3f(+extent,-extent,+extent);
        glNormal3f(-1f, 0f, 0f); glVertex3f(-extent,-extent,+extent); glVertex3f(-extent,+extent,+extent); glVertex3f(-extent,+extent,-extent); glVertex3f(-extent,-extent,-extent);
        glNormal3f( 0f,-1f, 0f); glVertex3f(-extent,-extent,+extent); glVertex3f(-extent,-extent,-extent); glVertex3f(+extent,-extent,-extent); glVertex3f(+extent,-extent,+extent);
        glNormal3f( 0f, 0f,-1f); glVertex3f(-extent,-extent,-extent); glVertex3f(-extent,+extent,-extent); glVertex3f(+extent,+extent,-extent); glVertex3f(+extent,-extent,-extent);
		glEnd();
	}

	private Sphere sphere = new Sphere();
	private Cylinder cylinder = new Cylinder();
	private Disk disk = new Disk();
	
	public void drawSphere(float radius, int slices, int stacks) {
		sphere.draw(gl, radius, 6, 6);
	}

	public void drawCylinder(float radius, float halfHeight, int upAxis) {
		glPushMatrix();
		
		Matrix4f mat = new Matrix4f();
		
		switch (upAxis) {
			case 0:
				//glRotatef(-90f, 0.0f, 1.0f, 0.0f);
				mat.rotY((float)Math.PI * -0.5f);
				mulMatrix(mat);
				glTranslatef(0.0f, 0.0f, -halfHeight);
				break;
			case 1:
				//glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
				mat.rotX((float)Math.PI * -0.5f);
				mulMatrix(mat);
				glTranslatef(0.0f, 0.0f, -halfHeight);
				break;
			case 2:
				glTranslatef(0.0f, 0.0f, -halfHeight);
				break;
			default: {
				assert (false);
			}
		}

		// The gluCylinder subroutine draws a cylinder that is oriented along the z axis. 
		// The base of the cylinder is placed at z = 0; the top of the cylinder is placed at z=height. 
		// Like a sphere, the cylinder is subdivided around the z axis into slices and along the z axis into stacks.

		disk.setDrawStyle(Quadric.GLU_FILL);
		disk.setNormals(Quadric.GLU_SMOOTH);
		disk.draw(gl, 0, radius, 8, 1);
		
		cylinder.setDrawStyle(Quadric.GLU_FILL);
		cylinder.setNormals(Quadric.GLU_SMOOTH);
		cylinder.draw(gl, radius, radius, 2f * halfHeight, 8, 1);
		
		glTranslatef(0f, 0f, 2f * halfHeight);
		mat.rotY(-(float)Math.PI);
		mulMatrix(mat);
		disk.draw(gl, 0, radius, 8, 1);

		glPopMatrix();
	}

	public void drawString(CharSequence s, int x, int y, float red, float green, float blue) {
		g.setColor(new Color(red, green, blue));
		g.drawString(s.toString(), x, y);
	}

}
