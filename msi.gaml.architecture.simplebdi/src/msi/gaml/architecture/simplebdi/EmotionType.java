package msi.gaml.architecture.simplebdi;

import java.util.Map;

import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

@type(name = "emotion", id = EmotionType.id, wraps = { Emotion.class })
public class EmotionType extends GamaType<Emotion> {

	public final static int id = IType.AVAILABLE_TYPES + 546656;
	
	@Override
	public boolean canCastToConst() {
		return true;
	}
	
	@Override
	public Emotion cast(final IScope scope, final Object obj, final Object val, final boolean copy)
		throws GamaRuntimeException {
		if ( obj instanceof Emotion ) { return (Emotion) obj; }
		return null;
	}

	@Override
	public Emotion getDefault() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
