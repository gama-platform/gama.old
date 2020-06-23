/*********************************************************************************************
 *
 * 'IBoxSettings.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;



public interface IBoxSettings {
	enum PropertiesKeys {
		ALL,Enabled, Name, BorderColor, HighlightColor, BorderWidth, HighlightWidth, RoundBox, HighlightOne, FillSelected, FillSelectedColor, Builder, Colors, Color,
		HighlightDrawLine, BorderDrawLine, FillGradient, FillGradientColor, FillOnMove, CirculateLevelColors, FillKeyModifier, FileNames, HighlightColorType, BorderColorType,
		HighlightLineStyle,BorderLineStyle,NoBackground,ExpandBox,Alpha,
	};
	
	void addPropertyChangeListener(IPropertyChangeListener listener);
	void removePropertyChangeListener(IPropertyChangeListener listener);
	
	String getName();
	void setName(String newName);
	
	boolean getEnabled();
	void setEnabled(boolean flag);
	
	void setFileNames(Collection<String> fileNames);
	Collection<String> getFileNames();

	String getText();
	void setText(String newText);
	
	void copyFrom(IBoxSettings other);
	void load(String string);
	String export();
	void export(OutputStream stream) throws Exception;
	void load(InputStream stream) throws Exception;
	
	void dispose();

	void setBorderRGB(RGB selectNewColor);
	Color getBorderColor();
	void setBorderWidth(int selectionIndex);
	int getBorderWidth();
	void setRoundBox(boolean selection);
	boolean getRoundBox();
	void setHighlightRGB(RGB selectNewColor);
	void setHighlightWidth(int selectionIndex);
	void setHighlightOne(boolean selection);
	Color getHighlightColor();
	int getHighlightWidth();
	boolean getHighlightOne();
	void setFillSelectedRGB(RGB selectNewColor);
	void setFillSelected(boolean selection);
	boolean getFillSelected();
	Color getFillSelectedColor();
	void setBuilder(String name);
	String getBuilder();
	Color[] getColors();
	void setColorsRGB(RGB[] gradient);
	int getColorsSize();
	void setColorsSize(int i);
	Color getColor(int level);
	void setColor(int level, RGB open);
	void setHighlightDrawLine(boolean flag);
	boolean getHighlightDrawLine();
	void setBorderDrawLine(boolean flag);
	boolean getBorderDrawLine();
	void setFillGradient(boolean flag);
	boolean getFillGradient();
	void setFillGradientColorRGB(RGB color);
	Color getFillGradientColor();
	void setFillOnMove(boolean selection);
	boolean getFillOnMove();
	boolean getCirculateLevelColors();
	void setCirculateLevelColors(boolean flag);
	void setFillKeyModifier(String key);
	String getFillKeyModifier();
	int getFillKeyModifierSWTInt();
	
	Color getBorderColor(int level);
	Color getHighlightColor(int level);
	void setBorderColorType(int selectionIndex);
	int getBorderColorType();
	void setHighlightColorType(int selectionIndex);
	int getHighlightColorType();
	void setBorderLineStyle(int selectionIndex);
	void setHighlightLineStyle(int selectionIndex);
	int getBorderLineStyle();
	int getBorderLineStyleSWTInt();
	int getHighlightLineStyle();
	int getHighlightLineStyleSWTInt();
	
	boolean getNoBackground();
	void setNoBackground(boolean flag);
	boolean getExpandBox();
	void setExpandBox(boolean flag);
	int getAlpha();
	void setAlpha(int alpha);
}
