/*********************************************************************************************
 *
 *
 * 'AbstractContainerStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gama.util.GamaList;
import msi.gama.util.GamaPair;
import msi.gama.util.IContainer;
import msi.gama.util.graph.IGraph;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SpeciesDescription;
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
@validator(ContainerValidator.class)
@SuppressWarnings({ "rawtypes" })
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
			if (whole != null && whole.getType().id() != IType.BOOL) {
				cd.setFacet(ITEM, wholeDesc);
				cd.removeFacets(ALL);
				cd.setFacet(ALL, IExpressionFactory.TRUE_EXPR);
			}

			/**
			 * After these operations, the statement should be provided with:
			 * ITEM: the object to add/remove/put ALL: a boolean indicating
			 * whether (or not) to treat the operation as "all" TO: the
			 * container to change AT: the index at which the operation should
			 * be done (if any)
			 *
			 * All other facets are then meaningless. As a consequence, ITEM is
			 * never null except in the case of the "remove all: true from: ..."
			 */

			final String keyword = cd.getKeyword();
			final boolean all = whole == null ? false : !whole.literalValue().equals(FALSE);
			if (item == null && !all && !keyword.equals(REMOVE) || list == null) {
				cd.error("The assignment appears uncomplete", IGamlIssue.GENERAL);
				return;
			}
			if (keyword.equals(ADD) || keyword.equals(REMOVE)) {
				final IType containerType = list.getType();
				if (containerType.isFixedLength()) {
					cd.error("Impossible to add/remove to/from " + list.serialize(false), IGamlIssue.WRONG_TYPE);
					return;
				}
			}
			/**
			 * Warnings for agent variables
			 */
			if (index != null && list.getType().isAgentType() && index.isConst()) {
				final String s = index.literalValue();
				final SpeciesDescription sd = list.getType().getSpecies();
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
			if (!keyword.equals(REMOVE)) {
				if (item == null) {
					// we are in the case "remove all: true from...". Nothing to
					// validate
					return;
				}
				final IType<?> contentType = list.getType().getContentType();
				boolean isAll = false;
				IType<?> valueType = Types.NO_TYPE;
				if (!keyword.equals(PUT) && all && item.getType().isTranslatableInto(Types.CONTAINER)) {
					isAll = true;
					valueType = item.getType().getContentType();
				} else {
					valueType = item.getType();
				}

				if (contentType != Types.NO_TYPE && !valueType.isTranslatableInto(contentType)) {
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
							isAll ? list.getType().toString() : contentType.toString());
				}
				final IType<?> keyType = list.getType().getKeyType();
				if (index != null && keyType != Types.NO_TYPE && !keyType.isTranslatableInto(index.getType())) {
					cd.warning(
							"The type of the index of " + list.serialize(false) + " (" + keyType
									+ ") does not match with the type of " + index.serialize(false) + " ("
									+ index.getType() + "). The latter will be casted to " + keyType,
							IGamlIssue.SHOULD_CAST, IKeyword.AT, keyType.toString());
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
		asAllValues = asAll && item != null && item.getType().isTranslatableInto(Types.CONTAINER);
		asAllIndexes = asAll && index != null && index.getType().isTranslatableInto(Types.CONTAINER);
		final IType<?> t = list.getType();
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
		if (item == null) {
			return null;
		}
		// For the moment, only graphs need to recompute their objects
		if (isGraph) {
			return buildValue(scope, (IGraph) container);
		}
		return item.value(scope);
	}

	protected Object identifyIndex(final IScope scope, final IContainer.Modifiable container) {
		if (index == null) {
			return null;
		}
		if (isGraph) {
			return buildIndex(scope, (IGraph) container);
		}
		return index.value(scope);
	}

	protected Object buildValue(final IScope scope, final IGraph container) {
		if (asAllValues) {
			return container.buildValues(scope, (IContainer) this.item.value(scope));
		}
		return container.buildValue(scope, this.item.value(scope));
	}

	protected Object buildIndex(final IScope scope, final IGraph container) {
		if (asAllIndexes) {
			return container.buildIndexes(scope, (IContainer) this.index.value(scope));
		}
		return container.buildIndex(scope, this.index.value(scope));
	}

	/**
	 * @throws GamaRuntimeException
	 * @return the container to which this command will be applied
	 */
	private IContainer.Modifiable identifyContainer(final IScope scope) throws GamaRuntimeException {
		final Object cont = list.value(scope);
		if (isDirect) {
			return (IContainer.Modifiable) cont;
		}
		if (cont instanceof IShape) {
			return ((IShape) cont).getOrCreateAttributes();
		}
		throw GamaRuntimeException.warning(
				"Cannot use " + list.serialize(false) + ", of type " + list.getType().toString() + ", as a container",
				scope);
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

	/**
	 * Placeholders for fake expressions used to build complex items (like edges
	 * and nodes). These expressions are never evaluated, and return special
	 * graph objects (node, edge, nodes and edges)
	 */

	public static interface GraphObjectToAdd {

		Object getObject();
	}

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

		@Override
		public Object getObject() {
			return object;
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

		@Override
		public Object getObject() {
			return object;
		}

	}

	public static class NodesToAdd extends GamaList<GraphObjectToAdd> implements GraphObjectToAdd {

		public NodesToAdd() {
			super(0, Types.NO_TYPE);
		}

		public static NodesToAdd from(final IScope scope, final IContainer object) {
			final NodesToAdd n = new NodesToAdd();
			for (final Object o : object.iterable(scope)) {
				n.add((GraphObjectToAdd) o);
			}
			return n;
		}

		@Override
		public Object getObject() {
			return this;
		}

	}

	public static class EdgesToAdd extends GamaList<GraphObjectToAdd> implements GraphObjectToAdd {

		public EdgesToAdd() {
			super(0, Types.NO_TYPE);
		}

		public static EdgesToAdd from(final IScope scope, final IContainer object) {
			final EdgesToAdd n = new EdgesToAdd();
			for (final Object o : object.iterable(scope)) {
				n.add((GraphObjectToAdd) o);
			}
			return n;
		}

		@Override
		public Object getObject() {
			return this;
		}

	}

	@operator(value = "edge", type = ITypeProvider.FIRST_TYPE, // FIXME This is
																// false
			category = {
					IOperatorCategory.GRAPH }, doc = @doc(value = "Allows to create a wrapper (of type unknown) that wraps two objects and indicates they should be considered as the source and the target of a new edge of a graph. The third parameter indicates which weight this edge should have in the graph", comment = "Useful only in graph-related operations (addition, removal of edges, creation of graphs)"))
	public static Object edge(final Object source, final Object target, final Double weight) {
		return edge(source, target, null, weight);
	}

	@operator(value = "edge", type = ITypeProvider.TYPE_AT_INDEX + 1, category = {
			IOperatorCategory.GRAPH }, doc = @doc(value = "Allows to create a wrapper (of type unknown) that wraps a pair of objects and a third and indicates  they should respectively be considered as the source (key of the pair), the target (value of the pair) and the actual object representing an edge of a graph. The third parameter indicates which weight this edge should have in the graph", comment = "Useful only in graph-related operations (addition, removal of edges, creation of graphs)"))
	public static Object edge(final GamaPair pair, final Object object, final Double weight) {
		return edge(pair.key, pair.value, object, weight);
	}

	@operator(value = "edge", type = ITypeProvider.FIRST_TYPE, // FIXME this is
																// false
			category = {
					IOperatorCategory.GRAPH }, doc = @doc(value = "Allows to create a wrapper (of type unknown) that wraps two objects and indicates they should be considered as the source and the target of a new edge of a graph ", comment = "Useful only in graph-related operations (addition, removal of edges, creation of graphs)"))
	public static Object edge(final Object source, final Object target) {
		return edge(source, target, null, null);
	}

	@operator(value = "edge", type = ITypeProvider.TYPE_AT_INDEX + 2, category = {
			IOperatorCategory.GRAPH }, doc = @doc(value = "Allows to create a wrapper (of type unknown) that wraps three objects and indicates they should respectively be considered as the source, the target and the actual object representing an edge of a graph", comment = "Useful only in graph-related operations (addition, removal of edges, creation of graphs)"))
	public static Object edge(final Object source, final Object target, final Object object) {
		return edge(source, target, object, null);
	}

	@operator(value = "edge", type = ITypeProvider.TYPE_AT_INDEX + 2, category = {
			IOperatorCategory.GRAPH }, doc = @doc(value = "Allows to create a wrapper (of type unknown) that wraps two objects and indicates they should be considered as the source and the target of a new edge of a graph. The fourth parameter indicates which weight this edge should have in the graph", comment = "Useful only in graph-related operations (addition, removal of edges, creation of graphs)"))
	public static Object edge(final Object source, final Object target, final Object object, final Double weight) {
		return new EdgeToAdd(source, target, object, weight);
	}

	@operator(value = "edge", type = ITypeProvider.FIRST_TYPE, category = {
			IOperatorCategory.GRAPH }, doc = @doc(value = "Allows to create a wrapper (of type unknown) that wraps an actual object and indicates it should be considered as an edge of a graph. The second parameter indicates which weight this edge should have in the graph", comment = "Useful only in graph-related operations (addition, removal of edges, creation of graphs)"))
	public static Object edge(final Object edgeObject, final Double weight) {
		return edge(null, null, edgeObject, weight);
	}

	@operator(value = "edge", type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
			IOperatorCategory.GRAPH }, doc = @doc(value = "Allows to create a wrapper (of type unknown) that wraps a pair of objects and indicates they should be considered as the source and target of an edge. The second parameter indicates which weight this edge should have in the graph", comment = "Useful only in graph-related operations (addition, removal of edges, creation of graphs)"))
	public static Object edge(final GamaPair pair, final Double weight) {
		return edge(pair.key, pair.value, null, weight);
	}

	@operator(value = "edge", type = ITypeProvider.FIRST_TYPE, category = {
			IOperatorCategory.GRAPH }, doc = @doc(value = "Allows to create a wrapper (of type unknown) that wraps an actual object and indicates it should be considered as an edge of a graph", comment = "Useful only in graph-related operations (addition, removal of edges, creation of graphs)"))
	public static Object edge(final Object object) {
		return edge(null, null, object, null);
	}

	@operator(value = "edge", type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
			IOperatorCategory.GRAPH }, doc = @doc(value = "Allows to create a wrapper (of type unknown) that wraps a pair of objects and indicates they should be considered as the source and target of an edge of a graph", comment = "Useful only in graph-related operations (addition, removal of edges, creation of graphs)"))
	public static Object edge(final GamaPair pair) {
		return edge(pair.key, pair.value, null, null);
	}

	@operator(value = "node", type = ITypeProvider.FIRST_TYPE, category = {
			IOperatorCategory.GRAPH }, doc = @doc(value = "Allows to create a wrapper (of type unknown) that wraps an actual object and indicates it should be considered as a node of a graph. The second parameter indicates which weight the node should have in the graph", comment = "Useful only in graph-related operations (addition, removal of nodes, creation of graphs)"))
	public static Object node(final Object object, final Double weight) {
		return new NodeToAdd(object, weight);
	}

	@operator(value = "node", type = ITypeProvider.FIRST_TYPE, category = {
			IOperatorCategory.GRAPH }, doc = @doc(value = "Allows to create a wrapper (of type unknown) that wraps an actual object and indicates it should be considered as a node of a graph", comment = "Useful only in graph-related operations (addition, removal of nodes, creation of graphs)"))
	public static Object node(final Object nodeObject) {
		return node(nodeObject, null);
	}

	@operator(value = "nodes", type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
			IOperatorCategory.GRAPH }, doc = @doc(value = "Allows to create a wrapper (of type list) that wraps a list of objects and indicates they should be considered as nodes of a graph", comment = "Useful only in graph-related operations (addition, removal of nodes, creation of graphs)"))
	public static IContainer nodes(final IScope scope, final IContainer nodes) {
		return NodesToAdd.from(scope, nodes);
	}

	@operator(value = "edges", type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
			IOperatorCategory.GRAPH }, doc = @doc(value = "Allows to create a wrapper (of type list) that wraps a list of objects and indicates they should be considered as edges of a graph", comment = "Useful only in graph-related operations (addition, removal of edges, creation of graphs)"))
	public static IContainer edges(final IScope scope, final IContainer nodes) {
		return EdgesToAdd.from(scope, nodes);
	}

}