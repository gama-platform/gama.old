/*******************************************************************************************************
 *
 * JsonSaver.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
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
import java.io.Writer;
import java.util.Set;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.json.Json;
import msi.gama.util.file.json.WriterConfig;
import msi.gaml.expressions.IExpression;

/**
 * The Class JsonSaver.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 4 nov. 2023
 */
public class JsonSaver extends AbstractSaver {

	@Override
	public void save(final IScope scope, final IExpression item, final File file, final String code,
			final boolean addHeader, final String type, final Object attributesToSave)
			throws GamaRuntimeException, IOException {
		try (Writer fw = new FileWriter(file, true)) {
			Json.getNew().valueOf(item.value(scope)).writeTo(fw, WriterConfig.PRETTY_PRINT);
		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	@Override
	protected Set<String> computeFileTypes() {
		return Set.of("json");
	}

}
