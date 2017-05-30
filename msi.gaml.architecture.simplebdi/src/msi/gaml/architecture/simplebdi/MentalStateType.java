package msi.gaml.architecture.simplebdi;

import java.util.Map;

import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

@SuppressWarnings("unchecked")
@type(name = "mental_state", id = MentalStateType.id, wraps = { MentalState.class }, concept = { IConcept.TYPE, IConcept.BDI })

public class MentalStateType extends GamaType<MentalState>{

	public final static int id = IType.AVAILABLE_TYPES + 546658;
	
	@Override
	public boolean canCastToConst() {
		return true;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
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
