/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.types;

import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.species.ISpecies;

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

	public GamaAgentType(final String speciesName, final short speciesId, final Class base) {
		name = speciesName;
		id = speciesId;
		supports = new Class[] { base };
	}

	@Override
	public IAgent cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		ISpecies species = (ISpecies) param;
		if ( obj == null ) { return null; }
		if ( species == null ) { return (IAgent) Types.get(IType.AGENT).cast(scope, obj, param); }
		if ( obj instanceof IAgent ) { return ((IAgent) obj).isInstanceOf(species, false)
			? (IAgent) obj : null; }
		if ( obj instanceof Integer ) { return scope.getAgentScope().getPopulationFor(species)
			.getAgent((Integer) obj); }
		if ( obj instanceof ILocation ) {
			IAgent result =
				scope.getAgentScope().getPopulationFor(species).getAgent((ILocation) obj);
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
	public Map<String, ? extends IGamlDescription> getFieldDescriptions(final ModelDescription model) {
		if ( model == null ) { return Collections.EMPTY_MAP; }
		return model.getSpeciesDescription(name).getVariables();
	}

	@Override
	public String getSpeciesName() {
		return name;
	}

	@Override
	public boolean isSuperTypeOf(final IType type) {
		return type != this && type instanceof GamaAgentType &&
			toClass().isAssignableFrom(type.toClass());
	}

	@Override
	public boolean canBeTypeOf(final IScope scope, final Object obj) {
		boolean b = super.canBeTypeOf(scope, obj);
		if ( b ) { return true; }
		if ( obj instanceof IAgent ) {
			ISpecies s = scope.getSimulationScope().getModel().getSpecies(getSpeciesName());
			return ((IAgent) obj).isInstanceOf(s, false);
		}
		return false;
	}

	@Override
	public IType defaultKeyType() {
		return Types.get(IType.STRING);
	}

	@Override
	public boolean hasContents() {
		return true;
	}

	@Override
	public boolean isFixedLength() {
		return false;
	}

}
