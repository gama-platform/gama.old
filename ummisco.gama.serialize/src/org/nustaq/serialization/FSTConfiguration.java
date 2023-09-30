/*******************************************************************************************************
 *
 * FSTConfiguration.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.nustaq.serialization.coders.FSTJsonDecoder;
import org.nustaq.serialization.coders.FSTJsonEncoder;
import org.nustaq.serialization.coders.FSTJsonFieldNames;
import org.nustaq.serialization.coders.FSTStreamDecoder;
import org.nustaq.serialization.coders.FSTStreamEncoder;
import org.nustaq.serialization.coders.Unknown;
import org.nustaq.serialization.serializers.FSTArrayListSerializer;
import org.nustaq.serialization.serializers.FSTBigIntegerSerializer;
import org.nustaq.serialization.serializers.FSTBigNumberSerializers;
import org.nustaq.serialization.serializers.FSTBitSetSerializer;
import org.nustaq.serialization.serializers.FSTCPEnumSetSerializer;
import org.nustaq.serialization.serializers.FSTCPThrowableSerializer;
import org.nustaq.serialization.serializers.FSTClassSerializer;
import org.nustaq.serialization.serializers.FSTCollectionSerializer;
import org.nustaq.serialization.serializers.FSTDateSerializer;
import org.nustaq.serialization.serializers.FSTEnumSetSerializer;
import org.nustaq.serialization.serializers.FSTJSonSerializers;
import org.nustaq.serialization.serializers.FSTJSonUnmodifiableCollectionSerializer;
import org.nustaq.serialization.serializers.FSTJSonUnmodifiableMapSerializer;
import org.nustaq.serialization.serializers.FSTMapSerializer;
import org.nustaq.serialization.serializers.FSTStringBufferSerializer;
import org.nustaq.serialization.serializers.FSTStringBuilderSerializer;
import org.nustaq.serialization.serializers.FSTStringSerializer;
import org.nustaq.serialization.serializers.FSTThrowableSerializer;
import org.nustaq.serialization.serializers.FSTTimestampSerializer;
import org.nustaq.serialization.util.DefaultFSTInt2ObjectMapFactory;
import org.nustaq.serialization.util.FSTInputStream;
import org.nustaq.serialization.util.FSTInt2ObjectMapFactory;
import org.nustaq.serialization.util.FSTUtil;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import msi.gama.runtime.IScope;

/**
 * Created with IntelliJ IDEA. User: ruedi Date: 18.11.12 Time: 20:41
 *
 * Holds a serialization configuration/metadata. Reuse this class !!! construction is very expensive. (just keep static
 * instances around or use thread locals)
 *
 */
public class FSTConfiguration {

	/**
	 * The scope. A bit of a hack, in order for serialisers to have access to the current scope when deserialising
	 * objects
	 */
	private IScope scope;

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public IScope getScope() { return scope; }

	/**
	 * Sets the scope.
	 *
	 * @param scope
	 *            the new scope
	 */
	public void setScope(final IScope scope) { this.scope = scope; }

