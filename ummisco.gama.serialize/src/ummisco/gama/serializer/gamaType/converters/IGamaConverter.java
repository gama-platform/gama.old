/*******************************************************************************************************
 *
 * IGamaConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

import msi.gama.common.interfaces.IScoped;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;

/**
 * The Interface IGamaConverter.
 *
 * @param <Input>
 *            the generic type that this converter can convert to XML or JSON
 * @param <Output>
 *            the generic type that this converter produces when reading XML or JSON serialized objects
 */
public interface IGamaConverter<Input, Output> extends Converter, IScoped {

	/**
	 * Unmarshal. This default implementation delegates to
	 * {@link #read(IScope, HierarchicalStreamReader, UnmarshallingContext)}
	 *
	 * @param reader
	 *            the reader
	 * @param context
	 *            the context
	 * @return the output
	 */
	@Override
	default Output unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		return read(getScope(), reader, context);
	}

	/**
	 * Marshal. This default implementation delegates to
	 * {@link #write(IScope, Input, HierarchicalStreamWriter, MarshallingContext)}
	 *
	 * @param object
	 *            the object
	 * @param writer
	 *            the writer
	 * @param context
	 *            the context
	 */
	@SuppressWarnings ("unchecked")
	@Override
	default void marshal(final Object object, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		write(getScope(), (Input) object, writer, context);
	}

	/**
	 * Read. Return an "Output" object obtained from the reader
	 *
	 * @param scope
	 *            the scope
	 * @param reader
	 *            the reader
	 * @param context
	 *            the context
	 * @return the output
	 */
	Output read(IScope scope, HierarchicalStreamReader reader, UnmarshallingContext context);

	/**
	 * Write. Creates a stream from an "Input" object
	 *
	 * @param scope
	 *            TODO
	 * @param object
	 *            the object
	 * @param writer
	 *            the writer
	 * @param context
	 *            the context
	 */
	void write(IScope scope, Input object, HierarchicalStreamWriter writer, MarshallingContext context);

	/**
	 * Sets the scope that can be used in {@link #read(IScope, HierarchicalStreamReader, UnmarshallingContext)} and
	 * {@link #write(IScope, Object, HierarchicalStreamWriter, MarshallingContext)}. Default implementation does nothing
	 *
	 * @param cs
	 *            the new scope
	 */
	default void setScope(final IScope cs) {}

	/**
	 * Gets the scope. A default implementation is provided to return the current runtime scope (the latest simulation
	 * scope or the experiment scope if no simulation is available), so that converters that do not need the scope do
	 * not have to implement it
	 *
	 * @return the scope
	 */
	@Override
	default IScope getScope() { return GAMA.getRuntimeScope(); }
}