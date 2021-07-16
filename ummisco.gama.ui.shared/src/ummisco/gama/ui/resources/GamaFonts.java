/*********************************************************************************************
 *
 * 'GamaFonts.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.resources;

import org.eclipse.swt.graphics.Resource;

public class GamaFonts {

	// TODO AD 07/21 :
	// Completely phased out this class in order to have a more bottom-up approach regarding fonts and use
	// CSS Theming in the future

	// private static Font systemFont;
	// private static FontData baseData;
	// private static String baseFont;
	// public static int baseSize = 11;
	// private static java.awt.Font awtBaseFont;
	// public static Font expandFont;
	// public static Font smallFont;
	// public static Font smallNavigFont;
	// public static Font smallNavigLinkFont;
	// public static Font labelFont;
	// public static Font navigRegularFont;
	// public static Font navigFileFont;
	// public static Font parameterEditorsFont;
	// public static Font navigResourceFont;
	// public static Font navigHeaderFont;
	// public static Font unitFont;
	// public static Font helpFont;
	// public static Font boldHelpFont;
	// public static Font categoryHelpFont;
	// public static Font categoryBoldHelpFont;

	// private static java.awt.Font getAwtBaseFont() {
	// if (awtBaseFont == null) { awtBaseFont = PreferencesHelper.BASE_BUTTON_FONT.getValue(); }
	// return awtBaseFont;
	// }

	// private static void setAwtBaseFont(final java.awt.Font awtBaseFont) {
	// GamaFonts.awtBaseFont = awtBaseFont;
	// }

	// public static String getBaseFont() {
	// if (baseFont == null) { baseFont = getBaseData().getName(); }
	// return baseFont;
	// }
	//
	// public static FontData getBaseData() {
	// if (baseData == null) { baseData = getSystemFont().getFontData()[0]; }
	// return baseData;
	// }

	// public static Font getSystemFont() {
	// if (systemFont == null) { systemFont = WorkbenchHelper.getDisplay().getSystemFont(); }
	// return systemFont;
	// }

	static void initFonts() {
		// DEBUG.LOG("System font = " + Arrays.toString(systemFont.getFontData()));
		// final Display d = WorkbenchHelper.getDisplay();
		// FontData fd = new FontData(getAwtBaseFont().getName(), getAwtBaseFont().getSize(),
		// getAwtBaseFont().getStyle());
		// final FontData original = fd;
		// labelFont = new Font(d, fd);
		// final FontData fd2 = new FontData(fd.getName(), fd.getHeight(), SWT.BOLD);
		// expandFont = new Font(d, fd2);
		// fd = new FontData(fd.getName(), fd.getHeight(), SWT.ITALIC);
		// unitFont = new Font(d, fd);
		// smallNavigLinkFont = new Font(d, fd);
		// fd = new FontData(fd.getName(), fd.getHeight() + 1, SWT.BOLD);
		// // bigFont = new Font(Display.getDefault(), fd);
		// navigHeaderFont = new Font(d, fd);
		// fd = new FontData(fd.getName(), fd.getHeight() - 1, SWT.NORMAL);
		// smallFont = new Font(d, fd);
		// smallNavigFont = new Font(d, fd);
		// fd = new FontData(fd.getName(), fd.getHeight(), SWT.NORMAL);
		// parameterEditorsFont = new Font(d, fd);
		// navigFileFont = new Font(d, fd);
		// fd = new FontData(fd.getName(), fd.getHeight(), SWT.NORMAL);
		// navigRegularFont = new Font(d, fd);
		// fd = new FontData(fd.getName(), fd.getHeight(), SWT.ITALIC);
		// navigResourceFont = new Font(d, fd);
		// fd = new FontData(original.getName(), 12, SWT.NORMAL);
		// helpFont = new Font(d, fd);
		// fd = new FontData(original.getName(), 12, SWT.BOLD);
		// boldHelpFont = new Font(d, fd);
		// fd = new FontData(fd.getName(), 14, SWT.NORMAL);
		// categoryHelpFont = new Font(d, fd);
		// fd = new FontData(fd.getName(), 14, SWT.BOLD);
		// categoryBoldHelpFont = new Font(d, fd);
		Resource.setNonDisposeHandler(null);
	}

	// public static void setLabelFont(final Font f) {
	// labelFont = f;
	// }
	//
	// public static void setLabelFont(final java.awt.Font font) {
	// setAwtBaseFont(font);
	// final FontData fd = GraphicsHelper.toSwtFontData(WorkbenchHelper.getDisplay(), font, true);
	// setLabelFont(new Font(WorkbenchHelper.getDisplay(), fd));
	// }

	// public static Font getLabelfont() {
	// if (labelFont == null) { initFonts(); }
	// return labelFont;
	// }

	// public static Font getSmallFont() {
	// if (smallFont == null) { initFonts(); }
	// return smallFont;
	// }

	// public static Font getExpandfont() {
	// if (expandFont == null) { initFonts(); }
	// return expandFont;
	// }

	// public static Font getHelpFont() {
	// if (helpFont == null) { initFonts(); }
	// return helpFont;
	// }
	//
	// public static Font getNavigFolderFont() {
	// if (navigRegularFont == null) { initFonts(); }
	// return navigRegularFont;
	// }

	// public static Font getNavigLinkFont() {
	// if (smallNavigLinkFont == null) { initFonts(); }
	// return smallNavigLinkFont;
	// }
	////
	// public static Font getNavigFileFont() {
	// if (navigFileFont == null) { initFonts(); }
	// return navigFileFont;
	// }

	// public static Font getNavigHeaderFont() {
	// if (navigHeaderFont == null) { initFonts(); }
	// return navigHeaderFont;
	// }

	// public static Font getResourceFont() {
	// if (navigResourceFont == null) { initFonts(); }
	// return navigResourceFont;
	// }

}
