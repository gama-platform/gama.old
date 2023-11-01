/*******************************************************************************************************
 *
 * FSTJsonEncoder.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.coders;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Map;

import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTEncoder;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.util.FSTOutputStream;
import org.nustaq.serialization.util.FSTUtil;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.io.SerializedString;

/**
 * Created by ruedi on 20/05/15.
 *
 */
public class FSTJsonEncoder implements FSTEncoder {

	/** The fac. */
	JsonFactory fac;

	/** The conf. */
	FSTConfiguration conf;

	/** The field names. */
	FSTJsonFieldNames fieldNames;

	/** The gen. */
	protected JsonGenerator gen;

	/** The out. */
	FSTOutputStream out;

	/**
	 * Instantiates a new FST json encoder.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @date 1 nov. 2023
	 */
	public FSTJsonEncoder(final FSTConfiguration conf) {
		fac = conf.getCoderSpecific();
		setConf(conf);
	}

	@Override
	public void setConf(final FSTConfiguration conf) {
		this.conf = conf;
		fieldNames = conf.getJsonFieldNames();
	}

	@Override
	public void writeRawBytes(final byte[] bufferedName, final int off, final int length) throws IOException {
		gen.writeBinary(bufferedName, off, length);
	}

	@Override
	public void writePrimitiveArray(final Object array, final int start, final int length) throws IOException {
		gen.writeStartArray();
		Class<?> componentType = array.getClass().getComponentType();
		if (componentType == int.class) { // fast path for int
			int arr[] = (int[]) array;
			for (int i = 0; i < length; i++) { gen.writeNumber(arr[i]); }
			gen.writeEndArray();
			return;
		}
		gen.writeString(componentType.getSimpleName());
		if (array instanceof boolean[]) {
			boolean arr[] = (boolean[]) array;
			for (int i = 0; i < length; i++) { gen.writeBoolean(arr[i]); }
		} else if (array instanceof long[]) {
			long arr[] = (long[]) array;
			for (int i = 0; i < length; i++) { gen.writeNumber(arr[i]); }
		} else if (array instanceof double[]) {
			double arr[] = (double[]) array;
			for (int i = 0; i < length; i++) { gen.writeNumber(arr[i]); }
		} else if (array instanceof char[]) {
			char arr[] = (char[]) array;
			for (int i = 0; i < length; i++) { gen.writeNumber(arr[i]); }
		} else {
			for (int i = 0; i < length; i++) {
				Number num = (Number) Array.get(array, start + i);
				if (num instanceof Float || num instanceof Double) {
					gen.writeNumber(num.doubleValue());
				} else {
					gen.writeNumber(num.longValue());
				}
			}
		}
		gen.writeEndArray();
	}

	@Override
	public void writeStringUTF(final String str) throws IOException {
		gen.writeString(str);
	}

	@Override
	public void writeFShort(final short c) throws IOException {
		gen.writeNumber(c);
	}

	@Override
	public void writeFChar(final char c) throws IOException {
		gen.writeNumber(c);
	}

	@Override
	public void writeFByte(final int v) throws IOException {
		gen.writeNumber(v);
	}

	@Override
	public void writeFInt(final int anInt) throws IOException {
		gen.writeNumber(anInt);
	}

	@Override
	public void writeFLong(final long anInt) throws IOException {
		gen.writeNumber(anInt);
	}

	@Override
	public void writeFFloat(final float value) throws IOException {
		gen.writeNumber(value);
	}

	@Override
	public void writeFDouble(final double value) throws IOException {
		gen.writeNumber(value);
	}

	@Override
	public int getWritten() {
		// try {
		// gen.flush();
		// } catch (IOException e) {
		// FSTUtil.<RuntimeException>rethrow(e);
		// }
		// System.out.println(pos+" "+out.pos);
		return out.pos - out.getOff() + ((FSTConfiguration.JacksonAccessWorkaround) gen).getOutputTail();
	}

	@Override
	public void skip(final int i) {
		throw new RuntimeException("not supported");
	}

	@Override
	public void close() throws IOException {
		gen.close();
		out.close();
	}

	@Override
	public void reset(final byte[] outbytes) {
		if (gen != null) {
			try {
				createGenerator();
			} catch (Exception e) {
				FSTUtil.<RuntimeException> rethrow(e);
			}
		}
		if (outbytes == null) {
			out.reset();
		} else {
			out.reset(outbytes);
		}
	}

	@Override
	public void flush() throws IOException {
		gen.flush();
		out.flush();
	}

	@Override
	public void writeInt32At(final int position, final int v) {
		throw new RuntimeException("not supported");
	}

