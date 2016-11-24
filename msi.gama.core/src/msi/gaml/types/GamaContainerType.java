/*********************************************************************************************
 *
 * 'GamaContainerType.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 11 nov. 2011
 * 
 * A generic type for containers. Tentative.
 * 
 */
@type(name = IKeyword.CONTAINER, id = IType.CONTAINER, wraps = {
		IContainer.class }, kind = ISymbolKind.Variable.CONTAINER, concept = { IConcept.TYPE, IConcept.CONTAINER })
public class GamaContainerType<T extends IContainer<?, ?>> extends GamaType<T> implements IContainerType<T> {

	@Override
	public T cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return cast(scope, obj, param, getKeyType(), getContentType(), copy);
		// return (T) (obj instanceof IContainer ? (IContainer) obj :
		// Types.get(LIST).cast(scope, obj, null,
		// Types.NO_TYPE, Types.NO_TYPE));
	}

	@SuppressWarnings("unchecked")
	@Override
	public T cast(final IScope scope, final Object obj, final Object param, final IType<?> keyType,
			final IType<?> contentType, final boolean copy) throws GamaRuntimeException {
		// by default
		return (T) (obj instanceof IContainer ? (IContainer<?, ?>) obj
				: (IList<?>) Types.get(LIST).cast(scope, obj, null, copy));
	}

	@Override
	public T getDefault() {
		return null;
	}

	@Override
	public IContainerType<T> getType() {
		return this;
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public boolean isCompoundType() {
		return true;
	}

	@Override
	public boolean isFixedLength() {
		return false;
	}

	@Override
	public IType<?> contentsTypeIfCasting(final IExpression exp) {
		final IType<?> itemType = exp.getType();
		if (itemType.isContainer() || itemType.isAgentType() || itemType.isCompoundType()) {
			return itemType.getContentType();
		}
		return itemType;
	}

	@Override
	public IContainerType<?> typeIfCasting(final IExpression exp) {
		return (IContainerType<?>) super.typeIfCasting(exp);
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IContainerType<?> of(final IType<?>... subs) {
		if (subs.length == 0) {
			return this;
		}
		IType<?> kt = subs.length == 1 ? getKeyType() : subs[0];
		IType<?> ct = subs.length == 1 ? subs[0] : subs[1];
		if (ct == Types.NO_TYPE) {
			if (kt == Types.NO_TYPE) {
				return this;
			}
			ct = getContentType();
		}
		if (kt == Types.NO_TYPE) {
			kt = getKeyType();
		}
		return new ParametricType((IContainerType<IContainer<?, ?>>) this, kt, ct);

	}

}
