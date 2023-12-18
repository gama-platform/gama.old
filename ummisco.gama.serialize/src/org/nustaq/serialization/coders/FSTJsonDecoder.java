/*******************************************************************************************************
 *
 * FSTJsonDecoder.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.coders;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTDecoder;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.util.FSTInputStream;
import org.nustaq.serialization.util.FSTUtil;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;

/**
 * Created by moelrue on 5/21/15.
 */
public class FSTJsonDecoder implements FSTDecoder {

	/** The conf. */
	protected FSTConfiguration conf;

	/** The field names. */
	protected FSTJsonFieldNames fieldNames;

	/** The input. */
	protected JsonParser input;

	/** The fac. */
	protected JsonFactory fac;

	/** The fst input. */
	protected FSTInputStream fstInput;

	/** The unknown fallback read field name. */
	protected String unknownFallbackReadFieldName; // contains read fieldName in case of Unknown resulting from plain
													// JSon structure

	/** The clz cache. */
	protected HashMap<String, Class> clzCache = new HashMap<>(31);

	/** The last unknown. */
	protected String lastUnknown;

	/** The unknown nest level. */
	protected int unknownNestLevel;

	/**
	 * Instantiates a new FST json decoder.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @date 6 déc. 2023
	 */
	public FSTJsonDecoder(final FSTConfiguration conf) {
		fac = conf.getCoderSpecific();
		setConf(conf);
	}

	@Override
	public void setConf(final FSTConfiguration conf) {
		this.conf = conf;
		fieldNames = conf.getJsonFieldNames();
	}

	@Override
	public String readStringUTF() throws IOException {
		if (unknownFallbackReadFieldName != null) {
			String unkReadAhead = unknownFallbackReadFieldName;
			unknownFallbackReadFieldName = null;
			return unkReadAhead;
		}
		JsonToken jsonToken = input.nextToken();
		if (jsonToken == JsonToken.VALUE_NULL) return null;
		if (jsonToken == JsonToken.FIELD_NAME) return input.getCurrentName();
		return input.getText();
	}

	@Override
	public String readStringAsc() throws IOException {
		return input.nextTextValue();
	}

