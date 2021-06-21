/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
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

package com.bulletphysics.demos.opengl;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import com.bulletphysics.demos.opengl.FontRender.GLFont;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

/**
 *
 * @author jezek2
 */
public class LwjglGL implements IGL {

	private static FloatBuffer floatBuf = BufferUtils.createFloatBuffer(16);
	
	private GLFont font;

	public void init() {
		try {
			//font = FontRender.createFont("Dialog", 11, false, true);
			font = new GLFont(IGL.class.getResourceAsStream("DejaVu_Sans_11.fnt"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void glLight(int light, int pname, float[] params) {
		FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(params.length);
		floatBuffer.put(params);
		floatBuffer.flip();
		GL11.glLight(light, pname, floatBuffer);
	}

	public void glEnable(int cap) {
		GL11.glEnable(cap);
	}

	public void glDisable(int cap) {
		GL11.glDisable(cap);
	}

	public void glShadeModel(int mode) {
		GL11.glShadeModel(mode);
	}

	public void glDepthFunc(int func) {
		GL11.glDepthFunc(func);
	}

	public void glClearColor(float red, float green, float blue, float alpha) {
		GL11.glClearColor(red, green, blue, alpha);
	}

	public void glMatrixMode(int mode) {
		GL11.glMatrixMode(mode);
	}

	public void glLoadIdentity() {
		GL11.glLoadIdentity();
	}

	public void glFrustum(double left, double right, double bottom, double top, double zNear, double zFar) {
		GL11.glFrustum(left, right, bottom, top, zNear, zFar);
	}

	public void gluLookAt(float eyex, float eyey, float eyez, float centerx, float centery, float centerz, float upx, float upy, float upz) {
		GLU.gluLookAt(eyex, eyey, eyez, centerx, centery, centerz, upx, upy, upz);
	}

	public void glViewport(int x, int y, int width, int height) {
		GL11.glViewport(x, y, width, height);
	}

	public void glPushMatrix() {
		GL11.glPushMatrix();
	}

	public void glPopMatrix() {
		GL11.glPopMatrix();
	}

	public void gluOrtho2D(float left, float right, float bottom, float top) {
		GLU.gluOrtho2D(left, right, bottom, top);
	}

	public void glScalef(float x, float y, float z) {
		GL11.glScalef(x, y, z);
	}

	public void glTranslatef(float x, float y, float z) {
		GL11.glTranslatef(x, y, z);
	}

	public void glColor3f(float red, float green, float blue) {
		GL11.glColor3f(red, green, blue);
	}

	public void glClear(int mask) {
		GL11.glClear(mask);
	}

	public void glBegin(int mode) {
		GL11.glBegin(mode);
	}

	public void glEnd() {
		GL11.glEnd();
	}

	public void glVertex3f(float x, float y, float z) {
		GL11.glVertex3f(x, y, z);
	}

	public void glLineWidth(float width) {
		GL11.glLineWidth(width);
	}

	public void glPointSize(float size) {
		GL11.glPointSize(size);
	}

	public void glNormal3f(float nx, float ny, float nz) {
		GL11.glNormal3f(nx, ny, nz);
	}

	public void glMultMatrix(float[] m) {
		floatBuf.clear();
		floatBuf.put(m).flip();
		GL11.glMultMatrix(floatBuf);
	}
	
	////////////////////////////////////////////////////////////////////////////

	public void drawCube(float extent) {
		extent = extent * 0.5f;
		
	    GL11.glBegin(GL11.GL_QUADS);
        GL11.glNormal3f( 1f, 0f, 0f); GL11.glVertex3f(+extent,-extent,+extent); GL11.glVertex3f(+extent,-extent,-extent); GL11.glVertex3f(+extent,+extent,-extent); GL11.glVertex3f(+extent,+extent,+extent);
        GL11.glNormal3f( 0f, 1f, 0f); GL11.glVertex3f(+extent,+extent,+extent); GL11.glVertex3f(+extent,+extent,-extent); GL11.glVertex3f(-extent,+extent,-extent); GL11.glVertex3f(-extent,+extent,+extent);
        GL11.glNormal3f( 0f, 0f, 1f); GL11.glVertex3f(+extent,+extent,+extent); GL11.glVertex3f(-extent,+extent,+extent); GL11.glVertex3f(-extent,-extent,+extent); GL11.glVertex3f(+extent,-extent,+extent);
        GL11.glNormal3f(-1f, 0f, 0f); GL11.glVertex3f(-extent,-extent,+extent); GL11.glVertex3f(-extent,+extent,+extent); GL11.glVertex3f(-extent,+extent,-extent); GL11.glVertex3f(-extent,-extent,-extent);
        GL11.glNormal3f( 0f,-1f, 0f); GL11.glVertex3f(-extent,-extent,+extent); GL11.glVertex3f(-extent,-extent,-extent); GL11.glVertex3f(+extent,-extent,-extent); GL11.glVertex3f(+extent,-extent,+extent);
        GL11.glNormal3f( 0f, 0f,-1f); GL11.glVertex3f(-extent,-extent,-extent); GL11.glVertex3f(-extent,+extent,-extent); GL11.glVertex3f(+extent,+extent,-extent); GL11.glVertex3f(+extent,-extent,-extent);
		GL11.glEnd();
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	private static final Cylinder cylinder = new Cylinder();
	private static final Disk disk = new Disk();
	private static final Sphere sphere = new Sphere();
	
	private static class SphereKey {
		public float radius;

		public SphereKey() {
		}

		public SphereKey(SphereKey key) {
			radius = key.radius;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof SphereKey)) return false;
			SphereKey other = (SphereKey)obj;
			return radius == other.radius;
		}

		@Override
		public int hashCode() {
			return Float.floatToIntBits(radius);
		}
	}
	
	private static Map<SphereKey,Integer> sphereDisplayLists = new HashMap<SphereKey,Integer>();
	private static SphereKey sphereKey = new SphereKey();
	
	public void drawSphere(float radius, int slices, int stacks) {
		sphereKey.radius = radius;
		Integer glList = sphereDisplayLists.get(sphereKey);
		if (glList == null) {
			glList = glGenLists(1);
			glNewList(glList, GL_COMPILE);
			sphere.draw(radius, 8, 8);
			glEndList();
			sphereDisplayLists.put(new SphereKey(sphereKey), glList);
		}
		
		glCallList(glList);
	}
	
	////////////////////////////////////////////////////////////////////////////

	
	private static class CylinderKey {
		public float radius;
		public float halfHeight;

		public CylinderKey() {
		}

		public CylinderKey(CylinderKey key) {
			radius = key.radius;
			halfHeight = key.halfHeight;
		}

		public void set(float radius, float halfHeight) {
			this.radius = radius;
			this.halfHeight = halfHeight;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof CylinderKey)) return false;
			CylinderKey other = (CylinderKey) obj;
			if (radius != other.radius) return false;
			if (halfHeight != other.halfHeight) return false;
			return true;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 23 * hash + Float.floatToIntBits(radius);
			hash = 23 * hash + Float.floatToIntBits(halfHeight);
			return hash;
		}
	}
	
	private static Map<CylinderKey,Integer> cylinderDisplayLists = new HashMap<CylinderKey,Integer>();
	private static CylinderKey cylinderKey = new CylinderKey();
	
	public void drawCylinder(float radius, float halfHeight, int upAxis) {
		glPushMatrix();
		switch (upAxis) {
			case 0:
				glRotatef(-90f, 0.0f, 1.0f, 0.0f);
				glTranslatef(0.0f, 0.0f, -halfHeight);
				break;
			case 1:
				glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
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

		cylinderKey.set(radius, halfHeight);
		Integer glList = cylinderDisplayLists.get(cylinderKey);
		if (glList == null) {
			glList = glGenLists(1);
			glNewList(glList, GL_COMPILE);

			disk.setDrawStyle(GLU_FILL);
			disk.setNormals(GLU_SMOOTH);
			disk.draw(0, radius, 15, 10);
			
			cylinder.setDrawStyle(GLU_FILL);
			cylinder.setNormals(GLU_SMOOTH);
			cylinder.draw(radius, radius, 2f * halfHeight, 15, 10);

			glTranslatef(0f, 0f, 2f * halfHeight);
			glRotatef(-180f, 0f, 1f, 0f);
			disk.draw(0, radius, 15, 10);
			
			glEndList();
			cylinderDisplayLists.put(new CylinderKey(cylinderKey), glList);
		}
		
		glCallList(glList);

		glPopMatrix();
	}
	
	////////////////////////////////////////////////////////////////////////////

	public void drawString(CharSequence s, int x, int y, float red, float green, float blue) {
		if (font != null) {
			FontRender.drawString(font, s, x, y, red, green, blue);
		}
	}

}
