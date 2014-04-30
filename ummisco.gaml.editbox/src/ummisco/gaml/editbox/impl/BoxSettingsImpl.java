package ummisco.gaml.editbox.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import ummisco.gaml.editbox.*;

public class BoxSettingsImpl implements IBoxSettings {

	private static final BoxSettingsImpl DEFAULT = new BoxSettingsImpl(){{
		name = "Default";
		borderWidth = 1;
		highlightWidth = 1;
		highlightOne = true;
		roundBox = true;
		borderColor = new Color(null, 0, 187, 187); 
		highlightColor = new Color(null, 0, 0, 0);
		builder = "Text";
		boxColors = new Color[]{null,new Color(null, 208, 221, 155), new Color(null, 205, 216, 185), new Color(null, 233, 245, 139)};
		fillSelected = true;
		fillColor = new Color(null,255, 255, 196);
		fillKeyModifier = "Alt";
	}};

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



	public void copyFrom(IBoxSettings other) {
		BoxSettingsImpl o = (BoxSettingsImpl) other;
		name = o.getName();
		enabled = o.getEnabled();
		fileNames = o.fileNames == null ? null : new ArrayList<String>(o.fileNames);
		borderColor = setColorCopy(borderColor, o.borderColor);
		highlightColor = setColorCopy(highlightColor, o.highlightColor);
		fillColor = setColorCopy(fillColor, o.fillColor);
		borderWidth = o.borderWidth;
		highlightWidth = o.highlightWidth;
		highlightOne = o.highlightOne;
		fillSelected = o.fillSelected;
		roundBox = o.roundBox;
		Color[] newBoxColors = copyColors(o);
		Color[] oldBoxColors = boxColors;
		boxColors = newBoxColors;
		disposeColors(oldBoxColors);
		builder = o.builder;
		borderDrawLine = o.borderDrawLine;
		highlightDrawLine = o.highlightDrawLine;
		fillGradient = o.fillGradient;
		fillGradientColor = setColorCopy(fillGradientColor,o.fillGradientColor);
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

	private Color[] copyColors(BoxSettingsImpl o) {
		Color[] newBoxColors = o.boxColors == null ? null : new Color[o.boxColors.length];
		if (newBoxColors != null)
			for (int i = 0; i < newBoxColors.length; i++) {
				Color c = o.boxColors[i];
				if (c != null)
					newBoxColors[i] = new Color(null, c.getRGB());
			}
		return newBoxColors;
	}

	private Color[] disposeColors(Color[] oldBoxColors) {
		if (oldBoxColors != null) {
			for (int i = 0; i < oldBoxColors.length; i++)
				if (oldBoxColors[i] != null)
					oldBoxColors[i].dispose();
		}
		return null;
	}

	protected Color setColorCopy(Color old, Color newColor) {
		if (old != null)
			old.dispose();
		return newColor == null ? null : new Color(null, newColor.getRGB());
	}

	public String export() {
		return new StringExternalization().export(this);
	}

	public void load(String string) {
		StringExternalization ext = new StringExternalization();
		boolean error = false;
		if (string != null)
			try {
				ext.load(string, this);
			} catch (Exception e) {
				EditBox.logError(this, "Cannot load EditBox settings from string: "+string, e);
				error = true;
			}
		if (error || string == null) {
			this.copyFrom(DEFAULT);
		}
		notifyChange(PropertiesKeys.ALL.name(), null, null);
	}

	public void export(OutputStream stream) throws Exception{
		new StringExternalization().export(stream,this);
	}

	public void load(InputStream stream) throws Exception{
		if (stream==null) 
			load((String)null);
		else
			new StringExternalization().load(stream,this);
		notifyChange(PropertiesKeys.ALL.name(), null, null);
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean flag) {
		this.enabled = flag;
		notifyChange(PropertiesKeys.Enabled.name(), null, flag);
	}

	public void setFileNames(Collection<String> fileNames) {
		this.fileNames = fileNames;
		notifyChange(PropertiesKeys.FileNames.name(), null, null);
	}

	public Collection<String> getFileNames() {
		return fileNames;
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
		notifyChange(PropertiesKeys.Name.name(), null, null);
	}

	public String getText() {
		return text;
	}

	public void setText(String newText) {
		this.text = newText;
	}

	protected Color setColor0(Color old, Color c) {
		disposeColor(old);
		return c;
	}

	protected Color setColor0(Color old, RGB c) {
		if (c == null)
			return old;
		return setColor0(old, new Color(null, c));
	}

	protected Color disposeColor(Color c) {
		if (c != null)
			c.dispose();
		return null;
	}

	public void dispose() {
		borderColor = disposeColor(borderColor);
		highlightColor = disposeColor(highlightColor);
		fillColor = disposeColor(fillColor);
		disposeColors(boxColors);
		boxColors = null;
		if (listeners!=null)
			listeners.clear();
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderRGB(RGB c) {
		borderColor = setColor0(borderColor, c);
		notifyChange(PropertiesKeys.BorderColor.name(), null, null);
	}

	public void setBorderWidth(int w) {
		borderWidth = w;
		notifyChange(PropertiesKeys.BorderWidth.name(), null, null);
	}

	public void setRoundBox(boolean flag) {
		roundBox = flag;
		notifyChange(PropertiesKeys.RoundBox.name(), null, null);
	}

	public boolean getRoundBox() {
		return roundBox;
	}

	public Color getHighlightColor() {
		return highlightColor;
	}

	public boolean getHighlightOne() {
		return highlightOne;
	}

	public int getHighlightWidth() {
		return highlightWidth;
	}

	public void setHighlightOne(boolean flag) {
		highlightOne = flag;
		notifyChange(PropertiesKeys.HighlightOne.name(), null, null);
	}

	public void setHighlightRGB(RGB highlightRGB) {
		highlightColor = setColor0(highlightColor, highlightRGB);
		notifyChange(PropertiesKeys.HighlightColor.name(), null, null);
	}

	public void setHighlightWidth(int w) {
		highlightWidth = w;
		notifyChange(PropertiesKeys.HighlightWidth.name(), null, null);
	}

	public boolean getFillSelected() {
		return fillSelected;
	}

	public Color getFillSelectedColor() {
		return fillColor;
	}

	public void setFillSelected(boolean flag) {
		fillSelected = flag;
		notifyChange(PropertiesKeys.FillSelected.name(), null, null);
	}

	public void setFillSelectedRGB(RGB newColor) {
		fillColor = setColor0(fillColor, newColor);
		notifyChange(PropertiesKeys.FillSelectedColor.name(), null, null);
	}

	public void setBuilder(String name) {
		builder = name;
		notifyChange(PropertiesKeys.Builder.name(), null, null);
	}

	public String getBuilder() {
		return builder;
	}

	public boolean getBorderDrawLine() {
		return borderDrawLine;
	}

	public boolean getFillGradient() {
		return fillGradient;
	}

	public Color getFillGradientColor() {
		return fillGradientColor;
	}

	public boolean getHighlightDrawLine() {
		return highlightDrawLine;
	}

	public void setBorderDrawLine(boolean flag) {
		borderDrawLine = flag;
		notifyChange(PropertiesKeys.BorderDrawLine.name(), null, null);
	}

	public void setFillGradient(boolean flag) {
		fillGradient = flag;
		notifyChange(PropertiesKeys.FillGradient.name(), null, null);
	}

	public void setFillGradientColorRGB(RGB color) {
		fillGradientColor = setColor0(fillGradientColor, color);
		notifyChange(PropertiesKeys.FillGradientColor.name(), null, null);
	}

	public void setHighlightDrawLine(boolean flag) {
		highlightDrawLine = flag;
		notifyChange(PropertiesKeys.HighlightDrawLine.name(), null, null);
	}

	public Color[] getColors() {
		return boxColors;
	}

	public boolean getFillOnMove() {
		return fillOnMove;
	}

	public void setFillOnMove(boolean selection) {
		fillOnMove = selection;
		notifyChange(PropertiesKeys.FillOnMove.name(), null, null);
	}


	public void setColorsRGB(RGB[] gradient) {
		if (gradient == null) {
			disposeColors(boxColors);
			boxColors = null;
		} else {
			Color[] c = new Color[gradient.length];
			for (int i = 0; i < gradient.length; i++)
				c[i] = new Color(null, gradient[i]);
			disposeColors(boxColors);
			boxColors = c;
		}
		notifyChange(PropertiesKeys.Colors.name(), null, null);
	}

	public void setColorsSize(int n) {
		n++;
		Color[] newColors = null;
		if (n == 0) {
			disposeColors(boxColors);
		} else if (boxColors != null) {
			newColors = new Color[n];
			for (int i = 0; i < n; i++) {
				if (i >= boxColors.length)
					break;
				newColors[i] = boxColors[i];
			}
			for (int i = n; i < boxColors.length; i++)
				disposeColor(boxColors[i]);
		} else 
			newColors = new Color[n];
		boxColors = newColors;
		notifyChange(PropertiesKeys.Colors.name(), null, null);
	}

	public int getColorsSize(){
		return boxColors == null ? 0 : boxColors.length - 1;
	}
	
	public Color getBorderColor(int level){
		if (borderColorType<1) return borderColor;

		if (boxColors == null)
			return null;
		
		if (borderColors != null && borderColors.length != boxColors.length)
			borderColors = disposeColors(borderColors);
		
		if (borderColors == null)
			borderColors = new Color[boxColors.length];
		
		int idx = getColorIndex(level);
		if (idx > -1){
			if (borderColors[idx]!=null) return borderColors[idx];
			if (boxColors[idx]==null) return null;
			return borderColors[idx] = calculateDarkerColor(boxColors[idx],borderColorType);
		}
		
		return null;
	}
	
	private final Color calculateDarkerColor(Color c, int type) {
		return new Color(null,calcDarker(c.getRed(),type),calcDarker(c.getGreen(),type),calcDarker(c.getBlue(),type));
	}

	private final int calcDarker(int r, int type){
		return r - r*type/4;
	}
	
	public Color getHighlightColor(int level){
		if (highlightColorType<1) return highlightColor;
		
		if (boxColors == null)
			return null;
		
		if (highlightColors != null && highlightColors.length != boxColors.length)
			highlightColors = disposeColors(highlightColors);
		
		if (highlightColors == null)
			highlightColors = new Color[boxColors.length];
		
		int idx = getColorIndex(level);
		if (idx > -1){
			if (highlightColors[idx]!=null) return highlightColors[idx];
			if (boxColors[idx]==null) return null;
			return highlightColors[idx] = calculateDarkerColor(boxColors[idx],highlightColorType);
		}
		
		return null;
	}
	
	public void setBorderColorType(int selectionIndex){
		this.borderColorType = selectionIndex;
		notifyChange(PropertiesKeys.BorderColorType.name(), null, null);
	}
	
	public int getBorderColorType(){
		return borderColorType;
	}
	
	public void setHighlightColorType(int selectionIndex){
		this.highlightColorType = selectionIndex;
		notifyChange(PropertiesKeys.HighlightColorType.name(), null, null);	
	}
	
	public int getHighlightColorType(){
		return highlightColorType;
	}
	
	public int getColorIndex(int level){
		if (boxColors.length == 1 && noBackground && level > 0)
			return -1;
		if (!circulateLevelColors && boxColors != null && boxColors.length <= level && boxColors.length>0)
			return boxColors.length - 1;
		if (boxColors!=null && boxColors.length>0 && level > -1) {
			return getNColor0(level);
		}
		return -1;
	}
	
	public Color getColor(int level) {
		int idx= getColorIndex(level);
		if (idx > -1)return boxColors[idx];
		return null;
	}
	
	int getNColor0(int n){
		int len = boxColors.length;
		if (len < 2) return 0;
		int x = n % (len + len - 2);
		if (x < len)
			 return x;
		else
			return  len - (x - len + 2) ;
	}
	
	public void setColor(int level, RGB rgb) {
		if (boxColors!=null && boxColors.length>0 && level > -1)
			boxColors[level % boxColors.length] = setColor0(boxColors[level % boxColors.length], rgb);
		notifyChange(PropertiesKeys.Color.name(), null, null);
	}

	public boolean getCirculateLevelColors(){
		return circulateLevelColors;
	}
	
	public void setCirculateLevelColors(boolean flag){
		circulateLevelColors = flag;
		notifyChange(PropertiesKeys.CirculateLevelColors.name(), null, null);
	}

	public void setFillKeyModifier(String key){
		fillKeyModifier = key;
		notifyChange(PropertiesKeys.FillKeyModifier.name(),null,null);
	}
	public String getFillKeyModifier(){
		return fillKeyModifier;
	}
	public int getFillKeyModifierSWTInt(){
		if (fillKeyModifier == null || fillKeyModifier.length() == 0)
			return 0;
		if ("Alt".equals(fillKeyModifier))
			return SWT.ALT;
		if ("Ctrl".equals(fillKeyModifier))
			return SWT.CTRL;
		if ("Shift".equals(fillKeyModifier))
			return SWT.SHIFT;
		return 0;
	}

	class StringExternalization {

		private static final String COMMENT = "Editbox Eclipse Plugin Settings";

		public String export(BoxSettingsImpl b) {
			Properties p = toProperies(b);
			return propertiesToString(p);
		}

		public void load(InputStream stream, BoxSettingsImpl b) throws Exception {
			Properties p = new Properties();
			p.load(stream);
			load(p,b);
		}

		public void export(OutputStream stream, BoxSettingsImpl b) throws Exception {
			Properties p = toProperies(b);
			p.store(stream, COMMENT);

		}

		public void load(String s, BoxSettingsImpl b) throws Exception {
			Properties p = loadProperties(s);
			load(p,b);
		}

		private void load(Properties p, BoxSettingsImpl b) {
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
		
		private Properties toProperies(BoxSettingsImpl b) {
			Properties p = new Properties();
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

		private Properties loadProperties(String s) throws IOException {
			Properties p = new Properties();
			p.load(new StringBufferInputStream(s));
			return p;
		}

		private String propertiesToString(Properties p) {
			try {
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				p.store(bo, "COMMENT");
				return bo.toString();
			} catch (IOException e) {
				EditBox.logError(this, "Cannot convert propeties to sring", e);
			}
			return "";
		}


		private Color[] parseColorsArray(Object o) {
			if (o == null || o.equals("null"))
				return null;
			String[] s = o.toString().split("-");
			Color[] c = new Color[s.length];
			for (int i = 0; i < c.length; i++)
				c[i] = parseColor(s[i]);
			return c;
		}

		private boolean parseBool(Object o) {
			if (o == null)
				return false;
			return o.equals("true");
		}

		private int parseInt(Object o) {
			if (o == null || o.equals("null"))
				return 0;
			return Integer.parseInt(o.toString());
		}

		private Color parseColor(Object o) {
			if (o == null || o.equals("null"))
				return null;
			String c = o.toString();
			if (c.length() != 6)
				return null;
			try {
				int r = Integer.parseInt(c.substring(0, 2), 16);
				int g = Integer.parseInt(c.substring(2, 4), 16);
				int b = Integer.parseInt(c.substring(4, 6), 16);
				return new Color(null, r, g, b);
			} catch (Exception e) {
				return null;
			}
		}

		private String parseString(Object o) {
			if (o == null || o.equals("null"))
				return null;
			return o.toString();
		}

		private String toS(boolean b) {
			return b ? "true" : "false";
		}

		private String toS(Color[] colors) {
			StringBuilder sb = new StringBuilder();
			if (colors == null || colors.length == 0)
				sb.append("null");
			else {
				for (int i = 0; i < colors.length; i++) {
					if (i > 0)
						sb.append("-");
					sb.append(toS(colors[i]));
				}
			}
			return sb.toString();
		}

		private String toS(int i) {
			return Integer.toString(i);
		}

		private String toS(Color c) {
			return c == null ? "null" : toHex(c.getRed()) + toHex(c.getGreen()) + toHex(c.getBlue());
		}

		private String toHex(int v) {
			String s = Integer.toHexString(v);
			if (s.length() == 1)
				s = "0" + s;
			return s;
		}

		private String toS(String s) {
			return s == null ? "null" : s;
		}
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		if (listeners == null)
			listeners = new ArrayList<IPropertyChangeListener>();
		listeners.add(listener);
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		if (listeners != null) listeners.remove(listener);
	}
	
	protected void notifyChange(String propertyName, Object oldValue, Object newValue){
		if (listeners!=null) {
			PropertyChangeEvent e = new PropertyChangeEvent(this,propertyName,oldValue,newValue);
			for (IPropertyChangeListener listener : listeners) 
				listener.propertyChange(e);
		}
	}

	public void setBorderLineStyle(int selectionIndex) {
		this.borderLineStyle = selectionIndex;
		notifyChange(PropertiesKeys.BorderLineStyle.name(), null, null);
	}

	public void setHighlightLineStyle(int selectionIndex) {
		this.highlightLineStyle = selectionIndex;
		notifyChange(PropertiesKeys.HighlightLineStyle.name(), null, null);
	}

	public int getBorderLineStyle() {
		return borderLineStyle;
	}

	public int getBorderLineStyleSWTInt() {
		return swtLineStyle(borderLineStyle);
	}

	private int swtLineStyle(int index) {
		switch (index) {
		case 0: return SWT.LINE_SOLID;
		case 1: return SWT.LINE_DOT;
		case 2: return SWT.LINE_DASH;
		case 3: return SWT.LINE_DASHDOT;
		case 4: return SWT.LINE_DASHDOTDOT;
		}
		return 0;
	}

	public int getHighlightLineStyle() {
		return highlightLineStyle;
	}

	public int getHighlightLineStyleSWTInt() {
		return swtLineStyle(highlightLineStyle);
	}

	public boolean getNoBackground() {
		return noBackground;
	}

	public void setNoBackground(boolean flag) {
		this.noBackground = flag;
		notifyChange(PropertiesKeys.NoBackground.name(), null, null);
	}

	public boolean getExpandBox() {
		return expandBox;
	}

	public void setExpandBox(boolean flag) {
		this.expandBox = flag;
		notifyChange(PropertiesKeys.ExpandBox.name(), null, null);
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
		notifyChange(PropertiesKeys.Alpha.name(), null, null);
	}

}
