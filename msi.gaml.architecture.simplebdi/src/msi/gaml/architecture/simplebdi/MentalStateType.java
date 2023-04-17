/*******************************************************************************************************
 *
 * MentalStateType.java, in msi.gaml.architecture.simplebdi, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture.simplebdi;

import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

/**
 * The Class MentalStateType.
 */
@SuppressWarnings("unchecked")
@type(name = "mental_state", id = MentalStateType.id, wraps = { MentalState.class }, concept = { IConcept.TYPE, IConcept.BDI })
@doc("a type representing a mental state")
public class MentalStateType extends GamaType<MentalState>{

	/** The Constant id. */
	public final static int id = IType.AVAILABLE_TYPES + 546658;
	
	@Override
	public boolean canCastToConst() {
		return true;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	@doc("cast an object as a mental state if it is an instance o a mental state")
	public MentalState cast(final IScope scope, final Object obj, final Object val, final boolean copy)
			throws GamaRuntimeException {
		if (obj instanceof MentalState) {
			return (MentalState) obj;
		}
		if (obj instanceof String) {
			return new MentalState((String) obj);
		}
		return null;
	}

	@Override
	public MentalState getDefault() {
		return null;
	}
	
}
