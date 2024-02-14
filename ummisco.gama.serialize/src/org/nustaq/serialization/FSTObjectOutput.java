/*******************************************************************************************************
 *
 * FSTObjectOutput.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.nustaq.serialization.FSTClazzInfo.FSTFieldInfo;
import org.nustaq.serialization.util.FSTUtil;

import ummisco.gama.dev.utils.DEBUG;

/**
 * Created with IntelliJ IDEA. User: Möller Date: 03.11.12 Time: 12:26 To change this template use File | Settings |
 * File Templates.
 */

/**
 * replacement of ObjectOutputStream
 */
public class FSTObjectOutput implements ObjectOutput {

	/** The null placeholder. */
	public static Object NULL_PLACEHOLDER = new Object() {
		@Override
		public String toString() {
			return "NULL_PLACEHOLDER";
		}
	};

	/** The Constant SPECIAL_COMPATIBILITY_OBJECT_TAG. */
	public static final byte SPECIAL_COMPATIBILITY_OBJECT_TAG = -19; // see issue 52

	/** The Constant ONE_OF. */
	public static final byte ONE_OF = -18;

	/** The Constant BIG_BOOLEAN_FALSE. */
	public static final byte BIG_BOOLEAN_FALSE = -17;

	/** The Constant BIG_BOOLEAN_TRUE. */
	public static final byte BIG_BOOLEAN_TRUE = -16;

	/** The Constant BIG_LONG. */
	public static final byte BIG_LONG = -10;

	/** The Constant BIG_INT. */
	public static final byte BIG_INT = -9;

	/** The Constant DIRECT_ARRAY_OBJECT. */
	public static final byte DIRECT_ARRAY_OBJECT = -8;

	/** The Constant HANDLE. */
	public static final byte HANDLE = -7;

	/** The Constant ENUM. */
	public static final byte ENUM = -6;

	/** The Constant ARRAY. */
	public static final byte ARRAY = -5;

	/** The Constant STRING. */
	public static final byte STRING = -4;

	/** The Constant TYPED. */
	public static final byte TYPED = -3; // var class == object written class

	/** The Constant DIRECT_OBJECT. */
	public static final byte DIRECT_OBJECT = -2;

	/** The Constant NULL. */
	public static final byte NULL = -1;

	/** The Constant OBJECT. */
	public static final byte OBJECT = 0;

	/** The codec. */
	protected FSTEncoder codec;

	/** The conf. */
	protected FSTConfiguration conf; // immutable, should only be set by FSTConf mechanics

	/** The objects. */
	protected FSTObjectRegistry objects;

	/** The cur depth. */
	protected int curDepth = 0;

	/** The write external write ahead. */
	protected int writeExternalWriteAhead = 8000; // max size an external may occupy FIXME: document this, create
													// annotation to configure this

	/** The listener. */
	protected FSTSerialisationListener listener;

	/** The dont share. */
	// double state to reduce pointer chasing
	protected boolean dontShare;

	/** The string info. */
	protected final FSTClazzInfo stringInfo;

	/** The is cross platform. */
	protected boolean isCrossPlatform;

	/** The refs local. */
	protected ThreadLocal<FSTClazzInfo.FSTFieldInfo[]> refsLocal = new ThreadLocal<>() {
		@Override
		protected FSTClazzInfo.FSTFieldInfo[] initialValue() {
			return new FSTClazzInfo.FSTFieldInfo[20];
		}
	};

	/** The refs. */
	FSTClazzInfo.FSTFieldInfo[] refs;

	/**
	 * Creates a new FSTObjectOutput stream to write data to the specified underlying output stream. The counter
	 * <code>written</code> is set to zero. Don't create a FSTConfiguration with each stream, just create one global
	 * static configuration and reuse it. FSTConfiguration is threadsafe.
	 *
	 * @param out
	 *            the underlying output stream, to be saved for later use.
	 */
	public FSTObjectOutput(final OutputStream out, final FSTConfiguration conf) {
		this.conf = conf;
		setCodec(conf.createStreamEncoder());
		getCodec().setOutstream(out);
		isCrossPlatform = conf.isCrossPlatform();

		objects = (FSTObjectRegistry) conf.getCachedObject(FSTObjectRegistry.class);
		if (objects == null) {
			objects = new FSTObjectRegistry(conf);
			objects.disabled = !conf.isShareReferences();
		} else {
			objects.clearForWrite(conf);
		}
		dontShare = objects.disabled;
		stringInfo = getClassInfoRegistry().getCLInfo(String.class, conf);
	}

	/**
	 * serialize without an underlying stream, the resulting byte array of writing to this FSTObjectOutput can be
	 * accessed using getBuffer(), the size using getWritten().
	 *
	 * Don't create a FSTConfiguration with each stream, just create one global static configuration and reuse it.
	 * FSTConfiguration is threadsafe.
	 *
	 * @param conf
	 * @throws IOException
	 */
	public FSTObjectOutput(final FSTConfiguration conf) {
		this(null, conf);
		getCodec().setOutstream(null);
	}

