/*******************************************************************************************************
 *
 * DXFHeader.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 *
 */
public class DXFHeader {
	
	/** The variables. */
	private final Hashtable<String, DXFVariable> variables = new Hashtable<>();

	/**
	 * Instantiates a new DXF header.
	 */
	public DXFHeader() {}

	/**
	 * Sets the variable.
	 *
	 * @param v the new variable
	 */
	public void setVariable(final DXFVariable v) {
		variables.put(v.getName(), v);
	}

	/**
	 * Checks for variable.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	public boolean hasVariable(final String name) {
		return variables.containsKey(name);
	}

	/**
	 * Gets the variable.
	 *
	 * @param name the name
	 * @return the variable
	 */
	public DXFVariable getVariable(final String name) {
		return variables.get(name);
	}

	/**
	 * Gets the varialbe iterator.
	 *
	 * @return the varialbe iterator
	 */
	public Iterator getVarialbeIterator() { return variables.values().iterator(); }

	/**
	 * Checks if is fill mode.
	 *
	 * @return true, if is fill mode
	 */
	public boolean isFillMode() {
		if (hasVariable("$FILLMODE") && getVariable("$FILLMODE").getDoubleValue("70") > 0) return true;

		return false;
	}

	/**
	 * Returns the global linetype scale factor.
	 *
	 * @return the global scalefactor
	 */
	public double getLinetypeScale() {
		double gscale = 1.0;

		if (hasVariable("$LTSCALE")) { gscale = getVariable("$LTSCALE").getDoubleValue("40"); }

		return gscale;
	}
}
