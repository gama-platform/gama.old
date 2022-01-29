/*******************************************************************************************************
 *
 * DXFLeader.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import msi.gama.ext.kabeja.dxf.helpers.Point;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFLeader extends DXFEntity {
	
	/** The style name. */
	protected String styleName = "";
	
	/** The arrow head size. */
	protected double arrowHeadSize = 0.0;
	
	/** The text gap. */
	protected double textGap;
	
	/** The scale factor. */
	protected double scaleFactor;
	
	/** The text width. */
	protected double textWidth;
	
	/** The text height. */
	protected double textHeight;
	
	/** The coordinates. */
	protected List<Point> coordinates = new ArrayList<>();
	
	/** The path type. */
	protected int pathType = 0;
	
	/** The creation type. */
	protected int creationType = 0;
	
	/** The hookline directon. */
	protected int hooklineDirecton = 0;
	
	/** The hookline. */
	protected boolean hookline = false;
	
	/** The horizontal direction. */
	protected Point horizontalDirection = new Point();
	
	/** The last offset text. */
	protected Point lastOffsetText = new Point();
	
	/** The last offset insertion. */
	protected Point lastOffsetInsertion = new Point();
	
	/** The arrow enabled. */
	protected boolean arrowEnabled = false;
	
	/** The text ID. */
	protected String textID = "";

	/**
	 * @return Returns the textID.
	 */
	public String getTextID() { return textID; }

	/**
	 * @param textID
	 *            The textID to set.
	 */
	public void setTextID(final String textID) { this.textID = textID; }

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.dxf.DXFEntity#getBounds()
	 */
	@Override
	public Bounds getBounds() {
		Bounds bounds = new Bounds();
		bounds.setValid(false);

		return bounds;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.dxf.DXFEntity#getType()
	 */
	@Override
	public String getType() { return DXFConstants.ENTITY_TYPE_LEADER; }

	/**
	 * @return Returns the arrowHeadSize.
	 */
	public double getArrowHeadSize() { return arrowHeadSize; }

	/**
	 * @param arrowHeadSize
	 *            The arrowHeadSize to set.
	 */
	public void setArrowHeadSize(final double arrowHeadSize) { this.arrowHeadSize = arrowHeadSize; }

	/**
	 * @return Returns the creationType.
	 */
	public int getCreationType() { return creationType; }

	/**
	 * @param creationType
	 *            The creationType to set.
	 */
	public void setCreationType(final int creationType) { this.creationType = creationType; }

	/**
	 * @return Returns the hookline.
	 */
	public boolean isHookline() { return hookline; }

	/**
	 * @param hookline
	 *            The hookline to set.
	 */
	public void setHookline(final boolean hookline) { this.hookline = hookline; }

	/**
	 * @return Returns the hooklineDirecton.
	 */
	public int getHooklineDirecton() { return hooklineDirecton; }

	/**
	 * @param hooklineDirecton
	 *            The hooklineDirecton to set.
	 */
	public void setHooklineDirecton(final int hooklineDirecton) { this.hooklineDirecton = hooklineDirecton; }

	/**
	 * @return Returns the horizontalDirection.
	 */
	public Point getHorizontalDirection() { return horizontalDirection; }

	/**
	 * @param horizontalDirection
	 *            The horizontalDirection to set.
	 */
	public void setHorizontalDirection(final Point horizontalDirection) {
		this.horizontalDirection = horizontalDirection;
	}

	/**
	 * @return Returns the lastOffsetInsertion.
	 */
	public Point getLastOffsetInsertion() { return lastOffsetInsertion; }

	/**
	 * @param lastOffsetInsertion
	 *            The lastOffsetInsertion to set.
	 */
	public void setLastOffsetInsertion(final Point lastOffsetInsertion) {
		this.lastOffsetInsertion = lastOffsetInsertion;
	}

	/**
	 * @return Returns the lastOffsetText.
	 */
	public Point getLastOffsetText() { return lastOffsetText; }

	/**
	 * @param lastOffsetText
	 *            The lastOffsetText to set.
	 */
	public void setLastOffsetText(final Point lastOffsetText) { this.lastOffsetText = lastOffsetText; }

	/**
	 * @return Returns the pathType.
	 */
	public int getPathType() { return pathType; }

	/**
	 * @param pathType
	 *            The pathType to set.
	 */
	public void setPathType(final int pathType) { this.pathType = pathType; }

	/**
	 * @return Returns the scaleFactor.
	 */
	public double getScaleFactor() { return scaleFactor; }

	/**
	 * @param scaleFactor
	 *            The scaleFactor to set.
	 */
	public void setScaleFactor(final double scaleFactor) { this.scaleFactor = scaleFactor; }

	/**
	 * @return Returns the styleName.
	 */
	public String getStyleNameID() { return styleName; }

	/**
	 * @param styleName
	 *            The styleName to set.
	 */
	public void setStyleNameID(final String styleName) { this.styleName = styleName; }

	/**
	 * @return Returns the textGap.
	 */
	public double getTextGap() { return textGap; }

	/**
	 * @param textGap
	 *            The textGap to set.
	 */
	public void setTextGap(final double textGap) { this.textGap = textGap; }

	/**
	 * @return Returns the textHeight.
	 */
	public double getTextHeight() { return textHeight; }

	/**
	 * @param textHeight
	 *            The textHeight to set.
	 */
	public void setTextHeight(final double textHeight) { this.textHeight = textHeight; }

	/**
	 * @return Returns the textWidth.
	 */
	public double getTextWidth() { return textWidth; }

	/**
	 * @param textWidth
	 *            The textWidth to set.
	 */
	public void setTextWidth(final double textWidth) { this.textWidth = textWidth; }

	/**
	 * Adds the coordinate.
	 *
	 * @param vertex the vertex
	 */
	public void addCoordinate(final Point vertex) {
		coordinates.add(vertex);
	}

	/**
	 * Gets the coordinate count.
	 *
	 * @return the coordinate count
	 */
	public int getCoordinateCount() { return this.coordinates.size(); }

	/**
	 * Gets the coordinate at.
	 *
	 * @param index the index
	 * @return the coordinate at
	 */
	public Point getCoordinateAt(final int index) {
		return this.coordinates.get(index);
	}

	/**
	 * Gets the coordinate iterator.
	 *
	 * @return the coordinate iterator
	 */
	public Iterator getCoordinateIterator() { return this.coordinates.iterator(); }

	/**
	 * @return Returns the arrowEnabled.
	 */
	public boolean isArrowEnabled() { return arrowEnabled; }

	/**
	 * @param arrowEnabled
	 *            The arrowEnabled to set.
	 */
	public void setArrowEnabled(final boolean arrowEnabled) { this.arrowEnabled = arrowEnabled; }

	/**
	 * Checks if is spline path.
	 *
	 * @return true, if is spline path
	 */
	public boolean isSplinePath() { return this.pathType == 1; }

	@Override
	public double getLength() {
		// TODO Auto-generated method stub
		return 0;
	}
}