	/**
	 * Flushes this data output stream. This forces any buffered output bytes to be written out to the stream.
	 * <p/>
	 * The <code>flush</code> method of <code>DataOutputStream</code> calls the <code>flush</code> method of its
	 * underlying output stream.
	 *
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 * @see java.io.FilterOutputStream#out
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		getCodec().flush();
		resetAndClearRefs();
	}

	/** The empty. */
	protected static ByteArrayOutputStream empty = new ByteArrayOutputStream(0);

	/** The closed. */
	protected boolean closed = false;

	@Override
	public void close() throws IOException {
		flush();
		closed = true;
		getCodec().close();
		resetAndClearRefs();
		conf.returnObject(objects);
	}

	/**
	 * since the stock writeXX methods on InputStream are final, i can't ensure sufficient bufferSize on the output
	 * buffer before calling writeExternal. Default value is 5000 bytes. If you make use of the externalizable interface
	 * and write larger Objects a) cast the ObjectOutput in readExternal to FSTObjectOutput and call ensureFree on this
	 * in your writeExternal method or b) statically set a sufficient maximum using this method.
	 */
	public int getWriteExternalWriteAhead() { return writeExternalWriteAhead; }

	/**
	 * since the stock writeXX methods on InputStream are final, i can't ensure sufficient bufferSize on the output
	 * buffer before calling writeExternal. Default value is 5000 bytes. If you make use of the externalizable interface
	 * and write larger Objects a) cast the ObjectOutput in readExternal to FSTObjectOutput and call ensureFree on this
	 * in your writeExternal method or b) statically set a sufficient maximum using this method.
	 *
	 * @param writeExternalWriteAhead
	 */
	public void setWriteExternalWriteAhead(final int writeExternalWriteAhead) {
		this.writeExternalWriteAhead = writeExternalWriteAhead;
	}

	/**
	 * Ensure free.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param bytes
	 *            the bytes
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	public void ensureFree(final int bytes) throws IOException {
		getCodec().ensureFree(bytes);
	}

	//////////////////////////////////////////////////////////////////////////
	//
	// ObjectOutput interface impl
	//
	@Override
	public void writeObject(final Object obj) throws IOException {
		writeObject(obj, (Class[]) null);
	}

	@Override
	public void write(final int b) throws IOException {
		getCodec().writeFByte(b);
	}

	@Override
	public void write(final byte[] b) throws IOException {
		getCodec().writePrimitiveArray(b, 0, b.length);
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		getCodec().writePrimitiveArray(b, off, len);
	}

	@Override
	public void writeBoolean(final boolean v) throws IOException {
		getCodec().writeFByte(v ? 1 : 0);
	}

	@Override
	public void writeByte(final int v) throws IOException {
		getCodec().writeFByte(v);
	}

	@Override
	public void writeShort(final int v) throws IOException {
		getCodec().writeFShort((short) v);
	}

	@Override
	public void writeChar(final int v) throws IOException {
		getCodec().writeFChar((char) v);
	}

	@Override
	public void writeInt(final int v) throws IOException {
		getCodec().writeFInt(v);
	}

	@Override
	public void writeLong(final long v) throws IOException {
		getCodec().writeFLong(v);
	}

	@Override
	public void writeFloat(final float v) throws IOException {
		getCodec().writeFFloat(v);
	}

	@Override
	public void writeDouble(final double v) throws IOException {
		getCodec().writeFDouble(v);
	}

	@Override
	public void writeBytes(final String s) throws IOException {
		byte[] bytes = s.getBytes();
		getCodec().writePrimitiveArray(bytes, 0, bytes.length);
	}

	@Override
	public void writeChars(final String s) throws IOException {
		char[] chars = s.toCharArray();
		getCodec().writePrimitiveArray(chars, 0, chars.length);
	}

	@Override
	public void writeUTF(final String s) throws IOException {
		getCodec().writeStringUTF(s);
	}

	//
	// .. end interface impl
	/////////////////////////////////////////////////////

	/**
	 * Write object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param obj
	 *            the obj
	 * @param possibles
	 *            the possibles
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	public void writeObject(final Object obj, final Class... possibles) throws IOException {
		if (isCrossPlatform) {
			writeObjectInternal(obj, null, (Class[]) null); // not supported cross platform
			return;
		}
		if (possibles != null && possibles.length > 1) {
			for (Class possible : possibles) { getCodec().registerClass(possible); }
		}
		writeObjectInternal(obj, null, possibles);
	}

	/**
	 * Gets the cached FI.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param possibles
	 *            the possibles
	 * @return the cached FI
	 * @date 29 sept. 2023
	 */
	// avoid creation of dummy ref
	protected FSTClazzInfo.FSTFieldInfo getCachedFI(final Class... possibles) {
		if (refs == null) { refs = refsLocal.get(); }
		if (curDepth >= refs.length) return new FSTClazzInfo.FSTFieldInfo(possibles, null, true);
		FSTClazzInfo.FSTFieldInfo inf = refs[curDepth];
		if (inf == null) {
			inf = new FSTClazzInfo.FSTFieldInfo(possibles, null, true);
			refs[curDepth] = inf;
			return inf;
		}
		inf.setPossibleClasses(possibles);
		return inf;
	}

