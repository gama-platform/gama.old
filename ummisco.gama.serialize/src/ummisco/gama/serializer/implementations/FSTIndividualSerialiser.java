/*******************************************************************************************************
 *
 * FSTIndividualSerialiser.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.io.IOException;

import org.nustaq.serialization.FSTBasicObjectSerializer;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTClazzInfo.FSTFieldInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import msi.gama.common.interfaces.ISerialisationConstants;
import msi.gama.runtime.IScope;

/**
 * The Class GamaFSTSerialiser.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 5 août 2023
 */
abstract class FSTIndividualSerialiser<T> extends FSTBasicObjectSerializer {

	/** The short name. */
	String shortName;

	/**
	 * Instantiates a new gama FST serialiser.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @date 7 août 2023
	 */
	void setName(final String name) { shortName = ISerialisationConstants.CLASS_PREFIX + name; }

	/**
	 * Instantiate.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param objectClass
	 *            the object class
	 * @param in
	 *            the in
	 * @param serializationInfo
	 *            the serialization info
	 * @param referencee
	 *            the referencee
	 * @param streamPosition
	 *            the stream position
	 * @return the t
	 * @throws Exception
	 *             the exception
	 * @date 7 août 2023
	 */
	@Override
	public final T instantiate(final Class objectClass, final FSTObjectInput in, final FSTClazzInfo serializationInfo,
			final FSTFieldInfo referencee, final int streamPosition) throws Exception {

		T result = deserialise(in.getConf().getScope(), in);
		in.registerObject(result, streamPosition, serializationInfo, referencee);
		return result;
	}

	/**
	 * Write object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param out
	 *            the out
	 * @param toWrite
	 *            the to write
	 * @param clzInfo
	 *            the clz info
	 * @param referencedBy
	 *            the referenced by
	 * @param streamPosition
	 *            the stream position
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 7 août 2023
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public void writeObject(final FSTObjectOutput out, final Object toWrite, final FSTClazzInfo clzInfo,
			final FSTFieldInfo referencedBy, final int streamPosition) throws IOException {
		try {
			serialise(out, (T) toWrite);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write. The method to redefine to allow for
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param out
	 *            the out
	 * @param toWrite
	 *            the to write
	 * @date 5 août 2023
	 */
	public void serialise(final FSTObjectOutput out, final T toWrite) throws Exception {}

	/**
	 * Read.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param in
	 *            the in
	 * @return the t
	 * @date 5 août 2023
	 */
	abstract public T deserialise(IScope scope, FSTObjectInput in) throws Exception;

}