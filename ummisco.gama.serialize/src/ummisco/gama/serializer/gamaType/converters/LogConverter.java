/*******************************************************************************************************
 *
 * LogConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.runtime.IScope;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class LogConverter.
 */
public class LogConverter extends AbstractGamaConverter<Object, Object> {

	/**
	 * Instantiates a new log converter.
	 *
	 * @param target
	 *            the target
	 */
	public LogConverter(final Class<Object> target) {
		super(target);
	}

	@Override
	public boolean canConvert(final Class arg0) {
		DEBUG.OUT("LOG Converter: " + arg0 + " super " + arg0.getSuperclass());
		return false;
	}

	@Override
	public void write(IScope scope, final Object arg0, final HierarchicalStreamWriter arg1, final MarshallingContext arg2) {}

	@Override
	public Object read(IScope scope, final HierarchicalStreamReader arg0, final UnmarshallingContext arg1) {
		return null;
	}
}
