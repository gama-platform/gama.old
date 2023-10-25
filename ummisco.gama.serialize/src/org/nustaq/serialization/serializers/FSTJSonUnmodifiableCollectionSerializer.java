/*******************************************************************************************************
 *
 * FSTJSonUnmodifiableCollectionSerializer.java, in ummisco.gama.serialize, is part of the source code of the GAMA
 * modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.serializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.util.FSTUtil;

/**
 * For JSON only, see {@link <a href="https://github.com/RuedigerMoeller/fast-serialization/issues/114">Unable to
 * deserialize unmodifiable collections from JSON</a>}.
 *
 * @author Jakub Kubrynski
 */
@SuppressWarnings ("unchecked")
public class FSTJSonUnmodifiableCollectionSerializer extends FSTCollectionSerializer {

	/** The Constant UNMODIFIABLE_LIST_CLASS. */
	public static final Class<?> UNMODIFIABLE_COLLECTION_CLASS, UNMODIFIABLE_RANDOM_ACCESS_LIST_CLASS,
			UNMODIFIABLE_SET_CLASS,
			// UNMODIFIABLE_SORTED_SET_CLASS,
			// UNMODIFIABLE_NAVIGABLE_SET_CLASS,
			UNMODIFIABLE_LIST_CLASS;

	static {
		UNMODIFIABLE_LIST_CLASS = Collections.unmodifiableList(new LinkedList()).getClass();
		UNMODIFIABLE_RANDOM_ACCESS_LIST_CLASS = Collections.unmodifiableList(new ArrayList()).getClass();
		UNMODIFIABLE_SET_CLASS = Collections.unmodifiableSet(Collections.emptySet()).getClass();
		// 1.8 only
		// UNMODIFIABLE_SORTED_SET_CLASS = Collections.unmodifiableSortedSet(Collections.emptySortedSet()).getClass();
		// UNMODIFIABLE_NAVIGABLE_SET_CLASS =
		// Collections.unmodifiableNavigableSet(Collections.emptyNavigableSet()).getClass();
		UNMODIFIABLE_COLLECTION_CLASS = Collections.unmodifiableCollection(new ArrayList()).getClass();
	}

	@Override
	public void writeObject(final FSTObjectOutput out, final Object toWrite, final FSTClazzInfo clzInfo,
			final FSTClazzInfo.FSTFieldInfo referencedBy, final int streamPosition) throws IOException {
		out.writeObject(clzInfo.getClazz());
		Collection coll = (Collection) toWrite;
		out.writeInt(coll.size());
		for (Iterator iterator = coll.iterator(); iterator.hasNext();) { out.writeObject(iterator.next()); }
	}

	@Override
	public Object instantiate(final Class objectClass, final FSTObjectInput in, final FSTClazzInfo serializationInfo,
			final FSTClazzInfo.FSTFieldInfo referencee, final int streamPosition) throws Exception {
		Class clazz = (Class) in.readObject();
		int len = in.readInt();

		try {
			if (UNMODIFIABLE_RANDOM_ACCESS_LIST_CLASS == clazz) {
				List res = new ArrayList(len);
				fillArray(in, serializationInfo, referencee, streamPosition, res, len);
				return Collections.unmodifiableList(res);
			}
			if (UNMODIFIABLE_LIST_CLASS == clazz) {
				List res = new LinkedList();
				fillArray(in, serializationInfo, referencee, streamPosition, res, len);
				return Collections.unmodifiableList(res);
			}
			if (UNMODIFIABLE_SET_CLASS == clazz) {
				Set res = new HashSet(len);
				fillArray(in, serializationInfo, referencee, streamPosition, res, len);
				return Collections.unmodifiableSet(res);
			}
			// 1.8 only
			// if ( UNMODIFIABLE_SORTED_SET_CLASS == clazz ) {
			// Set res = new TreeSet();
			// fillArray(in, serializationInfo, referencee, streamPosition, res, len);
			// return Collections.unmodifiableSet(res);
			// }
			// if (UNMODIFIABLE_NAVIGABLE_SET_CLASS == clazz) {
			// Set res = new TreeSet();
			// fillArray(in, serializationInfo, referencee, streamPosition, res, len);
			// return Collections.unmodifiableSet(res);
			// }
			if (UNMODIFIABLE_COLLECTION_CLASS == clazz) {
				Collection res = new ArrayList<>(len);
				fillArray(in, serializationInfo, referencee, streamPosition, res, len);
				return Collections.unmodifiableCollection(res);
			}
			throw new RuntimeException("unexpected class tag " + clazz);
		} catch (Throwable th) {
			FSTUtil.<RuntimeException> rethrow(th);
		}
		return null;
	}

	/**
	 * Fill array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param in
	 *            the in
	 * @param serializationInfo
	 *            the serialization info
	 * @param referencee
	 *            the referencee
	 * @param streamPosition
	 *            the stream position
	 * @param res
	 *            the res
	 * @param len
	 *            the len
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @date 30 sept. 2023
	 */
	@SuppressWarnings ("unchecked")
	private void fillArray(final FSTObjectInput in, final FSTClazzInfo serializationInfo,
			final FSTClazzInfo.FSTFieldInfo referencee, final int streamPosition, final Object res, final int len)
			throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException {
		in.registerObject(res, streamPosition, serializationInfo, referencee);
		Collection col = (Collection) res;
		if (col instanceof ArrayList) { ((ArrayList) col).ensureCapacity(len); }
		for (int i = 0; i < len; i++) {
			final Object o = in.readObject();
			col.add(o);
		}
	}
}
