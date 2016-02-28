/*******************************************************************************
 * Copyright (c) 2005-2008 SAS Institute Inc., ILOG S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * SAS Institute Inc. - initial API and implementation
 * ILOG S.A. - initial API and implementation
 *******************************************************************************/
package msi.gama.gui.swt.swing;

import java.awt.Toolkit;
import org.eclipse.swt.graphics.*;

/**
 * Converter of resources from AWT/Swing to SWT and vice versa.
 */
public class ResourceConverter {

	/**
	 * Converts a color from SWT to Swing.
	 * The argument Color remains owned by the caller.
	 */
	public java.awt.Color convertColor(final Color c) {
		return new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue());
	}

	/**
	 * Converts a font from SWT to Swing.
	 * The argument Font remains owned by the caller.
	 * @param swtFont An SWT font.
	 * @param swtFontData Result of <code>swtFont.getFontData()</code>,
	 *            obtained on the SWT event thread.
	 */
	public java.awt.Font convertFont(final Font swtFont, final FontData[] swtFontData) {
		FontData fontData0 = swtFontData[0];

		// AWT font sizes assume a 72 dpi resolution, always. The true screen resolution must be
		// used to convert the platform font size into an AWT point size that matches when displayed.
		int resolution = Toolkit.getDefaultToolkit().getScreenResolution();
		int awtFontSize = (int) Math.round((double) fontData0.getHeight() * resolution / 72.0);

		// The style constants for SWT and AWT map exactly, and since they are int constants, they should
		// never change. So, the SWT style is passed through as the AWT style.
		return new java.awt.Font(fontData0.getName(), fontData0.getStyle(), awtFontSize);
	}

	// ========================================================================
	// Singleton design pattern

	private static ResourceConverter theInstance = new ResourceConverter();

	/**
	 * Returns the currently active singleton of this class.
	 */
	public static ResourceConverter getInstance() {
		return theInstance;
	}

	/**
	 * Replaces the singleton of this class.
	 * @param instance An instance of this class or of a customized subclass.
	 */
	public static void setInstance(final ResourceConverter instance) {
		theInstance = instance;
	}

}
