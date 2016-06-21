package ummisco.gama.ui.modeling.editbox;

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
