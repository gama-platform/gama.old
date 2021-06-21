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
public class FastFormat {
	
	private static final char[] DIGITS = "0123456789".toCharArray();

	public static void append(StringBuilder b, int i) {
		if (i < 0) {
			b.append('-');
			i = Math.abs(i);
		}
		
		int digit = 1000000000;
		boolean first = true;
		while (digit >= 1) {
			int v = (i/digit);
			if (v != 0 || !first) {
				b.append(DIGITS[v]);
				first = false;
			}
			i -= v*digit;
			digit /= 10;
		}
		
		if (first) b.append('0');
	}

	public static void append(StringBuilder b, float f) {
		append(b, f, 2);
	}

	public static void append(StringBuilder b, float f, int fracDigits) {
		int mult = 10*fracDigits;
		int val = Math.round(f*mult);
		append(b, val / mult);
		b.append('.');
		append(b, Math.abs(val % mult));
	}
	
}
