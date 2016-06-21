package ummisco.gama.ui.modeling.editbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

public class Box {
	public int start,offset,end;
	public Box parent;
	public Rectangle rec;
	public boolean isOn;
	public int maxEndOffset;
	public int maxLineLen;
	public int level;
	public int tabsStart = -1;
	public boolean hasChildren;
	private List<Box> children;
	public Object data;
	
	@Override
	public String toString() {
		return "["+start+","+end+","+offset+","+maxEndOffset+"]";
	}

	public boolean intersects(int s, int e) {
		return between(s, start, end) || between(e, start, end) || between(start, s, e) || between(end, s ,e);
	}

	protected boolean between(int m, int s, int e) {
		return s<=m && e>=m;
	}

	public List<Box> children() {
		return children != null ? children : Collections.EMPTY_LIST;
	}
	
	public void addChild(Box box){
		if (children == null) 
			children = new ArrayList<Box>();
		children.add(box);
	}
}
