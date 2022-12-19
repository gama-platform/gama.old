/*******************************************************************************************************
 *
 * GamaBasicTypeConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
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
import msi.gaml.types.IType;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaBasicTypeConverter.
 */
public class GamaBasicTypeConverter extends AbstractGamaConverter<IType, IType> {

	/**
	 * Instantiates a new gama basic type converter.
	 *
	 * @param target
	 *            the target
	 */
	public GamaBasicTypeConverter(final Class<IType> target) {
		super(target);
	}

	/** The Constant TAG. */
	private final static String TAG = "GamaType";

	@Override
	public void write(final IScope scope, final IType type, final HierarchicalStreamWriter writer,
			final MarshallingContext arg2) {
		DEBUG.OUT("==GamaType  " + type);
		writer.startNode(TAG);
		writer.setValue("" + type.getName());
		writer.endNode();
	}

	@Override
	public IType read(final IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		reader.moveDown();
		try {
			return scope.getType(reader.getValue());
		} finally {
			reader.moveUp();
		}
	}

}
