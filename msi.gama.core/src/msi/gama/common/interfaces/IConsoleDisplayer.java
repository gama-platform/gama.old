/*******************************************************************************************************
 *
 * msi.gama.common.interfaces.IConsoleDisplayer.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.util.GamaColor;

public interface IConsoleDisplayer {

	void debugConsole(int cycle, String s, ITopLevelAgent root, GamaColor color);

	void debugConsole(int cycle, String s, ITopLevelAgent root);

	void informConsole(String s, ITopLevelAgent root, GamaColor color);

	void informConsole(String s, ITopLevelAgent root);

	void showConsoleView(ITopLevelAgent agent);

	public void eraseConsole(final boolean setToNull);
}
