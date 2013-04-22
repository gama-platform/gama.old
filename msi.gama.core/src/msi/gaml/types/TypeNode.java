/*
 * Copyright 2010 Visin Suresh Paliath
 * Distributed under the BSD license
 */

package msi.gaml.types;

import java.util.*;
import java.util.regex.*;

public class TypeNode<T> {

	private T data;
	private List<TypeNode<T>> children;
	private TypeNode<T> parent;

	public TypeNode() {
		super();
		children = new ArrayList<TypeNode<T>>();
	}

	public TypeNode(final T data) {
		this();
		setData(data);
	}

	public TypeNode<T> getParent() {
		return this.parent;
	}

	public List<TypeNode<T>> getChildren() {
		return this.children;
	}

	public int getNumberOfChildren() {
		return getChildren().size();
	}

	public boolean hasChildren() {
		return getNumberOfChildren() > 0;
	}

	public void setChildren(final List<TypeNode<T>> children) {
		for ( TypeNode<T> child : children ) {
			child.parent = this;
		}

		this.children = children;
	}

	public void addChildren(final Collection<T> children) {
		for ( T child : children ) {
			addChild(child);
		}
	}

	public void addChild(final TypeNode<T> child) {
		child.parent = this;
		children.add(child);
	}

	public TypeNode<T> addChild(final T child) {
		TypeNode<T> result = new TypeNode(child);
		addChild(result);
		return result;
	}

	public void addChildAt(final int index, final TypeNode<T> child) throws IndexOutOfBoundsException {
		child.parent = this;
		children.add(index, child);
	}

	public void removeChildren() {
		this.children = new ArrayList<TypeNode<T>>();
	}

	public void removeChildAt(final int index) throws IndexOutOfBoundsException {
		children.remove(index);
	}

	public TypeNode<T> getChildAt(final int index) throws IndexOutOfBoundsException {
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
		TypeNode<?> other = (TypeNode<?>) obj;
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

		for ( TypeNode<T> node : getChildren() ) {
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
