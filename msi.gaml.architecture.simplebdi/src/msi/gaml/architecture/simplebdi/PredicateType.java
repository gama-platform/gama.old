package msi.gaml.architecture.simplebdi;

import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

@type(name = "predicate", id = IType.AVAILABLE_TYPES, wraps = { Predicate.class }, kind = ISymbolKind.Variable.NUMBER)
public class PredicateType extends GamaType<Predicate> {

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public Predicate cast(IScope scope, Object obj, Object param)
			throws GamaRuntimeException {
		return null;
	}

	@Override
	public Predicate getDefault() {
		return null;
	}

}
