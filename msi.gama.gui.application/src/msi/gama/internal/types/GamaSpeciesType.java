/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.internal.types.*;

/**
 * The type used for representing species objects (since they can be manipulated in a model)
 * 
 * Written by drogoul Modified on 1 aožt 2010
 * 
 * @todo Description
 * 
 */
@type(value = IType.SPECIES_STR, id = IType.SPECIES, wraps = { ISpecies.class })
public class GamaSpeciesType extends GamaType<ISpecies> {

	@Override
	public ISpecies cast(final IScope scope, final Object obj, final Object param) {
		ISpecies species =
			obj == null ? getDefault() : obj instanceof ISpecies ? (ISpecies) obj
				: obj instanceof IAgent ? ((IAgent) obj).getSpecies() : obj instanceof String
					? scope.getAgentScope().getPopulationFor((String) obj).getSpecies()
					: getDefault();
		return species;
	}

	@Override
	public ISpecies getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.get(AGENT);
	}

}
