/*******************************************************************************************************
 *
 * GamaMatrixConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
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

import msi.gama.runtime.IScope;
import msi.gama.util.matrix.IMatrix;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.gamaType.reduced.GamaMatrixReducer;

/**
 * The Class GamaMatrixConverter.
 */
@SuppressWarnings ({ "rawtypes" })
public class GamaMatrixConverter extends AbstractGamaConverter<IMatrix, IMatrix> {

	/**
	 * Instantiates a new gama matrix converter.
	 *
	 * @param target
	 *            the target
	 */
	public GamaMatrixConverter(final Class<IMatrix> target) {
		super(target);
	}

	@Override
	public void write(final IScope scope, final IMatrix mat, final HierarchicalStreamWriter writer,
			final MarshallingContext context) {
		DEBUG.OUT("ConvertAnother : GamaMatrix " + mat.getClass());
		context.convertAnother(new GamaMatrixReducer(scope, mat));
		DEBUG.OUT("END --- ConvertAnother : GamaMatrix ");

	}

	@Override
	public IMatrix read(final IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		final GamaMatrixReducer rmt = (GamaMatrixReducer) context.convertAnother(null, GamaMatrixReducer.class);
		return rmt.constructObject(scope);
	}

}
