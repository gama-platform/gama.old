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
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
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

			// 17/02/14: We change the facets to simplify the writing of statements. EDGE and VERTEX are removed
			final Facets f = cd.getFacets();
			IExpressionDescription itemDesc = f.getDescr(ITEM, EDGE, VERTEX, NODE);
			IExpression item = itemDesc == null ? null : itemDesc.getExpression();
			if ( item != null ) {
				if ( f.containsKey(EDGE) ) {
					if ( f.contains(WEIGHT) ) {
						item =
							GAML.getExpressionFactory().createOperator("edge", cd, f.get(EDGE).getTarget(), item,
								f.getExpr(WEIGHT));
					} else {
						item = GAML.getExpressionFactory().createOperator("edge", cd, f.get(EDGE).getTarget(), item);
					}
					f.remove(EDGE);
					f.remove(WEIGHT);
				} else if ( f.containsKey(VERTEX) || f.containsKey(NODE) ) {
					boolean isNode = f.containsKey(NODE);
					if ( f.contains(WEIGHT) ) {
						item =
							GAML.getExpressionFactory().createOperator("node", cd,
								isNode ? f.get(NODE).getTarget() : f.get(VERTEX).getTarget(), item, f.getExpr(WEIGHT));
					} else {
						item =
							GAML.getExpressionFactory().createOperator("node", cd,
								isNode ? f.get(NODE).getTarget() : f.get(VERTEX).getTarget(), item);
					}
					f.remove(VERTEX);
					f.remove(NODE);
					f.remove(WEIGHT);
				}
				// itemDesc.setExpression(item);
				f.put(ITEM, item);
			}
			final IExpressionDescription listDesc = f.getDescr(TO, FROM, IN);
			IExpression list = listDesc == null ? null : listDesc.getExpression();
			if ( list != null ) {
				f.put(TO, listDesc);
				f.remove(FROM);
				f.remove(IN);
			}
			final IExpressionDescription indexDesc = f.getDescr(AT, KEY, INDEX);
			IExpression index = indexDesc == null ? null : indexDesc.getExpression();
			if ( index != null ) {
				f.put(AT, indexDesc);
				f.remove(KEY);
				f.remove(INDEX);
			}
			// If a container/value is passed to ALL, then it is copied to ITEM and ALL is set to "true"
			final IExpressionDescription wholeDesc = f.getDescr(ALL);
			IExpression whole = wholeDesc == null ? null : wholeDesc.getExpression();
			if ( whole != null && whole.getType().id() != IType.BOOL ) {
				f.put(ITEM, wholeDesc);
				f.remove(ALL);
				f.put(ALL, new ConstantExpression(true));
			}

			/**
			 * After these operations, the statement should be provided with:
			 * ITEM: the object to add/remove/put
			 * ALL: a boolean indicating whether (or not) to treat the operation as "all"
			 * TO: the container to change
			 * AT: the index at which the operation should be done (if any)
			 * 
			 * All other facets are then meaningless. As a consequence, ITEM is never null
			 * except in the case of the "remove all: true from: ..."
			 */

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

			// TODO Add an error if list is a VarExpression and is not modifiable
			// TODO Add an error if both item and whole are != null and whole is not a boolean
			// TODO Add an error if both index and all are != null and item == null
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
			IExpression item = cd.getFacets().getExpr(ITEM);
			IExpression list = cd.getFacets().getExpr(TO);
			IExpression whole = cd.getFacets().getExpr(ALL);
			IExpression index = cd.getFacets().getExpr(AT);
			if ( !keyword.equals(REMOVE) ) {
				if ( item == null ) {
					// we are in the case "remove all: true from...". Nothing to validate
					return;
				}
				final IType contentType = list.getType().getContentType();
				boolean isAll = false;
				IType valueType = Types.NO_TYPE;
				if ( !keyword.equals(PUT) && all && item.getType().isTranslatableInto(Types.get(IType.CONTAINER)) ) {
					isAll = true;
					valueType = item.getType().getContentType();
				} else {
					valueType = item.getType();
				}

				if ( contentType != Types.NO_TYPE && !valueType.isTranslatableInto(contentType) ) {
					String message =
						"The type of the elements of " + list.toGaml() + " (" + contentType +
							") does not match with the type of the ";
					if ( isAll ) {
						message += "elements of the argument";
					} else {
						message += "argument";
					}
					message += " (" + valueType + "). ";
					if ( isAll ) {
						message += "These elements will be casted to " + contentType + ". ";
					} else {
						message += "The argument will be casted to " + contentType + ". ";
					}
					cd.warning(message, IGamlIssue.SHOULD_CAST, IKeyword.ITEM, isAll ? list.getType().toString()
						: contentType.toString());
				}
				final IType keyType = list.getType().getKeyType();
				if ( index != null && keyType != Types.NO_TYPE && !keyType.isTranslatableInto(index.getType()) ) {
					cd.warning("The type of the index of " + list.toGaml() + " (" + keyType +
						") does not match with the type of " + index.toGaml() + " (" + index.getType() +
						"). The latter will be casted to " + keyType, IGamlIssue.SHOULD_CAST, IKeyword.AT,
						keyType.toString());
				}
			}
		}
	}

	protected IExpression item, index, list, all;
	final boolean asAll, asAllValues, asAllIndexes;
	// Identifies whether or not the container is directly modified by the statement or if it is a shape or an agent
	final boolean isDirect;
	// The "real" container type
	final IContainerType containerType;

	public AbstractContainerStatement(final IDescription desc) {
		super(desc);

		item = getFacet(IKeyword.ITEM);
		index = getFacet(IKeyword.AT);
		all = getFacet(IKeyword.ALL);
		list = getFacet(IKeyword.TO);

		asAll = all != null && all.literalValue().equals(IKeyword.TRUE);
		asAllValues = asAll && item != null && item.getType().isTranslatableInto(Types.get(IType.CONTAINER));
		asAllIndexes = asAll && index != null && index.getType().isTranslatableInto(Types.get(IType.CONTAINER));
		IType t = list.getType();
		isDirect = t.isContainer();
		containerType =
			(IContainerType) (isDirect ? t : GamaType
				.from(Types.get(IType.MAP), Types.get(IType.STRING), Types.NO_TYPE));
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		// We then identify the container
		final IContainer.Modifiable container = identifyContainer(scope);

		// TODO Create a buildIndexes method ?
		Object position = index != null ? buildIndex(scope, container) : null;
		Object object = item != null ? buildValue(scope, container) : null;
		// And apply the operation (add, put or remove)
		apply(scope, object, position, container);
		// If the list is a variable, we change its value
		if ( isDirect && list instanceof IVarExpression ) {
			((IVarExpression) list).setVal(scope, container, false);
		}
		// The defaut return value is the changed container
		return container;

	}

	protected Object buildValue(final IScope scope, final IContainer.Modifiable container) {
		if ( asAllValues ) { return container.buildValues(scope, (IContainer) this.item.value(scope), containerType); }
		return container.buildValue(scope, this.item.value(scope), containerType);
	}

	protected Object buildIndex(final IScope scope, final IContainer.Modifiable container) {
		if ( asAllIndexes ) { return container.buildIndexes(scope, (IContainer) this.index.value(scope), containerType); }
		return container.buildIndex(scope, this.index.value(scope), containerType);
	}

	/**
	 * @throws GamaRuntimeException
	 * @return the container to which this command will be applied
	 */
	private IContainer.Modifiable identifyContainer(final IScope scope) throws GamaRuntimeException {
		final Object cont = list.value(scope);
		if ( isDirect ) { return (IContainer.Modifiable) cont; }
		if ( cont instanceof IShape ) { return ((IShape) cont).getOrCreateAttributes(); }
		throw GamaRuntimeException.warning("Cannot use " + list.toGaml() + ", of type " + list.getType().toString() +
			", as a container");
	}

	/**
	 * Method to add, remove or put one individual item
	 * @param scope
	 * @param object
	 * @param position
	 * @param container
	 * @throws GamaRuntimeException
	 */
	protected abstract void apply(IScope scope, Object object, Object position, IContainer.Modifiable container)
		throws GamaRuntimeException;

	/**
	 * Placeholders for fake expressions used to build complex items (like edges and nodes). These expressions are never
	 * evaluated, and return special graph objects (node, edge, nodes and edges)
	 */

	public static interface GraphObjectToAdd {}

	public static class EdgeToAdd implements GraphObjectToAdd {

		public Object source, target;
		public Object object;
		public Double weight;

		public EdgeToAdd(final Object source, final Object target, final Object object, final Double weight) {
			this.object = object;
			this.weight = weight;
			this.source = source;
			this.target = target;
		}

		/**
		 * @param cast
		 */
		public EdgeToAdd(final Object o) {
			this.object = o;
		}
	}

	public static class NodeToAdd implements GraphObjectToAdd {

		public Object object;
		public Double weight;

		public NodeToAdd(final Object object, final Double weight) {
			this.object = object;
			this.weight = weight;
		}

		/**
		 * @param cast
		 */
		public NodeToAdd(final Object o) {
			object = o;
		}

	}

	public static class NodesToAdd extends GamaList<GraphObjectToAdd> implements GraphObjectToAdd {

		public NodesToAdd(final IScope scope, final IContainer object) {
			super(object.iterable(scope));
		}

	}

	public static class EdgesToAdd extends GamaList<GraphObjectToAdd> implements GraphObjectToAdd {

		public EdgesToAdd(final IScope scope, final IContainer object) {
			super(object.iterable(scope));
		}

	}

	@operator(value="edge", category={IOperatorCategory.GRAPH})
	public static Object edge(final Object source, final Object target, final Double weight) {
		return edge(source, target, null, weight);
	}

	@operator(value="edge", category={IOperatorCategory.GRAPH})
	public static Object edge(final GamaPair pair, final Object object, final Double weight) {
		return edge(pair.key, pair.value, object, weight);
	}

	@operator(value="edge", category={IOperatorCategory.GRAPH})
	public static Object edge(final Object source, final Object target) {
		return edge(source, target, null, null);
	}

	@operator(value="edge", category={IOperatorCategory.GRAPH})
	public static Object edge(final Object source, final Object target, final Object object) {
		return edge(source, target, object, null);
	}

	@operator(value="edge", category={IOperatorCategory.GRAPH})
	public static Object edge(final Object source, final Object target, final Object object, final Double weight) {
		return new EdgeToAdd(source, target, object, weight);
	}

	@operator(value = "edge", type = ITypeProvider.FIRST_TYPE, category={IOperatorCategory.GRAPH})
	public static Object edge(final Object edgeObject, final Double weight) {
		return edge(null, null, edgeObject, weight);
	}

	@operator(value = "edge", category={IOperatorCategory.GRAPH})
	public static Object edge(final GamaPair pair, final Double weight) {
		return edge(pair.key, pair.value, null, weight);
	}

	@operator(value = "edge", type = ITypeProvider.FIRST_TYPE, category={IOperatorCategory.GRAPH})
	public static Object edge(final Object object) {
		return edge(null, null, object, null);
	}

	@operator(value = "edge", type = ITypeProvider.FIRST_TYPE, category={IOperatorCategory.GRAPH})
	public static Object edge(final GamaPair pair) {
		return edge(pair.key, pair.value, null, null);
	}

	@operator(value = "node", type = ITypeProvider.FIRST_TYPE, category={IOperatorCategory.GRAPH})
	public static Object node(final Object object, final Double weight) {
		return new NodeToAdd(object, weight);
	}

	@operator(value = "node", type = ITypeProvider.FIRST_TYPE, category={IOperatorCategory.GRAPH})
	public static Object node(final Object nodeObject) {
		return node(nodeObject, null);
	}

	@operator(value = "nodes", type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.GRAPH})
	public static IContainer nodes(final IScope scope, final IContainer nodes) {
		return new NodesToAdd(scope, nodes);
	}

	@operator(value = "edges", type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.GRAPH})
	public static IContainer edges(final IScope scope, final IContainer nodes) {
		return new EdgesToAdd(scope, nodes);
	}

}