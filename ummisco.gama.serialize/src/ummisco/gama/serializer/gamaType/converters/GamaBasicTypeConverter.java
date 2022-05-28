/*******************************************************************************************************
 *
 * GamaBasicTypeConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
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
	public void write(IScope scope, final IType type, final HierarchicalStreamWriter writer, final MarshallingContext arg2) {
		DEBUG.OUT("==GamaType  " + type);
		// System.out.println("==GamaType " + arg0);
		writer.startNode(TAG);
		writer.setValue("" + type.getName());
		// writer.setValue(""+arg0.getClass());
		writer.endNode();
	}

	// TODO
	@Override
	public IType read(IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		reader.moveDown();
		final IType<?> t = getScope().getType(reader.getValue());
		// ModelDescription modelDesc = ((ModelDescription)
		// convertScope.getScope().getModelContext());
		// IType t = ((ModelDescription)
		// convertScope.getScope().getModelContext()).getTypesManager().get(type)
		// String val = reader.getValue();
		reader.moveUp();

		return t;
	}

}
