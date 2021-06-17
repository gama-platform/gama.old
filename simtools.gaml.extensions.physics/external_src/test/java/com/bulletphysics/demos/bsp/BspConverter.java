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

package com.bulletphysics.demos.bsp;

import com.bulletphysics.util.ObjectArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.vecmath.Vector3f;

/**
 *
 * @author jezek2
 */
public abstract class BspConverter {

	public void convertBsp(InputStream in) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String s;
		
		ObjectArrayList<Vector3f> vertices = new ObjectArrayList<Vector3f>();
		while ((s = r.readLine()) != null) {
			int count = Integer.parseInt(s);
			vertices.clear();
			for (int i=0; i<count; i++) {
				String[] c = r.readLine().split(" ");
				vertices.add(new Vector3f(
					Float.parseFloat(c[0]),
					Float.parseFloat(c[1]),
					Float.parseFloat(c[2])
				));
			}
			addConvexVerticesCollider(vertices);
		}
		r.close();
	}
	
	public abstract void addConvexVerticesCollider(ObjectArrayList<Vector3f> vertices);
	
}
