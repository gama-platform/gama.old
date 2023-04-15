/*******************************************************************************************************
 *
 * AbstractSaver.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.save;

import java.util.Set;

import msi.gama.common.interfaces.ISaveDelegate;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class AbstractSaver.
 */
public abstract class AbstractSaver implements ISaveDelegate {

	/** The file types. */
	Set<String> fileTypes = computeFileTypes();

	@Override
	public Set<String> getFileTypes() { return fileTypes; }

	/**
	 * Compute file types.
	 *
	 * @return the string[]
	 */
	protected abstract Set<String> computeFileTypes();

	@Override
	public IType getDataType() { return Types.NO_TYPE; }

}
