/*******************************************************************************************************
 *
 * DXFHatchPattern.java, in msi.gama.ext, is part of the source code of the
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

import msi.gama.ext.kabeja.dxf.helpers.HatchLineFamily;

/**
 * This class represent a single line family of a hatch pattern set.
 *
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth </a>
 *
 */
public class DXFHatchPattern {
	
	/** The id count. */
	private static int idCount = 0;
	
	/** The id. */
	private String id = null;
	
	/** The patterns. */
	private final List<HatchLineFamily> patterns = new ArrayList<>();
	
	/** The hatch. */
	private DXFHatch hatch;

	/**
	 * @return Returns the id.
	 */
	public String getID() {
		if (this.id == null) {
			this.id = "HATCH_PATTERN_ID_" + DXFHatchPattern.idCount;
			DXFHatchPattern.idCount++;
		}

		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setID(final String id) { this.id = id; }

	/**
	 * Adds the line family.
	 *
	 * @param pattern the pattern
	 */
	public void addLineFamily(final HatchLineFamily pattern) {
		patterns.add(pattern);
	}

	/**
	 * Gets the line family iterator.
	 *
	 * @return the line family iterator
	 */
	public Iterator getLineFamilyIterator() { return patterns.iterator(); }

	/**
	 * The associated hatch for this pattern.
	 *
	 * @return Returns the hatch.
	 */
	public DXFHatch getDXFHatch() { return this.hatch; }

	/**
	 * The associated hatch for this pattern.
	 *
	 * @param hatch
	 *            The hatch to set.
	 */
	public void setHatch(final DXFHatch hatch) { this.hatch = hatch; }

	/**
	 *
	 * @return the count of the used line families
	 */
	public int getLineFamilyCount() { return this.patterns.size(); }
}
