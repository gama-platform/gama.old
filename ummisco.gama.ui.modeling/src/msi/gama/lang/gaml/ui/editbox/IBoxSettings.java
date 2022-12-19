/*******************************************************************************************************
 *
 * IBoxSettings.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;



/**
 * The Interface IBoxSettings.
 */
public interface IBoxSettings {
	
	/**
	 * The Enum PropertiesKeys.
	 */
	enum PropertiesKeys {
		
		/** The all. */
		ALL,
/** The Enabled. */
Enabled, 
 /** The Name. */
 Name, 
 /** The Border color. */
 BorderColor, 
 /** The Highlight color. */
 HighlightColor, 
 /** The Border width. */
 BorderWidth, 
 /** The Highlight width. */
 HighlightWidth, 
 /** The Round box. */
 RoundBox, 
 /** The Highlight one. */
 HighlightOne, 
 /** The Fill selected. */
 FillSelected, 
 /** The Fill selected color. */
 FillSelectedColor, 
 /** The Builder. */
 Builder, 
 /** The Colors. */
 Colors, 
 /** The Color. */
 Color,
		
		/** The Highlight draw line. */
		HighlightDrawLine, 
 /** The Border draw line. */
 BorderDrawLine, 
 /** The Fill gradient. */
 FillGradient, 
 /** The Fill gradient color. */
 FillGradientColor, 
 /** The Fill on move. */
 FillOnMove, 
 /** The Circulate level colors. */
 CirculateLevelColors, 
 /** The Fill key modifier. */
 FillKeyModifier, 
 /** The File names. */
 FileNames, 
 /** The Highlight color type. */
 HighlightColorType, 
 /** The Border color type. */
 BorderColorType,
		
		/** The Highlight line style. */
		HighlightLineStyle,
/** The Border line style. */
BorderLineStyle,
/** The No background. */
NoBackground,
/** The Expand box. */
ExpandBox,
/** The Alpha. */
Alpha,
	};
	
	/**
	 * Adds the property change listener.
	 *
	 * @param listener the listener
	 */
	void addPropertyChangeListener(IPropertyChangeListener listener);
	
	/**
	 * Removes the property change listener.
	 *
	 * @param listener the listener
	 */
	void removePropertyChangeListener(IPropertyChangeListener listener);
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();
	
	/**
	 * Sets the name.
	 *
	 * @param newName the new name
	 */
	void setName(String newName);
	
	/**
	 * Gets the enabled.
	 *
	 * @return the enabled
	 */
	boolean getEnabled();
	
	/**
	 * Sets the enabled.
	 *
	 * @param flag the new enabled
	 */
	void setEnabled(boolean flag);
	
	/**
	 * Sets the file names.
	 *
	 * @param fileNames the new file names
	 */
	void setFileNames(Collection<String> fileNames);
	
	/**
	 * Gets the file names.
	 *
	 * @return the file names
	 */
	Collection<String> getFileNames();

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	String getText();
	
	/**
	 * Sets the text.
	 *
	 * @param newText the new text
	 */
	void setText(String newText);
	
	/**
	 * Copy from.
	 *
	 * @param other the other
	 */
	void copyFrom(IBoxSettings other);
	
	/**
	 * Load.
	 *
	 * @param string the string
	 */
	void load(String string);
	
	/**
	 * Export.
	 *
	 * @return the string
	 */
	String export();
	
	/**
	 * Export.
	 *
	 * @param stream the stream
	 * @throws Exception the exception
	 */
	void export(OutputStream stream) throws Exception;
	
	/**
	 * Load.
	 *
	 * @param stream the stream
	 * @throws Exception the exception
	 */
	void load(InputStream stream) throws Exception;
	
	/**
	 * Dispose.
	 */
	void dispose();

	/**
	 * Sets the border RGB.
	 *
	 * @param selectNewColor the new border RGB
	 */
	void setBorderRGB(RGB selectNewColor);
	
