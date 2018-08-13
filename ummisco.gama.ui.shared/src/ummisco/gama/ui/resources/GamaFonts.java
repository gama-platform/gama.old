/*********************************************************************************************
 *
 * 'GamaFonts.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.resources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import ummisco.gama.ui.utils.GraphicsHelper;
import ummisco.gama.ui.utils.PreferencesHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class GamaFonts {

	private static Font systemFont;
	private static FontData baseData;
	private static String baseFont;
	public static int baseSize = 11;
	private static java.awt.Font awtBaseFont;
	public static Font expandFont;
	public static Font smallFont;
	public static Font smallNavigFont;
	public static Font smallNavigLinkFont;
	public static Font labelFont;
	public static Font navigRegularFont;
	public static Font navigFileFont;
	public static Font parameterEditorsFont;
	public static Font navigResourceFont;
	public static Font navigHeaderFont;
	public static Font unitFont;
	public static Font helpFont;
	public static Font boldHelpFont;
	public static Font categoryHelpFont;
	public static Font categoryBoldHelpFont;

	private static java.awt.Font getAwtBaseFont() {
		if (awtBaseFont == null) {
			awtBaseFont = PreferencesHelper.BASE_BUTTON_FONT.getValue();
		}
		return awtBaseFont;
	}

	private static void setAwtBaseFont(final java.awt.Font awtBaseFont) {
		GamaFonts.awtBaseFont = awtBaseFont;
	}

	public static String getBaseFont() {
		if (baseFont == null) {
			baseFont = getBaseData().getName();
		}
		return baseFont;
	}

	public static void setBaseFont(final String baseFont) {
		GamaFonts.baseFont = baseFont;
	}

	public static FontData getBaseData() {
		if (baseData == null) {
			baseData = getSystemFont().getFontData()[0];
		}
		return baseData;
	}

	public static void setBaseData(final FontData baseData) {
		GamaFonts.baseData = baseData;
	}

	public static Font getSystemFont() {
		if (systemFont == null) {
			systemFont = WorkbenchHelper.getDisplay().getSystemFont();
		}
		return systemFont;
	}

	public static void setSystemFont(final Font systemFont) {
		GamaFonts.systemFont = systemFont;
	}

	static void initFonts() {
		// DEBUG.LOG("System font = " + Arrays.toString(systemFont.getFontData()));
		final Display d = WorkbenchHelper.getDisplay();
		FontData fd = new FontData(getAwtBaseFont().getName(), getAwtBaseFont().getSize(), getAwtBaseFont().getStyle());
		final FontData original = fd;
		labelFont = new Font(d, fd);
		final FontData fd2 = new FontData(fd.getName(), fd.getHeight(), SWT.BOLD);
		expandFont = new Font(d, fd2);
		fd = new FontData(fd.getName(), fd.getHeight(), SWT.ITALIC);
		unitFont = new Font(d, fd);
		smallNavigLinkFont = new Font(d, fd);
		fd = new FontData(fd.getName(), fd.getHeight() + 1, SWT.BOLD);
		// bigFont = new Font(Display.getDefault(), fd);
		navigHeaderFont = new Font(d, fd);
		fd = new FontData(fd.getName(), fd.getHeight() - 1, SWT.NORMAL);
		smallFont = new Font(d, fd);
		smallNavigFont = new Font(d, fd);
		fd = new FontData(fd.getName(), fd.getHeight(), SWT.NORMAL);
		parameterEditorsFont = new Font(d, fd);
		navigFileFont = new Font(d, fd);
		fd = new FontData(fd.getName(), fd.getHeight(), SWT.NORMAL);
		navigRegularFont = new Font(d, fd);
		fd = new FontData(fd.getName(), fd.getHeight(), SWT.ITALIC);
		navigResourceFont = new Font(d, fd);
		fd = new FontData(original.getName(), 12, SWT.NORMAL);
		helpFont = new Font(d, fd);
		fd = new FontData(original.getName(), 12, SWT.BOLD);
		boldHelpFont = new Font(d, fd);
		fd = new FontData(fd.getName(), 14, SWT.NORMAL);
		categoryHelpFont = new Font(d, fd);
		fd = new FontData(fd.getName(), 14, SWT.BOLD);
		categoryBoldHelpFont = new Font(d, fd);
	}

	public static void setLabelFont(final Font f) {
		if (labelFont == null) {
			labelFont = f;
			return;
		} else {
			labelFont = f;
		}
	}

	public static void setLabelFont(final java.awt.Font font) {
		setAwtBaseFont(font);
		final FontData fd = GraphicsHelper.toSwtFontData(WorkbenchHelper.getDisplay(), font, true);
		setLabelFont(new Font(WorkbenchHelper.getDisplay(), fd));
	}

	public static Font getLabelfont() {
		if (labelFont == null) {
			initFonts();
		}
		return labelFont;
	}

	public static Font getSmallFont() {
		if (smallFont == null) {
			initFonts();
		}
		return smallFont;
	}

	public static Font getExpandfont() {
		if (expandFont == null) {
			initFonts();
		}
		return expandFont;
	}

	public static Font getHelpFont() {
		if (helpFont == null) {
			initFonts();
		}
		return helpFont;
	}

	public static Font getBoldHelpFont() {
		if (boldHelpFont == null) {
			initFonts();
		}
		return boldHelpFont;
	}

	public static Font getParameterEditorsFont() {
		if (parameterEditorsFont == null) {
			initFonts();
		}
		return parameterEditorsFont;
	}

	public static Font getNavigFolderFont() {
		if (navigRegularFont == null) {
			initFonts();
		}
		return navigRegularFont;
	}

	public static Font getNavigLinkFont() {
		if (smallNavigLinkFont == null) {
			initFonts();
		}
		return smallNavigLinkFont;
	}

	public static Font getNavigFileFont() {
		if (navigFileFont == null) {
			initFonts();
		}
		return navigFileFont;
	}

	public static Font getNavigSmallFont() {
		if (smallNavigFont == null) {
			initFonts();
		}
		return smallNavigFont;
	}

	public static Font getNavigHeaderFont() {
		if (navigHeaderFont == null) {
			initFonts();
		}
		return navigHeaderFont;
	}

	public static Font getResourceFont() {
		if (navigResourceFont == null) {
			initFonts();
		}
		return navigResourceFont;
	}

	public static Font getUnitFont() {
		if (unitFont == null) {
			initFonts();
		}
		return unitFont;
	}

}
