/*******************************************************************************************************
 *
 * msi.gaml.types.GamaNoType.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;

/**
 * The type used to represent the absence of type
 *
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ("unchecked")
@type (
		name = IKeyword.UNKNOWN,
		id = IType.NONE,
		wraps = { Object.class },
		kind = ISymbolKind.Variable.REGULAR,
		concept = { IConcept.TYPE },
		doc = @doc ("A type, root of all other types, that represents values without a precise type"))
public class GamaNoType extends GamaType<Object> {

	@Override
	public Object cast(final IScope scope, final Object obj, final Object param, final boolean copy) {
		// WARNING: Should we obey the "copy" parameter in this case ?
		return obj;
	}

	@Override
	public Object getDefault() {
		return null;
	}

	@Override
	public boolean isSuperTypeOf(final IType<?> type) {
		return true;
	}

	@Override
	public IType<Object> findCommonSupertypeWith(final IType<?> iType) {
		// By default, this is the supertype common to all subtypes
		return /* iType.getDefault() == null ? iType : */this;
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

	/**
	 * An unknown value (at the time of compilation) cannot be translated into a bool, an int or a float value
	 */
	@Override
	public boolean isTranslatableInto(final IType<?> t) {
		switch (t.id()) {
			case IType.BOOL:
			case IType.INT:
			case IType.FLOAT:
				return false;
			default:
				return true;
		}
	}

}