	/**
	 * Gets the border color.
	 *
	 * @return the border color
	 */
	Color getBorderColor();
	
	/**
	 * Sets the border width.
	 *
	 * @param selectionIndex the new border width
	 */
	void setBorderWidth(int selectionIndex);
	
	/**
	 * Gets the border width.
	 *
	 * @return the border width
	 */
	int getBorderWidth();
	
	/**
	 * Sets the round box.
	 *
	 * @param selection the new round box
	 */
	void setRoundBox(boolean selection);
	
	/**
	 * Gets the round box.
	 *
	 * @return the round box
	 */
	boolean getRoundBox();
	
	/**
	 * Sets the highlight RGB.
	 *
	 * @param selectNewColor the new highlight RGB
	 */
	void setHighlightRGB(RGB selectNewColor);
	
	/**
	 * Sets the highlight width.
	 *
	 * @param selectionIndex the new highlight width
	 */
	void setHighlightWidth(int selectionIndex);
	
	/**
	 * Sets the highlight one.
	 *
	 * @param selection the new highlight one
	 */
	void setHighlightOne(boolean selection);
	
	/**
	 * Gets the highlight color.
	 *
	 * @return the highlight color
	 */
	Color getHighlightColor();
	
	/**
	 * Gets the highlight width.
	 *
	 * @return the highlight width
	 */
	int getHighlightWidth();
	
	/**
	 * Gets the highlight one.
	 *
	 * @return the highlight one
	 */
	boolean getHighlightOne();
	
	/**
	 * Sets the fill selected RGB.
	 *
	 * @param selectNewColor the new fill selected RGB
	 */
	void setFillSelectedRGB(RGB selectNewColor);
	
	/**
	 * Sets the fill selected.
	 *
	 * @param selection the new fill selected
	 */
	void setFillSelected(boolean selection);
	
	/**
	 * Gets the fill selected.
	 *
	 * @return the fill selected
	 */
	boolean getFillSelected();
	
	/**
	 * Gets the fill selected color.
	 *
	 * @return the fill selected color
	 */
	Color getFillSelectedColor();
	
	/**
	 * Sets the builder.
	 *
	 * @param name the new builder
	 */
	void setBuilder(String name);
	
	/**
	 * Gets the builder.
	 *
	 * @return the builder
	 */
	String getBuilder();
	
	/**
	 * Gets the colors.
	 *
	 * @return the colors
	 */
	Color[] getColors();
	
	/**
	 * Sets the colors RGB.
	 *
	 * @param gradient the new colors RGB
	 */
	void setColorsRGB(RGB[] gradient);
	
	/**
	 * Gets the colors size.
	 *
	 * @return the colors size
	 */
	int getColorsSize();
	
	/**
	 * Sets the colors size.
	 *
	 * @param i the new colors size
	 */
	void setColorsSize(int i);
	
	/**
	 * Gets the color.
	 *
	 * @param level the level
	 * @return the color
	 */
	Color getColor(int level);
	
	/**
	 * Sets the color.
	 *
	 * @param level the level
	 * @param open the open
	 */
	void setColor(int level, RGB open);
	
	/**
	 * Sets the highlight draw line.
	 *
	 * @param flag the new highlight draw line
	 */
	void setHighlightDrawLine(boolean flag);
	
	/**
	 * Gets the highlight draw line.
	 *
	 * @return the highlight draw line
	 */
	boolean getHighlightDrawLine();
	
	/**
	 * Sets the border draw line.
	 *
	 * @param flag the new border draw line
	 */
	void setBorderDrawLine(boolean flag);
	
	/**
	 * Gets the border draw line.
	 *
	 * @return the border draw line
	 */
	boolean getBorderDrawLine();
	
	/**
	 * Sets the fill gradient.
	 *
	 * @param flag the new fill gradient
	 */
	void setFillGradient(boolean flag);
	
	/**
	 * Gets the fill gradient.
	 *
	 * @return the fill gradient
	 */
	boolean getFillGradient();
	
