/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.interfaces;

import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.util.*;

/**
 * Written by drogoul Modified on 3 juin 2010
 * 
 * @todo Description
 * 
 */
public interface IGamaContainer<KeyType, ValueType> extends IValue, Iterable<ValueType> {

	// Operators available in GAML

	@operator(value = { "at", "@" }, can_be_const = true, type = ITypeProvider.LEFT_CONTENT_TYPE)
	public ValueType get(KeyType index) throws GamaRuntimeException;

	@operator(value = "contains", can_be_const = true)
	public boolean contains(Object o) throws GamaRuntimeException;

	@operator(value = "first", can_be_const = true, type = ITypeProvider.CHILD_CONTENT_TYPE)
	public ValueType first() throws GamaRuntimeException;

	@operator(value = "last", can_be_const = true, type = ITypeProvider.CHILD_CONTENT_TYPE)
	public ValueType last() throws GamaRuntimeException;

	@operator(value = "length", can_be_const = true)
	public int length();

	@operator(value = "max", can_be_const = true, type = ITypeProvider.CHILD_CONTENT_TYPE)
	public ValueType max() throws GamaRuntimeException;

	@operator(value = "min", can_be_const = true, type = ITypeProvider.CHILD_CONTENT_TYPE)
	public ValueType min() throws GamaRuntimeException;

	@operator(value = { "mul", "product" }, can_be_const = true, type = ITypeProvider.CHILD_CONTENT_TYPE)
	public Object product() throws GamaRuntimeException;

	@operator(value = "sum", can_be_const = true, type = ITypeProvider.CHILD_CONTENT_TYPE)
	public Object sum() throws GamaRuntimeException;

	@operator(value = "empty", can_be_const = true)
	public boolean isEmpty();

	@operator(value = "reverse", can_be_const = true, type = ITypeProvider.TYPE, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public IGamaContainer<KeyType, ValueType> reverse() throws GamaRuntimeException;

	/**
	 * @return one of the values stored in this container using GAMA.getRandom()
	 */
	@operator(value = { "any", "one_of" }, can_be_const = false, type = ITypeProvider.CHILD_CONTENT_TYPE)
	public ValueType any();

	// Interfaces used by GAML commands add, remove, put

	/**
	 * Checks if the container length is fixed (i.e. if add or remove operations are accepted)
	 * 
	 * @return true, if the length of the container is fixed.
	 */
	public boolean isFixedLength();

	/**
	 * Purpose of this method is to return whether or not the given index is valid.
	 * 
	 * @param index the index (can be null)
	 * @return true, if the index is valid
	 */
	public boolean checkIndex(Object index);

	/**
	 * Purpose of this method is to return whether or not the given value is valid to be stored in
	 * this container.
	 * 
	 * @param value the value (can be null)
	 * @return true, if the value is valid
	 */
	public boolean checkValue(Object value);

	/**
	 * 
	 * Purpose of this method is to return whether or not the given index is within the
	 * bounds allowed by the container.
	 * 
	 * 
	 * @param index the index (can be null)
	 * @param forAdding indicates if the test is made before adding or putting/removing a value
	 * @return true, if the index is within the bounds
	 */
	public boolean checkBounds(KeyType index, boolean forAdding);

	public void addAll(final IGamaContainer value, final Object param) throws GamaRuntimeException;

	public void addAll(final KeyType index, final IGamaContainer value, final Object param)
		throws GamaRuntimeException;

	public void add(final ValueType value, final Object param) throws GamaRuntimeException;

	public void add(final KeyType index, final ValueType value, Object param)
		throws GamaRuntimeException;

	public boolean removeFirst(final ValueType value) throws GamaRuntimeException;

	public boolean removeAll(final IGamaContainer<?, ValueType> value) throws GamaRuntimeException;

	public Object removeAt(final KeyType index) throws GamaRuntimeException;

	public void putAll(final ValueType value, Object param) throws GamaRuntimeException;

	public void put(final KeyType index, final ValueType value, Object param)
		throws GamaRuntimeException;

	public void clear() throws GamaRuntimeException;

	// Casting operations to/from common types of containers

	@operator(value = IType.LIST_STR, can_be_const = true, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public abstract GamaList listValue(IScope scope) throws GamaRuntimeException;

	@operator(value = IType.MATRIX_STR, can_be_const = true, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public abstract IMatrix matrixValue(IScope scope) throws GamaRuntimeException;

	@operator(value = "as_matrix", content_type = ITypeProvider.LEFT_CONTENT_TYPE, can_be_const = true)
	public abstract IMatrix matrixValue(IScope scope, GamaPoint preferredSize)
		throws GamaRuntimeException;

	@operator(value = IType.MAP_STR, can_be_const = true)
	public abstract GamaMap mapValue(IScope scope) throws GamaRuntimeException;

}
