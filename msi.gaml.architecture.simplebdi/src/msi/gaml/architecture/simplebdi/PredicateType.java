package msi.gaml.architecture.simplebdi;

import java.util.Map;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

@type(name = "predicate", id = PredicateType.id, wraps = { Predicate.class } )
public class PredicateType extends GamaType<Predicate> {

	public final static int id = IType.AVAILABLE_TYPES + 546654;
	
	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public Predicate cast(IScope scope, Object obj, Object param)
			throws GamaRuntimeException {
		if (obj instanceof Predicate) return (Predicate) obj;
		if (obj != null && obj instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) obj;
			String nm = (String) (map.containsKey("name") ? map.get("name") : "predicate");
			Double pr = (Double) (map.containsKey("priority") ? map.get("priority") : 1.0);
			IValue val = (IValue) (map.containsKey("value") ? map.get("value") : null);
			Map par = (Map) (map.containsKey("name") ? map.get("parameter") : null);
			return new Predicate(nm, val, pr, par);
		} 
		return null;
	}

	@Override
	public Predicate getDefault() {
		return null;
	}

}