	@Override
	public void setOutstream(final OutputStream outstream) {
		out = new FSTOutputStream(outstream);
		try {
			createGenerator();
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
	}

	/**
	 * Creates the generator.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 1 nov. 2023
	 */
	public void createGenerator() throws IOException {
		if (gen != null) { gen.close(); }
		gen = fac.createGenerator(out);
	}

	@Override
	public void ensureFree(final int bytes) throws IOException {
		out.ensureFree(bytes);
	}

	@Override
	public byte[] getBuffer() {
		try {
			gen.flush();
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
		return out.getBuf();
	}

	@Override
	public void registerClass(final Class possible) {

	}

	@Override
	public void writeClass(final Class cl) {
		// already written in write tag
	}

	@Override
	public void writeClass(final FSTClazzInfo clInf) {
		// already written in write tag
	}

	@Override
	public boolean writeTag(final byte tag, final Object infoOrObject, final long somValue, final Object toWrite,
			final FSTObjectOutput oout) throws IOException {
		switch (tag) {
			case FSTObjectOutput.HANDLE:
				gen.writeStartObject();
				gen.writeFieldName(fieldNames.REF_S);
				gen.writeNumber(somValue);
				gen.writeEndObject();
				return true;
			case FSTObjectOutput.NULL:
				gen.writeNull();
				return true;
			case FSTObjectOutput.TYPED:
			case FSTObjectOutput.OBJECT:

				if (toWrite instanceof Unknown) {
					writeUnkown((Unknown) toWrite, oout);
					return true;
				}

				FSTClazzInfo clzInfo = (FSTClazzInfo) infoOrObject;
				if (clzInfo.useCompatibleMode() && clzInfo.getSer() == null)
					throw new RuntimeException("Unsupported backward compatibility mode for class '"
							+ clzInfo.getClazz().getName() + "'. Pls register a Custom Serializer to fix");

				if ((clzInfo.getClazz() == String.class) || (clzInfo.getClazz() == Double.class) || (clzInfo.getClazz() == Float.class) || (clzInfo.getClazz() == Byte.class)) { break; }
				if (clzInfo.getClazz() == Short.class) { break; }
				if (clzInfo.getClazz() == Integer.class) { break; }
				if (clzInfo.getClazz() == Long.class) { break; }
				if (clzInfo.getClazz() == Character.class) { break; }
				if (clzInfo.getClazz() == Boolean.class) { break; }
				if (clzInfo.getSer() != null || clzInfo.isExternalizable()) {
					gen.writeStartObject();
					gen.writeFieldName(fieldNames.TYPE_S);
					writeSymbolicClazz(clzInfo, clzInfo.getClazz());
					gen.writeFieldName(fieldNames.OBJ_S);
					gen.writeStartArray();
				} else {
					gen.writeStartObject();
					gen.writeFieldName(fieldNames.TYPE_S);
					writeSymbolicClazz(clzInfo, clzInfo.getClazz());
					gen.writeFieldName(fieldNames.OBJ_S);
					gen.writeStartObject();
				}
				break;
			case FSTObjectOutput.ONE_OF:
				throw new RuntimeException("not implemented");
			case FSTObjectOutput.STRING:
				break; // ignore, header created by calling writeUTF
			case FSTObjectOutput.BIG_BOOLEAN_FALSE:
				gen.writeBoolean(false);
				break; // ignore, header created by writing long. FIXME: won't work
			case FSTObjectOutput.BIG_BOOLEAN_TRUE:
				gen.writeBoolean(true);
				break; // ignore, header created by writing long. FIXME: won't work
			case FSTObjectOutput.BIG_LONG:
				break; // ignore, header implicitely created by writing long.
			case FSTObjectOutput.BIG_INT:
				break;// ignore, header implicitely created by writing int.
			case FSTObjectOutput.ARRAY:
				Class<?> clz = infoOrObject.getClass();
				Class<?> componentType = clz.getComponentType();
				if (clz.isArray() && componentType.isPrimitive()) {
					writePrimitiveArray(infoOrObject, 0, Array.getLength(infoOrObject));
					return true;
				}
				gen.writeStartObject();
				gen.writeFieldName(fieldNames.SEQ_TYPE_S);
				writeSymbolicClazz(null, clz);
				gen.writeFieldName(fieldNames.SEQ_S);
				gen.writeStartArray();
				break;
			case FSTObjectOutput.ENUM:
				boolean isEnumClass = toWrite.getClass().isEnum();
				Class c = toWrite.getClass();
				if (!isEnumClass) {
					// weird stuff ..
					while (c != null && !c.isEnum()) { c = toWrite.getClass().getSuperclass(); }
					if (c == null) throw new RuntimeException("Can't handle this enum: " + toWrite.getClass());
				}
				gen.writeStartObject();
				gen.writeFieldName(fieldNames.ENUM_S);
				writeSymbolicClazz(null, c);
				gen.writeFieldName(fieldNames.VAL_S);
				gen.writeString(toWrite.toString());
				gen.writeEndObject();
				return true;
			default:
				throw new RuntimeException("unexpected tag " + tag);
		}
		return false;
	}

	/**
	 * Write unkown.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param toWrite
	 *            the to write
	 * @param oout
	 *            the oout
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 1 nov. 2023
	 */
	private void writeUnkown(final Unknown toWrite, final FSTObjectOutput oout) throws IOException {
		gen.writeStartObject();
		gen.writeFieldName(fieldNames.TYPE_S);
		gen.writeString(toWrite.getType());
		gen.writeFieldName(fieldNames.OBJ_S);
		if (toWrite.isSequence()) {
			gen.writeStartArray();
			for (Object o : toWrite.getItems()) { oout.writeObject(o); }
			gen.writeEndArray();
		} else {
			gen.writeStartObject();
			for (Map.Entry<String, Object> stringObjectEntry : toWrite.getFields().entrySet()) {
				gen.writeFieldName(stringObjectEntry.getKey());
				oout.writeObject(stringObjectEntry.getValue());
			}
			gen.writeEndObject();
		}
		gen.writeEndObject();
	}

	/**
	 * Write symbolic clazz.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param clzInfo
	 *            the clz info
	 * @param clz
	 *            the clz
	 * @date 1 nov. 2023
	 */
	private void writeSymbolicClazz(final FSTClazzInfo clzInfo, final Class<?> clz) {
		try {
			if (clzInfo != null) {
				SerializedString buffered = (SerializedString) clzInfo.getDecoderAttached();
				if (buffered == null) {
					buffered = new SerializedString(classToString(clz));
					clzInfo.setDecoderAttached(buffered);
				}
				gen.writeString(buffered);
			} else {
				gen.writeString(classToString(clz));
			}
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
	}

	/**
	 * Class to string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param clz
	 *            the clz
	 * @return the string
	 * @date 1 nov. 2023
	 */
	protected String classToString(final Class clz) {
		return conf.getCPNameForClass(clz);
	}

	@Override
	public boolean writeAttributeName(final FSTClazzInfo.FSTFieldInfo subInfo, final Object outerObjectToWrite) {
		try {
			SerializedString bufferedName = (SerializedString) subInfo.getBufferedName();
			if (bufferedName == null) {
				bufferedName = new SerializedString(subInfo.getName());
				subInfo.setBufferedName(bufferedName);
			}
			if (gen.getOutputContext().inArray()) {
				gen.writeString(bufferedName);
			} else {
				gen.writeFieldName(bufferedName);
			}

		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
		return false;
	}

	@Override
	public void externalEnd(final FSTClazzInfo clz) {
		try {
			Class clazz = clz.getClazz();
			if (clazz == Byte.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class
					|| clazz == Float.class || clazz == Double.class || clazz == Character.class
					|| clazz == Boolean.class)
				return;
			if (gen.getOutputContext().inArray()) { gen.writeEndArray(); }
			if (gen.getOutputContext().inObject()) { gen.writeEndObject(); }
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
	}

	@Override
	public boolean isWritingAttributes() { return true; }

	@Override
	public boolean isPrimitiveArray(final Object array, final Class<?> componentType) {
		return componentType.isPrimitive() && !(array instanceof double[]) && !(array instanceof float[]);
	}

	@Override
	public boolean isTagMultiDimSubArrays() { return true; }

	@Override
	public void writeVersionTag(final int version) throws IOException {
		// versioning not supported for minbin
	}

	@Override
	public boolean isByteArrayBased() { return true; }

	@Override
	public void writeArrayEnd() {
		try {
			if (gen.getOutputContext().inArray()) { gen.writeEndArray(); }
			if (gen.getOutputContext().inObject()) { gen.writeEndObject(); }
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
	}

	@Override
	public void writeFieldsEnd(final FSTClazzInfo serializationInfo) {
		try {
			JsonStreamContext outputContext = gen.getOutputContext();
			if (outputContext.inObject()) {
				gen.writeEndObject();
			} else {
				gen.writeEndArray();
			}
			if (outputContext.inObject()) { gen.writeEndObject(); }
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
			try {
				gen.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println(new String(out.buf, 0, out.pos));
		}
	}

	@Override
	public FSTConfiguration getConf() { return conf; }

}
