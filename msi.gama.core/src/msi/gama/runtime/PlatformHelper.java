/*******************************************************************************************************
 *
 * PlatformHelper.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
/*******************************************************************************
 * Copyright (c) 2007-2008 SAS Institute Inc., ILOG S.A. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: SAS Institute Inc. - initial API and implementation ILOG S.A. - initial API and implementation IBM
 * Corporation - Java/SWT versioning code (from org.eclipse.swt.internal.Library)
 *******************************************************************************/
package msi.gama.runtime;

import org.eclipse.core.runtime.Platform;

/**
 * The Class PlatformHelper.
 */
public class PlatformHelper {

	/** The platform string. */
	private static String platformString = Platform.getOS();

	/** The is windows. */
	private static boolean isWindows = "win32".equals(PlatformHelper.platformString);

	/** The is mac. */
	private static boolean isMac = "macosx".equals(PlatformHelper.platformString);

	/** The is linux. */
	private static boolean isLinux = "linux".equals(PlatformHelper.platformString);

	private static boolean isARM = Platform.ARCH_AARCH64.equals(Platform.getOSArch());

	/** The is developer. */
	private static volatile Boolean isDeveloper;

	/**
	 * Instantiates a new platform helper.
	 */
	private PlatformHelper() {}

	/**
	 * Checks if is windows.
	 *
	 * @return true, if is windows
	 */
	public static boolean isWindows() {
		return PlatformHelper.isWindows;
	}

	/**
	 * Checks if is linux.
	 *
	 * @return true, if is linux
	 */
	public static boolean isLinux() {
		return PlatformHelper.isLinux;
	}

	/**
	 * Checks if is mac.
	 *
	 * @return true, if is mac
	 */
	public static boolean isMac() {
		return PlatformHelper.isMac;
	}

	/**
	 * Checks if is developer.
	 *
	 * @return true, if is developer
	 */
	public static boolean isDeveloper() { // NO_UCD (unused code)
		if (PlatformHelper.isDeveloper == null) {
			PlatformHelper.isDeveloper = Platform.getInstallLocation() == null
					|| Platform.getInstallLocation().getURL().getPath().contains("org.eclipse.pde.core");
		}
		return PlatformHelper.isDeveloper;
	}

	/**
	 * The JAVA version
	 */
	public static final int JAVA_VERSION; // NO_UCD (unused code)
	static {
		JAVA_VERSION = parseVersion(System.getProperty("java.version")); //$NON-NLS-1$
	}

	/**
	 * Parses the version.
	 *
	 * @param version
	 *            the version
	 * @return the int
	 */
	static int parseVersion(final String version) {
		if (version == null) { return 0; }
		int major = 0, minor = 0, micro = 0;
		final var length = version.length();
		int index = 0, start = 0;
		while (index < length && Character.isDigit(version.charAt(index))) {
			index++;
		}
		try {
			if (start < length) { major = Integer.parseInt(version.substring(start, index)); }
		} catch (final NumberFormatException e) {}
		start = ++index;
		while (index < length && Character.isDigit(version.charAt(index))) {
			index++;
		}
		try {
			if (start < length) { minor = Integer.parseInt(version.substring(start, index)); }
		} catch (final NumberFormatException e) {}
		start = ++index;
		while (index < length && Character.isDigit(version.charAt(index))) {
			index++;
		}
		try {
			if (start < length) { micro = Integer.parseInt(version.substring(start, index)); }
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

	public static boolean isARM() {
		return PlatformHelper.isARM;
	}

}
