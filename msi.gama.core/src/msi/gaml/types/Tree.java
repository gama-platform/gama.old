package msi.gaml.types;

/*
 * Copyright 2010 Vivin Suresh Paliath
 * Distributed under the BSD License
 */

import java.util.*;

public class Tree<T> {

	public static enum Order {
		PRE_ORDER, POST_ORDER
	}

	private Node<T> root;

	public Tree() {
		super();
	}

	public Node<T> getRoot() {
		return this.root;
	}

	public void setRoot(final Node<T> root) {
		this.root = root;
	}

	public int getNumberOfNodes() {
		int numberOfNodes = 0;

		if ( root != null ) {
			numberOfNodes = auxiliaryGetNumberOfNodes(root) + 1; // 1 for the root!
		}

		return numberOfNodes;
	}

	private int auxiliaryGetNumberOfNodes(final Node<T> node) {
		int numberOfNodes = node.getNumberOfChildren();

		for ( Node<T> child : node.getChildren() ) {
			numberOfNodes += auxiliaryGetNumberOfNodes(child);
		}

		return numberOfNodes;
	}

	public boolean exists(final T dataToFind) {
		return find(dataToFind) != null;
	}

	public Node<T> find(final T dataToFind) {
		Node<T> returnNode = null;

		if ( root != null ) {
			returnNode = auxiliaryFind(root, dataToFind);
		}

		return returnNode;
	}

	private Node<T> auxiliaryFind(final Node<T> currentNode,
		final T dataToFind) {
		Node<T> returnNode = null;
		int i = 0;

		if ( currentNode.getData().equals(dataToFind) ) {
			returnNode = currentNode;
		}

		else if ( currentNode.hasChildren() ) {
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

	public List<Node<T>> build(final Order traversalOrder) {
		List<Node<T>> returnList = null;

		if ( root != null ) {
			returnList = build(root, traversalOrder);
		}

		return returnList;
	}

	public List<Node<T>> build(final Node<T> node,
		final Order traversalOrder) {
		List<Node<T>> traversalResult = new ArrayList<Node<T>>();

		if ( traversalOrder == Order.PRE_ORDER ) {
			buildPreOrder(node, traversalResult);
		}

		else if ( traversalOrder == Order.POST_ORDER ) {
			buildPostOrder(node, traversalResult);
		}

		return traversalResult;
	}

	private void buildPreOrder(final Node<T> node,
		final List<Node<T>> traversalResult) {
		traversalResult.add(node);

		for ( Node<T> child : node.getChildren() ) {
			buildPreOrder(child, traversalResult);
		}
	}

	private void buildPostOrder(final Node<T> node,
		final List<Node<T>> traversalResult) {
		for ( Node<T> child : node.getChildren() ) {
			buildPostOrder(child, traversalResult);
		}

		traversalResult.add(node);
	}

	public Map<Node<T>, Integer> buildWithDepth(
		final Order traversalOrder) {
		Map<Node<T>, Integer> returnMap = null;

		if ( root != null ) {
			returnMap = buildWithDepth(root, traversalOrder);
		}

		return returnMap;
	}

	public Map<Node<T>, Integer> buildWithDepth(final Node<T> node,
		final Order traversalOrder) {
		Map<Node<T>, Integer> traversalResult =
			new LinkedHashMap<Node<T>, Integer>();

		if ( traversalOrder == Order.PRE_ORDER ) {
			buildPreOrderWithDepth(node, traversalResult, 0);
		}

		else if ( traversalOrder == Order.POST_ORDER ) {
			buildPostOrderWithDepth(node, traversalResult, 0);
		}

		return traversalResult;
	}

	private void buildPreOrderWithDepth(final Node<T> node,
		final Map<Node<T>, Integer> traversalResult, final int depth) {
		traversalResult.put(node, depth);

		for ( Node<T> child : node.getChildren() ) {
			buildPreOrderWithDepth(child, traversalResult, depth + 1);
		}
	}

	private void buildPostOrderWithDepth(final Node<T> node,
		final Map<Node<T>, Integer> traversalResult, final int depth) {
		for ( Node<T> child : node.getChildren() ) {
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

		if ( root != null ) {
			stringRepresentation = build(Order.PRE_ORDER).toString();

		}

		return stringRepresentation;
	}

	public String toStringWithDepth() {
		/*
		 * We're going to assume a pre-order traversal by default
		 */

		String stringRepresentation = "";

		if ( root != null ) {
			stringRepresentation =
				buildWithDepth(Order.PRE_ORDER).toString();
		}

		return stringRepresentation;
	}
}