	/**
	 * The Enum ConfType.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	enum ConfType {

		/** The default. */
		DEFAULT,
		/** The unsafe. */
		UNSAFE,
		/** The json. */
		JSON,
		/** The jsonpretty. */
		JSONPRETTY
	}

	/**
	 * if all attempts fail to find a class this guy is asked. Can be used in case e.g. dynamic classes need get
	 * generated.
	 */
	public interface LastResortClassResolver {

		/**
		 * Gets the class.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param clName
		 *            the cl name
		 * @return the class
		 * @date 30 sept. 2023
		 */
		Class<?> getClass(String clName);
	}

	/**
	 * Security: disallow packages/classes upon deserialization
	 */
	public interface ClassSecurityVerifier {
		/**
		 * return false if your application does not allow to deserialize objects of type cl. This can be implemented
		 * using whitelisting/blacklisting whole packages, subpackages, single classes
		 *
		 * Note: this also disallows serialization of forbidden classes. For assymetric use cases register a custom
		 * serializer in order to prevent reading/writing of certain classes.
		 *
		 * @param cl
		 *            - the class being serialized/deserialized
		 * @return
		 */
		boolean allowClassDeserialization(Class<?> cl);
	}

	/** The stream coder factory. */
	StreamCoderFactory streamCoderFactory = new FSTDefaultStreamCoderFactory(this);

	/** The name. */
	String name;

	/** The verifier. */
	ClassSecurityVerifier verifier;

	/** The type. */
	ConfType type = ConfType.DEFAULT;

	/** The serialization info registry. */
	FSTClazzInfoRegistry serializationInfoRegistry = new FSTClazzInfoRegistry();

	/** The cached objects. */
	HashMap<Class, List<SoftReference>> cachedObjects = new HashMap<>(97);

	/** The int to object map factory. */
	FSTInt2ObjectMapFactory intToObjectMapFactory = new DefaultFSTInt2ObjectMapFactory();

	/** The class registry. */
	FSTClazzNameRegistry classRegistry = new FSTClazzNameRegistry(null);

	/** The prefer speed. */
	boolean preferSpeed = false; // hint to prefer speed over size in case, currently ignored.

	/** The share references. */
	boolean shareReferences = true;

	/** The class loader. */
	volatile ClassLoader classLoader = getClass().getClassLoader();

	/** The force serializable. */
	boolean forceSerializable = false; // serialize objects which are not instanceof serializable using default
										// serialization scheme.

	/** The instantiator. */
	FSTClassInstantiator instantiator = new FSTDefaultClassInstantiator();

	/** The coder specific. */
	Object coderSpecific;

	/** The last resort resolver. */
	LastResortClassResolver lastResortResolver;

	/** The force clz init. */
	boolean forceClzInit = false; // always execute default fields init, even if no transients

	/** The json field names. */
	FSTJsonFieldNames jsonFieldNames = new FSTJsonFieldNames("typ", "obj", "styp", "seq", "enum", "val", "ref");

	/**
	 * Gets the json field names.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the json field names
	 * @date 30 sept. 2023
	 */
	public FSTJsonFieldNames getJsonFieldNames() { return jsonFieldNames; }

	/**
	 * Sets the json field names.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param fieldNames
	 *            the new json field names
	 * @date 30 sept. 2023
	 */
	public void setJsonFieldNames(final FSTJsonFieldNames fieldNames) { this.jsonFieldNames = fieldNames; }

	/**
	 * Gets the verifier.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the verifier
	 * @date 30 sept. 2023
	 */
	public ClassSecurityVerifier getVerifier() { return verifier; }

	/**
	 * Sets the verifier.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param verifier
	 *            the verifier
	 * @return the FST configuration
	 * @date 30 sept. 2023
	 */
	public FSTConfiguration setVerifier(final ClassSecurityVerifier verifier) {
		this.verifier = verifier;
		return this;
	}

	/**
	 * Gets the int to object map factory.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the int to object map factory
	 * @date 30 sept. 2023
	 */
	public FSTInt2ObjectMapFactory getIntToObjectMapFactory() { return intToObjectMapFactory; }

	/**
	 * The Class FieldKey.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	// cache fieldinfo. This can be shared with derived FSTConfigurations in order to reduce footprint
	static class FieldKey {

		/** The clazz. */
		Class<?> clazz;

		/** The field name. */
		String fieldName;

		/**
		 * Instantiates a new field key.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param clazz
		 *            the clazz
		 * @param fieldName
		 *            the field name
		 * @date 30 sept. 2023
		 */
		public FieldKey(final Class<?> clazz, final String fieldName) {
			this.clazz = clazz;
			this.fieldName = fieldName;
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			FieldKey fieldKey = (FieldKey) o;

			if (!clazz.equals(fieldKey.clazz)) return false;
			return fieldName.equals(fieldKey.fieldName);

		}

		@Override
		public int hashCode() {
			int result = clazz.hashCode();
			result = 31 * result + fieldName.hashCode();
			return result;
		}
	}

	/** The field info cache. */
	final ConcurrentHashMap<FieldKey, FSTClazzInfo.FSTFieldInfo> fieldInfoCache;

	/**
	 * debug helper
	 *
	 * @return
	 */
	public String getName() { return name; }

	/**
	 * debug helper
	 *
	 * @param name
	 */
	public void setName(final String name) { this.name = name; }

	/////////////////////////////////////
	// cross platform stuff only

	/** The minbin names. */
	// contains symbol => full qualified name
	private final HashMap<String, String> minbinNames = new HashMap<>();

	/** The min bin names bytez. */
	// may contain symbol => cached binary output
	private final HashMap<String, byte[]> minBinNamesBytez = new HashMap<>();

	/** The minbin names reverse. */
	// contains full qualified name => symbol
	private final HashMap<String, String> minbinNamesReverse = new HashMap<>();

	/** The cross platform. */
	private boolean crossPlatform = false; // if true do not support writeObject/readObject etc.

	// end cross platform stuff only
	/////////////////////////////////////

	/**
	 * create a json conf with given attributes. Note that shared refs = true for jason might be not as stable as for
	 * binary encodings as fst relies on stream positions to identify objects within a given input, so any inbetween
	 * formatting will break proper reference resolution.
	 *
	 * WARNING: use of sharedrefs = true is Deprecated as its flakey
	 *
	 * @param prettyPrint
	 * @param shareReferences
	 * @return
	 */
	public static FSTConfiguration createJsonConfiguration(final boolean prettyPrint, final boolean shareReferences) {

		final FSTConfiguration conf = createDefaultConfiguration();
		conf.setCrossPlatform(true);
		// override some serializers
		FSTSerializerRegistry reg = conf.serializationInfoRegistry.getSerializerRegistry();
		reg.putSerializer(EnumSet.class, new FSTCPEnumSetSerializer(), true);
		reg.putSerializer(Throwable.class, new FSTCPThrowableSerializer(), true);
		// for crossplatform fallback does not work => register default serializers for collections and subclasses
		reg.putSerializer(AbstractCollection.class, new FSTCollectionSerializer(), true);
		reg.putSerializer(AbstractMap.class, new FSTMapSerializer(), true); // subclass should register manually
		conf.registerCrossPlatformClassMapping(new String[][] { { "map", HashMap.class.getName() },
				{ "list", ArrayList.class.getName() }, { "set", HashSet.class.getName() },
				{ "long", Long.class.getName() }, { "integer", Integer.class.getName() },
				{ "short", Short.class.getName() }, { "byte", Byte.class.getName() },
				{ "char", Character.class.getName() }, { "float", Float.class.getName() },
				{ "double", Double.class.getName() }, { "date", Date.class.getName() },
				{ "enumSet", "java.util.RegularEnumSet" }, { "array", "[Ljava.lang.Object;" },
				{ "String[]", "[Ljava.lang.String;" }, { "Double[]", "[Ljava.lang.Double;" },
				{ "Float[]", "[Ljava.lang.Float;" }, { "double[]", "[D" }, { "float[]", "[F" } });
		conf.registerSerializer(BigDecimal.class, new FSTJSonSerializers.BigDecSerializer(), true);
		reg.putSerializer(FSTJSonUnmodifiableCollectionSerializer.UNMODIFIABLE_COLLECTION_CLASS,
				new FSTJSonUnmodifiableCollectionSerializer(), true);
		reg.putSerializer(FSTJSonUnmodifiableMapSerializer.UNMODIFIABLE_MAP_CLASS,
				new FSTJSonUnmodifiableMapSerializer(), false);

		conf.type = prettyPrint ? ConfType.JSONPRETTY : ConfType.JSON;
		JsonFactory fac;
		if (prettyPrint) {
			fac = new JsonFactory() {
				@Override
				protected JsonGenerator _createUTF8Generator(final OutputStream out, final IOContext ctxt)
						throws IOException {
					UTF8JsonGenerator gen = new JacksonAccessWorkaround(ctxt, _generatorFeatures, _objectCodec, out);
					if (_characterEscapes != null) { gen.setCharacterEscapes(_characterEscapes); }
					SerializableString rootSep = _rootValueSeparator;
					if (rootSep != DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR) {
						gen.setRootValueSeparator(rootSep);
					}
					return gen;
				}

				@Override
				public JsonGenerator createGenerator(final OutputStream out) throws IOException {
					return super.createGenerator(out).setPrettyPrinter(new DefaultPrettyPrinter());
				}
			}.disable(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM).disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
		} else {
			fac = new JsonFactory() {
				@Override
				protected JsonGenerator _createUTF8Generator(final OutputStream out, final IOContext ctxt)
						throws IOException {
					UTF8JsonGenerator gen = new JacksonAccessWorkaround(ctxt, _generatorFeatures, _objectCodec, out);
					if (_characterEscapes != null) { gen.setCharacterEscapes(_characterEscapes); }
					SerializableString rootSep = _rootValueSeparator;
					if (rootSep != DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR) {
						gen.setRootValueSeparator(rootSep);
					}
					return gen;
				}
			};
			fac.disable(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM).disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
		}
		conf.setCoderSpecific(fac);
		conf.setStreamCoderFactory(new JSonStreamCoderFactory(conf));
		conf.setShareReferences(shareReferences);
		conf.setLastResortResolver(clName -> Unknown.class);
		return conf;

	}

	/**
	 * The Class JacksonAccessWorkaround.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	public static class JacksonAccessWorkaround extends UTF8JsonGenerator {

		/**
		 * Instantiates a new jackson access workaround.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param ctxt
		 *            the ctxt
		 * @param features
		 *            the features
		 * @param codec
		 *            the codec
		 * @param out
		 *            the out
		 * @date 30 sept. 2023
		 */
		public JacksonAccessWorkaround(final IOContext ctxt, final int features, final ObjectCodec codec,
				final OutputStream out) {
			super(ctxt, features, codec, out);
		}

		/**
		 * Instantiates a new jackson access workaround.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param ctxt
		 *            the ctxt
		 * @param features
		 *            the features
		 * @param codec
		 *            the codec
		 * @param out
		 *            the out
		 * @param outputBuffer
		 *            the output buffer
		 * @param outputOffset
		 *            the output offset
		 * @param bufferRecyclable
		 *            the buffer recyclable
		 * @date 30 sept. 2023
		 */
		public JacksonAccessWorkaround(final IOContext ctxt, final int features, final ObjectCodec codec,
				final OutputStream out, final byte[] outputBuffer, final int outputOffset,
				final boolean bufferRecyclable) {
			super(ctxt, features, codec, out, outputBuffer, outputOffset, bufferRecyclable);
		}

		/**
		 * Gets the output tail.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the output tail
		 * @date 30 sept. 2023
		 */
		public int getOutputTail() { return _outputTail; }
	}

	/**
	 * the standard FSTConfiguration. - safe (no unsafe r/w) - platform independent byte order - moderate compression
	 *
	 * note that if you are just read/write from/to byte arrays, its faster to use DefaultCoder.
	 *
	 * This should be used most of the time.
	 *
	 * @return
	 */
	public static FSTConfiguration createDefaultConfiguration() {
		FSTConfiguration conf = new FSTConfiguration();

		conf.registerIntToObjectMapFactory(new DefaultFSTInt2ObjectMapFactory());
		conf.addDefaultClazzes();
		// serializers
		FSTSerializerRegistry reg = conf.getCLInfoRegistry().getSerializerRegistry();
		reg.putSerializer(Class.class, new FSTClassSerializer(), false);
		reg.putSerializer(String.class, new FSTStringSerializer(), false);
		reg.putSerializer(Byte.class, new FSTBigNumberSerializers.FSTByteSerializer(), false);
		reg.putSerializer(Character.class, new FSTBigNumberSerializers.FSTCharSerializer(), false);
		reg.putSerializer(Short.class, new FSTBigNumberSerializers.FSTShortSerializer(), false);
		reg.putSerializer(Float.class, new FSTBigNumberSerializers.FSTFloatSerializer(), false);
		reg.putSerializer(Double.class, new FSTBigNumberSerializers.FSTDoubleSerializer(), false);
		reg.putSerializer(Date.class, new FSTDateSerializer(), false);
		reg.putSerializer(StringBuffer.class, new FSTStringBufferSerializer(), true);
		reg.putSerializer(StringBuilder.class, new FSTStringBuilderSerializer(), true);
		reg.putSerializer(EnumSet.class, new FSTEnumSetSerializer(), true);

		// for most cases don't register for subclasses as in many cases we'd like to fallback to JDK implementation
		// (e.g. TreeMap) in order to guarantee complete serialization
		reg.putSerializer(ArrayList.class, new FSTArrayListSerializer(), false);
		reg.putSerializer(Vector.class, new FSTCollectionSerializer(), true);
		reg.putSerializer(LinkedList.class, new FSTCollectionSerializer(), false); // subclass should register manually
		reg.putSerializer(HashSet.class, new FSTCollectionSerializer(), false); // subclass should register manually
		reg.putSerializer(HashMap.class, new FSTMapSerializer(), false); // subclass should register manually
		reg.putSerializer(LinkedHashMap.class, new FSTMapSerializer(), false); // subclass should register manually
		reg.putSerializer(Hashtable.class, new FSTMapSerializer(), true);
		reg.putSerializer(ConcurrentHashMap.class, new FSTMapSerializer(), true);
		// reg.putSerializer(FSTStruct.class, new FSTStructSerializer(), true);
		reg.putSerializer(Throwable.class, new FSTThrowableSerializer(), true);

		reg.putSerializer(BitSet.class, new FSTBitSetSerializer(), true);
		reg.putSerializer(Timestamp.class, new FSTTimestampSerializer(), true);

		// serializers for classes failing in fst JDK emulation (e.g. Android<=>JDK)
		reg.putSerializer(BigInteger.class, new FSTBigIntegerSerializer(), true);

		return conf;

	}

	/**
	 * Register int to object map factory.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param intToObjectMapFactory
	 *            the int to object map factory
	 * @date 30 sept. 2023
	 */
	public void registerIntToObjectMapFactory(final FSTInt2ObjectMapFactory intToObjectMapFactory) {
		if (intToObjectMapFactory != null) { this.intToObjectMapFactory = intToObjectMapFactory; }
	}

	// /**
	// * Creates the unsafe binary configuration.
	// *
	// * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	// * @param shared
	// * the shared
	// * @return the FST configuration
	// * @date 30 sept. 2023
	// */
	// public static FSTConfiguration createUnsafeBinaryConfiguration() {
	// final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
	// conf.type = ConfType.UNSAFE;
	// conf.setStreamCoderFactory(new FBinaryStreamCoderFactory(conf));
	// return conf;
	// }

	/**
	 * register a custom serializer for a given class or the class and all of its subclasses. Serializers must be
	 * configured identical on read/write side and should be set before actually making use of the Configuration.
	 *
	 * @param clazz
	 * @param ser
	 * @param alsoForAllSubclasses
	 */
	public void registerSerializer(final Class<?> clazz, final FSTObjectSerializer ser,
			final boolean alsoForAllSubclasses) {
		serializationInfoRegistry.getSerializerRegistry().putSerializer(clazz, ser, alsoForAllSubclasses);
	}

	/**
	 * Checks if is force clz init.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is force clz init
	 * @date 30 sept. 2023
	 */
	public boolean isForceClzInit() { return forceClzInit; }

	/**
	 * Gets the last resort resolver.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the last resort resolver
	 * @date 30 sept. 2023
	 */
	public LastResortClassResolver getLastResortResolver() { return lastResortResolver; }

	/**
	 * Sets the last resort resolver.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param lastResortResolver
	 *            the new last resort resolver
	 * @date 30 sept. 2023
	 */
	public void setLastResortResolver(final LastResortClassResolver lastResortResolver) {
		this.lastResortResolver = lastResortResolver;
	}

	/**
	 * always execute default fields init, even if no transients (so would get overwritten anyway) required for lossy
	 * codecs (kson)
	 *
	 * @param forceClzInit
	 * @return
	 */
	public FSTConfiguration setForceClzInit(final boolean forceClzInit) {
		this.forceClzInit = forceClzInit;
		return this;
	}

	/**
	 * Gets the instantiator.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param clazz
	 *            the clazz
	 * @return the instantiator
	 * @date 30 sept. 2023
	 */
	public FSTClassInstantiator getInstantiator(final Class<?> clazz) {
		return instantiator;
	}

	/**
	 * Sets the instantiator.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param instantiator
	 *            the new instantiator
	 * @date 30 sept. 2023
	 */
	public void setInstantiator(final FSTClassInstantiator instantiator) { this.instantiator = instantiator; }

	/**
	 * Gets the coder specific.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param <T>
	 *            the generic type
	 * @return the coder specific
	 * @date 30 sept. 2023
	 */
	public <T> T getCoderSpecific() { return (T) coderSpecific; }

	/**
	 * Sets the coder specific.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param coderSpecific
	 *            the new coder specific
	 * @date 30 sept. 2023
	 */
	public void setCoderSpecific(final Object coderSpecific) { this.coderSpecific = coderSpecific; }

	/**
	 * Sets the class loader.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param classLoader
	 *            the new class loader
	 * @date 30 sept. 2023
	 */
	public void setClassLoader(final ClassLoader classLoader) { this.classLoader = classLoader; }

	/**
	 * special configuration used internally for struct emulation
	 *
	 * @return
	 */
	public static FSTConfiguration createStructConfiguration() {
		FSTConfiguration conf = new FSTConfiguration();
		conf.setStructMode(true);
		return conf;
	}

	/**
	 * Instantiates a new FST configuration.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sharedFieldInfos
	 *            the shared field infos
	 * @date 30 sept. 2023
	 */
	protected FSTConfiguration() {
		this.fieldInfoCache = new ConcurrentHashMap<>();
	}

	/**
	 * Gets the stream coder factory.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the stream coder factory
	 * @date 30 sept. 2023
	 */
	public StreamCoderFactory getStreamCoderFactory() { return streamCoderFactory; }

	/**
	 * allows to use subclassed stream codecs. Can also be used to change class loading behaviour, as clasForName is
	 * part of a codec's interface.
	 *
	 * e.g. new StreamCoderFactory() {
	 *
	 * @Override public FSTEncoder createStreamEncoder() { return new FSTStreamEncoder(FSTConfiguration.this); }
	 *
	 * @Override public FSTDecoder createStreamDecoder() { return new FSTStreamDecoder(FSTConfiguration.this) { public
	 *           Class classForName(String name) { ... } } ; } };
	 *
	 *           You need to work with thread locals most probably as the factory is ~global (assigned to
	 *           fstconfiguration shared amongst streams)
	 *
	 * @param streamCoderFactory
	 */
	public void setStreamCoderFactory(final StreamCoderFactory streamCoderFactory) {
		this.streamCoderFactory = streamCoderFactory;
	}

	/**
	 * reuse heavy weight objects. If a FSTStream is closed, objects are returned and can be reused by new stream
	 * instances. the objects are held in soft references, so there should be no memory issues. FIXME: point of
	 * contention !
	 *
	 * @param cached
	 */
	public void returnObject(final Object cached) {
		try {
			while (!cacheLock.compareAndSet(false, true)) {
				// empty
			}
			List<SoftReference> li = cachedObjects.get(cached.getClass());
			if (li == null) {
				li = new ArrayList<>();
				cachedObjects.put(cached.getClass(), li);
			}
			if (li.size() < 5) { li.add(new SoftReference<>(cached)); }
		} finally {
			cacheLock.set(false);
		}
	}

	/**
	 * patch default serializer lookup. set to null to delete. Should be set prior to any serialization going on
	 * (serializer lookup is cached).
	 *
	 * @param del
	 */
	public void setSerializerRegistryDelegate(final FSTSerializerRegistryDelegate del) {
		serializationInfoRegistry.setSerializerRegistryDelegate(del);
	}

	/** The cache lock. */
	AtomicBoolean cacheLock = new AtomicBoolean(false);

	/**
	 * Gets the cached object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param cl
	 *            the cl
	 * @return the cached object
	 * @date 30 sept. 2023
	 */
	public Object getCachedObject(final Class<?> cl) {
		try {
			while (!cacheLock.compareAndSet(false, true)) {
				// empty
			}
			List<SoftReference> li = cachedObjects.get(cl);
			if (li == null) return null;
			for (int i = li.size() - 1; i >= 0; i--) {
				SoftReference<?> softReference = li.get(i);
				Object res = softReference.get();
				li.remove(i);
				if (res != null) return res;
			}
		} finally {
			cacheLock.set(false);
		}
		return null;
	}

	/**
	 * Checks if is force serializable.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is force serializable
	 * @date 30 sept. 2023
	 */
	public boolean isForceSerializable() { return forceSerializable; }

	/**
	 * treat unserializable classes same as if they would be serializable.
	 *
	 * @param forceSerializable
	 *            //
	 */
	public FSTConfiguration setForceSerializable(final boolean forceSerializable) {
		this.forceSerializable = forceSerializable;
		return this;
	}

	/**
	 * clear global deduplication caches. Useful for class reloading scenarios, else counter productive as
	 * j.reflect.Fiwld + Construtors will be instantiated more than once per class.
	 */
	public static void clearGlobalCaches() {
		FSTClazzInfo.sharedFieldSets.clear();
		FSTDefaultClassInstantiator.constructorMap.clear();
	}

	/**
	 * clear cached softref's and ThreadLocal.
	 */
	public void clearCaches() {
		try {
			FSTInputStream.cachedBuffer.set(null);
			while (!cacheLock.compareAndSet(false, true)) {
				// empty
			}
			cachedObjects.clear();
		} finally {
			cacheLock.set(false);
		}
	}

	/**
	 * Checks if is share references.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is share references
	 * @date 30 sept. 2023
	 */
	public boolean isShareReferences() { return shareReferences; }

	/**
	 * if false, identical objects will get serialized twice. Gains speed as long there are no double objects/cyclic
	 * references (typical for small snippets as used in e.g. RPC)
	 *
	 * Cycles and Objects referenced more than once will not be detected (if set to false). Additionally JDK
	 * compatibility is not supported (read/writeObject and stuff). Use case is highperformance serialization of plain
	 * cycle free data (e.g. messaging). Can perform significantly faster (20-40%).
	 *
	 * @param shareReferences
	 *
	 */
	public void setShareReferences(final boolean shareReferences) { this.shareReferences = shareReferences; }

	/**
	 *
	 * Preregister a class (use at init time). This avoids having to write class names. Its a very simple and effective
	 * optimization (frequently > 2 times faster for small objects).
	 *
	 * Read and write side need to have classes preregistered in the exact same order.
	 *
	 * The list does not have to be complete. Just add your most frequently serialized classes here to get significant
	 * gains in speed and smaller serialized representation size.
	 *
	 */
	public void registerClass(final Class<?>... c) {
		for (Class<?> element : c) {
			classRegistry.registerClass(element, this);
			try {
				Class<?> ac = Class.forName("[L" + element.getName() + ";");
				classRegistry.registerClass(ac, this);
			} catch (ClassNotFoundException e) {
				// silent
			}
		}
	}

	/**
	 * Adds the default clazzes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	void addDefaultClazzes() {
		classRegistry.registerClass(String.class, this);
		classRegistry.registerClass(Byte.class, this);
		classRegistry.registerClass(Short.class, this);
		classRegistry.registerClass(Integer.class, this);
		classRegistry.registerClass(Long.class, this);
		classRegistry.registerClass(Float.class, this);
		classRegistry.registerClass(Double.class, this);
		classRegistry.registerClass(BigDecimal.class, this);
		classRegistry.registerClass(BigInteger.class, this);
		classRegistry.registerClass(Character.class, this);
		classRegistry.registerClass(Boolean.class, this);
		classRegistry.registerClass(TreeMap.class, this);
		classRegistry.registerClass(HashMap.class, this);
		classRegistry.registerClass(ArrayList.class, this);
		classRegistry.registerClass(ConcurrentHashMap.class, this);
		classRegistry.registerClass(URL.class, this);
		classRegistry.registerClass(Date.class, this);
		classRegistry.registerClass(java.sql.Date.class, this);
		classRegistry.registerClass(SimpleDateFormat.class, this);
		classRegistry.registerClass(TreeSet.class, this);
		classRegistry.registerClass(LinkedList.class, this);
		classRegistry.registerClass(SimpleTimeZone.class, this);
		classRegistry.registerClass(GregorianCalendar.class, this);
		classRegistry.registerClass(Vector.class, this);
		classRegistry.registerClass(Hashtable.class, this);
		classRegistry.registerClass(BitSet.class, this);
		classRegistry.registerClass(Timestamp.class, this);
		classRegistry.registerClass(Locale.class, this);

		classRegistry.registerClass(StringBuffer.class, this);
		classRegistry.registerClass(StringBuilder.class, this);

		classRegistry.registerClass(Object.class, this);
		classRegistry.registerClass(Object[].class, this);
		classRegistry.registerClass(Object[][].class, this);
		classRegistry.registerClass(Object[][][].class, this);

		classRegistry.registerClass(byte[].class, this);
		classRegistry.registerClass(byte[][].class, this);

		classRegistry.registerClass(char[].class, this);
		classRegistry.registerClass(char[][].class, this);

		classRegistry.registerClass(short[].class, this);
		classRegistry.registerClass(short[][].class, this);

		classRegistry.registerClass(int[].class, this);
		classRegistry.registerClass(int[][].class, this);

		classRegistry.registerClass(float[].class, this);
		classRegistry.registerClass(float[][].class, this);

		classRegistry.registerClass(double[].class, this);
		classRegistry.registerClass(double[][].class, this);

		classRegistry.registerClass(long[].class, this);
		classRegistry.registerClass(long[][].class, this);

	}

	/**
	 * Gets the class registry.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the class registry
	 * @date 30 sept. 2023
	 */
	public FSTClazzNameRegistry getClassRegistry() { return classRegistry; }

	/**
	 * Gets the CL info registry.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the CL info registry
	 * @date 30 sept. 2023
	 */
	public FSTClazzInfoRegistry getCLInfoRegistry() { return serializationInfoRegistry; }

	/**
	 * Gets the class loader.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the class loader
	 * @date 30 sept. 2023
	 */
	public ClassLoader getClassLoader() { return classLoader; }

	/**
	 * Gets the class info.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param type
	 *            the type
	 * @return the class info
	 * @date 30 sept. 2023
	 */
	public FSTClazzInfo getClassInfo(final Class<?> type) {
		return serializationInfoRegistry.getCLInfo(type, this);
	}

	/**
	 * utility for thread safety and reuse. Do not close the resulting stream. However you should close the given
	 * InputStream 'in'
	 *
	 * @param in
	 * @return
	 */
	public FSTObjectInput getObjectInput(final InputStream in) {
		FSTObjectInput fstObjectInput = getIn();
		try {
			fstObjectInput.resetForReuse(in);
			return fstObjectInput;
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
		return null;
	}

	/**
	 * Gets the object input.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the object input
	 * @date 30 sept. 2023
	 */
	public FSTObjectInput getObjectInput() { return getObjectInput((InputStream) null); }

	/**
	 * take the given array as input. the array is NOT copied.
	 *
	 * WARNING: the input streams takes over ownership and might overwrite content of this array in subsequent IO
	 * operations.
	 *
	 * @param arr
	 * @return
	 */
	public FSTObjectInput getObjectInput(final byte arr[]) {
		return getObjectInput(arr, arr.length);
	}

	/**
	 * take the given array as input. the array is NOT copied.
	 *
	 * WARNING: the input streams takes over ownership and might overwrite content of this array in subsequent IO
	 * operations.
	 *
	 * @param arr
	 * @param len
	 * @return
	 */
	public FSTObjectInput getObjectInput(final byte arr[], final int len) {
		FSTObjectInput fstObjectInput = getIn();
		try {
			fstObjectInput.resetForReuseUseArray(arr, len);
			return fstObjectInput;
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
		return null;
	}

	/**
	 * take the given array and copy it to input. the array IS copied
	 *
	 * @param arr
	 * @param len
	 * @return
	 */
	public FSTObjectInput getObjectInputCopyFrom(final byte arr[], final int off, final int len) {
		FSTObjectInput fstObjectInput = getIn();
		try {
			fstObjectInput.resetForReuseCopyArray(arr, off, len);
			return fstObjectInput;
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
		return null;
	}

	/**
	 * Gets the in.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the in
	 * @date 30 sept. 2023
	 */
	protected FSTObjectInput getIn() {
		FSTObjectInput fstObjectInput = streamCoderFactory.getInput().get();
		if (fstObjectInput != null && fstObjectInput.isClosed()) { fstObjectInput = null; }
		if (fstObjectInput == null) {
			streamCoderFactory.getInput().set(new FSTObjectInput(this));
			return getIn();
		}
		fstObjectInput.conf = this;
		fstObjectInput.getCodec().setConf(this);
		return fstObjectInput;
	}

	/**
	 * Gets the out.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the out
	 * @date 30 sept. 2023
	 */
	protected FSTObjectOutput getOut() {
		FSTObjectOutput fstOut = streamCoderFactory.getOutput().get();
		if (fstOut == null || fstOut.closed) {
			streamCoderFactory.getOutput().set(new FSTObjectOutput(this));
			return getOut();
		}
		fstOut.conf = this;
		fstOut.getCodec().setConf(this);
		return fstOut;
	}

	/**
	 * utility for thread safety and reuse. Do not close the resulting stream. However you should close the given
	 * OutputStream 'out'
	 *
	 * @param out
	 *            - can be null (temp bytearrays stream is created then)
	 * @return
	 */
	public FSTObjectOutput getObjectOutput(final OutputStream out) {
		FSTObjectOutput fstObjectOutput = getOut();
		fstObjectOutput.resetForReUse(out);
		return fstObjectOutput;
	}

	/**
	 * @return a recycled outputstream reusing its last recently used byte[] buffer
	 */
	public FSTObjectOutput getObjectOutput() { return getObjectOutput((OutputStream) null); }

	/**
	 * Gets the object output.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param outByte
	 *            the out byte
	 * @return the object output
	 * @date 30 sept. 2023
	 */
	public FSTObjectOutput getObjectOutput(final byte[] outByte) {
		FSTObjectOutput fstObjectOutput = getOut();
		fstObjectOutput.resetForReUse(outByte);
		return fstObjectOutput;
	}

	/**
	 * ignores all serialization related interfaces (Serializable, Externalizable) and serializes all classes using the
	 * default scheme. Warning: this is a special mode of operation which fail serializing/deserializing many standard
	 * JDK classes.
	 *
	 * @param ignoreSerialInterfaces
	 */
	public void setStructMode(final boolean ignoreSerialInterfaces) {
		serializationInfoRegistry.setStructMode(ignoreSerialInterfaces);
	}

	/**
	 * special for structs
	 *
	 * @return
	 */
	public boolean isStructMode() { return serializationInfoRegistry.isStructMode(); }

	/**
	 * Gets the clazz info.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param rowClass
	 *            the row class
	 * @return the clazz info
	 * @date 30 sept. 2023
	 */
	public FSTClazzInfo getClazzInfo(final Class<?> rowClass) {
		return getCLInfoRegistry().getCLInfo(rowClass, this);
	}

	/**
	 * Sets the cross platform.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param crossPlatform
	 *            the new cross platform
	 * @date 30 sept. 2023
	 */
	public void setCrossPlatform(final boolean crossPlatform) { this.crossPlatform = crossPlatform; }

	/**
	 * Checks if is cross platform.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is cross platform
	 * @date 30 sept. 2023
	 */
	public boolean isCrossPlatform() { return crossPlatform; }

	/**
	 * Deep copy.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param <T>
	 *            the generic type
	 * @param metadata
	 *            the metadata
	 * @return the t
	 * @date 30 sept. 2023
	 */
	public <T> T deepCopy(final T metadata) {
		return (T) asObject(asByteArray(metadata));
	}

	/**
	 * A factory for creating StreamCoder objects.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	public interface StreamCoderFactory {

		/**
		 * Creates a new StreamCoder object.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the FST encoder
		 * @date 30 sept. 2023
		 */
		FSTEncoder createStreamEncoder();

		/**
		 * Creates a new StreamCoder object.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the FST decoder
		 * @date 30 sept. 2023
		 */
		FSTDecoder createStreamDecoder();

		/**
		 * Gets the input.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the input
		 * @date 30 sept. 2023
		 */
		ThreadLocal<FSTObjectInput> getInput();

		/**
		 * Gets the output.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the output
		 * @date 30 sept. 2023
		 */
		ThreadLocal<FSTObjectOutput> getOutput();
	}

	/**
	 * Creates the stream encoder.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the FST encoder
	 * @date 30 sept. 2023
	 */
	public FSTEncoder createStreamEncoder() {
		return streamCoderFactory.createStreamEncoder();
	}

	/**
	 * Creates the stream decoder.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the FST decoder
	 * @date 30 sept. 2023
	 */
	public FSTDecoder createStreamDecoder() {
		return streamCoderFactory.createStreamDecoder();
	}

	/**
	 * init right after creation of configuration, not during operation as it is not threadsafe regarding mutation
	 * currently only for minbin serialization
	 *
	 * @param keysAndVals
	 *            { { "symbolicName", "fullQualifiedClazzName" }, .. }
	 */
	public FSTConfiguration registerCrossPlatformClassMapping(final String[][] keysAndVals) {
		for (String[] keysAndVal : keysAndVals) { registerCrossPlatformClassMapping(keysAndVal[0], keysAndVal[1]); }
		return this;
	}

	/**
	 * Register cross platform class mapping.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param shortName
	 *            the short name
	 * @param fqName
	 *            the fq name
	 * @return the FST configuration
	 * @date 30 sept. 2023
	 */
	public FSTConfiguration registerCrossPlatformClassMapping(final String shortName, final String fqName) {
		minbinNames.put(shortName, fqName);
		minbinNamesReverse.put(fqName, shortName);
		return this;
	}

	/**
	 * shorthand for registerCrossPlatformClassMapping(_,_)
	 *
	 * @param shortName
	 *            - class name in json type field
	 * @param clz
	 *            - class
	 * @return
	 */
	public FSTConfiguration cpMap(final String shortName, final Class<?> clz) {
		return registerCrossPlatformClassMapping(shortName, clz.getName());
	}

	/**
	 * init right after creation of configuration, not during operation as it is not threadsafe regarding mutation
	 */
	public FSTConfiguration registerCrossPlatformClassMappingUseSimpleName(final Class<?>... classes) {
		registerCrossPlatformClassMappingUseSimpleName(Arrays.asList(classes));
		return this;
	}

	/**
	 * Register cross platform class mapping use simple name.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param classes
	 *            the classes
	 * @return the FST configuration
	 * @date 30 sept. 2023
	 */
	public FSTConfiguration registerCrossPlatformClassMappingUseSimpleName(final List<Class> classes) {
		for (Class<?> clz : classes) {
			minbinNames.put(clz.getSimpleName(), clz.getName());
			minbinNamesReverse.put(clz.getName(), clz.getSimpleName());
			try {
				if (!clz.isArray()) {
					Class<?> ac = Class.forName("[L" + clz.getName() + ";");
					minbinNames.put(clz.getSimpleName() + "[]", ac.getName());
					minbinNamesReverse.put(ac.getName(), clz.getSimpleName() + "[]");
				}
			} catch (ClassNotFoundException e) {
				FSTUtil.<RuntimeException> rethrow(e);
			}
		}
		return this;
	}

	/**
	 * get cross platform symbolic class identifier
	 *
	 * @param cl
	 * @return
	 */
	public String getCPNameForClass(final Class<?> cl) {
		String res = minbinNamesReverse.get(cl.getName());
		if (res == null) {
			if (cl.isAnonymousClass()) return getCPNameForClass(cl.getSuperclass());
			return cl.getName();
		}
		return res;
	}

	/**
	 * Gets the class for CP name.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @return the class for CP name
	 * @date 30 sept. 2023
	 */
	public String getClassForCPName(final String name) {
		String res = minbinNames.get(name);
		if (res == null) return name;
		return res;
	}

	/**
	 * convenience
	 */
	public Object asObject(final byte b[]) {
		try {
			return getObjectInput(b).readObject();
		} catch (Exception e) {
			System.out.println("unable to decode:" + new String(b, 0, 0, Math.min(b.length, 100)));
			FSTUtil.<RuntimeException> rethrow(e);
		}
		return null;
	}

	/**
	 * convenience. (object must be serializable)
	 */
	public byte[] asByteArray(final Object object) {
		FSTObjectOutput objectOutput = getObjectOutput();
		try {
			objectOutput.writeObject(object);
			return objectOutput.getCopyOfWrittenBuffer();
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
		return null;
	}

	/**
	 * Warning: avoids allocation + copying. The returned byteArray is a direct pointer to underlying buffer. the int
	 * length[] is expected to have at least on element. The buffer can be larger than written data, therefore length[0]
	 * will contain written length.
	 *
	 * The buffer content must be used (e.g. sent to network, copied to offheap) before doing another asByteArray on the
	 * current Thread.
	 */
	public byte[] asSharedByteArray(final Object object, final int length[]) {
		FSTObjectOutput objectOutput = getObjectOutput();
		try {
			objectOutput.writeObject(object);
			length[0] = objectOutput.getWritten();
			return objectOutput.getBuffer();
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
		return null;
	}

	/**
	 * utility/debug method. Use "asByteArray" for programmatic use as the byte array will already by UTF-8 and ready to
	 * be sent on network.
	 *
	 * @param o
	 * @return
	 */
	public String asJsonString(final Object o) {
		if (!(getCoderSpecific() instanceof JsonFactory)) return "can be called on JsonConfiguration only";
		return new String(asByteArray(o), StandardCharsets.UTF_8);
	}

	/**
	 * helper to write series of objects to streams/files > Integer.MAX_VALUE. it - serializes the object - writes the
	 * length of the serialized object to the stream - the writes the serialized object data
	 *
	 * on reader side (e.g. from a blocking socketstream, the reader then - reads the length - reads [length] bytes from
	 * the stream - deserializes
	 *
	 * @see decodeFromStream
	 *
	 * @param out
	 * @param toSerialize
	 * @throws IOException
	 */
	public void encodeToStream(final OutputStream out, final Object toSerialize) throws IOException {
		FSTObjectOutput objectOutput = getObjectOutput(); // could also do new with minor perf impact
		objectOutput.writeObject(toSerialize);
		int written = objectOutput.getWritten();
		out.write(written >>> 0 & 0xFF);
		out.write(written >>> 8 & 0xFF);
		out.write(written >>> 16 & 0xFF);
		out.write(written >>> 24 & 0xFF);

		// copy internal buffer to bufferedoutput
		out.write(objectOutput.getBuffer(), 0, written);
		objectOutput.flush();
	}

	/**
	 * @see encodeToStream
	 *
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public Object decodeFromStream(final InputStream in) throws Exception {
		int read = in.read();
		if (read < 0) throw new EOFException("stream is closed");
		int ch1 = read + 256 & 0xff;
		int ch2 = in.read() + 256 & 0xff;
		int ch3 = in.read() + 256 & 0xff;
		int ch4 = in.read() + 256 & 0xff;
		int len = (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
		if (len <= 0) throw new EOFException("stream is corrupted");
		byte buffer[] = new byte[len]; // this could be reused !
		while (len > 0) { len -= in.read(buffer, buffer.length - len, len); }
		return getObjectInput(buffer).readObject();
	}

	@Override
	public String toString() {
		return "FSTConfiguration{" + "name='" + name + '\'' + '}';
	}

	/**
	 * A factory for creating FSTDefaultStreamCoder objects.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	protected static class FSTDefaultStreamCoderFactory implements FSTConfiguration.StreamCoderFactory {

		/** The fst configuration. */
		private final FSTConfiguration fstConfiguration;

		/**
		 * Instantiates a new FST default stream coder factory.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param fstConfiguration
		 *            the fst configuration
		 * @date 30 sept. 2023
		 */
		public FSTDefaultStreamCoderFactory(final FSTConfiguration fstConfiguration) {
			this.fstConfiguration = fstConfiguration;
		}

		@Override
		public FSTEncoder createStreamEncoder() {
			return new FSTStreamEncoder(fstConfiguration);
		}

		@Override
		public FSTDecoder createStreamDecoder() {
			return new FSTStreamDecoder(fstConfiguration);
		}

		/** The input. */
		static ThreadLocal<FSTObjectInput> input = new ThreadLocal<>();

		/** The output. */
		static ThreadLocal<FSTObjectOutput> output = new ThreadLocal<>();

		@Override
		public ThreadLocal<FSTObjectInput> getInput() { return input; }

		@Override
		public ThreadLocal<FSTObjectOutput> getOutput() { return output; }

	}

	/**
	 * A factory for creating JSonStreamCoder objects.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	protected static class JSonStreamCoderFactory implements StreamCoderFactory {

		/** The conf. */
		protected final FSTConfiguration conf;

		/**
		 * Instantiates a new j son stream coder factory.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param conf
		 *            the conf
		 * @date 30 sept. 2023
		 */
		public JSonStreamCoderFactory(final FSTConfiguration conf) {
			this.conf = conf;
		}

		@Override
		public FSTEncoder createStreamEncoder() {
			return new FSTJsonEncoder(conf);
		}

		@Override
		public FSTDecoder createStreamDecoder() {
			return new FSTJsonDecoder(conf);
		}

		/** The input. */
		static ThreadLocal<FSTObjectInput> input = new ThreadLocal<>();

		/** The output. */
		static ThreadLocal<FSTObjectOutput> output = new ThreadLocal<>();

		@Override
		public ThreadLocal<FSTObjectInput> getInput() { return input; }

		@Override
		public ThreadLocal<FSTObjectOutput> getOutput() { return output; }
	}

	// /**
	// * A factory for creating FBinaryStreamCoder objects.
	// *
	// * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	// * @date 30 sept. 2023
	// */
	// protected static class FBinaryStreamCoderFactory implements StreamCoderFactory {
	//
	// /** The conf. */
	// protected final FSTConfiguration conf;
	//
	// /**
	// * Instantiates a new f binary stream coder factory.
	// *
	// * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	// * @param conf
	// * the conf
	// * @date 30 sept. 2023
	// */
	// public FBinaryStreamCoderFactory(final FSTConfiguration conf) {
	// this.conf = conf;
	// }
	//
	// @Override
	// public FSTEncoder createStreamEncoder() {
	// return new FSTBytezEncoder(conf, new HeapBytez(new byte[4096]));
	// }
	//
	// @Override
	// public FSTDecoder createStreamDecoder() {
	// return new FSTBytezDecoder(conf);
	// }
	//
	// /** The input. */
	// static ThreadLocal<FSTObjectInput> input = new ThreadLocal<>();
	//
	// /** The output. */
	// static ThreadLocal<FSTObjectOutput> output = new ThreadLocal<>();
	//
	// @Override
	// public ThreadLocal<FSTObjectInput> getInput() { return input; }
	//
	// @Override
	// public ThreadLocal<FSTObjectOutput> getOutput() { return output; }
	//
	// }
}
