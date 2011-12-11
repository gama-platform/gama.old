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
package msi.gaml.commands;

import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.internal.expressions.IVarExpression;
import msi.gama.kernel.exceptions.*;
import msi.gama.util.*;

/**
 * Written by drogoul Modified on 24 aožt 2010
 * 
 * @todo Description
 * 
 */
public abstract class AbstractContainerCommand extends AbstractCommand {

	public static class GamaIndexTypeWarning extends GamaRuntimeWarning {

		public GamaIndexTypeWarning(final Object i, final IType t, final IExpression in) {
			super("Cannot use " + t.toString() + " " + Cast.toGaml(i) + " as an index for " +
				in.type().toString() + " " + in.toGaml());
		}

	}

	public static class GamaValueTypeWarning extends GamaRuntimeWarning {

		public GamaValueTypeWarning(final Object o, final IType t, final IExpression in) {
			super("Cannot use " + t.toString() + " " + Cast.toGaml(o) + " as a value for " +
				in.type().toString() + " " + in.toGaml());
		}

	}

	public static class GamaContainerTypeWarning extends GamaRuntimeWarning {

		public GamaContainerTypeWarning(final IExpression in) {
			super("Cannot use " + in.type().toString() + " " + in.toGaml() + " as a container");
		}

	}

	protected final IExpression	item, index, list, all;
	boolean						asAll	= false;

	public AbstractContainerCommand(final IDescription desc) {
		super(desc);
		item = getFacet(ISymbol.ITEM, getFacet(ISymbol.EDGE, getFacet(ISymbol.VERTEX)));
		index = getFacet(ISymbol.INDEX, getFacet(ISymbol.AT, getFacet(ISymbol.KEY)));
		all = getFacet(ISymbol.ALL);
		list = getFacet(ISymbol.TO, getFacet(ISymbol.FROM, getFacet(ISymbol.IN)));
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		IGamaContainer container = createContainer(scope);
		Object position = createKey(scope, container);
		Object object = createItem(scope, container);
		apply(scope, object, position, asAll, container);
		scope.setStatus(ExecutionStatus.skipped);
		if ( list instanceof IVarExpression ) {
			((IVarExpression) list).setVal(scope, container, false);
		}
		return container;

	}

	/**
	 * @throws GamaRuntimeException
	 * @return the container to which this command will be applied
	 */
	private IGamaContainer createContainer(final IScope scope) throws GamaRuntimeException {
		final Object cont = list.value(scope);
		if ( !(cont instanceof IGamaContainer) ) { throw new GamaContainerTypeWarning(list); }
		return (IGamaContainer) cont;
	}

	private Object createKey(final IScope scope, final IGamaContainer container)
		throws GamaRuntimeException {
		final Object position = index == null ? null : index.value(scope);
		if ( index != null && !container.checkIndex(position) ) { throw new GamaIndexTypeWarning(
			position, index.type(), list); }
		return position;
	}

	private Object createItem(final IScope scope, final IGamaContainer container)
		throws GamaRuntimeException {
		if ( all == null ) {
			if ( item == null ) { return null; }
			final Object object = item.value(scope);
			if ( !container.checkValue(object) ) { throw new GamaValueTypeWarning(object,
				item.type(), list); }
			return object;
		}
		Object whole = all.value(scope);
		if ( item != null ) {
			if ( whole instanceof Boolean ) {
				asAll = (Boolean) whole;
				final Object object = item.value(scope);
				if ( !container.checkValue(object) ) { throw new GamaValueTypeWarning(object,
					item.type(), list); }
				return asAll ? GamaList.with(object) : object;
			}
			throw new GamaRuntimeWarning("'all: " + Cast.toGaml(whole) + "' cannot be used in " +
				getName());
		}
		asAll = true;
		if ( !(whole instanceof IGamaContainer) ) {
			whole = GamaList.with(whole);
		}
		for ( Object o : (IGamaContainer) whole ) {
			if ( !container.checkValue(o) ) { throw new GamaValueTypeWarning(o, all.type(), list); }
		}
		return whole;
	}

	protected abstract void apply(IScope stack, Object object, Object position, Boolean whole,
		IGamaContainer container) throws GamaRuntimeException;

}