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

import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4f;

/**
 *
 * @author jezek2
 */
public class Rasterizer {
	
	public static final int COLOR_BUFFER = 1 << 0;
	public static final int DEPTH_BUFFER = 1 << 1;
	
	private int[] pixels;
	private float[] zbuffer;
	private int width;
	private int height;
	
	private int minY, maxY;
	private Span[] spans;
	
	public void init(int[] pixels, int width, int height) {
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		
		spans = new Span[height];
		for (int i=0; i<height; i++) {
			spans[i] = new Span();
		}
		
		zbuffer = new float[width*height];
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void clear(int bufferMask, Tuple3f color) {
		if ((bufferMask & COLOR_BUFFER) != 0) {
			int r = (int)(color.x*255f);
			int g = (int)(color.y*255f);
			int b = (int)(color.z*255f);
			int c = (r << 16) | (g << 8) | b;
			for (int i=0; i<zbuffer.length; i++) {
				pixels[i] = c;
			}
		}

		if ((bufferMask & DEPTH_BUFFER) != 0) {
			for (int i=0; i<zbuffer.length; i++) {
				zbuffer[i] = Float.MAX_VALUE;
			}
		}
	}

	public void drawPolygon(int num, Tuple4f[] vertices, Tuple3f[] colors) {
		minY = Short.MAX_VALUE;
		maxY = Short.MIN_VALUE;

		for (int i=0; i<height; i++) {
			spans[i].x1 = Short.MAX_VALUE;
			spans[i].x2 = Short.MIN_VALUE;
		}
		
		for (int i=0; i<num; i++) {
			int j = (i+1)%num;
			scanEdge(vertices[i], vertices[j], colors[i], colors[j]);
		}
		
		for (int i=minY; i<maxY; i++) {
			int x1 = spans[i].x1;
			int x2 = spans[i].x2;
			
			if (x1 >= width) continue;
			if (x2 < 0) continue;
			
			if (x1 < 0) {
				float m = (float)(-x1) / (float)(x2 - x1);
				spans[i].c1r = (short)(spans[i].c1r + (spans[i].c2r - spans[i].c1r) * m);
				spans[i].c1g = (short)(spans[i].c1g + (spans[i].c2g - spans[i].c1g) * m);
				spans[i].c1b = (short)(spans[i].c1b + (spans[i].c2b - spans[i].c1b) * m);
				x1 = 0;
			}
			if (x2 >= width) {
				float m = (float)((width - 1) - x1) / (float)(x2 - x1);
				spans[i].c2r = (short)((float)spans[i].c1r + (spans[i].c2r - spans[i].c1r) * m);
				spans[i].c2g = (short)((float)spans[i].c1g + (spans[i].c2g - spans[i].c1g) * m);
				spans[i].c2b = (short)((float)spans[i].c1b + (spans[i].c2b - spans[i].c1b) * m);
				x2 = width - 1;
			}
			
			int pos = i*width;
			
			Span span = spans[i];
			
			float z1 = (float)span.z1;
			float z2 = (float)span.z2;

			float r1 = (float)span.c1r;
			float g1 = (float)span.c1g;
			float b1 = (float)span.c1b;
			float r2 = (float)span.c2r;
			float g2 = (float)span.c2g;
			float b2 = (float)span.c2b;

			for (int j=x1; j<=x2; j++) {
				float a = (float)(j - x1) / (float)(x2 - x1 + 1);
				float z = z1*(1f-a) + z2*a;

				if (z > zbuffer[pos+j]) {
					continue;
				}
				else {
					zbuffer[pos+j] = z;
				}

				float r = r1*(1f-a) + r2*a;
				float g = g1*(1f-a) + g2*a;
				float b = b1*(1f-a) + b2*a;
				int ir = ((int)r) & 0xFF;
				int ig = ((int)g) & 0xFF;
				int ib = ((int)b) & 0xFF;
				pixels[pos+j] = (ir << 16) | (ig << 8) | ib;
			}
		}
	}
	
	private void scanEdge(Tuple4f v1, Tuple4f v2, Tuple3f c1, Tuple3f c2) {
		Tuple4f first, last;
		Tuple3f cfirst, clast;
		
		if (v1.y < v2.y) {
			first = v1;
			last = v2;
			cfirst = c1;
			clast = c2;
		}
		else {
			first = v2;
			last = v1;
			cfirst = c2;
			clast = c1;
		}
		
		float y1 = (int)first.y;
		float y2 = (int)last.y;
		
		if (y1 >= height) return;
		if (y2 < 0) return;
		if (y2 >= height) y2 = height - 1;
		
		float slopeX, x;
		float slopeZ, z;
		float slopeCR, slopeCG, slopeCB, cr, cg, cb;
		
		float invSub = 1f / (last.y - first.y);
		
		x = first.x;
		slopeX = (last.x - first.x) * invSub;
		
		z = first.z;
		slopeZ = (last.z - first.z) * invSub;
		
		cr = cfirst.x;
		cg = cfirst.y;
		cb = cfirst.z;
		slopeCR = (clast.x - cfirst.x) * invSub;
		slopeCG = (clast.y - cfirst.y) * invSub;
		slopeCB = (clast.z - cfirst.z) * invSub;
		
		if (y1 < 0) {
			x += slopeX * (-y1);
			z += slopeZ * (-y1);
			cr += slopeCR * (-y1);
			cg += slopeCG * (-y1);
			cb += slopeCB * (-y1);
			y1 = 0;
		}
		
		if (y1 < minY) minY = (int)y1;
		if (y2 > maxY) maxY = (int)y2;
		
		for (int i=(int)y1; i<(int)y2; i++) {
			int sx = (int)x;
			int scr = (int)(cr*255f);
			int scg = (int)(cg*255f);
			int scb = (int)(cb*255f);
			
			if (sx < spans[i].x1) {
				spans[i].x1 = sx;
				spans[i].z1 = z;
				spans[i].c1r = (short)scr;
				spans[i].c1g = (short)scg;
				spans[i].c1b = (short)scb;
			}
			if (sx > spans[i].x2) {
				spans[i].x2 = sx;
				spans[i].z2 = z;
				spans[i].c2r = (short)scr;
				spans[i].c2g = (short)scg;
				spans[i].c2b = (short)scb;
			}
			x += slopeX;
			z += slopeZ;
			cr += slopeCR;
			cg += slopeCG;
			cb += slopeCB;
		}
	}
	
	private static final int EDGE_LEFT   = 1 << 0;
	private static final int EDGE_RIGHT  = 1 << 1;
	private static final int EDGE_TOP    = 1 << 2;
	private static final int EDGE_BOTTOM = 1 << 3;
	
	public void drawLine(Tuple4f[] vertices, Tuple3f[] colors) {
		float x1 = vertices[0].x + 0.5f;
		float y1 = vertices[0].y + 0.5f;
		float z1 = vertices[0].z;

		float x2 = vertices[1].x + 0.5f;
		float y2 = vertices[1].y + 0.5f;
		float z2 = vertices[1].z;
		
		while (true) {
			int outcode1 = 0;
			if (x1 < 0) outcode1 |= EDGE_LEFT;
			else if (x1 >= width) outcode1 |= EDGE_RIGHT;
			if (y1 < 0) outcode1 |= EDGE_TOP;
			else if (y1 >= height) outcode1 |= EDGE_BOTTOM;

			int outcode2 = 0;
			if (x2 < 0) outcode2 |= EDGE_LEFT;
			else if (x2 >= width) outcode2 |= EDGE_RIGHT;
			if (y2 < 0) outcode2 |= EDGE_TOP;
			else if (y2 >= height) outcode2 |= EDGE_BOTTOM;
			
			if (outcode1 == 0 && outcode2 == 0) {
				break;
			}
			else if ((outcode1 & outcode2) != 0) {
				return;
			}
			
			int outcode = (outcode1 != 0)? outcode1 : outcode2;
			float x, y, z;
			
			if ((outcode & EDGE_LEFT) != 0) {
				float m = (0 - x1) / (x2 - x1);
				x = 0f;
				y = y1 + (y2 - y1) * m;
				z = z1 + (z2 - z1) * m;
			}
			else if ((outcode & EDGE_RIGHT) != 0) {
				float m = ((width-1) - x1) / (x2 - x1);
				x = width - 1;
				y = y1 + (y2 - y1) * m;
				z = z1 + (z2 - z1) * m;
			}
			else if ((outcode & EDGE_TOP) != 0) {
				float m = (0 - y1) / (y2 - y1);
				x = x1 + (x2 - x1) * m;
				y = 0f;
				z = z1 + (z2 - z1) * m;
			}
			else/* if ((outcode & EDGE_BOTTOM) != 0)*/ {
				float m = ((height-1) - y1) / (y2 - y1);
				x = x1 + (x2 - x1) * m;
				y = height - 1;
				z = z1 + (z2 - z1) * m;
			}
			
			if (outcode == outcode1) {
				x1 = x;
				y1 = y;
				z1 = z;
			}
			else {
				x2 = x;
				y2 = y;
				z2 = z;
			}
		}

		int color = ((int)(colors[0].x * 255f) << 16) | ((int)(colors[0].y * 255f) << 8) | (int)(colors[0].z * 255f);
		lineBresenham((int)x1, (int)y1, (int)x2, (int)y2, z1, z2, color);
	}
	
	// http://cs.unc.edu/~mcmillan/comp136/Lecture6/Lines.html
	private void lineBresenham(int x0, int y0, int x1, int y1, float z1, float z2, int color) {
		int dy = y1 - y0;
		int dx = x1 - x0;
		int stepx, stepy;
		int pos;

		if (dy < 0) { dy = -dy;  stepy = -1; } else { stepy = 1; }
		if (dx < 0) { dx = -dx;  stepx = -1; } else { stepx = 1; }
		dy <<= 1;                                                  // dy is now 2*dy
		dx <<= 1;                                                  // dx is now 2*dx

		pos = y0*width+x0;
		if (z1 <= zbuffer[pos]) {
			zbuffer[pos] = z1;
			pixels[pos] = color;
		}
		
		if (dx > dy) {
			int fraction = dy - (dx >> 1);                         // same as 2*dy - dx
			float dz = (z2 - z1) / dx, z = z1 + dz;
			while (x0 != x1) {
				if (fraction >= 0) {
					y0 += stepy;
					fraction -= dx;                                // same as fraction -= 2*dx
				}
				x0 += stepx;
				fraction += dy;                                    // same as fraction -= 2*dy
				
				pos = y0*width+x0;
				if (z <= zbuffer[pos]) {
					zbuffer[pos] = z;
					pixels[pos] = color;
				}
				z += dz;
			}
		} else {
			int fraction = dx - (dy >> 1);
			float dz = (z2 - z1) / dy, z = z1 + dz;
			while (y0 != y1) {
				if (fraction >= 0) {
					x0 += stepx;
					fraction -= dy;
				}
				y0 += stepy;
				fraction += dx;
				
				pos = y0*width+x0;
				if (z <= zbuffer[pos]) {
					zbuffer[pos] = z;
					pixels[pos] = color;
				}
				z += dz;
			}
		}
	}
	
}
