package msi.gama.util;

import java.util.Collection;

public interface ICollector<E> extends Iterable<E> {

	void add(E vd);

	Collection<E> items();

	void remove(E e);

	boolean isEmpty();

	void clear();

}