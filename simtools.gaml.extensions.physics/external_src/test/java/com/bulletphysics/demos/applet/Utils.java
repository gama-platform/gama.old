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

/* 
 * Marked methods are from LWJGL library with following license:
 *
 * Copyright (c) 2002-2004 LWJGL Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are 
 * met:
 * 
 * * Redistributions of source code must retain the above copyright 
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of 
 *   its contributors may be used to endorse or promote products derived 
 *   from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.bulletphysics.demos.applet;

import java.awt.Rectangle;
import javax.vecmath.*;

/**
 *
 * @author jezek2
 */
public class Utils {

	private Utils() {}

	public static void setFrustum(Matrix4f mat, float left, float right, float bottom, float top, float near, float far) {
		mat.setZero();
		mat.m00 = 2f*near / (right - left);
		mat.m11 = 2f*near / (top - bottom);
		mat.m32 = -1f;
		mat.m02 = (right+left) / (right - left);
		mat.m12 = (top+bottom) / (top - bottom);
		mat.m22 = - (far + near) / (far - near);
		mat.m23 = - (2f * far * near) / (far - near);
	}
	
	// from LWJGL:
	public static void setPerspective(Matrix4f mat, float fovy, float aspect, float zNear, float zFar) {
		float sine, cotangent, deltaZ;
		float radians = fovy / 2 * (float)Math.PI / 180;

		deltaZ = zFar - zNear;
		sine = (float) Math.sin(radians);

		if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
			return;
		}

		cotangent = (float) Math.cos(radians) / sine;

