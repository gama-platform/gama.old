/*******************************************************************************************************
 *
 * FSTObjectInput.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectStreamClass;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.nustaq.serialization.FSTClazzInfo.FSTFieldInfo;
import org.nustaq.serialization.coders.Unknown;
import org.nustaq.serialization.util.FSTUtil;

import ummisco.gama.dev.utils.DEBUG;

/**
 * Created with IntelliJ IDEA. User: Möller Date: 04.11.12 Time: 11:53
 */
/**
 * replacement of ObjectInputStream
 */
public class FSTObjectInput implements ObjectInput {

	/** The register enums read. */
	public static boolean REGISTER_ENUMS_READ = false; // do not register enums on read. Flag is saver in case things
														// brake somewhere

	/** The empty stream. */
	public static ByteArrayInputStream emptyStream = new ByteArrayInputStream(new byte[0]);

	/** The codec. */
	protected FSTDecoder codec;

	/** The objects. */
	protected FSTObjectRegistry objects;

	/** The debug stack. */
	protected Stack<String> debugStack;

	/** The cur depth. */
	protected int curDepth;

	/** The callbacks. */
	protected ArrayList<CallbackEntry> callbacks;
	// FSTConfiguration conf;
	/** The ignore annotations. */
	// mirrored from conf
	protected boolean ignoreAnnotations;

	/** The cl info registry. */
	protected FSTClazzInfoRegistry clInfoRegistry;

	/** The conditional callback. */
	// done
	protected ConditionalCallback conditionalCallback;

	/** The read external read A head. */
	protected int readExternalReadAHead = 8000;

	/** The version conflict listener. */
	protected VersionConflictListener versionConflictListener;

	/** The conf. */
	protected FSTConfiguration conf;

	/** The is cross platform. */
	// copied values from conf
	protected boolean isCrossPlatform;

	/**
	 * Gets the conf.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the conf
	 * @date 29 sept. 2023
	 */
	public FSTConfiguration getConf() { return conf; }

	@Override
	public void readFully(final byte[] b) throws IOException {
		readFully(b, 0, b.length);
	}

	@Override
	public void readFully(final byte[] b, final int off, final int len) throws IOException {
		getCodec().readPlainBytes(b, off, len);

	}

	@Override
	public int skipBytes(final int n) throws IOException {
		getCodec().skip(n);
		return n;
	}

	@Override
	public boolean readBoolean() throws IOException {
		return getCodec().readFByte() != 0;
	}

	@Override
	public byte readByte() throws IOException {
		return getCodec().readFByte();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return getCodec().readFByte() + 256 & 0xff;
	}

	@Override
	public short readShort() throws IOException {
		return getCodec().readFShort();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return readShort() + 65536 & 0xffff;
	}

	@Override
	public char readChar() throws IOException {
		return getCodec().readFChar();
	}

	@Override
	public int readInt() throws IOException {
		return getCodec().readFInt();
	}

	@Override
	public long readLong() throws IOException {
		return getCodec().readFLong();
	}

	@Override
	public float readFloat() throws IOException {
		return getCodec().readFFloat();
	}

	@Override
	public double readDouble() throws IOException {
		return getCodec().readFDouble();
	}

	@Override
	public String readLine() throws IOException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String readUTF() throws IOException {
		return getCodec().readStringUTF();
	}

	/**
	 * Gets the codec.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the codec
	 * @date 29 sept. 2023
	 */
	public FSTDecoder getCodec() { return codec; }

	/**
	 * Sets the codec.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param codec
	 *            the new codec
	 * @date 29 sept. 2023
	 */
	protected void setCodec(final FSTDecoder codec) { this.codec = codec; }

	/**
	 * Checks if is closed.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is closed
	 * @date 29 sept. 2023
	 */
	public boolean isClosed() { return closed; }

	/**
	 * The Class CallbackEntry.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 sept. 2023
	 */
	protected static class CallbackEntry {

		/** The cb. */
		ObjectInputValidation cb;

		/** The prio. */
		int prio;

		/**
		 * Instantiates a new callback entry.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param cb
		 *            the cb
		 * @param prio
		 *            the prio
		 * @date 29 sept. 2023
		 */
		CallbackEntry(final ObjectInputValidation cb, final int prio) {
			this.cb = cb;
			this.prio = prio;
		}
	}

	/**
	 * The Interface ConditionalCallback.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 sept. 2023
	 */
	public interface ConditionalCallback {

		/**
		 * Should skip.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param halfDecoded
		 *            the half decoded
		 * @param streamPosition
		 *            the stream position
		 * @param field
		 *            the field
		 * @return true, if successful
		 * @date 29 sept. 2023
		 */
		boolean shouldSkip(Object halfDecoded, int streamPosition, Field field);
	}

	/**
	 * Instantiates a new FST object input.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @date 29 sept. 2023
	 */
	public FSTObjectInput(final FSTConfiguration conf) {
		this(emptyStream, conf);
	}

	/**
	 * Creates a FSTObjectInput that uses the specified underlying InputStream.
	 *
	 * Don't create a FSTConfiguration with each stream, just create one global static configuration and reuseit.
	 * FSTConfiguration is threadsafe.
	 *
	 * @param in
	 *            the specified input stream
	 */
	public FSTObjectInput(final InputStream in, final FSTConfiguration conf) {
		setCodec(conf.createStreamDecoder());
		getCodec().setInputStream(in);
		isCrossPlatform = conf.isCrossPlatform();
		initRegistries(conf);
		this.conf = conf;
	}

	/**
	 * Gets the class for name.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @return the class for name
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @date 29 sept. 2023
	 */
	public Class<?> getClassForName(final String name) throws ClassNotFoundException {
		return getCodec().classForName(name);
	}

	/**
	 * Inits the registries.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @date 29 sept. 2023
	 */
	protected void initRegistries(final FSTConfiguration conf) {
		ignoreAnnotations = conf.getCLInfoRegistry().isIgnoreAnnotations();
		clInfoRegistry = conf.getCLInfoRegistry();

		objects = (FSTObjectRegistry) conf.getCachedObject(FSTObjectRegistry.class);
		if (objects == null) {
			objects = new FSTObjectRegistry(conf);
		} else {
			objects.clearForRead(conf);
		}
	}

	/**
	 * Gets the conditional callback.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the conditional callback
	 * @date 29 sept. 2023
	 */
	public ConditionalCallback getConditionalCallback() { return conditionalCallback; }

	/**
	 * Sets the conditional callback.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conditionalCallback
	 *            the new conditional callback
	 * @date 29 sept. 2023
	 */
	public void setConditionalCallback(final ConditionalCallback conditionalCallback) {
		this.conditionalCallback = conditionalCallback;
	}

	/**
	 * Gets the read external read A head.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the read external read A head
	 * @date 29 sept. 2023
	 */
	public int getReadExternalReadAHead() { return readExternalReadAHead; }

