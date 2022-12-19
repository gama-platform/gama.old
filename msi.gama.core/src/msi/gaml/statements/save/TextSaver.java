package msi.gaml.statements.save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;

public class TextSaver {

	public void save(final IScope scope, final IExpression item, final OutputStream os, final boolean header)
			throws GamaRuntimeException {
		if (os == null) return;
		save(scope, new OutputStreamWriter(os), header, item);
	}

	public void save(final IScope scope, final IExpression item, final File f, final boolean header)
			throws GamaRuntimeException, IOException {
		if (f == null) return;
		save(scope, new FileWriter(f, true), header, item);
	}

	private void save(final IScope scope, final Writer fw, final boolean header, final IExpression item)
			throws GamaRuntimeException {
		try (fw) {
			fw.write(Cast.asString(scope, item.value(scope)) + Strings.LN);
		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}

	}

}
