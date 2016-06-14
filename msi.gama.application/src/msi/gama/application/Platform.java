/* $Id: Platform.java,v 1.6 2008/04/28 17:17:13 bhaible Exp $ */
/*******************************************************************************
 * Copyright (c) 2007-2008 SAS Institute Inc., ILOG S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * SAS Institute Inc. - initial API and implementation
 * ILOG S.A. - initial API and implementation
 * IBM Corporation - Java/SWT versioning code (from org.eclipse.swt.internal.Library)
 *******************************************************************************/
package msi.gama.application;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.Library;

public class Platform {

	private static String platformString = SWT.getPlatform();

	private Platform() {
		// prevent instantiation
	}

	// Window System
	public static boolean isWin32() {
		return "win32".equals(platformString); //$NON-NLS-1$
	}

	public static boolean isGtk() {
		return "gtk".equals(platformString); //$NON-NLS-1$
	}

	public static boolean isMotif() {
		return "motif".equals(platformString); //$NON-NLS-1$
	}

	public static boolean isCarbon() {
		return "carbon".equals(platformString); //$NON-NLS-1$
	}

	public static boolean isCocoa() {
		return "cocoa".equals(platformString);
	}

	// Java
	/**
	 * The JAVA version
	 */
	public static final int JAVA_VERSION;
	static {
		JAVA_VERSION = parseVersion(System.getProperty("java.version")); //$NON-NLS-1$
	}

	static int parseVersion(final String version) {
		if ( version == null ) { return 0; }
		int major = 0, minor = 0, micro = 0;
		final int length = version.length();
		int index = 0, start = 0;
		while (index < length && Character.isDigit(version.charAt(index))) {
			index++;
		}
		try {
			if ( start < length ) {
				major = Integer.parseInt(version.substring(start, index));
			}
		} catch (final NumberFormatException e) {}
		start = ++index;
		while (index < length && Character.isDigit(version.charAt(index))) {
			index++;
		}
		try {
			if ( start < length ) {
				minor = Integer.parseInt(version.substring(start, index));
			}
		} catch (final NumberFormatException e) {}
		start = ++index;
		while (index < length && Character.isDigit(version.charAt(index))) {
			index++;
		}
		try {
			if ( start < length ) {
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

	// SWT
	// It seems necessary to use private API to get this value. Provide delegating methods here so that
	// the internal dependency is localized.
	public static final int SWT_VERSION = Library.SWT_VERSION;
	// For the SWT version numbers, look at
	// <http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt/Eclipse SWT PI/common_j2me/org/eclipse/swt/internal/Library.java?view=log>
	public static final int SWT_33 = swtVersion(3, 346);
	public static final int SWT_FIX216431 = swtVersion(3, 426); // between 3.4M4 and 3.4M5
	// public static final int SWT_34 = swtVersion(3, 4??);

	private static int swtVersion(final int major, final int minor) {
		return Library.SWT_VERSION(major, minor);
	}
}
