package msi.gaml.architecture.simplebdi;

import java.util.Map;

import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.*;

@type(name = "predicate", id = PredicateType.id, wraps = { Predicate.class },
concept = { IConcept.TYPE, IConcept.BDI })
public class PredicateType extends GamaType<Predicate> {

	public final static int id = IType.AVAILABLE_TYPES + 546654;

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public Predicate cast(final IScope scope, final Object obj, final Object val, final boolean copy)
		throws GamaRuntimeException {
		if ( obj instanceof Predicate ) { return (Predicate) obj; }
		if (obj instanceof String) {
			return new Predicate((String) obj);
		}
		if ( obj != null && obj instanceof Map ) {
			Map<String, Object> map = (Map<String, Object>) obj;
			String nm = (String) (map.containsKey("name") ? map.get("name") : "predicate");
			Double pr = (Double) (map.containsKey("priority") ? map.get("priority") : 1.0);
			Map values = (Map) (map.containsKey("name") ? map.get("values") : null);
			return new Predicate(nm, pr, values);
		}
		return null;
	}

	@Override
	public Predicate getDefault() {
		return null;
	}

}
