package msi.gaml.architecture.simplebdi;

import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

@type(name= "social_link", id=SocialLinkType.id, wraps={SocialLink.class},concept = {IConcept.TYPE, IConcept.BDI})

public class SocialLinkType extends GamaType<SocialLink>{

	public final static int id = IType.AVAILABLE_TYPES + 546657;
	
	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public SocialLink cast(IScope scope, Object obj, Object param, boolean copy) throws GamaRuntimeException {
		if(obj instanceof SocialLink) {return (SocialLink) obj;}
		return null;
	}

	@Override
	public SocialLink getDefault() {
		// TODO Auto-generated method stub
		return null;
	}

}
