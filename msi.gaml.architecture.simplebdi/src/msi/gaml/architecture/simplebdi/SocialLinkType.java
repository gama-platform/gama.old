/*******************************************************************************************************
 *
 * SocialLinkType.java, in msi.gaml.architecture.simplebdi, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture.simplebdi;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

/**
 * The Class SocialLinkType.
 */
@SuppressWarnings("unchecked")
@type(name = "social_link", id = SocialLinkType.id, wraps = { SocialLink.class }, concept = { IConcept.TYPE,
		IConcept.BDI })
@doc("represents a social link")
public class SocialLinkType extends GamaType<SocialLink> {

	/** The Constant id. */
	public final static int id = IType.AVAILABLE_TYPES + 546657;

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	@doc("cast an object into a social link, if it is an instance of a social link")
	public SocialLink cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		if (obj instanceof SocialLink) {
			return (SocialLink) obj;
		}
		return null;
	}

	@Override
	public SocialLink getDefault() {
		return null;
	}

}
