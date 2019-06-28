/*
 * SVGDiagram.java
 *
 *
 * The Salamander Project - 2D and 3D graphics libraries in Java Copyright (C) 2004 Mark McKay
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Mark McKay can be contacted at mark@kitfox.com. Salamander and other projects can be found at http://www.kitfox.com
 *
 * Created on February 18, 2004, 5:04 PM
 */

package msi.gama.util.file.svgsalamander_copy;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;

/**
 * Top level structure in an SVG tree.
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class SVGDiagram implements Serializable {
	public static final long serialVersionUID = 0;

	// Indexes elements within this SVG diagram
	final HashMap<String, SVGElement> idMap = new HashMap<>();

	SVGRoot root;
	final SVGUniverse universe;

	/**
	 * This is used by the SVGRoot to determine the width of the
	 */
	private final Rectangle deviceViewport = new Rectangle(100, 100);

	/**
	 * If true, no attempt will be made to discard geometry based on it being out of bounds. This trades potentially
	 * drawing many out of bounds shapes with having to recalculate bounding boxes every animation iteration.
	 */
	protected boolean ignoreClipHeuristic = false;

	/**
	 * URL which uniquely identifies this document
	 */
	// final URI docRoot;

	/**
	 * URI that uniquely identifies this document. Also used to resolve relative urls. Default base for document.
	 */
	final URI xmlBase;

	/** Creates a new instance of SVGDiagram */
	public SVGDiagram(final URI xmlBase, final SVGUniverse universe) {
		this.universe = universe;
		// this.docRoot = docRoot;
		this.xmlBase = xmlBase;
	}

	/**
	 * Draws this diagram to the passed graphics context
	 */
	public void render(final Graphics2D g) throws SVGException {
		root.render(g);
	}

	/**
	 * Searches thorough the scene graph for all RenderableElements that have shapes that contain the passed point.
	 *
	 * For every shape which contains the pick point, a List containing the path to the node is added to the return
	 * list. That is, the result of SVGElement.getPath() is added for each entry.
	 *
	 * @return the passed in list
	 */
	// public List pick(final Point2D point, final List retVec) throws SVGException {
	// return pick(point, false, retVec);
	// }
	//
	// public List pick(final Point2D point, final boolean boundingBox, List retVec) throws SVGException {
	// if (retVec == null) {
	// retVec = new ArrayList();
	// }
	//
	// root.pick(point, boundingBox, retVec);
	//
	// return retVec;
	// }

	// public List pick(final Rectangle2D pickArea, final List retVec) throws SVGException {
	// return pick(pickArea, false, retVec);
	// }
	//
	// public List pick(final Rectangle2D pickArea, final boolean boundingBox, List retVec) throws SVGException {
	// if (retVec == null) {
	// retVec = new ArrayList();
	// }
	//
	// root.pick(pickArea, new AffineTransform(), boundingBox, retVec);
	//
	// return retVec;
	// }

	public SVGUniverse getUniverse() {
		return universe;
	}

	public URI getXMLBase() {
		return xmlBase;
	}

	// public URL getDocRoot()
	// {
	// return docRoot;
	// }

	public float getWidth() {
		if (root == null) { return 0; }
		return root.getDeviceWidth();
	}

	public float getHeight() {
		if (root == null) { return 0; }
		return root.getDeviceHeight();
	}

	/**
	 * Returns the viewing rectangle of this diagram in device coordinates.
	 */
	public Rectangle2D getViewRect(final Rectangle2D rect) {
		if (root != null) { return root.getDeviceRect(rect); }
		return rect;
	}

	public Rectangle2D getViewRect() {
		return getViewRect(new Rectangle2D.Double());
	}

	public SVGElement getElement(final String name) {
		return idMap.get(name);
	}

	public void setElement(final String name, final SVGElement node) {
		idMap.put(name, node);
	}

	public void removeElement(final String name) {
		idMap.remove(name);
	}

	public SVGRoot getRoot() {
		return root;
	}

	public void setRoot(final SVGRoot root) {
		this.root = root;
	}

	public boolean ignoringClipHeuristic() {
		return ignoreClipHeuristic;
	}

	public void setIgnoringClipHeuristic(final boolean ignoreClipHeuristic) {
		this.ignoreClipHeuristic = ignoreClipHeuristic;
	}

	/**
	 * Updates all attributes in this diagram associated with a time event. Ie, all attributes with track information.
	 */
	// public void updateTime(final double curTime) throws SVGException {
	// if (root == null) { return; }
	// root.updateTime(curTime);
	// }

	public Rectangle getDeviceViewport() {
		return deviceViewport;
	}

	/**
	 * Sets the dimensions of the device being rendered into. This is used by SVGRoot when its x, y, width or height
	 * parameters are specified as percentages.
	 */
	public void setDeviceViewport(final Rectangle deviceViewport) {
		this.deviceViewport.setBounds(deviceViewport);
		if (root != null) {
			try {
				root.build();
			} catch (final SVGException ex) {
				ex.printStackTrace();
			}
		}
	}
}
