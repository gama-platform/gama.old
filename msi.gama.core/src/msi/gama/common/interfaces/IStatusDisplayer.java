/*******************************************************************************************************
 *
 * msi.gama.common.interfaces.IStatusDisplayer.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.util.GamaColor;

public interface IStatusDisplayer {

	void resumeStatus();

	void waitStatus(String string);

	void informStatus(String string);

	void errorStatus(String message);

	void setSubStatusCompletion(double status);

	void setStatus(String msg, GamaColor color);

	void informStatus(String message, String icon);

	void setStatus(String msg, String icon);

	void beginSubStatus(String name);

	void endSubStatus(String name);

	void neutralStatus(String string);

}
