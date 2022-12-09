/*******************************************************************************************************
 *
 * AbstractGamaConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import msi.gama.runtime.IScope;

/**
 * The Class GamaConverter. Provides a basic implementation of a converter, that keeps a link to a scope
 */
public abstract class AbstractGamaConverter<Input, Output> implements IGamaConverter<Input, Output> {

	/** The target. */
	private final Class<Input> targetClass;

	/** The scope. */
	private IScope scope;

	/**
	 * Instantiates a new gama converter.
	 *
	 * @param scope
	 *            the scope
	 * @param target
	 *            the target
	 */
	public AbstractGamaConverter(final Class<Input> target) {
		this.targetClass = target;
	}

	/**
	 * Whether the converter can convert the objects of this class. Default is here (covers both superclasses and
	 * interfaces), subclasses can specialize
	 *
	 * @param clazz
	 *            the clazz
	 * @return true, if successful
	 */
	@Override
	public boolean canConvert(final Class clazz) {
		return targetClass.isAssignableFrom(clazz);
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	@Override
	public IScope getScope() { return scope; }

	/**
	 * Sets the scope.
	 *
	 * @param cs
	 *            the new scope
	 */
	@Override
	public void setScope(final IScope cs) { this.scope = cs; }

}
