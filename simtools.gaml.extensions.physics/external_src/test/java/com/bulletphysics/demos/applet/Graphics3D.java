/*
 * Software OpenGL-like 3D renderer (c) 2008 Martin Dvorak <jezek2@advel.cz>
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

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 *
 * @author jezek2
 */
public class Graphics3D {

	public static final int COLOR_BUFFER = Rasterizer.COLOR_BUFFER;
	public static final int DEPTH_BUFFER = Rasterizer.DEPTH_BUFFER;
	
	public static final int TRIANGLES    = 1;
	public static final int TRIANGLE_FAN = 2;
	public static final int QUADS        = 3;
	public static final int QUAD_STRIP   = 4;
	public static final int LINES        = 5;
	
	private Rasterizer rasterizer = new Rasterizer();
	private Vector4f[] vertices = new Vector4f[64];
	private Vector3f[] normals = new Vector3f[64];
	private Color3f[] colors = new Color3f[64];

	private int clippedCount;
	private Vector4f[] clippedVertices = new Vector4f[64];
	private Color3f[] clippedColors = new Color3f[64];
	
	private int primitiveType, primitivePos;
	private Vector3f currentNormal = new Vector3f();
	private Color3f currentColor = new Color3f();
	
	private Matrix4f projMatrix = new Matrix4f();
	private Matrix4f viewMatrix = new Matrix4f();
	private Matrix4f mergedMatrix = new Matrix4f();
	private boolean matrixDirty = false;
	
	private Color3f clearColor = new Color3f();
	
	private Matrix4f[] viewMatStack = new Matrix4f[16];
	private int viewMatStackTop = 0;
	
	private boolean lightingEnabled = false;
	private Light[] lights = new Light[4];
	
	private Vector3f origColor = new Vector3f();
	private Vector3f tmpVec3 = new Vector3f();
	private Vector3f[] lightResult = new Vector3f[lights.length];

	public Graphics3D() {
		for (int i=0; i<vertices.length; i++) vertices[i] = new Vector4f();
		for (int i=0; i<normals.length; i++) normals[i] = new Vector3f();
		for (int i=0; i<colors.length; i++) colors[i] = new Color3f();

		for (int i=0; i<clippedVertices.length; i++) clippedVertices[i] = new Vector4f();
		for (int i=0; i<clippedColors.length; i++) clippedColors[i] = new Color3f();
		
		viewMatrix.setIdentity();
		
		for (int i=0; i<viewMatStack.length; i++) viewMatStack[i] = new Matrix4f();
		for (int i=0; i<lights.length; i++) lights[i] = new Light();
		for (int i=0; i<lightResult.length; i++) lightResult[i] = new Vector3f();
	}
	
	public void init(int[] pixels, int width, int height) {
		rasterizer.init(pixels, width, height);
	}

	public void setClearColor(float r, float g, float b) {
		this.clearColor.set(r, g, b);
	}
	
	public void clear(int bufferMask) {
		rasterizer.clear(bufferMask, clearColor);
	}
	
	public void begin(int type) {
		if (primitiveType != 0) throw new IllegalStateException();
		primitiveType = type;
		primitivePos = 0;
	}
	
	public void end() {
		switch (primitiveType) {
			case TRIANGLE_FAN:
				transform(0, primitivePos);
				drawPolygon(primitivePos);
				break;
		}
		
		primitiveType = 0;
	}
	
	public void setNormal(float x, float y, float z) {
		currentNormal.set(x, y, z);
	}
	
	public void setColor(float r, float g, float b) {
		currentColor.set(r, g, b);
	}
	
