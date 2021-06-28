/*******************************************************************************************************
 *
 * msi.gaml.statements.AbstractContainerStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gama.util.graph.IGraph;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.BinaryOperator;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.statements.AbstractContainerStatement.ContainerValidator;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 24 ao�t 2010
 *
 * @todo Description
 *
 */
@validator (ContainerValidator.class)
@SuppressWarnings ({ "rawtypes" })
public abstract class AbstractContainerStatement extends AbstractStatement {

	public static class ContainerValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription cd) {

			// 17/02/14: We change the facets to simplify the writing of
			// statements. EDGE and VERTEX are removed
			final IExpressionDescription itemDesc = cd.getFacet(ITEM, EDGE, VERTEX, NODE);
			IExpression item = itemDesc == null ? null : itemDesc.getExpression();
			if (item != null) {
				if (cd.hasFacet(EDGE)) {
					if (cd.hasFacet(WEIGHT)) {
						item = GAML.getExpressionFactory().createOperator("edge", cd, cd.getFacet(EDGE).getTarget(),
								item, cd.getFacetExpr(WEIGHT));
					} else {
						item = GAML.getExpressionFactory().createOperator("edge", cd, cd.getFacet(EDGE).getTarget(),
								item);
					}
					cd.removeFacets(EDGE, WEIGHT);
				} else if (cd.hasFacet(VERTEX) || cd.hasFacet(NODE)) {
					final boolean isNode = cd.hasFacet(NODE);
					if (cd.hasFacet(WEIGHT)) {
						item = GAML.getExpressionFactory().createOperator("node", cd,
								isNode ? cd.getFacet(NODE).getTarget() : cd.getFacet(VERTEX).getTarget(), item,
								cd.getFacetExpr(WEIGHT));
					} else {
						item = GAML.getExpressionFactory().createOperator("node", cd,
								isNode ? cd.getFacet(NODE).getTarget() : cd.getFacet(VERTEX).getTarget(), item);
					}
					cd.removeFacets(VERTEX, NODE, WEIGHT);
				}
				// itemDesc.setExpression(item);
				cd.setFacet(ITEM, item);
			}
			final IExpressionDescription listDesc = cd.getFacet(TO, FROM, IN);
			final IExpression list = listDesc == null ? null : listDesc.getExpression();
			if (list != null) {
				cd.setFacet(TO, listDesc);
				cd.removeFacets(FROM, IN);
			}
			final IExpressionDescription indexDesc = cd.getFacet(AT, KEY, INDEX);
			final IExpression index = indexDesc == null ? null : indexDesc.getExpression();
			if (index != null) {
				cd.setFacet(AT, indexDesc);
				cd.removeFacets(KEY, INDEX);
			}
			// If a container/value is passed to ALL, then it is copied to ITEM
			// and ALL is set to "true"
			final IExpressionDescription wholeDesc = cd.getFacet(ALL);
			final IExpression whole = wholeDesc == null ? null : wholeDesc.getExpression();
			if (whole != null && whole.getGamlType().id() != IType.BOOL) {
				cd.setFacet(ITEM, wholeDesc);
				cd.removeFacets(ALL);
				cd.setFacet(ALL, IExpressionFactory.TRUE_EXPR);
			}

			/**
			 * After these operations, the statement should be provided with: ITEM: the object to add/remove/put ALL: a
			 * boolean indicating whether (or not) to treat the operation as "all" TO: the container to change AT: the
			 * index at which the operation should be done (if any)
			 *
			 * All other facets are then meaningless. As a consequence, ITEM is never null except in the case of the
			 * "remove all: true from: ..."
			 */

			final String keyword = cd.getKeyword();
			final boolean all = whole == null ? false : !whole.literalValue().equals(FALSE);
			if (item == null && !all && !keyword.equals(REMOVE) || list == null) {
				cd.error("The assignment appears uncomplete", IGamlIssue.GENERAL);
				return;
			}
			if (keyword.equals(ADD) || keyword.equals(REMOVE)) {
				final IType containerType = list.getGamlType();
				if (containerType.isFixedLength()) {
					cd.error("Impossible to add/remove to/from " + list.serialize(false), IGamlIssue.WRONG_TYPE);
					return;
				}
			}
			/**
			 * Warnings for agent variables
			 */
			if (index != null && list.getGamlType().isAgentType() && index.isConst()) {
				final String s = index.literalValue();
				final SpeciesDescription sd = list.getGamlType().getSpecies();
				if (sd.hasAttribute(s)) {
					if (keyword.equals(PUT)) {
						cd.warning("Attribute '" + s + "' will not be modified by this statement. Use '"
								+ list.serialize(false) + "." + s + "' instead", IGamlIssue.WRONG_CONTEXT);
					} else if (keyword.equals(REMOVE)) {
						cd.warning("Attribute '" + s + "' cannot be removed. ", IGamlIssue.WRONG_CONTEXT);
					}
				}

			}

			// TODO Add an error if list is a VarExpression and is not
			// modifiable
			// TODO Add an error if both item and whole are != null and whole is
			// not a boolean
			// TODO Add an error if both index and all are != null and item ==
			// null
			// TODO Add an error if graph and index not instance of pair
			// TODO Change the warning for graph indexes (like for maps?)
			validateIndexAndContentTypes(keyword, cd, all);

		}

		/**
		 * @param list
		 * @param item
		 * @param index
		 * @param whole
		 */
		public void validateIndexAndContentTypes(final String keyword, final IDescription cd, final boolean all) {
			final IExpression item = cd.getFacetExpr(ITEM);
			final IExpression list = cd.getFacetExpr(TO);
			// IExpression whole = cd.getFacets().getExpr(ALL);
			final IExpression index = cd.getFacetExpr(AT);

			if (list instanceof BinaryOperator && ((BinaryOperator) list).getName().equals("internal_between")) {
				// Corresponds to a wrong usage of the range with add, remove operators
				cd.error("Ranges of indices can only be used in conjunction with `put` or `<-`",
						IGamlIssue.CONFLICTING_FACETS, IKeyword.AT);
				return;
			}

			if (!keyword.equals(REMOVE)) {
				if (item == null) // we are in the case "remove all: true from...". Nothing to
					// validate
					return;
				final IType<?> contentType = list.getGamlType().getContentType();
				boolean isAll = false;
				IType<?> valueType;
				if (!keyword.equals(PUT) && all && item.getGamlType().isTranslatableInto(Types.CONTAINER)) {
					isAll = true;
					valueType = item.getGamlType().getContentType();
				} else {
					valueType = item.getGamlType();
				}

				if (contentType != Types.NO_TYPE && !valueType.isTranslatableInto(contentType)
						&& !Types.isEmptyContainerCase(contentType, item)) {
					String message = "The type of the elements of " + list.serialize(false) + " (" + contentType
							+ ") does not match with the type of the ";
					if (isAll) {
						message += "elements of the argument";
					} else {
						message += "argument";
					}
					message += " (" + valueType + "). ";
					if (isAll) {
						message += "These elements will be casted to " + contentType + ". ";
					} else {
						message += "The argument will be casted to " + contentType + ". ";
					}
					cd.warning(message, IGamlIssue.SHOULD_CAST, IKeyword.ITEM,
							isAll ? list.getGamlType().toString() : contentType.toString());
				}
				final IType<?> keyType = list.getGamlType().getKeyType();
				if (index != null && keyType != Types.NO_TYPE && !index.getGamlType().isTranslatableInto(keyType)) {
					if (Types.LIST.isAssignableFrom(list.getGamlType())) {
						if (Types.PAIR.of(Types.INT, Types.INT).equals(index.getGamlType())) return;
					}
					// These indices are accepted for matrices (int and list<int>)
					if (!(Types.MATRIX.isAssignableFrom(list.getGamlType()) && (index.getGamlType() == Types.INT
							|| index.getGamlType().equals(Types.LIST.of(Types.INT))))) {
						cd.warning(
								"The type of the index of " + list.serialize(false) + " (" + keyType
										+ ") does not match with the type of " + index.serialize(false) + " ("
										+ index.getGamlType() + "). The latter will be casted to " + keyType,
								IGamlIssue.SHOULD_CAST, IKeyword.AT, keyType.toString());
					}
				}
			}
		}
	}

	protected IExpression item, index, list, all;
	final boolean asAll, asAllValues, asAllIndexes;
	// Identifies whether or not the container is directly modified by the
	// statement or if it is a shape or an agent
	final boolean isDirect, isGraph;

	// The "real" container type
	// final IContainerType containerType;

	// private static final IType attributesType = Types.MAP.of(Types.STRING,
	// Types.NO_TYPE);

	public AbstractContainerStatement(final IDescription desc) {
		super(desc);

		item = getFacet(IKeyword.ITEM);
		index = getFacet(IKeyword.AT);
		all = getFacet(IKeyword.ALL);
		list = getFacet(IKeyword.TO);

		asAll = all != null && all.literalValue().equals(IKeyword.TRUE);
		asAllValues = asAll && item != null && item.getGamlType().isTranslatableInto(Types.CONTAINER);
		asAllIndexes = asAll && index != null && index.getGamlType().isTranslatableInto(Types.CONTAINER);
		final IType<?> t = list.getGamlType();
		isDirect = t.isContainer();
		isGraph = t.isTranslatableInto(Types.GRAPH);
		// containerType = (IContainerType) (isDirect ? t : attributesType);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		// We then identify the container
		final IContainer.Modifiable container = identifyContainer(scope);

		final Object position = identifyIndex(scope, container);
		final Object object = identifyValue(scope, container);
		// And apply the operation (add, put or remove)
		apply(scope, object, position, container);
		// Added fix for Issue 1048 (dont change the value of temp variables
		// If the list is an attribute of an agent, we change its value
		if (isDirect && list instanceof IVarExpression.Agent) {
			((IVarExpression) list).setVal(scope, container, false);
		}
		// The defaut return value is the changed container
		return container;

	}

	protected Object identifyValue(final IScope scope, final IContainer.Modifiable container) {
		if (item == null) return null;
		// For the moment, only graphs need to recompute their objects
		if (isGraph) return buildValue(scope, (IGraph) container);
		return item.value(scope);
	}

	protected Object identifyIndex(final IScope scope, final IContainer.Modifiable container) {
		if (index == null) return null;
		if (isGraph) return buildIndex(scope, (IGraph) container);
		return index.value(scope);
	}

	protected Object buildValue(final IScope scope, final IGraph container) {
		if (asAllValues) return container.buildValues(scope, (IContainer) this.item.value(scope));
		return container.buildValue(scope, this.item.value(scope));
	}

	protected Object buildIndex(final IScope scope, final IGraph container) {
		if (asAllIndexes) return container.buildIndexes(scope, (IContainer) this.index.value(scope));
		return container.buildIndex(scope, this.index.value(scope));
	}

	/**
	 * @throws GamaRuntimeException
	 * @return the container to which this command will be applied
	 */
	private IContainer.Modifiable identifyContainer(final IScope scope) throws GamaRuntimeException {
		final Object cont = list.value(scope);
		if (isDirect) return (IContainer.Modifiable) cont;
		if (cont instanceof IShape) return ((IShape) cont).getOrCreateAttributes();
		throw GamaRuntimeException.warning("Cannot use " + list.serialize(false) + ", of type "
				+ list.getGamlType().toString() + ", as a container", scope);
	}

	/**
	 * Method to add, remove or put one individual item
	 *
	 * @param scope
	 * @param object
	 * @param position
	 * @param container
	 * @throws GamaRuntimeException
	 */
	protected abstract void apply(IScope scope, Object object, Object position, IContainer.Modifiable container)
			throws GamaRuntimeException;

}