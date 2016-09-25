/*********************************************************************************************
 *
 *
 * 'GamaFolderFile.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.util.file;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.operators.Files;
import msi.gaml.statements.Facets;
import msi.gaml.types.IContainerType;
import msi.gaml.types.Types;

public class GamaFolderFile extends GamaFile<IList<String>, String, Integer, String> {

	public GamaFolderFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
		// AD 27/04/13 Let the flags of the file remain the same. Can be turned
		// off and on using the "read" and
		// "write" operators, so no need to decide for a default here
		// setWritable(true);
	}

	@Override
	protected void checkValidity(final IScope scope) throws GamaRuntimeException {
		if (!getFile(scope).isDirectory()) {
			throw GamaRuntimeException.error(getFile(scope).getAbsolutePath() + "is not a folder", scope);
		}
		if (!getFile(scope).exists()) {
			throw GamaRuntimeException.error("The folder " + getFile(scope).getAbsolutePath()
					+ " does not exist. Please use 'new_folder' instead", scope);
		}
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return Files.FOLDER + "('" + /* StringUtils.toGamlString(getPath()) */getPath(null) + "')";
	}

	@Override
	public IContainerType getType() {
		return Types.FILE.of(Types.STRING);
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// No attributes to speak of
		return GamaListFactory.create();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) {
			return;
		}
		setBuffer(GamaListFactory.createWithoutCasting(Types.STRING, getFile(scope).list()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {
		// Nothing to do

	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		final IContainer<Integer, String> files = getContents(scope);
		Envelope globalEnv = null;
		for (final String s : files.iterable(scope)) {
			final IGamaFile f = Files.from(scope, s);
			final Envelope env = f.computeEnvelope(scope);
			if (globalEnv == null) {
				globalEnv = env;
			} else {
				globalEnv.expandToInclude(env);
			}
		}
		return globalEnv;
	}

}
