/*******************************************************************************************************
 *
 * DXFMLine.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf;

import java.util.ArrayList;
import java.util.List;

import msi.gama.ext.kabeja.dxf.helpers.DXFMLineSegment;
import msi.gama.ext.kabeja.dxf.helpers.MLineConverter;
import msi.gama.ext.kabeja.dxf.helpers.Point;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFMLine extends DXFEntity {

	/** The Constant JUSTIFICATION_TOP. */
	public final static int JUSTIFICATION_TOP = 0;

	/** The Constant JUSTIFICATION_ZERO. */
	public final static int JUSTIFICATION_ZERO = 1;

	/** The Constant JUSTIFICATION_BOTTOM. */
	public final static int JUSTIFICATION_BOTTOM = 2;

	/** The scale. */
	protected double scale = 1.0;

	/** The start point. */
	protected Point startPoint = new Point();

	/** The mline segments. */
	protected List<DXFMLineSegment> mlineSegments = new ArrayList<>();

	/** The line count. */
	protected int lineCount = 0;

	/** The justification. */
	protected int justification = 0;

	/** The m line style ID. */
	protected String mLineStyleID = "";

	/** The m line style name. */
	protected String mLineStyleName = "";

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getBounds()
	 */
	@Override
	public Bounds getBounds() {
		Bounds b = new Bounds();
		DXFPolyline[] pl = this.toDXFPolylines();

		for (DXFPolyline element : pl) { b.addToBounds(element.getBounds()); }

		// b.setValid(false);
		return b;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getType()
	 */
	@Override
	public String getType() { return DXFConstants.ENTITY_TYPE_MLINE; }

	@Override
	public double getLength() {
		// TODO convert mline -> polyline only after changes
		DXFPolyline[] pl = toDXFPolylines();
		double l = 0;

		for (DXFPolyline element : pl) { l += element.getLength(); }

		return l;
	}

	/**
	 * Adds the DXFM line segement.
	 *
	 * @param seg
	 *            the seg
	 */
	public void addDXFMLineSegement(final DXFMLineSegment seg) {
		this.mlineSegments.add(seg);
	}

	/**
	 * Gets the DXFM line segment count.
	 *
	 * @return the DXFM line segment count
	 */
	public int getDXFMLineSegmentCount() { return this.mlineSegments.size(); }

	/**
	 * Gets the DXFM line segment.
	 *
	 * @param index
	 *            the index
	 * @return the DXFM line segment
	 */
	public DXFMLineSegment getDXFMLineSegment(final int index) {
		return this.mlineSegments.get(index);
	}

	/**
	 * Gets the scale.
	 *
	 * @return the scale
	 */
	public double getScale() { return scale; }

	/**
	 * Sets the scale.
	 *
	 * @param scale
	 *            the new scale
	 */
	public void setScale(final double scale) { this.scale = scale; }

	/**
	 * Gets the start point.
	 *
	 * @return the start point
	 */
	public Point getStartPoint() { return startPoint; }

	/**
	 * Sets the start point.
	 *
	 * @param startPoint
	 *            the new start point
	 */
	public void setStartPoint(final Point startPoint) { this.startPoint = startPoint; }

	/**
	 * Gets the line count.
	 *
	 * @return the line count
	 */
	public int getLineCount() { return lineCount; }

	/**
	 * Sets the line count.
	 *
	 * @param lineCount
	 *            the new line count
	 */
	public void setLineCount(final int lineCount) { this.lineCount = lineCount; }

	/**
	 * Gets the m line style ID.
	 *
	 * @return the m line style ID
	 */
	public String getMLineStyleID() { return mLineStyleID; }

	/**
	 * Sets the m line style ID.
	 *
	 * @param lineStyleID
	 *            the new m line style ID
	 */
	public void setMLineStyleID(final String lineStyleID) { mLineStyleID = lineStyleID; }

	/**
	 * Gets the justification.
	 *
	 * @return the justification
	 */
	public int getJustification() { return justification; }

	/**
	 * Sets the justification.
	 *
	 * @param justification
	 *            the new justification
	 */
	public void setJustification(final int justification) { this.justification = justification; }

	/**
	 * Gets the m line style name.
	 *
	 * @return the m line style name
	 */
	public String getMLineStyleName() { return mLineStyleName; }

	/**
	 * Sets the m line style name.
	 *
	 * @param lineStyleName
	 *            the new m line style name
	 */
	public void setMLineStyleName(final String lineStyleName) { mLineStyleName = lineStyleName; }

	/**
	 * To DXF polylines.
	 *
	 * @return the DXF polyline[]
	 */
	protected DXFPolyline[] toDXFPolylines() {
		return MLineConverter.toDXFPolyline(this);
	}

	/**
	 * Checks if is closed.
	 *
	 * @return true, if is closed
	 */
	public boolean isClosed() { return (this.flags & 2) == 2; }
}
