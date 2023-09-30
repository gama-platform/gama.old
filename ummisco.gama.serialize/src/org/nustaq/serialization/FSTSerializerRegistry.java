/*******************************************************************************************************
 *
 * FSTSerializerRegistry.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA. User: ruedi Date: 10.11.12 Time: 15:04
 *
 * contains a map from class => serializer. One can register Serializers for exact classes or a class and all its
 * subclasses (can have unexpected consequences in case a subclass holds additional state).
 *
 */
public class FSTSerializerRegistry {

	/** The delegate. */
	private FSTSerializerRegistryDelegate delegate;

	/** The null. */
	public static FSTObjectSerializer NULL = new NULLSerializer();

	/**
	 * Sets the delegate.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param delegate
	 *            the new delegate
	 * @date 30 sept. 2023
	 */
	public void setDelegate(final FSTSerializerRegistryDelegate delegate) { this.delegate = delegate; }

	/**
	 * Gets the delegate.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the delegate
	 * @date 30 sept. 2023
	 */
	public FSTSerializerRegistryDelegate getDelegate() { return delegate; }

	/**
	 * The Class NULLSerializer.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	static class NULLSerializer implements FSTObjectSerializer {

		@Override
		public void writeObject(final FSTObjectOutput out, final Object toWrite, final FSTClazzInfo clzInfo,
				final FSTClazzInfo.FSTFieldInfo referencedBy, final int streamPosition) {}

		@Override
		public void readObject(final FSTObjectInput out, final Object toWrite, final FSTClazzInfo clzInfo,
				final FSTClazzInfo.FSTFieldInfo referencedBy) throws Exception {}

		@Override
		public boolean willHandleClass(final Class cl) {
			return true;
		}

		/**
		 * @return true if FST can skip a search for same instances in the serialized ObjectGraph. This speeds up
		 *         reading and writing and makes sense for short immutable such as Integer, Short, Character, Date, .. .
		 *         For those classes it is more expensive (CPU, size) to do a lookup than to just write the Object twice
		 *         in case.
		 */
		@Override
		public boolean alwaysCopy() {
			return false;
		}

		@Override
		public Object instantiate(final Class objectClass, final FSTObjectInput fstObjectInput,
				final FSTClazzInfo serializationInfo, final FSTClazzInfo.FSTFieldInfo referencee,
				final int streamPosition) throws Exception {
			return null;
		}
	}

	/**
	 * The Class SerEntry.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	final static class SerEntry {

		/** The for sub classes. */
		boolean forSubClasses = false;

		/** The ser. */
		FSTObjectSerializer ser;

		/**
		 * Instantiates a new ser entry.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param forSubClasses
		 *            the for sub classes
		 * @param ser
		 *            the ser
		 * @date 30 sept. 2023
		 */
		SerEntry(final boolean forSubClasses, final FSTObjectSerializer ser) {
			this.forSubClasses = forSubClasses;
			this.ser = ser;
		}
	}

	/** The map. */
	HashMap<Class, SerEntry> map = new HashMap<>(97);

	/**
	 * Gets the serializer.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param cl
	 *            the cl
	 * @return the serializer
	 * @date 30 sept. 2023
	 */
	public final FSTObjectSerializer getSerializer(final Class cl) {
		if (cl.isPrimitive()) return null;
		if (delegate != null) {
			FSTObjectSerializer ser = delegate.getSerializer(cl);
			if (ser != null) return ser;
		}
		final Class[] lineage = FSTClazzLineageInfo.getLineage(cl);
		for (final Class ascendant : lineage) {
			final FSTObjectSerializer serializer = getSerializer(ascendant, cl);
			if (serializer != null) return serializer;
		}
		return null;
	}

	/**
	 * Gets the serializer.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param cl
	 *            the cl
	 * @param lookupStart
	 *            the lookup start
	 * @return the serializer
	 * @date 30 sept. 2023
	 */
	final FSTObjectSerializer getSerializer(final Class cl, final Class lookupStart) {
		if (cl == null) return null;
		final SerEntry serEntry = map.get(cl);
		if (serEntry != null) {
			if (cl == lookupStart && serEntry.ser.willHandleClass(cl)) return serEntry.ser;
			if (serEntry.forSubClasses && serEntry.ser.willHandleClass(cl)) {
				putSerializer(lookupStart, serEntry.ser, false);
				return serEntry.ser;
			}
		}
		return null;
	}

	/**
	 * Put serializer.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param cl
	 *            the cl
	 * @param ser
	 *            the ser
	 * @param includeSubclasses
	 *            the include subclasses
	 * @date 30 sept. 2023
	 */
	public void putSerializer(final Class cl, final FSTObjectSerializer ser, final boolean includeSubclasses) {
		map.put(cl, new SerEntry(includeSubclasses, ser));
	}
}