	@Override
	/**
	 * if array is null => create own array. if len == -1 => use len read
	 */
	public Object readFPrimitiveArray(final Object array, final Class componentType, final int len) {
		try {
			if (componentType == int.class) {
				int[] da = (int[]) array;
				for (int i = 0; i < da.length; i++) {
					input.nextToken();
					da[i] = input.getIntValue();
				}
				return da;
			}
			if (componentType == long.class) {
				long[] da = (long[]) array;
				for (int i = 0; i < da.length; i++) {
					input.nextToken();
					da[i] = input.getLongValue();
				}
				return da;
			} else if (componentType == double.class) {
				double[] da = (double[]) array;
				for (int i = 0; i < da.length; i++) {
					input.nextToken();
					da[i] = input.getDoubleValue();
				}
				return da;
			} else if (componentType == float.class) {
				float[] da = (float[]) array;
				for (int i = 0; i < da.length; i++) {
					input.nextToken();
					da[i] = input.getFloatValue();
				}
				return da;
			} else if (componentType == boolean.class) {
				boolean[] da = (boolean[]) array;
				for (int i = 0; i < da.length; i++) {
					input.nextToken();
					da[i] = input.getBooleanValue();
				}
				return da;
			} else if (componentType == byte.class) {
				byte[] da = (byte[]) array;
				for (int i = 0; i < da.length; i++) {
					input.nextToken();
					da[i] = input.getByteValue();
				}
				return da;
			} else if (componentType == short.class) {
				short[] da = (short[]) array;
				for (int i = 0; i < da.length; i++) {
					input.nextToken();
					da[i] = input.getShortValue();
				}
				return da;
			} else if (componentType == char.class) {
				char[] da = (char[]) array;
				for (int i = 0; i < da.length; i++) {
					input.nextToken();
					da[i] = (char) input.getIntValue();
				}
				return da;
			} else
				throw new RuntimeException("unsupported type " + componentType.getName());
		} catch (Exception e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
		return null;
	}

	@Override
	public void readFIntArr(final int len, final int[] arr) throws IOException {
		JsonToken jsonToken = input.nextToken();
		if (!jsonToken.isStructStart()) throw new RuntimeException("Expected array start");
		for (int i = 0; i < len; i++) {
			input.nextToken();
			arr[i] = input.getIntValue();
		}
	}

	@Override
	public int readFInt() throws IOException {
		return input.nextIntValue(-1);
	}

	@Override
	public double readFDouble() throws IOException {
		input.nextToken();
		return input.getDoubleValue();
	}

	@Override
	public float readFFloat() throws IOException {
		input.nextToken();
		return input.getFloatValue();
	}

	@Override
	public byte readFByte() throws IOException {
		input.nextToken();
		int currentTokenId = input.getCurrentTokenId();
		if (currentTokenId == JsonTokenId.ID_FALSE) return 0;
		if (currentTokenId == JsonTokenId.ID_TRUE) return 1;
		return input.getByteValue();
	}

	@Override
	public int readIntByte() throws IOException {
		input.nextToken();
		return input.getByteValue();
	}

	@Override
	public long readFLong() throws IOException {
		input.nextToken();
		return input.getLongValue();
	}

	@Override
	public char readFChar() throws IOException {
		input.nextToken();
		return (char) input.getIntValue();
	}

	@Override
	public short readFShort() throws IOException {
		input.nextToken();
		return input.getShortValue();
	}

	@Override
	public int readPlainInt() throws IOException {
		throw new RuntimeException("not supported");
	}

	@Override
	public byte[] getBuffer() { return fstInput.buf; }

	@Override
	public int getInputPos() {
		JsonLocation currentLocation = input.getCurrentLocation();
		long byteOffset = currentLocation.getByteOffset();
		if (input.getCurrentToken() == JsonToken.FIELD_NAME) {
			byteOffset -= 2; // eager parsing of jackson ':' + '['/'{'
		}
		return (int) byteOffset;
	}

	@Override
	public void moveTo(final int position) {
		throw new RuntimeException("not supported");
	}

	@Override
	public void setInputStream(final InputStream in) {
		try {
			if (fstInput != null) {
				fstInput.initFromStream(in);
			} else {
				fstInput = new FSTInputStream(in);
			}
			if (in != FSTObjectInput.emptyStream) { createParser(); }
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
	}

	@Override
	public int ensureReadAhead(final int bytes) {
		return 0;
	}

	@Override
	public void reset() {
		fstInput.reset();
		input = null;
		unknownNestLevel = 0;
		firstCall = true;
	}

	@Override
	public void resetToCopyOf(final byte[] bytes, final int off, final int len) {
		if (off != 0) throw new RuntimeException("not supported");
		byte b[] = new byte[len];
		System.arraycopy(bytes, off, b, 0, len);
		if (fstInput == null) {
			fstInput = new FSTInputStream(b);
		} else {
			fstInput.resetForReuse(bytes, len);
		}
		try {
			createParser();
			unknownNestLevel = 0;
			firstCall = true;
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
	}

	/**
	 * Creates the parser.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 6 déc. 2023
	 */
	public void createParser() throws IOException {
		if (input != null) { input.close(); }
		input = fac.createParser(fstInput);
	}

	@Override
	public void resetWith(final byte[] bytes, final int len) {
		if (fstInput == null) {
			fstInput = new FSTInputStream(bytes, 0, len);
		} else {
			fstInput.resetForReuse(bytes, len);
		}
		try {
			createParser();
			unknownNestLevel = 0; // fixme: should delegate to core reset (not now as I don't want to break things)
			firstCall = true;
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
	}

	@Override
	public int getObjectHeaderLen() // len field of last header read (if avaiable)
	{ return lastObjectLen; }

	/** The last object len. */
	int lastObjectLen;

	/** The last direct class. */
	Class lastDirectClass;

	/** The first call. */
	boolean firstCall = true;

	@Override
	public byte readObjectHeaderTag() throws IOException {
		boolean isFirst = firstCall;
		firstCall = false;
		lastObjectLen = -1;
		lastReadDirectObject = null;
		lastDirectClass = null;
		JsonToken jsonToken = input.nextToken();
		if (jsonToken == JsonToken.END_OBJECT) { jsonToken = input.nextToken(); }
		switch (jsonToken) {
			case VALUE_STRING: {
				lastReadDirectObject = input.getValueAsString();
				return FSTObjectOutput.DIRECT_OBJECT;
			}
			case VALUE_TRUE: {
				lastReadDirectObject = Boolean.TRUE;
				return FSTObjectOutput.DIRECT_OBJECT;
			}
			case VALUE_FALSE: {
				lastReadDirectObject = Boolean.FALSE;
				return FSTObjectOutput.DIRECT_OBJECT;
			}
			case VALUE_NUMBER_INT: {
				lastReadDirectObject = input.getNumberValue();
				return FSTObjectOutput.DIRECT_OBJECT;
			}
			case VALUE_NUMBER_FLOAT: {
				lastReadDirectObject = input.getDoubleValue();
				return FSTObjectOutput.DIRECT_OBJECT;
			}
			case START_ARRAY: {
				if (unknownNestLevel > 0 || isFirst) {
					lastReadDirectObject = createUnknownArray();
					return FSTObjectOutput.DIRECT_ARRAY_OBJECT;
				}
				lastReadDirectObject = createPrimitiveArrayFrom(readJSonArr2List(getTmpList()));
				return FSTObjectOutput.DIRECT_ARRAY_OBJECT;
			}
			case VALUE_NULL: {
				lastReadDirectObject = null;
				return FSTObjectOutput.NULL;
			}
		}
		if (jsonToken != JsonToken.START_OBJECT) {
			if (jsonToken == JsonToken.END_ARRAY) { // tweak to get end marker when reading json array with readObject
				lastReadDirectObject = "]";
				return FSTObjectOutput.DIRECT_OBJECT;
			}
			throw new RuntimeException("Expected Object start, got '" + jsonToken + "'");
		}

		String typeTag = input.nextFieldName();
		if (typeTag.equals(fieldNames.TYPE)) {
			// object
			String type = input.nextTextValue();
			String valueTag = input.nextFieldName();
			if (!fieldNames.OBJ.equals(valueTag))
				throw new RuntimeException("expected value attribute for object of type:" + type);
			if (!input.nextToken().isStructStart()) throw new RuntimeException("expected struct start");
			try {
				lastDirectClass = classForName(conf.getClassForCPName(type));
			} catch (ClassNotFoundException e) {
				FSTUtil.<RuntimeException> rethrow(e);
			}
			return FSTObjectOutput.OBJECT;
		}
		if (typeTag.equals(fieldNames.SEQ_TYPE)) {
			// sequence
			String type = input.nextTextValue();
			try {
				lastDirectClass = classForName(conf.getClassForCPName(type));
				String valueTag = input.nextFieldName();
				if (!fieldNames.SEQ.equals(valueTag))
					throw new RuntimeException("expected value attribute for object of type:" + type);
				if (!input.nextToken().isStructStart()) throw new RuntimeException("expected array start");
			} catch (ClassNotFoundException e) {
				FSTUtil.<RuntimeException> rethrow(e);
			}
			return FSTObjectOutput.ARRAY;
		} else if (typeTag.equals(fieldNames.REF))
			return FSTObjectOutput.HANDLE;
		else if (typeTag.equals(fieldNames.ENUM)) {
			try {
				String clName = input.nextTextValue();
				Class aClass = classForName(conf.getClassForCPName(clName));
				String valueTag = input.nextFieldName();
				if (!fieldNames.VAL.equals(valueTag))
					throw new RuntimeException("expected value attribute for enum of type:" + clName);
				String enumString = input.nextTextValue();
				lastReadDirectObject = Enum.valueOf(aClass, enumString);
				input.nextToken(); // object end
			} catch (ClassNotFoundException e) {
				FSTUtil.<RuntimeException> rethrow(e);
			}
			return FSTObjectOutput.DIRECT_OBJECT;
		}
		// fall back to unknown
		lastDirectClass = Unknown.class;
		unknownFallbackReadFieldName = typeTag;
		return FSTObjectOutput.OBJECT;
	}

	/** The tmp list. */
	List tmpList;

	/**
	 * Gets the tmp list.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the tmp list
	 * @date 6 déc. 2023
	 */
	private List getTmpList() {
		if (tmpList == null) {
			tmpList = new ArrayList(32);
		} else {
			tmpList.clear();
		}
		return tmpList;
	}

	/**
	 * Creates the unknown array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the unknown
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 6 déc. 2023
	 */
	protected Unknown createUnknownArray() throws IOException {
		unknownNestLevel++;
		List arrayTokens = new ArrayList(14);
		JsonToken elem = input.nextToken();
		while (!elem.isStructEnd()) {
			if (elem == JsonToken.VALUE_NUMBER_INT) {
				arrayTokens.add(input.getLongValue());
			} else if (elem == JsonToken.VALUE_NUMBER_FLOAT) {
				arrayTokens.add(input.getDoubleValue());
			} else if (elem == JsonToken.VALUE_TRUE) {
				arrayTokens.add(true);
			} else if (elem == JsonToken.VALUE_FALSE) {
				arrayTokens.add(false);
			} else if (elem == JsonToken.VALUE_NULL) {
				arrayTokens.add(null);
			} else if (elem == JsonToken.VALUE_STRING) {
				arrayTokens.add(input.getText());
			} else if (elem == JsonToken.START_OBJECT) {
				arrayTokens.add(readUnknownObject());
			} else if (elem == JsonToken.START_ARRAY) {
				arrayTokens.add(createUnknownArray());
			} else
				throw new RuntimeException("unexpected array content in Unknown array");
			elem = input.nextValue();
		}
		unknownNestLevel--;
		return new Unknown().items(arrayTokens);
	}

	/**
	 * Read unknown object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the unknown
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 6 déc. 2023
	 */
	private Unknown readUnknownObject() throws IOException {
		Unknown unk = new Unknown();
		JsonToken elem = input.nextToken();
		boolean expectField = true;
		String field = null;
		while (!elem.isStructEnd()) {
			if (expectField) {
				field = input.getValueAsString();
				expectField = false;
			} else {
				if (elem == JsonToken.VALUE_NUMBER_INT) {
					unk.set(field, input.getLongValue());
				} else if (elem == JsonToken.VALUE_NUMBER_FLOAT) {
					unk.set(field, input.getDoubleValue());
				} else if (elem == JsonToken.VALUE_TRUE) {
					unk.set(field, true);
				} else if (elem == JsonToken.VALUE_FALSE) {
					unk.set(field, false);
				} else if (elem == JsonToken.VALUE_NULL) {
					unk.set(field, null);
				} else if (elem == JsonToken.VALUE_STRING) {
					unk.set(field, input.getText());
				} else if (elem == JsonToken.START_OBJECT) {
					unk.set(field, readUnknownObject());
				} else if (elem == JsonToken.START_ARRAY) {
					unk.set(field, createUnknownArray());
				} else
					throw new RuntimeException("unexpected array content in Unknown array");
				expectField = true;
			}
			elem = input.nextToken();
		}
		return unk;
	}

	/**
	 * Creates the primitive array from.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param directObject
	 *            the direct object
	 * @return the object
	 * @date 6 déc. 2023
	 */
	private Object createPrimitiveArrayFrom(final List directObject) {
		if (directObject.size() == 0 || !(directObject.get(0) instanceof String)) {
			directObject.add(0, "int"); // fixme:slow
		}
		Class arrT = null;
		arrT = switch ((String) directObject.get(0)) {
			case "boolean" -> boolean.class;
			case "byte" -> byte.class;
			case "char" -> char.class;
			case "short" -> short.class;
			case "int" -> int.class;
			case "long" -> long.class;
			case "float" -> float.class;
			case "double" -> double.class;
			default -> {
				directObject.add(0, "dummy");
				yield String.class;
			}
		};
		Object newObj = Array.newInstance(arrT, directObject.size() - 1);
		for (int i = 0; i < directObject.size() - 1; i++) {
			Object n = directObject.get(i + 1);
			if (arrT == boolean.class) {
				Array.setBoolean(newObj, i, (Boolean) n);
			} else if (arrT == byte.class) {
				Array.setByte(newObj, i, ((Number) n).byteValue());
			} else if (arrT == char.class) {
				Array.setChar(newObj, i, (char) ((Number) n).intValue());
			} else if (arrT == short.class) {
				Array.setShort(newObj, i, ((Number) n).shortValue());
			} else if (arrT == int.class) {
				Array.setInt(newObj, i, ((Number) n).intValue());
			} else if (arrT == long.class) {
				Array.setLong(newObj, i, ((Number) n).longValue());
			} else if (arrT == float.class) {
				Array.setFloat(newObj, i, ((Number) n).floatValue());
			} else if (arrT == double.class) {
				Array.setDouble(newObj, i, ((Number) n).doubleValue());
			} else if (arrT == String.class) {
				Array.set(newObj, i, n);
			} else {
				System.err.println("unexpected primitive array type:" + arrT);
			}
		}
		return newObj;
	}

	/**
	 * Read J son arr 2 list.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param arrayTokens
	 *            the array tokens
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 6 déc. 2023
	 */
	public List readJSonArr2List(final List arrayTokens) throws IOException {
		JsonToken elem = input.nextToken();
		while (!elem.isStructEnd()) {
			if (elem == JsonToken.VALUE_NUMBER_INT) {
				arrayTokens.add(input.getLongValue());
			} else if (elem == JsonToken.VALUE_NUMBER_FLOAT) {
				arrayTokens.add(input.getDoubleValue());
			} else if (elem == JsonToken.VALUE_TRUE) {
				arrayTokens.add(true);
			} else if (elem == JsonToken.VALUE_FALSE) {
				arrayTokens.add(false);
			} else if (elem == JsonToken.VALUE_NULL) {
				arrayTokens.add(null);
			} else {
				arrayTokens.add(input.getText());
			}
			elem = input.nextValue();
		}
		return arrayTokens;
	}

	@Override
	public Object getDirectObject() // in case class already resolves to read object (e.g. mix input)
	{
		Object tmp = lastReadDirectObject;
		lastReadDirectObject = null;
		return tmp;
	}

	/** The last read direct object. */
	Object lastReadDirectObject; // in case readClass already reads full minbin value

	@Override
	public FSTClazzInfo readClass() throws IOException, ClassNotFoundException {
		if (lastDirectClass != null) {
			FSTClazzInfo clInfo = conf.getCLInfoRegistry().getCLInfo(lastDirectClass, conf);
			lastDirectClass = null;
			return clInfo;
		}
		return null;
	}

	@Override
	public Class classForName(final String name) throws ClassNotFoundException {
		Class aClass = clzCache.get(name);
		if (aClass == Unknown.class) { lastUnknown = name; }
		if (aClass != null) return aClass;
		aClass = conf.getClassRegistry().classForName(name, conf);
		if (aClass == Unknown.class) { lastUnknown = name; }
		clzCache.put(name, aClass);
		return aClass;
	}

	@Override
	public void registerClass(final Class possible) {
		// throw new RuntimeException("not implemented");
	}

	@Override
	public void close() {
		// nothing to do (?)
		// throw new RuntimeException("not implemented");
	}

	@Override
	public void skip(final int n) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void readPlainBytes(final byte[] b, final int off, final int len) {
		try {
			for (int i = 0; i < len; i++) {
				input.nextToken();
				b[i + off] = input.getByteValue();
			}
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
	}

	@Override
	public boolean isMapBased() { return true; }

	@Override
	public void consumeEndMarker() { // empty as flawed in minbin impl
	}

	@Override
	public Object readArrayHeader() throws Exception {
		if (lastDirectClass == null) {
			JsonToken jsonToken = input.nextToken();
			String type = null;
			if (jsonToken == JsonToken.START_ARRAY) {
				if (unknownNestLevel > 0)
					return createUnknownArray();
				else
					// direct primitive array [1,2, ..]
					return createPrimitiveArrayFrom(readJSonArr2List(getTmpList()));
			}
			if (jsonToken == JsonToken.VALUE_NULL)
				return null;
			else {
				jsonToken = input.nextToken(); // seqType
				if (fieldNames.TYPE.equals(input.getText())) {
					// object
					type = input.nextTextValue();
					String valueTag = input.nextFieldName();
					if (!fieldNames.OBJ.equals(valueTag))
						throw new RuntimeException("expected value attribute for object of type:" + type);
					return classForName(conf.getClassForCPName(type));
				}
				if (!fieldNames.SEQ_TYPE.equals(input.getText())) {
					System.out.println(">" + input.getCurrentToken() + " " + input.getText());
					input.nextToken();
					System.out.println(">" + input.getCurrentToken() + " " + input.getText());
					input.nextToken();
					System.out.println(">" + input.getCurrentToken() + " " + input.getText());
					input.nextToken();
					System.out.println(">" + input.getCurrentToken() + " " + input.getText());
					throw new RuntimeException("expected seqType");
				}
				jsonToken = input.nextToken(); // seqType : ""
				type = input.getText();
				jsonToken = input.nextToken(); // seq
				jsonToken = input.nextToken(); // seq : [
			}
			return classForName(conf.getClassForCPName(type));
		}
		Class ldc = this.lastDirectClass;
		if (ldc == Unknown.class) { ldc = Object[].class; }
		this.lastDirectClass = null; // marker, only valid once
		return ldc;
	}

	@Override
	public void readExternalEnd() {
		consumeEndMarker();
	}

	@Override
	public boolean isEndMarker(final String s) {
		return s == null || "}".equals(s) || "]".equals(s);
	}

	@Override
	public int readVersionTag() throws IOException {
		return 0; // versioning not supported for json
	}

	@Override
	public void pushBack(final int bytes) {
		// fstInput.psetPos(input.getPos()-bytes);
		throw new RuntimeException("not supported");
	}

	/**
	 * Consume end.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 6 déc. 2023
	 */
	private void consumeEnd() {
		try {
			JsonToken jsonToken = input.nextToken();
			if (jsonToken == null) return;
			if (!jsonToken.isStructEnd()) throw new RuntimeException("end of structure expected found:" + jsonToken
					+ " : value:" + input.getValueAsString() + " fname:'" + input.getCurrentName() + "'");
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
	}

	@Override
	public void readArrayEnd(final FSTClazzInfo clzSerInfo) {
		try {
			JsonToken jsonToken = input.nextToken(); // ]
			if (jsonToken == JsonToken.END_ARRAY) { // && (clzSerInfo == null || clzSerInfo.getSer() == null) ) { //
													// need to read only 1 in case of custom ser
				jsonToken = input.nextToken(); // }
			} else {
				// System.out.println("debug "+clzSerInfo);
			}
			Object dummyDebug = jsonToken;
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
	}

	@Override
	public void readObjectEnd() {
		consumeEnd();
	}

	@Override
	public Object coerceElement(final Class arrType, final Object value) {
		if (arrType == byte[].class && value instanceof String) {
			try {
				return ((String) value).getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (value instanceof Number n) {
			if (arrType == Byte.class) return Byte.valueOf(n.byteValue());
			if (arrType == Short.class)
				return Short.valueOf(n.shortValue());
			else if (arrType == Integer.class)
				return Integer.valueOf(n.intValue());
			else if (arrType == Long.class)
				return Long.valueOf(n.longValue());
			else if (arrType == Double.class)
				return Double.valueOf(n.doubleValue());
			else if (arrType == Float.class)
				return Float.valueOf(n.floatValue());
			else if (arrType == Character.class) return Character.valueOf((char) n.intValue());
		}
		return value;
	}

	@Override
	public int available() {
		fstInput.ensureReadAhead(1);
		return fstInput.available();
	}

	@Override
	public boolean inArray() {
		return input.getParsingContext().inArray();
	}

	@Override
	public void startFieldReading(final Object newObj) {
		if (newObj instanceof Unknown) {
			((Unknown) newObj).setType(lastUnknown);
			lastUnknown = null;
			unknownNestLevel++;
		}
	}

	@Override
	public void endFieldReading(final Object newObj) {
		if (newObj instanceof Unknown) { unknownNestLevel--; }
	}

}