		mat.setIdentity();
		mat.m00 = cotangent / aspect;
		mat.m11 = cotangent;
		mat.m22 = - (zFar + zNear) / deltaZ;
		mat.m32 = -1;
		mat.m23 = -2 * zNear * zFar / deltaZ;
		mat.m33 = 0;
	}
	
	public static void mulPerspective(Matrix4f mat, float fovy, float aspect, float zNear, float zFar) {
		Matrix4f tmpMat = new Matrix4f();
		setPerspective(tmpMat, fovy, aspect, zNear, zFar);
		mat.mul(tmpMat);
	}
	
	public static void setOrtho(Matrix4f mat, float left, float right, float bottom, float top, float near, float far) {
		mat.setIdentity();
		mat.m00 = 2f / (right - left);
		mat.m11 = 2f / (top - bottom);
		mat.m22 = -2f / (far - near);
		mat.m03 = -(right + left) / (right - left);
		mat.m13 = -(top + bottom) / (top - bottom);
		mat.m23 = -(far + near) / (far - near);
	}
	
	public static void mulOrtho(Matrix4f mat, float left, float right, float bottom, float top, float near, float far) {
		Matrix4f tmpMat = new Matrix4f();
		setOrtho(tmpMat, left, right, bottom, top, near, far);
		mat.mul(tmpMat);
	}
	
	public static void translate(Matrix4f mat, float x, float y, float z) {
		Matrix4f tmpMat = new Matrix4f();
		tmpMat.setIdentity();
		tmpMat.m03 = x;
		tmpMat.m13 = y;
		tmpMat.m23 = z;
		mat.mul(tmpMat);
	}
	
	public static void scale(Matrix4f mat, float x, float y, float z) {
		Matrix4f tmpMat = new Matrix4f();
		tmpMat.setIdentity();
		tmpMat.m00 = x;
		tmpMat.m11 = y;
		tmpMat.m22 = z;
		mat.mul(tmpMat);
	}

	// from LWJGL:
	public static void setLookAt(
		Matrix4f mat,
		float eyex,
		float eyey,
		float eyez,
		float centerx,
		float centery,
		float centerz,
		float upx,
		float upy,
		float upz) {

		Vector3f forward = new Vector3f();
		Vector3f side = new Vector3f();
		Vector3f up = new Vector3f();
		
		forward.x = centerx - eyex;
		forward.y = centery - eyey;
		forward.z = centerz - eyez;

		up.x = upx;
		up.y = upy;
		up.z = upz;

		forward.normalize();

		/* Side = forward x up */
		side.cross(forward, up);
		side.normalize();

		/* Recompute up as: up = side x forward */
		up.cross(side, forward);

		mat.setIdentity();
		mat.m00 = side.x;
		mat.m01 = side.y;
		mat.m02 = side.z;

		mat.m10 = up.x;
		mat.m11 = up.y;
		mat.m12 = up.z;

		mat.m20 = -forward.x;
		mat.m21 = -forward.y;
		mat.m22 = -forward.z;

		Utils.translate(mat, -eyex, -eyey, -eyez);
	}
	
	// from LWJGL:
	public static void mulPickMatrix(
		Matrix4f mat,
		float x,
		float y,
		float deltaX,
		float deltaY,
		Rectangle viewport) {
		if (deltaX <= 0 || deltaY <= 0) {
			return;
		}

		/* Translate and scale the picked region to the entire window */
		translate(mat,
			(viewport.width - 2 * (x - viewport.x)) / deltaX,
			(viewport.height - 2 * (y - viewport.y)) / deltaY,
			0);
		scale(mat, viewport.width / deltaX, viewport.height / deltaY, 1.0f);
	}
	
	// from LWJGL:
	private static void __gluMultMatrixVecf(Matrix4f mat, Tuple4f in, Tuple4f out) {
		out.x = in.x*mat.m00 + in.y*mat.m01 + in.z*mat.m02 + in.w*mat.m03;
		out.y = in.x*mat.m10 + in.y*mat.m11 + in.z*mat.m12 + in.w*mat.m13;
		out.z = in.x*mat.m20 + in.y*mat.m21 + in.z*mat.m22 + in.w*mat.m23;
		out.w = in.x*mat.m30 + in.y*mat.m31 + in.z*mat.m32 + in.w*mat.m33;
	}
	
	// from LWJGL:
	public static boolean project(
		float objx,
		float objy,
		float objz,
		Matrix4f modelMatrix,
		Matrix4f projMatrix,
		Rectangle viewport,
		Tuple3f win_pos) {

		Vector4f in = new Vector4f();
		Vector4f out = new Vector4f();

		in.x = objx;
		in.y = objy;
		in.z = objz;
		in.w = 1.0f;

		__gluMultMatrixVecf(modelMatrix, in, out);
		__gluMultMatrixVecf(projMatrix, out, in);

		if (in.w == 0.0)
			return false;

		in.w = (1.0f / in.w) * 0.5f;

		// Map x, y and z to range 0-1
		in.x = in.x * in.w + 0.5f;
		in.y = in.y * in.w + 0.5f;
		in.z = in.z * in.w + 0.5f;

		// Map x,y to viewport
		win_pos.x = in.x * viewport.width + viewport.x;
		win_pos.y = in.y * viewport.height + viewport.y;
		win_pos.z = in.z;

		return true;
	}
	
	// from LWJGL:
	public static boolean unproject(
		float winx,
		float winy,
		float winz,
		Matrix4f modelMatrix,
		Matrix4f projMatrix,
		Rectangle viewport,
		Tuple3f obj_pos) {
		
		Vector4f in = new Vector4f();
		Vector4f out = new Vector4f();
		Matrix4f finalMatrix = new Matrix4f();
		
		finalMatrix.mul(projMatrix, modelMatrix);

		try {
			finalMatrix.invert();
		}
		catch (SingularMatrixException e) {
			return false;
		}

		in.x = winx;
		in.y = winy;
		in.z = winz;
		in.w = 1.0f;

		// Map x and y from window coordinates
		in.x = (in.x - viewport.x) / viewport.width;
		in.y = (in.y - viewport.y) / viewport.height;

		// Map to range -1 to 1
		in.x = in.x * 2 - 1;
		in.y = in.y * 2 - 1;
		in.z = in.z * 2 - 1;

		__gluMultMatrixVecf(finalMatrix, in, out);

		if (out.w == 0.0)
			return false;

		out.w = 1.0f / out.w;

		obj_pos.x = out.x * out.w;
		obj_pos.y = out.y * out.w;
		obj_pos.z = out.z * out.w;

		return true;
	}
	
}
