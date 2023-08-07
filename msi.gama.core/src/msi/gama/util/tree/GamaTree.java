/*******************************************************************************************************
 *
 * GamaTree.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.tree;

/*
 * Copyright 2010 Vivin Suresh Paliath Distributed under the BSD License
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import msi.gama.util.GamaMapFactory;

/**
 * The Class GamaTree.
 *
 * @param <T>
 *            the generic type
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaTree<T> {

	/**
	 * With root.
	 *
	 * @param <T>
	 *            the generic type
	 * @param root
	 *            the root
	 * @return the gama tree
	 */
	public static <T> GamaTree<T> withRoot(final GamaNode<T> root) {
		final GamaTree<T> tree = new GamaTree<>();
		tree.setRoot(root);
		return tree;
	}

	/**
	 * With root.
	 *
	 * @param <T>
	 *            the generic type
	 * @param root
	 *            the root
	 * @return the gama tree
	 */
	public static <T> GamaTree<T> withRoot(final T root) {
		final GamaTree<T> tree = new GamaTree<>();
		tree.setRoot(root);
		return tree;
	}

	/**
	 * The Enum Order.
	 */
	public enum Order {

		/** The pre order. */
		PRE_ORDER,
		/** The post order. */
		POST_ORDER
	}

	/** The root. */
	private GamaNode<T> root;

	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	public GamaNode<T> getRoot() { return this.root; }

	/**
	 * Sets the root.
	 *
	 * @param root
	 *            the new root
	 */
	public void setRoot(final GamaNode<T> root) { this.root = root; }

	/**
	 * Sets the root.
	 *
	 * @param data
	 *            the new root
	 */
	public GamaNode<T> setRoot(final T data) {
		return setRoot(data, GamaNode.DEFAULT_WEIGHT);
	}

	/**
	 * Sets the root.
	 *
	 * @param root
	 *            the root
	 * @param weight
	 *            the weight
	 * @return the gama node
	 */
	public GamaNode<T> setRoot(final T root, final Integer weight) {
		final GamaNode<T> result = new GamaNode(root, weight);
		setRoot(result);
		return result;
	}

	/**
	 * Visits the tree in the order defined. No pruning is done.
	 *
	 * @param traversalOrder
	 * @param visitor
	 */
	public void visit(final Order traversalOrder, final Consumer<GamaNode<T>> visitor) {
		if (root == null) return;
		if (traversalOrder == Order.PRE_ORDER) {
			visitPreOrder(root, visitor);
		} else if (traversalOrder == Order.POST_ORDER) { visitPostOrder(root, visitor); }
	}

	/**
	 * Visit pre order.
	 *
	 * @param node
	 *            the node
	 * @param visitor
	 *            the visitor
	 */
	public void visitPreOrder(final GamaNode<T> node, final Consumer<GamaNode<T>> visitor) {
		visitor.accept(node);
		for (final GamaNode<T> child : node.getChildren()) { visitPreOrder(child, visitor); }
	}

	/**
	 * Visit post order.
	 *
	 * @param node
	 *            the node
	 * @param visitor
	 *            the visitor
	 */
	public void visitPostOrder(final GamaNode<T> node, final Consumer<GamaNode<T>> visitor) {
		for (final GamaNode<T> child : node.getChildren()) { visitPostOrder(child, visitor); }
		visitor.accept(node);
	}

	/**
	 * List.
	 *
	 * @param traversalOrder
	 *            the traversal order
	 * @return the list
	 */
	public List<GamaNode<T>> list(final Order traversalOrder) {
		if (root == null) return Collections.EMPTY_LIST;
		final List<GamaNode<T>> returnList = new ArrayList<>();
		visit(traversalOrder, returnList::add);
		return returnList;
	}

	/**
	 * Map by depth.
	 *
	 * @param traversalOrder
	 *            the traversal order
	 * @return the map
	 */
	public Map<GamaNode<T>, Integer> mapByDepth(final Order traversalOrder) {
		if (root == null) return Collections.EMPTY_MAP;
		final Map<GamaNode<T>, Integer> returnMap = GamaMapFactory.create();
		if (traversalOrder == Order.PRE_ORDER) {
			mapPreOrderWithDepth(root, returnMap, 0);
		} else if (traversalOrder == Order.POST_ORDER) { mapPostOrderWithDepth(root, returnMap, 0); }
		return returnMap;
	}

	/**
	 * Map pre order with depth.
	 *
	 * @param node
	 *            the node
	 * @param traversalResult
	 *            the traversal result
	 * @param depth
	 *            the depth
	 */
	private void mapPreOrderWithDepth(final GamaNode<T> node, final Map<GamaNode<T>, Integer> traversalResult,
			final int depth) {
		traversalResult.put(node, depth);
		for (final GamaNode<T> child : node.getChildren()) { mapPreOrderWithDepth(child, traversalResult, depth + 1); }
	}

	/**
	 * Map post order with depth.
	 *
	 * @param node
	 *            the node
	 * @param traversalResult
	 *            the traversal result
	 * @param depth
	 *            the depth
	 */
	private void mapPostOrderWithDepth(final GamaNode<T> node, final Map<GamaNode<T>, Integer> traversalResult,
			final int depth) {
		for (final GamaNode<T> child : node.getChildren()) { mapPostOrderWithDepth(child, traversalResult, depth + 1); }
		traversalResult.put(node, depth);
	}

	@Override
	public String toString() {
		return root.toString();
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		if (root != null) {
			root.dispose();
			root = null;
		}
	}

}
