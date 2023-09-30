/*******************************************************************************************************
 *
 * DefaultCoder.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.simpleapi;

import java.io.IOException;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectInputNoShared;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.FSTObjectOutputNoShared;
import org.nustaq.serialization.util.FSTUtil;

/**
 * Created by ruedi on 09.11.14.
 *
 * Encodes Objects to byte arrays and vice versa using slight value compression and a platform neutral data layout (no
 * diff regarding big/little endian). Implementation is conservative (no unsafe)
 *
 * As this makes use of the stream oriented API, operation is not zero copy. However this is not too significant
 * compared to cost of serialization.
 *
 * KEEP and reuse instances, creation is expensive.
 *
 * This class cannot be used concurrently.
 *
 * Works similar to the unsafe coders, but does not use Unsafe. Note that reading and writing coder must match each
 * other in type and configuration.
 *
 */
public class DefaultCoder implements FSTCoder {

	/** The conf. */
	protected FSTConfiguration conf;

	/** The input. */
	FSTObjectInput input;

	/** The output. */
	FSTObjectOutput output;

	/**
	 * Instantiates a new default coder.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param shared
	 *            the shared
	 * @param toPreRegister
	 *            the to pre register
	 * @date 30 sept. 2023
	 */
	public DefaultCoder(final boolean shared, final Class... toPreRegister) {
		conf = FSTConfiguration.createDefaultConfiguration();
		conf.setShareReferences(shared);
		if (toPreRegister != null && toPreRegister.length > 0) { conf.registerClass(toPreRegister); }
		if (shared) {
			input = new FSTObjectInput(conf);
			output = new FSTObjectOutput(conf);
		} else {
			input = new FSTObjectInputNoShared(conf);
			output = new FSTObjectOutputNoShared(conf);
		}
	}

	/**
	 * Instantiates a new default coder.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param preregister
	 *            the preregister
	 * @date 30 sept. 2023
	 */
	public DefaultCoder(final Class... preregister) {
		this(true, preregister);
	}

	/**
	 * Instantiates a new default coder.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	public DefaultCoder() {
		this(true, (Class[]) null);
	}

	/**
	 * will throw an FSTBufferTooSmallException if buffer is too small.
	 */
	@Override
	public int toByteArray(final Object obj, final byte result[], final int resultOffset, final int avaiableSize) {
		output.resetForReUse();
		try {
			output.writeObject(obj);
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
		int written = output.getWritten();
		if (written > avaiableSize) throw FSTBufferTooSmallException.Instance;
		System.arraycopy(output.getBuffer(), 0, result, resultOffset, written);
		return written;
	}

	@Override
	public byte[] toByteArray(final Object o) {
		output.resetForReUse();
		try {
			output.writeObject(o);
		} catch (IOException e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
		return output.getCopyOfWrittenBuffer();
	}

	@Override
	public FSTConfiguration getConf() { return conf; }

	@Override
	public Object toObject(final byte arr[], final int off, final int len) {
		try {
			if (off == 0) {
				input.resetForReuseUseArray(arr);
			} else {
				input.resetForReuseCopyArray(arr, off, len);
			}
			return input.readObject();
		} catch (Exception e) {
			FSTUtil.<RuntimeException> rethrow(e);
		}
		return null;
	}

	@Override
	public Object toObject(final byte arr[]) {
		return toObject(arr, 0, arr.length);
	}

}
