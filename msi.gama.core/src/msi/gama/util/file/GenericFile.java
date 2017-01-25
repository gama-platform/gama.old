package msi.gama.util.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.util.FileUtils;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.statements.Facets;
import msi.gaml.types.IContainerType;
import msi.gaml.types.Types;

public class GenericFile extends GamaFile<IList<String>, String, Integer, String> {

	private boolean shouldExist;

	public GenericFile(final String pathName) throws GamaRuntimeException {
		super(null, pathName);
	}

	public GenericFile(final String pathName, final boolean shouldExist) {
		this(pathName);
		this.shouldExist = shouldExist;
	}

	@Override
	public boolean shouldExist() {
		if (shouldExist)
			return super.shouldExist();
		return false;
	}

	public GenericFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	@Override
	public IContainerType<?> getType() {
		return Types.FILE;
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		return new Envelope(0, 0, 0, 0);
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }
		if (FileUtils.isBinaryFile(scope, getFile(scope))) {
			GAMA.reportAndThrowIfNeeded(scope, GamaRuntimeException
					.warning("Problem identifying the contents of " + getFile(scope).getAbsolutePath(), scope), false);
			setBuffer(GamaListFactory.create());
		}
		try {
			final BufferedReader in = new BufferedReader(new FileReader(getFile(scope)));
			final IList<String> allLines = GamaListFactory.create(Types.STRING);
			String str;
			str = in.readLine();
			while (str != null) {
				allLines.add(str);
				str = in.readLine();
			}
			in.close();
			setBuffer(allLines);
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}

	}

	@Override
	protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {}

}