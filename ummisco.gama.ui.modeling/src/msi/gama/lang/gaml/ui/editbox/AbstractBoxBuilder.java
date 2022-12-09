/*******************************************************************************************************
 *
 * AbstractBoxBuilder.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

/**
 * The Class AbstractBoxBuilder.
 */
public abstract class AbstractBoxBuilder implements IBoxBuilder {

	/** The name. */
	protected String name;
	
	/** The tab size. */
	protected int tabSize = 1;
	
	/** The text. */
	protected StringBuilder text;
	
	/** The caret offset. */
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
