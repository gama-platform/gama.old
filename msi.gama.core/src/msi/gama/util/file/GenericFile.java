/*******************************************************************************************************
 *
 * GenericFile.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.util.OldFileUtils;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.statements.Facets;
import msi.gaml.types.IContainerType;
import msi.gaml.types.Types;

/**
 * The Class GenericFile.
 */
public class GenericFile extends GamaFile<IList<String>, String> {

	/**
	 * Instantiates a new generic file.
	 *
	 * @param pathName the path name
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public GenericFile(final String pathName) throws GamaRuntimeException {
		super(GAMA.getRuntimeScope(), pathName);
	}

	/**
	 * Instantiates a new generic file.
	 *
	 * @param pathName the path name
	 * @param shouldExist the should exist
	 */
	public GenericFile(final String pathName, final boolean shouldExist) {
		super(GAMA.getRuntimeScope(), pathName, shouldExist);
	}

	/**
	 * Instantiates a new generic file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public GenericFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName, false);
	}

	@Override
	public IContainerType<?> getGamlType() {
		return Types.FILE;
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return Envelope3D.EMPTY;
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }
		if (OldFileUtils.isBinaryFile(scope, getFile(scope))) {
			GAMA.reportAndThrowIfNeeded(scope, GamaRuntimeException
					.warning("Problem identifying the contents of " + getFile(scope).getAbsolutePath(), scope), false);
			setBuffer(GamaListFactory.EMPTY_LIST);
		} else {
			try (final BufferedReader in = new BufferedReader(new FileReader(getFile(scope)))) {
				final IList<String> allLines = GamaListFactory.create(Types.STRING);
				String str;
				str = in.readLine();
				while (str != null) {
					allLines.add(str);
					str = in.readLine();
				}
				setBuffer(allLines);
			} catch (final IOException e) {
				throw GamaRuntimeException.create(e, scope);
			}
		}

	}

	@Override
	protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {}

}