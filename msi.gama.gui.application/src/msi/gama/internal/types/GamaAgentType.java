/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.internal.types;

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.GamaPoint;

/**
 * The type used to represent an agent of a species. Should be used by the species for all the
 * operations relative to casting, etc.
 * 
 * Written by drogoul Modified on 1 aožt 2010
 * 
 * @todo Description
 * 
 */
public class GamaAgentType extends GamaType<IAgent> {

	// SpeciesDescription species;

	public GamaAgentType(final String speciesName, final short speciesId, final Class base) {
		name = speciesName;
		id = speciesId;
		support = base;
		// species = sd;
	}

	@Override
	public IAgent cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		ISpecies species = (ISpecies) param;
		if ( obj == null ) { return null; }
		if ( species == null ) { return (IAgent) Types.get(IType.AGENT).cast(scope, obj); }
		if ( obj instanceof IAgent ) { return ((IAgent) obj).isInstanceOf(species, false)
			? (IAgent) obj : null; }
		// if ( obj instanceof String ) { return species.getAgent((String) obj); }
		if ( obj instanceof Integer ) { return scope.getAgentScope().getPopulationFor(species)
			.getAgent((Integer) obj); }
		if ( obj instanceof GamaPoint ) {
			IAgent result =
				scope.getAgentScope().getPopulationFor(species).getAgent((GamaPoint) obj);
			return result;
		}
		return null;
	}

	@Override
	public IAgent getDefault() {
		return null;
	}

	@Override
	public boolean isSpeciesType() {
		return true;
	}

	@Override
	public String getSpeciesName() {
		return name;
	}

}
