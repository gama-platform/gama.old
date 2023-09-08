/*******************************************************************************************************
 *
 * Box.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

/**
 * The Class Box.
 */
public class Box {
	
	/** The end. */
	public int start,offset,end;
	
	/** The parent. */
	public Box parent;
	
	/** The rec. */
	public Rectangle rec;
	
	/** The is on. */
	public boolean isOn;
	
	/** The max end offset. */
	public int maxEndOffset;
	
	/** The max line len. */
	public int maxLineLen;
	
	/** The level. */
	public int level;
	
	/** The tabs start. */
	public int tabsStart = -1;
	
	/** The has children. */
	public boolean hasChildren;
	
	/** The children. */
	private List<Box> children;
	
	/** The data. */
	public Object data;
	
	@Override
	public String toString() {
		return "["+start+","+end+","+offset+","+maxEndOffset+"]";
	}

	/**
	 * Intersects.
	 *
	 * @param s the s
	 * @param e the e
	 * @return true, if successful
	 */
	public boolean intersects(int s, int e) {
		return between(s, start, end) || between(e, start, end) || between(start, s, e) || between(end, s ,e);
	}

	/**
	 * Between.
	 *
	 * @param m the m
	 * @param s the s
	 * @param e the e
	 * @return true, if successful
	 */
	protected boolean between(int m, int s, int e) {
		return s<=m && e>=m;
	}

	/**
	 * Children.
	 *
	 * @return the list
	 */
	public List<Box> children() {
		return children != null ? children : Collections.EMPTY_LIST;
	}
	
	/**
	 * Adds the child.
	 *
	 * @param box the box
	 */
	public void addChild(Box box){
		if (children == null) 
			children = new ArrayList<Box>();
		children.add(box);
	}
}
