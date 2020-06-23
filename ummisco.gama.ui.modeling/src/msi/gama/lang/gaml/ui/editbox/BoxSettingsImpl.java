/*********************************************************************************************
 *
 * 'BoxSettingsImpl.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class BoxSettingsImpl implements IBoxSettings {

	private static final BoxSettingsImpl DEFAULT = new BoxSettingsImpl() {
		{
			name = "Default";
			borderWidth = 1;
			highlightWidth = 1;
			highlightOne = true;
			roundBox = true;
			borderColor = new Color(null, 0, 187, 187);
			highlightColor = new Color(null, 0, 0, 0);
			builder = "Text";
			boxColors = new Color[] { null, new Color(null, 208, 221, 155), new Color(null, 205, 216, 185),
					new Color(null, 233, 245, 139) };
			fillSelected = true;
			fillColor = new Color(null, 255, 255, 196);
			fillKeyModifier = "Alt";
		}
	};

	protected boolean enabled;
	protected String name;
	protected String text;

	protected Color borderColor;
	protected Color highlightColor;
	protected Color fillColor;
	protected int borderWidth;
	protected int highlightWidth;
	protected boolean highlightOne;
	protected boolean fillSelected;
	protected boolean roundBox;
	protected String builder;
	protected Color[] boxColors;
	protected ArrayList<IPropertyChangeListener> listeners;
	protected boolean highlightDrawLine;
	protected Color fillGradientColor;
	protected boolean fillGradient;
	protected boolean borderDrawLine;
	protected boolean fillOnMove;
	protected boolean circulateLevelColors;
	protected String fillKeyModifier;
	protected Collection<String> fileNames;
	protected int highlightColorType;
	protected int borderColorType;
	protected int borderLineStyle;
	protected int highlightLineStyle;
	protected boolean noBackground;
	protected boolean expandBox;
	protected int alpha;

	private transient Color[] borderColors;
	private transient Color[] highlightColors;

	@Override
	public void copyFrom(final IBoxSettings other) {
		final BoxSettingsImpl o = (BoxSettingsImpl) other;
		name = o.getName();
		enabled = o.getEnabled();
		fileNames = o.fileNames == null ? null : new ArrayList<>(o.fileNames);
		borderColor = setColorCopy(borderColor, o.borderColor);
		highlightColor = setColorCopy(highlightColor, o.highlightColor);
		fillColor = setColorCopy(fillColor, o.fillColor);
		borderWidth = o.borderWidth;
		highlightWidth = o.highlightWidth;
		highlightOne = o.highlightOne;
		fillSelected = o.fillSelected;
		roundBox = o.roundBox;
		final Color[] newBoxColors = copyColors(o);
		final Color[] oldBoxColors = boxColors;
		boxColors = newBoxColors;
		disposeColors(oldBoxColors);
		builder = o.builder;
		borderDrawLine = o.borderDrawLine;
		highlightDrawLine = o.highlightDrawLine;
		fillGradient = o.fillGradient;
		fillGradientColor = setColorCopy(fillGradientColor, o.fillGradientColor);
		fillOnMove = o.fillOnMove;
		circulateLevelColors = o.circulateLevelColors;
		fillKeyModifier = o.fillKeyModifier;
		borderColorType = o.borderColorType;
		highlightColorType = o.highlightColorType;
		borderColors = disposeColors(borderColors);
		highlightColors = disposeColors(highlightColors);
		highlightLineStyle = o.highlightLineStyle;
		borderLineStyle = o.borderLineStyle;
		noBackground = o.noBackground;
		expandBox = o.expandBox;
		alpha = o.alpha;
		notifyChange(PropertiesKeys.ALL.name(), null, null);
	}

	private Color[] copyColors(final BoxSettingsImpl o) {
		final Color[] newBoxColors = o.boxColors == null ? null : new Color[o.boxColors.length];
		if (newBoxColors != null) {
			for (int i = 0; i < newBoxColors.length; i++) {
				final Color c = o.boxColors[i];
				if (c != null) {
					newBoxColors[i] = new Color(null, c.getRGB());
				}
			}
		}
		return newBoxColors;
	}

	private Color[] disposeColors(final Color[] oldBoxColors) {
		if (oldBoxColors != null) {
			for (final Color oldBoxColor : oldBoxColors) {
				if (oldBoxColor != null) {
					oldBoxColor.dispose();
				}
			}
		}
		return null;
	}

	protected Color setColorCopy(final Color old, final Color newColor) {
		if (old != null) {
			old.dispose();
		}
		return newColor == null ? null : new Color(null, newColor.getRGB());
	}

	@Override
	public String export() {
		return new StringExternalization().export(this);
	}

	@Override
	public void load(final String string) {
		final StringExternalization ext = new StringExternalization();
		boolean error = false;
		if (string != null) {
			try {
				ext.load(string, this);
			} catch (final Exception e) {
				// EditBox.logError(this, "Cannot load EditBox settings from
				// string: " + string, e);
				error = true;
			}
		}
		if (error || string == null) {
			this.copyFrom(DEFAULT);
		}
		notifyChange(PropertiesKeys.ALL.name(), null, null);
	}

	@Override
	public void export(final OutputStream stream) throws Exception {
		new StringExternalization().export(stream, this);
	}

	@Override
	public void load(final InputStream stream) throws Exception {
		if (stream == null) {
			load((String) null);
		} else {
			new StringExternalization().load(stream, this);
		}
		notifyChange(PropertiesKeys.ALL.name(), null, null);
	}

	@Override
	public boolean getEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(final boolean flag) {
		this.enabled = flag;
		notifyChange(PropertiesKeys.Enabled.name(), null, flag);
	}

	@Override
	public void setFileNames(final Collection<String> fileNames) {
		this.fileNames = fileNames;
		notifyChange(PropertiesKeys.FileNames.name(), null, null);
	}

	@Override
	public Collection<String> getFileNames() {
		return fileNames;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String newName) {
		this.name = newName;
		notifyChange(PropertiesKeys.Name.name(), null, null);
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(final String newText) {
		this.text = newText;
	}

	protected Color setColor0(final Color old, final Color c) {
		disposeColor(old);
		return c;
	}

	protected Color setColor0(final Color old, final RGB c) {
		if (c == null) { return old; }
		return setColor0(old, new Color(null, c));
	}

	protected Color disposeColor(final Color c) {
		if (c != null) {
			c.dispose();
		}
		return null;
	}

	@Override
	public void dispose() {
		borderColor = disposeColor(borderColor);
		highlightColor = disposeColor(highlightColor);
		fillColor = disposeColor(fillColor);
		disposeColors(boxColors);
		boxColors = null;
		if (listeners != null) {
			listeners.clear();
		}
	}

	@Override
	public Color getBorderColor() {
		return borderColor;
	}

	@Override
	public int getBorderWidth() {
		return borderWidth;
	}

	@Override
	public void setBorderRGB(final RGB c) {
		borderColor = setColor0(borderColor, c);
		notifyChange(PropertiesKeys.BorderColor.name(), null, null);
	}

	@Override
	public void setBorderWidth(final int w) {
		borderWidth = w;
		notifyChange(PropertiesKeys.BorderWidth.name(), null, null);
	}

	@Override
	public void setRoundBox(final boolean flag) {
		roundBox = flag;
		notifyChange(PropertiesKeys.RoundBox.name(), null, null);
	}

	@Override
	public boolean getRoundBox() {
		return roundBox;
	}

	@Override
	public Color getHighlightColor() {
		return highlightColor;
	}

	@Override
	public boolean getHighlightOne() {
		return highlightOne;
	}

	@Override
	public int getHighlightWidth() {
		return highlightWidth;
	}

	@Override
	public void setHighlightOne(final boolean flag) {
		highlightOne = flag;
		notifyChange(PropertiesKeys.HighlightOne.name(), null, null);
	}

	@Override
	public void setHighlightRGB(final RGB highlightRGB) {
		highlightColor = setColor0(highlightColor, highlightRGB);
		notifyChange(PropertiesKeys.HighlightColor.name(), null, null);
	}

	@Override
	public void setHighlightWidth(final int w) {
		highlightWidth = w;
		notifyChange(PropertiesKeys.HighlightWidth.name(), null, null);
	}

	@Override
	public boolean getFillSelected() {
		return fillSelected;
	}

	@Override
	public Color getFillSelectedColor() {
		return fillColor;
	}

	@Override
	public void setFillSelected(final boolean flag) {
		fillSelected = flag;
		notifyChange(PropertiesKeys.FillSelected.name(), null, null);
	}

	@Override
	public void setFillSelectedRGB(final RGB newColor) {
		fillColor = setColor0(fillColor, newColor);
		notifyChange(PropertiesKeys.FillSelectedColor.name(), null, null);
	}

	@Override
	public void setBuilder(final String name) {
		builder = name;
		notifyChange(PropertiesKeys.Builder.name(), null, null);
	}

	@Override
	public String getBuilder() {
		return builder;
	}

	@Override
	public boolean getBorderDrawLine() {
		return borderDrawLine;
	}

	@Override
	public boolean getFillGradient() {
		return fillGradient;
	}

	@Override
	public Color getFillGradientColor() {
		return fillGradientColor;
	}

	@Override
	public boolean getHighlightDrawLine() {
		return highlightDrawLine;
	}

	@Override
	public void setBorderDrawLine(final boolean flag) {
		borderDrawLine = flag;
		notifyChange(PropertiesKeys.BorderDrawLine.name(), null, null);
	}

	@Override
	public void setFillGradient(final boolean flag) {
		fillGradient = flag;
		notifyChange(PropertiesKeys.FillGradient.name(), null, null);
	}

	@Override
	public void setFillGradientColorRGB(final RGB color) {
		fillGradientColor = setColor0(fillGradientColor, color);
		notifyChange(PropertiesKeys.FillGradientColor.name(), null, null);
	}

	@Override
	public void setHighlightDrawLine(final boolean flag) {
		highlightDrawLine = flag;
		notifyChange(PropertiesKeys.HighlightDrawLine.name(), null, null);
	}

	@Override
	public Color[] getColors() {
		return boxColors;
	}

	@Override
	public boolean getFillOnMove() {
		return fillOnMove;
	}

	@Override
	public void setFillOnMove(final boolean selection) {
		fillOnMove = selection;
		notifyChange(PropertiesKeys.FillOnMove.name(), null, null);
	}

	@Override
	public void setColorsRGB(final RGB[] gradient) {
		if (gradient == null) {
			disposeColors(boxColors);
			boxColors = null;
		} else {
			final Color[] c = new Color[gradient.length];
			for (int i = 0; i < gradient.length; i++) {
				c[i] = new Color(null, gradient[i]);
			}
			disposeColors(boxColors);
			boxColors = c;
		}
		notifyChange(PropertiesKeys.Colors.name(), null, null);
	}

	@Override
	public void setColorsSize(final int nb) {

		final int n = nb + 1;
		Color[] newColors = null;
		if (n == 0) {
			disposeColors(boxColors);
		} else if (boxColors != null) {
			newColors = new Color[n];
			for (int i = 0; i < n; i++) {
				if (i >= boxColors.length) {
					break;
				}
				newColors[i] = boxColors[i];
			}
			for (int i = n; i < boxColors.length; i++) {
				disposeColor(boxColors[i]);
			}
		} else {
			newColors = new Color[n];
		}
		boxColors = newColors;
		notifyChange(PropertiesKeys.Colors.name(), null, null);
	}

	@Override
	public int getColorsSize() {
		return boxColors == null ? 0 : boxColors.length - 1;
	}

	@Override
	public Color getBorderColor(final int level) {
		if (borderColorType < 1) { return borderColor; }

		if (boxColors == null) { return null; }

		if (borderColors != null && borderColors.length != boxColors.length) {
			borderColors = disposeColors(borderColors);
		}

		if (borderColors == null) {
			borderColors = new Color[boxColors.length];
		}

		final int idx = getColorIndex(level);
		if (idx > -1) {
			if (borderColors[idx] != null) { return borderColors[idx]; }
			if (boxColors[idx] == null) { return null; }
			return borderColors[idx] = calculateDarkerColor(boxColors[idx], borderColorType);
		}

		return null;
	}

	private final Color calculateDarkerColor(final Color c, final int type) {
		return new Color(null, calcDarker(c.getRed(), type), calcDarker(c.getGreen(), type),
				calcDarker(c.getBlue(), type));
	}

	private final int calcDarker(final int r, final int type) {
		return r - r * type / 4;
	}

	@Override
	public Color getHighlightColor(final int level) {
		if (highlightColorType < 1) { return highlightColor; }

		if (boxColors == null) { return null; }

		if (highlightColors != null && highlightColors.length != boxColors.length) {
			highlightColors = disposeColors(highlightColors);
		}

		if (highlightColors == null) {
			highlightColors = new Color[boxColors.length];
		}

		final int idx = getColorIndex(level);
		if (idx > -1) {
			if (highlightColors[idx] != null) { return highlightColors[idx]; }
			if (boxColors[idx] == null) { return null; }
			return highlightColors[idx] = calculateDarkerColor(boxColors[idx], highlightColorType);
		}

		return null;
	}

	@Override
	public void setBorderColorType(final int selectionIndex) {
		this.borderColorType = selectionIndex;
		notifyChange(PropertiesKeys.BorderColorType.name(), null, null);
	}

	@Override
	public int getBorderColorType() {
		return borderColorType;
	}

	@Override
	public void setHighlightColorType(final int selectionIndex) {
		this.highlightColorType = selectionIndex;
		notifyChange(PropertiesKeys.HighlightColorType.name(), null, null);
	}

	@Override
	public int getHighlightColorType() {
		return highlightColorType;
	}

	public int getColorIndex(final int level) {
		if (boxColors != null && boxColors.length == 1 && noBackground && level > 0) { return -1; }
		if (!circulateLevelColors && boxColors != null && boxColors.length <= level
				&& boxColors.length > 0) { return boxColors.length - 1; }
		if (boxColors != null && boxColors.length > 0 && level > -1) { return getNColor0(level); }
		return -1;
	}

	@Override
	public Color getColor(final int level) {
		final int idx = getColorIndex(level);
		if (idx > -1) { return boxColors[idx]; }
		return null;
	}

	int getNColor0(final int n) {
		final int len = boxColors.length;
		if (len < 2) { return 0; }
		final int x = n % (len + len - 2);
		if (x < len) { return x; }
		return len - (x - len + 2);
	}

	@Override
	public void setColor(final int level, final RGB rgb) {
		if (boxColors != null && boxColors.length > 0 && level > -1) {
			boxColors[level % boxColors.length] = setColor0(boxColors[level % boxColors.length], rgb);
		}
		notifyChange(PropertiesKeys.Color.name(), null, null);
	}

	@Override
	public boolean getCirculateLevelColors() {
		return circulateLevelColors;
	}

	@Override
	public void setCirculateLevelColors(final boolean flag) {
		circulateLevelColors = flag;
		notifyChange(PropertiesKeys.CirculateLevelColors.name(), null, null);
	}

	@Override
	public void setFillKeyModifier(final String key) {
		fillKeyModifier = key;
		notifyChange(PropertiesKeys.FillKeyModifier.name(), null, null);
	}

	@Override
	public String getFillKeyModifier() {
		return fillKeyModifier;
	}

	@Override
	public int getFillKeyModifierSWTInt() {
		if (fillKeyModifier == null || fillKeyModifier.length() == 0) { return 0; }
		if ("Alt".equals(fillKeyModifier)) { return SWT.ALT; }
		if ("Ctrl".equals(fillKeyModifier)) { return SWT.CTRL; }
		if ("Shift".equals(fillKeyModifier)) { return SWT.SHIFT; }
		return 0;
	}

	class StringExternalization {

		private static final String COMMENT = "Editbox Eclipse Plugin Settings";

		public String export(final BoxSettingsImpl b) {
			final Properties p = toProperies(b);
			return propertiesToString(p);
		}

		public void load(final InputStream stream, final BoxSettingsImpl b) throws Exception {
			final Properties p = new Properties();
			p.load(stream);
			load(p, b);
		}

		public void export(final OutputStream stream, final BoxSettingsImpl b) throws Exception {
			final Properties p = toProperies(b);
			p.store(stream, COMMENT);

		}

		public void load(final String s, final BoxSettingsImpl b) throws Exception {
			final Properties p = loadProperties(s);
			load(p, b);
		}

		private void load(final Properties p, final BoxSettingsImpl b) {
			b.name = parseString(p.get(PropertiesKeys.Name.name()));
			b.borderColor = parseColor(p.get(PropertiesKeys.BorderColor.name()));
			b.highlightColor = parseColor(p.get(PropertiesKeys.HighlightColor.name()));
			b.borderWidth = parseInt(p.get(PropertiesKeys.BorderWidth.name()));
			b.highlightWidth = parseInt(p.get(PropertiesKeys.HighlightWidth.name()));
			b.roundBox = parseBool(p.get(PropertiesKeys.RoundBox.name()));
			b.highlightOne = parseBool(p.get(PropertiesKeys.HighlightOne.name()));
			b.fillSelected = parseBool(p.get(PropertiesKeys.FillSelected.name()));
			b.fillColor = parseColor(p.get(PropertiesKeys.FillSelectedColor.name()));
			b.builder = parseString(p.get(PropertiesKeys.Builder.name()));
			b.boxColors = parseColorsArray(p.get(PropertiesKeys.Colors.name()));
			b.highlightDrawLine = parseBool(p.get(PropertiesKeys.HighlightDrawLine.name()));
			b.borderDrawLine = parseBool(p.get(PropertiesKeys.BorderDrawLine.name()));
			b.fillGradient = parseBool(p.get(PropertiesKeys.FillGradient.name()));
			b.fillGradientColor = parseColor(p.get(PropertiesKeys.FillGradientColor.name()));
			b.fillOnMove = parseBool(p.get(PropertiesKeys.FillOnMove.name()));
			b.circulateLevelColors = parseBool(p.get(PropertiesKeys.CirculateLevelColors.name()));
			b.fillKeyModifier = parseString(p.get(PropertiesKeys.FillKeyModifier.name()));
			b.borderColorType = parseInt(p.get(PropertiesKeys.BorderColorType.name()));
			b.highlightColorType = parseInt(p.get(PropertiesKeys.HighlightColorType.name()));
			b.highlightLineStyle = parseInt(p.get(PropertiesKeys.HighlightLineStyle.name()));
			b.borderLineStyle = parseInt(p.get(PropertiesKeys.BorderLineStyle.name()));
			b.noBackground = parseBool(p.get(PropertiesKeys.NoBackground.name()));
			b.expandBox = parseBool(p.get(PropertiesKeys.ExpandBox.name()));
			b.alpha = parseInt(p.get(PropertiesKeys.Alpha.name()));
		}

		private Properties toProperies(final BoxSettingsImpl b) {
			final Properties p = new Properties();
			p.put(PropertiesKeys.Name.name(), toS(b.name));
			p.put(PropertiesKeys.BorderColor.name(), toS(b.borderColor));
			p.put(PropertiesKeys.HighlightColor.name(), toS(b.highlightColor));
			p.put(PropertiesKeys.BorderWidth.name(), toS(b.borderWidth));
			p.put(PropertiesKeys.HighlightWidth.name(), toS(b.highlightWidth));
			p.put(PropertiesKeys.RoundBox.name(), toS(b.roundBox));
			p.put(PropertiesKeys.HighlightOne.name(), toS(b.highlightOne));
			p.put(PropertiesKeys.FillSelected.name(), toS(b.fillSelected));
			p.put(PropertiesKeys.FillSelectedColor.name(), toS(b.fillColor));
			p.put(PropertiesKeys.Builder.name(), toS(b.builder));
			p.put(PropertiesKeys.Colors.name(), toS(b.boxColors));
			p.put(PropertiesKeys.HighlightDrawLine.name(), toS(b.highlightDrawLine));
			p.put(PropertiesKeys.BorderDrawLine.name(), toS(b.borderDrawLine));
			p.put(PropertiesKeys.FillGradient.name(), toS(b.fillGradient));
			p.put(PropertiesKeys.FillGradientColor.name(), toS(b.fillGradientColor));
			p.put(PropertiesKeys.FillOnMove.name(), toS(b.fillOnMove));
			p.put(PropertiesKeys.CirculateLevelColors.name(), toS(b.circulateLevelColors));
			p.put(PropertiesKeys.FillKeyModifier.name(), toS(b.fillKeyModifier));
			p.put(PropertiesKeys.BorderColorType.name(), toS(b.borderColorType));
			p.put(PropertiesKeys.HighlightColorType.name(), toS(b.highlightColorType));
			p.put(PropertiesKeys.HighlightLineStyle.name(), toS(b.highlightLineStyle));
			p.put(PropertiesKeys.BorderLineStyle.name(), toS(b.borderLineStyle));
			p.put(PropertiesKeys.NoBackground.name(), toS(b.noBackground));
			p.put(PropertiesKeys.ExpandBox.name(), toS(b.expandBox));
			p.put(PropertiesKeys.Alpha.name(), toS(b.alpha));
			return p;
		}

		private Properties loadProperties(final String s) throws IOException {
			final Properties p = new Properties();
			p.load(new StringReader(s));
			return p;
		}

		private String propertiesToString(final Properties p) {
			try {
				final ByteArrayOutputStream bo = new ByteArrayOutputStream();
				p.store(bo, "COMMENT");
				return bo.toString();
			} catch (final IOException e) {
				// EditBox.logError(this, "Cannot convert propeties to sring",
				// e);
			}
			return "";
		}

		private Color[] parseColorsArray(final Object o) {
			if (o == null || o.equals("null")) { return null; }
			final String[] s = o.toString().split("-");
			final Color[] c = new Color[s.length];
			for (int i = 0; i < c.length; i++) {
				c[i] = parseColor(s[i]);
			}
			return c;
		}

		private boolean parseBool(final Object o) {
			if (o == null) { return false; }
			return o.equals("true");
		}

		private int parseInt(final Object o) {
			if (o == null || o.equals("null")) { return 0; }
			return Integer.parseInt(o.toString());
		}

		private Color parseColor(final Object o) {
			if (o == null || o.equals("null")) { return null; }
			final String c = o.toString();
			if (c.length() != 6) { return null; }
			try {
				final int r = Integer.parseInt(c.substring(0, 2), 16);
				final int g = Integer.parseInt(c.substring(2, 4), 16);
				final int b = Integer.parseInt(c.substring(4, 6), 16);
				return new Color(null, r, g, b);
			} catch (final Exception e) {
				return null;
			}
		}

		private String parseString(final Object o) {
			if (o == null || o.equals("null")) { return null; }
			return o.toString();
		}

		private String toS(final boolean b) {
			return b ? "true" : "false";
		}

		private String toS(final Color[] colors) {
			final StringBuilder sb = new StringBuilder();
			if (colors == null || colors.length == 0) {
				sb.append("null");
			} else {
				for (int i = 0; i < colors.length; i++) {
					if (i > 0) {
						sb.append("-");
					}
					sb.append(toS(colors[i]));
				}
			}
			return sb.toString();
		}

		private String toS(final int i) {
			return Integer.toString(i);
		}

		private String toS(final Color c) {
			return c == null ? "null" : toHex(c.getRed()) + toHex(c.getGreen()) + toHex(c.getBlue());
		}

		private String toHex(final int v) {
			String s = Integer.toHexString(v);
			if (s.length() == 1) {
				s = "0" + s;
			}
			return s;
		}

		private String toS(final String s) {
			return s == null ? "null" : s;
		}
	}

	@Override
	public void addPropertyChangeListener(final IPropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<>();
		}
		listeners.add(listener);
	}

	@Override
	public void removePropertyChangeListener(final IPropertyChangeListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	protected void notifyChange(final String propertyName, final Object oldValue, final Object newValue) {
		if (listeners != null) {
			final PropertyChangeEvent e = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
			for (final IPropertyChangeListener listener : listeners) {
				listener.propertyChange(e);
			}
		}
	}

	@Override
	public void setBorderLineStyle(final int selectionIndex) {
		this.borderLineStyle = selectionIndex;
		notifyChange(PropertiesKeys.BorderLineStyle.name(), null, null);
	}

	@Override
	public void setHighlightLineStyle(final int selectionIndex) {
		this.highlightLineStyle = selectionIndex;
		notifyChange(PropertiesKeys.HighlightLineStyle.name(), null, null);
	}

	@Override
	public int getBorderLineStyle() {
		return borderLineStyle;
	}

	@Override
	public int getBorderLineStyleSWTInt() {
		return swtLineStyle(borderLineStyle);
	}

	private int swtLineStyle(final int index) {
		switch (index) {
			case 0:
				return SWT.LINE_SOLID;
			case 1:
				return SWT.LINE_DOT;
			case 2:
				return SWT.LINE_DASH;
			case 3:
				return SWT.LINE_DASHDOT;
			case 4:
				return SWT.LINE_DASHDOTDOT;
		}
		return 0;
	}

	@Override
	public int getHighlightLineStyle() {
		return highlightLineStyle;
	}

	@Override
	public int getHighlightLineStyleSWTInt() {
		return swtLineStyle(highlightLineStyle);
	}

	@Override
	public boolean getNoBackground() {
		return noBackground;
	}

	@Override
	public void setNoBackground(final boolean flag) {
		this.noBackground = flag;
		notifyChange(PropertiesKeys.NoBackground.name(), null, null);
	}

	@Override
	public boolean getExpandBox() {
		return expandBox;
	}

	@Override
	public void setExpandBox(final boolean flag) {
		this.expandBox = flag;
		notifyChange(PropertiesKeys.ExpandBox.name(), null, null);
	}

	@Override
	public int getAlpha() {
		return alpha;
	}

	@Override
	public void setAlpha(final int alpha) {
		this.alpha = alpha;
		notifyChange(PropertiesKeys.Alpha.name(), null, null);
	}

}
