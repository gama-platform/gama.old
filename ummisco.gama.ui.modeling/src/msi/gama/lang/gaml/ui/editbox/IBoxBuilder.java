/*********************************************************************************************
 *
 * 'IBoxBuilder.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
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

import java.util.List;


public interface IBoxBuilder {
	String getName();
	void setName(String newName);
	
	void setTabSize(int tabSize);
	int getTabSize();
	
	void setCaretOffset(int carretOffset);
	int getCaretOffset();
	
	void setText(StringBuilder sb);
	List<Box> build();
}
