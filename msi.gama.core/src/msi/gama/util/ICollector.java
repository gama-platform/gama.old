package msi.gama.util;

import java.util.Collection;

public interface ICollector<E> extends Collection<E> {

	Collection<E> items();

}