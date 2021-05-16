/*******************************************************************************************************
 *
 * msi.gama.util.ICollector.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util;

import java.io.Closeable;
import java.util.Collection;

import msi.gama.common.util.RandomUtils;

public interface ICollector<E> extends Collection<E>, Closeable {

	Collection<E> items();

	@Override
	default void close() {
		Collector.release(this);
	}

	default void shuffleInPlaceWith(final RandomUtils random) {
		random.shuffleInPlace(items());
	}

	void set(final ICollector<?> c);

}