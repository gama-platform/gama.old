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
package msi.gaml.statements;

import msi.gama.common.interfaces.*;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractContainerStatement.ContainerValidator;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 24 ao�t 2010
 * 
 * @todo Description
 * 
 */
@validator(ContainerValidator.class)
public abstract class AbstractContainerStatement extends AbstractStatement {

	public static class ContainerValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription cd) {

			final Facets f = cd.getFacets();
			final IExpression item = f.getExpr(ITEM, f.getExpr(EDGE, f.getExpr(VERTEX)));
			final IExpression list = f.getExpr(TO, f.getExpr(FROM, f.getExpr(IN)));
			final IExpression index = f.getExpr(AT);
			final IExpression whole = f.getExpr(ALL);
			final String keyword = cd.getKeyword();
			final boolean all = whole == null ? false : !whole.literalValue().equals(FALSE);
			if ( item == null && !all && !keyword.equals(REMOVE) || list == null ) {
				cd.error("The assignment appears uncomplete", IGamlIssue.GENERAL);
				return;
			}
			if ( keyword.equals(ADD) || keyword.equals(REMOVE) ) {
				final IType containerType = list.getType();
				if ( containerType.isFixedLength() ) {
					cd.error("Impossible to add/remove to/from " + list.toGaml(), IGamlIssue.WRONG_TYPE);
					return;
				}
			}
			final IType contentType = list.getContentType();
			IType valueType = Types.NO_TYPE;
			if ( item == null ) {
				if ( whole != null && !whole.literalValue().equals(TRUE) ) {
					valueType = whole.getContentType();
				} else {
					valueType = contentType;
				}
			} else {
				if ( all && item.getType().isTranslatableInto(Types.get(IType.CONTAINER)) ) {
					valueType = item.getContentType();
				} else {
					valueType = item.getType();
				}
			}

			if ( contentType != Types.NO_TYPE && !valueType.isTranslatableInto(contentType) ) {
				cd.warning("The type of the contents of " + list.toGaml() + " (" + contentType +
					") does not match with " + valueType, IGamlIssue.SHOULD_CAST, item == null ? IKeyword.ALL
					: IKeyword.ITEM, contentType.toString());
			}
			final IType keyType = list.getKeyType();
			if ( index != null && keyType != Types.NO_TYPE && !keyType.isTranslatableInto(index.getType()) ) {
				cd.warning("The type of the index of " + list.toGaml() + " (" + keyType +
					") does not match with the type of " + index.toGaml() + " (" + index.getType() + ")",
					IGamlIssue.SHOULD_CAST, IKeyword.AT, keyType.toString());
			}

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
		final IContainer container = createContainer(scope);
		final Object position = createKey(scope, container);
		final Object object = createItem(scope, container);
		apply(scope, object, position, asAll, container);
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
		throw GamaRuntimeException.warning("Cannot use " + list.getType().toString() + " " + list.toGaml() +
			" as a container");
	}

	private Object createKey(final IScope scope, final IContainer container) throws GamaRuntimeException {
		Object position = index == null ? null : index.value(scope);
		// AD 29/02/13 : Normally taken in charge by the parser, now.
		// if ( index != null && !container.checkIndex(position) ) { throw new GamaIndexTypeWarning(
		// position, index.getType(), list); }
		if ( index != null ) {
			final IType t = list.getKeyType();
			final IType i = index.getType();
			if ( !i.isTranslatableInto(t) ) {
				position = t.cast(scope, position, null, Types.NO_TYPE);
			}
		}
		return position;
	}

	private Object createItem(final IScope scope, final IContainer container) throws GamaRuntimeException {
		Object result = null;
		if ( all == null ) {
			// Case add item: ITEM to: LIST
			if ( item != null ) {
				result = item.value(scope);
			}
		} else {
			final Object whole = all.value(scope);
			if ( item != null ) {
				if ( whole instanceof Boolean ) {
					// Case add item: ITEM all: true to: LIST
					asAll = (Boolean) whole;
					result = item.value(scope);
				} else {
					// Case add item: ITEM all: ITEMS to: LIST
					// Impossible
					throw GamaRuntimeException.warning("'all: " + StringUtils.toGaml(whole) + "' cannot be used in " +
						getName());
				}
			} else {
				// Case add all: [...] to: LIST
				asAll = true;
				result = whole;
			}
		}
		return result;

		// if ( all == null ) {
		// if ( item == null ) { return null; }
		// final Object object = item.value(scope);
		// // AD 29/02/13 : Normally taken in charge by the parser, now.
		// // if ( !container.checkValue(object) ) { throw new GamaValueTypeWarning(object,
		// // item.getType(), list); }
		// return object;
		// }
		// Object whole = all.value(scope);
		// if ( item != null ) {
		// if ( whole instanceof Boolean ) {
		// asAll = (Boolean) whole;
		// final Object object = item.value(scope);
		// // AD 29/02/13 : Normally taken in charge by the parser, now.
		// // if ( !container.checkValue(object) ) { throw new GamaValueTypeWarning(object,
		// // item.getType(), list); }
		// return asAll ? GamaList.with(object) : object;
		// }
		// throw new GamaRuntimeException("'all: " + StringUtils.toGaml(whole) +
		// "' cannot be used in " + getName(), true);
		// }
		// asAll = true;
		// if ( !(whole instanceof IContainer) ) {
		// whole = GamaList.with(whole);
		// }
		// // AD 29/02/13 : Normally taken in charge by the parser, now.
		// // for ( Object o : (IContainer) whole ) {
		// // if ( !container.checkValue(o) ) { throw new GamaValueTypeWarning(o, all.getType(),
		// list);
		// // }
		// // }
		// return whole;
	}

	protected abstract void apply(IScope stack, Object object, Object position, Boolean whole, IContainer container)
		throws GamaRuntimeException;

}