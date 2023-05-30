/*******************************************************************************************************
 *
 * GamaColorRecord.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.util.GamaColor;

/**
 * The GamaColorRecord.
 */
public class GamaColorRecord {

	/** The r. */
	float r;

	/** The g. */
	float g;

	/** The b. */
	float b;

	/** The a. */
	float a;

	/**
	 * Instantiates a new gama color record.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param r
	 *            the r.
	 * @param g
	 *            the g.
	 * @param b
	 *            the b.
	 * @param a
	 *            the a.
	 * @date 30 mai 2023
	 */
	public GamaColorRecord(final float r, final float g, final float b, final float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	/**
	 * Instantiates a new gama color reducer.
	 *
	 * @param c
	 *            the c
	 */
	public GamaColorRecord(final GamaColor c) {
		this(c.red(), c.green(), c.blue(), c.alpha());
	}

	/**
	 * Construct object.
	 *
	 * @return the object
	 */
	public GamaColor constructObject() {
		return new GamaColor(r / 255d, g / 255d, b / 255d, a / 255d);
	}

}