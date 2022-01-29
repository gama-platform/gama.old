/*******************************************************************************************************
 *
 * DXFRegion.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFRegion extends DXFEntity {
	
	/** The acis data. */
	protected List<String> acisData = new ArrayList<>();

	/**
	 *
	 *
	 * @return always invalid bounds
	 */
	@Override
	public Bounds getBounds() {
		Bounds bounds = new Bounds();
		bounds.setValid(false);

		return bounds;
	}

	/**
	 *
	 *
	 * @see org.kabeja.dxf.DXFEntity#getType()
	 */
	@Override
	public String getType() { return DXFConstants.ENTITY_TYPE_REGION; }

	/**
	 * The ACIS commands as a list of lines
	 *
	 * @return the list
	 */
	public List getACISDATA() { return acisData; }

	/**
	 * Append ACISDATA.
	 *
	 * @param data the data
	 */
	public void appendACISDATA(final String data) {
		acisData.add(data);
	}

	/**
	 * This entity is only a container of ACIS data.
	 *
	 * @return always 0
	 */
	@Override
	public double getLength() { return 0; }
}