	/**
	 *
	 * @param obj
	 * @param ci
	 * @param possibles
	 * @return last FSTClazzInfo if class is plain reusable (not replaceable, needs compatible mode)
	 * @throws IOException
	 */
	public FSTClazzInfo writeObjectInternal(final Object obj, final FSTClazzInfo ci, final Class... possibles)
			throws IOException {
		FSTClazzInfo.FSTFieldInfo info = getCachedFI(possibles);
		curDepth++;
		FSTClazzInfo fstClazzInfo = writeObjectWithContext(info, obj, ci);
		curDepth--;
		if (fstClazzInfo == null) return null;
		return fstClazzInfo.useCompatibleMode() ? null : fstClazzInfo;
	}

	/**
	 * Gets the listener.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the listener
	 * @date 29 sept. 2023
	 */
	public FSTSerialisationListener getListener() { return listener; }

	/**
	 * note this might slow down serialization significantly * @param listener
	 */
	public void setListener(final FSTSerialisationListener listener) { this.listener = listener; }

	/**
	 * hook for debugging profiling. register a FSTSerialisationListener to use
	 *
	 * @param obj
	 * @param streamPosition
	 */
	protected void objectWillBeWritten(final Object obj, final int streamPosition) {
		if (listener != null) { listener.objectWillBeWritten(obj, streamPosition); }
	}

	/**
	 * hook for debugging profiling. empty impl, you need to subclass to make use of this hook
	 *
	 * @param obj
	 * @param oldStreamPosition
	 * @param streamPosition
	 */
	protected void objectHasBeenWritten(final Object obj, final int oldStreamPosition, final int streamPosition) {
		if (listener != null) { listener.objectHasBeenWritten(obj, oldStreamPosition, streamPosition); }
	}

	/**
	 * Write object with context.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param toWrite
	 *            the to write
	 * @return the FST clazz info
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	protected FSTClazzInfo writeObjectWithContext(final FSTClazzInfo.FSTFieldInfo referencee, final Object toWrite)
			throws IOException {
		return writeObjectWithContext(referencee, toWrite, null);
	}

	/** The tmp. */
	protected int tmp[] = { 0 };

