package ummisco.gama.ui.modeling.editbox;

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
