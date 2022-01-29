/*******************************************************************************************************
 *
 * DXFStreamLayerFilter.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.parser.dxf.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import msi.gama.ext.kabeja.parser.DXFValue;
import msi.gama.ext.kabeja.parser.ParseException;

/**
 * The Class DXFStreamLayerFilter.
 */
public class DXFStreamLayerFilter extends DXFStreamEntityFilter {

	/** The Constant PROPERTY_LAYERS_EXCLUDE. */
	public final static String PROPERTY_LAYERS_EXCLUDE = "layers.exclude";

	/** The Constant PROPERTY_LAYERS_INCLUDE. */
	public final static String PROPERTY_LAYERS_INCLUDE = "layers.include";

	/** The Constant LAYER_NAME. */
	public final static int LAYER_NAME = 8;

	/** The parse values. */
	protected List<ParseValue> parseValues = new ArrayList<>();

	/** The exclude. */
	protected Set<String> exclude = new HashSet<>();

	/** The include. */
	protected Set<String> include = new HashSet<>();

	/** The layer. */
	protected String layer = "";

	/** The find layer. */
	boolean findLayer = true;

	@Override
	public void setProperties(final Map properties) {
		if (properties.containsKey(PROPERTY_LAYERS_INCLUDE)) {
			this.include.clear();

			StringTokenizer st = new StringTokenizer((String) properties.get(PROPERTY_LAYERS_INCLUDE), "|");

			while (st.hasMoreTokens()) {
				String layer = st.nextToken();

				this.include.add(layer);
			}
		}

		if (properties.containsKey(PROPERTY_LAYERS_EXCLUDE)) {
			this.exclude.clear();

			StringTokenizer st = new StringTokenizer((String) properties.get(PROPERTY_LAYERS_EXCLUDE), "|");

			while (st.hasMoreTokens()) { this.exclude.add(st.nextToken()); }
		}
	}

	@Override
	protected void endEntity() throws ParseException {
		if (include.contains(this.layer) || !exclude.contains(this.layer)) { this.outputEntity(); }
	}

	/**
	 * Output entity.
	 *
	 * @throws ParseException
	 *             the parse exception
	 */
	protected void outputEntity() throws ParseException {
		// give the complete entity to the next handler
		for (Object element : this.parseValues) {
			ParseValue v = (ParseValue) element;
			this.handler.parseGroup(v.getGroupCode(), v.getDXFValue());
		}
	}

	@Override
	protected void startEntity(final String type) throws ParseException {
		this.parseValues.clear();
		this.findLayer = true;
	}

	@Override
	protected void parseEntity(final int groupCode, final DXFValue value) throws ParseException {
		if (this.findLayer && groupCode == LAYER_NAME) {
			this.layer = value.getValue();
			this.findLayer = false;
		}

		// parse values to buffer
		ParseValue v = new ParseValue(groupCode, value);
		this.parseValues.add(v);
	}

	/**
	 * The Class ParseValue.
	 */
	private static class ParseValue {

		/** The group code. */
		int groupCode;

		/** The value. */
		DXFValue value;

		/**
		 * Instantiates a new parses the value.
		 *
		 * @param groupCode
		 *            the group code
		 * @param value
		 *            the value
		 */
		public ParseValue(final int groupCode, final DXFValue value) {
			this.groupCode = groupCode;
			this.value = value;
		}

		/**
		 * Gets the group code.
		 *
		 * @return the group code
		 */
		public int getGroupCode() { return this.groupCode; }

		/**
		 * Gets the DXF value.
		 *
		 * @return the DXF value
		 */
		public DXFValue getDXFValue() { return this.value; }
	}
}