	public void addVertex(float x, float y, float z) {
		colors[primitivePos].set(currentColor);
		normals[primitivePos].set(currentNormal);
		vertices[primitivePos].set(x, y, z, 1f);
		primitivePos++;
		
		switch (primitiveType) {
			case TRIANGLES:
				if (primitivePos == 3) {
					transform(0, 3);
					drawPolygon(3);
					primitivePos = 0;
				}
				break;
				
			case QUADS:
				if (primitivePos == 4) {
					transform(0, 4);
					drawPolygon(4);
					primitivePos = 0;
				}
				break;
				
			case QUAD_STRIP:
				if (primitivePos == 2) {
					transform(0, 2);
				}
				
				if (primitivePos == 3) {
					transform(2, 3);
					drawPolygon(3);
					shift(1, 3, 1);
					primitivePos = 2;
				}
				break;
				
			case LINES:
				if (primitivePos == 2) {
					transform(0, 2);
					drawLine();
					primitivePos = 0;
				}
				break;
		}
	}
	
	private void shift(int start, int count, int amount) {
		for (int i=start; i<count; i++) {
			vertices[i-amount].set(vertices[i]);
			normals[i-amount].set(normals[i]);
			colors[i-amount].set(colors[i]);
		}
	}

	public void setProjMatrix(Matrix4f mat) {
		projMatrix.set(mat);
		matrixDirty = true;
	}

	public void mulProjMatrix(Matrix4f mat) {
		projMatrix.mul(mat);
		matrixDirty = true;
	}
	
	public void setViewMatrix(Matrix4f mat) {
		viewMatrix.set(mat);
		matrixDirty = true;
	}

	public void mulViewMatrix(Matrix4f mat) {
		viewMatrix.mul(mat);
		matrixDirty = true;
	}

	public void pushViewMatrix() {
		viewMatStack[viewMatStackTop++].set(viewMatrix);
	}
	
	public void pushViewMatrix(Matrix4f mat, boolean multiply) {
		viewMatStack[viewMatStackTop++].set(viewMatrix);
		if (multiply) {
			viewMatrix.mul(mat);
		}
		else {
			viewMatrix.set(mat);
		}
		matrixDirty = true;
	}
	
	public void popViewMatrix() {
		viewMatrix.set(viewMatStack[--viewMatStackTop]);
		matrixDirty = true;
	}
	
	public void flush() {
	}

	public void setLightingEnabled(boolean b) {
		lightingEnabled = b;
	}

