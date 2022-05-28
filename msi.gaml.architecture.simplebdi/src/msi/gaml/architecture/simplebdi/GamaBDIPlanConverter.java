/*******************************************************************************************************
 *
 * GamaBDIPlanConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.architecture.simplebdi;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.runtime.IScope;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.gamaType.converters.AbstractGamaConverter;

/**
 * The Class GamaBDIPlanConverter.
 */
public class GamaBDIPlanConverter extends AbstractGamaConverter<BDIPlan, BDIPlan> {

	/**
	 * Instantiates a new gama BDI plan converter.
	 *
	 * @param target
	 *            the target
	 */
	public GamaBDIPlanConverter(final Class<BDIPlan> target) {
		super(target);
	}

	@Override
	public BDIPlan read(IScope scope, final HierarchicalStreamReader arg0, final UnmarshallingContext arg1) {
		return null;
	}

	/**
	 * Serialize.
	 * @param writer
	 *            the writer
	 * @param context
	 *            the context
	 * @param plan
	 *            the plan
	 */
	@Override
	public void write(IScope scope, final BDIPlan plan, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		DEBUG.OUT("ConvertAnother : BDIPlan " + plan.getClass() + " " + plan.getGamlType().getContentType());
		DEBUG.OUT("END --- ConvertAnother : BDIPlan ");

	}
}
