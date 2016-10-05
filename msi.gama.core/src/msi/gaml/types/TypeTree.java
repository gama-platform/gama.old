/*********************************************************************************************
 *
 *
 * 'TypeTree.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.types;

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
public class TypeTree<T> {

	public static enum Order {
		PRE_ORDER, POST_ORDER
	}

	private TypeNode<T> root;

	public TypeTree() {
		super();
	}

	public TypeTree(final TypeNode<T> root) {
		setRoot(root);
	}

	public TypeTree(final T root) {
		setRoot(new TypeNode(root));
	}

	public TypeNode<T> getRoot() {
		return this.root;
	}

	public void setRoot(final TypeNode<T> root) {
		this.root = root;
	}

	public TypeNode<T> setRoot(final T root) {
		final TypeNode<T> result = new TypeNode(root);
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

	private int auxiliaryGetNumberOfNodes(final TypeNode<T> node) {
		int numberOfNodes = node.getNumberOfChildren();

		for (final TypeNode<T> child : node.getChildren()) {
			numberOfNodes += auxiliaryGetNumberOfNodes(child);
		}

		return numberOfNodes;
	}

	public boolean exists(final T dataToFind) {
		return find(dataToFind) != null;
	}

	public TypeNode<T> find(final T dataToFind) {
		TypeNode<T> returnNode = null;

		if (root != null) {
			returnNode = auxiliaryFind(root, dataToFind);
		}

		return returnNode;
	}

	private TypeNode<T> auxiliaryFind(final TypeNode<T> currentNode, final T dataToFind) {
		TypeNode<T> returnNode = null;
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

	public List<TypeNode<T>> build(final Order traversalOrder) {
		List<TypeNode<T>> returnList = null;

		if (root != null) {
			returnList = build(root, traversalOrder);
		}

		return returnList;
	}

	public List<TypeNode<T>> build(final TypeNode<T> node, final Order traversalOrder) {
		final List<TypeNode<T>> traversalResult = new ArrayList<TypeNode<T>>();

		if (traversalOrder == Order.PRE_ORDER) {
			buildPreOrder(node, traversalResult);
		}

		else if (traversalOrder == Order.POST_ORDER) {
			buildPostOrder(node, traversalResult);
		}

		return traversalResult;
	}

	public List<T> getAllElements(final TypeNode<T> node, final Order traversalOrder) {

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

	public TypeTree<T> copy() {
		final TypeTree<T> result = new TypeTree();
		result.setRoot(getRoot().copy());
		return result;
	}

	public List<T> getAllElements(final Order order) {
		return getAllElements(root, order);
	}

	public List<T> getAllElements(final T data, final Order traversalOrder) {
		final List<T> traversalResult = new ArrayList<T>();
		final TypeNode<T> node = find(data);
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

	private void buildPreOrder(final TypeNode<T> node, final List<TypeNode<T>> traversalResult) {
		traversalResult.add(node);

		for (final TypeNode<T> child : node.getChildren()) {
			buildPreOrder(child, traversalResult);
		}
	}

	private void getAllPreOrder(final TypeNode<T> node, final List<T> traversalResult) {
		traversalResult.add(node.getData());

		for (final TypeNode<T> child : node.getChildren()) {
			getAllPreOrder(child, traversalResult);
		}
	}

	private void getAllPostOrder(final TypeNode<T> node, final List<T> traversalResult) {
		for (final TypeNode<T> child : node.getChildren()) {
			getAllPostOrder(child, traversalResult);
		}

		traversalResult.add(node.getData());
	}

	private void buildPostOrder(final TypeNode<T> node, final List<TypeNode<T>> traversalResult) {
		for (final TypeNode<T> child : node.getChildren()) {
			buildPostOrder(child, traversalResult);
		}

		traversalResult.add(node);
	}

	public Map<TypeNode<T>, Integer> buildWithDepth(final Order traversalOrder) {
		Map<TypeNode<T>, Integer> returnMap = null;

		if (root != null) {
			returnMap = buildWithDepth(root, traversalOrder);
		}

		return returnMap;
	}

	public Map<TypeNode<T>, Integer> buildWithDepth(final TypeNode<T> node, final Order traversalOrder) {
		final Map<TypeNode<T>, Integer> traversalResult = new TOrderedHashMap<TypeNode<T>, Integer>();

		if (traversalOrder == Order.PRE_ORDER) {
			buildPreOrderWithDepth(node, traversalResult, 0);
		}

		else if (traversalOrder == Order.POST_ORDER) {
			buildPostOrderWithDepth(node, traversalResult, 0);
		}

		return traversalResult;
	}

	private void buildPreOrderWithDepth(final TypeNode<T> node, final Map<TypeNode<T>, Integer> traversalResult,
			final int depth) {
		traversalResult.put(node, depth);

		for (final TypeNode<T> child : node.getChildren()) {
			buildPreOrderWithDepth(child, traversalResult, depth + 1);
		}
	}

	private void buildPostOrderWithDepth(final TypeNode<T> node, final Map<TypeNode<T>, Integer> traversalResult,
			final int depth) {
		for (final TypeNode<T> child : node.getChildren()) {
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
			final Map<TypeNode<T>, Integer> map = buildWithDepth(Order.PRE_ORDER);
			final StringBuilder sb = new StringBuilder();
			for (final TypeNode<T> t : map.keySet()) {
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
