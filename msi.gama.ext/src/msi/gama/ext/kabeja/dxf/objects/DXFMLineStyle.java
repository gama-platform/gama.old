/*******************************************************************************************************
 *
 * DXFMLineStyle.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import msi.gama.ext.kabeja.dxf.DXFConstants;

/**
 * The Class DXFMLineStyle.
 */
public class DXFMLineStyle extends DXFObject {

	/** The lines. */
	protected List<DXFMLineStyleElement> lines = new ArrayList<>();

	/** The name. */
	protected String name = "";

	/** The descrition. */
	protected String descrition = "";

	/** The fill color. */
	protected int fillColor = 256;

	/** The flags. */
	protected int flags = 0;

	/** The start angle. */
	protected double startAngle = 0;

	/** The end angle. */
	protected double endAngle = 0;

	@Override
	public String getObjectType() { return DXFConstants.OBJECT_TYPE_MLINESTYLE; }

	/**
	 * Adds the DXFM line style element.
	 *
	 * @param e
	 *            the e
	 */
	public void addDXFMLineStyleElement(final DXFMLineStyleElement e) {
		this.lines.add(e);
	}

	/**
	 * Gets the DXFM line style L element.
	 *
	 * @param index
	 *            the index
	 * @return the DXFM line style L element
	 */
	public DXFMLineStyleElement getDXFMLineStyleLElement(final int index) {
		return this.lines.get(index);
	}

	/**
	 * Removes the DXFM line style L element.
	 *
	 * @param index
	 *            the index
	 * @return the DXFM line style element
	 */
	public DXFMLineStyleElement removeDXFMLineStyleLElement(final int index) {
		return this.lines.remove(index);
	}

	/**
	 * Gets the DXFM line style L element count.
	 *
	 * @return the DXFM line style L element count
	 */
	public int getDXFMLineStyleLElementCount() { return this.lines.size(); }

	/**
	 * Sort DXFM line style elements.
	 *
	 * @param comp
	 *            the comp
	 */
	public void sortDXFMLineStyleElements(final Comparator<DXFMLineStyleElement> comp) {
		Collections.sort(this.lines, comp);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() { return name; }

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(final String name) { this.name = name; }

	/**
	 * Gets the descrition.
	 *
	 * @return the descrition
	 */
	public String getDescrition() { return descrition; }

	/**
	 * Sets the descrition.
	 *
	 * @param descrition
	 *            the new descrition
	 */
	public void setDescrition(final String descrition) { this.descrition = descrition; }

	/**
	 * Gets the fill color.
	 *
	 * @return the fill color
	 */
	public int getFillColor() { return fillColor; }

	/**
	 * Sets the fill color.
	 *
	 * @param fillColor
	 *            the new fill color
	 */
	public void setFillColor(final int fillColor) { this.fillColor = fillColor; }

	/**
	 * Gets the flags.
	 *
	 * @return the flags
	 */
	public int getFlags() { return flags; }

	/**
	 * Sets the flags.
	 *
	 * @param flags
	 *            the new flags
	 */
	public void setFlags(final int flags) { this.flags = flags; }

	/**
	 * Gets the start angle.
	 *
	 * @return the start angle
	 */
	public double getStartAngle() { return startAngle; }

	/**
	 * Sets the start angle.
	 *
	 * @param startAngle
	 *            the new start angle
	 */
	public void setStartAngle(final double startAngle) { this.startAngle = startAngle; }

	/**
	 * Gets the end angle.
	 *
	 * @return the end angle
	 */
	public double getEndAngle() { return endAngle; }

	/**
	 * Sets the end angle.
	 *
	 * @param endAngle
	 *            the new end angle
	 */
	public void setEndAngle(final double endAngle) { this.endAngle = endAngle; }

	/**
	 * Checks if is filled.
	 *
	 * @return true, if is filled
	 */
	public boolean isFilled() { return (this.flags & 1) == 1; }

	/**
	 * Checks for start square caps.
	 *
	 * @return true, if successful
	 */
	public boolean hasStartSquareCaps() {
		return (this.flags & 16) == 16;
	}

	/**
	 * Checks for start round caps.
	 *
	 * @return true, if successful
	 */
	public boolean hasStartRoundCaps() {
		return (this.flags & 64) == 64;
	}

	/**
	 * Checks for start inner arcs.
	 *
	 * @return true, if successful
	 */
	public boolean hasStartInnerArcs() {
		return (this.flags & 32) == 32;
	}

	/**
	 * Checks for end square caps.
	 *
	 * @return true, if successful
	 */
	public boolean hasEndSquareCaps() {
		return (this.flags & 256) == 256;
	}

	/**
	 * Checks for end round caps.
	 *
	 * @return true, if successful
	 */
	public boolean hasEndRoundCaps() {
		return (this.flags & 1024) == 1024;
	}

	/**
	 * Checks for end innder arcs.
	 *
	 * @return true, if successful
	 */
	public boolean hasEndInnderArcs() {
		return (this.flags & 512) == 512;
	}

	/**
	 * Show miters.
	 *
	 * @return true, if successful
	 */
	public boolean showMiters() {
		return (this.flags & 2) == 2;
	}
}
