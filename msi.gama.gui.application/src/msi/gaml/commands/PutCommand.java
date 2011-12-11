/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.commands;

import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.Cast;

/**
 * Written by drogoul Modified on 6 f√©vr. 2010
 * 
 * @todo Description
 * 
 */

@facets(value = { @facet(name = ISymbol.AT, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.KEY, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.ALL, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.ITEM, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.EDGE, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.VERTEX, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.WEIGHT, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.IN, type = { IType.CONTAINER_STR }, optional = false) }, combinations = {
	@combination({ ISymbol.AT, ISymbol.ITEM, ISymbol.IN }),
	@combination({ ISymbol.ALL, ISymbol.IN }) })
@symbol(name = ISymbol.PUT, kind = ISymbolKind.SINGLE_COMMAND)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
public class PutCommand extends AbstractContainerCommand {

	short listType;

	public PutCommand(final IDescription desc) {
		super(desc);
		listType = list.getContentType().id();
		setName("put in " + list.toGaml());
	}

	@Override
	protected void apply(final IScope scope, final Object object, final Object position,
		final Boolean whole, final IGamaContainer container) throws GamaRuntimeException {
		Object casted = whole ? ((IGamaContainer) object).get(0) : object;
		casted =
			listType == IType.FLOAT ? Cast.asFloat(scope, casted) : listType == IType.INT ? Cast
				.asInt(scope, casted) : casted;
		if ( whole ) {
			container.putAll(casted, null);
		} else {
			if ( index == null ) { throw new GamaRuntimeWarning("Cannot put " +
				Cast.toGaml(object) + " in " + list.toGaml() + " without a valid index"); }
			if ( !container.checkBounds(position, false) ) { throw new GamaRuntimeWarning("Index " +
				position + " out of bounds of " + list.toGaml()); }
			container.put(position, casted, null);
		}

	}

}
