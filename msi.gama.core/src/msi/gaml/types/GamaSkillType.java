/*********************************************************************************************
 *
 *
 * 'GamaSpeciesType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ISkill;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.AbstractGamlAdditions;

/**
 * The type used for representing species objects (since they can be manipulated in a model)
 *
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
@type(name = IKeyword.SKILL, id = IType.SKILL, wraps = { ISkill.class }, kind = ISymbolKind.Variable.REGULAR,
concept = { IConcept.TYPE, IConcept.SKILL })
public class GamaSkillType extends GamaType<ISkill> {

	@Override
	public ISkill cast(final IScope scope, final Object obj, final Object param, final boolean copy)
		throws GamaRuntimeException {
		if ( obj instanceof ISkill ) { return (ISkill) obj; }
		if ( obj instanceof String ) { return AbstractGamlAdditions.getSkillInstanceFor((String) obj); }
		return null;
	}

	@Override
	public ISkill getDefault() {
		return null;
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

}
