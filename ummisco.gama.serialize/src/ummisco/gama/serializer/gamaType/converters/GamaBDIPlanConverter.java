/*******************************************************************************************************
 *
 * GamaBDIPlanConverter.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gaml.architecture.simplebdi.BDIPlan;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaBDIPlanConverter.
 */
public class GamaBDIPlanConverter implements Converter {
	
	/** The convert scope. */
	ConverterScope convertScope;

	/**
	 * Instantiates a new gama BDI plan converter.
	 *
	 * @param s the s
	 */
	public GamaBDIPlanConverter(final ConverterScope s) {
		convertScope = s;
	}

	@Override
	public boolean canConvert(final Class arg0) {
		return BDIPlan.class.isAssignableFrom(arg0);
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter arg1, MarshallingContext arg2) {
		final BDIPlan plan = (BDIPlan) arg0;
		
		DEBUG.OUT("ConvertAnother : BDIPlan " + plan.getClass() + " " + plan.getGamlType().getContentType());
		DEBUG.OUT("END --- ConvertAnother : BDIPlan ");
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader arg0, UnmarshallingContext arg1) {
		return null;
	}
}
