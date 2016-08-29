/*********************************************************************************************
 * 
 *
 * 'TypeNode.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/

package msi.gaml.types;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeNode<T> {

	private T data;
	private final List<TypeNode<T>> children;
	private final List<TypeNode<T>> parents;

	public TypeNode() {
		super();
		children = new ArrayList<TypeNode<T>>();
		parents = new ArrayList<TypeNode<T>>();
	}

	public TypeNode(final T data) {
		this();
		setData(data);
	}

	public TypeNode<T> getParent() {
		return this.parents.isEmpty() ? null : this.parents.get(0);
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

	// public void addChildren(final Collection<T> children) {
	// for (final T child : children) {
	// addChild(child);
	// }
	// }

	public void addChildWithUniqueParent(final TypeNode<T> child) {
		child.parents.clear();
		child.parents.add(this);
		children.add(child);
	}

	public void addChild(final TypeNode<T> child) {
		if (!child.parents.contains(this))
			child.parents.add(this);
		children.add(child);
	}

	public TypeNode<T> addChildWithUniqueParent(final T child) {
		final TypeNode<T> result = new TypeNode(child);
		addChildWithUniqueParent(result);
		return result;
	}

	public TypeNode<T> addChild(final T child) {
		final TypeNode<T> result = new TypeNode(child);
		addChild(result);
		return result;
	}

	// public void addChildAt(final int index, final TypeNode<T> child) throws
	// IndexOutOfBoundsException {
	// child.parent = this;
	// children.add(index, child);
	// }

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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TypeNode<?> other = (TypeNode<?>) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
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

		for (final TypeNode<T> node : getChildren()) {
			stringRepresentation += node.getData().toString() + ", ";
		}

		// Pattern.DOTALL causes ^ and $ to match. Otherwise it won't. It's
		// retarded.
		final Pattern pattern = Pattern.compile(", $", Pattern.DOTALL);
		final Matcher matcher = pattern.matcher(stringRepresentation);

		stringRepresentation = matcher.replaceFirst("");
		stringRepresentation += "]";

		return stringRepresentation;
	}

	public void dispose() {
		parents.clear();
		for (final TypeNode<T> node : children) {
			node.dispose();
		}
		children.clear();
	}

	public TypeNode<T> copy() {
		final TypeNode<T> result = new TypeNode(getData());
		for (final TypeNode<T> node : children) {
			result.addChild(node.copy());
		}
		return result;
	}
}
