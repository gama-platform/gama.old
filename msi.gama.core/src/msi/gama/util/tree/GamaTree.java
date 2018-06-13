/*********************************************************************************************
 *
 * 'TypeTree.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.tree;

/*
 * Copyright 2010 Vivin Suresh Paliath
 * Distributed under the BSD License
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import msi.gama.util.TOrderedHashMap;
import msi.gaml.operators.Strings;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaTree<T> {

	public static enum Order {
		PRE_ORDER, POST_ORDER
	}

	private GamaTreeNode<T> root;

	public GamaTree() {
		super();
	}

	public GamaTree(final GamaTreeNode<T> root) {
		setRoot(root);
	}

	public GamaTree(final T root) {
		setRoot(new GamaTreeNode(root));
	}

	public GamaTreeNode<T> getRoot() {
		return this.root;
	}

	public void setRoot(final GamaTreeNode<T> root) {
		this.root = root;
	}

	public GamaTreeNode<T> setRoot(final T root) {
		final GamaTreeNode<T> result = new GamaTreeNode(root);
		setRoot(result);
		return result;
	}

	public int getNumberOfNodes() {
		int numberOfNodes = 0;

		if (root != null) {
			numberOfNodes = auxiliaryGetNumberOfNodes(root) + 1; // 1 for the
																	// root!
		}

		return numberOfNodes;
	}

	private int auxiliaryGetNumberOfNodes(final GamaTreeNode<T> node) {
		int numberOfNodes = node.getNumberOfChildren();

		for (final GamaTreeNode<T> child : node.getChildren()) {
			numberOfNodes += auxiliaryGetNumberOfNodes(child);
		}

		return numberOfNodes;
	}

	public boolean exists(final T dataToFind) {
		return find(dataToFind) != null;
	}

	public GamaTreeNode<T> find(final T dataToFind) {
		GamaTreeNode<T> returnNode = null;

		if (root != null) {
			returnNode = auxiliaryFind(root, dataToFind);
		}

		return returnNode;
	}

	private GamaTreeNode<T> auxiliaryFind(final GamaTreeNode<T> currentNode, final T dataToFind) {
		GamaTreeNode<T> returnNode = null;
		int i = 0;

		if (currentNode.getData().equals(dataToFind)) {
			returnNode = currentNode;
		}

		else if (currentNode.hasChildren()) {
			i = 0;
			while (returnNode == null && i < currentNode.getNumberOfChildren()) {
				returnNode = auxiliaryFind(currentNode.getChildAt(i), dataToFind);
				i++;
			}
		}

		return returnNode;
	}

	public boolean isEmpty() {
		return root == null;
	}

	public List<GamaTreeNode<T>> build(final Order traversalOrder) {
		List<GamaTreeNode<T>> returnList = null;

		if (root != null) {
			returnList = build(root, traversalOrder);
		}

		return returnList;
	}

	public List<GamaTreeNode<T>> build(final GamaTreeNode<T> node, final Order traversalOrder) {
		final List<GamaTreeNode<T>> traversalResult = new ArrayList<GamaTreeNode<T>>();

		if (traversalOrder == Order.PRE_ORDER) {
			buildPreOrder(node, traversalResult);
		}

		else if (traversalOrder == Order.POST_ORDER) {
			buildPostOrder(node, traversalResult);
		}

		return traversalResult;
	}

	public List<T> getAllElements(final GamaTreeNode<T> node, final Order traversalOrder) {

		if (node == null) {
			return Collections.EMPTY_LIST;
		}
		final List<T> traversalResult = new ArrayList<T>();
		if (traversalOrder == Order.PRE_ORDER) {
			getAllPreOrder(node, traversalResult);
		}

		else if (traversalOrder == Order.POST_ORDER) {
			getAllPostOrder(node, traversalResult);
		}

		return traversalResult;
	}

	public GamaTree<T> copy() {
		final GamaTree<T> result = new GamaTree();
		result.setRoot(getRoot().copy());
		return result;
	}

	public List<T> getAllElements(final Order order) {
		return getAllElements(root, order);
	}

	public List<T> getAllElements(final T data, final Order traversalOrder) {
		final List<T> traversalResult = new ArrayList<T>();
		final GamaTreeNode<T> node = find(data);
		if (node == null) {
			return Collections.EMPTY_LIST;
		}
		if (traversalOrder == Order.PRE_ORDER) {
			getAllPreOrder(node, traversalResult);
		}

		else if (traversalOrder == Order.POST_ORDER) {
			getAllPostOrder(node, traversalResult);
		}

		return traversalResult;

	}

	private void buildPreOrder(final GamaTreeNode<T> node, final List<GamaTreeNode<T>> traversalResult) {
		traversalResult.add(node);

		for (final GamaTreeNode<T> child : node.getChildren()) {
			buildPreOrder(child, traversalResult);
		}
	}

	private void getAllPreOrder(final GamaTreeNode<T> node, final List<T> traversalResult) {
		traversalResult.add(node.getData());

		for (final GamaTreeNode<T> child : node.getChildren()) {
			getAllPreOrder(child, traversalResult);
		}
	}

	private void getAllPostOrder(final GamaTreeNode<T> node, final List<T> traversalResult) {
		for (final GamaTreeNode<T> child : node.getChildren()) {
			getAllPostOrder(child, traversalResult);
		}

		traversalResult.add(node.getData());
	}

	private void buildPostOrder(final GamaTreeNode<T> node, final List<GamaTreeNode<T>> traversalResult) {
		for (final GamaTreeNode<T> child : node.getChildren()) {
			buildPostOrder(child, traversalResult);
		}

		traversalResult.add(node);
	}

	public Map<GamaTreeNode<T>, Integer> buildWithDepth(final Order traversalOrder) {
		Map<GamaTreeNode<T>, Integer> returnMap = null;

		if (root != null) {
			returnMap = buildWithDepth(root, traversalOrder);
		}

		return returnMap;
	}

	public Map<GamaTreeNode<T>, Integer> buildWithDepth(final GamaTreeNode<T> node, final Order traversalOrder) {
		final Map<GamaTreeNode<T>, Integer> traversalResult = new TOrderedHashMap<GamaTreeNode<T>, Integer>();

		if (traversalOrder == Order.PRE_ORDER) {
			buildPreOrderWithDepth(node, traversalResult, 0);
		}

		else if (traversalOrder == Order.POST_ORDER) {
			buildPostOrderWithDepth(node, traversalResult, 0);
		}

		return traversalResult;
	}

	private void buildPreOrderWithDepth(final GamaTreeNode<T> node, final Map<GamaTreeNode<T>, Integer> traversalResult,
			final int depth) {
		traversalResult.put(node, depth);

		for (final GamaTreeNode<T> child : node.getChildren()) {
			buildPreOrderWithDepth(child, traversalResult, depth + 1);
		}
	}

	private void buildPostOrderWithDepth(final GamaTreeNode<T> node, final Map<GamaTreeNode<T>, Integer> traversalResult,
			final int depth) {
		for (final GamaTreeNode<T> child : node.getChildren()) {
			buildPostOrderWithDepth(child, traversalResult, depth + 1);
		}

		traversalResult.put(node, depth);
	}

	@Override
	public String toString() {
		/*
		 * We're going to assume a pre-order traversal by default
		 */

		String stringRepresentation = "";

		if (root != null) {
			stringRepresentation = build(Order.PRE_ORDER).toString();

		}

		return stringRepresentation;
	}

	public String toStringWithDepth() {
		/*
		 * We're going to assume a pre-order traversal by default
		 */

		if (root != null) {
			final Map<GamaTreeNode<T>, Integer> map = buildWithDepth(Order.PRE_ORDER);
			final StringBuilder sb = new StringBuilder();
			for (final GamaTreeNode<T> t : map.keySet()) {
				for (int i = 0; i < map.get(t); i++) {
					sb.append(Strings.TAB);
				}
				sb.append(t.getData().toString());
				sb.append(Strings.LN);
			}
			return sb.toString();
		}
		return "";
	}

	public void dispose() {
		if (root != null) {
			root.dispose();
			root = null;
		}
	}

}
