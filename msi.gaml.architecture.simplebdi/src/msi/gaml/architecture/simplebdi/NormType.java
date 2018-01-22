package msi.gaml.architecture.simplebdi;

import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

//Classe reliée à la classe de Norme
@type(name = "Norm", id = NormType.id, wraps = { Norm.class }, concept = { IConcept.TYPE, IConcept.BDI })
public class NormType extends GamaType<Norm>{

	public final static int id = IType.AVAILABLE_TYPES + 546660;

	@Override
	public boolean canCastToConst() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Norm cast(IScope scope, Object obj, Object param, boolean copy) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Norm getDefault() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
