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
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;

/**
 * Written by drogoul Modified on 6 f√©vr. 2010
 * 
 * @todo Description
 * 
 */

@facets({ @facet(name = ISymbol.TO, type = { IType.CONTAINER_STR }, optional = false),
	@facet(name = ISymbol.ITEM, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.EDGE, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.VERTEX, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.AT, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.ALL, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.WEIGHT, type = IType.FLOAT_STR, optional = true) })
@symbol(name = ISymbol.ADD, kind = ISymbolKind.SINGLE_COMMAND)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
public class AddCommand extends AbstractContainerCommand {

	private final IExpression weight;

	public AddCommand(final IDescription desc) {
		super(desc);
		weight = getFacet(ISymbol.WEIGHT);
		setName("add to " + list.toGaml());
	}

	@Override
	protected void apply(final IScope stack, final Object object, final Object position,
		final Boolean whole, final IGamaContainer container) throws GamaRuntimeException {
		if ( container.isFixedLength() ) { throw new GamaRuntimeException("Cannot add to " +
			list.toGaml(), true); }
		Object param = weight == null ? null : weight.value(stack);
		if ( position == null ) {
			if ( asAll ) {
				container.addAll((IGamaContainer) object, param);
			} else {
				container.add(object, param);
			}
		} else {
			if ( !container.checkBounds(position, true) ) { throw new GamaRuntimeException(
				"Index " + position + " out of bounds of " + list.toGaml(), true); }
			if ( !asAll ) {
				container.add(position, object, param);
			} else {
				container.addAll(position, (IGamaContainer) object, param);
			}
		}
	}

}
