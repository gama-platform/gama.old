/*******************************************************************************************************
 *
 * SanctionType.java, in msi.gaml.architecture.simplebdi, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.architecture.simplebdi;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IMap;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

/**
 * The Class SanctionType.
 */
@type (
		name = "Sanction",
		id = SanctionType.id,
		wraps = { Sanction.class },
		concept = { IConcept.TYPE, IConcept.BDI })
@doc ("represents a sanction")
public class SanctionType extends GamaType<Sanction> {

	/** The Constant id. */
	//
	public final static int id = IType.AVAILABLE_TYPES + 546661;

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	@doc ("cast an object as a sanction, if it is an instance of a sanction")
	public Sanction cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		if (obj instanceof Sanction) return (Sanction) obj;
		return null;
	}

	@Override
	public Sanction getDefault() { return null; }

	@Override
	public Sanction deserializeFromJson(final IScope scope, final IMap<String, Object> map2) {
		// See later how to deal with BDI objects
		return null;
	}

}
