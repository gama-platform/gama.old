/*******************************************************************************************************
 *
 * TextSaver.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import msi.gama.common.interfaces.ISerialisationConstants;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;

/**
 * The Class TextSaver.
 */
public class TextSaver extends AbstractSaver {

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param item
	 *            the item
	 * @param os
	 *            the os
	 * @param header
	 *            the header
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public void save(final IScope scope, final IExpression item, final OutputStream os, final boolean header)
			throws GamaRuntimeException {
		if (os == null) return;
		String toSave = Cast.asString(scope, item.value(scope));
		char id = toSave.charAt(0);
		Charset ch = id == ISerialisationConstants.GAMA_AGENT_IDENTIFIER
				|| id == ISerialisationConstants.GAMA_OBJECT_IDENTIFIER
						? ISerialisationConstants.STRING_BYTE_ARRAY_CHARSET : StandardCharsets.UTF_8;
		final Writer fw = new OutputStreamWriter(os, ch);
		try (fw) {
			fw.write(toSave);
		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param item
	 *            the item
	 * @param file
	 *            the file
	 * @param code
	 *            the code
	 * @param addHeader
	 *            the add header
	 * @param type
	 *            the type
	 * @param attributesToSave
	 *            the attributes to save
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Override
	public void save(final IScope scope, final IExpression item, final File file, final String code,
			final boolean addHeader, final String type, final Object attributesToSave)
			throws GamaRuntimeException, IOException {
		String toSave = Cast.asString(scope, item.value(scope));
		char id = toSave.charAt(0);
		Charset ch = id == ISerialisationConstants.GAMA_AGENT_IDENTIFIER
				|| id == ISerialisationConstants.GAMA_OBJECT_IDENTIFIER
						? ISerialisationConstants.STRING_BYTE_ARRAY_CHARSET : StandardCharsets.UTF_8;
		try (final Writer fw = new FileWriter(file, ch, true)) {
			fw.write(Cast.asString(scope, item.value(scope)) + Strings.LN);
		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	@Override
	protected Set<String> computeFileTypes() {
		return Set.of("text", "txt");
	}

	@Override
	public BiMap<String, String> getSynonyms() { return ImmutableBiMap.of("txt", "text"); }

}