	/**
	 * Write object with context.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param toWrite
	 *            the to write
	 * @param ci
	 *            the ci
	 * @return the FST clazz info
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	// splitting this slows down ...
	protected FSTClazzInfo writeObjectWithContext(final FSTClazzInfo.FSTFieldInfo referencee, Object toWrite,
			final FSTClazzInfo ci) throws IOException {
		int startPosition = 0;
		try {
			if (toWrite == null) {
				getCodec().writeTag(NULL, null, 0, toWrite, this);
				return null;
			}
			startPosition = getCodec().getWritten();
			objectWillBeWritten(toWrite, startPosition);
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
				// shortpath
				if (!dontShare && writeHandleIfApplicable(toWrite, stringInfo)) return stringInfo;
				getCodec().writeTag(STRING, toWrite, 0, toWrite, this);
				getCodec().writeStringUTF((String) toWrite);
				return null;
			}
			if (clazz == Integer.class) {
				getCodec().writeTag(BIG_INT, null, 0, toWrite, this);
				getCodec().writeFInt(((Integer) toWrite).intValue());
				return null;
			}
			if (clazz == Long.class) {
				getCodec().writeTag(BIG_LONG, null, 0, toWrite, this);
				getCodec().writeFLong(((Long) toWrite).longValue());
				return null;
			}
			if (clazz == Boolean.class) {
				getCodec().writeTag(((Boolean) toWrite).booleanValue() ? BIG_BOOLEAN_TRUE : BIG_BOOLEAN_FALSE, null, 0,
						toWrite, this);
				return null;
			}
			if (referencee.getType() != null && referencee.getType().isEnum() || toWrite instanceof Enum)
				return writeEnum(referencee, toWrite);

			FSTClazzInfo serializationInfo = ci == null ? getFstClazzInfo(referencee, clazz) : ci;

			// check for identical / equal objects
			FSTObjectSerializer ser = serializationInfo.getSer();
			if (!dontShare && !referencee.isFlat() && !serializationInfo.isFlat()
					&& (ser == null || !ser.alwaysCopy())) {
				if (writeHandleIfApplicable(toWrite, serializationInfo)) return serializationInfo;
			}
			if (clazz.isArray()) {
				if (getCodec().writeTag(ARRAY, toWrite, 0, toWrite, this)) return serializationInfo; // some codecs
																										// handle
																										// primitive
																										// arrays like
																										// an primitive
																										// type
				writeArray(referencee, toWrite);
				getCodec().writeArrayEnd();
			} else if (ser == null) {
				// default write object wihtout custom serializer
				// handle write replace
				// if ( ! dontShare ) GIT ISSUE 80
				FSTClazzInfo originalInfo = serializationInfo;
				{
					if (serializationInfo.getWriteReplaceMethod() != null) {
						Object replaced = null;
						try {
							replaced = serializationInfo.getWriteReplaceMethod().invoke(toWrite);
						} catch (Exception e) {
							FSTUtil.<RuntimeException> rethrow(e);
						}
						if (replaced != null && replaced != toWrite) {
							toWrite = replaced;
							serializationInfo = getClassInfoRegistry().getCLInfo(toWrite.getClass(), conf);
							// fixme: update object map ?
						}
					}
					// clazz uses some JDK special stuff (frequently slow)
					if (serializationInfo.useCompatibleMode() && !serializationInfo.isExternalizable()) {
						writeObjectCompatible(referencee, toWrite, serializationInfo);
						return originalInfo;
					}
				}

				if (!writeObjectHeader(serializationInfo, referencee, toWrite)) { // skip in case codec can write object
																					// as primitive
					ser = serializationInfo.getSer();
					if (ser == null) {
						defaultWriteObject(toWrite, serializationInfo);
						if (serializationInfo.isExternalizable()) { getCodec().externalEnd(serializationInfo); }
					} else {
						// handle edge case: there is a serializer registered for replaced class
						// copied from below :(
						int pos = getCodec().getWritten();
						// write object depending on type (custom, externalizable, serializable/java, default)
						ser.writeObject(this, toWrite, serializationInfo, referencee, pos);
						getCodec().externalEnd(serializationInfo);
					}
				}
				return originalInfo;
			} else // Object header (nothing written till here)
			if (!writeObjectHeader(serializationInfo, referencee, toWrite)) { // skip in case code can write object as
																				// primitive
				int pos = getCodec().getWritten();
				// write object depending on type (custom, externalizable, serializable/java, default)
				ser.writeObject(this, toWrite, serializationInfo, referencee, pos);
				getCodec().externalEnd(serializationInfo);
			}
			return serializationInfo;
		} finally {
			objectHasBeenWritten(toWrite, startPosition, getCodec().getWritten());
		}
	}

	/**
	 * Write enum.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param toWrite
	 *            the to write
	 * @return the FST clazz info
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	protected FSTClazzInfo writeEnum(final FSTClazzInfo.FSTFieldInfo referencee, final Object toWrite)
			throws IOException {
		if (!getCodec().writeTag(ENUM, toWrite, 0, toWrite, this)) {
			boolean isEnumClass = toWrite.getClass().isEnum();
			if (isEnumClass) {
				FSTClazzInfo fstClazzInfo = getFstClazzInfo(referencee, toWrite.getClass());
				getCodec().writeClass(fstClazzInfo);
				getCodec().writeFInt(((Enum) toWrite).ordinal());
				return fstClazzInfo;
			}
			// anonymous enum subclass
			Class c = toWrite.getClass();
			c = toWrite.getClass().getSuperclass();
			if (c == null) throw new RuntimeException("Can't handle this enum: " + toWrite.getClass());
			getCodec().writeClass(c);
			getCodec().writeFInt(((Enum) toWrite).ordinal());
		}
		return null;
	}

	/**
	 * Write handle if applicable.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param toWrite
	 *            the to write
	 * @param serializationInfo
	 *            the serialization info
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	protected boolean writeHandleIfApplicable(final Object toWrite, final FSTClazzInfo serializationInfo)
			throws IOException {
		int writePos = getCodec().getWritten();
		int handle = objects.registerObjectForWrite(toWrite, writePos, serializationInfo, tmp);
		// determine class header
		if (handle >= 0) {
			final boolean isIdentical = tmp[0] == 0; // objects.getReadRegisteredObject(handle) == toWrite;
			if (isIdentical) {
				// System.out.println("POK writeHandle"+handle+" "+toWrite.getClass().getName());
				if (!getCodec().writeTag(HANDLE, null, handle, toWrite, this)) { getCodec().writeFInt(handle); }
				return true;
			}
		}
		return false;
	}

	/**
	 * if class is same as last referenced, returned cached clzinfo, else do a lookup
	 */
	protected FSTClazzInfo getFstClazzInfo(final FSTClazzInfo.FSTFieldInfo referencee, final Class clazz) {
		FSTClazzInfo serializationInfo = null;
		FSTClazzInfo lastInfo = referencee.lastInfo;
		if (lastInfo != null && lastInfo.getClazz() == clazz && lastInfo.conf == conf) {
			serializationInfo = lastInfo;
		} else {
			serializationInfo = getClassInfoRegistry().getCLInfo(clazz, conf);
			referencee.lastInfo = serializationInfo;
		}
		return serializationInfo;
	}

