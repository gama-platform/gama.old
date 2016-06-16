package ummisco.gama.ui.resources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import ummisco.gama.ui.utils.SWTUtils;

public class GamaFonts {

	public static FontData baseData = Display.getCurrent().getSystemFont().getFontData()[0];
	public static String baseFont = baseData.getName();
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

	static void initFonts() {
		FontData fd = new FontData(awtBaseFont.getName(), awtBaseFont.getSize(), awtBaseFont.getStyle());
		labelFont = new Font(Display.getCurrent(), fd);
		final FontData fd2 = new FontData(fd.getName(), fd.getHeight(), SWT.BOLD);
		expandFont = new Font(Display.getDefault(), fd2);
		fd = new FontData(fd.getName(), fd.getHeight(), SWT.ITALIC);
		unitFont = new Font(Display.getDefault(), fd);
		smallNavigLinkFont = new Font(Display.getDefault(), fd);
		fd = new FontData(fd.getName(), fd.getHeight() + 1, SWT.BOLD);
		// bigFont = new Font(Display.getDefault(), fd);
		navigHeaderFont = new Font(Display.getDefault(), fd);
		fd = new FontData(fd.getName(), fd.getHeight() - 1, SWT.NORMAL);
		smallFont = new Font(Display.getDefault(), fd);
		smallNavigFont = new Font(Display.getDefault(), fd);
		fd = new FontData(fd.getName(), fd.getHeight(), SWT.NORMAL);
		parameterEditorsFont = new Font(Display.getDefault(), fd);
		navigFileFont = new Font(Display.getDefault(), fd);
		fd = new FontData(fd.getName(), fd.getHeight(), SWT.NORMAL);
		navigRegularFont = new Font(Display.getDefault(), fd);
		fd = new FontData(fd.getName(), fd.getHeight(), SWT.ITALIC);
		navigResourceFont = new Font(Display.getDefault(), fd);
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
		awtBaseFont = font;
		final FontData fd = SWTUtils.toSwtFontData(Display.getCurrent(), font, true);
		setLabelFont(new Font(Display.getCurrent(), fd));
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
