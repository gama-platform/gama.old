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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import javax.swing.UIManager;

/**
 *
 * @author jezek2
 */
public class LookUtil {
	
	private static Object oldAA = null;
	
	public static void pushAntialias(Graphics2D g2) {
		oldAA = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}
	
	public static void popAntialias(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
		oldAA = null;
	}
	
	public static boolean isXP() {
		if (isWindows()) {
			Boolean b = (Boolean)Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive");
			if (b != null && b) return true;
		}
		
		return false;
	}
	
	public static boolean isWindows() {
		Object laf = UIManager.getLookAndFeel();
		if (laf.getClass().getName().equals("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
			return true;
		}
		return false;
	}
	
}
