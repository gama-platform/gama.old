/*********************************************************************************************
 *
 * 'AbstractBoxBuilder.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
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

public abstract class AbstractBoxBuilder implements IBoxBuilder {

	protected String name;
	protected int tabSize = 1;
	protected StringBuilder text;
	protected int caretOffset = -1;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setTabSize(int tabSize) {
		this.tabSize = tabSize;
	}

	public void setText(StringBuilder sb) {
		this.text = sb;
	}

	public int getTabSize() {
		return tabSize;
	}
	
	public void setCaretOffset(int newCarretOffset){
		this.caretOffset = newCarretOffset;
	}
	
	public int getCaretOffset() {
		return caretOffset;
	}

}
