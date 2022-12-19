/*******************************************************************************************************
 *
 * GamaMapConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
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

import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.IMap;
import ummisco.gama.serializer.gamaType.reduced.GamaMapReducer;

/**
 * The Class GamaMapConverter.
 */
public class GamaMapConverter extends AbstractGamaConverter<IMap, IMap> {

	/**
	 * Instantiates a new gama map converter.
	 *
	 * @param target
	 *            the target
	 */
	public GamaMapConverter(final Class<IMap> target) {
		super(target);
	}

	@Override
	public boolean canConvert(final Class clazz) {
		return !SavedAgent.class.isAssignableFrom(clazz) && super.canConvert(clazz);
	}

	@Override
	public void write(final IScope scope, final IMap map, final HierarchicalStreamWriter writer,
			final MarshallingContext context) {
		context.convertAnother(new GamaMapReducer(map));
	}

	@Override
	public IMap read(final IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		final GamaMapReducer rmt = (GamaMapReducer) arg1.convertAnother(null, GamaMapReducer.class);
		return rmt.constructObject(scope);
	}

}