	public Light getLight(int num) {
		return lights[num];
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	private void updateMatrix() {
		mergedMatrix.mul(projMatrix, viewMatrix);
		matrixDirty = false;
	}
	
	private void transform(int start, int num) {
		if (matrixDirty) {
			updateMatrix();
		}
		
		for (int i=start; i<num; i++) {
			mergedMatrix.transform(vertices[i]);
			mergedMatrix.transform(normals[i]);
			normals[i].normalize();
			
			if (lightingEnabled && primitiveType != LINES) {
				for (int j=0; j<lights.length; j++) {
					Light l = lights[j];
					if (!l.enabled) continue;

					lightResult[j].set(l.ambient.x, l.ambient.y, l.ambient.z);
					tmpVec3.set(l.position.x, l.position.y, l.position.z);
					float dot = normals[i].dot(tmpVec3);
					if (dot < 0f) dot = 0f;
					tmpVec3.set(l.diffuse.x, l.diffuse.y, l.diffuse.z);
					lightResult[j].scaleAdd(dot, tmpVec3, lightResult[j]);
					lightResult[j].x = lightResult[j].x * colors[i].x;
					lightResult[j].y = lightResult[j].y * colors[i].y;
					lightResult[j].z = lightResult[j].z * colors[i].z;
				}

				colors[i].set(0f, 0f, 0f);
				for (int j=0; j<lights.length; j++) {
					if (!lights[j].enabled) continue;

					colors[i].add(lightResult[j]);
				}

				clamp(colors[i], 0f, 1f);
			}
		}
	}
	
	private void clamp(Tuple3f t, float min, float max) {
		t.x = Math.min(Math.max(min, t.x), max);
		t.y = Math.min(Math.max(min, t.y), max);
		t.z = Math.min(Math.max(min, t.z), max);
	}
	
	private void drawPolygon(int num) {
		Boolean needsClip = clip(num);
		if (needsClip == null) return;
		
		rasterizer.drawPolygon(clippedCount, clippedVertices, needsClip? clippedColors : colors);
	}
	
	private void drawLine() {
		Boolean needsClip = clip(2);
		if (needsClip == null) return;
		
		rasterizer.drawLine(clippedVertices, needsClip? clippedColors : colors);
	}
	
	private Boolean clip(int num) {
		boolean needsClip = false, visible = false;
		for (int i=0; i<num; i++) {
			if (vertices[i].z < 0f) {
				needsClip = true;
			}
			else {
				visible = true;
			}
		}
		
		if (!visible) {
			return null;
		}
		
		if (needsClip) {
			clippedCount = 0;
			for (int i=0; i<num; i++) {
				clipEdge(i, (i+1)%num);
			}
		}
		else {
			clippedCount = num;
			for (int i=0; i<num; i++) {
				clippedVertices[i].set(vertices[i]);
			}
		}

		for (int i=0; i<clippedCount; i++) {
			clippedVertices[i].scale(1f / clippedVertices[i].w);
			
			clippedVertices[i].x = (clippedVertices[i].x+1f) * rasterizer.getWidth() * 0.5f;
			clippedVertices[i].y = rasterizer.getHeight() - 1 - (clippedVertices[i].y+1f) * rasterizer.getHeight() * 0.5f;
		}
		
		return needsClip;
	}

	private void clipEdge(int v1, int v2) {
		Vector4f vtx1 = vertices[v1];
		Vector4f vtx2 = vertices[v2];
		
		Color3f c1 = colors[v1];
		Color3f c2 = colors[v2];
		
		float minZ = 0f;
		float m = 1f;
		
		float dold = vtx2.z - vtx1.z;
		float dnew = minZ - vtx1.z;
		if (dold != 0f) {
			m = dnew / dold;
		}
		
		if (vtx1.z >= minZ && vtx2.z >= minZ) {
			clippedVertices[clippedCount].set(vtx2);
			clippedColors[clippedCount].set(c2);
			clippedCount++;
		}
		else if (vtx1.z >= minZ && vtx2.z < minZ) {
			clippedVertices[clippedCount].x = vtx1.x + (vtx2.x - vtx1.x)*m;
			clippedVertices[clippedCount].y = vtx1.y + (vtx2.y - vtx1.y)*m;
			clippedVertices[clippedCount].z = vtx1.z + (vtx2.z - vtx1.z)*m;
			clippedVertices[clippedCount].w = vtx1.w + (vtx2.w - vtx1.w)*m;
			clippedColors[clippedCount].x = c1.x + (c2.x - c1.x)*m;
			clippedColors[clippedCount].y = c1.y + (c2.y - c1.y)*m;
			clippedColors[clippedCount].z = c1.z + (c2.z - c1.z)*m;
			clippedCount++;
		}
		else if (vtx1.z < minZ && vtx2.z >= minZ) {
			clippedVertices[clippedCount].x = vtx1.x + (vtx2.x - vtx1.x)*m;
			clippedVertices[clippedCount].y = vtx1.y + (vtx2.y - vtx1.y)*m;
			clippedVertices[clippedCount].z = vtx1.z + (vtx2.z - vtx1.z)*m;
			clippedVertices[clippedCount].w = vtx1.w + (vtx2.w - vtx1.w)*m;
			clippedColors[clippedCount].x = c1.x + (c2.x - c1.x)*m;
			clippedColors[clippedCount].y = c1.y + (c2.y - c1.y)*m;
			clippedColors[clippedCount].z = c1.z + (c2.z - c1.z)*m;
			clippedCount++;
			clippedVertices[clippedCount].set(vtx2);
			clippedColors[clippedCount].set(c2);
			clippedCount++;
		}
	}
	
}
