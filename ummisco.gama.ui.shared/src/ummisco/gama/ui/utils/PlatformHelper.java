/*********************************************************************************************
 *
 * 'PlatformHelper.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
/*******************************************************************************
 * Copyright (c) 2007-2008 SAS Institute Inc., ILOG S.A. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: SAS Institute Inc. - initial API and implementation ILOG S.A. - initial API and implementation IBM
 * Corporation - Java/SWT versioning code (from org.eclipse.swt.internal.Library)
 *******************************************************************************/
package ummisco.gama.ui.utils;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.DPIUtil;
import  org.eclipse.swt.events.MouseEvent;

public class PlatformHelper extends DPIUtil {

	private static String platformString = SWT.getPlatform();
	private static boolean isWindows = "win32".equals(platformString);
	private static boolean isMac = "cocoa".equals(platformString) || "carbon".equals(platformString);
	private static boolean isLinux = "gtk".equals(platformString);
	private static boolean isHiDPI = DPIUtil.getDeviceZoom() > 100;
	
	private static Boolean isDeveloper;

	private PlatformHelper() {}
	
	public static boolean isHiDPI() {
		return isHiDPI;
	}

	public static boolean isWindows() {
		return isWindows;
	}

	public static boolean isLinux() {
		return isLinux;
	}

	public static boolean isMac() {
		return isMac;
	}

	public static boolean isDeveloper() {
		if (isDeveloper == null) {
			isDeveloper = Platform.getInstallLocation() == null
					|| Platform.getInstallLocation().getURL().getPath().contains("org.eclipse.pde.core");
		}
		return isDeveloper;
	}
	
	public static int scaleDownIfWin(int size) {
		return isWindows ? autoScaleDown(size): size;
	}
	
	public static int scaleDownIfMac(int size) {
		return isMac ? autoScaleDown(size): size;
	}
	
	public static int scaleDownIfLinux(int size) {
		return isLinux ? autoScaleDown(size): size;
	}
	
	public static float scaleUpIfWin(float size) {
		return isWindows ? autoScaleUp(size): size;
	}
	
	public static int scaleUpIfWin(int size) {
		return isWindows ? autoScaleUp(size): size;
	}
	
	public static int scaleUpIfMac(int size) {
		return isMac ? autoScaleUp(size): size;
	}
	
	public static int scaleUpIfLinux(int size) {
		return isLinux ? autoScaleUp(size): size;
	}
	
	public static Point scaleDownIfWin(Point size) {
		return isWindows ? autoScaleDown(size): size;
	}
	
	public static Point scaleDownIfMac(Point size) {
		return isMac ? autoScaleDown(size): size;
	}
	
	public static Point scaleDownIfLinux(Point size) {
		return isLinux ? autoScaleDown(size): size;
	}
	
	public static Point scaleUpIfWin(Point size) {
		return isWindows ? autoScaleUp(size): size;
	}
	
	public static Point scaleUpIfMac(Point size) {
		return isMac ? autoScaleUp(size): size;
	}
	
	public static Point scaleUpIfLinux(Point size) {
		return isLinux ? autoScaleUp(size): size;
	}
	
	

	
	private static MouseEvent autoScaleDown(MouseEvent e) {
		e.x = autoScaleDown(e.x);
		e.y = autoScaleDown(e.y);
		return e;
	}
	
	private static MouseEvent autoScaleUp(MouseEvent e) {
		e.x = autoScaleDown(e.x);
		e.y = autoScaleDown(e.y);
		return e;
	}
	
	public static MouseEvent scaleDownIfWin(MouseEvent size) {
		return isWindows ? autoScaleDown(size): size;
	}

	public static MouseEvent scaleDownIfMac(MouseEvent size) {
		return isMac ? autoScaleDown(size): size;
	}
	
	public static MouseEvent scaleDownIfLinux(MouseEvent size) {
		return isLinux ? autoScaleDown(size): size;
	}
	
	public static MouseEvent scaleUpIfWin(MouseEvent size) {
		return isWindows ? autoScaleUp(size): size;
	}
	
	public static MouseEvent scaleUpIfMac(MouseEvent size) {
		return isMac ? autoScaleUp(size): size;
	}
	
	public static MouseEvent scaleUpIfLinux(MouseEvent size) {
		return isLinux ? autoScaleUp(size): size;
	}

	/**
	 * The JAVA version
	 */
	public static final int JAVA_VERSION;
	static {
		JAVA_VERSION = parseVersion(System.getProperty("java.version")); //$NON-NLS-1$
	}

	static int parseVersion(final String version) {
		if (version == null) { return 0; }
		int major = 0, minor = 0, micro = 0;
		final int length = version.length();
		int index = 0, start = 0;
		while (index < length && Character.isDigit(version.charAt(index))) {
			index++;
		}
		try {
			if (start < length) {
				major = Integer.parseInt(version.substring(start, index));
			}
		} catch (final NumberFormatException e) {}
		start = ++index;
		while (index < length && Character.isDigit(version.charAt(index))) {
			index++;
		}
		try {
			if (start < length) {
				minor = Integer.parseInt(version.substring(start, index));
			}
		} catch (final NumberFormatException e) {}
		start = ++index;
		while (index < length && Character.isDigit(version.charAt(index))) {
			index++;
		}
		try {
			if (start < length) {
				micro = Integer.parseInt(version.substring(start, index));
			}
		} catch (final NumberFormatException e) {}
		return javaVersion(major, minor, micro);
	}

	/**
	 * Returns the Java version number as an integer.
	 * 
	 * @param major
	 * @param minor
	 * @param micro
	 * @return the version
	 */
	public static int javaVersion(final int major, final int minor, final int micro) {
		return (major << 16) + (minor << 8) + micro;
	}



}
