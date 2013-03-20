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
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.*;
import msi.gama.util.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 24 aožt 2010
 * 
 * @todo Description
 * 
 */
public abstract class AbstractContainerStatement extends AbstractStatement {

	public static class GamaIndexTypeWarning extends GamaRuntimeWarning {

		public GamaIndexTypeWarning(final Object i, final IType t, final IExpression in) {
			super("Cannot use " + t.toString() + " " + StringUtils.toGaml(i) + " as an index for " +
				in.getType().toString() + " " + in.toGaml());
		}

	}

	public static class GamaValueTypeWarning extends GamaRuntimeWarning {

		public GamaValueTypeWarning(final Object o, final IType t, final IExpression in) {
			super("Cannot use " + t.toString() + " " + StringUtils.toGaml(o) + " as a value for " +
				in.getType().toString() + " " + in.toGaml());
		}

	}

	public static class GamaContainerTypeWarning extends GamaRuntimeWarning {

		public GamaContainerTypeWarning(final IExpression in) {
			super("Cannot use " + in.getType().toString() + " " + in.toGaml() + " as a container");
		}

	}

	protected final IExpression item, index, list, all;
	boolean asAll = false;

	public AbstractContainerStatement(final IDescription desc) {
		super(desc);
		item = getFacet(IKeyword.ITEM, getFacet(IKeyword.EDGE, getFacet(IKeyword.VERTEX)));
		index = getFacet(IKeyword.INDEX, getFacet(IKeyword.AT, getFacet(IKeyword.KEY)));
		all = getFacet(IKeyword.ALL);
		list = getFacet(IKeyword.TO, getFacet(IKeyword.FROM, getFacet(IKeyword.IN)));

	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		IContainer container = createContainer(scope);
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
	private IContainer createContainer(final IScope scope) throws GamaRuntimeException {
		final Object cont = list.value(scope);
		if ( cont instanceof IContainer ) { return (IContainer) cont; }
		if ( cont instanceof ISpecies ) { return (ISpecies) cont; }
		if ( cont instanceof IShape ) { return ((IShape) cont).getAttributes(); }
		throw new GamaContainerTypeWarning(list);
	}

	private Object createKey(final IScope scope, final IContainer container)
		throws GamaRuntimeException {
		final Object position = index == null ? null : index.value(scope);
		if ( index != null && !container.checkIndex(position) ) { throw new GamaIndexTypeWarning(
			position, index.getType(), list); }
		return position;
	}

	private Object createItem(final IScope scope, final IContainer container)
		throws GamaRuntimeException {
		if ( all == null ) {
			if ( item == null ) { return null; }
			final Object object = item.value(scope);
			if ( !container.checkValue(object) ) { throw new GamaValueTypeWarning(object,
				item.getType(), list); }
			return object;
		}
		Object whole = all.value(scope);
		if ( item != null ) {
			if ( whole instanceof Boolean ) {
				asAll = (Boolean) whole;
				final Object object = item.value(scope);
				if ( !container.checkValue(object) ) { throw new GamaValueTypeWarning(object,
					item.getType(), list); }
				return asAll ? GamaList.with(object) : object;
			}
			throw new GamaRuntimeWarning("'all: " + StringUtils.toGaml(whole) +
				"' cannot be used in " + getName());
		}
		asAll = true;
		if ( !(whole instanceof IContainer) ) {
			whole = GamaList.with(whole);
		}
		for ( Object o : (IContainer) whole ) {
			if ( !container.checkValue(o) ) { throw new GamaValueTypeWarning(o, all.getType(), list); }
		}
		return whole;
	}

	protected abstract void apply(IScope stack, Object object, Object position, Boolean whole,
		IContainer container) throws GamaRuntimeException;

}