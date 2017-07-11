package idees.gama.types;

import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

@type(name = "classifier", id = GamaClassifierType.id, wraps = { GamaClassifier.class }, concept = { IConcept.TYPE, IConcept.STATISTIC })
public class GamaClassifierType extends GamaType<GamaClassifier> {

	public final static int id = IType.AVAILABLE_TYPES + 54736255;

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public GamaClassifier cast(IScope scope, Object obj, Object param, boolean copy) throws GamaRuntimeException {
		if (obj instanceof GamaClassifier) {
			return (GamaClassifier) obj;
		}
		return null;
	}

	@Override
	public GamaClassifier getDefault() {
		return null;
	}

}