	/**
	 * Default write object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param toWrite
	 *            the to write
	 * @param serializationInfo
	 *            the serialization info
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	public void defaultWriteObject(final Object toWrite, final FSTClazzInfo serializationInfo) throws IOException {
		if (serializationInfo.isExternalizable()) {
			getCodec().ensureFree(writeExternalWriteAhead);
			((Externalizable) toWrite).writeExternal(this);
		} else {
			FSTClazzInfo.FSTFieldInfo[] fieldInfo = serializationInfo.getFieldInfo();
			writeObjectFields(toWrite, serializationInfo, fieldInfo, 0, 0);
		}
	}

	/**
	 * Write object compatible.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param toWrite
	 *            the to write
	 * @param serializationInfo
	 *            the serialization info
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	protected void writeObjectCompatible(final FSTClazzInfo.FSTFieldInfo referencee, final Object toWrite,
			final FSTClazzInfo serializationInfo) throws IOException {
		// Object header (nothing written till here)
		writeObjectHeader(serializationInfo, referencee, toWrite);
		Class cl = serializationInfo.getClazz();
		writeObjectCompatibleRecursive(referencee, toWrite, serializationInfo, cl);
	}

	/**
	 * Write object compatible recursive.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param toWrite
	 *            the to write
	 * @param serializationInfo
	 *            the serialization info
	 * @param cl
	 *            the cl
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	protected void writeObjectCompatibleRecursive(final FSTClazzInfo.FSTFieldInfo referencee, final Object toWrite,
			final FSTClazzInfo serializationInfo, final Class cl) throws IOException {
		FSTClazzInfo.FSTCompatibilityInfo fstCompatibilityInfo = serializationInfo.getCompInfo().get(cl);
		if (!Serializable.class.isAssignableFrom(cl)) return; // ok here, as compatible mode will never be triggered for
																// "forceSerializable"
		writeObjectCompatibleRecursive(referencee, toWrite, serializationInfo, cl.getSuperclass());
		if (fstCompatibilityInfo != null && fstCompatibilityInfo.getWriteMethod() != null) {
			try {
				writeByte(55); // tag this is written with writeMethod
				fstCompatibilityInfo.getWriteMethod().invoke(toWrite,
						getObjectOutputStream(cl, serializationInfo, referencee, toWrite));
			} catch (Exception e) {
				if (e instanceof InvocationTargetException
						&& ((InvocationTargetException) e).getTargetException() != null) {
					FSTUtil.<RuntimeException> rethrow(((InvocationTargetException) e).getTargetException());
				}
				FSTUtil.<RuntimeException> rethrow(e);
			}
		} else if (fstCompatibilityInfo != null) {
			writeByte(66); // tag this is written from here no writeMethod
			writeObjectFields(toWrite, serializationInfo, fstCompatibilityInfo.getFieldArray(), 0, 0);
		}
	}

	/**
	 * Write object fields.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param toWrite
	 *            the to write
	 * @param serializationInfo
	 *            the serialization info
	 * @param fieldInfo
	 *            the field info
	 * @param startIndex
	 *            the start index
	 * @param version
	 *            the version
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	protected void writeObjectFields(final Object toWrite, final FSTClazzInfo serializationInfo,
			final FSTClazzInfo.FSTFieldInfo[] fieldInfo, final int startIndex, final int version) throws IOException {
		try {
			int booleanMask = 0;
			int boolcount = 0;
			final int length = fieldInfo.length;
			int j = startIndex;
			if (!getCodec().isWritingAttributes()) { // pack bools into bits in case it's not a chatty codec
				for (;; j++) {
					if (j == length || fieldInfo[j].getVersion() != version) {
						if (boolcount > 0) { getCodec().writeFByte(booleanMask << 8 - boolcount); }
						break;
					}
					final FSTClazzInfo.FSTFieldInfo subInfo = fieldInfo[j];
					if (subInfo.getIntegralType() != FSTFieldInfo.BOOL) {
						if (boolcount > 0) { getCodec().writeFByte(booleanMask << 8 - boolcount); }
						break;
					}
					if (boolcount == 8) {
						getCodec().writeFByte(booleanMask << 8 - boolcount);
						boolcount = 0;
						booleanMask = 0;
					}
					boolean booleanValue = subInfo.getBooleanValue(toWrite);
					booleanMask = booleanMask << 1;
					booleanMask = booleanMask | (booleanValue ? 1 : 0);
					boolcount++;
				}
			}
			for (int i = j; i < length; i++) {
				final FSTClazzInfo.FSTFieldInfo subInfo = fieldInfo[i];
				if (subInfo.getVersion() != version) {
					getCodec().writeVersionTag(subInfo.getVersion());
					writeObjectFields(toWrite, serializationInfo, fieldInfo, i, subInfo.getVersion());
					return;
				}
				if (getCodec().writeAttributeName(subInfo, toWrite)) { continue; }
				if (subInfo.isPrimitive()) {
					// speed safe
					int integralType = subInfo.getIntegralType();
					switch (integralType) {
						case FSTClazzInfo.FSTFieldInfo.BOOL:
							getCodec().writeFByte(subInfo.getBooleanValue(toWrite) ? 1 : 0);
							break;
						case FSTClazzInfo.FSTFieldInfo.BYTE:
							getCodec().writeFByte(subInfo.getByteValue(toWrite));
							break;
						case FSTClazzInfo.FSTFieldInfo.CHAR:
							getCodec().writeFChar((char) subInfo.getCharValue(toWrite));
							break;
						case FSTClazzInfo.FSTFieldInfo.SHORT:
							getCodec().writeFShort((short) subInfo.getShortValue(toWrite));
							break;
						case FSTClazzInfo.FSTFieldInfo.INT:
							getCodec().writeFInt(subInfo.getIntValue(toWrite));
							break;
						case FSTClazzInfo.FSTFieldInfo.LONG:
							getCodec().writeFLong(subInfo.getLongValue(toWrite));
							break;
						case FSTClazzInfo.FSTFieldInfo.FLOAT:
							getCodec().writeFFloat(subInfo.getFloatValue(toWrite));
							break;
						case FSTClazzInfo.FSTFieldInfo.DOUBLE:
							getCodec().writeFDouble(subInfo.getDoubleValue(toWrite));
							break;
					}
				} else if (subInfo.isConditional()) {
					final int conditional = getCodec().getWritten();
					getCodec().skip(4);
					// object
					Object subObject = subInfo.getObjectValue(toWrite);
					if (subObject == null) {
						getCodec().writeTag(NULL, null, 0, toWrite, this);
					} else {
						writeObjectWithContext(subInfo, subObject);
					}
					int v = getCodec().getWritten();
					getCodec().writeInt32At(conditional, v);
				} else {
					// object
					Object subObject = subInfo.getObjectValue(toWrite);
					if (subObject == null) {
						getCodec().writeTag(NULL, null, 0, toWrite, this);
					} else {
						writeObjectWithContext(subInfo, subObject);
					}
				}
			}
			getCodec().writeVersionTag((byte) 0);
			getCodec().writeFieldsEnd(serializationInfo);
		} catch (IllegalAccessException ex) {
			FSTUtil.<RuntimeException> rethrow(ex);
		}

	}

	/**
	 * Write compatible object fields.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param toWrite
	 *            the to write
	 * @param fields
	 *            the fields
	 * @param fieldInfo
	 *            the field info
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	// write identical to other version, but take field values from hashmap (because of annoying putField/getField
	// feature)
	protected void writeCompatibleObjectFields(final Object toWrite, final Map fields,
			final FSTClazzInfo.FSTFieldInfo[] fieldInfo) throws IOException {
		int booleanMask = 0;
		int boolcount = 0;
		for (FSTFieldInfo subInfo : fieldInfo) {
			try {
				boolean isarr = subInfo.isArray();
				Class subInfType = subInfo.getType();
				if ((subInfType != boolean.class || isarr) && boolcount > 0) {
					getCodec().writeFByte(booleanMask << 8 - boolcount);
					boolcount = 0;
					booleanMask = 0;
				}
				if (subInfo.isIntegral() && !isarr) {
					if (subInfType == boolean.class) {
						if (boolcount == 8) {
							getCodec().writeFByte(booleanMask << 8 - boolcount);
							boolcount = 0;
							booleanMask = 0;
						}
						boolean booleanValue = ((Boolean) fields.get(subInfo.getName())).booleanValue();
						booleanMask = booleanMask << 1;
						booleanMask = booleanMask | (booleanValue ? 1 : 0);
						boolcount++;
					} else if (subInfType == int.class) {
						getCodec().writeFInt(((Number) fields.get(subInfo.getName())).intValue());
					} else if (subInfType == long.class) {
						getCodec().writeFLong(((Number) fields.get(subInfo.getName())).longValue());
					} else if (subInfType == byte.class) {
						getCodec().writeFByte(((Number) fields.get(subInfo.getName())).byteValue());
					} else if (subInfType == char.class) {
						getCodec().writeFChar((char) ((Number) fields.get(subInfo.getName())).intValue());
					} else if (subInfType == short.class) {
						getCodec().writeFShort(((Number) fields.get(subInfo.getName())).shortValue());
					} else if (subInfType == float.class) {
						getCodec().writeFFloat(((Number) fields.get(subInfo.getName())).floatValue());
					} else if (subInfType == double.class) {
						getCodec().writeFDouble(((Number) fields.get(subInfo.getName())).doubleValue());
					}
				} else {
					// object
					Object subObject = fields.get(subInfo.getName());
					writeObjectWithContext(subInfo, subObject);
				}
			} catch (Exception ex) {
				FSTUtil.<RuntimeException> rethrow(ex);
			}
		}
		if (boolcount > 0) { getCodec().writeFByte(booleanMask << 8 - boolcount); }
	}

	/**
	 *
	 * @param clsInfo
	 * @param referencee
	 * @param toWrite
	 * @return true if header already wrote object
	 * @throws IOException
	 */
	protected boolean writeObjectHeader(final FSTClazzInfo clsInfo, final FSTClazzInfo.FSTFieldInfo referencee,
			final Object toWrite) throws IOException {
		if (toWrite.getClass() == referencee.getType() && !clsInfo.useCompatibleMode())
			return getCodec().writeTag(TYPED, clsInfo, 0, toWrite, this);
		final Class[] possibleClasses = referencee.getPossibleClasses();
		if (possibleClasses != null) {
			final int length = possibleClasses.length;
			for (int j = 0; j < length; j++) {
				final Class possibleClass = possibleClasses[j];
				if (possibleClass == toWrite.getClass()) {
					getCodec().writeFByte(j + 1);
					return false;
				}
			}
		}
		if (!getCodec().writeTag(OBJECT, clsInfo, 0, toWrite, this)) {
			getCodec().writeClass(clsInfo);
			return false;
		}
		return true;
	}

