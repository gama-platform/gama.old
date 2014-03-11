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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;

/**
 * The type used for representing species objects (since they can be manipulated in a model)
 * 
 * Written by drogoul Modified on 1 ao�t 2010
 * 
 * @todo Description
 * 
 */
@type(name = IKeyword.SPECIES, id = IType.SPECIES, wraps = { ISpecies.class }, kind = ISymbolKind.Variable.REGULAR)
public class GamaSpeciesType extends GamaContainerType<ISpecies> {

	@Override
	public ISpecies cast(final IScope scope, final Object obj, final Object param) throws GamaRuntimeException {
		// TODO Add a more general cast with list of agents to find a common species.
		ISpecies species =
			obj == null ? getDefault() : obj instanceof ISpecies ? (ISpecies) obj : obj instanceof IAgent
				? ((IAgent) obj).getSpecies() : obj instanceof String ? scope.getModel().getSpecies((String) obj)
					: getDefault();
		return species;
	}

	@Override
	public ISpecies cast(final IScope scope, final Object obj, final Object param, final IType keyType,
		final IType contentType) {
		if ( contentType.isAgentType() ) { return scope.getModel().getSpecies(contentType.getName()); }
		return cast(scope, obj, param);
	}

	// TODO Verify that we dont need to declare the other cast method

	@Override
	public ISpecies getDefault() {
		return null;
	}

	@Override
	public IType getContentType() {
		return Types.get(AGENT);
	}

	@Override
	public IType getKeyType() {
		return Types.get(IType.INT);
	}

	// @Override
	// public IType typeIfCasting(final IDescription context, final IExpression exp) {
	// IType itemType = exp.getType();
	// if ( itemType.isSpeciesType() ) { return itemType; }
	// switch (exp.getType().id()) {
	// case SPECIES:
	// return exp.getContentType();
	// case STRING:
	// if ( exp.isConst() ) {
	// SpeciesDescription spec = context.getModelDescription().getSpeciesDescription(exp.literalValue());
	// if ( spec != null ) { return spec.getType(); }
	// }
	// }
	// return Types.NO_TYPE;
	// }

	@Override
	public IType contentsTypeIfCasting(final IExpression exp) {
		IType itemType = exp.getType();
		if ( itemType.isAgentType() ) { return itemType; }
		switch (exp.getType().id()) {
			case SPECIES:
				return itemType.getContentType();
		}
		return exp.getType();
	}

}
