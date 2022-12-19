/*******************************************************************************************************
 *
 * IBoxBuilder.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.List;


/**
 * The Interface IBoxBuilder.
 */
public interface IBoxBuilder {
	
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
	 * Sets the tab size.
	 *
	 * @param tabSize the new tab size
	 */
	void setTabSize(int tabSize);
	
	/**
	 * Gets the tab size.
	 *
	 * @return the tab size
	 */
	int getTabSize();
	
	/**
	 * Sets the caret offset.
	 *
	 * @param carretOffset the new caret offset
	 */
	void setCaretOffset(int carretOffset);
	
	/**
	 * Gets the caret offset.
	 *
	 * @return the caret offset
	 */
	int getCaretOffset();
	
	/**
	 * Sets the text.
	 *
	 * @param sb the new text
	 */
	void setText(StringBuilder sb);
	
	/**
	 * Builds the.
	 *
	 * @return the list
	 */
	List<Box> build();
}
