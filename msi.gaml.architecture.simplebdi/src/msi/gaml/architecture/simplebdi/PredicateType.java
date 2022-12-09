/*******************************************************************************************************
 *
 * PredicateType.java, in msi.gaml.architecture.simplebdi, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture.simplebdi;

import java.util.Map;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IMap;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

/**
 * The Class PredicateType.
 */
@SuppressWarnings ("unchecked")
@type (
		name = "predicate",
		id = PredicateType.id,
		wraps = { Predicate.class },
		concept = { IConcept.TYPE, IConcept.BDI })
@doc ("represents a predicate")
public class PredicateType extends GamaType<Predicate> {

	/** The Constant id. */
	public final static int id = IType.AVAILABLE_TYPES + 546654;

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@SuppressWarnings ({ "rawtypes" })
	@Override
	@doc ("cast an object as a predicate")
	public Predicate cast(final IScope scope, final Object obj, final Object val, final boolean copy)
			throws GamaRuntimeException {
		if (obj instanceof Predicate) return (Predicate) obj;
		if (obj instanceof String) return new Predicate((String) obj);
		if (obj instanceof Map) {
			final Map<String, Object> map = (Map<String, Object>) obj;
			final String nm = (String) (map.containsKey("name") ? map.get("name") : "predicate");
			final IMap values = (IMap) (map.containsKey("name") ? map.get("values") : null);
			return new Predicate(nm, values);
		}
		return null;
	}

	@Override
	public Predicate getDefault() { return null; }

}