	/**
	 * Write array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param array
	 *            the array
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	// incoming array is already registered
	protected void writeArray(final FSTClazzInfo.FSTFieldInfo referencee, final Object array) throws IOException {
		if (array == null) {
			getCodec().writeClass(Object.class);
			getCodec().writeFInt(-1);
			return;
		}
		final int len = Array.getLength(array);
		Class<?> componentType = array.getClass().getComponentType();
		getCodec().writeClass(array.getClass());
		getCodec().writeFInt(len);
		if (!componentType.isArray()) {
			if (getCodec().isPrimitiveArray(array, componentType)) {
				getCodec().writePrimitiveArray(array, 0, len);
			} else { // objects
				Object arr[] = (Object[]) array;
				Class lastClz = null;
				FSTClazzInfo lastInfo = null;
				for (int i = 0; i < len; i++) {
					Object toWrite = arr[i];
					if (toWrite != null) {
						lastInfo = writeObjectWithContext(referencee, toWrite,
								lastClz == toWrite.getClass() ? lastInfo : null);
						lastClz = toWrite.getClass();
					} else {
						writeObjectWithContext(referencee, toWrite, null);
					}
				}
			}
		} else { // multidim array. FIXME shared refs to subarrays are not tested !!!
			Object[] arr = (Object[]) array;
			FSTClazzInfo.FSTFieldInfo ref1 = new FSTClazzInfo.FSTFieldInfo(referencee.getPossibleClasses(), null,
					conf.getCLInfoRegistry().isIgnoreAnnotations());
			for (int i = 0; i < len; i++) {
				Object subArr = arr[i];
				boolean needsWrite = true;
				if (getCodec().isTagMultiDimSubArrays()) {
					if (subArr == null) {
						needsWrite = !getCodec().writeTag(NULL, null, 0, null, this);
					} else {
						needsWrite = !getCodec().writeTag(ARRAY, subArr, 0, subArr, this);
					}
				}
				if (needsWrite) {
					writeArray(ref1, subArr);
					getCodec().writeArrayEnd();
				}
			}
		}
	}

	/**
	 * Write string UTF.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param str
	 *            the str
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	public void writeStringUTF(final String str) throws IOException {
		getCodec().writeStringUTF(str);
	}

	/**
	 * Reset and clear refs.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 sept. 2023
	 */
	protected void resetAndClearRefs() {
		getCodec().reset(null);
		objects.clearForWrite(conf);
	}