	/**
	 * Sets the fill gradient color RGB.
	 *
	 * @param color the new fill gradient color RGB
	 */
	void setFillGradientColorRGB(RGB color);
	
	/**
	 * Gets the fill gradient color.
	 *
	 * @return the fill gradient color
	 */
	Color getFillGradientColor();
	
	/**
	 * Sets the fill on move.
	 *
	 * @param selection the new fill on move
	 */
	void setFillOnMove(boolean selection);
	
	/**
	 * Gets the fill on move.
	 *
	 * @return the fill on move
	 */
	boolean getFillOnMove();
	
	/**
	 * Gets the circulate level colors.
	 *
	 * @return the circulate level colors
	 */
	boolean getCirculateLevelColors();
	
	/**
	 * Sets the circulate level colors.
	 *
	 * @param flag the new circulate level colors
	 */
	void setCirculateLevelColors(boolean flag);
	
	/**
	 * Sets the fill key modifier.
	 *
	 * @param key the new fill key modifier
	 */
	void setFillKeyModifier(String key);
	
	/**
	 * Gets the fill key modifier.
	 *
	 * @return the fill key modifier
	 */
	String getFillKeyModifier();
	
	/**
	 * Gets the fill key modifier SWT int.
	 *
	 * @return the fill key modifier SWT int
	 */
	int getFillKeyModifierSWTInt();
	
	/**
	 * Gets the border color.
	 *
	 * @param level the level
	 * @return the border color
	 */
	Color getBorderColor(int level);
	
	/**
	 * Gets the highlight color.
	 *
	 * @param level the level
	 * @return the highlight color
	 */
	Color getHighlightColor(int level);
	
	/**
	 * Sets the border color type.
	 *
	 * @param selectionIndex the new border color type
	 */
	void setBorderColorType(int selectionIndex);
	
	/**
	 * Gets the border color type.
	 *
	 * @return the border color type
	 */
	int getBorderColorType();
	
	/**
	 * Sets the highlight color type.
	 *
	 * @param selectionIndex the new highlight color type
	 */
	void setHighlightColorType(int selectionIndex);
	
	/**
	 * Gets the highlight color type.
	 *
	 * @return the highlight color type
	 */
	int getHighlightColorType();
	
	/**
	 * Sets the border line style.
	 *
	 * @param selectionIndex the new border line style
	 */
	void setBorderLineStyle(int selectionIndex);
	
	/**
	 * Sets the highlight line style.
	 *
	 * @param selectionIndex the new highlight line style
	 */
	void setHighlightLineStyle(int selectionIndex);
	
	/**
	 * Gets the border line style.
	 *
	 * @return the border line style
	 */
	int getBorderLineStyle();
	
	/**
	 * Gets the border line style SWT int.
	 *
	 * @return the border line style SWT int
	 */
	int getBorderLineStyleSWTInt();
	
	/**
	 * Gets the highlight line style.
	 *
	 * @return the highlight line style
	 */
	int getHighlightLineStyle();
	
	/**
	 * Gets the highlight line style SWT int.
	 *
	 * @return the highlight line style SWT int
	 */
	int getHighlightLineStyleSWTInt();
	
	/**
	 * Gets the no background.
	 *
	 * @return the no background
	 */
	boolean getNoBackground();
	
	/**
	 * Sets the no background.
	 *
	 * @param flag the new no background
	 */
	void setNoBackground(boolean flag);
	
	/**
	 * Gets the expand box.
	 *
	 * @return the expand box
	 */
	boolean getExpandBox();
	
	/**
	 * Sets the expand box.
	 *
	 * @param flag the new expand box
	 */
	void setExpandBox(boolean flag);
	
	/**
	 * Gets the alpha.
	 *
	 * @return the alpha
	 */
	int getAlpha();
	
	/**
	 * Sets the alpha.
	 *
	 * @param alpha the new alpha
	 */
	void setAlpha(int alpha);
}