	/**
	 * since the stock readXX methods on InputStream are final, i can't ensure sufficient readAhead on the inputStream
	 * before calling readExternal. Default value is 16000 bytes. If you make use of the externalizable interfac and
	 * write larger Objects a) cast the ObjectInput in readExternal to FSTObjectInput and call ensureReadAhead on this
	 * in your readExternal method b) set a sufficient maximum using this method before serializing.
	 *
	 * @param readExternalReadAHead
	 */
	public void setReadExternalReadAHead(final int readExternalReadAHead) {
		this.readExternalReadAHead = readExternalReadAHead;
	}

	@Override
	public Object readObject() throws ClassNotFoundException, IOException {
		try {
			return readObject((Class[]) null);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public int read() throws IOException {
		return getCodec().readIntByte();
	}

	@Override
	public int read(final byte[] b) throws IOException {
		getCodec().readPlainBytes(b, 0, b.length);
		return b.length;
	}

	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException {
		getCodec().readPlainBytes(b, off, len);
		return b.length;
	}

	@Override
	public long skip(final long n) throws IOException {
		getCodec().skip((int) n);
		return n;
	}

	@Override
	public int available() throws IOException {
		return getCodec().available();
	}

	/**
	 * Process validation.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws InvalidObjectException
	 *             the invalid object exception
	 * @date 29 sept. 2023
	 */
	protected void processValidation() throws InvalidObjectException {
		if (callbacks == null) return;
		Collections.sort(callbacks, (o1, o2) -> o2.prio - o1.prio);
		for (CallbackEntry callbackEntry : callbacks) {
			try {
				callbackEntry.cb.validateObject();
			} catch (Exception ex) {
				FSTUtil.<RuntimeException> rethrow(ex);
			}
		}
	}

	/**
	 * Read object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param possibles
	 *            the possibles
	 * @return the object
	 * @throws Exception
	 *             the exception
	 * @date 29 sept. 2023
	 */
	public Object readObject(final Class<?>... possibles) throws Exception {
		curDepth++;
		if (isCrossPlatform) return readObjectInternal(); // not supported cross platform
		try {
			if (possibles != null && possibles.length > 1) {
				for (Class<?> possible : possibles) { getCodec().registerClass(possible); }
			}
			Object res = readObjectInternal(possibles);
			processValidation();
			return res;
		} catch (Throwable th) {
			FSTUtil.<RuntimeException> rethrow(th);
		} finally {
			curDepth--;
		}
		return null;
	}

	/** The info cache. */
	protected FSTClazzInfo.FSTFieldInfo infoCache;

	/**
	 * Read object internal.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param expected
	 *            the expected
	 * @return the object
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @date 29 sept. 2023
	 */
	public Object readObjectInternal(final Class<?>... expected)
			throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException {
		try {
			FSTClazzInfo.FSTFieldInfo info = infoCache;
			infoCache = null;
			if (info == null) {
				info = new FSTClazzInfo.FSTFieldInfo(expected, null, ignoreAnnotations);
			} else {
				info.possibleClasses = expected;
			}
			Object res = readObjectWithHeader(info);
			infoCache = info;
			return res;
		} catch (Throwable t) {
			FSTUtil.<RuntimeException> rethrow(t);
		}
		return null;
	}

	/**
	 * Read object with header.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @return the object
	 * @throws Exception
	 *             the exception
	 * @date 29 sept. 2023
	 */
	public Object readObjectWithHeader(final FSTClazzInfo.FSTFieldInfo referencee) throws Exception {
		FSTClazzInfo clzSerInfo;
		Class<?> c;
		final int readPos = getCodec().getInputPos();
		byte code = getCodec().readObjectHeaderTag(); // NOTICE: THIS ADVANCES THE INPUT STREAM...
		if (code == FSTObjectOutput.OBJECT) {
			// class name
			clzSerInfo = readClass();
			c = clzSerInfo.getClazz();
			if (c.isArray()) return readArrayNoHeader(referencee, readPos, c);
			// fall through
		} else if (code == FSTObjectOutput.TYPED) {
			c = referencee.getType();
			clzSerInfo = getClazzInfo(c, referencee);
		} else if (code >= 1) {
			try {
				c = referencee.getPossibleClasses()[code - 1];
				clzSerInfo = getClazzInfo(c, referencee);
			} catch (Throwable th) {
				clzSerInfo = null;
				c = null;
				FSTUtil.<RuntimeException> rethrow(th);
			}
		} else {
			Object res = instantiateSpecialTag(referencee, readPos, code);
			return res;
		}
		try {
			FSTObjectSerializer ser = clzSerInfo.getSer();
			if (ser != null) {
				Object res = instantiateAndReadWithSer(c, ser, clzSerInfo, referencee, readPos);
				getCodec().readArrayEnd(clzSerInfo);
				return res;
			}
			Object res = instantiateAndReadNoSer(c, clzSerInfo, referencee, readPos);
			return res;
		} catch (Exception e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
		return null;
	}

	/**
	 * Instantiate special tag.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param readPos
	 *            the read pos
	 * @param code
	 *            the code
	 * @return the object
	 * @throws Exception
	 *             the exception
	 * @date 29 sept. 2023
	 */
	protected Object instantiateSpecialTag(final FSTClazzInfo.FSTFieldInfo referencee, final int readPos,
			final byte code) throws Exception {
		switch (code) {
			case FSTObjectOutput.STRING: {
				String res = getCodec().readStringUTF();
				objects.registerObjectForRead(res, readPos);
				return res;
			}
			case FSTObjectOutput.BIG_INT:
				return instantiateBigInt();
			case FSTObjectOutput.NULL:
				return null;
			default:
				switch (code) {
					// case FSTObjectOutput.BIG_INT: { return instantiateBigInt(); }
					case FSTObjectOutput.BIG_LONG: {
						return Long.valueOf(getCodec().readFLong());
					}
					case FSTObjectOutput.BIG_BOOLEAN_FALSE: {
						return Boolean.FALSE;
					}
					case FSTObjectOutput.BIG_BOOLEAN_TRUE: {
						return Boolean.TRUE;
					}
					case FSTObjectOutput.ONE_OF: {
						return referencee.getOneOf()[getCodec().readFByte()];
					}
					// case FSTObjectOutput.NULL: { return null; }
					case FSTObjectOutput.DIRECT_ARRAY_OBJECT: {
						Object directObject = getCodec().getDirectObject();
						objects.registerObjectForRead(directObject, readPos);
						return directObject;
					}
					case FSTObjectOutput.DIRECT_OBJECT: {
						Object directObject = getCodec().getDirectObject();
						if (directObject.getClass() == byte[].class && referencee != null
								&& referencee.getType() == boolean[].class) {
							byte[] ba = (byte[]) directObject;
							boolean res[] = new boolean[ba.length];
							for (int i = 0; i < res.length; i++) { res[i] = ba[i] != 0; }
							directObject = res;
						}
						objects.registerObjectForRead(directObject, readPos);
						return directObject;
					}
					// case FSTObjectOutput.STRING: return getCodec().readStringUTF();
					case FSTObjectOutput.HANDLE: {
						Object res = instantiateHandle(referencee);
						getCodec().readObjectEnd();
						return res;
					}
					case FSTObjectOutput.ARRAY: {
						Object res = instantiateArray(referencee, readPos);
						return res;
					}
					case FSTObjectOutput.ENUM: {
						return instantiateEnum(referencee, readPos);
					}
				}
				throw new RuntimeException("unknown object tag " + code);
		}
	}

	/**
	 * Gets the clazz info.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @param referencee
	 *            the referencee
	 * @return the clazz info
	 * @date 29 sept. 2023
	 */
	protected FSTClazzInfo getClazzInfo(final Class<?> c, final FSTClazzInfo.FSTFieldInfo referencee) {
		FSTClazzInfo clzSerInfo;
		FSTClazzInfo lastInfo = referencee.lastInfo;
		if (lastInfo != null && lastInfo.clazz == c && lastInfo.conf == conf) {
			clzSerInfo = lastInfo;
		} else {
			clzSerInfo = clInfoRegistry.getCLInfo(c, conf);
			referencee.lastInfo = clzSerInfo;
		}
		return clzSerInfo;
	}

	/**
	 * Instantiate handle.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @return the object
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	protected Object instantiateHandle(final FSTClazzInfo.FSTFieldInfo referencee) throws IOException {
		int handle = getCodec().readFInt();
		Object res = objects.getReadRegisteredObject(handle);
		if (res == null) throw new IOException(
				"unable to ressolve handle " + handle + " " + referencee.getDesc() + " " + getCodec().getInputPos());
		return res;
	}

	/**
	 * Instantiate array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param readPos
	 *            the read pos
	 * @return the object
	 * @throws Exception
	 *             the exception
	 * @date 29 sept. 2023
	 */
	protected Object instantiateArray(final FSTClazzInfo.FSTFieldInfo referencee, final int readPos) throws Exception {
		Object res = readArray(referencee, readPos); // NEED TO PASS ALONG THE POS FOR THE ARRAY

		/*
		 * registerObjectForRead alerady gets called by readArray (and with the proper pos now). that said, I'm unclear
		 * on the intent of the if ( ! referencee.isFlat() ) so I wanted to comment on that
		 *
		 * if ( ! referencee.isFlat() ) { objects.registerObjectForRead(res, readPos); }
		 */

		return res;
	}

	/**
	 * Instantiate enum.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param readPos
	 *            the read pos
	 * @return the object
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @date 29 sept. 2023
	 */
	protected Object instantiateEnum(final FSTClazzInfo.FSTFieldInfo referencee, final int readPos)
			throws IOException, ClassNotFoundException {
		FSTClazzInfo clzSerInfo;
		// Class c;
		clzSerInfo = readClass();
		// c = clzSerInfo.getClazz();
		int ordinal = getCodec().readFInt();
		Object[] enumConstants = clzSerInfo.getEnumConstants();
		if (enumConstants == null) // pseudo enum of anonymous classes tom style ?
			return null;
		Object res = enumConstants[ordinal];
		if (REGISTER_ENUMS_READ && !referencee.isFlat()) { // should be unnecessary
			objects.registerObjectForRead(res, readPos);
		}
		return res;
	}

	/**
	 * Instantiate big int.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the object
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	protected Object instantiateBigInt() throws IOException {
		int val = getCodec().readFInt();
		return Integer.valueOf(val);
	}

	/**
	 * Instantiate and read with ser.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @param ser
	 *            the ser
	 * @param clzSerInfo
	 *            the clz ser info
	 * @param referencee
	 *            the referencee
	 * @param readPos
	 *            the read pos
	 * @return the object
	 * @throws Exception
	 *             the exception
	 * @date 29 sept. 2023
	 */
	protected Object instantiateAndReadWithSer(Class<?> c, final FSTObjectSerializer ser, FSTClazzInfo clzSerInfo,
			final FSTClazzInfo.FSTFieldInfo referencee, final int readPos) throws Exception {
		boolean serInstance = false;
		Object newObj = ser.instantiate(c, this, clzSerInfo, referencee, readPos);
		if (newObj == null) {
			newObj = clzSerInfo.newInstance(getCodec().isMapBased());
		} else {
			serInstance = true;
		}
		if (newObj == null) throw new IOException(referencee.getDesc() + ":Failed to instantiate '" + c.getName()
				+ "'. Register a custom serializer implementing instantiate or define empty constructor..");
		if (newObj == FSTObjectSerializer.REALLY_NULL) {
			newObj = null;
		} else {
			if (newObj.getClass() != c) {
				// for advanced trickery (e.g. returning non-serializable from FSTSerializer)
				// this hurts. so in case of FSTSerializers incoming clzInfo will refer to the
				// original class, not the one actually instantiated
				c = newObj.getClass();
				clzSerInfo = clInfoRegistry.getCLInfo(c, conf);
			}
			if (!referencee.isFlat() && !clzSerInfo.isFlat() && !ser.alwaysCopy()) {
				objects.registerObjectForRead(newObj, readPos);
			}
			if (!serInstance) { ser.readObject(this, newObj, clzSerInfo, referencee); }
		}
		getCodec().consumeEndMarker(); // => bug when writing objects unlimited
		return newObj;
	}

	/**
	 * Instantiate and read no ser.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @param clzSerInfo
	 *            the clz ser info
	 * @param referencee
	 *            the referencee
	 * @param readPos
	 *            the read pos
	 * @return the object
	 * @throws Exception
	 *             the exception
	 * @date 29 sept. 2023
	 */
	protected Object instantiateAndReadNoSer(final Class<?> c, final FSTClazzInfo clzSerInfo,
			final FSTClazzInfo.FSTFieldInfo referencee, final int readPos) throws Exception {
		Object newObj;
		newObj = clzSerInfo.newInstance(getCodec().isMapBased());
		if (newObj == null) throw new IOException(referencee.getDesc() + ":Failed to instantiate '" + c.getName()
				+ "'. Register a custom serializer implementing instantiate or define empty constructor.");
		// fixme: code below improves unshared decoding perf, however disables to run mixed mode (clients can decide)
		// actually would need 2 flags for encode/decode
		// tested with json mixed mode does not work anyway ...
		final boolean needsRefLookup = conf.shareReferences && !referencee.isFlat() && !clzSerInfo.isFlat();
		// previously :
		// final boolean needsRefLookup = !referencee.isFlat() && !clzSerInfo.isFlat();
		if (needsRefLookup) { objects.registerObjectForRead(newObj, readPos); }
		if (clzSerInfo.isExternalizable()) {
			int tmp = readPos;
			getCodec().ensureReadAhead(readExternalReadAHead);
			((Externalizable) newObj).readExternal(this);
			getCodec().readExternalEnd();
			if (clzSerInfo.getReadResolveMethod() != null) {
				final Object prevNew = newObj;
				newObj = handleReadRessolve(clzSerInfo, newObj);
				if (newObj != prevNew && needsRefLookup) { objects.replace(prevNew, newObj, tmp); }
			}
		} else if (clzSerInfo.useCompatibleMode()) {
			Object replaced = readObjectCompatible(referencee, clzSerInfo, newObj);
			if (replaced != null && replaced != newObj) {
				objects.replace(newObj, replaced, readPos);
				newObj = replaced;
			}
		} else {
			FSTClazzInfo.FSTFieldInfo[] fieldInfo = clzSerInfo.getFieldInfo();
			readObjectFields(referencee, clzSerInfo, fieldInfo, newObj, 0, 0);
		}
		return newObj;
	}

	/**
	 * Read object compatible.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param serializationInfo
	 *            the serialization info
	 * @param newObj
	 *            the new obj
	 * @return the object
	 * @throws Exception
	 *             the exception
	 * @date 29 sept. 2023
	 */
	protected Object readObjectCompatible(final FSTClazzInfo.FSTFieldInfo referencee,
			final FSTClazzInfo serializationInfo, Object newObj) throws Exception {
		Class<?> cl = serializationInfo.getClazz();
		readObjectCompatibleRecursive(referencee, newObj, serializationInfo, cl);
		if (newObj != null && serializationInfo.getReadResolveMethod() != null) {
			newObj = handleReadRessolve(serializationInfo, newObj);
		}
		return newObj;
	}

	/**
	 * Handle read ressolve.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param serializationInfo
	 *            the serialization info
	 * @param newObj
	 *            the new obj
	 * @return the object
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @date 29 sept. 2023
	 */
	protected Object handleReadRessolve(final FSTClazzInfo serializationInfo, Object newObj)
			throws IllegalAccessException {
		Object rep = null;
		try {
			rep = serializationInfo.getReadResolveMethod().invoke(newObj);
		} catch (InvocationTargetException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
		newObj = rep;// FIXME: support this in call
		return newObj;
	}

	/**
	 * Read object compatible recursive.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param toRead
	 *            the to read
	 * @param serializationInfo
	 *            the serialization info
	 * @param cl
	 *            the cl
	 * @throws Exception
	 *             the exception
	 * @date 29 sept. 2023
	 */
	protected void readObjectCompatibleRecursive(final FSTClazzInfo.FSTFieldInfo referencee, final Object toRead,
			final FSTClazzInfo serializationInfo, final Class<?> cl) throws Exception {
		FSTClazzInfo.FSTCompatibilityInfo fstCompatibilityInfo = serializationInfo.getCompInfo().get(cl);
		if (!Serializable.class.isAssignableFrom(cl)) return; // ok here, as compatible mode will never be triggered for
																// "forceSerializable"
		readObjectCompatibleRecursive(referencee, toRead, serializationInfo, cl.getSuperclass());
		if (fstCompatibilityInfo != null && fstCompatibilityInfo.getReadMethod() != null) {
			try {
				int tag = readByte(); // expect 55
				if (tag == 66) {
					// no write method defined, but read method defined ...
					// expect defaultReadObject
					getCodec().moveTo(getCodec().getInputPos() - 1); // need to push back tag, cause defaultWriteObject
																		// on writer side does not write tag
					// input.pos--;
				}
				ObjectInputStream objectInputStream = getObjectInputStream(cl, serializationInfo, referencee, toRead);
				fstCompatibilityInfo.getReadMethod().invoke(toRead, objectInputStream);
				fakeWrapper.pop();
			} catch (Exception e) {
				FSTUtil.<RuntimeException> rethrow(e);
			}
		} else if (fstCompatibilityInfo != null) {
			int tag = readByte();
			if (tag == 55) {
				// came from writeMethod, but no readMethod defined => assume defaultWriteObject
				tag = readByte(); // consume tag of defaultwriteobject (99)
				if (tag == 77) // came from putfield
				{
					HashMap<String, Object> fieldMap =
							(HashMap<String, Object>) FSTObjectInput.this.readObjectInternal(HashMap.class);
					final FSTClazzInfo.FSTFieldInfo[] fieldArray = fstCompatibilityInfo.getFieldArray();
					for (FSTFieldInfo fstFieldInfo : fieldArray) {
						final Object val = fieldMap.get(fstFieldInfo.getName());
						if (val != null) { fstFieldInfo.setObjectValue(toRead, val); }
					}
					return;
				}
			}
			readObjectFields(referencee, serializationInfo, fstCompatibilityInfo.getFieldArray(), toRead, 0, 0);
		}
	}

	/**
	 * Default read object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param serializationInfo
	 *            the serialization info
	 * @param newObj
	 *            the new obj
	 * @date 29 sept. 2023
	 */
	public void defaultReadObject(final FSTClazzInfo.FSTFieldInfo referencee, final FSTClazzInfo serializationInfo,
			final Object newObj) {
		try {
			readObjectFields(referencee, serializationInfo, serializationInfo.getFieldInfo(), newObj, 0, -1); // -1 flag
																												// to
																												// indicate
																												// no
																												// object
																												// end
																												// should
																												// be
																												// called
		} catch (Exception e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
	}

	/**
	 * Read object fields.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param serializationInfo
	 *            the serialization info
	 * @param fieldInfo
	 *            the field info
	 * @param newObj
	 *            the new obj
	 * @param startIndex
	 *            the start index
	 * @param version
	 *            the version
	 * @throws Exception
	 *             the exception
	 * @date 29 sept. 2023
	 */
	protected void readObjectFields(final FSTClazzInfo.FSTFieldInfo referencee, final FSTClazzInfo serializationInfo,
			final FSTClazzInfo.FSTFieldInfo[] fieldInfo, final Object newObj, final int startIndex, int version)
			throws Exception {

		if (getCodec().isMapBased()) {
			readFieldsMapBased(referencee, serializationInfo, newObj);
			if (version >= 0 /* && newObj instanceof Unknown == false */ ) { getCodec().readObjectEnd(); }
			return;
		}
		if (version < 0) { version = 0; }
		int booleanMask = 0;
		int boolcount = 8;
		final int length = fieldInfo.length;
		int conditional = 0;
		for (int i = startIndex; i < length; i++) {
			try {
				FSTClazzInfo.FSTFieldInfo subInfo = fieldInfo[i];
				if (subInfo.getVersion() > version) {
					int nextVersion = getCodec().readVersionTag();
					if (nextVersion == 0) // old object read
					{
						oldVersionRead(newObj);
						return;
					}
					if (nextVersion != subInfo.getVersion()) throw new RuntimeException(
							"read version tag " + nextVersion + " fieldInfo has " + subInfo.getVersion());
					readObjectFields(referencee, serializationInfo, fieldInfo, newObj, i, nextVersion);
					return;
				}
				if (subInfo.isPrimitive()) {
					int integralType = subInfo.getIntegralType();
					if (integralType == FSTClazzInfo.FSTFieldInfo.BOOL) {
						if (boolcount == 8) {
							booleanMask = getCodec().readFByte() + 256 & 0xff;
							boolcount = 0;
						}
						boolean val = (booleanMask & 128) != 0;
						booleanMask = booleanMask << 1;
						boolcount++;
						subInfo.setBooleanValue(newObj, val);
					} else {
						switch (integralType) {
							case FSTClazzInfo.FSTFieldInfo.BYTE:
								subInfo.setByteValue(newObj, getCodec().readFByte());
								break;
							case FSTClazzInfo.FSTFieldInfo.CHAR:
								subInfo.setCharValue(newObj, getCodec().readFChar());
								break;
							case FSTClazzInfo.FSTFieldInfo.SHORT:
								subInfo.setShortValue(newObj, getCodec().readFShort());
								break;
							case FSTClazzInfo.FSTFieldInfo.INT:
								subInfo.setIntValue(newObj, getCodec().readFInt());
								break;
							case FSTClazzInfo.FSTFieldInfo.LONG:
								subInfo.setLongValue(newObj, getCodec().readFLong());
								break;
							case FSTClazzInfo.FSTFieldInfo.FLOAT:
								subInfo.setFloatValue(newObj, getCodec().readFFloat());
								break;
							case FSTClazzInfo.FSTFieldInfo.DOUBLE:
								subInfo.setDoubleValue(newObj, getCodec().readFDouble());
								break;
						}
					}
				} else {
					if (subInfo.isConditional() && conditional == 0) {
						conditional = getCodec().readPlainInt();
						if (skipConditional(newObj, conditional, subInfo)) {
							getCodec().moveTo(conditional);
							continue;
						}
					}
					// object
					Object subObject = readObjectWithHeader(subInfo);
					subInfo.setObjectValue(newObj, subObject);
				}
			} catch (IllegalAccessException ex) {
				throw new IOException(ex);
			}
		}
		// int debug =
		getCodec().readVersionTag();// just consume '0'
	}

	/**
	 * Gets the version conflict listener.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the version conflict listener
	 * @date 29 sept. 2023
	 */
	public VersionConflictListener getVersionConflictListener() { return versionConflictListener; }

	/**
	 * see @Version annotation
	 *
	 * @param versionConflictListener
	 */
	public void setVersionConflictListener(final VersionConflictListener versionConflictListener) {
		this.versionConflictListener = versionConflictListener;
	}

	/**
	 * Old version read.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param newObj
	 *            the new obj
	 * @date 29 sept. 2023
	 */
	protected void oldVersionRead(final Object newObj) {
		if (versionConflictListener != null) { versionConflictListener.onOldVersionRead(newObj); }
	}

	/**
	 * Read fields map based.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param serializationInfo
	 *            the serialization info
	 * @param newObj
	 *            the new obj
	 * @throws Exception
	 *             the exception
	 * @date 29 sept. 2023
	 */
	protected void readFieldsMapBased(final FSTClazzInfo.FSTFieldInfo referencee, final FSTClazzInfo serializationInfo,
			final Object newObj) throws Exception {
		String name;
		int len = getCodec().getObjectHeaderLen(); // check if len is known in advance
		if (len < 0) { len = Integer.MAX_VALUE; }
		int count = 0;
		boolean isUnknown = newObj.getClass() == Unknown.class; // json
		boolean inArray = isUnknown && getCodec().inArray(); // json externalized/custom serialized
		getCodec().startFieldReading(newObj);
		// fixme: break up this loop into separate impls.
		while (count < len) {
			if (inArray) {
				// unknwon json object written by externalize or custom serializer
				Object o = readObjectWithHeader(null);
				if (o != null && getCodec().isEndMarker(o.toString())) return;
				((Unknown) newObj).add(o);
				continue;
			}
			name = getCodec().readStringUTF();
			// int debug = getCodec().getInputPos();
			if (len == Integer.MAX_VALUE && getCodec().isEndMarker(name)) return;
			count++;
			if (isUnknown) {
				FSTClazzInfo.FSTFieldInfo fakeField = new FSTClazzInfo.FSTFieldInfo(null, null, true);
				fakeField.fakeName = name;
				Object toSet = readObjectWithHeader(fakeField);
				((Unknown) newObj).set(name, toSet);
			} else {
				FSTClazzInfo.FSTFieldInfo fieldInfo = serializationInfo.getFieldInfo(name, null);
				if (fieldInfo == null) {
					DEBUG.OUT(
							"warning: unknown field: " + name + " on class " + serializationInfo.getClazz().getName());
				} else if (fieldInfo.isPrimitive()) {
					// direct primitive field
					switch (fieldInfo.getIntegralType()) {
						case FSTClazzInfo.FSTFieldInfo.BOOL:
							fieldInfo.setBooleanValue(newObj, getCodec().readFByte() != 0);
							break;
						case FSTClazzInfo.FSTFieldInfo.BYTE:
							fieldInfo.setByteValue(newObj, getCodec().readFByte());
							break;
						case FSTClazzInfo.FSTFieldInfo.CHAR:
							fieldInfo.setCharValue(newObj, getCodec().readFChar());
							break;
						case FSTClazzInfo.FSTFieldInfo.SHORT:
							fieldInfo.setShortValue(newObj, getCodec().readFShort());
							break;
						case FSTClazzInfo.FSTFieldInfo.INT:
							fieldInfo.setIntValue(newObj, getCodec().readFInt());
							break;
						case FSTClazzInfo.FSTFieldInfo.LONG:
							fieldInfo.setLongValue(newObj, getCodec().readFLong());
							break;
						case FSTClazzInfo.FSTFieldInfo.FLOAT:
							fieldInfo.setFloatValue(newObj, getCodec().readFFloat());
							break;
						case FSTClazzInfo.FSTFieldInfo.DOUBLE:
							fieldInfo.setDoubleValue(newObj, getCodec().readFDouble());
							break;
						default:
							throw new RuntimeException("unkown primitive type " + fieldInfo);
					}
				} else {
					Object toSet = readObjectWithHeader(fieldInfo);
					toSet = getCodec().coerceElement(fieldInfo.getType(), toSet);
					fieldInfo.setObjectValue(newObj, toSet);
				}
			}
		}
		getCodec().endFieldReading(newObj);
	}

	/**
	 * Skip conditional.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param newObj
	 *            the new obj
	 * @param conditional
	 *            the conditional
	 * @param subInfo
	 *            the sub info
	 * @return true, if successful
	 * @date 29 sept. 2023
	 */
	protected boolean skipConditional(final Object newObj, final int conditional,
			final FSTClazzInfo.FSTFieldInfo subInfo) {
		if (conditionalCallback != null) return conditionalCallback.shouldSkip(newObj, conditional, subInfo.getField());
		return false;
	}

	/**
	 * Read compatible object fields.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param serializationInfo
	 *            the serialization info
	 * @param fieldInfo
	 *            the field info
	 * @param res
	 *            the res
	 * @throws Exception
	 *             the exception
	 * @date 29 sept. 2023
	 */
	protected void readCompatibleObjectFields(final FSTClazzInfo.FSTFieldInfo referencee,
			final FSTClazzInfo serializationInfo, final FSTClazzInfo.FSTFieldInfo[] fieldInfo,
			final Map<String, Object> res) throws Exception {
		int booleanMask = 0;
		int boolcount = 8;
		for (FSTFieldInfo subInfo : fieldInfo) {
			try {
				if (subInfo.isIntegral() && !subInfo.isArray()) {
					final Class<?> subInfoType = subInfo.getType();
					if (subInfoType == boolean.class) {
						if (boolcount == 8) {
							booleanMask = getCodec().readFByte() + 256 & 0xff;
							boolcount = 0;
						}
						boolean val = (booleanMask & 128) != 0;
						booleanMask = booleanMask << 1;
						boolcount++;
						res.put(subInfo.getName(), val);
					}
					if (subInfoType == byte.class) {
						res.put(subInfo.getName(), getCodec().readFByte());
					} else if (subInfoType == char.class) {
						res.put(subInfo.getName(), getCodec().readFChar());
					} else if (subInfoType == short.class) {
						res.put(subInfo.getName(), getCodec().readFShort());
					} else if (subInfoType == int.class) {
						res.put(subInfo.getName(), getCodec().readFInt());
					} else if (subInfoType == double.class) {
						res.put(subInfo.getName(), getCodec().readFDouble());
					} else if (subInfoType == float.class) {
						res.put(subInfo.getName(), getCodec().readFFloat());
					} else if (subInfoType == long.class) { res.put(subInfo.getName(), getCodec().readFLong()); }
				} else {
					// object
					Object subObject = readObjectWithHeader(subInfo);
					res.put(subInfo.getName(), subObject);
				}
			} catch (IllegalAccessException ex) {
				throw new IOException(ex);
			}
		}
	}

	/**
	 * Read string UTF.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	public String readStringUTF() throws IOException {
		return getCodec().readStringUTF();
	}

	/**
	 * len < 127 !!!!!
	 *
	 * @return
	 * @throws IOException
	 */
	public String readStringAsc() throws IOException {
		return getCodec().readStringAsc();
	}

	/**
	 * Read array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param pos
	 *            the pos
	 * @return the object
	 * @throws Exception
	 *             the exception
	 * @date 29 sept. 2023
	 */
	protected Object readArray(final FSTClazzInfo.FSTFieldInfo referencee, int pos) throws Exception {
		Object classOrArray = getCodec().readArrayHeader();
		if (pos < 0) { pos = getCodec().getInputPos(); }
		if (!(classOrArray instanceof Class)) return classOrArray;
		Object o = readArrayNoHeader(referencee, pos, (Class<?>) classOrArray);
		getCodec().readArrayEnd(null);
		return o;
	}

	/**
	 * Read array no header.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param referencee
	 *            the referencee
	 * @param pos
	 *            the pos
	 * @param arrCl
	 *            the arr cl
	 * @return the object
	 * @throws Exception
	 *             the exception
	 * @date 29 sept. 2023
	 */
	protected Object readArrayNoHeader(final FSTClazzInfo.FSTFieldInfo referencee, final int pos, final Class<?> arrCl)
			throws Exception {
		final int len = getCodec().readFInt();
		if (len == -1) return null;
		Class<?> arrType = arrCl.getComponentType();
		if (arrType.isArray()) { // multidim array
			Object array[] = (Object[]) Array.newInstance(arrType, len);
			if (!referencee.isFlat()) { objects.registerObjectForRead(array, pos); }
			FSTClazzInfo.FSTFieldInfo ref1 = new FSTClazzInfo.FSTFieldInfo(referencee.getPossibleClasses(), null,
					clInfoRegistry.isIgnoreAnnotations());
			for (int i = 0; i < len; i++) {
				Object subArray = readArray(ref1, -1);
				array[i] = subArray;
			}
			return array;
		}
		Object array = Array.newInstance(arrType, len);
		if (!referencee.isFlat()) { objects.registerObjectForRead(array, pos); }
		if (arrType.isPrimitive()) return getCodec().readFPrimitiveArray(array, arrType, len);
		Object arr[] = (Object[]) array;
		for (int i = 0; i < len; i++) {
			Object value = readObjectWithHeader(referencee);
			value = getCodec().coerceElement(arrType, value);
			arr[i] = value;
		}
		getCodec().readObjectEnd();
		return array;
	}

	/**
	 * Register object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param o
	 *            the o
	 * @param streamPosition
	 *            the stream position
	 * @param info
	 *            the info
	 * @param referencee
	 *            the referencee
	 * @date 29 sept. 2023
	 */
	public void registerObject(final Object o, final int streamPosition, final FSTClazzInfo info,
			final FSTClazzInfo.FSTFieldInfo referencee) {
		if (!objects.disabled && !referencee.isFlat() && (info == null || !info.isFlat())) {
			objects.registerObjectForRead(o, streamPosition);
		}
	}

	/**
	 * Read class.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the FST clazz info
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @date 29 sept. 2023
	 */
	public FSTClazzInfo readClass() throws IOException, ClassNotFoundException {
		return getCodec().readClass();
	}

	/**
	 * Reset and clear refs.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 sept. 2023
	 */
	protected void resetAndClearRefs() {
		try {
			reset();
			objects.clearForRead(conf);
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
	}

	/**
	 * Reset.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	public void reset() throws IOException {
		getCodec().reset();
	}

	/**
	 * Reset for reuse.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param in
	 *            the in
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	public void resetForReuse(final InputStream in) throws IOException {
		if (closed) throw new RuntimeException("can't reuse closed stream");
		getCodec().reset();
		getCodec().setInputStream(in);
		objects.clearForRead(conf);
		callbacks = null; // fix memory leak on reuse from default FstConfiguration
	}

	/**
	 * Reset for reuse copy array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param bytes
	 *            the bytes
	 * @param off
	 *            the off
	 * @param len
	 *            the len
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	public void resetForReuseCopyArray(final byte bytes[], final int off, final int len) throws IOException {
		if (closed) throw new RuntimeException("can't reuse closed stream");
		getCodec().reset();
		objects.clearForRead(conf);
		getCodec().resetToCopyOf(bytes, off, len);
		callbacks = null; // fix memory leak on reuse from default FstConfiguration
	}

	/**
	 * Reset for reuse use array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param bytes
	 *            the bytes
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	public void resetForReuseUseArray(final byte bytes[]) throws IOException {
		resetForReuseUseArray(bytes, bytes.length);
	}

	/**
	 * Reset for reuse use array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param bytes
	 *            the bytes
	 * @param len
	 *            the len
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	public void resetForReuseUseArray(final byte bytes[], final int len) throws IOException {
		if (closed) throw new RuntimeException("can't reuse closed stream");
		objects.clearForRead(conf);
		getCodec().resetWith(bytes, len);
		callbacks = null; // fix memory leak on reuse from default FstConfiguration
	}

	/**
	 * Read F int.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the int
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	public final int readFInt() throws IOException {
		return getCodec().readFInt();
	}

	/** The closed. */
	protected boolean closed = false;

	@Override
	public void close() throws IOException {
		closed = true;
		resetAndClearRefs();
		conf.returnObject(objects);
		getCodec().close();
	}

	////////////////////////////////////////////////////// epic compatibility hack
	////////////////////////////////////////////////////// /////////////////////////////////////////////////////////

	/** The fake wrapper. */
	protected MyObjectStream fakeWrapper; // some jdk classes hash for ObjectStream, so provide the same instance always

	/**
	 * Gets the object input stream.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param cl
	 *            the cl
	 * @param clInfo
	 *            the cl info
	 * @param referencee
	 *            the referencee
	 * @param toRead
	 *            the to read
	 * @return the object input stream
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	protected ObjectInputStream getObjectInputStream(final Class<?> cl, final FSTClazzInfo clInfo,
			final FSTClazzInfo.FSTFieldInfo referencee, final Object toRead) throws IOException {
		ObjectInputStream wrapped = new ObjectInputStream() {
			@Override
			public Object readObjectOverride() throws IOException, ClassNotFoundException {
				try {
					byte b = FSTObjectInput.this.readByte();
					if (b != FSTObjectOutput.SPECIAL_COMPATIBILITY_OBJECT_TAG) {
						Constructor<?>[] constructors = OptionalDataException.class.getDeclaredConstructors();
						FSTObjectInput.this.pushBack(1);
						for (Constructor<?> constructor : constructors) {
							Class[] typeParameters = constructor.getParameterTypes();
							if (typeParameters != null && typeParameters.length == 1
									&& typeParameters[0] == int.class) {
								constructor.setAccessible(true);
								OptionalDataException ode;
								try {
									ode = (OptionalDataException) constructor.newInstance(0);
									throw ode;
								} catch (InvocationTargetException e) {
									break;
								}
							}
						}
						throw new EOFException("if your code relies on this, think");
					}
					return FSTObjectInput.this.readObjectInternal(referencee.getPossibleClasses());
				} catch (IllegalAccessException | InstantiationException e) {
					throw new IOException(e);
				}
			}

			@Override
			public Object readUnshared() throws IOException, ClassNotFoundException {
				try {
					return FSTObjectInput.this.readObjectInternal(referencee.getPossibleClasses()); // fixme
				} catch (IllegalAccessException | InstantiationException e) {
					throw new IOException(e);
				}
			}

			@Override
			public void defaultReadObject() throws IOException, ClassNotFoundException {
				try {
					int tag = readByte();
					if (tag == 77) // came from writeFields
					{
						fieldMap = (HashMap<String, Object>) FSTObjectInput.this.readObjectInternal(HashMap.class);
						// object has been written with writeFields, is no read with defaultReadObjects,
						// need to autoapply map to object vars.
						// this might be redundant in case readObject() pulls a getFields() .. (see bitset testcase)
						for (String key : fieldMap.keySet()) {
							FSTClazzInfo.FSTFieldInfo fieldInfo = clInfo.getFieldInfo(key, null);// in case fieldName is
																									// not unique =>
																									// cannot
																									// recover/fix
							if (fieldInfo != null) { fieldInfo.setObjectValue(toRead, fieldMap.get(key)); }
						}
					} else {
						FSTObjectInput.this.readObjectFields(referencee, clInfo,
								clInfo.getCompInfo().get(cl).getFieldArray(), toRead, 0, 0); // FIXME: only fields of
																								// current class
					}
				} catch (Exception e) {
					throw new IOException(e);
				}
			}

			HashMap<String, Object> fieldMap;

			@Override
			public GetField readFields() throws IOException, ClassNotFoundException {
				int tag = readByte();
				try {
					FSTClazzInfo.FSTCompatibilityInfo fstCompatibilityInfo = clInfo.getCompInfo().get(cl);
					if (tag == 99 || tag == 66) { // came from defaultwriteobject
						// Note: in case number and names of instance fields of reader/writer are different,
						// this fails as code below implicitely assumes, fields of writer == fields of reader
						// unfortunately one can use defaultWriteObject at writer side but use getFields at reader side
						// in readObject(). if then fields differ, code below reads BS and fails.
						// Its impossible to fix that except by always using putField + getField for
						// JDK compatibility classes, however this will waste lots of performance. As
						// it would be necessary to *always* write full metainformation (a map of fieldName => value
						// pairs)
						// see #53
						fieldMap = new HashMap<>();
						FSTObjectInput.this.readCompatibleObjectFields(referencee, clInfo,
								fstCompatibilityInfo.getFieldArray(), fieldMap);
						getCodec().readVersionTag(); // consume dummy version tag as created by defaultWriteObject
					} else {
						fieldMap = (HashMap<String, Object>) FSTObjectInput.this.readObjectInternal(HashMap.class);
					}
				} catch (Exception e) {
					FSTUtil.<RuntimeException> rethrow(e);
				}
				return new GetField() {
					@Override
					public ObjectStreamClass getObjectStreamClass() { return ObjectStreamClass.lookup(cl); }

					@Override
					public boolean defaulted(final String name) throws IOException {
						return fieldMap.get(name) == null;
					}

					@Override
					public boolean get(final String name, final boolean val) throws IOException {
						if (fieldMap.get(name) == null) return val;
						return ((Boolean) fieldMap.get(name)).booleanValue();
					}

					@Override
					public byte get(final String name, final byte val) throws IOException {
						if (fieldMap.get(name) == null) return val;
						return ((Byte) fieldMap.get(name)).byteValue();
					}

					@Override
					public char get(final String name, final char val) throws IOException {
						if (fieldMap.get(name) == null) return val;
						return ((Character) fieldMap.get(name)).charValue();
					}

					@Override
					public short get(final String name, final short val) throws IOException {
						if (fieldMap.get(name) == null) return val;
						return ((Short) fieldMap.get(name)).shortValue();
					}

					@Override
					public int get(final String name, final int val) throws IOException {
						if (fieldMap.get(name) == null) return val;
						return ((Integer) fieldMap.get(name)).intValue();
					}

					@Override
					public long get(final String name, final long val) throws IOException {
						if (fieldMap.get(name) == null) return val;
						return ((Long) fieldMap.get(name)).longValue();
					}

					@Override
					public float get(final String name, final float val) throws IOException {
						if (fieldMap.get(name) == null) return val;
						return ((Float) fieldMap.get(name)).floatValue();
					}

					@Override
					public double get(final String name, final double val) throws IOException {
						if (fieldMap.get(name) == null) return val;
						return ((Double) fieldMap.get(name)).doubleValue();
					}

					@Override
					public Object get(final String name, final Object val) throws IOException {
						Object res = fieldMap.get(name);
						if (res == null) return val;
						return res;
					}
				};
			}

			@Override
			public void registerValidation(final ObjectInputValidation obj, final int prio)
					throws NotActiveException, InvalidObjectException {
				if (callbacks == null) { callbacks = new ArrayList<>(); }
				callbacks.add(new CallbackEntry(obj, prio));
			}

			@Override
			public int read() throws IOException {
				return getCodec().readFByte();
			}

			@Override
			public int read(final byte[] buf, final int off, final int len) throws IOException {
				return FSTObjectInput.this.read(buf, off, len);
			}

			@Override
			public int available() throws IOException {
				return FSTObjectInput.this.available();
			}

			@Override
			public void close() throws IOException {}

			@Override
			public boolean readBoolean() throws IOException {
				return FSTObjectInput.this.readBoolean();
			}

			@Override
			public byte readByte() throws IOException {
				return getCodec().readFByte();
			}

			@Override
			public int readUnsignedByte() throws IOException {
				return FSTObjectInput.this.readUnsignedByte();
			}

			@Override
			public char readChar() throws IOException {
				return getCodec().readFChar();
			}

			@Override
			public short readShort() throws IOException {
				return getCodec().readFShort();
			}

			@Override
			public int readUnsignedShort() throws IOException {
				return FSTObjectInput.this.readUnsignedShort();
			}

			@Override
			public int readInt() throws IOException {
				return getCodec().readFInt();
			}

			@Override
			public long readLong() throws IOException {
				return getCodec().readFLong();
			}

			@Override
			public float readFloat() throws IOException {
				return getCodec().readFFloat();
			}

			@Override
			public double readDouble() throws IOException {
				return getCodec().readFDouble();
			}

			@Override
			public void readFully(final byte[] buf) throws IOException {
				FSTObjectInput.this.readFully(buf);
			}

			@Override
			public void readFully(final byte[] buf, final int off, final int len) throws IOException {
				FSTObjectInput.this.readFully(buf, off, len);
			}

			@Override
			public int skipBytes(final int len) throws IOException {
				return FSTObjectInput.this.skipBytes(len);
			}

			@Override
			public String readUTF() throws IOException {
				return getCodec().readStringUTF();
			}

			@Override
			public String readLine() throws IOException {
				return FSTObjectInput.this.readLine();
			}

			@Override
			public int read(final byte[] b) throws IOException {
				return FSTObjectInput.this.read(b);
			}

			@Override
			public long skip(final long n) throws IOException {
				return FSTObjectInput.this.skip(n);
			}

			@Override
			public void mark(final int readlimit) {
				throw new RuntimeException("not implemented");
			}

			@Override
			public void reset() throws IOException {
				FSTObjectInput.this.reset();
			}

			@Override
			public boolean markSupported() {
				return false;
			}
		};
		if (fakeWrapper == null) { fakeWrapper = new MyObjectStream(); }
		fakeWrapper.push(wrapped);
		return fakeWrapper;
	}

	/**
	 * Push back.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param i
	 *            the i
	 * @date 29 sept. 2023
	 */
	protected void pushBack(final int i) {
		getCodec().pushBack(i);
	}

	/**
	 * The Class MyObjectStream.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 sept. 2023
	 */
	protected static class MyObjectStream extends ObjectInputStream {

		/** The wrapped. */
		ObjectInputStream wrapped;

		/** The wrapped stack. */
		ArrayDeque<ObjectInputStream> wrappedStack = new ArrayDeque<>();

		/**
		 * Push.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param in
		 *            the in
		 * @date 29 sept. 2023
		 */
		public void push(final ObjectInputStream in) {
			wrappedStack.push(in);
			wrapped = in;
		}

		/**
		 * Pop.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @date 29 sept. 2023
		 */
		public void pop() {
			wrapped = wrappedStack.pop();
		}

		/**
		 * Instantiates a new my object stream.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 * @throws SecurityException
		 *             the security exception
		 * @date 29 sept. 2023
		 */
		MyObjectStream() throws IOException, SecurityException {}

		@Override
		public Object readObjectOverride() throws IOException, ClassNotFoundException {
			return wrapped.readObject();
		}

		@Override
		public Object readUnshared() throws IOException, ClassNotFoundException {
			return wrapped.readUnshared();
		}

		@Override
		public void defaultReadObject() throws IOException, ClassNotFoundException {
			wrapped.defaultReadObject();
		}

		@Override
		public ObjectInputStream.GetField readFields() throws IOException, ClassNotFoundException {
			return wrapped.readFields();
		}

		@Override
		public void registerValidation(final ObjectInputValidation obj, final int prio)
				throws NotActiveException, InvalidObjectException {
			wrapped.registerValidation(obj, prio);
		}

		@Override
		public int read() throws IOException {
			return wrapped.read();
		}

		@Override
		public int read(final byte[] buf, final int off, final int len) throws IOException {
			return wrapped.read(buf, off, len);
		}

		@Override
		public int available() throws IOException {
			return wrapped.available();
		}

		@Override
		public void close() throws IOException {
			wrapped.close();
		}

		@Override
		public boolean readBoolean() throws IOException {
			return wrapped.readBoolean();
		}

		@Override
		public byte readByte() throws IOException {
			return wrapped.readByte();
		}

		@Override
		public int readUnsignedByte() throws IOException {
			return wrapped.readUnsignedByte();
		}

		@Override
		public char readChar() throws IOException {
			return wrapped.readChar();
		}

		@Override
		public short readShort() throws IOException {
			return wrapped.readShort();
		}

		@Override
		public int readUnsignedShort() throws IOException {
			return wrapped.readUnsignedShort();
		}

		@Override
		public int readInt() throws IOException {
			return wrapped.readInt();
		}

		@Override
		public long readLong() throws IOException {
			return wrapped.readLong();
		}

		@Override
		public float readFloat() throws IOException {
			return wrapped.readFloat();
		}

		@Override
		public double readDouble() throws IOException {
			return wrapped.readDouble();
		}

		@Override
		public void readFully(final byte[] buf) throws IOException {
			wrapped.readFully(buf);
		}

		@Override
		public void readFully(final byte[] buf, final int off, final int len) throws IOException {
			wrapped.readFully(buf, off, len);
		}

		@Override
		public int skipBytes(final int len) throws IOException {
			return wrapped.skipBytes(len);
		}

		@Override
		public String readUTF() throws IOException {
			return wrapped.readUTF();
		}

		@Override
		public String readLine() throws IOException {
			return wrapped.readLine();
		}

		@Override
		public int read(final byte[] b) throws IOException {
			return wrapped.read(b);
		}

		@Override
		public long skip(final long n) throws IOException {
			return wrapped.skip(n);
		}

		@Override
		public void mark(final int readlimit) {
			wrapped.mark(readlimit);
		}

		@Override
		public void reset() throws IOException {
			wrapped.reset();
		}

		@Override
		public boolean markSupported() {
			return wrapped.markSupported();
		}
	}

}