	/**
	 * if out == null => automatically create/reuse a bytebuffer
	 *
	 * @param out
	 */
	public void resetForReUse(final OutputStream out) {
		if (closed) throw new RuntimeException("Can't reuse closed stream");
		getCodec().reset(null);
		if (out != null) { getCodec().setOutstream(out); }
		objects.clearForWrite(conf);
	}

	/**
	 * reset keeping the last used byte[] buffer
	 */
	public void resetForReUse() {
		resetForReUse((byte[]) null);
	}

	/**
	 * Reset for re use.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param out
	 *            the out
	 * @date 29 sept. 2023
	 */
	public void resetForReUse(final byte[] out) {
		if (closed) throw new RuntimeException("Can't reuse closed stream");
		getCodec().reset(out);
		objects.clearForWrite(conf);
	}

	/**
	 * Gets the class info registry.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the class info registry
	 * @date 29 sept. 2023
	 */
	public FSTClazzInfoRegistry getClassInfoRegistry() { return conf.getCLInfoRegistry(); }

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////// java serialization compatibility ////////////////////////////////////////////

	/**
	 *
	 * @param cl
	 *            - class or superclass of currently serialized obj, write declared fields of this class only
	 * @param clinfo
	 * @param referencee
	 * @param toWrite
	 * @return
	 * @throws IOException
	 */
	public ObjectOutputStream getObjectOutputStream(final Class cl, final FSTClazzInfo clinfo,
			final FSTClazzInfo.FSTFieldInfo referencee, final Object toWrite) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream() {
			@Override
			public void useProtocolVersion(final int version) throws IOException {}

			@Override
			protected void writeObjectOverride(final Object obj) throws IOException {
				getCodec().writeFByte(SPECIAL_COMPATIBILITY_OBJECT_TAG);
				FSTObjectOutput.this.writeObjectInternal(obj, null, referencee.getPossibleClasses());
			}

			@Override
			public void writeUnshared(final Object obj) throws IOException {
				writeObjectOverride(obj); // fixme
			}

			@Override
			public void defaultWriteObject() throws IOException {
				writeByte(99); // tag defaultwriteObject
				FSTClazzInfo newInfo = clinfo;
				Object replObj = toWrite;
				if (newInfo.getWriteReplaceMethod() != null) {
					DEBUG.OUT("WRITE REPLACE NOT FULLY SUPPORTED");
					try {
						Object replaced = newInfo.getWriteReplaceMethod().invoke(replObj);
						if (replaced != null && replaced != toWrite) {
							replObj = replaced;
							newInfo = getClassInfoRegistry().getCLInfo(replObj.getClass(), conf);
						}
					} catch (Exception e) {
						FSTUtil.<RuntimeException> rethrow(e);
					}
				}
				FSTObjectOutput.this.writeObjectFields(replObj, newInfo, newInfo.getCompInfo().get(cl).getFieldArray(),
						0, 0);
			}

			PutField pf;
			HashMap<String, Object> fields = new HashMap<>(); // fixme: init lazy

			@Override
			public PutField putFields() throws IOException {
				if (pf == null) {
					pf = new PutField() {
						@Override
						public void put(final String name, final boolean val) {
							fields.put(name, val);
						}

						@Override
						public void put(final String name, final byte val) {
							fields.put(name, val);
						}

						@Override
						public void put(final String name, final char val) {
							fields.put(name, val);
						}

						@Override
						public void put(final String name, final short val) {
							fields.put(name, val);
						}

						@Override
						public void put(final String name, final int val) {
							fields.put(name, val);
						}

						@Override
						public void put(final String name, final long val) {
							fields.put(name, val);
						}

						@Override
						public void put(final String name, final float val) {
							fields.put(name, val);
						}

						@Override
						public void put(final String name, final double val) {
							fields.put(name, val);
						}

						@Override
						public void put(final String name, final Object val) {
							fields.put(name, val);
						}

						@Override
						public void write(final ObjectOutput out) throws IOException {
							throw new IOException("cannot act compatible, use a custom serializer for this class");
						}
					};
				}
				return pf;
			}

			@Override
			public void writeFields() throws IOException {
				writeByte(77); // tag writeFields
				// FSTClazzInfo.FSTCompatibilityInfo fstCompatibilityInfo = clinfo.compInfo.get(cl);
				// if ( fstCompatibilityInfo.isAsymmetric() ) {
				// FSTObjectOutput.this.writeCompatibleObjectFields(toWrite, fields,
				// fstCompatibilityInfo.getFieldArray());
				// } else {
				FSTObjectOutput.this.writeObjectInternal(fields, null, HashMap.class);
				// }
			}

			@Override
			public void reset() throws IOException {
				throw new IOException("cannot act compatible, use a custom serializer for this class");
			}

			@Override
			public void write(final int val) throws IOException {
				getCodec().writeFByte(val);
			}

			@Override
			public void write(final byte[] buf) throws IOException {
				FSTObjectOutput.this.write(buf);
			}

			@Override
			public void write(final byte[] buf, final int off, final int len) throws IOException {
				FSTObjectOutput.this.write(buf, off, len);
			}

			@Override
			public void flush() throws IOException {
				FSTObjectOutput.this.flush();
			}

			@Override
			public void close() throws IOException {}

			@Override
			public void writeBoolean(final boolean val) throws IOException {
				FSTObjectOutput.this.writeBoolean(val);
			}

			@Override
			public void writeByte(final int val) throws IOException {
				getCodec().writeFByte(val);
			}

			@Override
			public void writeShort(final int val) throws IOException {
				getCodec().writeFShort((short) val);
			}

			@Override
			public void writeChar(final int val) throws IOException {
				getCodec().writeFChar((char) val);
			}

			@Override
			public void writeInt(final int val) throws IOException {
				getCodec().writeFInt(val);
			}

			@Override
			public void writeLong(final long val) throws IOException {
				getCodec().writeFLong(val);
			}

			@Override
			public void writeFloat(final float val) throws IOException {
				getCodec().writeFFloat(val);
			}

			@Override
			public void writeDouble(final double val) throws IOException {
				getCodec().writeFDouble(val);
			}

			@Override
			public void writeBytes(final String str) throws IOException {
				FSTObjectOutput.this.writeBytes(str);
			}

			@Override
			public void writeChars(final String str) throws IOException {
				FSTObjectOutput.this.writeChars(str);
			}

			@Override
			public void writeUTF(final String str) throws IOException {
				getCodec().writeStringUTF(str);
			}
		};

