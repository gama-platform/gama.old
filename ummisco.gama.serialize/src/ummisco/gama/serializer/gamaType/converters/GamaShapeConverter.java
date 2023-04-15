/*******************************************************************************************************
 *
 * GamaShapeConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.metamodel.shape.GamaShape;
import msi.gama.runtime.IScope;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaShapeConverter.
 */
public class GamaShapeConverter extends AbstractGamaConverter<GamaShape, GamaShape> {

	/**
	 * Instantiates a new gama shape converter.
	 *
	 * @param target
	 *            the target
	 */
	public GamaShapeConverter(final Class<GamaShape> target) {
		super(target);
	}

	@Override
	public void write(IScope scope, final GamaShape agt,
			final HierarchicalStreamWriter writer, final MarshallingContext context) {
		DEBUG.OUT("ConvertAnother : AgentConverter " + agt.getClass());
		// context.convertAnother(agt);
		DEBUG.OUT("===========END ConvertAnother : GamaShape");
	}

	@Override
	public GamaShape read(IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		return (GamaShape) arg1.convertAnother(null, GamaShape.class); // ragt;
	}

}
