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

/**
 *
 * @author jezek2
 */
public interface IGL {

	public static final int GL_LIGHT0 = 0x4000;
	public static final int GL_LIGHT1 = 0x4001;
	public static final int GL_AMBIENT = 0x1200;
	public static final int GL_DIFFUSE = 0x1201;
	public static final int GL_SPECULAR = 0x1202;
	public static final int GL_POSITION = 0x1203;
	public static final int GL_LIGHTING = 0xb50;
	public static final int GL_SMOOTH = 0x1d01;
	public static final int GL_DEPTH_TEST = 0xb71;
	public static final int GL_LESS = 0x201;
	public static final int GL_MODELVIEW = 0x1700;
	public static final int GL_PROJECTION = 0x1701;
	public static final int GL_COLOR_BUFFER_BIT = 0x4000;
	public static final int GL_DEPTH_BUFFER_BIT = 0x100;
	public static final int GL_POINTS = 0x0;
	public static final int GL_LINES = 0x1;
	public static final int GL_TRIANGLES = 0x4;
	public static final int GL_COLOR_MATERIAL = 0xb57;
	public static final int GL_QUADS = 0x0007;
	
	public void glLight(int light, int pname, float[] params);
	public void glEnable(int cap);
	public void glDisable(int cap);
	public void glShadeModel(int mode);
	public void glDepthFunc(int func);
	public void glClearColor(float red, float green, float blue, float alpha);
	public void glMatrixMode(int mode);
	public void glLoadIdentity();
	public void glFrustum(double left, double right, double bottom, double top, double zNear, double zFar);
	public void gluLookAt(float eyex, float eyey, float eyez, float centerx, float centery, float centerz, float upx, float upy, float upz);
	public void glViewport(int x, int y, int width, int height);
	public void glPushMatrix();
	public void glPopMatrix();
	public void gluOrtho2D(float left, float right, float bottom, float top);
	public void glScalef(float x, float y, float z);
	public void glTranslatef(float x, float y, float z);
	public void glColor3f(float red, float green, float blue);
	public void glClear(int mask);
	public void glBegin(int mode);
	public void glEnd();
	public void glVertex3f(float x, float y, float z);
	public void glLineWidth(float width);
	public void glPointSize(float size);
	public void glNormal3f(float nx, float ny, float nz);
	public void glMultMatrix(float[] m);
	
	public void drawCube(float extent);
	public void drawSphere(float radius, int slices, int stacks);
	public void drawCylinder(float radius, float halfHeight, int upAxis);

	public void drawString(CharSequence s, int x, int y, float red, float green, float blue);
	
}