		return out;
	}

	/**
	 * Gets the object map.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the object map
	 * @date 29 sept. 2023
	 */
	public FSTObjectRegistry getObjectMap() { return objects; }

	/**
	 * @return the written buffer reference. use getWritten() to obtain the length of written bytes. WARNING: if more
	 *         than one objects have been written, an implicit flush is triggered, so the buffer only contains the last
	 *         written object. getWritten() then has a larger size than the buffer length. only usable if one single
	 *         object is written to the stream (e.g. messaging)
	 *
	 *         note: in case of non-standard underlyings (e.g. serializing to direct offheap or DirectBuffer, this
	 *         method might cause creation of a byte array and a copy.
	 */
	public byte[] getBuffer() { return getCodec().getBuffer(); }

	/**
	 * @return a copy of written bytes. Warning: if the stream has been flushed, this will fail with an exception. a
	 *         flush is triggered after each 1st level writeObject.
	 *
	 *         note: in case of non-stream based serialization (directbuffer, offheap mem) getBuffer will return a copy
	 *         anyways.
	 */
	public byte[] getCopyOfWrittenBuffer() {
		if (!getCodec().isByteArrayBased()) return getBuffer();
		byte res[] = new byte[getCodec().getWritten()];
		byte[] buffer = getBuffer();
		System.arraycopy(buffer, 0, res, 0, getCodec().getWritten());
		return res;
	}

	/**
	 * Gets the conf.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the conf
	 * @date 29 sept. 2023
	 */
	public FSTConfiguration getConf() { return conf; }

	/**
	 * @return the number of bytes written to this stream. This also is the number of valid bytes in the buffer one
	 *         obtains from the various getBuffer, getCopyOfBuffer methods. Warning: if the stream has been flushed
	 *         (done after each 1st level object write), the buffer will be smaller than the value given here or contain
	 *         invalid bytes.
	 */
	public int getWritten() { return getCodec().getWritten(); }

	/**
	 * Write class tag.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param aClass
	 *            the a class
	 * @date 29 sept. 2023
	 */
	public void writeClassTag(final Class aClass) {
		getCodec().writeClass(aClass);
	}

	/**
	 * Gets the codec.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the codec
	 * @date 29 sept. 2023
	 */
	public FSTEncoder getCodec() { return codec; }

	/**
	 * Sets the codec.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param codec
	 *            the new codec
	 * @date 29 sept. 2023
	 */
	protected void setCodec(final FSTEncoder codec) { this.codec = codec; }
}