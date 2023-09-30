/*******************************************************************************************************
 *
 * FSTObjectOutputNoShared.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Subclass optimized for "unshared mode". Cycles and Objects referenced more than once will not be detected.
 * Additionally JDK compatibility is not supported (read/writeObject and stuff). Use case is highperformance
 * serialization of plain cycle free data (e.g. messaging). Can perform significantly faster (20-40%).
 */
// FIXME: needs adaption to 2.0
public class FSTObjectOutputNoShared extends FSTObjectOutput {

	/**
	 * Creates a new FSTObjectOutputNoShared stream to write data to the specified underlying output stream. The counter
	 * <code>written</code> is set to zero. Don't create a FSTConfiguration with each stream, just create one global
	 * static configuration and reuse it. FSTConfiguration is threadsafe.
	 *
	 * @param out
	 *            the underlying output stream, to be saved for later use.
	 * @param conf
	 */
	public FSTObjectOutputNoShared(final OutputStream out, final FSTConfiguration conf) {
		super(out, conf);
		conf.setShareReferences(false);
		objects.disabled = true;
	}

	/**
	 * serialize without an underlying stream, the resulting byte array of writing to this FSTObjectOutput can be
	 * accessed using getBuffer(), the size using getWritten().
	 * <p/>
	 * Don't create a FSTConfiguration with each stream, just create one global static configuration and reuseit.
	 * FSTConfiguration is threadsafe.
	 *
	 * @param conf
	 */
	public FSTObjectOutputNoShared(final FSTConfiguration conf) {
		super(conf);
		conf.setShareReferences(false);
		objects.disabled = true;
	}

	@Override
	protected FSTClazzInfo writeObjectWithContext(final FSTClazzInfo.FSTFieldInfo referencee, final Object toWrite)
			throws IOException {
		int startPosition = getCodec().getWritten();
		boolean dontShare = true;
		objectWillBeWritten(toWrite, startPosition);

		try {
			if (toWrite == null) {
				getCodec().writeTag(NULL, null, 0, toWrite, this);
				return null;
			}
			final Class clazz = toWrite.getClass();
			if (clazz == String.class) {
				String[] oneOf = referencee.getOneOf();
				if (oneOf != null) {
					for (int i = 0; i < oneOf.length; i++) {
						String s = oneOf[i];
						if (s.equals(toWrite)) {
							getCodec().writeTag(ONE_OF, oneOf, i, toWrite, this);
							getCodec().writeFByte(i);
							return null;
						}
					}
				}
				if (dontShare) {
					getCodec().writeTag(STRING, toWrite, 0, toWrite, this);
					getCodec().writeStringUTF((String) toWrite);
					return null;
				}
			} else if (clazz == Integer.class) {
				getCodec().writeTag(BIG_INT, null, 0, toWrite, this);
				getCodec().writeFInt(((Integer) toWrite).intValue());
				return null;
			} else if (clazz == Long.class) {
				getCodec().writeTag(BIG_LONG, null, 0, toWrite, this);
				getCodec().writeFLong(((Long) toWrite).longValue());
				return null;
			} else if (clazz == Boolean.class) {
				getCodec().writeTag(((Boolean) toWrite).booleanValue() ? BIG_BOOLEAN_TRUE : BIG_BOOLEAN_FALSE, null, 0,
						toWrite, this);
				return null;
			} else if (referencee.getType() != null && referencee.getType().isEnum() || toWrite instanceof Enum) {
				if (!getCodec().writeTag(ENUM, toWrite, 0, toWrite, this)) {
					boolean isEnumClass = toWrite.getClass().isEnum();
					if (!isEnumClass) {
						// weird stuff ..
						Class c = toWrite.getClass();
						while (c != null && !c.isEnum()) { c = toWrite.getClass().getSuperclass(); }
						if (c == null) throw new RuntimeException("Can't handle this enum: " + toWrite.getClass());
						getCodec().writeClass(c);
					} else {
						getCodec().writeClass(getFstClazzInfo(referencee, toWrite.getClass()));
					}
					getCodec().writeFInt(((Enum) toWrite).ordinal());
				}
				return null;
			}

			FSTClazzInfo serializationInfo = getFstClazzInfo(referencee, clazz);
			// check for identical / equal objects
			FSTObjectSerializer ser = serializationInfo.getSer();
			if (clazz.isArray()) {
				if (getCodec().writeTag(ARRAY, toWrite, 0, toWrite, this)) return null; // some codecs handle primitive
																						// arrays like an primitive type
				writeArray(referencee, toWrite);
			} else if (ser == null) {
				// default write object wihtout custom serializer
				// handle write replace
				if (!writeObjectHeader(serializationInfo, referencee, toWrite)) { // skip in case codec can write object
																					// as primitive
					defaultWriteObject(toWrite, serializationInfo);
					if (serializationInfo.isExternalizable()) { getCodec().externalEnd(serializationInfo); }
				}
			} else { // object has custom serializer
				// Object header (nothing written till here)
				int pos = getCodec().getWritten();
				if (!writeObjectHeader(serializationInfo, referencee, toWrite)) { // skip in case code can write object
																					// as primitive
					// write object depending on type (custom, externalizable, serializable/java, default)
					ser.writeObject(this, toWrite, serializationInfo, referencee, pos);
					getCodec().externalEnd(serializationInfo);
				}
			}
			return serializationInfo;
		} finally {
			objectHasBeenWritten(toWrite, startPosition, getCodec().getWritten());
		}
	}

	@Override
	public void resetForReUse(final OutputStream out) {
		if (closed) throw new RuntimeException("Can't reuse closed stream");
		getCodec().reset(null);
		if (out != null) { getCodec().setOutstream(out); }
	}

	@Override
	public void resetForReUse(final byte[] out) {
		if (closed) throw new RuntimeException("Can't reuse closed stream");
		getCodec().reset(out);
	}

}
