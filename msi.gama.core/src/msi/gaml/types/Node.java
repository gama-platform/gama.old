/*
 * Copyright 2010 Visin Suresh Paliath
 * Distributed under the BSD license
 */

package msi.gaml.types;

import java.util.*;
import java.util.regex.*;

public class Node<T> {

	private T data;
	private List<Node<T>> children;
	private Node<T> parent;

	public Node() {
		super();
		children = new ArrayList<Node<T>>();
	}

	public Node(final T data) {
		this();
		setData(data);
	}

	public Node<T> getParent() {
		return this.parent;
	}

	public List<Node<T>> getChildren() {
		return this.children;
	}

	public int getNumberOfChildren() {
		return getChildren().size();
	}

	public boolean hasChildren() {
		return getNumberOfChildren() > 0;
	}

	public void setChildren(final List<Node<T>> children) {
		for ( Node<T> child : children ) {
			child.parent = this;
		}

		this.children = children;
	}

	public void addChild(final Node<T> child) {
		child.parent = this;
		children.add(child);
	}

	public void addChildAt(final int index, final Node<T> child)
		throws IndexOutOfBoundsException {
		child.parent = this;
		children.add(index, child);
	}

	public void removeChildren() {
		this.children = new ArrayList<Node<T>>();
	}

	public void removeChildAt(final int index) throws IndexOutOfBoundsException {
		children.remove(index);
	}

	public Node<T> getChildAt(final int index) throws IndexOutOfBoundsException {
		return children.get(index);
	}

	public T getData() {
		return this.data;
	}

	public void setData(final T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return getData().toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if ( this == obj ) { return true; }
		if ( obj == null ) { return false; }
		if ( getClass() != obj.getClass() ) { return false; }
		Node<?> other = (Node<?>) obj;
		if ( data == null ) {
			if ( other.data != null ) { return false; }
		} else if ( !data.equals(other.data) ) { return false; }
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (data == null ? 0 : data.hashCode());
		return result;
	}

	public String toStringVerbose() {
		String stringRepresentation = getData().toString() + ":[";

		for ( Node<T> node : getChildren() ) {
			stringRepresentation += node.getData().toString() + ", ";
		}

		// Pattern.DOTALL causes ^ and $ to match. Otherwise it won't. It's retarded.
		Pattern pattern = Pattern.compile(", $", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(stringRepresentation);

		stringRepresentation = matcher.replaceFirst("");
		stringRepresentation += "]";

		return stringRepresentation;
	}
}
