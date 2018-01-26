package msi.gaml.architecture.simplebdi;

import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

//classe liée à la classe Sanction
@type(name = "Sanction", id = SanctionType.id, wraps = { Sanction.class }, concept = { IConcept.TYPE, IConcept.BDI })
public class SanctionType extends GamaType<Sanction>{
//
	public final static int id = IType.AVAILABLE_TYPES + 546661;
	
	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public Sanction cast(IScope scope, Object obj, Object param, boolean copy) throws GamaRuntimeException {
		if (obj instanceof Sanction) {
			return (Sanction) obj;
		}
		return null;
	}

	@Override
	public Sanction getDefault() {
		return null;
	}

}
