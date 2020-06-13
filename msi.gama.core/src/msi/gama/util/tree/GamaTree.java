/*******************************************************************************************************
 *
 * msi.gama.util.tree.GamaTree.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaTree<T> {

	public static <T> GamaTree<T> withRoot(final GamaNode<T> root) {
		final GamaTree<T> tree = new GamaTree<>();
		tree.setRoot(root);
		return tree;
	}

	public static <T> GamaTree<T> withRoot(final T root) {
		final GamaTree<T> tree = new GamaTree<>();
		tree.setRoot(root);
		return tree;
	}

	public enum Order {
		PRE_ORDER, POST_ORDER
	}

	private GamaNode<T> root;

	public GamaNode<T> getRoot() {
		return this.root;
	}

	public void setRoot(final GamaNode<T> root) {
		this.root = root;
	}

	public void setRoot(final T data) {
		setRoot(data, GamaNode.DEFAULT_WEIGHT);
	}

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
		if (root == null) { return; }
		if (traversalOrder == Order.PRE_ORDER) {
			visitPreOrder(root, visitor);
		} else if (traversalOrder == Order.POST_ORDER) {
			visitPostOrder(root, visitor);
		}
	}

	public void visitPreOrder(final GamaNode<T> node, final Consumer<GamaNode<T>> visitor) {
		visitor.accept(node);
		for (final GamaNode<T> child : node.getChildren()) {
			visitPreOrder(child, visitor);
		}
	}

	public void visitPostOrder(final GamaNode<T> node, final Consumer<GamaNode<T>> visitor) {
		for (final GamaNode<T> child : node.getChildren()) {
			visitPostOrder(child, visitor);
		}
		visitor.accept(node);
	}

	public List<GamaNode<T>> list(final Order traversalOrder) {
		if (root == null) { return Collections.EMPTY_LIST; }
		final List<GamaNode<T>> returnList = new ArrayList<>();
		visit(traversalOrder, returnList::add);
		return returnList;
	}

	public Map<GamaNode<T>, Integer> mapByDepth(final Order traversalOrder) {
		if (root == null) { return Collections.EMPTY_MAP; }
		final Map<GamaNode<T>, Integer> returnMap = GamaMapFactory.create();
		if (traversalOrder == Order.PRE_ORDER) {
			mapPreOrderWithDepth(root, returnMap, 0);
		} else if (traversalOrder == Order.POST_ORDER) {
			mapPostOrderWithDepth(root, returnMap, 0);
		}
		return returnMap;
	}

	private void mapPreOrderWithDepth(final GamaNode<T> node, final Map<GamaNode<T>, Integer> traversalResult,
			final int depth) {
		traversalResult.put(node, depth);
		for (final GamaNode<T> child : node.getChildren()) {
			mapPreOrderWithDepth(child, traversalResult, depth + 1);
		}
	}

	private void mapPostOrderWithDepth(final GamaNode<T> node, final Map<GamaNode<T>, Integer> traversalResult,
			final int depth) {
		for (final GamaNode<T> child : node.getChildren()) {
			mapPostOrderWithDepth(child, traversalResult, depth + 1);
		}
		traversalResult.put(node, depth);
	}

	@Override
	public String toString() {
		return root.toString();
	}

	public void dispose() {
		if (root != null) {
			root.dispose();
			root = null;
		}
	}

}
