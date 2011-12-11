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

@facets(value = { @facet(name = ISymbol.ITEM, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.FROM, type = { IType.CONTAINER_STR }, optional = false),
	@facet(name = ISymbol.INDEX, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.KEY, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.ALL, type = IType.NONE_STR, optional = true) })
@symbol(name = ISymbol.REMOVE, kind = ISymbolKind.SINGLE_COMMAND)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
public class RemoveCommand extends AbstractContainerCommand {

	public RemoveCommand(final IDescription desc) {
		super(desc);
		setName("remove from " + list.toGaml());
	}

	@Override
	protected void apply(final IScope stack, final Object object, final Object position,
		final Boolean whole, final IContainer container) throws GamaRuntimeException {
		if ( container.isFixedLength() ) { throw new GamaRuntimeException("Cannot remove from " +
			list.toGaml(), true); }
		if ( whole ) {
			container.removeAll((IContainer) object);
		} else if ( position != null ) {
			if ( !container.checkBounds(position, false) ) { throw new GamaRuntimeException(
				"Index " + position + " out of bounds of " + item.toGaml(), true); }
			container.removeAt(position);
		} else {
			container.removeFirst(object);
		}
	}
}
